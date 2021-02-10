<%-- 
    Document   : gpsmf
    Created on : Sep 26, 2013, 9:26:53 AM
    Author     : dunlop
--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page language="java" import="java.util.*" session="true"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
          "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
	<title>Galco Parametric Search - Manufacturer Alias Menu</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        
<!--

	GPS Manufacturer Menu

	Modification History

-->



<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
	if (divName == "moCreate") {return "Click Create to add a new Index Alias.";}
	if (divName == "moModify") {return "Click Modify to make changes to an existing Index Alias.";}
	if (divName == "moDelete") {return "Click Delete to remove an existing Index Alias.";}
        if (divName == "moExit") {return "Click Exit to return to the main menu.";}
	if (divName == "moList") {return "Click List to display all Index Aliases.";}
	if (divName == "x") {return "x";}
	return "";
}

function setDefaults() {
	var myForm = document.form1;
	window.defaultStatus = "";
}

//	*************************************************************
//	*			Form Validation Pre-Submit	    *
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
	
//-->	
</script>


<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form method="post" action="gpsmf.do" onsubmit="return My_Validator()" name="form1">
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
        Parametric Search<br />Manufacturer Alias Maintenance			
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
	<td rowspan="16">
		&nbsp;
	</td>
        <td>
          <center>
            <input type="button" value="Create Manufacturer Alias" name="create" onclick="Javascript: window.location='gpsmcf1.do' " 
                onmouseover="showTip(event, 'moCreate')" 
                onmouseout="hideTip()"
            />
          </center>
        </td></tr>

       <tr><td>
          <center>
            <input type="button" value="Modify Manufacturer Alias" name="modify" onclick="Javascript: window.location='gpsmmf1.do' " 
                onmouseover="showTip(event, 'moModify')" 
                onmouseout="hideTip()"
            />
          </center>
        </td></tr>
        
        <tr><td>
          <center>
            <input type="button" value="Delete Manufacturer Alias" name="delete" onclick="Javascript: window.location='gpsmdf1.do' " 
                onmouseover="showTip(event, 'moDelete')" 
                onmouseout="hideTip()"
            />
          </center>
        </td></tr>
        
        <tr><td>
          <center>
            <input type="button" value="&nbsp;List Manufacturer Aliases&nbsp;" name="list" onclick="Javascript: window.location='gpsmlf1.do' " 
                onmouseover="showTip(event, 'moList')" 
                onmouseout="hideTip()"
            />
          </center>
        </td></tr> 
       
        <tr><td>
          <center>
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" name="Exit" onclick="Javascript: window.location='index.jsp' " 
                onmouseover="showTip(event, 'moExit')" 
                onmouseout="hideTip()"
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
