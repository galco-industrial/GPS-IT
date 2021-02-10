/*
 * GPScvt.java
 *
 * Created on October 24, 2006, 3:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sauter.util.*;

/**
 *
 * @author Sauter
 */
public class GPScvt {
    
    private boolean debugSw = false;
    private static final String version = "1.5.00";
    
    private String charSet;
    private boolean commas;
    private int decimalShift;
    private String delimiter;
    private GPSselectBox deSelectBoxObject = null;
    private String errorMsg = "";
    private String flags;
    private float maximum;
    private int maxLen;
    private float minimum;
    private int minLen;
    private String multipliers;
    private String newLine;
    private String parmValue;
    
    /** Creates a new instance of GPScvt */
    public GPScvt() {
    }
    
    private void checkCharSet(String stringToCheck, String aCharSet) {
	// charSet contains all valid characters allowable
	// Check each char in stringToCheck and ensure it contains valid chars defined by charSet
	
	int loopCounter;
        String myChar;
	int stringIndex;
	int strlen = stringToCheck.length();
        
	for (loopCounter = 0; loopCounter < strlen; loopCounter++) {
            myChar = stringToCheck.substring(loopCounter, loopCounter + 1);
            stringIndex = aCharSet.indexOf(myChar);
            if (stringIndex == -1) {
                if (myChar == " ") {
                    myChar = "<space>";
		}
		errorMsg += "Error - This character is not allowed: " + myChar + newLine;
		return;
            }
        }
        return;
    }
    
    private String checkDefaultIndividualNum(String work) {
        String x = parseNumber(work, (flags.indexOf("F") != -1), (flags.indexOf("S") != -1));
	if (x.equals("")) {
            errorMsg += "Error - Illegal Number '" + work + "'" + newLine; 
            return work;
	}
	x = shiftDecimal(x, decimalShift);
        return x;
    }
    
    private String checkDefaultIndividualStr(String work) {
        if (flags.indexOf("0") != -1) { work = EditText.deleteNPC(work); }
	if (flags.indexOf("1") != -1) { work = EditText.deleteSpaces(work); }
	if (flags.indexOf("2") != -1) { work = EditText.deleteLeadingSpaces(work); }
	if (flags.indexOf("3") != -1) { work = EditText.deleteTrailingSpaces(work); }
	if (flags.indexOf("4") != -1) { work = EditText.reduceSpaces(work); }
	if (flags.indexOf("5") != -1) { work = work.toUpperCase(); }
	if (flags.indexOf("6") != -1) { work = work.toLowerCase(); }
	if (flags.indexOf("R") == -1) { 
            checkCharSet(work, charSet);      // charSet is string of valid charries
        } else {
            if (charSet.length() > 0 && work.length() > 0 ) { // make sure reg expr & value are not empty
                Pattern patt = Pattern.compile(charSet);  // charSet is a Reg Expr
                Matcher mat = patt.matcher(work);
                if (!mat.matches()) {
                    errorMsg += "Error - Input data failed the Regular Expression test for this value." + newLine;
                }
            }
        }
        return work;
    }
    
