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
	<title>Galco Parametric Search - Modify Select Box Option</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- Modification History
        
        version 1.5.00
        
        09/01/2007 DES Modified to use Ajax to obtain line/family/subfamily codes
        07/20/2010 DES Modified to support optionImage field
        
        -->


<script language="JavaScript" type="text/javascript">
<!--

function checkOptionDefault() {
    var myForm = document.form1;
    if (myForm.optionDefault.checked) {
        myForm.optionDefault.value="default";
        setOldDefault();
    } else {
        myForm.optionDefault.value="";
        myForm.oldDefault.value = "";
    }
    //alert (myForm.optionDefault.value);
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
    if (work != myForm.oldDisplayOrder.value) {
        if (displayOrderExists()) {
            var myForm = document.form1;
            myForm.displayOrder.focus();
        }
    }
}

function checkOptionText() {
    var myForm = document.form1;
    var oldValue = myForm.oldOptionText.value;
    var work = myForm.optionText.value;
    var min = myForm.minimum.value;
    var max = myForm.maximum.value;
    work = deleteLeadingSpaces(deleteTrailingSpaces(reduceSpaces(work)));
    myForm.optionText.value = work;
    if (work.length != 0 && work != oldValue) {
        for (var i=0; i < iO; i++) {
            if (work == options [i] [2] ) {
                alert ("Error! This Option Text already exists; Please choose another.");
                myForm.optionText.focus();
                return;
            }
        }
        var low = 0;
        if (min != "(none)") {
            low = parseInt(min);
        }
        var high = 0;
        if (max != "(none)") {
            high = parseInt(max);
        }
        var len = work.length;
        if (low !=0 && len < low) {
            alert ("Error! Option Text is less than minimum characters long.");
            myForm.optionText.focus();
            return;
        }
        if (high !=0 && len > high) {
            alert ("Error! Option Text is greater than maximum characters long.");
            myForm.optionText.focus();
            return;
        }
    }
}

function checkOptionValue1() {
    var myForm = document.form1;
    var oldValue = myForm.oldOptionValue1.value;
    var work = myForm.optionValue1.value;
    work = deleteLeadingSpaces(deleteTrailingSpaces(reduceSpaces(work)));
    var type = myForm.dataType.value;
    var min = myForm.minimum.value;
    var max = myForm.maximum.value;
    myForm.optionValue1.value = work;
    if (work.length != 0 && work != oldValue) {
        if (type == "NUMERIC") {
            if (isNaN(work)) {
                alert ("Error! This is a numeric Select Box and the number you entered looks ugly.");
                myForm.optionValue1.focus();
                return;
            }
        }
                if (type == "STRING") {
            if (!checkCharSet(work, UC + LC + NU + SP + "~!@#$%^&*()-_+=:;,./?")) {
                myForm.optionValue1.focus();
                return;
            }
        }
        if (type == "LOGICAL") {
            if (work != "0" && work != "-1") {
                alert ("Error! Set Logical Option Values to 0 for False or -1 for True!");
                myForm.optionValue1.focus();
                return;
            }
        }
        for (var i=0; i < iO; i++) {
            if (work == options [i] [3] ) {
                alert ("Error! This Option Value already exists; Please choose another.");
                myForm.optionValue1.focus();
                return;
            }
        }
    }
}

function checkOptionImage() {
    var myForm = document.form1;
    var work = myForm.optionImage.value;
    work = deleteSpaces(work);
    if (!checkCharSet(work, UC + LC + NU + "/-_.")) {
        myForm.optionImage.focus();
        return;
    }
}

function displayOrderExists() {
    var myForm = document.form1;
    var order = myForm.displayOrder.value;
    if (order.length > 0) {
        for (var j = 0; j < iO; j++) {
            if (options[j][1] == order) {
                alert ("Error - This Display Order is already defined for this Option in this Select Box.");
                return true;
            }
        }
    }
    return false;
}

