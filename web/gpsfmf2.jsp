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
	<title>Galco Parametric Search - Modify Family Code</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- Version 1.5.05
        
        Modified 4/21/2008 by DES to support 4 divisions
        Modified 4.05.2010 by DES to support Index Keywords
        Modified 7/14/2010 by DES to remove Index Keywords.
         Modified 12/09/2011 by DES to ALWAYS update the Index.
        
        -->
      
<script language="JavaScript" type="text/javascript">
<!--

function altProductLineChanged() {
    loadAltFamilyCodes();
}

function checkDisplayOrder() {
	var myForm = document.form1;
	var work = myForm.displayOrder.value;
	work = deleteLeadingZeroes(deleteSpaces(work));
	myForm.displayOrder.value = work;
        if (work.length > 0) {
		if (checkCharSet(work, NU) == false) {
			alert ("Please enter a valid numeric value.");
			myForm.displayOrder.focus();
			return;
		}
                var k = parseInt(myForm.oldDisplayOrder.value);
                var j = parseInt(work);
                if (checkIfProductLineChanged() == false && k == j) {
                    return;  // No Change
                }
                newProductLineCode = myForm.productLine.value;
                for (var i = 0; i < iL; i++) {
                    if (parseInt(familyCodes[i][2]) == j
                            && familyCodes[i][3] == newProductLineCode) {
                        alert ("Error. This Display Order value is currently assigned to " + familyCodes[i][0] + " - " + familyCodes[i][1]);
                        myForm.displayOrder.value = "";
                        myForm.displayOrder.focus();
                        return;
                    }
                }
	}
}

function checkDisplayOrderAndName() {
    checkDisplayOrder();
    checkFamilyName();
}

function checkFamilyName() {
    var myForm = document.form1;
    var work = myForm.familyName.value;
    work = deleteLeadingSpaces(deleteTrailingSpaces(reduceSpaces(work)));
    //work = work.toUpperCase();
    myForm.familyName.value = work;
    if (!checkCharSet(work, UC + LC + NU + SP + "/-;&()" )) {
        myForm.familyName.focus();
        return;
    }
    if (checkIfProductLineChanged() == false) {
        //alert ("Product Line did not change");
        if (work == myForm.oldFamilyName.value) {
            //alert ("Neither did the Family Name");
            return;
        }
    }
    checkIfFamilyNameExists();
}

function checkIfFamilyNameExists() {
    var myForm = document.form1;
    var work = myForm.familyName.value;
    var work2 = myForm.productLine.value;
    for (var i = 0; i < iF; i++) {
        if (work2 == familyCodes[i][3]) {
            if (familyCodes[ i ] [ 1 ] == work) {
                alert ("Error! This Family Name already exists in this Product line!");
                alert (familyCodes[i][0] + " - " + familyCodes [ i ] [ 1 ]);
                myForm.familyName.value = "";
                myForm.familyName.focus();
                break;
            }
        }
    }
}

function checkIfProductLineChanged() {
    var myForm = document.form1;
    var newProductLineCode = myForm.productLine.value;
    //alert ("Old Product Line = " + oldProductLineCode + " New Product Line Code = " + newProductLineCode);
    if (newProductLineCode != oldProductLineCode) {
        return true;
    }
    return false;
}

