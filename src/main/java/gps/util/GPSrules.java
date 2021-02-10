/*
 * GPSrules.java
 *
 * Created on November 9, 2006, 5:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

import OEdatabase.WDSconnect;
import java.util.*;
import java.sql.*;
import sauter.util.*;
import gps.util.*;

/**
 *
 * @author Sauter
 *
 * Modification history
 * Version 1.5.01
 *
 * 07/06/2007   DES Added support for Match Order, Preview Order, and Series Implicit
 * 07/29/2009   DES added forceInactive switch
 * 02/16/2010   DES added getArrayListElement3()
 * 07/11/2017   DES Added enhanced error message for invalid dataType codes
 *
 */
public class GPSrules {
    
    private boolean debugSw = false;
    private static final String version = "1.5.02";
    
    private static final String UC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LC = "abcdefghijklmnopqrstuvwxyz";
    private static final String SP = " ";
    private static final String NU = "0123456789";
    private static final String AP = "'";
    private static final String QU = "\"";
    private boolean allowDuplicates;
    private boolean allowFractions; //cr
    private boolean allowSign; // cr
    private boolean allowTilde; // cr
    private boolean allowZero; // cr
    private String auditCode;  
    private String auditDate;
    private int auditTimeRaw;
    private String auditUserID;
    private String charSet; // built by setProperties method
     // also used by the GUI buffer
    private String charSetGroups;
    private String cookedValue; // used during a search op *********************
    private String dataType; // m
    private String dateFormat;
    private int decShift;   // defined by setDecShift method
    private String defaultValueRaw; // raw value
    private String defaultValueCooked;  // used by the GUI buffer
    private float defaultValueFloat;
    private boolean deleteLS;
    private boolean deleteNPC;
    private boolean deleteSP;
    private boolean deleteTS;
    private String deMultipliers;
    private String deObject;
    private int deOrder; // m
    private boolean deRequired; // m
    private String description; // m
    private String deSelectBoxName;
    private int deTextBoxSize;
    private String deToolTip;
    private String displayJust;
    private String displayMultipliers;
    private int displayOrder;
    private String displayUnits;  // cr
    private String familyCode; // m
    private String familyName;
    private String flags;  // used by the GUI buffer
    private boolean forceInactive = false;
    private boolean forceLC;
    private boolean forceUC;
    private String imageType;
    private boolean isValid = false;
    
    private int matchOrder;
    //private String maxDate;
    private int maxDecimalDigits;
    //private int maxTime;
    private String maxValueRaw; // raw value
    private String maxValueCooked;  // used by the GUI buffer
    private float maxValueFloat;
    private int maxLength;
    //private String minDate;
    private int minDecimalDigits;
    private int minLength;
    //private minTime;
    private String minValueRaw; // raw value
    private String minValueCooked;  // used by the GUI buffer
    private float minValueFloat;
    private String otherCharSet;
    private String parmDelimiter;
    private String parmName; // m
    private String parmStatus;
    private int peerGroup;
    private int peerSubgroup;
    private int previewOrder;
    private String productLineCode; // *****************************************
    private String productLineName; // *****************************************
    private String qObject;
    private int qTextBoxSize;
    private String qSelectBoxName;
    private String rawValue; // used during a search op ************************
    private boolean reduceSP;
    private boolean regExpr;
    private String ruleScope; // m
    private String searchLogicalDefault;
    private int searchMax;
    private float searchMaxValue; // used during a search op *******************
    private int searchMin;
    private float searchMinValue; // used during a search op *******************
    private int searchOrder;
    private boolean searchRequired;
    private String searchString; // used during a search op ********************
    private String searchToolTip;
    private float searchValue; // used during a search op
    private int searchWeight;
    private int seqNum; // m
    private int selectBoxChild;
    private boolean selectBoxFilter;
    private boolean seriesImplicit;
    private int sigDigits;
    private String subfamilyCode; // m
    private String subfamilyName; // used by the GUI buffer
    private String timeFormat;
        
    private GPSselectBox deSelectBox = null; // ********************************
    private GPSselectBox qSelectBox = null; // *********************************
    private GPSunit unit = null;
    
    /**
     * Creates a new instance of GPSrules
     */
    public GPSrules() {
    }
    
    private void debug(String msg) {
        if (debugSw) {
            System.out.println(msg);
        }
    }
    public boolean deleteRules(WDSconnect conn, String aFamilyCode, String aSubfamilyCode, 
            String aSeqNum, String aAuditCode, String aAuditUserID) {
        // Build Delete SQL Command String!
        
        boolean completionCode;
        auditCode = aAuditCode;
        auditDate = DateTime.getDateMMDDYY(); //sdf.format(cal.getTime());
        auditTimeRaw = DateTime.getSecondsSinceMidnight();  
        auditUserID = aAuditUserID;
        String queryString = "DELETE FROM pub.ps_rules ";
        queryString += " WHERE family_code = '" + aFamilyCode + "'";
        queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
        queryString += " AND seq_num = " + aSeqNum;
        debug (queryString);
        // Now let's try to delete it
        completionCode = conn.runUpdate(queryString);
        return completionCode; 
    }
    
    public boolean getAllowDuplicates() {
        return allowDuplicates;
    }
    
    public boolean getAllowFractions() {
        return allowFractions;
    }
    
    public boolean getAllowSign() {
        return allowSign;
    }
    
    public boolean getAllowTilde() {
        return allowTilde;
    }
    
    public boolean getAllowZero() {
        return allowZero;
    }
    
    public String getArrayListElement() {
        String listItem = "";
        listItem += "\"";
        listItem += seqNum + "\",\"";
        listItem += familyCode + "\",\"";
        listItem += subfamilyCode + "\",\"";
        listItem += ruleScope + "\",\"";
        listItem += parmName + "\",\"";
        listItem += dataType + "\",\"";
        if (dataType.equals("N")) {
            listItem += displayUnits + "\"";
        } else {
            listItem += "\"";
        }
        return listItem;
    }
    
