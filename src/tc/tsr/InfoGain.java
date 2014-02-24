/** 
 * Term Space Reduction by Information Gain.
 * (see dimreduct-4up.pdf and termextract-4up.pdf)
 * (S. Luz, luzs@cs.tcd.ie)
 **/
package tc.tsr;

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
		double first_ = (double)(p.tc) * Maths.log2((double)(p.tc)/ (double)(p.t * p.c)); //P(t, c) = P (t|c) P(c)
		double second_ = (double)(p.ntc ) * Maths.log2((double)p.ntc/(double)(1-p.t)*(double)(1-p.c)); //P(^t, c) = (1 − P(t|c))P(c))
		double third_ = (double)(p.tnc) * Maths.log2((double)p.tnc/ (double)(p.t) * (double)(1-p.c)); //P(t, ^c) = P(t) − P(t, c)
		double fourth_ = ((double)p.ntnc) * Maths.log2((double)p.ntnc / ((double)(1-p.t) * (double)(1-p.c))); //P (^t, ^c) = (1 − P(t))P(^t, c)
		double ig = first_ + second_ + third_ + fourth_;
		return ig;
	}
}
