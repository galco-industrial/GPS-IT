/*
 * OptionsBuilder.java
 *
 * Created on June 12, 2008, 1:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package webTest;

import OEdatabase.*;
import gps.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;

/**
 *
 * @author Sauter
 *
 * Method getOptions
 * Input: rs - SQL Result Set containing rows of data containing parametric field values
 *          to be used in building an option list.
 *        inStock - boolean value set to true if option list is limited to in-stock items
 *        seqNum - field number of database field to process
 * Output: options - an array list of options derived from the result set.
 *
 */
public class OptionsBuilder {
    
    static boolean debugSw = true;
    
    /** Creates a new instance of OptionsBuilder */
    public OptionsBuilder() {
    }
    
    /*
    public static List<String> getOptionsCooked(WDSconnect aConn1, SROconnect aConn2,
            WWWconnect aConn3, String aFamilyCode, String aSubfamilyCode, 
            String aMfgrCode, String aSeries, int[] aSeqNum, 
            String aSearchWithin, boolean aInStockOnly) {
        
        // I return an arrayList object
        
        
        
        
        boolean addOption = true;
        String option = "";
        int optionIndex = -1;
        List<String> optionsRaw = new ArrayList<String>();
        List<String> optionsCooked = new ArrayList<String>();
        int optionCount = 0;
        String parmValue = "";
        String partNum = "";
        int rc;
        ResultSet rs = null;
        GPSselectBox sBox = new GPSselectBox();
        
        
        try {
            rc = sBox.open(aConn1, aFamilyCode, aSubfamilyCode, aSelectBoxName);
            if (rc != 1) {
                debug ("Error #" + rc + " creating select box object for " + aFamilyCode + "/"
                        + aSubfamilyCode + ":" + aSelectBoxName);
                return null;
            }
            optionCount = sBox.size();
            String queryString = "SELECT p.part_num, v.parm_value";
            queryString += " FROM pub.part p, pub.ps_parm_data v";
            queryString += " WHERE p.family_code = '" + aFamilyCode + "'";
            queryString += " AND p.subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " AND p.part_num = v.part_num";
            queryString += " AND v.seq_num = " + aSeqNum;
            debug ("SQL statement is " + queryString);
            rs = aConn1.runQuery(queryString);
            debug ("I got the Result Set; building the option list now...");
            if (rs != null) {
                while (rs.next()) {
                    parmValue = rs.getString("parm_value");
                    if (parmValue != null 
                            && !parmValue.equals("") 
                            && !optionsRaw.contains(parmValue)
                            && getWebQty(aConn3, rs.getString("part_num")) > -1) {
                        addOption = true;
                        if (aInStockOnly == true) {
                            partNum = rs.getString("part_num");
                            if (GPSpart.getAvailable(aConn1, partNum) < 1) {
                                addOption = false;
                            }
                        }
                        if (addOption) {
                            debug ("Adding Raw Option:  " + parmValue);
                            optionsRaw.add(parmValue);
                            option = "*Orphaned - " + parmValue;
                            optionIndex = sBox.optionValue1IndexOf(parmValue);
                            if (optionIndex  > -1) {
                                option = sBox.getOptionText(optionIndex);
                            }
                            optionsCooked.add(option);
                                                      
                            //if (optionCount == optionsRaw.size()) {
                            //    debug ("break");
                            //    break;
                            //} 
                        }
                    }
                }
                rs.close();
                
                if (optionsCooked.size() > 1) {
                    Collections.sort(optionsCooked);
                }
            }
        }
        catch (Exception e) {
            debug ("Error building Option list.");
            e.printStackTrace();
            
            return null;
        }
        debug ("Option list built.");
        
        return optionsCooked;
    }
    */
    public static List<String> getStringOptionsRaw(WDSconnect aConn1, WWWconnect aConn3, 
            String aFamilyCode, String aSubfamilyCode, int aSeqNum, boolean aInStockOnly) {
        
        boolean addOption = true;
        String option = "";
        List<String> optionsRaw = new ArrayList<String>();
        String parmValue = "";
        String partNum = "";
        ResultSet rs = null;
        
        try {
            String queryString = "SELECT p.part_num, v.parm_value";
            queryString += " FROM pub.part p, pub.ps_parm_data v";
            queryString += " WHERE p.family_code = '" + aFamilyCode + "'";
            queryString += " AND p.subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " AND p.part_num = v.part_num";
            queryString += " AND v.seq_num = " + aSeqNum;
            debug ("SQL statement is " + queryString);
            rs = aConn1.runQuery(queryString);
            debug ("I got the Result Set; building the option list now...");
            if (rs != null) {
                while (rs.next()) {
                    parmValue = rs.getString("parm_value");
                    if (parmValue != null 
                            && !parmValue.equals("") 
                            && !optionsRaw.contains(parmValue)
                            && getWebQty(aConn3, rs.getString("part_num")) > -1) {
                        addOption = true;
                        if (aInStockOnly == true) {
                            partNum = rs.getString("part_num");
                            if (GPSpart.getAvailable(aConn1, partNum) < 1) {
                                addOption = false;
                            }
                        }
                        if (addOption) {
                            debug ("Adding Raw Option:  " + parmValue);
                            optionsRaw.add(parmValue);
                        }
                    }
                }
                rs.close();
                if (optionsRaw.size() > 1) {
                    Collections.sort(optionsRaw);
                }
            }
        }
        catch (Exception e) {
            debug ("Error building Option list.");
            e.printStackTrace();
            
            return null;
        }
        debug ("Option list built.");
        
        return optionsRaw;
    }
    
