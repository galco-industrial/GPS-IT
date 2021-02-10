<%-- 
    Document   : gpsmcf1
    Created on : Sep 26, 2013, 9:34:45 AM
    Author     : dunlop
--%>

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
	<title>Galco Parametric Search - Create Manufacturer Alias</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        
        <!-- gpsmcf1.jsp

        Modification History
        
            
                
        
        -->
<script language="JavaScript" type="text/javascript">
    function getMessage(divName) {   
        if (divName == "moActive") {return "Select 'No' if you want to hide this Alias from the Index";}
        if (divName == "moAuditUserID") {return "Enter your User ID.";}
        if (divName == "moCreate") {return "Click Create to add this Manufacturer Alias.";}
        if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";} 
        if (divName == "moMfgName") {return "Enter up to 36 alphanumeric characters.";}    
        return "";
    }
   
    function setDefaults() {
        var myForm = document.form1;
        myForm.mfgAlias.focus();
    }
   
    function My_Validator() {
        var myForm = document.form1;
        var work;
        
        work = myForm.mfgAlias.value;
        if (work.length == 0 ) {
            alert ("Please enter a valid Manufacturer Alias Name.");
            myForm.mfgAlias.focus();
            return false;
        }   
      
        // Check for a selected mfg Name
        work = myForm.names.value;        
        var div = work.lastIndexOf("-");        
        if (work.length == 0 || work.substr(0,6)=='Enter ' || div == -1) {            
            alert ("Please choose a Manufacturer Name.");
            myForm.mfgAlias.focus();
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
   
    function checkmfgAlias() {
        var myForm = document.form1;
        var work = myForm.mfgAlias.value;
        work = deleteLeadingSpaces(deleteTrailingSpaces(reduceSpaces(work)));
        myForm.mfgAlias.value = work;
        if (!checkCharSet(work, UC + LC + NU + SP + "/;&()" )) {
            myForm.mfgAlias.focus();
            return;
        } 
    } 
   
    function createAjaxRequest() {
        var request = null;
        if (window.XMLHttpRequest) {
            request = new XMLHttpRequest();
        } else {
            if (window.ActiveXObject) {
                try {
                    request = new ActiveXObject("Msml2.XMLHTTP");
                } catch (err1) {
                    try {
                        request = new ActiveXObject("Microsoft.XMLHTTP");
                    } catch (err2) {
                    }
                }
            }
        }
        if (request == null) {
            alert ("Attempt to create Ajax Request Object failed!");
        } else {
            //alert ("Attempt to create Ajax Request Object successful!");
        }
        return request;
    }
   
    var typingTimer;
    var doneTypingInterval = 700;

    //Detect keystroke and only execute after the user has finish typing
    function delayExecute() {
        clearTimeout(typingTimer);
        typingTimer = setTimeout(
            function(){mfgNamesUpdate('names')},
            doneTypingInterval
        );
        return true;
    }

    function mfgNamesUpdate(theInputName) {
        content = document.getElementById(theInputName).value;
        if (content != "Enter Manufacturer Name") {        
            ajaxMfgReq = createAjaxRequest(); // ajaxMfgReq is a global
            ajaxMfgReq.onreadystatechange = mfgNamesRequestStateChange;
            ajaxMfgReq.open ("GET", "getMfgrNames.do?mfgrName=" 
            + escape(document.getElementById(theInputName).value) + "&ts=" + new Date().getTime(), true);
            ajaxMfgReq.send (null);
            if(content.length > 0) {
                box('1');
            } else {
                box('0');
            }
        }
    }
    
    function mfgNamesRequestStateChange(){
        if (ajaxMfgReq.readyState == 4) {
            if (ajaxMfgReq.status == 200) { 
                mfgrNamesXMLParser();
            } else {
                alert ("Unexpected Error " + ajaxMfgReq.status);                
            }
        }
    }
    
    function mfgrNamesXMLParser() {
        var response = ajaxMfgReq.responseXML;
        var elements = response.getElementsByTagName("manufacturers");
        if (elements == null) {
            alert ("The manufacturers part of the document is null.");
            return;
        }
        var element = elements[0].getElementsByTagName("manufacturer");        
        var result = "";
        var item;
        var code;
        var nodeName = "";
        var output = "<table border=0 width=100%>";        
        for (var i = 0; i < element.length; i++) {            
            item = element[i].getElementsByTagName("name");
            code = element[i].getElementsByTagName("code");
            nodeName = item[0].firstChild.nodeValue + "-" + code[0].firstChild.nodeValue;
            result += "<tr style='border-collapse:collapse'; onmouseover='highlight(true,this)'; \n\
                        onmouseout='highlight(false, this)'; \n\
                        onClick='display(\"" + nodeName + "\")';><td>"
                        + item[0].firstChild.nodeValue + "-" + code[0].firstChild.nodeValue; "</td></tr>";
        }
        
        if(element.length>0) {
            result = output + result + "</table>";
            document.getElementById("autocomplete_choices").innerHTML=result;
            box('1');
        }
        else
            box('0');   
    }
    
    function box(act) {
        if(act=='0') {
            document.getElementById('autocomplete_choices').style.display = 'none';
        }
        else
            document.getElementById('autocomplete_choices').style.display = 'block';
    } 
    
    function highlight(action, obj) {
        if(action) {	
            obj.bgColor = "#E8E8E8";             
        }
        else
            obj.bgColor = "#FFFFFF";
            
    }
    
    function display(word) {
        document.getElementById('names').value = word;
        document.getElementById('autocomplete_choices').style.display = 'none';
        document.getElementById('names').focus();        
    } 
    
    var boxTimer;
    var doneBoxInterval = 700;
    
    function hidebox(obj) {
        if(obj.value.length<1) {
            obj.value='Enter Manufacturer Name';
        }        
        clearTimeout(boxTimer);
        boxTimer = setTimeout(
            function(){clearbox(obj)},
            doneBoxInterval
        );
        return true;    
    }
    
    function clearbox(obj) {        
        if(document.getElementById('autocomplete_choices').style.display == "block") {
            document.getElementById('autocomplete_choices').style.display = 'none';
            obj.value='Enter Manufacturer Name';
        }
    }
    
    
</script>
    </head>
    <body onload="setDefaults()">     
  
        <div style="position: absolute; left: 10px; top: 10px; width: 700px; ">

        <div class="toolTip" id="virtualToolTip">
            <div class="toolTipHeader" >Tip</div>
            <div id="virtualToolTipText"></div>
        </div>

        <form name="form1" action="gpsmcf2.do" method=post onsubmit="return My_Validator()">
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
                                Create Manufacturer Alias
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
                
                <!-- Index Status  -->
  
                <tr>
                    <td align="right">
                        <span class="requiredLabel">Active:&nbsp;</span>
                    </td>
                    <td align="left">
                        <span class="dataField">
                        <select name="active" size="0"
                            onmouseover="showTip(event, 'moActive', 50, -50)"
                                    onmouseout="hideTip()">
                            <option selected="selected" value="Y">Yes</option>
                            <option value="N">No</option>
                        </select> 
                        </span>
                    </td>
                </tr>
                
                <!-- Manufacturer Alias  -->  
                
                <tr>
                    <td align="right">
                        <span class="requiredLabel">Manufacturer Alias Name:&nbsp;</span>
                    </td>
                    <td align="left">
                        <span class="dataField">
                        <input type="text" name="mfgAlias" size="36" maxlength="36"
                            onmouseover="showTip(event, 'moMfgName', 50, -50)"
                            onmouseout="hideTip()" 
                            onblur="checkmfgAlias()"				
                        />
                        </span>
                    </td>
                </tr>                
                <!-- Autocomplete List -->                
                <tr> 
                    <td align="right">
                        <span class="requiredLabel">Manufacturer Name:&nbsp;</span>
                    </td> 
                    <td>
                       <input type="text" autocomplete="off" size="36" maxlength="36" 
                              onkeypress="return delayExecute()"
                              name="names" value="Enter Maufacturer Name" onClick="if(this.value.substr(0,6)=='Enter ') this.value=''"
                              id="names" onBlur="return hidebox(this)">                       
                       <div id="autocomplete_choices" class="autocomplete" ></div>
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
                        <input type="submit" value="Create" name="B1" 
                               onmouseover="showTip(event, 'moCreate')" 
                               onmouseout="hideTip()" />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="button" 
                            value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" 
                            name="B3" onclick="Javascript: window.location='gpsmf.jsp'; " 
                            onmouseover="showTip(event, 'moExit')" 
                            onmouseout="hideTip()" />
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
