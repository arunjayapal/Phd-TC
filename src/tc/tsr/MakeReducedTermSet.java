/** 
 * Parse Reuters file, perform term filtering and print the reduced
 * term set with the score for each term.
 *
 **/
package tc.tsr;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
	@SuppressWarnings("unused")
	private static int aggressiveness = 0;

	/**
	 * Set up the main user interface items
	 */
	@SuppressWarnings("static-access")
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
		Set<String> global_scores = new HashSet<String>(Arrays.asList("_MAX",
				"_SUM", "_WAVG"));
		if (method.equals("ig")) {
			InfoGain ig = new InfoGain(pm);
			for (WordScorePair obj : wsp) {
				if (methodOrCat.equals("_MAX"))
					obj.setScore(ig.computeGlobalScoresMAX(obj.getWord()));
				else if (methodOrCat.equals("_SUM"))
					obj.setScore(ig.computeGlobalScoresSUM(obj.getWord()));
				else if (methodOrCat.equals("_WAVG"))
					obj.setScore(ig.computeGlobalScoresWAVG(obj.getWord()));
				else
					obj.setScore(ig.computeLocalTermScore(obj.getWord(),
							methodOrCat));
			}
		} else if (method.equals("df")) {
			DocumentFrequency df = new DocumentFrequency(pm);
			for (WordScorePair obj : wsp) {
				if (methodOrCat.equals("_MAX"))
					obj.setScore(df.computeGlobalScoresMAX(obj.getWord()));
				else if (methodOrCat.equals("_SUM"))
					obj.setScore(df.computeGlobalScoresSUM(obj.getWord()));
				else if (methodOrCat.equals("_WAVG"))
					obj.setScore(df.computeGlobalScoresWAVG(obj.getWord()));
				else
					obj.setScore(df.computeLocalTermScore(obj.getWord(),
							methodOrCat));
			}
		} else {
			System.err.println("The command line parameters should be checked again");
			System.exit(0);
		}
		
		return wsp;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Double> sortWordScorePairs(WordScorePair[] wsp) {
		HashMap<String, Double> temp_map_ = new HashMap<String, Double>();
		for (WordScorePair obj : wsp)
			temp_map_.put(obj.getWord(), obj.getScore());
		// ValueComparator bvc = new ValueComparator(temp_map_);
		// TreeMap<String, Double> sorted_map_ = new TreeMap<String, Double>(
		// temp_map_);
		Map<String, Double> sorted_map_ = sortByComparator(temp_map_);
		return sorted_map_;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map sortByComparator(Map unsortMap) {

		List list = new LinkedList(unsortMap.entrySet());

		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return -1
						* ((Comparable) ((Map.Entry) (o1)).getValue())
								.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// put sorted list into map again
		// LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	@SuppressWarnings({ "rawtypes" })
	public Map<String, Double> getReducedTermSet(String termFilter, String category, ProbabilityModel pm) {
		try {
			
			for (Enumeration e = clist.elements(); e.hasMoreElements();) {
				String fname = (String) e.nextElement();
				System.err.print("\n----- Processing: " + fname + " ------\n");
				pm.addParsedText(parseNews(fname), swlist);
			}
			System.err.println("Probability Model size " + pm.getTermSetSize());
			WordScorePair[] wsp = rank(termFilter, pm, category);
			System.gc(); // garbage collection
			System.err.println("Reducing Term set");
			// **** Lab02 exercise: reduce the term set (according to
			// 'aggressiveness') and print out each
			// **** term in the reduced set next to its score
			Map<String, Double> sorted = sortWordScorePairs(wsp);
			try {
				int tmp_count = 0;
				aggressiveness = sorted.size() / aggressiveness;
				for (String items : sorted.keySet()) {
//					System.out.println(items + ":" + sorted.get(items));
					if ((sorted.get(items).compareTo(Double.NaN) != 0)
							&& tmp_count <= aggressiveness) {
						System.out.println(items + ":" + sorted.get(items));
						++tmp_count;
					} else if (tmp_count > aggressiveness)
						break;
					else
						continue;
				}
			} catch (NullPointerException npe) {
				System.err.println("Nothing in the array");
			}
			return sorted;

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
		return null;
	}
}

class ValueComparator implements Comparator<String> {

	Map<String, Double> base;

	public ValueComparator(Map<String, Double> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}