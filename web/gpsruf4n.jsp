<%@page contentType="text/html"%>
<%@page language="java" import="java.util.*" session="true"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
          "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
	<title>Galco Parametric Search - Copy Rule Set Part 3N</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>

        <jsp:useBean id="sRuleSet" class="gps.util.GPSrules" scope="session"/>
        

    <!-- gpsruf4n.jsp

    Modification History

    version 1.3.00


    04/23/2008      DES     Modified to support 4 Divisions

    -->


<!--	*************************************************************
	*		My JavaScript Functions and junk	      	*
	*************************************************************  -->


<script language="JavaScript" type="text/javascript">
<!--

function calcDEMultipliers() {
    var myForm = document.form1;
    var work = "";
    for (i = 0; i < 15; i++) {
        if (myForm.deMultipliers[i].checked == true) {
            work += myForm.deMultipliers[i].value;
	}
    }
    myForm.txtDEMultipliers.value = work;
    checkDefaultValue();
}

function calcDisplayMultipliers() {
	var myForm = document.form1;
	var work = "";
	for (i = 0; i < 15; i++) {
		if (myForm.displayMultipliers[i].checked == true) {
			work += myForm.displayMultipliers[i].value;
		}
	}
	myForm.txtDisplayMultipliers.value = work;
	checkMinAndMax = true;
	checkMinValue();
}

function checkAllowDuplicates() {
	myForm = document.form1;
	var sel = getSelectedRadioValue(myForm.parmDelimiter);
	if (sel == "") {
		myForm.allowDuplicates[1].checked = true;
		myForm.allowDuplicates[1].disabled = true;
		myForm.allowDuplicates[0].disabled = true;
		return;
	} else {
		myForm.allowDuplicates[0].disabled = false;
		myForm.allowDuplicates[1].disabled = false;
	}
}

function checkAllowFractions() {
	myForm = document.form1;
	var sel = getSelectedRadioValue(myForm.allowFractions);
	if (sel == "N") {
		alert ("When Allow Fractions is set to No, Decimal Digits will be forced to zero and only whole integer values will be allowed. Decimal fractions will be truncated.");
		myForm.minDecimalDigits.value = "0";
		myForm.minDecimalDigits.disabled = true;
		disableFractionDisplayMultipliers();
		disableFractionDEMultipliers();
		eraseMinMaxDefault();
	}
	if (sel == "Y") {
		myForm.minDecimalDigits.disabled = false;
		enableFractionDisplayMultipliers();
		enableFractionDEMultipliers();
	}
}

function checkBase() {
	myForm = document.form1;
	var sel = getSelectedRadioValue(myForm.unitsBase);
	if (sel != "D") {
		alert ("Decimal Digits will always be forced to zero when Base is set to Binary, Octal or Hexadecimal");
		myForm.minDecimalDigits.value = "0";
	}
}

function checkDefaultIndividual(work) {
	var myForm = document.form1;
	var x = parseNumber(work, myForm.allowFractions[0].checked, myForm.txtDEMultipliers.value, myForm.allowSign[0].checked);
	if (x == "") {
		alert("Illegal Number"); 
		globalDefaultValid = false;
		myForm.defaultValueCooked.focus();
		return work;
	}
	var s = parseInt(myForm.txtUnitsShift.value);
	x = shiftDecimal(x, s);
	return x;
}

function checkDefaultValue() {
 	// squish spaces and commas

	var myForm = document.form1;
	var work = myForm.defaultValueCooked.value;
	work = deleteCommas(deleteSpaces(work));
	myForm.defaultValueCooked.value = work;
	globalDefaultValid = true;
	if (work.length > 0) {
		// Do we have any delimiters defined?
		var delim = "";
		if (myForm.parmDelimiter[1].checked) { delim = ";"; }
		if (myForm.parmDelimiter[2].checked) { delim = "/"; }
		if (delim.length > 0) {
			var resultSet = delim;
			var hold = "";
			var items = work.split(delim);
			for (var i = 0; i < items.length; i++) {
				hold = checkForTilde(items[i]) + delim;
				// ignore duplicates
				if (myForm.allowDuplicates[0].checked == true
					|| resultSet.indexOf(delim + hold) == -1) {
                                            resultSet += hold;
                                }
			}
			// delete beginning and trailing delimiters
			resultSet = resultSet.slice(1, resultSet.length - 1);
		} else {   	// do this if there were no delimiters found
			resultSet = checkForTilde(work);
		}
		myForm.defaultValueRaw.value = resultSet;
		//alert ("resultSet = " + resultSet);
		if (globalDefaultValid) { myForm.defaultValueCooked.value = reformat(resultSet); }
	} else {     	// ensure normalized default value(s) are null if work was null
		myForm.defaultValueRaw.value = "";
	}
}

function checkDEObject() {
	// DE Text Box or Select Box has been selected
	var myForm = document.form1;
	var sel = getSelectedRadioValue(myForm.deObject);
	if (sel == "T") {
		myForm.deSelectBoxName.selectedIndex = 0;
		myForm.deSelectBoxName.disabled = true;
		myForm.deTextBoxSize.disabled = false;
	}
	if (sel == "S") {
		// DE Select Box has been selected
		myForm.deTextBoxSize.value = "";
		myForm.deSelectBoxName.disabled = false;
		myForm.deTextBoxSize.disabled = true;
	}
}

function checkDETextBoxSize() {
    // squish spaces and check for null or numerics
    var myForm = document.form1;
    var work = myForm.deTextBoxSize.value;
    work = deleteSpaces(work);
    myForm.deTextBoxSize.value = work;
    if (work.length > 0) {
    	if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value.");
            myForm.deTextBoxSize.focus();
            return;
	}
        if (parseInt(work) == 0) {
            myForm.deTextBoxSize.value = "";
            return;
        }
    }
}

function checkForTilde(work) {
	var myForm = document.form1;
	var hold;
	var min;
	var max;
	var len;
	if (!myForm.allowTilde[0].checked) {
		work = checkDefaultIndividual(work);
		checkRange(work);
		return work;
	} else {
		if (work.indexOf("~") == -1) {
			work = checkDefaultIndividual(work);
			checkRange(work);
			return work;
		}
		var parts = work.split("~");
		if (parts.length != 2) {
			alert ("Illegal use of multiple tildes in data element.");
			globalDefaultValid = false;
			myForm.defaultValueCooked.focus();
			return work;
		} else {
			var part1 = checkDefaultIndividual(parts[0]);
			var part2 = checkDefaultIndividual(parts[1]);
			if (part1 != part2) {
				work = part1 + "~" + part2;
				if (part1.length == 0 || part2.length == 0) {
					alert ("One or both values in range specification are missing.");
					globalDefaultValid = false;
					myForm.defaultValueCooked.focus();
					return work;
				}
				checkRange(part1);
				checkRange(part2);
				if (parseFloat(part1) > parseFloat(part2)) {
					alert ("Error! Beginning value in a range cannot be greater than the ending value.");
					globalDefaultValid = false;
					myForm.defaultValueCooked.focus();
				}
				return work;
			} else {  // If we get something like  15 ~ 15
        			work = part1;
				checkRange(work);
				return work;
			}
		}
	}
}

function checkForZero(work) {
	var myForm = document.form1;
	if (myForm.allowZero[1].checked) {
		if (parseFloat(work) == 0.0) {
			return true;
		}	
	}
	return false;
}

