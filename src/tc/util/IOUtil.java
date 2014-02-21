package tc.util;
import java.io.*;
import tc.dstruct.*;
/**
 *  Handle serialization and other IO stuff
 *
 * @author  S Luz &#60;luzs@cs.tcd.ie&#62;
 * @version <font size=-1>$Id: IOUtil.java,v 1.1 2004/03/25 16:09:42 luzs Exp $</font>
 * @see  
*/
public class IOUtil {


  public static void dumpProbabilityModel (ProbabilityModel pm, String filename) 
  {
    try {
      FileOutputStream out = new FileOutputStream(filename);
      ObjectOutputStream s = new ObjectOutputStream(out);
      s.writeObject(pm);
      s.flush();
    }
    catch (Exception e){
      System.err.println("Error saving Probability Model"); 
      e.printStackTrace();
    }
  }

  public static ProbabilityModel loadProbabilityModel (String filename)
  {
    try {
      FileInputStream in = new FileInputStream(filename);
      ObjectInputStream s = new ObjectInputStream(in);
      return (ProbabilityModel)s.readObject();
    }
    catch (Exception e){
      System.err.println("Error reading Probability Model");
      e.printStackTrace();
    }
    return null;
  }
}
