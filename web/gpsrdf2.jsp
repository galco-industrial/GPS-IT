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
      
        <!-- gpsrdf2.jsp

        Modification History
        
        version 1.5.00
        

        04/23/2008      DES     Modified to support 4 Divisions
        
        -->
        
<script language="JavaScript" type="text/javascript">
<!--

function checkAuditUserID() {
	// Check User ID

	var myForm = document.form1;
	var work = myForm.auditUserID.value;
	work = deleteSpaces(work);
	work = work.toUpperCase();
	myForm.auditUserID.value = work;
	if (work.length > 0) {
            if (checkCharSet(work, UC + NU) == false || work.length != 4) {
		alert ("Please enter a valid User ID.");
		myForm.auditUserID.focus();
		return;
            }
	}
}

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

function deleteLeadingSpaces(argwork) {
	// I delete beginning spaces from a string

	var work = argwork;
	while (work.charCodeAt(0) == 32) {
		work = work.slice(1);
	}
	return work;
}


function deleteLeadingZeroes(work) {
    // I delete leading zeroes
    if (work.length > 1) {
        while (work.charCodeAt(0) == 48) {
            work = work.slice(1);
        }
        if (work.length == 0) {
            work = "0";
        }
    }
    return work;
}

function deleteSpaces(stringToCheck) {
    // Eliminate all spaces
    var myChar;
    var loopCounter;
    var resultString = "";
    var strlen = stringToCheck.length;
    for (loopCounter = 0; loopCounter < strlen; loopCounter++) {
        myChar = stringToCheck.charAt(loopCounter);
        if (myChar != " ") {
            resultString += myChar;
        }
    }
    return resultString;
}

function deleteTrailingSpaces(argwork) {
	// I delete trailing spaces from a string

	var work = argwork;
	var lastc = work.length - 1;
	while (work.charCodeAt(lastc--) == 32) {
		work = work.slice(0,-1);
	}
	return work;
}