function checkIndexKeywords() {
    var myForm = document.form1;
    var work = myForm.iKeywords.value;
    myForm.iKeywords.value = removeDuplicates(editKeywords(work));
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

function getMessage(divName) {
    if (divName == "moAllowParentAll") {return "I don't know what this does.";}
    if (divName == "moAltFamilyCode") {return "If applicable, select an Alt. Family Code from this list.";}
    if (divName == "moAltProductLine") {return "If applicable, select an Alt. Product Line from this list.";}
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moCancel"){return "Click Cancel to return to the previous screen.";}
    if (divName == "moDisplayOrder") {return "Enter a new Display order between 0 and 9999.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    if (divName == "moFamilyCode") {return "The Family Code cannot be modified.";}
    if (divName == "moFamilyName") {return "Enter up to 36 alphanumeric characters.";}
    if (divName == "moIKeywords") {return "Check this box to automatically update the Family Index, when applicable.";}
    if (divName == "moIKeywords2") {return "Check this box to automatically update the Subfamily Indeces, when applicable.";}
    if (divName == "moModify") {return "Click to Modify this Family Code.";}
    if (divName == "moNoModify") {return "This field cannot be modified.";}
    if (divName == "moParentFamilyCode") {return "If applicable, select a parent Family from this list.";}
    if (divName == "moPKeywords") {return "If applicable, enter any plural keywords separated by commas.";}
    if (divName == "moProductLine") {return "Select a new parent Product Line from this list.";}
    if (divName == "moSKeywords") {return "If applicable, enter any singular keywords separated by commas.";}
    return "";
}

function loadAltFamilyCodes() {
    //alert("Loading Alt Family Codes.");
    var myForm = document.form1;
    var oListbox = myForm.altFamilyCode;
    var productLineCode = myForm.altProductLine.value;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing options
    // load new Family Codes for this Product Line
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
            if (productLineCode == myForm.txtAltProductLineCode.value) {
                if (myForm.txtAltFamilyCode.value == code) {
                    oOption.setAttribute("selected", true);
                }
            }
            oListbox.appendChild(oOption);
        }
    }
}

function loadParentFamilyCodes() {
    //alert("Loading Parent Family Codes.");
    var myForm = document.form1;
    var oListbox = myForm.parentFamilyCode;
    var productLineCode = myForm.productLine.value;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing options
    // load new Family Codes for this Product Line
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
            if (productLineCode == oldProductLineCode) {
                if (myForm.txtParentFamilyCode.value == code) {
                    oOption.setAttribute("selected", true);
                }
            }
            oListbox.appendChild(oOption);
        }
    }
}

