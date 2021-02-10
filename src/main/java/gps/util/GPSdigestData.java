/*
 * GPSdigestData.java
 *
 * Created on January 8, 2007, 4:22 PM
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
 * @author Sauter
 */
public class GPSdigestData {
    
    private boolean debugSw = false;
    private static final String version = "1.5.00";
    
    private String category;
    private String digestValue[] = new String[99];
    private String header[] = new String [99];
    private int kount = 0;
    private String partNum;
    private int seqNum[] = new int[99];
    private String subcategory;
    private String template;
    
    /** Creates a new instance of GPSdigestData */
    public GPSdigestData() {
    }
    
    // How to use this object:
    
    // First instantiate a GPSdigestData object e.g.
    //   GPSdigestData junk = GPSdigestData();
    // Next initialize the digest template and header info
    // passing a connection object, category and subcategory codes
    //   boolean rc = junk.initDigestHeaders(conn, "DRIV", "ABBI");
    // junk instance variables will be initialized
    // if rc is true then 
    //   template, category, subcategory, seq numbers array and header array
    //   are defined and digestValues array is initialized to empty strings
    // otherwize initialization failed with permanent errors.
    //
    // To access digest data for a part number:
    //
    //    rc = junk.readDigestValues(conn, "1N4002-NTE")
    // if rc = true, we found some digest data
    // else digest data was not found and all values are set to empty strings
    //
   
    public static boolean exists(WDSconnect conn, String partNum) {
        boolean found = false;
        ResultSet rs = null;
        try {
            String queryString = "SELECT DISTINCT part_num";
            queryString += " FROM pub.catalog";
            queryString += " WHERE part_num = '" + partNum + "'";
            queryString += " AND seqnum > '00'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                found = rs.next();
                rs.close();
                rs = null;
                conn.closeStatement();
                return found;
            }
            return false;          
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String getDigestHeader(int i) {
        if (i > -1 && i < kount) {
            return header[i];
        }
        return "";
    }
    
    public String getDigestValue(int i) {
        if (i > -1 && i < kount) {
            return digestValue [i];
        }
        return "";
    }
    
    public String getPartNum(){
        return partNum;
    }
    
    public int getSeqNum(int i) {
        if (i > -1 && i < kount) {
            return seqNum[i];
        }
        return -1;
    }
    
    public String getTemplate() {
        return template;
    }
   
    public boolean initDigestHeaders(WDSconnect conn, String aCategory, String aSubcategory) { 
        category = "";
        digestValue = new String [99];
        header = new String [99];
        kount = 0;
        seqNum = new int[99];
        subcategory = "";
        template = "";
        // First we get template number
        aCategory = aCategory.toUpperCase();
        aSubcategory = aSubcategory.toUpperCase();
        if (!initTemplate(conn, aCategory, aSubcategory)) {
            return false;
        }
        // Now we set the headings for each digest field
        boolean returnCode = false;
        ResultSet rs = null;
        try {
            int i;
            int j = 0;
            String queryString = "SELECT *";
            queryString += " FROM pub.template";
            queryString += " WHERE template_num = '" + template + "'";
            queryString += " ORDER BY seqnum";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    i = rs.getInt("seqnum");
                    if ( i > 0 ) {
                        seqNum[j] = i;
                        header[j] = rs.getString("t_label");
                        digestValue[j++] = "";
                        returnCode = true;
                    }
                }
                kount = j;
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            return returnCode;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
            
    private boolean initTemplate(WDSconnect conn, String aCategory, String aSubcategory) {
        boolean returnCode = false;
        template = "";
        ResultSet rs = null;
        try {
            String queryString = "SELECT template_num";
            queryString += " FROM pub.part_cat";
            queryString += " WHERE category = '" + aCategory + "'";
            queryString += " AND subcategory = '" + aSubcategory + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    template = rs.getString("template_num");
                    returnCode = true;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            return returnCode;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean readDigestValues(WDSconnect conn, String aPartNum) {
        partNum = aPartNum.toUpperCase();
        ResultSet rs = null;
        String work = "";
        digestValue = new String[99];
        for (int j = 0; j < kount; j++) {
            digestValue[j] = "";
        }
        boolean returnCode = false;
        try {
            int i = 0;
            String queryString = "SELECT part_num, seqnum, attrib_value";
            queryString += " FROM pub.catalog";
            queryString += " WHERE part_num = '" + partNum + "'";
            queryString += " AND active = '1'";
            queryString += " ORDER BY seqnum";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    i = rs.getInt("seqnum");
                    if (i > 0) {
                        for (int j = 0 ; j < kount ; j++) {
                            if (seqNum[j] == i) {
                                work = rs.getString("attrib_value");
                                if (work == null) {
                                        work = "";
                                }
                                digestValue[j] = work;
                                returnCode = true;
                                break;
                            }
                        }
                    }
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!returnCode) {
                partNum = "";
                digestValue = new String[99];
            }
            return returnCode;
        }
    }
    
    public int size() {
        return kount;
    }
 
}
