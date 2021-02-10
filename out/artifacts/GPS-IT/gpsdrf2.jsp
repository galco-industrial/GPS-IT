<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
	<title>Galco Parametric Search - Part Number Data</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
       
</head>
<body>

<script language="JavaScript" type="text/javascript">
<!--
    
        var j;
        var cell = new Array();

        <c:forEach var="item" items="${generatedRows}">
            ${item}    
        </c:forEach>

-->    
</script> 

     <center>
          
    <h2>Search Results<br />
    Family Name: ${sessionScope.sbFamilyName}<br />
    </h2>    
    </center>
    
    <table align="center" border="1">

<script language="JavaScript" type="text/javascript">
<!--
    var columns = 2;
    if (cell [0] [2] != "" ) { columns = 3;}
    if (cell [0] [3] != "" ) { columns = 4;}
    if (cell [0] [4] != "" ) { columns = 5;}
    if (cell [0] [5] != "" ) { columns = 6;}
    
    for (var k = 0; k < j; k++) {
        document.write("<tr>");
        document.write("<td><b>" + cell [k] [0] + "&nbsp;</b></td>");
        for (var m = 1; m < columns; m++) {
            document.write("<td>" + cell [k] [m] + "&nbsp;</td>");
        }
        document.write("</tr>");
    }
    document.write ("</table>");
    if ("${close}" == "1") {
        document.write("<p><center><a href=\"javascript:window.close()\">Close</a></center></p>");
    } else {
        document.write("<p><center><a href=\"javascript:history.back()\">Back</a></center></p>");
    }
    document.close();
-->    
</script>             
</body>
</html>