function getMessage(divName) {
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moCancel") {return "Click Cancel to return to the previous screen.";}
    if (divName == "moClick") {return "Click to Delete the Rule Set for this field.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the Previous Menu.";}
    if (divName == "moProductLine") {return "This is the Product line you selected.";}
    if (divName == "moFamilyName") {return "This is the Product Family you selected.";}
    if (divName == "moSubfamilyName") {return "This is the Product Subfamily you selected.";}
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

function reduceSpaces(argwork) {
    // replace multiple spaces with one
    var work = argwork;
    var myIndex = work.indexOf("  ");
    while (myIndex != -1) {
    	work=work.slice(0,myIndex) + work.slice(myIndex + 1);
	myIndex = work.indexOf("  ");
    }
    return work;
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
        var rules = new Array();
        var iR = 0;

        <c:forEach var="item" items="${rulesList}">
            rules[iR++] = new Array(${item});
        </c:forEach>
        
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
        
<div class="toolTip" id="moAuditUserID">
	<div class="toolTipHeader" >Tip</div>
	Enter your User ID. Eventually your User ID will be extracted from your sign on info.
</div>

<div class="toolTip" id="moCancel">
	<div class="toolTipHeader">Tip</div>
	Click Cancel to return to the previous screen.
</div>

<div class="toolTip" id="moClick">
	<div class="toolTipHeader">Tip</div>
	Click to Delete the Rule Set for this field
</div>

<div class="toolTip" id="moExit">
	<div class="toolTipHeader">Tip</div>
	Click Exit to abandon this operation and return to the previous Menu.
</div>

<div class="toolTip" id="moFamilyName">
	<div class="toolTipHeader">Tip</div>
	This is the Product Family you selected.
</div>

<div class="toolTip" id="moSubfamilyName">
	<div class="toolTipHeader">Tip</div>
	This is the Product Subfamily; All = Global Rules.
</div>

<div class="toolTip" id="moProductLine">
	<div class="toolTipHeader">Tip</div>
	This is the Product line you selected.
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
                <h3 class="red">
                    Field names in RED are required.
                </h3>
                <h3 class="blue">
                    ${statusMessage}
                </h3>
            </td>
        </tr>
    </table>
        
    <table border="0" align="center" width="100%">
            <tr>
                <td colspan="2" align="center">
                    <p>Click the desired Rule Set you wish to Delete.
                    </p><br />
                </td>
            </tr>
            
<!-- Product Line -->
            
      <tr>
        <td align="right">
          <span class="requiredLabel">Product Line:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="36"  name="productLine"
	
		value="${productLineName}"
                readonly="readonly"

          onmouseover="showTip(event, 'moProductLine')"
          onmouseout="hideTip('moProductLine')" 
          /> 
        </span></td>
      </tr>
      
<!-- Product Family -->
            
      <tr>
        <td align="right">
          <span class="requiredLabel">Product Family:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="36"  name="family"
	
		value="${familyName}"
                readonly="readonly"

          onmouseover="showTip(event, 'moFamilyName')"
          onmouseout="hideTip('moFamilyName')" 
          /> 
        </span></td>
      </tr>
      
<!-- Product Subfamily -->
            
      <tr>
        <td align="right">
          <span class="requiredLabel">Product Subfamily:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="36"  name="subfamily"
	
		value="${subfamilyName}"
                readonly="readonly"

          onmouseover="showTip(event, 'moSubfamilyName')"
          onmouseout="hideTip('moSubfamilyName')" 
          /> 
        </span></td>
      </tr>
                        
    </table>
    <br /><br /><br />
    

        
    <table border="1" align="center" width="100%">

            <tr>
                <td width="5%">
                    <span class="requiredLabel">Click to<br />Delete</span>
                </td>
                <td width="5%">
                    <span class="requiredLabel">Seq<br />Num</span>
                </td>
                <!---
                <td width="10%">
                    <span class="requiredLabel">Family<br />Code</span>
                </td>
                <td width="30%">
                    <span class="requiredLabel">Subfamily<br />Code</span>
                </td>
                                -->
                <td width="5%">
                    <span class="requiredLabel">RuleSet<br />Scope</span>
                </td>
                <td width="10%">
                    <span class="requiredLabel">RuleSet<br />Status</span>
                </td>
                 <td width="35%">
                    <span class="requiredLabel">Parm<br />Name</span>
                </td>
                <td width="10%">
                    <span class="requiredLabel">Data<br />Type</span>
                </td>
                <td width="30%">
                    <span class="requiredLabel">Units</span>
                </td>
             </tr>
            <script language="JavaScript" type="text/javascript">
<!--
            if (rules.length > 0) {
                for (var i = 0; i < rules.length; i++){
                    document.write("<tr><td><center><input type=\"button\" value=\"X\"");
                    document.write(" name=\"B4\" onclick=\"Javascript: window.location='gpsrdf3.do?familyCode=");
                    document.write(escape(rules[i][1]) + "&subfamilyCode=" + escape(rules[i][2]) + "&ruleScope=" + escape(rules[i][3]) + "&seqNum=" + escape(rules[i][0]));
                    document.write("&productLineName=" + escape("${productLineName}"));
                    document.write("'; \" onmouseover=\"showTip(event, 'moClick')\""); 
                    document.write(" onmouseout=\"hideTip('moClick')\" /></center>");
                    document.write("</td><td><span class='dataField'>");
                    document.write(rules[i][0]);
                    document.write("</span></td><td><span class='dataField'>");
                    //document.write(rules[i][1]);
                    //document.write("</span></td><td><span class='dataField'>");
                    //document.write(rules[i][2]);
                    //document.write("</span></td><td><span class='dataField'>");
                    document.write(rules[i][3]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(rules[i][4]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(rules[i][5]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(rules[i][6]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(rules[i][7] + "&nbsp;");
                    document.write("</span></td></tr>");
                }
            } else {
                    document.write("<tr><td colspan='7'><span class='dataField'>");
                    document.write("<center>No RuleSets exist for this Family/Subfamily</center>");
                    document.write("</span></td></tr>");
            }
//-->
            </script>
            
    </table>        

 <table border="0">
<!--     Continue      -->

      <tr>
        <td colspan="2">
          <center>
            <br />
                     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
             <input type="button" value="Cancel" name="B9" onclick="Javascript: history.back(); " 
	onmouseover="showTip(event, 'moCancel')" 
        onmouseout="hideTip('moCancel')"
	/>
         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsrf.jsp'; " 
	onmouseover="showTip(event, 'moExit')" 
        onmouseout="hideTip('moExit')"
	/>
          </center>
        </td>
      </tr>
    </table>
<br /><br />
  <p>
    <img src="w3cxhtml10.bmp"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
  </p>
    </form>
    </div>  
</body>
</html>