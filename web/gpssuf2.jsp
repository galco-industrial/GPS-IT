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
	<title>Galco Parametric Search - Copy Select Box</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
                
        <!-- gpssuf2.jsp

        Modification History
        
        version 1.5.00
        
        08/13/2007 DES Modified to use Ajax to obtain line/family/subfamily codes
        
        
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
    var line = myForm.productLine.value;
    var family = myForm.familyCode.value;
    subfamilyCodesUpdate(line, family);
}

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
    if (initialize) {
        subfamilyCodesUpdate(prevLine, prevFamily)
    }
}

function getMessage(divName) {
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moCancel"){return "Click Cancel to return to the previous screen.";}
    if (divName == "moDataType") {return "The Select Box data Type (raw value).";}
    if (divName == "moCopy") {return "Click to Copy this Select Box.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    if (divName == "moFamily") {return "Select a Product Family from this list.";}
    if (divName == "moMaximum") {return "The optional maximum value for Numeric items or maximum length for Strings.";}
    if (divName == "moMinimum") {return "The optional minimum value for Numeric items or minimum length for Strings.";}
    if (divName == "moProductLine") {return "Select a Product Line from this list.";}
    if (divName == "moSelectBoxName") {return "The New Select Box to copy to.";}  
    if (divName == "moSize") {return "The number of options currently defined within this Select Box.";}
    if (divName == "moSrcFamily") {return "This is the Product Family you are copying from.";}
    if (divName == "moSrcSelectBoxName") {return "The Select Box to copy from.";} 
    if (divName == "moSrcSubfamily") {return "This is the Product Subfamily you are copying from.";}
    if (divName == "moSubfamily") {return "Select a Product Subfamily from this list.";}
    return "";
}

function productLineHasChanged() {
    var myForm = document.form1;
    var line = myForm.productLine.value;
    familyCodesUpdate(line);
}

function selectBoxExistsCheck(famCode, subfamCode) {
    var myForm = document.form1;
    var work = myForm.selectBoxName.value;
   
    ajaxSelectBoxReq = createAjaxRequest(); // ajaxSelectBoxReq is a global
    ajaxSelectBoxReq.onreadystatechange = selectBoxRequestStateChange;
    ajaxSelectBoxReq.open ("GET", "doesSelectBoxExist.do?name=" + escape(work) 
        + "&family=" + escape(famCode) + "&subfamily=" + escape(subfamCode) 
        + "&ts=" + new Date().getTime(), true); // asynchronous call
    ajaxSelectBoxReq.send (null);
}

function selectBoxNameCheck() {
    var myForm = document.form1;
    var work = myForm.selectBoxName.value;
    work = deleteSpaces(work);
    work = work.toUpperCase();
    myForm.selectBoxName.value = work;
    if (!checkCharSet(work, UC + NU + "-" )) {
        myForm.selectBoxName.focus();
        return;
    }
    if (work.length != 0) {
        var famCode = myForm.familyCode.value;
        var subfamCode = myForm.subfamilyCode.value;
        selectBoxExistsCheck(famCode, subfamCode);
    }
}

function selectBoxRequestStateChange(){
    if (ajaxSelectBoxReq.readyState == 4) {
        if (ajaxSelectBoxReq.status == 200) {
            selectBoxExistsParser();
        } else {
            alert ("Unexpected Error " + ajaxSelectBoxReq.status);
            var myForm = document.form1;
            myForm.selectBoxName.value = "";
        }
    }
}

function selectBoxExistsParser() {
    var results = ajaxSelectBoxReq.responseText;
    if (results.indexOf("true") != -1) {
        alert ("Error - this Select Box Name already exists.");
        var myForm = document.form1;
        myForm.selectBoxName.focus();
        return;
    }
    if (results.indexOf("false") != -1) {
        return;
    }
    alert ("An unexpected error occurred when checking the Select Box Name.");
    var myForm = document.form1;
    myForm.selectBoxName.value="";
}

function setDefaults() {
    var myForm = document.form1;
    if (prevLine.length > 0 && prevFamily.length > 0 && prevSubfamily.length > 0) {
        initialize = true;
        setLine(prevLine);
    }
    if (statMsg.length != 0) {
        alert (statMsg);
    }
    myForm.productLine.focus();
}
 
function setLine(line) {
    var myForm = document.form1;
    var work = myForm.productLine;
    for (var i = 0; i < work.length; i++) {
        var x = work.options[i].value;
        if (x == line) {
            work.selectedIndex = i ;
            break;
        }
    }
    productLineHasChanged();
}

function subfamilyCodeHasChanged() {
    var myForm = document.form1;
    var line = myForm.productLine.value;
    var family = myForm.familyCode.value;
    var subfamily = myForm.subfamilyCode.value;
    myForm.selectBoxName.value = "";
}

function subfamilyCodesLookUp(code) {
    ajaxSubfamReq = createAjaxRequest(); // ajaxSubfamReq is a global
    ajaxSubfamReq.onreadystatechange = subfamilyCodesRequestStateChange;
    ajaxSubfamReq.open ("GET", "getSubfamilyCodes.do?family=" + escape(code) 
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
        oOption.appendChild(document.createTextNode(sname[0].firstChild.nodeValue));
        var val = code[0].firstChild.nodeValue;
        oOption.setAttribute("value", val);
        if (initialize) {
            if (prevSubfamily == val) {
                oOption.setAttribute("selected", true);
            }
        }
        oListbox.appendChild(oOption);    
    }
    if (initialize) {
        initialize = false;
        selectBoxNamesLookUp(myForm.familyCode.value, myForm.subfamilyCode.value);
        myForm.selectBoxName.focus();
    }
}

function validateLineFamSubfam() {
    var myForm = document.form1;
    if (myForm.productLine.selectedIndex == 0) {
        alert ("Please select a Product Line");
        myForm.productLine.focus();
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
    // Everything is OK if we get here
    return true;
}

function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
    if (!validateLineFamSubfam()) {
        return false;
    }
   
    myForm.familyName.value = myForm.familyCode.options[myForm.familyCode.selectedIndex].text;
    myForm.subfamilyName.value = myForm.subfamilyCode.options[myForm.subfamilyCode.selectedIndex].text;
    if (myForm.selectBoxName.value == "") {
        alert ("Please enter a Select Box Name.");
        myForm.selectBoxName.focus();
        return false;
    }
    
    work = myForm.auditUserID.value;
    if (work.length == 0) {
        alert ("Please enter a valid User ID.");
        myForm.auditUserID.focus();
        return false;
    }
    
    if (false) {
        alert ("srcFamilyCode is " + myForm.srcFamilyCode.value);
        alert ("srcSubfamilyCode is " + myForm.srcSubfamilyCode.value);
        alert ("Old Select Box Name is " + myForm.srcSelectBoxName.value);
        alert ("New Product Line is " + myForm.productLine.value);
        alert ("New FamilyCode is " + myForm.familyCode.value);
        alert ("New SubfamilyCode is " + myForm.subfamilyCode.value);
        alert ("New Select Box Name is " + myForm.selectBoxName.value);
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

        var initialize;
        ajaxSBReq = null;
        var productLines = new Array();
        var iL = 0;
        var initialize = false; 
        var prevLine = "" //${sessionScope.sbProductLineCode}";
        var prevFamily = "" //${sessionScope.sbFamilyCode}";
        var prevSubfamily = "" //${sessionScope.sbSubfamilyCode}";
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

<form name="form1" action="gpssuf3.do" method=post onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
            <input type="hidden" name="srcFamilyCode" value="${sbFamilyCode}" />
            <input type="hidden" name="srcSubfamilyCode" value="${sbSubfamilyCode}" />
            <input type="hidden" name="familyName" value="" />
            <input type="hidden" name="subfamilyName" value="" />
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
			Copy a Select Box
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
                    <p>When a Select Box is Copied, all existing options
                    are copied to the destination Select Box.
                    </p><br />
                </td>
            </tr>

 <!-- Family Name -->
     
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Source Family:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="srcFamily" size="36"
                        value = "${srcFamilyName}"
                        readonly = "readonly"
                        onmouseover="showTip(event, 'moSrcFamily')"
                        onmouseout="hideTip()" 
                    />
                </span>
                </td>
            </tr>   
      
               
<!-- Subfamily Name -->
     
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Source Subfamily:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="srcSubfamily" size="36"
                        value = "${srcSubfamilyName}"
                        readonly = "readonly"
                        onmouseover="showTip(event, 'moSrcSubfamily')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr> 
              
            
<!-- source select box name -->            
            
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Source Select Box Name:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="srcSelectBoxName" size="36"
                        value = "${srcSelectBoxName}"
                        readonly = "readonly"
                        onmouseover="showTip(event, 'moSrcSelectBoxName', 50, 100)"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            
<!-- Raw Data Type -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Data Type:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="dataType" size="16" 
                        value = "${dataType}"
                        readonly = "readonly"
                        onmouseover="showTip(event, 'moDataType', 50, 100)"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
 
 <! Minimum Length -->          
            
            <tr>
                <td align="right">
                    <span class="fixedLabel">Minimum Text Length:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="minimum" size="2"
                        value = "${minimum}"
                        readonly = "readonly"
                        onmouseover="showTip(event, 'moMinimum', 50, 100)"
                        onmouseout="hideTip()" 
                    />
                    </span>
                </td>
            </tr>
            
 <! Maximum Length -->          
            
            <tr>
                <td align="right">
                    <span class="fixedLabel">Maximum Text Length:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="maximum" size="2" 
                        value = "${maximum}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moMaximum', 50, 100)"
                        onmouseout="hideTip()" 
                    />
                    </span>
                </td>
            </tr>
            
<!-- Number of Existing Options -->          
            
            <tr>
                <td align="right">
                    <span class="fixedLabel">Options:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="size" size="4" 
                        value = "${size}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moSize', 50, 100)"
                        onmouseout="hideTip()" 
                    />
                    </span>
                </td>
            </tr>

            <!-- Product Line -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Product Line:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="productLine" size="1"
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
                        onmouseover="showTip(event, 'moFamily', 50, 100)"
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
                        onmouseover="showTip(event, 'moSubfamily', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="0">Please select a Product Line First</option>
                    </select>
                </td>
            </tr>           
            
<!-- New Select Box Name -->            

            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Destination<br /> Select Box Name:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="selectBoxName" size="36" maxlength="24"
                        onblur="selectBoxNameCheck()"
                        onmouseover="showTip(event, 'moSelectBoxName')"
                        onmouseout="hideTip()"
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
  <table>    

<!--     Continue      -->

<table border="0" width="100%">
      <tr>
        <td colspan="2">
          <center>
            <br />
            <input type="submit" value="Copy" name="B1"   
        	onmouseover="showTip(event, 'moCopy')" 
                onmouseout="hideTip()"
            />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;Cancel&nbsp;&nbsp;&nbsp;" name="B9" onclick="Javascript: window.location='gpssuf1.do'; " 
	onmouseover="showTip(event, 'moCancel')" 
        onmouseout="hideTip()"
	/>
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpssf.jsp'; " 
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