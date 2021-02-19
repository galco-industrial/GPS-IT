/*
 * GPSsubfamilyCodes.java
 *
 * Created on November 4, 2006, 4:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

import OEdatabase.WDSconnect;
import XML.util.*;
import java.util.*;
import java.sql.*;
import gps.util.*;

/**
 *
 * @author Sauter
 *
 * 8/17/2007
 *
 * 04/05/2010 DES Add Index Keyword support
 * Modified 7/14/2010 by DES to note that Index Keywords are not used here anymore.
 *
 */
public class GPSsubfamilyCodes {
      
    private boolean debugSw = false;
    private final String version = "1.5.05";
    
    private final int ARRAY_SIZE = 2000;
    private String[] altFamilyCode = new String[ARRAY_SIZE];
    private String[] altSubfamilyCode = new String[ARRAY_SIZE]; 
    private int[] custBuys = new int[ARRAY_SIZE];
    private int[] displayOrder = new int[ARRAY_SIZE];
    private String[] familyCode = new String[ARRAY_SIZE];
    private int[] indexLevel = new int[ARRAY_SIZE];
    private String[] keywordsIndex = new String[ARRAY_SIZE];
    private String[] keywordsPlural = new String[ARRAY_SIZE];
    private String[] keywordsSingular = new String[ARRAY_SIZE];
    private final String INVALID = "*invalid*";
    private boolean isValid = false;
    private int size;
    public final int SUBFAMILY_CODES_EMPTY = 1;
    public final int SUBFAMILY_CODES_OK = 0;
    public final int SUBFAMILY_CODES_DATABASE_ERROR = -1;
    public final int SUBFAMILY_CODES_SIZE_OVERFLOW = -2;
    private String[] subfamilyCode = new String[ARRAY_SIZE];
    private String[] subfamilyName = new String[ARRAY_SIZE];
    private int[] totalBuys = new int[ARRAY_SIZE];
    
    /** Creates a new instance of GPSsubfamilyCodes */
    public GPSsubfamilyCodes() {
    }
    
    private void debug(String msg) {
        if (debugSw) {
            System.out.println(msg);
        }
    }

    public String getAltFamilyCode(int index) {
        if (isValid && index > -1 && index < size) {
            return altFamilyCode[index];
        }
        return INVALID;
    }

    public String getAltSubfamilyCode(int index) {
        if (isValid && index > -1 && index < size) {
            return altSubfamilyCode[index];
        }
        return INVALID;
    }
    
    public ArrayList <String> getArrayList() {
        if (isValid) {
            ArrayList <String> list = new ArrayList <String> ();
            String work;
            for (int j = 0; j < size; j++) {
                work = "\"" + familyCode[j] + "\", \"" + subfamilyCode[j]+ "\", \"" + subfamilyName[j] + "\",\"" 
                          + displayOrder[j] + "\"";
                list.add(work);
            }
            return list;
        }
        return null;
    }
    
    public ArrayList <String> getArrayList(String aFamilyCode) {
        if (isValid) {
            ArrayList <String> list = new ArrayList <String> ();
            String work;
            for (int j = 0; j < size; j++) {
                if (familyCode[j].equals(aFamilyCode) ) {
                    work = "\"" + familyCode[j] + "\", \"" + subfamilyCode[j]+ "\", \"" + subfamilyName[j] + "\",\"" 
                          + displayOrder[j] + "\"";
                    list.add(work);
                }
            }
            return list;
        }
        return null;
    }
    
