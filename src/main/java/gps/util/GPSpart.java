/*
 * GPSpart.java
 *
 * Created on October 30, 2006, 5:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * 04/26/2011 DES added support for audit date/time/userid
 *
 */

package gps.util;

import OEdatabase.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import sauter.util.*;
import XML.util.*;

/**
 *
 * @author Sauter
 */
public class GPSpart {
    
    private boolean debugSw = false;
    private static final String version = "1.5.01";
    
    private String auditDate = "";
    private String auditTime = "";
    private String auditUserID = "";
    private String description;
    private String description2;
    private String description3;
    private String dimensionsHeight;
    private String dimensionsWidth;
    private String dimensionsDepth;    
    private String familyCode = "";
    private String familyName = "";
    private String hashedPartNum;
    private boolean hasPSData;
    private boolean isValid=false;
    private String[] parmValuesCooked = new String[100];
    private int parmValuesCount = 0;
    private String[] parmValuesFieldName = new String[100];
    private int[] parmValuesMap = new int[100];
    private String[] parmValuesRaw = new String[100];
    private String[] parmValuesUnits = new String[100];
    private String partNum;
    private String salesCat;
    private String salesSubcat;
    private String series;
    private String subfamilyCode = "";
    private String subfamilyName = "";
    private String weightGross;
    private String weightNet;
    private String weightUnit;
    private String WombatSeculorum;
    
   
       
    /** Creates a new instance of GPSpart */
    public GPSpart() {
        setAuditDate(DateTime.getDateMMDDYY());
        setAuditTime(DateTime.getTimeHHMMSS(":"));
        setAuditUserID("gps");
    }
 
