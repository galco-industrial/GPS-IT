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
      
        <!-- gpsrdf3.jsp

        Modification History
        
        version 1.3.00
        

        04/23/2008      DES     Modified to support 4 Divisions
        
        -->
        

<!--	*************************************************************
	*		My JavaScript Functions and junk	      	*
	*************************************************************  -->

<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moExit"){return "Click Exit to return to the Previous Menu.";}
    if (divName == "moDelete"){return "Click to permanently DELETE this rule.";}
    return "";
}

function hideTip(divName) {
	var myForm = document.form1;
	window.defaultStatus = "";
	if (myForm.enableToolTips.checked == true)
	{
		var oDiv = document.getElementById(divName);
		oDiv.style.visibility = "hidden";
		globalDivName = "";
	}
}

function setDefaults() {
	var myForm = document.form1;
	window.defaultStatus = "";
        if (kount != 0 ) {
            alert ("Found existing parametric data for this field. You cannot delete this Rule Set."); 
            myForm.B1.disabled = true;
        }
	myForm.B9.focus();
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


//	*************************************************************
//	*			Form Validation Pre-Submit			*
//	*************************************************************

function My_Validator() {
	var work;
	var work2;
	var myForm = document.form1;

	// All Validation tests are complete

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
        var rules = new Array();
        var iR = 0;
        var kount = ${references};

        <c:forEach var="item" items="${ruleData}">
            rules[iR++] = new Array(${item});
        </c:forEach>


//-->	
</script>


<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">


<div class="toolTip" id="moExit">
	<div class="toolTipHeader">Tip</div>
	Click Exit to return to the Rules Menu.
</div>

<div class="toolTip" id="moDelete">
	<div class="toolTipHeader">Tip</div>
	Click to permanently DELETE this Rule Set.
</div>


<form method="post" action="gpsrdf4.do" onsubmit="return My_Validator()" name="form1">
<p>
  <input type="hidden" name="validation" value="Error" />
  <input type="hidden" name="familyCode" value="${familyCode}" />
  <input type="hidden" name="familyName" value="${familyName}" />
  <input type="hidden" name="subfamilyCode" value="${subfamilyCode}" />
  <input type="hidden" name="productLineName" value="${productLineName}" />
  <input type="hidden" name="ruleScope" value="${ruleScope}" />
  <input type="hidden" name="seqNum" value="${seqNum}" />
  
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
        Parametric Search Rules Maintenance<br />
			Delete Rule Set
	</h2>
        <script language="JavaScript" type="text/javascript">
<!--
        if (kount != 0 ) {
            document.write("<h3>Existing parametric data for this field was found.</h3>"); 
        }

//-->
        </script>

    </td>
  </tr>
  
</table>

  <table border="1" width="100%">
      <script language="JavaScript" type="text/javascript">
<!--
          for (var i = 0; i < iR; i++){
              document.write("<tr><td align=\"right\" width=\"30%\"><span class=\"requiredLabel\">");
              document.write(rules[i][0]);
              document.write("</span></td><td align=\"left\" width=\"70%\"><span class=\"dataField\">");
              document.write(rules[i][1]);
              document.write("</span></td></tr>");
          }
//-->
      </script>
     

<!--     Continue      -->

      <tr>
        <td colspan="2">
          <center>
            <br />
            <input type="submit" value="Delete" name="B1" 
                onmouseover="showTip(event, 'moDelete')" 
                onmouseout="hideTip('moDelete')"
            />
      
                     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
             <input type="button" value="Cancel" name="B9" onclick="Javascript: history.back(); " 
	onmouseover="showTip(event, 'moCancel')" 
        onmouseout="hideTip('moCancel')"
	/>
           &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsrf.jsp'; " 
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
