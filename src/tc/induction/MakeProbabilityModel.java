package tc.induction;
import tc.dstruct.*;
import tc.parser.*;
import tc.tsr.*;
import tc.util.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Set;
import java.util.Arrays;
/**
 * Parse Reuters file, perform term filtering, and generate a
 * probability table containing joint term-category probabilities and
 * a few extra things, and saves it to disk (as a serialized object.)
 *
 * Usage:
 * <pre>
 MakeProbabilityModel corpus_list stopwdlist aggr tf_method categ pmfile

SYNOPSIS:
  Tokenise each file in corpus_list, remove words in stopwdlist
  and reduce the term set by a factor of aggr.

ARGUMENTS
 tf_method: term filtering method. One of: 
         'df': document frequency, local,
         'dfg': document frequency, global,
         'ig': information gain.

 categ: target category (e.g. 'acq'.) for local term filtering OR
         a method for combining local scores. One of:
            '_DFG' (global document frequency),
            '_MAX' (maximum local score),
            '_SUM' (sum of local scores),
            '_WAVG' (sum of local scores wbeighted by category generality.)

 pmfile: name of output file for probability model.

  </pre>
 * @author  
 * @version <font size=-1>$Id: MakeProbabilityModel.java,v 1.2 2004/03/29 14:29:33 luzs Exp $</font>
 * @see  ProbabilityModel
 * @see TermFilter
 * @see NewsParser
*/

public class MakeProbabilityModel
{
    /**  ********************  Lab 03: Exercise *********************
     * implement the specification above. 
     */ 
}

