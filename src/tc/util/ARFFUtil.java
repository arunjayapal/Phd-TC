package tc.util;
import tc.dstruct.*;
import java.io.*;
import java.util.Vector;
import java.util.Set;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
/**
 *  Manipulate strings for/from ARFF files
 *
 * @author  S Luz &#60;luzs@cs.tcd.ie&#62;
 * @version <font size=-1>$Id: ARFFUtil.java,v 1.3 2004/03/19 15:48:08 luzs Exp $</font>
 * @see  
*/
public class ARFFUtil {


  public static String toString(boolean[] ba){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ba.length ; i++)
      sb.append(ba[i]+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(int[] ba){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ba.length ; i++)
      sb.append(ba[i]+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(long[] ba){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ba.length ; i++)
      sb.append(ba[i]+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(double[] ba){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ba.length ; i++)
      sb.append(ba[i]+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(String[] ba){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ba.length ; i++)
      sb.append(ba[i]+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(WordFrequencyPair[] wfp){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < wfp.length ; i++)
      sb.append(wfp[i].getWord()+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(Vector v){
    StringBuffer sb = new StringBuffer();
    for (Enumeration e = v.elements() ; e.hasMoreElements() ;)
      sb.append((String)e.nextElement()+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(Set v){
    StringBuffer sb = new StringBuffer();
    for (Iterator e = v.iterator() ; e.hasNext() ;)
      sb.append((String)e.next()+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static void printOccurARFF(ProbabilityModel pm, 
                                      WordFrequencyPair[] wfp, 
                                      String category,
                                      PrintStream out ) 
  {
    Set ds = pm.getDocSet();
    // print header
    out.print("@RELATION text\n\n");
    Set cs = null;
    for (int i = 0; i < wfp.length ; i++)
      out.print("@ATTRIBUTE "+wfp[i].getWord()+" REAL\n");
    if (category == null){
      cs = pm.getCategorySet();
      out.print("@ATTRIBUTE category {"+toString(cs)+"}\n");
    }
    else
      out.print("@ATTRIBUTE "+category+"? {true,false}\n");
    out.print("\n@DATA\n");
    // print data
    int c =  1;
    for (Iterator e = ds.iterator(); e.hasNext() ; ){
      PrintUtil.printNoMove("Printing ...", c++);
      String id = (String)e.next();
      int[] tabv = new int[wfp.length];
      for (int i = 0; i < wfp.length ; i++)
        tabv[i] = pm.getCount(id, wfp[i].getWord());
      NewsItemAsOccurVector niaov = new NewsItemAsOccurVector(pm.getCategVector(id),
                                                              tabv,
                                                              id);
      String oas = ARFFUtil.toString(niaov.getOccurrenceArray());
      if (category == null)
        for (Iterator i = niaov.getCategVector().iterator(); i.hasNext() ; ){
          out.print(oas);
          out.print(","+i.next()+"\n");
        }
      else {
        out.print(oas);
        out.print(","+niaov.isOfCategory(category)+"\n");
      }
    }
    PrintUtil.donePrinting();
  }

  public static void printBooleanARFF(ProbabilityModel pm, 
                                      WordFrequencyPair[] wfp, 
                                      String category,
                                      PrintStream out ) 
  {
    Set ds = pm.getDocSet();
    // print header
    out.print("@RELATION text\n\n");
    for (int i = 0; i < wfp.length ; i++)
      out.print("@ATTRIBUTE "+wfp[i].getWord()+" {true,false}\n");
    Set cs = null;
    if (category == null){
      cs = pm.getCategorySet();
      out.print("@ATTRIBUTE category {"+toString(cs)+"}\n");
    }
    else
      out.print("@ATTRIBUTE "+category+"? {true,false}\n");
    out.print("\n@DATA\n");
    // print data
    int c =  1;
    for (Iterator e = ds.iterator(); e.hasNext() ; ){
      PrintUtil.printNoMove("Printing ...", c++);
      String id = (String)e.next();
      int[] tabv = new int[wfp.length];
      for (int i = 0; i < wfp.length ; i++)
        tabv[i] = pm.getCount(id, wfp[i].getWord());
      NewsItemAsOccurVector niaov = new NewsItemAsOccurVector(pm.getCategVector(id),
                                                              tabv,
                                                              id);
      String oas = ARFFUtil.toString(niaov.getBooleanTextArray());
      if (category == null)
        for (Iterator i = niaov.getCategVector().iterator(); i.hasNext() ; ){
          out.print(oas);
          out.print(","+i.next()+"\n");
        }
      else {
        out.print(oas);
        out.print(","+niaov.isOfCategory(category)+"\n");
      }
    }
    PrintUtil.donePrinting();
  }

  public static void printTFIDFARFF(ProbabilityModel pm, 
                                    WordFrequencyPair[] wfp, 
                                    String category,
                                    PrintStream out ) 
  {
    Set ds = pm.getDocSet();
    // print header
    out.print("@RELATION text\n\n");
    for (int i = 0; i < wfp.length ; i++)
      out.print("@ATTRIBUTE "+wfp[i].getWord()+" REAL\n");
    Set cs = null;
    if (category == null){
      cs = pm.getCategorySet();
      out.print("@ATTRIBUTE category {"+toString(cs)+"}\n");
    }
    else
      out.print("@ATTRIBUTE "+category+"? {true,false}\n");
    out.print("\n@DATA\n");
    // print data
    int c =  1;
    for (Iterator e = ds.iterator(); e.hasNext() ; ){
      PrintUtil.printNoMove("Printing ...", c++);
      String id = (String)e.next();
      int[] tabv = new int[wfp.length];
      for (int i = 0; i < wfp.length ; i++)
        tabv[i] = pm.getCount(id, wfp[i].getWord());
      NewsItemAsOccurVector niaov = new NewsItemAsOccurVector(pm.getCategVector(id),
                                                              tabv,
                                                              id);
      String oas = ARFFUtil.toString(niaov.getTFIDFVector(wfp, pm.corpusSize));
      if (category == null)
        for (Iterator i = niaov.getCategVector().iterator(); i.hasNext() ; ){
          out.print(oas);
          out.print(","+i.next()+"\n");
        }
      else {
        out.print(oas);
        out.print(","+niaov.isOfCategory(category)+"\n");
      }
    }
    PrintUtil.donePrinting();
  }

  public static void printPWeightARFF(ProbabilityModel pm, 
                                      WordFrequencyPair[] wfp, 
                                      String category,
                                      PrintStream out ) 
  {
    Set ds = pm.getDocSet();
    // print header
    out.print("@RELATION text\n\n");
    for (int i = 0; i < wfp.length ; i++)
      out.print("@ATTRIBUTE "+wfp[i].getWord()+" REAL\n");
    Set cs = null;
    if (category == null){
      cs = pm.getCategorySet();
      out.print("@ATTRIBUTE category {"+toString(cs)+"}\n");
    }
    else
      out.print("@ATTRIBUTE "+category+"? {true,false}\n");
    out.print("\n@DATA\n");
    // print data
    int c =  1;
    for (Iterator e = ds.iterator(); e.hasNext() ; ){
      PrintUtil.printNoMove("Printing ...", c++);
      String id = (String)e.next();
      int[] tabv = new int[wfp.length];
      for (int i = 0; i < wfp.length ; i++)
        tabv[i] = pm.getCount(id, wfp[i].getWord());
      NewsItemAsOccurVector niaov = new NewsItemAsOccurVector(pm.getCategVector(id),
                                                              tabv,
                                                              id);
     String oas = ARFFUtil.toString(niaov.getPWEIGHTVector(wfp.length));
      if (category == null)
        for (Iterator i = niaov.getCategVector().iterator(); i.hasNext() ; ){
          out.print(oas);
          out.print(","+i.next()+"\n");
        }
      else {
        out.print(oas);
        out.print(","+niaov.isOfCategory(category)+"\n");
      }
    }
    PrintUtil.donePrinting();
  }

  public static void printDebug(ProbabilityModel pm,
                                WordFrequencyPair[] wfp,
                                PrintStream out ) 
  {
    Set ds = pm.getDocSet();
    // print header
    out.print("TEMPLATE: "+ARFFUtil.toString(wfp));
    out.print("\nNo. of DOCS: "+pm.corpusSize);
    out.print("\nTERM SET SIZE: "+wfp.length);
    // print data
    int c =  1;
    for (Iterator e = ds.iterator(); e.hasNext() ; ){
      PrintUtil.printNoMove("Printing ...", c++);
      String id = (String)e.next();
      int[] tabv = new int[wfp.length];
      for (int i = 0; i < wfp.length ; i++)
        tabv[i] = pm.getCount(id, wfp[i].getWord());
      NewsItemAsOccurVector niaov = new NewsItemAsOccurVector(pm.getCategVector(id),
                                                              tabv,
                                                              id);
      out.print("\n================"
                +"\nID: "+niaov.getId()
                +"\nCATEGS: "+ARFFUtil.toString(niaov.getCategVector())
                +"\nOCCUR. VECTOR:\n "+ARFFUtil.toString(niaov.getOccurrenceArray())
                +"\nPWEIGHT VECTOR:\n "+ARFFUtil.toString(niaov.getPWEIGHTVector(wfp.length))
                +"\nTFIDF VECTOR:\n "+ARFFUtil.toString(niaov.getTFIDFVector(wfp, pm.corpusSize))
                );
    }
    PrintUtil.donePrinting();
  }



}
