/**
 * 
 */
package pathoptimizer.data;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.biopax.paxtools.io.BioPAXIOHandler;
import org.biopax.paxtools.io.jena.JenaIOHandler;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.BiochemicalReaction;
import org.biopax.paxtools.model.level3.Entity;
import org.biopax.paxtools.model.level3.Pathway;
import org.biopax.paxtools.model.level3.Process;

/**
 * @author Michael Vieths
 * 
 */
public class PathOpt {
	Model model;
	TreeNode root;

	// Stack<Reaction> path;

	public PathOpt(String paxFile) {
		init(paxFile);
	}

	private void init(String pathFile) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(pathFile);

			// Hide the warnings that convertFromOWL produces
			PrintStream oldErr = System.err;
			PrintStream newErr = new PrintStream(new ByteArrayOutputStream());
			System.setErr(newErr);

			BioPAXIOHandler bpioHandler = new JenaIOHandler(BioPAXLevel.L3);

			model = bpioHandler.convertFromOWL(fis);

			System.setErr(oldErr);

		} catch (FileNotFoundException e) {
			System.out.println("File " + pathFile + " could not be found");
			System.exit(1);
		}

		if (model != null) {
			// in order to collect "top" pathways, first, we create a copy of
			// the collection (because model.getObjects.remove() method is not
			// supported!)
			final Set<BioPAXElement> thisPathway = new HashSet<BioPAXElement>();
			thisPathway.addAll(model.getObjects());
			System.out.println("There are " + model.getObjects().size() + " objects in the model");

			Vector<Pathway> n = new Vector<Pathway>();

			Iterator<BioPAXElement> iter = thisPathway.iterator();
			while (iter.hasNext()) {
				BioPAXElement foo = iter.next();
				if (foo instanceof Pathway) {
					Pathway bar = (Pathway) foo;
					// System.out.println("- " + bar.getRDFId() + "\t\t(" + bar.getDisplayName() + ")");
					n.add(bar);
				}
			}

			Collections.sort(n, new PathwayComparator());

			// We only sorted so we could get the first element in the pathway.
			// This lets us create the root node.
			root = new TreeNode(n.get(0));

			// buildTree(root);
			populate(root);
			navigate(root, 0);
		}
	}

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

	public void navigate(TreeNode node, int depth) {
		BioPAXElement bpe = node.getElement();
		// System.out.println(depth + "\t" + bpe.getRDFId());
		System.out.println(depth + "\t" + node.getElementName());
		Vector<TreeNode> tn = node.getChildren();
		Iterator<TreeNode> iter = tn.iterator();
		while (iter.hasNext()) {
			navigate(iter.next(), depth + 1);
		}
	}

	public void buildTree(TreeNode node) {
		// Recursively walk through the pathways
		BioPAXElement bpe = node.getElement();

		if (bpe instanceof Pathway) {
			Pathway myPath = (Pathway) bpe;

			Set<Process> components = myPath.getPathwayComponent();

			Iterator<Process> process = components.iterator();
			while (process.hasNext()) {
				Process p = process.next();
				node.addChild(new TreeNode(p));
				System.out.println("=- " + p.getRDFId() + "\t\t(" + p.getDisplayName() + ")");
			}

			// If it's a pathway, keep adding children to it
			Vector<TreeNode> myChildren = node.getChildren();
			Iterator<TreeNode> i = myChildren.iterator();
			while (i.hasNext()) {
				TreeNode tn = i.next();
				if (!tn.isLeaf()) {
					buildTree(i.next());
				}
			}

		} else if (bpe instanceof BiochemicalReaction) {
			BiochemicalReaction bcr = (BiochemicalReaction) bpe;
			Set<Entity> participants = bcr.getParticipant();
			Iterator<Entity> e = participants.iterator();
			while (e.hasNext()) {
				Entity ent = e.next();
				node.addChild(new TreeNode(ent));
				System.out.println("==- " + ent.getRDFId() + "\t\t" + ent.getDisplayName());
			}
		}

	}

	public String parseRDFId(String id) {
		String[] parts = id.split("#");
		if (parts.length == 2) {
			return id.split("#")[1];
		} else {
			return "Unknown";
		}
	}
}
