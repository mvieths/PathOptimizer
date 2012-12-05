/**
 * 
 */
package pathoptimizer.data;

import java.util.Vector;

import org.sbml.libsbml.Reaction;
import org.sbml.libsbml.SpeciesReference;

/**
 * @author Foeclan
 * 
 */
public class Molecule {
	private SpeciesReference molSpecies;
	private double startingQuantity;
	private double currentQuantity;
	private Vector<Reaction> molReactants;
	private Vector<Reaction> molProducts;

	public Molecule(SpeciesReference species) {
		molSpecies = species;
		startingQuantity = species.getStoichiometry();
		currentQuantity = startingQuantity;
		molReactants = new Vector<Reaction>();
		molProducts = new Vector<Reaction>();
	}

	/*
	 * Retrieves a list of reactions in which this molecule is a reactant
	 */
	public Vector<Reaction> getReactants() {
		return molReactants;
	}

	/*
	 * Adds a reaction to the list of reactions in which this molecule is a reactant
	 */
	public void addReactant(Reaction reaction) {
		molReactants.add(reaction);
	}

	/*
	 * Removes a reaction from the list of reactions in which this molecule is a reactant
	 */
	public void removeReactant(Reaction reaction) {
		molReactants.remove(reaction);
	}

	/*
	 * Retrieves a list of reactions in which this molecule is a Product
	 */
	public Vector<Reaction> getProducts() {
		return molProducts;
	}

	/*
	 * Adds a reaction to the list of reactions in which this molecule is a Product
	 */
	public void addProduct(Reaction reaction) {
		molProducts.add(reaction);
	}

	/*
	 * Removes a reaction from the list of reactions in which this molecule is a Product
	 */
	public void removeProduct(Reaction reaction) {
		molProducts.remove(reaction);
	}

	/*
	 * Returns the species of molecule (of the format species_######, converted to text by the model)
	 */
	public String getName() {
		return molSpecies.getSpecies();
	}

	/*
	 * Lock the starting quantity at the current level
	 */
	public void lockQuantity() {
		startingQuantity = currentQuantity;
	}

	/*
	 * Returns how many of this molecule exist in the current pathway (to be used as the default quantity)
	 */
	public double getQuantity() {
		return currentQuantity;
	}

	/*
	 * Sets the quantity to a specific value
	 */
	public void setQuantity(double value) {
		startingQuantity = value;
		currentQuantity = startingQuantity;
	}

	/*
	 * Adds 1 molecule to the quantity
	 */
	public void addMolecule() {
		addMolecule(1.0);
	}

	/*
	 * Adds howMany molecules to the quantity
	 */
	public void addMolecule(double howMany) {
		currentQuantity += howMany;
	}

	/*
	 * Subtracts a molecule from the quantity
	 */
	public void subtractMolecule() {
		subtractMolecule(1.0);
	}

	/*
	 * Subtracts howMany molecules from the quantity
	 */
	public void subtractMolecule(double howMany) {
		currentQuantity -= howMany;
	}

	/*
	 * Resets to the starting quantity
	 */
	public void resetQuantity() {
		currentQuantity = startingQuantity;
	}

	/*
	 * Returns whether or not this molecule is a reactant
	 */
	public boolean isReactant() {
		return (molReactants.size() > 0);
	}

	/*
	 * Returns whether or not this molecule is a product
	 */
	public boolean isProduct() {
		return (molProducts.size() > 0);
	}

	/*
	 * Returns the species of this molecule
	 */
	public SpeciesReference getSpecies() {
		return molSpecies;
	}

	/*
	 * Compares two molecules
	 */
	public boolean equals(Molecule myMolecule) {
		if (myMolecule.getName().equals(getName())) {
			return true;
		} else {
			return false;
		}
	}
}
