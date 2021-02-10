<%@page contentType="text/html"%>
<%@page language="java" import="java.util.*" session="true"%>
<%@page pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
	<title>Galco Parametric Search - Copy Rule Set - Part 2</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <jsp:useBean id="sRuleSet" class="gps.util.GPSrules" scope="session"/>
        

    <!-- gpsruf3.jsp

    Modification History

    version 1.3.00


    04/23/2008      DES     Modified to support 4 Divisions

    -->

<!--	*************************************************************
	*		My JavaScript Functions and junk	      	*
	*************************************************************  -->

<script language="JavaScript" type="text/javascript">
<!--

function checkDescription() {
    var myForm = document.form1;
    var work = myForm.description.value;
    myForm.description.value = checkForXMLEntities(work);
}

function checkDEOrder() {
    // squish spaces and check for null or numerics
    var myForm = document.form1;
    var work = myForm.deOrder.value;
    work = deleteLeadingZeroes(deleteSpaces(work));
    myForm.deOrder.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value.");
            myForm.deOrder.focus();
            return;
	} else {
            if (parseInt(work) == 0 ) {
		alert ("The value cannot be zero.");
		myForm.deOrder.focus();
		return;                    
            }
        }
    }
}

function checkDEToolTip() {
    var myForm = document.form1;
    var work = myForm.deToolTip.value;
    myForm.deToolTip.value = checkForXMLEntities(work);
}

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
    }
}

function checkForEntity(work, from, to) {
    var result = "";
    var pos = work.indexOf(from);
    var len = from.length;
    while (pos > -1) {
        result += work.substring(0, pos) + to;
        work = work.substring(pos + len);
        pos = work.indexOf(from);
    }
    result += work;
    return result;
}

function checkForXMLEntities(work) {
    work = checkForEntity(work, "&amp;", "&");
    work = checkForEntity(work, "&lt;", "<");
    work = checkForEntity(work, "&gt;", ">");
    work = checkForEntity(work, "&apos;", "'");
    work = checkForEntity(work, "&quot;", '"');
    work = checkForEntity(work, "&", "&amp;");
    work = checkForEntity(work, "<", "&lt;");
    work = checkForEntity(work, ">", "&gt;");
    work = checkForEntity(work, "'", "&apos;");
    work = checkForEntity(work, '"', "&quot;");
    return work;
}

function checkMandatory() {
    // if Mandatory for a search, set and disable DE required field.
    var myForm = document.form1;
    var sel = getSelectedRadioValue(myForm.searchRequired);
    if (sel == "Y") {
	// alert ("When Mandatory for a Search is set to Yes, Required for Data Entry will be forced to Yes as well.");
	
        myForm.deRequired[0].checked = true;
	myForm.deRequired[0].disabled = true;
	myForm.deRequired[1].disabled = true;
    }
    if (sel == "N") {
        myForm.deRequired[0].disabled = false;
	myForm.deRequired[1].disabled = false;
    }
}

function checkMatchOrder() {
    // squish spaces and check for null or numerics
    var myForm = document.form1;
    var work = myForm.matchOrder.value;
    work = deleteSpaces(work);
    myForm.matchOrder.value = work;
    if (work.length > 0) {
    	if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value.");
            myForm.matchOrder.focus();
            return;
        }
    }
}

function checkParmName() {
    var myForm = document.form1;
    var work = myForm.parmName.value;
    work = deleteTrailingSpaces(deleteLeadingSpaces(reduceSpaces(work)));
    myForm.parmName.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, UC + LC + NU + " @#$%^&*()-+':,.?/") == false) {
            alert ("Please enter a valid Label.");
            myForm.parmName.focus();
            return;
	}
    }
}

function checkPreviewOrder() {
    var myForm = document.form1;
    var work = myForm.previewOrder.value;
    work = deleteSpaces(work);
    myForm.previewOrder.value = work;
    if (work.length > 0) {
    	if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value.");
            myForm.previewOrder.focus();
            return;
	}
    }
}

function checkSearchOrder() {
    var myForm = document.form1;
    var work = myForm.searchOrder.value;
    work = deleteSpaces(work);
    myForm.searchOrder.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, NU) == false) {
            alert ("Please enter a valid numeric value.");
            myForm.searchOrder.focus();
            return;
	}
    }
}

