/*
 * GPSindex.java
 *
 * Created on June 16, 2010, 5:39 PM
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
 */
public class GPSindex {
    private boolean debugSw = false;
    private static final String version = "1.5.04";
    
    private ArrayList <String> familyAliases = new ArrayList <String> ();  // This holds the Family Alias
    private ArrayList <String> subfamilyAliases = new ArrayList <String> ();  // This holds the Subfamily Alias
    private ArrayList <String> activeAliases = new ArrayList <String> ();  // This holds the Alias status
    private String familyCode = "";
    private String subfamilyCode = "";
    private boolean activeOnlyFlag = false;
       
    /** Creates a new instance of GPSindex */
    public GPSindex(WDSconnect conn, String aFamilyCode, String aSubfamilyCode, boolean activeOnly) {
    
        boolean code = false;
        familyCode = aFamilyCode;
        subfamilyCode = aSubfamilyCode;
        activeOnlyFlag = activeOnly;
        String queryString = "";
        ResultSet rs = null;
        String work = "";
        
        try {
            queryString = "SELECT family_alias, subfamily_alias, active ";
            queryString += " FROM pub.ps_index_entry";
            queryString += " WHERE family_code = '" + aFamilyCode + "'";
            queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";
            queryString += " AND division = 'CP'";
            if (activeOnly) {
                queryString += " AND active = '1' ";
            }
            queryString += " ORDER BY family_alias, subfamily_alias";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    familyAliases.add(rs.getString("family_alias"));
                    subfamilyAliases.add(rs.getString("subfamily_alias"));
                    code = rs.getBoolean("active");
                    work = code ? "Y" : "N";
                    activeAliases.add(work);
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                //debug ("" + size + " aliases were found in the ps_index_entry table.");
                return;
            }
            conn.closeStatement();
            return;
        } catch (SQLException e) {
            //debug ("Database error while attempting to find Index Aliases for family/subfamilty " + familyCode + "/" + subfamilyCode);
            e.printStackTrace();
            return;
        }   
    }
    
    public String getActiveAlias(int i) {
        if (i > -1 && i < activeAliases.size() ) {
            return activeAliases.get(i);
        } else {
            return null;
        }
    }
    
    public boolean getActiveOnlyFlag() {
        return activeOnlyFlag;        
    }
        
    public String getFamilyAlias(int i) {
        if (i > -1 && i < familyAliases.size() ) {
            return familyAliases.get(i);
        } else {
            return null;
        }
    }
    
    public String getFamilyCode() {
        return familyCode;
    }
        
    public int getSize() {
        return activeAliases.size();
    }
    
    public String getSubfamilyAlias(int i) {
        if (i > -1 && i < subfamilyAliases.size() ) {
            return subfamilyAliases.get(i);
        } else {
            return null;
        }
    }
    
        
    public String getSubfamilyCode() {
        return subfamilyCode;
    }
    
}
