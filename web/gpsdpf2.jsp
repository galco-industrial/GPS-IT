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
	<title>Galco Parametric Search - Delete Data</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>

<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
	if (divName == "moBuild"){return "Click to build Web Landing Page options for this Family Code.";}
        return "";
}

//-->
</script>
</head>
<body>
     
    <script language="JavaScript" type="text/javascript">
    <!--
    
    //-->    
    </script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">

<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>
  
    <form name="form1" action="#" method="post" onsubmit="return My_Validator()">
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
                    Parametric Search Database Maintenance<br />
			Purge Parametric Data
                </h2>
            </td>
        </tr>

    </table>
        
    <h1>Family:&nbsp; ${familyDescription}</h1>
    <h1>Subfamily:&nbsp; ${subfamilyDescription}</h1>
    <h3>Log File Name:&nbsp; ${logFileName}</h3>
    <h3>${message}</h3>
        
        <center>
            
        <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Build Web Landing Page Options&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsdbf1.do'; " 
	onmouseover="showTip(event, 'moBuild')" 
        onmouseout="hideTip()"
	/>    
        
        <!--
        <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsdf.jsp'; " 
	onmouseover="showTip(event, 'moExit')" 
        onmouseout="hideTip()"
	/>
        -->
        </center>
        
    </form>
</div>
</body>
</html>
