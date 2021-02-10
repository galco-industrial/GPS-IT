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
	<title>Galco Parametric Search - Delete Select Box Option</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- Modification History
        
        version 1.5.01
        
        09/01/2007 DES Modified to use Ajax to obtain line/family/subfamily codes
        
        -->

<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "moAuditUserID") { return "Enter your User ID. Eventually your User ID will be extracted from your sign on info.";}
    if (divName == "moCancel") { return "Click to Cancel and return to the previous window.";}
    if (divName == "moDataType") { return "This is the Data Type of the Option raw value.";}
    if (divName == "moDelete") { return "Click to Delete this Option from the Select Box.";}
    if (divName == "moDisplayOrder") { return "This is the order in which the option displays within the Select Box.";}
    if (divName == "moExit") {return "Click Exit to return to the previous Menu.";}
    if (divName == "moFamilyName") {return "This is the Family name for the Select Box that contains this Option.";}
    if (divName == "moMaximum") {return "This is the maximum length of the Option Text value.";}
    if (divName == "moMinimum") {return "This is the minimum length of the Option Text value.";}
    if (divName == "moOptionDefault") { return "This option is the default option for this Select Box.";}
    if (divName == "moOptionText") { return "This is the Option text (Cooked).";}
    if (divName == "moOptionValue1") { return "This is the Option Value (Raw).";}
    if (divName == "moOptionValue2") { return "";}
    if (divName == "moSelectBoxName") {return "This is the Select Box Name that contains this Option.";}  
    if (divName == "moSubfamilyName") {return "This is the Subfamily name for the Select Box that contains this Option.";}
    return "";
}

function setDefaults() {
    var myForm = document.form1;
    myForm.B9.focus();
}

function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
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

<form name="form1" action="gpsodf4.do" method="post" onsubmit="return My_Validator()">
<p>
    <input type="hidden" value="error" name="validation" />
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
			Delete Select Box Option
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
            <tr>
                <td colspan="2" align="center">
                    <p>This Option will be deleted from this Select Box. 
                    Any references to the deleted raw value within 
                    the parametric data will
                    no longer match an entry in this Select Box.
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
                    <input type="text" name="selectBoxName" size="36"
                        value = "${selectBoxName}"
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
            
<!-- Option Text -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Option Text:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="optionText" size="48"
                        value = "${optionText}"
                        readonly="readonly"
                        onmouseover="showTip(event, 'moOptionText')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
            

<!-- Option Value 1 -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Option Value (Raw):&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="optionValue1" size="48"
                        value="${optionValue1}"
                        readonly="readonly"
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
                    <span class="fixedLabel">Option Value 2 (Raw):&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="optionValue2" size="48" 
                        value="${optionValue2}"
                        readonly = "readonly"                        
                        onmouseover="showTip(event, 'moOptionValue2')"
                        onmouseout="hideTip()"
                    />
                </span>
                </td>
            </tr>
-->
<!-- Option Image -->
           
            <tr>
                <td align="right" width="30%">
                    <span class="fixedLabel">Option Image:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                <span class="dataField">
                    <input type="text" name="optionImage" size="48" 
                        readonly="readonly"
                        value="${optionImage}${fileStatus}"
                        onmouseover="showTip(event, 'moOptionImage')"
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
<!--  Display Ordering  -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Display Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="4" name="displayOrder"
                value="${displayOrder}"
                readonly="readonly"
                onmouseover="showTip(event, 'moDisplayOrder')"
                onmouseout="hideTip()" 
          /> 
            </span>
        </td>
      </tr>
      
<!--  Default  -->

      <tr>
        <td align="right">
          <span class="fixedLabel">Default:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="8" name="optionDefault"
                value="${optionDefault}"
                readonly="readonly"
                onmouseover="showTip(event, 'moOptionDefault')"
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
            <input type="submit" value="Delete" name="B1" 
	onmouseover="showTip(event, 'moDelete')" 
        onmouseout="hideTip()"
	/>
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;Cancel&nbsp;&nbsp;&nbsp;" name="B9" onclick="Javascript: window.location='gpsodf1.do'; " 
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
                <td width="8%">
                    <span class="requiredLabel">Default</span>
                </td>
                <!--
                <td width="28%">
                    <span class="requiredLabel">Value 2 (Raw)</span>
                </td>
                -->
                <td width="26">
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
                    //document.write("</span></td><td><span class='dataField'>");
                    //document.write(options[i][4] + "&nbsp;" );
                    document.write("</span></td><td><span class='dataField'>");
                    document.write(options[i][6] + "&nbsp;" );
                    document.write("</span></td></tr>");
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