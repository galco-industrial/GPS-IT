/*
 * GPSparmCheck.java
 *
 * Created on November 30, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

import java.util.*;
import java.text.*;
import sauter.util.*;

/**
 *
 * @author Sauter
 */
public class GPSparmCheck {
    
    private static final String version = "1.5.00";
    
    /** Creates a new instance of GPSparmCheck */
    public GPSparmCheck() {
    }
    
    public static boolean stringMatch(String parmData, String searchValue, String delimiter, boolean tilde) {
        String elements[] = new String[99];
        String work;
        if (delimiter.length() == 0 || parmData.indexOf(delimiter) == -1) {  // No delmiters allowed
            if (!tilde) {                           // if tildes are not allowed
                return parmData.equals(searchValue);
            } else {                                // Otherwise
                return checkTilde(parmData, searchValue);
            }
        } else {                                // Delimiters allowed
            elements = parmData.split(delimiter);
            for (int i = 0; i < elements.length; i++) {
                work = elements[i];
                if (!tilde) {                       // if tildes are not allowed
                    if (work.equals(searchValue)) {
                        return true;
                    }
                } else {                            // Otherwise
                    if (checkTilde(work, searchValue)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    private static boolean checkTilde(String parmData, String searchValue) {
        String begin;
        String end;
        int t1 = parmData.indexOf("~");
        if (t1 < 0) { // if no tilde
            return parmData.equals(searchValue);            
        } else {
            if (parmData.indexOf("~", t1 + 1) != -1 ) {
                return false;
            }
            begin = parmData.substring(0,t1);
            end = parmData.substring(t1 + 1);
            if (searchValue.compareTo(begin) < 0) {
                return false;
            }
            if (searchValue.compareTo(end) > 0) {
                return false;
            }
        }
        return true;
    }
    
    public static float numericMatch(String parmData, float searchValue, float searchMinValue, float searchMaxValue, 
            String delimiter, boolean tilde) {
        
        // I return a floating point number
        // a negative value means parse error or NO match
        // a zero value (0.0) means a perfect match
        // a positive value means a match within min and max range
        // the higher the number, the worse the match
        // if multiple values were parsed, I return
        //    -2 if there was a parse error on any field, or 
        //    -1 if there was No Match on all values, or
        //    0.0 ~ whatever for the best (lowest) match I found.
        
        float bestMatch = 9999999;
        float elementResult;
        String elements[] = new String[99];
        float fWork;
        float match;
        String work;
               
        final float errorMatch = -2;
        final float noMatch = -1;
        final float perfectMatch = 0;
        
        if (delimiter.length() == 0 || parmData.indexOf(delimiter) == -1) { // No delmiters allowed
            if (!tilde) {                          // if tildes are not allowed
                if (!Convert.isFloat(parmData)) {
                    return errorMatch;
                }
                fWork = Float.parseFloat(parmData);
                if (fWork < searchMinValue) {
                    return noMatch;
                }
                if (fWork > searchMaxValue) {
                    return noMatch;
                }
                return Math.abs(searchValue - fWork) / fWork;
            } else {                                // Otherwise
                return checkNumericTilde(parmData, searchValue, searchMinValue, searchMaxValue);
            }
        } else {          // Delimiters allowed and I have at least 2 values
            elements = parmData.split(delimiter);
            for (int i = 0; i < elements.length; i++) {
                work = elements[i];
                if (!tilde) {                       // if tildes are not allowed
                    if (!Convert.isFloat(work)) {
                       return errorMatch;              // Parse error
                    }
                    fWork = Float.parseFloat(work);
                    if (fWork < searchMinValue) {
                        elementResult = noMatch;
                    } else {
                        if (fWork > searchMaxValue) {
                            elementResult = noMatch;
                        } else {
                            elementResult = Math.abs(searchValue - fWork) / fWork;
                        }
                    }
                    // When we get here, elementResult has
                    // -1 if no Match
                    // or calculated score
                    // if we found a parse error; we already returned with a -2
                } else {                            // Otherwise
                    elementResult = checkNumericTilde(work, searchValue, searchMinValue, searchMaxValue);
                    if (elementResult == -2) {
                        return errorMatch;          // abort on parse error
                    }
                }
                // check element and update lowest score here
                if (elementResult >= 0) {
                    if (elementResult < bestMatch) {
                        bestMatch = elementResult;
                    }
                }
            } // end for (int i = 0; i < elements.length; i++) {
            return bestMatch;
        }  // end if (delimiter.length() == 0 || parmData.indexOf(delimiter) == -1) {
    }
    
    private static float checkNumericTilde(String parmData, float searchValue, float searchMinValue, float searchMaxValue) {
        
        String begin;
        String end;
        float fWork;
        float fBegin;
        float fEnd;
        float matchLow;
        float matchHigh;
        
        final float errorMatch = -2;
        final float noMatch = -1;
        final float perfectMatch = 0;
        
        int t1 = parmData.indexOf("~");
        if (t1 < 0) { // if no tilde
            if (!Convert.isFloat(parmData)) {
                return errorMatch;
            }
            fWork = Float.parseFloat(parmData);
            if (fWork < searchMinValue) {
                return noMatch;
            }
            if (fWork > searchMaxValue) {
                return noMatch;
            }
            return Math.abs(searchValue - fWork) / fWork;
        } else {
            if (parmData.indexOf("~", t1 + 1) != -1 ) {
                return errorMatch;
            }
            begin = parmData.substring(0,t1);
            end = parmData.substring(t1 + 1);
            if (!Convert.isFloat(begin)) {
                return errorMatch;
            }
            if (!Convert.isFloat(end)) {
                return errorMatch;
            }
            fBegin = Float.parseFloat(begin);
            fEnd = Float.parseFloat(end);
            if (fEnd < searchMinValue) {
                return noMatch;
            }
            if (fBegin > searchMaxValue) {
                return noMatch;
            }
            if (searchValue >= fBegin && searchValue <= fEnd) {
                return perfectMatch;
            }
            matchLow = Math.abs(fBegin - searchMinValue);
            matchHigh = Math.abs(fEnd - searchMaxValue);
            if (matchLow > matchHigh) {
                matchLow = matchHigh;
            }
            return matchLow / searchValue;
        }
    }
   
}