function checkMaxValue() {
	// Check Maximum Range Value
	var myForm = document.form1;
	var work = myForm.maxValueCooked.value;
	var fracOK = myForm.allowFractions[0].checked;
        var mult1 = myForm.txtDEMultipliers.value;
	var mult2 = myForm.txtDisplayMultipliers.value;
	var signOK = myForm.allowSign[0].checked;
	work = deleteCommas(deleteSpaces(work));
	myForm.maxValueCooked.value = work;
	if (work.length > 0) {
		var x = parseNumber(work, fracOK, mult1, signOK, true);
		if (x == "") {
			alert("Illegal Number"); 
			myForm.maxValueCooked.focus();
			return;
		} else {
			var s = parseInt(myForm.txtUnitsShift.value);
			x = shiftDecimal(x, s);
			myForm.maxValueRaw.value = x;
			x = shiftDecimal(x, -s);
			work = formatNumber(x, mult2, myForm.minDecimalDigits.value, true);
			myForm.maxValueCooked.value = work;
		}
		if (checkForZero(x)) {
			alert("This value cannot be 0"); 
			myForm.maxValueCooked.focus();
			return;
		}
	} else {
		myForm.maxValueRaw.value = "";
	}
}

function checkMinDecimalDigits() {
	// Check Decimal Digits
 	// squish spaces and check for null or numerics
	var myForm = document.form1;
	var work = myForm.minDecimalDigits.value;
	work = deleteSpaces(work);
	myForm.minDecimalDigits.value = work;
	if (work.length > 0) {
		if (checkCharSet(work, NU) == false) {
			alert ("Please enter a valid numeric value between 0 and 9.");
			myForm.minDecimalDigits.focus();
			return;
		}
		if (parseInt(work) > 9) {
			alert ("Please enter a valid numeric value between 0 and 9.");
			myForm.minDecimalDigits.focus();
			return;
		}
		// checkBase();
	}
}

function checkMinValue() {
	// Check Minimum Range Value
	var myForm = document.form1;
	var work = myForm.minValueCooked.value;
	var fracOK = myForm.allowFractions[0].checked;
        var mult1 = myForm.txtDEMultipliers.value;
	var mult2 = myForm.txtDisplayMultipliers.value;
	var signOK = myForm.allowSign[0].checked;
	work = deleteCommas(deleteSpaces(work));
	myForm.minValueCooked.value = work;
	if (work.length > 0) {
		var x = parseNumber(work, fracOK, mult1, signOK, true);
		if (x == "") {
			alert("Illegal Number"); 
			myForm.minValueCooked.focus();
			return;
		} else {
			var s = parseInt(myForm.txtUnitsShift.value);
			x = shiftDecimal(x, s);
			myForm.minValueRaw.value = x;
			x = shiftDecimal(x, -s);
			work = formatNumber(x, mult2, myForm.minDecimalDigits.value, true);
			myForm.minValueCooked.value = work;
		}
		if (checkForZero(x)) {
			alert("This value cannot be 0"); 
			myForm.minValueCooked.focus();
			return;
		}
	} else {
		myForm.minValueRaw.value = "";
	}
	if (checkMinAndMax == true) {
		checkMinAndMax = false;
		checkMaxValue();
	}
}

function checkParmDelimiter() {
	var myForm = document.form1;
	if (myForm.parmDelimiter[0].checked == true) {
		return;
	}
	if (myForm.dataType[2].checked == true 
            || myForm.dataType[3].checked == true) {
		alert ("You cannot use Delimiters with Logical or Date/Time Data Types.");
		myForm.parmDelimiter[0].checked = true;
		return;
	}
	if (myForm.dataType[0].checked == true 
            && myForm.parmDelimiter[1].checked == true) {
		alert ("You cannot use Comma Delimiters with Numeric Data Types.");
		myForm.parmDelimiter[0].checked = true;
		return;
	}
}

function checkQObject() {
	//  Text Box or Select Box has been selected
	var myForm = document.form1;
	var sel = getSelectedRadioValue(myForm.qObject);
	if (sel == "T") {
		myForm.qSelectBoxName.selectedIndex = 0;
		myForm.qSelectBoxName.disabled = true;
		myForm.qTextBoxSize.disabled = false;
	}
	if (sel == "S") {
		myForm.qTextBoxSize.value = "";
		myForm.qSelectBoxName.disabled = false;
		myForm.qTextBoxSize.disabled = true;
	}
}

function checkQTextBoxSize() {
    // squish spaces and check for null or numerics
    var myForm = document.form1;
    var work = myForm.qTextBoxSize.value;
    work = deleteSpaces(work);
    myForm.qTextBoxSize.value = work;
    if (work.length > 0) {
    	if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value.");
            myForm.qTextBoxSize.focus();
            return;
	}
        if (parseInt(work) == 0) {
            myForm.qTextBoxSize.value = "";
            return;
        }
    }
}

function checkRange(work) {
	var myForm = document.form1;
	var len = work.length;
	var valu = parseFloat(work);
	if (myForm.allowZero[1].checked == true) {
		if (len > 0 && valu == 0.0) {
			alert ("Data value cannot be zero.");
			globalDefaultValid = false;
			myForm.defaultValueCooked.focus();
			return;
		}
	}
	hold = myForm.minValueRaw.value;
	min = parseFloat(hold);
	if (!isNaN(min)) {
		if (len > 0  && valu < min) {
			alert ("Data value must be greater than minimum value specified.");
			globalDefaultValid = false;
			myForm.defaultValueCooked.focus();
			return;
		}
	}
	hold = myForm.maxValueRaw.value;
	max = parseFloat(hold);
	if (!isNaN(max)) {
		if (len > 0  && valu > max) {
			alert ("Data value cannot be greater than maximum value specified.");
			globalDefaultValid = false;
			myForm.defaultValueCooked.focus();
		}
	}
}

function checkSearchMax() {
	// Check Search Maximum
	var myForm = document.form1;
	var work = myForm.searchMax.value;
	work = deleteSpaces(work);
	myForm.searchMax.value = work;
	if (work.length > 0) {
		if (checkCharSet(work,NU) == false) {
			alert ("Please enter a valid numeric value between 0 and 500.");
			myForm.searchMax.focus();
			return;
		}
		if (parseInt(work) > 500) {
			alert ("Please enter a valid numeric value between 0 and 500.");
			myForm.searchMax.focus();
			return;
		}
	}
}

function checkSearchMin() {
	// Check Search Minimum
	var myForm = document.form1;
	var work = myForm.searchMin.value;
	work = deleteSpaces(work);
	myForm.searchMin.value = work;
	if (work.length > 0) {
		if (checkCharSet(work,NU) == false) {
			alert ("Please enter a valid numeric value between 0 and 100.");
			myForm.searchMin.focus();
			return;
		}
		if (parseInt(work) > 100) {
			alert ("Please enter a valid numeric value between 0 and 100.");
			myForm.searchMin.focus();
			return;
		}
	}
}

function checkSearchWeight() {
	// Check Search Weight
	var myForm = document.form1;
	var work = myForm.searchWeight.value;
	work = deleteSpaces(work);
	myForm.searchWeight.value = work;
	if (work.length > 0) {
		if (checkCharSet(work,NU) == false) {
			alert ("Please enter a valid numeric value between 0 and 100.");
			myForm.searchWeight.focus();
			return;
		}
		if (parseInt(work) > 100) {
			alert ("Please enter a valid numeric value between 0 and 100.");
			myForm.searchWeight.focus();
			return;
		}
	}
}

function checkSigDigits() {
	// Check Significant Digits
	var myForm = document.form1;
	var work = myForm.sigDigits.value;
	work = deleteSpaces(work);
	myForm.sigDigits.value = work;
	if (work.length > 0) {
		if (checkCharSet(work,NU) == false || work == "0") {
			alert ("Please enter a valid numeric value between 1 and 9.");
			myForm.sigDigits.focus();
			return;
		}
	}
}

