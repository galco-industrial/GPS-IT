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
	<title>Galco Parametric Search - Modify Product Line Code</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>   
        
        <!-- Version 1.3.00
        
        Modified 4/16/2008 by DES to support 4 divisions
        
        -->
        
<script language="JavaScript" type="text/javascript">
<!--


function checkDisplayOrder() {
	var myForm = document.form1;
	var work = myForm.displayOrder.value;
	work = deleteLeadingZeroes(deleteSpaces(work));
	myForm.displayOrder.value = work;
	if (work.length > 0) {
		if (checkCharSet(work, NU) == false) {
			alert ("Please enter a valid numeric value.");
			myForm.displayOrder.focus();
			return;
		}
                var k = parseInt(myForm.oldDisplayOrder.value);
                var j = parseInt(work);
                if (k != j) {
                    for (var i = 0; i < iL; i++) {
                        if (parseInt(lineCodes[i][2]) == j) {
                            alert ("Error. This Display Order value is currently assigned.");
                            myForm.displayOrder.focus();
                            return;
                        }
                    }
                }
	}
}

function checkProductLineCode() {
    var myForm = document.form1;
    var work = myForm.productLineCode.value;
    work = deleteSpaces(work);
    work = work.toUpperCase();
    myForm.productLineCode.value = work;
    if (!checkCharSet(work, UC + NU)) {
        myForm.productLineCode.focus();
        return;
    }
    checkIfProductLineCodeExists();
}

function checkProductLineName() {
    var myForm = document.form1;
    var work = myForm.productLineName.value;
    work = deleteLeadingSpaces(deleteTrailingSpaces(reduceSpaces(work)));
    //work = work.toUpperCase();
    myForm.productLineName.value = work;
    if (!checkCharSet(work, UC + LC + NU + SP + "/-;&()" )) {
        myForm.productLineName.focus();
        return;
    }
}

function checkIfProductLineCodeExists() {
    var myForm = document.form1;
    for (var i = 0; i < iL; i++) {
        if (lineCodes[ i ] [ 0 ] == myForm.productLineCode.value) {
            alert ("Error! This Product Line Code already exists!");
            alert (myForm.productLineCode.value + " - " + lineCodes [ i ] [ 1 ]);
            myForm.productLineCode.focus();
            break;
        }
    }
}

function getMessage(divName) {
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moCancel"){return "Click Cancel to return to the previous screen.";}
    if (divName == "moModify") {return "Click to Modify this Product Line Code.";}
    if (divName == "moNoModify") {return "This field cannot be modified.";}
    if (divName == "moDisplayOrder"){return "The Display Order (0 - 9999) sets the order of this option when displayed.";}
    if (divName == "moCancel"){return "Click Cancel to return to the previous screen.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    if (divName == "moProductLineCode") {return "The Product Line Code cannot be changed.";}
    if (divName == "moProductLineDivision") {return "The Product Line Division cannot be modified.";}
    if (divName == "moProductLineName") {return "Enter up to 36 alphanumeric characters.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    setOldName();
    myForm.productLineName.focus();
}

function setOldName() {
    var myForm = document.form1;
    var work = myForm.productLineCode.value;
    for (var i = 0; i < iL; i++) {
        if (lineCodes[i][0] == work) {
            myForm.oldProductLineName.value = lineCodes[i][1];
            myForm.oldDisplayOrder.value = lineCodes[i][2];
            myForm.productLineName.value = lineCodes[i][1];
            myForm.displayOrder.value = lineCodes[i][2];
            var division = lineCodes[i][3];
            myForm.oldProductLineDivisionCode.value = division;
            if (division == "CP") {
                myForm.productLineDivision.value = "Control Products";
            } else if (division == "DR") {
                myForm.productLineDivision.value = "Depot Repair";
            } else if (division == "FS") {
                myForm.productLineDivision.value = "Field Service";
            } else if (division == "ES") {
                myForm.productLineDivision.value = "Engineered Systems";
            } else {
                myForm.productLineDivision.value = "**invalid**";
            }
            break;
        }
    }
}
 
function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
    work = myForm.productLineName.value;
    if (work.length == 0) {
        alert ("Please enter a valid Product Line Name");
        myForm.productLineName.focus();
        return false;
    }
    
    // Check Display Order

    work = myForm.displayOrder.value;
    if (work.length == 0) {	
		alert("Please enter a Display Order value between 0 and 999.");
		myForm.displayOrder.focus();
		return false;
    }

    
    
    if (myForm.auditUserID.value == "") {
        alert ("Please enter your User ID.");
        myForm.auditUserID.focus();
        return false;
    }
    
        
    if (myForm.oldProductLineName.value == myForm.productLineName.value
               &&
        myForm.oldDisplayOrder.value == myForm.displayOrder.value) {
            alert ("No changes are pending; Make changes, cancel or choose Exit to abort.");
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

        var lineCodes = new Array();
        var iL = 0;
        
        <c:forEach var="item" items="${lines}">
            lineCodes[iL++] = new Array(${item});
        </c:forEach>
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
    
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="gpslmf3.do" method="post" onsubmit="return My_Validator()">
    <p>
        <input type="hidden" name="validation" value="Error" />
        <input type="hidden" name="oldProductLineDivisionCode" value="" />
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
                    Parametric Search<br />Product Line Code Maintenance<br />
			Modify Product Line
                        
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
                    <p> You can modify a Product Line Name or its display order. 
                    A Product Line Code cannot be modified.
                    To change the Product Line Code, you must delete the
                    original code and create a new code.
                    The Product Line Name can contain up to 36 alphanumeric characters.
                    </p><br />
                </td>
            </tr>

            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Product Line Code:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="fixedField">
                    <input type="text" name="productLineCode" size="8" maxlength="8"
                        value="${param.code}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moProductlineCode', 50, 100)"
                        onmouseout="hideTip()"
                    />
                </span>
                <span class="limits">&nbsp;&nbsp;(Cannot be changed here.)</span>
                </td>
            </tr>
            
            <tr>
                <td align="right">
                    <span class="fixedLabel">Old Product Line Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="oldProductLineName" size="48" maxlength="48"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moNoModify', 50, 100)"
                        onmouseout="hideTip()" 
                        				
                    />
                    </span>
                </td>
            </tr>
            
            <tr>
                <td align="right">
                    <span class="requiredLabel">New Product Line Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <input type="text" name="productLineName" size="48" maxlength="48"
                        onmouseover="showTip(event, 'moProductLineName', 50, 100)"
                        onmouseout="hideTip()" 
                        onblur="checkProductLineName()"				
                    />
                    </span>
                </td>
            </tr>

                        
<!--  Product Line Division  -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Product Line Division:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="24" name="productLineDivision"
                readonly="readonly"
                onmouseover="showTip(event, 'moProductLineDivision')"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>
            
<!--  Display Ordering  -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Old Display Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="4" maxlength="4" name="oldDisplayOrder"
                readonly="readonly"
                onmouseover="showTip(event, 'moNoModify')"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>

      <tr>
        <td align="right">
          <span class="requiredLabel">New Display Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="4" maxlength="4" name="displayOrder"
                onblur="checkDisplayOrder()"
                onmouseover="showTip(event, 'moDisplayOrder')"
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
            <input type="button" value="&nbsp;&nbsp;&nbsp;Cancel&nbsp;&nbsp;&nbsp;" name="B9" onclick="Javascript: window.location='gpslmf1.do'; " 
	onmouseover="showTip(event, 'moCancel')" 
        onmouseout="hideTip()"
	/>
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpslf.jsp'; " 
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