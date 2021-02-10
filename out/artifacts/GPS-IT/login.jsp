<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page language="java" import="java.util.*" session="true"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
          "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
	<title>Galco Parametric Search - Log On</title>
	<link type="text/css" rel="stylesheet" href="gpsrules.css" />
<!--

	GPS Main Menu

	Modification History

	06/02/06	DES	Begin Development

-->

<!--	*************************************************************
	*		My JavaScript Functions and junk	      	*
	*************************************************************  -->

<script language="JavaScript" type="text/javascript">
<!--

function checkPassword() {
	// Check Password

	var myForm = document.form1;
	var work = myForm.password.value;
	if (work.length > 0) {
		if (work == "admin9090") {
                    myForm.start.disabled = false;
                    myForm.start.click();
                    return;
		} else {
                    alert ("Invalid password.");
                    myForm.exit.focus();
                }
	}
}

function setDefaults() {
	var myForm = document.form1;
	window.defaultStatus = "";
        myForm.password.focus();
}


//	*************************************************************
//	*			Form Validation Pre-Submit			*
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
	// Define some globals here
	var ucLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var lcLetters = "abcdefghijklmnopqrstuvwxyz";
	var numerics = "0123456789";
	var spaces = " ";
	
//-->	
</script>


<div style="position: absolute; left: 10px; top: 10px; width: 700px; ">


<form method="post" action="index.jsp" onsubmit="return My_Validator()" name="form1">
<p>
  <input type="hidden" name="validation" value="Error" />

</p>

<table border="0" width="100%">

<!-- Logo and Heading  -->

  <tr>
    <td align="center" width="20%">
      <img src="gl_25.gif" alt="Galco logo" /><br />

    </td>
    <td align="center" width="80%">
	<h2>
        Parametric Search<br />
			Logon Menu
	</h2>
    </td>
  </tr>
  <tr>
    <td>
      &nbsp;
    </td>
    <td>
      
    </td>
  </tr>




<!--     Continue      -->

      <tr>
	<td rowspan="12">
		&nbsp;
	</td>
        <td>
          <center>
             Enter Password&nbsp; <input type="password" name="password" size="20" onblur="checkPassword()" />
          </center>
        </td>
	</tr>
        
        
        <tr>
        <td>
          <center>
              <br />
            <input type="submit" value="Start" name="start" disabled="disabled" />
          </center>
        </td>
      </tr>

        <tr>
        <td>
          <center>
              <br />
            <input type="button" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Exit&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" name="exit" onclick="Javascript: window.close() " 
	/>
          </center>
        </td>
      </tr>
    </table>
<br /><br />
  <p>
    <img src="http://www.w3.org/Icons/valid-xhtml10"
        alt="Valid XHTML 1.0 Transitional" height="31" width="88" />
  </p>
  </form>
</div>
</body>
</html>
