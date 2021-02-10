/*
 * gpsdbf2.java
 *
 * Created on March 26, 2009, 2:36 PM
 *
 * I am used to create option list data in the ps_option_lists table
 * for Galco web landing pages in catalog.htm
 *
 * I plow through the catalogitem table and for every part of type Catalog
 * I create filter option list data for the following landing pages:
 * a family level search using global rules
 * a family/mfgr level search using global rules
 * a family/subfamily level search using family/subfamily rules
 * a family/subfamily/mfgr level search using family/subfamily rules
 *
 * Modification History
 *
 * 06/08/2010 DES Begin modification to support child Select Box Value Generation
 *                Additions/Changes are flagged as ECP-1
 * 08/19/2016 DES This version "gpsdbf2.java" has been deprecated and is replaced by
 *                "gpsdbf3.java"
 *
 */

package gpsParmData;

import OEdatabase.WDSconnect;
import OEdatabase.SROconnect;
import OEdatabase.WWWconnect;
import gps.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import sauter.util.*;

/**
 *
 * @author Sauter
 * @version
 */
public class gpsdbf2 extends HttpServlet {
                
    private int debugLevel = 0;
    private final String REDIRECT = "index.jsp";
    private final String SERVLET_NAME = "gpsdbf2.java";
    private final String VERSION = "1.5.01";
    
    private String beginPair = "{[";   // ECP-1
    private int childArrayIndex = -1;  // ECP-1
    private String endPair = "]}";     // ECP-1
    private String midPair = "]~[";    // ECP-1
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
         
        boolean completedOK = false;
        GPScvt cvt = new GPScvt();
        String dataType;
        String familyCode;
        String familyName;
        GPSfieldSet fieldSetFamily = null;
        GPSfieldSet fieldSetSubfamily = null;
        int gtot = 0;
        int i = 0;
        int ichunk = 0;
        int index = 0;
        int iWork = 0;
        boolean junk;
        int kount = 0;
        String message;
        ArrayList<String> mfgrCodes = new ArrayList<String>();
        String mfgrCode;
        GPSopts optionsArray; // A handle for a single options Array object
        int optionsArrayIndex = 0;
        ArrayList<GPSopts> optionsArraysFamily = new ArrayList<GPSopts>(); // List of family Options Arrays objects
        int[] optionsArraysFamilyVectorMap = new int[100]; // Indeces of OptionsArraysFamily objects
                                                          // indexed by family seqNum
        ArrayList<GPSopts> optionsArraysSubfamily = new ArrayList<GPSopts>(); // List of subfamily Options Arrays objects
        int[] optionsArraysSubfamilyVectorMap = new int[100]; // Indeces of OptionsArraysSubfamily objects
                                                          // indexed by subfamily seqNum
        ArrayList<String> optionsSet = null;
        int[] parmSeqNums = new int[100];  // a list of seq nums of parms found for a part number
                                           // terminated by an element containing zero.
        GPSparmSet parmSet = null;
        String parmString = "";
        String parmValue = "";
        String parmValueNext = "";
        String[] parmValues = new String[100]; // parm values for a part; indexed by seqNum
        String partNumber = "";
        int[] partNumSeqNums = new int[100]; // this is an array of all seq nums found for a given partnum in a resultset
        String productLineCode;
        String productLineName;
        String queryString = "";
        int rc = 0;
        ResultSet rs1 = null;
        ResultSet rs3 = null;
        GPSrules ruleSet = null;     
        int[] ruleSetFamilyVectorMap = new int[100];
        int ruleSetFamilyVectorMapCount = 0;
        GPSrules[] ruleSetsFamily = null;
        int[] ruleSetSubfamilyVectorMap = new int[100];
        int ruleSetSubfamilyVectorMapCount = 0;
        GPSrules[] ruleSetsSubfamily = null;
        int seqNum = 0;
        String SQLCommand = "";
        GPSsubfamilyCodes subfamilyCodes = null;
        String subfamilyCode = "";
        String subfamilyCodePrev = "";
        boolean subfamiliesExist = true;
        String subfamilyName = "";
        String work;
        
        
        //System.out.println("Max memory is " + Runtime.getRuntime().maxMemory());
        
        if (true) {
            request.setAttribute("message", "Module " + SERVLET_NAME + "; This servlet has been deprecated. Please use gpsdbf3.java.");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }

         
        familyCode = request.getParameter("familyCode");
        if (familyCode == null) {
            familyCode = "";
        }
        
        if (familyCode.equals("")) {    // familyCode is mandatory
            System.out.println(SERVLET_NAME + " was missing family code.");
            response.sendRedirect(REDIRECT);
            return;
        }
        debug (2, "familyCode:  " + familyCode);

