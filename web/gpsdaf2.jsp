<%@page contentType="text/html"%>
<%@page language="java" import="java.util.*" session="true"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
	<title>Galco Parametric Search - Add Data</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
                
        <!-- gpsdaf2.jsp

        Modification History
        
        version 1.3.00
        
        09/10/2007      DES     Modified to use Ajax to obtain 
                                line/family/subfamily codes
        09/10/2007      DES     Add common.js and
                                gpscommon.js support
        
        04/25/2008      DES     support 4 divisions
        
                
        -->

<script language="JavaScript" type="text/javascript">
<!--

function checkDefaultIndividualNum(work, obj, fieldNum) {
        var nFlags = aFlags[fieldNum];
        var DEMultipliers = aDEMultipliers[fieldNum];
	var x = parseNumber(work, (nFlags.indexOf("F") != -1), DEMultipliers, (nFlags.indexOf("S") != -1));
	if (x == "") {
		alert("Illegal Number"); 
		globalDefaultValid = false;
		obj.focus();
		return work;
	}
	if (globalTB) { x = shiftDecimal(x,parseInt(aDecShift[fieldNum])); }
        //alert ("raw value is " + x);
	return x;
}


function checkDefaultIndividualStr(work, obj, fieldNum) {
        var sFlags = aFlags[fieldNum];
	if (sFlags.indexOf("0") != -1) { work = doDeleteNPC(work); }
	if (sFlags.indexOf("1") != -1) { work = deleteSpaces(work); }
	if (sFlags.indexOf("2") != -1) { work = deleteLeadingSpaces(work); }
	if (sFlags.indexOf("3") != -1) { work = deleteTrailingSpaces(work); }
	if (sFlags.indexOf("4") != -1) { work = reduceSpaces(work); }
	if (sFlags.indexOf("5") != -1) { work = work.toUpperCase(); }
	if (sFlags.indexOf("6") != -1) { work = work.toLowerCase(); }
	if (sFlags.indexOf("R") == -1) {
                // First build the string of valid charries
                var charSet = aCharSet[fieldNum];
                if (sFlags.indexOf("U") != -1) { charSet += UC; }
                if (sFlags.indexOf("L") != -1) { charSet += LC; }
                if (sFlags.indexOf("S") != -1) { charSet += " "; }
                if (sFlags.indexOf("N") != -1) { charSet += NU; }
                if (sFlags.indexOf("A") != -1) { charSet += "'"; }
                if (sFlags.indexOf("Q") != -1) { charSet += '"'; }
		if (!checkCharSet(work, charSet)) {
			obj.focus();
			globalDefaultValid = false;
			return work;
		}
	} else {
		var rExpr = aCharSet[fieldNum];
		var flags = "g";
		if (rExpr.length > 0 && work.length > 0) {
			var myRegExp = new RegExp(rExpr,flags);
			if (!myRegExp.test(work)) {
				alert ("The value you entered failed the Regular Expression test specified in the rules for this field.");
				globalDefaultValid = false;
				obj.focus();
				return work;
			}
		}
	}
	return work;
}

function checkForTildeNum(work, obj, fieldNum) {
        var nFlags = aFlags[fieldNum];
	if (nFlags.indexOf("T") == -1) {
		work = checkDefaultIndividualNum(work, obj, fieldNum);
		checkRange(work, obj, fieldNum);
		return work;
	} else {
		if (work.indexOf("~") == -1) {
			work = checkDefaultIndividualNum(work, obj, fieldNum);
			checkRange(work, obj, fieldNum);
			return work;
		}
		var parts = work.split("~");
		if (parts.length != 2) {
			alert ("Illegal use of multiple tildes in data element.");
			globalDefaultValid = false;
			obj.focus();
			return work;
		} else {
			var part1 = checkDefaultIndividualNum(parts[0], obj, fieldNum);
			var part2 = checkDefaultIndividualNum(parts[1], obj, fieldNum);
			if (part1 != part2) {
				work = part1 + "~" + part2;
				if (part1.length == 0 || part2.length == 0) {
					alert ("One or both values in range specification are missing.");
					globalDefaultValid = false;
					obj.focus();
					return work;
				}
				checkRange(part1, obj, fieldNum);
				checkRange(part2, obj, fieldNum);
				if (parseFloat(part1) > parseFloat(part2)) {
					alert ("Error! Beginning value in a range cannot be greater than the ending value.");
					globalDefaultValid = false;
					obj.focus();
				}
				return work;
			} else {
                                // If we get something like  15 ~ 15
				work = part1;
				checkRange(work, obj, fieldNum);
				return work;
			}
                        return work;
		}
	}
}

function checkForTildeStr(work, obj, fieldNum) {
	var hold;
	var min;
	var max;
	var len;
        var sFlags = aFlags[fieldNum];
	if (sFlags.indexOf("T") == -1) {
		work = checkDefaultIndividualStr(work, obj, fieldNum);
		checkLength(work, obj, fieldNum);
		return work;
	} else {
		if (work.indexOf("~") == -1) {
			work = checkDefaultIndividualStr(work, obj, fieldNum);
			checkLength(work, obj, fieldNum);
			return work;
		}
		var parts = work.split("~");
		if (parts.length != 2)
		{
			alert ("Illegal use of multiple tildes in data element.");
			globalDefaultValid = false;
			obj.focus();
			return work;
		}
		else
		{
			var part1 = checkDefaultIndividualStr(parts[0], obj, fieldNum);
			var part2 = checkDefaultIndividualStr(parts[1], obj, fieldNum);
			if (part1.toUpperCase() != part2.toUpperCase())
			{
				work = part1 + "~" + part2;
				if (part1.length == 0 || part2.length == 0)
				{
					alert ("One or both values in range specification are missing.");
					globalDefaultValid = false;
					obj.focus();
					return work;
				}
				checkLength(part1, obj, fieldNum);
				checkLength(part2, obj, fieldNum);
				if (part1.toUpperCase() > part2.toUpperCase())
				{
					alert ("Error! Beginning value in a range cannot be greater than the ending value.");
					globalDefaultValid = false;
					obj.focus();
				}
				return work;
			} else   {                   // If we get something like  AA ~ AA
				work = part1;
				checkLength(work, obj, fieldNum);
				return work;
			}
		}
	}
}

