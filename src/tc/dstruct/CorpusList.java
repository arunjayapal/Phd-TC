package tc.dstruct;
import java.io.*;
import java.util.Vector;
/**
 *  List of filenames (full path) in the corpus
 *
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: CorpusList.java,v 1.4 2004/03/19 15:48:08 luzs Exp $</font>
 * @see  
*/
public class CorpusList extends  Vector
{

  public CorpusList (String flist) 
  {
    super();
    try {
      BufferedReader in
        = new BufferedReader(new FileReader(flist));
      String fname = null;
      while ( (fname = in.readLine()) != null )
        {
          this.add(fname);
        }
    }
    catch (IOException e){
      System.err.println("Error reading corpus list "+flist);
      e.printStackTrace();
    }
  }
}
