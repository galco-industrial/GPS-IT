/*
 * GPSfamilyCodes.java
 *
 * Created on November 2, 2006, 6:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

import OEdatabase.WDSconnect;
import XML.util.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Sauter
 *
 * 8/17/2007
 *
 * 03/24/2010 DES Added support for allow_parent_all field in ps_family table.
 * 04/05/2010 DES Add Index Keyword support
 * Modified 7/14/2010 by DES to note that Index Keywords are not used here anymore.
 * modified 08/1/2016 by DES to support wildcard * All Product Lines   (ECP-1)
 *
 */

public class GPSfamilyCodes {
    private boolean debugSw = false;
    private static final String version = "1.6.0";
    
    private final int ARRAY_SIZE = 1000;
    private String[] allowParentAll = new String[ARRAY_SIZE];
    private String[] altFamilyCodes = new String[ARRAY_SIZE];
    private String[] altProductLineCodes = new String[ARRAY_SIZE];
    private int[] custBuys = new int[ARRAY_SIZE];
    private int[] displayOrders = new int[ARRAY_SIZE];
    private String[] familyCodes = new String[ARRAY_SIZE];
    private String[] familyNames = new String[ARRAY_SIZE];
    private boolean isValid = false;
    private String[] keywordsIndex = new String[ARRAY_SIZE];
    private String[] keywordsPlural = new String[ARRAY_SIZE];
    private String[] keywordsSingular = new String[ARRAY_SIZE];
    private String[] parentFamilyCodes = new String[ARRAY_SIZE];
    private String[] productLineCodes = new String[ARRAY_SIZE];
    private int size;
    private int[] totalBuys = new int[ARRAY_SIZE];
    // Constants
    private final String INVALID = "*invalid*";
    public final int FAMILY_CODES_EMPTY = 1;
    public final int FAMILY_CODES_OK = 0;
    public final int FAMILY_CODES_DATABASE_ERROR = -1;
    public final int FAMILY_CODES_SIZE_OVERFLOW = -2;
    //public final int FAMILY_CODE_EXISTS = -11;

    /** Creates a new instance of GPSfamilyCodes */
    public GPSfamilyCodes() {
    }
    
    private void debug(String msg) {
        if (debugSw) {
            System.out.println(msg);
        }
    }
     
    public ArrayList <String> getArrayList() {
        if (isValid) {
            ArrayList <String> list = new ArrayList <String> ();
            String work;
            for ( int j = 0; j < size; j++) {
                work = "\"" + familyCodes[j] + "\", \"" + familyNames[j] + "\",\"" 
                          + displayOrders[j] + "\",\""  + productLineCodes[j] + "\"";
                list.add(work);
            }
            return list;
        }
        return null;
    }
    
    public ArrayList <String> getArrayList(String aProductLineCode) {
        if (isValid) {
            ArrayList <String> list = new ArrayList <String> ();
            String work;
            for ( int j = 0; j < size; j++) {
                if (productLineCodes[j].equals(aProductLineCode) ) {
                    work = "\"" + familyCodes[j] + "\", \"" + familyNames[j] + "\",\"" 
                          + displayOrders[j] + "\",\""  + productLineCodes[j] + "\"";
                    list.add(work);
                }
            }
            return list;
        }
        return null;
    }

    public String getAllowParentAll(int index) {
        if (isValid && index > -1 && index < size) {
            return allowParentAll[index];
        }
        return "*undefined*";
    }
    
    public String getAltFamilyCode(int index) {
        if (isValid && index > -1 && index < size) {
            return altFamilyCodes[index];
        }
        return "*undefined*";
    }

    public String getAltProductLineCode(int index) {
        if (isValid && index > -1 && index < size) {
            return altProductLineCodes[index];
        }
        return "*undefined*";
    }

    public int getCustBuys(int index) {
        if (isValid && index > -1 && index < size) {
            return custBuys[index];
        }
        return -1;
    }
        
    public int getDisplayOrder(int index) {
        if (isValid && index > -1 && index < size) {
            return displayOrders[index];
        }
        return -1;
    }
    
    public String getFamilyCode(int index) {
        if (isValid && index > -1 && index < size) {
            return familyCodes[index];
        }
        return INVALID;
    }
    