function checkLength(work, obj, fieldNum) {
	var len = work.length;
	var min = aMin[fieldNum];
	if (len > 0 && len < min) 	{
		alert ("Data value must be at least " + min + " characters long.");
		globalDefaultValid = false;
		return;
	}
	max = aMax[fieldNum];
	if (max > 0  && len > max) {
		alert ("Data value cannot be greater than " + max + " characters long.");
		globalDefaultValid = false;
		return;
        }
}

function checkNumeric(obj, fieldNum) {
        globalTB = aDETextBoxSize[fieldNum] != 0;       
        var oRaw = document.getElementById("raw" + fieldNum);
	var work = obj.value;
	work = deleteCommas(deleteSpaces(work));
	if (globalTB) { obj.value = work; }
	globalDefaultValid = true;
	if (work.length > 0) {
		var delim = aDelim[fieldNum];
                var nFlags = aFlags[fieldNum];
	        // Do we have any delimiters defined?
		if (delim.length > 0) {   // Yes
			var resultSet = delim;
			var hold = "";
			var items = work.split(delim);
			for (var i = 0; i < items.length; i++) {
				hold = checkForTildeNum(items[i], obj, fieldNum) + delim;
				// ignore duplicates unless allowed
				if ((nFlags.indexOf("D")  != -1)
					|| (resultSet.indexOf(delim + hold) == -1)) {
                                    resultSet += hold;
                                }
			}
			// Finally delete beginning and trailing delimiters
			resultSet = resultSet.slice(1, resultSet.length - 1);
		} else {
			// do this if there were no delimiters to process
			resultSet = checkForTildeNum(work, obj, fieldNum);
		}
		oRaw.value = resultSet;  // This was txtdefaultvalue
		//alert ("resultSet = " + resultSet);
		if (globalDefaultValid && globalTB) { obj.value = reformat(resultSet, fieldNum); }
	} else {
		oRaw.value = ""; // ensure normalized default value(s) are null if work was null
		if (globalTB) { obj.value = ""; }              
	}
}

function checkPartNumber() {
    var myForm = document.form1;
    var work = myForm.partNum.value;
    work = work.toUpperCase();
    myForm.partNum.value = work;
    if (!checkCharSet(work, UC + LC + NU + SP + "/.,>;:~!@#$%^&*()-_+=")) {
        myForm.partNum.focus();
    }
    partNumNameCheck();
    return;
}

function checkRange(work, obj, fieldNum) {
        var nFlags = aFlags[fieldNum];
	var len = work.length;
	var valu = parseFloat(work);
	if (nFlags.indexOf("Z") == -1) {
		if (len > 0 && valu == 0.0) {
			alert ("Data value cannot be zero.");
			globalDefaultValid = false;
			obj.focus();
			return;
		}
	}
	hold = aMin[fieldNum];
	min = parseFloat(hold);
	if (!isNaN(min)) {
		if (len > 0  && valu < min) {
			alert ("Data value is too small.");
			globalDefaultValid = false;
			obj.focus();
			return;
		}
	}
	hold = aMax[fieldNum];
	max = parseFloat(hold);
	if (!isNaN(max)) {
		if (len > 0  && valu > max) {
			alert ("Data value cannot be greater than maximum value specified.");
			globalDefaultValid = false;
			obj.focus();
		}
	}
}

function checkString(obj, fieldNum) {
        // Maybe we need a special version of this for select boxes only
	var work = obj.value;
	var delim = aDelim[fieldNum];
        var resultSet = delim;
       	globalDefaultValid = true;
	if (delim.length > 0) {
		var hold = "";
		var items = work.split(delim);
		for (var i = 0; i < items.length; i++) {
			hold = checkForTildeStr(items[i],obj, fieldNum) + delim;
			if (resultSet.indexOf(delim+hold) == -1) { resultSet += hold }
		}
		resultSet = resultSet.slice(1, resultSet.length - 1);
	} else {
		resultSet = checkForTildeStr(work, obj, fieldNum);
	}
	obj.value = resultSet;
}

function clearForm() {
    var myForm = document.form1;
    var oMsg = document.getElementById("Msg");
    oMsg.innerHTML = "";
    var oHandle = null;
    myForm.partNum.value="";
    for (var i = 0; i < f2; i++) {
        if (aFlags[i].indexOf("n") != -1) {
            if (parseInt(aDETextBoxSize[i]) != 0) {
                oHandle = document.getElementById("raw" + i);
                oHandle.value = aDefault[i];
                oHandle = document.getElementById("inputObject" + i);
                oHandle.value = reformat(aDefault[i],i);
            } else {
                oHandle = document.getElementById("inputObject" + i);
                oHandle.selectedIndex = parseInt(aDefault[i]);
            }
        }
        if (aFlags[i].indexOf("s") != -1) {
            oHandle = document.getElementById("inputObject" + i);
            if (parseInt(aDETextBoxSize[i]) != 0) {
                oHandle.value = aDefault[i];
            } else {
                oHandle.selectedIndex = parseInt(aDefault[i]);
            }
        }
        if (aFlags[i].indexOf("l") != -1) {
            oHandle = document.getElementsByName("inputObject" + i);
            oHandle[0].checked = false;
            oHandle[1].checked = false;
            if (aDefault[i] == "Y") {oHandle[0].checked = true; }
            if (aDefault[i] == "N") {oHandle[1].checked = true; }
        }
    }
}

function cookSB(index) {
    // Nothing needs to be done here because
    // the Select Box value is raw and
    // select box display option is already cooked
    // alert ("Fix me to cook select box entries");
}

function cookTB(index) {
    //alert ("Cooking raw"+index);
    var oRaw = document.getElementById("raw" + index);
    var oCooked = document.getElementById("inputObject" + index);
    var work = oRaw.value;
    oCooked.value = reformat(work, index);
}

