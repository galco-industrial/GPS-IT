<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
	<title>Galco Parametric Search - Read Part Number Data</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
<script language="JavaScript" type="text/javascript">
<!--

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

function checkPartNumber() {
    var myForm = document.form1;
    var work = myForm.partNumber1.value;
    work = work.toUpperCase();
    myForm.partNumber1.value = work;
    if (!checkCharSet(work, UC + NU + SP + "#$%^&*()_-=+/:;>.,?")) {
        myForm.partNumber1.focus();
    }
    partNumNameCheck();
    return;
    
}

function getMessage(divName) {
    if (divName == "moAuditUserID") {return "Enter your initials.";}
    if (divName == "moExit"){return "Click to abandon this operation and return to the previous Menu.";}
    if (divName == "moPartNum") {return "Enter a Part Number here.";}
    if (divName == "moRead") {return "Click to read the parametric data for this Part Number.";}
    return "";
}  

function initValidation() {
    document.form1.validation.value = "Error";
}

function partNumExistsCheck() {
    var myForm = document.form1;
    var work = myForm.partNumber1.value;
    ajaxPartNumReq = createAjaxRequest(); // ajaxPartNumReq is a global
    ajaxPartNumReq.onreadystatechange = partNumRequestStateChange;
    ajaxPartNumReq.open ("GET", "getPartNumInfo.do?partNum=" + encodeURIComponent(work)
        + "&ts=" + new Date().getTime(), false); // synchronous call
    ajaxPartNumReq.send (null);
}


function partNumNameCheck() {
    var myForm = document.form1;
    var work = myForm.partNumber1.value;
    work = deleteLeadingSpaces(deleteTrailingSpaces(work));
    work = work.toUpperCase();
    myForm.partNumber1.value = work;
    if (!checkCharSet(work, UC + NU + SP + "#$%^&*()_-=+/:;>.,?" )) {
        myForm.partNumber1.focus();
        return "false";
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
            partNumInfoXMLParser();
        } else {
            alert ("Unexpected Error " + ajaxPartNumReq.status);
            var myForm = document.form1;
            myForm.partNumber1.value = "";
        }
    }
}

function partNumExistsParser() {
    var results = ajaxPartNumReq.responseText;
    if (results.indexOf("false") != -1) {
        alert ("Error - this Part Number does not exist.");
        var myForm = document.form1;
        myForm.partNumber1.focus();
        return;
    }
    if (results.indexOf("true") != -1) {
        return;
    }
    alert ("An unexpected error occurred when checking the Part Number.");
    var myForm = document.form1;
    myForm.partNumber1.value = "";
}

function partNumInfoXMLParser() {
    //alert ( ajaxPartNumReq.responseText);
    var pn = "";
    var fc = "";
    var sc = "";
    var hpsd = "false";
    var message = "";
    var myForm = document.form1;
    var results = ajaxPartNumReq.responseXML.getElementsByTagName("partInfo");
    var PN = results[0].getElementsByTagName("partNum");
    if (PN[0]) {
        pn = PN[0].firstChild.nodeValue;
        myForm.partNumXML.value = pn;
        //alert (pn);
    }
    myForm.partFamilyCodeXML.value = "";
    var FC = results[0].getElementsByTagName("partFamilyCode");
    if (FC[0]) {
        fc = FC[0].firstChild;
        if (fc) {
            fc = fc.nodeValue;
            myForm.partFamilyCodeXML.value = fc;
            myForm.familyCode.value = fc;
            //alert (" fc is " + fc);
        }
    }
    myForm.partSubfamilyCodeXML.value = "";
    var SC = results[0].getElementsByTagName("partSubfamilyCode");
    if (SC[0]) {
        sc = SC[0].firstChild;
        if (sc) {
            sc = sc.nodeValue;
            myForm.partSubfamilyCodeXML.value = sc;
            //alert (" fc is " + fc);
        }
    }
    var HPSD = results[0].getElementsByTagName("partHasPSData");
    if (HPSD[0]) {
        hpsd = HPSD[0].firstChild.nodeValue;
        myForm.partHasPSDataXML.value = hpsd;
        //alert (hpsd);
    }
    if (pn.length == 0) {
        message = "This Part Number does not exist.";
        alert (message);
        var myForm = document.form1;
        document.getElementById("Msg").innerHTML = message;
        myForm.partNumber1.focus();
        return;
    }
    
    if (hpsd == "true") {
        message = " Family Code " + myForm.partFamilyCodeXML.value;
        message += " Subfamily Code " + myForm.partSubfamilyCodeXML.value;
        message = "This Part Number currently has Parametric Data in" + message;
        document.form1.validation.value = "OK";
    } else {
        message = "This Part Number currently has No Parametric Data.";
        document.form1.validation.value = "";
        
    }
    document.getElementById("Msg").innerHTML = message;
    alert (message);
    return;
}