    public int getFamilyCodeDisplayOrder(String code) {
        if (isValid) {
            int i = getFamilyCodeIndex(code);
            if (i > -1 ) {
                return getDisplayOrder(i);
            }
        }
        return -1;
    }
    
    public int getFamilyCodeIndex(String code) {
        if (isValid) {
            for (int i=0; i < size; i++) {
                if (familyCodes[i].equals(code)) {
                    return i;
                }
            }
        }
        return -1;
    }
        
    public String getFamilyName(int index) {
        if (isValid && index > -1 && index < size) {
            return familyNames[index];
        }
        return INVALID;
    }
    
     public String getFamilyName(String code) {
        if (isValid) {
            int i = getFamilyCodeIndex(code);
            if (i > -1) {
                return familyNames[i];
            }
        }
        return INVALID;
    }
     
    public String getFamilyProductLineCode(String code) {
        if (isValid) {
            int i = getFamilyCodeIndex(code);
            if (i > -1) {
                return productLineCodes[i];
            }
        }
        return INVALID;
    }
         
    public String getFamilyProductLineCode(int i) {
        if (isValid) {
            if (i > -1 && i < size) {
                return productLineCodes[i];
            }
        }
        return INVALID;
    }
       
    public String getKeywordsIndex(int index) {
        // Effective 7/14/2010 by DES Index Keywords are inactive .
        if (isValid && index > -1 && index < size) {
            return keywordsIndex[index];
        }
        return "*undefined*";
    }
   
    public String getKeywordsPlural(int index) {
        if (isValid && index > -1 && index < size) {
            return keywordsPlural[index];
        }
        return "*undefined*";
    }
    
    public String getKeywordsSingular(int index) {
        if (isValid && index > -1 && index < size) {
            return keywordsSingular[index];
        }
        return "*undefined*";
    }
    
    public String getParentFamilyCode(int index) {
        if (isValid && index > -1 && index < size) {
            return parentFamilyCodes[index];
        }
        return "*undefined*";
    }
    