    public static List<String> getStringOptionsCooked(WDSconnect aConn1, WWWconnect aConn3, 
            String aFamilyCode, String aSubfamilyCode, int aSeqNum, boolean aInStockOnly,
            String aSelectBoxName) {
        
        boolean addOption = true;
        String option = "";
        int optionIndex = -1;
        List<String> optionsRaw = new ArrayList<String>();
        List<String> optionsCooked = new ArrayList<String>();
        int optionCount = 0;
        String parmValue = "";
        String partNum = "";
        int rc;
        ResultSet rs = null;
        GPSselectBox sBox = new GPSselectBox();
        
        
        try {
            rc = sBox.open(aConn1, aFamilyCode, aSubfamilyCode, aSelectBoxName);
            if (rc != 1) {
                debug ("Error #" + rc + " creating select box object for " + aFamilyCode + "/"
                        + aSubfamilyCode + ":" + aSelectBoxName);
                return null;
            }
            optionCount = sBox.size();
            String queryString = "SELECT p.part_num, v.parm_value";
            queryString += " FROM pub.part p, pub.ps_parm_data v";
            queryString += " WHERE p.family_code = '" + aFamilyCode + "'";
            queryString += " AND p.subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " AND p.part_num = v.part_num";
            queryString += " AND v.seq_num = " + aSeqNum;
            debug ("SQL statement is " + queryString);
            rs = aConn1.runQuery(queryString);
            debug ("I got the Result Set; building the option list now...");
            if (rs != null) {
                while (rs.next()) {
                    parmValue = rs.getString("parm_value");
                    if (parmValue != null 
                            && !parmValue.equals("") 
                            && !optionsRaw.contains(parmValue)
                            && getWebQty(aConn3, rs.getString("part_num")) > -1) {
                        addOption = true;
                        if (aInStockOnly == true) {
                            partNum = rs.getString("part_num");
                            if (GPSpart.getAvailable(aConn1, partNum) < 1) {
                                addOption = false;
                            }
                        }
                        if (addOption) {
                            debug ("Adding Raw Option:  " + parmValue);
                            optionsRaw.add(parmValue);
                            option = "*Orphaned - " + parmValue;
                            optionIndex = sBox.optionValue1IndexOf(parmValue);
                            if (optionIndex  > -1) {
                                option = sBox.getOptionText(optionIndex);
                            }
                            optionsCooked.add(option);
                                                      
                            //if (optionCount == optionsRaw.size()) {
                            //    debug ("break");
                            //    break;
                            //} 
                        }
                    }
                }
                rs.close();
                
                if (optionsCooked.size() > 1) {
                    Collections.sort(optionsCooked);
                }
            }
        }
        catch (Exception e) {
            debug ("Error building Option list.");
            e.printStackTrace();
            
            return null;
        }
        debug ("Option list built.");
        
        return optionsCooked;
    }
    
    public static int getWebQty(WWWconnect aConn3, String aPartNum) {
        int result = -1; // Default = -1 = not a web item
        ResultSet rs = null;
        String queryString = "SELECT Part_Num, Qty_Avail, List_Type ";
        queryString += " FROM pub.catalogitem";
        queryString += " WHERE Part_Num = '" + aPartNum + "'";
        queryString += " AND List_Type = 'Catalog'";
        try {
            rs = aConn3.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    result = rs.getInt("Qty_Avail");
                    rs.close();
                    return result;
                }
                rs.close();
                return 0;
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
private static void debug (String x) {
    if (debugSw) {
        System.out.println(x);
    }
}
}
