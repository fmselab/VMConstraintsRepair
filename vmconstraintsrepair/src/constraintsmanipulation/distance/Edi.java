package constraintsmanipulation.distance;

import constraintsmanipulation.model.Model;

/** Edit distance index
 * 
 * @author marcoradavelli
 *
 */
public class Edi extends BFED {

	public static Edi instance = new Edi();
	
	/** @return the distance m1-m2 (the m2 is taken as reference in the ratio) */
	public double getDistance(Model m1, Model m2) {
		double d = super.getDistance(m1, m2);
		return d / m2.getTotalNodes();
	}
	
	@Override
	public String getName() {return "Edi";}
}
