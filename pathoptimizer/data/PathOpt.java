/**
 * 
 */
package pathoptimizer.data;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.biopax.paxtools.controller.AbstractTraverser;
import org.biopax.paxtools.controller.EditorMap;
import org.biopax.paxtools.controller.PropertyEditor;
import org.biopax.paxtools.controller.SimpleEditorMap;
import org.biopax.paxtools.io.BioPAXIOHandler;
import org.biopax.paxtools.io.jena.JenaIOHandler;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.Pathway;

/**
 * @author Michael Vieths
 * 
 */
public class PathOpt {
	MoleculeList allMolecules;
	Model model;

	// Stack<Reaction> path;

	public PathOpt(String sbmlFile) {
		init(sbmlFile);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (model != null) {
			// Model model1 = model.getLevel().getDefaultFactory().createModel();

			// PropertyFilter filter = new PropertyFilter() {
			// public boolean filter(PropertyEditor editor) {
			// return !"nextStep".equalsIgnoreCase(editor.getProperty());
			// // property will be ignored if 'false'
			// //is returned (i.e., 'nextStep' will be ignored by the following
			// //traverser, see below)
			// }
			// };

			// in order to collect "top" pathways, first, we create a copy of
			// the collection (because model.getObjects.remove() method is not
			// supported!)
			final Set<Pathway> thisPathway = new HashSet<Pathway>();
			thisPathway.addAll(model.getObjects(Pathway.class));

			Iterator<Pathway> iter = thisPathway.iterator();
			while (iter.hasNext()) {
				Pathway foo = iter.next();
				System.out.println(" " + foo.getName());
			}

			EditorMap editorMap = SimpleEditorMap.get(model.getLevel());

			@SuppressWarnings("unchecked")
			AbstractTraverser checker = new AbstractTraverser(editorMap) {
				protected void visit(Object value, BioPAXElement parent, Model model, PropertyEditor editor) {
					if (value instanceof Pathway && thisPathway.contains(value))
						model.remove((BioPAXElement) value); // - not a "root" pathway
				}
			};

			// now, let's run it to remove sub-pathways -
			for (BioPAXElement e : model.getObjects()) {
				checker.traverse(e, null);
			}

		}
	}

	public PathOpt(String sbmlFile, String defaultsFile) {
		init(sbmlFile);
		readDefaultsFile(defaultsFile);
	}

	/*
	 * Read in a defaults file of the format: species_name=XX.X
	 * 
	 * This value will replace the starting quantity for that molecule species
	 */
	public void readDefaultsFile(String defaultsFile) {
		try {
			// Read the file
			FileInputStream inputFile = new FileInputStream(defaultsFile);
			DataInputStream inStream = new DataInputStream(inputFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
			String strLine;
			String species;
			double quantity;

			// Grab a line, split it to get a value pair
			while ((strLine = reader.readLine()) != null) {
				// Check for comments or blank lines and ignore them
				if (strLine.startsWith("#") || strLine.equals("")) {
					continue;
				}

				// Check for otherwise invalid formats
				if (!Pattern.matches(".*=.*", strLine)) {
					System.out.println("Invalid line: " + strLine);
					continue;
				}

				String[] values = strLine.split("=");

				species = values[0];
				quantity = new Double(values[1]).doubleValue();

				// If we couldn't find the species, print a warning
				if (!allMolecules.setQuantity(species, quantity)) {
					System.out.println("Species " + species + " defined in " + defaultsFile + " does not exist in this pathway");
					continue;
				}
			}

			inStream.close();
		} catch (Exception ex) {
			System.out.println("Caught exception " + ex.getMessage());
		}
	}

	/*
	 * Reset the quantity of molecules to their default values
	 */
	public void resetSimulation() {
		for (Molecule m : allMolecules) {
			m.resetQuantity();
		}
	}

	public MoleculeList getMolecules() {
		return allMolecules;
	}

}
