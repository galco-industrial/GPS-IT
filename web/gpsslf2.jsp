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
	<title>Galco Parametric Search - List Select Boxes</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
   <!-- gpsslf2.jsp

        Modification History

        version 1.5.01
        
        08/13/2007 DES Modified to use Ajax to obtain line/family/subfamily codes
    
        
        -->

<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moBack") {return "Click Back to previous screen.";}
    if (divName == "moExit") {return "Click Exit to return to the Select Box Menu.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    myForm.B3.focus();
}

//-->    
</script>        

</head>
<body onload="setDefaults">

    <script language="JavaScript" type="text/javascript">
    <!--

        var selectBoxes = new Array();
        var iB = 0;
        
        <c:forEach var="item" items="${selectBoxList}">
            selectBoxes[iB++] = new Array(${item});
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
                    Parametric Search<br />Select Box Maintenance<br />
			List Select Boxes
                </h2>
            </td>
        </tr>
                <tr>
            <td>
                &nbsp;
            </td>
            <td>
                <h3 class="blue">
                    ${statusMessage}
                </h3>
            </td>
        </tr>
    </table>
    <br /><br /><br />
    

        
    <table border="1" align="center" width="100%">

            <tr>
                <td width="15%">
                    <span class="requiredLabel">Family Code&nbsp;</span>
                </td>
                <td width="15%">
                    <span class="requiredLabel">Subfamily Code&nbsp;</span>
                </td>
                <td width="34%">
                    <span class="requiredLabel">Select Box Name&nbsp;</span>
                </td>
                <td width="10%">
                    <span class="requiredLabel">Data Type<br /></span>
                </td>
                <td width="10%">
                    <span class="requiredLabel">Min Len</span>
                </td>
                <td width="10%">
                    <span class="requiredLabel">Max Len</span>
                </td>
                <td width="6%">
                    <span class="requiredLabel">Images</span>
                </td>
            </tr>
            <script language="JavaScript" type="text/javascript">
<!--
                for (var i = 0; i < selectBoxes.length; i++){
                    document.write("<tr><td><span class='dataField'>");
                    document.write(selectBoxes[i][0] + "&nbsp;" );
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(selectBoxes[i][1] + "&nbsp;" );
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(selectBoxes[i][2] + "&nbsp;" );
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(selectBoxes[i][3] + "&nbsp;" );
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(selectBoxes[i][4] + "&nbsp;" );
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(selectBoxes[i][5] + "&nbsp;" );
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(selectBoxes[i][6] + "&nbsp;" );
                    document.write("</span></td></tr>");
                }
                document.close();
//-->
            </script>
          
    </table>        

<!--     Exit      -->

          <br /><br /><br />
<p><center>
                <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Back&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsslf1.do'; " 
                    onmouseover="showTip(event, 'moBack')" 
                    onmouseout="hideTip()"
                />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B4" onclick="Javascript: window.location='gpssf.jsp'; " 
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