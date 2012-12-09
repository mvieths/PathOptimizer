package pathoptimizer.data;

import java.util.Vector;

import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.level3.BiochemicalReaction;
import org.biopax.paxtools.model.level3.Pathway;

public class TreeNode {
	Vector<TreeNode> children;
	BioPAXElement thisElement;

	public TreeNode(BioPAXElement element) {
		children = new Vector<TreeNode>();
		thisElement = element;
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

	public boolean isLeaf() {
		if (children.size() > 0) {
			return false;
		} else {
			return true;
		}
	}
}
