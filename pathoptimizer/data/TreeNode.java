package pathoptimizer.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.level3.BiochemicalReaction;
import org.biopax.paxtools.model.level3.Pathway;
import org.biopax.paxtools.model.level3.PhysicalEntity;
import org.biopax.paxtools.model.level3.Stoichiometry;

public class TreeNode {
	Vector<TreeNode> children;
	BioPAXElement thisElement;
	HashMap<String, Float> stoichiometry;
	Set<PhysicalEntity> products;

	public TreeNode(BioPAXElement element) {
		children = new Vector<TreeNode>();
		stoichiometry = new HashMap<String, Float>();

		thisElement = element;
		// If this is a reaction, fill in the products of that reaction
		if (thisElement instanceof BiochemicalReaction) {
			BiochemicalReaction bcr = (BiochemicalReaction) thisElement;
			// We only care about the products in this case, so only get those on the right side
			products = bcr.getRight();

			// Fill in the stoichiometry data
			Set<Stoichiometry> stoich = bcr.getParticipantStoichiometry();
			Iterator<Stoichiometry> iter = stoich.iterator();
			while (iter.hasNext()) {
				Stoichiometry s = iter.next();
				String name = s.getPhysicalEntity().getDisplayName();

				float sc = s.getStoichiometricCoefficient();
				stoichiometry.put(name, sc);
			}
		}
	}

	public void addChild(TreeNode element) {
		children.add(element);
	}

	public Vector<TreeNode> getChildren() {
		return children;
	}

	public BioPAXElement getElement() {
		return thisElement;
	}

	/**
	 * Provide the displayname of this node's element, if it's a Pathway or BiochemicalReaction
	 * 
	 * @return The getDisplayName() of the provided element or 'Unnamed' if it's not a Pathway or BiochemicalReaction
	 */
	public String getElementName() {
		if (thisElement instanceof Pathway) {
			Pathway p = (Pathway) thisElement;
			return p.getDisplayName();
		} else if (thisElement instanceof BiochemicalReaction) {
			BiochemicalReaction b = (BiochemicalReaction) thisElement;
			return b.getDisplayName();
		} else {
			return "Unnamed";
		}
	}

	/**
	 * Provides a set of all the Products (those on the right side of the biochemical reaction)
	 * 
	 * @return A Set containing all of the products as PhysicalEntity objects
	 */
	public Set<PhysicalEntity> getProducts() {
		return products;
	}

	/**
	 * Provides the stoichiometry data for this element, if any
	 * 
	 * @return A HashMap with the molecule name as a key and quantity as the value
	 */
	public HashMap<String, Float> getStoichiometry() {
		return stoichiometry;
	}
}
