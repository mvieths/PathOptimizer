/**
 * 
 */
package pathoptimizer.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.regex.Pattern;

import org.sbml.libsbml.Model;
import org.sbml.libsbml.Reaction;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLReader;
import org.sbml.libsbml.SpeciesReference;

/**
 * @author Michael Vieths
 * 
 */
public class Pathway {
	static {
		System.loadLibrary("sbmlj");
	}

	Vector<Reaction> reactions;
	Vector<SpeciesReference> molecules;
	MoleculeList allMolecules;
	Model model;

	public Pathway(String sbmlFile) {
		init(sbmlFile);
	}

	private void init(String sbmlFile) {
		reactions = new Vector<Reaction>();
		molecules = new Vector<SpeciesReference>();
		allMolecules = new MoleculeList();

		SBMLReader reader = new SBMLReader();
		SBMLDocument document;
		int level, version;

		document = reader.readSBML(sbmlFile);
		if (document.getNumErrors() > 0) {
			document.printErrors();
			System.out.println("Printing skipped.  Please correct the above problems first.");
			System.exit(1);
		}

		model = document.getModel();

		if (model == null) {
			System.out.println("There does not appear to be a model in this file");
			System.exit(1);
		}

		level = (int) document.getLevel();
		version = (int) document.getVersion();

		System.out.println("File: " + sbmlFile + " (Level " + level + ", version " + version + ")\n");

		if (level == 1) {
			System.out.println("model name: " + model.getName());
		} else {
			System.out.println("  model id: " + (model.isSetId() ? model.getId() : "(empty)"));
		}

		System.out.println("functionDefinitions: " + model.getNumFunctionDefinitions());
		System.out.println("    unitDefinitions: " + model.getNumUnitDefinitions());
		System.out.println("       compartments: " + model.getNumCompartments());
		System.out.println("            species: " + model.getNumSpecies());
		System.out.println("         parameters: " + model.getNumParameters());
		System.out.println("          reactions: " + model.getNumReactions());
		System.out.println("              rules: " + model.getNumRules());
		System.out.println("             events: " + model.getNumEvents());

		// Structure:
		// reaction
		// .notes
		// .annotation
		// .listOfReactants
		// ..speciesReference
		// .listOfProducts
		// ..speciesReference

		// Cycle through the reactions, adding up how many of each reactant and product are defined as present
		for (int x = 0; x < model.getNumReactions(); x++) {
			Reaction myReaction = model.getReaction(x);
			reactions.add(myReaction);
			boolean alreadyAdded = false;

			// Add up the reactants
			for (int y = 0; y < myReaction.getNumReactants(); y++) {
				SpeciesReference sr = myReaction.getReactant(y);
				Molecule myMolecule = new Molecule(sr);
				// Only add each species of reactant to the overall list once
				allMolecules.addUnique(myMolecule);

				if (myReaction.getReversible()) {
					myMolecule.addProduct(myReaction);
					myMolecule.addReactant(myReaction);
					alreadyAdded = true;
				} else {
					myMolecule.addReactant(myReaction);
				}

				molecules.add(sr);
			}

			// Add up the products
			for (int y = 0; y < myReaction.getNumProducts(); y++) {
				SpeciesReference sr = myReaction.getProduct(y);
				Molecule myMolecule = new Molecule(sr);
				// Only add each species of product to the overall list once
				allMolecules.addUnique(myMolecule);

				if (!alreadyAdded) {
					myMolecule.addProduct(myReaction);
				}

				molecules.add(sr);
			}
		}

		// Lock the starting quantities at their current levels
		for (Molecule m : allMolecules) {
			m.lockQuantity();
		}

		System.out.println("=== there are " + allMolecules.size() + " unique molecules in this pathway");
		System.out.println("=== there are " + molecules.size() + " non-unique molecules in this pathway");

		// for (Molecule m : allMolecules) {
		// System.out.println("There are " + m.getQuantity() + " of " + model.getSpecies(m.getName()).getName() + " (" + m.getName() + ")");
		// }
	}

	public Pathway(String sbmlFile, String defaultsFile) {
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

	public void runSimulation(long iterations) throws Exception {
		// Repeat as many times as requested
		for (int i = 0; i < iterations; i++) {
			// Go through all the reactions
			for (int j = 0; j < reactions.size(); j++) {
				// Subtract reactants
				Reaction myReaction = reactions.get(j);
				if (myReaction != null) {
					for (int x = 0; x < myReaction.getNumReactants(); x++) {
						SpeciesReference sr = myReaction.getReactant(x);
						Molecule myMolecule = new Molecule(sr);
						try {
							allMolecules.subtractQuantity(myMolecule, sr.getStoichiometry());
							// System.out.println("Subtracting " + sr.getStoichiometry() + " from " + myMolecule.getName() + " (reaction " + x + ")");
						} catch (Exception ex) {
							// We couldn't find the molecule; pass it along
							throw new Exception(ex);
						}
					}

					// Add products
					for (int y = 0; y < myReaction.getNumProducts(); y++) {
						SpeciesReference sr = myReaction.getProduct(y);
						Molecule myMolecule = new Molecule(sr);
						try {
							allMolecules.addQuantity(myMolecule, sr.getStoichiometry());
							// System.out.println("Adding " + sr.getStoichiometry() + " to " + myMolecule.getName() + " (reaction " + y + ")");
						} catch (Exception ex) {
							// We couldn't find the molecule; pass it along
							throw new Exception(ex);
						}
					}
				} else {
					System.out.println("Reaction was null");
				}
			}
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

	public void printResults(MoleculeList list) {
		for (Molecule m : list) {
			System.out.println("There are " + m.getQuantity() + " of " + model.getSpecies(m.getName()).getName() + " (" + m.getName() + ")");
		}
	}

	public MoleculeList getMolecules() {
		return allMolecules;
	}

	public Vector<Reaction> getReactions() {
		return reactions;
	}

	public Model getModel() {
		return model;
	}
}
