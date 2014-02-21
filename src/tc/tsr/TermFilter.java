package tc.tsr;
import java.util.Arrays;
import java.util.Set;

import tc.dstruct.BagOfWords;
import tc.dstruct.ProbabilityModel;
import tc.dstruct.WordFrequencyPair;
import tc.dstruct.WordScorePair;

/**
 *  Abstract class for term set reduction
 *
 * @author  S Luz &#60;luzs@cs.tcd.ie&#62;
 * @version <font size=-1>$Id: TermFilter.java,v 1.6 2004/03/29 14:29:33 luzs Exp $</font>
 * @see  
*/
public abstract class TermFilter {

  ProbabilityModel pm;
  WordScorePair[] wsp = null;

  public  TermFilter(ProbabilityModel pm) {
    this.pm = pm;
    wsp = pm.getBlankWordScoreArray();
  }

  /**
   * All Term Filters must implement  computeLocalTermScore()
   */
  abstract double computeLocalTermScore(String term, String cat);

  /** 
   *  Return a  term set reduced by a afctor of aggr
   */ 
  public Set getReducedTermSet (int aggr)
  {
    return BagOfWords.extractTermCollection(getReducedFreqList(aggr));
  }

  /** 
   *  Sort score table, pick the first n=rsize terms and return their frequency list
   */
  public WordFrequencyPair[] getReducedFreqList(int aggr)
  {
    // ***** N.B.: you'll need to change this if you want to implement
    // this method (should it be implemented here, in the abstract
    // class? or will it be specific to each TSR method?)
    return new WordFrequencyPair[1];
  }


  public WordScorePair[] getSortedScores() {
    Arrays.sort(wsp);
    return wsp;
  }
  

  /** 
     * Get the value of wsp.
     * @return value of wsp.
     */
  public WordScorePair[] getWsp() {
    return wsp;
  }
  
  /**
   * Set the value of wsp.
   * @param v  Value to assign to wsp.
   */
  public void setWsp(WordScorePair[]  v) {
    this.wsp = v;
  }

  /** ********** Lab 02 (note): *************** 
   *  the way each local score is computed depends on the TSR method
   * you're using, therefore you will need to implement different
   * computeLocalTermScore() methods for different subclasses of
   * TermFilter
   * 
   */
  public void computeLocalScores (String cat){
    System.err.println("Computing LOCAL TSR for "+wsp.length+" terms and category "+cat);
    // convert wsp, initially filled with frequencies, into a score
    // table. (Scores will depend on the particular TSR implementation
    // that extends this abstract class.)
    for (int i = 0; i < wsp.length; i++) {
      wsp[i].setScore(computeLocalTermScore(wsp[i].getWord(),cat));
    }
  }
  
  /** ********** Lab 02 exercise: *************** 
   * Implement a method to combine local, category-specific, scores
   * (computed by computeLocalScores and stored in wsp) into global
   * scores through the method of SUM, and update the WordScorePair
   * table (wsp) with the global values. (Should this method be
   * implemented here or in TermFilter's subclasses?)
   */
  public void computeGlobalScoresSUM (){
    System.err.println("Computing GLOBAL TSR for "+wsp.length+" terms using f_sum");
  }

  /** ********** Lab 02 exercise: *************** 
   * Implement a method to combine local, category-specific, scores
   * (computed by computeLocalScores and stored in wsp) into global
   * scores through the method of MAXIMA, and update the WordScorePair
   * table (wsp) with the global values. (Should this method be
   * implemented here or in TermFilter's subclasses?)
   */
  public void computeGlobalScoresMAX (){
    System.err.println("Computing GLOBAL TSR for "+wsp.length+" terms using f_max");
  }

  /** ********** Lab 02 exercise: *************** 
   * Implement a method to combine local, category-specific, scores
   * (computed by computeLocalScores and stored in wsp) into global
   * scores through the method of WEIGHTED AVERAGE, and update the WordScorePair
   * table (wsp) with the global values. (Should this method be
   * implemented here or in TermFilter's subclasses?)
   */
  public void computeGlobalScoresWAVG (){
    System.err.println("Computing GLOBAL TSR for "+wsp.length+" using f_wavg");
  }


}