function checkUnits() {
	var myForm = document.form1;
	var work = myForm.units.value;
	eraseMinMaxDefault();
	myForm.txtUnitsShift.value = unitsArray[myForm.units.selectedIndex] [1];
	if (myForm.txtUnitsShift.value != "0") {
		alert ("Since the units you have selected have an embedded multiplier, all data entry and display multipliers (except None) will be unchecked and disabled.");
		disableDEMultipliers();
		disableDisplayMultipliers();
		return;
	}
	enableDisplayMultipliers();
	enableDEMultipliers();
	if (myForm.allowFractions[1].checked == true) {
		disableFractionDEMultipliers();
		disableFractionDisplayMultipliers();
	}
}

function disableDEMultipliers() {
	var myForm = document.form1;
	var work = "";
	for (i = 0; i < 15; i++) {
		if (i != 8) {
			myForm.deMultipliers[i].checked = false;
			myForm.deMultipliers[i].disabled = true;
		}
	}
	calcDEMultipliers();
}

function disableDisplayMultipliers() {
	var myForm = document.form1;
	var work = "";
	for (i = 0; i < 15; i++) {
		if (i != 8) {
			myForm.displayMultipliers[i].checked = false;
			myForm.displayMultipliers[i].disabled = true;
		}
	}
	calcDisplayMultipliers();
}

function disableFractionDEMultipliers() {
	var myForm = document.form1;
	var work = "";
	for (i = 0; i < 8; i++) {
		myForm.deMultipliers[i].checked = false;
		myForm.deMultipliers[i].disabled = true;
	}
	calcDEMultipliers();
}

function disableFractionDisplayMultipliers() {
	var myForm = document.form1;
	var work = "";
	for (i = 0; i < 8; i++) {
		myForm.displayMultipliers[i].checked = false;
		myForm.displayMultipliers[i].disabled = true;
	}
	calcDisplayMultipliers();
}


function enableDEMultipliers() {
	var myForm = document.form1;
	var work = "";
	for (i = 0; i < 15; i++) {
		if (i != 8) { myForm.deMultipliers[i].disabled = false; }
	}
}

function enableDisplayMultipliers() {
	var myForm = document.form1;
	var work = "";
	for (i = 0; i < 15; i++) {
		if (i != 8) { myForm.displayMultipliers[i].disabled = false; }
	}
}

function enableFractionDEMultipliers() {
	var myForm = document.form1;
	var work = "";
	for (i = 0; i < 8; i++) {
		myForm.deMultipliers[i].disabled = false;
	}
}

function enableFractionDisplayMultipliers() {
	var myForm = document.form1;
	var work = "";
	for (i = 0; i < 8; i++) {
		myForm.displayMultipliers[i].disabled = false;
	}
}

function eraseMinMaxDefault() {
	var myForm = document.form1;
	myForm.minValueRaw.value = "";
	myForm.maxValueRaw.value = "";
	myForm.defaultValueRaw.value = "";
	myForm.minValueCooked.value = "";
	myForm.maxValueCooked.value = "";
	myForm.defaultValueCooked.value = "";
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

	// if integer part is a zero, delete it.

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
	if (divName == "dMultipliers"){return "These are the preferred multipliers used to display the search results.";}
	if (divName == "deMultipliers"){return "Only these multipliers are allowable as input during data entry.";}
	if (divName == "header"){return "You cannot change the Rule Scope, Family/Subfamily name, Data Type, or the Field Number here.";}
	if (divName == "moAllowAtSign"){return "At sign notation can be used when specifying things like torque ft lbs @ rpm: e.g., 6,000 @ 2,000.";}
	if (divName == "moallowDuplicates"){return "When delimiters are allowed, you must choose whether duplicate values can be specified.";}
	if (divName == "moallowFractions"){return "When set to No, all numeric values must be whole integers.";}
	if (divName == "moAllowSign"){return "Determines if a negative value can be entered. Positive signs are always stripped and never displayed.";}
	if (divName == "moAllowZero"){return "This rules determines if a zero value is allowed.";}
	if (divName == "moContinue"){return "Click continue to finish Copying this rule.";}
	if (divName == "moDefaultValue"){return "Enter an optional default value for data entry.";}
        if (divName == "moDEObjectSB"){return "Select this option to use a Select Box for Data Entry of this parameter.";}
	if (divName == "moDEObjectTB"){return "Select this option to use a Text Box for Data Entry of this parameter.";}
	if (divName == "moDESelectBoxName"){return "Enter the name of the Select Box that will contain allowable Data Entry values.";}
	if (divName == "moDETextBoxSize"){return "Enter the Data Entry Text Box size in number of characters.";}
	if (divName == "moExit"){return "Click Exit to abandon this rule and return to the Rules Menu.";}
	if (divName == "moMaxValue"){return "The parametric value cannot exceed this maximum limit.";}
	if (divName == "moMinDecimalDigits"){return "Determines minimum number of Decimal digits to display.";}
	if (divName == "moMinValue"){return "The parametric value cannot be less than this minimum limit.";}
	if (divName == "moParmDelimiter"){return "Use a Delimiter to allow MULTIPLE VALUES within a field.";}
	if (divName == "moQObjectSB"){return "Select this option to use a Select Box for Search parameter values.";}
	if (divName == "moQObjectTB"){return "Select this option to use a Text Box for Search parameter values.";}
	if (divName == "moQSelectBoxName"){return "Enter the name of the Select Box that will contain allowable search values.";}
	if (divName == "moQTextBoxSize"){return "Enter the Search Value Text Box size in number of characters.";}
	if (divName == "moSearchMax"){return "Enter a percentage between 0 and 500 used to calculate the maximum value acceptable for a match.";}
	if (divName == "moSearchMin"){return "Enter a percentage between 0 and 100 used to calculate the minimum value acceptable for a match.";}
	if (divName == "moSearchObjectTB"){return "Select this option to use a Text Box for a Search argument for this parameter.";}
	if (divName == "moSearchObjectSB"){return "Select this option to use a Select Box for Search argument for this parameter.";}
	if (divName == "moSearchWeight"){return "Enter a relative weight for this search field. Relative weights should add up to 100.";}
	if (divName == "moSigDigits"){return "Determines the minimum number of significant digits to enter or display (1 to 9).";}
	if (divName == "moStartOver"){return "Click Start Over to abandon this rule and start from the beginning.";}
	if (divName == "moTilde"){return "When enabled, a tilde '~' can be used to specify a range of values, e.g., 208~240 volts.";}
	if (divName == "moUnits"){return "The selected units will appear after the displayed parametric value.";}
	if (divName == "moUnitsBase"){return "Specifies the Base for the numbering system used for this field.";}
	if (divName == "x"){return "x";}
	return "";
}

function getUnitsIndex(work) {
	// uI is a global
	var myForm = document.form1;
	if (work.length != 0) {
		for (var i = 0; i < uI; i++ ) {
			if (unitsArray[i] [0] == work) {
				return i;
			}
		}
	}
	return -1;
}

function loadUnits() {
	// uI is a global that contains the size of the Units Declaration Array
	var myForm = document.form1;
	var o = myForm.units;
        var sel = myForm.txtUnits.value;
	for (var i = 0; i < uI; i++) {
		var oOption = document.createElement("option");
                var temp =  unitsArray [i] [0];
		oOption.appendChild(document.createTextNode( temp ));
		oOption.setAttribute("value", temp );
                if (temp == sel) {
                    oOption.setAttribute("selected", true );
                }
		o.appendChild(oOption);
	}
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

	var decPointRight = "0";

	if (multiplier == "D") { decPointRight = 1; }
	if (multiplier == "H") { decPointRight = 2; }
	if (multiplier == "K") { decPointRight = 3; }
	if (multiplier == "M") { decPointRight = 6; }
	if (multiplier == "G") { decPointRight = 9; }
	if (multiplier == "T") { decPointRight = 12; }
	if (multiplier == "P") { decPointRight = 15; }
	if (multiplier == "E") { decPointRight = 18; }

	if (decPointRight > 0 )
	{
		fraction = fraction + "00000000000000000000000000000";
		whole = whole + fraction.slice(0,decPointRight);
		fraction = fraction.slice(decPointRight);
	}

	var decPointLeft = "0";

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

	while (fraction.charAt(fraction.length - 1) == "0" ) {
		fraction=fraction.slice(0,fraction.length -1);
	}

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
	if (decimalOK == false  && fraction.length > 0) {
		alert ("Decimal Fractions are not allowed");
		return "";
	}
	if (fraction.length != 0 ) {
		if (!decimalOK) {
			alert ("Decimal Fractions are not allowed");
			return "";
		}
		result = result + "." + fraction; 
	}
	var j = parseFloat(result);
	return result;
}

