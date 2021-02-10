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
	<title>Galco Parametric Search - Database Menu</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
                
<!--

	GPS Database Menu

	Modification History

	06/02/06	DES	Begin Development
        09/08/16        DES     Added Abort Building Option Lists

-->

<!--	*************************************************************
	*		My JavaScript Functions and junk	      	*
	*************************************************************  -->

<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
	if (divName == "moAdd"){return "Click to Add Parametric data for an existing Part Number.";}
        if (divName == "moBuild"){return "Click to Build Option Lists for Web Landing Pages.";}
        if (divName == "moStop"){return "Click to Shut Down a currently running 'Build Option Lists' operation.";}
        if (divName == "moDelete"){return "Click to Delete Parametric data for an existing Part Number.";}
        if (divName == "moExit"){return "Click Exit to return to the main menu.";}
        if (divName == "moExport"){return "Click to create a csv worksheet containing parametric data.";}
        if (divName == "moExtract"){return "Click to create a csv worksheet containing digest info.";}
        if (divName == "moImport"){return "Click to Import parametric data from a csv worksheet.";}
        if (divName == "moKill"){return "Click to Kill a currently running Import Operation.";}
        if (divName == "moModify"){return "Click to Modify the parametric data for a Part Number.";}
        if (divName == "moPNSearch"){return "Click to find an equivalent parametric match using a Part Number.";}
	if (divName == "moPurge"){return "Click to delete parametric data for a group of part numbers in a CSV worksheet.";}
	if (divName == "moRead"){return "Click Read Rule to display an existing parametric data for a part number.";}
	if (divName == "moValSearch"){return "Click to find part numbers using parametric values.";}
       	return "";
}

function setDefaults() {
	var myForm = document.form1;
	window.defaultStatus = "";
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

	
//-->	
</script>


<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
    
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>
    
<form method="post" action="#" onsubmit="return My_Validator()" name="form1">
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
		//String tip = request.getParameter("enableToolTips");
                //session.setAttribute("enableToolTips", tip);
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
        Parametric Search Maintenance<br />
			Parametric Database Menu
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
	<td rowspan="15">
		&nbsp;
	</td>
        <td>
          <center>
            <input type="button" value="&nbsp;&nbsp;&nbsp;Add Data&nbsp;&nbsp;" name="addData" onclick="Javascript: window.location='gpsdaf1.do' " 
	onmouseover="showTip(event, 'moAdd')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
        </tr><tr>
                <td>
          <center>
            <input type="button" value="Build Option Lists&nbsp;" name="buildOptions" onclick="Javascript: window.location='gpsdbf1.do' " 
	onmouseover="showTip(event, 'moBuild')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
        </tr><tr>
                <td>
          <center>
            <input type="button" value="Stop Build Option Lists&nbsp;" name="abortBuildOptions" onclick="Javascript: window.location='gpsdsf1.do' " 
	onmouseover="showTip(event, 'moAbort')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
	</tr><tr>
                <td>
          <center>
            <input type="button" value="Delete Data&nbsp;" name="deleteData" onclick="Javascript: window.location='gpsddf1.do' " 
	onmouseover="showTip(event, 'moDelete')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
	</tr><tr>
        
        <td>
          <center>
            <input type="button" value="Export Data&nbsp;" name="exportData" onclick="Javascript: window.location='gpsdef1.do' " 
	onmouseover="showTip(event, 'moExport')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
	</tr><tr>
                
        <td>
          <center>
            <input type="button" value="Extract Data" name="extractData" onclick="Javascript: window.location='gpsdxf1.do' " 
	onmouseover="showTip(event, 'moExtract')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
	</tr>
        <tr>
                       
        <td>
          <center>
            <input type="button" value="&nbsp;Import Data&nbsp;" name="importData" onclick="Javascript: window.location='gpsdif1.do' " 
	onmouseover="showTip(event, 'moImport')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
	</tr>
        <tr>
        <td>
          <center>
            <input type="button" value="&nbsp;Kill Import&nbsp;" name="killImport" onclick="Javascript: window.location='gpsdkf1.do' " 
	onmouseover="showTip(event, 'moKill')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
	</tr>

        <tr>
        <td>
          <center>
            <input type="button" value="&nbsp;&nbsp;Modify Data&nbsp;&nbsp;&nbsp;" name="listData" onclick="Javascript: window.location='gpsdmf1.do' " 
	onmouseover="showTip(event, 'moModify')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
	</tr>
        
        <tr>
        <td>
          <center>
            <input type="button" value="PN Search&nbsp;&nbsp;" name="pnSearch" onclick="Javascript: window.location='gpsdpf1.do' " 
	onmouseover="showTip(event, 'moPNSearch')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
	</tr>
        
        <tr>
        <td>
          <center>
            <input type="button" value="&nbsp;Read Data&nbsp;&nbsp;" name="readData" onclick="Javascript: window.location='gpsdrf1.do' " 
	onmouseover="showTip(event, 'moRead')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
	</tr>

<tr>
        <td>
          <center>
            <input type="button" value="Purge Data" name="purgeData" onclick="Javascript: window.location='gpsdpf1.do' " 
	onmouseover="showTip(event, 'moPurge')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
	</tr>

       <tr>
        <td>
          <center>
            <input type="button" value="Value Search" name="valSearch" onclick="Javascript: window.location='gpsdvf1.do' " 
	onmouseover="showTip(event, 'moValSearch')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
	</tr>
     
       <tr>
        <td>
          <center>
            <input type="button" value="Web Search" name="test" onclick="Javascript: window.location='searchInit.do' " 
	onmouseover="showTip(event, 'moTest')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
	</tr>
        
        <tr>
        <td>
          <center>
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" name="Exit" onclick="Javascript: window.location='index.jsp' " 
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
