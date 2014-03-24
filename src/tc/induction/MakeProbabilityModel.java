package tc.induction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import tc.dstruct.ProbabilityModel;
import tc.parser.NewsParser;
import tc.tsr.MakeReducedTermSet;
import tc.tsr.TermFilter;

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
 * @author
 * @version <font size=-1>$Id: MakeProbabilityModel.java,v 1.2 2004/03/29
 *          14:29:33 luzs Exp $</font>
 * @see ProbabilityModel
 * @see TermFilter
 * @see NewsParser
 */

public class MakeProbabilityModel {

	private static void saveProbModel(String PathToSave, ProbabilityModel pm) {
		try {
			if (!PathToSave.substring(PathToSave.length() - 1).equals("/"))
				PathToSave += "/";
			FileOutputStream fos = new FileOutputStream(PathToSave
					+ "prob_model.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(pm);
			oos.close();
			fos.close();
			System.err
					.println("Serialized Probability Model & data is saved in prob_model.ser");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static void serializeMap(Map<String, Double> sorted_map,
			String path_to_serialize) {
		try {
			if (!path_to_serialize.substring(path_to_serialize.length() - 1)
					.equals("/"))
				path_to_serialize += "/";
			FileOutputStream fos = new FileOutputStream(path_to_serialize
					+ "word_score.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(sorted_map);
			oos.close();
			fos.close();
			System.err
					.println("Serialized HashMap data is saved in hashmap.ser");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	private static ProbabilityModel loadProbModel(String path_to_load_model) {
		ProbabilityModel pm = null;
		try {
			FileInputStream fis = new FileInputStream(path_to_load_model);
			ObjectInputStream ois = new ObjectInputStream(fis);
			pm = (ProbabilityModel) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
		}
		System.err.println("Deserialized Probability model..");
		return pm;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Double> loadMap(String path_to_load_model) {
		Map<String, Double> map = null;
		try {
			FileInputStream fis = new FileInputStream(path_to_load_model);
			ObjectInputStream ois = new ObjectInputStream(fis);
			map = (Map) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
		}
		System.err.println("Deserialized Word score..");
		return map;
	}

	public static void main(String args[]) {
		try {
			String model_path = args[5], mod_word_score_model_ = null, mod_prob_model_ = null;
			String word_score_model_ = "word_score.ser", prob_model_ = "prob_model.ser";
			Map<String, Double> sorted_map = null;
			ProbabilityModel pm = null;
			if (!model_path.substring(model_path.length() - 1).equals("/")) {
				mod_word_score_model_ = model_path + "/" + word_score_model_;
				mod_prob_model_ = model_path + "/" + prob_model_;
			} else {
				mod_word_score_model_ = model_path + word_score_model_;
				mod_prob_model_ = model_path + prob_model_;
			}

			File f1 = new File(mod_word_score_model_), f2 = new File(
					mod_prob_model_);

			if (f1.exists() && !f1.isDirectory() && f2.exists()
					&& !f2.isDirectory()) {
				sorted_map = loadMap(mod_word_score_model_);
				pm = loadProbModel(mod_prob_model_);
			} else {
				pm = new ProbabilityModel();
				MakeReducedTermSet f = new MakeReducedTermSet(args[0], args[1],
						args[2]);
				sorted_map = f.getReducedTermSet(args[3], args[4], pm);
				serializeMap(sorted_map, model_path);
				saveProbModel(model_path, pm);
			}

			// System.out.println(pm.getCategorySet());
			for (String words : sorted_map.keySet()) {
				System.out.println(words + ":" + pm.getDocCount("acq") + ":"
						+ pm.getProbabilities(words, "acq").t);
			}

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
