/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gps.util;

import OEdatabase.WWWconnect;
import XML.util.*;
import java.sql.ResultSet;
import java.util.ArrayList;
/**
 *
 * @author dunlop
 */
public class GPSmfgrAliasNames {
    private boolean debugSwitch = false;
    private final String VERSION = "1.5.00";
    
    private final int ARRAY_SIZE = 100;
    private boolean isValid = false;
    private String[] mfgCode = new String[ARRAY_SIZE];
    private String[] mfgName = new String[ARRAY_SIZE];    
    private String[] mfgActive = new String[ARRAY_SIZE]; 
    private String[] mfgUserID = new String[ARRAY_SIZE];
    private int size = 0;    
    // Constants
    private final String INVALID = "*invalid*";
    public final int MANUFACTURER_NAMES_EMPTY = 1;
    public final int MANUFACTURER_NAMES_OK = 0;
    public final int MANUFACTURER_NAMES_DATABASE_ERROR = -1;
    public final int MANUFACTURER_NAMES_SIZE_OVERFLOW = -2;    
    boolean activeBoolean = false;
    
    /** Creates a new instance of GPSmfgrNames */
    public GPSmfgrAliasNames() {
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
                work = "\"" + mfgCode[j] + "\",\"" + mfgName[j] + "\",\"" + mfgActive[j] + "\",\"" + mfgUserID[j] + "\"";
                debug (work);
                list.add(work);
            }
            return list;
        }
        return null;
    }    
    
    public int open(WWWconnect conn)  {
        ResultSet rs =  null;
        try {
            debug ("Attempting to load Manufacturer Alias Name object for mfgr names " );
            isValid = false;
            String queryString = "SELECT mfg_code, mfg_alias, active, audit_userid";
            queryString += " FROM pub.ps_index_mfg";
            queryString += " ORDER BY mfg_alias";
            rs = conn.runQuery(queryString);
            int j = 0;
            if (rs != null) {
                System.out.println("Eureka! We have a Manufacturer Alias Name!");                
                while (rs.next()) {
                    if (j >= ARRAY_SIZE ) {
                        System.out.println("Manufacturer Alias Names overflow in GPSmfgrNames.java.");
                        rs.close();
                        return MANUFACTURER_NAMES_SIZE_OVERFLOW;
                    }
                    mfgName[j] = rs.getString("mfg_alias");                    
                    mfgCode[j] = rs.getString("mfg_code");
                    activeBoolean = rs.getBoolean("active");
                    mfgActive[j] = activeBoolean ? "Yes" : "No";
                    mfgUserID[j] = rs.getString("audit_userid");
                    j++;
                }
                rs.close();
                rs = null;
                conn.closeStatement();
                isValid = true;
                size = j;
                if (size == 0) {
                    debug ("No Manufacturer Alias Names were found.");
                    return MANUFACTURER_NAMES_EMPTY;
                } else {
                    debug ("Loaded " + size + " Manufacturer Alias Names.");
                    return MANUFACTURER_NAMES_OK;
                }    
            }
            debug ("Failed to obtain Result Set when attempting to load Manufacturer Alias Names.");
            return MANUFACTURER_NAMES_DATABASE_ERROR;
        } catch (Exception e) {
            debug ("Database error when attempting to load Manufacturer Alias Names.");
            e.printStackTrace();
            return MANUFACTURER_NAMES_DATABASE_ERROR;
        }
    }    
}