function createAjaxRequest() {
    var request = null;
    if (window.XMLHttpRequest) {
        request = new XMLHttpRequest();
    } else {
        if (window.ActiveXObject) {
            try {
                request = new ActiveXObject("Msml2.XMLHTTP");
            } catch (err1) {
                try {
                    request = new ActiveXObject("Microsoft.XMLHTTP");
                } catch (err2) {
                }
            }
        }
    }
    if (request == null) {
        alert ("Attempt to create Ajax Request Object failed!");
    } else {
        //alert ("Attempt to create Ajax Request Object successful!");
    }
    return request;
}

function displayCookedValues() {
    var myForm = document.form1;
    var work;
    for (var i = 0; i < f; i++) {
        if (aFlags[i].indexOf("n") != -1) {
            if (aDETextBoxSize[i] != "0") {
                cookTB(i);
            } else {
                cookSB(i);
            }
        }
    }
}

function doDeleteNPC(work) {
	var result = "";
	var strlen = work.length;
	var myChar;
	for (var i = 0; i < strlen; i++) {
		myChar = work.charAt(i);
		if (myChar >= " " && myChar <= "~") {
			result += myChar;
		}
	}
	return result;
}

function formatNumber(number, multipliers, decimalPlaces, commas) {
	// I take a parsed number (valid format) and apply a best-fit multiplier
	// this function expects the number contains no illegal characters

	// first delete sign if any

	var minusSign = false;
	if (number.charAt(0) == "-") {
		minusSign = true;
		number = number.slice(1);
	}

        // separate integer from decimal parts

	var decPosition = number.indexOf(".");
	var fraction = "";
	var whole = number;
	if (decPosition != -1) {
		whole = number.slice(0, decPosition);
		fraction = number.slice(decPosition + 1);
	}

	// if integer part is a zero, remove it.

	if (whole == "0") { whole = ""; }
	var iLen = whole.length;
	var fLen = fraction.length;
	var m = 0;  // decimal places to shift
	var M = ""; // Multiplier to use
	
	// if integer part is non zero,	
	// walk down multiplier tree and find biggest Multiplier that will give me
	// 1 or more digits in the integer result
  
  if (iLen > 0  ) {	
	if (multipliers.indexOf("D") != -1) {
		if (iLen > 1) { m = 1; M = "D";}
	}
	if (multipliers.indexOf("H") != -1) {
		if (iLen > 2) { m = 2; M = "H";}
	}
	if (multipliers.indexOf("K") != -1) {
		if (iLen > 3) { m = 3; M = "K";}
	}
	if (multipliers.indexOf("M") != -1) {
		if (iLen > 6) { m = 6; M = "M";}
	}
	if (multipliers.indexOf("G") != -1) {
		if (iLen > 9) { m = 9; M = "G";}
	}
	if (multipliers.indexOf("T") != -1) {
		if (iLen > 12) { m = 12; M = "T";}
	}
	if (multipliers.indexOf("P") != -1) {
		if (iLen > 15) { m = 15; M = "P";}
	}
	if (multipliers.indexOf("E") != -1) {
		if (iLen > 18) { m = 18; M = "E";}
	}

	// shift decimal to the left and apply multiplier
	
	if (m > 0 ) {
		fraction = whole.slice(whole.length - m) + fraction;
		whole = whole.slice(0, whole.length - m);
	}
  }

	// handle situations where the integer part is zero and
	// we need to normalize the fractional part with whatever
	// multipliers are allowed for negative powers of ten

	// first count number of leading zeroes on the fraction

	var z = 0; // number of zeroes found
	var junk = fraction;
	while (junk.indexOf("0") == 0) {
		junk = junk.slice(1);
		z++;
	}

	// z contains number of leading zeroes in the fraction

  if (iLen == 0 && fLen != 0 )  {

	if (multipliers.indexOf("d") != -1) {
		if (z > -1) { m = -1; M = "d";}
	}	
	if (multipliers.indexOf("c") != -1) {
		if (z > -1) { m = -2; M = "c";}
	}
	if (multipliers.indexOf("m") != -1) {
		if (z > -1) { m = -3; M = "m";}
	}
	if (multipliers.indexOf("u") != -1) {
		if (z > 2) { m = -6; M = "u";}
	}
	if (multipliers.indexOf("n") != -1) {
		if (z > 5) { m = -9; M = "n";}
	}
	if (multipliers.indexOf("p") != -1) {
		if (z > 8) { m = -12; M = "p";}
	}
	if (multipliers.indexOf("f") != -1) {
		if (z > 11) { m = -15; M = "f";}
	}
	if (multipliers.indexOf("a") != -1) {
		if (z > 14) { m = -18; M = "a";}
	}

	// shift decimal point right if m is non zero

	if (m != 0) {
		m = -m;
		fraction = fraction + "000000000000000000";
		whole = whole + fraction.slice(0, m);
		fraction = fraction.slice(m);
	}
  }
			
	// delete any leading zeroes from the integer

	while (whole.indexOf("0") == 0) {
		whole = whole.slice(1);
	}
	
	// Make sure we have 1 zero if null integer

	if (whole.length == 0) { whole = "0"; }

	// reduce trailing zeroes on fraction to none

	while (fraction.charAt(fraction.length - 1) == "0" ) {
		fraction=fraction.slice(0,fraction.length -1);
	}
	if (M == "") {
		if (fraction.length < decimalPlaces) {
			fraction = fraction + "0000000000000000000000";
			fraction = fraction.slice(0,decimalPlaces);
		}
	}

	// insert commas if requested

	if (commas) {
		// Do integer first

		var work = "";
		var wLen = whole.length;
		while (wLen > 3) {
			wLen = wLen -3;
			work = "," + whole.slice(wLen) + work;
			whole = whole.slice(0,wLen);
		}
		whole = whole + work;

		// Now do fraction

		work = "";
		wLen = fraction.length;
		while (wLen > 3) {
			wLen = wLen - 3;
			work = work + fraction.slice(0,3) + ",";
			fraction = fraction.slice(3);
		}
		fraction = work + fraction;
	}

	// Reassemble the number

	number = whole;
	if (number == "") { number = "0"; }
	if (fraction.length > 0) { number = number + "." + fraction; }

	// Add the new Multiplier if any

	if (M.length != 0) { number = number + " " + M; }
		
	// Give back negative sign if necessary

	if (minusSign) {number = "-" + number; }
	return number;
}