    private String checkForTildeNum(String work) {
        
        boolean allowZero = flags.indexOf("Z") != -1;
        String parts [];
        
        // if tildes are allowed AND work string starts with ± do this:
        if ( (flags.indexOf("T") > -1) && (work.indexOf("±") == 0) ) {
            work = work.substring(1);
            work = "-" + work + "~" + work;
        }
        // if no tildes are allowed OR there are no tildes in the work string do this:
        if ( (flags.indexOf("T") == -1) || (work.indexOf("~") == -1) ) {
            work = checkDefaultIndividualNum(work);
            if (checkRangeError(work, allowZero)) {
                return work;
            }
            return work;
	}
	parts = work.split("~");
	if (parts.length != 2) {
            errorMsg += "Error - Too many tildes in range expression." + newLine;
            return work;
        }
        parts [0] = checkDefaultIndividualNum(parts[0]);
        parts [1] = checkDefaultIndividualNum(parts[1]);
        if (!parts[0].equals(parts [1]) ) {
            if (parts[0].length() == 0 || parts[1].length() == 0) {
                errorMsg += "Error - Invalid / Missing value in range expression." + newLine;
                return work;
            }
            if (checkRangeError(parts[0], allowZero)) {
                return work;
            }
            if (checkRangeError(parts[1], allowZero)) {
                return work;
            }
            if (Float.parseFloat(parts[0]) > Float.parseFloat(parts[1])) {
                errorMsg += "Error! Beginning value in a range cannot be greater than the ending value." + newLine;
                return work;
            }
            work = parts [0] + "~" + parts [1];
        } else {    // If we get something like  15 ~ 15
            work = parts[0];
            if (checkRangeError(work, allowZero)) {
		return work;
            }
        }
        return work;
    }
    
    private String checkForTildeStr(String work) {

        String hold;
        String part1;
        String part2;
        String parts[];

        if (flags.indexOf("T") == -1) {
            work = checkDefaultIndividualStr(work);
            checkLength(work);
            return work;
	} else {
            if (work.indexOf("~") == -1) {
		work = checkDefaultIndividualStr(work);
		checkLength(work);
		return work;
            }
            parts = work.split("~");
            if (parts.length != 2) {
                errorMsg += "Error - Illegal use of multiple tildes in data element." + newLine;
                return work;
            } else {
                part1 = checkDefaultIndividualStr(parts[0]);
                part2 = checkDefaultIndividualStr(parts[1]);
		if (!part1.toUpperCase().equals(part2.toUpperCase()) ) {
                    work = part1 + "~" + part2;
                    if (part1.length() == 0 || part2.length() == 0) {
                        errorMsg += "Error - One or both values in range specification are missing." + newLine;
                    	return work;
                    }
                    checkLength(part1);
                    checkLength(part2);
                    if (part1.toUpperCase().compareTo(part2.toUpperCase()) > 0) {
                        errorMsg += "Error - Beginning value in a range cannot be greater than the ending value." + newLine;
                    }
                    return work;
		} else {                   // If we get something like  AA ~ AA
                    work = part1;
                    checkLength(work);
                    return work;
		}
            }
	}
    }

    private String checkLength(String work) {
	int len = work.length();
	if (len < minLen) {
            errorMsg += "Error - Data string must be at least " + Integer.toString(minLen) + " characters long." + newLine;
        }
	if (maxLen > 0  && len > maxLen) {
            errorMsg += "Data string cannot be greater than " + Integer.toString(maxLen) + " characters long." + newLine;
        }
        return work;
    }
    
