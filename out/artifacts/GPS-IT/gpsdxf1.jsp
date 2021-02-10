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
	<title>Galco Parametric Search - Extract Digest Data</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- Modification History
        
        version 1.0.00
        
        04/29/2008  DES fixed load family codes and subfamily codes to 
                        implement IE workaround to eliminate "Click OK" alert boxes
  
        
        -->

 <script language="JavaScript" type="text/javascript">
<!--

function changedFamilyCode() {
    //  I am invoked every time family code option changes
    deleteSubfamilyOptions();
    loadSubfamilyOptions();
}

function changedLineCode() {
    //  I am invoked every time Line code option changes
    deleteFamilyOptions();
    loadFamilyOptions();
    deleteSubfamilyOptions();
    loadSubfamilyOptions();
}

function checkCategoryCode() {
	var myForm = document.form1;
        var catCode = myForm.categoryCode.value;
        if (catCode != "0" ) {
            myForm.validation.value = "OK";
            myForm.action = "gpsdxf2.do";
            myForm.submit();
        } else {
            alert ("Add code to reset subcategory and template select boxes here.");
            myForm.categoryCode.focus();
        }
        return;
}

function checkTemplate() {
	var myForm = document.form1;
	if (myForm.categoryCode.value == "0") {
		alert ("Please select a Category Code first.");
                myForm.categoryCode.focus();
                return;
	}
        var subcatCode = myForm.subcategoryCode.value;
        if (subcatCode == "0" ) {
		alert ("Please select a Subcategory Code first.");
                myForm.subcategoryCode.focus();
                return;
        }
        return;
}

function checkPEOnly() {
    var myForm = document.form1;
    var objY = myForm.excludePreExisting[0];
    var objN = myForm.excludePreExisting[1];
    if (myForm.preExistingOnly[0].checked == true) {

        objN.checked = true;
        objN.disabled = true;
        objY.disabled = true;
    } else {
        objY.disabled = false;
        objN.disabled = false;
    }
}

function checkSubcategoryCode() {
	var myForm = document.form1;
	if (myForm.categoryCode.value == "0") {
		alert ("Please select a Category Code first.");
                myForm.categoryCode.focus();
                return;
	}
        var subcatCode = myForm.subcategoryCode.value;
        if (subcatCode != "0" ) {
            myForm.validation.value = "OK";
            myForm.action = "gpsdxf3.do";
            myForm.submit();
        } else {
            alert ("Add code to reset template select boxes here.");
            myForm.subcategoryCode.focus();
        }
        return;
}

function clickFamilyCode() {
    //  I am invoked every time family code option is clicked
    var myForm = document.form1;
    if (myForm.lineCode.value == "0") {
        alert ("Please select a Product Line first.");
        myForm.lineCode.focus();
        return;
    }
}

function clickSubfamilyCode() {
    var myForm = document.form1;
    if (myForm.lineCode.value == "0") {
    	alert ("Please select a Product Line first.");
        myForm.lineCode.focus();
        return;
    }
    if (myForm.familyCode.value == "0") {
    	alert ("Please select a Family first.");
        myForm.familyCode.focus();
    }
}

function deleteFamilyOptions() {
    var myForm = document.form1;
    var oListbox = myForm.familyCode;
    for (var i = oListbox.options.length-1; i >= 0; i--) {
        oListbox.remove(i);
    }
    return;
}
function deleteSpaces(stringToCheck) {
	// Eliminate all spaces
	var myChar;
	var loopCounter;
	var resultString = "";
	var strlen = stringToCheck.length;
	for (loopCounter = 0; loopCounter < strlen; loopCounter++) {
		myChar = stringToCheck.charAt(loopCounter);
		if (myChar != " ") {
			resultString += myChar;
		}
	}
	return resultString;
}

function deleteSubcategoryOptions() {
	var myForm = document.form1;
	var oListbox = myForm.subcategoryCode;
	for (var i = oListbox.options.length-1; i >= 0; i--) {
		oListbox.remove(i);
	}
	return true;
}

function deleteTemplateOptions() {
	var myForm = document.form1;
	var oListbox = myForm.template;
	for (var i = oListbox.options.length-1; i >= 0; i--) {
		oListbox.remove(i);
	}
	return true;
}