function getMessage(divName) {
	if (divName == "header"){return "Enter the Parametric data values for an existing Part Number.";}
	if (divName == "moAdd"){return "Click to Add parametric data for this Part Number.";}
        if (divName == "moClear"){return "Click Clear to reset this form to the original defaults.";}
        if (divName == "moExit"){return "Click Exit to abandon this entry and return to the previous Menu.";}
        if (divName == "moAutoClearN"){return "Check this box if you want form to retain last successful Add values.";}	
        if (divName == "moAutoClearY"){return "Check this box if you want form values to be reset to defaults after an Add.";}	
        if (divName == "moReplaceN"){return "Check this box if you do not want to replace any pre-existing data.";}	
        if (divName == "moReplaceY"){return "Check this box if you want to replace any pre-existing data with this data.";}	
        if (divName == "moStartOver"){return "Click this button to enter data for a new Family/SubFamily.";}
        if (divName == "moPartNum"){return "Enter a part number here. The part number must already exist within the WDS database.";}
        return "";
}

function parseNumber(number, decimalOK, multipliers, sign) {

	// If I find a bad number I return a zero length string

	//	number is a string to parse
	//	decimal OK is true if a decimal fraction is allowed
	//	multipliers are valid multiplier suffixes
	//	sign is true if we allow a plus or minus

	var work;
	var multiplier = "";
	number = deleteSpaces(number);
	var len = number.length;
	if (len == 0) { return ""; }
	var minusSign = false;
	if (sign) {
		work = number.charAt(0);
		if (work == "-") { minusSign = true; } 
		if (work == "+" || minusSign == true) { number = number.slice(1); }
		if (number.length == 0) { return ""; }
	}
	work = number.charAt(number.length - 1);
	if (multipliers.indexOf(work) != -1) {
		multiplier = work;
		len = number.length;
		if (len == 1) { return ""; }
		number = number.slice(0, --len);
	}
        //	at this point, minusSign is true if there was a valid minus sign
        //	multiplier contains any valid multiplier if one was found
	var whole;
	var fraction;
	var dec = number.indexOf(".");
	if (dec == -1) {
		whole = number;
		fraction = "";
	} else {
		whole = number.slice(0, dec );
		fraction = number.slice(dec + 1, number.length);
	}

	//	The integer portion is now in whole
	//	the decimal portion is in fraction if a decimal point was found

	//	delete any pesky commas

	whole = deleteCommas(whole);
	fraction = deleteCommas(fraction);

	if (checkCharSet(whole,NU) == false)  {return ""; }
	if (checkCharSet(fraction,NU) == false)  {return ""; }

	if (whole.length == 0) { whole = "0"; }
	// Change to units (no multipliers)
	var decPointRight = 0;
	if (multiplier == "D") { decPointRight = 1; }
	if (multiplier == "H") { decPointRight = 2; }
	if (multiplier == "K") { decPointRight = 3; }
	if (multiplier == "M") { decPointRight = 6; }
	if (multiplier == "G") { decPointRight = 9; }
	if (multiplier == "T") { decPointRight = 12; }
	if (multiplier == "P") { decPointRight = 15; }
	if (multiplier == "E") { decPointRight = 18; }
	if (decPointRight > 0 ) {
		fraction = fraction + "00000000000000000000000000000";
		whole = whole + fraction.slice(0,decPointRight);
		fraction = fraction.slice(decPointRight);
	}
	var decPointLeft = 0;
	if (multiplier == "d") { decPointLeft= 1; }
	if (multiplier == "c") { decPointLeft= 2; }
	if (multiplier == "m") { decPointLeft= 3; }
	if (multiplier == "u") { decPointLeft= 6; }
	if (multiplier == "n") { decPointLeft= 9; }
	if (multiplier == "p") { decPointLeft= 12; }
	if (multiplier == "f") { decPointLeft= 15; }
	if (multiplier == "a") { decPointLeft= 18; }
	if (decPointLeft > 0 ) {
		whole = "00000000000000000000000000" + whole;
		fraction = whole.slice(whole.length - decPointLeft) + fraction;
		whole = whole.slice(0, whole.length - decPointLeft);
	}
        
	// reduce multiple leading zeroes to 1
	var myIndex = whole.indexOf("0");
	while (myIndex == 0) {
		whole = whole.slice(1);
		myIndex = whole.indexOf("0");
	}
	if (whole.length == 0) { whole = "0"; }

	// reduce trailing zeroes on fraction to none
	//while (fraction.charAt(fraction.length - 1) == "0" ) {
	//	fraction=fraction.slice(0,fraction.length -1);
	//}

	// Force negative sign off if result is zero
	if (fraction.length == 0 && whole == "0") { minusSign = false; }
	var result = whole;	
	if (minusSign) { result = "-" + result; }
	if (fraction.length != 0 ) {
		// fraction = fraction.slice(0,decimalPositions);
		// reduce trailing zeroes on fraction to none
		while (fraction.charAt(fraction.length - 1) == "0" ) {
			fraction=fraction.slice(0,fraction.length -1);
		}
	}
	//if (decimalOK == false  && fraction.length > 0) {
	//	alert ("Decimal Fractions are not allowed");
        //	return "";
	//}
	if (fraction.length != 0 ) {
		if (!decimalOK) {
			alert ("Decimal Fractions are not allowed");
			return "";
		}
		result = result + "." + fraction; 
	}
	//var j = parseFloat(result);
	return result;
}

function partNumExistsCheck() {
    var myForm = document.form1;
    var work = myForm.partNum.value;
    ajaxPartNumReq = createAjaxRequest(); // ajaxPartNumReq is a global
    ajaxPartNumReq.onreadystatechange = partNumRequestStateChange;
    ajaxPartNumReq.open ("GET", "getPartNumInfo.do?partNum=" + encodeURIComponent(work)
        + "&ts=" + new Date().getTime(), true); // asynchronous call
    ajaxPartNumReq.send (null);
}

