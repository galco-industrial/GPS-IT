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
	<title>Galco Parametric Search - Delete Units</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>

<script language="JavaScript" type="text/javascript">
<!--

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
    if (divName == "moDelete") {return "Click to Delete this Units record.";}
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
    var k = 0;
    myForm.auditUserID.focus();
    
    if (iR > 0) {
        alert ("This Unit cannot be deleted because there are references to it in the Rules table.");
        myForm.B1.disabled = true;
        myForm.B2.focus();
        return;
    }
    if (isBaseUnitDef) {
        for (var i = 0; i < iU; i++) {
            if (units[i][2] == "${baseUnits}") {
                if (units[i][1] != "${baseUnits}") {
                    k++;
                }
            }            
        }
    }
    if (k > 0) {
        alert ("This Base Unit cannot be deleted because there are Multiplier Units that reference it.");
        myForm.B1.disabled = true;
        myForm.B2.focus();
    }
}

function My_Validator() {
    var myForm = document.form1;
    var work = "";
    
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
        var references = new Array();
        var iU = 0;
        var iR = 0;
                
        <c:forEach var="item" items="${unitsList}">
            units[iU++] = new Array(${item});
        </c:forEach>
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

<form name="form1" action="gpsudf3.do" method="post" onsubmit="return My_Validator()">
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
			Delete Unit.
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
                    <p>Review the Unit you wish to Delete.
                     
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
          <span class="fixedLabel">Display Unit:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="24" maxlength="24" name="displayUnits"
	      readonly="readonly" 
                value="${displayUnits}"
              onmouseover="showTip(event, 'moNoModify')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
<!-- Old Display Order -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Display Order:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="6" maxlength="4" name="displayOrder"
	      readonly="readonly" value="${displayOrder}"
              onmouseover="showTip(event, 'moNoModify')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
<!-- Old Multiplier Base -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Multiplier Base:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="12" maxlength="12" name="multiplierBase"
                value = "${multiplierBase}"
                readonly="readonly"
              onmouseover="showTip(event, 'moNoModify')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      

<!-- Old Multiplier Exponent -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Multiplier Exponent:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="12" maxlength="3" name="multiplierExp"
	      readonly="readonly"
                value="${multiplierExp}"
              onmouseover="showTip(event, 'moNoModify')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
        
<!-- Old Multiplier Pre-Adjust -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Multiplier Pre-Adjust:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="12" maxlength="12" name="multiplierPreAdjust"
                          value="${multiplierPreAdjust}"
	      readonly="readonly"
              onmouseover="showTip(event, 'moNoModify')"
              onmouseout="hideTip()" />
        </td>
      </tr>
      
<!-- Old Multiplier Post-Adjust -->
           
      <tr>
        <td align="right">
          <span class="fixedLabel">Multiplier Post-Adjust:&nbsp;</span>
        </td>
        <td><span class="dataField">
          <input type="text" size="12" maxlength="12" name="multiplierPostAdjust"
                value="${multiplierPostAdjust}"
	      readonly="readonly"
              onmouseover="showTip(event, 'moNoModify')"
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
                    <center><b> Existing Multiplier Units with the same Base Units.</b>
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
                  if (units[i][2] == "${baseUnits}" && units[i][2] != units[i][1]) {
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
                    document.write("No Multiplier Units currently exist for this Base Unit.");
                    document.write("</td></tr>");
               }
               
//-->
            </script>
            
<!-- Display any existing references  in Rules Table -->

    <script language="JavaScript" type="text/javascript">
<!--
      
    if (iR > 0) {
        document.write("<table border=\"1\" align=\"center\" width=\"50%\">");
        document.write("<tr><td colspan=\"4\" align=\"center\">");
        document.write("<b>Existing references in Rules Table</b>");
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
        document.write("<tr><td colspan=\"8\"><div><br /><p><center><h3>No references to this Display Unit exist in the Rules Table.</h3></center></p></div></td></tr>");
    
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
            <input type="submit" value="Delete" name="B1" 
	onmouseover="showTip(event, 'moDelete')" 
        onmouseout="hideTip()"
	/>
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;Cancel&nbsp;" name="B2" onclick="Javascript: window.location='gpsudf1.do'; " 
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