function deleteSubfamilyOptions() {
	var myForm = document.form1;
	var oListbox = myForm.subfamilyCode;
	for (var i = oListbox.options.length-1; i >= 0; i--) {
		oListbox.remove(i);
	}
	return;
}

function getMessage(divName) {
	if (divName == "moAuditUserID") {return "Enter your initials.";}
        if (divName == "moCategoryCode") {return "Select a Category Code for this extract.";}
        if (divName == "moExcludePreExisting") {return "Check Yes to exclude part numbers that already have parametric data on file."}
        if (divName == "moExit"){return "Click Exit to abandon this rule and return to the Rules Menu.";}
        if (divName == "moFamilyCode") {return "Select a Family Code for this extract.";}    
        if (divName == "moIncludeDigest") {return "Check Yes to include Digest information in the extract."}
        if (divName == "moLineCode") {return "Select a Product Line for this extract.";}  
        if (divName == "moPreExistingOnly") {return "Choose PreExisting Only to select only part numbers with parametric data."};
        if (divName == "moReset"){return "Click Reset to return the form to its initial state when first displayed.";} 
	if (divName == "moSubcategoryCode") {return "Select a Product Subcategory or 'ALL' for All subcategories.";}
	if (divName == "moSubfamilyCode") {return "Select a Subfamily Code for this extract.";}
        if (divName == "moTemplate"){return "Please select a Template for this extract.";}
	return "";
}
 
function loadFamilyOptions(deflt) {
    var myForm = document.form1;
    var work = myForm.lineCode.value;
    var oListbox = myForm.familyCode;
    var oOption;
    if (work == "0") {
        oOption = document.createElement("option");
        oOption.appendChild(document.createTextNode("Please select a Product Line first"));
        oOption.setAttribute("value", "0");
        oListbox.appendChild(oOption);
        oOption = null;
        return;
    }
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Please select a Family"));
    oOption.setAttribute("value", "0");
    oListbox.appendChild(oOption);
    oOption = null;
    for (var i = 0; i < family.length; i++) {
        if (family [i] [3] == work) {
            oOption = document.createElement("option");
            oOption.appendChild(document.createTextNode(family [i] [1] ));
            oOption.setAttribute("value", family [i] [0] );
            if (family [i] [0] == deflt) {
                oOption.setAttribute("selected", true);
            }
            oListbox.appendChild(oOption);
            oOption = null;
        }
    }
}

function loadSubfamilyOptions(deflt) {
    var myForm = document.form1;
    var work = myForm.familyCode.value;
    var oListbox = myForm.subfamilyCode;
    var oOption;
    if (work == "0") {
        oOption = document.createElement("option");
        oOption.appendChild(document.createTextNode("Please select a Family first"));
        oOption.setAttribute("value", "0");
        oListbox.appendChild(oOption);
        oOption = null;
        return;
    }
    oOption = document.createElement("option");
    oOption.appendChild(document.createTextNode("Please select a Subfamily"));
    oOption.setAttribute("value", "0");
    oListbox.appendChild(oOption);
    //oOption = document.createElement("option");
    //oOption.appendChild(document.createTextNode("All Subfamilies"));
    //oOption.setAttribute("value", "*");
    //oListbox.appendChild(oOption);
    //oOption = null;
    for (var i = 0; i < subfamily.length; i++) {
        if (subfamily [i] [0] == work) {
            oOption = document.createElement("option");
            oOption.appendChild(document.createTextNode(subfamily [i] [2] ));
            oOption.setAttribute("value", subfamily [i] [1] );
            if (subfamily [i] [1] == deflt) {
                oOption.setAttribute("selected", true);
            }    
            oListbox.appendChild(oOption);
            oOption = null;
        }
    }
}

function setDefaults() {
    var myForm = document.form1;
    var x;
    checkPEOnly();
    setSelectedLineOption();
    setSelectedFamilyOption();
    setSelectedSubfamilyOption();
    setSelectedCatCode();
    setSelectedSubcatCode();
    
    if (myForm.lineCode.value == "0") {
        myForm.lineCode.focus();
        return;
    }
    if (myForm.familyCode.value == "0") {
        myForm.familyCode.focus();
        return;
    }
    if (myForm.subfamilyCode.value == "0") {
        myForm.subfamilyCode.focus();
        return;
    }
    if (myForm.categoryCode.value == "0") {
        myForm.categoryCode.focus();
        return;
    }
    if (myForm.subcategoryCode.value == "0") {
        myForm.subcategoryCode.focus();
        return;
    }
    myForm.template.focus();
}

