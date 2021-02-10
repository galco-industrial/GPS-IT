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
	<title>Galco Parametric Search - Modify Rule Set Part 3S</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <jsp:useBean id="sRuleSet" class="gps.util.GPSrules" scope="session"/>
        
        <!-- gpsrmf4s.jsp

        Modification History
        
        version 1.5.00
     
        04/23/2008      DES     Modified to support 4 Divisions
        
        -->

<!--	*************************************************************
	*		My JavaScript Functions and junk	      	*
	*************************************************************  -->

<script language="JavaScript" type="text/javascript">
<!--

function calcCharsAllowed() {
	var myForm = document.form1;
	var work = "";
	if (myForm.cbCharSet[0].checked == true) {work += UC; }
	if (myForm.cbCharSet[1].checked == true) {work += LC; }
	if (myForm.cbCharSet[2].checked == true) {work += SP; }
	if (myForm.cbCharSet[3].checked == true) {work += NU; }
	if (myForm.cbCharSet[4].checked == true) {work += '"'; }
	if (myForm.cbCharSet[5].checked == true) {work += "'" ; }

	var appender = myForm.otherCharSet.value;
	var char;
	var work2 = "";
	appender = stripCharacter(appender," ");
	appender = stripCharacter(appender,"'");
	appender = stripCharacter(appender,'"');
	if (myForm.allowTilde[0].checked == true ) { appender = stripCharacter(appender,"~"); }
	if (myForm.parmDelimiter[1].checked == true ) { appender = stripCharacter(appender,","); }
	if (myForm.parmDelimiter[2].checked == true ) { appender = stripCharacter(appender,";"); }
	if (myForm.parmDelimiter[3].checked == true ) { appender = stripCharacter(appender,"/"); }
	if (myForm.forceCase[1].checked == true ) { appender = stripLC(appender); }
	if (myForm.forceCase[2].checked == true ) { appender = stripUC(appender); }
	for (var i = 0; i < appender.length; i++) {
		char = appender.charAt(i);
		if (work.indexOf(char) == -1) {
			work += char;
			work2 += char;
		}
	}

	myForm.txtCharsAllowed.value = sortChars(work);
	myForm.otherCharSet.value = sortChars(work2);
}

function checkAllowDelimiters() {
    var myForm = document.form1;
    if (myForm.parmDelimiter[0].checked == false
        && myForm.regExpr[1].checked == true) {
            calcCharsAllowed();
    }
}

function checkAllowTilde() {
    var myForm = document.form1;
    if (myForm.allowTilde[0].checked == true && myForm.regExpr[1].checked == true) {
        calcCharsAllowed();
    }
}

function checkDefaultIndividual(work) {
    var myForm = document.form1;
    if (myForm.deleteNPC[0].checked) { work = doDeleteNPC(work); }
    if (myForm.deleteSP[0].checked) { work = deleteSpaces(work); }
    if (myForm.deleteLS[0].checked) { work = deleteLeadingSpaces(work); }
    if (myForm.deleteTS[0].checked) { work = deleteTrailingSpaces(work); }
    if (myForm.reduceSP[0].checked) { work = reduceSpaces(work); }
    if (myForm.forceCase[1].checked) { work = work.toUpperCase(); }
    if (myForm.forceCase[2].checked) { work = work.toLowerCase(); }
    if (myForm.regExpr[1].checked) {
    	var charSet = myForm.txtCharsAllowed.value;
        if (!checkCharSet(work, charSet)) {
            myForm.defaultValueRaw.focus();
            globalDefaultValid = false;
            return work;
	}
    } else {
        var rExpr = myForm.otherCharSet.value;
	var flags = "g";
	if (rExpr.length > 0 && work.length > 0) {
            var myRegExp = new RegExp(rExpr,flags);
            if (!myRegExp.test(work)) {
                alert ("A Data Entry Default value failed the Regular Expression test you specified.");
		globalDefaultValid = false;
		myForm.defaultValueRaw.focus();
		return work;
            }
	}
    }
    return work;
}

