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
	<title>Galco Parametric Search - Create Rule Set Part 6</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <jsp:useBean id="sRuleSet" class="gps.util.GPSrules" scope="session"/>
        
        
        <!-- gpsrmf7.jsp

        Modification History
        
        version 1.5.00
     
        04/23/2008      DES     Modified to support 4 Divisions
        
        -->

<!--	*************************************************************
	*		My JavaScript Functions and junk	      	*
	*************************************************************  -->

<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moExit"){return "Click to return to the previous menu.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
}

//	*************************************************************
//	*		Form Validation Pre-Submit                  *
//	*************************************************************

function My_Validator() {
	var myForm = document.form1;
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

<form method="post" action="#" onsubmit="return My_Validator()" name="form1">
<p>
    <input type="hidden" name="validation" value="Bad" />
    <input type="hidden" name="status" value="${status}" />
</p>

<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
    
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<table border="0" width="100%">

<!-- Logo and Heading  -->

  <tr>
    <td align="center" colspan="2">
	<h2>
        Parametric Search Rules Maintenance - Modify Rule Set - Part 6
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
                    document.close();
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

<!--  Continue or Clear  -->

<br />

<center>
    <h2>
        ${message}
    </h2>
</center>

<p>
<center>
        <input type="button" 
            value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" 
            name="B3" 
            onclick="Javascript: window.location='gpsrf.jsp'; " 
            onmouseover="showTip(event, 'moExit')" 
            onmouseout="hideTip()"
	/>
</center>
</p>
<br />
</form>
</div>
</body>
</html>
