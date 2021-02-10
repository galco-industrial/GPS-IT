/*
 * GPSselectBox.java
 *
 * Created on November 1, 2006, 12:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

import OEdatabase.WDSconnect;
import java.util.*;
import java.sql.*;
import sauter.util.*;

/**
 *
 * @author Sauter
 *
 * Modified 07/19/2010 by DES to add support for the option_image field in the ps_select_boxes table.
 * Modified 10/05/2016 by DES to fix missing ';' in statement
 *                            work = "    aDefault[f] = \"" + optionDefault + "\";";
 *                            in getOptionList() ans getQOptionList() methods
 *
 */
public class GPSselectBox {
    private boolean debugSw = false;
    
    private String dataType = "";
    private int[] displayOrder = new int[1000];
    private String familyCode = "";
    private int imageCount = 0;
    private boolean isValid = false;
    private String minimum = "";
    private String maximum = "";
    private String optionDataType;
    private int optionDefault;
    private float optionMaximum;
    private int optionMaxLen;
    private float optionMinimum;
    private int optionMinLen;
    private String optionImage = "";
    private String[] optionImages = new String[1000];
    private String[] optionText = new String[1000];
    private String[] optionValue1 = new String[1000];
    private String[] optionValue2 = new String[1000];
    private String selectBoxName;
    private boolean showImages = false;
    private String subfamilyCode = "";
    private int size;
    private final String INVALID = "*invalid*"; 
    public static final int SELECT_BOX_CLONE_EXISTS = 2;
    public static final int SELECT_BOX_OK = 1;
    public static final int SELECT_BOX_EMPTY = 0;
    public static final int SELECT_BOX_DATABASE_ERROR = -1;
    public static final int SELECT_BOX_SIZE_OVERFLOW = -2;
    public static final int SELECT_BOX_NOT_FOUND = -3;
    public static final int SELECT_BOX_BAD_DATA_TYPE = -4;
    public static final int SELECT_BOX_BAD_DATA_MIN_MAX = -5;
    public static final int SELECT_BOX_INVALID_FORMAT = -6;
    public static final int SELECT_BOX_INVALID_ARGUMENT = -7;
    
    
    /** Creates a new instance of GPSselectBox */
    public GPSselectBox() {
    }
    
