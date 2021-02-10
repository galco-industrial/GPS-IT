/*
 * GPSparmSet.java
 *
 * Created on October 27, 2006, 9:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

import OEdatabase.WDSconnect;
import java.io.*;
import java.sql.*;
import java.util.*;
/**
 * 
 * 
 * @author Master
 * @version 1.3.01
 * 
 * I am used as a container class to create objects that
 * hold parametric values for a given part number. The read method extracts
 * raw parametric values for a part from the database.
 * 
 * Right now the parmValuesCooked Array holds null values
 * but eventually methods will be added that compute the 
 * cooked values from raw values.
 */
public class GPSparmSet {
    
    private int debugLevel = 0;
    private static final String version = "1.5.00";
  
    private String description = "";
    private float finalScore;
    private boolean initialized = false;
    private List <String> parmValuesCooked = new ArrayList <String> ();
    private List <String> parmValuesRaw = new ArrayList <String> ();
    private String partNum;
    private List <String> seqNums = new ArrayList <String> ();
    
        
    /**
     * Creates a new instance of GPSparmSet
     */
    public GPSparmSet() {
    }
    
    /**
     * Creates a new instance of GPSparmSet
     * and loads the parm data for a part number
     * using a predefined ruleSet[]
     */
    public GPSparmSet(WDSconnect conn, String aPartNum, GPSrules[] aRuleSets) {
        // If I complete successfully, initialized will be set to true
        try {
            partNum = aPartNum;
            if (read(conn, aPartNum)) {
                // Seq numbers and raw values are now loaded into the arraylists
                // Now lets run through the GPSrules array and cook the raw data
                int ruleCount = -1;
                for (int i = 0; i < 100; i++) {
                    if (aRuleSets[i] == null) {
                        ruleCount = i;
                        break;
                    }
                }
                GPScvt cvt = new GPScvt();
                StringBuffer sBuffer = new StringBuffer();
                parmValuesCooked.addAll(parmValuesRaw);;
                for (int ruleNo = 0; ruleNo < ruleCount; ruleNo++) {
                    GPSrules ruleSet = aRuleSets[ruleNo];
                    GPSselectBox selectBox = null;
                    int seqNum = ruleSet.getSeqNum();
                    String dataType = ruleSet.getDataType();
                    int seqNumIndex = seqNums.indexOf(Integer.toString(seqNum));
                    if (seqNumIndex > -1) {
                        String parmValueRaw = parmValuesRaw.get(seqNumIndex);
                        String parmValueCooked = parmValueRaw;
                        if (!parmValueRaw.equals("")) {
                            String parmName = ruleSet.getParmName();
                            String displayUnits = "";
                            debug (8, "Processing " +seqNum + "; raw value = " + parmValueRaw);
                            if ("SN".contains(dataType)) {
                            
                                // If a DE select box name exists...
                                // make sure the select box object for it was created
                        
                                String selectBoxName = ruleSet.getDeSelectBoxName();
                                if (ruleSet.getDeTextBoxSize() == 0 && !selectBoxName.equals("") ) {
                                    debug (6, "Checking for DE Select Box named " + selectBoxName + " for seq num " + seqNum);
                                    selectBox = ruleSet.getDeSelectBox();
                                    if (selectBox == null) {
                                        debug (6, "Attempting to create DE Select Box object for " + selectBoxName);
                                        selectBox = new GPSselectBox();  // create a new select box object
                                        int rc = selectBox.open(conn, ruleSet.getFamilyCode(), ruleSet.getSubfamilyCode(), selectBoxName);
                                        if (rc < 0) {
                                            String errorMessage = "Unexpected error " + rc + " creating Select Box object for " + selectBoxName;
                                                debug (0, errorMessage);   
                                                break;
                                            }
                                            ruleSet.setDeSelectBox(selectBox);
                                            debug (6, "Created DE Select Box object for " + selectBoxName);
                                        } else {
                                            debug (6, "DE Select Box object for " + selectBoxName + " already exists.");
                                        }
                                    } else {
                                        debug (6, "There is no DE Select Box for seq num " + seqNum);
                                    }

                                    /////////////////////////////////////////////////////////////////////////////////
                                    // Cook the raw value here                                                     //
                                    /////////////////////////////////////////////////////////////////////////////////
                        
                                    debug (6, "Now converting raw value to cooked for field seq num " + seqNum);

                                    ruleSet.setRawValue(parmValueRaw);
                
                                    // If a DE select box is applicable
                                    // try to do a select box look up on the raw value
                            
                                    if (selectBox != null) { 
                                        // Do this if there is a select box defined for this field
                                        // Let's attempt a look up and replace the raw with the select box entry
                                        int rc = selectBox.optionValue1IndexOf(parmValueRaw);
                                        debug (8, "SB Lookup Return code was " + rc);
                                        if (rc > -1) {
                                            parmValueCooked = selectBox.getOptionText(rc);
                                        }
                                    } else if (dataType.equals("N") ) {
                                        parmValueCooked = cvt.toCooked(parmValueRaw, ruleSet.getDisplayMultipliers(),
                                        ruleSet.getParmDelimiter(), ruleSet.getDecShift(), 
                                        ruleSet.getAllowDuplicates(), ruleSet.getAllowTilde(), true);
                                    }
                                debug (8, "Cooked value = '" + parmValueCooked + "'");
                                ruleSet.setCookedValue(parmValueCooked);
                                parmValuesCooked.set(seqNumIndex, parmValueCooked);
                            
                                if (dataType.equals("N")) {
                                    displayUnits = ruleSet.getDisplayUnits();
                                    if (displayUnits.equalsIgnoreCase("None")) {
                                        displayUnits = "";
                                    }
                                }
                            } else if (dataType.equals("L")) {
                                parmValueCooked = parmValueRaw.equals("1") ? "Yes" : "No";
                            }    
                
                            // Add to description string now
                
                            sBuffer.append("<li>");
                            sBuffer.append(parmName + ": ");
                            sBuffer.append(parmValueCooked);
                            if (!displayUnits.equals("")) {
                                sBuffer.append(" " + displayUnits);
                            }
                            sBuffer.append("</li>");
                        }
                    }    
                }
                description = sBuffer.toString();
                initialized = true;
                debug (4, "Description is " + description.length() + " characters long.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GPSparmSet(WDSconnect conn, String aPartNum, GPSrules[] displayRuleSets, String[] parmValues) {
        // If I complete successfully, initialized will be set to true
        partNum = aPartNum;
        try {
            // raw values were pre-loaded in parmValues String array
            // the field sequence number is the index of the raw value
            // First run through the raw values and cook the raw data
            // displayRuleSets is an array of the display rulesets in display order
            // find out the actual number of rulesets defined in the displayRuleSets array
            int fieldCount = -1;
            for (int i = 0; i < 100; i++) {
                if (displayRuleSets[i] == null) {
                    fieldCount = i;
                    break;
                }
            }
            GPScvt cvt = new GPScvt();
            StringBuffer sBuffer = new StringBuffer(); // I will hold the description html text
            //parmValuesCooked.addAll(parmValuesRaw);; // init cooked values to raw values
            for (int fieldNo = 0; fieldNo < fieldCount; fieldNo++) {
                GPSrules ruleSet = displayRuleSets[fieldNo];
                GPSselectBox selectBox = null;
                int seqNum = ruleSet.getSeqNum();
                String work = Integer.toString(seqNum);
                seqNums.add(work);
                int seqNumIndex = seqNums.indexOf(work);
                String dataType = ruleSet.getDataType();
                String parmValueRaw = parmValues[seqNum];
                parmValuesRaw.add(parmValueRaw);
                String parmValueCooked = parmValueRaw;
                if (!parmValueRaw.equals("")) {
                    String parmName = ruleSet.getParmName();
                    String displayUnits = "";
                    debug (8, "Processing " +seqNum + "; raw value = " + parmValueRaw);
                    if ("SN".contains(dataType)) {
                        // If a DE select box name exists...
                        // make sure the select box object for it was created
                        String selectBoxName = ruleSet.getDeSelectBoxName();
                        if (ruleSet.getDeTextBoxSize() == 0 && !selectBoxName.equals("") ) {
                            debug (6, "Checking for DE Select Box named " + selectBoxName + " for seq num " + seqNum);
                            selectBox = ruleSet.getDeSelectBox();
                            if (selectBox == null) {
                                debug (6, "Attempting to create DE Select Box object for " + selectBoxName);
                                selectBox = new GPSselectBox();  // create a new select box object
                                int rc = selectBox.open(conn, ruleSet.getFamilyCode(), ruleSet.getSubfamilyCode(), selectBoxName);
                                if (rc < 0) {
                                    String errorMessage = "Unexpected error " + rc + " creating Select Box object for " + selectBoxName;
                                    debug (0, errorMessage);   
                                    break;
                                }
                                ruleSet.setDeSelectBox(selectBox);
                                debug (6, "Created DE Select Box object for " + selectBoxName);
                            } else {
                                debug (6, "DE Select Box object for " + selectBoxName + " already exists.");
                            }
                        } else {
                            debug (6, "There is no DE Select Box for seq num " + seqNum);
                        }

                        /////////////////////////////////////////////////////////////////////////////////
                        // Cook the raw value here                                                     //
                        /////////////////////////////////////////////////////////////////////////////////
                        
                        debug (6, "Now converting raw value to cooked for field seq num " + seqNum);
                        ruleSet.setRawValue(parmValueRaw);
               
                        // If a DE select box is applicable
                        // try to do a select box look up on the raw value
                            
                        if (selectBox != null) { 
                            // if there is a select box defined for this field
                            // Let's attempt a look up and replace the raw with the select box entry
                            int rc = selectBox.optionValue1IndexOf(parmValueRaw);
                            debug (8, "SB Lookup Return code was " + rc);
                            if (rc > -1) {
                                parmValueCooked = selectBox.getOptionText(rc);
                            }
                        } else if (dataType.equals("N") ) {
                            parmValueCooked = cvt.toCooked(parmValueRaw, ruleSet.getDisplayMultipliers(),
                                ruleSet.getParmDelimiter(), ruleSet.getDecShift(), 
                                ruleSet.getAllowDuplicates(), ruleSet.getAllowTilde(), true);
                        }
                        debug (8, "Cooked value = '" + parmValueCooked + "'");
                        ruleSet.setCookedValue(parmValueCooked);
                        if (dataType.equals("N")) {
                            displayUnits = ruleSet.getDisplayUnits();
                            if (displayUnits.equalsIgnoreCase("None")) {
                                displayUnits = "";
                            }
                        }
                    } else if (dataType.equals("L")) {
                        parmValueCooked = parmValueRaw.equals("1") ? "Yes" : "No";
                    }
                                  
                    // Add to description string now
                
                    sBuffer.append("<li>");
                    sBuffer.append(parmName + ": ");
                    sBuffer.append(parmValueCooked);
                    if (!displayUnits.equals("")) {
                        sBuffer.append(" " + displayUnits);
                    }
                    sBuffer.append("</li>");
                }
                parmValuesCooked.add(parmValueCooked);
            }
            description = sBuffer.toString();
            initialized = true;
            debug (4, "Description is " + description.length() + " characters long.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean add(WDSconnect conn, String aPartNum, int aSeqNum, String aParmValueRaw, 
            String aAuditDate, String aAuditTimeRaw, String aAuditUserID)
        throws IOException, SQLException  {
        boolean completedOK = false;
        try {
            String SQLCommand = "INSERT INTO pub.ps_parm_data";
            SQLCommand += " (part_num, seq_num, parm_value, audit_date, audit_time_raw, audit_userid)";
            SQLCommand += " VALUES ( '" + aPartNum + "'," + aSeqNum + ", '" + aParmValueRaw + "','";
            SQLCommand += aAuditDate + "'," + aAuditTimeRaw + ",'" + aAuditUserID + "')";
            completedOK = conn.runUpdate(SQLCommand);
        } catch (Exception e) {
            e.printStackTrace();
            completedOK = false;
        } finally {
            return completedOK;
        }
    }    
        
    private void debug (int level, String x) {
        if (debugLevel >= level) {
            System.out.println(x);
        }
    }
    
    public static boolean delete(WDSconnect conn, String aPartNum)
        throws IOException, SQLException  {
        boolean completedOK = false;
        try {
            String SQLCommand = "DELETE ";
            SQLCommand += " FROM pub.ps_parm_data";
            SQLCommand += " WHERE part_num = '" + aPartNum + "'";
            completedOK = conn.runUpdate(SQLCommand);
        } catch (Exception e) {
            e.printStackTrace();
            completedOK = false;
        } finally {
            return completedOK;
        }
    }
       
    public static boolean exists(WDSconnect conn, String aPartNum) {
        boolean found = false;
        ResultSet rs = null;
        try {
            String queryString = "SELECT DISTINCT part_num";
            queryString += " FROM pub.ps_parm_data";
            queryString += " WHERE part_num = '" + aPartNum + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                found = rs.next();
                rs.close();
                conn.closeStatement();
                return found;
            }
            return false;          
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static String[] getArrayOfRawParms(WDSconnect conn, String aPartNum) {
        
        int i = 0;
        String queryString = "";
        String[] rawParms = new String[100];
        ResultSet rs;
        int seqNum = 0;
        Statement statement;
        
        for (i = 0; i < 100; i++) {
            rawParms[i] = "";
        }
        try {
            queryString = "SELECT seq_num, parm_value";
            queryString += " FROM pub.ps_parm_data";
            queryString += " WHERE part_num = '" + aPartNum + "'";
            queryString += " ORDER BY seq_num";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    seqNum = rs.getInt("seq_num");
                    if (seqNum > 0 && seqNum < 100) {
                        rawParms[seqNum] = rs.getString("parm_value");
                    }
                }
                statement = rs.getStatement();
                rs.close();
                statement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return rawParms;
        }
    }
  
    public String getDescription() {
        if (initialized) {
            return description;
        }
        return "<li>Error</li>";
    }
    public float getFinalScore(){
        return finalScore;
    }
            
    public String getParmValue(int aSeqNumber) {
        int i = seqNums.indexOf(Integer.toString(aSeqNumber));
        if (i > -1) {
            return (String) parmValuesRaw.get(i);
        }
        return "";
    }
    
    public String getParmValueCooked(int aSeqNumber) {
        int i = seqNums.indexOf(Integer.toString(aSeqNumber));
        if (i > -1) {
            return (String) parmValuesCooked.get(i);
        }
        return "";
    }
    
    public String getParmValueRaw(int aSeqNumber) {
        int i = seqNums.indexOf(Integer.toString(aSeqNumber));
        if (i > -1) {
            return (String) parmValuesRaw.get(i);
        }
        return null;
    }
    
    public String getPartNum(){
        return partNum;
    }
    
    private int getSeqNumIndex(String seqNum) {
        return seqNums.indexOf(seqNum);
    }
 
    public boolean read(WDSconnect conn, String aPartNum) 
        throws IOException, SQLException {
        
        Statement statement;
        
        partNum = aPartNum;
        ResultSet rs = null;
        seqNums.clear();
        parmValuesRaw.clear();
        parmValuesCooked.clear();
        try {
            rs = readResultSet(conn, partNum);
            if (rs == null) {
                return false;
            }
            while (rs.next()) {
                seqNums.add(rs.getString("seq_num"));
                parmValuesRaw.add(rs.getString("parm_value"));
            }
            statement = rs.getStatement();
            rs.close();
            statement.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (rs != null) {
                statement = rs.getStatement();
                rs.close();
                statement.close();
            }
            return false;
        }
    }
    
    public static String readParmValue(WDSconnect conn, String aPartNum, int aSeqNum) {
        String parmValue = "*** Not Found Error***";
        ResultSet rs;
        Statement statement;
        try {
            String queryString = "SELECT parm_value";
            queryString += " FROM pub.ps_parm_data";
            queryString += " WHERE part_num = '" + aPartNum + "'";
            queryString += " AND seq_num = " + aSeqNum;
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    parmValue = rs.getString("parm_value");
                }
                statement = rs.getStatement();
                rs.close();
                statement.close();
            } else {
                parmValue = "***SQL error***";
            }
        } catch (Exception e) {
            e.printStackTrace();
            parmValue = "***Unexpected Error***";
        } finally {
            return parmValue;
        }
    }
            
    public static ResultSet readResultSet(WDSconnect conn, String aPartNum) {
        ResultSet rs = null;
        try {
            String queryString = "SELECT seq_num, parm_value";
            queryString += " FROM pub.ps_parm_data";
            queryString += " WHERE part_num = '" + aPartNum + "'";
            queryString += " ORDER BY seq_num";
            rs = conn.runQuery(queryString);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return rs;
        }
    }
    
    public void setFinalScore(float value) {
        finalScore = value;
    }

    
}