function partNumNameCheck() {
    var myForm = document.form1;
    var work = myForm.partNum.value;
    work = deleteLeadingSpaces(deleteTrailingSpaces(work));
    work = work.toUpperCase();
    myForm.partNum.value = work;
    if (!checkCharSet(work, UC + NU + SP + "#$%^&*()_-=+/:;>.,?" )) {
        myForm.partNum.focus();
        return;
    }
    if (work.length != 0) {
        partNumExistsCheck();
    } else {
        document.getElementById("Msg").innerHTML = "";
    }
}

function partNumRequestStateChange(){
    if (ajaxPartNumReq.readyState == 4) {
        if (ajaxPartNumReq.status == 200) {
            //partNumExistsParser();
            partNumInfoXMLParser();
        } else {
            alert ("Unexpected Error " + ajaxPartNumReq.status);
            var myForm = document.form1;
            myForm.partNum.value = "";
        }
    }
}

function partNumExistsParser() {
    var results = ajaxPartNumReq.responseText;
    if (results.indexOf("false") != -1) {
        alert ("Error - this Part Number does not exist.");
        var myForm = document.form1;
        myForm.partNum.focus();
        return;
    }
    if (results.indexOf("true") != -1) {
        return;
    }
    alert ("An unexpected error occurred when checking the Part Number.");
    var myForm = document.form1;
    myForm.partNum.value = "";
}

function partNumInfoXMLParser() {
    //alert ( ajaxPartNumReq.responseText);
    var pn = "";
    var fc = "";
    var sc = "";
    var hpsd = "";
    var message = "";
    var myForm = document.form1;
    var results = ajaxPartNumReq.responseXML.getElementsByTagName("partInfo");
    var PN = results[0].getElementsByTagName("partNum");
    if (PN[0]) {
        pn = PN[0].firstChild.nodeValue;
        myForm.partNumXML.value = pn;
        //alert (pn);
    }
    myForm.partFamilyCodeXML.value = "";
    var FC = results[0].getElementsByTagName("partFamilyCode");
    if (FC[0]) {
        fc = FC[0].firstChild;
        if (fc) {
            fc = fc.nodeValue;
            myForm.partFamilyCodeXML.value = fc;
            //alert (" fc is " + fc);
        }
    }
    myForm.partSubfamilyCodeXML.value = "";
    var SC = results[0].getElementsByTagName("partSubfamilyCode");
    if (SC[0]) {
        sc = SC[0].firstChild;
        if (sc) {
            sc = sc.nodeValue;
            myForm.partSubfamilyCodeXML.value = sc;
            //alert (" fc is " + fc);
        }
    }
    var HPSD = results[0].getElementsByTagName("partHasPSData");
    if (HPSD[0]) {
        hpsd = HPSD[0].firstChild.nodeValue;
        myForm.partHasPSDataXML.value = hpsd;
        //alert (hpsd);
    }
    if (pn.length == 0) {
        message = "This Part Number does not exist.";
        alert (message);
        var myForm = document.form1;
        document.getElementById("Msg").innerHTML = message;
        myForm.partNum.focus();
        return;
    }
    
    if (hpsd == "true") {
        message = " Family Code " + myForm.partFamilyCodeXML.value;
        message += " Subfamily Code " + myForm.partSubfamilyCodeXML.value;
        message = "This Part Number currently has Parametric Data in" + message;
        alert (message);
        document.getElementById("Msg").innerHTML = message;
    } else {
        document.getElementById("Msg").innerHTML = "";
    }
}

function reformat(work, fieldNum) {
    if (work.length > 0) {
    	// Do we have any delimiters defined?
	var delim = aDelim[fieldNum];
	if (delim.length > 0) {
            var resultSet = delim;
            var hold = "";
            var items = work.split(delim);
            for (var i = 0; i < items.length; i++) {
                hold = reformat2(items[i], fieldNum) + delim;
		// ignore duplicates
		if (aFlags[fieldNum].indexOf("D") != -1
                    || resultSet.indexOf(delim + hold) == -1) {
                        resultSet += hold;
                }
            }
            // delete beginning and trailing delimiters
            resultSet = resultSet.slice(1, resultSet.length - 1);
        } else {   // do this if there were no delimiters found
            resultSet = reformat2(work, fieldNum);
        }
        return resultSet;
    }
    return work;
}

function reformat2(work, fieldNum) {
    if (aFlags[fieldNum].indexOf("T") == -1) {
	work = reformat3(work, fieldNum);
	return work;
    } else {
        if (work.indexOf("~") == -1) {
            work = reformat3(work, fieldNum);
            return work;
	}
	var parts = work.split("~");
	var part1 = reformat3(parts[0], fieldNum);
	var part2 = reformat3(parts[1], fieldNum);
	work = part1 + "~" + part2;
	return work;
    }
}

function reformat3(work, fieldNum) {
    var s = parseInt(aDecShift[fieldNum]);
    work = shiftDecimal(work, -s);
    work = formatNumber(work, aDEMultipliers[fieldNum], 0, true);
    return work;
}

