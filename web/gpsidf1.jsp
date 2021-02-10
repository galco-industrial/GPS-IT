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
	<title>Galco Parametric Search - Delete Index Alias</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
                
        <!-- gpsidf1.jsp

        Modification History
        
        version 1.5.01
        
         *
         * 09/28/2010 DES fixed to correctly support division CP and family code / subfamily code.
         *     
        
        -->

<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the Previous Menu.";}
    if (divName == "moDelete") {return "Click to Delete this Index Alias.";}    
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    // myForm.B3.focus();
}

//-->    
</script>        

</head>
<body onload="setDefaults()">
    <script language="JavaScript" type="text/javascript">
    <!--

        var indeces = new Array();
               
        <c:forEach var="item" items="${indexList}">
            indeces[indeces.length] = new Array(${item});
        </c:forEach>
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
   
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="#" method="post" >

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
                    Parametric Search<br />Index Maintenance<br />
			Delete Alias
                </h2>
            </td>
        </tr>
        <tr>
        <td>&nbsp;</td>
        <td>${statusMessage}</td>
        </td>
        
    </table>
    <br /><br /><br />
    

        
    <table border="1" align="center" width="100%">

            <tr>
                <td width="6%">
                    <span class="requiredLabel">Click to<br />Delete</span>
                </td>
                <td width="6%">
                    <span class="requiredLabel">Active</span>
                </td>
                <td width="34%">
                    <span class="requiredLabel">Family Alias</span>
                </td>
                <td width="34%">
                    <span class="requiredLabel">Subfamily Alias</span>
                </td>
                <td width="10%">
                    <span class="requiredLabel">Family Code</span>
                </td>
                <td width="10%">
                    <span class="requiredLabel">Subfamily Code</span>
                </td>
                <!--
                <td width="5%">
                    <span class="requiredLabel">Audit<br />User<br />ID</span>
                </td>
                <td width="11%">
                    <span class="requiredLabel">Audit<br />Date</span>
                </td>
                <td width="9%">
                    <span class="requiredLabel">Audit<br />Time</span>
                </td>
                -->
            </tr>
            <script language="JavaScript" type="text/javascript">
<!--
                for (var i = 0; i < indeces.length; i++){
                    document.write("<tr><td><center><input type=\"button\" value=\"X\"");
                    document.write(" name=\"B4\" onclick=\"Javascript: window.location='gpsidf2.do?familyalias=");
                    document.write(encodeURIComponent(indeces[i][0]));
                    document.write("&subfamilyalias=");
                    document.write(encodeURIComponent(indeces[i][1]));
                    document.write("&familycode=");
                    document.write(encodeURIComponent(indeces[i][2]));
                    document.write("&subfamilycode=");
                    document.write(encodeURIComponent(indeces[i][3]));
                    document.write("'; \" onmouseover=\"showTip(event, 'moClick')\""); 
                    document.write(" onmouseout=\"hideTip('moClick')\" /></center>");
                    document.write("</td><td><span class='dataField'>");
                    document.write( indeces[i][7]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write( indeces[i][0]);
                    document.write("</span></td><td><span class='dataField'>");
                    if (indeces[i][1] == "") {
                        document.write( "&lt;none&gt;");
                    } else {
                        document.write( indeces[i][1]);
                    }
                    document.write("</span></td><td><span class='dataField'>");
                    document.write( indeces[i][2]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write( indeces[i][3] + "&nbsp;");
                    document.write("</span></td>");
                    
                    //document.write("<td><span class='dataField'>");
                    //document.write( indeces[i][4]);
                    //document.write("</span></td><td><span class='dataField'>");
                    //document.write( indeces[i][5]);
                    //document.write("</span></td><td><span class='dataField'>");
                    //document.write( indeces[i][6]);
                    //document.write("</span></td>");
                    
                    document.write("</tr>");
                }
                document.close();
//-->
            </script>
            
    </table>        

<!--     Exit      -->

<br /><br /><br />
<p><center>
                <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsif.jsp'; " 
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