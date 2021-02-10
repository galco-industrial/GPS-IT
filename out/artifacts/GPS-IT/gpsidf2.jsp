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
        
        
        <!-- gpsicf1.jsp

        Modification History
        
        version 1.5.00
        
                
        
        -->

<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moActive") {return "Inactive Aliases are NOT shown in the Index.";}
    if (divName == "moAuditDate") {return "This is the date when this record was last changed.";}
    if (divName == "moAuditTime") {return "This is the time when this record was last changed.";}
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moDelete") {return "Click Delete to remove this Index Alias.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    if (divName == "moFamilyCode") {return "This is the Product Family Code for this Alias.";}
    if (divName == "moFamilyAlias") {return "This is the Family Alias.";}
    if (divName == "moProductLine") {return "Select a Product Line from this list.";}
    if (divName == "moSubfamilyCode") {return "This is the Subfamily Code for thia Alias.";}
    if (divName == "moSubfamilyAlias") {return "This is the Subfamily Alias.";}
    if (divName == "moUserID") {return "This is User ID of the last person th change this record.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    var work = "";
    if (myForm.subfamilyAlias.value == "") {
        myForm.subfamilyAlias.value = "<none>";
    }
    myForm.B3.focus();
}

function My_Validator() {
    var myForm = document.form1;
    var work = myForm.auditUserID.value;
    if (work.length == 0) {
        alert ("Please enter your User ID.");
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

        var userID = "${sessionScope.auditUserID}";
        var toolTips = "${sessionScope.enableToolTips}";
        var statMsg = "${statusMessage}";

                 
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">

<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="gpsidf3.do" method=post onsubmit="return My_Validator()">
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
                    Parametric Search<br />Select Box Maintenance<br />
			Delete Index Alias
                </h2>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;
            </td>
            <td>
                <h3 class="red">
                    You cannot modify Index Alias information here.
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
                    <p>This Index Family Alias and/or Subfamily Alias
                    has been selected for Deletion.
                    </p><br />
                </td>
            </tr>
  
  <!-- Index Status  -->
  
            <tr>
                <td align="right">
                    <span class="fixedLabel">Active:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="active" size="6" maxlength="6"
                        onmouseover="showTip(event, 'moActive', 50, -50)"
                        onmouseout="hideTip()" 
                        value="${activeCode}"
                        readonly="readonly"
                    />
                    </span>
                </td>
            </tr>    
            
  <!-- Index Family Alias  -->
  
            <tr>
                <td align="right">
                    <span class="fixedLabel">Alias Family Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="familyAlias" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moFamilyAlias', 50, -50)"
                        onmouseout="hideTip()" 
                        value="${familyAlias}"
                        readonly="readonly"
                    />
                    </span>
                </td>
            </tr>
            
                
  <!-- Index Subfamily Alias  -->
  
            <tr>
                <td align="right">
                    <span class="fixedLabel">Alias Subfamily Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="subfamilyAlias" size="36" maxlength="36"
                        onmouseover="showTip(event, 'moSubfamilyAlias', 50, -50)"
                        onmouseout="hideTip()" 
                        value="${subfamilyAlias}"
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
            
<!-- Product Family -->

            <tr>
                <td align="right">
                    <span class="fixedLabel">Family Code:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="familyCode" size="8" maxlength="8"
                        value = "${familyCode}"
                        onmouseover="showTip(event, 'moFamilyCode', 50, -50)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
            
 <!-- Product Family Name -->

            <tr>
                <td align="right">
                    <span class="fixedLabel">Family Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="familyName" size="36" maxlength="36"
                        value = "${familyName}"
                        onmouseover="showTip(event, 'moFamilyCode', 50, -50)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>
 
<!-- Product Subfamily -->
            
            <tr>
                <td align="right">
                    <span class="fixedLabel">Subfamily Code:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="subfamilyCode" size="8" maxlength="8"
                        value = "${subfamilyCode}"
                        onmouseover="showTip(event, 'moSubfamilyCode', 50, -50)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>   

<!-- Product Subfamily Name -->
            
            <tr>
                <td align="right">
                    <span class="fixedLabel">Subfamily Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="subfamilyName" size="36" maxlength="36"
                        value = "${subfamilyName}"
                        onmouseover="showTip(event, 'moSubfamilyCode', 50, -50)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr> 
             
 <!-- Audit User Id -->
            
            <tr>
                <td align="right">
                    <span class="fixedLabel">Audit User ID:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="userID" size="4" maxlength="4"
                        value = "${auditUserID}"
                        onmouseover="showTip(event, 'moUserID', 50, -50)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr> 

<!-- Audit Date -->
            
            <tr>
                <td align="right">
                    <span class="fixedLabel">Audit Date:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="auditDate" size="10" maxlength="10"
                        value = "${auditDate}"
                        onmouseover="showTip(event, 'moAuditDate', 50, -50)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
                </td>
            </tr>   
 
 <!-- Audit Time -->
            
            <tr>
                <td align="right">
                    <span class="fixedLabel">Audit Time:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="auditTime" size="8" maxlength="8"
                        value = "${auditTime}"
                        onmouseover="showTip(event, 'moAuditTime', 50, -50)"
                        onmouseout="hideTip()" 
                        readonly="readonly"				
                    />
                    </span>
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
            <input type="submit" value="Delete" name="B1" 
	onmouseover="showTip(event, 'moDelete')" 
        onmouseout="hideTip()"
	/>
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" 
                value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" 
                name="B3" onclick="Javascript: window.location='gpsif.jsp'; " 
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