    public String getArrayListElement2() {
        String listItem = "";
        listItem += "\"";
        listItem += seqNum + "\",\"";
        listItem += familyCode + "\",\"";
        listItem += subfamilyCode + "\",\"";
        listItem += ruleScope + "\",\"";
        if (parmStatus.equals("A")) {
            listItem += "Active\",\"";
        } else {
        listItem += "<font color='&CC0000'>Inactive</font>\",\"";
        }
        listItem += parmName + "\",\"";
        listItem += dataType + "\",\"";
        if (dataType.equals("N")) {
            listItem += displayUnits + "\"";
        } else {
            listItem += "\"";
        }
        return listItem;
    }
    
    public String getArrayListElement3() {
        String listItem = "";
        listItem += "\"";
        listItem += seqNum + "\",\"";
        listItem += familyCode + "\",\"";
        listItem += subfamilyCode + "\",\"";
        listItem += ruleScope + "\",\"";
        if (parmStatus.equals("A")) {
            listItem += "Active\",\"";
        } else {
        listItem += "<font color='&CC0000'>Inactive</font>\",\"";
        }
        listItem += parmName + "\",\"";
        listItem += dataType + "\",\"";
        if (dataType.equals("N")) {
            listItem += displayUnits + "\",\"";
        } else {
            listItem += "\",\"";
        }
        listItem += searchOrder + "\",\"";
        listItem += displayOrder + "\"";
        return listItem;
    }
        
    public String getAuditCode() {
        return auditCode;
    }  
    
    public String getAuditDate() {
        return auditDate;
    }
    
    public int getAuditTimeRaw() {
        return auditTimeRaw;
    }
    
    public String getAuditUserID() {
        return auditUserID;
    }
    
    public String getCharSet() {
        charSet = otherCharSet; 
        if (!regExpr) {
             if (charSetGroups.indexOf("U") != -1) { charSet += UC; }
             if (charSetGroups.indexOf("L") != -1) { charSet += LC; }
             if (charSetGroups.indexOf("S") != -1) { charSet += SP; }
             if (charSetGroups.indexOf("N") != -1) { charSet += NU; }
             if (charSetGroups.indexOf("A") != -1) { charSet += AP; }
             if (charSetGroups.indexOf("Q") != -1) { charSet += QU; }
        }
        return charSet;
    }
    
    public String getCharSetGroups() {
        
        return charSetGroups;
    }
    
