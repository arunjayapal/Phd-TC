/** 
 * Term Space Reduction by Information Gain.
 * (see dimreduct-4up.pdf and termextract-4up.pdf)
 * (S. Luz, luzs@cs.tcd.ie)
 **/
package tc.tsr;

import java.util.Set;

import tc.dstruct.Probabilities;
import tc.dstruct.ProbabilityModel;
import tc.util.Maths;

/**
 * 
 * 
 * @author S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: InfoGain.java,v 1.5 2004/03/29 14:29:33 luzs Exp
 *          $</font>
 * @see GenerateARFF
 */

public class InfoGain extends TermFilter {

	public InfoGain(ProbabilityModel pm) {
		super(pm);
	}

	/**
	 * ********** Lab 02 exercise: ***************
	 * 
	 * Implement a method to calculate and return (as a double-precision
	 * integer) the information gain for a given term and category.
	 */
	public double computeLocalTermScore(String term, String cat) {
		Probabilities p = pm.getProbabilities(term, cat);
		return Maths.xTimesLog2y(p.tc, p.tc / (p.t * p.c))
				+ Maths.xTimesLog2y(p.ntc, p.ntc / ((1 - p.t) * p.c))
				+ Maths.xTimesLog2y(p.tnc, p.tnc / (p.t * (1 - p.c)))
				+ Maths.xTimesLog2y(p.ntnc, p.ntnc / ((1 - p.t) * (1 - p.c)));
	}
}
