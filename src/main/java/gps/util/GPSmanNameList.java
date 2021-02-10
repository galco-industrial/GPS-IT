/*
 * GPSmanNameList.java
 *
 * Created on October 7, 2008, 3:27 PM
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
 *
 * I am a list of manufacturer codes and names maintained so I do not have to repeatedly do
 * a database query every time a manufacturer code/name changes. Here is how I work.
 *
 * If I am asked to return a manufacturer name for a given code,  first check my arraylist
 * cache. if found, I return the result.
 * If not found, I do a SQL query and add it to the cache and return.
 */
public class GPSmanNameList {
            
    private boolean debugSw = false;
    private static final String version = "1.5.00";

    private String manCode = "";
    private ArrayList<String> manCodes = new ArrayList<String>();
    private String manName = "";
    private ArrayList<String> manNames = new ArrayList<String>();

    /** Creates a new instance of GPSmanNameList */
    public GPSmanNameList() {
    }
    
    public String getName(SROconnect conn, String aManCode) {
        if (aManCode.equals("")) {
            return "";
        }
        if (aManCode.equals(manCode)) {
            return manName;
        }
        int index = manCodes.indexOf(aManCode);
        if (index > -1) {
            manCode = aManCode;
            manName = manNames.get(index);
            return manName;
        }
        try {
            manName = GPSmanCodes.getMfgrName(conn, aManCode);
            if (manName != null) {
                manCode = aManCode;
                manCodes.add(manCode);
                manNames.add(manName);
                return manName;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "***Run Time Error***";
        }
    }
    
}