function setSelectedCatCode() {
    var myForm = document.form1;
    var work = myForm.selectedCategory.value;
    if (work.length != 0) {
        for (var i = 0; i < iC; i++) {
            if (category [i] == work) {
                myForm.categoryCode.selectedIndex = i + 1;
                break;
            }
        }
    }
}

function setSelectedFamilyOption() {
    var myForm = document.form1;
    var work = myForm.selectedFamily.value;
    //alert ("Selected Family Code is " + work);
    if (work.length != 0) {
        deleteFamilyOptions();
        loadFamilyOptions(work);
        //for (var i = 0; i < myForm.familyCode.length; i++) {
        //    if (myForm.familyCode.options[i].value == work) {
        //        alert ("Click OK");
        //        //alert (work + " matches Family Code option index " + i);
        //        myForm.familyCode.selectedIndex = i;
        //        //while(true) try { myForm.familyCode.options[i].selected = true; break; } catch(e) {}
        //        //myForm.familyCode.options[0].setAttribute('selected',false);
        //        //myForm.familyCode.options[i].setAttribute('selected',true);
        //        break;
        //    }
        //}
    }
}

function setSelectedLineOption() {
    var myForm = document.form1;
    var work = myForm.selectedLine.value;
    //alert ("Selected Product Line Code is " + work);
    if (work.length != 0) {
        for (var i = 0; i < iL; i++) {
            if (line [i] [0] == work) {
                myForm.lineCode.selectedIndex = i + 1;
                //alert ("Selected Index is " + i++);
                break;
            }
        }
    }
}

function setSelectedSubcatCode() {
    var myForm = document.form1;
    var work = myForm.selectedSubcategory.value;
    if (work.length != 0) {
        for (var i = 0; i < iB; i++) {
            if (subcategory [i] [1] == work) {
                myForm.subcategoryCode.selectedIndex = i ;
                break;
            }
        }
    }
}

function setSelectedSubfamilyOption() {
    var myForm = document.form1;
    var work = myForm.selectedSubfamily.value;
    //alert ("Selected Subfamily Code is " + work);
    var oListBox = myForm.subfamilyCode;
    if (work.length != 0) {
        deleteSubfamilyOptions();
        loadSubfamilyOptions(work);
        //for (var i = 0; i < oListBox.length; i++) {
        //    if (oListBox.options[i].value == work) {
        //        alert ("Click OK");
        //        //alert (work + " matches Subfamily Code option index " + i);
        //        myForm.subfamilyCode.selectedIndex = i;
        //        //while(true) try { myForm.subfamilyCode.options[i].selected = true; break; } catch(e) {}
        //        //myForm.subfamilyCode.options[i].setAttribute('selected',true);
        //        break;
        //    }
        //}
    }
    //alert (oListBox.length);
}

function My_Validator() {
    var myForm = document.form1;
        
    if (myForm.lineCode.value == "0") {
        alert ("Please select a Product Line");
        myForm.lineCode.focus();
        return false;
    }
    if (myForm.familyCode.value == "0") {
        alert ("Please select a Family code");
        myForm.familyCode.focus();
        return false;
    }
    
    if (myForm.subfamilyCode.value == "0") {
        alert ("Please select a Subfamily code");
        myForm.subfamilyCode.focus();
        return false;
    }
    
    if (myForm.categoryCode.value == "0") {
        alert ("Please select a Category code");
        myForm.categoryCode.focus();
        return false;
    }
    
    if (myForm.subcategoryCode.value == "0"
            || myForm.subcategoryCode.value == "*") {
        alert ("Please select a Subcategory code.");
        myForm.subcategoryCode.focus();
        return false;
    }
    
    if (myForm.template.value == "0") {
        alert ("Please select a Template.");
        myForm.template.focus();
        return false;
    }
    
    	// Check Audit User ID

	work = myForm.auditUserID.value;
	if (work.length == 0)
	{	
		alert("Please enter your User ID.");
		myForm.auditUserID.focus();
		return false;
	}

    alert ("Your request is being submitted to the server. Depending upon the amount of data being processed, the extract may take several minutes. Please be patient.");
        
    myForm.validation.value = "OK";
    return true;
}