function checkSearchToolTip() {
    var myForm = document.form1;
    var work = myForm.searchToolTip.value;
    myForm.searchToolTip.value = checkForXMLEntities(work);
}

function checkSeqNum() {
    var myForm = document.form1;
    var sn = myForm.seqNum.value;
    var localRule = myForm.ruleScope.value == "L";
    if (localRule && sn < 51) {
        alert ("Warning! This local Rule will overide any global (family) Rule with the same field number.");
    }
}
    

function checkSelectBoxFilter() {
}

function checkSeriesImplicit() {
}

function getMessage(divName) {
    if (divName == "header"){return "You cannot change the Rule Scope, Line/Family/Subfamily name, or the Field Number here.";}
    if (divName == "moContinue"){return "Click to continue with copying this field's rule set.";}
    if (divName == "moDEOrder"){return "The Data Entry Order (1 - 99) sets the field order expected for data entry.";}
    if (divName == "moDEToolTip"){return "Enter a help message to be displayed during data entry.";}
    if (divName == "moDescr"){return "Enter general description and implementation comments here. Click the upper left corner of the box to begin.";}
    if (divName == "moDispJust"){return "Justification determines how the data is presented in a report column.";}
    if (divName == "moDisplayOrder"){return "The Display Order (0 - 99) sets the relative order of this field when displayed on a screen or report.";}
    if (divName == "moExit"){return "Click Exit to abandon this rule set and return to the previous Menu.";}
    if (divName == "moMatchOrder"){return "This is the order in which search values are matched when cross-referencing by Part Number.";}
    if (divName == "moParmName"){return "The Parm Name is a label/heading that identifies a parametric field/column. It can be up to 24 characters long.";}
    if (divName == "moRequired"){return "Required parametric data fields can never be left blank.";}
    if (divName == "moPreviewOrder"){return "This is the order in which values are displayed for match lists when searching by value.";}
    if (divName == "moSearchOrder"){return "This rule determines the order in which parametric fields are searched.";}
    if (divName == "moSearchToolTip"){return "Enter a help message to be displayed during a search operation.";}
    if (divName == "moSearchReqd"){return "Identifies a field that must contain a mandatory search argument.";}
    if (divName == "moSelectBoxFilter"){return "Select Yes if this field should be used as a filter to populate subsequent Select Box Option Values for a Search.";}
    if (divName == "moSeqNum"){return "Choose a Field Number for this rule set.";}
    if (divName == "moSeriesImplicit"){return "A Yes indicates that the manufacturer series implies the value for this field.";}
    if (divName == "moStartOver"){return "Click to abandon this rule set and start from the beginning.";}
    if (divName == "moStatus"){return "Inactive parametric fields are ignored for display, and searches.";}
    return "";
}

function setDefaults() {
	var myForm = document.form1;
        checkMandatory();
        myForm.parmName.focus();
}


//	*************************************************************
//	*           Form Validation Pre-Submit                      *
//	*************************************************************