function productLineChanged() {
    checkDisplayOrder();
    checkFamilyName();
    loadParentFamilyCodes();
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

function setDefaults() {
    var myForm = document.form1;
    setOldValues();
    myForm.familyName.focus();
}

function setOldAltFamilyName() {
    var myForm = document.form1;
    for (var j = 0; j < iF; j++) {
        if (myForm.txtAltFamilyCode.value == familyCodes[j][0]) {
            myForm.oldAltFamilyName.value = familyCodes[j][1];
            return;
        }
    }
    myForm.oldAltFamilyName.value = "None";
}

function setOldAltProductLineName() {
    var myForm = document.form1;
    for (var j = 0; j < iL; j++) {
        if (myForm.txtAltProductLineCode.value == productLines[j][0]) {
            myForm.oldAltProductLineName.value = productLines[j][1];
            return;
        }
    }
    myForm.oldAltProductLineName.value = "None";
}

function setOldKeywords() {
    var myForm = document.form1;
    myForm.pKeywords.value = editKeywords(myForm.txtKeywordsPlural.value);
    myForm.oldPKeywords.value = myForm.pKeywords.value;
    myForm.sKeywords.value = editKeywords(myForm.txtKeywordsSingular.value);
    myForm.oldSKeywords.value = myForm.sKeywords.value;
    //myForm.iKeywords.value = editKeywords(myForm.txtKeywordsIndex.value);
    //myForm.oldIKeywords.value = myForm.iKeywords.value;
}

function setOldParentFamilyName() {
    var myForm = document.form1;
    var work = myForm.txtParentFamilyCode.value;
    for (var j = 0; j < iF; j++) {
        if (work == familyCodes[j][0]) {
            myForm.oldParentFamilyName.value = familyCodes[j][1];
            return;
        }
    }
    myForm.oldParentFamilyName.value = "None";
}

function setOldProductLineName() {
    var myForm = document.form1;
    for (var j = 0; j < iL; j++) {
        if (oldProductLineCode == productLines[j][0]) {
            myForm.oldProductLineName.value = productLines[j][1];
            return;
        }
    }
    myForm.oldProductLineName.value = "*undefined*";
}

function setOldValues() {
    var myForm = document.form1;
    var work = myForm.familyCode.value;
    if ("${allowParentAll}" == "Y") {
        myForm.allowParentAll[0].checked = true;
    }
    for (var i = 0; i < iF; i++) {
        if (work == familyCodes[i][0]) {
            setOldProductLineName();
            myForm.oldDisplayOrder.value = familyCodes[i][2];
            myForm.displayOrder.value = familyCodes[i][2];
            myForm.oldFamilyName.value = familyCodes[i][1];
            myForm.familyName.value = familyCodes[i][1];
            setOldParentFamilyName();
            setOldAltProductLineName();
            setOldAltFamilyName();
            setOldKeywords();
            return;
        }
    }
}

function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
    work = myForm.familyName.value;
    if (work.length == 0) {
        alert ("Please enter a valid Family Name.");
        myForm.familyName.focus();
        return false;
    }
    
    if (myForm.productLine.selectedIndex == 0) {
        alert ("Please select a Product Line.");
        myForm.productLine.focus();
        return false;
    }
    
    work = myForm.displayOrder.value;
    if (work.length == 0) {
        alert ("Please enter a Display Order value between 0 and 9999.");
        myForm.displayOrder.focus();
        return false;
    }
    
    if (myForm.familyCode.value == myForm.parentFamilyCode.value) {
        alert ("Parent Family cannot be the same as the Family."); 
        myForm.parentFamilyCode.focus();
        return false;        
    }
    
    if (myForm.familyCode.value == myForm.altFamilyCode.value) {
        alert ("Alt Family cannot be the same as the Family."); 
        myForm.altFamilyCode.focus();
        return false;        
    }
    
    if (myForm.altProductLine.value != "") {
        if (myForm.altFamilyCode.value == "") {
            alert ("Please select an Alternate Family for this Alternate Product Line.");
            myForm.altFamilyCode.focus();
            return false;
        }
    }
    
    if (myForm.sKeywords.value != "") {
        if (myForm.pKeywords.value == "") {
            alert ("Please enter a value for Plural Keywords.");
            myForm.pKeywords.focus();
            return false;
        }
    }
    
    if (myForm.pKeywords.value != "") {
        if (myForm.sKeywords.value == "") {
            alert ("Please enter a value for Singular Keywords.");
            myForm.sKeywords.focus();
            return false;
        }
    }
      
    if (myForm.auditUserID.value == "") {
        alert ("Please enter your User ID.");
        myForm.auditUserID.focus();
        return false;
    }
    
    if (myForm.parentFamilyCode.value == "" ) {
           myForm.allowParentAll[1].checked = true;
    }
    myForm.txtOldProductLineCode.value = oldProductLineCode;
    
    myForm.validation.value = "OK";
    return true;
}

//-->    
</script>        