function getMessage(divName) {
    if (divName == "moAuditUserID") { return "Enter your User ID. Eventually your User ID will be extracted from your sign on info.";}
    if (divName == "moCancel") { return "Click to Cancel and choose a different Select Box.";}
    if (divName == "moDataType") { return "This is the Data Type of the Option raw value.";}
    if (divName == "moDisplayOrder") { return "Enter a 4 digit number to control the Display Order of this Option.";}
    if (divName == "moExit") {return "Click Exit to return to the previous Menu.";}
    if (divName == "moFamilyName") {return "This is the Family name for the Select Box that contains this Option.";}
    if (divName == "moMaximum") {return "This is the maximum length of the Option Text value.";}
    if (divName == "moMinimum") {return "This is the minimum length of the Option Text value.";}
    if (divName == "moModify") { return "Click to Modify this Option.";}
    if (divName == "moOldDefault") { return "This is the current default setting for this Option.";}
    if (divName == "moOldOptionImage") { return "This is the current setting for this Option.";}
    if (divName == "moOldOptionText") { return "This is the current Text setting for this Option.";}
    if (divName == "moOldOptionValue1") { return "This is the current Value setting for this Option.";}
    if (divName == "moOldOptionValue2") { return "This is the current Value 2 setting for this Option.";}
    if (divName == "moOldOrder") { return "This is the current Display order setting for this Option.";}
    if (divName == "moOptionDefault") { return "Check this box to flag this option as the Default Option for this Select Box.";}
    if (divName == "moOptionImage") { return "Enter up to 36 characters for an image file name for this Option.";}
    if (divName == "moOptionText") { return "Enter up to 36 characters for the Option Text in Cooked format.";}
    if (divName == "moOptionValue1") { return "Enter up to 36 characters for the Option Value in Raw format.";}
    if (divName == "moOptionValue2") { return "Enter up to 36 characters for the Option Value 2 in Raw format.";}
    if (divName == "moSelectBoxName") {return "This is the Select Box Name that contains this Option.";}  
    if (divName == "moSubfamilyName") {return "This is the Subfamily name for the Select Box that contains this Option.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    if (myForm.oldOptionDefault.value == "True") {
        myForm.optionDefault.checked = true;
    }
    myForm.optionText.focus();
}


function setOldDefault() {
    var myForm = document.form1;
    for (var i = 0; i < iO; i++) {
        if (options [i] [5] == "default") {
            myForm.oldDefault.value = options [i] [1];
            return;
        }
    }
} 

