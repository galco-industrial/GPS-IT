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
	<title>Galco Parametric Search - Modify Select Box Option</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- Modification History
        
        version 1.5.01
        
        09/01/2007 DES Modified to use Ajax to obtain line/family/subfamily codes
        07/20/2010 DES Modified to support optionImage field
        
        -->

<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moClick") {return "Click to Modify this Option.";}    
    if (divName == "moDataType") { return "This is the Data Type of the Option raw value.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    if (divName == "moFamilyName") {return "This is the Family name for this Select Box.";}
    if (divName == "moMaximum") {return "This is the maximum length of the Option Text value.";}
    if (divName == "moMinimum") {return "This is the minimum length of the Option Text value.";}
    if (divName == "moSelectBoxName") {return "The Select Box Name that will contain the new option.";}  
    if (divName == "moSubfamilyName") {return "This is the Subfamily name for the rule that uses this Select Box.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    myForm.B3.focus();
}


//-->    
</script>        

</head>
<body onload="setDefaults()">

    <script language="JavaScript" type="text/javascript">
    <!--
    
        var options = new Array();
        var iO = 0;
        
        <c:forEach var="item" items="${optionList}">
            options[iO++] = new Array(${item});
        </c:forEach>
        
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="#" method="post" onsubmit="return false" >
<p>
    <input type="hidden" name="familyCode" value="${familyCode}" />
    <input type="hidden" name="subfamilyCode" value="${subfamilyCode}" />
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
                    Parametric Search<br />Select Box Options Maintenance<br />
			Modify Select Box Option
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
    <br />
       
    <table border="0" align="center" width="100%">

 <!-- Family Name -->
     
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Family:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="family" size="36"
                        value = "${familyName}"
                        readonly = "readonly"
                        onmouseover="showTip(event, 'moFamilyName', 50, 100)"
                        onmouseout="hideTip()" 
                    />
                </span>
                </td>
            </tr>   
               
<!-- Subfamily Name -->
     
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Subfamily:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="subfamily" size="36"
                        value = "${subfamilyName}"
                        readonly = "readonly"
                        onmouseover="showTip(event, 'moSubfamilyName', 50, 100)"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr> 
           
<!-- Select Box Name -->            
            
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Select Box Name:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="selectBoxName" size="36"
                        value = "${selectBoxName}"
                        readonly = "readonly"
                        onmouseover="showTip(event, 'moSelectBoxName', 50, 100)"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>            
            
<!-- Data Type -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Data Type:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="dataType" size="16"
                        value = "${dataType}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moDataType')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            
<!-- Minimum -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Minimum Text Length:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="minimum" size="2" 
                        value = "${minimum}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moMinimum')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            
<!-- Maximum -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Maximum Text Length:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="maximum" size="2"
                        value = "${maximum}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moMaximum')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
    </table>
    <br />
            
    <table border="1" align="center" width="100%">
            <tr>
                <td width="5%">
                    <span class="requiredLabel">Click to<br />Modify</span>
                </td>
                <td width="6%">
                    <span class="requiredLabel">Display<br />Order&nbsp;</span>
                </td>
                <td width="30%">
                    <span class="requiredLabel">Option Text<br />(Cooked)</span>
                </td>
                <td width="30%">
                    <span class="requiredLabel">Value<br />(Raw)</span>
                </td>
                
                <!--
                <td width="28%">
                    <span class="requiredLabel">Value 2<br />(Raw)</span>
                </td>
                
                -->
                <td width="8%">
                    <span class="requiredLabel">Default</span>
                </td>
                <td width="21%">
                    <span class="requiredLabel">Image Name</span>
                </td>
            </tr>
            <script language="JavaScript" type="text/javascript">
<!--
                for (var i = 0; i < options.length; i++){
                    document.write("<tr><td><center><input type=\"button\" value=\"X\"");
                    document.write(" name=\"B4\" onclick=\"Javascript: window.location='gpsomf3.do?selectBoxName=");
                    document.write(encodeURIComponent(options[i][0]) + "&displayOrder=" + options[i][1]);
                    document.write("&familyCode=" + encodeURIComponent(document.form1.familyCode.value));
                    document.write("&subfamilyCode=" + encodeURIComponent(document.form1.subfamilyCode.value));
                    document.write("'; \" onmouseover=\"showTip(event, 'moClick')\""); 
                    document.write(" onmouseout=\"hideTip('moClick')\" /></center>");
                    document.write("</td><td><span class='dataField'>");
                    document.write(options[i][1]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(options[i][2]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(options[i][3] + "&nbsp;" );
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(options[i][5] + "&nbsp;" );
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(options[i][6] + "&nbsp;" );
                    document.write("</span></td></tr>");
                }
                if (options.length == 0) {
                    document.write("<tr><td colspan=\"5\" align=\"center\">");
                    document.write("No Options currently exist in this Select Box.");
                    document.write("</td></tr>");
                }
                document.close();
//-->
            </script>
    </table>        

<!--     Exit      -->

          <br /><br />
<p><center>
                <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsomf1.do'; " 
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