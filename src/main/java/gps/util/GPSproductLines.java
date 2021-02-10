/*
 * GPSproductLines.java
 *
 * Created on February 1, 2007, 2:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

import OEdatabase.WDSconnect;
import java.util.*;
import java.sql.*;

/**
 *
 * @author Sauter
 *
 * @version 1.5.00
 *
 * modified 4/16/2008 by DES to support 4 Divisions within Product Lines 
 * modified 8/19/2016 by DES to support a wildcard * for ALL Product Lines (ECP-1)
 *
 */
public class GPSproductLines {
    
    private boolean debugSwitch = false;
    private final String VERSION = "1.5.00";
    
    private final int ARRAY_SIZE = 100;
    private int[] displayOrder = new int[ARRAY_SIZE];
    private boolean isValid = false;
    private String[] productLineCode = new String[ARRAY_SIZE];
    private String[] productLineName = new String[ARRAY_SIZE];
    private String[] productLineDivision = new String[ARRAY_SIZE];
    private int size = 0;
    // Constants
    private final String INVALID = "*invalid*";
    public final int PRODUCT_LINE_CODES_EMPTY = 1;
    public final int PRODUCT_LINE_CODES_OK = 0;
    public final int PRODUCT_LINE_CODES_DATABASE_ERROR = -1;
    public final int PRODUCT_LINE_CODES_SIZE_OVERFLOW = -2;
    
    /** Creates a new instance of GPSproductLines */
    
    public GPSproductLines() {
    }
         
    private void debug(String msg) {
        if (debugSwitch) {
            System.out.println(msg);
        }
    }
    
    public ArrayList <String> getArrayList() {
        if (isValid) {
            ArrayList <String> list = new ArrayList <String> ();
            String work;
            for ( int j = 0; j < size; j++) {
                work = "\"" + productLineCode[j] + "\", \"" + productLineName[j] + "\",\"" 
                        + displayOrder[j] + "\", \"" + productLineDivision[j] + "\"";
                list.add(work);
            }
            return list;
        }
        return null;
    }
 
    public ArrayList <String> getArrayList2() {                // ECP-1
        if (isValid) {
            ArrayList <String> list = new ArrayList <String> ();
            String work;
            work = "\"*\", \"All Product Lines\", \"0\", \"CP\"";
            list.add(work);
            for ( int j = 0; j < size; j++) {
                work = "\"" + productLineCode[j] + "\", \"" + productLineName[j] + "\",\"" 
                        + displayOrder[j] + "\", \"" + productLineDivision[j] + "\"";
                list.add(work);
            }
            return list;
        }
        return null;
    }
    
    public ArrayList <String> getArrayList(String productLineDivisionCode) {
        if (isValid) {
            ArrayList <String> list = new ArrayList <String> ();
            String work;
            for ( int j = 0; j < size; j++) {
                if (productLineDivisionCode.equals(productLineDivision[j])) {
                    work = "\"" + productLineCode[j] + "\", \"" + productLineName[j] + "\",\""
                        + displayOrder[j] + "\", \"" + productLineDivision[j] + "\"";
                list.add(work);
                }
            }
            return list;
        }
        return null;
    }
    
    public ArrayList <String> getArrayList(GPSfamilyCodes famCodes) {
        if (isValid) {
            ArrayList <String> list = new ArrayList <String> ();
            String work;
            for ( int j = 0; j < size; j++) {
                if (famCodes.getProductLineCodeReference(productLineCode[j])) {
                    work = "\"" + productLineCode[j] + "\", \"" + productLineName[j] + "\",\"" 
                        + displayOrder[j] + "\", \"" + productLineDivision[j] + "\"";
                    list.add(work);                    
                }
            }
            return list;
        }
        return null;
    }
    