function myValidator() {
    checkPartNumber();
    if (document.form1.validation.value != "OK") {
        return false;
    }
    
}

//-->
</script>
        
</head>
<body >
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
       
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form method="post" name="form1" action="gpsdrf2.do" onsubmit="return myValidator()" >
<p>
    <input type="hidden" name="validation" value="Error" />
    <input type="hidden" name="close" value="0" />
    <input type="hidden" name="partNumXML" value="" />
    <input type="hidden" name="partFamilyCodeXML" value="" />
    <input type="hidden" name="familyCode" value="" />
    <input type="hidden" name="partSubfamilyCodeXML" value="" />
    <input type="hidden" name="partHasPSDataXML" value="" />
</p>
    
<table border="0" width="100%">

<!-- Logo and Heading  -->

  <tr>
    <td align="center" colspan="2">
	<h2>
        Parametric Search Database<br />
            Read Parametric Data
	</h2>
    </td>
  </tr>
  <tr>
    <td rowspan="3" align="center" width="25%">
      <img src="gl_25.gif" alt="Galco logo" /><br />

	<div class="toolTipSwitch">
		<input type="checkbox" 
<%
		String tip = (String) session.getAttribute("enableToolTips");
                if (tip != null && tip.equals("checked")) {
			out.println(" checked=\"checked\" ");
                }
%> 
		name="enableToolTips"
		value="checked"
		/>
		Enable Tool Tips
	</div>
    </td>
    <td>
        &nbsp;
  </td></tr>
</table>
<br />


<table border="0" width="100%">

<!-- Table Header  -->

    <tr>
        <td align='center' width='25%' >
            <span class='requiredLabel'>
                Fields in RED are required.
            </span>
        </td>
        <td colspan='2'>
            <div id="Msg"> <span class="units">
            ${statusMessage}&nbsp;
            </span></div>
        </td>
    </tr>
    
     
<!--  Part Number  -->

      <tr>
        <td align="right" >
          <span class="requiredLabel"> Part Number: &nbsp;</span>
        </td>
        <td align="left" colspan="2"><span class="datafield">
            <input type="text" size="32" maxlength="32" name="partNumber1"  
                value =""
                onchange = "initValidation()"
                onmouseover="showTip(event, 'moPartNum')"
                onmouseout="hideTip()" 
          /> 
        </span></td>
      </tr>

<!--  Continue -->

      <tr>
        <td colspan="3">
            <br />
            <center>
            <input type="submit" 
                value="Read" name="B1" 
                
                onmouseover="showTip(event, 'moRead')" 
                onmouseout="hideTip()"
            />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" 
                value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" 
                name="B3" onclick="Javascript: window.location='gpsdf.jsp'; " 
                onmouseover="showTip(event, 'moExit')" 
                onmouseout="hideTip('moExit')"
            />
            </center>
        </td>
      </tr>
    </table>
<br /> 
<br />
<p>
    <img src="http://www.w3.org/Icons/valid-xhtml10"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
</p>
</form>
</div>
</body>
</html>
