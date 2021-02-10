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
	<title>Galco Parametric Search - Read Family Code</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- Version 1.5.03
        
        Modified 4/21/2008 by DES to support 4 divisions
        Modified 4.05.2010 by DES to support Index Keywords
        Modified 7/14/2010 by DES to remove Index Keywords.
        
        -->
      
<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moAllowParentAll") {return "I don't know what this does.";}
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moDisplayOrder"){return "The Display Order (0 - 999) sets the order of this option when displayed.";}
    if (divName == "moCancel"){return "Click Cancel to return to the previous screen.";}
    if (divName == "moExit") {return "Click Exit to return to the Previous Menu.";}
    if (divName == "moFamilyCode") {return "This is the Family Code ID.";}
    if (divName == "moFamilyName") {return "This is the name assigned to this Family Code.";}
    if (divName == "moProductLine") {return "This Family Code is assigned to this Product Line.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    if ("${allowParentAll}" == "Y") {
        myForm.allowParentAll[0].checked = true;
    }
    myForm.B9.focus();
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
        var lineCodes = new Array();
        var iL = 0;
        
        <c:forEach var="item" items="${lines}">
            lineCodes[iL++] = new Array(${item});
        </c:forEach>
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="gpsfdf3.do" method="post" onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
            <input type="hidden" name="productLineCode" value="${productLineCode}" />
            <input type="hidden" name="txtAltFamilyCode" value="${altFamilyCode}" />
            <input type="hidden" name="txtAltProductLineCode" value="${altProductLineCode}" />
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
                    Parametric Search<br />Family Code Maintenance<br />
			Read Family Code
                        
                </h2>
            </td>
        </tr>

    </table>
        
    <table border="0" align="center" width="100%">
            <tr>
                <td colspan="2" align="center">
                        &nbsp;
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
                </td>
            </tr>
                        
            <tr>
                <td align="right">
                    <span class="fixedLabel">Family Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="familyName" size="36" maxlength="36"
                        value="${familyName}"
                        onmouseover="showTip(event, 'moFamilyName', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
       
            <tr>
                <td align="right">
                    <span class="fixedLabel">Product Line Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="productLineName" size="36" maxlength="36"
                        value="${productLineName}"
                        onmouseover="showTip(event, 'moProductLine', 50, 100)"
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
          <input type="text" size="4" maxlength="4" name="displayOrder"
                value="${displayOrder}"
                readonly="readonly"
                onmouseover="showTip(event, 'moDisplayOrder')"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>
      
<!-- Parent Family -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Parent Family:&nbsp;</span>
        </td>
        <td align="left">
          <span class="dataField">
            <input type="text" name="parentFamilyName" size="36" maxlength="36"
                        value="${parentFamilyName}"
                        onmouseover="showTip(event, 'moParentFamily', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
            />
          </span>
        </td>
      </tr>
 
<!--  Allow Parent All  -->

      <tr>
        <td align="right" width="20%">
          <span class="fixedLabel">Allow Parent All:&nbsp;</span>
        </td>
        <td align="left"><span class="dataField">
            <input type="radio" name="allowParentAll" value="Y"
                onmouseover="showTip(event, 'moAllowParentAll', 50, 100)"
                onmouseout="hideTip()" readonly="readonly" />
            Yes&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="radio" name="allowParentAll" value="N"
                onmouseover="showTip(event, 'moAllowParentAll')" 
                onmouseout="hideTip()"
                checked="checked" readonly="readonly" />
            No
        </span></td>
      </tr>
              
<!-- Alt Product Line -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Alt Product Line:&nbsp;</span>
        </td>
        <td align="left">
          <span class="dataField">
            <input type="text" name="altProductLineName" size="36" maxlength="36"
                        value="${altProductLineName}"
                        onmouseover="showTip(event, 'moAltProductLineName', 50, 100)"
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
            <input type="text" name="altFamilyName" size="36" maxlength="36"
                        value="${altFamilyName}" 
                        onmouseover="showTip(event, 'moAltFamilyName', 50, 100)"
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
              <input type="text" name="sKeywords" size="72" maxlength="72"
                        value="${keywordsSingular}"
                        onmouseover="showTip(event, 'moSKeywords', 50, 100)"
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
            <input type="text" name="pKeywords" size="72" maxlength="72"
                        value="${keywordsPlural}"
                        onmouseover="showTip(event, 'moPKeywords', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
            />
          </span>
        </td>
      </tr>
          
<!--  Index Keywords  
                
      <tr>
        <td align="right">
          <span class="fixedLabel">Index Keywords:&nbsp;</span>
        </td>
          <td align="left">
            <span class="dataField">
              <input type="text" name="iKeywords" size="72" maxlength="72"
                        value="${keywordsIndex}"
                        onmouseover="showTip(event, 'moIKeywords', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
              />
            </span>
          </td>
      </tr>
-->

<!--  Customer Buys  -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Customer Buys:&nbsp;</span>
        </td>
        <td align="left">
          <span class="dataField">
            <input type="text" name="custBuys" size="16" maxlength="16"
                        value="${custBuys}"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
            />
          </span>
        </td>
      </tr>
      
            
<!--  Total Buys  -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Total Buys:&nbsp;</span>
        </td>
        <td align="left">
          <span class="dataField">
            <input type="text" name="totalBuys" size="16" maxlength="16"
                        value="${totalBuys}"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
            />
          </span>
        </td>
      </tr>
      
      <tr>   
          <td colspan="2" align="center">
              <span class="fixedLabel"><br />Family/Subfamily Aliases</span><br /><br />
              <span class="dataField">
                  <c:forEach var="aliasItem" items="${aliasList}">
                      ${aliasItem}<br />
                  </c:forEach>
              </span>
          </td>
      </tr>
      
<!--     Continue      -->

      <tr>
        <td colspan="2">
          <center>
            <br />

            <input type="button" value="&nbsp;&nbsp;&nbsp;Cancel&nbsp;&nbsp;&nbsp;" name="B9" onclick="Javascript: window.location='gpsflf1.do'; " 
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
  <p>
    <img src="w3cxhtml10.bmp"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
  </p>
    </form>
    </div>  
</body>
</html>
