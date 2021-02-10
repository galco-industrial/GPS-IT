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
	<title>Galco Parametric Search - Modify Subfamily Code</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- 
        gpsbmf3.jsp
        Version 1.5.05
        
        Modified 4/21/2008 by DES to support 4 divisions
        Modified 4.05.2010 by DES to support Index Keywords
        Modified 7/14/2010 by DES to remove Index Keywords.
        Modified 12/09/2011 by DES to ALWAYS update the Index.
        
        -->
    
<script language="JavaScript" type="text/javascript">
<!--
function altFamilyCodeChanged() {
    loadAltSubfamilyCodes();
}

function altProductLineChanged() {
    loadAltFamilyCodes();
    loadAltSubfamilyCodes();
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
        if (k == j) {
            return;  // No Change
        }
        for (var i = 0; i < iS; i++) {
            if (parseInt(subfamilyCodes[i][3]) == j
                    && subfamilyCodes[i][0] == "${familyCode}" ) {
                alert ("Error. This Display Order value is currently assigned to " + subfamilyCodes[i][1] + " - " + subfamilyCodes[i][2]);
                myForm.displayOrder.focus();
                return;
            }
        }
    }
}

function checkIfSubfamilyNameExists() {
    var myForm = document.form1;
    var work = myForm.subfamilyName.value;
    var famCode = "${familyCode}";
    var subfamCode = "${subfamilyCode}";
    for (var i = 0; i < iS; i++) {
        if (subfamilyCodes[i][1] != subfamCode) {
            if (famCode == subfamilyCodes[i][0]) {
                if (subfamilyCodes[ i ] [ 2 ] == work) {
                    alert ("Error! This Subfamily Name already exists in this Family!");
                    alert (subfamilyCodes[i][1] + " - " + subfamilyCodes [ i ] [ 2 ]);
                    myForm.subfamilyName.focus();
                    break;
                }
            }
        }
    }
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
    if (divName == "moAltFamilyCode") {return "If applicable, select an Alt. Family Code from this list.";}
    if (divName == "moAltProductLine") {return "If applicable, select an Alt. Product Line from this list.";}
    if (divName == "moAltSubfamilyCode") {return "If applicable, select an Alt. Subfamily Code from this list.";}
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moCancel"){return "Click Cancel to return to the previous screen.";}
    if (divName == "moDisplayOrder") {return "Enter a new Display order between 0 and 9999.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    if (divName == "moIKeywords") {return "If applicable, enter any index keywords separated by commas.";}
    if (divName == "moIKeywords2") {return "Check this box to automatically update the Subfamily Index.";}
    if (divName == "moIndexLevel") {return "Select an Web nav Index Level from this list.";}
    if (divName == "moModify") {return "Click to Modify this Subfamily Code.";}
    if (divName == "moNoModify") {return "This field cannot be modified.";}
    if (divName == "moPKeywords") {return "If applicable, enter any plural keywords separated by commas.";}
    if (divName == "moSKeywords") {return "If applicable, enter any singular keywords separated by commas.";}
    if (divName == "moSubfamilyName") {return "Enter up to 36 alphanumeric characters.";}
    return "";
}

function loadAltFamilyCodes() {
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
    myForm.subfamilyName.focus();
}

function setOldValues() {
    var myForm = document.form1;
    var work = myForm.subfamilyCode.value;
    for (var i = 0; i < iS; i++) {
        if (myForm.familyCode.value == subfamilyCodes[i][0] && work == subfamilyCodes[i][1]) {
            myForm.oldDisplayOrder.value = subfamilyCodes[i][3];
            myForm.displayOrder.value = subfamilyCodes[i][3];
            myForm.oldSubfamilyName.value = subfamilyCodes[i][2];
            myForm.subfamilyName.value = subfamilyCodes[i][2];
            break;
        }
    }
    //myForm.oldIndexLevel.value = myForm.txtIndexLevel.value;
    myForm.oldIndexLevel.value = indexLevel[${indexLevel}];
    myForm.indexLevel.selectedIndex = ${indexLevel};
    //if (myForm.oldIndexLevel.value == "2") {
    //    myForm.indexLevel.selectedIndex = 1;
    //}
    //if (myForm.oldIndexLevel.value == "3") {
    //    myForm.indexLevel.selectedIndex = 2;
    //}
    myForm.oldSKeywords.value = myForm.txtKeywordsSingular.value;
    myForm.sKeywords.value = myForm.txtKeywordsSingular.value;
    myForm.oldPKeywords.value = myForm.txtKeywordsPlural.value;
    myForm.pKeywords.value = myForm.txtKeywordsPlural.value;
    //myForm.iKeywords.value = editKeywords(myForm.txtKeywordsIndex.value);
    //myForm.oldIKeywords.value = myForm.iKeywords.value;
    work = myForm.txtAltProductLineCode.value;
    for (var i = 0; i < iL; i++) {
        if (work == productLines[i][0]) {
            myForm.oldAltProductLineName.value = productLines[i][1];
            break;
        }
    }
    work = myForm.txtAltFamilyCode.value;
    for (var i = 0; i < iF; i++) {
        if (work == familyCodes[i][0]) {
            myForm.oldAltFamilyName.value = familyCodes[i][1];
            break;
        }
    }
    var workS = myForm.txtAltSubfamilyCode.value;
    for (var i = 0; i < iS; i++) {
        if (work == subfamilyCodes[i][0] && workS == subfamilyCodes[i][1]) {
            myForm.oldAltSubfamilyName.value = subfamilyCodes[i][2];
            break;
        }
    }
}

function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
    work = myForm.subfamilyName.value;
    if (work.length == 0) {
        alert ("Please enter a valid Subfamily Name.");
        myForm.subfamilyName.focus();
        return false;
    }
    
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
       
//    if (myForm.oldSubfamilyName.value == myForm.subfamilyName.value
//            && myForm.oldDisplayOrder.value == myForm.displayOrder.value ) {
//        alert ("No changes are pending; Modify is aborted.");
//        return false;
//    }
    
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

        var indexLevel = new Array();
        indexLevel[0] = "Do Not Display in Index";
        indexLevel[1] = "Display at Subfamily Level";
        indexLevel[2] = "Display at Family Level Only";
        indexLevel[3] = "Display at Both Family and Subfamily Levels";
        
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

<form name="form1" action="gpsbmf4.do" method="post" onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
            <input type="hidden" name="familyCode" value="${familyCode}" />
            <input type="hidden" name="txtAltFamilyCode" value="${altFamilyCode}" />
            <input type="hidden" name="txtAltProductLineCode" value="${altProductLine}" />
            <input type="hidden" name="txtAltSubfamilyCode" value="${altSubfamilyCode}" />
            <input type="hidden" name="txtFamilyName" value="${familyName}" />
            <!--
            <input type="hidden" name="txtKeywordsIndex" value="${keywordsIndex}" />
            -->
            <input type="hidden" name="txtKeywordsPlural" value="${keywordsPlural}" />
            <input type="hidden" name="txtIndexLevel" value="${indexLevel}" />
            <input type="hidden" name="txtKeywordsSingular" value="${keywordsSingular}" />
                       
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
			Modify Subfamily Code
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
                    <p>The Product Line, Family, and Subfamily Code cannot be changed.
                    A Subfamily Name can contain up to 36 alphanumeric characters 
                        and must be unique within a Family.
                    </p><br />
                </td>
            </tr>
            
<!-- Product Line -->

                <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Product Line:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="productLineName" size="36" maxlength="36"
                        value="${productLineName}"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                    <span class="limits">&nbsp;&nbsp;(Cannot be changed.)</span>
                </td>
            </tr>
            
<!-- Family Name -->           
            
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Family:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="familyName" size="36"
                        value="${familyName}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()"
                    />
                </span>
                <span class="limits">&nbsp;&nbsp;(Cannot be changed.)</span>
                </td>
            </tr>
            
<!-- Subfamily Code -->
            
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Subfamily Code:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="subfamilyCode" size="8"
                        value="${subfamilyCode}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()"
                    />
                </span>
                <span class="limits">&nbsp;&nbsp;(Cannot be changed.)</span>
                </td>
            </tr>

<!-- Subfamly Name -->

            <tr>
                <td align="right">
                    <span class="fixedLabel">Old SubFamily Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="oldSubfamilyName" size="36" 
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
            
            <tr>
                <td align="right">
                    <span class="requiredLabel">New Subfamily Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="subfamilyName" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moSubfamilyName', 50, 100)"
                        onmouseout="hideTip()" 
                        onblur="checkSubfamilyName()"				
                    />
                    </span>
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
                        onchange="altFamilyCodeChanged()"
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
                                    //if (fc != familyCodes[i][0]) { 
                                        document.write("<option");
                                        if (work == familyCodes[i][0]) {
                                            document.write(" selected=\"selected\"");
                                        }
                                        document.write(" value=\"" + familyCodes[i][0] + "\">" + familyCodes[i][1]+"</option>");
                                    //}
                                }
                            }
                        //-->
                        </script>
                    </select>
                </td>
            </tr>
    
            
