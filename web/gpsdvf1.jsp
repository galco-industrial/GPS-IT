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
	<title>Galco Parametric Search - Search by Values</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        
        <!-- gpsdvf1.jsp
        
        I get the Product Line, Family, Subfamily, and Manufacturer
        for this search then xfer to gpsdvf2.java

        Modification History
        
        version 1.2.00
        
        09/27/2007 DES Modified to use Ajax to obtain line/family/subfamily codes
                        and Manufacturer codes.
       
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

function familyCodeHasChanged() {
    var myForm = document.form1;
    var line = myForm.productLineCode.value;
    var family = myForm.familyCode.value;
    setFamilyName();
    subfamilyCodesUpdate(line, family);
}

function familyCodesLookUp(line) {
    ajaxFamReq = createAjaxRequest(); // ajaxFamReq is a global
    ajaxFamReq.onreadystatechange = familyCodesRequestStateChange;
    ajaxFamReq.open ("GET", "getFamilyCodes.do?productLine=" 
        + encodeURIComponent(line) + "&ts=" + new Date().getTime(), true);
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
    var oListbox = myForm.familyCode;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing codes
    subfamilyCodesUpdate(line,"0");
       
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
    var oListbox = myForm.familyCode;
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
        var work = fname[0].firstChild.nodeValue;
        oOption.appendChild(document.createTextNode(work));
        var val = code[0].firstChild.nodeValue;
        oOption.setAttribute("value", val);
        if (initialize) {
            if (prevFamily == val) {
                oOption.setAttribute("selected", true);
                setFamilyName();
            }
        }
        oListbox.appendChild(oOption);
    }
    if (initialize) {
        subfamilyCodesUpdate(prevProductLine, prevFamily)
    }
}

function getMessage(divName) {
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    if (divName == "moFamilyCode") {return "Select a Product Family from this list.";}
    if (divName == "moInStockOnly") {return "Check Yes to search for in-stock parts only."}
    if (divName == "moManufacturerCode") {return "Select a Manufacturer from this list.";}
    if (divName == "moProductLineCode") {return "Select a Product Line from this list.";}
    if (divName == "moSearch") {return "Click to Search for part numbers by parametric values.";}
    if (divName == "moSubfamilyCode") {return "Select a Product Subfamily from this list.";}
    return "";
}

function manufacturerCodeHasChanged() {
    setManufacturerName();
}

function manufacturerCodesLookUp(fcode, scode) {
    //alert ("scode is " + scode);
    ajaxManCodeReq = createAjaxRequest(); // ajaxManCodeReq is a global
    ajaxManCodeReq.onreadystatechange = manufacturerCodesRequestStateChange;
    ajaxManCodeReq.open ("GET", "getManCodes.do?family=" + encodeURIComponent(fcode)
        + "&subfamily=" + encodeURIComponent(scode)
        + "&ts=" + new Date().getTime(), true);
    ajaxManCodeReq.send (null);
}

function manufacturerCodesRequestStateChange() {
    if (ajaxManCodeReq.readyState == 4) {
        if (ajaxManCodeReq.status == 200) {
            manufacturerCodesXMLParser();
        } else {
            alert ("Unexpected Error " + ajaxManCodeReq.status);
        }
    }
}