function My_Validator() {
    var work;
    var work2;
    var myForm = document.form1;

	// Check Parm Label

    work = myForm.parmName.value;
    if (work.length == 0) {	
    	alert("Please enter a Parameter Label for this field.");
        myForm.parmName.focus();
	return false;
    }

	// Field cannot be optional if required for a search

    if (myForm.searchRequired[0].checked == true && myForm.deRequired[1].checked == true) {	
        alert("Mandatory Search fields must be Required for Data Entry.");
	myForm.deRequired[0].focus();
	return false;
    }

	// Make sure we filled out whether field is required for a search or not.

    if (myForm.searchRequired[0].checked == false && myForm.searchRequired[1].checked == false) {	
        alert("Please choose if this parameter must be required for a search.");
     	myForm.searchRequired[0].focus();
	return false;
    }

    	// Check Data Entry Order
        
    work = myForm.deOrder.value;
    if (work.length == 0) {	
        alert("Please enter a Data Entry Order value between 1 and 99 for this Rule.");
	myForm.deOrder.focus();
	return false;
    }
    if (parseInt(work) == 0 ) {
        alert("Data Entry Order cannot be zero.");
	myForm.de.focus();
	return false;
    }

	// Check Display Order

    work = myForm.displayOrder.value;
    if (work.length == 0) {	
        alert("Please enter a Display Order value between 0 and 99 for this Rule.");
        myForm.displayOrder.focus();
	return false;
    }
    if (parseInt(work) == 0 && myForm.searchRequired[0].checked == true) {
    	alert("Display Order cannot be zero if this field is required for a search.");
        myForm.displayOrder.focus();
	return false;
    }

	// Check Search Order

    work = myForm.searchOrder.value;
    if (work.length == 0) {	
        alert("Please enter a Search Order value between 0 and 99 for this Rule.");
      	myForm.searchOrder.focus();
	return false;
    }
    if (parseInt(work) == 0 && myForm.searchRequired[0].checked == true) {
        alert("Search Order cannot be zero if this field is required for a search.");
	myForm.searchOrder.focus();
	return false;
    }
        
	// Check Preview Order

    work = myForm.previewOrder.value;
    if (work.length == 0) {	
        alert("Please enter a Preview Order value between 0 and 99 for this Rule.");
	myForm.previewOrder.focus();
	return false;
    }
    if (parseInt(work) == 0 && myForm.searchRequired[0].checked == true) {
        alert("Preview Order cannot be zero if this field is required for a search.");
       	myForm.previewOrder.focus();
	return false;
    }
                
	// Check Match Order

    work = myForm.matchOrder.value;
    if (work.length == 0) {	
        alert("Please enter a Match Order value between 0 and 99 for this Rule.");
        myForm.matchOrder.focus();
	return false;
    }
    if (parseInt(work) == 0 && myForm.searchRequired[0].checked == true) {
        alert("Match Order cannot be zero if this field is required for a search.");
	myForm.matchOrder.focus();
	return false;
    }

	// Get Data type in work	

    work = myForm.txtDataType.value;

	// Set URL to point to next page based upon data type in work

    myForm.action = "gpsruf4" + work.toLowerCase() + ".do";

	// Enable disabled objects so their values will be sent to the server

    myForm.deRequired[0].disabled = false;
    myForm.deRequired[1].disabled = false;   
    
    // All Validation tests are complete
    myForm.validation.value = "OK";
    return true;
}

//-->
</script>
</head>

<body onload="setDefaults()">

<script language="JavaScript" type="text/javascript">
<!--

    var junk = "";
    var inputObject = "";
    
//-->	
</script>

<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">

<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>

<form method="post" action="#" onsubmit="return My_Validator()" name="form1">
<p>
  <input type="hidden" name="validation" value="Error" />
  <input type="hidden" name="txtDataType" 
         value="<jsp:getProperty name="sRuleSet" property="dataType" />"
         />
  <input type="hidden" name="ruleScope" 
        value="<jsp:getProperty name="sRuleSet" property="ruleScope" />"
        />
</p>

<table border="0" width="100%">

<!-- Logo and Heading  -->

  <tr>
    <td align="center" colspan="2">
      <h2>
        Parametric Search Rules Maintenance - Copy Rule Set- Part 2
      </h2>
    </td>
  </tr>
  <tr>
    <td rowspan="3" align="center" width="25%">
      <img src="gl_25.gif" alt="Galco logo is shown here" />
      <br />
      <div class="toolTipSwitch">
		<input type="checkbox" 
<%
		String tip = (String) session.getAttribute("enableToolTips");
                if (tip != null && tip.equals("checked")) {
			out.println(" checked=\"checked\" ");
                }
%> 
		name="enableToolTips"
		value="checked"
		/>
		Enable Tool Tips
      </div>
    </td>
    <td>     
 <div class="masthead" onmouseover="showTip(event, 'header')" onmouseout="hideTip()" >
        <table border="1" width="98%" align="center" >
          <tr>
            <td>
              <table border="0" width="100%">

<!-- Product Line -->

    <tr>
        <td colspan="4" align="center">
            <span class="headerLabel">
                Product Line:&nbsp;&nbsp;
            </span>
            <span class="headerData">
                <jsp:getProperty name="sRuleSet" property="productLineName" />
            </span>
        </td>
    </tr>  
    
