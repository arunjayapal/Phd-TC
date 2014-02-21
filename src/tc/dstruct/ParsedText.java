package tc.dstruct;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;
/**
 *  Store ParsedNewsItem's.
 *
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ParsedText.java,v 1.5 2004/03/25 16:09:43 luzs Exp $</font>
 * @see  
*/
public class ParsedText extends Vector
{

  public ParsedText () 
  {
    super();
  }

  public boolean addNewsItem (ParsedNewsItem pni){
    return add(pni);
  }

  public void append(ParsedText pt){
    try {
      addAll(pt);
    }
    catch (Exception e)
      {
        System.err.println("Error appending parsed text");
        e.printStackTrace();
        System.exit(0);
      }
  }

  /**
   * Get the sub-corpus defined by documents belonging to category cat
   */
  public ParsedText getCategSubCorpus(String cat) {
    ParsedText pt = new ParsedText();
    for (Enumeration e = this.elements() ; e.hasMoreElements() ;){
      ParsedNewsItem pni = (ParsedNewsItem)e.nextElement();
      if ( pni.isOfCategory(cat) )
        pt.addNewsItem(pni);
    }
    System.err.println("Subcorpus for "+cat+
                       " contains "+pt.size()*100/this.size()+
                       "% of texts.");
    return pt;
  }
  
  /**
   * Get the overall probability of category cat classifying a
   * document in the corpus (represented by this ParsedText)
   */

  public double getCategProbability(String cat) {
   // TO BE IMPLEMENTED 
   return 1;
  }

  /** 
   * Get the joint probability of term 'term' occurring in a document and category 'cat' classifying it.
   *
   */
  public Probabilities getProbabilities (String term, String cat){
   // TO BE IMPLEMENTED
    return new Probabilities(1,
                             1,
                             1,
                             1,
                             1,
                             1);
  }


  public String toString(){
    StringBuffer sb = new StringBuffer();
    for (Enumeration e = this.elements() ; e.hasMoreElements() ;){
      ParsedNewsItem pni = (ParsedNewsItem)e.nextElement();
      sb.append(pni.toString());
    }
    return sb.toString();
  }


}
