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
	<title>Galco Parametric Search - Purge Parametric Data</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
<!-- 
            gpsdpf1.jsp   Version 1.2.00
            
            Modification History
        
        Date        Who     What
        
                            
-->

<script language="JavaScript" type="text/javascript">
<!--

function checkEditOnly() {
    
}

function checkFileName() {
    var myForm = document.form1;
    var work = myForm.fileName.value;
    if (!checkCharSet(work, UC + LC + NU + SP + ".-_")) {
        alert ("Please enter a valid import file name");
        fileName.focus();
        return;
    }
    var work2 = work.toUpperCase();
    var i = work2.indexOf(".IMP");
    if (i == work.length - 4) {
        work = work.slice(0, i);
        myForm.fileName.value = work;
    }
}

function checkIfFamilySelected() {
    var myForm = document.form1;
    if (myForm.familyCode.value == "0") {
        alert ("Please select a Family Code first.");
        myForm.familyCode.focus();
    }
}

function deleteSubfamilyOptions() {
    var myForm = document.form1;
    var oListbox = myForm.subfamilyCode;
    for (var i = oListbox.options.length-1; i >= 0; i--) {
    	oListbox.remove(i);
    }
    return true;
}

function getMessage(divName) {
    if (divName == "moAuditUserID") {return "Enter your initials.";}
    if (divName == "moDelete"){return "Click to Delete the parametric data from the database.";}
    if (divName == "moEditOnly") {return "Select Yes if you want to generate an error log without actually deleting the data.";}
    if (divName == "moExit"){return "Click Exit to return to the Database Menu.";}
    if (divName == "moFamilyCode") {return "Select a Product Family for the parametric data you are deleting.";}
    if (divName == "moFileName") { return "Please enter a worksheet file name using upper and lower case, numerics, and periods only.";}
    if (divName == "moLogLevel"){return "Select High if you want maximum detail written to the logging file.";}
    if (divName == "moReset"){return "Click Reset to return the form to its initial state when first displayed.";} 
    if (divName == "moSubfamilyCode") {return "Select a Product Subfamily for the parametric data you are deleting.";}
    return "";
}

function setDefaults() {
    checkEditOnly();
}

function setSubfamilyOptions() {
	var myForm = document.form1;
	var oListbox = myForm.subfamilyCode;
	var famCode = myForm.familyCode.value;
	var junk = deleteSubfamilyOptions();
	// if (myForm.ruleScope[0].checked == true)
	//{
	//	junk = setSubfamilyToAll();
	//	return true;
	//}
        var iFlag = false;
	for (var i = 0; i < subfamily.length; i++) {
		if (subfamily [i] [0] == famCode) {
			if (iFlag == false) {
                            var oOption = document.createElement("option");
                            oOption.appendChild(document.createTextNode("Please choose a Subfamily"));
                            oOption.setAttribute("value", "");
                            oListbox.appendChild(oOption);
                            iFlag = true;
                        }
                        oOption = document.createElement("option");
			oOption.appendChild(document.createTextNode(subfamily [i] [2] ));
                        oOption.setAttribute("value", subfamily [i] [1] );
                        oListbox.appendChild(oOption);
		}
	}
	return true;
}

