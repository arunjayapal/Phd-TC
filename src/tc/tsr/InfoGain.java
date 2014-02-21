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
		double first_ = (p.tc * p.c) * Maths.log2((p.tc * p.c)/ (p.t * p.c)); //P(t, c) = P (t|c) P(c)
		double second_ = ((1-p.tc) * p.c ) * Maths.log2(((1-p.tc) * p.c)/(1-p.t)*(1-p.c)); //P(^t, c) = (1 − P(t|c))P(c))
		double third_ = (p.t - p.tc) * Maths.log2((p.t - p.tc)/ (p.t) * (1-p.c)); //P(t, ^c) = P(t) − P(t, c)
		double fourth_ = ((1-p.t) * second_) * Maths.log2(((1-p.t) * second_) / ((1-p.t) * (1-p.c))); //P (^t, ^c) = (1 − P(t))P(^t, c)
		double ig = first_ + second_ + third_ + fourth_;
		return ig;
	}
}
