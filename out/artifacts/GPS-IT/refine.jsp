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
	<title>Galco Parametric Search - Web Search Test</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
        <script type="text/javascript" src="common.js"></script>
        <script type="text/javascript" src="gpscommon.js"></script>
        
        <!-- Version 1.0.00
        

        
        -->

<script language="JavaScript" type="text/javascript">
<!--

function doBCT(index, page) {
    var myForm = document.form1;
    var url = "";
    if (index == 0) {
        window.location = bct[0][0];
        return false;
    }
    for (var i = 1; i <= index; i++) {
        url += bct[i][0];
    }
    url += "&itemsperpage=" + myForm.itemsperpage.value;
    if (page) {
        url +="&pagenum=" + page;
        url +="&searchwithinresults=" + myForm.searchwithinresults.value;
        if (myForm.searchwithin) {
            url += "&searchwithin=" + myForm.searchwithin.value;
        }
    } else {
        url +="&pagenum=1";
    }
    if (document.form1.instockonly.checked == true) {
        url +="&instockonly=y";
    }
    window.location = url;
    return false;
}

function getMessage(divName) {
    if (divName == "moExit") {return "Click to return to the main menu";}
    return "";
}

function searchWithinBlur() {
    var myForm = document.form1;
    var searchWithin = deleteLeadingSpaces(deleteTrailingSpaces(reduceSpaces(myForm.searchwithin.value)));
    myForm.searchwithin.value = searchWithin;
    if (searchWithin == "") {
        myForm.searchwithin.value = "Search Within Results";
        myForm.refine.disabled = true;
        return;
    }
    if (searchWithin == "Search Within Results") {
        myForm.refine.disabled = true;
        return;
    }
    myForm.refine.disabled = false;
    myForm.refine.focus();
}

function searchWithinFocus() {
    var myForm = document.form1;
    var searchWithin = myForm.searchwithin.value;
    if (searchWithin == "Search Within Results") {
        myForm.searchwithin.value = "";
    }
}

function searchWithinKeyPress() {
    var myForm = document.form1;
    var searchWithin = myForm.searchwithin.value;
    if (searchWithin == "") {
        myForm.refine.disabled = true;
        return;
    }
    if (searchWithin == "Search Within Results") {
        myForm.refine.disabled = true;
        return;
    }
    myForm.refine.disabled = false;
}

function myValidator() {
    myForm = document.form1;
    myForm.selseqnum.value = "";
}

//-->
</script>
        
</head>
<body>
    
