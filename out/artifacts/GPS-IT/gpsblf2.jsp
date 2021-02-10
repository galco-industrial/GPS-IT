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
	<title>Galco Parametric Search - List Subfamily Codes</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- gpsblf2.jsp
        Version 1.5.03
        
        Modified 4/21/2008 by DES to support 4 divisions
        
        -->
    
<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the Previous Menu.";}
    if (divName == "moClick") {return "Click this button to Read this item.";}
    return "";
}

//-->    
</script>        

</head>
<body>

    <script language="JavaScript" type="text/javascript">
    <!--

        var lineCode = "${lineCode}";
        var lineName = "${lineName}";
        var familyCode = "${familyCode}";
        var familyName = "${familyName}";
        var subfamilyCodes = new Array();
        var iS = 0;
        
        <c:forEach var="item" items="${subfamilyCodesList}">
            subfamilyCodes[iS++] = new Array(${item});
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
                    Parametric Search<br /> Subfamily Code Maintenance<br />
			List Subfamily Codes <br />
                        Product Line: ${lineName} <br />
                        Family: ${familyName}
                </h2>
            </td>
        </tr>
        
    </table>
<br /><br /><br />

    <table border="1" align="center" width="100%">

            <tr>
                <td width="5%">
                    <span class="requiredLabel">Click to<br />Read</span>
                </td>
                <td width="25%">
                    <span class="requiredLabel">Product Line&nbsp;</span>
                </td>
                <td width="25%">
                    <span class="requiredLabel">Family&nbsp;</span>
                </td>
                <td width="5%">
                    <span class="requiredLabel">Display<br />Order</span>
                </td>
                <td width="14%">
                    <span class="requiredLabel">SubFamily<br />Code&nbsp;</span>
                </td>

                <td width="26%">
                    <span class="requiredLabel">Subfamily Name</span>
                </td>
            </tr>
            <script language="JavaScript" type="text/javascript">
<!--
                    for (var i = 0; i < iS; i++){
                            document.write("<tr><td><center><input type=\"button\" value=\"X\"");
                            document.write(" name=\"B4\" onclick=\"Javascript: window.location='gpsblf3.do?familyCode=");
                            document.write(subfamilyCodes[i][0] + "&subfamilyCode=" + encodeURIComponent(subfamilyCodes[i][1]));
                            document.write("'; \" onmouseover=\"showTip(event, 'moClick')\""); 
                            document.write(" onmouseout=\"hideTip('moClick')\" /></center>");
                            document.write("</td><td><span class='dataField'>");
                            //document.write(lookUpLine(subfamilyCodes[i][0]));
                            document.write(lineName);
                            document.write("</span></td><td><span class='dataField'>");
                            //document.write(lookUpFamily(subfamilyCodes[i][0]));
                            document.write(familyName);
                            document.write("</span></td><td><span class='dataField'>");
                            document.write(subfamilyCodes[i][3]);
                            document.write("</span></td><td><span class='dataField'>");
                            document.write(subfamilyCodes[i][1]);
                            document.write("</span></td><td><span class='dataField'>");
                            document.write(subfamilyCodes[i][2]);
                            document.write("</span></td></tr>");
                       
                    }

                document.close();
//-->
            </script>
    </table>        

<!--     Exit      -->

<br /><br /><br />
<p>
    <center>
        <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsbf.jsp'; " 
            onmouseover="showTip(event, 'moExit')" 
            onmouseout="hideTip()"
        />
    </center>
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