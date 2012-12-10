/**
 * 
 */
package pathoptimizer.data;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.biopax.paxtools.io.BioPAXIOHandler;
import org.biopax.paxtools.io.jena.JenaIOHandler;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.Pathway;
import org.biopax.paxtools.model.level3.PhysicalEntity;
import org.biopax.paxtools.model.level3.Process;

/**
 * @author Michael Vieths
 * 
 */
public class PathSearch {
	Model model;
	TreeNode root;
	// Key is the pathway name (Pathway1, etc.)
	// Value is a hash of the molecules (second key) and the quantities produced (second value) in that pathway
	HashMap<String, HashMap<String, Integer>> quantities;

	public PathSearch(String paxFile) {
		init(paxFile);
	}

	/**
	 * Initialize the object. Imports the specified file.
	 * 
	 * @param pathFile
	 *            An OWL file in BioPAX level 3 format
	 */
	private void init(String pathFile) {
		FileInputStream fis;
		quantities = new HashMap<String, HashMap<String, Integer>>();

		try {
			fis = new FileInputStream(pathFile);

			// Hide the warnings that convertFromOWL produces
			PrintStream oldErr = System.err;
			// Create a new stream that we'll just ignore
			PrintStream newErr = new PrintStream(new ByteArrayOutputStream());
			System.setErr(newErr);

			BioPAXIOHandler bpioHandler = new JenaIOHandler(BioPAXLevel.L3);

			// Import the pathway from the provided file
			model = bpioHandler.convertFromOWL(fis);

			// Switch back to the normal error stream
			System.setErr(oldErr);

		} catch (FileNotFoundException e) {
			System.out.println("File " + pathFile + " could not be found");
			System.exit(1);
		}

		if (model != null) {
			final Set<BioPAXElement> thisPathway = new HashSet<BioPAXElement>();
			// Add all the objects in the model
			thisPathway.addAll(model.getObjects());
			// System.out.println("There are " + model.getObjects().size() + " objects in the model");

			// We need to sort the pathways, since they're not specified in a particular order in the file.
			// This will give us the 'root' pathway, allowing us to create a tree based off of it
			Vector<Pathway> n = new Vector<Pathway>();

			Iterator<BioPAXElement> iter = thisPathway.iterator();
			while (iter.hasNext()) {
				BioPAXElement bpe = iter.next();
				// Only add actual Pathways
				if (bpe instanceof Pathway) {
					Pathway bar = (Pathway) bpe;
					n.add(bar);
				}
			}

			// Sort them alphabetically by name (Pathway1 comes before Pathway2)
			Collections.sort(n, new PathwayComparator());

			// We only sorted so we could get the first element in the pathway.
			// This lets us create the root node.
			root = new TreeNode(n.get(0));

			// Create the tree starting at the root node
			populate(root);
			// Navigate through the generated tree, filling in the products and their quantities in a HashMap
			navigate(root, (Pathway) root.getElement(), 0);
			// printTotals();
			findMost("ADP");
			System.out.println("\n");
			printTotals();
		}
	}

	/**
	 * Starts with the given node, which is presumed to contain a BioPAXElement object.
	 * 
	 * Recurses through the components of that object, adding them and their products to the tree. The root should contain a Pathway object.
	 * 
	 * @param node
	 *            A TreeNode containing a BioPAX object
	 */
	public void populate(TreeNode node) {
		BioPAXElement bpe = node.getElement();
		if (bpe instanceof Pathway) {
			// Get the components
			Pathway myPath = (Pathway) bpe;
			Set<Process> components = myPath.getPathwayComponent();
			Iterator<Process> iter = components.iterator();
			while (iter.hasNext()) {
				Process process = iter.next();
				TreeNode next = new TreeNode(process);
				node.addChild(next);
				populate(next);
			}
		}
	}

	/**
	 * Starts with the given node and walks through its children.
	 * 
	 * @param node
	 *            A TreeNode containing a BioPAXElement object
	 * @param depth
	 *            The current tree depth
	 */
	public void navigate(TreeNode node, Pathway curPathway, int depth) {
		BioPAXElement bpe = node.getElement();
		// System.out.println(depth + "\t" + node.getElementName());// + "\t" + parseRDFId(bpe.getRDFId()));

		// Update the current pathway if we're in a new one
		if (bpe instanceof Pathway) {
			curPathway = (Pathway) bpe;
		}
		HashMap<String, Integer> curHash;
		if (quantities.containsKey(curPathway.getDisplayName())) {
			curHash = quantities.get(curPathway.getDisplayName());
		} else {
			curHash = new HashMap<String, Integer>();
		}

		Set<PhysicalEntity> j = node.getProducts();
		if (j != null) {
			Iterator<PhysicalEntity> pe = j.iterator();
			while (pe.hasNext()) {
				PhysicalEntity p = pe.next();
				if (curHash.containsKey(p.getDisplayName())) {
					curHash.put(p.getDisplayName(), curHash.get(p.getDisplayName()) + 1);
				} else {
					curHash.put(p.getDisplayName(), 1);
				}
				// System.out.println("\t\t" + p.getDisplayName() + "\t" + curHash.get(p.getDisplayName()));
			}
		}

		quantities.put(curPathway.getDisplayName(), curHash);

		Vector<TreeNode> tn = node.getChildren();
		Iterator<TreeNode> iter = tn.iterator();
		while (iter.hasNext()) {
			navigate(iter.next(), curPathway, depth + 1);
		}
	}

	/**
	 * Print out a list of all pathways with their resulting molecules and quantities
	 */
	public void printTotals() {
		Set<String> keys = quantities.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			System.out.println("Pathway " + key + ":\t");
			HashMap<String, Integer> value = quantities.get(key);
			Set<String> keys1 = value.keySet();
			Iterator<String> iter1 = keys1.iterator();
			while (iter1.hasNext()) {
				String key1 = iter1.next();
				System.out.println("\t" + key1 + "\t" + value.get(key1));
			}
		}
	}

	/**
	 * Return the pathway with the greatest quantity of the given molecule
	 * 
	 * @param molecule
	 * @return
	 */
	public Vector<String> findMost(String molecule) {
		Vector<String> pathways = new Vector<String>();
		int most = 0;

		Set<String> keys = quantities.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			HashMap<String, Integer> value = quantities.get(key);

			Set<String> keys1 = value.keySet();
			Iterator<String> iter1 = keys1.iterator();
			while (iter1.hasNext()) {
				String key1 = iter1.next();
				if (key1.equals(molecule)) {
					if (most < value.get(key1)) {
						pathways.clear();
						pathways.add(key);
						most = value.get(key1);
					} else if (most == value.get(key1)) {
						pathways.add(key);
					}
				}
			}
		}

		System.out.println("The most " + molecule + " in a pathway is " + most + ", found in pathway(s):");
		Iterator<String> i = pathways.iterator();
		while (i.hasNext()) {
			System.out.println("\t" + i.next());
		}
		return pathways;

	}

	/**
	 * Parses out the label from an RDFId. Normal format is 'http://www.reactome.org/biopax/109869#Pathway1', returns 'Pathway1'
	 * 
	 * @param id
	 * @return Portion of RDF after the hash sign
	 */
	public String parseRDFId(String id) {
		String[] parts = id.split("#");
		if (parts.length == 2) {
			return id.split("#")[1];
		} else {
			return "Unknown";
		}
	}
}