</head>
<body onload="setDefaults()">

    <script language="JavaScript" type="text/javascript">
    <!--

        var oldProductLineCode = "";
        var familyCodes = new Array();
        var productLines = new Array();
        var iF = 0;
        var iL = 0;
        
        <c:forEach var="item" items="${familyCodes}">
            familyCodes[iF++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${lines}">
            productLines[iL++] = new Array(${item});
        </c:forEach>
        
        var myF = document.form1;
        var w = "${param.code}";
        for (var k = 0; k < iF; k++) {
            if (w == familyCodes[k][0]) {
                oldProductLineCode = familyCodes[k][3];
                break;
            }
        }
        
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="gpsfmf3.do" method="post" onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
            <input type="hidden" name="txtOldProductLineCode" />
            <input type="hidden" name="txtAllowParentAll" value="${allowParentAll}" />
            <input type="hidden" name="txtAltFamilyCode" value="${altFamilyCode}" />
            <input type="hidden" name="txtAltProductLineCode" value="${altProductLine}" />
            <!--
            <input type="hidden" name="txtKeywordsIndex" value="${keywordsIndex}" />
            -->
            <input type="hidden" name="txtKeywordsPlural" value="${keywordsPlural}" />
            <input type="hidden" name="txtKeywordsSingular" value="${keywordsSingular}" />
            <input type="hidden" name="txtParentFamilyCode" value="${parentFamilyCode}" />
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
                    Parametric Search<br />Family Maintenance<br />
			Modify Family Code
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
                    <p>The Family Code cannot be changed.
                    A Family Name can contain up to 36 alphanumeric characters and must be assigned to a valid Product Line.
                    </p><br />
                </td>
            </tr>
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Family Code:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="familyCode" size="8" maxlength="8"
                        value="${param.code}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moFamilyCode', 50, 100)"
                        onmouseout="hideTip()"
                    />
                </span>
                <span class="limits">&nbsp;&nbsp;(Cannot be changed here.)</span>
                </td>
            </tr>
            
            <tr>
                <td align="right">
                    <span class="fixedLabel">Old Family Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="oldFamilyName" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
            
            <tr>
                <td align="right">
                    <span class="requiredLabel">New Family Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="familyName" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moFamilyName', 50, 100)"
                        onmouseout="hideTip()" 
                        onblur="checkFamilyName()"				
                    />
                    </span>
                </td>
            </tr>
            
<!-- Product Line -->

                <tr>
                <td align="right">
                    <span class="fixedLabel">Old Product Line:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="oldProductLineName" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">New Product Line:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="productLine" size="1"
                        onchange="productLineChanged()"
                        onmouseover="showTip(event, 'moProductLine', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value=""> Select a Product Line </option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            for (var i = 0; i < iL; i++){
                                document.write("<option");
                                if (oldProductLineCode == productLines[i][0]) {
                                    document.write(" selected=\"selected\"");
                                }
                                document.write(" value=\"" + productLines[i][0] + "\">" + productLines[i][1]+"</option>");
                            }
                            
                        //-->
                        </script>
                    </select>
                </td>
            </tr>

<!--  Display Ordering  -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Old Display Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="4" maxlength="4" name="oldDisplayOrder"
                readonly="readonly"
                onmouseover="showTip(event, 'moNoModify')"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>
      <tr>
        <td align="right">
          <span class="requiredLabel">New Display Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="4" maxlength="4" name="displayOrder"
                onblur="checkDisplayOrder()"
                onmouseover="showTip(event, 'moDisplayOrder')"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>

<!-- Parent Family -->

                <tr>
                <td align="right">
                    <span class="fixedLabel">Old Parent Family:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="oldParentFamilyName" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">New Parent Family:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="parentFamilyCode" size="1"
                        onmouseover="showTip(event, 'moParentFamilyCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="">None</option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            var work = document.form1.txtParentFamilyCode.value;
                            for (var i = 0; i < iF; i++){
                                if (familyCodes[i][3] == oldProductLineCode) {
                                    if ("${param.code}" != familyCodes[i][0]) { 
                                        document.write("<option");
                                        if (work == familyCodes[i][0]) {
                                            document.write(" selected=\"selected\"");
                                        }
                                        document.write(" value=\"" + familyCodes[i][0] + "\">" + familyCodes[i][1]+"</option>");
                                    }
                                }
                            }
                        //-->
                        </script>
                    </select>
                </td>
            </tr>

<!--  Allow Parent All  -->

      <tr>
        <td align="right" width="20%">
          <span class="requiredLabel">Allow Parent All:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
            <input type="radio" name="allowParentAll" value="Y"
                onmouseover="showTip(event, 'moAllowParentAll', 50, 100)"
                onmouseout="hideTip()"  />
            Yes&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="radio" name="allowParentAll" value="N"
                onmouseover="showTip(event, 'moAllowParentAll')" 
                onmouseout="hideTip()"
                checked="checked"  />
            No
        </span></td>
      </tr>
          
