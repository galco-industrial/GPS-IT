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
	<title>Galco Parametric Search - Modify Product Line Code</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>

        <!-- Version 1.3.00
        
        Modified 4/16/2008 by DES to support 4 divisions
        
        -->
        
<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the Product Line Menu.";}
    if (divName == "moClick") {return "Click this button to modify this item.";}
   return "";
}

//-->    
</script>        

</head>
<body>

    <script language="JavaScript" type="text/javascript">
    <!--
    
        var lineCodes = new Array();
        var iL = 0;
        
        <c:forEach var="item" items="${lines}">
            lineCodes[iL++] = new Array(${item});
        </c:forEach>
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
    
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>


<form name="form1" action="#" method="post" onsubmit="">

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
                Product Line Code Maintenance<br />
			Modify Product Line
                </h2>
            </td>
        </tr>
         <tr>
                <td colspan="2"><h3 class="red">
                    <br />${statusMessage}
                </h3>
                </td>
         </tr>
        
    </table>
    <br /><br /><br />
    

        
    <table border="1" align="center" width="100%">

            <tr>
                <td width="5%">
                    <span class="requiredLabel">Click to<br />Modify</span>
                </td>
                <td width="15%">
                    <span class="requiredLabel">Product<br />Line<br />Code</span>
                </td>
                <td width="5%">
                    <span class="requiredLabel">Display<br />Order</span>
                </td>
                <td width="60%">
                    <span class="requiredLabel">Product Line Name</span>
                </td>
                <td width="15%">
                    <span class="requiredLabel">Product Line Division</span>
                </td>
            </tr>
            <script language="JavaScript" type="text/javascript">
<!--
                for (var i = 0; i < lineCodes.length; i++){
                    document.write("<tr><td><center><input type=\"button\" value=\"X\"");
                    document.write(" name=\"B4\" onclick=\"Javascript: window.location='gpslmf2.do?code=");
                    document.write(lineCodes[i][0]);
                    document.write("'; \" onmouseover=\"showTip(event, 'moClick')\""); 
                    document.write(" onmouseout=\"hideTip('moClick')\" /></center>");
                    document.write("</td><td><span class='dataField'>");
                    document.write(lineCodes[i][0]);
                    document.write("</span></td><td>");
                    document.write(lineCodes[i][2]);
                    document.write("</td><td><span class='dataField'>");
                    document.write(lineCodes[i][1]);
                    document.write("</span></td><td><span class='dataField'>");
                    var division = lineCodes[i][3];
                    if (division == "CP") {
                        document.write("Control<br />Products");
                    } else if (division == "DR") {
                        document.write("Depot<br />Repair");
                    } else if (division == "FS") {
                        document.write("Field<br />Service");
                    } else if (division == "ES") {
                        document.write("Engineered<br />Systems");
                    } else {
                        document.write("&nbsp;");
                    }
                    document.write("</span></td></tr>");
                }
                document.close();
//-->
            </script>
            
    </table>        

<!--     Exit      -->

<br /><br /><br />
<p><center>
                <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpslf.jsp'; " 
                    onmouseover="showTip(event, 'moExit')" 
                    onmouseout="hideTip()"
                /></center>

</p>

<br /><br />
  <p>
    <img src="w3cxhtml10.bmp"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
  </p>
  </form>

    </div>  
</body>
</html>