function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
    // Check Option Text
    
    work = myForm.optionText.value;
    if (work.length == 0) {
        alert ("Please enter a valid Option Text.");
        myForm.optionText.focus();
        return false;
    }
    
    // Check Option Value
    
    work = myForm.optionValue1.value;
    if (work.length == 0) {
        alert ("Please enter a valid Option Value.");
        myForm.optionValue1.focus();
        return false;
    }
    
    // Check Display Order
    
    work = myForm.displayOrder.value;
    if (work.length == 0) {
        alert ("Please enter a Display Order value between 0 and 9999.");
        myForm.displayOrder.focus();
        return false;
    }
    
    var oldDef = false;
    if (myForm.oldOptionDefault.value == "True") {
        oldDef = true;
    }
    var newDef = myForm.optionDefault.checked;
    if (myForm.oldOptionText.value == myForm.optionText.value
        && myForm.oldOptionValue1.value == myForm.optionValue1.value
        && myForm.oldDisplayOrder.value == myForm.displayOrder.value
        && myForm.oldOptionImage.value == myForm.optionImage.value
        && oldDef == newDef) {
            alert ("No Changes were made; Modify has been aborted.");
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
    
        var image = "${imageURLBase}${optionImage}";
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

<form name="form1" action="gpsomf4.do" method=post onsubmit="return My_Validator()">
    
<p>
    <input type="hidden" value="error" name="validation" />
    <input type="hidden" value="" name="oldDefault" />
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
                    Parametric Search<br />Select Box Options Maintenance<br />
			Modify Select Box Option
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
    <br />
    
    <table border="0" align="center" width="100%">
            <tr>
                <td colspan="2" align="center">
                    <p>You can modify the contents of a Select Box Option here.
                    The Option Text, Option Value, and Display Order must contain unique data
                    for every Option defined within a Select Box.
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
            
<!-- Select Box Name -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Select Box Name:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="selectBoxName" size="36"                        value = "${selectBoxName}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moSelectBoxName')"
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
                    <input type="text" name="minimum" size="2" maxlength="2"
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
            
<!-- Old Option Text -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Old Option Text (Cooked):&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="oldOptionText" size="48"
                        value = "${optionText}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moOldOptionText')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            
<!-- Option Text -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">New Option Text (Cooked):&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="optionText" size="48"
                        value = "${optionText}"
                        onblur="checkOptionText()"
                        onmouseover="showTip(event, 'moOptionText')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            
                        
<!-- Old Option Value 1 -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Old Option Value (Raw):&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="oldOptionValue1" size="48" 
                        readonly="readonly"
                        value="${optionValue1}"
                        onmouseover="showTip(event, 'moOldOptionValue1')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            
<!-- Option Value 1 -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">New Option Value (Raw):&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="optionValue1" size="48" maxlength="48"
                        value="${optionValue1}"
                        onblur="checkOptionValue1()"
                        onmouseover="showTip(event, 'moOptionValue1')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            
 <!-- Option Value 2 -->
 <!--          
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Option Value 2 (Raw):&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="optionValue2" size="48" maxlength="48"
                        onblur="checkOptionValue2()"
                        onmouseover="showTip(event, 'moOldOptionValue2')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
-->

                        
<!-- Old Option Image -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Old Option Image:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="oldOptionImage" size="48" 
                        readonly="readonly"
                        value="${optionImage}${fileStatus}"
                        onmouseover="showTip(event, 'moOldOptionImage')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            
<script language="JavaScript" type="text/javascript">
<!--
    if ("${optionImage}" > "" && "${fileStatus}" == "") {
        document.write("<tr><td>&nbsp;</td><td>");
        document.write("<img name='x' src='" + image + "' border='1' width='300px' />");
        document.write("</td></tr>");
    }
//-->    
</script> 
<!--
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">&nbsp;</span>
                </td>
                <td align="left" width="70%">

                    <img name="optionImageView" src="${imageURLBase}${optionImage}" border="1" width="300"  />

                </td>
            </tr>
-->            
<!-- Option Image -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">New Option Image:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="optionImage" size="48" maxlength="48"
                        value="${optionImage}"
                        onblur="checkOptionImage()"
                        onmouseover="showTip(event, 'moOptionImage')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>

<!--  Old Display Ordering  -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Old Display Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="4" name="oldDisplayOrder"
                readonly="readonly"
                value = "${displayOrder}"
                onmouseover="showTip(event, 'moOldOrder')"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>

<!--  Display Ordering  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">New Display Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="4" maxlength="4" name="displayOrder"
                value = "${displayOrder}"
                onblur="checkDisplayOrder()"
                onmouseover="showTip(event, 'moDisplayOrder')"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>
  
<!--  Old Default  -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Old Default Setting:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" name="oldOptionDefault" size="8"
                readonly="readonly"
                value = "${optionDefault}"               
                onmouseover="showTip(event, 'moOldDefault')"
                onmouseout="hideTip()" 
          />
            </span>
        </td>
      </tr>
      
<!--  Default  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Set as Default:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="checkbox" name="optionDefault"
                onblur="checkOptionDefault()"
                onmouseover="showTip(event, 'moOptionDefault')"
                onmouseout="hideTip()" 
          /> &nbsp;Default
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
            <input type="button" value="&nbsp;&nbsp;&nbsp;Cancel&nbsp;&nbsp;&nbsp;" name="B9" onclick="Javascript: window.location='gpsomf1.do'; " 
	onmouseover="showTip(event, 'moCancel')" 
        onmouseout="hideTip()"
	/>
                     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsof.jsp'; " 
	onmouseover="showTip(event, 'moExit')" 
        onmouseout="hideTip()"
	/>
          </center>
        </td>
      </tr>
    </table>
    <br />
    

        
    <table border="1" align="center" width="100%">

            <tr>
                <td colspan="4" align="center">
                Existing Options in this Select Box.
                </td>
            </tr>            
            <tr>
                <td width="6%">
                    <span class="requiredLabel">Display<br />Order&nbsp;</span>
                </td>
                <td width="30%">
                    <span class="requiredLabel">Option Text (Cooked)</span>
                </td>
                <td width="30%">
                    <span class="requiredLabel">Value (Raw)</span>
                </td>
                <!--
                <td width="28%">
                    <span class="requiredLabel">Value 2 (Raw)</span>
                </td>
                -->
                <td width="8%">
                    <span class="requiredLabel">Default</span>
                </td>
                <td width="28%">
                    <span class="requiredLabel">Image</span>
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

            


<!--     Exit      -->

          <br /><br /><br />
<p><center>
                <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsof.jsp'; " 
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