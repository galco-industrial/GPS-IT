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
	<title>Galco Parametric Search - Renumber Product Line Codes</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- Version 1.3.00
        
        Modified 4/21/2008 by DES to support 4 divisions
        
        -->
        
<script language="JavaScript" type="text/javascript">
<!--

function checkIncrementBy() {
    var myForm = document.form1;
    var work = myForm.incrementBy.value;
    work = deleteLeadingZeroes(deleteSpaces(work));
    myForm.incrementBy.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value.");
            myForm.incrementBy.focus();
            return;
        }
    }
    if (parseInt(work) == 0) {
            alert ("Increment cannot be zero!");
            myForm.incrementBy.focus();
            return;
    }
}

function checkStartingNumber() {
    var myForm = document.form1;
    var work = myForm.startingNumber.value;
    work = deleteLeadingZeroes(deleteSpaces(work));
    myForm.startingNumber.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value.");
            myForm.startingNumber.focus();
            return;
        }
    }
}
   
function getMessage(divName) {
    if (divName == "moAuditUserID") {return "Enter your User ID.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the Product Line Menu.";}
    if (divName == "moRenumber") {return "Click to Renumber the Product Line Codes.";}
    if (divName == "moIncrementBy") {return "Specify an increment value for this Renumber Operation.";}
    if (divName == "moStartingNumber") {return "Specify a starting number for this Renumber Operation.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    myForm.startingNumber.focus();
}

function My_Validator() {
    var myForm = document.form1;
    var work = "";
    var work2 = "";
    var start = 0;
    var total = 0;
    var incr = 0;
   
    // check starting number
    
    work = myForm.startingNumber.value;
    if (work.length == 0) {
        alert ("Please enter a Starting Number.");
        myForm.startingNumber.focus();
        return false;
    }
    
    // check increment by
    
    work2 = myForm.incrementBy.value;
    if (work2.length == 0) {
        alert ("Please enter an Increment.");
        myForm.incrementBy.focus();
        return false;
    }
    
    // check expected renumber range
    
    incr = parseInt(work2);
    total = incr * (iL - 1);
    start = parseInt(work);
    total = total + start;
    work = " " + total;
    work = deleteSpaces(work);
    if (work.length > 4) {
        alert ("Starting number plus all increments exceeds 9999; try again.");
        myForm.startingNumber.focus();
        return false;
    }
    if (myForm.auditUserID.value == "") {
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

<form name="form1" action="gpslnf2.do" method="post" onsubmit="return My_Validator()">
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
                    Parametric Search<br />Product Line Code Maintenance<br />
			Renumber Product Line Codes
                </h2>
            </td>
        </tr>
        <tr> <td>&nbsp;</td><td>
            <h3 class="blue">
                ${statusMessage}
            </h3>
        </td></tr>
        
    </table>
    <br /><br /><br />
       
    <table border="1" align="center" width="100%">

            <tr>
                <td width="15%">
                    <span class="requiredLabel">Product Line<br />Code&nbsp;</span>
                </td>
                <td width="5%">
                    <span class="requiredLabel">Display<br />Order</span>
                </td>
                <td width="65%">
                    <span class="requiredLabel">Product Line Name</span>
                </td>
                <td width="15%">
                    <span class="requiredLabel">Product Line Division</span>
                </td>
            </tr>
            <script language="JavaScript" type="text/javascript">
<!--
                for (var i = 0; i < lineCodes.length; i++){
                    document.write("<tr><td><span class='dataField'>");
                    document.write(lineCodes[i][0]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(lineCodes[i][2]);
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(lineCodes[i][1]);
                    document.write("</span></td><td><span class='dataField'>");
                    var division = lineCodes[i][3];
                    if (division == "CP") {
                        document.write("Control<br />Products");
                    } else if (division == "DR") {
                        document.write("Depot<br />Repair");
                    } else if (division == "FS") {
                        document.write("Field<br />Service");
                    } else if (division == "ES") {
                        document.write("Engineered<br />Systems");
                    } else {
                        document.write("&nbsp;");
                    }
                    document.write("</span></td></tr>");
                }
                document.close();
//-->
            </script>
            
    </table>        

<br />            
<table>
    
    <!--  Display ReOrdering  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Starting number:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="4" maxlength="4" name="startingNumber"
                onblur="checkStartingNumber()"
                onmouseover="showTip(event, 'moStartingNumber')"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>

      <tr>
        <td align="right">
          <span class="requiredLabel">Increment By:</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="4" maxlength="4" name="incrementBy"
                onblur="checkIncrementBy()"
                onmouseover="showTip(event, 'moIncrementBy')"
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

    
</table>

<!--     Exit      -->

<br /><br /><br />
<p><center>
            <input type="submit" value="Renumber" name="B1" 
                onmouseover="showTip(event, 'moRenumber')" 
                onmouseout="hideTip()"
            />
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpslf.jsp'; " 
                    onmouseover="showTip(event, 'moExit')" 
                    onmouseout="hideTip()"
                /></center>
</p>
<br /><br />
  <p>
    <img src="w3cxhtml10.bmp"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
  </p>
  </form>

    </div>  
</body>
</html>