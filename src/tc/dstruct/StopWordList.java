package tc.dstruct;
import java.io.*;
import java.util.Vector;
/**
 *  List of stop words to be removed
 *
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: StopWordList.java,v 1.4 2004/03/19 15:48:08 luzs Exp $</font>
 * @see  
*/
public class StopWordList extends  Vector
{

  public StopWordList (String flist) 
  {
    super();
    try {
      BufferedReader in
        = new BufferedReader(new FileReader(flist));
      String word = null;
      while ( (word = in.readLine()) != null )
        {
          this.add(word.toLowerCase());
        }
    }
    catch (IOException e){
      System.err.println("Error reading stopword list "+flist);
      e.printStackTrace();
    }
  }

  public boolean contains (String tw ){
    return super.contains(tw.toLowerCase());
  } 
}
