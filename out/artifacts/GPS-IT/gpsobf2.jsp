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
	<title>Galco Parametric Search - Build Select Box Options Worksheet</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
                
        <!-- gpsrmf2.jsp

        Modification History
        
        version 1.5.00
     
        04/23/2008      DES     Modified to support 4 Divisions
        
        -->


<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moCancel") {return "Click Cancel to return to the previous screen.";}
    if (divName == "moClick") {return "Click to Build an Options Worksheet for a selected parm field.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the Previous Menu.";}
    if (divName == "moProductLine") {return "This is the Product line you selected.";}
    if (divName == "moFamilyName") {return "This is the Product Family you selected.";}
    if (divName == "moSubfamilyName") {return "This is the Product Subfamily you selected.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    myForm.B3.focus();
}


function My_Validator() {
    var myForm = document.form1;
    var work = "";

    myForm.validation.value = "OK";
    return true;
}

//-->    
</script>        

</head>
<body onload="setDefaults()">

    <script language="JavaScript" type="text/javascript">
    <!--
    
        var rules = new Array();
        var iR = 0;

        <c:forEach var="item" items="${rulesList}">
            rules[iR++] = new Array(${item});
        </c:forEach>
        
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="" method=post onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
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
			Build an Options Worksheet
                </h2>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;
            </td>
            <td>
                <h3 class="red">
                    Field names in RED are required.
                </h3>
                <h3 class="blue">
                    ${statusMessage}
                </h3>
            </td>
        </tr>
    </table>
        
    <table border="0" align="center" width="100%">
            <tr>
                <td colspan="2" align="center">
                    <p>Click the desired field to Build an Options Worksheet.
                    </p><br />
                </td>
            </tr>
            
<!-- Product Line -->
            
      <tr>
        <td align="right">
          <span class="requiredLabel">Product Line:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="36"  name="productLine"
	
		value="${productLineName}"
                readonly="readonly"

          onmouseover="showTip(event, 'moProductLine')"
          onmouseout="hideTip()" 
          /> 
        </span></td>
      </tr>
      
<!-- Product Family -->
            
      <tr>
        <td align="right">
          <span class="requiredLabel">Product Family:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="36"  name="family"
	
		value="${familyName}"
                readonly="readonly"

          onmouseover="showTip(event, 'moFamilyName')"
          onmouseout="hideTip()" 
          /> 
        </span></td>
      </tr>
      
<!-- Product Subfamily -->
            
      <tr>
        <td align="right">
          <span class="requiredLabel">Product Subfamily:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="36"  name="subfamily"
	
		value="${subfamilyName}"
                readonly="readonly"

          onmouseover="showTip(event, 'moSubfamilyName')"
          onmouseout="hideTip()" 
          /> 
        </span></td>
      </tr>
                        
    </table>
    <br /><br /><br />
           
    <table border="1" align="center" width="100%">

            <tr>
                <td width="5%">
                    <span class="requiredLabel">Click to<br />Select</span>
                </td>
                <td width="5%">
                    <span class="requiredLabel">Seq<br />Num</span>
                </td>
                <td width="10%">
                    <span class="requiredLabel">Rule<br />Scope</span>
                </td>

                 <td width="40%">
                    <span class="requiredLabel">Parm<br />Name</span>
                </td>
                <td width="10%">
                    <span class="requiredLabel">Data<br />Type</span>
                </td>
                <td width="30%">
                    <span class="requiredLabel">Units</span>
                </td>
             </tr>
            <script language="JavaScript" type="text/javascript">
<!--
            if (rules.length > 0) {
                var fCode = document.form1.familyCode.value;
                var sCode = document.form1.subfamilyCode.value;
                for (var i = 0; i < rules.length; i++){
                    document.write("<tr><td><center><input type=\"button\" value=\"X\"");
                    document.write(" name=\"B4\" onclick=\"Javascript: window.location='gpsobf3.do?familyCode=");
                    document.write(encodeURIComponent(fCode) + "&subfamilyCode=" + encodeURIComponent(sCode) + "&seqNum=" + encodeURIComponent(rules[i][0]));
                    //document.write("&productLine=" + encodeURIComponent("${productLineName}"));
                    document.write("'; \" onmouseover=\"showTip(event, 'moClick')\""); 
                    document.write(" onmouseout=\"hideTip()\" /></center>");
                    document.write("</td><td><span class='dataField'>");
                    document.write(rules[i][0]);
                    document.write("</span></td><td><span class='dataField'>");
                    //document.write(rules[i][1]);
                    //document.write("</span></td><td><span class='dataField'>");
                    //document.write(rules[i][2]);
                    //document.write("</span></td><td><span class='dataField'>");
                    document.write(rules[i][3]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(rules[i][4]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(rules[i][5]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(rules[i][6] + "&nbsp;");
                    //document.write("</span></td><td><span class='dataField'>");
                    //document.write(rules[i][7]);
                    document.write("</span></td></tr>");
                }
            } else {
                    document.write("<tr><td colspan='6'><span class='dataField'>");
                    document.write("<center>No Rules exist for this Family/Subfamily</center>");
                    document.write("</span></td></tr>");
            }
            document.close();
//-->
            </script>
            
    </table>        

 <table border="0">
<!--     Continue      -->

      <tr>
        <td colspan="2">
          <center>
            <br />
                     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
             <input type="button" value="Cancel" name="B9" onclick="Javascript: history.back(); " 
	onmouseover="showTip(event, 'moCancel')" 
        onmouseout="hideTip()"
	/>
         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsof.jsp'; " 
	onmouseover="showTip(event, 'moExit')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
      </tr>
    </table>
<br /><br />
  <p>
    <img src="w3cxhtml10.bmp"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
  </p>
    </form>
    </div>  
</body>
</html>