function My_Validator() {
    var myForm = document.form1;
    if (myForm.familyCode.value == "0") {
        alert ("Please select a product Family code");
        myForm.familyCode.focus();
        return false;
    }
    
    if (myForm.subfamilyCode.value == "") {
        alert ("Please select a product Subfamily code");
        myForm.subfamilyCode.focus();
        return false;
    }
    
    if (myForm.fileName.value == "") {
        alert ("Please enter an worksheet file name.");
        myForm.fileName.focus();
        return false;        
    }
    
    if (myForm.auditUserID.value == "") {
        alert ("Please enter your User ID.");
        myForm.auditUserID.focus();
        return false;
    }
    
    //	Set Family Description

    for (var i = 0; i < family.length; i++) {
	if (family [i] [0] == myForm.familyCode.value) {
            myForm.familyDescription.value = family [i] [1];
            break;
	}
    }

    //	Set Subfamily Description

    for (var i = 0; i < subfamily.length; i++) {
        if (subfamily [i] [0] == myForm.familyCode.value) {
            if (subfamily [i] [1] == myForm.subfamilyCode.value) {
                myForm.subfamilyDescription.value = subfamily [i] [2];
		break;
            }
        }
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
    
        var family = new Array();
        var subfamily = new Array();
        var iF = 0;
        var iS = 0;
        <c:forEach var="item" items="${families}">
            family[iF++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${subfamilies}">
            subfamily[iS++] = new Array(${item});
        </c:forEach>
    //-->    
    </script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">

<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>
        
<form name="form1" action="gpsdpf2.do" method="post" onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
            <input type="hidden" name="familyDescription" value="" />
            <input type="hidden" name="subfamilyDescription" value="" />
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
			Purge Parametric Data
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
                    <p> This operation obtains part numbers from a parametric
                    data worksheet and deletes the corresponding parametric data
                    values from the database. Choose a Family and Subfamily that
                    matches the information contained in the worksheet.
                    </p><br />
                </td>
            </tr>
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Family Name:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="familyCode" size="1"
                        onchange="setSubfamilyOptions()"
                        onmouseover="showTip(event, 'moFamilyCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="0">Please select a Product Family</option>
                        
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            for (var i = 0; i < family.length; i++){
                                document.write("<option value=\""+family[i][0]+"\">"+family[i][1]+"</option>");
                            }
                            document.close();
                        //-->
                        </script>
                    </select>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <span class="requiredLabel">Subfamily Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <select name="subfamilyCode" size="1"
                    onmouseover="showTip(event, 'moSubfamilyCode', 50, 100)"
                    onmouseout="hideTip()" 
                    onfocus="checkIfFamilySelected()">				
                        <option selected="selected" value="*">Please select a Product Family first</option>
                    </select>
                    </span>
                </td>
            </tr>


      
      <!--  Get Worksheet File name -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Worksheet File Name:&nbsp;</span>
        </td>
        <td><span class="dataField">
          ${gpsImportPath}&nbsp;&nbsp;  
          <input type="text" size="40" maxlength="64" name="fileName"
          onblur="checkFileName()"
          onmouseover="showTip(event, 'moFileName')"
          onmouseout="hideTip()" 
          /> .csv
        </span></td>
      </tr>
      
<!--  Edit Only? -->  

            <tr>
                <td align="right"><span class="label">
                    Edit  Only?:&nbsp;
                </span></td>
                <td align="left"><span class="dataField">
                    <input type="radio" name="editOnly"
                        checked="checked"
                        value="Y"
                        onblur="checkEditOnly()"
                        onmouseover="showTip(event,'moEditOnly')" 
                        onmouseout="hideTip()"
                    />
                    Yes&nbsp;&nbsp;&nbsp;&nbsp;
                    <input type="radio" name="editOnly"
                        value="N"
                        onblur="checkEditOnly()"
                        onmouseover="showTip(event,'moEditOnly')" 
                        onmouseout="hideTip()"
                    />
                    No
                </span></td>
            </tr>

                        
                  <!--  Logging level  -->

            <tr>
                <td align="right"><span class="label">
                    Logging Level:&nbsp;
                </span></td>
                <td align="left"><span class="dataField">
                    <input type="radio" name="logLevel"
                        checked="checked"
                        value="H"
                        onmouseover="showTip(event,'moLogLevel')" 
                        onmouseout="hideTip()"
                    />
                    High&nbsp;&nbsp;&nbsp;&nbsp;
                    <input type="radio" name="logLevel"
                        value="L"
                        onmouseover="showTip(event,'moLogLevel')" 
                        onmouseout="hideTip()"
                    />
                    Low
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
            <input type="submit" value="Delete" name="B1" 
	onmouseover="showTip(event, 'moDelete')" 
        onmouseout="hideTip()"
	/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="reset" value="&nbsp;&nbsp;Clear&nbsp;&nbsp;" name="B2" 
	onmouseover="showTip(event, 'moReset')" 
        onmouseout="hideTip()"
	/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsdf.jsp'; " 
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
