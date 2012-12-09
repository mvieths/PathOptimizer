/**
 * 
 */
package pathoptimizer.data;

import java.util.Comparator;

import org.biopax.paxtools.model.BioPAXElement;

/**
 * @author Foeclan
 * 
 */
public class PathwayComparator implements Comparator<BioPAXElement> {

	@Override
	public int compare(BioPAXElement arg0, BioPAXElement arg1) {
		// TODO Auto-generated method stub
		return arg0.getRDFId().compareTo(arg1.getRDFId());
	}

}
