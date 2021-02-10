/*
 * GPSmanCodes.java
 *
 * Created on June 25, 2008, 2:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
public class GPSmanCodes {
        
    private boolean debugSw = false;
    private static final String version = "1.5.00";
    
    /** Creates a new instance of GPSmanCodes */
    public GPSmanCodes() {
    }
    
    public static ArrayList<String> getAllMfgrCodes(WDSconnect conn, String aFamilyCode) {
        ArrayList<String> manCodes = new ArrayList<String>();
        ResultSet rs = null;;
        
        try {
            String queryString = "SELECT DISTINCT sales_subcat";
            queryString += " FROM pub.part";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " ORDER BY sales_subcat";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    manCodes.add(rs.getString("sales_subcat"));
                    //System.out.println("I got a man code.");
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            return manCodes;
        } catch (Exception e ) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static ArrayList<String> getAllMfgrCodes(WDSconnect conn, String aFamilyCode, String aSubfamilyCode) {
        ArrayList<String> manCodes = new ArrayList<String>();
        ResultSet rs = null;
               
        try {
            String queryString = "SELECT DISTINCT sales_subcat";
            queryString += " FROM pub.part";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " ORDER BY sales_subcat";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    manCodes.add(rs.getString("sales_subcat"));
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            return manCodes;
        } catch (Exception e ) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String getMfgrName(SROconnect conn2, String mfgrCode) {
        String mfgrName = "";
        ResultSet rs;
        String work = "";
        
        if (!mfgrCode.equals("")) {
            String queryString = "SELECT description";
            queryString += " FROM pub.codes_s";
            queryString += " WHERE valid_code = '" + mfgrCode + "'";
            queryString += " AND code_type = 'MANUFACTURER'";
            rs = conn2.runQuery(queryString);
            try {
                if (rs != null) {
                    if (rs.next()) {
                        mfgrName = rs.getString("description").trim();
                    } else {
                        mfgrName = "***UNDEFINED***";
                    }
                    rs.close();
                    rs = null;
                    conn2.closeStatement();
                } else {
                    mfgrName = "***Database Error***";
                }
            } catch (Exception e) {
                e.printStackTrace();
                mfgrName = "***Run Time Error***";
            } finally {
                return mfgrName;
            }
        }
        return null;
    }
    
    public static ArrayList<String> getMfgrNames(SROconnect conn2, ArrayList<String> mCodes) {
        ArrayList<String> mfgrNames = new ArrayList<String>();
        String mfgrCode;
        ResultSet rs;
        String work = "";
                
        if (mCodes != null) {
            for (int i=0; i < mCodes.size(); i++) {
                mfgrCode = mCodes.get(i);
                String queryString = "SELECT description";
                queryString += " FROM pub.codes_s";
                queryString += " WHERE valid_code = '" + mfgrCode + "'";
                queryString += " AND code_type = 'MANUFACTURER'";
                rs = conn2.runQuery(queryString);
                try {
                    if (rs != null) {
                        if (rs.next()) {
                            work = "\"" + mfgrCode + "\",\"" + rs.getString("description").trim() + "\"";
                        } else {
                            work = "\"" + mfgrCode + "\",\"***UNDEFINED***\"";
                        }
                        rs.close();
                        rs = null;
                        conn2.closeStatement();
                    } else {
                        work = "\"" + mfgrCode + "\",\"***Database Error***\"";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    work = "\"" + mfgrCode + "\",\"***Run Time Error***\"";
                } finally {
                    mfgrNames.add(work);
                }
            }
            return mfgrNames;
        }
        return null;
    }
    
    public static ArrayList<String> getMfgrSeries(WDSconnect conn1, String familyCode, String mfgrCode) {
        ArrayList<String> mfgrSeries = new ArrayList<String>();
        ResultSet rs;
        String work = "";
                
        if (!familyCode.equals("") && !mfgrCode.equals("")) {
            String queryString = "SELECT DISTINCT series";
            queryString += " FROM pub.part";
            queryString += " WHERE sales_subcat = '" + mfgrCode + "'";
            queryString += " AND family_code = '" + familyCode + "'";
            queryString += " ORDER BY series";
            rs = conn1.runQuery(queryString);
            try {
                if (rs != null) {
                    while (rs.next()) {
                        work = rs.getString("series").trim();
                        if (work.length() > 0) {
                            work = "\"" + work + "\"";
                            mfgrSeries.add(work);
                        }
                    }
                    rs.close();
                    rs = null;
                    conn1.closeStatement();
                    return mfgrSeries;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static ArrayList<String> getMfgrSeries(WDSconnect conn1, String familyCode, String subfamilyCode, 
            String mfgrCode) {
        ArrayList<String> mfgrSeries = new ArrayList<String>();
        ResultSet rs = null;
        String work = "";
                
        if (!familyCode.equals("") && !mfgrCode.equals("")) {
            String queryString = "SELECT DISTINCT series";
            queryString += " FROM pub.part";
            queryString += " WHERE sales_subcat = '" + mfgrCode + "'";
            queryString += " AND family_code = '" + familyCode + "'";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            queryString += " ORDER BY series";
            rs = conn1.runQuery(queryString);
            try {
                if (rs != null) {
                    while (rs.next()) {
                        work = rs.getString("series").trim();
                        if (work.length() > 0) {
                            work = "\"" + work + "\"";
                            mfgrSeries.add(work);
                        }
                    }
                    rs.close();
                    rs = null;
                    conn1.closeStatement();
                    return mfgrSeries;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }    

}