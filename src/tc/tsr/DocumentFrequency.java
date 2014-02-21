/** 
 * A simple implementation of Term Space Reduction for Text
 * Categorisation based on frequency of documents in which a given
 * feature occurs...
 * (S. Luz, luzs@cs.tcd.ie)
 **/
package tc.tsr;

import tc.dstruct.ProbabilityModel;

/**
 * Document frequency produces a value based on the term
 * occurring in number of documents 
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
		return pm.getDocCount(term, cat)/pm.getDocSet().size();
	}

	/**
	 * ********** Lab 02 exercise (optional): ***************
	 * 
	 * Implemennt a method to set the global document frequency more efficiently
	 * than through computeGlobalScoresSUM
	 */
	public void computeGlobalDocFrequency() {
	}

}
