/** 
 * NewsParser Reuters file and store the results as a ParsedText object.
 * (S. Luz, luzs@cs.tcd.ie)
 **/ 
package tc.parser;
import tc.parser.*;
import tc.dstruct.*;
import java.io.*;
import org.xml.sax.Parser;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.ParserFactory;
import org.xml.sax.InputSource;

/**
 * Parse a Reuters file and store the results as a ParsedText object.
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: NewsParser.java,v 1.4 2004/03/19 15:48:08 luzs Exp $</font>
 * @see  TypeListHandler
*/

public class NewsParser
{

  private ParsedText parsedText = null;
  private String filename;

  /** 
   *  Set up the main user interface items
   */
  public  NewsParser(String fn) {
    this.filename = fn;
  }
  
  /** 
   * parseNews: Set up parser object, perform parsing
   */
  public void  parse ()
  {
    try {
      FileInputStream in = new FileInputStream(filename);
      InputSource source = new InputSource(in);
      
      Parser parser = ParserFactory.makeParser("com.jclark.xml.sax.Driver");
      TypeListHandler handler = new TypeListHandler();
      
      source.setEncoding("ISO-8859-1");
      parser.setDocumentHandler(handler);
      parser.setErrorHandler((ErrorHandler) handler);

      System.err.println("handler set ");
      parser.parse(source);
      System.err.println("xml parsed ");
      parsedText =  handler.getParsedText();
    }
    catch (Exception e) 
      {
        System.err.println("Error parsing "+filename);
        e.printStackTrace();
      }
  }

  public ParsedText getParsedText () {
    if (parsedText == null)
      parse();
    return parsedText;
  }

  /**
   *  main method for test purposes. 
   */
  public static void main(String[] args) {
    try {
      NewsParser f = new NewsParser(args[0]);
      System.out.println(f.getParsedText());
    }
    catch (Exception e){
      System.err.println("tc.parser.NewsParser: ");
      System.err.println("Usage: NewsParser FILENAME");
      //e.printStackTrace();
    } 
  }  


}

