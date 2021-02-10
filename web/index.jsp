<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page language="java" import="java.util.*" session="true"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>--%>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
          "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
	<title>Galco Parametric Search - Main Menu</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
<!--

	GPS Main Menu

	Modification History

	06/02/06	DES	Begin Development

-->

<!--	*************************************************************
	*		My JavaScript Functions and junk	      	*
	*************************************************************  -->

<script language="JavaScript" type="text/javascript">
<!--


function checkAuditUserID()
{
	// Check User ID

 	// squish spaces 
	var myForm = document.form1;
	var work = myForm.auditUserID.value;
	work = deleteTrailingSpaces(deleteLeadingSpaces(reduceSpaces(work)));
	work = work.toUpperCase();
	myForm.auditUserID.value = work;
	if (work.length > 0)
	{
		if (checkCharSet(work,ucLetters + numerics) == false 	||  work.length != 4) 
		{
			alert ("Please enter a valid User ID.");
			myForm.auditUserID.focus();
			return;
		}
	}
}


function getMessage(divName) {
	if (divName == "moDatabase"){return "Click Database to perform Parametric Database maintenance.";}
	if (divName == "moExit"){return "Click Exit to exit the system.";}
	if (divName == "moFamilies"){return "Click Families to perform Family Code Maintenance.";}
        if (divName == "moIndexAlias"){return "Click Families to perform Index Alias Maintenance.";}
        if (divName == "moMfgAlias"){return "Click Manufacturer Aliases to perform Manufacturer Alias Maintenance.";}
        if (divName == "moProductLines"){return "Click Product Lines to perform Product Line Code Maintenance.";}
        if (divName == "moSubfamilies"){return "Click Subfamilies to perform Subfamily Code Maintenance.";}
        if (divName == "moReports"){return "Click Reports to generate Management Reports.";}
	if (divName == "moRules"){return "Click Rules to perform Data Entry & Search Rules Maintenance.";}
	if (divName == "moSearch"){return "Click Search to perform a Parametric Search.";}
	if (divName == "moSelectBoxes"){return "Click Select Boxes to perform Select Box Maintenance.";}
        if (divName == "moSelectBoxOptions"){return "Click Select Box Options to perform Select Box Options Maintenance.";}
	if (divName == "moUsers"){return "Click Users to create User Accounts and assign Roles.";}
        if (divName == "moUnits"){return "Click Units to manage Display Units and Base Units.";}
        if (divName == "x"){return "x";}
	return "";
}

function getSelectedRadioValue(radioGroup)
{
	var selectedRadioValue = "";
	var radioIndex;
	for (radioIndex = 0; radioIndex < radioGroup.length; radioIndex++)
	{
		if (radioGroup[radioIndex].checked)
		{
			selectedRadioValue = radioGroup[radioIndex].value;
			break;
		}
	}
	return selectedRadioValue;
}

function hideTip(divName)
{
	var myForm = document.form1;
	window.defaultStatus = "";
	if (myForm.enableToolTips.checked == true)
	{
		var oDiv = document.getElementById(divName);
		oDiv.style.visibility = "hidden";
		globalDivName = "";
	}
}

function launch(file,name,winwidth,winheight)
{
	var string = "width=" + winwidth
				+ ",height=" + winheight
				+ "toolbar=yes,directories=yes,menubar=yes,resizable=yes,dependent=no";
	var hwnd = window.open(file,name,string);
	if (navigator.appName == "Netscape")
	{
		hwnd.focus();
	}
}




function setDefaults()
{
	var myForm = document.form1;
	window.defaultStatus = "";


}