<!--  Family Name  -->

                <tr>
                  <td align="right" width="25%">
                    <span class="headerLabel">
                      Family:&nbsp;
                    </span>
                  </td>
                  <td align="left" width="25%">
                    <span class="headerData">
                      <jsp:getProperty name="sRuleSet" property="familyName" />
                    </span>
                  </td>

<!--  Subfamily Name -->

                  <td align="right" width="25%">
                    <span class="headerLabel">
                      Subfamily:&nbsp;
                    </span>
                  </td>
                  <td align="left" width="25%">
                    <span class="headerData">
                      <jsp:getProperty name="sRuleSet" property="subfamilyName" />
                    </span>
                  </td>
                </tr>

<!--  Scope  -->

                <tr>
                  <td align="right" >
                    <span class="headerLabel">
                      Scope:&nbsp;
                    </span>
                  </td>
                  <td align="left" >
                    <span class="headerData">
                        
<script language="JavaScript" type="text/javascript">
<!--
                    junk = "<jsp:getProperty name="sRuleSet" property="ruleScope" />";
                    if (junk == "G") {
                        document.write("Global");
                    }
                    if (junk == "L") {
                        document.write("Local");
                    }
//-->
</script>
                    </span>
                  </td>

<!--  Sequence Number  -->

                  <td align="right">
                    <span class="headerLabel">
                      Field No.:&nbsp;
                    </span>
                  </td>
                  <td align="left">
                    <span class="headerData">
                       <jsp:getProperty name="sRuleSet" property="seqNum" /> 
                    </span>
                  </td>
                </tr>

<!--  Field Name --> 

                <tr>
                  <td align="right">
                    <span class="headerLabel">
                      &nbsp;
                    </span>
                  </td>
                  <td align="left">
                    <span class="headerData">
                      &nbsp;
                    </span>
                  </td>
          
<!--  Data Type -->

                  <td align="right">
                    <span class="headerLabel">
                      Data Type:&nbsp;
                    </span>
                  </td>
                  <td align="left">
                    <span class="headerData">
<script language="JavaScript" type="text/javascript">
<!--
                    junk = "<jsp:getProperty name="sRuleSet" property="dataType" />";
                    if (junk == "N") {
                        document.write("N - Numeric");
                    }
                    if (junk == "S") {
                        document.write("S - String");
                    }
                    if (junk == "L") {
                        document.write("L - Logical");
                    }
                    if (junk == "D") {
                        document.write("D - Date");
                    }
 
//-->
</script>                        
                        
	
                    </span>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>      
      </div>
    </td>
  </tr>
</table>


<table border="0" width="100%">

<!--  Field Name  -->
        
      <tr>
        <td align="right" width="25%">
          <span class="requiredLabel">Field Name:&nbsp;</span>
        </td>
        <td align="left" width="75%"><span class="datafield">
          <input type="text" size="36" maxlength="24" name="parmName"
            value="<jsp:getProperty name="sRuleSet" property="parmName" />"
            onblur="checkParmName()"
            onmouseover="showTip(event, 'moParmName')"
            onmouseout="hideTip()" 
          /> 
        </span></td>
      </tr>

<!--  Description  -->

      <tr>
        <td align="right">
          <span class="label">Description:&nbsp;<br /><br /><br /></span>
        </td>
        <td align="left"><span class="datafield">
          <textarea cols="60" rows="4" name="description" 
          wrap="soft" onblur="checkDescription()"
          onmouseover="showTip(event, 'moDescr')"
          onmouseout="hideTip()"><jsp:getProperty name="sRuleSet" property="description" /></textarea>
        </span></td>
      </tr>
      
<!--  DE Tool Tip  -->

      <tr>
        <td align="right">
          <span class="label">D.E. Tool Tip:&nbsp;<br /><br /><br /></span>
        </td>
        <td align="left"><span class="datafield">
          <textarea cols="60" rows="4" name="deToolTip" 
          wrap="soft" onblur="checkDEToolTip()"
          onmouseover="showTip(event, 'moDEToolTip')"
          onmouseout="hideTip()"><jsp:getProperty name="sRuleSet" property="deToolTip" /></textarea>
        </span></td>
      </tr>
      
