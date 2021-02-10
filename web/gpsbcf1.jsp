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
	<title>Galco Parametric Search - Create Subfamily Code</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- Version 1.5.05
        
        Modified 4/21/2008 by DES to support 4 divisions
        Modified 4.05.2010 by DES to support Index Keywords
        Modified 7/14/2010 by DES to remove Index Keywords.
        Modified 9/01/2010 by DES to update Index
        Modified 12/09/2011 by DES to always update Index
        
        -->
    
<script language="JavaScript" type="text/javascript">
<!--

function changedAltFamilyCode() {
    loadAltSubfamilyCodes();
}

function changedAltProductLine() {
    loadAltFamilyCodes();
    loadAltSubfamilyCodes();
}

function changedFamilyCode() {
    var myForm = document.form1;
    myForm.subfamilyCode.value = "";
    myForm.subfamilyName.value = "";
    myForm.displayOrder.value = "";
    resetAltSelectBoxes();
}

function changedProductLine() {
    var myForm = document.form1;
    loadFamilyCodes();
    myForm.subfamilyCode.value = "";
    myForm.subfamilyName.value = "";
    myForm.displayOrder.value = "";
    resetAltSelectBoxes();
}

function checkAltSubfamilyCode() {

}

function checkDisplayOrder() {
    var myForm = document.form1;
    var work = myForm.displayOrder.value;
    work = deleteLeadingZeroes(deleteSpaces(work));
    myForm.displayOrder.value = work;
    if (!checkCharSet(work, NU)) {
        myForm.displayOrder.focus();
        return;
    }
    if (displayOrderExists()) {
        var myForm = document.form1;
        // myForm.displayOrder.value = "";
        myForm.displayOrder.focus();
    }
}

function checkDisplayOrderOnFocus() {
    var myForm = document.form1;
    if (productLineNotSelected() ) {
        return;
    }
    if (familyNotSelected() ) {
        return;
    }
    if (subfamilyCodeBlank() ) {
        return;
    }
    if (subfamilyNameBlank() ) {
        return;
    }
}  

function checkIfSubfamilyCodeExists() {
    var myForm = document.form1;
    var work = myForm.familyCode.value;
    var work2 = myForm.subfamilyCode.value;
    for (var i = 0; i < iS; i++) {
        if (subfamilyCodes[ i ] [ 0 ] == work
                && subfamilyCodes[i][1] == work2) {
            alert ("Error! Subfamily Code " + work2 + " already exists in this Family!");
            myForm.subfamilyCode.focus();
            break;
        }
    }
}

function checkIfSubfamilyNameExists() {
    var myForm = document.form1;
    var work = myForm.familyCode.value;
    var work2 = myForm.subfamilyName.value;
    for (var i = 0; i < iS; i++) {
        if (work == subfamilyCodes[ i ] [ 0 ]) {
            if (subfamilyCodes[ i ] [ 2 ] == work2) {
                alert ("Error! Subfamily Name " + work2 + " already exists in this Family!");
                myForm.subfamilyName.focus();
                break;
            }
        }
    }
}

function checkIndexKeywords() {
    var myForm = document.form1;
    var work = myForm.iKeywords.value;
    work = editKeywords(work);
    myForm.iKeywords.value = removeDuplicates(work);
}

function checkPluralKeywords() {
    var myForm = document.form1;
    var work = myForm.pKeywords.value;
    myForm.pKeywords.value = editKeywords(work);
}

function checkSingularKeywords() {
    var myForm = document.form1;
    var work = myForm.sKeywords.value;
    work = editKeywords(work);
    myForm.sKeywords.value = work;
    if (work.length > 0) {
        if (myForm.pKeywords.value == "") {
            myForm.pKeywords.value = work;
        }
    }
}

function checkSubfamilyCode() {
    var myForm = document.form1;
    var work = myForm.subfamilyCode.value;
    work = deleteSpaces(work);
    work = work.toUpperCase();
    myForm.subfamilyCode.value = work;
    if (!checkCharSet(work, UC + NU + "*")) {
        myForm.subfamilyCode.focus();
        return;
    }
    checkIfSubfamilyCodeExists();
    if (myForm.subfamilyCode.value == "") {
        resetAltSelectBoxes();
        myForm.altProductLine.disabled = true;
    } else {
        myForm.altProductLine.disabled = false;
    }
}

