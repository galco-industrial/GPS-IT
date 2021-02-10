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
	<title>Galco Parametric Search - Create Units</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>

<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moContinue") {return "Click Continue to Create a new Unit record.";}
    if (divName == "moBaseUnits") {return "Choose the Base Units for the new Unit record.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    myForm.baseUnits.focus();
}

function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
    work = myForm.baseUnits.value;
    if (work.length == 0) {
        alert ("Please choose a Base Unit.");
        myForm.baseUnits.focus();
        return false;
    }
    
    work = myForm.auditUserID.value;
    if (work.length == 0) {
        alert ("Please enter a valid User ID.");
        myForm.auditUserID.focus();
        return false;
    }
      
    myForm.validation.value = "OK";
    return true;
}

//-->    
</script>        

</head>
<body onload="setDefaults()">

    <script language="JavaScript" type="text/javascript">
    <!--
        var units = new Array();
        var iU = 0;
                
        <c:forEach var="item" items="${unitsList}">
            units[iU++] = new Array(${item});
        </c:forEach>
                
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
   
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="gpsucf2.do" method="post" onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
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
                    Parametric Search<br />Units Maintenance<br />
			Create New Unit.
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
                    <p>First choose the Base Units for this new Unit record.
                     
                    </p><br />
                </td>
            </tr>
            
            
<!-- Select Box Name -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Base Units:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="baseUnits" size="1"
                        onmouseover="showTip(event, 'moBaseUnits', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="">Please choose the Base Units</option>
                        <option value="*new*">**New Base Unit**</option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            for (var i = 0; i < iU; i++){
                                if (units[i][1] == units[i][2]) {
                                    document.write("<option value=\"" + units[i][2] + "\">" + units[i][2]+"</option>");
                                }
                            }
                        //-->
                        </script>
                    </select>
                </td>
            </tr>
            

<!--  User ID  (This actually should be obtained from log in data -->

      <tr>
        <td align="right">
          <span class="requiredLabel">User ID:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="4" maxlength="4" name="auditUserID"
	
		value="${sessionScope.auditUserID}"

          onblur="checkAuditUserID()"
          onmouseover="showTip(event, 'moAuditUserID')"
          onmouseout="hideTip()" 
          /> 
        </span></td>
      </tr>

<!--     Continue      -->

      <tr>
        <td colspan="2">
          <center>
            <br />
            <input type="submit" value="Continue" name="B1" 
	onmouseover="showTip(event, 'moContinue')" 
        onmouseout="hideTip()"
	/>
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsuf.jsp'; " 
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