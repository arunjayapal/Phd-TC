package tc.evaluation;

/**
 * The method used in converting CSV_rank into CVS_hard. (provided for lab04)
 * 
 * @author S Luz &#60;luzs@cs.tcd.ie&#62;
 * @version <font size=-1>$Id: $</font>
 * @see
 */
public class ThresholdStrategy {

	/**
	 * CSV threshold
	 */
	public double threshold = 0;
	/**
	 * indicate user-specified threshold (the threshold variable must be set
	 * when this is set to true)
	 */
	public boolean ucut = false;

	/**
	 * indicate proportional thresholding (default)
	 */
	public boolean proportional = false;

	public int docsPerCat = 0;
	/**
	 * indicate fixed number of docs per category (docsPerCat must be set when
	 * this is true)
	 */
	boolean rcut = false;

	/**
	 * Create an object describing the strategy used in converting CSV_rank into
	 * CVS_hard. If thresholdorstrategy is a string naming a strategy (RCut,
	 * proportional), set the appropriate flag. If thresholdorstrategy is a
	 * parsable number, the number will be assumed to be the threshold for a
	 * UCut thresholding strategy.
	 */
	public ThresholdStrategy(String thresholdorstrategy) {
		if (thresholdorstrategy.equals("RCut"))
			rcut = true;
		else if (thresholdorstrategy.equals("proportional"))
			proportional = true;
		else
			try {
				threshold = Double.parseDouble(thresholdorstrategy);
				ucut = true;
			} catch (NumberFormatException e) {
				proportional = true;
			}
	}

}
