/**
 * 
 */
package pathoptimizer.data;

import java.util.AbstractList;
import java.util.Vector;

/**
 * @author Michael Vieths
 * 
 */
public class MoleculeList extends AbstractList<Molecule> {
	private Vector<Molecule> molList;

	public MoleculeList() {
		molList = new Vector<Molecule>();
	}

	public MoleculeList(Vector<Molecule> list) {
		molList = new Vector<Molecule>();
		molList.addAll(list);
	}

	public void addUnique(Molecule myMolecule) {
		// Check to see if this molecule is already in here
		boolean isPresent = false;
		for (Molecule m : molList) {
			if (m.getName().equals(myMolecule.getName())) {
				m.addMolecule(myMolecule.getQuantity());
				isPresent = true;
				// System.out.println("Molecule " + myMolecule.getName() + " is in the list, adding " + myMolecule.getQuantity());
			}
		}

		// If it's not, add it
		if (isPresent == false) {
			molList.add(myMolecule);
			// System.out.println("Molecule " + myMolecule.getName() + " is not in the list, adding " + myMolecule.getQuantity());
		}

	}

	public void addAll(MoleculeList list) {
		molList.addAll(list.getAsVector());
	}

	public void remove(Molecule myMolecule) {
		molList.remove(myMolecule);
	}

	public Molecule get(int position) {
		return molList.elementAt(position);
	}

	public int size() {
		return molList.size();
	}

	public void clear() {
		molList.clear();
	}

	public Vector<Molecule> getAsVector() {
		return molList;
	}

	public double addQuantity(Molecule myMolecule, double amount) throws Exception {
		for (Molecule i : molList) {
			if (i.equals(myMolecule)) {
				i.addMolecule(amount);
				return i.getQuantity();
			}
		}
		throw new Exception("Unable to find molecule " + myMolecule.getName() + " while adding");
	}

	public double subtractQuantity(Molecule myMolecule, double amount) throws Exception {
		for (Molecule i : molList) {
			if (i.equals(myMolecule)) {
				i.subtractMolecule(amount);
				return i.getQuantity();
			}
		}
		throw new Exception("Unable to find molecule " + myMolecule.getName() + " while subtracting");
	}

	public boolean setQuantity(String species, double quantity) {
		boolean found = false;
		for (Molecule i : molList) {
			if (i.getName().equals(species)) {
				i.setQuantity(quantity);
				found = true;
				break;
			}
		}
		return found;
	}
}
