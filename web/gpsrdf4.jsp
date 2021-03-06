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
	<title>Galco Parametric Search - Delete Rule Set</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        
              
        <!-- gpsrdf4.jsp

        Modification History
        
        version 1.3.00
        

        04/23/2008      DES     Modified to support 4 Divisions
        
        -->
        
<script language="JavaScript" type="text/javascript">
<!--

function checkCharSet(stringToCheck,charSet) {
	// charSet contains all valid characters allowable
	// Check each char in stringToCheck and ensure it contains valid chars defined by charSet

	var myChar;
	var loopCounter;
	var stringIndex;
	var strlen = stringToCheck.length;
	for (loopCounter = 0; loopCounter < strlen; loopCounter++) {
		myChar = stringToCheck.charAt(loopCounter);
		stringIndex = charSet.indexOf(myChar);
		if (stringIndex == -1) {
			if (myChar == " ") {
				myChar = "<space>";
			}
			alert ("The following character is not allowed ---> " + myChar);
			return false;
		}
	}
	return true;
}

function getMessage(divName) {
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the Previous Menu.";}
    return "";
}

function hideTip(divName) {
    var myForm = document.form1;
    window.defaultStatus = "";
    if (myForm.enableToolTips.checked == true) {
    	var oDiv = document.getElementById(divName);
    	oDiv.style.visibility = "hidden";
    	globalDivName = "";
    }
}   

function setDefaults() {
    var myForm = document.form1;
    myForm.B3.focus();
}
 
function showTip(oEvent, divName, x, y) {
    if (!x) { x = 5; }
    if (!y) { y = 5; }
    var myForm = document.form1;
    if (myForm.enableToolTips.checked == true) {
    	if (globalDivName != "") { hideTip(globalDivName); }
            var oDiv = document.getElementById(divName);
            oDiv.style.visibility = "visible";
            oDiv.style.left = oEvent.clientX + x;
            oDiv.style.top = oEvent.clientY + y;
            globalDivName = divName;
	}
    window.defaultStatus = getMessage(divName);
}

function My_Validator() {
    var myForm = document.form1;
    var work = "";

    myForm.validation.value = "OK";
    return true;
}

//-->    
</script>        

</head>

<body onload="setDefaults()">

    <script language="JavaScript" type="text/javascript">
    <!--
    
    	// Define some globals here
	var UC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var LC = "abcdefghijklmnopqrstuvwxyz";
	var NU = "0123456789";
	var SP = " ";
	var globalDivName = "";
        
    //-->    
</script>

<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
<div class="toolTip" id="moExit">
	<div class="toolTipHeader">Tip</div>
	Click Exit to abandon this operation and return to the previous Menu.
</div>

        
<form name="form1" action="" method=post onsubmit="return My_Validator()">
<p>
            <input type="hidden" name="validation" value="Error" />
            <input type="hidden" name="familyCode" value="${familyCode}" />
            <input type="hidden" name="subfamilyCode" value="${subfamilyCode}" />
</p>

<table border="0" width="100%">

<!-- Logo and Heading  -->

        <tr>
            <td align="center" width="20%">
                <img src="gl_25.gif" alt="Galco logo" /><br />
                <div class="toolTipSwitch">
                    <input type="checkbox" 
<%
                        String tip = (String) session.getAttribute("enableToolTips");
                        if (tip != null && tip.equals("checked")) {
                            out.println (" checked=\"checked\" ");
                        }
%> 
                        name="enableToolTips"
                        value="checked"
                    />
                    Enable Tool Tips
                </div>
            </td>
            <td align="center" width="80%">
                <h2>
                    Parametric Search<br />Rules Maintenance<br />
			Delete Family/Subfamily Rule Set
                </h2>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;
            </td>
            <td>
                <h3 class="blue">
                    ${statusMessage}
                </h3>
            </td>
        </tr>
</table>
    
<table border="0">
<!--     Continue      -->

      <tr>
        <td>
          <center>
            <br />
              <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsrf.jsp'; " 
	onmouseover="showTip(event, 'moExit')" 
        onmouseout="hideTip('moExit')"
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
</body>
</html>