    public int cloneSelectBox(WDSconnect aConn, String aOldFamilyCode, String aOldSubfamilyCode,
            String aOldSelectBoxName, String aNewFamilyCode, String aNewSubfamilyCode, String aNewSelectBoxName)
        throws SQLException {
                
        boolean completedOK;
        String message = "";
        int rc = 0;
        String SQLCommand = "";
        
        // Should we check select box name, and family, subfamily names?
        
        if (aConn == null) {
            return SELECT_BOX_INVALID_ARGUMENT;
        }
        if (aOldFamilyCode == null || aOldFamilyCode.length() == 0) {
            return SELECT_BOX_INVALID_ARGUMENT;
        }
        if (aOldSubfamilyCode == null || aOldSubfamilyCode.length() == 0) {
            return SELECT_BOX_INVALID_ARGUMENT;
        }
        if (aOldSelectBoxName == null || aOldSelectBoxName.length() == 0) {
            return SELECT_BOX_INVALID_ARGUMENT;
        }
        if (aNewFamilyCode == null || aNewFamilyCode.length() == 0) {
            return SELECT_BOX_INVALID_ARGUMENT;
        }
        if (aNewSubfamilyCode == null || aNewSubfamilyCode.length() == 0) {
            return SELECT_BOX_INVALID_ARGUMENT;
        }
        if (aNewSelectBoxName == null || aNewSelectBoxName.length() == 0) {
            return SELECT_BOX_INVALID_ARGUMENT;
        }
            
        //   If the select box already exists for the new family/subfamily
        //       no new select box is created / return +2
        
        if (exists(aConn, aNewFamilyCode, aNewSubfamilyCode, aNewSelectBoxName)) {
            return SELECT_BOX_CLONE_EXISTS;
        }
        
        // Open the source select box. If any error occurs, return with the 
        // error code
        
        rc = open(aConn, aOldFamilyCode, aOldSubfamilyCode, aOldSelectBoxName);
        if (rc < 0) {
            return rc;
        }
        
        // If I get here, isValid should be true....
        
        if (!isValid) {
            message = " Database error opening select box " + aOldSelectBoxName + " for " + aOldFamilyCode + " - " + aOldSubfamilyCode;
            System.out.println(message);
            return SELECT_BOX_DATABASE_ERROR;
        }
        
        // Now create the select box header record for the new select box
        
        try {
            String dataTypeLong = "";
            if (dataType.substring(0,1).equalsIgnoreCase("S")) {
                dataTypeLong = "STRING";
            }
            if (dataType.substring(0,1).equalsIgnoreCase("N")) {
                dataTypeLong = "NUMERIC";
            }
            debug ("Attempting to clone Select Box " + aNewSelectBoxName);
            SQLCommand = "INSERT INTO pub.ps_select_boxes";
            SQLCommand += " (family_code, subfamily_code, select_box_name, option_index, option_text, option_dflt, option_value1, option_value2, option_image)";
            SQLCommand += " VALUES ( '" + aNewFamilyCode + "','" + aNewSubfamilyCode + "','" 
                    + aNewSelectBoxName + "', -1, '" + dataTypeLong + "', 0, '" + minimum + "', '" + maximum  + "', '" + optionImage + "')";
            completedOK = aConn.runUpdate(SQLCommand);
            message = "Select Box header record" + aNewSelectBoxName + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " cloned successfully for " + aNewFamilyCode + " - " + aNewSubfamilyCode;
            debug (message);
            if (completedOK) {
                for (int w = 0; w < size; w++) {
                    SQLCommand = "INSERT INTO pub.ps_select_boxes";
                    SQLCommand += " (family_code, subfamily_code, select_box_name, option_index, option_text, option_dflt, option_value1, option_value2, option_image)";
                    SQLCommand += " VALUES ( '" + aNewFamilyCode + "','" + aNewSubfamilyCode + "','" 
                        + aNewSelectBoxName + "', " + displayOrder[w] 
                        + ", '" + optionText[w] 
                        + "', " 
                        + (optionDefault == w ? "1" : "0")
                        + ",'" 
                        + optionValue1[w] + "', '" + optionValue2[w] + "', '" + optionImages[w] + "')";
                    completedOK = aConn.runUpdate(SQLCommand);
                    if (!completedOK) {
                        message = " Database error creating option " + w + " for select box " + aNewSelectBoxName + " for " + aNewFamilyCode + " - " + aNewSubfamilyCode;
                        System.out.println(message);
                        return SELECT_BOX_DATABASE_ERROR;
                    }
                }
                message = " Select box " + aNewSelectBoxName + " for " + aNewFamilyCode + " - " + aNewSubfamilyCode + " successfully cloned.";
                debug(message);
                return SELECT_BOX_OK;
            } else {
                message = " Database error creating select box " + aNewSelectBoxName + " for " + aNewFamilyCode + " - " + aNewSubfamilyCode;
                System.out.println(message);
                return SELECT_BOX_DATABASE_ERROR;
            }
        } catch (Exception e) {
            System.out.println("Database error while attempting Select Box Clone Operation.");
            e.printStackTrace();
            return SELECT_BOX_DATABASE_ERROR;
        }
    }
        