//-->    
</script>        
</head>
<body onload="setDefaults()">

    <script language="JavaScript" type="text/javascript">
    <!--

        var category = new Array();
        var subcategory = new Array();
        var oTemplate = new Array();
        var line = new Array();
        var family = new Array();
        var subfamily = new Array();
        var iC = 0;
        var iB = 0;
        var iT = 0;
        var iL = 0;
        var iF = 0;
        var iS = 0;
        <c:forEach var="item" items="${sessionScope.categories}">
            category[iC++] = ${item};
        </c:forEach>
        <c:forEach var="item" items="${sessionScope.subcategories}">
            subcategory[iB++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${sessionScope.template}">
            oTemplate[iT++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${sessionScope.lines}">
            line[iL++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${sessionScope.families}">
            family[iF++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${sessionScope.subfamilies}">
            subfamily[iS++] = new Array(${item});
        </c:forEach>
    //-->    
    </script>
    
<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">
    
<div class="toolTip" id="virtualToolTip">
    <div class="toolTipHeader" >Tip</div>
    <div id="virtualToolTipText"></div>
</div>
    
<form name="form1" action="gpsdxf4.do" method="post" onsubmit="return My_Validator()">
        <p>
            <input type="hidden" name="validation" value="Error" />
            <input type="hidden" name="description" value="" />
            <input type="hidden" name="selectedCategory" value = "${selectedCategory}" />
            <input type="hidden" name="selectedSubcategory" value = "${selectedSubcategory}" />
            <input type="hidden" name="selectedLine" value = "${selectedLine}" />
            <input type="hidden" name="selectedFamily" value = "${selectedFamily}" />
            <input type="hidden" name="selectedSubfamily" value = "${selectedSubfamily}" />
            
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
                    Parametric Search Database Maintenance<br />
			Extract Digest Data - Part 1
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
            </td>
        </tr>
    </table>
        
        <table border="0" align="center" width="100%">
            
            <tr>
                <td colspan="2" align="center">
                    <br />
                    <p> First choose the Product Line, Family and Subfamily codes which will apply to the
                    spreadsheet data you will eventually import.
                    </p><br />
                </td>
            </tr>
            
<!-- Product Line -->
            
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Product Line:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="lineCode" size="1"
                        onchange="changedLineCode()"
                        onmouseover="showTip(event, 'moLineCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="0">Please select a Product Line</option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            for (var i = 0; i < line.length; i++){
                                document.write("<option value=\""+line[i][0]+"\">"+line[i][1]+"</option>");
                            }
                        //-->
                        </script>
                    </select>
                </td>
            </tr>
                       
<!-- Family Code -->
            
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Family:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="familyCode" size="1"
                        onclick="clickFamilyCode()"
                        onchange="changedFamilyCode()"
                        onmouseover="showTip(event, 'moFamilyCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="0">
                            Please select a Product Line first.
                        </option>
                    </select>
                </td>
            </tr>
            
<!-- Subfamily Code -->
            
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Subfamily:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="subfamilyCode" size="1"
                        onclick="clickSubfamilyCode()"
                        onmouseover="showTip(event, 'moSubfamilyCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="0">
                            Please select a Product Line first
                        </option>
                    </select>
                </td>
            </tr>  
            
 <!-- Category Code  -->           
            
            <tr>
                <td colspan="2" align="center">
                    <br />
                    <p> You can create a part number worksheet by selecting a category, 
                   subcategory, and template from the select boxes below.
                    </p><br />
                </td>
            </tr>
            <tr>
                <td align="right" width="30%">
                    <span class="requiredLabel">Category Code:&nbsp;</span>
                </td>
                <td align="left" width="70%">
                    <select name="categoryCode" size="1"
                        onchange="checkCategoryCode()"
                        onmouseover="showTip(event, 'moCategoryCode', 50, 100)"
                        onmouseout="hideTip()">
                        <option selected="selected" value="0">Please select a Category</option>
                        <script language="JavaScript" type="text/javascript">
                        <!--
                            for (var i = 0; i < category.length; i++){
                                document.write("<option value=\""+category[i]+"\">"+category[i]+"</option>");
                            }
                        //-->
                        </script>
                    </select>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <span class="requiredLabel">SubCategory Name:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <select name="subcategoryCode" size="1"
                    onmouseover="showTip(event, 'moSubcategoryCode', 50, 100)"
                    onmouseout="hideTip()" 
                    onchange="checkSubcategoryCode()" />
                    <script language="JavaScript" type="text/javascript">
                        <!--
                            for (var i = 0; i < subcategory.length; i++){
                                document.write("<option value=\""+subcategory[i] [1] + "\">" + subcategory[i] [2] + "</option>");
                            }
                        //-->
                        </script>
                      </select>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <span class="requiredLabel">Template:&nbsp;</span>
                </td>
                <td align="left">
                    <span class="dataField">
                    <select name="template" size="1"
                    onmouseover="showTip(event, 'moTemplate', 50, 100)"
                    onmouseout="hideTip()" 
                    onfocus="checkTemplate()" />
                    <script language="JavaScript" type="text/javascript">
                        <!--
                            for (var i = 0; i < oTemplate.length; i++){
                                document.write("<option value=\""+oTemplate[i] [0] + "\">" + oTemplate[i] [1] + "</option>");
                            }
                            document.close();
                        //-->
                        </script>
                    </select>
                    </span>
                </td>
            </tr>         

<!--  Pre-existing Only Parm data  -->

            <tr>
                <td align="right"><span class="label">
                    Pre-existing Only:&nbsp;
                </span></td>
                <td align="left"><span class="dataField">
                    <input type="radio" name="preExistingOnly"
<%
                        String gwork = (String) session.getAttribute("preExistingOnly");
                        if (gwork == null) {gwork = "";}
                        if (gwork.equals("Y")) {
                            out.println(" checked=\"checked\" ");
                        }
%>   
                        value="Y"
                        onmouseover="showTip(event,'moPreExistingOnly')" 
                        onmouseout="hideTip()"
                        onblur="checkPEOnly()"
                    />
                    Yes&nbsp;&nbsp;&nbsp;&nbsp;
                    <input type="radio" name="preExistingOnly"
<%
                        if(gwork.equals("N") || gwork.equals("")) {
                            out.println(" checked=\"checked\" ");
                        }
%>  
                        value="N"
                        onmouseover="showTip(event,'moPreExistingOnly')" 
                        onmouseout="hideTip()"
                        onblur="checkPEOnly()"
                    />
                    No
                </span></td>
            </tr>
              
            
<!--  Exclude Pre-existing Parm data  -->

            <tr>
                <td align="right"><span class="label">
                    Exclude Pre-existing:&nbsp;
                </span></td>
                <td align="left"><span class="dataField">
                    <input type="radio" name="excludePreExisting"
<%
                        gwork = (String) session.getAttribute("excludePreExisting");
                        if (gwork == null) {gwork = "";}
                        if (gwork.equals("Y")) {
                            out.println(" checked=\"checked\" ");
                        }
%>   
                        value="Y"
                        onmouseover="showTip(event,'moExcludePreExisting')" 
                        onmouseout="hideTip()"
                    />
                    Yes&nbsp;&nbsp;&nbsp;&nbsp;
                    <input type="radio" name="excludePreExisting"
<%
                        if(gwork.equals("N") || gwork.equals("")) {
                            out.println(" checked=\"checked\" ");
                        }
%>  
                        value="N"
                        onmouseover="showTip(event,'moExcludePreExisting')" 
                        onmouseout="hideTip()"
                    />
                    No
                </span></td>
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
            <input type="submit" value="Continue" name="B1" 
	onmouseover="showTip(event, 'moContinue')" 
        onmouseout="hideTip()"
	/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="reset" value="&nbsp;&nbsp;Clear&nbsp;&nbsp;" name="B2" 
	onmouseover="showTip(event, 'moReset')" 
        onmouseout="hideTip()"
	/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='gpsdf.jsp'; " 
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