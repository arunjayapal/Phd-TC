/** 
 * Parse Reuters file, perform term filtering and print the reduced
 * term set with the score for each term.
 *
 **/
package tc.tsr;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import tc.dstruct.CorpusList;
import tc.dstruct.ParsedText;
import tc.dstruct.ProbabilityModel;
import tc.dstruct.StopWordList;
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
 *          'df': document frequency,
 *          'ig': information gain.
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
 * @see NewsParser
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
	 * 
	 * @throws Exception
	 */
	public ParsedText parseNews(String filename) throws Exception {
		NewsParser np = new NewsParser(filename);
		System.err.println("text parsed");
		return np.getParsedText();
	}

	/**
	 * ******* LAB 02 exercise: implement a method to rank the term set
	 * according to a term filtering technique (by using a subclass of
	 * TermFilter; either InfoGain or DocumentFrequency). If a methodOrCat is a
	 * category, rank locally w.r.t that category, otherwise rank globally by
	 * combining local scores as requested (_WAVG, _SUM, or _MAX)
	 */
	public WordScorePair[] rank(String method, ProbabilityModel pm,
			String methodOrCat) {
		System.err.println("Starting filtering...");
		WordScorePair[] wsp = pm.getWordScoreArray();
		System.out.println(methodOrCat);
		if (method.equals("ig")){
			InfoGain ig = new InfoGain(pm);
			for (WordScorePair obj:wsp) {
				System.out.println(obj.getWord());
				System.out.println(ig.computeLocalTermScore(obj.getWord(), methodOrCat));
//				obj.setScore(ig.computeLocalTermScore(obj.getWord(), methodOrCat));
			}
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
			System.err.println("Reducing Term set");
//			try {
//				for (int i = 0; i < wsp.length; i++) {
//					System.out.println(wsp[i].getWord() + ":"
//							+ wsp[i].getScore());
//				}
//			} catch (NullPointerException npe) {
//				System.err.println("Nothing in the array");
//			}
			// **** Lab02 exercise: reduce the term set (according to
			// 'aggressiveness') and print out each
			// **** term in the reduced set next to its score
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
			System.err.println(" CATEG: target category (e.g. 'acq'.) or");
			System.err
					.println("    a method for combining local scores. One of:");
			System.err
					.println("            '_DFG' (global document frequency),");
			System.err.println("            '_MAX' (maxioutmum local score),");
			System.err.println("            '_SUM' (sum of local scores),");
			System.err
					.println("            '_WAVG' (sum of local scores weighted by category generality),");
			e.printStackTrace();
		}
	}
}
