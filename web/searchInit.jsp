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
	<title>Galco Parametric Search - Web Search Test</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- Version 1.0.00
        

        
        -->
    
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

//function familyCodeHasChanged() {
//    var myForm = document.form1;
//    var line = myForm.productline.value;
//    var family = myForm.familycode.value;
    // alert ("Family Code has changed.");
//}

function familyCodesLookUp(line) {
    ajaxFamReq = createAjaxRequest(); // ajaxFamReq is a global
    ajaxFamReq.onreadystatechange = familyCodesRequestStateChange;
    ajaxFamReq.open ("GET", "getFamilyCodes.do?productLine=" 
        + escape(line) + "&ts=" + new Date().getTime(), true);
    ajaxFamReq.send (null);
}

function familyCodesRequestStateChange() {
    if (ajaxFamReq.readyState == 4) {
        if (ajaxFamReq.status == 200) {
            familyCodesXMLParser();
        } else {
            alert ("Unexpected Error " + ajaxFamReq.status);
        }
    }
}
function familyCodesUpdate(line) {
    var myForm = document.form1;
    var oListbox = myForm.familycode;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing codes
    
       
    // Create first entry in Select Box
    
    if (line == "0") {      // No product line selected
        oOption = document.createElement("option");
        oOption.appendChild(document.createTextNode("Please choose a Product Line first"));
        oOption.setAttribute("value", "0" );
        oListbox.appendChild(oOption);
        return;
    }
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Working..."));
    oOption.setAttribute("value", "0" );
    oListbox.appendChild(oOption);    
    
    // Get Family Codes for this Product Line from the server
    
    familyCodesLookUp(line);
}

function familyCodesXMLParser() {
    var results = ajaxFamReq.responseXML.getElementsByTagName("families");
    var family = results[0].getElementsByTagName("family");
    var myForm = document.form1;
    var oListbox = myForm.familycode;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing codes
       
    // load new Family Codes for this Product Line
    
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Please choose a Product Family"));
    oOption.setAttribute("value", "0" );
    oListbox.appendChild(oOption);
    
    for (var i = 0; i < family.length; i++) {
        var code = family[i].getElementsByTagName("code");
        var fname = family[i].getElementsByTagName("name");
        oOption = document.createElement("option");
        oOption.appendChild(document.createTextNode(fname[0].firstChild.nodeValue));
        var val = code[0].firstChild.nodeValue;
        oOption.setAttribute("value", val);
        if (initialize) {
            if (prevFamily == val) {
                oOption.setAttribute("selected", true);
            }
        }
        oListbox.appendChild(oOption);
    }

}

function getMessage(divName) {
    if (divName == "moExit") {return "Click to return to the main menu";}
    if (divName == "moSearch") {return "Click to start a Search";}
    if (divName == "moProductLine") {return "Select a Product Line from this list.";}
    if (divName == "moFamilyCode") {return "Select a Family Code from this list.";}
    return "";
}

function noEnter(){
    return !(window.event && window.event.keyCode == 13); 
}

function productLineHasChanged() {
    var myForm = document.form1;
    var line = myForm.productlinecode.value;
    familyCodesUpdate(line);
}

function setDefaults() {
    var myForm = document.form1;
    if (prevLine.length > 0 ) {
        initialize = true;
        setLine(prevLine);
    }
    myForm.productlinecode.focus();
}

function setLine(line) {
    var myForm = document.form1;
    var work = myForm.productlinecode;
    for (var i = 0; i < work.length; i++) {
        var x = work.options[i].value;
        if (x == line) {
            work.selectedIndex = i ;
            break;
        }
    }
    productLineHasChanged();
}



function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
    // First Check Product Line
    
    if (myForm.productlinecode.selectedIndex == 0) {
        alert ("Please select a Product Line.");
        myForm.productlinecode.focus();
        return false;
    }
    
    // Next chack Family Code
    
    if (myForm.familycode.selectedIndex == 0) {
        alert ("Please select a Family.");
        myForm.familycode.focus();
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

        var productLines = new Array();
        var iL = 0;
        var initialize = false; 
        var prevLine = ""
        <c:forEach var="item" items="${lines}">
            productLines[iL++] = new Array(${item});
        </c:forEach>
        
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="searchDispatcher.do" method="post" onsubmit="return My_Validator()">
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
                    Web Search Test
                </h2>
            </td>
        </tr>
    </table>
        
    <table border="0" align="center" width="100%">

            
<!-- Product Line -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Product Line:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="productlinecode" size="1"
                        onchange="productLineHasChanged()"
                        onmouseover="showTip(event, 'moProductLine', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="0">Please select a Product Line</option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            for (var i = 0; i < iL; i++){
                                document.write("<option ");
                                document.write(" value=\"" + productLines[i][0] + "\">" + productLines[i][1]+"</option>");
                            }
                            document.close();
                        //-->
                        </script>
                    </select>
                </td>
            </tr>
            
<!-- Product Family -->
 
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Product Family:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="familycode" size="1"
                        onchange="submit()"
                        onmouseover="showTip(event, 'moFamily', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="0">Please select a Product Line first</option>
                    </select>
                </td>
            </tr>

    </table>
<br /><br />

<!--     Continue     -->
          <center>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='index.jsp'; " 
	onmouseover="showTip(event, 'moExit')" 
        onmouseout="hideTip()"
	/>
          </center>

  <p>
    <img src="w3cxhtml10.bmp"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
  </p>
    </form>
    </div>  
</body>
</html>
