package tc.evaluation;

import tc.dstruct.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.io.Serializable;

public class CSVTable extends HashMap implements CSVManipulation {

	private String category = null;
	private Map<String, Vector> id_cat_map = new HashMap<String, Vector>();

	public CSVTable(String category) {
		super();
		this.category = category;
	}

	@Override
	public double getCSV(String id) {
		return (Double) get(id);
	}
	
	public void setOrigcat (String id, Vector cat) {
		id_cat_map.put(id, cat);
	}

	@Override
	public void setCSV(String id, double csv) {
		put(id, csv);
	}

	@Override
	public void applyUCutThreshold(double threshold) {
		// TODO Auto-generated method stub
		for (Iterator e = this.entrySet().iterator(); e.hasNext();) {
			Map.Entry kv = (Map.Entry) e.next();
			double csv = (Double) kv.getValue();
			if (csv < threshold)
				kv.setValue(0.0);
			else
				kv.setValue(1.0);
		}
	}

	@Override
	public void applyProportionalThreshold(double generality) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getPrecision() {
		Integer TP = 0, FP = 0, TN = 0, FN = 0;
		for (Iterator e = this.entrySet().iterator(); e.hasNext();) {
			Map.Entry kv = (Map.Entry) e.next();
			if (((Double) kv.getValue() == 1.0) && id_cat_map.get(kv.getKey()).contains(category))
				TP += 1;
			else if (((Double) kv.getValue() == 1.0) && !id_cat_map.get(kv.getKey()).contains(category))
				FP += 1;
			else if (((Double) kv.getValue() == 0.0) && id_cat_map.get(kv.getKey()).contains(category))
				FN += 1;
			else
				TN += 1;
		}
		System.out.println(TP + ":" + FP);
		return (double)TP / (double) (TP + FP);
	}

	@Override
	public double getRecall() {
		Integer TP = 0, FP = 0, TN = 0, FN = 0;
		for (Iterator e = this.entrySet().iterator(); e.hasNext();) {
			Map.Entry kv = (Map.Entry) e.next();
			if (((Double) kv.getValue() == 1.0) && id_cat_map.get(kv.getKey()).contains(category))
				TP += 1;
			else if (((Double) kv.getValue() == 1.0) && !id_cat_map.get(kv.getKey()).contains(category))
				FP += 1;
			else if (((Double) kv.getValue() == 0.0) && id_cat_map.get(kv.getKey()).contains(category))
				FN += 1;
			else
				TN += 1;
		}
		return (double) TP / (double) (TP + FN);
	}

	@Override
	public double getAccuracy() {
		// TODO Auto-generated method stub
		return 0;
	}
	/**
	 * ******************** Lab 04: Exercise ********************* Implement the
	 * methods specified by the CSVManipulation interface
	 */
}
