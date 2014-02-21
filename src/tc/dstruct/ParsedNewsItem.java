package tc.dstruct;
import tc.parser.Tokenizer;
import java.util.Vector;
import java.util.Enumeration;

/**
 *  Store text (as a StringBuffer), its id (as a String), and the
 *  categories to which it belongs (as a vector)
 *
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ParsedNewsItem.java,v 1.5 2004/03/25 16:09:43 luzs Exp $</font>
 * @see  
*/
public class ParsedNewsItem {

  private Vector categs = null;
  private StringBuffer text   = null;
  private String id = null;

  public ParsedNewsItem (Vector categs, String text, String id)
  {
    super();
    this.id = id;
    this.categs = categs;
    this.text = new StringBuffer(text);
  }

  public ParsedNewsItem (String text, String id)
  {
    super();
    this.id = id;
    this.categs = new Vector();
    this.text = new StringBuffer(text);
  }

  public ParsedNewsItem ()
  {
    super();
    this.categs = new Vector();
    this.text = null;
    this.id = null;
  }


  public void addCategory (String topic)
  {
    categs.add(topic);
  }

  public void addText (String text)
  {
    if (this.text == null)
      this.text = new StringBuffer(text);
    else
      this.text = this.text.append(" "+text);
  }

  public Enumeration getCategories ()
  {
    return categs.elements();
  }


  public Vector getCategVector ()
  {
    return categs;
  }

  public String getText ()
  {
    return  text+"";
  }

  public void setText (String text)
  {
    this.text = new StringBuffer(text);
  }

  public boolean isOfCategory(String cat){
    boolean barcat = false;
    if (Tokenizer.isBar(cat)){
      barcat = true;
      cat = Tokenizer.disbar(cat);
    }
    for (Enumeration e = getCategories() ; e.hasMoreElements() ;){
      String c = (String)e.nextElement();
      if (c.equals(cat))
        return barcat? false : true;
    }
    return barcat? true : false;
  }

  /**
   * Get the value of id.
   * @return value of id.
   */
  public String getId() {
    return id;
  }
  
  /**
   * Set the value of id.
   * @param v  Value to assign to id.
   */
  public void setId(String  v) {
    this.id = v;
  }

  public String toString(){
    return "ID: "+getId()+"\n"+
      "CATEGORIES: "+getCategVector()+"\n"+
      "TEXT: "+getText()+"\n";
  }

}