function shiftDecimal(number,places) {

//	I am used to shift the decimal point in a numeric value left or right
//	number contains a valid number with no embedded commas or spaces
//	places is the number of places to shift
//	negative numbers shift decimal point left
//	positive numbers shift decimal point right

	// return number unchanged if number or places equals zero

	// first delete sign if any

	var minusSign = false;
	if (number.charAt(0) == "-") {
		minusSign = true;
		number = number.slice(1);
	}

        // separate integer from decimal parts
	var decPosition = number.indexOf(".");
	var fraction = "";
	var whole = number;
	if (decPosition != -1) {
		whole = number.slice(0, decPosition);
		fraction = number.slice(decPosition + 1);
	}

	// if integer part is a zero, delete it.
	if (whole == "0") {
		whole = "";
	}
	var iLen = whole.length;
	var fLen = fraction.length;

	// If places is < 0, shift decimal to the left
	if (places < 0 ) {
		places = - places;
		while (whole.length < places) {
			whole = "0" + whole;
		}
		fraction = whole.slice(whole.length - places) + fraction;
		whole = whole.slice(0, whole.length - places);
	} else {

	// If places is > 0, shift decimal to the right
		while (fraction.length < places) {
			fraction += "0";
		}
		whole = whole + fraction.slice(0, places);
		fraction = fraction.slice(places);
	}

	// delete any leading zeroes from the integer
	while (whole.indexOf("0") == 0) {
		whole = whole.slice(1);
	}
	
	// Make sure we have 1 zero if null integer
	if (whole.length == 0) { whole = "0"; }

	// reduce trailing zeroes on fraction to none
	while (fraction.charAt(fraction.length - 1) == "0" ) {
		fraction=fraction.slice(0,fraction.length -1);
	}

	// Reassemble the number
	number = whole;
	if (fraction.length > 0) { number = number + "." + fraction; }

	// Restore negative sign if necessary
	if (minusSign) {number = "-" + number; }
	return number;
}


function setDefaults() {
	var myForm = document.form1;
        var work = "";
        displayCookedValues();
        var splashMessage = "${sessionScope.statusMessage}";
        if (splashMessage.length != 0 ) {
            alert (splashMessage);
        }
        work = splashMessage.toUpperCase();
        if (work.indexOf("ERROR") == -1) {
            if (myForm.autoClear[0].checked == true) {
                clearForm();
            }
        }
        myForm.partNum.focus();
}


function myValidator() {
    var myForm = document.form1;
    var oHandle;
    var oHandle2;
    var oName = "";
    var junk = "";

    // Check replace option
    
    oHandle = document.getElementsByName("replaceExistingData");
    junk = getSelectedRadioValue(oHandle);
    if (junk == "") {
        alert ("Please select an option for pre-existing data");
        oHandle[0].focus();
        return false;
    }
    
    // Check AutoClear option
    
    oHandle2 = document.getElementsByName("autoClear");
    junk = getSelectedRadioValue(oHandle2);
    if (junk == "") {
        alert ("Please select an option for Auto Clear on Add");
        oHandle2[0].focus();
        return false;
    }
    
    // Check Part Number field here
    
    junk = myForm.partNum.value;
    if (junk.length == 0) {
        alert ("Please enter a valid Part Number.");
        myForm.partNum.focus();
        return false;
    }
    
    
    if (myForm.partHasPSDataXML.value == "true") {
        if (oHandle[1].checked == true) {
            alert ("This part number has parametric data and No Replace Option is set. Aborting...");
            oHandle[1].focus();
            return false;        
        } 
    }
    
    if (myForm.partFamilyCodeXML.value != ""
        && myForm.partSubfamilyCodeXML.value != "") {
            if (myForm.partFamilyCodeXML.value != "${sessionScope.familyCode}"
                || myForm.partSubfamilyCodeXML.value != "${sessionScope.subfamilyCode}" ) {
                    var answer = confirm ("WARNING! The original Family/Subfamily and Parametric Data for this Part Number will be changed. Do you still wish to proceed?");
                    if (!answer) {
                        return false;
                    }
            }
    }    

    // check mandatory fields
     
    for (var i = 0; i < f ; i++) {
        if (aDERequired[i] == "Y") {
            oName = "inputObject" + i;
            oHandle = document.getElementById(oName);            
            if (aFlags[i].indexOf("l") == -1) {
                if (aDETextBoxSize[i] != 0) {
                    if (oHandle.value == "") {
                        alert ("Please enter a value for " + aLabel[i]);
                        oHandle.focus();
                        return false;
                    }
                } else {
                      if (oHandle.selectedIndex == 0) {
                          alert ("Please select a value for " + aLabel[i]);
                          oHandle.focus();
                          return false;
                      }
                }
            } else {
                oHandle = document.getElementsByName(oName);
                junk = getSelectedRadioValue(oHandle);
                if (junk == "") {
                        alert ("Please select a value for " + aLabel[i]);
                        oHandle[0].focus();
                        return false;
                }
            }
        }
    }
       
    // Validation is complete
            
    myForm.validation.value = "OK";
    return true;
}

//-->
</script>
</head>

<body onload="setDefaults()">
   
<script language="JavaScript" type="text/javascript">
<!--    

	var checkMinAndMax = false;
	var globalDefaultValid = false;
        var globalTB = false;
        var ajaxPartNumReq = null;
        
        // parm data tables
        
        var x;
        var f = 0;
        var f2 = 0;
        var aCharSet = new Array();
        var aDecShift = new Array();
        var aDefault = new Array();
        var aDelim = new Array();
        var aDEMultipliers = new Array();
        var aDEOptions = new Array;
        var aDEOptionsSize = new Array();
        var aDERequired = new Array();
        var aDETextBoxSize = new Array();
	var aFlags = new Array();
        var aLabel = new Array();
        var aMax = new Array();
        var aMin = new Array();
        var aToolTip = new Array();
	var aUnits = new Array();
        var aPreviousValue = new Array();

        <c:forEach var="item" items="${sessionScope.generatedScript}">
            ${item}
        </c:forEach>
        
        <c:forEach var="item" items="${sessionScope.previousValue}">
            ${item}
        </c:forEach>
        
//-->    
</script>  

<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
       
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form method="post" name="form1" action="gpsdaf3.do" onsubmit="return myValidator()" >
<p>
    <input type="hidden" name="validation" value="Error" />
    <input type="hidden" name="partNumXML" value="" />
    <input type="hidden" name="partFamilyCodeXML" value="" />
    <input type="hidden" name="partSubfamilyCodeXML" value="" />
    <input type="hidden" name="partHasPSDataXML" value="" />
</p>
    
<table border="0" width="100%">

<!-- Logo and Heading  -->

  <tr>
    <td align="center" colspan="2">
	<h2>
        Parametric Search Database<br />
            Add Parametric Data - Part 2
	</h2>
    </td>
  </tr>
  <tr>
    <td rowspan="3" align="center" width="25%">
      <img src="gl_25.gif" alt="Galco logo" /><br />

	<div class="toolTipSwitch">
		<input type="checkbox" 