function reformat(work) {
	var myForm = document.form1;
	if (work.length > 0) {
		// Do we have any delimiters defined?
		var delim = "";
		if (myForm.parmDelimiter[1].checked) { delim = ";"; }
		if (myForm.parmDelimiter[2].checked) { delim = "/"; }
		if (delim.length > 0) {
			var resultSet = delim;
			var hold = "";
			var items = work.split(delim);
			for (var i = 0; i < items.length; i++) {
				hold = reformat2(items[i]) + delim;
				// ignore duplicates
				if (myForm.allowDuplicates[0].checked == true
					|| resultSet.indexOf(delim + hold) == -1) {
                                            resultSet += hold;
                                }
			}
			// delete beginning and trailing delimiters
			resultSet = resultSet.slice(1, resultSet.length - 1);
		} else {   // do this if there were no delimiters found
			resultSet = reformat2(work);
		}
		return resultSet;
	}
	return work;
}

function reformat2(work) {
	var myForm = document.form1;
	if (!myForm.allowTilde[0].checked) {
		work = reformat3(work);
		return work;
	} else {
		if (work.indexOf("~") == -1) {
			work = reformat3(work);
			return work;
		}
		var parts = work.split("~");
		var part1 = reformat3(parts[0]);
		var part2 = reformat3(parts[1]);
		work = part1 + "~" + part2;
		return work;
	}
}

function reformat3(work) {
	var myForm = document.form1;
	var s = parseInt(myForm.txtUnitsShift.value);
	work = shiftDecimal(work, -s);
	work = formatNumber(work, myForm.txtDEMultipliers.value, myForm.minDecimalDigits.value, true);
	return work;
}

function setDefaults() {
	var myForm = document.form1;
	checkDEObject();
	checkQObject();
	loadUnits();
	setUnits();
	checkAllowDuplicates();
	setDEMultipliers();
	setDisplayMultipliers();
	setFractionMultipliers();
	myForm.deObject[0].focus();
}

function setDEMultipliers() {
	var myForm = document.form1;
	var work = myForm.txtDEMultipliers.value;
	if (work.length == 0) { work = "U"; }
	myForm.txtDEMultipliers.value = work;
	for (i = 0; i < 15; i++) {
		if (work.indexOf(myForm.deMultipliers[i].value) != -1) {
			myForm.deMultipliers[i].checked = true;
		}
	}
}

function setDisplayMultipliers() {
	var myForm = document.form1;
	var work = myForm.txtDisplayMultipliers.value;
	if (work.length == 0) { work = "U"; }
	myForm.txtDisplayMultipliers.value = work;
	for (i = 0; i < 15; i++) {
		if (work.indexOf(myForm.displayMultipliers[i].value) != -1) {
			myForm.displayMultipliers[i].checked = true;
		}
	}
}

function setFractionMultipliers() {
	myForm = document.form1;
	var sel = getSelectedRadioValue(myForm.allowFractions);
	if (sel == "N") {
		myForm.minDecimalDigits.disabled = true;
		disableFractionDisplayMultipliers();
		disableFractionDEMultipliers();
	}
}

function setUnits() {
	// uI is a global
	var myForm = document.form1;
	var work = myForm.txtUnits.value;
	if (work.length != 0) {
		var i = getUnitsIndex(work);
		if (i > -1) {
			myForm.txtUnitsShift.value = unitsArray[i][1];
			if ( myForm.txtUnitsShift.value != "0" ) {
				disableDisplayMultipliers();
				disableDEMultipliers();
			}
		}
	}
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
	if (decPosition != -1)
	{
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
	}
	else {

	// If places is > 0, shift decimal to the right

		while (fraction.length < places) {
			fraction += "0";
		}
		whole = whole + fraction.slice(0, places);
		fraction = fraction.slice(places);
	}

	// delete any leading zeroes from the integer

	while (whole.indexOf("0") == 0)
	{
		whole = whole.slice(1);
	}
	
	// Make sure we have 1 zero if null integer

	if (whole.length == 0) { whole = "0"; }

	// reduce trailing zeroes on fraction to none

	while (fraction.charAt(fraction.length - 1) == "0" )
	{
		fraction=fraction.slice(0,fraction.length -1);
	}

	// Reassemble the number

	number = whole;

	if (fraction.length > 0) { number = number + "." + fraction; }

	// Restore negative sign if necessary

	if (minusSign) {number = "-" + number; }
	return number;
}

function uncheckDEMultipliers() {
	var myForm = document.form1;
	var work = "";
	for (i = 0; i < 8; i++) {
		myForm.deMultipliers[i].checked = false;
		myForm.deMultipliers[i].disabled = true;
	}
	for (i = 9; i < 15; i++) {
		myForm.deMultipliers[i].checked = false;
		myForm.deMultipliers[i].disabled = true;
	}
	calcDEMultipliers();
	eraseMinMaxDefault();
}

function uncheckDEMultipliers2() {
	var myForm = document.form1;
	var work = "";
	for (i = 0; i < 8; i++) {
		myForm.deMultipliers[i].checked = false;
		myForm.deMultipliers[i].disabled = true;
	}
	calcDEMultipliers();
	eraseMinMaxDefault();
}


//	*********************************************************
//	*		Form Validation Pre-Submit		*
//	*********************************************************

