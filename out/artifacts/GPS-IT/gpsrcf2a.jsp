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
	<title>Galco Parametric Search - Create Rule Set - Error</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
<!--

	GPS Add Rule - Error
        
        version 1.3.00

	Modification History

        04/23/2008      DES     Modified to support 4 Divisions
        
        
-->

<!--	*************************************************************
	*		My JavaScript Functions and junk	      	*
	*************************************************************  -->

<script language="JavaScript" type="text/javascript">
<!--

function getMessage(divName) {
    if (divName == "header"){return "To change the Rule Scope, Line/Family/Subfamily name, or the Field Number, you must start over.";}
    if (divName == "moExit"){return "Click Exit to abandon this rule set and return to the previous Menu.";}
    if (divName == "moStartOver"){return "Click to abandon this rule set and start from the beginning.";}
    return "";
}

function setDefaults() {
	var myForm = document.form1;
}

//	*************************************************************
//	*           Form Validation Pre-Submit                      *
//	*************************************************************

function My_Validator() {
    var myForm = document.form1;

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

<jsp:useBean id="sRuleSet" class="gps.util.GPSrules" scope="session"/>

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
  <input type="hidden" name="seqNumbers" value="${sessionScope.seqNumbers}" />
  <input type="hidden" name="ruleScope" 
        value="<jsp:getProperty name="sRuleSet" property="ruleScope" />"
        />
</p>

<table border="0" width="100%">

<!-- Logo and Heading  -->

  <tr>
    <td align="center" colspan="2">
      <h2>
        Parametric Search Rules Maintenance - Create Rule Set- Error
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
    
    <tr>
        <td>
            <h2>
                ${message}
            </h2>
            <h3>
                To create a new global rule, you must first export and delete 
                all pre-existing parametric data for this family.
            </h3>
            <h3>
                To create a new local rule, you must first export and delete 
                all pre-existing parametric data for this family/subfamily.
            </h3>
        </td>
    </tr>
<!--  Continue or Clear  -->

      <tr>
        <td>
	<br />
        <center>

        <input type="button" value="Start Over" name="B2" 
            onclick='JavaScript: location.href="gpsrcf1.do";' 
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