function checkDefaultValue() {
	var myForm = document.form1;
	var work = myForm.defaultValueRaw.value;
	var delim = "";
	globalDefaultValid = true;
	if (myForm.parmDelimiter[1].checked) { delim = ","; }
	if (myForm.parmDelimiter[2].checked) { delim = ";"; }
	if (myForm.parmDelimiter[3].checked) { delim = "/"; }
	var resultSet = delim;
	if (delim.length > 0) {
		var hold = "";
		var items = work.split(delim);
		for (var i = 0; i < items.length; i++) {
			hold = checkForTilde(items[i]) + delim;
			if (resultSet.indexOf(delim+hold) == -1) { resultSet += hold }
		}
		resultSet = resultSet.slice(1, resultSet.length - 1);
	} else {
		resultSet = checkForTilde(work);
	}
	myForm.defaultValueRaw.value = resultSet;
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

function checkDeleteSp() {
	var myForm = document.form1;
	if (myForm.deleteSP[0].checked == true) {
		myForm.deleteLS[0].checked = true;
		myForm.deleteLS[1].disabled = true;
		myForm.deleteLS[0].disabled = true;
		myForm.deleteTS[0].checked = true;
		myForm.deleteTS[1].disabled = true;
		myForm.deleteTS[0].disabled = true;
		myForm.reduceSP[0].checked = true;
		myForm.reduceSP[1].disabled = true;
		myForm.reduceSP[0].disabled = true;
		myForm.cbCharSet[2].checked = false;
		myForm.cbCharSet[2].disabled = true;
		if ( myForm.regExpr[1].checked == true ) {
			calcCharsAllowed();
		}
	} else {
		myForm.deleteLS[1].disabled = false;
		myForm.deleteLS[0].disabled = false;
		myForm.deleteLS[0].checked = true;
		myForm.deleteTS[1].disabled = false;
		myForm.deleteTS[0].disabled = false;
		myForm.deleteTS[0].checked = true;
		myForm.reduceSP[1].disabled = false;
		myForm.reduceSP[0].disabled = false;
		myForm.reduceSP[0].checked = true;
		if (myForm.allowTilde[1].checked == true) {
			myForm.cbCharSet[2].disabled = false;
		}
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

function checkForceCase() {
	var myForm = document.form1;
	if (myForm.forceCase[0].checked == true) {
		if (myForm.regExpr[1].checked == true) {
			myForm.cbCharSet[0].disabled = false;
			myForm.cbCharSet[1].disabled = false;
		}
		return;
	}
	if (myForm.forceCase[1].checked == true) {
		myForm.cbCharSet[1].checked = false;
		myForm.cbCharSet[1].disabled = true;
		if (myForm.regExpr[1].checked == true) {
			myForm.cbCharSet[0].disabled = false;
			calcCharsAllowed();
		}
		return;
	}
	if (myForm.forceCase[2].checked == true) {
		myForm.cbCharSet[0].checked = false;
		myForm.cbCharSet[0].disabled = true;
		if (myForm.regExpr[1].checked == true) {
			myForm.cbCharSet[1].disabled = false;
			calcCharsAllowed();
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
		checkLength(work);
		return work;
	} else {
		if (work.indexOf("~") == -1) {
			work = checkDefaultIndividual(work);
			checkLength(work);
			return work;
		}
		var parts = work.split("~");
		if (parts.length != 2) {
			alert ("Illegal use of multiple tildes in data element.");
			globalDefaultValid = false;
			myForm.defaultValueRaw.focus();
			return work;
		} else {
			var part1 = checkDefaultIndividual(parts[0]);
			var part2 = checkDefaultIndividual(parts[1]);
			if (part1.toUpperCase() != part2.toUpperCase()) {
				work = part1 + "~" + part2;
				if (part1.length == 0 || part2.length == 0) {
					alert ("One or both values in range specification are missing.");
					globalDefaultValid = false;
					myForm.defaultValueRaw.focus();
					return work;
				}
				checkLength(part1);
				checkLength(part2);
				if (part1.toUpperCase() > part2.toUpperCase()) {
					alert ("Error! Beginning value in a range cannot be greater than the ending value.");
					globalDefaultValid = false;
					myForm.defaultValueRaw.focus();
				}
				return work;
			} else {  // If we get something like  AA ~ AA
				work = part1;
				checkLength(work);
				return work;
			}
		}
	}
}
	
function checkLength(work) {
    var myForm = document.form1;
    var len = work.length;
    var hold = myForm.minLength.value;
    var min = parseInt(hold);
    if (!isNaN(min)) {
        if (len > 0  && len < min) {
            alert ("Data value must be at least " + min + " characters long.");
            globalDefaultValid = false;
            myForm.defaultValueRaw.focus();
            return;
	}
    }
    hold = myForm.maxLength.value;
    max = parseInt(hold);
    if (!isNaN(max)) {
    	if (max > 0  && len > max) {
            alert ("Data value cannot be greater than " + max + " characters long.");
            globalDefaultValid = false;
            myForm.defaultValueRaw.focus();
            return;
	}
    }
}        

function checkMaxLength() {
    var myForm = document.form1;
    var work = myForm.maxLength.value;
    work = deleteSpaces(work);
    myForm.maxLength.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value.");
            myForm.maxLength.focus();
            return;
	}
    }
}

function checkMinLength() {
    var myForm = document.form1;
    var work = myForm.minLength.value;
    work = deleteSpaces(work);
    myForm.minLength.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value.");
            myForm.minLength.focus();
            return;
	}
	if (parseInt(work) == 0 && myForm.required.value == "Y") {
            alert ("Required fields must be at least of length 1.");
            myForm.minLength.focus();
            return;
	}
    }
}

