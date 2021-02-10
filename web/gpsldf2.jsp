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
	<title>Galco Parametric Search - Delete Product Line Code</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- Version 1.3.00
        
        Modified 4/16/2008 by DES to support 4 divisions
        
        -->
  
<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moCancel"){return "Click Cancel to return to the previous screen.";}
    if (divName == "moDelete") {return "Click Delete to remove this Product Line Code.";}
    if (divName == "moDisplayOrder"){return "The Display Order (0 - 999) sets the order of this option when displayed.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the Product Line Menu.";}
    if (divName == "moProductLineCode") {return "You can only delete a Product Line Code when there are no longer any references to it in the database.";}
    if (divName == "moProductLineDivision") {return "This is the Division assigned to this Product Line.";}
    if (divName == "moProductLineName") {return "This is the name assigned to this Product Line Code.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    var division = myForm.productLineDivisionCode.value;
    if (division == "CP") {
        myForm.productLineDivisionName.value = "Control Products";
    } else if (division == "DR") {
        myForm.productLineDivisionName.value = "Depot Repair";
    } else if (division == "FS") {
        myForm.productLineDivisionName.value = "Field Service";
    } else if (division == "ES") {
        myForm.productLineDivisionName.value = "Engineered Systems";
    } else {
        myForm.productLineDivisionName.value = "**invalid**";
    }
    myForm.B9.focus();
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

<form name="form1" action="gpsldf3.do" method="post" onsubmit="return My_Validator()">
    <p>
        <input type="hidden" name="validation" value="Error" />
        <input type="hidden" name="productLineDivisionCode" value="${productLineDivisionCode}" />
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
                    Parametric Search<br />Product Line Code Maintenance<br />
			Delete Product Line
                        
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
                    <p> You can only delete a Product Line when there are no active
                    references to the Product Line Code in the database. 
                    </p><br />
                </td>
            </tr>

            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Product Line Code:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="productLineCode" size="8" maxlength="8"
                        value="${param.code}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moProductlineCode', 50, 100)"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
   
<!-- Product Line Name  -->            
       
            <tr>
                <td align="right">
                    <span class="fixedLabel">Product Line Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="productLineName" size="36" maxlength="36"
                        value="${productLineName}"
                        onmouseover="showTip(event, 'moProductLineName', 50, 100)"
                        onmouseout="hideTip()" 
                        readonly="readonly"
                        />
                    </span>
                </td>
            </tr>

<!-- Product Line Division -->

<tr>
                <td align="right">
                    <span class="fixedLabel">Product Line Division:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="productLineDivisionName" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moProductLineDivision', 50, 100)"
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
            <input type="button" value="&nbsp;&nbsp;&nbsp;Cancel&nbsp;&nbsp;&nbsp;" name="B9" onclick="Javascript: window.location='gpsldf1.do'; " 
	onmouseover="showTip(event, 'moCancel')" 
        onmouseout="hideTip()"
	/>
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpslf.jsp'; " 
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