    public boolean getProductLineCodeReference(String code) {
        if (isValid) {
            for (int i = 0; i < size; i++) {
                if (productLineCodes[i].equals(code)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public ArrayList <String> getPartReferences(WDSconnect conn, String aFamilyCode) {
        ResultSet rs = null;
        try {
            String work = "";
            String queryString = "SELECT part_num, subfamily_code ";
            queryString += " FROM pub.part";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " ORDER BY subfamily_code, part_num";
            rs = conn.runQuery(queryString);
            int j = 0;
            ArrayList<String> refs = new ArrayList <String> ();
            if (rs != null) {
                while (rs.next()) {
                    work = "\"" + aFamilyCode + "\"";
                    work += ",\"" + rs.getString("subfamily_code") + "\"";
                    work += ",\"" + rs.getString("part_num") + "\"";
                    refs.add(work);
                    j++;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                debug ("" + j + " Family Code references were found in the Part table.");
                return refs;
            }
            return null;
        } catch (SQLException e) {
            debug ("Database error while attempting to find Family Code references in Part table.");
            e.printStackTrace();
            return null;
        }   
    } 
           
    public ArrayList <String> getRuleReferences(WDSconnect conn, String aFamilyCode) {
        ResultSet rs = null;
        try {
            String work = "";
            String queryString = "SELECT subfamily_code, seq_num, rule_scope ";
            queryString += " FROM pub.ps_rules";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " ORDER BY subfamily_code, seq_num, rule_scope DESC";
            rs = conn.runQuery(queryString);
            int j = 0;
            ArrayList<String> refs = new ArrayList <String> ();
            if (rs != null) {
                while (rs.next()) {
                    work = "\"" + aFamilyCode + "\"";
                    work += ",\"" + rs.getString("subfamily_code") + "\"";
                    work += ",\"" + rs.getInt("seq_num") + "\"";
                    work += ",\"" + rs.getString("rule_scope") + "\"";
                    refs.add(work);
                    j++;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                debug ("" + j + " Family Code references were found in the Rules table.");
                return refs;
            }
            return null;
        } catch (Exception e) {
            debug ("Database error while attempting to find Family Code references in Rules table.");
            e.printStackTrace();
            return null;
        }   
    } 
    
    public ArrayList <String> getSelectBoxReferences(WDSconnect conn, String aFamilyCode) {
        ResultSet rs = null;
        try {
            String work = "";
            String queryString = "SELECT subfamily_code, select_box_name ";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " AND option_index = -1";
            queryString += " ORDER BY subfamily_code, select_box_name";
            rs = conn.runQuery(queryString);
            int j = 0;
            ArrayList<String> refs = new ArrayList <String> ();
            if (rs != null) {
                while (rs.next()) {
                    work = "\"" + aFamilyCode + "\"";
                    work += ",\"" + rs.getString("subfamily_code") + "\"";
                    work += ",\"" + rs.getString("select_box_name") + "\"";
                    refs.add(work);
                    j++;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                debug ("" + j + " Family Code references were found in the Select Box table.");
                return refs;
            }
            return null;
        } catch (Exception e) {
            debug ("Database error while attempting to find Family Code references in Select Box table.");
            e.printStackTrace();
            return null;
        }   
    } 
   
    public int getTotalBuys(int index) {
        if (isValid && index > -1 && index < size) {
            return totalBuys[index];
        }
        return -1;
    }
        
    public String getXMLList(String aProductLineCode) {
        //  Fix Node.textNode to encode entities like ampersands!!!!!!!!!!!!!!!!!!!!!!!!!!  
        if (isValid) {
            String list = "";
            String code = "";
            String name = "";
            for ( int j = 0; j < size; j++) {
                if (productLineCodes[j].equals(aProductLineCode) ) {
                    code = Node.textNode("code", Node.doXMLEntities(familyCodes[j]));
                    name = Node.textNode("name", Node.doXMLEntities(familyNames[j]));
                    list += Node.textNode("family", code + name);
                }
            }
            list =  Node.XML_HEADER + Node.textNode("families", list);
            return list;
        }
        return null;
    }

    public String getXMLList2(String aProductLineCode) {    // ECP-1
        //  Fix Node.textNode to encode entities like ampersands!!!!!!!!!!!!!!!!!!!!!!!!!!  
        if (isValid) {
            String list = "";
            String code = "";
            String name = "";
            if (aProductLineCode.equals("*")) {
                code = Node.textNode("code", Node.doXMLEntities("*"));
                name = Node.textNode("name", Node.doXMLEntities("All Families"));
                list += Node.textNode("family", code + name);
            } else {
                for ( int j = 0; j < size; j++) {
                    if (productLineCodes[j].equals(aProductLineCode) ) {
                        code = Node.textNode("code", Node.doXMLEntities(familyCodes[j]));
                        name = Node.textNode("name", Node.doXMLEntities(familyNames[j]));
                        list += Node.textNode("family", code + name);
                    }
                }
            }
            list =  Node.XML_HEADER + Node.textNode("families", list);
            return list;
        }
        return null;
    }    
    
    
    public static String lookUpFamilyName(WDSconnect conn, String aFamilyCode) {
        ResultSet rs = null;
        try {
            String result = "***Invalid***";
            String queryString = "SELECT family_name ";
            queryString += " FROM pub.ps_family";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    result = rs.getString("family_name");
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "***Runtime Error***";
        }
    }
         
    public static String lookUpFamilyProductLineCode(WDSconnect conn, String aFamilyCode) {
        ResultSet rs =  null;
        try {
            String result = "***Invalid***";
            String queryString = "SELECT product_line_code ";
            queryString += " FROM pub.ps_family";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    result = rs.getString("product_line_code");
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "***Runtime Error***";
        }
    }
    
    public int open(WDSconnect conn) {
        ResultSet rs = null;
        try {
            debug ("Attempting to load family codes object.");
            isValid = false;
            String queryString = "SELECT family_code, family_name, product_line_code, display_order, ";
            queryString += " parent_family_code, allow_parent_all, alt_product_line_code, alt_family_code, ";
            queryString += " keywords_singular, keywords_plural, index_keywords, cust_buys, total_buys";
            queryString += " FROM pub.ps_family";
            queryString += " ORDER BY product_line_code, display_order";
            rs = conn.runQuery(queryString);
            int j = 0;
            if (rs != null) {
                while (rs.next()) {
                    if (j >= ARRAY_SIZE ) {
                        System.out.println("Family codes overflow in GPSfamilyCodes.java.");
                        rs.close();
                        return FAMILY_CODES_SIZE_OVERFLOW;
                    }
                    familyCodes[j] = rs.getString("family_code");
                    familyNames[j] = rs.getString("family_name");
                    productLineCodes[j] = rs.getString("product_line_code");
                    displayOrders [j] = rs.getInt("display_order");
                    altProductLineCodes[j] = rs.getString("alt_product_line_code");
                    altFamilyCodes[j] = rs.getString("alt_family_code");
                    parentFamilyCodes[j] = rs.getString("parent_family_code");
                    keywordsIndex[j] = rs.getString("index_keywords");
                    keywordsPlural[j] = rs.getString("keywords_plural");
                    keywordsSingular[j] = rs.getString("keywords_singular");
                    custBuys[j] = rs.getInt("cust_buys");
                    totalBuys[j] = rs.getInt("total_buys");
                    allowParentAll[j] = rs.getBoolean("allow_parent_all") ? "Y" : "N";
                    j++;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                isValid = true;
                size = j;
                if (size == 0) {
                    debug ("No Family Codes were found.");
                    return FAMILY_CODES_EMPTY;
                } else {
                    debug ("Loaded " + size + " Family Codes.");
                    return FAMILY_CODES_OK;
                }    
            }
            debug ("Failed to obtain Result Set when attempting to load Family Codes.");
            return FAMILY_CODES_DATABASE_ERROR;
        } catch (Exception e) {
            debug ("Database error when attempting to load Family Codes.");
            e.printStackTrace();
            return FAMILY_CODES_DATABASE_ERROR;
        }
    }    
    
    public int open(WDSconnect conn, String aProductLine) {
        ResultSet rs =  null;
        try {
            debug ("Attempting to load family codes object for product line " + aProductLine);
            isValid = false;
            String queryString = "SELECT family_code, family_name, product_line_code, display_order, ";
            queryString += " parent_family_code, allow_parent_all, alt_product_line_code, alt_family_code, ";
            queryString += " keywords_singular, keywords_plural, index_keywords, cust_buys, total_buys";
            queryString += " FROM pub.ps_family";
            queryString += " WHERE product_line_code = '" + aProductLine + "'";
            queryString += " ORDER BY display_order";
            rs = conn.runQuery(queryString);
            int j = 0;
            if (rs != null) {
                while (rs.next()) {
                    if (j >= ARRAY_SIZE ) {
                        System.out.println("Family codes overflow in GPSfamilyCodes.java.");
                        rs.close();
                        return FAMILY_CODES_SIZE_OVERFLOW;
                    }
                    familyCodes[j] = rs.getString("family_code");
                    familyNames[j] = rs.getString("family_name");
                    productLineCodes[j] = rs.getString("product_line_code");
                    displayOrders [j] = rs.getInt("display_order");
                    altProductLineCodes[j] = rs.getString("alt_product_line_code");
                    altFamilyCodes[j] = rs.getString("alt_family_code");
                    parentFamilyCodes[j] = rs.getString("parent_family_code");
                    keywordsIndex[j] = rs.getString("index_keywords");
                    keywordsPlural[j] = rs.getString("keywords_plural");
                    keywordsSingular[j] = rs.getString("keywords_singular");
                    custBuys[j] = rs.getInt("cust_buys");
                    totalBuys[j] = rs.getInt("total_buys");
                    allowParentAll[j] = rs.getBoolean("allow_parent_all") ? "Y" : "N";
                    j++;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                isValid = true;
                size = j;
                if (size == 0) {
                    debug ("No Family Codes were found.");
                    return FAMILY_CODES_EMPTY;
                } else {
                    debug ("Loaded " + size + " Family Codes.");
                    return FAMILY_CODES_OK;
                }    
            }
            debug ("Failed to obtain Result Set when attempting to load Family Codes.");
            return FAMILY_CODES_DATABASE_ERROR;
        } catch (Exception e) {
            debug ("Database error when attempting to load Family Codes.");
            e.printStackTrace();
            return FAMILY_CODES_DATABASE_ERROR;
        }
    } 
    
    public int size() {
        return size;
    }
    
}            
       