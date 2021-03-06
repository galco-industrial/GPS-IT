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
	<title>Galco Parametric Search - Copy Rule Set</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        
        <jsp:useBean id="sRuleSet" class="gps.util.GPSrules" scope="session"/>
        

    <!-- gpsruf6.jsp

    Modification History

    version 1.3.00


    04/23/2008      DES     Modified to support 4 Divisions

    -->


<script language="JavaScript" type="text/javascript">
<!--

function checkSeqNum() {
    var myForm = document.form1;
    var sn = myForm.newSeqNum.value;
    var localRule = myForm.ruleScope.value == "L";
    if (localRule && sn < 51) {
        alert ("Warning! This local Rule will override any global (family) Rule with the same field number.");
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
    //alert ("4. - Existing family codes have been emptied.");
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
    if (divName == "moContinue") {return "Click to Copy this Rule set into the new Field.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    if (divName == "moFamily") {return "Select a Product Family from this list.";}
    if (divName == "moProductLine") {return "Select a Product Line from this list.";}
    if (divName == "moSubfamily") {return "To Create a Local Rule Set, select a Product Subfamily from this list.";}
    return "";
}

function loadSeqNums() {
    //alert ("7. Sequence Numbers are being loaded.");
    var myForm = document.form1;
    var oListbox = myForm.newSeqNum;
    var values = myForm.sequenceNumbers.value;
    var localRule = myForm.ruleScope.value == "L";
    var oOption;
    var j = 1;
    var k = 51;
    var num;
    var opt;
    
    // load available Field Numbers for this Rule
       
    if (localRule) {
        j = 1;
        k = 100;
    }
    //alert ("Values are:  " + values);
    oListbox.options.length = 0; // Delete existing codes
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Select One"));
    oOption.setAttribute("value", "");
    oListbox.appendChild(oOption);
    for (var i = j; i < k; i++) {
        num = "," + i + ",";
    	if (values.indexOf(num) == -1) {
            opt = "" + i;
            oOption = document.createElement("option");
	    oOption.appendChild(document.createTextNode(opt));
            oOption.setAttribute("value", opt);
            oListbox.appendChild(oOption);
	}
    }
}

function noEnter(){
    return !(window.event && window.event.keyCode == 13); 
}

function productLineHasChanged() {
    var myForm = document.form1;
    var line = myForm.productLine.value;
    //alert ("3. Now updating family codes for " + line);
    familyCodesUpdate(line);
}

function setDefaults() {
    var myForm = document.form1;
    if (prevLine.length > 0 && prevFamily.length > 0 && prevSubfamily.length > 0) {
        initialize = true;
        //alert ("1. Setting line, family, subfamily...");
        setLine(prevLine);
    }
    myForm.productLine.focus();
}

function seqNumsInit(){
    var myForm = document.form1;
    var oListbox = myForm.newSeqNum;
    var oOption;
    oListbox.options.length = 0; // Delete existing codes
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Select a Subfamily Code first"));
    oOption.setAttribute("value", "");
    oListbox.appendChild(oOption);
    //alert ("6. - Existing sequence numbers have been emptied.");
}

function seqNumsLookUp(fcode, scode) {
    var myForm = document.form1;
    var oListbox = myForm.newSeqNum;
    var oOption;
    oListbox.options.length = 0; // Delete existing codes
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Working..."));
    oOption.setAttribute("value", "");
    oListbox.appendChild(oOption);
    ajaxSNReq = createAjaxRequest(); // ajaxSNReq is a global
    ajaxSNReq.onreadystatechange = seqNumsRequestStateChange;
    ajaxSNReq.open ("GET", "getExistingSeqNums.do?familyCode=" 
        + encodeURIComponent(fcode) + "&subfamilyCode=" + encodeURIComponent(scode) 
        + "&ts=" + new Date().getTime(), true);
    ajaxSNReq.send (null);
}

function seqNumsRequestStateChange() {
    if (ajaxSNReq.readyState == 4) {
        if (ajaxSNReq.status == 200) {
            //alert ("I got an AJAX response.");
            seqNumsParser();
        } else {
            alert ("Unexpected Error " + ajaxSNReq.status);
        }
    }
}

function seqNumsParser() {
    var myForm = document.form1;
    myForm.sequenceNumbers.value = ajaxSNReq.responseText;
    loadSeqNums();
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
    //alert ("2. Product line has been set for " + line);
    productLineHasChanged();
}

function subfamilyCodeHasChanged() {
    var myForm = document.form1;
    seqNumsLookUp(myForm.familyCode.value, myForm.subfamilyCode.value);
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
    //alert ("5. - Existing subfamily codes have been emptied.");
    seqNumsInit();
    
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
        
    }
    subfamilyCodeHasChanged();

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
    
    myForm.productLineName.value = myForm.productLine.options[myForm.productLine.selectedIndex].text;
    myForm.familyName.value = myForm.familyCode.options[myForm.familyCode.selectedIndex].text;
    myForm.subfamilyName.value = myForm.subfamilyCode.options[myForm.subfamilyCode.selectedIndex].text;
        
    // Set Rule Scope here (Scope is implied by subfamily code; i.e., All = Global)
    
    myForm.ruleScope.value = "L";
    if (myForm.subfamilyCode.value == "*" ) {
        myForm.ruleScope.value = "G";
    }
   
    if (myForm.newSeqNum.value == "") {
        alert ("Please select a Sequence Number for the rule you are copying.");
        myForm.newSeqNum.focus();
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
        var prevLine = "${sessionScope.sbProductLineCode}";
        //<jsp:getProperty name="sRuleSet" property="productLineCode" />";
        var prevFamily = "${sessionScope.sbFamilyCode}";
        //<jsp:getProperty name="sRuleSet" property="familyCode" />";
        var prevSubfamily = "${sessionScope.sbSubfamilyCode}";
        //<jsp:getProperty name="sRuleSet" property="subfamilyCode" />";
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

<form name="form1" action="gpsruf7.do" method="post" onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
            <input type="hidden" name="ruleScope" 
                value="<jsp:getProperty name="sRuleSet" property="ruleScope" />"
            />            
            <input type="hidden" name="productLineName" value="" />
            <input type="hidden" name="familyName" value="" />
            <input type="hidden" name="sequenceNumbers" value="" />
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
                    Parametric Search<br />Rules Maintenance<br />
			Copy Family (Global)/Subfamily (Local) Rule Set
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
                    <p>Select the Product Line/Family/Subfamily codes for the NEW field.
                    </p><br />
                </td>
            </tr>
                        
<!-- Product Line -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">New Product Line:&nbsp;</span>
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
                            document.close();
                        //-->
                        </script>
                    </select>
                </td>
            </tr>
            
<!-- Product Family -->
 
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">New Product Family:&nbsp;</span>
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
                    <span class="requiredLabel">New Product Subfamily:&nbsp;</span>
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
            
<!-- Field Number -->
    
      <tr>
        <td align="right" width="25%">
          <span class="requiredLabel">
            New Field No.:&nbsp;
          </span>
        </td>
        <td align="left" width="75%">
          <select name="newSeqNum" size="1"
            onchange="checkSeqNum()"
            onmouseover="showTip(event, 'moSeqNum', 50, 100)"
            onmouseout="hideTip()">
          </select>
        </td>
      </tr>
      
<!--     Continue      -->

      <tr>
        <td colspan="2">
          <center>
            <br />
            <input type="submit" value="Copy" name="B1" 
                onmouseover="showTip(event, 'moContinue')" 
                onmouseout="hideTip()"
            />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" 
                value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" 
                name="B3" 
                onclick="Javascript: window.location='gpsrf.jsp'; " 
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