function checkSubfamilyCodeOnFocus() {
    var myForm = document.form1;
    if (productLineNotSelected() ) {
        return;
    }
    if (familyNotSelected() ) {
        return;
    }
}

function checkSubfamilyName() {
    var myForm = document.form1;
    var work = myForm.subfamilyName.value;
    work = deleteLeadingSpaces(deleteTrailingSpaces(reduceSpaces(work)));
    myForm.subfamilyName.value = work;
    if (!checkCharSet(work, UC + LC + NU + SP + "/-;&()" )) {
        myForm.subfamilyName.focus();
        return;
    }
    checkIfSubfamilyNameExists();
}

function checkSubfamilyNameOnFocus() {
    var myForm = document.form1;
    if (productLineNotSelected() ) {
        return;
    }
    if (familyNotSelected() ) {
        return;
    }
    if (subfamilyCodeBlank() ) {
        return;
    }

}

function displayOrderExists() {
    var myForm = document.form1;
    var order = myForm.displayOrder.value;
    var fcode = myForm.familyCode.value;
    if (myForm.productLine.selectedIndex != 0
            && myForm.familyCode.selectedIndex != 0 
            && order.length > 0) {
        for (var j = 0; j < iS; j++) {
            if (subfamilyCodes[j][0] == fcode
                    && subfamilyCodes[j][3] == order) {
                alert ("Error - This Display Order is already defined for this Subfamily Code in this Family.");
                return true;
            }
        }
    }
    return false;
}

function editKeyword(work) {
    var result = "";
    var strlen = work.length;
    for (loopCounter = 0; loopCounter < strlen; loopCounter++) {
    	myChar = work.charAt(loopCounter);
    	stringIndex = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".indexOf(myChar);
    	if (stringIndex != -1) {
            result += myChar;
        }
    }
    result = deleteLeadingSpaces(deleteTrailingSpaces(reduceSpaces(result)));
    return result;
}

function editKeywords(work) {
    var kwords = new Array();
    var src = work.split(",");
    var indx = 0;
    var result = "";
    for (var i = 0; i < src.length; i++) {
        var kword = editKeyword(src[indx++]);
        if (kword != "") {
            result += "," + kword;
        }
    }
    return result.slice(1);
}

function familyNotSelected() {
    var myForm = document.form1;
    if (myForm.familyCode.value == "") {
        alert ("Please select a Family First");
        myForm.familyCode.focus();
        return true;
    }
    return false;
}

function getMessage(divName) {
    if (divName == "moAltFamilyCode") {return "If applicable, select an Alt. Family Code from this list.";}
    if (divName == "moAltProductLine") {return "If applicable, select an Alt. Product Line from this list.";}
    if (divName == "moAltSubfamilyCode") {return "If applicable, select an Alt. Subfamily Code from this list.";}
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moCreate") {return "Click Create to add this Subfamily Code.";}
    if (divName == "moDisplayOrder") {return "Enter a Display order between 0 and 9999.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the Family Menu.";}
    if (divName == "moFamilyCode") {return "Select a Family Code from this list.";}
    if (divName == "moIKeywords") {return "If checked, this subfamily name will be automatically added to the Index.";}
    if (divName == "moIndexLevel") {return "Select a web site navigation Index Level for this Subfamily.";}
    if (divName == "moPKeywords") {return "If applicable, enter any plural keywords separated by commas.";}
    if (divName == "moProductLine") {return "Select a Product Line from this list.";}
    if (divName == "moSKeywords") {return "If applicable, enter any singular keywords separated by commas.";}
    if (divName == "moSubfamilyCode") {return "Enter up to 8 alphanumeric characters.";}
    if (divName == "moSubfamilyName") {return "Enter up to 36 alphanumeric characters.";}
    return "";
}