    public String getCookedValue() {
        return cookedValue;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public String getDateFormat() {
        return dateFormat;
    }
    
    public int getDecShift() {
        return decShift;
    }
    
    public String getDefaultValueRaw() {
        return defaultValueRaw;
    }
    
    public String getDefaultValueCooked() {
        return defaultValueCooked;
    }
    
    public float getDefaultValueFloat() {
        return defaultValueFloat;
    }
    
    public boolean getDeleteLS() {
        return deleteLS;
    }
    
    public boolean getDeleteNPC() {
        return deleteNPC;
    }
    
    public boolean getDeleteSP() {
        return deleteSP;
    }
    
    public boolean getDeleteTS() {
        return deleteTS;
    }
    
    //public String getDEDefault() {
    //    return deLogicalDefault;
    //}
    
    public String getDeMultipliers() {
        return deMultipliers;
    }
    
    public String getDeObject(){
        return deObject;
    }
    
    public int getDeOrder() {
        return deOrder;
    }
    
    public boolean getDeRequired() {
        return deRequired;
    }
    
    public String getDeRequiredYN() {
        return deRequired ? "Y" : "N";
    }
    
    public String getDescription() {
        return description;
    }
    
    public GPSselectBox getDeSelectBox() {
        return deSelectBox;
    }
    
    public String getDeSelectBoxName() {
        return deSelectBoxName;
    }
        
    public int getDeTextBoxSize(){
        return deTextBoxSize;
    }
    
    public String getDeToolTip() {
        return deToolTip;
    }
    
    public String getDisplayJust() {
        return displayJust;
    }
    
    public String getDisplayMultipliers() {
        return displayMultipliers;
    }
        
    public int getDisplayOrder() {
        return displayOrder;
    }
    
    public String getDisplayUnits() {
        return displayUnits;
    }
    
    public static String getPreExistingSeqNums(WDSconnect conn, String aFamilyCode, 
            String aSubfamilyCode) throws SQLException {
        // Build string of exisiting fields for this scope
      String seqNum = "";
      String seqNumbers = ",";
      try {
        String queryString = "SELECT seq_num";
        queryString += " FROM pub.ps_rules";
        queryString += " WHERE family_code = '" + aFamilyCode + "'";
        queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
        queryString += " ORDER BY seq_num";
        ResultSet rs = conn.runQuery(queryString);
        if (rs != null) {
            while (rs.next()) {
                seqNum = rs.getString("seq_num");
                seqNumbers += seqNum + ",";
            }
            rs.close();
            rs = null;
            conn.closeStatement();
        }
      } catch (Exception e) {
          e.printStackTrace();
          seqNumbers = "";
      } finally {
          return seqNumbers;
      }
    }
    
    public String getFamilyCode(){
        return familyCode;
    }
    
    public String getFamilyName(){
        return familyName;
    }
    
    public String getFlags() {
        flags = "";
        if (dataType.equals("N")) {
            // calculate flags
            // D = Duplicates allowed
            // T = Tilde Allowed
            // Z = Zeroes allowed
            // F - Fractions (decimal point) allowed
            // S - Negative signs allowed

            if (allowDuplicates) { flags += "D"; }
            if (allowTilde) { flags += "T"; }
            if (allowZero) { flags += "Z"; }
            if (allowFractions) { flags += "F"; }    
            if (allowSign) { flags += "S"; }
        }
        if (dataType.equals("S")) {
            if (allowTilde) { flags += "T"; } 
            if (deleteNPC) { flags += "0"; }
            if (deleteSP) { flags += "1"; }
            if (deleteLS) { flags += "2"; }
            if (deleteTS) { flags += "3"; }
            if (reduceSP) { flags += "4"; }
            if (forceUC) { flags += "5"; }
            if (forceLC) { flags += "6"; }
            if (regExpr) { flags += "R"; }
        }
        return flags;
    }
    
    public boolean getForceInactive() {
        return forceInactive;
    }    
    public boolean getForceLC() {
        return forceLC;
    }
    
    public boolean getForceUC() {
        return forceUC;
    }
    
    public String getImageType() {
        return imageType;
    }
    
    public int getMatchOrder() {
        return matchOrder;
    }
    
    public int getMaxDecimalDigits() {
        return maxDecimalDigits;
    }
    
    public int getMaxLength() {
        return maxLength;
    }
    
    public String getMaxValueRaw() {
        return maxValueRaw;
    }
    
    public String getMaxValueCooked() {
        return maxValueCooked;
    }
    
    public float getMaxValueFloat() {
        return maxValueFloat;
    }
        
    public int getMinDecimalDigits() {
        return minDecimalDigits;
    }
    
    public int getMinLength() {
        return minLength;
    }
    
    public String getMinValueRaw() {
        return minValueRaw;
    }
        
    public String getMinValueCooked() {
        return minValueCooked;
    }
    
    public float getMinValueFloat() {
        return minValueFloat;
    }
    
    public String getOtherCharSet() {
        return otherCharSet;
    }
    
    public String getParmDelimiter() {
        return parmDelimiter;
    }
    
    public String getParmName() {
        return parmName;
    }
    
    public String getParmStatus() {
        return parmStatus;
    }
    
    public int getPeerGroup() {
        return peerGroup;
    }
    
    public int getPeerSubgroup() {
        return peerSubgroup;
    }
        
    public int getPreviewOrder() {
        return previewOrder;
    }
    
    public String getProductLineCode() {
        return productLineCode;
    }
    
    public String getProductLineName() {
        return productLineName;
    }
    
    public String getQobject() {
        return qObject;
    }
    
    public int getQtextBoxSize() {
        return qTextBoxSize;
    }
    
    public GPSselectBox getQselectBox() {
        return qSelectBox;
    }
    
    public String getQselectBoxName() {
        return qSelectBoxName;
    }
    
    public String getRawValue() {
        return rawValue;
    }
    
    public boolean getReduceSP() {
        return reduceSP;
    }
    
    public boolean getRegExpr() {
        return regExpr;
    }
    
    public String getRuleScope() {
        return ruleScope; 
    }
    
    public ArrayList<String> getSearchGeneratedScript(WDSconnect conn, int fieldNum) {
        
        int rc = 0;   // return code
        try {
            if (!isValid) {
                debug ("Rule Set for this field " + fieldNum + " is invalid.");
                return null;
            }
        
            // Abort on invalid data Type
        
            if ("NSL".indexOf(dataType) == -1) {
                debug ("Data Type Code for this field " + fieldNum + " is invalid.");
                return null;
            }
            ArrayList <String> generatedScript = new ArrayList <String> ();
            String work = "";
            String work2 = "";
            GPSselectBox sb = null;
            GPSunit unit = null;
            work = "    // Field " + fieldNum;
            generatedScript.add(work);
                        
            // Is this a NUMERIC field?
           
            if (dataType.equals("N")) {
                work = "    aCharSet[f] = \"\";";
                generatedScript.add(work);
                unit = new GPSunit();
                if (!unit.open(conn, displayUnits)) {
                    debug ("Database Error while attempting to obtain ps_units data for field " + fieldNum + ".");
                    return null;
                }
                decShift = unit.getMultiplierExp();
                if (unit.getMultiplierBase() == 10 && unit.getNumericBase() == 10) {
                    work = "    aDecShift[f] = \"" + Integer.toString(decShift) + "\";";
                } else {
                    work = "    aDecShift[f] = \"0\";";
                }
                generatedScript.add(work);
                work = "    aDefault[f] = \"\";"; 
                generatedScript.add(work);
                work = "    aDelim[f] = \"\"";  // Not for a Search field + parmDelimiter + "\";"; 
                generatedScript.add(work);
                work = "    aDEMultipliers[f] = \"" + deMultipliers + "\";"; 
                generatedScript.add(work);
                work = "    aRequired[f] = \"" + (searchRequired ? "Y" : "N") + "\";";
                generatedScript.add(work);
                work = "    aQTextBoxSize[f] = \"" + Integer.toString(qTextBoxSize) + "\";";
                generatedScript.add(work);
                work = "n";
                //if (allowDuplicates) { work += "D"; } Not on a search field
                if (allowFractions) { work += "F"; }
                if (allowSign) { work += "S"; }
                //if (allowTilde) { work += "T"; }  Not on a search field
                if (allowZero) { work += "Z"; }
                work = "    aFlags[f] = \"" + work + "\";";
                generatedScript.add(work);
                work = "    aLabel[f] = \"" + parmName + "\";";
                generatedScript.add(work);
                work = "    aMax[f] = \"" + maxValueRaw + "\";";
                generatedScript.add(work);
                work = "    aMin[f] = \"" + minValueRaw + "\";";
                generatedScript.add(work);
                work = "    aToolTip[f] = \"" + searchToolTip + "\";";
                generatedScript.add(work);
                if (displayUnits.equalsIgnoreCase("none")) {
                    displayUnits = "";
                }
                work = "    aUnits[f] = \"" + displayUnits + "\";";
                generatedScript.add(work);
            
                // if QTextBoxSize is zero we generate a select box here 
                // from ps_select_boxes table here:
                
                if (qTextBoxSize == 0) {
                    if (qSelectBoxName.length() == 0) {
                        debug ("Search Text Box Size is 0 and no Select Box Name specified for field " + fieldNum + ".");
                        return null;
                    }
                    sb = new GPSselectBox();
                    rc = sb.open(conn, familyCode, subfamilyCode, qSelectBoxName);
                    if (rc != GPSselectBox.SELECT_BOX_OK) {
                        debug ("Database error #" + rc + " while attempting to access select Box " + qSelectBoxName
                            + " for field " + fieldNum + ".");
                        return null;
                    }
                    ArrayList <String> oList = sb.getQOptionList();
                    for (int i = 0; i < oList.size() ;  i++) {
                        work = (String) oList.get(i);
                        generatedScript.add(work);
                    }
            
                    // we be done with a numeric field
                }       
            }
            
            // Is this a STRING field?
           
            if (dataType.equals("S")) {
                work = "    aCharSet[f] = \"" + otherCharSet + "\";";
                generatedScript.add(work);
                work = "    aDecShift[f] = \"\";";
                generatedScript.add(work);
                work = "    aDefault[f] = \"\";";
                generatedScript.add(work);
                work = "    aDelim[f] = \"\""; // Not for a Search field  + qRules.getParmDelimiter() + "\";";
                generatedScript.add(work);
                work = "    aDEMultipliers[f] = \"\";";
                generatedScript.add(work);
                work = "    aRequired[f] = \"" + (searchRequired ? "Y" : "N") + "\";";
                generatedScript.add(work);
                work = "    aQTextBoxSize[f] = \"" + Integer.toString(qTextBoxSize) + "\";";
                generatedScript.add(work);
                work2 = "s";
                if (deleteNPC) { work2 += "0"; }
                if (deleteSP) { work2 += "1"; }
                if (deleteLS) { work2 += "2"; }
                if (deleteTS) { work2 += "3"; }
                if (reduceSP) { work2 += "4"; }
                if (forceUC) { work2 += "5"; }
                if (forceLC) { work2 += "6"; }
                //if (allowDuplicates) { work2 += "D"; }
                //if (allowTilde) { work2 += "T"; }
                if (regExpr) { work2 += "R"; }
                work2 += charSetGroups;
                work = "    aFlags[f] = \"" + work2 + "\";";
                generatedScript.add(work);
                work = "    aLabel[f] = \"" + parmName + "\";";
                generatedScript.add(work);
                work = "    aMax[f] = \"" + Integer.toString(maxLength) + "\";";
                generatedScript.add(work);
                work = "    aMin[f] = \"" + Integer.toString(minLength) + "\";";
                generatedScript.add(work);
                work = "    aToolTip[f] = \"" + searchToolTip + "\";";
                generatedScript.add(work);
                work = "    aUnits[f] = \"\";";
                generatedScript.add(work);
                
                // if QTextBoxSize is zero we generate a select box here 
                // from ps_select_boxes table here:
                
                if (qTextBoxSize == 0) {
                    if (qSelectBoxName.length() == 0) {
                        debug ("Search Text Box Size is 0 and no Select Box Name specified for field " + fieldNum + ".");
                        return null;
                    }
                    sb = new GPSselectBox();
                    rc = sb.open(conn, familyCode, subfamilyCode, qSelectBoxName);
                    if (rc != GPSselectBox.SELECT_BOX_OK) {
                        debug ("Database error #" + rc + " while attempting to access select Box " + qSelectBoxName
                            + " for field " + fieldNum + "; family code is " + familyCode +
                                "; subfamily Code is " + subfamilyCode + ".");
                        return null;
                    }
                    ArrayList <String> oList = sb.getQOptionList();
                    for (int i = 0; i < oList.size() ;  i++) {
                        work = (String) oList.get(i);
                        generatedScript.add(work);
                    }
                    // we be done with a string field
                }      
            }
            
            // Is this a LOGICAL field?
           
            if (dataType.equals("L")) {
                work = "    aCharSet[f] = \"\";";
                generatedScript.add(work);
                work = "    aDecShift[f] = \"\";";
                generatedScript.add(work);
                work2 = searchLogicalDefault;
                work = "";
                if (work2.equals("N")) { work = "N"; }
                if (work2.equals("Y")) { work = "Y"; }
                work = "    aDefault[f] = \"" + work + "\";";
                generatedScript.add(work);
                work = "    aDelim[f] = \"\";";
                generatedScript.add(work);
                work = "    aDEMultipliers[f] = \"\";";
                generatedScript.add(work);
                work = "    aRequired[f] = \"" + (searchRequired ? "Y" : "N") + "\";";
                generatedScript.add(work);
                work = "    aQTextBoxSize[f] = \"\";";
                generatedScript.add(work);
                work = "    aFlags[f] = \"l\";";
                generatedScript.add(work);
                work = "    aLabel[f] = \"" + parmName + "\";";
                generatedScript.add(work);
                work = "    aMax[f] = \"\";";
                generatedScript.add(work);
                work = "    aMin[f] = \"\";";
                generatedScript.add(work);
                work = "    aToolTip[f] = \"" + searchToolTip + "\";";
                generatedScript.add(work);
                work = "    aUnits[f] = \"\";";
                generatedScript.add(work);
        
                // End of Logical field javascript
            }    
        
            // Is this a DATE field?
           
            if (dataType.equals("D")) {
                // End of Date field javascript
            }  
            work = "    f++;";
            generatedScript.add(work);
            return generatedScript;
        } catch (Exception e) {
            e.printStackTrace();
            debug ("Unexpected error generating script for field " + fieldNum + ".");
            return null;
        }
    }
    
    public String getSearchLogicalDefault() {
        return searchLogicalDefault;
    }
    
    public int getSearchMax() {
        return searchMax;
    }
    
    public float getSearchMaxValue() {
        return searchMaxValue;
    }
    
    public int getSearchMin() {
        return searchMin;
    }
    
    public float getSearchMinValue() {
        return searchMinValue;
    }
      
    public int getSearchOrder() {
        return searchOrder;
    }
    
    public boolean getSearchRequired() {
        return searchRequired;
    }
    
    public String getSearchString() {
        return searchString;
    }
    
    public String getSearchToolTip() {
        return searchToolTip;
    }
    
    public int getSearchWeight() {
        return searchWeight;
    }
    
    public float getSearchValue() {
        return searchValue;
    }
    
    public int getSeqNum() {
        return seqNum;
    }
            
    public int getSelectBoxChild() {
        return selectBoxChild;
    } 
    
    public boolean getSelectBoxFilter() {
        return selectBoxFilter;
    }        
        
    public boolean getSeriesImplicit() {
        return seriesImplicit;
    }
    
    public int getSigDigits() {
        return sigDigits;
    }
    
    public String getSubfamilyCode() {
        return subfamilyCode;
    }
    
    public String getSubfamilyName() {
        return subfamilyName;
    }
    
    public String getTimeFormat() {
        return timeFormat;
    }
    
    /* I initialize the basic variables common to
     * all data types N, S, L, and D
     */
    public void initCommon() {
        setAllowDuplicates(false);
        setAllowFractions(false);
        setAllowSign(false);
        setAllowTilde(false);
        setAllowZero(false);
        setAuditCode("");
        setAuditDate("");
        setAuditTimeRaw(0);
        setAuditUserID("");
        setCharSetGroups("");
        setDataType("");
        setDateFormat("");
        setDefaultValueRaw("");
        setDefaultValueCooked("");
        setDeleteLS(true);
        setDeleteNPC(true);
        setDeleteSP(false);
        setDeleteTS(true);
        setDeMultipliers("");
        setDeObject("");
        setDeOrder(-1);
        setDeRequired(false);
        setDescription("");
        setDeSelectBoxName("");
        setDeTextBoxSize(0);
        setDeToolTip("");
        setDisplayJust("");
        setDisplayMultipliers("");
        setDisplayOrder(-1);
        setDisplayUnits("");
        setFamilyCode("");
        setFamilyName("");
        setForceInactive(false);
        setForceLC(false);
        setForceUC(false);
        setImageType("");
        setMatchOrder(-1);
        setMaxDecimalDigits(0);
        setMaxLength(0);
        setMaxValueRaw("");
        setMaxValueCooked("");
        setMinDecimalDigits(0);
        setMinLength(0);
        setMinValueRaw("");
        setMinValueCooked("");
        setOtherCharSet("");
        setParmDelimiter("");
        setParmName("");
        setParmStatus("A");
        setPreviewOrder(-1);
        setProductLineCode("");
        setProductLineName("");
        setQobject("");
        setQselectBoxName("");
        setQtextBoxSize(0);
        setReduceSP(true);
        setRegExpr(false);
        setRuleScope("");
        setSearchLogicalDefault("");
        setSearchMax(0);
        setSearchMin(0);
        setSearchOrder(-1);
        setSearchRequired(false);
        setSearchToolTip("");
        setSearchWeight(0);
        setSeqNum(-1);
        setSelectBoxChild(-1);
        setSelectBoxFilter(false);
        setSeriesImplicit(false);
        setSigDigits(0);
        setSubfamilyCode("");
        setSubfamilyName("");
        setTimeFormat("");
    }
    
    public void initDateRuleSet() {
        setDataType("D");
    }
    
    public void initLogicalRuleSet() {
        setDataType("L");
    }
    
    public void initNumericRuleSet() {
        setDataType("N");
        setDecShift(0);
        setDeMultipliers("U");
        setDisplayMultipliers("U");    
    }
    
    public void initStringRuleSet() {
        setDataType("S");
    }
    
    public boolean read(WDSconnect conn, String aFamilyCode, String aSubfamilyCode, 
            String aScope, int aSeqNum) {
            
        String queryString;
        ResultSet rs;
        
        try {
            queryString = " SELECT * ";
            queryString += " FROM pub.ps_rules";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " AND rule_scope = '" + aScope + "'";
            queryString += " AND seq_num = " + aSeqNum ;
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    if (!setProperties(rs) ) {
                        String message = "Failed to read rules for this Family/Subfamily/Scope/SeqNum " + familyCode + " - " + subfamilyCode;
                        debug (message);
                        rs.close();
                        return false;
                    } else {
                        rs.close();
                        if (dataType.equals("N")) {
                            unit = new GPSunit();
                            unit.open(conn, displayUnits);
                            if (unit.getNumericBase() == 10 && unit.getMultiplierBase() == 10.0) {
                                setDecShift(unit.getMultiplierExp());
                            } else {
                                setDecShift(0);
                            }
                        }
                        unit = null;  // release units object
                        return true;
                    }
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            return false;
        } catch (Exception e) {
            debug ("SQL Exception while trying to read rules");
            e.printStackTrace();
            return false;
        }
    }
    
    public void setAllowDuplicates(boolean x) {
        allowDuplicates = x;
    }
    
    public void setAllowFractions(boolean x) {
        allowFractions = x;
    }
    
    public void setAllowSign(boolean x) {
        allowSign = x;
    }
    
    public void setAllowTilde(boolean x) {
        allowTilde = x;
    }
    
    public void setAllowZero(boolean x) {
        allowZero = x;
    }
    
    public void setAuditCode(String x) {
        auditCode = x;
    }  
    
    public void setAuditDate(String x) {
        auditDate = x;
    }
    
    public void setAuditTimeRaw(int x) {
        auditTimeRaw = x;
    }
    
    public void setAuditUserID(String x) {
        auditUserID = x;
    }
    
    public void setCharSet(String x) {
        charSet = x;
    }
            
    public void setCharSetGroups(String x) {
        charSetGroups = x;
    }
    
    public void setCookedValue(String x) {
        cookedValue = x;
    }
    
    public void setDataType(String x) {
        dataType = x;
    }
    
    public void setDateFormat(String x) {
        dateFormat = x;
    }
    
    public void setDecShift(int x) {
        if (x < -21 || x > 21) {
            decShift = 0;
        } else {
            decShift = x;    
        }    
    }
    
    public void setDefaultValueRaw(String x) {
        defaultValueRaw = x;
    }
        
    public void setDefaultValueCooked(String x) {
        defaultValueCooked = x;
    }
    
    /*
    public void setDefaultValueFloat() {
        defaultValueFloat;
    }
    */
    
    public void setDeleteLS(boolean x) {
        deleteLS = x;
    }
    
    public void setDeleteNPC(boolean x) {
        deleteNPC = x;
    }
    
    public void setDeleteSP(boolean x) {
        deleteSP = x;
    }
    
    public void setDeleteTS(boolean x) {
        deleteTS = x;
    }
    
    public void setDeMultipliers(String x) {
        deMultipliers =x;
    }
    
    public void setDeObject(String x){
        deObject = x;
    }
    
    public void setDeOrder(int x) {
        deOrder = x;
    }
    
    public void setDeRequired(boolean x) {
        deRequired = x;
    }
    
    public void setDescription(String x) {
        description = x;
    }
    
    public void setDeSelectBox(GPSselectBox selectBox) {
        deSelectBox = selectBox;
    }
        
    public void setDeSelectBoxName(String x) {
        deSelectBoxName = x;
    }
        
    public void setDeTextBoxSize(int x){
        deTextBoxSize = x;
    }
    
    public void setDeToolTip(String x) {
        deToolTip = x;
    }
    
    public void setDisplayJust(String x) {
        displayJust = x;
    }
    
    public void setDisplayMultipliers(String x) {
        displayMultipliers = x;
    }
        
    public void setDisplayOrder(int x) {
        displayOrder = x;
    }
    
    public void setDisplayUnits(String x) {
        displayUnits = x;
    }
    
    public void setFamilyCode(String x){
        familyCode = x;
    }
    
    public void setFamilyName(String x){
        familyName = x;
    }
    
    /*
    public String setFlags() {
        flags;
    }
    */
    
    public void setForceInactive(boolean x) {
        forceInactive = x;
    }
        
    public void setForceLC(boolean x) {
        forceLC = x;
    }
    
    public void setForceUC(boolean x) {
        forceUC = x;
    }
    
    public void setImageType(String x) {
        imageType = x;
    }
    
    public void setMatchOrder(int x) {
        matchOrder = x;
    }
    
    public void setMaxDecimalDigits(int x) {
        maxDecimalDigits = x;
    }
    
    public void setMaxLength(int x) {
        maxLength = x;
    }
    
    public void setMaxValueRaw(String x) {
        maxValueRaw = x;
    }
    
    public void setMaxValueCooked(String x) {
        maxValueCooked = x;
    }
    
    /*
    public void setMaxValueFloat() {
        return maxValueFloat;
    }
    */
    
    public void setMinDecimalDigits(int x) {
        minDecimalDigits = x;
    }
    
    public void setMinLength(int x) {
        minLength = x;
    }
    
    public void setMinValueRaw(String x) {
        minValueRaw = x;
    }
    
    public void setMinValueCooked(String x) {
        minValueCooked = x;
    }
    
    /*
    public void setMinValueFloat() {
        return minValueFloat;
    }
    */
    
    public void setOtherCharSet(String x) {
        otherCharSet = x;
    }
    
    public void setParmDelimiter(String x) {
        parmDelimiter = x;
    }
    
    public void setParmName(String x) {
        parmName = x;
    }
    
    public void setParmStatus(String x) {
        parmStatus = x;
    }
    
    public void setPeerGroup(int x) {
        peerGroup = x;
    }
    
    public void setPeerSubgroup(int x) {
        peerSubgroup = x;
    }
        
    public void setPreviewOrder(int x) {
        previewOrder = x;
    }
    
    public void setProductLineCode(String x) {
        productLineCode = x;
    }
    
    public void setProductLineName(String x) {
        productLineName = x;
    }
    
    /*
    public void setProductLineCode(String x) {
        productLineCode = x;
    }
    
    public void setProductLineName(String x) {
        productLineName = x;
    }
     */
        
    public boolean setProperties(ResultSet rs) {
        
        // Warning!!!! decShift is NOT set by this method!
        // You must manually set decShift using the setDecShift method
        // after obtaining the appropriate value associated with
        // the display units name row in the the ps_units table
        // the deSelectBox and qSelectBox objects are not created here either
        
        try {
            // Get mandatory fields first
            ruleScope = rs.getString("rule_scope");
            familyCode = rs.getString("family_code");
            subfamilyCode = rs.getString("subfamily_code");
            parmName = rs.getString("parm_name");
            seqNum = rs.getInt("seq_num");
            auditCode = rs.getString("audit_code");
            auditDate = rs.getString("audit_date");
            auditTimeRaw = rs.getInt("audit_time_raw");
            auditUserID = rs.getString("audit_userid");
            deOrder = rs.getInt("de_order");
            deRequired = rs.getBoolean("de_required");
            dataType = rs.getString("data_type");
            defaultValueRaw = rs.getString("default_value");
            description = rs.getString("description");
            deToolTip = rs.getString("de_tool_tip");
            displayJust = rs.getString("display_just");
            displayOrder = rs.getInt("display_order");
            matchOrder = rs.getInt("match_order");
            parmStatus = rs.getString("parm_status");
            peerGroup = rs.getInt("peer_group");
            peerSubgroup = rs.getInt("peer_subgroup");
            previewOrder = rs.getInt("preview_order");
            searchMax = rs.getInt("search_max");
            searchMin = rs.getInt("search_min");
            searchOrder = rs.getInt("search_order");
            searchRequired = rs.getBoolean("search_required");
            searchToolTip = rs.getString("search_tool_tip");
            searchWeight = rs.getInt("search_weight");
            selectBoxChild = rs.getInt("select_box_child");
            selectBoxFilter = rs.getBoolean("select_box_filter");
            seriesImplicit = rs.getBoolean("series_implicit");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (dataType.equals("N")) {
            // Process mandatory numeric fields here
            try {
                allowDuplicates = rs.getBoolean("allow_duplicates");
                allowFractions = rs.getBoolean("allow_fractions");
                allowSign = rs.getBoolean("allow_sign");
                allowTilde = rs.getBoolean("allow_tilde");
                allowZero = rs.getBoolean("allow_zero");
                if (Convert.isFloat(defaultValueRaw)) {
                    defaultValueFloat = Float.parseFloat(defaultValueRaw);
                } 
                deMultipliers = rs.getString("de_multipliers");
                deObject = rs.getString("de_object");
                deSelectBoxName = rs.getString("de_select_box_name");
                deTextBoxSize = rs.getInt("de_text_box_size");
                displayMultipliers = rs.getString("display_multipliers");
                displayUnits = rs.getString("display_units").trim();
                // *****************************************************************
                //  decShift comes from an individual call to setDecShift method.
                // ******************************************************************
                maxDecimalDigits = rs.getInt("max_decimal_digits");
                maxValueRaw = rs.getString("max_value");
                maxValueFloat = Float.parseFloat(maxValueRaw);
                minDecimalDigits = rs.getInt("min_decimal_digits");
                minValueRaw = rs.getString("min_value");
                minValueFloat = Float.parseFloat(minValueRaw);
                parmDelimiter = rs.getString("parm_delimiter");
                qObject = rs.getString("q_object");
                qSelectBoxName = rs.getString("q_select_box_name");
                qTextBoxSize = rs.getInt("q_text_box_size");
                sigDigits = rs.getInt("sig_digits");

                // calculate flags
                // D = Duplicates allowed
                // T = Tilde Allowed
                // Z = Zeroes allowed
                // F - Fractions (decimal point) allowed
                // S - Negative signs allowed
                flags = "";
                if (allowDuplicates) { flags += "D"; }
                if (allowTilde) { flags += "T"; }
                if (allowZero) { flags += "Z"; }
                if (allowFractions) { flags += "F"; }    
                if (allowSign) { flags += "S"; }
                               
                // hmmm... lets provide a setDecShift method instead
                // that way we do not have to do a DB look up internally here

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            isValid = true;
            return true;
        }
        if (dataType.equals("S")) {
            // Process mandatory string fields here
            try {
                allowDuplicates = rs.getBoolean("allow_duplicates");
                allowTilde = rs.getBoolean("allow_tilde");
                charSetGroups = rs.getString("char_set_groups");
                deleteLS = rs.getBoolean("delete_ls");
                deleteNPC = rs.getBoolean("delete_npc");
                deleteSP = rs.getBoolean("delete_sp");
                deleteTS = rs.getBoolean("delete_ts");
                deObject = rs.getString("de_object");
                deSelectBoxName = rs.getString("de_select_box_name");
                deTextBoxSize = rs.getInt("de_text_box_size");
                forceLC = rs.getBoolean("force_lc");
                forceUC = rs.getBoolean("force_uc");
                imageType = rs.getString("image_type");
                maxLength = rs.getInt("max_length");
                minLength = rs.getInt("min_length");
                otherCharSet = rs.getString("other_char_set");
                parmDelimiter = rs.getString("parm_delimiter");
                qObject = rs.getString("q_object");
                qSelectBoxName = rs.getString("q_select_box_name");
                qTextBoxSize = rs.getInt("q_text_box_size");
                reduceSP = rs.getBoolean("reduce_sp");
                regExpr = rs.getBoolean("reg_expr");
                // build the charSet property
                charSet = otherCharSet;
                if (!regExpr) {
                    if (charSetGroups.indexOf("U") != -1) { charSet += UC; }
                    if (charSetGroups.indexOf("L") != -1) { charSet += LC; }
                    if (charSetGroups.indexOf("S") != -1) { charSet += SP; }
                    if (charSetGroups.indexOf("N") != -1) { charSet += NU; }
                    if (charSetGroups.indexOf("A") != -1) { charSet += AP; }
                    if (charSetGroups.indexOf("Q") != -1) { charSet += QU; }
                }
                flags = "";
                if (allowTilde) { flags += "T"; } 
                if (deleteNPC) { flags += "0"; }
                if (deleteSP) { flags += "1"; }
                if (deleteLS) { flags += "2"; }
                if (deleteTS) { flags += "3"; }
                if (reduceSP) { flags += "4"; }
                if (forceUC) { flags += "5"; }
                if (forceLC) { flags += "6"; }
                if (regExpr) { flags += "R"; }
                
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            isValid = true;
            return true;
        }
        if (dataType.equals("L")) {
            // import any logical properties here
            try {
                defaultValueRaw = rs.getString("default_value");
                searchLogicalDefault = rs.getString("search_logical_default");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            isValid = true;
            return true;
        }
        if (dataType.equals("D")) {
            // Process mandatory date fields here
            try {
               // not supported yet    
                //dateFormat;
                //timeFormat;
      
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            isValid = false;
            return false;  // change to true when supported
        }
        System.out.println("Class GPSrules.java found invalid data type code '" + dataType + "' for field seqnum '" + seqNum 
                + "' in " + familyCode + " - " + subfamilyCode);
        return false;    
    }
    
    public void setQobject(String x) {
        qObject = x;
    }
    
    public void setQselectBox(GPSselectBox x) {
        qSelectBox = x;
    }
        
    public void setQselectBoxName(String x) {
        qSelectBoxName = x;
    }
    
    public void setQtextBoxSize(int x) {
        qTextBoxSize = x;
    }
        
    public void setRawValue(String x) {
        rawValue = x;
    }
    
    public void setReduceSP(boolean x) {
        reduceSP = x;
    }
    
    public void setRegExpr(boolean x) {
        regExpr = x;
    }
    
    public void setRuleScope(String x) {
        ruleScope = x; 
    }
    
    public void setSearchLogicalDefault(String x) {
        searchLogicalDefault = x;
    }
    
    public void setSearchMax(int x) {
        searchMax = x;
    }
    
    public void setSearchMaxValue(float x) {
        searchMaxValue = x;
    }
    
    public void setSearchMin(int x) {
        searchMin = x;
    }
    
    public void setSearchMinValue(float x) {
        searchMinValue = x;
    }
      
    public void setSearchOrder(int x) {
        searchOrder = x;
    }
        
    public void setSearchRequired(boolean x) {
        searchRequired = x;
    }
    
    public void setSearchString(String x) {
        searchString = x;
    }
    
    public void setSearchToolTip(String x) {
        searchToolTip = x;
    }
        
    public void setSearchValue(float x) {
        searchValue = x;
    }
    
    public void setSearchWeight(int x) {
        searchWeight = x;
    }

    public void setSeqNum(int x) {
        seqNum = x;
    }
            
    public void setSelectBoxChild(int x) {
        selectBoxChild = x;
    } 
    
    public void setSelectBoxFilter(boolean x) {
        selectBoxFilter = x;
    }        
        
    public void setSeriesImplicit(boolean x) {
        seriesImplicit = x;
    }
    
    public void setSigDigits(int x) {
        sigDigits = x;
    }
    
    public void setSubfamilyCode(String x) {
        subfamilyCode = x;
    }
    
    public void setSubfamilyName(String x) {
        subfamilyName = x;
    }
    
    public void setTimeFormat(String x) {
        timeFormat = x;
    }
   
    public boolean writeRules(WDSconnect conn, String aFamilyCode, String aSubfamilyCode, 
            String aSeqNum, String aAuditCode, String aAuditUserID) {
        // Build Insert SQL Command String!
        
        boolean completionCode;
        auditCode = aAuditCode;
        auditDate = DateTime.getDateMMDDYY(); //sdf.format(cal.getTime());
        auditTimeRaw = DateTime.getSecondsSinceMidnight();  
        auditUserID = aAuditUserID;
        familyCode = aFamilyCode;
        subfamilyCode = aSubfamilyCode;
        seqNum = Integer.parseInt(aSeqNum);
        String queryString = "INSERT INTO pub.ps_rules ";
        String keyString = " (";
        String valueString = " VALUES (";
        keyString += "allow_duplicates, ";
        valueString += "'" + (allowDuplicates ? "1" : "0")  + "', ";
        keyString += "allow_fractions, ";
        valueString += "'" + (allowFractions ? "1" : "0") + "', ";
        keyString += "allow_sign, ";
        valueString += "'" + (allowSign ? "1" : "0") + "', ";
        keyString += "allow_tilde, ";
        valueString += "'" + (allowTilde ? "1" : "0") + "', ";
        keyString += "allow_zero, ";
        valueString += "'" + (allowZero ? "1" : "0") + "', ";
        keyString += "audit_code, ";
        valueString += "'" + auditCode + "', ";
        keyString += "audit_date, "; 
        valueString += "'" + auditDate + "', ";
        keyString += "audit_time_raw, ";
        valueString += Integer.toString(auditTimeRaw) + ", ";  
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
        valueString += Integer.toString(deOrder) + ", ";
        keyString += "de_required, ";
        valueString += "'" + (deRequired ? "1" : "0") + "', ";
        keyString += "de_select_box_name, ";
        valueString += "'" + deSelectBoxName + "', ";
        keyString += "de_text_box_size, ";
        valueString += Integer.toString(deTextBoxSize) + ", ";
        keyString += "default_value, ";
        valueString += "'" + defaultValueRaw + "', ";
        keyString += "delete_ls, ";
        valueString += "'" + (deleteLS ? "1" : "0") + "', ";
        keyString += "delete_npc, ";
        valueString += "'" + (deleteNPC ? "1" : "0") + "', ";
        keyString += "delete_sp, ";
        valueString += "'" + (deleteSP ? "1" : "0") + "', ";
        keyString += "delete_ts, ";
        valueString += "'" + (deleteTS ? "1" : "0") + "', ";
        keyString += "description, ";
        valueString += "'" + description + "', ";
        keyString += "de_tool_tip, ";
        valueString += "'" + deToolTip + "', ";
        keyString += "display_multipliers, ";
        valueString += "'" + displayMultipliers + "', ";
        keyString += "display_just, ";
        valueString += "'" + displayJust + "', ";
        keyString += "display_order, ";
        valueString += Integer.toString(displayOrder) + ", ";
        keyString += "display_units, ";
        valueString += "'" + displayUnits + "', ";
        keyString += "family_code, ";
        valueString += "'" + familyCode + "', ";
        keyString += "force_lc, ";
        valueString += "'" + (forceLC ? "1" : "0") + "', ";
        keyString += "force_uc, ";
        valueString += "'" + (forceUC ? "1" : "0") + "', ";
        keyString += "image_type, ";
        valueString += "'" + imageType + "', ";
        keyString += "match_order, ";
        valueString += Integer.toString(matchOrder) + ", ";
        keyString += "max_decimal_digits, ";
        valueString += Integer.toString(maxDecimalDigits) + ", ";
        keyString += "max_length, ";
        valueString += Integer.toString(maxLength) + ", ";
        keyString += "max_value, ";
        valueString += "'" + maxValueRaw + "', ";
        keyString += "min_decimal_digits, ";
        valueString += Integer.toString(minDecimalDigits) + ", "; 
        keyString += "min_length, ";
        valueString += Integer.toString(minLength) + ", ";
        keyString += "min_value, ";
        valueString += "'" + minValueRaw + "', ";
        keyString += "other_char_set, ";
        valueString += "'" + otherCharSet + "', ";
        keyString += "parm_delimiter, ";
        valueString += "'" + parmDelimiter + "', ";
        keyString += "parm_name, ";
        valueString += "'" + parmName + "', ";
        keyString += "parm_status, ";
        valueString += "'" + parmStatus + "', ";
        keyString += "peer_group, ";
        valueString += Integer.toString(peerGroup) + ", ";
        keyString += "peer_subgroup, ";
        valueString += Integer.toString(peerSubgroup) + ", ";
        keyString += "preview_order, ";
        valueString += Integer.toString(previewOrder) + ", ";
        keyString += "q_object, ";
        valueString += "'" + qObject + "', ";
        keyString += "q_select_box_name, ";
        valueString += "'" + qSelectBoxName + "', ";
        keyString += "q_text_box_size, ";
        valueString += Integer.toString(qTextBoxSize) + ", ";
        keyString += "reduce_sp, ";
        valueString += "'" + (reduceSP ? "1" : "0") + "', ";
        keyString += "reg_expr, ";
        valueString += "'" + (regExpr ? "1" : "0") + "', ";
        keyString += "rule_scope, ";
        valueString += "'" + ruleScope + "', ";
        keyString += "search_logical_default, ";
        valueString += "'" + searchLogicalDefault + "', ";
        keyString += "search_max, ";
        valueString += Integer.toString(searchMax) + ", ";   
        keyString += "search_min, ";
        valueString += Integer.toString(searchMin) + ", ";
        keyString += "search_order, ";
        valueString += Integer.toString(searchOrder) + ", ";
        keyString += "search_required, ";
        valueString += "'" + (searchRequired ? "1" : "0") + "', ";
        keyString += "search_tool_tip, ";
        valueString += "'" + searchToolTip + "', ";
        keyString += "search_weight, ";
        valueString += Integer.toString(searchWeight) + ", ";
        keyString += "seq_num, ";
        valueString += Integer.toString(seqNum) + ", ";
        keyString += "series_implicit, ";
        valueString += "'" + (seriesImplicit ? "1" : "0") + "', ";
        keyString += "select_box_filter, ";
        valueString += "'" + (selectBoxFilter ? "1" : "0") + "', ";
        keyString += "subfamily_code, ";
        valueString += "'" + subfamilyCode + "', ";

        // end of key/value pairs / remove last comma/space from strings
                    
        keyString = keyString.substring(0,keyString.length()-2);
        valueString = valueString.substring(0,valueString.length()-2);
        keyString += ") ";
        valueString += ") ";
        queryString += keyString;
        queryString += valueString;
        debug (queryString);
        // Now let's try to add it
                    
        completionCode = conn.runUpdate(queryString);
        return completionCode; 
    }
}
