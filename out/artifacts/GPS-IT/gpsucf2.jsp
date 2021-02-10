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
	<title>Galco Parametric Search - Create Base Units</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- gpsucf2
        
        I am used to create a NEW base unit entry int he units table
        
        
        -->

<script language="JavaScript" type="text/javascript">
<!--

function baseUnitsAlreadyExists() {
    var myForm = document.form1;
    var work = myForm.baseUnits.value;
    if (work.length > 0 ) {
        for (var i = 0; i < iU; i++) {
            if (units[i][1] == work)  {
                alert ("Error! This Base Unit is already defined.");
                return;
            }
        }
    }
}

function checkBaseUnits() {
        var myForm = document.form1;
	var work = myForm.baseUnits.value;
	work = deleteLeadingSpaces(deleteTrailingSpaces(reduceSpaces(work)));
	myForm.baseUnits.value = work;
	if (work.length > 0) {
            if (checkCharSet(work, UC + LC + NU + SP + "-") == false ) {
		alert ("Please enter a valid Base Unit.");
		myForm.baseUnits.focus();
		return;
            }
	}
        baseUnitsAlreadyExists();
}


function checkDisplayOrder() {
    var myForm = document.form1;
    var work = myForm.displayOrder.value;
    work = deleteLeadingZeroes(deleteSpaces(work));
    myForm.displayOrder.value = work;
    if (!checkCharSet(work, NU)) {
        myForm.displayOrder.focus();
        return;
    }
    if (displayOrderExists()) {
        var myForm = document.form1;
        // myForm.displayOrder.value = "";
        myForm.displayOrder.focus();
    }
}

function displayOrderExists() {
    var myForm = document.form1;
    var order = myForm.displayOrder.value;
    if (order.length > 0) {
        for (var j = 0; j < iU; j++) {
            if (units[j][0] == order) {
                alert ("Error - This Display Order is already defined.");
                return true;
            }
        }
    }
    return false;
}

function getMessage(divName) {
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moCreate") {return "Click to Create a new Base Units record.";}
    if (divName == "moNumericBase") { return "Only Base 10 is currently supported.";}
    if (divName == "moBaseUnits") {return "Enter the Base Units name for the new record.";}
    if (divName == "moDisplayOrder") {return "Enter the Display Order for the new record.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    myForm.baseUnits.focus();
}
 
function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
    work = myForm.baseUnits.value;
    if (work.length == 0) {
        alert ("Please enter a Base Unit name.");
        myForm.baseUnits.focus();
        return false;
    }
    myForm.displayUnits.value = work;
    
    work = myForm.displayOrder.value;
    if (work.length == 0) {
        alert ("Please enter the Display Order.");
        myForm.displayOrder.focus();
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

        var units = new Array();
        var iU = 0;
                
        <c:forEach var="item" items="${unitsList}">
            units[iU++] = new Array(${item});
        </c:forEach>
                
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
   
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="gpsucf3.do" method="post" onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
            <input type="hidden" name="displayUnits" value="" />
            <input type="hidden" name="multiplierBase" value="10" />
            <input type="hidden" name="multiplierExp" value="0" />
            <input type="hidden" name="multiplierPreAdjust" value="0" />
            <input type="hidden" name="multiplierPostAdjust" value="0" />
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
                    Parametric Search<br />Units Maintenance<br />
			Create New Base Unit.
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
                    <p>Enter a unique name for the new Base Unit.
                     
                    </p><br />
                </td>
            </tr>
            
            
<!-- Base Units Name -->
           
      <tr>
        <td align="right">
          <span class="requiredLabel">Base Unit:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="24" maxlength="24" name="baseUnits"
	      onblur="checkBaseUnits()"
              onmouseover="showTip(event, 'moBaseUnits')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
<!-- Number Base -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Number Base:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <select size="1" name="numericBase"
	      onmouseover="showTip(event, 'moNumericBase')"
              onmouseout="hideTip()" >
              <option selected="selected" value="10">10</option>
          </select>
        </td>
      </tr>
      
<!-- Display Order -->
           
      <tr>
        <td align="right">
          <span class="requiredLabel">Display Order:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="6" maxlength="4" name="displayOrder"
	      onblur="checkDisplayOrder()"
              onmouseover="showTip(event, 'moDisplayOrder')"
              onmouseout="hideTip()" />
        </td>
      </tr>
            


<!--     Continue      -->

      <tr>
        <td colspan="2">
          <center>
            <br />
            <input type="submit" value="Create" name="B1" 
	onmouseover="showTip(event, 'moCreate')" 
        onmouseout="hideTip()"
	/>
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;Cancel&nbsp;" name="B2" onclick="Javascript: window.location='gpsucf1.do'; " 
	onmouseover="showTip(event, 'moCancel')" 
        onmouseout="hideTip()"
	/>
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsuf.jsp'; " 
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