function loadAltFamilyCodes() {
    var myForm = document.form1;
    var oListbox = myForm.altFamilyCode;
    var productLineCode = myForm.altProductLine.value;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing options
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("None"));
    oOption.setAttribute("value", "" );
    oListbox.appendChild(oOption);
    if (productLineCode == "") {
        return;
    }
    for (var i = 0; i < familyCodes.length; i++) {
        var code = familyCodes[i] [0];
        var name = familyCodes[i] [1];
        var line = familyCodes[i] [3];
        if (line == productLineCode) {
            oOption = document.createElement("option");
            oOption.appendChild(document.createTextNode(name));
            oOption.setAttribute("value", code);
            oListbox.appendChild(oOption);
        }
    }
}

function loadAltProductLines() {
    var myForm = document.form1;
    var oListbox = myForm.altProductLine;
    var productLineCode = myForm.productLine.value;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing options
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("None"));
    oOption.setAttribute("value", "" );
    oListbox.appendChild(oOption);
    if (productLineCode == "") {
        return;
    }
    for (var i = 0; i < productLines.length; i++) {
        var code = productLines[i] [0];
        var name = productLines[i] [1];
        var line = productLines[i] [3];
        oOption = document.createElement("option");
        oOption.appendChild(document.createTextNode(name));
        oOption.setAttribute("value", code);
        oListbox.appendChild(oOption);

    }
}

function loadAltSubfamilyCodes() {
    var myForm = document.form1;
    var oListbox = myForm.altSubfamilyCode;
    var sCode = myForm.subfamilyCode.value;
    var familyLineCode = myForm.altFamilyCode.value;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing options
    // load new Alt Subfamily Codes for this Family
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("None"));
    oOption.setAttribute("value", "" );
    oListbox.appendChild(oOption);
    if (familyLineCode == "") {
        return;
    }
    for (var i = 0; i < subfamilyCodes.length; i++) {
        var code = subfamilyCodes[i] [1];
        var name = subfamilyCodes[i] [2];
        var line = subfamilyCodes[i] [0];
        if (line == familyLineCode && code != sCode) {
            oOption = document.createElement("option");
            oOption.appendChild(document.createTextNode(name));
            oOption.setAttribute("value", code);
            oListbox.appendChild(oOption);
        }
    }
}

function loadFamilyCodes() {
    var myForm = document.form1;
    var oListbox = myForm.familyCode;
    var productLineCode = myForm.productLine.value;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing options
    //alert ("Loading new Family Codes for this Product Line " + productLineCode);
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Please select a Product Line first"));
    oOption.setAttribute("value", "" );
    oListbox.appendChild(oOption);
    if (productLineCode != "") {
        for (var i = 0; i < familyCodes.length; i++) {
            var code = familyCodes[i] [0];
            var name = familyCodes[i] [1];
            var line = familyCodes[i] [3];
            if (line == productLineCode) {
                oOption = document.createElement("option");
                oOption.appendChild(document.createTextNode(name));
                oOption.setAttribute("value", code);
                oListbox.appendChild(oOption);
            }
        }
    }
}

function productLineNotSelected() {
    var myForm = document.form1;
    if (myForm.productLine.value == "") {
        alert ("Please select a Product Line First");
        myForm.productLine.focus();
        return true;
    }
    return false;
}

function removeDuplicates(inp) {
    var work;
    var workUC;
    var out = new Array();
    var i = -1;
    var source = inp.split(",");
    for (var j = 0; j < inp.length; j++) {
        work = source[j];
        if (work) {
            if (work != "") {
                workUC = work.toUpperCase();
                for (k = 0; k < out.length; k++) {
                    if (workUC == out[k].toUpperCase()) {
                        workUC = "";
                        break;
                    }
                }
                if (workUC != "") {
                    out[++i] = work;
                }
            }
        }
    }
    return out.join(",");
}

function resetAltSelectBoxes() {
        var myForm = document.form1;
        loadAltProductLines();
        loadAltFamilyCodes();
        loadAltSubfamilyCodes();
        myForm.altProductLine.disabled = true;
}

function setDefaults() {
    var myForm = document.form1;
    myForm.productLine.focus();
}

function subfamilyCodeBlank() {
    var myForm = document.form1;
    if (myForm.subfamilyCode.value == "") {
        alert ("Please select a Subfamily Code first");
        myForm.subfamilyCode.focus();
        return true;
    }
    return false;
}