    public static String cookedToRaw(WDSconnect conn, String aFamilyCode, 
            String aSubfamilyCode, String aSelectBoxName, String aOptionText) {
        ResultSet rs = null;
        String result = "***Not Found Error***";
        try {
            aFamilyCode = aFamilyCode.toUpperCase();
            aSubfamilyCode = aSubfamilyCode.toUpperCase();
            aSelectBoxName = aSelectBoxName.toUpperCase();
            String queryString = "SELECT option_value1";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE select_box_name = '"  + aSelectBoxName + "'";
            queryString += " AND family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " AND option_text = '" + aOptionText + "'";
            queryString += " AND option_index <> -1";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    result = rs.getString("option_value1");
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                return result;
            }
            return "***SQL ERROR***";
        } catch (Exception e) {
            e.printStackTrace();
            return "***Unexpected ERROR***";
        }
    }
        
    private void debug (String x) {
        if (debugSw) {
            System.out.println(x);
        }
    }
    
     public boolean exists(WDSconnect conn, String aFamilyCode, String aSubfamilyCode, String aSelectBoxName) {
        ResultSet rs = null;
        familyCode = aFamilyCode;
        subfamilyCode = aSubfamilyCode;
        try {
            String queryString = "SELECT * ";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE select_box_name = '"  + aSelectBoxName + "'";
            queryString += " AND family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " AND option_index = -1";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    rs.close();
                    rs = null;
                    conn.closeStatement();
                    return true;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
   
    public ArrayList <String> getArrayList()
        throws SQLException {
        
        // I return an array list containing the options for this select.
        
        ArrayList <String> options = new ArrayList <String> ();
        try {
            if (!isValid) {
                return null;  // if select box contents is not valid, return null
            }
            String work;
            for (int k = 0; k < size; k++) {
                work = "\"" + selectBoxName
                        + "\"," + displayOrder[k] 
                        + ",\"" + optionText[k] 
                        + "\",\"" + optionValue1[k]
                        + "\",\"" + optionValue2[k];
                if (optionDefault == k) {
                    work +=  "\",\"default\"";
                } else {
                    work +=  "\",\"\"";
                }
                work += ",\"" + optionImages[k] + "\"";
                options.add(work);    
            }
            return options;
        } catch (Exception e) {
            e.printStackTrace();
            debug ("Something ugly happened when GPSselectBox.java tried to put the options in an ArrayList.");
            return null;
        }
    }
    
    
    public String getDataType() {
        if (isValid) {
            return optionDataType;
        }
        return INVALID;
    }
    
    public int getDisplayOrder(int index) {
        // returns Display Order of Option
        if (isValid && index > -1 && index < size) {
            return displayOrder[index];
        }
        return -1;
    }
    
    public int getDisplayOrderIndexOf(int order) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (displayOrder[i] == order) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int getDisplayOrderIndexOf(String order) {
        int iOrder = Integer.parseInt(order);
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (displayOrder[i] == iOrder) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public String getFamilyCode() {
        if (isValid) {
            return familyCode;
        }
        return INVALID;
    }
    
    public int getImageCount() {
        if (isValid) {
            return imageCount;
        }
        return -1;
    }
    
    public String getMaximum() {
        if (isValid) {
            return maximum;
        }
        return INVALID;
    }
    
    public String getMinimum() {
        if (isValid) {
            return minimum;
        }
        return INVALID;
    }
        
    public int getOptionDefault() {
        if (isValid) {
            return optionDefault;
        } else {
            return -1;
        }
    }
       
    public String getOptionImage(int index) {
        if (isValid && index > -1 && index < size) {
            return optionImages[index];
        } else {
            return INVALID;
        }
    }
        
    public ArrayList <String> getOptionList() {
        if (isValid) {
            ArrayList <String> optionList = new ArrayList <String> ();
            String work = "";
            optionList.add( "   x = 0;" );
            optionList.add( "   aDEOptions[f] = new Array();" );
            work = "  aDEOptions[f] [x++] = new Array(\"\",\"Select from list\");";
            optionList.add( work );
            for (int i = 0; i < size; i++) {
                work = "  aDEOptions[f] [x++] = new Array(\"" + optionValue1 [i] + "\",\"" + 
                        optionText [i] + "\",\"" + optionImages [i] + "\");";
                optionList.add( work );
            }
            work = "    aDefault[f] = \"" + optionDefault + "\";";
            optionList.add( work );
            work = "    aDEOptionsSize[f] = x;";
            optionList.add( work );
            return optionList;
        } else {
            return null;
        }
    }
    
    public ArrayList <String> getQOptionList() {
        if (isValid) {
            ArrayList <String> optionList = new ArrayList <String> ();
            String work = "";
            optionList.add( "   x = 0;" );
            optionList.add( "   aQOptions[f] = new Array();" );
            work = "  aQOptions[f] [x++] = new Array(\"\",\"Select from list\");";
            optionList.add( work );
            for (int i = 0; i < size; i++) {
                work = "  aQOptions[f] [x++] = new Array(\"" + optionValue1 [i] + "\",\"" + 
                        optionText [i] + "\",\"" + optionImages [i] + "\");";
                optionList.add( work );
            }
            work = "    aDefault[f] = \"" + optionDefault + "\";";
            optionList.add( work );
            work = "    aQOptionsSize[f] = x;";
            optionList.add( work );
            return optionList;
        } else {
            return null;
        }
    }
       
    public String getOptionText(int index) {
        if (isValid && index > -1 && index < size) {
            return optionText[index];
        } else {
            return INVALID;
        }
    }
    
    public String getOptionValue1(int index) {
        if (isValid && index > -1 && index < size) {
            return optionValue1[index];
        } else {
            return INVALID;
        }
    }
    
    public String getOptionValue2(int index) {
        if (isValid && index > -1 && index < size) {
            return optionValue2[index];
        } else {
            return INVALID;
        }
    }
    
     public ArrayList <String> getRuleReferences(WDSconnect conn, String aFamilyCode, String aSubfamilyCode, String aSelectBoxName) {
        
        // I return an array list containing the rules that have active references
        // to this select box name.
        
        ResultSet rs = null;
        ArrayList <String> references = new ArrayList <String> ();
        try {
            if (!isValid) {
                return null;  // if select box name is not valid, return null
            }
            String work;
            //int k = 0;
            String queryString = "SELECT family_code, subfamily_code, seq_num, rule_scope ";
            queryString += " FROM pub.ps_rules";
            queryString += " WHERE (q_select_box_name = '"  + aSelectBoxName + "'";
            queryString += " OR de_select_box_name = '"  + aSelectBoxName + "') ";
            queryString += " AND family_code = '"  + aFamilyCode + "' ";
            queryString += " AND subfamily_code = '"  + aSubfamilyCode + "' ";
            queryString += " ORDER BY seq_num, rule_scope";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    // Process references here
                    work = "\"" + rs.getString("family_code") 
                        + "\",\"" + rs.getString("subfamily_code") 
                        + "\",\"" + rs.getInt("seq_num") 
                        + "\",\"" + rs.getString("rule_scope")
                        + "\"";
                    references.add(work);    
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                return references;
            } //  end if (rs != null) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            debug ("Something ugly happened when GPSselectBox.java tried to find select box references in the rules table.");
            return null;
        }
    }
    
    public String getSelectBoxName() {
        if (isValid) {
            return selectBoxName;
        }
        return INVALID;
    }
         
    public String getSubfamilyCode() {
        if (isValid) {
            return subfamilyCode;
        }
        return INVALID;
    }
    
    public int open(WDSconnect conn, String aFamilyCode, String aSubfamilyCode, String aSelectBoxName) {
        ResultSet rs = null;
        familyCode = aFamilyCode;
        subfamilyCode = aSubfamilyCode;
        try {
            isValid = false;
            String work;
            size = 0;
            optionDefault = 0;
            String queryString = "SELECT * ";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE select_box_name = '"  + aSelectBoxName + "'";
            queryString += " AND family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " ORDER BY option_index";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    
                    // Process Select Box Header here
                    
                    if (rs.getInt("option_index") == -1) {
                        optionDataType = rs.getString("option_text");
                        if (optionDataType.length() == 0) {
                            rs.close();
                            rs = null;
                            conn.closeStatement();
                            return SELECT_BOX_BAD_DATA_TYPE;
                        }
                        optionDataType = optionDataType.toUpperCase();
                        dataType = optionDataType.substring(0,1);
                        if ("NSLD".indexOf(dataType) == -1) {
                            rs.close();
                            rs = null;
                            conn.closeStatement();
                            return SELECT_BOX_BAD_DATA_TYPE;
                        }
                        minimum = rs.getString("option_value1");
                        maximum = rs.getString("option_value2");
                        optionImage = rs.getString("option_image");
                        showImages = optionImage.toUpperCase().equals("SHOW");
                        
                        // The following is temporarily disabled by the X
                        // when supported change the X to a N
                        if (dataType.equals("X")) {
                            work = rs.getString("option_value1");
                            if (Convert.isFloat(work)) {
                                optionMinimum = Float.parseFloat(work);
                            } else {
                                rs.close();
                                rs = null;
                            conn.closeStatement();
                                return SELECT_BOX_BAD_DATA_MIN_MAX;
                            }
                            work = rs.getString("option_value2");
                            if (Convert.isFloat(work)) {
                                optionMaximum = Float.parseFloat(work);
                            } else {
                                rs.close();
                                rs = null;
                                conn.closeStatement();
                                return SELECT_BOX_BAD_DATA_MIN_MAX;
                            }
                        }  // if (dataType.equals("X")) {
                        
                        //This is temporarily disabled too; replace X with S later
                        if (dataType.equals("X")) {
                            work = rs.getString("option_value1");
                            if (Convert.isInteger(work)) {
                                optionMinLen = Integer.parseInt(work);
                            } else {
                                rs.close();
                                rs = null;
                                conn.closeStatement();
                                return SELECT_BOX_BAD_DATA_MIN_MAX;
                            }
                            work = rs.getString("option_value2");
                            if (Convert.isInteger(work)) {
                                optionMaxLen = Integer.parseInt(work);
                            } else {
                                rs.close();
                                rs = null;
                                conn.closeStatement();
                                return SELECT_BOX_BAD_DATA_MIN_MAX;
                            }
                        } //  end if (dataType.equals("X")) {
                    } else {
                        // We get here if there was no valid header rec
                        rs.close();
                        rs = null;
                        conn.closeStatement();
                        return SELECT_BOX_INVALID_FORMAT;
                    } // if (rs.getInt("option_index") == -1) {
                } else {
                    rs.close();
                    rs = null;
                    conn.closeStatement();
                    return SELECT_BOX_NOT_FOUND;
                } //if (rs.next()) {
                
                // Process select box options here
                
                while (rs.next()) {
                    optionText[size] = rs.getString("option_text");
                    optionValue1[size] = rs.getString("option_value1");
                    optionValue2[size] = rs.getString("option_value2");
                    optionImages[size] = rs.getString("option_image").trim();;
                    displayOrder[size] = rs.getInt("option_index");
                    if (rs.getBoolean("option_dflt")) {
                        optionDefault =  size;
                    }
                    if (!optionImages[size].equals("")) {
                        imageCount++;
                    }
                    size++;
                }  // end while (rs.next()) {
            } //  end if (rs != null) {
            
            rs.close();
            rs = null;
            conn.closeStatement();
            isValid = true;
            if (imageCount == 0) {
                showImages = false;
            }
            selectBoxName = aSelectBoxName;
            if (size == 0 ) {
                return SELECT_BOX_EMPTY;
            }
            return SELECT_BOX_OK;
        } catch (Exception e) {
            e.printStackTrace();
            return SELECT_BOX_DATABASE_ERROR;
        }
    }
    
    public boolean optionTextExists(String item) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (optionText[i].equals(item) ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean optionTextExists(int item) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (Integer.parseInt(optionText[i]) == item) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean optionTextExists(float item) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (Float.parseFloat(optionText[i]) == item) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int optionTextIndexOf(String item) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (optionText[i].equals(item) ) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public boolean optionValue1Exists(String item) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (optionValue1[i].equals(item) ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean optionValue1Exists(int item) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (Integer.parseInt(optionValue1[i]) == item) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean optionValue1Exists(float item) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (Float.parseFloat(optionValue1[i]) == item) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int optionValue1IndexOf(String item) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (optionValue1[i].equals(item) ) {
                    return i;
                }
            }
        }
        return -1;
    }
     
    public boolean optionValue2Exists(String item) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (optionValue2[i].equals(item) ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean optionValue2Exists(int item) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (Integer.parseInt(optionValue2[i]) == item) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean optionValue2Exists(float item) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (Float.parseFloat(optionValue2[i]) == item) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int optionValue2IndexOf(String item) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (optionValue2[i].equals(item) ) {
                    return i;
                }
            }
        }
        return -1;
    }  
    
    public static String rawToCooked(WDSconnect conn, String aFamilyCode, 
            String aSubfamilyCode, String aSelectBoxName, String aOptionValue1) {
        ResultSet rs = null;
        String result = "***Not Found Error***";
        try {
            aFamilyCode = aFamilyCode.toUpperCase();
            aSubfamilyCode = aSubfamilyCode.toUpperCase();
            aSelectBoxName = aSelectBoxName.toUpperCase();
            
            String queryString = "SELECT option_text ";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE select_box_name = '"  + aSelectBoxName + "'";
            queryString += " AND family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            
            queryString += " AND option_value1 = '" + aOptionValue1 + "'";
            queryString += " AND option_index <> -1";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    result = rs.getString("option_text");
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                return result;
            }
            return "***SQL ERROR***";
        } catch (Exception e) {
            e.printStackTrace();
            return "***Unexpected ERROR***";
        }
    }
    
    public boolean showImages() {
        if (isValid) {
            return showImages;
        }
        return false;
    }
    
    public int size() {
        if (isValid) {
            return size;
        } else {
            return -1;
        }
    }
}
