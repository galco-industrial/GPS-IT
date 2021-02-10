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
	<title>Galco Parametric Search - Add Data Test</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
                
        <!-- gpsdaf3.jsp

        Modification History
        
        version 1.3.00
        
        09/10/2007      DES     Modified to use Ajax to obtain 
                                line/family/subfamily codes
        09/10/2007      DES     Add common.js and
                                gpscommon.js support
        
        04/25/2008      DES     support 4 divisions
        
                
        -->

    </head>
    <body>

    <h1>JSP Page</h1>

    <p>
        <c:forEach var="item" items="${dataMap}">
            ${item} <br />
        </c:forEach>
    </p>
    </body>
</html>