function showTip(oEvent, divName, x, y)
{
	if (!x) { x = 5; }
	if (!y) { y = 5; }
	var myForm = document.form1;
	if (myForm.enableToolTips.checked == true)
	{
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

function My_Validator()
{
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

<%
	// Check Log In and  Initialize Session variables

        
%>
	
<script language="JavaScript" type="text/javascript">
<!--
	// Define some globals here
	var ucLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var lcLetters = "abcdefghijklmnopqrstuvwxyz";
	var numerics = "0123456789";
	var spaces = " ";
	globalDivName = "";

	
//-->	
</script>


<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">


<div class="toolTip" id="moDatabase">
	<div class="toolTipHeader" >Tip</div>
	Click Database to perform Parametric Database maintenance.
</div>

<div class="toolTip" id="moExit">
	<div class="toolTipHeader" >Tip</div>
	Click Exit to exit the system.
</div>

<div class="toolTip" id="moProductLines">
	<div class="toolTipHeader" >Tip</div>
	Click Product Lines to perform Product Line Code Maintenance.
</div>


<div class="toolTip" id="moFamilies">
	<div class="toolTipHeader" >Tip</div>
	Click Families to perform Family Code Maintenance.
</div>

<div class="toolTip" id="moSubfamilies">
	<div class="toolTipHeader" >Tip</div>
	Click Families to perform Subfamily Code Maintenance.
</div>

<div class="toolTip" id="moIndexAlias">
	<div class="toolTipHeader" >Tip</div>
	Click Index Alias to perform Index Alias Maintenance.
</div>
    
<div class="toolTip" id="moMfgAlias">
	<div class="toolTipHeader" >Tip</div>
	Click Manufacturer Alias to perform Index Manufacturer Maintenance.
</div>
  
<div class="toolTip" id="moReports">
	<div class="toolTipHeader" >Tip</div>
	Click Reports to generate Management Reports.
</div>

<div class="toolTip" id="moRules">
	<div class="toolTipHeader" >Tip</div>
	Click Rules to perform Data Entry & Search Rules Maintenance.
</div>

<div class="toolTip" id="moSearch">
	<div class="toolTipHeader" >Tip</div>
	Click Search to perform a Parametric Search.
</div>

<div class="toolTip" id="moSelectBoxes">
	<div class="toolTipHeader" >Tip</div>
	Click Select Boxes to perform Select Box Maintenance.
</div>

<div class="toolTip" id="moSelectBoxOptions">
	<div class="toolTipHeader" >Tip</div>
	Click Select Boxes to perform Select Box Options Maintenance.
</div>

<div class="toolTip" id="moUsers">
	<div class="toolTipHeader" >Tip</div>
	Click Users to create User Accounts and assign Roles.
</div>


<div class="toolTip" id="moUnits">
	<div class="toolTipHeader" >Tip</div>
	Click Units to manage Display Units and Base Units.
</div>

<form method="post" action="gpsxxx.do" onsubmit="return My_Validator()" name="form1">
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
        Parametric Search<br />
			Main Menu
	</h2>
    </td>
  </tr>
  <tr>
    <td>
      &nbsp;
    </td>
    <td>
      
    </td>
  </tr>




<!--     Continue      -->

      <tr>
	<td rowspan="13">
		&nbsp;
	</td>
        <td>
          <center>
            <input type="button" value="Product Lines" name="lines" onclick="Javascript: window.location='gpslf.jsp' " 
	onmouseover="showTip(event, 'moProductLines')" 
        onmouseout="hideTip('moProductLines')"
	/>
          </center>
        </td>
	</tr>
        <tr>
        <td>
          <center>
            <input type="button" value="&nbsp;&nbsp;&nbsp;Families&nbsp;&nbsp;&nbsp;&nbsp;" name="families" onclick="Javascript: window.location='gpsff.jsp' " 
	onmouseover="showTip(event, 'moFamilies')" 
        onmouseout="hideTip('moFamilies')"
	/>
          </center>
        </td>
	</tr>
        
        <tr>
        <td>
          <center>
            <input type="button" value="&nbsp;Subfamilies&nbsp;" name="subfamilies" onclick="Javascript: window.location='gpsbf.jsp' " 
	onmouseover="showTip(event, 'moSubfamilies')" 
        onmouseout="hideTip('moSubfamilies')"
	/>
          </center>
        </td>
	</tr>
        
        <tr>
        <td>
          <center>
            <input type="button" value="&nbsp;Index Aliases&nbsp;" name="indexAlias" onclick="Javascript: window.location='gpsif.jsp' " 
	onmouseover="showTip(event, 'moIndexAlias')" 
        onmouseout="hideTip('moIndexAlias')"
	/>
          </center>
        </td>
	</tr>
        
        
        <tr>
        <td>
          <center>
            <input type="button" value="&nbsp;Manufacturer Aliases&nbsp;" name="mfgAlias" onclick="Javascript: window.location='gpsmf.jsp' " 
	onmouseover="showTip(event, 'moMfgAlias')" 
        onmouseout="hideTip('moMfgAlias')"
	/>
          </center>
        </td>
	</tr>

        
    <tr>
        <td>
          <center>
            <input type="button" value="Select Boxes" name="selectBoxes" onclick="Javascript: window.location='gpssf.jsp' " 
	onmouseover="showTip(event, 'moSelectBoxes')" 
        onmouseout="hideTip('moSelectBoxes')"
	/>
          </center>
        </td>
	</tr>
        
            <tr>
        <td>
          <center>
            <input type="button" value="SBox Options" name="selectBoxOptions" onclick="Javascript: window.location='gpsof.jsp' " 
	onmouseover="showTip(event, 'moSelectBoxOptions')" 
        onmouseout="hideTip('moSelectBoxOptions')"
	/>
          </center>
        </td>
	</tr>
        
            <tr>
        <td>
          <center>
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Units&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" name="units" onclick="Javascript: window.location='gpsuf.jsp' " 
	onmouseover="showTip(event, 'moUnits')" 
        onmouseout="hideTip('moUnits')"
	/>
          </center>
        </td>
	</tr>

    <tr>
        <td>
          <center>
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Rules&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" name="rules" onclick="Javascript: window.location='gpsrf.jsp' " 
	onmouseover="showTip(event, 'moRules')" 
        onmouseout="hideTip('moRules')"
	/>
          </center>
        </td>
	</tr><tr>
        <td>
          <center>
            <input type="button" value="&nbsp;&nbsp;&nbsp;Database&nbsp;&nbsp;&nbsp;" name="database" onclick="Javascript: window.location='gpsdf.jsp' " 
	onmouseover="showTip(event, 'moDatabase')" 
        onmouseout="hideTip('moDatabase')"
	/>
          </center>
        </td>
	</tr>
<!--
        <tr>
        <td>
          <center>
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Search&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" name="search" onclick="Javascript: window.location='gpssf.jsp' " 
	onmouseover="showTip(event, 'moSearch')" 
        onmouseout="hideTip('moSearch')"
	/>
          </center>
        </td>
	</tr>

-->

        <tr>
        <td>
          <center>
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Reports&nbsp;&nbsp;&nbsp;&nbsp;" name="reports" onclick="Javascript: window.location='gpsmf.jsp' " 
	onmouseover="showTip(event, 'moReports')" 
        onmouseout="hideTip('moReports')"
	/>
          </center>
        </td>
	</tr><tr>
        <td>
          <center>
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Users&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" name="users" onclick="Javascript: window.location='gpsuf.jsp' " 
	onmouseover="showTip(event, 'moUsers')" 
        onmouseout="hideTip('moUsers')"
	/>
          </center>
        </td>
	</tr><tr>
        <td>
          <center>
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" name="Exit" onclick="Javascript: window.close() " 
	onmouseover="showTip(event, 'moExit')" 
        onmouseout="hideTip('moExit')"
	/>
          </center>
        </td>
      </tr>
    </table>
<br /><br />
  <p>
    <img src="http://www.w3.org/Icons/valid-xhtml10"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
  </p>
  </form>
</div>
</body>
</html>
