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
	<title>Galco Parametric Search - Modify Rule Set Part 3L</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
                
        <jsp:useBean id="sRuleSet" class="gps.util.GPSrules" scope="session"/>
        
        <!-- gpsrmf4l.jsp

        Modification History
        
        version 1.5.00
     
        04/23/2008      DES     Modified to support 4 Divisions
        
        -->

<!--	*************************************************************
	*		My JavaScript Functions and junk	      	*
	*************************************************************  -->

<script language="JavaScript" type="text/javascript">
<!--

function checkSearchMax() {
    var myForm = document.form1;
    var work = myForm.searchMax.value;
    work = deleteSpaces(work);
    myForm.searchMax.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value between 0 and 100.");
            myForm.searchMax.focus();
            return;
	}
    	if (parseInt(work) > 100) {
            alert ("Please enter a valid numeric value between 0 and 100.");
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
        if (checkCharSet(work,numerics) == false) {
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
    if (divName == "header"){return "You cannot change the Rule Scope, Family/Subfamily name, Data Type, or the Field Number.";}
    if (divName == "moContinue"){return "Click continue to finish Modifying this rule.";}
    if (divName == "moDataEntryDflt"){return "Choose a default setting for Data Entry of this logical parameter field.";}
    if (divName == "moExit"){return "Click Exit to abandon this rule and return to the previous Menu.";}
    if (divName == "moSearchDefault"){return "Choose a default setting for conducting a logical Search on this field.";}
   if (divName == "moStartOver"){return "Click Start Over to abandon this rule and start from the beginning.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    window.defaultStatus = "These rules affect logical Parametric Values.";
    myForm.defaultValue[0].focus();
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
        var junk = "";
//-->	
</script>

<div style="position: absolute; left: 10px; top: 10px; width: 700px; " >


<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form method="post" action="gpsrmf5.do" onsubmit="return My_Validator()" name="form1">
<p>
  <input type="hidden" name="validation" value="Error" />
  <input type="hidden" name="status" value="${status}" />
</p>

<table border="0" width="100%">

<!-- Logo and Heading  -->

  <tr>
    <td align="center" colspan="2">
	<h2>
        Parametric Search Rules Maintenance - Modify Rule Set - Part 3L
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

<div  class="masthead" 
      onmouseover="showTip(event, 'header')" 
      onmouseout="hideTip()" >

  <table border="1" width="98%"  align="center" >
  <tr><td><table border="0" width="100%">
                  
<!-- Product Line -->

    <tr>
        <td colspan="4" align="center">
            <span class="headerLabel">
                Product Line:&nbsp;&nbsp;
            </span>
            <span class="headerData">
                <jsp:getProperty name="sRuleSet" property="productLineName" />
            </span>
        </td>
    </tr>          
          
<!--  Family Description  -->

    <tr>
      <td align="right" width="25%"><span class="headerLabel">
        Family:&nbsp;
      </span></td>
      <td align="left" width="25%"><span class="headerData">

          <jsp:getProperty name="sRuleSet" property="familyName" />

      </span></td>

<!--  Subfamily Description  -->

      <td align="right" width="25%"><span class="headerLabel">
        Subfamily:&nbsp;
      </span></td>
        <td align="left" width="25%"><span class="headerData">

          <jsp:getProperty name="sRuleSet" property="subfamilyName" />

        </span></td>
      </tr>

<!--  Scope  -->

    <tr>
      <td align="right" ><span class="headerLabel">
        Scope:&nbsp;
      </span></td>
      <td align="left" ><span class="headerData">
<script language="JavaScript" type="text/javascript">
<!--
                    junk = "<jsp:getProperty name="sRuleSet" property="ruleScope" />";
                    if (junk == "G") {
                        document.write("Global");
                    }
                    if (junk == "L") {
                        document.write("Local");
                    }
//-->
</script>
      </span></td>

<!--  Sequence Number  -->

        <td align="right"><span class="headerLabel">
          Field No.:&nbsp;
        </span></td>
        <td align="left"><span class="headerData">
       <jsp:getProperty name="sRuleSet" property="seqNum" />
        </span></td>
      </tr>

<!--  Parm Name  -->

      <tr>
        <td align="right"><span class="headerLabel">
          Field Name:&nbsp;
        </span></td>
        <td align="left"><span class="headerData">
        <jsp:getProperty name="sRuleSet" property="parmName" />
        </span></td>

<!--  Data Type -->

        <td align="right"><span class="headerLabel">
          Data Type:&nbsp;
        </span></td>
        <td align="left"><span class="headerData">
<script language="JavaScript" type="text/javascript">
<!--
                    junk = "<jsp:getProperty name="sRuleSet" property="dataType" />";
                    if (junk == "N") {
                        document.write("N - Numeric");
                    }
                    if (junk == "S") {
                        document.write("S - String");
                    }
                    if (junk == "L") {
                        document.write("L - Logical");
                    }
                    if (junk == "D") {
                        document.write("D - Date");
                    }
 //-->
</script>  

        </span></td>
      </tr>
<script language="JavaScript" type="text/javascript">
<!--
                    if (document.form1.status.value != "A") {
                        document.write("<tr><td align=\"center\" colspan=\"4\"><font color=\"&CC0000\"><b>");
                        document.write("This parm field is currently INACTIVE.");               
                        document.write("</b></font></td></tr>");
                    }
 
//-->
</script> 
  </table></td></tr>
 </table>

</div>

  </td></tr>
</table>

<table width="100%" border="0">

<!--  DE Default setting  -->

      <tr>
        <td align="right" width="25%">
          <span class="label">Data Entry Default:&nbsp;</span>
        </td>
        <td align="left" width="75%"><span class="dataField">
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="defaultValue" value="U"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moDataEntryDflt'";
        inputObject += ',100,25)" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="defaultValueRaw" />';
        if (junk == "" || junk == "U") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

          Undefined&nbsp;&nbsp;&nbsp;&nbsp;
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="defaultValue" value="Y"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moDataEntryDflt'";
        inputObject += ',100,25)" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="defaultValueRaw" />';
        if (junk == "Y") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 
            Yes&nbsp;&nbsp;&nbsp;&nbsp;
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="defaultValue" value="N"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moDataEntryDflt'";
        inputObject += ',100,25)" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="defaultValueRaw" />';
        if (junk == "N") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 
            No
        </span></td>
      </tr>

<!--  Search Checkbox default  -->

       <tr>
        <td align="right" width="25%">
          <span class="label">Search Default:&nbsp;</span>
        </td>
        <td align="left" width="75%"><span class="dataField">
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="searchLogicalDefault" value="U"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moSearchDflt'";
        inputObject += ',100,25)" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="searchLogicalDefault" />';
        if (junk == "" || junk == "U") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 

          Undefined&nbsp;&nbsp;&nbsp;&nbsp;
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="searchLogicalDefault" value="Y"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moSearchDflt'";
        inputObject += ',100,25)" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="searchLogicalDefault" />';
        if (junk == "Y") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script> 
            Yes&nbsp;&nbsp;&nbsp;&nbsp;

<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="searchLogicalDefault" value="N"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moSearchDflt'";
        inputObject += ',100,25)" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="searchLogicalDefault" />';
        if (junk == "N") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
        document.close();
//-->
</script> 
            No
        </span></td>
      </tr>

<!--  Continue or Clear  -->

      <tr>
        <td colspan="2">
          <center>
            <input type="submit" 
                value="Continue" 
                name="3L"
                onmouseover="showTip(event, 'moContinue')" 
                onmouseout="hideTip()"
	/>
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <input type="button" 
            value="Start Over" 
            name="B2" 
            onclick='JavaScript: location.href="gpsrmf1.do";' 
            onmouseover="showTip(event, 'moStartOver')" 
            onmouseout="hideTip()"
	/>
           &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" 
                value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" 
                name="B3" 
                onclick="Javascript: window.location='gpsrf.jsp'; " 
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
