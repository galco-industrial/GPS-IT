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
	<title>Galco Parametric Search - Delete Subfamily Code</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- 
        gpsbdf3.jsp
        Version 1.5.05
        
        Modified 4/21/2008 by DES to support 4 divisions
        Modified 4.05.2010 by DES to support Index Keywords
        Modified 7/14/2010 by DES to remove Index Keywords.
        Modified 9/01/2010 by DES to update Index
        Modified 12/09/2011 by DES To ALWAYS update the Index
        
        -->
    
<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moCancel"){return "Click Cancel to return to the previous screen.";}
    if (divName == "moDelete") {return "Click to Delete this Subfamily Code.";}
    if (divName == "moNoModify") {return "This field cannot be modified.";}
    if (divName == "moDisplayOrder") {return "This is the Subfamily Code display order.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    if (divName == "moIKeywords") {return "Select this option to automatically delete this Subfamily from the Index.";}
    if (divName == "moSubfamilyName") {return "This is the name of the Subfamily.";}
    if (divName == "moSubfamilyCode") {return "You can only delete a Subfamily Code when there are no longer any references to it in the database.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    setOldValues();
    myForm.B9.focus();
}

function setOldValues() {
    var myForm = document.form1;
    var work = myForm.subfamilyCode.value;
    for (var i = 0; i < iS; i++) {
        if (myForm.familyCode.value == subfamilyCodes[i][0] && work == subfamilyCodes[i][1]) {
            myForm.oldDisplayOrder.value = subfamilyCodes[i][3];
            myForm.oldSubfamilyName.value = subfamilyCodes[i][2];
            break;
        }
    }
    //myForm.oldIndexLevel.value = myForm.txtIndexLevel.value;
    myForm.oldIndexLevel.value = indexLevel[${indexLevel}];
    myForm.oldSKeywords.value = myForm.txtKeywordsSingular.value;
    myForm.oldPKeywords.value = myForm.txtKeywordsPlural.value;
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

<form name="form1" action="gpsbdf4.do" method="post" onsubmit="return My_Validator()">
<p>
    <input type="hidden" name="validation" value="Error" />
    <input type="hidden" name="familyCode" value="${familyCode}" />
    <input type="hidden" name="txtAltFamilyCode" value="${altFamilyCode}" />
    <input type="hidden" name="txtAltProductLineCode" value="${altProductLine}" />
    <input type="hidden" name="txtAltSubfamilyCode" value="${altSubfamilyCode}" />
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
			Delete Subfamily Code
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
                    <p>A Subfamily can be deleted only if all there are no references to it in the database. 
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
                    <span class="fixedLabel">SubFamily Name:&nbsp;</span>
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

<!--  Display Ordering  -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Display Order:&nbsp;</span>
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
         
<!-- Alt Product Line -->

                <tr>
                <td align="right">
                    <span class="fixedLabel">Alt Product Line:&nbsp;</span>
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
           
<!-- Alt Family -->

                <tr>
                <td align="right">
                    <span class="fixedLabel">Alt Family:&nbsp;</span>
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
           
<!-- Alt SubFamily -->

                <tr>
                <td align="right">
                    <span class="fixedLabel">Alt Subfamily:&nbsp;</span>
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
        
<!--  Singular Keywords  -->
                
            <tr>
                <td align="right">
                    <span class="fixedLabel">Singular Keywords:&nbsp;</span>
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
      
<!--  Plural Keywords  -->

            <tr>
                <td align="right">
                    <span class="fixedLabel">Plural Keywords:&nbsp;</span>
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
      
<!--  Index Level  -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Index Level:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="45" name="oldIndexLevel"
                readonly="readonly"
                onmouseover="showTip(event, 'moNoModify')"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>

                              
<!--  Index Keyword 

      <tr>
        <td align="right">
          <span class="label">Delete from Index:&nbsp;</span>
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
            <input type="submit" value="Delete" name="B1" 
	onmouseover="showTip(event, 'moDelete')" 
        onmouseout="hideTip()"
	/>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;Cancel&nbsp;&nbsp;&nbsp;" name="B9" onclick="Javascript: window.location='gpsbdf1.do'; " 
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