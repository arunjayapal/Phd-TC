package tc.parser;
import java.util.StringTokenizer;
/**
 *  Handle simple tokenisation tasks relating to terms (words) and categories
 *
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: Tokenizer.java,v 1.5 2004/03/25 16:09:42 luzs Exp $</font>
 * @see  
*/
public class Tokenizer extends StringTokenizer {

	public static char[] SEPTKARR = {' ',
                                         '|',
                                         '\'',
                                         '`',
                                         '"',
                                         '-',
                                         '_',
                                         (new String(",")).charAt(0),
                                         (new String("\n")).charAt(0),
                                         (new String("\t")).charAt(0),
                                         //(new String(".")).charAt(0),
                                         // we want to be able to get abbreviations (e.g. "U.S.A." => "USA")
                                         '?',
                                         '!',
                                         (new String(";")).charAt(0),
                                         ':',
                                         '<',
                                         '>',
                                         '{',
                                         '}',
                                         '[',
                                         ']',
                                         '=',
                                         '+',
                                         '/',
                                         '\\',
                                         '%',
                                         '$',
                                         '*',
                                         '&',
                                         '(',
                                         ')' };
  public static String SEPTOKEN = new String(SEPTKARR);
  
  public Tokenizer (String text)
  {
    // Tokens are all strings separated by SEPTOKEN, not including 
    // separators themselves
    super(text, SEPTOKEN, false);
  }
  
  /**
   * Delete dots (e.g. "U.S.A" => "USA"), remove spaces, clean up any
   * remaining garbage left by Tokenizer
   * @return clean type
   */
  public static String fixType(String type)
  {
    char[] nt = new char[type.length()];
    int j = 0;
    for (int i = 0; i < type.length(); i++)
      {
        if ( type.charAt(i) != '.' )
          nt[j++] = type.charAt(i);
      }
    
    return new String(nt, 0, j);
  }
  
  
  /**
   * Check is token is a negated token (e.g '¬c' in p(t|¬c))
   */
  public static boolean isBar(String token){
    return token.charAt(0) == '-';
  }
  
  /**
   *  Disbar token
   */
  public static String disbar(String token){
    return isBar(token) ? token.substring(1) : token;
  }
  
}
