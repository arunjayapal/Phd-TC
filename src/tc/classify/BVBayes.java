package tc.classify;

import tc.parser.*;
import tc.dstruct.*;
import tc.evaluation.*;
import tc.induction.MakeProbabilityModel;
import tc.util.*;

import java.util.Set;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Naive Bayes classifier for documents represented as Boolean vectors. This
 * class uses the CSV described in the lecture notes
 * (http://www.cs.tcd.ie/courses/baict/baim/ss/part2/ctinduction-4up.pdf,
 * equation (5)) and (Sebastiani 2002). CSV values are not probabilities but the
 * function is monotonically increasing on the estimated probability function.
 * 
 * BVBayes: Categorise each news item in corpus_list according to categ using
 * Boolean Vector Naive Bayes (see lecture notes ctinduction.pdf, p 7)
 * 
 * Usage:
 * 
 * <pre>
 *  BVBayes corpus_list categ prob_model 
 * 
 * SYNOPSIS:
 *   Categorise each news item in corpus_list according to categ using
 *   Boolean Vector Naive Bayes (see lecture notes ctinduction.pdf, p 7)
 * 
 * ARGUMENTS
 *  corpus_list: list of files to be classified
 * 
 *  categ: target category (e.g. 'acq'.) The classifier will define CSV 
 *         as CSV_{categ}
 * 
 *  pmfile: file containing a  probability model generated via, say, 
 *          tc.induction.MakeProbabilityModel.
 * </pre>
 * 
 * @author
 * @version <font size=-1>$Id: BVBayes.java,v 1.2 2004/03/29 14:29:33 luzs Exp
 *          $</font>
 * @see ProbabilityModel
 * @see NewsParser
 */

public class BVBayes {
	private static CorpusList clist = null;
	private static ProbabilityModel pm = null;