    private boolean checkNumerics(String work) {
        String ch;
        String charSet = "0123456789";
        
        for (int i = 0; i < work.length(); i++) {
            ch = work.substring(i, i+1);
            if (charSet.indexOf(ch) == -1) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkRangeError(String work, boolean allowZero) {
        if (work.length() == 0) {
            return false;
        }
        if (!isFloat(work) ) {
            errorMsg += "Error - Illegal numeric value '" + work + "'" + newLine;
            return true;
        }
	float valu = Float.parseFloat(work);
	if (!allowZero) {
            if (valu == 0.0) {
                errorMsg += "Error - Data value cannot be zero." + newLine;
                return true;
            }
	}
	if (valu < minimum) {
            errorMsg += "Error - Data value '" + work + "' is less than minimum '" + Float.toString(minimum) + "'" + newLine;
            return true;
        }
	if (valu > maximum) {
            errorMsg += "Error - Data value '" + work + "' is greater than maximum '" + Float.toString(maximum) + "'" + newLine;
            return true;
        }
        return false;
    }
    
    public String checkString(String argParmValue, String argDelimiter, String argFlags,
            int argMinLen, int argMaxLen, String argCharSet, String argNewLine) {
        
        // I parse a String input value
        // After calling me, you must call the getErrorMsg() method.
        // if getErrorMsg() returns an empty string, field is good and parsed to normalized format
        // otherwise field is BAD and getErrorMsg() explains why
        // Note that if a select box is associated with this field,
        // you must use a separate method to ensure this is a valid selctbox item
        // charset is all valid characters allowed or a regular expression if flags contains an R
                
        // flags:
        
        // T = Tilde Allowed
        // 0 = delete NPC
        // 1 = delete SP
        // 2 - delete LS
        // 3 - delete TS
        // 4 - reduce SP
        // 5 - force UC
        // 6 - force LC
        // R - othCharSet is a Reg Expr
     
        String hold;
        String items[];
        String result;
        String work;
        
        // Set instance variables from arguments passed
               
        charSet = argCharSet;
        decimalShift = 0;
        delimiter = argDelimiter;
        errorMsg = "";        // Init Global error message to nothing
        flags = argFlags;
        maximum = 0;
        maxLen = argMaxLen;
        minimum = 0;
        minLen = argMinLen;
        multipliers = null;
        newLine = argNewLine;
        parmValue = argParmValue;
               
        if (parmValue.length() == 0) {      //If parm is  empty string, don't bother
            return "";
        }
        result = delimiter;
       	if (delimiter.length() > 0) {
            hold = "";
            items = parmValue.split(delimiter);
            for (int i = 0; i < items.length; i++) {
		hold = checkForTildeStr(items[i]) + delimiter;
		if (result.indexOf(delimiter + hold) == -1) {
                    result += hold;
                }
            }
            result = result.substring(1, result.length() - 1);
	} else {
            result = checkForTildeStr(parmValue);
	}
	return result;
}
    
    private String cookIndividual(String raw) {
        String result;
        result = shiftDecimal(raw, -decimalShift);
        result = formatNumber(result, 0);
        return result;
    }
    
    private String cookTildes(String raw, boolean tilde) {
        String part1;
        String part2;
        String parts[];
        String result = "";

        if (!tilde || raw.indexOf("~") == -1) {
            result = cookIndividual(raw);
        } else {
            parts = raw.split("~");
            part1 = cookIndividual(parts[0]);
            part2 = cookIndividual(parts[1]);
            result = part1 + "~" + part2;
            if (part1.indexOf("-") == 0) {
                part1 = part1.substring(1);
                if (part1.equals(part2)) {
                    result = "±" + part1;
                }
            }
        }
        return result;
    }
    

    
     private String formatNumber(String number, int decimalPlaces) {
        // I take a valid parsed numeric string and apply a best fit multiplier
        // I also insert commas if requested to group thousands
        // first remove any sign
        
        int decimalPosition;
        int fLen;
        String fraction;
        int iLen;
        String junk;
        boolean minusSign = false;
        String mult; // multiplier to use
        int shift;  // decimal places to shift
        String whole;
        int wLen;
        String work;
        int z;  // number of zeroes found
        
        if (number.indexOf("-") == 0) {
            minusSign = true;
            number = number.substring(1);
        }
        
        // separate integer from fraction
        
        decimalPosition = number.indexOf(".");
        fraction = "";
        whole = number;
        if (decimalPosition != -1) {
            whole = number.substring(0, decimalPosition);
            fraction = number.substring(decimalPosition + 1);
        }
        
        // if integer part is zero, remove it
        
        if (whole.equals("0")) {
            whole = "";
        }
        iLen = whole.length();
        fLen = fraction.length();
        shift = 0;  // decimal places to shift
        mult = ""; // multiplier to use
        
        // if integer part is non-zero
        // walk down multiplier list and find biggest Multiplier that will
        // give me 1 or more digits in the integer result
        
        if (iLen > 0) {
            if (multipliers.indexOf("D") != -1) {
                if (iLen > 1) {
                    shift = 1;
                    mult = "D";
                }
            }
            if (multipliers.indexOf("H") != -1) {
                if (iLen > 2) {
                    shift = 2;
                    mult = "H";
                }
            }
            if (multipliers.indexOf("K") != -1) {
                if (iLen > 3) {
                    shift = 3;
                    mult = "K";
                }
            }
            if (multipliers.indexOf("M") != -1) {
                if (iLen > 6) {
                    shift = 6;
                    mult = "M";
                }
            }
            if (multipliers.indexOf("G") != -1) {
                if (iLen > 9) {
                    shift = 9;
                    mult = "G";
                }
            }
            if (multipliers.indexOf("T") != -1) {
                if (iLen > 12) {
                    shift = 12;
                    mult = "T";
                }
            }
            if (multipliers.indexOf("P") != -1) {
                if (iLen > 15) {
                    shift = 15;
                    mult = "P";
                }
            }
            if (multipliers.indexOf("E") != -1) {
                if (iLen > 18) {
                    shift = 18;
                    mult = "E";
                }
            }
            
            // shift decimal to the left and apply multiplier
            
            if (shift > 0 ) {
                fraction = whole.substring(whole.length() - shift) + fraction;
                whole = whole.substring(0, whole.length() - shift);
            }
        }    
        
        // Now we handle situations where the integer part is zero and
        // we need to normalize the fractional part with whatever
        // multipliers are allowed for negative powers of ten
        // first we'll count the number of leading zeroes on the fraction
       
        z = 0;  // number of zeroes found
        junk = fraction;
        while (junk.indexOf("0") == 0) {
            junk = junk.substring(1);
            z++;
        }
        
        // now z has number leading zeroes in the fraction
        
        if (iLen == 0 && fLen != 0) {
            if (multipliers.indexOf("d") != -1) {
		if (z > -1) {
                    shift = -1;
                    mult = "d";
                }
            }	
            if (multipliers.indexOf("c") != -1) {
		if (z > -1) { 
                    shift = -2;
                    mult = "c";
                }
            }
            if (multipliers.indexOf("m") != -1) {
		if (z > -1) { 
                    shift = -3; 
                    mult = "m";
                }
            }
            if (multipliers.indexOf("u") != -1) {
		if (z > 2) { 
                    shift = -6; 
                    mult = "u";
                }
            }
            if (multipliers.indexOf("n") != -1) {
		if (z > 5) {
                    shift = -9;
                    mult = "n";
                }
            }
            if (multipliers.indexOf("p") != -1) {
		if (z > 8) { 
                    shift = -12; 
                    mult = "p";
                }
            }
            if (multipliers.indexOf("f") != -1) {
		if (z > 11) { 
                    shift = -15; 
                    mult = "f";
                }
            }
            if (multipliers.indexOf("a") != -1) {
		if (z > 14) { 
                    shift = -18;
                    mult = "a";
                }
            }

            // shift decimal point right if m is non zero
            
            if (shift != 0) {
		shift = -shift;
		fraction = fraction + "000000000000000000000";
		whole += fraction.substring(0, shift);
		fraction = fraction.substring(shift);
            }
        }
        
        // remove any leading zeroes from the integer

	while (whole.indexOf("0") == 0) {
		whole = whole.substring(1);
	}
	
	// Make sure we have 1 zero if null integer

	if (whole.length() == 0) {
            whole = "0";
        }

	// reduce trailing zeroes on fraction to none

	while (fraction.endsWith("0")) {
            fraction = fraction.substring(0, fraction.length() - 1);
	}
	if (mult == "") {
            if (fraction.length() < decimalPlaces) {
		fraction += "0000000000000000000000";
                fraction = fraction.substring(0, decimalPlaces);
            }
        }

	// insert commas if requested

	if (commas) {
            
            // Do integer first

            work = "";
            wLen = whole.length();
            while (wLen > 3) {
                wLen = wLen - 3;
                work = "," + whole.substring(wLen) + work;
            	whole = whole.substring(0, wLen);
            }
            whole += work;

            // Now do fraction

            work = "";
            wLen = fraction.length();
            while (wLen > 3) {
            	wLen = wLen - 3;
		work = work + fraction.substring(0, 3) + ",";
		fraction = fraction.substring(3);
            }
            fraction = work + fraction;
	}

	// Now Reassemble the number

	number = whole;
	if (number.equals("")) {
            number = "0"; 
        }
	if (fraction.length() > 0) {
            number += "." + fraction; 
        }

	// Add the new Multiplier if any

	if (mult.length() != 0) { 
            number += mult; 
        }
		
	// Give back negative sign if necessary

	if (minusSign) {
            number = "-" + number; 
        }
	return number;
    }
    
    public String getErrorMsg() {
        return errorMsg;
    }
    
    private boolean isFloat(String work) {
        float f;
        try {
            f = Float.parseFloat(work);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private String parseNumber(String number, boolean decimalOK, boolean sign) {

	// If I find a bad number I return a zero length string

	//	number is a string to parse
	//	decimal OK is true if a decimal fraction is allowed
	//	multipliers are valid multiplier suffixes
	//	sign is true if we allow a plus or minus

	int dec;
        int decPointLeft;
        int decPointRight;
        String fraction;
        int len;
        boolean minusSign;
	String multiplier = "";
        int myIndex;
        String result;
        String whole;
        String work;
        
	number = EditText.deleteSpaces(number);
	len = number.length();
	if (len == 0) {
            return "";
        }
	minusSign = false;
	if (sign) {
            if (number.indexOf("-") == 0) { 
                minusSign = true; 
            } 
            if (number.indexOf("+") == 0 || minusSign == true) { 
                number = number.substring(1);
            }
            if (number.length() == 0) { 
                return "";
            }
	}
	work = number.substring(number.length() - 1);
	if (multipliers.indexOf(work) != -1) {   // Is it a valid multiplier?
            multiplier = work;
            len = number.length();
            if (len == 1) { 
                return ""; 
            }
            number = number.substring(0, --len); // remove valid multiplier
	}
        //	at this point, minusSign is true if there was a valid minus sign
        //	multiplier contains any valid multiplier if one was found
	dec = number.indexOf(".");
	if (dec == -1) {
            whole = number;
            fraction = "";
	} else {
            whole = number.substring(0, dec );
            fraction = number.substring(dec + 1);
	}

	//	The integer portion is now in whole
	//	the decimal portion is in fraction if a decimal point was found
        //	remove any pesky commas

	whole = EditText.deleteCommas(whole);
	fraction = EditText.deleteCommas(fraction);

	if (checkNumerics(whole) == false)  {return ""; }
	if (checkNumerics(fraction) == false)  {return ""; }

	if (whole.length() == 0) { 
            whole = "0";
        }
	// Change to units (no multipliers)
	decPointRight = 0;
        decPointRight = ".DHK..M..G..T..P..E".indexOf(multiplier);
	//if (multiplier.equals("D")) { decPointRight = 1; }
	//if (multiplier.equals("H")) { decPointRight = 2; }
	//if (multiplier.equals("K")) { decPointRight = 3; }
	//if (multiplier.equals("M")) { decPointRight = 6; }
	//if (multiplier.equals("G")) { decPointRight = 9; }
	//if (multiplier.equals("T")) { decPointRight = 12; }
	//if (multiplier.equals("P")) { decPointRight = 15; }
	//if (multiplier.equals("E")) { decPointRight = 18; }
	if (decPointRight > 0 ) {
            fraction = fraction + "00000000000000000000000000000";
            whole = whole + fraction.substring(0, decPointRight);
            fraction = fraction.substring(decPointRight);
	}
	decPointLeft = 0;
	decPointLeft = ".dcm..u..n..p..f..a".indexOf(multiplier);
        //if (multiplier.equals("d")) { decPointLeft= 1; }
	//if (multiplier.equals("c")) { decPointLeft= 2; }
	//if (multiplier.equals("m")) { decPointLeft= 3; }
	//if (multiplier.equals("u")) { decPointLeft= 6; }
	//if (multiplier.equals("n")) { decPointLeft= 9; }
	//if (multiplier.equals("p")) { decPointLeft= 12; }
	//if (multiplier.equals("f")) { decPointLeft= 15; }
	//if (multiplier.equals("a")) { decPointLeft= 18; }
	if (decPointLeft > 0 ) {
            whole = "00000000000000000000000000" + whole;
            fraction = whole.substring(whole.length() - decPointLeft) + fraction;
            whole = whole.substring(0, whole.length() - decPointLeft);
	}
        
	// reduce multiple leading zeroes to 1
	myIndex = whole.indexOf("0");
	while (myIndex == 0) {
            whole = whole.substring(1);
            myIndex = whole.indexOf("0");
	}
	if (whole.length() == 0) {
            whole = "0"; 
        }

	// reduce trailing zeroes on fraction to none
	while (fraction.endsWith("0") ) {
            fraction = fraction.substring(0,fraction.length() - 1);
	}

	// Force negative sign off if result is zero
	if (fraction.length() == 0 && whole.equals("0")) {
            minusSign = false; 
        }
	result = whole;	
	if (minusSign) { 
            result = "-" + result; }
	if (fraction.length() != 0 ) {
            if (!decimalOK) {
		errorMsg += "Error - Decimal Fractions are not allowed" + newLine;
		return "";
            }
            result = result + "." + fraction; 
	}
	return result;
}
      
    private String shiftDecimal(String number, int places) {
        // I take a string of numeric characters and shift the decimal point left or right
        // the number of specified places.
        // number contains a valid number with no commas or spaces
        // negative values shift decimal point left
        // positive right
        
        // return number unchanges if value = 0 or 
        // places to shift = 0
        
        
        int decimalPosition;
        String fraction;
        boolean minusSign;
        String whole;
        
        if (places == 0) {
            return number;
        }
        if (Float.parseFloat(number) == 0.0) {
            return "0";
        }
        
        // Minus sign?
        
        minusSign = false;
        if (number.indexOf("-") == 0 ) {
            minusSign = true;
            number = number.substring(1);
        }
        
        // separate integer from fraction
        
        decimalPosition = number.indexOf(".");
        fraction = "";
        whole = number;
        if (decimalPosition != -1) {
            whole = number.substring(0, decimalPosition);
            fraction = number.substring(decimalPosition + 1);
        }
        
        // if integer part is zero, remove it
        
        if (whole.equals("0")) {
            whole = "";
        }

        // if places is negative, sheft decimal left
        
        if (places < 0) {
            places = -places;
            while (whole.length() < places) {
                whole = "0" + whole;
            }
            fraction = whole.substring(whole.length() - places) + fraction;
            whole = whole.substring(0,whole.length() - places);
        } else {    // else we shift right
            while (fraction.length() < places) {
                fraction += "0";
            }
            whole += fraction.substring(0, places);
            fraction = fraction.substring(places);
        }
        
        // remove any leading zeroes from the integer
        
        while (whole.indexOf("0") == 0) {
            whole = whole.substring(1);
        }
        
        // make sure we have at least 1 zero
        
        if (whole.length() == 0) {
            whole = "0";
        }
        
        // trim trailing zeroes on fraction to none
        
        while (fraction.endsWith("0")) {
            fraction = fraction.substring(0, fraction.length() - 1);
        }
        
        // Reassemble and add sign if necessary
        
        number = whole;
        if (fraction.length() > 0) {
            number += "." + fraction;
        }
        if (minusSign) {
            number = "-" + number;
        }
        return number;
    }
    
    public String toCooked(String raw, String argMultipliers, String argDelimiter,
            int argDecimalShift, boolean duplicatesOK, boolean tilde, boolean argCommas) {
        // Warning!!!
        // This function assumes that the raw string to cook contains no syntax errors
        // For any dynamic unvalidated input, you MUST call the parser function first.
        
        errorMsg = "";        // Init Global error message to nothing
        String hold;
        String items[];
        String result = "";
                
        // Set instance variables
        
        commas = argCommas;
        decimalShift = argDecimalShift;
        delimiter = argDelimiter;
        multipliers = argMultipliers;
        
        
        if (raw.length() == 0) {            // if raw was an empty string
            return result;                  // return an empty string as cooked result
        }
        // Are delimiters allowed?
        if (delimiter.length() == 0) {      // Nope
            // Process any possible ranges
            result = cookTildes(raw, tilde);
        } else {                            // Otherwise multiple values are possible
            result = delimiter;             // Init result to the delimiter
            hold = "";                      // Init hold to nothing
            items = raw.split(delimiter);   // Create items array split by delimiter
            for (int i = 0; i < items.length; i++) { // for each item in array
                hold = cookTildes(items[i], tilde) + delimiter; // process any tildes
                if (duplicatesOK            // if duplicates are allowed
                    || result.indexOf(delimiter + hold) == -1) { // or this is not a dupe
                    result += hold;         // append to result string
                }
            }
            result = result.substring(1,result.length() - 1); // remove begin and end delimiter
        }
        return result;
    }
    
    public String toRaw(String argParmValue, String argMultipliers, String argDelimiter, String argFlags,
            float argMinimum, float argMaximum, int argDecimalShift, String argNewLine) {
        
        // I parse a value object
        // After calling me, you must call the getErrorMsg() method.
        // if getErrorMsg() returns an empty string, field is good and parsed to raw format
        // otherwise field is BAD and getErrorMsg() explains why
        // minimum and maximum should be in raw format
        // Note that if a select box is associated with this field,
        // you must use a separate method to ensure this is a valid selctbox item
        
        // flags:
        // D = Duplicates allowed
        // T = Tilde Allowed
        // Z = Zeroes allowed
        // F - Fractions (decimal point) allowed
        
        String hold;
        String items[];
        String result;
        String work;
        
        // Set instance variables from arguments passed
                
        charSet = "";
        decimalShift = argDecimalShift;
        delimiter = argDelimiter;
        errorMsg = "";        // Init Global error message to nothing
        flags = argFlags;
        maximum = argMaximum;
        maxLen = 0;
        minimum = argMinimum;
        minLen = 0;
        multipliers = argMultipliers;
        newLine = argNewLine;
        parmValue = argParmValue;
               
        parmValue = EditText.deleteCommas(EditText.deleteSpaces(parmValue));
	if (parmValue.length() == 0) {
            return "";
        }
	// Do we have any delimiters defined?
	if (delimiter.length() > 0) {   // Yes
	    result = delimiter;
            hold = "";
            items = parmValue.split(delimiter);
            for (int i = 0; i < items.length; i++) {
                hold = checkForTildeNum(items[i] ) + delimiter;
		if ( (flags.indexOf("D") != -1)     
                    || (result.indexOf(delimiter + hold) == -1) ) {   // ignore duplicates unless allowed
                        result += hold;
                }
            }
            // Finally remove beginning and trailing delimiters
            result = result.substring(1, result.length() - 1);
        } else {
            // do this if there were no legal delimiters to process
            result = checkForTildeNum(parmValue);
	}
        return result;
    }
    
    public String validateRawNum(String argParmValue, String argDelimiter, String argFlags,
            float argMinimum, float argMaximum, GPSselectBox aDeSelectBoxObject, String argNewLine) {
        
        // I check a raw numeric value object to ensure it does not break any rule.
        // if I return an empty string, field is good
        // otherwise field is BAD and returned string result explains why
        // minimum and maximum should be in raw format
        
        // flags:
        // D = Duplicates allowed
        // T = Tilde Allowed
        // Z = Zeroes allowed
        // F - Fractions (decimal point) allowed
        // S - Signed (Negative) values allowed
        
        String hold;
        String items[];
        String result;
        String work;
        
        // Set instance variables from arguments passed
                
        charSet = "";
        decimalShift = 0;
        delimiter = argDelimiter;
        deSelectBoxObject = aDeSelectBoxObject;
        errorMsg = "";        // Init Global error message to nothing
        flags = argFlags;
        maximum = argMaximum;
        maxLen = 0;
        minimum = argMinimum;
        minLen = 0;
        multipliers = "U";
        newLine = argNewLine;
        parmValue = argParmValue;
                
        parmValue = EditText.deleteCommas(EditText.deleteSpaces(parmValue));
	if (parmValue.length() == 0) {
            return "";
        }
	// Do we have any delimiters defined?
	if (delimiter.length() > 0) {   // Yes
	    result = delimiter;
            hold = "";
            items = parmValue.split(delimiter);
            for (int i = 0; i < items.length; i++) {
                hold = checkForTildeNum(items[i] ) + delimiter;
		if ( (flags.indexOf("D") != -1)     
                    || (result.indexOf(delimiter + hold) == -1) ) {   // ignore duplicates unless allowed
                        result += hold;
                }
            }
            // Finally remove beginning and trailing delimiters
            result = result.substring(1, result.length() - 1);
        } else {
            // do this if there were no legal delimiters to process
            result = checkForTildeNum(parmValue);
	}
        if (errorMsg.equals("")) {
            if (deSelectBoxObject == null) {
                return "";
            } else {
                for (int j = 0; j < deSelectBoxObject.size(); j++) {
                    if (result.equals(deSelectBoxObject.getOptionValue1(j))) {
                        return "";
                    }
                }
                errorMsg = "Error! Value '" + result + "' not found in Select Box " + deSelectBoxObject.getSelectBoxName() + "'";
            }
        }
        return errorMsg;
    }
    
    public String validateRawStr(String argParmValue, String argDelimiter, String argFlags,
            int argMinLen, int argMaxLen, String argCharSet, GPSselectBox aDeSelectBoxObject, String argNewLine) {
        
        // I check a raw string value object to ensure it does not break any rule.
        // if I return an empty string, field is good
        // otherwise field is BAD and returned string result explains why
                        
        // flags:
        
        // T = Tilde Allowed
        // 0 = delete NPC
        // 1 = delete SP
        // 2 - delete LS
        // 3 - delete TS
        // 4 - reduce SP
        // 5 - force UC
        // 6 - force LC
        // R - othCharSet is a Reg Expr
     
        String hold;
        String items[];
        String result;
        String work;
        
        // Set instance variables from arguments passed
                
        deSelectBoxObject = aDeSelectBoxObject;
        
        
        charSet = argCharSet;
        decimalShift = 0;
        delimiter = argDelimiter;
        errorMsg = "";        // Init Global error message to nothing
        flags = argFlags;
        maximum = 0;
        maxLen = argMaxLen;
        minimum = 0;
        minLen = argMinLen;
        multipliers = null;
        newLine = argNewLine;
        parmValue = argParmValue;
               
        if (parmValue.length() == 0) {      //If parm is  empty string, don't bother
            return "";
        }
        result = delimiter;
       	if (delimiter.length() > 0) {
            hold = "";
            items = parmValue.split(delimiter);
            for (int i = 0; i < items.length; i++) {
		hold = checkForTildeStr(items[i]) + delimiter;
		if (result.indexOf(delimiter + hold) == -1) {
                    result += hold;
                }
            }
            result = result.substring(1, result.length() - 1);
	} else {
            result = checkForTildeStr(parmValue);
	}
        if (errorMsg.equals("")) {
            if (deSelectBoxObject == null) {
                return "";
            } else {
                for (int j = 0; j < deSelectBoxObject.size(); j++) {
                    if (result.equals(deSelectBoxObject.getOptionValue1(j))) {
                        return "";
                    }
                }
                errorMsg = "Error! Value '" + result + "' not found in Select Box " + deSelectBoxObject.getSelectBoxName() + "'";
            }
        }
        return errorMsg;
    }
}