function checkOtherCharSet() {
    var myForm = document.form1;
    var work = myForm.otherCharSet.value;
    if (work.length != 0) {
        if (myForm.regExpr[0].checked == false) {
            calcCharsAllowed();
	}
    }
}

function checkQObject() {
	// DE Text Box or Select Box has been selected
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

function checkRegExpr() {
    // If Reg Expr is selected, then Special Characters is a Reg Expr string
    // and we need to disable the other Char set related fields
    var myForm=document.form1;
    myForm.defaultValueRaw.value = "";
    if (myForm.regExpr[0].checked == true) {
        myForm.otherCharSet.value="";
	myForm.txtCharsAllowed.value = "";
	myForm.cbCharSet[0].checked = false;
	myForm.cbCharSet[1].checked = false;
	myForm.cbCharSet[2].checked = false;
	myForm.cbCharSet[3].checked = false;
	myForm.cbCharSet[4].checked = false;
	myForm.cbCharSet[5].checked = false;
	disableCharSet();
    } else {
	myForm.otherCharSet.value="";
	myForm.txtCharsAllowed.value = "";
	myForm.cbCharSet[0].disabled = false;
	myForm.cbCharSet[1].disabled = false;
	myForm.cbCharSet[2].disabled = false;
	myForm.cbCharSet[3].disabled = false;
	myForm.cbCharSet[4].disabled = false;
	myForm.cbCharSet[5].disabled = false;
	if (myForm.forceCase[1].checked == true) {
            myForm.cbCharSet[1].disabled = true;
	}
	if (myForm.forceCase[2].checked == true) {
            myForm.cbCharSet[0].disabled = true;
	}
	if (myForm.deleteSP[0].checked == true) {
            myForm.cbCharSet[2].disabled = true;
	}
    }
}

function checkSearchMax() {
    var myForm = document.form1;
    var work = myForm.searchMax.value;
    work = deleteSpaces(work);
    myForm.searchMax.value = work;
    if (work.length > 0) {
    	if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value between 0 and 100.");
            myForm.searchMax.focus();
            return;
        }
	if (parseInt(work) > 100) {
            alert ("Please enter a valid numeric value between 0 and 100.");
            myForm.searchMax.focus();
            return;
	}
    }
}

