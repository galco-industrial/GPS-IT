/*
 * gpsrcf5.java
 *
 * Created on April 17, 2007, 3:23 PM
 */

package gpsRules;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import sauter.util.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I create the rule set in WDS
 *
 * * 09/07/2007   DES 
 * Made changes to support selectBoxFilter, matchOrder, PreviewOrder, and Series Implici
 *
 * Modified 10/24/07 by DES to use the GPSrules Class to create a session scoped rules object
 * that stores the rules values during execution of gpsrcf1 thru gpsrcf5 instead of 
 * storing the data in individual session variables.
 *
 */
public class gpsrcf5 extends HttpServlet {
            
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.00";
    
    private static final String UC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LC = "abcdefghijklmnopqrstuvwxyz";
    private static final String SP = " ";
    private static final String NU = "0123456789";
    private static final String QU = "\"";
    private static final String AP = "'";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
                                       
        /* Boilerplate */
        int debugLevel = 0;
        HttpSession session = null;
        Statement statement;
        String sWork = "";
        String userID = "";
        String userRole = "";
        String uStamp = "";
        RequestDispatcher view = null;
        
        session = request.getSession();
        if (session.isNew()) {
            response.sendRedirect ("gpstimeout.htm");
            return;
        }
        sWork = (String) session.getAttribute("debugLevel");
        if (sWork != null) {
            debugLevel = Integer.parseInt(sWork);
        }
        sWork = (String) session.getAttribute("userID");
        if (sWork != null) {
            userID = sWork;
        }
        sWork = (String) session.getAttribute("userRole");
        if (sWork != null) {
            userRole = sWork;
        }
        uStamp = SERVLET_NAME + " Version " + VERSION + " User ID '" + userID + "' Role '" + userRole + "'";
        debug (debugLevel, 10, uStamp + " executing.");        
        /* End Boilerplate */
        
        String abortMessage = "";
        boolean completionCode = false;
        double dDefaultValue;
        double dMaxValue;
        double dMinValue;
        String eString = "";
        String keyString;
        String message = "";
        String queryString;
        ResultSet rs;
        GPSrules ruleSet;
        String tempWork;
        String valueString;
        String work;
        int workInt;

        // Check for invalid Call  i.e., validation key must be set to "OK" */

	work = request.getParameter("validation2");
        if (!work.equals("OK")) {
            //conn.close();
            response.sendRedirect ("gpsabend.jsp?rc=gps0001");
            return;
        }

        session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
        ruleSet = (GPSrules) session.getAttribute("sRuleSet");
                    
        // Get Rules values from Session based Rules Object
        // Make sure we have no null object references.
        // Order of the fields is determined by their appearance in the ADD GUI
        
