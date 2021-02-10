/*
 * CSV.java
 *
 * Created on October 27, 2006, 1:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sauter.util;

import java.util.*;

/**
 *
 * @author Sauter
 */
public class CSV {
    
        private static final String APOST = "'";
        private static final String DQUOTE = "\"\"";
        private static final String QUOTE = "\"";
        private static final String SPACE = " ";
    
    /** Creates a new instance of CSV */
    public CSV() {
    }
    
    public static List <String> getItems(String text, String DELIM) {
        
        int apost;
        String chunk = "";
        String currChar = "";
        int delim;
        boolean error = false;
        String item = "";
        boolean outside = true;
        String prevChar = DELIM;
        int quote;
        List <String> results = new ArrayList <String> ();
               
        text = text.trim();
        if (text.length() > 0) {
            text += DELIM;
        }
        while (text.length() > 0) {
            currChar = text.substring(0,1);
            if (outside) {
                if (currChar.equals(DELIM)) {
                    results.add(item.trim());
                    item = "";
                    text = deleteLS(text.substring(1));
                    prevChar = DELIM;
                } else {
                    if (currChar.equals(QUOTE)) {
                        if (prevChar.equals(DELIM)) {
                            outside = false;
                            text = text.substring(1);
                            prevChar = QUOTE;
                        } else {
                            error = true;
                            text = "";
                        }
                    } else {
                        item += currChar;
                        prevChar = currChar;
                        text = text.substring(1);
                    }
                }
            } else {            // we're INSIDE a quoted string
                if (!currChar.equals(QUOTE)) {
                    item += currChar;
                    text = text.substring(1);
                    prevChar = currChar;
                } else {
                    if (text.indexOf(QUOTE, 1) == 1) {
                        item += QUOTE;
                        text = text.substring(2);
                        prevChar = "";
                    } else {
                        text = deleteLS(text.substring(1));
                        if (text.indexOf(DELIM) == 0 ) {
                            prevChar = QUOTE;
                            outside = true;
                        } else {
                            error = true;
                            text = "";
                        }
                    }
                }
            }
        }
        if (error) {
            results.add("error");
        }
        return results;
    }    
   
    private static String deleteLS(String work) {
        while (work.indexOf(SPACE) == 0) {
            work = work.substring(1);
        }
        return work;
    }
    
}