<%
		String tip = (String) session.getAttribute("enableToolTips");
                if (tip != null && tip.equals("checked")) {
			out.println(" checked=\"checked\" ");
                }
%> 
		name="enableToolTips"
		value="checked"
		/>
		Enable Tool Tips
	</div>
    </td>
    <td>


<div  class="masthead" onmouseover="showTip(event, 'header')" 
      onmouseout="hideTip()" >

  <table border="1" width="98%"  align="center" >
  <tr><td><table border="0" width="100%">

<!--  Family Description  -->

    <tr>
      <td align="right" width="25%"><span class="headerLabel">
        Family:&nbsp;
      </span></td>
      <td align="left" width="25%"><span class="headerData">

        ${sessionScope.familyName}

      </span></td>

<!--  Subfamily Description  -->

      <td align="right" width="25%"><span class="headerLabel">
        Subfamily:&nbsp;
      </span></td>
        <td align="left" width="25%"><span class="headerData">

         ${sessionScope.subfamilyName}

        </span></td>
      </tr>

<!--  User ID  -->

    <tr>
      <td align="right" ><span class="headerLabel">
       User ID:&nbsp;
      </span></td>
      <td align="left" ><span class="headerData">
 
        ${sessionScope.auditUserID}

      </span></td>

<!--  Date  -->

        <td align="right"><span class="headerLabel">
          Date:&nbsp;
        </span></td>
        <td align="left"><span class="headerData">

        ${sessionScope.traxDate}

        </span></td>
      </tr>

      

  </table></td></tr>
 </table>

</div>

  </td></tr>
</table>
<br />


  <table border="0" width="100%">

<!-- Table Header  -->

    <tr>
        <td align='center' width='25%' >
            <span class='requiredLabel'>
                Fields in RED are required.
            </span>
        </td>
        <td colspan='2'>
            <div id="Msg"> <span class="units">
            ${statusMessage}&nbsp;
            </span></div>
        </td>
    </tr>
    
    
<!--  Replace pre-existing data?  -->

      <tr>
        <td align="right" >
          <span class="requiredLabel"> Replace Existing Data: &nbsp;</span>
        </td>
        <td align="left" colspan="2"><span class="datafield">
            <input type="radio" name="replaceExistingData" value = "Y" 
<%
      	String gwork = (String) session.getAttribute("replaceExistingData");
        if (gwork == null) {gwork="";}
        if (gwork.equals("Y")) {
            out.println (" checked=\"checked\" ");
        }
%>           
          onmouseover="showTip(event, 'moReplaceY')"
          onmouseout="hideTip()" 
          /> </span>
        <span class='units'>&nbsp;Yes&nbsp;&nbsp;&nbsp;</span>
        <span class="datafield">
            <input type="radio" name="replaceExistingData" value = "N" 
<%
      	if (gwork.equals("N")) {
            out.println (" checked=\"checked\" ");
        }
%>            
          onmouseover="showTip(event, 'moReplaceN')"
          onmouseout="hideTip()" 
          /> </span>
        <span class='units'>&nbsp;No</span>
        
        </td>
      </tr>
      
 <!--  AutoClear on Add?  -->

      <tr>
        <td align="right" >
          <span class="requiredLabel"> AutoClear on Add: &nbsp;</span>
        </td>
        <td align="left" colspan="2"><span class="datafield">
            <input type="radio" name="autoClear" value = "Y" 
<%
      	gwork = (String) session.getAttribute("autoClear");
        if (gwork == null) {gwork="";}
        if(gwork.equals("Y")) {
            out.println (" checked=\"checked\" ");
        }
%>   
          onmouseover="showTip(event, 'moAutoClearY')"
          onmouseout="hideTip()" 
          /> </span>
        <span class='units'>&nbsp;Yes&nbsp;&nbsp;&nbsp;</span>
        <span class="datafield">
            <input type="radio" name="autoClear" value = "N" 
<%
      	if(gwork.equals("N")) {
            out.println (" checked=\"checked\" ");
        }
%>           
          onmouseover="showTip(event, 'moAutoClearN')"
          onmouseout="hideTip()" 
          /> </span>
        <span class='units'>&nbsp;No</span>
        
        </td>
      </tr>
      
<!--  Part Number  -->

      <tr>
        <td align="right" >
          <span class="requiredLabel"> Part Number: &nbsp;</span>
        </td>
        <td align="left" colspan="2"><span class="datafield">
            <input type="text" size="32" maxlength="32" name="partNum"  

            value ="${sessionScope.previousPartNum}"
            
          onblur="checkPartNumber()"
          onmouseover="showTip(event, 'moPartNum')"
          onmouseout="hideTip()" 
          /> 
        </span></td>
      </tr>
   
