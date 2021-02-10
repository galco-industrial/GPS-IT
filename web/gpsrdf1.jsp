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
      
        <!-- gpsrdf1.jsp

        Modification History
        
        version 1.3.00
        

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

function checkFamilyCode() {
    var myForm = document.form1;
    if (myForm.familyCode.selectedIndex == 0) {
        setSubfamilyOptionsToNull();
    } else {
        setSubfamilyOptions();
    }
}

function checkProductLine() {
    var myForm = document.form1;
    if (myForm.productLine.selectedIndex == 0) {
        setFamilyOptionsToNull();
        setSubfamilyOptionsToNull();
    } else {
        setFamilyOptions();
        setSubfamilyOptionsToNull();
    }
}

function checkSubfamilyCode() {
    var myForm = document.form1;
}

function deleteFamilyOptions() {
    var myForm = document.form1;
    var oListbox = myForm.familyCode;
    for (var i = oListbox.options.length-1; i >= 0; i--) {
    	oListbox.remove(i);
    }
}

function deleteSubfamilyOptions() {
    var myForm = document.form1;
    var oListbox = myForm.subfamilyCode;
    for (var i = oListbox.options.length-1; i >= 0; i--) {
        oListbox.remove(i);
    }
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
    if (divName == "moContinue") {return "Click Continue to select a Rule Set to delete.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    if (divName == "moFamily") {return "Select a Product Family from this list.";}
    if (divName == "moProductLine") {return "Select a Product Line from this list.";}
    if (divName == "moSubfamily") {return "Select a Product Subfamily from this list.";}
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


function noEnter(){
    return !(window.event && window.event.keyCode == 13); 
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
    myForm.productLine.focus();
}

function setFamilyOptions() {
    var myForm = document.form1;
    var oListbox = myForm.familyCode;
    var lineCode = myForm.productLine.value;
    var oOption;
    deleteFamilyOptions();
       
    // Create first entry in Select Box
    
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Please choose a Product Family"));
    oOption.setAttribute("value", "0" );
    oListbox.appendChild(oOption);
    
    // load new Family Options for this Product Line

    for (var i = 0; i < familyCodes.length; i++) {
    	if (familyCodes [i] [3] == lineCode) {
            oOption = document.createElement("option");
	    oOption.appendChild(document.createTextNode(familyCodes [i] [1] ));
            oOption.setAttribute("value", familyCodes [i] [0] );
            oListbox.appendChild(oOption);
	}
    }
}

function setFamilyOptionsToNull() {
    var myForm = document.form1;
    var oListbox = myForm.familyCode;
    var oOption;
    deleteFamilyOptions();
       
    // Create first entry in Select Box
    
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Please choose a Product Line first"));
    oOption.setAttribute("value", "0" );
    oListbox.appendChild(oOption);
}

function setSubfamilyOptionsToNull() {
    var myForm = document.form1;
    var oListbox = myForm.subfamilyCode;
    var oOption;
    deleteSubfamilyOptions();
       
    // Create first entry in Select Box
    
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Please choose a Product Family first"));
    oOption.setAttribute("value", "0" );
    oListbox.appendChild(oOption);
}

function setSubfamilyOptions() {
    var myForm = document.form1;
    var oListbox = myForm.subfamilyCode;
    var famCode = myForm.familyCode.value;
    var oOption;
    deleteSubfamilyOptions();
       
    // Create first entry in Select Box
    
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Please choose a Product Subfamily"));
    oOption.setAttribute("value", "0" );
    oListbox.appendChild(oOption);
    
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("All Subfamilies"));
    oOption.setAttribute("value", "*" );
    oListbox.appendChild(oOption);
    
    // load new Subfamily Options for this Family

    for (var i = 0; i < subfamilyCodes.length; i++) {
    	if (subfamilyCodes [i] [0] == famCode) {
            oOption = document.createElement("option");
	    oOption.appendChild(document.createTextNode(subfamilyCodes [i] [2] ));
            oOption.setAttribute("value", subfamilyCodes [i] [1] );
            oListbox.appendChild(oOption);
	}
    }
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
        
    if (myForm.productLine.selectedIndex == 0) {
        alert ("Please select a Product Line.");
        myForm.productLine.focus();
        return false;
    } else {
        myForm.productLineName.value = myForm.productLine.options[myForm.productLine.selectedIndex].text;
        //alert ("Selected Index = " + myForm.productLine.selectedIndex);
        //alert ("Value is " + myForm.productLine.value);
        //alert ("Option value is " + myForm.productLine.options[myForm.productLine.selectedIndex].value);
        //alert ("Product Line Name is " + myForm.productLineName.value);
    }
    
    if (myForm.familyCode.selectedIndex == 0) {
        alert ("Please select a Product Family.");
        myForm.familyCode.focus();
        return false;
    } else {
        myForm.familyName.value = myForm.familyCode.options[myForm.familyCode.selectedIndex].text;
        //alert ("Family Name is " + myForm.familyName.value);
    }
    
    if (myForm.subfamilyCode.selectedIndex == 0) {
        alert ("Please select a Product Subfamily.");
        myForm.subfamilyCode.focus();
        return false;
    } else {
        myForm.subfamilyName.value = myForm.subfamilyCode.options[myForm.subfamilyCode.selectedIndex].text;
        //alert ("Subfamily Name is " + myForm.subfamilyName.value);
    }
    
    // Set Rule Scope here (Scope is implied by subfamily code; i.e., All = Global)
    
    myForm.ruleScope.value = "L";
    if (myForm.subfamilyCode.value == "*" ) {
        myForm.ruleScope.value = "G";
    }
    
    //alert ("Rule Scope is " + myForm.ruleScope.value);
    
    if (myForm.auditUserID.value == "") {
        alert ("Please enter your User ID.");
        myForm.auditUserID.focus();
        return false;
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
    
    	// Define some globals here
	var UC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var LC = "abcdefghijklmnopqrstuvwxyz";
	var NU = "0123456789";
	var SP = " ";
	var globalDivName = "";
        var productLines = new Array();
        var familyCodes = new Array();
        var subfamilyCodes = new Array();
        var iL = 0;
        var iF = 0;
        var iS = 0;

        <c:forEach var="item" items="${lines}">
            productLines[iL++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${familyCodes}">
            familyCodes[iF++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${subfamilyCodes}">
            subfamilyCodes[iS++] = new Array(${item});
        </c:forEach>
        
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
        
<div class="toolTip" id="moAuditUserID">
	<div class="toolTipHeader" >Tip</div>
	Enter your User ID. Eventually your User ID will be extracted from your sign on info.
</div>

<div class="toolTip" id="moContinue">
	<div class="toolTipHeader">Tip</div>
	Click Continue to Read a Rule Set.
</div>

<div class="toolTip" id="moExit">
	<div class="toolTipHeader">Tip</div>
	Click Exit to abandon this operation and return to the previous Menu.
</div>

<div class="toolTip" id="moFamily">
	<div class="toolTipHeader">Tip</div>
	Choose a Product Family from this list.
</div>

<div class="toolTip" id="moProductLine">
	<div class="toolTipHeader">Tip</div>
	Choose a parent Product Line from this list.
</div>

<div class="toolTip" id="moSubfamily">
	<div class="toolTipHeader">Tip</div>
	Choose a Product Subfamily from this list. Choose All for Global Rules.
</div>

<form name="form1" action="gpsrdf2.do" method=post onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
            <input type="hidden" name="ruleScope" value="" />            
            <input type="hidden" name="productLineName" value="" />
            <input type="hidden" name="familyName" value="" />
            <input type="hidden" name="subfamilyName" value="" />
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
                <!-- <h3 class="blue">
                    ${statusMessage}
                </h3>  -->
            </td>
        </tr>
    </table>
        
    <table border="0" align="center" width="100%">
            <tr>
                <td colspan="2" align="center">
                    <p>Select the Product Line for the Product Family/Subfamily Rule Set you wish to Delete.
                    </p><br />
                </td>
            </tr>
                        
<!-- Product Line -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Product Line:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="productLine" size="1"
                        onchange="checkProductLine()"
                        onmouseover="showTip(event, 'moProductLine', 50, 100)"
                        onmouseout="hideTip('moProductLine')">
                        <option selected="selected" value="0">Please select a Product Line</option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            for (var i = 0; i < iL; i++){
                                document.write("<option ");
                                if (productLines[i][0] == "${sessionScope.sessionProductLineCode}") {
                                    document.write(" selected=\"selected\"");
                                }
                                document.write(" value=\"" + productLines[i][0] + "\">" + productLines[i][1]+"</option>");
                            }
                        //-->
                        </script>
                    </select>
                </td>
            </tr>
            
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Product Family:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="familyCode" size="1"
                        onchange="checkFamilyCode()"
                        onmouseover="showTip(event, 'moFamily', 50, 100)"
                        onmouseout="hideTip('moFamily')">
                        <option selected="selected" value="0">Please select a Product Line first</option>
                    </select>
                </td>
            </tr>
            
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Product Subfamily:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="subfamilyCode" size="1"
                        onchange="checkSubfamilyCode()"
                        onmouseover="showTip(event, 'moSubfamily', 50, 100)"
                        onmouseout="hideTip('moSubfamily')">
                        <option selected="selected" value="0">Please select a Product Line First</option>
                    </select>
                </td>
            </tr>
            
<!--  User ID  (This actually should be obtained from log in data -->

      <tr>
        <td align="right">
          <span class="requiredLabel">User ID:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="4" maxlength="4" name="auditUserID"
	
		value="${sessionScope.auditUserID}"

          onkeypress="return noEnter()"
          onblur="checkAuditUserID()"
          onmouseover="showTip(event, 'moAuditUserID')"
          onmouseout="hideTip('moAuditUserID')" 
          /> 
        </span></td>
      </tr>

<!--     Continue      -->

      <tr>
        <td colspan="2">
          <center>
            <br />
            <input type="submit" value="Continue" name="B1" 
	onmouseover="showTip(event, 'moContinue')" 
        onmouseout="hideTip('moContinue')"
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