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
	<title>Galco Parametric Search - Export Data Results</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
      <!-- Modification History
        
        version 1.0.00
        
  
        
        -->

        
    </head>
    <body>

    <h1>Category:&nbsp; ${selectedCategory}</h1>

    <h1>Subcategory:&nbsp; ${selectedSubcategory}</h1>
    
    <h1>Series:&nbsp; ${selectedSeries}</h1>
    
    <h3>Output File Name:&nbsp; ${outFileName}</h3>
    
        <c:forEach var="item" items="${partNums}">
            <h2> ${item} </h2>
        </c:forEach>

<!--     Continue      -->

    <br />
    <p>
        <center>
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsdf.jsp'; " 
	    />
        </center>  
    <br />
    <br />
    
    <img src="w3cxhtml10.bmp"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
    </p>
       
    </body>
</html>