        String ruleScope = ruleSet.getRuleScope();
            ruleScope = setDefault(ruleScope,"");
            if (ruleScope.equals("") ) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3001");
                return;
            }
        String familyCode = ruleSet.getFamilyCode();
            familyCode = setDefault(familyCode,"");
            if (familyCode.equals("") ) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3002");
                return;
            }    
        String subfamilyCode = ruleSet.getSubfamilyCode();
            subfamilyCode = setDefault(subfamilyCode,"");
            if (subfamilyCode.equals("") ) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3003");
                return;
            }
        String seqNum = Integer.toString(ruleSet.getSeqNum());
            seqNum = setDefault(seqNum,"");
            if (seqNum.equals("")  ) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3004");
                return;
            }        
        String dataType = ruleSet.getDataType();
            dataType = setDefault(dataType,"");
            if (dataType.equals("")  ) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3005");
                return;
            }
        String auditUserID = ruleSet.getAuditUserID();
            auditUserID = setDefault(auditUserID,"");
            if (auditUserID.equals("") ) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3006");
                return;
            }
        String auditCode = "A";  // A is for ADD Operation
        String auditDate = DateTime.getDateMMDDYY(); //sdf.format(cal.getTime());
        String auditTimeRaw = Integer.toString(DateTime.getSecondsSinceMidnight());           
        
        // Page 2 variables
        
        String parmName = ruleSet.getParmName();
            parmName = setDefault(parmName,"");
            if (parmName.equals("")
                || parmName.length() > 24
                    || !EditText.checkCharSet(LC + UC + SP + NU +"@#$%^&*()-+':,.?/",parmName) ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3007");
                        return;
            }  
        String description = ruleSet.getDescription();
            description = setDefault(description,"");
            if (description.indexOf(AP) > -1  ) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3008");
                return;
            }
        String deToolTip = ruleSet.getDeToolTip();
            deToolTip = setDefault(deToolTip,"");
            if (deToolTip.indexOf(AP) > -1  ) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3008");
                return;
            } 
        String searchToolTip = ruleSet.getSearchToolTip();
            searchToolTip = setDefault(searchToolTip,"");
            if (searchToolTip.indexOf(AP) > -1  ) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3008");
                return;
            } 
        String parmStatus = ruleSet.getParmStatus();
            parmStatus = setDefault(parmStatus,"");
            if (parmStatus.equals("") 
                || !EditText.checkCharSet("AI",parmStatus) ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3009");
                    return;
            }
        String searchRequired = ruleSet.getSearchRequired() ? "1" : "0";
        String deRequired = ruleSet.getDeRequired() ? "1" : "0";
        String seriesImplicit = ruleSet.getSeriesImplicit() ? "1" : "0";
        String selectBoxFilter = ruleSet.getSelectBoxFilter() ? "1" : "0";

        workInt = ruleSet.getDeOrder();
        String deOrder = Integer.toString(workInt);
            if (workInt < 1 || workInt > 99) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3012");
                return;
            }
        
        workInt = ruleSet.getDisplayOrder();
        String displayOrder = Integer.toString(workInt);
            if (workInt < 0 || workInt > 99) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3013");
                return;
            }
        
        workInt = ruleSet.getMatchOrder();
        String matchOrder = Integer.toString(workInt);
            if (workInt < 0 || workInt > 99) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3013a");
                return;
            }
        
        workInt = ruleSet.getPreviewOrder();
        String previewOrder = Integer.toString(workInt);
            if (workInt < 0 || workInt > 99) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3013b");
                return;
            }
        
        workInt = ruleSet.getSearchOrder();
        String searchOrder = Integer.toString(workInt);
            if (workInt < 0 || workInt > 99) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3014");
                return;
            }
        
        String displayJust = ruleSet.getDisplayJust();
            displayJust = setDefault(displayJust,"");
            if (displayJust.equals("") 
                || !EditText.checkCharSet("LRC",displayJust) ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3015");
                    return;
            }
        String peerGroup = "0"; // Reserved for future use
        String peerSubgroup = "0"; // Reserved for Future use
        
        // Page 3 variables
        
        /* Set any null variables to a zero length String by default 
           Then go through each data type on Page 3 and selectively validate
           contents of related variables; all N/A values should be empty strings */

        String allowDuplicates = ruleSet.getAllowDuplicates() ? "1" : "0";
        String allowFractions = ruleSet.getAllowFractions() ? "1" : "0";
        String allowSign = ruleSet.getAllowSign() ? "1" : "0";
        String allowTilde = ruleSet.getAllowTilde() ? "1" : "0";
        String allowZero = ruleSet.getAllowZero() ? "1" : "0";
        String charSetGroups = ruleSet.getCharSetGroups();
        String dateFormat = ruleSet.getDateFormat();
        String defaultValue = ruleSet.getDefaultValueRaw();
        String deMultipliers = ruleSet.getDeMultipliers();
        String deleteLS = ruleSet.getDeleteLS() ? "1" : "0";
        String deleteNPC = ruleSet.getDeleteNPC() ? "1" : "0";
        String deleteSp = ruleSet.getDeleteSP() ? "1" : "0";
        String deleteTS = ruleSet.getDeleteTS() ? "1" : "0";
        String deObject = ruleSet.getDeObject();
        String deSelectBoxName = ruleSet.getDeSelectBoxName();
        String deTextBoxSize = Integer.toString(ruleSet.getDeTextBoxSize());
        String displayMultipliers = ruleSet.getDisplayMultipliers();
        String forceLC = ruleSet.getForceLC() ? "1" : "0";
        String forceUC = ruleSet.getForceUC() ? "1" : "0";
        String imageType = ruleSet.getImageType();
        String maxDecimalDigits = Integer.toString(ruleSet.getMaxDecimalDigits());
        String maxLength = Integer.toString(ruleSet.getMaxLength());
        //String maxTime = (String)session.getAttribute("maxTime");
        String maxValue = ruleSet.getMaxValueRaw();
        //String minDate = (String)session.getAttribute("minDate");
        String minDecimalDigits = Integer.toString(ruleSet.getMinDecimalDigits());
        String minLength = Integer.toString(ruleSet.getMinLength());
        //String minTime = (String)session.getAttribute("minTime");
        String minValue = ruleSet.getMinValueRaw();
        String otherCharSet = ruleSet.getOtherCharSet();
        String parmDelimiter = ruleSet.getParmDelimiter();
        String qObject = ruleSet.getQobject();
        String qSelectBoxName = ruleSet.getQselectBoxName();
        String qTextBoxSize = Integer.toString(ruleSet.getQtextBoxSize());
        String reduceSp = ruleSet.getReduceSP() ? "1" : "0";
        String regExpr = ruleSet.getRegExpr() ? "1" : "0" ;
        String searchLogicalDefault = ruleSet.getSearchLogicalDefault();
        String searchMax = Integer.toString(ruleSet.getSearchMax());
        String searchMin = Integer.toString(ruleSet.getSearchMin());
        String searchWeight = Integer.toString(ruleSet.getSearchWeight());
        //String sigDigits = ""; //(String)session.getAttribute("sigDigits");
        //String timeFormat = (String)session.getAttribute("timeFormat");
        //String unitsBase = (String)session.getAttribute("unitsBase");
        String unitsShift = Integer.toString(ruleSet.getDecShift());
        String units = ruleSet.getDisplayUnits().trim();

       
        // Page 3N Numeric field validation
        
        if (dataType.equals("N")) {
            deObject = deObject.toUpperCase();
            if (deObject.length() != 1
                    || !EditText.checkCharSet("TS",deObject) ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3016");
                        return;
            }
            if (deObject.equals("T") ) {
                if (deTextBoxSize.equals("")
                    || !Convert.checkIntegerRange(deTextBoxSize, 1, 99)  ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3017");
                        return;
                }
            }
            if (deObject.equals("S") ) {                
                if (deSelectBoxName.equals("")  ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3018");
                    return;
                }
            }
            qObject = qObject.toUpperCase();
            if ( qObject.length() != 1
                    || !EditText.checkCharSet("TS",qObject) ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3019");
                        return;
            }
            if (qObject.equals("T") ) {
                if (qTextBoxSize.equals("")
                    || !Convert.checkIntegerRange(qTextBoxSize, 1, 99)  ) {
                            response.sendRedirect ("gpsabend.jsp?rc=gps3020");
                            return;
                }
            }    
            if (qObject.equals("S") ) {                
                if (qSelectBoxName.equals("") ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3021");
                    return;
                }
            }
            if (units.equals("") ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3022");
                    return;
            }
            //unitsBase = unitsBase.toUpperCase();    
            //if (unitsBase.equals("")
            //    || !EditText.checkCharSet("DBOH",unitsBase)) {
            //        response.sendRedirect ("gpsabend.jsp?rc=gps3023");
            //        return;
            //}
            if (parmDelimiter.equals("") ) {
                allowDuplicates = "0";    
            } else {
                if (!EditText.checkCharSet(";/", parmDelimiter) ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3024");
                    return;
                }
            }
            if (allowFractions.equals("0") ) {
                minDecimalDigits = "0";
            } else {
                if (minDecimalDigits.length() != 1
                    || !EditText.checkCharSet("0123456789",minDecimalDigits) ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3030");
                        return;
                }
            }
            tempWork = "U";
            if (Integer.parseInt(unitsShift) == 0) {
                tempWork = "afpnumcdUKMGTPE";
            }
            if (allowFractions.equals("0") ) {
                tempWork = "UKMGTPE";
            }
            deMultipliers = EditText.removeDuplicates(deMultipliers);
            if ( deMultipliers.length() < 1 
                || !EditText.checkCharSet(tempWork,deMultipliers) ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3033");
                    return;
            }
            displayMultipliers = EditText.removeDuplicates(displayMultipliers);
            if ( displayMultipliers.length() < 1 
                || !EditText.checkCharSet(tempWork,displayMultipliers) ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3034");
                    return;
            }
            if (minValue.length() < 1
                || !Convert.isDouble(minValue) ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3035");
                    return;
            }
            dMinValue = Double.parseDouble(minValue);
            if (maxValue.length() < 1
                || !Convert.isDouble(maxValue) ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3036");
                    return;
            }
            dMaxValue = Double.parseDouble(maxValue);                        
            if (dMaxValue < dMinValue ) {
                response.sendRedirect ("gpsabend.jsp?rc=gps3037");
                return;
            } 
            if (defaultValue.length() != 0 ) {
                
                // Fix me to handle multiple values and ranges!!!!!!!!!!!!!!!!!!
                
                // These tests are performed iff there is a default value specified
            
                // Intercept multiple values in default for now
                
                if (parmDelimiter.length() > 0){
                    if (defaultValue.indexOf(parmDelimiter) != -1) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3074");
                        return; 
                    }
                }
                                
                // Intercept Allow Tilde for now
                
                if (allowTilde.equals("0")) {
                    if (defaultValue.indexOf("~") != -1) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3075");
                        return; 
                    }
                }
                
                if (!Convert.isDouble(defaultValue)) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3038");
                    return;
                }
                if (allowFractions.equals("0") ) {
                    if (!Convert.isLong(defaultValue) ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3039");
                        return;
                    }
                }
                dDefaultValue = Double.parseDouble(defaultValue);
                if (dDefaultValue < 0 
                    && allowSign.equals("0")) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3040");
                        return;
                }
                if (dDefaultValue == 0 
                    && allowZero.equals("0")) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3041");
                        return;
                }
                if (dDefaultValue < dMinValue ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3042");
                    return;
                }
                if (dDefaultValue > dMaxValue ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3043");
                    return;
                }
            }   
            
            // end of checking default value iff present
            
            if (searchMin.equals("") 
                || !Convert.isInteger(searchMin) 
                    || !Convert.checkIntegerRange(searchMin,0,999) ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3044");
                        return;
            }
            if (searchMax.equals("") 
                || !Convert.isInteger(searchMax) 
                    || !Convert.checkIntegerRange(searchMax,0,999) ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3045");
                        return;
            }
            if (searchWeight.equals("") 
                || !Convert.isInteger(searchWeight) 
                    || !Convert.checkIntegerRange(searchWeight,0,100) ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3046");
                        return;
            }
        }         //     ********** End of Numeric Checks ************
            
        // Page 3S String field validation
        
        if (dataType.equals("S") ) {    
            deObject = deObject.toUpperCase();
            if (deObject.length() != 1
                || !EditText.checkCharSet("TS",deObject) ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3047");
                    return;
            }
            if (deObject.equals("T") ) {
                if (deTextBoxSize.equals("")
                    || !Convert.checkIntegerRange(deTextBoxSize, 1, 99)  ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3048");
                        return;
                }
            }
            if (deObject.equals("S") ) {                
                if (deSelectBoxName.equals("") ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3049");
                    return;
                }
            }
            qObject = qObject.toUpperCase();
            if ( qObject.length() != 1
                || !EditText.checkCharSet("TS",qObject) ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3050");
                    return;                    
            }
            if (qObject.equals("T") ) {
                if (qTextBoxSize.equals("")
                    || !Convert.checkIntegerRange(qTextBoxSize, 1, 99) ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3051");
                        return;
                }
            }
            if (qObject.equals("S") ) {                
                if (qSelectBoxName.equals("") ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3052");
                    return;
                }
            }    
            if (minLength.equals("") 
                || !Convert.isInteger(minLength) 
                    || !Convert.checkIntegerRange(minLength,0,99) ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3053");
                        return;
            }
            if (maxLength.equals("") 
                || !Convert.isInteger(maxLength) 
                    || !Convert.checkIntegerRange(maxLength,1,99) ) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3054");
                        return;
            }
            if (deleteSp.equals("1") ) {
                deleteTS = "1";
                deleteLS = "1";
                reduceSp = "1";
            }
            if (!parmDelimiter.equals("") ) {
                 if (!EditText.checkCharSet(",;/", parmDelimiter) ) {
                     response.sendRedirect ("gpsabend.jsp?rc=gps3061");
                     return;
                 }
            }
            charSetGroups = charSetGroups.toUpperCase();
            charSetGroups = EditText.removeDuplicates(charSetGroups);
            if (forceLC.equals("1") 
                && charSetGroups.indexOf("U") > -1 ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3064");
                    return;
            }
            if (forceUC.equals("1") 
                && charSetGroups.indexOf("L") > -1 ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3065");
                    return;
            }
            if (deleteSp.equals("1") 
                && charSetGroups.indexOf("S") > -1 ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3066");
                    return;
            }
            if (regExpr.equals("1") ) {
                if (otherCharSet.length() == 0) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3067");
                    return;
                }
                charSetGroups = "";
            } else {
                if ((otherCharSet.length() == 0 
                    && charSetGroups.length() == 0 )
                        || !EditText.checkCharSet("ULSNAQ",charSetGroups) ) {
                            response.sendRedirect ("gpsabend.jsp?rc=gps3068");
                            return;
                }
            }
            if (defaultValue.length() != 0 ) {
                
                // Fix me to handle multiple values and ranges!!!!!!!!!!!!!!
                // strip non printing characters
                
                if (deleteNPC.equals("1")) {
                    String result = "";
                    for (int q = 0; q < defaultValue.length(); q++) {
                        char c = defaultValue.charAt(q);
                        int v = (int) c;
                        if (v > 31 && v < 127) {
                            result += c;
                        }
                    }
                    defaultValue = result;
                }
                
                // remove all spaces

                if (deleteSp.equals("1") ) {
                    String result = "";
                    for (int q = 0; q < defaultValue.length(); q++) {
                        char c = defaultValue.charAt(q);
                        int v = (int) c;
                        if (v != 32) {
                            result += c;
                        }
                    }
                    defaultValue = result;                    
                }
                
                // remove leading spaces
                
                if (deleteLS.equals("1")) {
                    while (defaultValue.length() > 0 && ((int) defaultValue.charAt(0) == 32)) {
                        defaultValue = defaultValue.substring(1);
                    }
                }    
                
                // remove trailing spaces
                
                if (deleteTS.equals("1")) {
                    while (defaultValue.length() > 0 && ((int) defaultValue.charAt(defaultValue.length() - 1) == 32)) {
                        defaultValue = defaultValue.substring(0, defaultValue.length() - 1);
                    }
                }
                
                // reduce spaces

                if (reduceSp.equals("1") ) {
                    int v = defaultValue.indexOf("  ");
                    while (v != -1) {
                        defaultValue = defaultValue.substring(0,v) + defaultValue.substring(v+1);
                        v = defaultValue.indexOf("  ");
                    }
                }
                
                // Check min length
                
                if (defaultValue.length() < Integer.parseInt(minLength) ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3072");
                    return;                    
                }
                
                // Check max length
                
                workInt = Integer.parseInt(maxLength);
                if (workInt > 0 && defaultValue.length() > workInt ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3073");
                    return;                    
                }
                
                // force case
                
                if (forceUC.equals("1") ) {
                    defaultValue = defaultValue.toUpperCase();
                }
                
                if (forceLC.equals("1") ) {
                    defaultValue = defaultValue.toLowerCase();
                }
                
                // Intercept multiple values in default for now
                
                if (parmDelimiter.length() > 0){
                    if (defaultValue.indexOf(parmDelimiter) != -1) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3074");
                        return; 
                    }
                }
                                
                // Intercept Allow Tilde for now
                
                if (allowTilde.equals("1")) {
                    if (defaultValue.indexOf("~") != -1) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3075");
                        return; 
                    }
                }
                
                // check reg expr
                
                if (regExpr.equals("1") ) {
                    Pattern patt = Pattern.compile(otherCharSet);
                    Matcher mat = patt.matcher(defaultValue);
                    if (!mat.matches()) {
                        response.sendRedirect ("gpsabend.jsp?rc=gps3080");
                        return;
                    }
                } else {
                
                    // check char set
                    
                    String charSetString = "";
                
                    if (charSetGroups.indexOf("U") != -1) {
                        charSetString += UC;
                    }
                    if (charSetGroups.indexOf("L") != -1) {
                        charSetString += LC;
                    }
                    if (charSetGroups.indexOf("S") != -1) {
                        charSetString += SP;
                    }
                    if (charSetGroups.indexOf("N") != -1) {
                        charSetString += NU;
                    }
                    if (charSetGroups.indexOf("A") != -1) {
                        charSetString += AP;
                    }
                    if (charSetGroups.indexOf("Q") != -1) {
                        charSetString += QU;
                    }
                    for (int i = 0; i < defaultValue.length(); i++) {
                        if (charSetString.indexOf(defaultValue.charAt(i)) == -1) {
                            response.sendRedirect ("gpsabend.jsp?rc=gps3081");
                            return;
                        }
                    }
                }
            }
        }   // ******* End of String Checks ************            
            
            // Page 3D Date Time field validation
        
        if (dataType.equals("D")) {    
             response.sendRedirect ("gpsabend.jsp?rc=gps3099");
             return;
             
             // Date Time is not supported yet
            /*
            String dateFormat = (String)session.getAttribute("dateFormat");
            String defaultValue = (String)session.getAttribute("defaultValue");
            String maxDate = (String)session.getAttribute("maxDate");
            String maxTime = (String)session.getAttribute("maxTime");
            String minDate = (String)session.getAttribute("minDate");   
            String minTime = (String)session.getAttribute("minTime");   
                if (searchMin.equals("") 
                    || !isInteger(searchMin) 
                        || !checkIntegerRange(searchMin,0,100) ) {
                            response.sendRedirect ("gpsabend.jsp?rc=gps3117");
                            return;
                }
                if (searchMax.equals("") 
                    || !isInteger(searchMax) 
                        || !checkIntegerRange(searchMax,0,100) ) {
                            response.sendRedirect ("gpsabend.jsp?rc=gps3118");
                            return;
                }
                if (searchWeight.equals("") 
                    || !isInteger(searchWeight) 
                        || !checkIntegerRange(searchWeight,0,100) ) {
                            response.sendRedirect ("gpsabend.jsp?rc=gps3119");
                            return;
                }
                String timeFormat = (String)session.getAttribute("timeFormat");               
               
            */            
        }   // End of time checks        

        // Page 3L LogicalDate Time field validation
            
            
        if (dataType.equals("L")) {    
            if (defaultValue.length() != 1 
                || !EditText.checkCharSet("YNU",defaultValue) ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3120");
                    return;
            }
            if (searchLogicalDefault.length() != 1 
                || !EditText.checkCharSet("YNU",searchLogicalDefault) ) {
                    response.sendRedirect ("gpsabend.jsp?rc=gps3121");
                    return;
            }

        }    // end of Logical field checks
        
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {        
            sWork = uStamp + " failed to connect to WDS database; aborting.";
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }

        // Let's create the Insert String and try to add the bugger        

        try {
            queryString = "SELECT family_code, subfamily_code, seq_num ";
            queryString += "FROM pub.ps_rules ";
            queryString += "WHERE family_code='" + familyCode + "' AND subfamily_code='" + subfamilyCode + "' AND seq_num="+seqNum;
            debug (debugLevel, 4, uStamp + " Attempting to verify rule currently does not exist.");
            rs = conn.runQuery(queryString);
            if(!(rs.next())) {
                try { // Build Insert SQL Command String!
                    queryString = "INSERT INTO pub.ps_rules ";
                    keyString = " (";
                    valueString = " VALUES (";
                    keyString += "allow_duplicates, ";
                    valueString += "'" + allowDuplicates + "', ";
                    keyString += "allow_fractions, ";
                    valueString += "'" + allowFractions + "', ";
                    keyString += "allow_sign, ";
                    valueString += "'" + allowSign + "', ";
                    keyString += "allow_tilde, ";
                    valueString += "'" + allowTilde + "', ";
                    keyString += "allow_zero, ";
                    valueString += "'" + allowZero + "', ";
                    keyString += "audit_code, ";
                    valueString += "'" + auditCode + "', ";
                    keyString += "audit_date, "; 
                    valueString += "'" + auditDate + "', ";
                    keyString += "audit_time_raw, ";
                    valueString += auditTimeRaw + ", ";  
                    keyString += "audit_userid, ";
                    valueString += "'" + auditUserID + "', ";
                    keyString += "char_set_groups, ";
                    valueString += "'" + charSetGroups +"', ";
                    keyString += "data_type, ";
                    valueString += "'" + dataType + "', ";
                    keyString += "de_multipliers, ";
                    valueString += "'" + deMultipliers + "', ";
                    keyString += "de_object, ";
                    valueString += "'" + deObject + "', ";
                    keyString += "de_order, ";
                    valueString += deOrder + ", ";
                    keyString += "de_required, ";
                    valueString += "'" + deRequired + "', ";
                    keyString += "de_select_box_name, ";
                    valueString += "'" + deSelectBoxName + "', ";
                    keyString += "de_text_box_size, ";
                    valueString += deTextBoxSize + ", ";
                    keyString += "default_value, ";
                    valueString += "'" + defaultValue + "', ";
                    keyString += "delete_ls, ";
                    valueString += "'" + deleteLS + "', ";
                    keyString += "delete_npc, ";
                    valueString += "'" + deleteNPC + "', ";
                    keyString += "delete_sp, ";
                    valueString += "'" + deleteSp + "', ";
                    keyString += "delete_ts, ";
                    valueString += "'" + deleteTS + "', ";
                    keyString += "description, ";
                    valueString += "'" + description + "', ";
                    keyString += "de_tool_tip, ";
                    valueString += "'" + deToolTip + "', ";
                    keyString += "display_multipliers, ";
                    valueString += "'" + displayMultipliers + "', ";
                    keyString += "display_just, ";
                    valueString += "'" + displayJust + "', ";
                    keyString += "display_order, ";
                    valueString += displayOrder + ", ";
                    keyString += "display_units, ";
                    valueString += "'" + units + "', ";
                    keyString += "family_code, ";
                    valueString += "'" + familyCode + "', ";
                    keyString += "force_lc, ";
                    valueString += "'" + forceLC + "', ";
                    keyString += "force_uc, ";
                    valueString += "'" + forceUC + "', ";
                    keyString += "image_type, ";
                    valueString += "'" + imageType + "', ";
                    keyString += "match_order, ";
                    valueString += matchOrder + ", ";
                    keyString += "max_decimal_digits, ";
                    valueString += maxDecimalDigits + ", ";
                    keyString += "max_length, ";
                    valueString += maxLength + ", ";
                    keyString += "max_value, ";
                    valueString += "'" + maxValue + "', ";
                    keyString += "min_decimal_digits, ";
                    valueString += minDecimalDigits + ", "; 
                    keyString += "min_length, ";
                    valueString += minLength + ", ";
                    keyString += "min_value, ";
                    valueString += "'" + minValue + "', ";
                    keyString += "other_char_set, ";
                    valueString += "'" + otherCharSet + "', ";
                    keyString += "parm_delimiter, ";
                    valueString += "'" + parmDelimiter + "', ";
                    keyString += "parm_name, ";
                    valueString += "'" + parmName + "', ";
                    keyString += "parm_status, ";
                    valueString += "'" + parmStatus + "', ";
                    keyString += "peer_group, ";
                    valueString += peerGroup + ", ";
                    keyString += "peer_subgroup, ";
                    valueString += peerSubgroup + ", ";
                    keyString += "preview_order, ";
                    valueString += previewOrder + ", ";
                    keyString += "q_object, ";
                    valueString += "'" + qObject + "', ";
                    keyString += "q_select_box_name, ";
                    valueString += "'" + qSelectBoxName + "', ";
                    keyString += "q_text_box_size, ";
                    valueString += qTextBoxSize + ", ";
                    keyString += "reduce_sp, ";
                    valueString += "'" + reduceSp + "', ";
                    keyString += "reg_expr, ";
                    valueString += "'" + regExpr + "', ";
                    keyString += "rule_scope, ";
                    valueString += "'" + ruleScope + "', ";
                    keyString += "search_logical_default, ";
                    valueString += "'" + searchLogicalDefault + "', ";
                    keyString += "search_max, ";
                    valueString += searchMax + ", ";   
                    keyString += "search_min, ";
                    valueString += searchMin + ", ";
                    keyString += "search_order, ";
                    valueString += searchOrder + ", ";
                    keyString += "search_required, ";
                    valueString += "'" + searchRequired + "', ";
                    keyString += "search_tool_tip, ";
                    valueString += "'" + searchToolTip + "', ";
                    keyString += "search_weight, ";
                    valueString += searchWeight + ", ";
                    keyString += "seq_num, ";
                    valueString += seqNum + ", ";
                    keyString += "series_implicit, ";
                    valueString += "'" + seriesImplicit + "', ";
                    keyString += "select_box_filter, ";
                    valueString += "'" + selectBoxFilter + "', ";
                    keyString += "subfamily_code, ";
                    valueString += "'" + subfamilyCode + "', ";

                    // end of key/value pairs / remove last comma/space from strings
                    
                    keyString = keyString.substring(0,keyString.length()-2);
                    valueString = valueString.substring(0,valueString.length()-2);
                    keyString += ") ";
                    valueString += ") ";
                    queryString += keyString;
                    queryString += valueString;
                    debug (debugLevel, 4, uStamp + " Query string is " + queryString);
                    // Now let's try to add it
                    
                    debug (debugLevel, 4, uStamp + " Attempting to insert new Rule Set.");
                    completionCode = conn.runUpdate(queryString);
                    if (completionCode) {
                        message = " Rule Set created successfully.<br />" + message;
                    } else { 
                        message = " Error! Attempt to Create rule set failed.<br />" + conn.getError();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    message = " Error:" + e;
                }
            } else {
                message = " Error! Rule set already Exists.";
            }
            statement = rs.getStatement();
            rs.close();
            statement.close();
        } catch (Exception e){
            e.printStackTrace();
            message = " Error:<br />" + e;
        } finally { 
            conn.close();
        }
        request.setAttribute("statusMessage",message);
        view = request.getRequestDispatcher("gpsrcf5.jsp");
        view.forward(request,response);
    }
          
    private void debug (int limit, int level, String x) {
        if (limit >= level) {
            System.out.println(x);
        }
    }
    
    private String setDefault(String item, String itemDefault) {
        if (item == null) {
            item = "";
        }
        if (item.equals("") ) {
            item = itemDefault;
        }
        return item;
    }
            
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