    public static boolean doesPSDataExist(WDSconnect conn, String aFamilyCode, String aSubfamilyCode) {
        
        // aSubfamilyCode must contain an "*" for Global Rules
        
        boolean rc = false;
        ResultSet rs = null;
        String queryString = "SELECT TOP 1 part_num";
        queryString += " FROM pub.part";
        queryString += " WHERE family_code = '" + aFamilyCode +"' ";
        queryString += " AND has_ps_data = '1' ";
        if (!aSubfamilyCode.equals("*")) {
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
        }
        try {
            rs = conn.runQuery(queryString);
            if (rs != null) {
                rc = rs.next();
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            return rc;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error retrieving data from part table.");
            return false;
        }
   }
    
    public static boolean exists(WDSconnect conn, String aPartNum) {
        boolean rc = false;
        ResultSet rs = null;
        try {
            if (aPartNum == null) {return false;}
            if (aPartNum.length() == 0) { return false;}
            aPartNum = aPartNum.toUpperCase();
            String queryString = "SELECT part_num";
            queryString += " FROM pub.part";
            queryString += " WHERE part_num = '" + aPartNum + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                rc = rs.next();
                rs.close();
                rs = null;
                conn.closeStatement();
                return rc;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean exists(WDSconnect conn, String aFamilyCode, 
        String aSubfamilyCode, String aMfgrCode) {
        boolean rc = false;
        ResultSet rs = null;
        try {
            //if (aFamilyCode == null) {return false;}
            //if (aFamilyCode.length() == 0) { return false;}
            String queryString = "SELECT TOP 1 part_num";
            queryString += " FROM pub.part";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " AND sales_subcat = '" + aMfgrCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                rc = rs.next();
                rs.close();
                rs = null;
                conn.closeStatement();
                return rc;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean exists(WDSconnect conn, String aFamilyCode, 
        String aSubfamilyCode, String aMfgrCode, String aSeriesCode) {
        boolean rc = false;
        ResultSet rs = null;
        try {
            //if (aFamilyCode == null) {return false;}
            //if (aFamilyCode.length() == 0) { return false;}
            String queryString = "SELECT TOP 1 part_num";
            queryString += " FROM pub.part";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " AND sales_subcat = '" + aMfgrCode + "'";
            queryString += " AND series = '" + aSeriesCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                rc = rs.next();
                rs.close();
                rs = null;
                conn.closeStatement();
                return rc;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static long getAvailable(WDSconnect conn, String partNum) {
        ResultSet rs = null;
        String queryString  = "";
        long available = 0;
        
        try {
            queryString = "SELECT location, qty_onhand, qty_commit, qty_backord";
            queryString += " FROM pub.partloc";
            queryString += " WHERE part_num = '" + partNum + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    available += rs.getLong("qty_onhand") - rs.getLong("qty_commit") - rs.getLong("qty_backord");
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
        } catch (Exception e) {
            e.printStackTrace();
            available = -1;
        } finally {
            return available;
        }
    }
    
    public static GPSpartsAvailable getAvailableArray(WDSconnect conn, String familyCode, String subfamilyCode,
            String mfgrCode, String seriesCode) {
        int available = 0;
        String partNum = "";
        String queryString  = "";
        String previousPartNum = "";
        GPSpartsAvailable results = new GPSpartsAvailable();
        ResultSet rs = null;
        
        try {
            queryString = "SELECT p.part_num, v.location, v.qty_onhand, v.qty_commit, v.qty_backord";
            queryString += " FROM pub.part p, pub.partloc v";
            queryString += " WHERE p.family_code = '" + familyCode + "'";
            queryString += " AND p.subfamily_code = '" + subfamilyCode + "'";
            queryString += " AND p.part_num = v.part_num";
            queryString += " AND p.has_ps_data = 1";
            if (!mfgrCode.equals("")) {
                queryString += " AND p.sales_subcat = '" + mfgrCode + "'";
            }
            if (!seriesCode.equals("")) {
                queryString += " AND p.series = '" + seriesCode + "'";
            }
            queryString += " ORDER BY p.part_num";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    partNum = rs.getString("part_num").toUpperCase();
                    if (previousPartNum.equals(partNum)) {
                        available += rs.getInt("qty_onhand") - rs.getInt("qty_commit") - rs.getInt("qty_backord");
                    } else {
                        if (!previousPartNum.equals("") && available > 0) {
                            results.addPartsAvailable(previousPartNum, available);
                        }
                        previousPartNum = partNum;
                        available = rs.getInt("qty_onhand") - rs.getInt("qty_commit") - rs.getInt("qty_backord");
                    }
                }
                if (!previousPartNum.equals("") && available > 0) {
                    results.addPartsAvailable(previousPartNum, available);
                } 
                rs.close();
                rs = null;
                conn.closeStatement();
            }
        } catch (Exception e) {
            e.printStackTrace();

            results = null;
        } finally {
            return results;
        }
    }
        
    public String getDescription() {
        return description;
    }
        
    public String getDescription2() {
        return description2;
    }
        
    public String getDescription3() {
        return description3;
    }
    
    public String getDimensionsHeight() {
        return dimensionsHeight;
    }
      
    public String getDimensionsDepth() {
        return dimensionsDepth;
    }
            
    public String getDimensionsLWH() {
        String lwh = "";
        if (Float.parseFloat(dimensionsDepth) > 0
            && Float.parseFloat(dimensionsWidth) > 0
            && Float.parseFloat(dimensionsHeight) > 0) {
                lwh = dimensionsDepth + " X "
                    + dimensionsWidth + " X "
                    + dimensionsHeight;
                
        }
        return lwh;
    }
        
    public String getDimensionsHWD() {
        String hwd = "";
        if (Float.parseFloat(dimensionsDepth) > 0
            && Float.parseFloat(dimensionsWidth) > 0
            && Float.parseFloat(dimensionsHeight) > 0) {
                hwd = dimensionsHeight + " X "
                    + dimensionsWidth + " X "
                    + dimensionsDepth;
                
        }
        return hwd;
    }
    
    public String getDimensionsLength() {
        return dimensionsDepth;
    }
    
    public String getDimensionsWidth() {
        return dimensionsWidth;
    }
    
    public String getFamilyCode() {
        return familyCode;
    }
    
    public String getHashedPartNum() {
        return hashedPartNum;
    }

    public boolean getHasPSData() {
        return hasPSData;
    }
    
    public static String getInStock(WDSconnect conn, String partNum) {
        //
        // I differ from getAvailable() in that I return a location and qty in stock
        // regardless of quantities committed or back ordered.
        // If multiple locations have stock, I concatenate the results
        
        long available = 0;
        String item = "";
        String queryString  = "";
        ResultSet rs = null;
        String stock = "";
        try {
            queryString = "SELECT location, qty_onhand, qty_commit, qty_backord, normal_binnum";
            queryString += " FROM pub.partloc";
            queryString += " WHERE part_num = '" + partNum + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    available = rs.getLong("qty_onhand");
                    item = "[" + rs.getString("location") + "/" + rs.getString("normal_binnum") + "] = " + available;
                    if (stock.length() != 0) {
                        stock += "; ";
                    }
                    stock += item;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stock = "*** Error ***";
        } finally {
            return stock;
        }
    }
            
    public static String getManCodesXMLList(WDSconnect conn, String aFamilyCode, 
            String aSubfamilyCode) {
        String result = "";
        String work = "";
        String code = "";
        String name = "";
        ResultSet rs = null;
        try {
            String queryString = "SELECT DISTINCT sales_subcat";
            queryString += " FROM pub.part";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            if (!aSubfamilyCode.equals("*")) {
                queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            }
            queryString += " ORDER BY sales_subcat";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while(rs.next()) {
                    code = rs.getString("sales_subcat");
                    name = code; // getManName(conn, code);
                    name = Node.textNode("name", code + "-" + name);
                    code = Node.textNode("code", code);
                    result += Node.textNode("manufacturer", code + name);
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            result =  Node.XML_HEADER + Node.textNode("manufacturers", result);
            //System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String getManName(WDSconnect conn, String aManCode) {
        String name = "*UNDEFINED*";
        ResultSet rs = null;
        try {
            String queryString = "SELECT description";
            queryString += " FROM pub.codes_s";
            queryString += " WHERE valid_code = '" + aManCode + "'";
            queryString += " AND code_type = 'MANUFACTURER'";
            System.out.println (queryString);
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if(rs.next()) {
                    name = rs.getString("description");
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return name;
        }
    }
    
    public String getParmBySeqNum(int index) {
        String result = "***INVALID***";
        String work = "";
        if (parmValuesCooked[index] != null) {
            result = parmValuesFieldName[index] + ": " + parmValuesCooked[index];
            work = parmValuesUnits[index];
            if (!work.equals("")) {
                result += " " + work;
            }
        }
        return result;
    }
    
    public String getParmInOrder(int index, boolean suppressBlanks) {
        String result = "***INVALID***";
        String work = "";
        index = parmValuesMap[index];
        if (index > -1) {
            if (suppressBlanks) {
                work = parmValuesCooked[index];
                if (work.equals("")) {
                    return "";
                }
            }
            result = parmValuesFieldName[index] + ": " + parmValuesCooked[index];
            work = parmValuesUnits[index];
            if (!work.equals("")) {
                result += " " + work;
            }
        }
        return result;
    }
    
    public int getParmValuesCount() {
        return parmValuesCount;
    }
        
    public static String getPartImageStatus(WDSconnect conn, WWWconnect conn3, 
            String aPartNum, String aMfgrCode, String aSeries,
            int aParmField, String aFamilyCode, String aSubfamilyCode) {
        
        // aPartNum is stripped of the -MfgrCode
        
        // If a part number has a current part image, return "Part"
        // If no Part Image is available, but a family/subfamily image parm field #
        // is defined, try to look it up.
        // If found return "Family/Subfamily"
        // else return name of F/S file not found
        // If no part image or F/S image, see if current Series image exists,
        // if it does, return "Series"
        // if no Series, try concatenating the family and subfamily codes to the series
        // If no Series image exists but a legacy image is found, return "Legacy"
        // else if none of the above, return "None"
             
        int iPtr = 0;
        String fullPartNum = aPartNum + "-" + aMfgrCode;
        String parmValue = "";
        String partBase = "";
        String partPicture = "";
        String seriesBase = "";
        String stdPart = "";
        
        // First check if there is a part image
        
        stdPart = aPartNum.trim().toLowerCase().replace(" ","_").replace("/","_");
        partBase = "/images/" + aMfgrCode.toLowerCase() + "/" + stdPart;
        partPicture = partBase + "_p.jpg";
        if (imageExists(partPicture)) {
            return "Part=> " + partPicture;
        }
        
        // ADD code here to check to see if a family/subfamily image is available
        // if the parm field number is non-zero
        // Be wary of local rules that might override a global rule
        // 
        
        if (aParmField > 0) {       // if a parm value exists for this field,
            parmValue = "";
            ResultSet rs = null;
            try {
                String queryString = "SELECT parm_value";
                queryString += " FROM pub.ps_parm_data";
                queryString += " WHERE part_num = " + "'" + fullPartNum + "'";
                queryString += " AND seq_num = " + aParmField;
                rs = conn.runQuery(queryString);
                if (rs != null) {
                    if (rs.next()) {
                        parmValue = rs.getString("parm_value");
                    }
                    rs.close();
                    rs = null;
                    conn.closeStatement();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "***SQL Error***";
            }
            parmValue = parmValue.trim().toLowerCase().replace(" ","_").replace("/","_");
            if (parmValue.length() > 0) {
                parmValue = "/images/" + aMfgrCode.toLowerCase() + "/" + parmValue + "_p.jpg";
                if (imageExists(parmValue)) {
                    return "F/S=> " + parmValue;
                } else {
                    return "F/S=> " + parmValue + " **NOF**";
                }
            } //else {
              //  return "Pending";
            // }
        }
        
        // If no part picture and no family/subfamily picture
        // check to see if a series image is available
        
        seriesBase = aSeries.trim();
        if (seriesBase.length() > 0) {
            seriesBase = seriesBase.toLowerCase().replace(" ","_").replace("/","_");
            partPicture = "/images/" + aMfgrCode.toLowerCase() + "/" 
                    + seriesBase  + "_1.jpg";
            if (imageExists(partPicture)) {
                return "Series=> " + partPicture;
            }
            partPicture = "/images/" + aMfgrCode.toLowerCase() + "/"
                    + aFamilyCode.toLowerCase() + "_" + aSubfamilyCode.toLowerCase() + "_"
                    + seriesBase  + "_1.jpg";
            if (imageExists(partPicture)) {
                return "Series=> " + partPicture;
            }
        }
        
        // Finally if there was no part or series image, check to see if a legacy image exists
        
        partPicture = partBase + ".jpg";
        if (imageExists(partPicture)) {
            return "Legacy=> " + partPicture;
        }
        
        partPicture = partBase + ".gif";
        if (imageExists(partPicture)) { 
            return "Legacy=> " + partPicture;
        }
        
        // Check legacy links here to see if a legacy link exists!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //
        
        partPicture = "";
        ResultSet rs3 = null;
        try {
            String queryString = "SELECT img_location";
            queryString += " FROM pub.part_img";
            queryString += " WHERE part_num = " + "'" + fullPartNum + "'";
            queryString += " AND img_type = 'Picture'";
            rs3 = conn3.runQuery(queryString);
            if (rs3 != null) {
                if (rs3.next()) {
                    partPicture = rs3.getString("img_location");
                }
                rs3.close();
                rs3 = null;
                conn3.closeStatement();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "***SQL Error***";
        }
        
        if (partPicture.length() != 0) {
            partPicture = "/" + partPicture.toLowerCase();
            if (imageExists(partPicture)) { 
                return "Legacy=> " + partPicture;
            }
        }

        // Otherwise no images were found
        
        return "None";
    }
       
    public String getPartNum() {
        return partNum;
    }
    
    public String getSalesCat() {
        return salesCat;
    }
    
    public String getSalesSubcat() {
        return salesSubcat;
    }
    
    public String getSeries() {
        return series;
    }
    
    public String getSubfamilyCode() {
        return subfamilyCode;
    }
    
    public String getWeightGross() {
        return weightGross;
    }
    
    public String getWeightNet() {
        return weightNet;
    }
        
    public String getWeightUnit() {
        return weightUnit;
    }
    
    private static boolean imageExists(String image) {
        String imagePath = "/var/www/www.galco.com/htdocs";
        String imageFileName = imagePath + image;
        boolean exists;
        File name = new File(imageFileName);
        exists = name.exists();
        name = null;
        return exists;
    }
    
    public boolean read(WDSconnect con2, String aPartNum) {
        isValid = false;
        ResultSet rs = null;
        try {
            if (aPartNum == null) {return false;}
            if (aPartNum.length() == 0) { return false;}
            aPartNum = aPartNum.toUpperCase();
            String queryString = "SELECT description, description2, description3, ";
            queryString += " family_code, hashed_part_num, has_ps_data, part_num,";
            queryString += " sales_cat, sales_subcat, series, subfamily_code,";
            queryString += " gross_weight, net_weight, weight_unit,";
            queryString += " p_height, p_width, p_depth";
            queryString += " FROM pub.part";
            queryString += " WHERE part_num = '" + aPartNum + "'";
            rs = con2.runQuery(queryString);   
            if (rs != null) {
                if ( rs.next() ) {
                    description = rs.getString("description");
                    description2 = rs.getString("description2");
                    description3 = rs.getString("description3");
                    dimensionsHeight = rs.getString("p_height");
                    dimensionsDepth = rs.getString("p_depth");
                    dimensionsWidth = rs.getString("p_width");
                    familyCode = rs.getString("family_code");
                    hashedPartNum = rs.getString("hashed_part_num");
                    hasPSData = rs.getBoolean("has_ps_data");
                    partNum = rs.getString("part_num");
                    salesCat = rs.getString("sales_cat");
                    salesSubcat = rs.getString("sales_subcat");
                    series = rs.getString("series");
                    subfamilyCode = rs.getString("subfamily_code");
                    weightGross = rs.getString("gross_weight");
                    weightNet = rs.getString("net_weight");
                    weightUnit = rs.getString("weight_unit");
                    rs.close();
                    rs = null;
                    con2.closeStatement();
                    if (!familyCode.equals("")) {
                        familyName = GPSfamilyCodes.lookUpFamilyName(con2, familyCode);
                    }
                    if (!subfamilyCode.equals("")) {
                        subfamilyName = GPSsubfamilyCodes.lookUpSubfamilyName(con2, familyCode, subfamilyCode);
                    }
                    for (int i = 0; i < 100; i++) {
                        parmValuesFieldName[i] = "";
                        parmValuesRaw[i] = null;
                        parmValuesCooked[i] = null;
                        parmValuesUnits[i] = "";
                        parmValuesMap[i] = -1;
                    }
                    parmValuesCount = 0;
                    isValid = true;
                    return isValid;
                }
                rs.close();
                rs = null;
                con2.closeStatement();
            }
            isValid = false;
            return isValid;
        } catch (Exception e) {
            e.printStackTrace();
            isValid = false;
            return isValid;
        }
    }
    
    /** I read the parametric data for this part instance
     *
     **/
    public boolean readParmData(WDSconnect conn, String order) {
        GPScvt cvt;
        String dataType = "";
        String errorMessage = "";
        String parmValueCooked = "";
        String parmValueRaw = "";
        int rc = 0;
        String ruleSubfamilyCode = "";
        GPSselectBox selectBox;
        String selectBoxName = "";
        int seqNum = 0;
        GPSunit units;
        String work = "";

        if (!isValid) {
            return false;
        }
        if (!hasPSData) {
            return false;
        }
        
        try {
            if (parmValuesCount > 0) { 
                for (int i = 0; i < 100; i++) {
                    parmValuesFieldName[i] = "";
                    parmValuesRaw[i] = null;
                    parmValuesCooked[i] = null;
                    parmValuesUnits[i] = "";
                    parmValuesMap[i] = -1;
                }
                parmValuesCount = 0;
            }
            GPSfieldSet fieldSet = new GPSfieldSet();
            GPSrules[] ruleSets = fieldSet.getRules(conn, familyCode, subfamilyCode, order);
            for (int i = 0; i < fieldSet.size(); i++)  {
                GPSrules ruleSet = ruleSets[i];

                // Check the family code for this Part Number against the Rules
                    
                if (!ruleSet.getFamilyCode().equals(familyCode)) {
                    errorMessage = "Unexpected Error getting parm data - Family code mismatch for PN " + partNum;
                    debug (errorMessage);
                    return false;
                }
             
                ruleSubfamilyCode = ruleSet.getSubfamilyCode().trim();
                //(Note that rulesubfamilyCode could be diferent than part subfamilyCode if
                //        we have a global rule here)
                  
                //      look up any units data we need for numeric items
                //      load any select boxes we need fo string and numeric items
                        
                seqNum = ruleSet.getSeqNum();
                dataType = ruleSet.getDataType();
                debug ("Processing ruleset for seq num " + seqNum + "; Field name: " + ruleSet.getParmName());
                if (dataType.equals("N") ) {
                    // Look up and set Display units info here
                    units = new GPSunit();
                    units.open(conn, ruleSet.getDisplayUnits());
                    ruleSet.setDecShift(units.getMultiplierExp());
                    units = null;
                }
                if ( "SN".contains(dataType)) {    
                    // If a DE select box exists...
                    // try to load a select box object for it
                    selectBoxName = ruleSet.getDeSelectBoxName();
                    if (ruleSet.getDeTextBoxSize() == 0 && !selectBoxName.equals("") ) {
                        debug ("I am going to try to open Select Box named " + selectBoxName);
                        selectBox = new GPSselectBox();  // create a new select box object
                        rc = selectBox.open(conn, familyCode, ruleSubfamilyCode, selectBoxName);
                        if (rc < 0) {
                            errorMessage = "Unexpected error " + rc + " opening Select Box " + selectBoxName;
                            debug (errorMessage);   
                            return false;
                        }
                        ruleSet.setDeSelectBox(selectBox);
                        debug ("Created and stored the select box object.");
                    } else {
                        ruleSet.setDeSelectBox(null);
                        debug ("There is no Select Box for seq num " + seqNum);
                    }
                }
                debug ("Now converting raw value to cooked for field seq num " + seqNum);
                    
                cvt = new GPScvt();  // We need this class to cook raw values
                    
                parmValueRaw = GPSparmSet.readParmValue(conn, partNum, seqNum); // this is the raw value
                parmValueCooked = parmValueRaw; // default cooked is raw
                ruleSet.setRawValue(parmValueRaw);
                
                // If a DE select box is applicable
                // try to do a select box look up on the raw value
                selectBox = ruleSet.getDeSelectBox();
                if (selectBox != null) { 
                    // Do this if there is a select box defined for this field
                    // Let's attempt a look up and replace the raw with the select box entry
                    debug ("Select Box found for field # " + seqNum);
                    rc = selectBox.optionValue1IndexOf(parmValueRaw);
                    debug ("SB Lookup Return code was " + rc);
                    if (rc > -1) {
                        parmValueCooked = selectBox.getOptionText(rc);
                    }
                } else if (dataType.equals("N") ) {
                    parmValueCooked = cvt.toCooked(parmValueRaw, ruleSet.getDisplayMultipliers(),
                        ruleSet.getParmDelimiter(), ruleSet.getDecShift(), 
                        ruleSet.getAllowDuplicates(), ruleSet.getAllowTilde(), true);
                }
                debug ("Cooked value = '" + parmValueCooked + "'");
                ruleSet.setCookedValue(parmValueCooked);
                parmValuesRaw[seqNum] = parmValueRaw;
                parmValuesCooked[seqNum] = parmValueCooked;
                parmValuesFieldName[seqNum] = ruleSet.getParmName();
                if (dataType.equals("N")) {
                    work = ruleSet.getDisplayUnits();
                    if (work.equalsIgnoreCase("None")) {
                        work = "";
                    }
                    parmValuesUnits[seqNum] = work;
                }
                parmValuesMap[parmValuesCount++] = seqNum;
            } // end for i
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Unexpected error processing parm data.";
            debug (errorMessage);   
            return false;
        }
    }
    
    /** I read the parametric data for this part instance
     * ruleSets[] object has been pre-loaded and passed to me
     **/
    public boolean readParmData(WDSconnect conn, GPSrules[] ruleSets) {
        GPScvt cvt;
        String dataType = "";
        String errorMessage = "";
        String parmValueCooked = "";
        String parmValueRaw = "";
        int rc = 0;
        String ruleSubfamilyCode = "";
        GPSselectBox selectBox;
        String selectBoxName = "";
        int seqNum = 0;
        int size = 0;
        GPSunit units;
        String work = "";

        if (!isValid) {
            return false;
        }
        if (!hasPSData) {
            return false;
        }
        
        try {
            if (parmValuesCount > 0) { 
                for (int i = 0; i < 100; i++) {
                    parmValuesFieldName[i] = "";
                    parmValuesRaw[i] = null;
                    parmValuesCooked[i] = null;
                    parmValuesUnits[i] = "";
                    parmValuesMap[i] = -1;
                }
                parmValuesCount = 0;
            }
            for (size = 0; size < ruleSets.length; size++) {
                if (ruleSets[size] == null) {
                    break;
                }
            }
            for (int i = 0; i < size; i++)  {
                GPSrules ruleSet = ruleSets[i];

                // Check the family code for this Part Number against the Rules
                    
                if (!ruleSet.getFamilyCode().equals(familyCode)) {
                    errorMessage = "Unexpected Error getting parm data - Family code mismatch for PN " + partNum;
                    debug (errorMessage);
                    return false;
                }
             
                ruleSubfamilyCode = ruleSet.getSubfamilyCode().trim();
                //(Note that rulesubfamilyCode could be different than part subfamilyCode if
                //        we have a global rule here)
                  
                //      look up any units data we need for numeric items
                //      load any select boxes we need fo string and numeric items
                        
                seqNum = ruleSet.getSeqNum();
                dataType = ruleSet.getDataType();
                debug ("Processing ruleset for seq num " + seqNum + "; Field name: " + ruleSet.getParmName());
                if (dataType.equals("N") ) {
                    // Look up and set Display units info here
                    units = new GPSunit();
                    units.open(conn, ruleSet.getDisplayUnits());
                    ruleSet.setDecShift(units.getMultiplierExp());
                    units = null;
                }
                if ( "SN".contains(dataType)) {    
                    // If a DE select box exists...
                    // try to load a select box object for it
                    selectBoxName = ruleSet.getDeSelectBoxName();
                    if (ruleSet.getDeTextBoxSize() == 0 && !selectBoxName.equals("") ) {
                        debug ("I am going to try to open Select Box named " + selectBoxName);
                        selectBox = new GPSselectBox();  // create a new select box object
                        rc = selectBox.open(conn, familyCode, ruleSubfamilyCode, selectBoxName);
                        if (rc < 0) {
                            selectBox = null;
                            errorMessage = "Unexpected error " + rc + " opening Select Box " + selectBoxName;
                            debug (errorMessage);
                            return false;
                        }
                        ruleSet.setDeSelectBox(selectBox);
                        debug ("Created and stored the select box object.");
                    } else {
                        ruleSet.setDeSelectBox(null);
                        debug ("There is no Select Box for seq num " + seqNum);
                    }
                }
                debug ("Now converting raw value to cooked for field seq num " + seqNum);
                    
                cvt = new GPScvt();  // We need this class to cook raw values
                    
                parmValueRaw = GPSparmSet.readParmValue(conn, partNum, seqNum); // this is the raw value
                parmValueCooked = parmValueRaw; // default cooked is raw
                ruleSet.setRawValue(parmValueRaw);
                
                // If a DE select box is applicable
                // try to do a select box look up on the raw value
                selectBox = ruleSet.getDeSelectBox();
                if (selectBox != null) { 
                    // Do this if there is a select box defined for this field
                    // Let's attempt a look up and replace the raw with the select box entry
                    debug ("Select Box found for field # " + seqNum);
                    rc = selectBox.optionValue1IndexOf(parmValueRaw);
                    debug ("SB Lookup Return code was " + rc);
                    if (rc > -1) {
                        parmValueCooked = selectBox.getOptionText(rc);
                    }
                } else if (dataType.equals("N") ) {
                    parmValueCooked = cvt.toCooked(parmValueRaw, ruleSet.getDisplayMultipliers(),
                        ruleSet.getParmDelimiter(), ruleSet.getDecShift(), 
                        ruleSet.getAllowDuplicates(), ruleSet.getAllowTilde(), true);
                }
                debug ("Cooked value = '" + parmValueCooked + "'");
                ruleSet.setCookedValue(parmValueCooked);
                parmValuesRaw[seqNum] = parmValueRaw;
                parmValuesCooked[seqNum] = parmValueCooked;
                parmValuesFieldName[seqNum] = ruleSet.getParmName();
                if (dataType.equals("N")) {
                    work = ruleSet.getDisplayUnits();
                    if (work.equalsIgnoreCase("None")) {
                        work = "";
                    }
                    parmValuesUnits[seqNum] = work;
                }
                parmValuesMap[parmValuesCount++] = seqNum;
            } // end for i
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Unexpected error processing parm data.";
            debug (errorMessage);   
            return false;
        }
    }
    
    public static boolean searchWithin(String markUp, String keyWord) {
        markUp = markUp.toLowerCase();
        int pointer = 0;
        pointer = markUp.indexOf(keyWord, pointer);
        while (pointer > -1) {
            if (" -,:~@#()_+=;/>".contains(markUp.substring(pointer - 1, pointer))) {
                return true;
            }
            pointer = markUp.indexOf(keyWord, ++pointer);
        }
        return false;
    }
    
    public void setAuditDate(String x) {
        x = x.trim();
        auditDate = x;
    }
    
    public void setAuditTime(String x) {
        x = x.trim();
        auditTime = x;
    }
        
    public void setAuditUserID(String x) {
        x = x.trim().toLowerCase();
        auditUserID = x;
    }
    
    public void setDimensionsDepth(String x) {
        x = x.trim();
        dimensionsDepth = x;
    }
        
    public void setDimensionsHeight(String x) {
        x = x.trim();
        dimensionsHeight = x;
    }
        
    public void setDimensionsLength(String x) {
        x = x.trim();
        dimensionsDepth = x;
    }
    
    public void setDimensionsWidth(String x) {
        x = x.trim();
        dimensionsWidth = x;
    }
    
    public void setFamilyCode(String x) {
        x = x.trim();
        familyCode = x.toUpperCase();
    }
    
    public void setHasPSData(boolean x) {
        hasPSData = x;
    }
   
    public void setSeries(String x) {
        x = x.trim();
        series = x;
    }
       
    public void setSubfamilyCode(String x) {
        x = x.trim();
        subfamilyCode = x.toUpperCase();
    }
    
    public void setWeightGross(String x) {
        x = x.trim();
        weightGross = x;
    }

    public void setWeightNet(String x) {
        x = x.trim();
        weightNet = x;
    }
    
    public void setWeightUnit(String x) {
        x = x.trim();
        weightUnit = x;
    }
    
    public boolean updateFSH(WDSconnect con2, String aPartNum) {
        
        // I only update Family code, Subfamily code, and Has PS Data flag
        // in part record
        
        boolean rc = false;
        try {
            if (aPartNum == null) {return false;}
            if (aPartNum.length() == 0) { return false;}
            aPartNum = aPartNum.toUpperCase();
            if (familyCode == null) {return false;}
            //if (familyCode.length() == 0) { return false;}
            if (subfamilyCode == null) {return false;}
            //if (subfamilyCode.length() == 0) { return false;}            
                        
            String SQLCommand = "UPDATE pub.part";
            SQLCommand += " SET family_code = '" + familyCode + "'";
            SQLCommand += ", subfamily_code = '" + subfamilyCode +"'";
            SQLCommand += ", audit_date = '" + auditDate + "'";
            SQLCommand += ", audit_time = '" + auditTime + "'";
            SQLCommand += ", audit_userid = '" + auditUserID + "'";
            if (hasPSData) {
                SQLCommand += ", has_ps_data = '1'";
            } else {
                SQLCommand += ", has_ps_data = '0'";
            }
            SQLCommand += " WHERE part_num = '" + aPartNum + "'";
            rc = con2.runUpdate(SQLCommand);
        } catch (Exception e) {
            e.printStackTrace();
            rc = false;
        } finally {
            return rc;
        }
    }

    public boolean updateFSHS(WDSconnect con2, String aPartNum)
        throws IOException, SQLException  {
        
        // I update Family code, Subfamily code, and Has PS Data flag
        // also series, gross weight, net weight, weight units, and
        // height, width, and length
        // as contained inside my privates 
        // :-)
        
        boolean rc = false;
        try {
            //System.out.println("PN, FC, SC, S = '" + aPartNum + "','" + familyCode + "','" + subfamilyCode + "','" + series +"'");
            if (aPartNum == null) {return false;}
            if (aPartNum.length() == 0) { return false;}
            aPartNum = aPartNum.toUpperCase();
            if (familyCode == null) {return false;}
            if (familyCode.length() == 0) { return false;}
            if (subfamilyCode == null) {return false;}
            if (subfamilyCode.length() == 0) { return false;} 
            if (series == null) {return false;}
            if (dimensionsHeight == null) {return false;}
            if (dimensionsWidth == null) {return false;}
            if (dimensionsDepth == null) {return false;}
            if (weightGross == null) {return false;}
            if (weightNet == null) {return false;}
            if (weightUnit == null) {return false;}
                        
            String SQLCommand = "UPDATE pub.part";
            SQLCommand += " SET family_code = '" + familyCode + "'";
            SQLCommand += ", subfamily_code = '" + subfamilyCode +"'";
            SQLCommand += ", series = '" + series + "'";
            SQLCommand += ", gross_weight = " + weightGross;
            SQLCommand += ", net_weight = " + weightNet;
            SQLCommand += ", weight_unit = '" + weightUnit + "'";
            SQLCommand += ", p_height = " + dimensionsHeight;
            SQLCommand += ", p_width = " + dimensionsWidth;
            SQLCommand += ", p_depth = " + dimensionsDepth;
            SQLCommand += ", audit_date = '" + auditDate + "'";
            SQLCommand += ", audit_time = '" + auditTime + "'";
            SQLCommand += ", audit_userid = '" + auditUserID + "'";
            if (hasPSData) {
                SQLCommand += ", has_ps_data = '1'";
            } else {
                SQLCommand += ", has_ps_data = '0'";
            }
            SQLCommand += " WHERE part_num = '" +aPartNum + "'";
            rc = con2.runUpdate(SQLCommand);
        } catch (Exception e) {
            e.printStackTrace();
            rc = false;
        } finally {
            return rc;
        }
    }
    
    public static boolean updatePartRec(WDSconnect conn, String aPartNum, String aFamilyCode, String aSubfamilyCode)
        throws IOException, SQLException  {
        boolean completedOK = false;
        try {
            String SQLCommand = "UPDATE pub.part";
            SQLCommand += " SET family_code = '" + aFamilyCode + "'";
            SQLCommand += ", subfamily_code = '" + aSubfamilyCode +"'";
            SQLCommand += ", has_ps_data = '1'";
            SQLCommand += " WHERE part_num = '" + aPartNum + "'";
            completedOK = conn.runUpdate(SQLCommand);
        } catch (Exception e) {
            e.printStackTrace();
            completedOK = false;
        } finally {
            return completedOK;
        }
    }
    
    private void debug (String x) {
        if (debugSw) {
            System.out.println(x);
        }
    }

}