	/**
	 * Set up the main user interface items
	 */
	public BVBayes(String clist, String pmfile) {
		super();
		this.clist = new CorpusList(clist);
		this.pm = IOUtil.loadProbabilityModel(pmfile);
		System.err.println("Probability model loaded successfully\n");
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

	/**
	 * CSV_i(d_j) = \sum_0^T tkj log p(t|c) * (1 - p(t|�c) / p(t|�c) * (1 -
	 * p(t|c)
	 * 
	 * (where tkj \in {0, 1} is the binary weight at position k in vector d_j;
	 * multiplying by it causes terms that do not occur in the document to be
	 * ignored)
	 * 
	 * CAVEAT: larger documents receive disproportionally large CSV's simply by
	 * virtue of containing more terms. Document lenght normalisation could be
	 * employed here to correct this distortion. A simpler approach would be to
	 * base the probability model on term events rather than document events, as
	 * described in (Mitchell 97, ch 6).
	 */
	public double computeCSV(String cat, ParsedNewsItem pni) {
		/**
		 * ******************** Lab 04: Exercise ********************* implement
		 * the CSV as specified above.
		 */
		BagOfWords word_map = new BagOfWords(pni.getText());
		double csv = 0.0;
		for (Object words : word_map.keySet()) {
			Probabilities probs = pm.getProbabilities((String) words, cat);
			Integer ti = 0;
			if (word_map.keySet().contains((String) words))
				ti = 1;
			// consider p(a|b) = p(a^b)/p(b)
			Double p_TgivenC = (double) probs.tc / (double) probs.c;
			Double p_TgivenNotC = (double) probs.tnc / (double) (1 - probs.c);
			csv += ti
					* (double) Maths.safeLog2((p_TgivenC * (1 - p_TgivenNotC))
							/ (p_TgivenNotC * (1 - p_TgivenC)));
		}
		return csv;
	}

	public static void main(String[] args) {
		try {
			String clistfn = args[0];
			String category = args[1];
			String pmfile = args[2];
			ThresholdStrategy tstrategy = new ThresholdStrategy(args[3]);
			CSVTable csvt = new CSVTable(category);
			/**
			 * ******************** Lab 04: Exercise *********************
			 * Implement the main method of the classifier. This method will:
			 * 
			 * - load the probability model (created by
			 * tc.induction.MakeProbabilityModel), - parse each file in the file
			 * list (clistfn), - obtain a classification status value (CSV) for
			 * each news item in the resulting ParsedText by calling
			 * computeCSV() [which you also must implement], - store these CSVs
			 * along with each document's true classification in (
			 * tc.evaluation.CSVTable), - perform hard classification by
			 * applying a threshold (or thresholding strategy, e.g. proportional
			 * thresholding) to the CSV results, and - print a summary of
			 * evaluation results (see below) for the entire test set at the end
			 * of processing.
			 * 
			 * N.B.: this file won't compile unless CSVTable is implemented.
			 */
			BVBayes bayes = new BVBayes(clistfn, pmfile);
			for (Object file : clist) {
				ParsedText pt = bayes.parseNews((String) file);
				for (Iterator i = pt.iterator(); i.hasNext();) {
					ParsedNewsItem pni = (ParsedNewsItem) i.next();
					double csv = bayes.computeCSV(category, pni);
					csvt.setCSV(pni.getId(), csv);
					csvt.setOrigcat(pni.getId(), pni.getCategVector());
				}
			}

//			System.out.println(csvt.keySet());

			if (tstrategy.proportional)
				csvt.applyProportionalThreshold(pm.getCatGenerality(category));
			else
				// UCut strategy (RCut will not be implemented)
				csvt.applyUCutThreshold(tstrategy.threshold);
			System.out.println("Classification results for " + category);
			System.out.println("EFFECTIVENESS:\n" + "  accuracy = "
					+ csvt.getAccuracy() + "  precision = "
					+ csvt.getPrecision() + "  recall = " + csvt.getRecall());
		} catch (Exception e) {
			System.err.println("USAGE:");
			System.err
					.println(" BVBayes corpus_list categ prob_model threshold\n");
			System.err.println("SYNOPSIS:");
			System.err
					.println("  Categorise each news item in corpus_list according to categ using");
			System.err
					.println("  Boolean Vector Naive Bayes (see lecture notes ctinduction.pdf, p 7)\n");
			System.err.println("ARGUMENTS:");
			System.err
					.println(" corpus_list: list of files to be classified\n");
			System.err
					.println(" categ: target category (e.g. 'acq'.) The classifier will define CSV ");
			System.err.println("     as CSV_{categ}\n");
			System.err
					.println(" pmfile: file containing a  probability model generated via, say, ");
			System.err.println("     tc.induction.MakeProbabilityModel.\n");
			System.err
					.println(" threshold: a real number (for UCut thresholding) or the name of a");
			System.err
					.println("     thresholding strategy. Currently supported strategy:");
			System.err
					.println("      - 'proportional': choose threshold s.t. that g_Tr(ci) is");
			System.err.println("         closest to g_Tv(ci). [DEFAULT]");
			System.err.println(" ...");
			e.printStackTrace();
		}
	}
}

/*
 * System.err.println(" WORD: "+term+"\n" +" P(c)  ="+p.c+"\n"
 * +" P(t,c)  ="+p.tc+"\n" +" P(t,�c)  ="+p.tnc+"\n"
 * +" P(t|c)  ="+p.getPTgivenC()+"\n" +" P(t|�c) ="+p.getPTgiven_C()+"\n"
 * +" partial CSV ="+ Maths.safeLog2((p.getPTgivenC() * (1 - p.getPTgiven_C()))
 * / (p.getPTgiven_C() * (1 - p.getPTgivenC())))+"\n");
 */

// System.out.println("NEWSID: "+pni.getId()+"\n"
// +"CATEGS: "+ARFFUtil.toString(pni.getCategVector())+"\n"
// +"CVS_"+category+": "+csv+"\n");
