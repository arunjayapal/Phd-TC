/** 
 * A simple implementation of Term Space Reduction for Text
 * Categorisation based on frequency of documents in which a given
 * feature occurs...
 * (S. Luz, luzs@cs.tcd.ie)
 **/
package tc.tsr;

import java.util.Set;

import tc.dstruct.ProbabilityModel;

/**
 * Document frequency produces a value based on the term occurring in number of
 * documents
 * 
 * @author S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: DocumentFrequency.java,v 1.5 2004/03/29 14:29:33
 *          luzs Exp $</font>
 * @see IndentationHandler
 */

public class DocumentFrequency extends TermFilter {

	public DocumentFrequency(ProbabilityModel pm) {
		super(pm);
	}

	/**
	 * ********** Lab 02 exercise: ***************
	 * 
	 * Implement a method to calculate and return (as a double-precision
	 * integer) the document frequency for a given term and category.
	 */
	public double computeLocalTermScore(String term, String cat) {
		return (double) pm.getDocCount(term, cat) / (double) pm.getDocSet().size();
	}

	/**
	 * ********** Lab 02 exercise (optional): ***************
	 * 
	 * Implemennt a method to set the global document frequency more efficiently
	 * than through computeGlobalScoresSUM
	 */
	public void computeGlobalDocFrequency() {
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
		double df = 0.0;
		
		for(String cat: all_cats)
			df+=computeLocalTermScore(term, cat);
		
		return df;
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
		double df = 0.0;
		for(String cat: all_cats){
			if (computeLocalTermScore(term, cat) > df)
				df = computeLocalTermScore(term, cat);
		}
		return df;
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
		double df = 0.0;
		
		for(String cat: all_cats)
			df+=computeLocalTermScore(term, cat);
		
		df /= all_cats.size();
		
		return df;
	}

}