function manufacturerCodesUpdate(line, family, subfamily) {
    var myForm = document.form1;
    var oListbox = myForm.manufacturerCode;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing manufacturerCode options
       
    // Create first entry in Select Box
    
    if (line == "0") {      // No product line selected
        oOption = document.createElement("option");
        oOption.appendChild(document.createTextNode("Please choose a Product Line first"));
        oOption.setAttribute("value", "0" );
        oListbox.appendChild(oOption);
        return;
    }
    
    if (family == "0") {      // No family selected
        oOption = document.createElement("option");
        oOption.appendChild(document.createTextNode("Please choose a Family first"));
        oOption.setAttribute("value", "0" );
        oListbox.appendChild(oOption);
        return;
    }
 
    if (subfamily == "0") {      // No subfamily selected
        oOption = document.createElement("option");
        oOption.appendChild(document.createTextNode("Please choose a Subfamily first"));
        oOption.setAttribute("value", "0" );
        oListbox.appendChild(oOption);
        return;
    }
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Working..."));
    oOption.setAttribute("value", "0" );
    oListbox.appendChild(oOption);    
    
    // Get Manufacturer Codes for this Family/Subfamily from the server
    
    manufacturerCodesLookUp(family, subfamily);
}
function manufacturerCodesXMLParser() {
    var results = ajaxManCodeReq.responseXML.getElementsByTagName("manufacturers");
    var manufacturer = results[0].getElementsByTagName("manufacturer");
    var myForm = document.form1;
    var oListbox = myForm.manufacturerCode;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing manufacturer options
       
    // load new Manufacturer Codes for this Family/Subfamily
    
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Please choose a Manufacturer"));
    oOption.setAttribute("value", "0" );
    oListbox.appendChild(oOption);
    
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("All Manufacturers"));
    oOption.setAttribute("value", "*" );
    if (initialize) {
        if (prevManufacturer == "*") {
            oOption.setAttribute("selected", true);
            setManufacturerName();
        }
    }
    oListbox.appendChild(oOption);
        
    for (var i = 0; i < manufacturer.length; i++) {
        var code = manufacturer[i].getElementsByTagName("code");
        var mname = manufacturer[i].getElementsByTagName("name");
        oOption = document.createElement("option");
        var work = mname[0].firstChild.nodeValue;
        oOption.appendChild(document.createTextNode(work));
        var val = code[0].firstChild.nodeValue;
        oOption.setAttribute("value", val);
        if (initialize) {
            if (prevManufacturer == val) {
                oOption.setAttribute("selected", true);
                setManufacturerName();
            }
        }
        oListbox.appendChild(oOption);    
    }
    if (initialize) {
        initialize = false;
    }
}

function productLineCodeHasChanged() {
    var myForm = document.form1;
    var line = myForm.productLineCode.value;
    setProductLineName();
    familyCodesUpdate(line);
}

function setDefaults() {
    var myForm = document.form1;
    var work = "";
    if (prevProductLine.length > 0 && prevFamily.length > 0 
        && prevSubfamily.length > 0 && prevManufacturer.length > 0) {
            initialize = true;
            setProductLine(prevProductLine);
            //setFamilyName();
            //setSubfamilyName();
            //setManufacturerName();
    }
    if (statMsg.length != 0) {
        alert (statMsg);
    }
    myForm.productLineCode.focus();
}

function setFamilyName() {
    myForm = document.form1;
    var i = myForm.familyCode.selectedIndex;
    myForm.familyName.value = myForm.familyCode.options[i].text;
    //alert ("Family Name is set to " + myForm.familyName.value);
}

function setManufacturerName() {
    var myForm = document.form1;
    var i = myForm.manufacturerCode.selectedIndex;
    myForm.manufacturerName.value = myForm.manufacturerCode.options[i].text;
    //alert ("Manufacturer Name is set to " + myForm.manufacturerName.value);
}

function setProductLine(line) {
    var myForm = document.form1;
    var work = myForm.productLineCode;
    for (var i = 0; i < work.length; i++) {
        var x = work.options[i].value;
        if (x == line) {
            work.selectedIndex = i ;
            break;
        }
    }
    productLineCodeHasChanged();
}

function setProductLineName() {
    var myForm = document.form1;
    var i = myForm.productLineCode.selectedIndex;
    myForm.productLineName.value = myForm.productLineCode.options[i].text;
    //alert ("Product Line Name is set to " + myForm.productLineName.value);
}

function setSubfamilyName() {
    myForm = document.form1;
    var i = myForm.subfamilyCode.selectedIndex;
    myForm.subfamilyName.value = myForm.subfamilyCode.options[i].text;
    //alert ("Subfamily Name is set to " + myForm.subfamilyName.value);
}