<!--  Search Tool Tip  -->

      <tr>
        <td align="right">
          <span class="label">Search Tool Tip:&nbsp;<br /><br /><br /></span>
        </td>
        <td align="left"><span class="datafield">
          <textarea cols="60" rows="4" name="searchToolTip" 
          wrap="soft" onblur="checkSearchToolTip()"
          onmouseover="showTip(event, 'moSearchToolTip')"
          onmouseout="hideTip()"><jsp:getProperty name="sRuleSet" property="searchToolTip" /></textarea>
        </span></td>
      </tr>

<!--  Status  -->

      <tr>
        <td align="right">
          <span class="label">Status:&nbsp;</span>
        </td>
        <td align="left"><span class="datafield">

<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="parmStatus" value="A"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moStatus'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="parmStatus" />';
        if (junk == "" || junk == "A") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          Active&nbsp;&nbsp;&nbsp;&nbsp;

<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="parmStatus" value="I"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moStatus'";
        inputObject += ')" onmouseout="hideTip()"';
        // junk = '<jsp:getProperty name="sRuleSet" property="parmStatus" />';
        if (junk == "I") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            Inactive
        </span></td>
      </tr>


<!--  Mandatory Search Field  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Mandatory for a Search:&nbsp;</span>
        </td>
        <td align="left"><span class="datafield">
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="searchRequired" value="Y"';
        inputObject += ' onclick="checkMandatory()" onmouseover="showTip(event, ';
        inputObject += "'moSearchReqd'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="searchRequired" />';
        if (junk == "true") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          Yes&nbsp;&nbsp;&nbsp;&nbsp;
            
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="searchRequired" value="N"';
        inputObject += ' onclick="checkMandatory()" onmouseover="showTip(event, ';
        inputObject += "'moSearchReqd'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="searchRequired" />';
        if (junk == "" || junk == "false") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            No
        </span></td>
      </tr>


<!--  deRequired  -->

      <tr>
        <td align="right">
          <span class="label">Required for D.E.:&nbsp;</span>
        </td>
        <td align="left"><span class="datafield">
                        
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="deRequired" value="Y"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moRequired'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="deRequired" />';
        if (junk == "" || junk == "true") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          Yes&nbsp;&nbsp;&nbsp;&nbsp;
                                  
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="deRequired" value="N"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moRequired'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="deRequired" />';
        if (junk == "false") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          No
        </span></td>
      </tr>
       
<!--  Series Implicit  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Mfgr Series Implicit:&nbsp;</span>
        </td>
        <td align="left"><span class="datafield">
                                              
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="seriesImplicit" value="Y"';
        inputObject += ' onclick="checkSeriesImplicit()" onmouseover="showTip(event, ';
        inputObject += "'moSeriesImplicit'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="seriesImplicit" />';
        if (junk == "true") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          Yes&nbsp;&nbsp;&nbsp;&nbsp;
                                              
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="seriesImplicit" value="N"';
        inputObject += ' onclick="checkSeriesImplicit()" onmouseover="showTip(event, ';
        inputObject += "'moSeriesImplicit'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="seriesImplicit" />';
        if (junk == "false") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            No
        </span></td>
      </tr>
      
<!--  Select Box Filter  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Select Box Filter:&nbsp;</span>
        </td>
        <td align="left"><span class="datafield">
                                                          
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="selectBoxFilter" value="Y"';
        inputObject += ' onclick="checkSelectBoxFilter()" onmouseover="showTip(event, ';
        inputObject += "'moSelectBoxFilter'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="selectBoxFilter" />';
        if (junk == "true") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          Yes&nbsp;&nbsp;&nbsp;&nbsp;
                                                                    
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="selectBoxFilter" value="N"';
        inputObject += ' onclick="checkSelectBoxFilter()" onmouseover="showTip(event, ';
        inputObject += "'moSelectBoxFilter'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="selectBoxFilter" />';
        if (junk == "false") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            No
        </span></td>
      </tr>


<!--  D E Ordering  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">D. E. Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="3" maxlength="2" name="deOrder"
            
          value="<jsp:getProperty name="sRuleSet" property="deOrder" />"

          onblur="checkDEOrder()"
          onmouseover="showTip(event, 'moDEOrder')"
          onmouseout="hideTip()" 
          /> 
        </span></td>
      </tr>


