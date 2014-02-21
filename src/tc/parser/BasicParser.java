/** 
 * 4ICT2, Lab 01: Parsing the REUTERS-21578 corpus
 *
 * --Saturnino Luz, luzs@cs.tcd.ie
 **/ 
package tc.parser;
import java.io.*;
import java.util.Enumeration;
import java.util.Arrays;
import org.xml.sax.Parser;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.ParserFactory;
import org.xml.sax.InputSource;

/**
 * 
 * Read a REUTERS-21578 document (encoded in XML) and parse it, extract
 * text, title and categories and print these data on the standard output.
 * 
 * This program illustrates the use of the XML parser (xp.jar and
 * sax.jar), and auxiliary classes (package tsr) which you may want to
 * use later to implement your Term Space Reduction (feature
 * selection) algorithm, classifier etc.

 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: BasicParser.java,v 1.4 2004/03/19 15:48:08 luzs Exp $</font>
 * @see  IndentationHandler
*/
public class BasicParser
{


  String document = null;
  static Parser parser = null;

  /* ---- You have to implement this!! ---- */
  static BasicHandler handler = new BasicHandler();

  /** 
   *  Set up the main user interface items
   */
  public  BasicParser() {    
     super();
  }
  
  
  
  /** 
   * parseNews: Set up parser object, perform parsing of REUTERS-21578
   * documents, and print data onto STDOUT
   */
  public static void  setParser ()
    throws Exception
  {
    
    parser = ParserFactory.makeParser("com.jclark.xml.sax.Driver");
    parser.setDocumentHandler(handler);
    parser.setErrorHandler((ErrorHandler) handler);

  }

  public static void  parseNews (String filename)
    throws Exception 
  {
    FileInputStream in = new FileInputStream(filename);
    InputSource source = new InputSource(in);
    source.setEncoding("ISO-8859-1");
    parser.parse(source);
    
  }


  public static void main(String[] args) {
    try {
      BasicParser f = new BasicParser();
      setParser();
//      parseNews(args[0]);
      parseNews("/home/arunjayapal/Public/PhD/courses/MachineLearning/ProgAss1/reuters21578-xml/reut2-000.xml");
     }
    catch (Exception e){
      e.printStackTrace();
      System.err.println("tc.parser.BasicParser: ");
      System.err.println("Usage: BasicParser FILENAME");
    } 
  }
}
