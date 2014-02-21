package tc.parser;
import tc.util.*;
import java.util.Vector;
import java.util.Enumeration;
import org.xml.sax.HandlerBase;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * BasicHandler: Solution for 4ict2 lab 01: print out the contents of
 * each document, along with its identication and the categories
 * to which it belongs.
 *
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: BasicHandler.java,v 1.4 2004/03/19 15:48:08 luzs Exp $</font>
 * @see  
*/


public class BasicHandler extends HandlerBase 
{

  private String content = null;
  private int error = 0;
  private boolean openCategory = false;
  private boolean openText = false;
  private StringBuffer text = null;
  private int newsID = 0;
  private PrintUtil printUtil = null;

  //private Vector catTextVector = new Vector(); 
  /**
   * Categories annotated in REUTERS-21578
   */
  private static final String[] categoryArray =  {"TOPICS","PEOPLE","PLACES","ORGS"};
  private static final String[] textArray =  {"TITLE", "BODY"};
  private static final String newsTag = "REUTERS";

  
  public void resetCounter()
  {
    if (printUtil != null)
      printUtil.resetCounter();
  }
  

  public void startElement (String name, AttributeList atts)
  {
    if ( newsItemElement(name) ){
      newsID = (new Integer((String)atts.getValue("NEWID"))).intValue();
      if (printUtil == null)
        printUtil = new PrintUtil();
      PrintUtil.printNoMove("Reading news item ", newsID);
    }
    else if ( categoryElement(name) )
      openCategory = true;
    else if ( textElement(name) )
      openText = true;
    else
      content = null;
  }
  
  public void endElement (String name)
  {
    if (name.equals("D") && (content != null) )
     System.out.println("CATEGORY: \n"+content+ "\n(from newsitem "+ newsID + ")");
    if ( categoryElement(name) )
      {
        openCategory = false;
        if (content != null) 
          System.out.println(name+":\n"+content+ "\n(from newsitem "+ newsID + ")");
      }
    if ( textElement(name) )
      {
        openText = false;
        if (content != null)
          addText(new String(content));
      }
    content = null;
    if ( newsItemElement(name) ) {
      //System.out.println("----> ended news item :"+newsID+":"+name+".\n"+getText());
      openCategory = false;
      openText = false;
      System.out.println("Text of newsitem "+ newsID + ":"+getText());
      newsID = 0;
      text = null;
    }
  }
  
  public void characters (char ch[], int start, int length)
  { 
    if ( openCategory || openText )
      content = new String(ch,start,length);
  }
  
  /**
   * Print a message for ignorable whitespace.
   *
   * @see org.xml.sax.DocumentHandler#ignorableWhitespace
   */
  public void ignorableWhitespace (char ch[], int start, int length)
  {
    //System.out.print("Ignorable Whitespace found ");
  }
  
  /**
   * Report all warnings, and continue parsing.
   *
   * @see org.xml.sax.ErrorHandler#warning
   */
  public void warning (SAXParseException exception)
  {
    error++;
    System.err.print("Warning: " +
                   exception.getMessage() +
                   " (" +
                   exception.getSystemId() +
                   ':' +
                   exception.getLineNumber() +
                   ',' +
                   exception.getColumnNumber() +
                   ')' + "\n");
  }
  
  
  /**
   * Report all recoverable errors, and try to continue parsing.
   *
   * @see org.xml.sax.ErrorHandler#error
   */
  public void error (SAXParseException exception)
  {
    error++;
    System.err.print("Recoverable Error: " +
                   exception.getMessage() +
                   " (" +
                   exception.getSystemId() +
                   ':' +
                   exception.getLineNumber() +
                   ',' +
                   exception.getColumnNumber() +
                   ')' + "\n");
  }
  
  /**
   * Report all fatal errors, and try to continue parsing.
   *
   * <p>Note: results are no longer reliable once a fatal error has
   * been reported.</p>
   *
   * @see org.xml.sax.ErrorHandler#fatalError
   */
  public void fatalError (SAXParseException exception)
  {
    error++;
    System.err.print("Fatal Error: " +
                   exception.getMessage() +
                   " (" +
                   exception.getSystemId() +
                   ':' +
                   exception.getLineNumber() +
                   ',' +
                   exception.getColumnNumber() +
                   ')' + "\n");
  }
  
  /**
   *  Indexer Interface 
   */

  public void addText (String text)
  {
    if (this.text == null)
      this.text = new StringBuffer(text);
    else
      this.text = this.text.append(" "+text);
    //System.out.println("---> adding text:"+this.text);
  }

  public String getText ()
  {
    return this.text+"";
  }
  /**
   *  Utilities
   */
  
  private  boolean newsItemElement (String name)
  {
    return name.equals(newsTag);
  } 

  private  boolean categoryElement (String name) 
  {
    
    for (int i = 0; i < categoryArray.length; i++)
      if ( name.equals(categoryArray[i]) )
        return true;
    return false;
  }

  private  boolean textElement (String name) 
  {
    
    for (int i = 0; i < textArray.length; i++)
      if ( name.equals(textArray[i]) )
        return true;
    return false;
  }
  
  private String fixElementName (String name) {
    
    StringBuffer out = new StringBuffer();
    out.append( Character.toUpperCase(name.charAt(0)) );
    for (int i = 1; i <  name.length(); i++ )  
      if ( Character.isUpperCase(name.charAt(i)) ) 
        out.append(" " + name.charAt(i) );
      else
        out.append( name.charAt(i) );
    
    return out.toString();
  }
}