function subfamilyCodeHasChanged() {
    myForm = document.form1;
    setSubfamilyName();
    manufacturerCodesUpdate(myForm.productLineCode.value, myForm.familyCode.value, myForm.subfamilyCode.value);
}

function subfamilyCodesLookUp(code) {
    ajaxSubfamReq = createAjaxRequest(); // ajaxSubfamReq is a global
    ajaxSubfamReq.onreadystatechange = subfamilyCodesRequestStateChange;
    ajaxSubfamReq.open ("GET", "getSubfamilyCodes.do?family=" + encodeURIComponent(code) 
        + "&ts=" + new Date().getTime(), true);
    ajaxSubfamReq.send (null);
}

function subfamilyCodesRequestStateChange() {
    if (ajaxSubfamReq.readyState == 4) {
        if (ajaxSubfamReq.status == 200) {
            subfamilyCodesXMLParser();
        } else {
            alert ("Unexpected Error " + ajaxSubfamReq.status);
        }
    }
}

function subfamilyCodesUpdate(line, family) {
    var myForm = document.form1;
    var oListbox = myForm.subfamilyCode;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing subfamily options
    manufacturerCodesUpdate(line, family, "0");
       
    // Create first entry in Select Box
    
    if (line == "0") {      // No product line selected
        oOption = document.createElement("option");
        oOption.appendChild(document.createTextNode("Please choose a Product Line first"));
        oOption.setAttribute("value", "0" );
        oListbox.appendChild(oOption);
        return;
    }
    
    if (family == "0") {      // No family selected
        oOption = document.createElement("option");
        oOption.appendChild(document.createTextNode("Please choose a Family first"));
        oOption.setAttribute("value", "0" );
        oListbox.appendChild(oOption);
        return;
    }
    
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Working..."));
    oOption.setAttribute("value", "0" );
    oListbox.appendChild(oOption);    
    
    // Get Subfamily Codes for this Family from the server
    
    subfamilyCodesLookUp(family);
}

function subfamilyCodesXMLParser() {
    var results = ajaxSubfamReq.responseXML.getElementsByTagName("subfamilies");
    var subfamily = results[0].getElementsByTagName("subfamily");
    var myForm = document.form1;
    var oListbox = myForm.subfamilyCode;
    var oOption = null;
    oListbox.options.length = 0; // Delete existing subfamily options
       
    // load new Subfamily Codes for this Product Line
    
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Please choose a Product Subfamily"));
    oOption.setAttribute("value", "0" );
    oListbox.appendChild(oOption);
    
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("All Subfamilies"));
    oOption.setAttribute("value", "*" );
    if (initialize) {
        if (prevSubfamily == "*") {
            oOption.setAttribute("selected", true);
        }
    }
    oListbox.appendChild(oOption);
        
    for (var i = 0; i < subfamily.length; i++) {
        var code = subfamily[i].getElementsByTagName("code");
        var sname = subfamily[i].getElementsByTagName("name");
        oOption = document.createElement("option");
        var work = sname[0].firstChild.nodeValue;
        oOption.appendChild(document.createTextNode(work));
        var val = code[0].firstChild.nodeValue;
        oOption.setAttribute("value", val);
        if (initialize) {
            if (prevSubfamily == val) {
                oOption.setAttribute("selected", true);
                setSubfamilyName();
            }
        }
        oListbox.appendChild(oOption);    
    }
    if (initialize) {
        manufacturerCodesUpdate(prevProductLine, prevFamily, prevSubfamily);
    }
}

function validateLineFamSubfam() {
    var myForm = document.form1;
    if (myForm.productLineCode.selectedIndex == 0) {
        alert ("Please select a Product Line");
        myForm.productLineCode.focus();
        return false;
    }
    if (myForm.familyCode.selectedIndex == 0) {
        alert ("Please select a Family Code");
        myForm.familyCode.focus();
        return false;
    }
    if (myForm.subfamilyCode.selectedIndex == 0) {
        alert ("Please select a Subfamily Code");
        myForm.subfamilyCode.focus();
        return false;
    }
    if (myForm.manufacturerCode.selectedIndex == 0) {
        alert ("Please select a Manufacturer");
        myForm.manufacturerCode.focus();
        return false;
    }
    // Everything is OK if we get here
    return true;
}

