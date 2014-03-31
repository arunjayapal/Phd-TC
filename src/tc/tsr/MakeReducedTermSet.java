/** 
 * Parse Reuters file, perform term filtering and print the reduced
 * term set with the score for each term.
 *
 * (S. Luz, luzs@cs.tcd.ie)
 **/
package tc.tsr;

import java.util.Enumeration;
import tc.dstruct.CorpusList;
import tc.dstruct.ParsedText;
import tc.dstruct.ProbabilityModel;
import tc.dstruct.StopWordList;
import tc.dstruct.WordFrequencyPair;
import tc.dstruct.WordScorePair;
import tc.parser.NewsParser;

/**
 * Parse Reuters file, perform term filtering and print the reduced term set
 * with the score for each term.
 * 
 * Usage:
 * 
 * <pre>
 *  MakeReducedTermSet corpus_list stopwdlist aggr tf_method categ
 * 
 * SYNOPSIS:
 *   Tokenise each file in corpus_list, remove words in stopwdlist
 *   and reduce the term set by a factor of aggr.
 * 
 * ARGUMENTS
 *  tf_method: term filtering method. One of: 
 *          'df': document frequency, local,
 *          'dfg': document frequency, global,
 *          'ig': information gain.
 *          'gss': GSS coefficient
 * 
 * categ: target category (e.g. 'acq'.) for local term filtering OR
 *          a method for combining local scores. One of:
 *             '_DFG' (global document frequency),
 *             '_MAX' (maximum local score),
 *             '_SUM' (sum of local scores),
 *             '_WAVG' (sum of local scores wbeighted by category generality.)
 * </pre>
 * 
 * @author Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: MakeReducedTermSet.java,v 1.1 2004/03/29 14:27:59
 *          luzs Exp $</font>
 * @see ProbabilityModel
 * @see TermFilter
 * @see NewsParser <<<<<<< HEAD
 */

public class MakeReducedTermSet {
	private static CorpusList clist = null;
	private static StopWordList swlist = null;
	private static int aggressiveness = 0;

	/**
	 * Set up the main user interface items
	 */
	public MakeReducedTermSet(String clist, String swlist, String aggr) {
		super();
		this.clist = new CorpusList(clist);
		this.swlist = new StopWordList(swlist);
		this.aggressiveness = (new Integer(aggr)).intValue();
		return;
	}

	/**
	 * parseNews: Set up parser object, perform parsing, and print indented
	 * contents onto stdout (for test purposes only)
	 */
	public ParsedText parseNews(String filename) {
		NewsParser np = new NewsParser(filename);
		System.err.println("text parsed");
		return np.getParsedText();
	}

	private void computeScores(TermFilter tf, String methodOrCat) {
		if (methodOrCat.equals("_MAX"))
			tf.computeGlobalScoresMAX();
		else if (methodOrCat.equals("_WAVG"))
			tf.computeGlobalScoresWAVG();
		else if (methodOrCat.equals("_SUM"))
			tf.computeGlobalScoresSUM();
		else if (methodOrCat.equals("_DFG"))
			((DocumentFrequency) tf).computeGlobalDocFrequency();
		else
			tf.computeLocalScores(methodOrCat);
	}

	public WordScorePair[] rank(String method, ProbabilityModel pm,
			String methodOrCat) {
		System.err.println("Starting filtering...");
		WordScorePair[] wsp = null;
		if (method.equals("gss")) {
			System.err.println("Filtering term set by GSS coefficient.");
			GSScoefficient tf = new GSScoefficient(pm);
			computeScores(tf, methodOrCat);
			wsp = tf.getSortedScores();
		} else if (method.equals("or")) {
			System.err.println("Filtering term set by Odds Ratio");
			OddsRatio tf = new OddsRatio(pm);
			computeScores(tf, methodOrCat);
			wsp = tf.getSortedScores();
		} else if (method.equals("ig")) {
			System.err.println("Filtering term set by info gain");
			InfoGain tf = new InfoGain(pm);
			computeScores(tf, methodOrCat);
			wsp = tf.getSortedScores();
		} else if (method.equals("dfg")) {
			System.err
					.println("Filtering term set by document frequency (global)");
			DocumentFrequency tf = new DocumentFrequency(pm);
			computeScores(tf, methodOrCat);
			wsp = tf.getSortedScores();
		} else {
			System.err.println("Filtering term set by document frequency");
			DocumentFrequency tf = new DocumentFrequency(pm);
			computeScores(tf, methodOrCat);
			wsp = tf.getSortedScores();
		}
		return wsp;
	}

	public static void main(String[] args) {
		try {
			MakeReducedTermSet f = new MakeReducedTermSet(args[0], args[1],
					args[2]);
			String termFilter = args[3];
			String category = args[4];
			ProbabilityModel pm = new ProbabilityModel();
			for (Enumeration e = f.clist.elements(); e.hasMoreElements();) {
				String fname = (String) e.nextElement();
				System.err.print("\n----- Processing: " + fname + " ------\n");
				pm.addParsedText(f.parseNews(fname), swlist);
			}
			System.err.println("Probability Model size " + pm.getTermSetSize());
			WordScorePair[] wsp = f.rank(termFilter, pm, category);
			int size = pm.getTermSetSize();
			int rsize = size / aggressiveness;
			WordFrequencyPair[] rwfp = new WordFrequencyPair[rsize];
			int j = 0;
			System.err.println("Reducing T from " + size + " to " + rsize);
			int stop = size - rsize - 1;
			for (int i = size - 1; i > stop; i--)
				System.out
						.println(wsp[i].getWord() + " = " + wsp[i].getScore());
		} catch (Exception e) {
			System.err
					.println("\nUsage: MakeReducedTermSet CORPUS_LIST STOPWDLIST AGGRESSIVENESS TF_METHOD CATEG");
			System.err
					.println("       tokenise each file in CORPUS_LIST, remove words in STOPWDLIST");
			System.err
					.println("       and reduce the term set by a factor of AGGRESSIVENESS.\n");
			System.err.println(" TF_METHOD: term filtering method. One of:");
			System.err.println("            'df' (document frequency),");
			System.err.println("            'ig' (information gain)");
			System.err.println("            'gss' (GSS coefficient)");
			System.err.println("            'or' (Odds ratio)");
			System.err.println(" CATEG: target category (e.g. 'acq'.) or");
			System.err
					.println("    a method for combining local scores. One of:");
			System.err
					.println("            '_DFG' (global document frequency),");
			System.err.println("            '_MAX' (maximum local score),");
			System.err.println("            '_SUM' (sum of local scores),");
			System.err
					.println("            '_WAVG' (sum of local scores wbeighted by category generality),");
			e.printStackTrace();
		}
	}
}
