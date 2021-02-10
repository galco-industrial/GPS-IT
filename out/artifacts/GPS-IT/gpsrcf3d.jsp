<%@page contentType="text/html"%>
<%@page language="java" import="java.util.*" session="true"%>
<%@page pageEncoding="UTF-8"%>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
          "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
	<title>Galco Parametric Search - Create Rule Set Part 3D</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
<!--

	GPS Add Rule - Part 3d

	Version 1.3.00
        
        Modification History

	05/16/06	DES	Begin Development
        09/07/2007      DES     Add common.js and
                                gpscommon.js support
        04/23/2008      DES     Modified to support 4 Divisions
        
-->

<!--	*************************************************************
	*		My JavaScript Functions and junk	      	*
	*************************************************************  -->

<script language="JavaScript" type="text/javascript">
<!--

function checkDefaultValue() {
	alert ("Validation code gets executed here.");
}

function checkMaxDate() {
	alert ("Validation code gets executed here.");
}

function checkMaxTime() {
	alert ("Validation code gets executed here.");
}

function checkMinDate() {
	alert ("Validation code gets executed here.");
}

function checkMinTime() {
	alert ("Validation code gets executed here.");
}

function checkSearchMax() {
    var myForm = document.form1;
    var work = myForm.searchMax.value;
    work = deleteSpaces(work);
    myForm.searchMax.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value between 0 and 500.");
            myForm.searchMax.focus();
            return;
        }
	if (parseInt(work) > 500) {
            alert ("Please enter a valid numeric value between 0 and 500.");
            myForm.searchMax.focus();
            return;
	}
    }
}

function checkSearchMin() {
    var myForm = document.form1;
    var work = myForm.searchMin.value;
    work = deleteSpaces(work);
    myForm.searchMin.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value between 0 and 100.");
            myForm.searchMin.focus();
            return;
        }
        if (parseInt(work) > 100) {
            alert ("Please enter a valid numeric value between 0 and 100.");
            myForm.searchMin.focus();
            return;
        }
    }
}

function checkSearchWeight() {
    var myForm = document.form1;
    var work = myForm.searchWeight.value;
    work = deleteSpaces(work);
    myForm.searchWeight.value = work;
    if (work.length > 0) {
    	if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value between 0 and 100.");
            myForm.searchWeight.focus();
            return;
	}
	if (parseInt(work) > 100) {
            alert ("Please enter a valid numeric value between 0 and 100.");
            myForm.searchWeight.focus();
            return;
	}
    }
}

function getMessage(divName) {
    if (divName == "header"){return "To change the Rule Scope, Family/Subfamily name, Data Type, or the Field Number, you must start over.";}
    if (divName == "moContinue"){return "Click continue to finish adding this rule.";}
    if (divName == "moDateFormat"){return "Select a date format from the list of options.";}
    if (divName == "moDefaultValue"){return "Enter an optional default date for data entry.";}
    if (divName == "moExit"){return "Click Exit to abandon this rule and return to the Rules Menu.";}
    if (divName == "moMaxDate"){return "If applicable, enter an ending date here. To prohibit a future date, enter the word NOW.";}
    if (divName == "moMaxTime"){return "If applicable, enter an ending time here.";}
    if (divName == "moMinDate"){return "If applicable, enter a beginning date here. To require some future date, enter the word NOW.";}
    if (divName == "moMinTime"){return "If applicable, enter a starting time here.";}
    if (divName == "moSearchMax"){return "Enter a percentage between 0 and 500 used to calculate the maximum value acceptable for a match.";}
    if (divName == "moSearchMin"){return "Enter a percentage between 0 and 100 used to calculate the minimum value acceptable for a match.";}
    if (divName == "moSearchWeight"){return "Enter a relative weight for this search field. Relative weights should add up to 100.";}
    if (divName == "moStartOver"){return "Click Start Over to abandon this rule and start from the beginning.";}
    if (divName == "moTimeFormat"){return "Select a time format from the list of options.";}
    if (divName == "x"){return "x";}
    return "";
}

function setDefaults() {
	var myForm = document.form1;
	window.defaultStatus = "These rules affect Date and Time Parametric Values.";
	myForm.dateFormat.focus();
}

//	*************************************************************
//	*			Form Validation Pre-Submit			*
//	*************************************************************

