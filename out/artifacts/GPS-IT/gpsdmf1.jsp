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
	<title>Galco Parametric Search - Modify Parametric Data</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
                
        <!-- gpsdmf1.jsp

        Modification History
        
        version 1.2.00
        
       
        -->

<script language="JavaScript" type="text/javascript">
<!--


function checkPartNumber() {
    var myForm = document.form1;
    var work = myForm.partNum.value;
    work = work.toUpperCase();
    myForm.partNum.value = work;
    if (!checkCharSet(work, UC + NU + SP + "#$%^&*()_-=+/:;>.,?")) {
        myForm.partNum.focus();
    }
    partNumNameCheck();
    return;
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

function getMessage(divName) {
	if (divName == "moAuditUserID"){return "Enter your User ID here.";}
	if (divName == "moModify"){return "Click to Modify parametric data for this Part Number.";}
        if (divName == "moExit"){return "Click Exit to return to the previous Menu.";}
        if (divName == "moPartNum"){return "Enter a part number here. The part number must already exist within the WDS database.";}
        return "";
}


function partNumExistsCheck() {
    var myForm = document.form1;
    var work = myForm.partNum.value;
    ajaxPartNumReq = createAjaxRequest(); // ajaxPartNumReq is a global
    ajaxPartNumReq.onreadystatechange = partNumRequestStateChange;
    ajaxPartNumReq.open ("GET", "getPartNumInfo.do?partNum=" + encodeURIComponent(work)
        + "&ts=" + new Date().getTime(), true); // asynchronous call
    ajaxPartNumReq.send (null);
}

function partNumNameCheck() {
    var myForm = document.form1;
    var work = myForm.partNum.value;
    work = deleteLeadingSpaces(deleteTrailingSpaces(work));
    work = work.toUpperCase();
    myForm.partNum.value = work;
    if (!checkCharSet(work, UC + NU + SP + "#$%^&*()_-=+/:;>.,?" )) {
        myForm.partNum.focus();
        return;
    }
    if (work.length != 0) {
        partNumExistsCheck();
    } else {
        document.getElementById("Msg").innerHTML = "";
    }
}

function partNumRequestStateChange(){
    if (ajaxPartNumReq.readyState == 4) {
        if (ajaxPartNumReq.status == 200) {
            //partNumExistsParser();
            partNumInfoXMLParser();
        } else {
            alert ("Unexpected Error " + ajaxPartNumReq.status);
            var myForm = document.form1;
            myForm.partNum.value = "";
        }
    }
}

function partNumExistsParser() {
    var results = ajaxPartNumReq.responseText;
    if (results.indexOf("false") != -1) {
        alert ("Error - this Part Number does not exist.");
        var myForm = document.form1;
        myForm.partNum.focus();
        return;
    }
    if (results.indexOf("true") != -1) {
        return;
    }
    alert ("An unexpected error occurred when checking the Part Number.");
    var myForm = document.form1;
    myForm.partNum.value = "";
}

function partNumInfoXMLParser() {
    //alert ( ajaxPartNumReq.responseText);
    var pn = "";
    var fc = "";
    var sc = "";
    var hpsd = "";
    var message = "";
    var myForm = document.form1;
    var results = ajaxPartNumReq.responseXML.getElementsByTagName("partInfo");
    var PN = results[0].getElementsByTagName("partNum");
    if (PN[0]) {
        pn = PN[0].firstChild.nodeValue;
    }

    var HPSD = results[0].getElementsByTagName("partHasPSData");
    if (HPSD[0]) {
        hpsd = HPSD[0].firstChild.nodeValue;
        
    }
    if (pn.length == 0) {
        message = "This Part Number does not exist.";
        alert (message);
        var myForm = document.form1;
        document.getElementById("Msg").innerHTML = message;
        myForm.partNum.focus();
        return;
    }
    
    if (hpsd == "false") {
        message = "This Part Number currently has no Parametric Data.";
        alert (message);
        document.getElementById("Msg").innerHTML = message;
    } else {
        document.getElementById("Msg").innerHTML = "";
    }
}


function setDefaults() {
    var myForm = document.form1;
    if (statusMessage.length != 0 ) {
        alert (statusMessage);
    }
    myForm.partNum.focus();
}


function My_Validator() {
    var myForm = document.form1;
    
    // Check Part Number field here
    
    junk = myForm.partNum.value;
    if (junk.length == 0) {
        alert ("Please enter a valid Part Number.");
        myForm.partNum.focus();
        return false;
    }
    
        
    // Check Audit User ID

    if (myForm.auditUserID.value == "") {
        alert ("Please enter your User ID.");
        myForm.auditUserID.focus();
        return false;
    } 
    

    // Validation is complete
    
           
            
    myForm.validation.value = "OK";
    return true;
}

//-->
</script>

</head>

<body onload="setDefaults()">

    <script language="JavaScript" type="text/javascript">
    <!--
        var statusMessage = "${statusMessage}";

    //-->    
    </script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="gpsdmf2.do" method="post" onsubmit="return My_Validator()">
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
                    Parametric Search Database Maintenance<br />
			Modify Parametric Data
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
            </td>
        </tr>
    </table>
        
        <table border="0" align="center" width="100%">
            <tr>
                <td colspan="2" align="center">
                    <p>Enter the Part Number whose Parametric Data you wish to change.
                    </p><br />
                </td>
            </tr>
            
            <tr>
                <td colspan="2" align="center">
                    <div id="Msg">&nbsp;
                    </div>
                </td>
            </tr>
            
<!--  Part Number  -->

      <tr>
        <td align="right"  width="25%">
          <span class="requiredLabel"> Part Number: &nbsp;</span>
        </td>
        <td align="left" ><span class="datafield">
            <input type="text" size="32" maxlength="32" name="partNum"  
                value =""
                onblur="checkPartNumber()"
                onmouseover="showTip(event, 'moPartNum')"
                onmouseout="hideTip('moPartNum')" 
          /> 
        </span></td>
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
	onmouseover="showTip(event, 'moModify')" 
        onmouseout="hideTip()"
	/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;"
                name="B3" onclick="Javascript: window.location='gpsdf.jsp'; " 
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
