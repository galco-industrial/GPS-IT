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
	<title>Galco Parametric Search - Modify Units</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>

<script language="JavaScript" type="text/javascript">
<!--

function displayUnitsAlreadyExists() {
    var myForm = document.form1;
    var work = myForm.displayUnits.value;
    if (work.length > 0 && work != myForm.displayUnits.value) {
        for (var i = 0; i < iU; i++) {
            if (units[i][1] == work)  {
                alert ("Error! This Display Unit is already defined.");
                return;
            }
        }
    }
}

function checkDisplayUnits() {
        var myForm = document.form1;
	var work = myForm.displayUnits.value;
	work = deleteLeadingSpaces(deleteTrailingSpaces(reduceSpaces(work)));
	myForm.displayUnits.value = work;
	if (work.length > 0) {
            if (checkCharSet(work, UC + LC + NU + SP + "-") == false ) {
		alert ("Please enter a valid Display Unit.");
		myForm.displayUnits.focus();
		return;
            }
	}
        displayUnitsAlreadyExists();
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
        myForm.displayOrder.focus();
    }
}

function checkMultiplierBase() {
    var myForm = document.form1;
    var errorMessage = "Please enter a valid decimal value.";
    var work = myForm.multiplierBase.value;
    work = deleteSpaces(work);
    myForm.multiplierBase.value = work;
    if (work.length != 0) {
        if (checkCharSet(work, NU + ".-") && isValidFloat(work)) {
            return;
        }
        alert (errorMessage);
        myForm.multiplierBase.focus();
        return;
    }
}

function checkMultiplierExp() {
    var myForm = document.form1;
    var errorMessage = "Please enter a valid integer between  -21 and +21.";
    var work = myForm.multiplierExp.value;
    work = deleteSpaces(work);
    myForm.multiplierExp.value = work;
    work = myForm.multiplierExp.value;
    if (work.length != 0) {
        if (checkCharSet(work, NU + "-") && isValidInteger(work)) {
            var exp = parseInt(work);
            if ( exp > -22 && exp < 22) {
                return;
            }
        }
        alert (errorMessage);
        myForm.multiplierExp.focus();
        return;
    }
}

function checkMultiplierPreAdjust() {
    var myForm = document.form1;
    var errorMessage = "Please enter a valid decimal value.";
    var work = myForm.multiplierPreAdjust.value;
    work = deleteSpaces(work);
    myForm.multiplierPreAdjust.value = work;
    if (work.length != 0) {
        if (checkCharSet(work, NU + ".-") && isValidFloat(work)) {
            return;
        }
        alert (errorMessage);
        myForm.multiplierPreAdjust.focus();
    }
}