function subfamilyNameBlank() {
    var myForm = document.form1;
    if (myForm.subfamilyName.value == "") {
        alert ("Please select a Subfamily Name first");
        myForm.subfamilyName.focus();
        return true;
    }
    return false;
}

function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
    // First Check Product Line
    
    if (myForm.productLine.selectedIndex == 0) {
        alert ("Please select a Product Line.");
        myForm.productLine.focus();
        return false;
    }
    
    // Next check Family Code
    
    if (myForm.familyCode.selectedIndex == 0) {
        alert ("Please select a Family.");
        myForm.familyCode.focus();
        return false;
    }
    myForm.familyName.value = myForm.familyCode.options[myForm.familyCode.selectedIndex].text;
    
    // Next Check Subfamily Code
    
    work = myForm.subfamilyCode.value;
    if (work.length == 0) {
        alert ("Please enter a valid Subfamily code");
        myForm.subfamilyCode.focus();
        return false;
    }
    
    work = myForm.subfamilyName.value;
    if (work.length == 0) {
        alert ("Please enter a valid Subfamily Name.");
        myForm.subfamilyName.focus();
        return false;
    }
    
    // Check Display Order
    
    work = myForm.displayOrder.value;
    if (work.length == 0) {
        alert ("Please enter a Display Order value between 0 and 9999.");
        myForm.displayOrder.focus();
        return false;
    }
    
    // Check alt Product Line
    
    if (myForm.altProductLine.value != "") {
        if (myForm.altFamilyCode.value == "") {
            alert ("Please select an Alternate Family for this Alternate Product Line.");
            myForm.altFamilyCode.focus();
            return false;
        }
    }

    // Check alt Family Code
    
    if (myForm.altFamilyCode.value != "") {
        if (myForm.altSubfamilyCode.value == "") {
            alert ("Please select an Alternate Subfamily for this Alternate Family Code.");
            myForm.altSubfamilyCode.focus();
            return false;
        }
    }
     
    // Check User ID

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
    
        //var previousProductLine = "";
        var familyCodes = new Array();
        var subfamilyCodes = new Array();
        var productLines = new Array();
        var iF = 0;
        var iL = 0;
        var iS = 0;
        
        <c:forEach var="item" items="${productLinesList}">
            productLines[iL++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${familyCodesList}">
            familyCodes[iF++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${subfamilyCodesList}">
            subfamilyCodes[iS++] = new Array(${item});
        </c:forEach>
                
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="gpsbcf2.do" method="post" onsubmit="return My_Validator()">
<p>
   <input type="hidden" name="validation" value="Error" />
   <input type="hidden" name="familyName" value="" />
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
                    Parametric Search<br />Subfamily Maintenance<br />
			Create Subfamily Code
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
                    <p>A Subfamily Code must be assigned to a parent Family.
                    A Subfamily Code must contain 1 to 8 alphanumeric characters with no
                    embedded spaces. 
                    All Subfamily Codes must be unique within the parent Family. 
                    A Subfamily Name can contain up to 36 alphanumeric characters.
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
                        onchange="changedProductLine()"
                        onmouseover="showTip(event, 'moProductLine', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="">Please select a Product Line</option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            // var mf = document.form1;
                            for (var i = 0; i < iL; i++){
                                document.write("<option ");
                                //if (productLines[i][0] == "${productLineCode}") { 
                                //    document.write(" selected=\"selected\"");
                                //}
                                document.write(" value=\"" + productLines[i][0] + "\">" + productLines[i][1]+"</option>");
                            }
                        //-->
                        </script>
                    </select>
                </td>
            </tr>
            
<!-- Family -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Family:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="familyCode" size="1"
                        onchange="changedFamilyCode()"
                        onmouseover="showTip(event, 'moFamilyCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="">Please select a Product Line first</option>
                    </select>
                </td>
            </tr>
            
<!-- Subfamily Code -->

            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Subfamily Code:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="subfamilyCode" size="8" maxlength="8"
                        onkeypress="checkSubfamilyCodeOnFocus()"
                        onblur="checkSubfamilyCode()"
                        onmouseover="showTip(event, 'moSubfamilyCode', 50, 100)"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            
<!-- Subfamily Name -->

            <tr>
                <td align="right">
                    <span class="requiredLabel">Subfamily Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="subfamilyName" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moSubfamilyName', 50, 100)"
                        onmouseout="hideTip()"
                        onkeypress="checkSubfamilyNameOnFocus()"
                        onchange="checkSubfamilyName()"
                        onblur="checkSubfamilyName()"				
                    />
                    </span>
                </td>
            </tr>
            


<!--  Display Ordering  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Display Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="4" maxlength="4" name="displayOrder"
                onkeypress="checkDisplayOrderOnFocus()"
                onblur="checkDisplayOrder()"
                onmouseover="showTip(event, 'moDisplayOrder')"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>
            
<!-- Alt Product Line -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="label">Alt. Product Line:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="altProductLine" size="1"
                        onchange="changedAltProductLine()"
                        disabled="disabled"
                        onmouseover="showTip(event, 'moAltProductLine', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="">None</option>
                    </select>
                </td>
            </tr>
            
<!--  Alternate Family Code  -->

      <tr>
        <td align="right">
          <span class="label">Alt. Family:&nbsp;</span>
        </td>
                <td align="left" width="70%">
                    <select name="altFamilyCode" size="1"
                        onchange="changedAltFamilyCode()"
                        onmouseover="showTip(event, 'moAltFamilyCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="">None</option>
                    </select>
                </td>
      </tr>
                
<!--  Alternate Subfamily Code  -->

      <tr>
        <td align="right">
          <span class="label">Alt. Subfamily:&nbsp;</span>
        </td>
                <td align="left" width="70%">
                    <select name="altSubfamilyCode" size="1"
                        onchange="checkAltSubfamilyCode()"
                        onmouseover="showTip(event, 'moAltSubfamilyCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="">None</option>
                    </select>
                </td>
      </tr>
      
<!--  Singular Keywords  -->

      <tr>
        <td align="right">
          <span class="label">Singular Keywords:&nbsp;</span>
        </td>
        <td align="left" width="70%">
            <span class="dataField">
                <input type="text" name="sKeywords" size="72" maxlength="72"
                    onblur="checkSingularKeywords()"
                    onmouseover="showTip(event, 'moSKeywords', 50, 100)"
                    onmouseout="hideTip()"
                />
            </span>
        </td>
      </tr>
      
<!--  Plural Keywords  -->

      <tr>
        <td align="right">
          <span class="label">Plural Keywords:&nbsp;</span>
        </td>
        <td align="left" width="70%">
            <span class="dataField">
                <input type="text" name="pKeywords" size="72" maxlength="72"
                    onblur="checkPluralKeywords()"
                    onmouseover="showTip(event, 'moPKeywords', 50, 100)"
                    onmouseout="hideTip()"
                />
            </span>
        </td>
      </tr>
                       
<!--  Index Keyword 

      <tr>
        <td align="right">
          <span class="label">Add to Index:&nbsp;</span>
        </td>
        <td align="left" width="70%">
            <span class="dataField">
                <input type="checkbox" name="index" checked="checked" value="1" 
                    onmouseover="showTip(event, 'moIKeywords', 50, 100)"
                    onmouseout="hideTip()"
                />
            </span>
        </td>
      </tr>
      
      -->

<!--  Index Level  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Index Level:&nbsp;</span>
        </td>
        <td>
            <span class="dataField">
                <select name="indexLevel" size="1"
                        onmouseover="showTip(event, 'moIndexLevel', 50, 100)"
                        onmouseout="hideTip()">
                        <option value="0">Do Not Display in Index</option>
                        <option selected="selected" value="1">Display at Subfamily Level</option>
                        <option value="2">Display at Family Level Only</option>
                        <option value="3">Display at Both Family and Subfamily Levels</option>
                </select>
            </span>
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

          onblur="checkAuditUserID()"
          onmouseover="showTip(event, 'moAuditUserID')"
          onmouseout="hideTip()" 
          /> 
        </span></td>
      </tr>

<!--     Continue      -->

      <tr>
        <td colspan="2">
          <center>
            <br />
            <input type="submit" value="Create" name="B1" 
	onmouseover="showTip(event, 'moCreate')" 
        onmouseout="hideTip()"
	/>
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsbf.jsp'; " 
	onmouseover="showTip(event, 'moExit')" 
        onmouseout="hideTip()"
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
