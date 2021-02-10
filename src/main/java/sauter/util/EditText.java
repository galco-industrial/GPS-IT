/*
 * EditText.java
 *
 * Created on October 19, 2006, 2:19 PM
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
public class EditText {
    
    /** Creates a new instance of EditText */
    public static void EditText() {
    }
     
    public static boolean checkCharSet(String charSet, String item) {
        int i;
        for (i = 0; i < item.length(); i++ ) {
            if (charSet.indexOf(item.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }
    
    public static String deleteCommas(String x) {
        int i = x.indexOf(",");
        while (i != -1) {
            x = x.substring(0, i) + x.substring(i+1);
            i = x.indexOf(",");
        }
        return x;
    }
    
    public static String deleteLeadingSpaces(String x) {
	while (x.indexOf(" ") == 0) {
            x = x.substring(1);
	}
	return x;
    }
        
    public static String deleteNPC(String work) {
	String result = "";
	int len = work.length();
	String myChar;
	for (int i = 0; i < len; i++) {
		myChar = work.substring(i, i + 1);
		if (myChar.compareTo(" ") != -1) {
                    result += myChar;
		}
	}
	return result;
    }

    public static String deleteSpaces(String x) {
        int i = x.indexOf(" ");
        while (i != -1) {
            x = x.substring(0, i) + x.substring(i+1);
            i = x.indexOf(" ");
        }
        return x;
    }
    public static String deleteTrailingSpaces(String x) {
	while (x.endsWith(" ")) {
            x = x.substring(0, x.length() - 1);
	}
	return x;
    }

    public static String encodeEntity(String work) {
        /* I accept a string in variable work. Well formed entity references 
        * are left unaltered. However the characters &, <, >, ", and ' are
        * replaced with xml built-in entities &amp;, &lt;, &gt;, &quot;, and &apos;
        * respectively.
        * An entity reference is of the form &xxxx; where
        * the beginning character is an ampersand &,
        * the ending character is a ;
        * there is no whitespace between the & and the ;
        *
        *   This function returns an encoded result where
        * No & is followed by a space or another &
        * Characters <, >, ", and ' have all been replaced by xml built-in entities.
        */
    
        if (work == null) {
            work = "";
        }
        String result = "";
        int i = 0;
        int j = 0;
        
        // First we eliminate &'s that do NOT define an entity
    
        i = work.indexOf("&");                      // Do we have an & ?
        while (i != -1) {                            // if so, let's parse it out
            result += work.substring(0,i);          // acquire everything before the &
            work = work.substring(i);               // work has & and remainder of string to parse
            j = entityLength(work);                 // get length of any valid beginning entity here
            if (j > 0 ) {                           // if string begins with a valid entity
                result += work.substring(0,j);      // append it to result string and
                work = work.substring(j);           // remove entity from remaining string to parse
            } else {                                // otherwise
                result += "&amp;";                  // replace the & with &amp; in the result
                work = work.substring(1);           // discard the & and get remaining string
            }
            i = work.indexOf("&");                  // process any more &'s remaining
        }
        result += work;                             // Do not forget to append remainder of work
        work = result;
        i = work.indexOf("\"");
        while (i != -1) {
            work = work.substring(0,i) + "&quot;" + work.substring(i + 1);
            i = work.indexOf("\"");
        }
        i = work.indexOf("'");
        while (i != -1) {
            work = work.substring(0,i) + "&apos;" + work.substring(i+1);
            i = work.indexOf("'");
        }
        i = work.indexOf("<");
        while (i != -1) {
            work = work.substring(0,i) + "&lt;" + work.substring(i+1);
            i = work.indexOf("<");
        }
        i = work.indexOf(">");
        while (i != -1) {
            work = work.substring(0,i) + "&gt;" + work.substring(i+1);
            i = work.indexOf(">");
        }
        return work;
    }
   
    private static int entityLength(String x) {
        /* Hi. I am the dudette that examines a string x that begins with an &
         * If it begins with a validly formed entity reference
         * I will return its length
         * otherwise I return a zero
         */
        
        int space = x.indexOf(" ");             // Do we have a space?
        int semicolon = x.indexOf(";");         // Do we have a semi kernel?
        int amp = x.indexOf("&",1);             // Do we have an &?
        int apos = x.indexOf("'");
        int quote = x.indexOf("\"");
        int lt = x.indexOf("<");
        int gt = x.indexOf(">");
        
        if (semicolon == -1) {                  // if there is no semi kernel
            return 0;                           // there is no entity, so return with 0 length
        }
        if (space != -1) {                      // if we found a space
            if (space < semicolon) {            // and it occurred BEFORE the semi kernel
                return 0;                       // then there is no entity
            }
        }
        if (apos != -1) {                      // if we found an apostrophe
            if (apos < semicolon) {            // and it occurred BEFORE the semi kernel
                return 0;                       // then there is no entity
            }
        }
        if (quote != -1) {                      // if we found quotation marks
            if (quote < semicolon) {            // and it occurred BEFORE the semi kernel
                return 0;                       // then there is no entity
            }
        }
        if (lt != -1) {                         // if we found an <
            if (lt < semicolon) {               // and it occurred BEFORE the semi kernel
                return 0;                       // then there is no entity
            }
        }
        if (gt != -1) {                         // if we found an >
            if (gt < semicolon) {               // and it occurred BEFORE the semi kernel
                return 0;                       // then there is no entity
            }
        }
        if (amp > 0) {                          // if we found a second & after the beginning &
            if (amp < semicolon) {              // AND the second & is BEFORE the ;
                return 0;                       // then everything up to the second & is not
            }                                   // a valid entity
        }                                       // Otherwise we found an entity
        return semicolon + 1;                   // and return with its length
    }
 
    public static String escapeQuote(String x) {
        String r = "";
        String c;
        int i;
        for (i = 0; i < x.length(); i++) {
            c = x.substring(i, i + 1);
            if (c.equals("\"")) {
                c = "\\" + c;
            }
            r += c;
        }
        return r;    
    }
    
    public static String reduceSpaces(String x) {
	// replace multiple spaces with one
        int i = x.indexOf("  ");
	while (i != -1) {
            x = x.substring(0, i) + x.substring(i + 1);
            i = x.indexOf("  ");
	}
	return x;
    }
   
    public static String removeDuplicates(String work) {
        // I return a string of unduplicated, unique characters derived from work
        String result = "";
        int i;
        for (i=0; i < work.length(); i++) {
            if (result.indexOf(work.charAt(i)) == -1 ) {
                result += work.charAt(i);
            }
        }
        return result;
    }
    
    public static String replaceFunnyCharacters(String toScan, String replacement) {
        toScan = toScan.toLowerCase();
        String result = "";
        String character = "";
        for (int i = 0; i < toScan.length(); i++) {
            character = toScan.substring(i, i + 1);
            if ("0123456789.-_%~abcdefghijklmnopqrstuvwxyz".contains(character)) {
                result += character;
            } else {
                result += replacement;
            }
        }
        result = reduceSpaces(result).trim();
        return result;
    }
    
    public static String setDefault(String item, String itemDefault) {
        if (item == null) {
            item = "";
        }
        if (item.equals("") ) {
            item = itemDefault;
        }
        return item;
    }
        
    public static String toDoubleQuote(String work) {
        String result = "";
        final String doubleQuote = "\"\"";
        int pos = work.indexOf("\"");
        while (pos != -1) {
            result += work.substring(0, pos) + doubleQuote;
            work = work.substring(++pos);
            pos = work.indexOf("\"");
        }
        result += work;
        return result;
    }
    
   
}
