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
//		System.out.println(p.t + ":" + p.c + ":" + p.tc + ":" + p.c * p.t + ":"
//				+ (1 - p.t) * (p.c) +":"+(p.t) * (1 - p.c)+":"+(1 - p.t) * (1 - p.c));
		Double first_ = (double) (p.tc)
				* (double) Maths.log2((double) (p.tc) / (double) (p.t * p.c)); // P(t, c)
																		// = P
																		// (t|c)
																		// P(c)
		Double second_ = (double) (p.ntc)
				* (double) Maths.log2((double) p.ntc / (double) ((1 - p.t) * (p.c))); // P(^t,
																				// c)
																				// =
																				// (1
																				// −
																				// P(t|c))P(c))
		Double third_ = (double) (p.tnc)
				* (double) Maths.log2((double) p.tnc / (double) ((p.t) * (1 - p.c))); // P(t,
																				// ^c)
																				// =
																				// P(t)
																				// −
																				// P(t,
																				// c)
		Double fourth_ = (double) p.ntnc
				* (double) Maths.log2((double) p.ntnc / (double) ((1 - p.t) * (1 - p.c))); // P
																					// (^t,
																					// ^c)
																					// =
																					// (1
																					// −
																					// P(t))-P(^t,
																					// c)
																					// System.out.println(first_
																					// +
																					// ":"+second_+":"
																					// +
																					// third_+":"
																					// +
																					// fourth_);
		if (first_.compareTo(Double.NaN)==0) first_ = 0.0;
		if (second_.compareTo(Double.NaN)==0) second_ = 0.0;
		if (third_.compareTo(Double.NaN)==0) third_ = 0.0;
		if (fourth_.compareTo(Double.NaN)==0) fourth_ = 0.0;
		
		double ig = first_ + second_ + third_ + fourth_;
		return ig;
	}

	/**
	 * ********** Lab 02 exercise: *************** Implement a method to combine
	 * local, category-specific, scores (computed by computeLocalScores and
	 * stored in wsp) into global scores through the method of SUM, and update
	 * the WordScorePair table (wsp) with the global values. (Should this method
	 * be implemented here or in TermFilter's subclasses?)
	 */
	public double computeGlobalScoresSUM(String term) {
		System.err.println("Computing GLOBAL TSR for " + wsp.length
				+ " terms using f_sum");
		Set<String> all_cats = pm.getCategorySet();
		double ig = 0.0;

		for (String cat : all_cats)
			ig += computeLocalTermScore(term, cat);

		return ig;
	}

	/**
	 * ********** Lab 02 exercise: *************** Implement a method to combine
	 * local, category-specific, scores (computed by computeLocalScores and
	 * stored in wsp) into global scores through the method of MAXIMA, and
	 * update the WordScorePair table (wsp) with the global values. (Should this
	 * method be implemented here or in TermFilter's subclasses?)
	 */
	public double computeGlobalScoresMAX(String term) {
		System.err.println("Computing GLOBAL TSR for " + wsp.length
				+ " terms using f_max");
		Set<String> all_cats = pm.getCategorySet();
		double ig = 0.0;
		for (String cat : all_cats) {
			if (computeLocalTermScore(term, cat) > ig)
				ig = computeLocalTermScore(term, cat);
		}
		return ig;
	}

	/**
	 * ********** Lab 02 exercise: *************** Implement a method to combine
	 * local, category-specific, scores (computed by computeLocalScores and
	 * stored in wsp) into global scores through the method of WEIGHTED AVERAGE,
	 * and update the WordScorePair table (wsp) with the global values. (Should
	 * this method be implemented here or in TermFilter's subclasses?)
	 */
	public double computeGlobalScoresWAVG(String term) {
		System.err.println("Computing GLOBAL TSR for " + wsp.length
				+ " using f_wavg");
		Set<String> all_cats = pm.getCategorySet();
		double ig = 0.0;

		for (String cat : all_cats)
			ig += computeLocalTermScore(term, cat);

		ig /= all_cats.size();

		return ig;
	}
}
