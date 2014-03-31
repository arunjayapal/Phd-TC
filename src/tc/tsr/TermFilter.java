package tc.tsr;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import tc.dstruct.BagOfWords;
import tc.dstruct.ProbabilityModel;
import tc.dstruct.WordFrequencyPair;
import tc.dstruct.WordScorePair;
import tc.util.PrintUtil;

/**
 * Abstract class for term set reduction
 * 
 * @author S Luz &#60;luzs@cs.tcd.ie&#62;
 * @version <font size=-1>$Id: TermFilter.java,v 1.6 2004/03/29 14:29:33 luzs
 *          Exp $</font>
 * @see
 */
public abstract class TermFilter {

	ProbabilityModel pm;
	WordScorePair[] wsp = null;

	public TermFilter(ProbabilityModel pm) {
		this.pm = pm;
		wsp = pm.getBlankWordScoreArray();
	}

	/**
	 * All Term Filters must implement computeLocalTermScore()
	 */
	abstract double computeLocalTermScore(String term, String cat);

	/**
	 * Return a term set reduced by a afctor of aggr
	 */
	public Set getReducedTermSet(int aggr) {
		return BagOfWords.extractTermCollection(getReducedFreqList(aggr));
	}

	/**
	 * Sort score table, pick the first n=rsize terms and return their frequency
	 * list
	 */
	public WordFrequencyPair[] getReducedFreqList(int aggr) {
		// ***** N.B.: you'll need to change this if you want to implement
		// this method (should it be implemented here, in the abstract
		// class? or will it be specific to each TSR method?)
		Arrays.sort(wsp);
	    int size = pm.getTermSetSize();
	    int rsize = size / aggr;
	    WordFrequencyPair[] rwfp = new WordFrequencyPair[rsize];
	    int j = 0;
	    System.err.println("Reducing T from "+size+" to "+rsize);
	    int stop = size-rsize-1;
	    for(int i = size-1; i > stop ; i--)
	      rwfp[j++] = new WordFrequencyPair(wsp[i].getWord(), 
	                                        pm.getTermCount(wsp[i].getWord()));
	    return rwfp;
	}

	public WordScorePair[] getSortedScores() {
		Arrays.sort(wsp);
		return wsp;
	}

	/**
	 * Get the value of wsp.
	 * 
	 * @return value of wsp.
	 */
	public WordScorePair[] getWsp() {
		return wsp;
	}

	/**
	 * Set the value of wsp.
	 * 
	 * @param v
	 *            Value to assign to wsp.
	 */
	public void setWsp(WordScorePair[] v) {
		this.wsp = v;
	}

	/**
	 * ********** Lab 02 (note): *************** the way each local score is
	 * computed depends on the TSR method you're using, therefore you will need
	 * to implement different computeLocalTermScore() methods for different
	 * subclasses of TermFilter
	 * 
	 */
	public void computeLocalScores(String cat) {
		System.err.println("Computing LOCAL TSR for " + wsp.length
				+ " terms and category " + cat);
		// convert wsp, initially filled with frequencies, into a score
		// table. (Scores will depend on the particular TSR implementation
		// that extends this abstract class.)
		for (int i = 0; i < wsp.length; i++) {
			PrintUtil.printNoMove("Computing TSR  ...",i);
			wsp[i].setScore(computeLocalTermScore(wsp[i].getWord(), cat));
//			System.out.println(wsp[i].getWord()+":"+wsp[i].getScore());
		}
	}
	public void computeGlobalScoresSUM (){
	    System.err.println("Computing GLOBAL TSR for "+wsp.length+" using f_sum");
	    // convert wsp, initially filled with frequencies, into a score table
	    Set cs = pm.getCategorySet();
	    for (int i = 0; i < wsp.length; i++) {
	      PrintUtil.printNoMove("Computing TSR  ...",i);
	      for (Iterator e = cs.iterator(); e.hasNext() ;)
	        wsp[i].setScore(wsp[i].getScore() + 
	                        computeLocalTermScore(wsp[i].getWord(), (String)e.next()));
	    }
	    PrintUtil.printNoMove("Computing TSR  ...",wsp.length);
	    PrintUtil.donePrinting();
	  }

	  public void computeGlobalScoresMAX (){
	    System.err.println("Computing GLOBAL TSR for "+wsp.length+" using f_max");
	    // convert wsp, initially filled with frequencies, into a score table
	    Set cs = pm.getCategorySet();
	    for (int i = 0; i < wsp.length; i++) {
	      PrintUtil.printNoMove("Computing TSR  ...",i);
	      for (Iterator e = cs.iterator(); e.hasNext() ;){
	        double s = computeLocalTermScore(wsp[i].getWord(), (String)e.next());
	        wsp[i].setScore(wsp[i].getScore() > s ? wsp[i].getScore() : s);
	      }
	    }
	    PrintUtil.printNoMove("Computing TSR  ...",wsp.length);
	    PrintUtil.donePrinting();
	  }

	  public void computeGlobalScoresWAVG (){
	    System.err.println("Computing GLOBAL TSR for "+wsp.length+" using f_wavg");
	    // convert wsp, initially filled with frequencies, into a score table
	    Set cs = pm.getCategorySet();
	    for (int i = 0; i < wsp.length; i++) {
	      PrintUtil.printNoMove("Computing TSR  ...",i);
	      for (Iterator e = cs.iterator(); e.hasNext() ;){
	        String cat = (String)e.next(); 
	        wsp[i].setScore(wsp[i].getScore() + 
	                        pm.getCatGenerality(cat) *
	                        computeLocalTermScore(wsp[i].getWord(), cat));
	      }
	    }
	    PrintUtil.donePrinting();
	  }
}
