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
	<title>Galco Parametric Search - Renumber Select Box Options</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
                
        <!-- Modification History
        
        version 1.5.01
        
        09/01/2007 DES Modified to use Ajax to obtain line/family/subfamily codes
        
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
    if (divName == "moDataType") { return "This is the Data Type of the Option raw value.";}
    if (divName == "moExit") {return "Click Exit to abandon this operation and return to the previous Menu.";}
    if (divName == "moFamilyName") {return "This is the Family name for this Select Box.";}
    if (divName == "moIncrementBy") {return "Specify an increment value for this Renumber Operation.";}
    if (divName == "moMaximum") {return "This is the maximum length of the Option Text value.";}
    if (divName == "moMinimum") {return "This is the minimum length of the Option Text value.";}
    if (divName == "moRenumber") {return "Click to Renumber the Options within the Select Box.";}
    if (divName == "moSelectBoxName") {return "This is the Select Box Name that will be renumbered.";}  
    if (divName == "moStartingNumber") {return "Specify a starting number for this Renumber Operation.";}
    if (divName == "moSubfamilyName") {return "This is the Subfamily name for the rule that uses this Select Box.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    myForm.startingNumber.focus();
    if (iO == 0 ) {
        myForm.B1.disabled = true;
    }
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
    total = incr * (iO - 1);
    start = parseInt(work);
    total = total + start;
    work = " " + total;
    work = deleteSpaces(work);
    if (work.length > 4) {
        alert ("Starting number plus all increments exceeds 9999; try again.");
        myForm.startingNumber.focus();
        return false;
    }
    
    // Check for User ID
    
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

        var options = new Array();
        var iO = 0;
        
        <c:forEach var="item" items="${optionList}">
            options[iO++] = new Array(${item});
        </c:forEach>
        
    //-->    
</script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
        
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form name="form1" action="gpsonf3.do" method="post" onsubmit="return My_Validator()" >
<p>
    <input type="hidden" name="familyCode" value="${familyCode}" />
    <input type="hidden" name="subfamilyCode" value="${subfamilyCode}" />
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
                    Parametric Search<br />Select Box Options Maintenance<br />
			Renumber Select Box Options
                </h2>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;
            </td>
            <td>
                <h3 class="blue">
                    ${statusMessage}
                </h3>
            </td>
        </tr>
</table>

       
    <table border="0" align="center" width="100%">

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
  
<!-- Select Box Name -->            
            
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Select Box Name:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="selectBoxName" size="36"
                        value = "${selectBoxName}"
                        readonly = "readonly"
                        onmouseover="showTip(event, 'moSelectBoxName', 50, 100)"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>            
            
<!-- Data Type -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Data Type:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="dataType" size="16"
                        value = "${dataType}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moDataType')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            
<!-- Minimum -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Minimum Text Length:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="minimum" size="2"
                        value = "${minimum}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moMinimum')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            
<!-- Maximum -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Maximum Text Length:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="maximum" size="2"
                        value = "${maximum}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moMaximum')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
             
<!--  Display ReOrdering  -->
    <tr>
        <td width="20%" align="right">
            <span class="requiredLabel">Starting number:&nbsp;</span>
        </td>
        <td width="80%" align="left">
            <span class="datafield">
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
        <td align="left">
            <span class="datafield">
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
        <td>
            <span class="dataField">
                <input type="text" size="4" maxlength="4" name="auditUserID"
                    value="${sessionScope.auditUserID}"
                    onblur="checkAuditUserID()"
                    onmouseover="showTip(event, 'moAuditUserID')"
                    onmouseout="hideTip()" 
                /> 
            </span>
        </td>
    </tr>
    </table>
    <br />
                 
<!--     Exit      -->

<p>
    <center>
        <input type="submit" value="Renumber" name="B1" 
            onmouseover="showTip(event, 'moRenumber')" 
            onmouseout="hideTip()"
        />
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsonf1.do'; " 
            onmouseover="showTip(event, 'moExit')" 
            onmouseout="hideTip()"
        />
    </center>
</p>
<br />
               
<table border="1" align="center" width="100%">
   <tr>
       <td width="6%">
           <span class="requiredLabel">Display<br />Order&nbsp;</span>
       </td>
       <td width="30%">
           <span class="requiredLabel">Option Text<br />(Cooked)</span>
       </td>
       <td width="30%">
           <span class="requiredLabel">Value<br />(Raw)</span>
       </td>
       <td width="8%">
           <span class="requiredLabel">Default</span>
       </td>
       <td width="26%">
           <span class="requiredLabel">Image Name</span>
       </td>
   </tr>
       <script language="JavaScript" type="text/javascript">
<!--
       for (var i = 0; i < options.length; i++){
           document.write("<tr><td><span class='dataField'>");
           document.write(options[i][1]);
           document.write("</span></td><td><span class='dataField'>");
           document.write(options[i][2]);
           document.write("</span></td><td><span class='dataField'>");
           document.write(options[i][3] + "&nbsp;" );
           document.write("</span></td><td><span class='dataField'>");
           document.write(options[i][5] + "&nbsp;" );
           document.write("</span></td><td><span class='dataField'>");
           document.write(options[i][6] + "&nbsp;" );
           document.write("</span></td></tr>");
       }
       if (options.length == 0) {
           document.write("<tr><td colspan=\"4\" align=\"center\">");
           document.write("No Options currently exist in this Select Box.");
           document.write("</td></tr>");
       }
       document.close();
//-->
       </script>
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