function My_Validator() {
    var myForm = document.form1;
    var work;
    
    if (!validateLineFamSubfam()) {
        return false;
    }
    
    setProductLineName();
    setFamilyName();
    setSubfamilyName();
    setManufacturerName();
    
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

        var productLines = new Array();
        var iL = 0;
        var initialize = false; 
        var prevProductLine = "${sessionScope.sbProductLineCode}";
        var prevFamily = "${sessionScope.sbFamilyCode}";
        var prevSubfamily = "${sessionScope.sbSubfamilyCode}";
        var prevManufacturer = "${sessionScope.sbManufacturerCode}";
        var userID = "${sessionScope.auditUserID}";
        var toolTips = "${sessionScope.enableToolTips}";
        var familyName = "";
        var subfamilyName = "";
        var manufacturerName = "";        
        var statMsg = "${statusMessage}";

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

<form name="form1" action="gpsdvf2.do" method=post onsubmit="return My_Validator()">
<p>
    <input type="hidden" name="validation" value="Error" />
    <input type="hidden" name="productLineName" value = "" />
    <input type="hidden" name="familyName" value = "" />
    <input type="hidden" name="subfamilyName" value = "" />
    <input type="hidden" name="manufacturerName" value = "" />
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
                    Parametric Search<br /><br />
			Search Database by Values
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
                    <p>Select the Product Line, Family, Subfamily, and Manufacturer to
                    be used for this Parametric Search Operation.
                    </p><br />
                </td>
            </tr>
           
<!-- Product Line -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Product Line:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="productLineCode" size="1"
                        onchange="productLineCodeHasChanged()"
                        onmouseover="showTip(event, 'moProductLineCode', 50, 100)"
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
                    <select name="familyCode" size="1"
                        onchange="familyCodeHasChanged()"
                        onmouseover="showTip(event, 'moFamilyCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="0">Please select a Product Line first</option>
                    </select>
                </td>
            </tr>

<!-- Product Subfamily -->
            
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Product Subfamily:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="subfamilyCode" size="1"
                        onchange="subfamilyCodeHasChanged()"
                        onmouseover="showTip(event, 'moSubfamilyCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="0">Please select a Product Line First</option>
                    </select>
                </td>
            </tr>           

<!-- Manufacturer Code -->
            
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Manufacturer:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="manufacturerCode" size="1"
                        onchange="manufacturerCodeHasChanged()"
                        onmouseover="showTip(event, 'moManufacturerCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="0">Please select a Product Line First</option>
                    </select>
                </td>
            </tr>           
            
<!--  In stock only Search  -->

            <tr>
                <td align="right"><span class="label">
                    In stock only:&nbsp;
                </span></td>
                <td align="left"><span class="dataField">
                    <input type="radio" name="inStockOnly"
<%
                        String gwork = (String) session.getAttribute("inStockOnly");
                        if (gwork == null) {gwork = "";}
                        if (gwork.equals("Y")) {
                            out.println(" checked=\"checked\" ");
                        }
%>   
                        value="Y"
                        onmouseover="showTip(event,'moInStockOnly')" 
                        onmouseout="hideTip()"
                    />
                    Yes&nbsp;&nbsp;&nbsp;&nbsp;
                    <input type="radio" name="inStockOnly"
<%
                        if(gwork.equals("N") || gwork.equals("")) {
                            out.println(" checked=\"checked\" ");
                        }
%>  
                        value="N"
                        onmouseover="showTip(event,'moInStockOnly')" 
                        onmouseout="hideTip()"
                    />
                    No
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
            <input type="submit" value="Search" name="B1" 
	onmouseover="showTip(event, 'moSearch')" 
        onmouseout="hideTip()"
	/>
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" 
                value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" 
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