<!-- Alt SubFamily -->

                <tr>
                <td align="right">
                    <span class="fixedLabel">Old Alt Subfamily:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="oldAltSubfamilyName" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">New Alt Subfamily:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="altSubfamilyCode" size="1"
                        onmouseover="showTip(event, 'moAltSubfamilyCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="">None</option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            var work = document.form1.txtAltFamilyCode.value;
                            var workSub = document.form1.txtAltSubfamilyCode.value;
                            var sfc = document.form1.subfamilyCode.value;
                            for (var i = 0; i < iS; i++){
                                if (subfamilyCodes[i][0] == work) {
                                    if (sfc != subfamilyCodes[i][1]) { 
                                        document.write("<option");
                                        if (workSub == subfamilyCodes[i][1]) {
                                            document.write(" selected=\"selected\"");
                                        }
                                        document.write(" value=\"" + subfamilyCodes[i][1] + "\">" + subfamilyCodes[i][2]+"</option>");
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

<!--  Index Level  -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Old Index Level:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="42" name="oldIndexLevel"
                readonly="readonly"
                onmouseover="showTip(event, 'moNoModify')"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>
      <tr>
        <td align="right">
          <span class="requiredLabel">New Index Level:&nbsp;</span>
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
     
              
<!--  Index Keyword 

      <tr>
        <td align="right">
          <span class="label">Update Subfamily Index:&nbsp;</span>
        </td>
        <td align="left" width="70%">
            <span class="dataField">
                <input type="checkbox" name="index" checked="checked" value="1" 
                    onmouseover="showTip(event, 'moIKeywords2', 50, 100)"
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
            <input type="button" value="&nbsp;&nbsp;&nbsp;Cancel&nbsp;&nbsp;&nbsp;" name="B9" onclick="Javascript: window.location='gpsbmf1.do'; " 
	onmouseover="showTip(event, 'moCancel')" 
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