        WDSconnect conn1 = new WDSconnect();                // Connect to WDS database 
        if (!conn1.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to WDS database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        SROconnect conn2 = new SROconnect();                // Connect to SRO database 
        if (!conn2.connect()) {         
            conn1.close();
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to SRO database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        WWWconnect conn3 = new WWWconnect();                // Connect to Web database 
        if (!conn3.connect()) {         
            conn1.close();
            conn2.close();
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to WWW database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        // Look up Family Name               

        familyName = GPSfamilyCodes.lookUpFamilyName(conn1, familyCode);
               
        // Create a subfamily code object here and load it
        // make sure there is at least 1 subfamily code
        
        subfamilyCodes = new GPSsubfamilyCodes();
        rc = subfamilyCodes.open(conn1, familyCode);
        if (rc == 1) {
            GPSproductLines productLines = new GPSproductLines();
            if (productLines.open(conn1) != 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                conn1.close();
                closeConn2(conn2);
                closeConn3(conn3);
                return;
            }   
            ArrayList <String> lines = productLines.getArrayList();
            try {
                request.setAttribute("statusMessage", "No subfamily codes were found for Family Code: " + familyCode);
                request.setAttribute("lines", lines);
                RequestDispatcher view = request.getRequestDispatcher("gpsdbf1.jsp");
                view.forward(request,response);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + ": <br />" + e);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
            } finally {
                closeConn1(conn1);
                closeConn2(conn2);
                closeConn3(conn3);
            }
            return;
        }
               
        if (rc != 0) {
            System.out.println("Unexpected error " + rc + " reading subfamily codes.");
            response.sendRedirect(REDIRECT);
            conn1.close();
            conn2.close();
            conn3.close();
            return;
        }
        
        // The subfamilesExist flag is on by default.
        // If there is only one subfamily code for this family
        // AND it is the same as the family code:
        // turn off the subfamilesExist flag
        
        if (subfamilyCodes.size() == 1) {
            work = subfamilyCodes.getSubfamilyCode(0);
            if (work.equals(familyCode)) {
                subfamiliesExist = false;
                subfamilyCode = work;
                debug (2, "Subfamily codes do not exist for family code: " + familyCode);
            }
        }
        
        if (subfamiliesExist && !subfamilyCode.equals("") && subfamilyName.equals("") ) {
            subfamilyName = GPSsubfamilyCodes.lookUpSubfamilyName(conn1, familyCode, subfamilyCode);
            debug (2, "Subfamily Name is set to " + subfamilyName + " for subfamily code " + subfamilyCode);
        }
        
        // Generate Fieldset for family level searches.
        
        fieldSetFamily = new GPSfieldSet();
        try {
            ruleSetsFamily = fieldSetFamily.getRules(conn1, familyCode, "*", GPSfieldSet.SEARCH_ORDER);
            // ruleSetsFamily is an array of family level parm fields and their corresponding rulesets
        } catch (Exception e) {
            message = "Unexpected error reading Family Level rules for family code " 
                    + familyCode + " in " + SERVLET_NAME;
            System.out.println(message);
            response.sendRedirect(REDIRECT);
            conn1.close();
            conn2.close();
            conn3.close();
            return;
        }
        debug (2, "Found " + fieldSetFamily.size() + " Family Level rulesets for family code " 
                    + familyCode + " in " + SERVLET_NAME);
            
        // Make a vector map where seq num as an index points to ruleset # in searchFieldSet[]
        for (i = 0; i < 100; i++) {
            ruleSetFamilyVectorMap[i] = -1; // initially vector map is empty;  index i = seq num
            optionsArraysFamilyVectorMap[i] = -1;
        }
        ruleSetFamilyVectorMapCount = 0; // number of parm field rulesets currently in vector map
        for (i = 0; i < fieldSetFamily.size(); i++) {
            ruleSet = ruleSetsFamily[i];
            seqNum = ruleSet.getSeqNum();
            ruleSetFamilyVectorMap[seqNum] = i;
            dataType = ruleSet.getDataType();
            if (ruleSet.getParmDelimiter() == null) {
                ruleSet.setParmDelimiter("");
            }
            if ("NS".contains(dataType) && ruleSet.getQobject().equals("S") && ruleSet.getQtextBoxSize() == 0 ) {
                // Get name of pre-built select boxes for numeric / string data types
                work = ruleSet.getQselectBoxName();
                if (work == null) {
                    ruleSet.setQselectBoxName("");
                }
            }
            ruleSetFamilyVectorMapCount++;
        }
        // Note: ruleSetVectorMapCount should now equal fieldSet.size()
        debug (2, SERVLET_NAME + " found " + ruleSetFamilyVectorMapCount + " Family Level Search field ruleSets. ");
            
            //////////////////////////////////////////////////////////
            // Do not read this comment; it is no longer applicable //
            //////////////////////////////////////////////////////////
            
        if (ruleSetFamilyVectorMapCount != fieldSetFamily.size()) {
            System.out.println(SERVLET_NAME + " Error processing Family Level search fields; Aborting...");
            response.sendRedirect(REDIRECT); 
            conn1.close();
            conn2.close();
            conn3.close();
            return;
        }
        
        /////////////////////////////////////////////////////////////
        //     Now we get part numbers and their parametric values //
        /////////////////////////////////////////////////////////////
     
        try {
            queryString =  "SELECT part_num, family_code, subfamily_code, sales_subcat";
            queryString += " FROM pub.catalogitem";
            queryString += " WHERE family_code = '" + familyCode + "'";
            queryString += " AND list_type = 'Catalog'";
            queryString += " ORDER BY subfamily_code";
            debug (2, "SQL statement is " + queryString);
            rs3 = conn3.runQuery(queryString); // a whole lot of junk happens here
                
            /////////////////////////////////////////////////////////////////////////////
            // Now we will Process the Result Set and                                  //
            // create options arrays for the search parms                              //
            /////////////////////////////////////////////////////////////////////////////
            
            if (rs3 != null) {
                debug (2, "I got the Result Set; Now building options for Family Code " + familyCode);
                // Initialization 
                parmSeqNums[0] = 0; // Init end of list with a zero entry
                for (i = 0; i < 100; i++) {
                    parmValues[i] = "";
                }
                mfgrCodes = new ArrayList<String>();
                debug (2, "Preparing to process result set of part numbers found for this family.");
                while (rs3.next()) {
                    gtot++;
                    if (++ichunk == 500) {
                        debug(5, "Processed " + gtot + " parts so far...");
                        debug(5, "Free Heap is now " + fmt(Runtime.getRuntime().freeMemory()));
                        ichunk = 0;
                    }
                    partNumber = rs3.getString("part_num").toUpperCase();
                    mfgrCode = rs3.getString("sales_subcat");
                    subfamilyCode = rs3.getString("subfamily_code");
                    if (!subfamilyCode.equals(subfamilyCodePrev)) { // Did subfamily code just change?
                        ////////////////////////////////////////////
                        // New Subfamily Code control break here  //
                        ////////////////////////////////////////////
                        if (!subfamilyCodePrev.equals("")) {
                            // Delete the old SF opts objects
                            debug (5, "Processed " + kount + " subfamily part numbers.");
                            if (!deleteOptionsFromDatabase(conn1, familyCode, subfamilyCodePrev, "*", "*")) {
                                closeResultSet(rs3);
                                closeConn1(conn1);
                                closeConn2(conn2);
                                closeConn3(conn3);
                                response.sendRedirect(REDIRECT);
                                return;
                            }
                            // Write out new SF options to ps_options table here.
                            while (optionsArraysSubfamily.size() > 0) {
                                optionsArray = optionsArraysSubfamily.get(0);
                                
                                if (!writeNewOptionsToDatabase(conn1, familyCode, subfamilyCodePrev, "*", "*", optionsArray)) {
                                    System.out.println(SERVLET_NAME + " Error writing subfamily search parms to database; Aborting...");
                                    response.sendRedirect(REDIRECT); 
                                    closeResultSet(rs3);
                                    closeConn1(conn1);
                                    closeConn2(conn2);
                                    closeConn3(conn3);
                                    return;
                                }
                                deleteOptionsArray(optionsArraysSubfamily, optionsArraysSubfamilyVectorMap); 
                            }
                        }
                        // Then load new rulesets for the new SF code
                        fieldSetSubfamily = new GPSfieldSet();
                        try {
                            ruleSetsSubfamily = fieldSetSubfamily.getRules(conn1, familyCode, subfamilyCode, GPSfieldSet.SEARCH_ORDER);
                        } catch (Exception e) {
                            message = "Unexpected error reading subfamily rules for family code " 
                                           + familyCode + " subfamily code " + subfamilyCode + " in " + SERVLET_NAME;
                            System.out.println(message);
                            response.sendRedirect(REDIRECT);
                            closeResultSet(rs3);
                            closeConn1(conn1);
                            closeConn2(conn2);
                            closeConn3(conn3);
                            return;
                        }
                        debug (2, "Found " + fieldSetSubfamily.size() + " subfamily rulesets for family code " 
                                    + familyCode + " subfamily code " + subfamilyCode + " in " + SERVLET_NAME);
                        // Do new SF code init
                        // Make a vector map where seq num as an index points to ruleset # in searchFieldSet[]
                        for (i = 0; i < 100; i++) {
                            ruleSetSubfamilyVectorMap[i] = -1; // initially vector map is empty;  index i = seq num
                            optionsArraysSubfamilyVectorMap[i] = -1;
                        }
                        ruleSetSubfamilyVectorMapCount = 0; // number of parm field rule sets currently in vector map
                        for (i = 0; i < fieldSetSubfamily.size(); i++) {
                            ruleSet = ruleSetsSubfamily[i];
                            seqNum = ruleSet.getSeqNum();
                            ruleSetSubfamilyVectorMap[seqNum] = i;
                            dataType = ruleSet.getDataType();
                            if (ruleSet.getParmDelimiter() == null) {
                                ruleSet.setParmDelimiter("");
                            }
                            if ("NS".contains(dataType) && ruleSet.getQobject().equals("S") && ruleSet.getQtextBoxSize() == 0 ) {
                                // Get name of pre-built select boxes for numeric / string data types
                                work = ruleSet.getQselectBoxName();
                                if (work == null) {
                                    ruleSet.setQselectBoxName("");
                                }
                            }
                            ruleSetSubfamilyVectorMapCount++;
                        }
                        debug (2, SERVLET_NAME + " found " + ruleSetSubfamilyVectorMapCount + " Subfamily Search field ruleSets. ");
            
                        //////////////////////////////////////////////////////////
                        // Do not read this comment; it is no longer applicable //
                        //////////////////////////////////////////////////////////
                        if (ruleSetSubfamilyVectorMapCount == 0 || ruleSetSubfamilyVectorMapCount != fieldSetSubfamily.size()) {
                            System.out.println(SERVLET_NAME + " Error processing subfamily search parms; Aborting...");
                            response.sendRedirect(REDIRECT); 
                            closeResultSet(rs3);
                            closeConn1(conn1);
                            closeConn2(conn2);
                            closeConn3(conn3);
                            return;
                        }
                        subfamilyCodePrev = subfamilyCode;
                        kount = 0;
                        // Reset the Child Array starting index
                        childArrayIndex = -1;  // ECP-1
                        debug (6, "Now processing Family Code/Subfamily Code " + familyCode + "/" + subfamilyCode);
                        ////////////////////////////////////////////
                        // End of Subfamily Code control break    //
                        ////////////////////////////////////////////  
                        System.gc();
                        debug (5, "Free Heap after garbage collector is " + fmt(Runtime.getRuntime().freeMemory()));
                    }
                    // Look up parm data for this part number
                    debug (8, " Looking up parm data for part number " + partNumber);
                    queryString =  "SELECT seq_num, parm_value";
                    queryString += " FROM pub.ps_parm_data";
                    queryString += " WHERE part_num = '" + partNumber + "'";
                    rs1 = conn1.runQuery(queryString);
                    if (rs1 != null) {
                        i = 0;
                        while (rs1.next()) {
                            // only load searchable parm values
                            seqNum = rs1.getInt("seq_num");
                            if (ruleSetSubfamilyVectorMap[seqNum] > -1)  {  
                                parmSeqNums[i++] = seqNum;
                                parmValues[seqNum] = rs1.getString("parm_value");
                                debug (10, "  Set parm value for seq num " + seqNum + " to '" + parmValues[seqNum] + "'");
                            }    
                        }
                        rs1.close();
                        rs1 = null;
                        conn1.closeStatement();
                    }
                    parmSeqNums[i++] = 0; // Mark end of list with a zero entry
                    debug (8, " Found " + i + " parm values for part number " + partNumber);
                    // Do Family Level ditties first.... for each family level parm value, add it to its option list
                    for (iWork = 0; parmSeqNums[iWork] > 0; iWork++) {
                        seqNum = parmSeqNums[iWork];
                        if (ruleSetFamilyVectorMap[seqNum] > -1)  {  
                            parmValue = parmValues[seqNum];
                            if (!parmValue.equals("")) { // if an option value is present
                                optionsArrayIndex = optionsArraysFamilyVectorMap[seqNum]; // find out which option array it goes inside
                                if (optionsArrayIndex == -1) { // if options array does not exist yet, build one 
                                    optionsArray = new GPSopts();
                                    junk = optionsArray.init(conn1, familyCode, "*", seqNum);
                                    optionsArraysFamily.add(optionsArray); // Add it to the list of Family options arrays
                                    optionsArrayIndex = optionsArraysFamily.size() - 1; // calculate it's index
                                    optionsArraysFamilyVectorMap[seqNum] = optionsArrayIndex; // update the vector map
                                } else {
                                    optionsArray = optionsArraysFamily.get(optionsArrayIndex);
                                }
                                optionsArray.addOption(parmValue); // Add the parm value
                                debug (10, "   Added raw value '" + parmValue + "' for Family seq num " + seqNum + " to options Array object");
                            } // end if
                        } // end if
                    } // end for
                                       
                    // For each Subfamily parm value, add it to its option list
                    for (iWork = 0; parmSeqNums[iWork] > 0; iWork++) {
                        seqNum = parmSeqNums[iWork];
                        parmValue = parmValues[seqNum];
                        if (!parmValue.equals("")) { // if an option value is present
                            optionsArrayIndex = optionsArraysSubfamilyVectorMap[seqNum]; // find out which option array it goes inside
                            if (optionsArrayIndex == -1) { // if options array does not exist yet, build one 
                                optionsArray = new GPSopts();
                                junk = optionsArray.init(conn1, familyCode, subfamilyCode, seqNum);
                                optionsArraysSubfamily.add(optionsArray); // Add it to the list of subfamily options arrays
                                optionsArrayIndex = optionsArraysSubfamily.size() - 1; // calculate it's index
                                optionsArraysSubfamilyVectorMap[seqNum] = optionsArrayIndex; // update the vector map
                            } else {
                                optionsArray = optionsArraysSubfamily.get(optionsArrayIndex);
                            }
                            optionsArray.addOption(parmValue); // Add the parm value
                            debug (10, "   Added raw value '" + parmValue + "' for subfamily seq num " + seqNum + " to options Array object");
                            parmValues[seqNum] = ""; // then erase it to clean up after this part num
                        } // end if
                    } // end for
                    kount++;
                } // end While loop plowing thru recordset
                closeResultSet(rs3);
                // Finish generating last subfamily options list in database 
                if (!subfamilyCodePrev.equals("")) {
                    // Delete the old SF opts objects
                    debug (5, "Processed " + kount + " subfamily part numbers.");
                    if (!deleteOptionsFromDatabase(conn1, familyCode, subfamilyCodePrev, "*", "*")) {
                        closeResultSet(rs3);
                        closeConn1(conn1);
                        closeConn2(conn2);
                        closeConn3(conn3);
                        response.sendRedirect(REDIRECT);
                        return;
                    }
                    // Reset the Child Array starting index
                    childArrayIndex = -1;  // ECP-1
                    // Write out new SF options to ps_options table here.
                    while (optionsArraysSubfamily.size() > 0) {
                        
                        optionsArray = optionsArraysSubfamily.get(0);
                        if (!writeNewOptionsToDatabase(conn1, familyCode, subfamilyCodePrev, "*", "*", optionsArray)) {
                            System.out.println(SERVLET_NAME + " Error writing subfamily search parms to database; Aborting...");
                            response.sendRedirect(REDIRECT); 
                            closeResultSet(rs3);
                            closeConn1(conn1);
                            closeConn2(conn2);
                            closeConn3(conn3);
                            return;
                        }
                        deleteOptionsArray(optionsArraysSubfamily, optionsArraysSubfamilyVectorMap); 
                    }
                }
                // Finish generating last family level options list in database 
                if (!familyCode.equals("")) {
                    // Delete the old Family level opts objects
                    if (!deleteOptionsFromDatabase(conn1, familyCode, "*", "*", "*")) {
                        closeResultSet(rs3);
                        closeConn1(conn1);
                        closeConn2(conn2);
                        closeConn3(conn3);
                        response.sendRedirect(REDIRECT);
                        return;
                    }
                    // Reset the Child Array starting index
                    childArrayIndex = -1;  // ECP-1
                    // Write out new Family level options to ps_options table here.
                    while (optionsArraysFamily.size() > 0) {
                        optionsArray = optionsArraysFamily.get(0);
                        if (!writeNewOptionsToDatabase(conn1, familyCode, "*", "*", "*", optionsArray)) {
                            System.out.println(SERVLET_NAME + " Error writing family search parms to database; Aborting...");
                            response.sendRedirect(REDIRECT); 
                            closeResultSet(rs3);
                            closeConn1(conn1);
                            closeConn2(conn2);
                            closeConn3(conn3);
                            return;
                        }
                        deleteOptionsArray(optionsArraysFamily, optionsArraysFamilyVectorMap); 
                    }
                }
            } // end if
        } catch (Exception e) {
            message = "Unexpected error processing parm data for family code " 
                        + familyCode + " in " + SERVLET_NAME;
            System.out.println(message);
            e.printStackTrace();
            response.sendRedirect(REDIRECT);
            closeResultSet(rs3);
            closeConn1(conn1);
            closeConn2(conn2);
            closeConn3(conn3);
            return;
        }
        debug (2, "Processing complete.");
        debug (2, "Successfully built landing page options for family code " + familyCode + " - " + familyName);
                
        GPSproductLines productLines = new GPSproductLines();
        if (productLines.open(conn1) != 0) {
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            productLines = null;
            conn1.close();
            return;
        }   
        ArrayList <String> lines = productLines.getArrayList();
    
        try {
            request.setAttribute("statusMessage", "Option lists were successfully created for Family Code " + 
                    familyCode + " - " + familyName);
            request.setAttribute("lines", lines);
            RequestDispatcher view = request.getRequestDispatcher("gpsdbf1.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + ": <br />" + e);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } finally {
            closeConn1(conn1);
            closeConn2(conn2);
            closeConn3(conn3);
        }
    }
    
    private void closeConn1(WDSconnect con) {
        con.closeStatement();
        con.close();
        con = null;
    }
    
    private void closeConn2(SROconnect con) {
        con.closeStatement();
        con.close();
        con = null;
    }
    
    private void closeConn3(WWWconnect con) {
        con.closeStatement();
        con.close();
        con = null;
    }
    
    private void closeResultSet(ResultSet rs) {
        try {
            rs.close();
        } catch (Exception e) {
            // ignore close errors       
        } finally {
            rs = null;    
        }       
    }
        
    private void debug (int level, String x) {
        if (debugLevel >= level) {
            System.out.println(x);
        }
    }
    
    private void deleteOptionsArray(ArrayList<GPSopts> optionsArraysMap, int[] vectorMap) { 
        GPSopts optionsArray; // A handle for a single options Array object
        optionsArray = optionsArraysMap.remove(0);
        vectorMap[optionsArray.getSeqNum()] = -1;
        optionsArray = null;
    }
    
    private void deleteOptionsArrays(ArrayList<GPSopts> optionsArraysMap, int[] vectorMap) { 
        GPSopts optionsArray; // A handle for a single options Array object
        while (optionsArraysMap.size() > 0) {
            optionsArray = optionsArraysMap.remove(0);
            vectorMap[optionsArray.getSeqNum()] = -1;
            optionsArray = null;
        }
    }
    
    private boolean deleteOptionsFromDatabase(
            WDSconnect aConn,
            String aFamilyCode,
            String aSubfamilyCode,
            String aMfgrCode,
            String aSeriesCode) {
        
        String message = "";
        boolean rc;
        String SQLCommand = "";
        try {
            debug (5, "Attempting to delete option lists for family/subfamily/mfgr/series: " 
                    + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "/" + aSeriesCode + "...");
            SQLCommand = "DELETE FROM pub.ps_option_lists";
            SQLCommand += " WHERE family_code = '" + aFamilyCode + "'";
            SQLCommand += " AND subfamily_code = '" + aSubfamilyCode + "'";
            SQLCommand += " AND mfgr_code = '" + aMfgrCode + "'";
            SQLCommand += " AND series_code = '" + aSeriesCode + "'";
            // SQLCommand += " AND option_list_type = 'P'";                       //  Pre ECP-1
            SQLCommand += " AND (option_list_type = 'P' OR option_list_type = 'C')";   //  ECP-1
            debug (5, SQLCommand);
            rc = aConn.runUpdate(SQLCommand);
            message = "  Options were";
            if (!rc) {
                message += " NOT";
            }
            message += " deleted successfully. ";
            debug (5, message);
            return true;
        } catch (Exception e) {
            message = "Unexpected error deleting options.";
            System.out.println(message);
            e.printStackTrace();
            return false;
        }
    }

    private String fmt(long junk) {
        String work = "";
        int k = 0;
        String src = Long.toString(junk);
        while (src.length() > 0) {
             if (++k == 4) {
                 work = "," + work;
                 k = 1;
             }
             work = src.substring(src.length() - 1) + work;
             src = src.substring(0, src.length() - 1);
        }
        return work;
    }
    
    private boolean writeNewOptionsToDatabase(
            WDSconnect aConn,
            String aFamilyCode,
            String aSubfamilyCode,
            String aMfgrCode,
            String aSeriesCode,
            GPSopts aOptionsArray) {
        
        String childPointer = "";                          // ECP-1
        int count;
        String message = "";
        int parentOptionIndex = 0;                         // ECP-1
        boolean rangeFound = false;                        // ECP-1
        String rawValue = "";                              // ECP-1
        boolean rc;
        int seqNum = aOptionsArray.getSeqNum();
        String SQLCommand = "";
        String timeStamp = DateTime.getTimeStamp();
        
        debug (5, timeStamp);
        debug (5, "Cooking options for family/subfamily/mfgr/series/index/SeqNum: " 
                    + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "/" + aSeriesCode
                    + "/" + seqNum + "...");
        aOptionsArray.cookOptionList();
        count = aOptionsArray.getListEntriesRawSize();  
        try {
            debug (5, "Writing " + count + " options for family/subfamily/mfgr/series/SeqNum/ParmName: " 
                    + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "/" + aSeriesCode
                    + "/" + seqNum + "/" + aOptionsArray.getParmName() + " to database...");
            SQLCommand = "INSERT INTO pub.ps_option_lists";
            SQLCommand += " (family_code, subfamily_code, mfgr_code, series_code,";
            SQLCommand += " option_list_type, option_list_index, ";
            SQLCommand += " option_value_index,  option_value_raw, option_value_cooked,option_value_units,";
            SQLCommand += " date_created ) VALUES (";
            SQLCommand += " '" + aFamilyCode + "'";
            SQLCommand += ",'" + aSubfamilyCode + "'"; 
            SQLCommand += ",'" + aMfgrCode + "'";
            SQLCommand += ",'" + aSeriesCode + "'";
            SQLCommand += ",'P'";
            SQLCommand += "," + seqNum;
            SQLCommand += ", 0";
            SQLCommand += ", " + seqNum;
            SQLCommand += ",'" + aOptionsArray.getParmName() + "'";
            SQLCommand += ",'" + aOptionsArray.getDisplayUnits() + "'";
            SQLCommand += ",'" + timeStamp + "'";
            SQLCommand += ")";
            debug (8, SQLCommand);
            rc = aConn.runUpdate(SQLCommand);
            message = "  Header Option 0 was";
            if (!rc) {
                message += " NOT";
            }
            message += " written to database successfully. ";
            debug (10, message);
            for (int i = 0; i < count; i++) {
                parentOptionIndex = i + 1;                              // ECP-1
                
                rangeFound = false;                                     // ECP-1
                childPointer = "";                                      // ECP-1
                rawValue = aOptionsArray.getListEntryRaw(i);            // ECP-1
                if (rawValue.startsWith(beginPair) &&                   // ECP-1
                    rawValue.indexOf(midPair,3) > 0 &&                  // ECP-1
                    rawValue.endsWith(endPair)) {                       // ECP-1
                    rangeFound = aOptionsArray.buildChildren(rawValue); // ECP-1
                    if (rangeFound) {                                   // ECP-1
                        childPointer = "#" + ++childArrayIndex;         // ECP-1
                        debug (5, "Generating child array # " + childArrayIndex);
                    }                                                   // ECP-1
                }
                if (aOptionsArray.getIsImageSelectBox()) {
                    childPointer = aOptionsArray.getListEntryImage(i);
                }
                // ECP-1
                // Add code here to inspect the raw value
                // If the raw value is a range:
                //     set the range flag ON
                //     increment the child Array index
                //     create a substitute range raw value entry that contains
                //     a reference to the childArray index
                //     this will be writtten to the display units field in this parent record
                // else
                //     set displayunits to blank              
                
                SQLCommand = "INSERT INTO pub.ps_option_lists";
                SQLCommand += " (family_code, subfamily_code, mfgr_code, series_code,";
                SQLCommand += " option_list_type, option_list_index, ";
                SQLCommand += " option_value_index,  option_value_raw, option_value_cooked, option_value_units,";
                SQLCommand += " date_created ) VALUES (";
                SQLCommand += " '" + aFamilyCode + "'";
                SQLCommand += ",'" + aSubfamilyCode + "'"; 
                SQLCommand += ",'" + aMfgrCode + "'";
                SQLCommand += ",'" + aSeriesCode + "'";
                SQLCommand += ",'P'";
                SQLCommand += "," + seqNum;
                SQLCommand += "," + parentOptionIndex;
                SQLCommand += ",'" + rawValue + "'";   // ECP-1
                SQLCommand += ",'" + aOptionsArray.getListEntryCooked(i) + "'";
                SQLCommand += ",'" + childPointer + "'";
                SQLCommand += ",'" + timeStamp + "'";
                SQLCommand += ")";
                debug (8, SQLCommand);
                rc = aConn.runUpdate(SQLCommand);
                message = "  Option " + i + " was";
                if (!rc) {
                    message += " NOT";
                }
                message += " written to database successfully. ";
                debug (10, message);
                if (rangeFound) {
                    boolean x = writeNewChildOptionsToDatabase(aConn, aFamilyCode, aSubfamilyCode, aMfgrCode, aSeriesCode, 
                                                   childArrayIndex, parentOptionIndex, aOptionsArray, rawValue);
                    
                }                
            }
            debug( 5, "Free Heap is now " + fmt(Runtime.getRuntime().freeMemory()));
            return true;
        } catch (Exception e) {
            message = "Unexpected error while writing options to database.";
            System.out.println(message);
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean writeNewChildOptionsToDatabase(
            WDSconnect aConn,
            String aFamilyCode,              // Family Code for the child options  array
            String aSubfamilyCode,           // Subfamily Code or "*" if this is a family level options list
            String aMfgrCode,                // Mfgr Code if applicable or "*" for all Mfgrs
            String aSeriesCode,              // Series Code for this mfgr code or * if all series codes for this mfgr
                                             // if Mfgr code is * then series must be a * too
            int aChildArrayIndex,             // index of this child array option list
            //String parentArrayIndex,         // index of parent option array; if parent array is also a child, then precede index with a "#""
            int parentArrayOptionIndex,      // index of range option within the parent option array
            GPSopts aOptionsArray,           // the options array object that contains all the possible parm values for this seq num
            String aRange) {                 // the range of options that defines what children will be stored in this child options array
        
        int count;
        String message = "";
        boolean rc;
        // int seqNum = aOptionsArray.getSeqNum();
        String SQLCommand = "";
        String timeStamp = DateTime.getTimeStamp();
        debug (5, timeStamp);
        debug (5, "Processing child options for range " + aRange + " parent in family/subfamily/mfgr/series/ChildArrayIndex: " 
                    + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "/" + aSeriesCode
                    + "/" + aChildArrayIndex + "...");
        count = aOptionsArray.getChildrenSize();
        if (count > 0) {
            try {
                debug (5, "Writing " + count + " child options for this range to the database...");
                SQLCommand = "INSERT INTO pub.ps_option_lists";
                SQLCommand += " (family_code, subfamily_code, mfgr_code, series_code,";
                SQLCommand += " option_list_type, option_list_index, ";
                SQLCommand += " option_value_index,  option_value_raw, option_value_cooked,option_value_units,";
                SQLCommand += " date_created ) VALUES (";
                SQLCommand += " '" + aFamilyCode + "'";
                SQLCommand += ",'" + aSubfamilyCode + "'"; 
                SQLCommand += ",'" + aMfgrCode + "'";
                SQLCommand += ",'" + aSeriesCode + "'";
                SQLCommand += ",'C'";
                SQLCommand += "," + aChildArrayIndex;
                SQLCommand += ", 0";
                //SQLCommand += ", '" + parentArrayIndex + "," + parentArrayOptionIndex + "'";
                SQLCommand += ", '" + parentArrayOptionIndex + "'";
                SQLCommand += ",'" + aOptionsArray.getParmName() + "'";
                SQLCommand += ",'" + aOptionsArray.getDisplayUnits() + "'";
                SQLCommand += ",'" + timeStamp + "'";
                SQLCommand += ")";
                debug (8, SQLCommand);
                rc = aConn.runUpdate(SQLCommand);
                message = "  Header (Option 0) for children was";
                if (!rc) {
                    message += " NOT";
                }
                message += " written to database successfully. ";
                debug (10, message);
                for (int i = 0; i < count; i++) {
                    SQLCommand = "INSERT INTO pub.ps_option_lists";
                    SQLCommand += " (family_code, subfamily_code, mfgr_code, series_code,";
                    SQLCommand += " option_list_type, option_list_index, ";
                    SQLCommand += " option_value_index,  option_value_raw, option_value_cooked, option_value_units,";
                    SQLCommand += " date_created ) VALUES (";
                    SQLCommand += " '" + aFamilyCode + "'";
                    SQLCommand += ",'" + aSubfamilyCode + "'"; 
                    SQLCommand += ",'" + aMfgrCode + "'";
                    SQLCommand += ",'" + aSeriesCode + "'";
                    SQLCommand += ",'C'";
                    SQLCommand += "," + aChildArrayIndex;
                    SQLCommand += "," + (i + 1);
                    SQLCommand += ",'" + aOptionsArray.getChildRaw(i) + "'";
                    SQLCommand += ",'" + aOptionsArray.getChildCooked(i) + "'";
                    SQLCommand += ",''";
                    SQLCommand += ",'" + timeStamp + "'";
                    SQLCommand += ")";
                    debug (8, SQLCommand);
                    rc = aConn.runUpdate(SQLCommand);
                    message = "  Child Option " + i + " was";
                    if (!rc) {
                        message += " NOT";
                    }
                    message += " written to database successfully. ";
                debug (10, message);
                }
                debug( 5, "Free Heap is now " + fmt(Runtime.getRuntime().freeMemory()));
                return true;
            } catch (Exception e) {
                message = "Unexpected error while writing children options to database.";
                System.out.println(message);
                e.printStackTrace();
                return false;
            }
        } else {
            debug( 0, "Failed to create child options for family/subfamily/mfgr/series/ChildArrayIndex: " 
                    + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "/" + aSeriesCode
                    + "/" + aChildArrayIndex + " for " + aOptionsArray.getParmName() + " in database.");
            // Child range build failed.
            return false;
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
