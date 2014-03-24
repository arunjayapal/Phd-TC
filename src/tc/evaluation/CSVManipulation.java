package tc.evaluation;
/**
 *  Classes that store and manipulate Categorisation Status Values
 *  (CSV) must implement this interface
 *
 * @author  S Luz &#60;luzs@cs.tcd.ie&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/
public interface CSVManipulation {


   /**
   * Get (ranking) categorisation status value for document id
   */
  double getCSV(String id);

  /**
   * Set (ranking) categorisation status value for document id to csv
   */
  void setCSV (String id, double csv);
  
  /** select documents (as belonging to category) whose CSVs are
   * greater than threshold
   */
  void applyUCutThreshold(double threshold);

  /** Proportional thresholding: select documents (as belonging to
   * category) according to the generality criteria
   */
  void applyProportionalThreshold(double generality);

  /** calculate precision score = TP / (TP + FP)
   * @return classification precision (after thresholding)
   */
  double getPrecision();

  /** calculate recall score = TP / (TP + FN)
   * @return classification recall (after thresholding)
   */
  double getRecall();

  /** calculate according score = no_of_correctly_classified_items / total_no_of_items 
   * @return classification accuracy (after thresholding)
   */
  double getAccuracy();

}