function checkMultiplierPostAdjust() {
    var myForm = document.form1;
    var errorMessage = "Please enter a valid decimal value.";
    var work = myForm.multiplierPostAdjust.value;
    work = deleteSpaces(work);
    myForm.multiplierPostAdjust.value = work;
    if (work.length != 0) {
        if (checkCharSet(work, NU + "-.") && isValidFloat(work)) {
            return;
        }
        alert (errorMessage);
        myForm.multiplierPostAdjust.focus();
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
    if (divName == "moCancel") {return "Click to return to the previous menu.";}
    if (divName == "moModify") {return "Click to Modify this Units record.";}
    if (divName == "moNumericBase") { return "Only Base 10 is currently supported.";}
    if (divName == "moBaseUnits") {return "The Base Units for the new record.";}
    if (divName == "moDisplayUnits") {return "Enter the new Display Units name.";}
    if (divName == "moDisplayOrder") {return "Enter the new Display Order for this record.";}
    if (divName == "moNoModify") { return "This field cannot be changed.";}
    if (divName == "moMultiplierBase") {return "Enter the new multiplier base value.";}
    if (divName == "moMultiplierExp") {return "Enter the new exponent value for the multipler.";}
    if (divName == "moMultiplierPreAdjust") {return "Enter the new value to add/subtract before applying the multiplier.";}
    if (divName == "moMultiplierPostAdjust") {return "Enter the new value to add/subtract after applying the multiplier.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    return "";
}

function isValidFloat(work) {
    return !isNaN(parseFloat(work))
}
   
function isValidInteger(work) {
    if (!isNaN(parseInt(work))) {
        if (work.indexOf(".") == -1) {
            return true;
        }
    }
    return false;
}

function setDefaults() {
    var myForm = document.form1;
    myForm.displayUnits.focus();
}

function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
    work = myForm.displayUnits.value;
    if (work.length == 0) {
        alert ("Please enter a Display Unit.");
        myForm.displayUnits.focus();
        return false;
    }
    
    work = myForm.displayOrder.value;
    if (work.length == 0) {
        alert ("Please enter the Display Order.");
        myForm.displayOrder.focus();
        return false;
    }
    
    work = myForm.multiplierBase.value;
    if (work.length == 0) {
        alert ("Please enter a valid Multiplier Base.");
        myForm.multiplierBase.focus();
        return false;
    }
    
    work = myForm.multiplierExp.value;
    if (work.length == 0) {
        alert ("Please enter a valid Multiplier Exponent.");
        myForm.multiplierExp.focus();
        return false;
    }
    
    work = myForm.multiplierPreAdjust.value;
    if (work.length == 0) {
        alert ("Please enter a valid Multiplier Pre Adjust.");
        myForm.multiplierPreAdjust.focus();
        return false;
    }
    
    work = myForm.multiplierPostAdjust.value;
    if (work.length == 0) {
        alert ("Please enter a valid Multiplier Post Adjust.");
        myForm.multiplierPostAdjust.focus();
        return false;
    }
    
    work = myForm.auditUserID.value;
    if (work.length == 0) {
        alert ("Please enter your User ID.");
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

        var isBaseUnitDef = ("${baseUnits}" == "${displayUnits}");
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

<form name="form1" action="gpsumf3.do" method="post" onsubmit="return My_Validator()">
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
                    Parametric Search<br />Units Maintenance<br />
			Modify Unit.
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
                    <p>Enter changes for this Unit. Fields in Green cannot be changed.
                     
                    </p><br />
                </td>
            </tr>
            
            
<!-- Base Units Name -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Base Unit:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="24" name="baseUnits"
	      value = "${baseUnits}"
                readonly="readonly"
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
      
<!-- Old Display Units Name -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Old Display Unit:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="24" maxlength="24" name="oldDisplayUnits"
	      readonly="readonly" 
                value="${displayUnits}"
              onmouseover="showTip(event, 'moNoModify')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
<!-- Display Units Name -->
           
      <tr>
        <td align="right">
          <span class="requiredLabel">New Display Unit:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="24" maxlength="24" name="displayUnits"
	      onblur="checkDisplayUnits()"
               value="${displayUnits}"
              onmouseover="showTip(event, 'moDisplayUnits')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
<!-- Old Display Order -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Old Display Order:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="6" maxlength="4" name="oldDisplayOrder"
	      readonly="readonly" value="${displayOrder}"
              onmouseover="showTip(event, 'moNoModify')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
<!-- Display Order -->
           
      <tr>
        <td align="right">
          <span class="requiredLabel">New Display Order:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="6" maxlength="4" name="displayOrder"
              value="${displayOrder}"
	      onblur="checkDisplayOrder()"
              onmouseover="showTip(event, 'moDisplayOrder')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
<!-- Old Multiplier Base -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Old Multiplier Base:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="12" maxlength="12" name="oldMultiplierBase"
                value = "${multiplierBase}"
                readonly="readonly"
              onmouseover="showTip(event, 'moNoModify')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
<!-- Multiplier Base -->
           
      <tr>
        <td align="right">
          <span class="requiredLabel">New Multiplier Base:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="12" maxlength="12" name="multiplierBase"
           value = "${multiplierBase}"
	      onblur="checkMultiplierBase()"
              onmouseover="showTip(event, 'moMultiplierBase')"
              onmouseout="hideTip()" />
        </td>
      </tr>
     
 
<!-- Old Multiplier Exponent -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Old Multiplier Exponent:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="12" maxlength="3" name="oldMultiplierExp"
	      readonly="readonly"
                value="${multiplierExp}"
              onmouseover="showTip(event, 'moNoModify')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
<!-- Multiplier Exponent -->
           
      <tr>
        <td align="right">
          <span class="requiredLabel">New Multiplier Exponent:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="12" maxlength="3" name="multiplierExp"
                          value="${multiplierExp}"
	      onblur="checkMultiplierExp()"
              onmouseover="showTip(event, 'moMultiplierExp')"
              onmouseout="hideTip()" />
        </td>
      </tr>

            
<!-- Old Multiplier Pre-Adjust -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Old Multiplier Pre-Adjust:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="12" maxlength="12" name="noMultiplierPreAdjust"
                          value="${multiplierPreAdjust}"
	      readonly="readonly"
              onmouseover="showTip(event, 'moNoModify')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
<!-- Multiplier Pre-Adjust -->
           
      <tr>
        <td align="right">
          <span class="requiredLabel">New Multiplier Pre-Adjust:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="12" maxlength="12" name="multiplierPreAdjust"
                       value="${multiplierPreAdjust}"
	      onblur="checkMultiplierPreAdjust()"
              onmouseover="showTip(event, 'moMultiplierPreAdjust')"
              onmouseout="hideTip()" />
        </td>
      </tr>

      
<!-- Old Multiplier Post-Adjust -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Old Multiplier Post-Adjust:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="12" maxlength="12" name="oldMultiplierPostAdjust"
                value="${multiplierPostAdjust}"
	      readonly="readonly"
              onmouseover="showTip(event, 'moNoModify')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
<!-- Multiplier Post-Adjust -->
           
      <tr>
        <td align="right">
          <span class="requiredLabel">New Multiplier Post-Adjust:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="12" maxlength="12" name="multiplierPostAdjust"
                value="${multiplierPostAdjust}"
	      onblur="checkMultiplierPostAdjust()"
              onmouseover="showTip(event, 'moMultiplierPostAdjust')"
              onmouseout="hideTip()" />
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
            
      <br />
      <table border="1" align="center" width="100%">
            <tr>
                <td colspan="8">
                    <center><b> Existing Units with the same Base Units.</b>
                    </center>
                </td>
            </tr>
            <tr>
                <td width="5%">
                    <span class="requiredLabel">Display<br />Order</span>
                </td>
                <td width="30%">
                    <span class="requiredLabel">Display Units&nbsp;</span>
                </td>
                <td width="30%">
                    <span class="requiredLabel">Base Units&nbsp;</span>
                </td>
                <td width="5%">
                    <span class="requiredLabel">Number<br />Base&nbsp;</span>
                </td>
                <td width="10%">
                    <span class="requiredLabel">Multiplier<br />Base&nbsp;</span>
                </td>
                <td width="10%">
                    <span class="requiredLabel">Multiplier<br />Exponent&nbsp;</span>
                </td>
                <td width="5%">
                    <span class="requiredLabel">Pre<br />Adjust</span>
                </td>
                <td width="5%">
                    <span class="requiredLabel">Post<br />Adjust</span>
                </td>
            </tr>
            <script language="JavaScript" type="text/javascript">
<!--
                var q = 0;
                for (var i = 0; i < units.length; i++){
                  if (units[i][2] == "${baseUnits}") {
                    document.write("<tr><td><span class='dataField'>");
                    document.write(units[i][0]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(units[i][1]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(units[i][2]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(units[i][3]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(units[i][4]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(units[i][5]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(units[i][6]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(units[i][7]);
                    document.write("</span></td></tr>");
                    q++
                  }
                }
                if (q == 0) {
                    document.write("<tr><td colspan=\"8\" align=\"center\">");
                    document.write("No Units currently exist for this Base Unit.");
                    document.write("</td></tr>");
               }
               document.close();
//-->
            </script>
    </table>        

<!--     Continue      -->

      <tr>
        <td colspan="2">
          <center>
            <br />
            <input type="submit" value="Modify" name="B1" 
	onmouseover="showTip(event, 'moModify')" 
        onmouseout="hideTip()"
	/>
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;Cancel&nbsp;" name="B2" onclick="Javascript: window.location='gpsumf1.do'; " 
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