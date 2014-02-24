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
	private Set<String> termSet = new HashSet<String>();
	private Map<String, Map<String, Integer>> termCount_cat = new HashMap<String, Map<String, Integer>>();
	private int totalTermCountinCorpus = 0;
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
		for (int i = 0; i < pt.size(); ++i) {
			ParsedNewsItem pni = (ParsedNewsItem) pt.get(i);
			addParsedNewsItem(pni, swlist);
		}
	}

	public List<String> getTerms(String id) {
		List<String> terms = new ArrayList<String>();
		for (String key : tTable.keySet()) {
			Map internal_map = tTable.get(key);
			if (internal_map.keySet().contains(id)) {
				terms.add(key);
			}
		}
		return terms;
	}

	public Set<String> getTermId(String cat) {
		return cTable.get(cat);
	}

	public Integer getDocCount(String cat) {
		return cTable.get(cat).size();
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
		BagOfWords bow = new BagOfWords(pni.getText(), swlist);
		String id = pni.getId();
		String[] terms = bow.getTermSet();
		termSet.addAll(Arrays.asList(terms));

		Vector<String> cat = pni.getCategVector();

		Map<String, Integer> termcount = new HashMap<String, Integer>();
		docSet.add(id);

		for (String term : terms) {
			++totalTermCountinCorpus;
			if (termcount.containsKey(term))
				termcount.put(term, termcount.get(term) + 1);
			else
				termcount.put(term, 1);
		}

		for (String term : termcount.keySet())
			putIntoTTable(term, id, termcount.get(term));

		for (String categ : cat) {
			putIntoCTable(categ, id);
			if (termCount_cat.containsKey(categ)) {
				for (String term : terms) {
					Map<String, Integer> internal_map = null;
					if (termCount_cat.get(categ).containsKey(term)) {
						internal_map = termCount_cat.get(categ);
						internal_map.put(term, internal_map.get(term) + 1);
					} else {
						internal_map = new HashMap<String, Integer>();
						internal_map.put(term, 1);
					}
					termCount_cat.put(categ, internal_map);
				}
			} else {
				Map<String, Integer> internal_map = new HashMap<String, Integer>();
				int count = 0;
				for (String term : terms) {
					if (count == 0) {
						internal_map.put(term, 1);
						termCount_cat.put(categ, internal_map);
						++count;
					} else {
						if (termCount_cat.get(categ).containsKey(term)) {
							internal_map = termCount_cat.get(categ);
							internal_map.put(term, internal_map.get(term) + 1);
						} else {
							internal_map = new HashMap<String, Integer>();
							internal_map.put(term, 1);
						}
						termCount_cat.put(categ, internal_map);
					}
				}
			}
		}
	}

	// return set containing all categories that occur in the corpus
	public Set<String> getCategorySet() {
		return cTable.keySet();
	}

	public Set<String> getDocSet() {
		return docSet;
	}

	public Set<String> getTermSet() {
		return termSet;
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
		return getDocCount(cat) / getDocSet().size();
	}

	public int getTotalTermCount() {
		int count = 0;
		for (String each_key : tTable.keySet()) {
			Map<String, Integer> internal_tTable = tTable.get(each_key);
			for (String each_id : internal_tTable.keySet())
				count += internal_tTable.get(each_id);
		}
		return count;
	}

	public int getTermCountInCorpus(String term) {
		int count = 0;
		Map<String, Integer> internal_tTable = tTable.get(term);
		for (String each_id : internal_tTable.keySet())
			count += internal_tTable.get(each_id);
		return count;
	}

	public int getTermCountInCategory(String term, String cat) {
		int count = 0;
		for (Integer val : termCount_cat.get(cat).values())
			count += val;
		return count;
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

		// pT = total count of Term T in Corpus / total number of terms in
		// Corpus
		double pT = (double) getTermCount(term)
				/ (double) getDocSet().size();

		// pC = total count of Docs in Category C / total number of documents in
		// Corpus
		double pC = (double) getDocCount(cat) / (double) getDocSet().size();

		// pTAndC = total count of term T in Category C / total number of terms
		// in Corpus
		double pTAndC = ((double) getDocCount(term, cat)
				/ (double) getDocSet().size()) * (pC); //

		// pTAnd_C = total count of term T not in Category C / total number of
		// terms in Corpus //P(t, ^c) = P(t) − P(t, c)
		double pTAnd_C = pT - pTAndC;

		// p_TAndC //P(^t, c) = (1 − P(t|c))P(c))
		double p_TAndC = 1 - pTAndC;

		// p_TAnd_C //P (^t, ^c) = (1 − P(t))P(^t, c)
		double p_TAnd_C = (1-pT)*p_TAndC;

		System.out.println(pT + ":" + pC + ":" + pTAndC + ":" + pTAnd_C + ":"
				+ p_TAndC + ":" + p_TAnd_C);

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

	public int getDocCount(String term, String cat) {
		Set<String> doc_ids_t = tTable.get(term).keySet();
		Set<String> doc_ids_c = cTable.get(cat);
		Set<String> docs_with_term_cat = new HashSet<String>();
		for (String docs : doc_ids_t) {
			if (doc_ids_c.contains(docs))
				docs_with_term_cat.add(docs);
		}
		return docs_with_term_cat.size();
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