function My_Validator() {
	var work;
	var work2;
	var myForm = document.form1;

	// Check for a Date or Time type selected

	var xDate = myForm.elements["dateFormat"];
	var xTime = myForm.elements["timeFormat"];
	if (xDate[0].selected == true
		&& xTime[0].selected == true) {
		alert("Please select a Date and/or Time format.");
		myForm.dateFormat.focus();
		return false;
	}

	// check max date is not < min date

	alert ("Make sure format was selected if there is a max date or a min date");

	alert ("check max date is not < min date");

	// check max time is not < min time

	alert ("Make sure format was selected if there is a max time or a min time");

	alert ("check max time is not < min time");

	// if a Default value is specified, check it's range

	alert ("if a Default value is specified, check it's value against format and ranges.");

	// Check searchMin

	work = myForm.searchMin.value;
	if (work.length == 0) {
		alert("Please enter a Minimum percent for this Rule.");
		myForm.searchMin.focus();
		return false;
	}

	// Check searchMax

	work = myForm.searchMax.value;
	if (work.length == 0) {
		alert("Please enter a Maximum percent for this Rule.");
		myForm.searchMax.focus();
		return false;
	}

	// Check search Relative Weight

	work = myForm.searchWeight.value;
	if (work.length == 0) {
		alert("Please enter a Search Weight for this Rule.");
		myForm.searchWeight.focus();
		return false;
	}
	
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


//-->	
</script>

<div style="position: absolute; left: 10px; top: 10px; width: 700px; " >

<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form method="post" action="gpsrcf4.do" onsubmit="return My_Validator()" name="form1">
<p>
  <input type="hidden" name="validation" value="Error" />
</p>

<table border="0" width="100%">

<!-- Logo and Heading  -->

  <tr>
    <td align="center" colspan="2">
	<h2>
        Parametric Search Rules Maintenance - Create Rule Set - Part 3D
	</h2>
    </td>
  </tr>
  <tr>
    <td rowspan="3" align="center" width="20%">
      <img src="gl_25.gif" alt="Galco logo" />
      <br />

	<div class="toolTipSwitch">
		<input type="checkbox"  
<%
        String tip = (String) session.getAttribute("enableToolTips");
        if (tip != null && tip.equals("checked")) {
            out.println(" checked=\"checked\" ");
        }
%> 

		name="enableToolTips"
		value="checked"
		/>
		Enable Tool Tips
	</div>
    </td>
    <td>


<div  class="masthead" onmouseover="showTip(event, 'header')" onmouseout="hideTip()" >
  <table border="1" width="98%"  align="center" >
      <tr><td><table border="0" width="100%">
                  
<!-- Product Line -->

    <tr>
        <td colspan="4" align="center">
            <span class="headerLabel">
                Product Line:&nbsp;&nbsp;
            </span>
            <span class="headerData">
                ${sessionScope.productLineName}
            </span>
        </td>
    </tr>            
          
<!--  Family Description  -->

    <tr>
      <td align="right" width="25%"><span class="headerLabel">
        Family:&nbsp;
      </span></td>
      <td align="left" width="25%"><span class="headerData">

          ${sessionScope.familyName}

      </span></td>

<!--  Subfamily Description  -->

      <td align="right" width="25%"><span class="headerLabel">
        Subfamily:&nbsp;
      </span></td>
        <td align="left" width="25%"><span class="headerData">

          ${sessionScope.subfamilyName}

        </span></td>
      </tr>

<!--  Scope  -->

    <tr>
      <td align="right" ><span class="headerLabel">
        Scope:&nbsp;
      </span></td>
      <td align="left" ><span class="headerData">
 
<%
      String gwork = (String) session.getAttribute("ruleScope");
          if (gwork.equals("G")) {
              out.println("Global");
          }
          if (gwork.equals("L")) {
              out.println("Local");
          }
%>

      </span></td>

<!--  Sequence Number  -->

        <td align="right"><span class="headerLabel">
          Field No.:&nbsp;
        </span></td>
        <td align="left"><span class="headerData">

        ${sessionScope.seqNum}



        </span></td>
      </tr>

<!--  Parm Name  -->

      <tr>
        <td align="right"><span class="headerLabel">
          Field Name:&nbsp;
        </span></td>
        <td align="left"><span class="headerData">

        ${sessionScope.parmName}

        </span></td>

<!--  Data Type -->

        <td align="right"><span class="headerLabel">
          Data Type:&nbsp;
        </span></td>
        <td align="left"><span class="headerData">
<%
        gwork = (String) session.getAttribute("dataType");
        if(gwork.equals("N")) {
            out.println("N - Numeric");
        }
        if(gwork.equals("S")) {
            out.println("S - String");
        }
        if(gwork.equals("L")) {
            out.println("L - Logical");
        }
        if(gwork.equals("D")) {
            out.println("D - Date");
        }
%>

        </span></td>
      </tr>
  </table></td></tr>
 </table>
</div>
</td></tr>
</table>

<table width="100%" border="0">

<!--  Date Format  --> 

      <tr>
        <td align="right">
          <span class="requiredLabel">Date Format:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <select size="1" name="dateFormat"
	  onmouseover="showTip(event,'moDateFormat',100,20)" 
          onmouseout="hideTip()">
		<option selected="selected" value="None">None</option>
		<option value="MM/DD/YY">MM/DD/YY</option>
		<option value="MM/DD/YYYY">MM/DD/YYYY</option>
		<option value="MM/YY">MM/YY</option>
		<option value="MM/YYYY">MM/YYYY</option>
		<option value="YYYY">YYYY</option>
		</select>
        </span></td>
      </tr>

<!--  Time Format  --> 

      <tr>
        <td align="right">
          <span class="requiredLabel">Time Format:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <select size="1" name="timeFormat"
	  onmouseover="showTip(event,'moTimeFormat',100,20)" 
          onmouseout="hideTip()">
		<option selected="selected" value="None">None</option>
		<option value="HH:MM:SS">HH:MM:SS</option>
		<option value="HH:MM">HH:MM</option>
		<option value="HH:MM:SS AM">HH:MM:SS AM</option>
		<option value="HH:MM AM">HH:MM AM</option>
		</select>
        </span></td>
      </tr>

<!--  Minimum Date  --> 

      <tr>
        <td align="right">
          <span class="label">Minimum Date:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" 
            name="minDate" 
            maxlength="20"
            size="20"
            value="${sessionScope.minDate}"
            onchange="checkMinDate()"
            onmouseover="showTip(event,'moMinDate')" 
            onmouseout="hideTip()"
	  />
        </span></td>
      </tr>



<!--  Maximum Date  --> 

      <tr>
        <td align="right">
          <span class="label">Maximum Date:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" 
            name="maxDate" 
            maxlength="20"
            size="20" 
            value="${sessionScope.maxDate}"
            onchange="checkMaxDate()"
            onmouseover="showTip(event,'moMaxDate')" 
            onmouseout="hideTip()"
	  />
        </span></td>
      </tr>

<!--  Minimum Time  --> 

      <tr>
        <td align="right">
          <span class="label">Minimum Time:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" 
            name="minTime" 
            maxlength="10" 
            size="20"
            value="${sessionScope.minTime}"
            onchange="checkMinTime()"
            onmouseover="showTip(event,'moMinTime')" 
            onmouseout="hideTip()"
	  />
        </span></td>
      </tr>

<!--  Maximum Time  --> 

      <tr>
        <td align="right">
          <span class="label">Maximum Time:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" 
            name="maxTime" 
            maxlength="10" 
            size="20"
            value="${sessionScope.maxTime}"
            onchange="checkMaxTime()"
            onmouseover="showTip(event,'moMaxTime')" 
            onmouseout="hideTip()"
	  />
        </span></td>
      </tr>

<!--  Default Value  -->

      <tr>
        <td align="right">
          <span class="label">D.E. Default:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" 
            size="20" 
            maxlength="20" 
            name="defaultValue"
            value="${sessionScope.defaultValue}"
            onchange="checkDefaultValue()"
            onmouseover="showTip(event,'moDefaultValue')" 
            onmouseout="hideTip()"
	  />
        </span></td>
      </tr>

<!--  Search Match Value  -->

    <tr>
      <td align="right">
        <span class="requiredLabel">
          Search Rank:&nbsp;
        </span>
      </td>
      <td>
        <span class="dataField">
          <input type="text" 
            size="4" 
            maxlength="3" 
            name="searchMin"
            value="${sessionScope.searchMin}"
            onblur="checkSearchMin()"
            onmouseover="showTip(event, 'moSearchMin')" 
            onmouseout="hideTip()"
          /> 
          No Match&nbsp;&nbsp;&nbsp;

          <input type="text" 
            size="4" 
            maxlength="3" 
            name="searchMax"
            value="${sessionScope.searchMax}"
            onblur="checkSearchMax()"
            onmouseover="showTip(event, 'moSearchMax')" 
            onmouseout="hideTip()"
          /> 
          Match

<!--  Relative Weight  -->

          &nbsp;&nbsp;&nbsp;
          <input type="text" 
            size="3" 
            maxlength="3" 
            name="searchWeight"
            value="${sessionScope.searchWeight}"
            onblur="checkSearchWeight()"
            onmouseover="showTip(event, 'moSearchWeight')" 
            onmouseout="hideTip()"
          /> 
          Relative Wt
        </span>
      </td>
    </tr>

<!--  Continue or Clear  -->

      <tr>
        <td colspan="2">
        <center><br />
          <input type="submit" value="Continue" name="3D" 
        	onmouseover="showTip(event, 'moContinue')" 
                onmouseout="hideTip()"
	/>
               &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <input type="button" value="Start Over" name="B2" onclick='JavaScript: location.href="gpsrcf1.do";' 
                onmouseover="showTip(event, 'moStartOver')" 
                onmouseout="hideTip()"
	/>
               &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsrf.jsp'; " 
                onmouseover="showTip(event, 'moExit')" 
                onmouseout="hideTip()"
	/>
        </center>
        </td>
      </tr>

    </table>
    <br /> 
    <br />
  <p>
    <img src="http://www.w3.org/Icons/valid-xhtml10"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
  </p>
  </form>
</div>
</body>
</html>
