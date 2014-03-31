/** 
 * Parse Reuters file, perform term filtering and generate a
 * probability table containing joint term-category probabilities and 
 * a few extra things.
 *
 * (S. Luz, luzs@cs.tcd.ie)
 **/
package tc.induction;

import tc.dstruct.*;
import tc.parser.*;
import tc.tsr.*;
import tc.util.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Set;
import java.util.Arrays;

/**
 * Parse Reuters file, perform term filtering, and generate a probability table
 * containing joint term-category probabilities and a few extra things, and
 * saves it to disk (as a serialized object.)
 * 
 * Usage:
 * 
 * <pre>
 *  MakeProbabilityModel corpus_list stopwdlist aggr tf_method categ pmfile
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
 * 
 *  categ: target category (e.g. 'acq'.) for local term filtering OR
 *          a method for combining local scores. One of:
 *             '_DFG' (global document frequency),
 *             '_MAX' (maximum local score),
 *             '_SUM' (sum of local scores),
 *             '_WAVG' (sum of local scores wbeighted by category generality.)
 * 
 *  pmfile: name of output file for probability model.
 * </pre>
 * 
 * @author Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: MakeProbabilityModel.java,v 1.2 2004/03/29
 *          14:29:33 luzs Exp $</font>
 * @see ProbabilityModel
 * @see TermFilter
 * @see NewsParser
 */

public class MakeProbabilityModel {

	private static CorpusList clist = null;
	private static StopWordList swlist = null;
	private static int aggressiveness = 0;

	/**
	 * Set up the main user interface items
	 */
	public MakeProbabilityModel(String clist, String swlist, String aggr) {
		super();
		this.clist = new CorpusList(clist);
		this.swlist = new StopWordList(swlist);
		this.aggressiveness = (new Integer(aggr)).intValue();
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

	public Set filter(String method, ProbabilityModel pm, String categ) {
		System.err.println("Starting filtering...");
		// NB: rts = 'reduced term set'
		Set rts = null;
		if (method.equals("gss")) {
			System.err.println("Filtering term set by GSS coefficient");
			GSScoefficient tf = new GSScoefficient(pm);
			computeScores(tf, categ);
			rts = tf.getReducedTermSet(aggressiveness);
			tf = null;
		} else if (method.equals("or")) {
			System.err.println("Filtering term set by odds ratio");
			OddsRatio tf = new OddsRatio(pm);
			computeScores(tf, categ);
			rts = tf.getReducedTermSet(aggressiveness);
			tf = null;
		} else if (method.equals("ig")) {
			System.err.println("Filtering term set by info gain");
			InfoGain tf = new InfoGain(pm);
			computeScores(tf, categ);
			rts = tf.getReducedTermSet(aggressiveness);
			tf = null;
		} else if (method.equals("dfg")) {
			System.err
					.println("Filtering term set by document frequency (global)");
			DocumentFrequency tf = new DocumentFrequency(pm);
			computeScores(tf, categ);
			rts = tf.getReducedTermSet(aggressiveness);
			tf = null;
		} else {
			System.err.println("Filtering term set by document frequency");
			DocumentFrequency tf = new DocumentFrequency(pm);
			computeScores(tf, categ);
			rts = tf.getReducedTermSet(aggressiveness);
			tf = null;
		}
		return rts;
	}

	public static void main(String[] args) {
		try {
			MakeProbabilityModel f = new MakeProbabilityModel(args[0], args[1],
					args[2]);
			String termFilter = args[3];
			String category = args[4];
			String pmfile = args[5];
			System.out.println(args[0] + ":" + args[1] + ":" + args[2]
					+ ":TermFilter" + termFilter + ":Category" + category
					+ ":pmfile" + pmfile);
			// System.exit(0);
			ProbabilityModel pm = new ProbabilityModel();
			for (Enumeration e = f.clist.elements(); e.hasMoreElements();) {
				String fname = (String) e.nextElement();
				System.err.print("\n----- Processing: " + fname + " ------\n");
				pm.addParsedText(f.parseNews(fname), swlist);
			}
			System.gc();
			System.err.println("Probability Model size " + pm.getTermSetSize());
			Set rts = f.filter(termFilter, pm, category);
			System.out.println(rts);
			System.out.println("Am heeeeeeeeeee..");
			pm.trimTermSet(rts);
			System.err.println("Saving probability model for "
					+ pm.getTermSetSize() + " terms into " + pmfile);
			IOUtil.dumpProbabilityModel(pm, pmfile);
		} catch (Exception e) {
			System.err
					.println("\nUsage: MakeProbabilityModel CORPUS_LIST STOPWDLIST AGGRESSIVENESS TF_METHOD CATEG PMFILE");
			System.err
					.println("       tokenise each file in CORPUS_LIST, remove words in STOPWDLIST");
			System.err
					.println("       and reduce the term set by a factor of AGGRESSIVENESS.\n");

			System.err
					.println(" TF_METHOD: term filtering method. One of: 'df' (document frequency, local),");
			System.err
					.println("            'dfg' (document frequency, global),");
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
					.println("            '_WAVG' (sum of local scores weighted by category generality),");
			System.err.println(" PMFILE: output probability file\n");
			// e.printStackTrace();
		}
	}
}