function My_Validator() {
	var work;
	var work2;
	var work3;
	var bSwitch1 = false;
	var bSwitch2 = false;
	var myForm = document.form1;

	// Check for Data Entry Object Text Box Size or Select Box Name
        
        var sel = getSelectedRadioValue(myForm.deObject);
	if (sel == "T") {
            work = myForm.deTextBoxSize.value;
            if (work.length == 0) {
                alert ("Please enter a Data Entry Select Box size.");
                myForm.deTextBoxSize.focus();
                return false;
            }
        }    
	if (sel == "S") {
            if (myForm.deSelectBoxName.selectedIndex == 0) {
                alert ("Please choose a Query Select Box name from the list.");
                myForm.deSelectBoxName.focus();
                return false;
            }
	}
        
        if (sel == "") {
            alert ("Please choose a Data Entry field Object and enter a Text Box field size or choose a Select Box Name.");
            myForm.deObject[0].focus();
            return false;
        }

        // Check for Search Object Text Box Size or Select Box Name
        
        var sel = getSelectedRadioValue(myForm.qObject);
	if (sel == "T") {
            work = myForm.qTextBoxSize.value;
            if (work.length == 0) {
                alert ("Please enter a Search Select Box size.");
                myForm.qTextBoxSize.focus();
                return false;
            }
        }    
	if (sel == "S") {
            if (myForm.qSelectBoxName.selectedIndex == 0) {
                alert ("Please choose a Search Select Box name from the list.");
                myForm.deSelectBoxName.focus();
                return false;
            }
	}
        
        if (sel == "") {
            alert ("Please choose a Search Object and enter a Text Box field size or choose a Select Box Name.");
            myForm.qObject[0].focus();
            return false;
        }
        
	// Check for an entry in Units Select Box

	if (myForm.units.options[0].selected == true) {
		alert ("Please select the units for this field.");
		myForm.units.focus();
		return false;
	}

	// Check Base (Currently defaults to Decimal and Binary/Octal/Hex are disabled)


	// Check data entry multipliers ensuring at least one option set

	if (myForm.txtDEMultipliers.value == "") {
		alert ("Select at least one option in the Data Entry Multipliers Box.");
		myForm.deMultipliers[8].focus();
		return false;
	}

	// Check display multipliers ensuring at least one option set

	if (myForm.txtDisplayMultipliers.value == "") {
		alert ("Select at least one option in the Display Multipliers Box.");
		myForm.displayMultipliers[8].focus();
		return false;
	}

	// Check Decimal digits vs DE / Display Multipliers

	work = myForm.minDecimalDigits.value;
	if (work == "") { 
		work = "0";
		myForm.minDecimalDigits.value = work;
	}
	if (myForm.txtDisplayMultipliers.value != "U") {
		if (parseInt(work) > 0) {
			alert ("Decimal Places must be zero if you have selected any display Multipliers other than None.");
			myForm.minDecimalDigits.focus();
			return false;
		}
	}

	// Check Min Values are present legal

	work = myForm.minValueRaw.value;
	if (work.length == 0) {
		alert ("Please enter a Minimum value for numeric range checking.");
		myForm.minValueCooked.focus();
		return false;
	}

	bSwitch1 = (myForm.allowSign[0].checked == true); // I am true if negs are allowed

	if (!bSwitch1) {
		if (work.charAt(0) == "-") {
			alert ("Rule Conflict! Negative values are NOT allowed.");
			myForm.minValueCooked.focus();
			return false;
		}
	}

	bSwitch2 = (myForm.allowZero[0].checked == true); // I am true if zero is allowed

	if (!bSwitch2) {
		if (parseFloat(work) == 0.0 ) {
			alert ("Rule Conflict! Zero values are NOT allowed.");
			myForm.minValueCooked.focus();
			return false;
		}
	}

	// Check Max Values are present and legal

	work2 = myForm.maxValueRaw.value;
	if (work2.length == 0) {
		alert ("Please enter a Maximum value for numeric range checking.");
		myForm.maxValueCooked.focus();
		return false;
	}

	if (!bSwitch1) {
		if (work2.charAt(0) == "-") {
			alert ("Rule Conflict! Negative values are NOT allowed.");
			myForm.maxValueCooked.focus();
			return false;
		}
	}

	if (!bSwitch2) {
		if (parseFloat(work2) == 0.0 ) {
			alert ("Rule Conflict! Zero values are NOT allowed.");
			myForm.maxValueCooked.focus();
			return false;
		}
	}

	// Finally check to make sure min is not > max

	if (parseFloat(work) > parseFloat(work2)) {
		alert ("The Minimum Range value cannot be greater than the Maximum Range value.");
		myForm.minValueCooked.focus();
		return false;
	}		

	// Check Data Entry default is valid and between Min and Max Values if present
	
	checkDefaultValue();
	if (globalDefaultValid == false)
	{
		myForm.defaultValueCooked.focus();
		return false;
	}	

	// Check searchMin

	work = myForm.searchMin.value;
	if (work.length == 0) {
		alert("Please enter a Minimum percent for this Rule.");
		myForm.searchMin.focus();
		return false;
	}

	// Check searchMax

	work = myForm.searchMax.value;
	if (work.length == 0) {
		alert("Please enter a Maximum percent for this Rule.");
		myForm.searchMax.focus();
		return false;
	}


	// Check search Relative Weight

	work = myForm.searchWeight.value;
	if (work.length == 0) {
		alert("Please enter a Search Weight for this Rule.");
		myForm.searchWeight.focus();
		return false;
	}

	// All Validation tests are complete
        
        // enable disabled controls so their values will be sent to the server
        
        myForm.deTextBoxSize.disabled = false;
        myForm.deSelectBoxName.disabled = false;
        myForm.qTextBoxSize.disabled = false;
        myForm.qSelectBoxName.disabled = false;
        myForm.allowDuplicates[0].disabled = false;
        myForm.allowDuplicates[1].disabled = false;
        myForm.minDecimalDigits.disabled = false;
        if (myForm.deTextBoxSize.value == "") {
            myForm.deTextBoxSize.value = "0"
        }
        if (myForm.qTextBoxSize.value == "") {
            myForm.qTextBoxSize.value = "0"
        }
        if (myForm.minDecimalDigits.value == "") {
            myForm.minDecimalDigits.value = "0"
        }
        
	myForm.validation.value = "OK";
	return true;
}

//-->
</script>
</head>

<body onload="setDefaults()">
	
