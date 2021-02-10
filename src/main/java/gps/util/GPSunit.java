/*
 * GPSunits.java
 *
 * Created on October 19, 2006, 7:12 PM
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
public class GPSunit {
    
    private final String CLASS_NAME = this.getClass().getName();
    private final String VERSION = "1.5.00";
    
    boolean debugSw = false;
    
    private String baseUnits;
    private int displayOrder;
    private String displayUnits;
    private float multiplierBase;
    private int multiplierExp;
    private float multiplierPostAdjust;
    private float multiplierPreAdjust;
    private int numericBase;
   
    /** Creates a new instance of GPSunits */
    public GPSunit() {
    }
    
    /** Creates a units object for Display Units */
    
    public boolean open(WDSconnect conn, String unitsName) {
        if (unitsName.equalsIgnoreCase("none")) {
            displayUnits = "";
            baseUnits = "";
            numericBase = 10;
            multiplierBase = 10;
            multiplierExp = 0;
            multiplierPreAdjust = 0;
            multiplierPostAdjust = 0;
            return true;
        }
        ResultSet rs = null;
        try {
            String queryString = "SELECT base_units, numeric_base, multiplier_base,";
            queryString += " multiplier_exp, multiplier_pre_adjust, multiplier_post_adjust, ";
            queryString += " display_order";
            queryString += " FROM pub.ps_units";
            queryString += " WHERE display_units = '" + unitsName + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    baseUnits = rs.getString("base_units");
                    numericBase = rs.getInt("numeric_base");
                    multiplierBase = rs.getFloat("multiplier_base");
                    multiplierPreAdjust = rs.getFloat("multiplier_pre_adjust");
                    multiplierPostAdjust = rs.getFloat("multiplier_post_adjust");
                    multiplierExp = rs.getInt("multiplier_exp");
                    displayOrder = rs.getInt("display_order");
                    displayUnits = unitsName;
                    rs.close();
                    return true;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
            }
            displayUnits = unitsName;
            baseUnits = "";
            numericBase = 0;
            multiplierBase = 0;
            multiplierExp = 0;
            multiplierPreAdjust = 0;
            multiplierPostAdjust = 0;
            return false;    
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
     
    private void debug (String x) {
        if (debugSw) {
            System.out.println(x);
        }
    }
   
    public String getArrayListElement() {
        String work = "";
        work += "" + displayOrder;
        work += ",'" + displayUnits;
        work += "','" + baseUnits;
        work += "'," + numericBase;
        work += "," + multiplierBase;
        work += "," + multiplierExp;
        work += "," + multiplierPreAdjust;
        work += "," + multiplierPostAdjust;
        return work; 
    }
    
    public String getBaseUnits() {
        return baseUnits;
    }
    
    public int getDisplayOrder() {
        return displayOrder;
    } 
        
    public String getDisplayUnits() {
        return displayUnits;
    }
   
    public float getMultiplierBase() {
        return multiplierBase;
    }
    
    public float getMultiplierPreAdjust() {
        return multiplierPreAdjust;
    }
   
    public float getMultiplierPostAdjust() {
        return multiplierPostAdjust;
    }
   
    public int getMultiplierExp() {
        return multiplierExp;
    }
    
    public int getNumericBase() {
        return numericBase;
    }
    
    public static ArrayList <String> getUnitReferences(WDSconnect conn, String aDisplayUnits) {
        
        // I return an array list containing the rules that have active references
        // to this display unit name.
        
        ResultSet rs = null;
        ArrayList <String> references = new ArrayList <String> ();
        try {
            String work;
            //int k = 0;
            String queryString = "SELECT family_code, subfamily_code, seq_num, rule_scope ";
            queryString += " FROM pub.ps_rules";
            queryString += " WHERE display_units = '"  + aDisplayUnits + "'";
            queryString += " ORDER BY family_code, subfamily_code, seq_num, rule_scope";
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
            //debug ("Something ugly happened when GPSunits.java tried to find display units references in the rules table.");
            return null;
        }
    }
    
    public void setBaseUnits(String base) {
        baseUnits = base;
    }
    
    public void setDisplayOrder(int order) {
        if (order < 0 || order > 9999) {
            order = -1;
        }
        displayOrder = order;
    } 
        
    public void setDisplayUnits(String units) {
        displayUnits = units;
    }

    public void setMultiplierBase(float base) {
        multiplierBase = base;
    }
    
    public void setMultiplierPreAdjust(float preAdjust) {
        multiplierPreAdjust = preAdjust;
    }
   
    public void setMultiplierPostAdjust(float postAdjust) {
        multiplierPostAdjust = postAdjust;
    }
   
    public void setMultiplierExp(int exp) {
        multiplierExp = exp;
    }
    
    public void setNumericBase(int base) {
        numericBase = 0;
        if (base == 2 || base == 8 || base == 10 || base == 16 ) {
            numericBase = base;
        }
    }
    
}