    public ArrayList <String> getArrayList(GPSfamilyCodes famCodes, String aDivision) {
        if (isValid) {
            ArrayList <String> list = new ArrayList <String> ();
            String work;
            for ( int j = 0; j < size; j++) {
                if (famCodes.getProductLineCodeReference(productLineCode[j])
                    && aDivision.equals(productLineDivision[j])) {
                        work = "\"" + productLineCode[j] + "\", \"" + productLineName[j] + "\",\"" 
                            + displayOrder[j] + "\", \"" + productLineDivision[j] + "\"";
                        list.add(work);                    
                }
            }
            return list;
        }
        return null;
    }
    
    public int getDisplayOrder(int index) {
        if (isValid && index > -1 && index < size) {
            return displayOrder[index];
        }
        return -1;
    }
    
    public String getProductLineCode(int index) {
        if (isValid && index > -1 && index < size) {
            return productLineCode[index];
        }
        return INVALID;
    }
    
    public int getProductLineCodeDisplayOrder(String code) {
        if (isValid) {
            int i = getProductLineCodeIndex(code);
            if (i > -1 ) {
                return getDisplayOrder(i);
            }
        }
        return -1;
    }
    
    public int getProductLineCodeIndex(String code) {
        if (isValid) {
            for (int i=0; i < size; i++) {
                if (productLineCode[i].equals(code)) {
                    return i;
                }
            }
        }
        return -1;
    }
       
    public String getProductLineDivision(int index) {
        if (isValid && index > -1 && index < size) {
            return productLineDivision[index];
        }
        return INVALID;
    }
     
    public String getProductLineDivision(String code) {
        if (isValid) {
            int i = getProductLineCodeIndex(code);
            if (i > -1) {
                return productLineDivision[i];
            }
        }
        return INVALID;
    }
    
    public String getProductLineName(int index) {
        if (isValid && index > -1 && index < size) {
            return productLineName[index];
        }
        return INVALID;
    }
     
    public String getProductLineName(String code) {
        if (isValid) {
            int i = getProductLineCodeIndex(code);
            if (i > -1) {
                return productLineName[i];
            }
        }
        return INVALID;
    }
     
    public int getProductLineNameIndex(String name) {
        if (isValid) {
            for (int i=0; i < size; i++) {
                if (productLineName[i].equals(name)) {
                    return i;
                }
            }
        }
        return -1;
    }
         
    public static String lookUpProductLineName(WDSconnect conn, String aProductLineCode) {
        try {
            String result = "***Invalid***";
            String queryString = "SELECT product_line_name ";
            queryString += " FROM pub.ps_product_line";
            queryString += " WHERE product_line_code = '" + aProductLineCode + "'";
            ResultSet rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    result = rs.getString("product_line_name");
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
            debug ("Attempting to load Product Lines object.");
            isValid = false;
            String queryString = "SELECT product_line_code, product_line_name, product_line_division, display_order";
            queryString += " FROM pub.ps_product_line";
            queryString += " ORDER BY display_order";
            ResultSet rs = conn.runQuery(queryString);
            int j = 0;
            if (rs != null) {
                while (rs.next()) {
                    if (j >= ARRAY_SIZE ) {
                        System.out.println("Product Lines overflow in GPSproductLines.java.");
                        rs.close();
                        return PRODUCT_LINE_CODES_SIZE_OVERFLOW;
                    }
                    productLineCode[j] = rs.getString("product_line_code");
                    productLineName[j] = rs.getString("product_line_name");
                    productLineDivision[j] = rs.getString("product_line_division");
                    displayOrder [j] = rs.getInt("display_order");
                    j++;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                isValid = true;
                size = j;
                if (size == 0) {
                    debug ("No Product Lines were found.");
                    return PRODUCT_LINE_CODES_EMPTY;
                } else {
                    debug ("Loaded " + size + " Product Line Codes.");
                    return PRODUCT_LINE_CODES_OK;
                }    
            }
            debug ("Failed to obtain Result Set when attempting to load Product Line Codes.");
            return PRODUCT_LINE_CODES_DATABASE_ERROR;
        } catch (Exception e) {
            debug ("Database error when attempting to load Product Line Codes.");
            e.printStackTrace();
            return PRODUCT_LINE_CODES_DATABASE_ERROR;
        }
    }
    
    public int size() {
        return size;
    }
      
}
