package tc.dstruct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import tc.parser.Tokenizer;
import tc.util.PrintUtil;

/**
 * Store inverted indices of terms and categories (indexed to documents) which
 * form the basis of a probability model (see tTable and cTable vars below), and
 * implement methods to estimate probabilities based on these indices.
 * 
 * @author S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ProbabilityModel.java,v 1.6 2004/03/29 14:11:18
 *          luzs Exp $</font>
 * @see
 */
public class ProbabilityModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean ignoreCase = true;
	/**
	 * term table: ( term1 , [(doc_id1, No_of_occurrences_of_term1_in_id1), ...,
	 * (doc_idn, No_of_occurrences_of_term1_in_idn)], ... termk , [(doc_idm,
	 * No_of_occurrences_of_termk_in_idm), ..., (doc_idz,
	 * No_of_occurrences_of_termk_in_idz)]) where term is a String and the
	 * elements of its Map value are No. of occurences, indexed by doc_id's.
	 */
	private Map<String, Map<String, Integer>> tTable = new HashMap<String, Map<String, Integer>>();

	/**
	 * category table: ( cat1 , [id1, ..., idn] ... catk , [id1, ..., idn] )
	 * where cat (the key) is a String and the elements of its HashSet values
	 * are Strings (the same type as IDs, above)
	 */
	private Map<String, Set<String>> cTable = new HashMap<String, Set<String>>();
	/**
	 * store the set of documents that make up this ProbabilityModel.
	 */
	private Set<String> docSet = new HashSet<String>();
	public int corpusSize = 0;

	public ProbabilityModel() {
	}

	public ProbabilityModel(ParsedText pt, StopWordList swlist) {
		addParsedText(pt, swlist);
	}

	/**
	 * ******************** Lab 02: Exercise *********************
	 * 
	 * Implement addParsedText. This method will: receive a ParsedText, tokenise
	 * each text it contains, index the resulting terms (except the ones
	 * contained in the StopWordList), and store the text's category(ies). See
	 * also addParsedNewsItem, which will actually do the indexing.
	 */
	public void addParsedText(ParsedText pt, StopWordList swlist) {
		for (Iterator i = pt.iterator(); i.hasNext();) {
			corpusSize++;
			PrintUtil.printNoMove("Generating prob models ...", corpusSize);
			addParsedNewsItem((ParsedNewsItem) i.next(), swlist);
		}
		PrintUtil.donePrinting();
	}

	/**
	 * ******************** Lab 02: Exercise *********************
	 * 
	 * Lab 02: tokenise pni, and index its terms (except the ones contained in
	 * swlist) and categories, that is: put each term (word) into tTable and
	 * each category the document belongs to into cTable. As each document is
	 * processed, store its ID into docSet, so as to keep track of which
	 * documents where used in generating this ProbabilityModel.
	 * 
	 * TIP: Use BagOfWords for tokenising pni.
	 */
	@SuppressWarnings("unchecked")
	public void addParsedNewsItem(ParsedNewsItem pni, StopWordList swlist) {
		String id = pni.getId();
		docSet.add(id);
		// System.err.println("Generating set of words for text ID "+id);
		WordFrequencyPair[] wfp = (new BagOfWords(pni.getText(), swlist))
				.getWordFrequencyArray();
		// System.err.println("Updating terms index");
		for (int i = 0; i < wfp.length; i++)
			putIntoTTable(wfp[i].getWord(), id, wfp[i].getIntegerCount());
		// System.err.println("Updating categories index");
		for (Iterator k = pni.getCategVector().iterator(); k.hasNext();)
			putIntoCTable((String) k.next(), id);
	}

	// return set containing all categories that occur in the corpus
	public Set<String> getCategorySet() {
		return cTable.keySet();
	}

	public Set<String> getDocSet() {
		return docSet;
	}

	/**
	 * Add file (id) to the set of files categorised as cat Return true if cat
	 * is new to cTable, false otherwise.
	 */
	private Object putIntoTTable(String term, String id, Integer count) {
		Map<String, Integer> idset = tTable.get(term);
		if (idset == null)
			idset = new HashMap<String, Integer>();
		idset.put(id, count);
		return tTable.put(term, idset);
	}

	/**
	 * Add file (id) to the set of files categorised as cat Return true if cat
	 * is new to cTable, false otherwise.
	 */
	private Object putIntoCTable(String cat, String id) {
		Set<String> idset = cTable.get(cat);
		if (idset == null)
			idset = new HashSet<String>();
		idset.add(id);
		return cTable.put(cat, idset);
	}

	public boolean containsTerm(String term) {
		return tTable.containsKey(term);
	}

	/**
	 * ******************** Lab 02: Exercise *********************
	 * 
	 * Calculate (and return) the generality of category 'cat' with respect to
	 * this model
	 */
	public double getCatGenerality(String cat) {
		// *** this return statement is just a place holder. you'll need to
		// modify it
		boolean barcat = false;
		if (Tokenizer.isBar(cat)) {
			cat = Tokenizer.disbar(cat);
			barcat = true;
		}
		Set cs = (Set) cTable.get(cat);
		int css = cs == null ? 0 : cs.size();
		double c = (double) css / corpusSize; // p(c)
		return barcat ? 1 - c : c;
	}

	/**
	 * ******************** Lab 02: Exercise *********************
	 * 
	 * Estimate the prior probabilities of term and cat, the joint probabilities
	 * for each (Boolean) value combination for these two variables (as defined
	 * in class Probabilities) and return a Probabilities object.
	 */
	public Probabilities getProbabilities(String term, String cat) {
		// *** this return statement is just a place holder. you'll need to
		// modify it
		Set ts = tTable.containsKey(term) ? ((Map) tTable.get(term)).keySet()
				: null;
		Set cs = (Set) cTable.get(cat);
		int tss = ts == null ? 0 : ts.size();
		int css = cs == null ? 0 : cs.size();
		int iss = 0; // intersection of ts and cs (couldn't be done with
						// ts.retainAll(cs) since retainAll is destructive)
		if (tss > 0 && css > 0)
			for (Iterator i = ts.iterator(); i.hasNext();)
				if (cs.contains(i.next()))
					iss++;
		// pT = total count of Term T in Corpus / total number of terms in
		// Corpus

		double pT = (double) tss / (double) corpusSize;

		// pC = total count of Docs in Category C / total number of documents in
		// Corpus
		double pC = (double) css / (double) corpusSize;

		// pTAndC = total count of term T in Category C / total number of terms
		// in Corpus
		double pTAndC = (double) iss / (double) corpusSize; //

		// pTAnd_C = total count of term T not in Category C / total number of
		// terms in Corpus //P(t, ^c) = P(t) − P(t, c)
		double pTAnd_C = (double) (tss - iss) / (double) corpusSize;

		// p_TAndC //P(^t, c) = (1 − P(t|c))P(c))
		double p_TAndC = (double) (css - iss) / (double) corpusSize;

		// p_TAnd_C //P (^t, ^c) = (1 − P(t))P(^t, c)

		double p_TAnd_C = (double) (corpusSize - (css + tss - iss))
				/ (double) corpusSize;

		// System.out.println(pT + ":" + pC + ":" + pTAndC + ":" + pTAnd_C + ":"
		// + p_TAndC + ":" + p_TAnd_C);

		return new Probabilities(pT, pC, pTAndC, pTAnd_C, p_TAndC, p_TAnd_C);
	}

	public int getTermSetSize() {
		return tTable.size();
	}

	/**
	 * Delete all entries for terms not in the reduced term set
	 */
	public void trimTermSet(Set<String> rts) {
		tTable.keySet().retainAll(rts);
	}

	/**
	 * Delete all entries for terms not in the reduced term set
	 */
	public void trimTermSet(WordFrequencyPair[] rts) {
		tTable.keySet().retainAll(BagOfWords.extractTermCollection(rts));
	}

	public int getCategSetSize() {
		return cTable.size();
	}

	// Return the number of terms in id
	public int getCount(String id, String term) {
		Integer count = (Integer) tTable.get(term).get(id);
		if (count == null)
			return 0;
		else
			return count.intValue();
	}

	// return the vector of categories to which document id belongs
	// (shouldn't this return a set instead?)
	public Vector getCategVector(String id) {
		Vector cv = new Vector();
		for (Iterator e = cTable.entrySet().iterator(); e.hasNext();) {
			Map.Entry kv = (Map.Entry) e.next();
			if (((HashSet) kv.getValue()).contains(id))
				cv.add(kv.getKey());
		}
		return cv;
	}

	// make a new wsp[] with scores initialised to zero
	public WordScorePair[] getBlankWordScoreArray() {
		WordScorePair[] wsp = new WordScorePair[tTable.size()];
		int i = 0;
		for (Iterator e = tTable.entrySet().iterator(); e.hasNext();) {
			Map.Entry kv = (Map.Entry) e.next();
			wsp[i++] = new WordScorePair((String) kv.getKey(), 0);
		}
		return wsp;
	}

	// gets an initialised wsp and populate it with global term frequency
	public WordScorePair[] setFreqWordScoreArray(WordScorePair[] wsp) {
		int i = 0;
		for (Iterator e = tTable.entrySet().iterator(); e.hasNext();) {
			Map.Entry kv = (Map.Entry) e.next();
			wsp[i++] = new WordScorePair((String) kv.getKey(),
					(double) ((HashMap) kv.getValue()).size());
		}
		return wsp;
	}

	public WordScorePair[] getWordScoreArray() {
		WordScorePair[] wsp = new WordScorePair[tTable.size()];
		int i = 0;
		for (Iterator e = tTable.entrySet().iterator(); e.hasNext();) {
			Map.Entry kv = (Map.Entry) e.next();
			wsp[i++] = new WordScorePair((String) kv.getKey(),
					(double) ((HashMap) kv.getValue()).size());
		}
		return wsp;
	}

	/**
	 * Get the number of files a term occurs in
	 * 
	 * @return number of files a term occurs in
	 */
	public int getTermCount(String term) {
		return tTable.get(term).size();
	}

	public boolean occursInCategory(String term, String cat) {
		Set<String> ds = tTable.get(term).keySet();
		Set<String> cs = cTable.get(cat);
		for (Iterator i = ds.iterator(); i.hasNext();)
			if (cs.contains(i.next()))
				return true;
		return false;
	}

	/**
	 * Get the value of ignoreCase.
	 * 
	 * @return value of ignoreCase.
	 */
	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	/**
	 * Set the value of ignoreCase.
	 * 
	 * @param v
	 *            Value to assign to ignoreCase.
	 */
	public void setIgnoreCase(boolean v) {
		this.ignoreCase = v;
	}

}