<script language="JavaScript" type="text/javascript">
    <!--

        var cols = 0;
        var bct = new Array();
        var subfamilyCodes = new Array();
        var mfgrCodes = new Array();
        var seriesCodes = new Array();
        var parms = new Array();
        var iP = new Array();
        var filters = 16; // supports 0 thru 15
        for (var i = 0; i < filters; i++) {
            parms[i] = new Array();
            iP[i] = 0;
        }
        var filterIndex = 0;
        var pageIndex = new Array();
        // Items per page
        var iPP = new Array("5","10","15","20","25","50");
        var iB = 0;
        var iS = 0;
        var iM = 0;
        var iR = 0;
        var iPI = 0;
        <c:forEach var="item" items="${bctarray}">
            bct[iB++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${subfamilycodes}">
            subfamilyCodes[iS++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${mfgrnames}">
            mfgrCodes[iM++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${seriescodes}">
            seriesCodes[iR++] = new Array(${item});
        </c:forEach>
        <c:forEach var="item" items="${parmset0}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset1}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset2}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset3}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset4}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset5}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset6}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset7}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset8}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset9}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset10}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset11}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset12}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset13}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset14}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        <c:forEach var="item" items="${parmset15}">
            parms[filterIndex][iP[filterIndex]++] = new Array(${item});
        </c:forEach>
        filterIndex++;
        // page Index
        <c:forEach var="item" items="${pageindex}">
            pageIndex[iPI++] = "${item}";
        </c:forEach>
        
    //-->    
</script>

    <center>
        
        <h1>Galco Web Search</h1>
        <br />        
    </center>

    <p>
<script language="JavaScript" type="text/javascript">
<!--
    var iB2 = --iB;
    for (var i = 0; i < iB2; i++) {
        document.write("<a href=\"#\" onclick=\"return doBCT(" + i + ")\">" + bct[i][1] + "</a> > ");
    }
    document.write(bct[iB2][1]);
//-->
</script>
    </p>
    <hr />

    <form name="form1" action="searchDispatcher.do" method="post" onsubmit="return myValidator()">
        <input type="hidden" name="familycode" value="${familycode}" />
        <input type="hidden" name="familyname" value="${familyname}" />
        <input type="hidden" name="mfgrcode" value="${mfgrcode}" />
        <input type="hidden" name="mfgrname" value="${mfgrname}" />
        <input type="hidden" name="optionsperpageset" value="${optionsperpage}" />
        <input type="hidden" name="pagenum" value="${pagenum}" />
        <input type="hidden" name="pageend" value="${pageend}" />
        <input type="hidden" name="productlinecode" value="${productlinecode}" />
        <input type="hidden" name="productlinename" value="${productlinename}" />
        <input type="hidden" name="searchwithinresults" value="${searchwithin}" />
        <input type="hidden" name="subfamilycode" value="${subfamilycode}" />
        <input type="hidden" name="subfamilyname" value="${subfamilyname}" />
        <input type="hidden" name="selseqnum" value="" />
        <input type="hidden" name="seriescode" value="${seriescode}" />
        <input type="hidden" name="seriesname" value="${seriesname}" />
        <input type="hidden" name="sn0" value="${sn0}" />
        <input type="hidden" name="v0" value="${v0}" />
        <input type="hidden" name="sn1" value="${sn1}" />
        <input type="hidden" name="v1" value="${v1}" />
        <input type="hidden" name="sn2" value="${sn2}" />
        <input type="hidden" name="v2" value="${v2}" />
        <input type="hidden" name="sn3" value="${sn3}" />
        <input type="hidden" name="v3" value="${v3}" />
        <input type="hidden" name="sn4" value="${sn4}" />
        <input type="hidden" name="v4" value="${v4}" />
        <input type="hidden" name="sn5" value="${sn5}" />
        <input type="hidden" name="v5" value="${v5}" />
        <input type="hidden" name="sn6" value="${sn6}" />
        <input type="hidden" name="v6" value="${v6}" />
        <input type="hidden" name="sn7" value="${sn7}" />
        <input type="hidden" name="v7" value="${v7}" />
        <input type="hidden" name="sn8" value="${sn8}" />
        <input type="hidden" name="v8" value="${v8}" />
        <input type="hidden" name="sn9" value="${sn9}" />
        <input type="hidden" name="v9" value="${v9}" />
        <input type="hidden" name="sn10" value="${sn10}" />
        <input type="hidden" name="v10" value="${v10}" />
        <input type="hidden" name="sn11" value="${sn11}" />
        <input type="hidden" name="v11" value="${v11}" />
        <input type="hidden" name="sn12" value="${sn12}" />
        <input type="hidden" name="v12" value="${v12}" />
        <input type="hidden" name="sn13" value="${sn13}" />
        <input type="hidden" name="v13" value="${v13}" />
        <input type="hidden" name="sn14" value="${sn14}" />
        <input type="hidden" name="v14" value="${v14}" />
        <input type="hidden" name="sn15" value="${sn15}" />
        <input type="hidden" name="v15" value="${v15}" />

<table border="0" width="100%">
    <tr>
        <td align="left">
            <b>Narrow your Search</b> within ${familyname}...
        </td>
        <td align="center">
                
                <script language="JavaScript" type="text/javascript">
<!--
    document.write("<input type=\"checkbox\" name=\"instockonly\"");
    if ("${instockonly}" == "y") {
        document.write("checked=\"checked\"");        
    }
    document.write(" onclick=\"return doBCT(" + iB2 + ")\" ");
    document.write("value=\"y\" />");
//-->
                </script> 
                &nbsp;In Stock Only
        </td>
        <td align="right">
            &nbsp;
            
<script language="JavaScript" type="text/javascript">
<!--
    if (iS == 0 && ${itemsfound} > 1 && "${searchwithin}" == "") {
        document.write("<input type=\"text\" value=\"Search Within Results\" size=\"24\"");
        document.write(" name=\"searchwithin\" onfocus=\"searchWithinFocus()\"");
        document.write(" onblur=\"searchWithinBlur()\" onkeyup=\"searchWithinKeyPress()\" />");
        document.write(" <input type=\"submit\" value=\"Refine\" name=\"refine\" disabled=\"disabled\" />");
    }
//-->
</script>
            &nbsp;&nbsp;&nbsp;
        </td>
    </tr>
</table>       
<hr />    
    <table border="0" width="100%">
        <tr>

<script language="JavaScript" type="text/javascript">
<!--

// Subfamily Codes

    if (iS > 0) {
        document.write("<td align=\"left\">");
        document.write("<select name=\"subfamilycodes\" size=\"1\" onmousedown=\"document.form1.selseqnum.value=''\" onchange=\"submit()\" >");
        document.write("<option selected=\"selected\" value=\"\">Subfamily</option>");
        for (var i = 0; i < iS; i++){
            document.write("<option value=\"" + subfamilyCodes[i][1] + "\">" + subfamilyCodes[i][2] + "</option>");
        }
        document.write("</select></td>");
        cols++;
    }

// Manufacturer Codes 

    if (iM > 1) {
        document.write("<td align=\"left\">");
        document.write("<select name=\"mfgrcodes\" size=\"1\" onmousedown=\"document.form1.selseqnum.value=''\" onchange=\"submit()\" >");
        document.write("<option selected=\"selected\" value=\"\">Manufacturer</option>");
        for (var i = 0; i < iM; i++){
            document.write("<option value=\"" + mfgrCodes[i][0] + "\">" + mfgrCodes[i][1]+"</option>");
        }
        document.write("</select></td>");
        cols++
    }
    
// Manufacturer Series Codes 

    if (iR > 1) {
        document.write("<td align=\"left\">");
        document.write("<select name=\"seriescodes\" size=\"1\" onmousedown=\"document.form1.selseqnum.value=''\" onchange=\"submit()\" >");
        document.write("<option selected=\"selected\" value=\"\">Series</option>");
        for (var i = 0; i < iR; i++){
            document.write("<option value=\"" + seriesCodes[i][0] + "\">" + seriesCodes[i][0]+"</option>");
        }
        document.write("</select></td>");
        cols++;
    }

// Filter Parms 

for (var g = 0; g < filters; g++) {
    if (iP[g] > 2) {
        document.write("<td align=\"left\">");
        document.write("<select name=\"parmvalue" + g + "\" size=\"1\" onmousedown=\"document.form1.selseqnum.value='" + parms[g][0][0] + "'\" onchange=\"submit()\" >");
        document.write("<option selected=\"selected\" value=\"\">" + parms[g][0][1] + "</option>");
        for (var i = 1; i < iP[g]; i++){
            document.write("<option value=\"" + parms[g][i][0] + "\">" + parms[g][i][1]+"</option>");
        }
        document.write("</select>&nbsp;" + parms[g][0][2] + "</td>");
        cols++;
    }
        
    if (cols > 3) {
        document.write("</tr><tr>");
        cols = 0;
    }
}
    
//-->
</script>        
        
        </tr>
    </table>
   <br /><br />
   <hr />
    <table width="100%" border="0">
        <tr>
            <td width="15%" align="center">
                <i>${itemsfound} Matches</i>
            </td>
            <td width="25%" align="center">
                <i>Show&nbsp;</i>
                <select name="itemsperpage" size="1">
<script language="JavaScript" type="text/javascript">
<!--                    
        var i = 0;
        while (iPP[i]) {
            document.write("<option value=\"" + iPP[i] + "\"");
            if (iPP[i] == "${itemsperpageset}") {
                document.write(" selected=\"selected\" ");
            }
            document.write(" >" + iPP[i] + "</option>");
            i++;
        }
//-->
</script>
                </select>
                <i>&nbsp;Items / Page&nbsp;</i>
            </td>
            <td width="60%" align="right">
                
<script language="JavaScript" type="text/javascript">
<!--
                var curPage = document.form1.pagenum.value;
                if (curPage != 1 ) {
                    document.write("<a href=\"#\" onclick=\"return doBCT(iB2, '1')\">&lt;&lt;</a>&nbsp;");
                    document.write("<a href=\"#\" onclick=\"return doBCT(iB2, '");
                    document.write("" + (curPage - 1) + "')\">Back</a>&nbsp;");
                } else {
                    document.write("&lt;&lt;&nbsp;Back&nbsp;");
                }
                var low = parseInt(curPage) - 8;
                var high = parseInt(curPage) + 8;
                for (var i = 0; i < iPI; i++) {
                  if (pageIndex[i] > low && pageIndex[i] < high) {
                    if (pageIndex[i] != curPage) {
                        document.write("<a href=\"#\" onclick=\"return doBCT(iB2, '");
                        document.write(pageIndex[i] + "')\">" + pageIndex[i] + "</a>&nbsp;");
                    } else {
                        document.write("&nbsp;"+curPage+"&nbsp;");
                    }
                  }
                }
                var endPage = document.form1.pageend.value;
                var nextPage = parseInt(curPage) + 1;
                if (curPage != endPage ) {
                    document.write("<a href=\"#\" onclick=\"return doBCT(iB2, '");
                    document.write("" + nextPage + "')\">Next</a>&nbsp;");
                    document.write("<a href=\"#\" onclick=\"return doBCT(iB2, '");
                    document.write("" + endPage + "')\">&gt;&gt;</a>&nbsp;");
                } else {
                    document.write("Next&nbsp;&gt;&gt;");
                }
//-->
</script>
               
            </td>
        </tr>
    </table>
<hr />
    <h2> ${results}</h2>
    <br />
    <c:forEach var="item" items="${partnumberspage}">
            ${item}
    </c:forEach>
 
</form> 
    <h2>Page created in ${elapsed} seconds.</h2>
    <br />
    
<!--     Continue     -->
          <center>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;" name="B3" onclick="Javascript: window.location='index.jsp'; " 

	/>
          </center>
    
<!-- Close the DOM to keep some browsers happy. -->

<script language="JavaScript" type="text/javascript">
<!--
    document.close();
//-->
</script>
</body>
</html>
