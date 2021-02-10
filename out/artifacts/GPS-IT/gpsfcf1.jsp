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
	<title>Galco Parametric Search - Create Family Code</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- Version 1.5.05
        
        Modified 4/21/2008 by DES to support 4 divisions
        Modified 4.05.2010 by DES to support Index Keywords
        Modified 7/14/2010 by DES to remove Index Keywords.
        Modified 9/01/2010 by DES to update Index
         Modified 12/09/2011 by DES to ALWAYS update the Index.
        
        -->
      
<script language="JavaScript" type="text/javascript">
<!--


function checkAltFamilyCode() {
}

function checkAltProductLine() {
    loadAltFamilyCodes();
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

function checkFamilyCode() {
    var myForm = document.form1;
    var work = myForm.familyCode.value;
    work = deleteSpaces(work);
    work = work.toUpperCase();
    myForm.familyCode.value = work;
    if (!checkCharSet(work, UC + NU )) {
        myForm.familyCode.focus();
        return;
    }
    checkIfFamilyCodeExists();
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
    checkIfFamilyNameExists();
}

function checkIfFamilyCodeExists() {
    var myForm = document.form1;
    for (var i = 0; i < iF; i++) {
        if (familyCodes[ i ] [ 0 ] == myForm.familyCode.value) {
            alert ("Error! This Family Code already exists!");
            alert (myForm.familyCode.value + " - " + familyCodes [ i ] [ 1 ]);
            myForm.familyCode.focus();
            break;
        }
    }
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

function displayOrderExists() {
    var myForm = document.form1;
    var order = myForm.displayOrder.value;
    var pLine = myForm.productLine.value;
    if (myForm.productLine.selectedIndex != 0
        && order.length > 0) {
        for (var j = 0; j < iF; j++) {
            if (familyCodes[j][3] == pLine
                    && familyCodes[j][2] == order) {
                alert ("Error - This Display Order is already defined for this Family Code in this Product Line.");
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

function getMessage(divName) {
    if (divName == "moAllowParentAll") {return "I don't know what this does.";}
    if (divName == "moAltFamilyCode") {return "If applicable, select an Alt. Family Code from this list.";}
    if (divName == "moAltProductLine") {return "If applicable, select an Alt. Product Line from this list.";}
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moCreate") {return "Click Create to add this Family Code.";}
    if (divName == "moDisplayOrder") {return "Enter a Display order between 0 and 9999.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the Family Menu.";}
    if (divName == "moFamilyCode") {return "Enter up to 8 alphanumeric characters.";}
    if (divName == "moFamilyName") {return "Enter up to 36 alphanumeric characters.";}
    if (divName == "moIKeywords") {return "If checked, this family name will be automatically added to the Index.";}
    if (divName == "moParentFamilyCode") {return "If applicable, select a parent Family Code from this list.";}
    if (divName == "moPKeywords") {return "If applicable, enter any plural keywords separated by commas.";}
    if (divName == "moProductLine") {return "Select a Product Line from this list.";}
    if (divName == "moSKeywords") {return "If applicable, enter any singular keywords separated by commas.";}
    return "";
}

function loadAltFamilyCodes() {
    var myForm = document.form1;
    var oListbox = myForm.altFamilyCode;
    var productLineCode = myForm.altProductLine.value;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing options
    // load new Alt Family Codes for this Product Line
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
            //if (initialize) {
            //    if (prevParentFamily == code) {
            //        oOption.setAttribute("selected", true);
            //        //parentFamilyName = work;
            //    }
            //}
            oListbox.appendChild(oOption);
        }
    }
}

function loadParentFamilyCodes() {
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
            //if (initialize) {
            //    if (prevParentFamily == code) {
            //        oOption.setAttribute("selected", true);
            //        //parentFamilyName = work;
            //    }
            //}
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
    myForm.familyCode.focus();
}
 
function My_Validator() {
    var myForm = document.form1;
    var work = "";
    work = myForm.familyCode.value;
    if (work.length == 0) {
        alert ("Please enter a valid Family code");
        myForm.familyCode.focus();
        return false;
    }
    
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
    
    myForm.validation.value = "OK";
    return true;
}

//-->    
</script>        

</head>
<body onload="setDefaults()">

    <script language="JavaScript" type="text/javascript">
    <!--

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
        
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="gpsfcf2.do" method="post" onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
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
			Create Family Code
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
                    <p>A Family Code must be assigned to a parent Product Line.
                    A Family Code must contain 1 to 8 alphanumeric characters with no
                    embedded spaces. 
                    All Family Codes must be unique, regardless of the 
                    Product Line they belong to. 
                    A Family Name can contain up to 36 alphanumeric characters.
                    </p><br />
                </td>
            </tr>
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Family Code:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="familyCode" size="8" maxlength="8"
                        onblur="checkFamilyCode()"
                        onmouseover="showTip(event, 'moFamilyCode', 50, -50)"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <span class="requiredLabel">Family Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="familyName" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moFamilyName', 50, -50)"
                        onmouseout="hideTip()" 
                        onblur="checkFamilyName()"				
                    />
                    </span>
                </td>
            </tr>
            
<!-- Product Line -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Product Line:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="productLine" size="1"
                        onchange="productLineChanged()"
                        onmouseover="showTip(event, 'moProductLine', 50, -100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="0">Please select a Product Line</option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            for (var i = 0; i < iL; i++){
                                document.write("<option value=\"" + productLines[i][0] + "\">" + productLines[i][1]+"</option>");
                            }
                            //document.close();
                        //-->
                        </script>
                    </select>
                </td>
            </tr>

<!--  Display Ordering  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Display Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="4" maxlength="4" name="displayOrder"
                onblur="checkDisplayOrder()"
                onmouseover="showTip(event, 'moDisplayOrder', 200, -10)"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>
            
<!-- Parent Family Code -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="label">Parent Family:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="parentFamilyCode" size="1"
                        onmouseover="showTip(event, 'moParentFamilyCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="">None</option>
                        
                    </select>
                </td>
            </tr>

<!--  Allow Parent All  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Allow Parent All:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
            <input type="radio" name="allowParentAll" value="Y"
                onmouseover="showTip(event, 'moAllowParentAll', 50, 100)"
                onmouseout="hideTip()" />
            Yes&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="radio" name="allowParentAll" value="N"
                onmouseover="showTip(event, 'moAllowParentAll')" 
                onmouseout="hideTip()"
                checked="checked" />
            No
        </span></td>
      </tr>
      
<!-- Alt Product Line -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="label">Alt. Product Line:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="altProductLine" size="1"
                        onchange="checkAltProductLine()"
                        onmouseover="showTip(event, 'moAltProductLine', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="">None</option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            for (var i = 0; i < iL; i++){
                                document.write("<option value=\"" + productLines[i][0] + "\">" + productLines[i][1]+"</option>");
                            }
                            //document.close();
                        //-->
                        </script>
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
                        onmouseover="showTip(event, 'moAltFamilyCode', 50, 100)"
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
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsff.jsp'; " 
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
    <script language="JavaScript" type="text/javascript">
        document.close();
    //-->
    </script>
</body>
</html>