<!--  Display Ordering  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Display Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="3" maxlength="2" name="displayOrder"
	
	  value="<jsp:getProperty name="sRuleSet" property="displayOrder" />"

          onblur="checkDisplayOrder()"
          onmouseover="showTip(event, 'moDisplayOrder')"
          onmouseout="hideTip()" 
          /> 
          <font color="#FF0000"><i>&nbsp;&nbsp;(0 = Hide; Do not display)</i></font>
        </span></td>
      </tr>

<!--  Search Order  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Search Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="3" maxlength="2" name="searchOrder"
	
	value="<jsp:getProperty name="sRuleSet" property="searchOrder" />"

          onblur="checkSearchOrder()"
          onmouseover="showTip(event, 'moSearchOrder')"
          onmouseout="hideTip()" 
          /> 
          <font color="#FF0000"><i>&nbsp;&nbsp;(0 = Not a Search field)</i></font>
        </span></td>
      </tr>
     
<!--  Preview Order  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Preview Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="3" maxlength="2" name="previewOrder"
	
	value="<jsp:getProperty name="sRuleSet" property="previewOrder" />"

          onblur="checkPreviewOrder()"
          onmouseover="showTip(event, 'moPreviewOrder')"
          onmouseout="hideTip()" 
          /> 
          <font color="#FF0000"><i>&nbsp;&nbsp;(0 = Not a Preview field)</i></font>
        </span></td>
      </tr>
      
<!--  Match Order  -->

      <tr>
        <td align="right">
          <span class="requiredLabel">Match Order:&nbsp;</span>
        </td>
        <td><span class="datafield">
          <input type="text" size="3" maxlength="2" name="matchOrder"
	
	value="<jsp:getProperty name="sRuleSet" property="matchOrder" />"

          onblur="checkMatchOrder()"
          onmouseover="showTip(event, 'moMatchOrder')"
          onmouseout="hideTip()" 
          /> 
          <font color="#FF0000"><i>&nbsp;&nbsp;(0 = Not a Match field)</i></font>
        </span></td>
      </tr>

<!--  Display Justification  -->

      <tr>
        <td align="right" width="20%">
          <span class="label">Justification:&nbsp;</span>
        </td>
        <td align="left"><span class="datafield">
                                                                                
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="displayJust" value="L"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moDispJust'";
        inputObject += ')" onmouseout="hideTip()"';
        junk = '<jsp:getProperty name="sRuleSet" property="displayJust" />';
        if (junk == "" || junk == "L") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

          Left&nbsp;&nbsp;&nbsp;&nbsp;
                                                                                          
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="displayJust" value="R"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moDispJust'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="displayJust" />';
        if (junk == "R") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
//-->
</script>

            Right&nbsp;&nbsp;&nbsp;&nbsp;
                                                                                                      
<script language="JavaScript" type="text/javascript">
<!--
        inputObject = '<input type="radio" name="displayJust" value="C"';
        inputObject += ' onmouseover="showTip(event, ';
        inputObject += "'moDispJust'";
        inputObject += ')" onmouseout="hideTip()"';
        //junk = '<jsp:getProperty name="sRuleSet" property="displayJust" />';
        if (junk == "C") {
            inputObject += ' checked="checked"';
        }
        inputObject += ' />';
        document.write(inputObject);
        document.close();
//-->
</script>

            Center
        </span></td>
      </tr>

<!--  Continue or Clear  -->

      <tr>
        <td colspan="2">
	<br />
        <center>
        <input type="submit" value="Continue" name="B1" 
            onmouseover="showTip(event, 'moContinue')" 
            onmouseout="hideTip()"
	/>

        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="button" value="Start Over" name="B2" 
            onclick='JavaScript: location.href="gpsruf1.do";' 
            onmouseover="showTip(event, 'moStartOver')" 
            onmouseout="hideTip()"
	/>

        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" 
            name="B3" 
            onclick="Javascript: window.location='gpsrf.jsp'; " 
            onmouseover="showTip(event, 'moExit')" 
            onmouseout="hideTip()"
	/>

          </center>
        </td>
      </tr>
</table>
<br /> 
<br />
<p>
    <img src="http://www.w3.org/Icons/valid-xhtml10"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
</p>

</form>
</div>
</body>

</html>