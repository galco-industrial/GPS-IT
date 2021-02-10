/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gps.util;

import OEdatabase.SROconnect;
import OEdatabase.WWWconnect;
import XML.util.*;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author dunlop
 */

public class GPSmfgrNames {
    private boolean debugSwitch = false;
    private final String VERSION = "1.5.00";
    
    private final int ARRAY_SIZE = 100;
    private boolean isValid = false; 
    private String[] mfgCode = new String[ARRAY_SIZE];
    private String[] mfgName = new String[ARRAY_SIZE];      
    private static String parentCode;
    private static String description; 
    private static String codetype; 
    private int size = 0;    
    // Constants
    private final String INVALID = "*invalid*";
    public final int MANUFACTURER_NAMES_EMPTY = 1;
    public final int MANUFACTURER_NAMES_OK = 0;
    public final int MANUFACTURER_NAMES_DATABASE_ERROR = -1;
    public final int MANUFACTURER_NAMES_SIZE_OVERFLOW = -2; 
    public static String f_result = "";   
    
    /** Creates a new instance of GPSmfgrNames */
    public GPSmfgrNames() {
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
                work = "\"" + mfgCode[j] + "\", \"" + mfgName[j] + "\"";
                debug (work);
                list.add(work);
            }
            return list;
        }
        return null;
    } 
    
    public String getXMLList() {
        if (isValid) {
            String list = "";
            String newmfgName = "";
            String newmfgCode = "";
            String code = "";
            String name = "";
            for ( int j = 0; j < size; j++) {
                //Check Manufacturer Name for Special Characters
                newmfgName = mfgName[j];
                if(newmfgName.contains("&")){
                    newmfgName = mfgName[j].replace("&","&amp;");
                } 
                if(newmfgName.contains("<")){
                    newmfgName = newmfgName.replace("<","&lt;");
                } 
                if(newmfgName.contains(">")){
                    newmfgName = newmfgName.replace(">","&gt;");
                }
                if(newmfgName.contains("'")){
                    newmfgName = newmfgName.replace("'","&apos;");
                }
                if(newmfgName.contains("'")){
                    newmfgName = newmfgName.replace("\"","&quot;");
                } 
                
                //Check Manufacturer Code for Special Characters
                newmfgCode = mfgCode[j];
                if(newmfgCode.contains("&")){
                    newmfgCode = mfgCode[j].replace("&","&amp;");
                }                 
                
                code = Node.textNode("code", newmfgCode);
                name = Node.textNode("name", newmfgName);
                list += Node.textNode("manufacturer", code + name);
                
            } 
            list =  Node.XML_HEADER + Node.textNode("manufacturers", list);            
            return list;
        }
        return null;
    }
    
   public int open(SROconnect conn)  {
        ResultSet rs =  null;
        try {
            debug ("Attempting to load Manufacturer Codes object for mfgr code " );
            isValid = false;
            String queryString = "SELECT DISTINCT valid_code, misc_alpha4, description";
            queryString += " FROM pub.codes_s";            
            queryString += " WHERE (pub.codes_s.code_type = 'MANUFACTURER'"; 
            queryString += " OR pub.codes_s.code_type = 'PARENT MFGR')";            
            queryString += " ORDER BY pub.codes_s.description";
            rs = conn.runQuery(queryString);
            int j = 0;
            if (rs != null) {
                System.out.println("Eureka! We have a Code!");                
                while (rs.next()) {
                    if (j > 49) {
                        break;
                    }
                    if (j >= ARRAY_SIZE ) {
                        System.out.println("Manufacturer Codes overflow in GPSmfgrNames.java.");
                        rs.close();
                        return MANUFACTURER_NAMES_SIZE_OVERFLOW;
                    }
                    description = rs.getString("description");                    
                    if (description != null && !"".equals(description)) {
                        parentCode = rs.getString("misc_alpha4");
                        if (parentCode != null && !"".equals(parentCode)) {                            
                            mfgCode[j] = parentCode;
                            mfgName[j] = rs.getString("description");
                            j++;
                        } 
                        mfgCode[j] = rs.getString("valid_code");
                        mfgName[j] = rs.getString("description");
                        j++;
                    }                    
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                isValid = true;
                size = j;
                if (size == 0) {
                    debug ("No Manufacturer Codes were found.");
                    return MANUFACTURER_NAMES_EMPTY;
                } else {
                    debug ("Loaded " + size + " Manufacturer Codes.");
                    return MANUFACTURER_NAMES_OK;
                }    
            }
            debug ("Failed to obtain Result Set when attempting to load Manufacturer Codes.");
            return MANUFACTURER_NAMES_DATABASE_ERROR;
        } catch (Exception e) {
            debug ("Database error when attempting to load Manufacturer Codes.");
            e.printStackTrace();
            return MANUFACTURER_NAMES_DATABASE_ERROR;
        }
    }
    
    public int open(SROconnect conn, WWWconnect conn1, String aMfgrName)  {
        ResultSet rs =  null;
        ResultSet rc =  null;        
        try {
            debug ("Attempting to load Manufacturer Names object for manufacturer " + aMfgrName );
            isValid = false;
            String queryString = "SELECT DISTINCT description, misc_alpha4, valid_code, code_type";
            queryString += " FROM pub.codes_s";            
            queryString += " WHERE (codes_s.code_type = 'MANUFACTURER'"; 
            queryString += " OR codes_s.code_type = 'PARENT MFGR')";      
            queryString += " AND description LIKE '" + aMfgrName + "%'";            
            queryString += " ORDER BY description";            
            rs = conn.runQuery(queryString);
            int j = 0; 
            int n = 0;
            if (rs != null) {
                System.out.println("Eureka! We have a Name!");
                while (rs.next()) {
                    if (j > 25) {
                        break;
                    }
                    if (j >= ARRAY_SIZE ) {
                        System.out.println("Manufacturer Names overflow in GPSmfgrNames.java.");
                        rs.close();
                        return MANUFACTURER_NAMES_SIZE_OVERFLOW;
                    }                    
                    String qString = "SELECT TOP 1 part_num";
                    qString += " FROM pub.catalogitem";            
                    qString += " WHERE list_type = 'Catalog'";
                    qString += " AND sales_subcat = '" + rs.getString("valid_code") + "'";                    
                    rc = conn1.runQuery(qString);
                    if (rc != null) {                     
                        parentCode = rs.getString("misc_alpha4"); 
                        codetype = rs.getString("code_type");
                       /* if ("MANUFACTURER".equals(codetype) && "".equals(parentCode)){                                             
                            mfgCode[j] = rs.getString("valid_code");
                            mfgName[j] = rs.getString("description");
                            j++;
                        } else {
                            if ("PARENT MFGR".equals(codetype)){                                             
                                mfgCode[j] = rs.getString("valid_code");
                                mfgName[j] = rs.getString("description");
                                j++;
                            }
                        } */ 
                        if ("PARENT MFGR".equals(codetype)){                                             
                           mfgCode[j] = rs.getString("valid_code");
                           mfgName[j] = rs.getString("description");
                           j++;
                        }
                        if ("MANUFACTURER".equals(codetype)){                                             
                            mfgCode[j] = rs.getString("valid_code");
                            mfgName[j] = rs.getString("description");
                            j++;
                        }                        
                    }                    
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                rc.close();
                rc = null;
                conn1.closeStatement();                
                size = j;                   
                isValid = true;                
                if (size == 0) {
                    debug ("No Manufacturer Names were found.");
                    return MANUFACTURER_NAMES_EMPTY;
                } else {
                    debug ("Loaded " + size + " Manufacturer Names.");
                    return MANUFACTURER_NAMES_OK;
                }    
            }
            debug ("Failed to obtain Result Set when attempting to load Manufacturer Names.");
            return MANUFACTURER_NAMES_DATABASE_ERROR;
        } catch (Exception e) {
            debug ("Database error when attempting to load Manufacturer Names.");
            e.printStackTrace();
            return MANUFACTURER_NAMES_DATABASE_ERROR;
        }
    }
    
    public int size() {
        return size;
    }    
}
