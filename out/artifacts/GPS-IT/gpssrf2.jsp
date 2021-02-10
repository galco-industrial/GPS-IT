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
	<title>Galco Parametric Search - Rename Select Box</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
                
        <!-- gpssrf2.jsp

        Modification History
        
        version 1.5.00
        
        08/13/2007 DES Modified to use Ajax to obtain line/family/subfamily codes
        
        
        -->


<script language="JavaScript" type="text/javascript">
<!--

function checkSelectBoxName() {
    var myForm = document.form1;
    var work = myForm.selectBoxName.value;
    work = deleteSpaces(work);
    work = work.toUpperCase();
    myForm.selectBoxName.value = work;
    if (!checkCharSet(work, UC + NU + "-" )) {
        myForm.selectBoxName.focus();
        return;
    }
    checkIfSelectBoxExists();
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
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moCancel"){return "Click Cancel to return to the previous screen.";}
    if (divName == "moDataType") {return "The Select Box data Type (raw value).";}
    if (divName == "moRename") {return "Click to Rename this Select Box.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    if (divName == "moFamilyName") {return "This is the Family name for the rule that uses this Select Box.";}
    if (divName == "moMaximum") {return "The optional maximum value for Numeric items or maximum length for Strings.";}
    if (divName == "moMinimum") {return "The optional minimum value for Numeric items or minimum length for Strings.";}
    if (divName == "moOldSelectBoxName") {return "The Old Select Box Name.";} 
    if (divName == "moSelectBoxName") {return "The New Select Box Name.";}  
    if (divName == "moSize") {return "The number of options currently defined within this Select Box.";}
    if (divName == "moSubfamilyName") {return "This is the Subfamily name for the rule that uses this Select Box.";}
    return "";
}
 
function selectBoxExistsCheck(famCode, subfamCode) {
    var myForm = document.form1;
    var work = myForm.selectBoxName.value;
   
    ajaxSelectBoxReq = createAjaxRequest(); // ajaxSelectBoxReq is a global
    ajaxSelectBoxReq.onreadystatechange = SelectBoxRequestStateChange;
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

function SelectBoxRequestStateChange(){
    if (ajaxSelectBoxReq.readyState == 4) {
        if (ajaxSelectBoxReq.status == 200) {
            SelectBoxExistsParser();
        } else {
            alert ("Unexpected Error " + ajaxSelectBoxReq.status);
            var myForm = document.form1;
            myForm.selectBoxName.value = "";
        }
    }
}

function SelectBoxExistsParser() {
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
    myForm.selectBoxName.focus();
}

function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
    work = myForm.selectBoxName.value;
    if (work.length == 0) {
        alert ("Please enter New Select Box Name.");
        myForm.selectBoxName.focus();
        return false;
    }
    
    work = myForm.auditUserID.value;
    if (work.length == 0) {
        alert ("Please enter a valid User ID.");
        myForm.auditUserID.focus();
        return false;
    }
    
    if (iR != 0) {
        alert (" " + iR + " references in the Rules database will be modified.");
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
    
        var references = new Array();
        var iR = 0;
                
        <c:forEach var="item" items="${references}">
            references[iR++] = new Array(${item});
        </c:forEach>
                
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
   
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="gpssrf3.do" method=post onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
            <input type="hidden" name="familyCode" value="${familyCode}" />
            <input type="hidden" name="subfamilyCode" value="${subfamilyCode}" />
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
			Rename Select Box
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
                    <p>When a Select Box is Renamed, all existing references to it
                    within the Rules Table will be changed.
                    </p><br />
                </td>
            </tr>
            
  <!-- Family Name -->
     
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Family:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="family" size="36"
                        value = "${familyName}"
                        readonly = "readonly"
                        onmouseover="showTip(event, 'moFamilyName', 50, 100)"
                        onmouseout="hideTip()" 
                    />
                </span>
                </td>
            </tr>   
      
               
     <!-- Subfamily Name -->
     
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Subfamily:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="subfamily" size="36"
                        value = "${subfamilyName}"
                        readonly = "readonly"
                        onmouseover="showTip(event, 'moSubfamilyName', 50, 100)"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr> 
  
            
<!-- old Select Box Name -->            
            
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Old Select Box Name:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="oldSelectBoxName" size="36"
                        value = "${selectBoxName}"
                        readonly = "readonly"
                        onmouseover="showTip(event, 'moSelectBoxName', 50, 100)"
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
                    <span class="fixedLabel">Minimum Length:&nbsp;</span>
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
                    <span class="fixedLabel">Maximum Length:&nbsp;</span>
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
            
<! Number of Existing Options -->          
            
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
            
<!-- New Select Box Name -->            

            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">New Select Box Name:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="selectBoxName" size="36" maxlength="24"
                        onblur="selectBoxNameCheck()"
                        onmouseover="showTip(event, 'moSelectBoxName', 50, 100)"
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
      
<!-- Display any existing references  in Rules Table -->

    <script language="JavaScript" type="text/javascript">
<!--
      
    if (iR > 0) {
        document.write("<table border=\"1\" align=\"center\" width=\"50%\">");
        document.write("<tr><td colspan=\"4\" align=\"center\">");
        document.write("These references in the Rules Table also will be changed.");
        document.write("</td></tr><tr><td width=\"30%\">");
        document.write("<span class=\"requiredLabel\">Family Code&nbsp;</span>");
        document.write("</td><td width=\"30%\">");
        document.write("<span class=\"requiredLabel\">Subfamily Code</span>");
        document.write("</td><td width=\"20%\">");
        document.write("<span class=\"requiredLabel\">Seq. No.</span>");
        document.write("</td><td width=\"20%\">");
        document.write("<span class=\"requiredLabel\">Scope</span>");
        document.write("</td></tr>");
        for (var i = 0; i < iR; i++){
            document.write("<tr><td><span class='dataField'>");
            document.write(references[i][0]);
            document.write("</span></td><td><span class='dataField'>");
            document.write(references[i][1]);
            document.write("</span></td><td><span class='dataField'>");
            document.write(references[i][2]);
            document.write("</span></td><td><span class='dataField'>");
            document.write(references[i][3]);
            document.write("</span></td></tr>");
        }
        document.write("</table>");
    } else {
        document.write("<div><br /><p><center><h3>No references to this Select Box exist in the Rules Table.</h3></center></p></div>");
    
    }
    document.close();
    //-->
            </script>
      

<!--     Continue      -->

<table border="0" width="100%">
      <tr>
        <td colspan="2">
          <center>
            <br />
            <input type="submit" value="Rename" name="B1"   
        	onmouseover="showTip(event, 'moRename')" 
                onmouseout="hideTip()"
            />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;Cancel&nbsp;&nbsp;&nbsp;" name="B9" onclick="Javascript: window.location='gpssrf1.do'; " 
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