<!-- Alt Product Line -->

                <tr>
                <td align="right">
                    <span class="fixedLabel">Old Alt Product Line:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="oldAltProductLineName" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">New Alt Product Line:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="altProductLine" size="1"
                        onchange="altProductLineChanged()"
                        onmouseover="showTip(event, 'moAltProductLine', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="">None</option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            var prev = document.form1.txtAltProductLineCode.value;
                            for (var i = 0; i < iL; i++){
                                document.write("<option");
                                if (prev == productLines[i][0]) {
                                    document.write(" selected=\"selected\"");
                                }
                                document.write(" value=\"" + productLines[i][0] + "\">" + productLines[i][1]+"</option>");
                            }
                        //-->
                        </script>
                    </select>
                </td>
            </tr>
            
<!-- Alt Family -->

                <tr>
                <td align="right">
                    <span class="fixedLabel">Old Alt Family:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="oldAltFamilyName" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">New Alt Family:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="altFamilyCode" size="1"
                        onmouseover="showTip(event, 'moAltFamilyCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="">None</option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            var work = document.form1.txtAltFamilyCode.value;
                            var altPL = document.form1.altProductLine.value;
                            var fc = document.form1.familyCode.value;
                            for (var i = 0; i < iF; i++){
                                if (familyCodes[i][3] == altPL) {
                                    if (fc != familyCodes[i][0]) { 
                                        document.write("<option");
                                        if (work == familyCodes[i][0]) {
                                            document.write(" selected=\"selected\"");
                                        }
                                        document.write(" value=\"" + familyCodes[i][0] + "\">" + familyCodes[i][1]+"</option>");
                                    }
                                }
                            }
                        //-->
                        </script>
                    </select>
                </td>
            </tr>
    
<!--  Singular Keywords  -->
                
            <tr>
                <td align="right">
                    <span class="fixedLabel">Old Singular Keywords:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="oldSKeywords" size="72" maxlength="72"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
      <tr>
        <td align="right">
          <span class="label">New Singular Keywords:&nbsp;</span>
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
                    <span class="fixedLabel">Old Plural Keywords:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="oldPKeywords" size="72" maxlength="72"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
      <tr>
        <td align="right">
          <span class="label">New Plural Keywords:&nbsp;</span>
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
          <span class="label">Update Family Index:&nbsp;</span>
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
<!--  Index Keyword 

      <tr>
        <td align="right">
          <span class="label">Update Subfamily Indeces:&nbsp;</span>
        </td>
        <td align="left" width="70%">
            <span class="dataField">
                <input type="checkbox" name="index2" checked="checked" value="1" 
                    onmouseover="showTip(event, 'moIKeywords2', 50, 100)"
                    onmouseout="hideTip()"
                />
            </span>
        </td>
      </tr>
      -->
  <!--  Index Keywords  
                
            <tr>
                <td align="right">
                    <span class="fixedLabel">Old Index Keywords:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="oldIKeywords" size="72" maxlength="72"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>

      <tr>
        <td align="right">
          <span class="label">New Index Keywords:&nbsp;</span>
        </td>
        <td align="left" width="70%">
            <span class="dataField">
                <input type="text" name="iKeywords" size="72" maxlength="72"
                    onblur="checkIndexKeywords()"
                    onmouseover="showTip(event, 'moIKeywords', 50, 100)"
                    onmouseout="hideTip()"
                />
            </span>
        </td>
      </tr>
-->      
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
            <input type="submit" value="Modify" name="B1" 
	onmouseover="showTip(event, 'moModify')" 
        onmouseout="hideTip()"
	/>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;Cancel&nbsp;&nbsp;&nbsp;" name="B9" onclick="Javascript: window.location='gpsfmf1.do'; " 
	onmouseover="showTip(event, 'moCancel')" 
        onmouseout="hideTip()"
	/>
       
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsff.jsp'; " 
	onmouseover="showTip(event, 'moExit')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
      </tr>
    </table>
<br /><br />
<script language="JavaScript" type="text/javascript">
<!--
    document.close();
//-->
</script>
  <p>
    <img src="w3cxhtml10.bmp"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
  </p>
    </form>
    </div>  
</body>
</html>