<script language="JavaScript" type="text/javascript">
<!--
//
// Data Entry Object Generator
//
  for (x=0; x<f; x++) {
    var work = "";
    var num1 = 0;
    var num2 = 0;
    var opt = false;
    var notes = "";
    var flag = aFlags[x];
    var num = flag.indexOf("n") != -1;
    var str = flag.indexOf("s") != -1;
    var log = flag.indexOf("l") != -1;
    var dat = flag.indexOf("d") != -1;
    
    // We generate the following regardless of data type:

    document.write ("<tr><td align='right' size='25%'>");
    if (aDERequired[x] == "Y") {
        document.write ("<span class='requiredLabel'>");
    } else {
        document.write ("<span class='label'>");    
    }
    document.write (aLabel[x] + ":&nbsp;&nbsp;</span>");
    document.write ("</td><td align='left' size='30%'><span class='dataField'>");

    // Generate a hidden text box for numeric raw values
    /////////////////////////////////////////////////////////////////
    //  This may need to be fixed for String select boxes too!!!   //
    /////////////////////////////////////////////////////////////////
    // fixed by DES on 5/29/2007
    
    if (num) {
        document.write ("<input type='hidden' value='");  
        if (f2 > 0 ) {
            document.write (aPreviousValue[x] + "' ");
        } else {
            if (aDETextBoxSize[x] > 0 ) {    // Text Box?  // fixed 5/29/07
                document.write (aDefault[x] + "' ");       // fixed 5/29/07
            } else {                        // Oh, OK it is a Select Box  //fixed 5/29/07
                var optDefault = aDefault[x];             // fixed 5/29/07
                document.write (aDEOptions[x] [optDefault] [0] + "' "); // fixed 5/29/07
                // above gets raw value for default option in this selectbox
            }                                              // fixed 5/29/07
        }
        document.write (" name='raw" + String(x) + "' />");           
    } 
   
    // Generate a custom DE object based upon data type, etc.
    // Numeric Item or String Item?

    if (num || str) {     // Text Box Object?
        if (aDETextBoxSize[x] > 0 ) {
            document.write ("<input type='text' size='" + aDETextBoxSize[x] + "' ");
            if (str) {
                if (f2 > 0 ) {
                    document.write (" value='" + aPreviousValue[x] + "' ");
                } else {
                    document.write (" value='" + aDefault[x] + "' ");                
                }
            }
        } else {          // Oh, OK so it is a Select Box?
            document.write ("<select size='0' ");
            opt = true;
        }
        if (num) {
            document.write (" onblur='checkNumeric(this," + String(x) + ")' ");
        }
        if (str) {
            document.write (" onblur='checkString(this," + String(x) + ")' ");
        }
    }
    
    // Generate a check box for logicals
    
    if (log) {
        document.write ("<input type='radio' value='Y' ");
        document.write (" onmouseover='showTip(event, " + String(x) + ")' onmouseout='hideTip()' "); 
        if ((f2 == 0 && aDefault[x] == "Y")
            || (f2 > 0 && aPreviousValue[x] == "Y")) {
                document.write (" checked='checked' ");
        }
        document.write (" name='inputObject" + String(x) + "' /><span class='units'>&nbsp;&nbsp;Yes&nbsp;&nbsp;</span>");
        document.write ("<input type='radio' value='N' ");
        if ((f2 == 0 && aDefault[x] == "N")
            || (f2 > 0 && aPreviousValue[x] == "N")) {
                document.write (" checked='checked' ");
        }
    }
    document.write (" onmouseover='showTip(event, " + String(x) + ")' onmouseout='hideTip()' "); 
    document.write (" name='inputObject" + String(x) + "' />");
    if (log) {
        document.write ("<span class='units'>&nbsp;&nbsp;&nbsp;No</span>");
    }

    // Do we have Options to load in a select box?

    if (opt) {
        for (var i=0; i < aDEOptionsSize[x]; i++) {
            document.write ("<option value='" + aDEOptions[x] [i] [0] + "' ");
            if (f2 == 0) {
                if (parseInt(aDefault[x]) == i) {
                    document.write (" selected='selected' ");
                }
            } else {
                if (aDEOptions[x] [i] [0] == aPreviousValue[x]) {
                    document.write (" selected='selected' ");
                }           
            }
            document.write (">" + aDEOptions[x] [i] [1] + "</option>");
        }
        document.write ("</select>"); 
    }
    if (num) {
        document.write ("</span><span class='units'>&nbsp;" + aUnits[x] );
    }
    document.write ("</span></td><td align='left' size='45%'>");
    
//  Add any useful D.E. Notes

    if (num) {
        document.write ("<span class='limits'> Limits:&nbsp;"
            + formatNumber(shiftDecimal(aMin[x],-parseInt(aDecShift[x])), aDEMultipliers[x], 0, true) + "&nbsp;-&nbsp; "
            + formatNumber(shiftDecimal(aMax[x],-parseInt(aDecShift[x])), aDEMultipliers[x], 0, true) + "<br />");
        if (flag.indexOf("Z") == -1) { notes += "Non-zero"; }
        if (flag.indexOf("T") != -1) { notes += " Tilde-OK"; }
        if (flag.indexOf("S") == -1) { notes += " No-Sign"; }
        if (flag.indexOf("F") == -1) { notes += " Whole-Numbers"; }
        work = aDelim[x];
        if (work.length > 0) { notes += " Delim&nbsp;=&nbsp;" + work; }
        document.write (notes + " </span>");
    }
    if (str) {
        document.write ("<span class='limits2'> ");
        num1 = parseInt(aMin[x]);
        num2 = parseInt(aMax[x]);
        if (num1 == 0) {
            document.write ("Up&nbsp;to");
        } else {
            document.write ("&nbsp;" + num1 + "&nbsp;-");
        }
        document.write ("&nbsp;" + num2 + "&nbsp;Chars");
        if (flag.indexOf("T") != -1) { notes += " Tilde-OK"; }
        if (flag.indexOf("R") != -1) { notes += " Reg Expr"; }
        work = aDelim[x];
        if (work.length > 0) { notes += " Delim&nbsp;=&nbsp;" + work; }
        document.write (notes + " </span>");
    }
    document.write (" </td></tr>");
  }
  document.close();
  
//-->
</script> 

<!--  Continue or Clear  -->

      <tr>
        <td colspan="3">
		<br />
          <center>
            <input type="submit" value="Add" name="B1" 
	onmouseover="showTip(event, 'moAdd')" 
        onmouseout="hideTip()"
	/>

                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="Clear" name="B2" onclick="clearForm()" 
	onmouseover="showTip(event, 'moClear')" 
        onmouseout="hideTip()"
	/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="Start Over" name="B3" onclick='JavaScript: location.href="gpsdaf1.do";' 
	onmouseover="showTip(event, 'moStartOver')" 
        onmouseout="hideTip()"
	/>
        

            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsdf.jsp'; " 
	onmouseover="showTip(event, 'moExit')" 
        onmouseout="hideTip()"
	/>

          </center>
        </td>
      </tr>

    </table>
<br /> 
<br />
<p>
    <img src="http://www.w3.org/Icons/valid-xhtml10"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
</p>
</form>
</div>
</body>
</html>