<script language="JavaScript" type="text/javascript">
<!--

	var junk = "";
        var inputObject = "";
        var checkMinAndMax = false;
	var globalDefaultValid = false;
        var prevDESelectBoxName = "<jsp:getProperty name="sRuleSet" property="deSelectBoxName" />";
        var prevQSelectBoxName = "<jsp:getProperty name="sRuleSet" property="qselectBoxName" />";
        var sbName = "";
        var unitsArray = new Array();
        var sbNamesArray = new Array();
	var uI = 0;
	var sbnI = 0;
	unitsArray [uI++] = new Array("Select One","0");
        unitsArray [uI++] = new Array("None","0");
        <c:forEach var="item" items="${selectUnits}">
            unitsArray [uI++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${selectBoxNames}">
            sbNamesArray [sbnI++] = "${item}";
        </c:forEach>

//-->	
</script>

<div style="position: absolute; left: 10px; top: 10px; width: 690px; ">
    
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form method="post" action="gpsruf5.do" onsubmit="return My_Validator()" name="form1">
<p>
    <input type="hidden" name="validation" value="Error" />
    <input type="hidden" name="txtDisplayMultipliers"  
        value="<jsp:getProperty name="sRuleSet" property="displayMultipliers" />"
    />
    <input type="hidden" name="txtDEMultipliers" 
        value="<jsp:getProperty name="sRuleSet" property="deMultipliers" />"
    /> 
    <input type="hidden" name="minValueRaw" 
        value="<jsp:getProperty name="sRuleSet" property="minValueRaw" />"
    /> <!-- raw format -->
    <input type="hidden" name="maxValueRaw" 
        value="<jsp:getProperty name="sRuleSet" property="maxValueRaw" />"
    />  <!-- raw format -->
    <input type="hidden" name="defaultValueRaw" 
        value="<jsp:getProperty name="sRuleSet" property="defaultValueRaw" />"
    />  <!-- raw format -->
    <input type="hidden" name="txtDESelectBoxName" 
        value="<jsp:getProperty name="sRuleSet" property="deSelectBoxName" />"
    />  <!-- Used for an update op -->
    <input type="hidden" name="txtQSelectBoxName"
        value="<jsp:getProperty name="sRuleSet" property="qselectBoxName" />"
    />  <!-- Used for an update op -->
    <input type="hidden" name="txtUnits"  
        value="<jsp:getProperty name="sRuleSet" property="displayUnits" />"
    /> <!-- Used to set Units for an update op -->
    <input type="hidden" name="txtUnitsShift"
        value="<jsp:getProperty name="sRuleSet" property="decShift" />"
    />   <!-- set when Units are selected -->
    <input type="hidden" name="txtSearchOrder"
        value="<jsp:getProperty name="sRuleSet" property="searchOrder" />"
    />
</p>

<table border="0" width="100%">

<!-- Logo and Heading  -->

  <tr>
    <td align="center" colspan="2">
	<h2>
        Parametric Search Rules Maintenance - Copy Rule Set - Part 3N
	</h2>
    </td>
  </tr>
  <tr>
    <td rowspan="3" align="center" width="20%">
      <img src="gl_25.gif" alt="Galco logo" />
      <br />

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


<div class="masthead" onmouseover="showTip(event, 'header')" onmouseout="hideTip()" >

  <table border="1" width="98%"  align="center" >
  <tr><td><table border="0" width="100%">

<!-- Product Line -->

    <tr>
        <td colspan="4" align="center">
            <span class="headerLabel">
                Product Line:&nbsp;&nbsp;
            </span>
            <span class="headerData">
                <jsp:getProperty name="sRuleSet" property="productLineName" />
            </span>
        </td>
    </tr>                
            
<!--  Family Description  -->

    <tr>
      <td align="right" width="25%"><span class="headerLabel">
        Family:&nbsp;
      </span></td>
      <td align="left" width="25%"><span class="headerData">
            <jsp:getProperty name="sRuleSet" property="familyName" />
      </span></td>

<!--  Subfamily Description  -->

      <td align="right" width="25%"><span class="headerLabel">
        Subfamily:&nbsp;
      </span></td>
        <td align="left" width="25%"><span class="headerData">
            <jsp:getProperty name="sRuleSet" property="subfamilyName" />
        </span></td>
      </tr>

<!--  Scope  -->

    <tr>
      <td align="right" ><span class="headerLabel">
        Scope:&nbsp;
      </span></td>
      <td align="left" ><span class="headerData">
<script language="JavaScript" type="text/javascript">
<!--
                    junk = "<jsp:getProperty name="sRuleSet" property="ruleScope" />";
                    if (junk == "G") {
                        document.write("Global");
                    }
                    if (junk == "L") {
                        document.write("Local");
                    }
//-->
</script>
      </span></td>

<!--  Sequence Number  -->

        <td align="right"><span class="headerLabel">
          Field No.:&nbsp;
        </span></td>
        <td align="left"><span class="headerData">
            <jsp:getProperty name="sRuleSet" property="seqNum" />
        </span></td>
      </tr>

<!--  Parm Name  -->

      <tr>
        <td align="right"><span class="headerLabel">
          Field Name:&nbsp;
        </span></td>
        <td align="left"><span class="headerData">
            <jsp:getProperty name="sRuleSet" property="parmName" />
        </span></td>

<!--  Data Type -->

        <td align="right"><span class="headerLabel">
          Data Type:&nbsp;
        </span></td>
        <td align="left"><span class="headerData">
<script language="JavaScript" type="text/javascript">
<!--
                    junk = "<jsp:getProperty name="sRuleSet" property="dataType" />";
                    if (junk == "N") {
                        document.write("N - Numeric");
                    }
                    if (junk == "S") {
                        document.write("S - String");
                    }
                    if (junk == "L") {
                        document.write("L - Logical");
                    }
                    if (junk == "D") {
                        document.write("D - Date");
                    }
 //-->
</script>        
        </span></td>
      </tr>

  </table></td></tr>
 </table>
</div>
</td></tr>
</table>

<table width="100%" border="0">

<!--  Data Entry Object  -->

    <tr>
        <td align="right">
            <span class="requiredLabel">Data Entry&nbsp;&nbsp;<br />Object:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
            <table border="1" width="100%"><tr><td>
                <table border="0">
                    <tr>
                        <td width="25%" align="left">
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="deObject" value="T"';
        inputObject += ' onclick="checkDEObject()" onmouseover="showTip(event, ';
        inputObject += "'moDEObjectTB'";
        inputObject += ',100,25)" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="deObject" />';
        if (junk == "" || junk == "T") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>                            
                            
                        &nbsp;Text Box
                        </td>
                        <td width="20%" align="right">
                            Size:&nbsp;
                        </td>
                        <td align="left">

<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="text" size="3" maxlength="2" name="deTextBoxSize" disabled="disabled"';
        inputObject += ' onblur="checkDETextBoxSize()" onmouseover="showTip(event, ';
        inputObject += "'moDETextBoxSize'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="deObject" />';
        if (junk == "T") {
            inputObject += ' value="<jsp:getProperty name="sRuleSet" property="deTextBoxSize" />"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

                        </td>
                    </tr>
                    <tr>
                        <td>
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="deObject" value="S"';
        inputObject += ' onclick="checkDEObject()" onmouseover="showTip(event, ';
        inputObject += "'moDEObjectSB'";
        inputObject += ',100,25)" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="deObject" />';
        if (junk == "S") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

                            &nbsp;Select Box
                        </td>
                        <td align="right">
                            Name:&nbsp;
                        </td>
                        <td>
                            <select name="deSelectBoxName" size="1"
                                disabled="disabled"
                                onmouseover="showTip(event,'moDESelectBoxName')" 
                                onmouseout="hideTip()">
                                <option selected="selected" value="">Choose a Select Box</option>
                                <script language="JavaScript" type="text/javascript">
                                <!--
                                    for (var i = 0; i < sbnI; i++){
                                        sbName = sbNamesArray[i];
                                        document.write("<option ");
                                        if (sbName == prevDESelectBoxName) {
                                            document.write("selected=\"selected\" ");
                                        }
                                        document.write(" value=\"" + sbNamesArray[i] + "\">" + sbNamesArray[i]+"</option>");
                                    }
                                //-->
                                </script>
                            </select>
                        </td>
                    </tr>
                </table>
            </td></tr></table>
        </span></td>
      </tr>
      
<!--  Search Object  -->

    <tr>
        <td align="right">
            <span class="requiredLabel">Search&nbsp;&nbsp;<br />Object:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
            <table border="1" width="100%"><tr><td>
                <table border="0">
                    <tr>
                        <td width="25%" align="left">
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="qObject" value="T"';
        inputObject += ' onclick="checkQObject()" onmouseover="showTip(event, ';
        inputObject += "'moQObjectTB'";
        inputObject += ',100,25)" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="qobject" />';
        if (junk == "" || junk == "T") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

                            &nbsp;Text Box
                        </td>
                        <td width="20%" align="right">
                            Size:&nbsp;
                        </td>
                        <td align="left">
                            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="text" size="3" maxlength="2" name="qTextBoxSize" disabled="disabled"';
        inputObject += ' onblur="checkQTextBoxSize()" onmouseover="showTip(event, ';
        inputObject += "'moQTextBoxSize'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="qobject" />';
        if (junk == "T") {
            inputObject += ' value="<jsp:getProperty name="sRuleSet" property="qtextBoxSize" />"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

                        </td>
                    </tr>
                    <tr>
                        <td>
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="qObject" value="S"';
        inputObject += ' onclick="checkQObject()" onmouseover="showTip(event, ';
        inputObject += "'moQObjectSB'";
        inputObject += ',100,25)" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="qobject" />';
        if (junk == "S") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

                            &nbsp;Select Box
                        </td>
                        <td align="right">
                            Name:&nbsp;
                        </td>
                        <td>
                            <select name="qSelectBoxName" size="1"
                                disabled="disabled"
                                onmouseover="showTip(event,'moQSelectBoxName')" 
                                onmouseout="hideTip()">
                                <option selected="selected" value="">Choose a Select Box</option>
                                <script language="JavaScript" type="text/javascript">
                                <!--
                                    for (var i = 0; i < sbnI; i++){
                                        sbName = sbNamesArray[i];
                                        document.write("<option ");
                                        if (sbName == prevQSelectBoxName) {
                                            document.write("selected=\"selected\" ");
                                        }
                                        document.write(" value=\"" + sbNamesArray[i] + "\">" + sbNamesArray[i]+"</option>");
                                    }
                                //-->
                                </script>
                            </select>
                        </td>
                    </tr>
                </table>
            </td></tr></table>
        </span></td>
      </tr>

<!--  Units  --> 

      <tr>
        <td align="right">
          <span class="requiredLabel">Units:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <select size="1" name="units"
		onchange="checkUnits()"
        	onmouseover="showTip(event, 'moUnits', 200, 50)" 
        	onmouseout="hideTip()">
          </select>

        </span></td>
      </tr>

<!--  Delimiter  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Allow Delimiter:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="parmDelimiter" value=""';
        inputObject += ' onchange="checkAllowDuplicates()" onmouseover="showTip(event, ';
        inputObject += "'moParmDelimiter'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="parmDelimiter" />';
        if (junk == "") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          None&nbsp;&nbsp;&nbsp;&nbsp;

<!-- No comma delimiter allowed for Numeric data types -->

<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="parmDelimiter" value=";"';
        inputObject += ' onchange="checkAllowDuplicates()" onmouseover="showTip(event, ';
        inputObject += "'moParmDelimiter'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="parmDelimiter" />';
        if (junk == ";") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            Semicolon&nbsp;&nbsp;&nbsp;&nbsp;
   
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="parmDelimiter" value="/"';
        inputObject += ' onchange="checkAllowDuplicates()" onmouseover="showTip(event, ';
        inputObject += "'moParmDelimiter'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="parmDelimiter" />';
        if (junk == "/") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            Slash
        </span></td>
      </tr>

<!--  Allow Duplicates  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Allow Duplicates:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="allowDuplicates" value="Y"';
        inputObject += ' onchange="checkAllowDuplicates()" onmouseover="showTip(event, ';
        inputObject += "'moAllowDuplicates'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="allowDuplicates" />';
        if (junk == "true") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          Yes&nbsp;&nbsp;&nbsp;&nbsp;
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="allowDuplicates" value="N"';
        inputObject += ' onchange="checkAllowDuplicates()" onmouseover="showTip(event, ';
        inputObject += "'moAllowDuplicates'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="allowDuplicates" />';
        if (junk == "" || junk == "false") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            No
		         
        </span></td>
      </tr>

<!--  Allow Zero  -->

      <tr>
        <td align="right">
          <span class="label">Allow Zero:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
       
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="allowZero" value="Y"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moAllowZero'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="allowZero" />';
        if (junk == "true") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          Yes&nbsp;&nbsp;&nbsp;&nbsp;
       
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="allowZero" value="N"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moAllowZero'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="allowZero" />';
        if (junk == "" || junk == "false") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            No
        </span></td>
      </tr>

<!--  Allow Sign  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Allow Sign:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
                   
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="allowSign" value="Y"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moAllowSign'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="allowSign" />';
        if (junk == "true") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          Yes&nbsp;&nbsp;&nbsp;&nbsp;
                   
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="allowSign" value="N"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moAllowSign'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="allowSign" />';
        if (junk == "" || junk == "false") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            No
        </span></td>
      </tr>

<!--  Allow Tilde  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Allow Tilde:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
           
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="allowTilde" value="Y"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moTilde'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="allowTilde" />';
        if (junk == "true") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          Yes&nbsp;&nbsp;&nbsp;&nbsp;
           
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="allowTilde" value="N"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moTilde'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="allowTilde" />';
        if (junk == "" || junk == "false") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            No
        </span></td>
      </tr>

<!--  Allow Fractions  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Allow Fractions:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
                       
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="allowFractions" value="Y"';
        inputObject += ' onchange="checkAllowFractions()" onmouseover="showTip(event, ';
        inputObject += "'moAllowFractions'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="allowFractions" />';
        if (junk == "" || junk == "true") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          Yes&nbsp;&nbsp;&nbsp;&nbsp;
                       
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="allowFractions" value="N"';
        inputObject += ' onchange="checkAllowFractions()" onmouseover="showTip(event, ';
        inputObject += "'moAllowFractions'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="allowFractions" />';
        if (junk == "false") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            No
		         
        </span></td>
      </tr>
													
<!-- Decimal Places  -->

      <tr>
        <td align="right">
          <span class="label">Decimal Places:&nbsp;</span>
        </td>
        <td><span class="dataField">
                              
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="text" size="2" maxlength="1" name="minDecimalDigits" ';
        inputObject += ' onblur="checkMinDecimalDigits()" onmouseover="showTip(event, ';
        inputObject += "'moMinDecimalDigits'";
        inputObject += ', 200, 50)" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="minDecimalDigits" />';
        if (junk == "") {
            junk = "0";
        }
        inputObject += ' value="' + junk + '"';
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

		&nbsp;(applicable when Allow Fractions is Yes.)

        </span></td>
      </tr>

<!--  Input Multipliers  -->

      <tr>
        <td align="right" width="20%">
          <span class="requiredLabel">
            Data Entry Multipliers:&nbsp;
          </span>
        </td>
        <td align="left">
          <div onmouseover="showTip(event,'deMultipliers')" onmouseout="hideTip()">
            <table width="100%" border="1">
              <tr>
                <td>
                  <table width="100%" border="0">
                    <tr>
                      <td width="25%">
                        <span class="dataField2">

<!--                      <input type="checkbox" name="deMultipliers" value="y"	
		            onclick="calcDEMultipliers()"
		            /> y yocto 10^-24  -->

<!--                      <input type="checkbox" name="deMultipliers" value="z"		
		            onclick="calcDEMultipliers()"
		            /> z zepto 10^-21  -->

                          <input type="checkbox" name="deMultipliers" value="a"		
		            onclick="calcDEMultipliers()"
		            /> a atto  10^-18
                        </span>
                      </td>

                      <td width="25%">
                        <span class="dataField2">
                          <input type="checkbox" name="deMultipliers" value="f"	
		            onclick="calcDEMultipliers()"
		            /> f femto 10^-15
                        </span>
                      </td>

                      <td width="25%">
                        <span class="dataField2">
                          <input type="checkbox" name="deMultipliers" value="p"		
		            onclick="calcDEMultipliers()"
		            /> p pico  10^-12
                        </span>
                      </td>

                      <td width="25%">
                        <span class="dataField2">
                          <input type="checkbox" name="deMultipliers" value="n"		
		            onclick="calcDEMultipliers()"
		            /> n nano  10^-9
                        </span>
                      </td>
                    </tr>

                    <tr>
                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="deMultipliers" value="u"		
		          onclick="calcDEMultipliers()"
		          /> u micro 10^-6
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="deMultipliers" value="m"		
		          onclick="calcDEMultipliers()"
		          /> m milli 10^-3
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="deMultipliers" value="c"		
		          onclick="calcDEMultipliers()"
		          /> c centi 10^-2
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="deMultipliers" value="d"		
		          onclick="calcDEMultipliers()"
		          /> d deci  10^-1
                      </span>
                    </td>
                  </tr>

                  <tr>
                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="deMultipliers" value="U"		
		          onclick="calcDEMultipliers()"
		          />   none  10^0
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">

<!--                    <input type="checkbox" name="deMultipliers" value="D"		
		          onclick="calcDEMultipliers()"
	                  />   Deka  10^1  -->

<!--                    <input type="checkbox" name="deMultipliers" value="H"		
		          onclick="calcDEMultipliers()"
		          /> h hekto 10^2  -->

                        <input type="checkbox" name="deMultipliers" value="K"		
		          onclick="calcDEMultipliers()"
		          /> K Kilo  10^3  2^10
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="deMultipliers" value="M"		
		          onclick="calcDEMultipliers()"
		          /> M Mega  10^6  2^20
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="deMultipliers" value="G"		
		          onclick="calcDEMultipliers()"
		        /> G Giga  10^9  2^30
                      </span>
                    </td>
                  </tr>

                  <tr>
                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="deMultipliers" value="T"		
		          onclick="calcDEMultipliers()"
		          /> T Tera  10^12 2^40
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="deMultipliers" value="P"	
		          onclick="calcDEMultipliers()"
		          /> P Peta  10^15 2^50
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="deMultipliers" value="E"		
		          onclick="calcDEMultipliers()"
		          /> E Exa   10^18 2^60
                      </span> 
                    </td>

                    <td width="25%">
                      <span class="dataField2">

<!--                    <input type="checkbox" name="deMultipliers" value="Z"		
		          onclick="calcDEMultipliers()"
		          /> Z Zetta 10^21 2^70 -->
 
<!--                    <input type="checkbox" name="deMultipliers" value="Y"		
		          onclick="calcDEMultipliers()"
		          /> Y Yotta 10^24 2^80 -->

                      </span>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </div>
      </td>
    </tr>

<!--  Display Multipliers  -->

      <tr>
        <td align="right" width="20%">
          <span class="requiredLabel">
            Display Multipliers:&nbsp;
          </span>
        </td>
        <td align="left">
          <div onmouseover="showTip(event,'dMultipliers')" onmouseout="hideTip()">
            <table width="100%" border="1">
              <tr>
                <td>
                  <table width="100%" border="0">
                    <tr>
                      <td width="25%">
                        <span class="dataField2">

<!--                      <input type="checkbox" name="displayMultipliers" value="y"	
		            onclick="calcDisplayMultipliers()"
		            /> y yocto 10^-24  -->

<!--                      <input type="checkbox" name="displayMultipliers" value="z"		
		            onclick="calcDisplayMultipliers()"
		            /> z zepto 10^-21  -->

                          <input type="checkbox" name="displayMultipliers" value="a"		
		            onclick="calcDisplayMultipliers()"
		            /> a atto  10^-18
                        </span>
                      </td>

                      <td width="25%">
                        <span class="dataField2">
                          <input type="checkbox" name="displayMultipliers" value="f"	
		            onclick="calcDisplayMultipliers()"
		            /> f femto 10^-15
                        </span>
                      </td>

                      <td width="25%">
                        <span class="dataField2">
                          <input type="checkbox" name="displayMultipliers" value="p"		
		            onclick="calcDisplayMultipliers()"
		            /> p pico  10^-12
                        </span>
                      </td>

                      <td width="25%">
                        <span class="dataField2">
                          <input type="checkbox" name="displayMultipliers" value="n"		
		            onclick="calcDisplayMultipliers()"
		            /> n nano  10^-9
                        </span>
                      </td>
                    </tr>

                    <tr>
                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="displayMultipliers" value="u"		
		          onclick="calcDisplayMultipliers()"
		          /> u micro 10^-6
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="displayMultipliers" value="m"		
		          onclick="calcDisplayMultipliers()"
		          /> m milli 10^-3
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="displayMultipliers" value="c"		
		          onclick="calcDisplayMultipliers()"
		          /> c centi 10^-2
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="displayMultipliers" value="d"		
		          onclick="calcDisplayMultipliers()"
		          /> d deci  10^-1
                      </span>
                    </td>
                  </tr>

                  <tr>
                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="displayMultipliers" value="U"		
		          onclick="calcDisplayMultipliers()"
		          />   none  10^0
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">

<!--                    <input type="checkbox" name="displayMultipliers" value="D"		
		          onclick="calcDisplayMultipliers()"
	                  />   Deka  10^1  -->

<!--                    <input type="checkbox" name="displayMultipliers" value="H"		
		          onclick="calcDisplayMultipliers()"
		          /> h hekto 10^2  -->

                        <input type="checkbox" name="displayMultipliers" value="K"		
		          onclick="calcDisplayMultipliers()"
		          /> K Kilo  10^3  2^10
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="displayMultipliers" value="M"		
		          onclick="calcDisplayMultipliers()"
		          /> M Mega  10^6  2^20
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="displayMultipliers" value="G"		
		          onclick="calcDisplayMultipliers()"
		        /> G Giga  10^9  2^30
                      </span>
                    </td>
                  </tr>

                  <tr>
                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="displayMultipliers" value="T"		
		          onclick="calcDisplayMultipliers()"
		          /> T Tera  10^12 2^40
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="displayMultipliers" value="P"	
		          onclick="calcDisplayMultipliers()"
		          /> P Peta  10^15 2^50
                      </span>
                    </td>

                    <td width="25%">
                      <span class="dataField2">
                        <input type="checkbox" name="displayMultipliers" value="E"		
		          onclick="calcDisplayMultipliers()"
		          /> E Exa   10^18 2^60
                      </span> 
                    </td>

                    <td width="25%">
                      <span class="dataField2">

<!--                    <input type="checkbox" name="displayMultipliers" value="Z"		
		          onclick="calcDisplayMultipliers()"
		          /> Z Zetta 10^21 2^70 -->
 
<!--                    <input type="checkbox" name="displayMultipliers" value="Y"		
		          onclick="calcDisplayMultipliers()"
		          /> Y Yotta 10^24 2^80 -->

                      </span>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </div>
      </td>
    </tr>


<!--  Minimum & Maximum  Value  -->

    <tr>
      <td align="right">
        <span class="requiredLabel">
          Limits:&nbsp;
        </span>
      </td>
      <td>
        <span class="dataField">
                              
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="text" size="24" maxlength="24" name="minValueCooked" ';
        inputObject += ' onblur="checkMinValue()" onmouseover="showTip(event, ';
        inputObject += "'moMinValue'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="minValueCooked" />';
        inputObject += ' value="' + junk + '"';
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

          Min
                                        
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="text" size="24" maxlength="24" name="maxValueCooked" ';
        inputObject += ' onblur="checkMaxValue()" onmouseover="showTip(event, ';
        inputObject += "'moMaxValue'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="maxValueCooked" />';
        inputObject += ' value="' + junk + '"';
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

          Max
        
        </span>
      </td>
    </tr>


<!--  Default Value  -->

    <tr>
      <td align="right">
        <span class="label">
          D.E. Default:&nbsp;
        </span>
      </td>
      <td>
        <span class="dataField">
                                                    
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="text" size="24" maxlength="24" name="defaultValueCooked" ';
        inputObject += ' onblur="checkDefaultValue()" onmouseover="showTip(event, ';
        inputObject += "'moDefaultValue'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="defaultValueCooked" />';
        inputObject += ' value="' + junk + '"';
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

        </span>
      </td>
    </tr>


<!--  Search Percent Match Value  -->

    <tr>
      <td align="right">
        <span class="requiredLabel">
          Search Percent:&nbsp;
        </span>
      </td>
      <td>
        <span class="dataField">
                                                               
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="text" size="4" maxlength="3" name="searchMin" ';
        inputObject += ' onblur="checkSearchMin()" onmouseover="showTip(event, ';
        inputObject += "'moSearchMin'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="searchMin" />';
        inputObject += ' value="' + junk + '"';
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

          - % Low
                                                               
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="text" size="4" maxlength="3" name="searchMax" ';
        inputObject += ' onblur="checkSearchMax()" onmouseover="showTip(event, ';
        inputObject += "'moSearchMax'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="searchMax" />';
        inputObject += ' value="' + junk + '"';
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

          + % High

<!--  Relative Weight  -->

          &nbsp;&nbsp;&nbsp;
                                                                        
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="text" size="3" maxlength="3" name="searchWeight" ';
        inputObject += ' onblur="checkSearchWeight()" onmouseover="showTip(event, ';
        inputObject += "'moSearchWeight'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="searchWeight" />';
        inputObject += ' value="' + junk + '"';
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

          Relative Wt
        </span>
      </td>
    </tr>

<!--  Continue or Clear  -->

    <tr>
      <td colspan="2">
        <center><br />
          <input type="submit" 
            value="Continue" 
            name="3N" 
            onmouseover="showTip(event, 'moContinue')" 
            onmouseout="hideTip()"
          />
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <input type="button" 
            value="Start Over" 
            name="B2" 
            onclick='JavaScript: location.href="gpsruf1.do";' 
            onmouseover="showTip(event, 'moStartOver')" 
            onmouseout="hideTip()"
	  />
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <input type="button" 
            value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" 
            name="B3" 
            onclick="Javascript: window.location='gpsrf.jsp'; " 
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
    <img src="w3cxhtml10.bmp"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
  </p>
  </form>
</div>
<script language="JavaScript" type="text/javascript">
<!--
    document.close();
//-->
</script>
</body>
</html>