    public ArrayList <String> getArrayList(WDSconnect conn, String aFamilyCode, String aMfgrCode) {
        if (isValid) {
            try {
                ArrayList <String> list = new ArrayList <String> ();
                String work;
                for (int j = 0; j < size; j++) {
                    if (familyCode[j].equals(aFamilyCode) ) {
                        if (GPSpart.exists(conn, aFamilyCode, subfamilyCode[j], aMfgrCode)) {
                            work = "\"" + familyCode[j] + "\", \"" + subfamilyCode[j] + "\", \"" + subfamilyName[j] + "\",\"" 
                                + displayOrder[j] + "\"";
                            list.add(work);
                        }
                    }
                }
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }    
        return null;
    }
    
    public ArrayList <String> getArrayList(WDSconnect conn, String aFamilyCode, String aMfgrCode, String aSeriesCode) {
        if (isValid) {
            try {
                ArrayList <String> list = new ArrayList <String> ();
                String work;
                for (int j = 0; j < size; j++) {
                    if (familyCode[j].equals(aFamilyCode) ) {
                        if (GPSpart.exists(conn, aFamilyCode, subfamilyCode[j], aMfgrCode, aSeriesCode)) {
                            work = "\"" + familyCode[j] + "\", \"" + subfamilyCode[j] + "\", \"" + subfamilyName[j] + "\",\"" 
                                + displayOrder[j] + "\"";
                            list.add(work);
                        }
                    }
                }
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }    
        return null;
    }
    
    public int getCustBuys(int index) {
        if (isValid && index > -1 && index < size) {
            return custBuys[index];
        }
        return -1;
    }
    
    public int getDisplayOrder(int index) {
        if (isValid && index > -1 && index < size) {
            return displayOrder[index];
        }
        return -1;
    }
    
    public String getFamilyCode(int index) {
        if (isValid && index > -1 && index < size) {
            return familyCode[index];
        }
        return INVALID;
    }
    
    public int getIndexLevel(int index) {
        if (isValid && index > -1 && index < size) {
            return indexLevel[index];
        }
        return -1;
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
        return INVALID;
    }
    
    public String getKeywordsSingular(int index) {
        if (isValid && index > -1 && index < size) {
            return keywordsSingular[index];
        }
        return INVALID;
    }
        
    public ArrayList <String> getPartReferences(WDSconnect conn, String aFamilyCode, String aSubfamilyCode) {
        try {
            String work = "";
            String queryString = "SELECT part_num ";
            queryString += " FROM pub.part";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " ORDER BY part_num";
            ResultSet rs = conn.runQuery(queryString);
            int j = 0;
            ArrayList<String> refs = new ArrayList <String> ();
            if (rs != null) {
                while (rs.next()) {
                    work = "\"" + aFamilyCode + "\"";
                    work += ",\"" + aSubfamilyCode + "\"";
                    work += ",\"" + rs.getInt("part_num") + "\"";
                    refs.add(work);
                    j++;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                debug ("" + j + " Subfamily Code references were found in the Part table.");
                return refs;
            }
            return null;
        } catch (Exception e) {
            debug ("Database error while attempting to find Subfamily Code references in Part table.");
            e.printStackTrace();
            return null;
        }   
    }
    
    public ArrayList <String> getRuleReferences(WDSconnect conn, String aFamilyCode, String aSubfamilyCode) {
        try {
            String work = "";
            String queryString = "SELECT seq_num, rule_scope ";
            queryString += " FROM pub.ps_rules";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " ORDER BY seq_num, rule_scope DESC";
            ResultSet rs = conn.runQuery(queryString);
            int j = 0;
            ArrayList<String> refs = new ArrayList <String> ();
            if (rs != null) {
                while (rs.next()) {
                    work = "\"" + aFamilyCode + "\"";
                    work += ",\"" + aSubfamilyCode + "\"";
                    work += ",\"" + rs.getInt("seq_num") + "\"";
                    work += ",\"" + rs.getString("rule_scope") + "\"";
                    refs.add(work);
                    j++;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                debug ("" + j + " Subfamily Code references were found in the Rules table.");
                return refs;
            }
            return null;
        } catch (Exception e) {
            debug ("Database error while attempting to find Subfamily Code references in Rules table.");
            e.printStackTrace();
            return null;
        }   
    } 

    public ArrayList <String> getSelectBoxReferences(WDSconnect conn, String aFamilyCode, String aSubfamilyCode) {
        try {
            String work = "";
            String queryString = "SELECT select_box_name ";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " AND option_index = -1";
            queryString += " ORDER BY select_box_name";
            ResultSet rs = conn.runQuery(queryString);
            int j = 0;
            ArrayList<String> refs = new ArrayList <String> ();
            if (rs != null) {
                while (rs.next()) {
                    work = "\"" + aFamilyCode + "\"";
                    work += ",\"" + aSubfamilyCode + "\"";
                    work += ",\"" + rs.getString("select_box_name") + "\"";
                    refs.add(work);
                    j++;
                }
                rs.close();
                debug ("" + j + " Subfamily Code references were found in the Select Box table.");
                return refs;
            }
            return null;
        } catch (Exception e) {
            debug ("Database error while attempting to find Subfamily Code references in Select Box table.");
            e.printStackTrace();
            return null;
        }   
    } 
        
    public String getSubfamilyCode(int index) {
        if (isValid && index > -1 && index < size) {
            return subfamilyCode[index];
        } else {
            return INVALID;
        }
    }
    
    public int getSubfamilyCodeDisplayOrder(String aFamilyCode, String aSubfamilyCode) {
        if (isValid) {
            int i = getSubfamilyCodeIndex(aFamilyCode, aSubfamilyCode);
            if (i > -1 ) {
                return getDisplayOrder(i);
            }
        }
        return -1;
    }
            
    public int getSubfamilyCodeIndex(String aFamilyCode, String aSubfamilyCode) {
        if (isValid) {
            for (int i=0; i < size; i++) {
                if (familyCode[i].equals(aFamilyCode)) {
                    if (subfamilyCode[i].equals(aSubfamilyCode)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
      
    public String getSubfamilyName(int index) {
        if (isValid && index > -1 && index < size) {
            return subfamilyName[index];
        }
        return INVALID;
    }

    public String getSubfamilyName(String aFamilyCode, String aSubfamilyCode) {
        if (isValid) {
            int i = getSubfamilyCodeIndex(aFamilyCode, aSubfamilyCode);
            if (i != -1) {
                return subfamilyName[i];
            }
        }
        return INVALID;
    }
        
    public int getTotalBuys(int index) {
        if (isValid && index > -1 && index < size) {
            return totalBuys[index];
        }
        return -1;
    }
            
    public String getXMLList(String aFamilyCode) {
        if (isValid) {
            String list = "";
            String code = "";
            String name = "";
            for ( int j = 0; j < size; j++) {
                if (familyCode[j].equals(aFamilyCode) ) {
                    code = Node.textNode("code", Node.doXMLEntities(subfamilyCode[j]));
                    name = Node.textNode("name", Node.doXMLEntities(subfamilyName[j]));
                    list += Node.textNode("subfamily", code + name);
                }
            }
            list =  Node.XML_HEADER + Node.textNode("subfamilies", list);
            return list;
        }
        return null;
    }
   
    public static String lookUpSubfamilyName(WDSconnect conn, String aFamilyCode, String aSubfamilyCode) {
        try {
            if (aSubfamilyCode.equals("*")) {
                return "All Subfamilies";
            }
            String result = "***Invalid***";
            String queryString = "SELECT subfamily_name ";
            queryString += " FROM pub.ps_subfamily";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            ResultSet rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    result = rs.getString("subfamily_name");
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
        try {
            debug ("Attempting to load subfamily codes object.");
            isValid = false;
            String queryString = "SELECT * ";
            queryString += " FROM pub.ps_subfamily";
            queryString += " ORDER BY family_code, display_order";
            ResultSet rs = conn.runQuery(queryString);
            int j = 0;
            if (rs != null) {
                while (rs.next()) {
                    if (j >= ARRAY_SIZE ) {
                        System.out.println("Subfamily codes overflow in GPSsubfamilyCodes.java.");
                        rs.close();
                        return SUBFAMILY_CODES_SIZE_OVERFLOW;
                    }
                    familyCode[j] = rs.getString("family_code");
                    subfamilyCode[j] = rs.getString("subfamily_code");
                    subfamilyName[j] = rs.getString("subfamily_name");
                    displayOrder [j] = rs.getInt("display_order");
                    indexLevel [j] = rs.getInt("index_level");
                    keywordsIndex[j] = rs.getString("index_keywords");
                    keywordsSingular[j] = rs.getString("keywords_singular");
                    keywordsSingular[j] = rs.getString("keywords_singular");
                    keywordsPlural[j] = rs.getString("keywords_plural");
                    altFamilyCode[j] = rs.getString("alt_family_code");
                    altSubfamilyCode[j] = rs.getString("alt_subfamily_code");
                    custBuys[j] = rs.getInt("cust_buys");
                    totalBuys[j] = rs.getInt("total_buys");
                    j++;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                isValid = true;
                size = j;
                if (size == 0) {
                    debug ("No Subfamily Codes were found.");
                    return SUBFAMILY_CODES_EMPTY;
                } else {
                    debug ("Loaded " + size + " Subfamily Codes.");
                    return SUBFAMILY_CODES_OK;
                }    
            }
            debug ("Failed to obtain Result Set when attempting to load Subfamily Codes.");
            return SUBFAMILY_CODES_DATABASE_ERROR;
        } catch (Exception e) {
            debug ("Database error when attempting to load Subfamily Codes.");
            e.printStackTrace();
            return SUBFAMILY_CODES_DATABASE_ERROR;
        }
    }    

    public int open(WDSconnect conn, String aFamilyCode) {
        
        // I am used when only subfamilies within a given family are desired
        
        try {
            debug ("Attempting to load subfamily codes object for family " + aFamilyCode);
            isValid = false;
            String queryString = "SELECT * ";
            queryString += " FROM pub.ps_subfamily";
            queryString += " WHERE family_code ='" + aFamilyCode + "'";
            queryString += " ORDER BY display_order";
            ResultSet rs = conn.runQuery(queryString);
            int j = 0;
            if (rs != null) {
                while (rs.next()) {
                    if (j >= ARRAY_SIZE ) {
                        System.out.println("Subfamily codes overflow in GPSsubfamilyCodes.java.");
                        rs.close();
                        return SUBFAMILY_CODES_SIZE_OVERFLOW;
                    }
                    familyCode[j] = rs.getString("family_code");
                    subfamilyCode[j] = rs.getString("subfamily_code");
                    subfamilyName[j] = rs.getString("subfamily_name");
                    displayOrder [j] = rs.getInt("display_order");
                    indexLevel [j] = rs.getInt("index_level");
                    keywordsIndex[j] = rs.getString("index_keywords");
                    keywordsSingular[j] = rs.getString("keywords_singular");
                    keywordsPlural[j] = rs.getString("keywords_plural");
                    altFamilyCode[j] = rs.getString("alt_family_code");
                    altSubfamilyCode[j] = rs.getString("alt_subfamily_code");
                    custBuys[j] = rs.getInt("cust_buys");
                    totalBuys[j] = rs.getInt("total_buys");
                    j++;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                isValid = true;
                size = j;
                if (size == 0) {
                    debug ("No Subfamily Codes were found for family " + aFamilyCode);
                    return SUBFAMILY_CODES_EMPTY;
                } else {
                    debug ("Loaded " + size + " Subfamily Codes for Family " + aFamilyCode);
                    return SUBFAMILY_CODES_OK;
                }    
            }
            debug ("Failed to obtain Result Set when attempting to load Subfamily Codes.");
            return SUBFAMILY_CODES_DATABASE_ERROR;
        } catch (Exception e) {
            debug ("Database error when attempting to load Subfamily Codes.");
            e.printStackTrace();
            return SUBFAMILY_CODES_DATABASE_ERROR;
        }
    }
    
    public int size() {
        if (isValid) {
            return size;
        }
        return -1;
    }
    
    
    // The static method below is deprecated; use getArrayList() method
    
    public static List <String> getAllSubfamilies(WDSconnect conn) {
        try {
            List <String> subfamilies = new ArrayList <String> ();
            String queryString = "SELECT family_code, subfamily_code, subfamily_name";
            queryString += " FROM pub.ps_subfamily";
            queryString += " ORDER BY family_code, subfamily_name";
            ResultSet rs = conn.runQuery(queryString);
            int j = 0;
            if (rs != null) {
                while(rs.next()) {
                    String work = "\""+rs.getString("family_code")+"\", \""+rs.getString("subfamily_code")+"\", \""+rs.getString("subfamily_name")+"\"";
                    if (work.indexOf("*") == -1) {subfamilies.add(work);}
                    j++;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            if (j == 0) {
                subfamilies = null;
            }
            return subfamilies;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
