/** 
 * Term Space Reduction by GSS coefficient filtering
 * (see dimreduct-4up.pdf and termextract-4up.pdf)
 * (S. Luz, luzs@cs.tcd.ie)
 **/ 
package tc.tsr;
import tc.parser.*;
import tc.dstruct.*;
import tc.util.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Arrays;

/**
 * 
 * 
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: GSScoefficient.java,v 1.1 2004/03/29 14:27:59 luzs Exp $</font>
 * @see  GenerateARFF
*/

public class GSScoefficient extends TermFilter
{


  public  GSScoefficient(ProbabilityModel pm){
    super(pm);
  }

  public double computeLocalTermScore(String term, String cat){
    Probabilities p = pm.getProbabilities(term, cat);
    return (p.tc * p.ntnc) - (p.tnc * p.ntc);
  }

}
      
// System.err.println(wsp[i].getWord()+"\n p(t)    = "+p.t+"\n p(c)    = "+p.c+"\n p(tc)   = "+p.tc+"\n p(ntc)  = "+p.ntc+"\n p(tnc)  = "+p.tnc+"\n p(ntnc) = "+p.ntnc+"\n IG      = "+Maths.xTimesLog2y(p.tc,   p.tc/(p.t * p.c))+" + "+Maths.xTimesLog2y(p.ntc,  p.ntc/((1-p.t) * p.c))+" + "+Maths.xTimesLog2y(p.tnc,  p.tnc/(p.t * (1-p.c))) +" + "+Maths.xTimesLog2y(p.ntnc, p.ntnc/((1-p.t) * (1-p.c)))+" = "+ig); 