function checkSearchMin() {
    var myForm = document.form1;
    var work = myForm.searchMin.value;
    work = deleteSpaces(work);
    myForm.searchMin.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, NU) == false) {
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
    var myForm = document.form1;
    var work = myForm.searchWeight.value;
    work = deleteSpaces(work);
    myForm.searchWeight.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, NU) == false) {
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

function disableCharSet() {
    var myForm=document.form1;
    myForm.cbCharSet[0].disabled = true;
    myForm.cbCharSet[1].disabled = true;
    myForm.cbCharSet[2].disabled = true;
    myForm.cbCharSet[3].disabled = true;
    myForm.cbCharSet[4].disabled = true;
    myForm.cbCharSet[5].disabled = true;
}

function getMessage(divName) {
    if (divName == "header"){return "You cannot change the Rule Scope, Line/Family/Subfamily name, Data Type, or the Field Number here.";}
    if (divName == "moCharSetA"){return "Use this option to allow Apostrophes (NOT RECOMMENDED).";}
    if (divName == "moCharSetL"){return "Use this option to allow Lower Case Letters a-z.";}
    if (divName == "moCharSetN"){return "Use this option to allow numeric characters 0-9.";}	
    if (divName == "moCharSetQ"){return "Use this option to allow Quotation Marks (NOT RECOMMENDED).";}
    if (divName == "moCharSetS"){return "Use this option to allow Spaces.";}
    if (divName == "moCharSetU"){return "Use this option to allow Upper Case Letters A-Z.";}
    if (divName == "moContinue"){return "Click to finish modifying this rule set.";}
    if (divName == "moDefaultValue"){return "Enter an optional default value for data entry.";}
    if (divName == "moDeleteLS"){return "Select this option to delete leading spaces.";}
    if (divName == "moDeleteNPC"){return "Select this option to delete all Non Print Characters.";}
    if (divName == "moDeleteSp"){return "Select this option to delete all whitespace.";}
    if (divName == "moDeleteTS"){return "Select this option to delete trailing spaces.";}
    if (divName == "moDEObjectSB"){return "Select this option to use a Select Box for Data Entry of this parameter.";}
    if (divName == "moDEObjectTB"){return "Select this option to use a Text Box for Data Entry of this parameter.";}
    if (divName == "moExit"){return "Click Exit to abandon this rule and return to the Rules Menu.";}
    if (divName == "moForceCase"){return "Use this option to force all letters to upper or lower case.";}
    if (divName == "moImageType"){return "If this field has an associated Image available, select the Image Type here.";}
    if (divName == "moMaxLength"){return "You can specify a maximum string length here.";}
    if (divName == "moMinLength"){return "You can specify a minimum string length here. It cannot be 0 if this field is flagged as required.";}
    if (divName == "moOtherCharSet"){return "Enter any letters, numbers, or other print characters here EXCEPT apostrophe's and quotation marks!";}
    if (divName == "moParmDelimiter"){return "Use a Delimiter to allow MULTIPLE VALUES within a field.";}
    if (divName == "moQObjectSB"){return "Select this option to use a Select Box for Searching by this parameter.";}
    if (divName == "moQObjectTB"){return "Select this option to use a Text Box for Searching by of this parameter.";}
    if (divName == "moReduceSp"){return "Select this option to reduce all whitespace to single spaces.";}
    if (divName == "moRegExpr"){return "Select Yes if you are using '+ Other Characters' as a Regular Expression.";}
    if (divName == "moSearchObjectSB"){return "Select this option to use a Select Box for Search argument for this parameter.";}
    if (divName == "moSearchObjectTB"){return "Select this option to use a Text Box for a Search argument for this parameter.";}
    if (divName == "moSearchMax"){return "Enter a value between 0 and 100 used to calculate the ranking on a match. Zero is good; 100 is bad.";}
    if (divName == "moSearchMin"){return "Enter a value between 0 and 100 used to calculate the ranking on no match.  Zero is good; 100 is bad.";}
    if (divName == "moSearchWeight"){return "Enter a relative weight for this search field. Relative weights should add up to 100.";}	
    if (divName == "moStartOver"){return "Click to abandon this rule set and start from the beginning.";}
    if (divName == "moTilde"){return "When enabled, a tilde '~' can be used to specify a range of values, e.g., A~Z.";}
    return "";
}

function loadImageTypes() {
	// iType is a global that contains the size of the Array
	var myForm = document.form1;
	var o = myForm.imageType;
        var oOption = document.createElement("option");
        oOption.appendChild(document.createTextNode( "<none>" ));
	oOption.setAttribute("value", "" );
        o.appendChild(oOption);
	for (var i = 0; i < iType; i++) {
		oOption = document.createElement("option");
                var temp =  imageTypes [i] [0];
		oOption.appendChild(document.createTextNode( imageTypes [i] [1] ));
		oOption.setAttribute("value", temp );
                if (temp == prevImageType) {
                    oOption.setAttribute("selected", true );
                }
		o.appendChild(oOption);
	}
}

function setDefaults() {
	var myForm = document.form1;
	if (myForm.regExpr[0].checked == true) { disableCharSet(); }
        loadImageTypes();
	checkDEObject();
	checkQObject();
        checkDeleteSp();
	window.defaultStatus = "These rules affect String Parametric Values.";
	myForm.deObject[0].focus();
}

function sortChars(work) {
    var result = "";
    var char;
    for ( var i = 32; i < 128; i++) {
    	char = String.fromCharCode(i);
        if  ( work.indexOf(char) != -1) {
            result += char;
	}
    }
    return result;
}

function stripCharacter(sString, cChar) {
    var i;
    i = sString.indexOf(cChar);
    while (i != -1) {
    	sString = sString.slice(0, i) + sString.slice(i + 1);
        i = sString.indexOf(cChar);
    }
    return sString;
}

function stripLC(work) {
    var char;
    for (var i = 97; i < 123; i++) {
    	char = String.fromCharCode(i);
        work = stripCharacter(work,char);
        if (work.length == 0) {
            return  ""; 
        }
    }
    return work;
}

function stripUC(work) {
    var char;
    for (var i = 65; i < 91; i++) {
        char = String.fromCharCode(i);
      	work = stripCharacter(work,char);
	if (work.length == 0) { 
            return  "";
        }
    }
    return work;
}

//	*********************************************************
//	*       	Form Validation Pre-Submit		*
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

        // Check Length Min

	work = myForm.minLength.value;
	if (work.length == 0) {
		if (myForm.required.value == "N") {
			myForm.minLength.value = "0";
			work = "0";
		}
		else {
			alert ("Please enter a Minimum Length for this String field.");
			myForm.minLength.focus();
			return false;
		}
	}
	work = parseInt(work);
	if (work == 0 && myForm.required.value == "Y") {
		alert ("The Minimum Length cannot be zero since this a a required field.");
		myForm.minLength.focus();
		return false;
	}
		
	// Check Max Length

	work2 = myForm.maxLength.value;
	if (work2.length == 0) {
		alert("Please enter a Maximum Length for this String field.");
		myForm.maxLength.focus();
		return false;
	}
	work2 = parseInt(work2);
	if (work2 < work || work2 == 0) {
		alert("Error! Maximum Length must be greater than zero and cannot be less than the Minimum Length.");
		myForm.maxLength.focus();
		return false;
	}	

	// Check Characters allowed is not empty
	// Check Regular Expression

	work = myForm.txtCharsAllowed.value;
	if (work.length == 0) {
		if (myForm.regExpr[1].checked) {
			alert ("You must enter a valid character set for input of acceptable data values.");
			myForm.otherCharSet.focus();
			return false;
		}
		if (myForm.regExpr[0].checked == true && myForm.otherCharSet.length == 0) {
			alert ("You selected a Regular Expression and the Regular Expression field is blank.");
			myForm.otherCharSet.focus();
			return false;
		}
	}

	// Check Default (length and Characters allowed and/or reg Expr)

	work = myForm.defaultValueRaw.value;
	if (work.length > 0) {
		checkDefaultValue();
		if (globalDefaultValid == false) {
			myForm.defaultValueRaw.focus();
			return false;
		}
	}
	
	// All Validation tests are complete
        
                // enable disabled controls so their values will be sent to the server
        
        myForm.deTextBoxSize.disabled = false;
        myForm.deSelectBoxName.disabled = false;
        myForm.qTextBoxSize.disabled = false;
        myForm.qSelectBoxName.disabled = false;
        if (myForm.deTextBoxSize.value == "") {
            myForm.deTextBoxSize.value = "0"
        }
        if (myForm.qTextBoxSize.value == "") {
            myForm.qTextBoxSize.value = "0"
        }
        myForm.deleteLS[0].disabled = false;
        myForm.deleteLS[1].disabled = false;
        myForm.deleteTS[0].disabled = false;
        myForm.deleteTS[1].disabled = false;
        myForm.reduceSP[0].disabled = false;
        myForm.reduceSP[1].disabled = false;
        myForm.cbCharSet[0].disabled = false;
        myForm.cbCharSet[1].disabled = false;
        myForm.cbCharSet[2].disabled = false;
        myForm.cbCharSet[3].disabled = false;
        myForm.cbCharSet[4].disabled = false;
        myForm.cbCharSet[5].disabled = false;

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
        var junk2 = "";
	var globalDefaultValid = false;
        var prevDESelectBoxName = "<jsp:getProperty name="sRuleSet" property="deSelectBoxName" />";
        var prevImageType = "<jsp:getProperty name="sRuleSet" property="imageType" />";
        var prevQSelectBoxName = "<jsp:getProperty name="sRuleSet" property="qselectBoxName" />";
        var sbNamesArray = new Array();
        var imageTypes = new Array();
	var sbnI = 0;
        var iType = 0;
        <c:forEach var="item" items="${selectBoxNames}">
            sbNamesArray [sbnI++] = "${item}";
        </c:forEach>
        <c:forEach var="item" items="${imageTypes}">
            imageTypes [iType++] = new Array(${item});
        </c:forEach>

//-->	
</script>

<div style="position: absolute; left: 10px; top: 10px; width: 710px; ">
    
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form method="post" action="gpsrmf5.do" onsubmit="return My_Validator()" name="form1">
<p>
  <input type="hidden" name="validation" value="Error" />
  <input type="hidden" name="status" value="${status}" />
  <input type="hidden" name="required" 
        value="<jsp:getProperty name="sRuleSet" property="deRequiredYN" />"
    />
  <input type="hidden" name="txtCharsAllowed" 
        value="<jsp:getProperty name="sRuleSet" property="charSet" />"
    />  <!-- Used for an update op -->
  
</p>

<table border="0" width="100%">

<!-- Logo and Heading  -->

  <tr>
    <td align="center" colspan="2">
	<h2>
        Parametric Search Rules Maintenance - Modify Rule Set - Part 3S
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


<div  class="masthead" onmouseover="showTip(event, 'header')" onmouseout="hideTip()" >
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
<script language="JavaScript" type="text/javascript">
<!--
                    if (document.form1.status.value != "A") {
                        document.write("<tr><td align=\"center\" colspan=\"4\"><font color=\"&CC0000\"><b>");
                        document.write("This parm field is currently INACTIVE.");               
                        document.write("</b></font></td></tr>");
                    }
 
//-->
</script>       
      
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

<!--  Image Type  --> 

      <tr>
        <td align="right">
          <span class="requiredLabel">Image Type:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <select size="1" name="imageType"
		onmouseover="showTip(event, 'moImageType', 200, 50)" 
        	onmouseout="hideTip()">
          </select>
        </span></td>
      </tr>

<!--  Minimum & Maximum  Length  -->

      <tr>
        <td align="right" >
          <span class="requiredLabel">Length:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="3" maxlength="2" name="minLength"
	
	    value="<jsp:getProperty name="sRuleSet" property="minLength" />"

          onblur="checkMinLength()"
	  onmouseover="showTip(event,'moMinLength')" 
          onmouseout="hideTip()"
          /> 
          Min
          <input type="text" size="3" maxlength="2" name="maxLength"

	    value="<jsp:getProperty name="sRuleSet" property="maxLength" />"

          onblur="checkMaxLength()"
          onmouseover="showTip(event,'moMaxLength')" 
          onmouseout="hideTip()"
          /> 
          Max
        </span></td>
      </tr>

<!--  Delete Non printing Characters  -->

      <tr>
        <td align="right">
          <span class="label">Delete NP Characters:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="deleteNPC" value="Y"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moDeleteNPC'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="deleteNPC" />';
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
        inputObject = '<input type="radio" name="deleteNPC" value="N"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moDeleteNPC'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="deleteNPC" />';
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


<!--  Delete All Spaces  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Delete All Spaces:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">

<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="deleteSP" value="Y"';
        inputObject += ' onclick="checkDeleteSp()" onmouseover="showTip(event, ';
        inputObject += "'moDeleteSp'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="deleteSP" />';
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
        inputObject = '<input type="radio" name="deleteSP" value="N"';
        inputObject += ' onclick="checkDeleteSp()" onmouseover="showTip(event, ';
        inputObject += "'moDeleteSp'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="deleteSP" />';
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

<!--  Delete Leading Spaces  -->

      <tr>
        <td align="right">
          <span class="label">Delete Leading Spaces:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="deleteLS" value="Y"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moDeleteLS'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="deleteLS" />';
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
        inputObject = '<input type="radio" name="deleteLS" value="N"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moDeleteLS'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="deleteLS" />';
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

<!--  Delete Trailing Spaces  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Delete Trailing Spaces:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="deleteTS" value="Y"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moDeleteTS'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="deleteTS" />';
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
        inputObject = '<input type="radio" name="deleteTS" value="N"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moDeleteTS'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="deleteTS" />';
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

<!--  Reduce Spaces  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Reduce Spaces:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
           
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="reduceSP" value="Y"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moReduceSp'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="reduceSP" />';
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
        inputObject = '<input type="radio" name="reduceSP" value="N"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moReduceSp'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="reduceSP" />';
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

<!--  Force Case  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Force Case:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
        
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="forceCase" value="N"';
        inputObject += ' onchange="checkForceCase()" onmouseover="showTip(event, ';
        inputObject += "'moForceCase'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="forceUC" />';
        junk2 = '<jsp:getProperty name="sRuleSet" property="forceLC" />';
        if (junk == "false" && junk2 == "false") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          None&nbsp;&nbsp;&nbsp;&nbsp;

<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="forceCase" value="U"';
        inputObject += ' onchange="checkForceCase()" onmouseover="showTip(event, ';
        inputObject += "'moForceCase'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="forceUC" />';
        if (junk == "true") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>          

            To Upper Case&nbsp;&nbsp;&nbsp;&nbsp;
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="forceCase" value="L"';
        inputObject += ' onchange="checkForceCase()" onmouseover="showTip(event, ';
        inputObject += "'moForceCase'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk2 = '<jsp:getProperty name="sRuleSet" property="forceLC" />';
        if (junk2 == "true") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 
            To Lower Case
        </span></td>
      </tr>

<!--  Delimiter  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Delimiter:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="parmDelimiter" value=""';
        inputObject += ' onchange="checkAllowDelimiters()" onmouseover="showTip(event, ';
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

<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="parmDelimiter" value=","';
        inputObject += ' onchange="checkAllowDelimiters()" onmouseover="showTip(event, ';
        inputObject += "'moParmDelimiter'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="parmDelimiter" />';
        if (junk == ",") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>
            Comma&nbsp;&nbsp;&nbsp;&nbsp;
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="parmDelimiter" value=";"';
        inputObject += ' onchange="checkAllowDelimiters()" onmouseover="showTip(event, ';
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
        inputObject += ' onchange="checkAllowDelimiters()" onmouseover="showTip(event, ';
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
        junk = '<jsp:getProperty name="sRuleSet" property="allowTilde" />';
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
    </table>

<fieldset>
  <legend>Character Set</legend>
    <table width="100%" border="0" >

<!--  Treat as Reg Expression  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Treat Special Chars&nbsp; as RegExp:&nbsp;</span>
        </td>
        <td align="left" width="80%"><span class="dataField">
          
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="regExpr" value="Y"';
        inputObject += ' onchange="checkRegExpr()" onmouseover="showTip(event, ';
        inputObject += "'moRegExpr'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="regExpr" />';
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
        inputObject = '<input type="radio" name="regExpr" value="N"';
        inputObject += ' onchange="checkRegExpr()" onmouseover="showTip(event, ';
        inputObject += "'moRegExpr'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="regExpr" />';
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

<!--  Special Characters  -->

      <tr>
        <td align="right">
          <span class="label">Special Chars:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
          <input type="text" name="otherCharSet" size="78" maxlength="60"
 
	    value="<jsp:getProperty name="sRuleSet" property="otherCharSet" />"

          onblur="checkOtherCharSet()"
	    onmouseover="showTip(event,'moOtherCharSet')" 
            onmouseout="hideTip()"
          />
          
        </span></td>
      </tr>

<!--  Character Set  -->

      <tr>
        <td align="right">
          <span class="label">Character&nbsp;&nbsp;Groups:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="checkbox" name="cbCharSet" value="U"';
        inputObject += ' onclick="calcCharsAllowed()" onmouseover="showTip(event, ';
        inputObject += "'moCharSetU'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="charSetGroups" />';
        if (junk.indexOf("U") > -1) {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>
          
          Upper Case Letters&nbsp;&nbsp;&nbsp;&nbsp;
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="checkbox" name="cbCharSet" value="L"';
        inputObject += ' onclick="calcCharsAllowed()" onmouseover="showTip(event, ';
        inputObject += "'moCharSetL'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="charSetGroups" />';
        if (junk.indexOf("L") > -1) {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            Lower Case Letters&nbsp;&nbsp;&nbsp;&nbsp;
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="checkbox" name="cbCharSet" value="S"';
        inputObject += ' onclick="calcCharsAllowed()" onmouseover="showTip(event, ';
        inputObject += "'moCharSetS'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="charSetGroups" />';
        if (junk.indexOf("S") > -1) {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            Spaces&nbsp;&nbsp;&nbsp;&nbsp;
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="checkbox" name="cbCharSet" value="N"';
        inputObject += ' onclick="calcCharsAllowed()" onmouseover="showTip(event, ';
        inputObject += "'moCharSetN'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="charSetGroups" />';
        if (junk.indexOf("N") > -1) {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>            

            Numbers
        </span></td>
      </tr>

<!--  Restricted Character Set  -->

      <tr>
        <td align="right">
          <span class="label">Restricted&nbsp;&nbsp; Characters:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
              
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="checkbox" name="cbCharSet" value="Q"';
        inputObject += ' onclick="calcCharsAllowed()" onmouseover="showTip(event, ';
        inputObject += "'moCharSetQ'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="charSetGroups" />';
        if (junk.indexOf("Q") > -1) {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          Quotes
          <font color="#FF0000"><i>&nbsp;(NOT recommended)</i></font>

&nbsp;&nbsp;
   
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="checkbox" name="cbCharSet" value="A"';
        inputObject += ' onclick="calcCharsAllowed()" onmouseover="showTip(event, ';
        inputObject += "'moCharSetA'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="charSetGroups" />';
        if (junk.indexOf("A") > -1) {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            Apostrophes
          <font color="#FF0000"><i>&nbsp;(NOT recommended)</i></font>
        </span></td>
      </tr>
    </table>
</fieldset>

<table width="100%" border="0">


<!--  Default Value  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">D.E. Default:&nbsp;</span>
        </td>
        <td><span class="dataField">
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="text" size="80" maxlength="80" name="defaultValueRaw" ';
        inputObject += ' onblur="checkDefaultValue()" onmouseover="showTip(event, ';
        inputObject += "'moDefaultValue'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="defaultValueRaw" />';
        inputObject += ' value="' + junk + '"';
        inputObject += ' />';
        document.write(inputObject);
        document.close();
//-->
</script> 

        </span></td>
      </tr>

<!--  Continue or Clear  -->

      <tr>
        <td colspan="2">
          <center>
            <input type="submit" 
                value="Continue" 
                name="3S"
                onmouseover="showTip(event, 'moContinue')" 
                onmouseout="hideTip()"
            />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" 
                value="Start Over" 
                name="B2" 
                onclick='JavaScript: location.href="gpsrmf1.do";' 
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
    <img src="http://www.w3.org/Icons/valid-xhtml10"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
  </p>
  </form>
</div>
</body>
</html>