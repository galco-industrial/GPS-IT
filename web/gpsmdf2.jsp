<%-- 
    Document   : gpsmdf2
    Created on : Oct 24, 2013, 11:07:37 AM
    Author     : dunlop
--%>

<%@page contentType="text/html"%>
<%@page language="java" import="java.util.*" session="true"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
	<title>Galco Parametric Search - Delete Manufacturer Alias</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        
        <!-- gpsmdf2.jsp

        Modification History
        
        version 7.1.00
        
                
        
        -->
<script language="JavaScript" type="text/javascript">
<!--

        function getMessage(divName) {
            if (divName == "moActive") {return "Inactive Aliases are NOT shown in the Index.";}
            if (divName == "moAuditDate") {return "This is the date when this record was last changed.";}
            if (divName == "moAuditTime") {return "This is the time when this record was last changed.";}
            if (divName == "moAuditUserID") {return "Enter your User ID.";}
            if (divName == "moModify") {return "Click Modify to modify this Index Alias.";}
            if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
            if (divName == "moMfgAlias") {return "Enter up to 36 alphanumeric characters.";}
            if (divName == "moMfgCode") {return "Enter up to 36 alphanumeric characters.";}
            if (divName == "moUserID") {return "This is User ID of the last person to change this record.";} 
            return "";
        }

        function setDefaults() {
            var myForm = document.form1;            
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
    <body>
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
            <form name="form1" action="gpsmdf3.do" method=post onsubmit="return My_Validator()">
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
                                Parametric Search<br />
                                Delete Manufacturer Alias
                            </h2>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            &nbsp;
                        </td>
                        <td>
                            <h3 class="red">
                                You cannot modify Manufacturer Alias information here.
                            </h3>
                            <h3 class="blue">
                                ${statusMessage}
                            </h3>
                        </td>
                    </tr>
                </table>
                <table border="0" align="center" width="100%">
                    <!-- Index Status  -->  
                    <tr>
                        <td align="right">
                            <span class="fixedLabel">Active:&nbsp;</span>
                        </td>
                        <td align="left">
                            <span class="dataField">
                            <input type="text" name="active" size="6" maxlength="6"
                                onmouseover="showTip(event, 'moChange', 50, -50)"
                                onmouseout="hideTip()" 
                                value="${ActiveCode}"
                                readonly="readonly"
                            />
                            </span>
                        </td>
                    </tr> 
                    <!-- Manufacturer Alias Code -->
                    <tr>
                        <td align="right">
                            <span class="fixedLabel">Manufacturer Alias Code:&nbsp;</span>
                        </td>
                        <td align="left">
                            <span class="dataField">
                            <input type="text" name="mfgAliasCode" size="8" maxlength="8"
                                value = "${mfgAliasCode}"
                                onmouseover="showTip(event, 'moMfgCode', 50, -50)"
                                onmouseout="hideTip()" 
                                readonly="readonly"				
                            />
                            </span>
                        </td>
                    </tr>

                    <!-- Manufacturer Alias Name -->
                    <tr>
                        <td align="right">
                            <span class="fixedLabel">Manufacturer Alias Name:&nbsp;</span>
                        </td>
                        <td align="left">
                            <span class="dataField">
                            <input type="text" name="mfgAliasName" size="36" maxlength="36"
                                value = "${mfgAliasName}"
                                onmouseover="showTip(event, 'moMfgAlias', 50, -50)"
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
                                name="B3" onclick="Javascript: window.location='gpsmf.jsp'; " 
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
