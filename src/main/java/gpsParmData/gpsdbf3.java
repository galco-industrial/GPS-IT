/* gpsdbf3.java
 *
 * (replaces gpsdbf2.java which has been depricated 8/2016)
 *
 * Created on March 26, 2009, 2:36 PM
 * Revised August, 2016
 *
 * I create option list data in the ps_option_lists table
 * for Galco web landing pages in catalog.htm
 *
 * I plow through the catalogitem table and for every part of list_type "Catalog"
 * I create filter option list data for the following landing pages 
 * for a specific family code or for ALL family codes:
 *
 * (a family level search using global rules is currently disabled
 * disabled family/mfgr level search using global rules
 * disabled family/mfgr/series level search using global rules)
 *
 * a family/subfamily level search using family/subfamily rules
 * optional family/subfamily/mfgr level search using family/subfamily rules
 * optional family/subfamily/mfgr/series level search using family/subfamily rules
 *
 * Logic OverView:
 * I make only one pass through the catalogitem data.
 * The data is sorted by Family code, subfamily code, mfgr code, and series.
 * The data is accumulated in two sets of tables:
 * (disabled: Family level tables using global rules)
 * Family/subfamily level tables using global/local rules
 *
 * As each catalogitem part number record is processed, 
 * the local series tables are updated with the option values.
 * As long as the fc/sfc/mfgr/series codes do not change
 * the option values are collected in the local series level tables.
 * 
 * When a series level change is detected, the options are 
 * 1) optionally written to the ps_option_lists database table for that
 *     fc/sfc/mfgr/series, then 
 * 2) copied to the mfgr level table, and 
 * 3) finally the series data arraylist is deleted/cleared.
 *
 * Similarly when a change in mfgr occurs, series tables are processed, 
 * then the mfgr level options are 
 * 1) Optionally written to the ps_option_lists for that fc/sfc/mfgr, and
 * 2) the mfgr data is copied to the fc/sfc ps_option_lists level tables, and 
 * 3) the mfgr data is then deleted/cleared from the arraylist. 
 *
 * Modification History
 *
 * 06/08/2010 DES Begin modification to support child Select Box Value Generation
 *                Additions/Changes were flagged as ECP-1
 * 04/22/2016 DES Entire servlet under review/complete rewrite by Dan
 *            Also support to process All families in one pass has been added. 
 * 08/25/2016 DES Altered code to use a single time stamp (the date/time when the
 *            program began execution) rather than a different time stamp 
 *            that could vary continuously during execution.
 * 08/26/2016 DES Added code to write option list seq num headers for a 
 *            family/subfamily/mfgr/series only after the option list data was
 *            completely written to the database; similarly, added code to delete
 *            the seq num headers BEFORE deleting the option list entries. 
 *             When the sqlHeaderEnabled swicth is true, headers will only be
 *             written after options have been written. (ECP-5) 
 * 08/30/2016 DES Added code to delete orphaned option lists whenever running
 *            ALL families/subfamilies.
 * 09/06/2016 DES Add shared Class variable and code to only allow one instance of 
 *            gpsdbf3.java to run at a time.
 * 09/07/2016 DES Add shared Class variable(s) and code to allow for a clean shutdown
 *            of any running instance of gpsdbf3.java. Note that these 2 shared class
 *            variables are NOT thread safe at this time.
 * 
 *
 * This servlet named gpsdbf3.java replaces  
 * pre-existing servlet gpsdbf2.java. gpsdbf2.java is deprecated and is 
 * saved for archival purposes.
 *
 * Family Level (Global) options are currently disabled as of 6/30/2016
 * because Family level searches are currently not implemented in the web site.
 * The doGlobal switch has been introduced to handle this scenario. Also,
 * the code for global level family searches may need additional work and
 * has NOT been fully tested.
 *
 * Whenever a fc/sfc/mfgr/series is encountered where the series name is too
 * long ( > 128 characters), no ps_option_list data will be generated for that series.
 *
 * Naughty Parametric search values that exceed 256 characters are skipped.
 *
 * Note that we attempt to handle the following situations:
 * A shutdown request is made and there is no running Build request.
 * A shutdown request is made and there is a running Build request.
 * A shutdown request is made and a running Build request is currently shutting down.
 * A Build request is made and another Build request is already running
 * A Build request is made and there is a pre-existing shutdown request.
 * A Build request is made and no other Build requests are running/shutting down.
 *
 *
 * To run this servlet in batch mode to build option lists for ALL family codes,
 * use the following url:
 *
 * app1.galco.com:8080/GPS-IT/gpsdbf3.do?batch=y,familycode=*
 *
 */


//  UNDER CONSTRUCTION!!!


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
 * @version 1.5.0
 */

public class gpsdbf3 extends HttpServlet {
                
    
    private static int     instanceCount = 0;   // Number of tasks tunning "Build All Families" - We only allow one instance
    private static boolean shutDown = false;    // Causes All currently running copies to shudown
    
    // Define some class instance variables accessible by any method within this class.
        
    // NOTE!!! Servlet Class Variables are treated like  "static" variables by Tomcat.
    // Only 1 copy of servlet is resident at any time regardless of # of 
    // concurrent users and they all share the same servlet class instance
    // variables.
    
    // Note we employ code that only allows one user to execute at a time
    // else everything would go rotten.
    
    private final int DEBUGLEVEL = 2;
        // 0 - always written to log - critical error logging
        // 2 - minimal event logging
        // 4 - moderate event logging
        // 6 - intense event logging
        // 8 - radical event logging
        // 10 - insane event logging
    
    private final String REDIRECT = "index.jsp";
    private final String SERVLET_NAME = "gpsdbf3.java";
    private final String VERSION = "1.5.0";
    private final boolean TESTING = false;
    
    private boolean abortSwitch;
    private boolean batchMode;  // intended for future use when invoked by a cron job
    private final String BEGINPAIR = "{[";   // ECP-1
    private int childArrayIndex;  //  ECP-1
    private String completedFamily;
    private String completedSubfamily;
    private WDSconnect conn1; 
    //private SROconnect conn2;
    private WWWconnect conn3; 
    private String dateCreated;
    private boolean doAllFamilies;
    private boolean doGlobal;
    private boolean doMfg;       // ECP-2
    private boolean doSeries;    // ECP-2
    private final String ENDPAIR = "]}";     // ECP-1
    private String familyCode;
    private String familyCodeCurr;
    private String familyCodePrev;
    private String familyName;
    private String fcTestPrefix;                                                //    ECP-99
    private GPSfieldSet fieldSetFamily;
    private GPSfieldSet fieldSetSubfamily;
    private String mfgrCode;
    private String mfgrCodePrev; 
    private final String MIDPAIR = "]~[";    // ECP-1
    private GPSopts optionsArray; // A handle for a single options Array object
    private int optionsArraysIndex; 
    private ArrayList<GPSopts> optionsArraysFamilyG; // List of family Options Arrays objects
    private int[] optionsArraysFamilyGVectorMap; // Indeces of OptionsArraysFamily objects
                                                               // indexed by family seqNum
    private ArrayList<GPSopts> optionsArraysMfgr; // List of mfgr Options Arrays objects
    private int[] optionsArraysMfgrVectorMap; // Indeces of OptionsArraysMfgr objects
                                                             // indexed by mfg seqNum 
    private ArrayList<GPSopts> optionsArraysMfgrG; // List of Family level mfgr Options Arrays objects
    private int[] optionsArraysMfgrGVectorMap; // Indeces of OptionsArraysMfgrG objects
                                                              // indexed by mfg seqNum 
    private ArrayList<GPSopts> optionsArraysSeries; // List of Subfamily level mfgr/Series Options Arrays objects   ECP-2
    private int[] optionsArraysSeriesVectorMap; // Indeces of OptionsArraysSeries objects                       ECP-2
                                                               // indexed by mfg/series seqNum                                 ECP-2  
    private ArrayList<GPSopts> optionsArraysSeriesG; // List of Family level mfgr/Series Options Arrays objects   ECP-2
    private int[] optionsArraysSeriesGVectorMap; // Indeces of OptionsArraysSeriesG objects                       ECP-2
                                                                // indexed by mfg/series seqNum                                 ECP-2  
    private ArrayList<GPSopts> optionsArraysSubfamily; // List of subfamily Options Arrays objects
    private int[] optionsArraysSubfamilyVectorMap; // Indeces of OptionsArraysSubfamily objects
                                                                  // indexed by subfamily seqNum  
    private ArrayList<GPSopts> optionsArraysSubfamilyG; // List of Family level subfamily Options Arrays objects
    private int[] optionsArraysSubfamilyGVectorMap; // Indeces of OptionsArraysSubfamilyG objects
                                                                   // indexed by subfamily seqNum 
    private int[] parmSeqNums;  // a list of seq nums of parms found for a part number
                                               // terminated by an element containing zero.
    private String parmValue;
    private String[] parmValues; // parm values for a part; indexed by seqNum
    private GPSrules ruleSet;         // a work object to hold the ruleset for the current parametric field 
    private int[] ruleSetFamilyVectorMap;
    private int ruleSetFamilyVectorMapCount = 0;
    private GPSrules[] ruleSetsFamily = null; // ruleSetsFamily is an array of family level parm fields and their corresponding rulesets
    private GPSrules[] ruleSetsSubfamily = null; // ruleSetsSubfamily is an array of family/subfamily level parm fields and their corresponding rulesets
    private int[] ruleSetSubfamilyVectorMap = new int[100];
    private int ruleSetSubfamilyVectorMapCount;
    private boolean running;
    private String seriesCode;
    private String seriesCodePrev;
    private boolean sqlHeaderEnabled; // ECP-5
    private ArrayList<String> sqlHeaders;  // ArrayList to hold pending option List headers   ECP-5
    private String subfamilyCode;
    private String subfamilyCodePrev;
    private GPSsubfamilyCodes subfamilyCodes;
    private String subfamilyName;
       
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         
        
        
        // Define a few local work variables for this method
        
        int gtot = 0;
        int i = 0;
        int ichunk = 0;
        boolean init = false;
        boolean junk;
        String partNumber = "";
        String queryString = ""; 
        int rc = 0;
        ResultSet rs1 = null;
        ResultSet rs3 = null;
        int seqNum = 0;
        boolean subfamiliesExist = true;
        String work;
        boolean setShutDown = false;                       
                             
        // Do we want to execute an orderly shutdown of any running instances?
        
        debug (6, "Shutdown bit is currently " + shutDown);
        debug (4, "instanceCount is currently " + instanceCount);
        
        work = request.getParameter("shutdown");                    
        if (work != null) {
            setShutDown = (work.toLowerCase().equals("y"));
        }
        if (setShutDown) {
            shutDown = true;
            debug (6, "shutdown=y");
        } else {
            debug (6, "shutdown=");
        }
        // If shutDown bit was already on, it will remain on
        // regardless of a second shutdown request or
        // an attempt to start another Build.
                
        debug (6, "Shutdown bit is now " + shutDown);
         
        if (setShutDown) { // This instance just requested a Shut down.
            if (instanceCount == 1) { // A Build is currently running...
                request.setAttribute("message", "Attempting an orderly shutdown of " + SERVLET_NAME + ".");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
            } else { // No Build is currently running
                shutDown = false;
                request.setAttribute("message", "No instance of " + SERVLET_NAME + " is currently executing; Shutdown request will be ignored.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
            }
            return;
        }
        
        // If we get here, we have a new Build request...
        
        // Allow only one instance of this Build to run at a time
        
        //System.out.printf("Servlet " + SERVLET_NAME + " instance count is currently %d\n", instanceCount);
        if (instanceCount == 1) {
            request.setAttribute("message", "Another instance of " + SERVLET_NAME + " is currently executing; try again later.");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        // Do not initiate a new Build if shutdown bit is currently set.
        
        if (shutDown) { // Was shutdown bit previously set?
            request.setAttribute("message", "Cannot start " + SERVLET_NAME + "; Run 'Stop Build Option Lists' and then try again.");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        // NOTE! This code is NOT running in a synchronized thread and could possibly fail
        //       in some rare situations.
        
        // Begin a new Build...
        
        instanceCount = 1;  // Now we're the only game in town
        running = true;
        
        // At this point we can safely start up this single instance of this servlet.
        // Re-Initialize the servlet class instance variables here.
        
        abortSwitch = false;
        batchMode = false;  // For future use
        childArrayIndex = -1;
        completedFamily = "none";
        completedSubfamily = "none";
        doAllFamilies = false;
        doGlobal = false; // We currently do NOT support Family level search option lists
        doMfg = false;
        doSeries = false;
        fcTestPrefix = "";  
        mfgrCode = "";
        mfgrCodePrev = "";
        optionsArraysIndex = 0;
        optionsArraysFamilyG = new ArrayList<GPSopts>();
        optionsArraysFamilyGVectorMap = new int[100];
        optionsArraysMfgr = new ArrayList<GPSopts>();
        optionsArraysMfgrVectorMap = new int[100];
        optionsArraysMfgrG = new ArrayList<GPSopts>();
        optionsArraysMfgrGVectorMap = new int[100];
        optionsArraysSeries = new ArrayList<GPSopts>();
        optionsArraysSeriesVectorMap = new int[100];
        optionsArraysSeriesG = new ArrayList<GPSopts>();
        optionsArraysSeriesGVectorMap = new int[100];
        optionsArraysSubfamily = new ArrayList<GPSopts>();
        optionsArraysSubfamilyVectorMap = new int[100];
        optionsArraysSubfamilyG = new ArrayList<GPSopts>();
        optionsArraysSubfamilyGVectorMap = new int[100];
        parmSeqNums = new int[100];
        parmValue = "";
        parmValues = new String[100];
        ruleSetFamilyVectorMap = new int[100];
        ruleSetFamilyVectorMapCount = 0;
        seriesCode = "";
        seriesCodePrev = "";
        sqlHeaderEnabled = true;
        sqlHeaders = new ArrayList<String>(); 
        subfamilyCode = "";
        subfamilyCodePrev = "";
        subfamilyName = "";

        //debug (0, "Max memory is " + fmt(Runtime.getRuntime().maxMemory()));
        
        debug (4, "Debug Level is " + DEBUGLEVEL);
        
        // Test mode?
        if (TESTING) {
            fcTestPrefix = "*TEST*";
            debug (0, "TESTING mode is enabled.");
        }
        
        // Get Keyword parameters for this run
        
        work = request.getParameter("batch");      // Intended for future use              
        if (work != null) {
            batchMode = (work.toLowerCase().equals("y"));
        }
        
        familyCode = request.getParameter("familyCode");
        if (familyCode == null) {
            familyCode = "";
        }
        
        if (familyCode.equals("")) {    // a familyCode or wildcard "*" is mandatory
            System.out.println(SERVLET_NAME + " is missing family code; aborting...");
            response.sendRedirect(REDIRECT);
            done();
            return;
        }
        
        doAllFamilies = familyCode.equals("*");
        dateCreated = DateTime.getTimeStamp();
        
        /* NOTE: Currently we build series AND mfgr level option lists by default
        work = request.getParameter("mfg");                    
        if (work != null) {
            doMfg = (work.toLowerCase().equals("y"));
            if (doMfg) {
                work = request.getParameter("series");
                if (work != null) {
                    doSeries = (work.toLowerCase().equals("y"));
                }
            }
        }
        */

        doMfg = true;         
        doSeries = true;      
        
        conn1 = new WDSconnect(); 
        if (!conn1.connect()) {         // Connect to WDS database 
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to WDS database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            done();
            return;
        }
        
        //conn2 = new SROconnect();
        //if (!conn2.connect()) {         // Connect to SRO database 
        //    closeConn1();                              //  ECP-2
        //    request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to SRO database");
        //    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
        //    view.forward(request,response);
        //    done();
        //    return;
        //}
        
        conn3 = new WWWconnect();
        if (!conn3.connect()) {         // Connect to Web database 
            //closeConn2();                              // ECP-2
            closeConn1();                              // ECP-2
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to WWW database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            done();
            return;
        }
        
        //////////////////////////////////////////////////////////
        // Do not read this comment; it is no longer applicable //
        //////////////////////////////////////////////////////////
                                                
        /////////////////////////////////////////////////////////////
        //     Now we get part numbers and their parametric values //
        /////////////////////////////////////////////////////////////
        
        try {
            queryString =  "SELECT part_num, family_code, subfamily_code, parent_mfg_code, series";
            queryString += " FROM pub.catalogitem";
            queryString += " WHERE list_type = 'Catalog'";
            if (!doAllFamilies) {
                queryString += " AND family_code = '" + familyCode + "'";
            } else {
                queryString += " AND family_code > ''";
            }
            queryString += " ORDER BY family_code, subfamily_code, parent_mfg_code, series";               

            debug(6, "Extracting part numbers; SQL statement is " + queryString);
            rs3 = conn3.runQuery(queryString); // a whole lot of junk happens here             
            
            /////////////////////////////////////////////////////////////////////////////
            // Now we will Process the Result Set and                                  //
            // create options arrays for the search parm values                        //
            /////////////////////////////////////////////////////////////////////////////

            if (rs3 != null) {
                debug (8, "I got the Result Set; Now building options for Family Code '" + familyCode + "'");
                
                // Initialization 
                
                parmSeqNums[0] = 0; // Init end of list with a zero entry
                for (i = 0; i < 100; i++) {
                    parmValues[i] = "";
                }
                while (rs3.next() && !abortSwitch) {   
                    gtot++;
                    if (++ichunk > 499) {
                        debug (8, "Processed " + gtot + " parts so far...");
                        debug (8, "Free Heap is now " + fmt(Runtime.getRuntime().freeMemory()));
                        ichunk = 0; 
                    }
                    partNumber =    rs3.getString("part_num").toUpperCase();
                    familyCodeCurr =    rs3.getString("family_code").toUpperCase();
                    subfamilyCode = rs3.getString("subfamily_code").toUpperCase(); 
                    mfgrCode =      rs3.getString("parent_mfg_code").toUpperCase();
                    seriesCode =    rs3.getString("series").toUpperCase();
                    if (seriesCode.equals("")) {
                        seriesCode = "*none*";
                    }
                    
                    if (!init) {
                        startNewFamily();
                        startNewSubfamily();
                        startNewMfgr();
                        startNewSeries();
                        init = true;
                        debug (8, "Loop initialization is complete");
                    }
 
                        //////////////////////////////////////////////////
                        // Check for New Family Code control break      //
                        // If true,                                     //
                        // Force Series Code control break              //
                        // Mfgr control break                           //
                        // and Subfamily Code control break             //
                        // and Family Code control break                //
                        //////////////////////////////////////////////////
 
                    if (!familyCodeCurr.equals(familyCodePrev)) {                           // Did family code just change?
                        debug (8, "Family '" + familyCodePrev + "' changed to '" + familyCodeCurr + "'.");
                        finishOldSeries();    // Perform Finish old series
                        finishOldMfgr();      // Perform Finish old Mfg
                        finishOldSubfamily(); // Perform Finish old subfamily:
                        finishOldFamily();    // Perform Finish old family:
                        startNewFamily();     // Perform Start new family:
                        startNewSubfamily();  // Perform Start new subfamily:
                        startNewMfgr();       // Perform Start new Mfg
                        startNewSeries();     // Perform Start new series
                                                
                    } // End family code just changed
                    
                        //////////////////////////////////////////////////
                        // Check for New Subfamily Code control break   //
                        // If true,                                     //
                        // Force Series Code control break              //
                        // Mfgr control break                           //
                        // and Subfamily Code control break             //
                        //////////////////////////////////////////////////
                    
                    else if (!subfamilyCode.equals(subfamilyCodePrev)) {                           // Did subfamily code just change?
                        debug (8, "Subfamily '" + subfamilyCodePrev + "' changed to '" + subfamilyCode + "'.");
                        finishOldSeries();    // Perform Finish old series
                        finishOldMfgr();      // Perform Finish old Mfg
                        finishOldSubfamily(); // Perform Finish old subfamily:
                        startNewSubfamily();  // Perform Start new subfamily:
                        startNewMfgr();       // Perform Start new Mfg
                        startNewSeries();     // Perform Start new series
                                                
                    } // End subfamily code just changed
                    
                        ////////////////////////////////////////////////
                        // Check for New Mfgr Code control break here //
                        // if true,                                   //
                        // Force Series Code control break            //
                        // then do Mfgr control break                 //
                        ////////////////////////////////////////////////
                    
                    else if (!mfgrCode.equals(mfgrCodePrev)) {                                       // Did mfgr code just change?
                        debug (8, "Mfgr '" + mfgrCodePrev + "' changed to '" + mfgrCode + "'.");
                        finishOldSeries();    // Perform Finish old series
                        finishOldMfgr();      // Perform Finish old Mfg
                        startNewMfgr();       // Perform Start new Mfg
                        startNewSeries();     // Perform Start new series
                                                    
                    } // End mfgr code just changed
                    
                        /////////////////////////////////////////////
                        // New Series Code control break here      //
                        /////////////////////////////////////////////
                    
                    else if (!seriesCode.equals(seriesCodePrev)) {                                   // Did series code just change?
                        debug (8, "Series '" + seriesCodePrev + "' changed to '" + seriesCode + "'.");
                        finishOldSeries();    // Perform Finish old series
                        startNewSeries();     // Perform Start new series
                        
                    } // End series code just changed
                                       
                    //////////////////////////////////
                    //  Process this detail record  //
                    //////////////////////////////////
                                                    
                    // Look up parametric data for this part number
                    debug (10, " Looking up parm data for part number " + partNumber);
                    queryString =  "SELECT seq_num, parm_value";
                    queryString += " FROM pub.ps_parm_data";
                    queryString += " WHERE part_num = '" + partNumber + "'";
                    queryString += " ORDER BY seq_num";
                    rs1 = conn1.runQuery(queryString);
                    if (rs1 != null) {
                        i = 0;
                        while (rs1.next()) {
                            // only load searchable parm values
                            seqNum = rs1.getInt("seq_num");
                            if (ruleSetSubfamilyVectorMap[seqNum] > -1)  {               // FIX ME to work with global/local seqnums
                                parmSeqNums[i++] = seqNum;
                                parmValue = rs1.getString("parm_value");
                                parmValues[seqNum] = parmValue;
                                debug (10, "     Set parm value for seq num " + seqNum + " to '" + parmValue + "'");
                            }    
                        }
                        rs1.close();
                        rs1 = null; 
                        conn1.closeStatement();
                    }                    
                    parmSeqNums[i++] = 0; // Mark end of list with a zero entry
                    debug (10, " Found " + i + " parm values for part number " + partNumber);
                    
                    // For each Family Subfamily Mfgr Series parm value, add it to its option list
                    // If there is no applicable series code, the seriesCode is set to "*none*".
                                       
                    if(!seriesCode.equals("")) {    // Note: seriesCode will never be blank here
                        for (i = 0; parmSeqNums[i] > 0; i++) {
                            seqNum = parmSeqNums[i];
                            parmValue = parmValues[seqNum];
                            if (!parmValue.equals("")) { // if an option value is present
                                // First handle the Subfamily (local) level option lists
                                if (ruleSetSubfamilyVectorMap[seqNum] > -1) {
                                    optionsArraysIndex = optionsArraysSeriesVectorMap[seqNum]; // find out which Seqnum options array it goes inside
                                    if (optionsArraysIndex == -1) { // if options array does not yet exist for this seqnum, build a new one... 
                                        optionsArray = new GPSopts();                                 
                                        junk = optionsArray.init(conn1, familyCodeCurr, subfamilyCode, seqNum);
                                        optionsArraysSeries.add(optionsArray); // Add it to the list of series options arrays                                
                                        optionsArraysIndex = optionsArraysSeries.size() - 1; // calculate its index
                                        optionsArraysSeriesVectorMap[seqNum] = optionsArraysIndex; // update the vector map                             
                                    } else {
                                        optionsArray = optionsArraysSeries.get(optionsArraysIndex);                                
                                    }
                                    optionsArray.addOption(parmValue); // Add the parm value                             
                                    debug (10, "   Added raw value '" + parmValue + "' for seq num " + seqNum + " to Series local options Array object");
                                }
                                
                                // Now handle the Family Global level option lists
                                
                                if (doGlobal) {  // This is currently disabled 
                                    if (ruleSetFamilyVectorMap[seqNum] > -1) {
                                        optionsArraysIndex = optionsArraysSeriesGVectorMap[seqNum]; // find out which Family Global options array it goes inside
                                        if (optionsArraysIndex == -1) { // if options array does not exist yet for this seqnum, build one 
                                            optionsArray = new GPSopts();                                 
                                            junk = optionsArray.init(conn1, familyCodeCurr, "*", seqNum);
                                            optionsArraysSeriesG.add(optionsArray); // Add it to the list of global series options arrays                                
                                            optionsArraysIndex = optionsArraysSeriesG.size() - 1; // calculate its index
                                            optionsArraysSeriesGVectorMap[seqNum] = optionsArraysIndex; // update the vector map                             
                                        } else {
                                            optionsArray = optionsArraysSeriesG.get(optionsArraysIndex);                                
                                        }
                                        optionsArray.addOption(parmValue); // Add the parm value                             
                                        debug (10, "   Added raw value '" + parmValue + "' for seq num " + seqNum + " to Series Global options Array object");
                                    }
                                }
                                
                                parmValues[seqNum] = ""; // then erase it to clean up after this part num
                                // optionsArray = null;     // Discard this handle
                            } // end if
                        } // end for 
                    }
                } // end while (rs3.next())
                
                rs3.close();
                rs3 = null; 
                conn3.closeStatement();
                                
                ///////////////////////////////////////
                // Catalogitem part numbers EOF      //
                ///////////////////////////////////////
                
                if (!abortSwitch) {
                    debug (8, "EOF on part numbers result set.");
                    finishOldSeries();    // Perform Finish old series
                    finishOldMfgr();      // Perform Finish old Mfg
                    finishOldSubfamily(); // Perform Finish old subfamily:
                    finishOldFamily();    // Perform Finish old Family:
                    if (doAllFamilies) {
                        doCleanUp();
                    }
                }
            } else {
                debug (0, "Result set was null.");
            }   // end if (rs3 != null)
            
            if (abortSwitch) {
                debug (2, "Option Lists were gracefully shut down.");
                debug (2, "Last subfamily code completed was '" + completedSubfamily + "' in family code '" + completedFamily + "'.");
            }
            debug (2, "Processing complete.");
            done();
            if (batchMode) {     // For future use
                closeAll();
                return;
            }
            
            GPSproductLines productLines = new GPSproductLines();
            if (productLines.open(conn1) != 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                done();
                closeAll();
                return;
            }   
            ArrayList <String> lines = productLines.getArrayList2();
            try {
                if (!abortSwitch) {
                    work = "all family codes.";
                    if (!doAllFamilies) {
                        work = "family code '" + familyCode + "' - " + familyName + ".";
                    }
                    if (gtot > 0) {
                        request.setAttribute("statusMessage", "Option lists were successfully created for " + work);
                    } else {
                        request.setAttribute("statusMessage", "No Option list data was found for family code " + familyCode);
                    }
                } else {
                    shutDown = false;
                    //abortSwitch = false;
                    //request.setAttribute("shutdown", "");
                    request.setAttribute("statusMessage", "Option lists creation was gracefully shut down; Try again later.");
                }
                request.setAttribute("lines", lines);
                RequestDispatcher view = request.getRequestDispatcher("gpsdbf1.jsp");
                view.forward(request,response);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + ": <br />" + e);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + ": <br />" + e);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } finally {
            done();
            closeAll();
        }     
    }
      
    private boolean checkForShutdown() {
        debug (8, "Checking shut down bit; shutdown = " + shutDown);
        if (shutDown) {
            abortSwitch = true;
            shutDown = false;
        }
        return abortSwitch;
    }
    
    private void closeAll() {
        closeConn3();
       //closeConn2();
        closeConn1();
    }
    
    private void closeConn1() {
        try {
            conn1.closeStatement();
        } catch (Exception e) {
            // Ignore errors
        }
        try {
            conn1.close();
        } catch (Exception e) {
            // Ignore errors
        } finally {
            conn1 = null;
        }
    }
    
    //private void closeConn2() {
    //    try {
    //        conn2.closeStatement();
    //    } catch (Exception e) {
    //        // Ignore errors
    //    }
    //    try {
    //        conn2.close();
    //    } catch (Exception e) {
    //        // Ignore errors
    //    } finally {
    //        conn2 = null;
    //    }
    //}
    
    private void closeConn3() {
        try {
            conn3.closeStatement();
        } catch (Exception e) {
            // Ignore errors
        }
        try {
            conn3.close();
        } catch (Exception e) {
            // Ignore errors
        } finally {
            conn3 = null;
        }
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
    
    private void copySubfamilyOptionsToFamily() {
        int i;
        int imax;
        int j;
        int jmax;
        boolean junk;
        int toOptionsArraysIndex;
        int workSeqNum;
        
        GPSopts fromOptionsArray;
        GPSopts toOptionsArray;
        
        imax = optionsArraysSubfamilyG.size();
        for (i = 0; i < imax; i++) {
           fromOptionsArray = optionsArraysSubfamilyG.get(i);
           workSeqNum = fromOptionsArray.getSeqNum();
           toOptionsArraysIndex = optionsArraysFamilyGVectorMap[workSeqNum]; // find out which option array it goes to
           if (toOptionsArraysIndex == -1) { // if Family level options array does not exist yet for this seqnum, build one 
               toOptionsArray = new GPSopts();                                 
               junk = toOptionsArray.init(conn1, familyCodePrev, "*", workSeqNum);
               optionsArraysFamilyG.add(toOptionsArray); // Add it to the list of Family level options arrays                                
               toOptionsArraysIndex = optionsArraysFamilyG.size() - 1; // calculate its index
               optionsArraysFamilyGVectorMap[workSeqNum] = toOptionsArraysIndex; // update the vector map                             
           } else {
               toOptionsArray = optionsArraysFamilyG.get(toOptionsArraysIndex); 
           }
           jmax = fromOptionsArray.getOptionListSize();
           for (j = 0; j < jmax; j++) {
               parmValue = fromOptionsArray.getOptionListValue(j);
               toOptionsArray.addOption(parmValue); // Add the parm value to the upper Family level array                             
               debug (8, "   Added raw value '" + parmValue + "' for Subfamily seq num " + workSeqNum + " to Family options Array object");
           } // end for
        } // end for
        
        // These object handles are going out of scope:
        fromOptionsArray = null;
        toOptionsArray = null;
    }
    
    private void copyMfgrOptionsToSubfamily() {
        int i;
        int imax;
        int j;
        int jmax;
        boolean junk;
        int toOptionsArraysIndex;
        int workSeqNum;
        
        GPSopts fromOptionsArray;
        GPSopts toOptionsArray;
        
        imax = optionsArraysMfgr.size();
        for (i = 0; i < imax; i++) {
           fromOptionsArray = optionsArraysMfgr.get(i);
           workSeqNum = fromOptionsArray.getSeqNum();
           toOptionsArraysIndex = optionsArraysSubfamilyVectorMap[workSeqNum]; // find out which option array it goes to
           if (toOptionsArraysIndex == -1) { // if options array does not exist yet for this seqnum, build one 
               toOptionsArray = new GPSopts();                                 
               junk = toOptionsArray.init(conn1, familyCodePrev, subfamilyCodePrev, workSeqNum);
               optionsArraysSubfamily.add(toOptionsArray); // Add it to the list of Subfamily options arrays                                
               toOptionsArraysIndex = optionsArraysSubfamily.size() - 1; // calculate its index
               optionsArraysSubfamilyVectorMap[workSeqNum] = toOptionsArraysIndex; // update the vector map                             
           } else {
               toOptionsArray = optionsArraysSubfamily.get(toOptionsArraysIndex); 
           }
           jmax = fromOptionsArray.getOptionListSize();
           for (j = 0; j < jmax; j++) {
               parmValue = fromOptionsArray.getOptionListValue(j);
               toOptionsArray.addOption(parmValue); // Add the parm value to the upper level array                             
               debug (8, "   Added raw value '" + parmValue + "' for Mfgr seq num " + workSeqNum + " to Subfamily options Array object");
           } // end for
        } // end for
        
        // Then do the Family level options
        
        if (doGlobal) {   // This is currently disabled.
            //debug (0, "  Now we do the Global values.");
            imax = optionsArraysMfgrG.size();
            for (i = 0; i < imax; i++) {
                fromOptionsArray = optionsArraysMfgrG.get(i);
                workSeqNum = fromOptionsArray.getSeqNum();
                toOptionsArraysIndex = optionsArraysSubfamilyGVectorMap[workSeqNum]; // find out which option array it goes to
                //debug (0, "   toOptionsArrayIndex is " + toOptionsArraysIndex + "; Seqnum is " + workSeqNum);
                //debug (0, "   optionsArraysSubfamilyG.size() is " + optionsArraysSubfamilyG.size() );
                if (toOptionsArraysIndex == -1) { // if Family level options array does not exist yet for this seqnum, build one 
                    toOptionsArray = new GPSopts();                                 
                    junk = toOptionsArray.init(conn1, familyCodePrev, "*", workSeqNum);
                    optionsArraysSubfamilyG.add(toOptionsArray); // Add it to the list of Family level Mfgr options arrays                                
                    toOptionsArraysIndex = optionsArraysSubfamilyG.size() - 1; // calculate its index
                    optionsArraysSubfamilyGVectorMap[workSeqNum] = toOptionsArraysIndex; // update the vector map                             
                } else {
                    toOptionsArray = optionsArraysSubfamilyG.get(toOptionsArraysIndex); 
                }
                jmax = fromOptionsArray.getOptionListSize();
                for (j = 0; j < jmax; j++) {
                    parmValue = fromOptionsArray.getOptionListValue(j);
                    toOptionsArray.addOption(parmValue); // Add the parm value to the upper Family level array                             
                    //debug (0, "   Added raw value '" + parmValue + "' for Family level Mfgr seq num " + workSeqNum + " to Subfamily options Array object");
                } // end for
            } // end for
        }   
        // These object handles are going out of scope:
        fromOptionsArray = null;
        toOptionsArray = null;
    }
    
    private void copySeriesOptionsToMfgr() {
        int i;
        int imax;
        int j;
        int jmax;
        boolean junk;
        int toOptionsArraysIndex;
        int workSeqNum;
        
        GPSopts fromOptionsArray;
        GPSopts toOptionsArray;
                
        // First do the subfamily level options
        
        imax = optionsArraysSeries.size();
        for (i = 0; i < imax; i++) {
           fromOptionsArray = optionsArraysSeries.get(i);
           workSeqNum = fromOptionsArray.getSeqNum();
           debug (8, "   fromOptionsArray Index is " + i + "; Seqnum is " + workSeqNum);
           toOptionsArraysIndex = optionsArraysMfgrVectorMap[workSeqNum]; // find out which option array it goes to
           debug (8, "   toOptionsArrayIndex is " + toOptionsArraysIndex + "; Seqnum is " + workSeqNum);
           if (toOptionsArraysIndex == -1) { // if options array does not exist yet for this seqnum, build one 
               toOptionsArray = new GPSopts();  
               debug (8, "  I just created a new GPSopts object for the Mfgr options Array.");
               junk = toOptionsArray.init(conn1, familyCodePrev, subfamilyCodePrev, workSeqNum);
               debug (8, "  I just initialized the new GPSopts object.");
               optionsArraysMfgr.add(toOptionsArray); // Add it to the list of Mfgr options arrays                                
               toOptionsArraysIndex = optionsArraysMfgr.size() - 1; // calculate its index
               debug (8, "  The new GPSopts object Index is " + toOptionsArraysIndex);
               optionsArraysMfgrVectorMap[workSeqNum] = toOptionsArraysIndex; // update the vector map 
               debug (8, "  The Mfgr vector map for seq num " + workSeqNum + " has been set to " + toOptionsArraysIndex);
           } else {
               toOptionsArray = optionsArraysMfgr.get(toOptionsArraysIndex); 
           }
           jmax = fromOptionsArray.getOptionListSize();
           debug (8, "   Copying " + jmax + " Series parms to Mfgr options Array object");
           for (j = 0; j < jmax; j++) {
               parmValue = fromOptionsArray.getOptionListValue(j);
               toOptionsArray.addOption(parmValue); // Add the parm value to the upper level array                             
               debug (8, "   Added raw value #" + j + "  value '" + parmValue + "' for Subfamily level Series seq num " + workSeqNum + " to Mfgr options Array object");
           } // end for
        } // end for
                
        // Then do the Family (Global) level options
        
        if (doGlobal) {  // This is currently disabled
        //debug (0, "  Now we do the Global values.");
            imax = optionsArraysSeriesG.size();
            for (i = 0; i < imax; i++) {
                fromOptionsArray = optionsArraysSeriesG.get(i);
                workSeqNum = fromOptionsArray.getSeqNum();
                toOptionsArraysIndex = optionsArraysMfgrGVectorMap[workSeqNum]; // find out which option array it goes to
                if (toOptionsArraysIndex == -1) { // if Family level options array does not exist yet for this seqnum, build one 
                    toOptionsArray = new GPSopts();                                 
                    junk = toOptionsArray.init(conn1, familyCodePrev, "*", workSeqNum);
                    optionsArraysMfgrG.add(toOptionsArray); // Add it to the list of Family level Mfgr options arrays                                
                    toOptionsArraysIndex = optionsArraysMfgrG.size() - 1; // calculate its index
                    optionsArraysMfgrGVectorMap[workSeqNum] = toOptionsArraysIndex; // update the vector map                             
                } else {
                    toOptionsArray = optionsArraysMfgrG.get(toOptionsArraysIndex); 
                }
                jmax = fromOptionsArray.getOptionListSize();
                for (j = 0; j < jmax; j++) {
                    parmValue = fromOptionsArray.getOptionListValue(j);
                    toOptionsArray.addOption(parmValue); // Add the parm value to the upper Family level array                             
                    //debug (0, "   Added raw value '" + parmValue + "' for Family level Series seq num " + workSeqNum + " to Mfgr options Array object");
                } // end for
            } // end for
        }
        
        // These object handles are going out of scope:
        fromOptionsArray = null;
        toOptionsArray = null;
    }

    private void debug(int level, String x) {        
        if (DEBUGLEVEL >= level) {
            System.out.println(x);
        }
    }
  
    /* IMPORTANT: Note that we delete existing option list data in separate chunks
     * to avoid database "Lock table full" errors
     */
    
    private boolean deleteFamilyOptionsFromDatabase(String aFamilyCode) {
        
        String bFamilyCode;
        int currIndex = 0;
        String currMfg = "";
        String currSeries = "";
        String currSFC = "";
        int prevIndex = -1;
        String prevMfg = "";
        String prevSeries = "#";
        String prevSFC = "";
        String message;
        String queryString;
        boolean rc = false;
        ResultSet rs1;
  
        bFamilyCode = fcTestPrefix + aFamilyCode;
                
        rc = deleteOptionHeaders(bFamilyCode);  // First delete the option list index headers
        
        try {
            queryString =  "SELECT subfamily_code, mfgr_code, series_code, option_list_index ";
            queryString += " FROM pub.ps_option_lists";
            queryString += " WHERE family_code = '" + bFamilyCode + "'";
            queryString += " AND option_value_index = 1 ";  // Headers have already been deleted
            queryString += " AND option_list_type = 'P' ";
            queryString += " ORDER BY subfamily_code, mfgr_code, series_code, option_list_index";
            
            debug (8, queryString);
            rs1 = conn1.runQuery(queryString);
            if (rs1 != null) {
                while (rs1.next()) {
                    //prevSFC    = currSFC;
                    //prevMfg    = currMfg;
                    //prevSeries = currSeries;
                    //prevIndex  = currIndex;
                    currSFC    = rs1.getString("subfamily_code");
                    currMfg    = rs1.getString("mfgr_code");
                    currSeries = rs1.getString("series_code");
                    currIndex  = rs1.getInt("option_list_index");
                    if (!currSFC.equals(prevSFC)) { // The SFC has changed.
                        // Delete all options for this fc/sfc/mfg/series/list_index
                        if (rc) {
                            rc = deleteSfcMfgSeriesIndexOptions(bFamilyCode, currSFC, currMfg, currSeries, currIndex); // Delete this family/subfamily/mfg/series level
                        }
                        if (rc) {
                            rc = deleteSfcMfgSeriesChildrenOptions(bFamilyCode, currSFC, currMfg, currSeries); // Delete the children for this family/subfamily/mfg/series level
                        }
                        prevSFC    = currSFC;
                        prevMfg    = currMfg;
                        prevSeries = currSeries;
                        prevIndex  = currIndex;
                    } 
                    else if (!currMfg.equals(prevMfg)) {   // The Mfg and series have changed
                        if (rc) {
                            rc = deleteSfcMfgSeriesIndexOptions(bFamilyCode, currSFC, currMfg, currSeries, currIndex); // Delete this family/subfamily/mfg/series level
                        }
                        if (rc) {
                            rc = deleteSfcMfgSeriesChildrenOptions(bFamilyCode, currSFC, currMfg, currSeries); // Delete the children for this family/subfamily/mfg/series level
                        }
                        prevMfg    = currMfg;
                        prevSeries = currSeries;
                        prevIndex  = currIndex;
                    }
                    else if (!currSeries.equals(prevSeries)) {  // Just the series changed
                        if (rc) {
                            rc = deleteSfcMfgSeriesIndexOptions(bFamilyCode, currSFC, currMfg, currSeries, currIndex); // delete this family/subfamily/mfg/series level
                        }
                        if (rc) {
                            rc = deleteSfcMfgSeriesChildrenOptions(bFamilyCode, currSFC, currMfg, currSeries); // Delete the children for this family/subfamily/mfg/series level
                        }
                        prevSeries = currSeries;
                        prevIndex  = currIndex;
                    }
                    else if (!(currIndex == prevIndex)) {  // Just the Index changed
                        if (rc) {
                            rc = deleteSfcMfgSeriesIndexOptions(bFamilyCode, currSFC, currMfg, currSeries, currIndex); // delete this family/subfamily/mfg/series level
                        }
                        //if (rc) {
                        //    rc = deleteSfcMfgSeriesChildrenOptions(bFamilyCode, currSFC, currMfg, currSeries); // Delete the children for this family/subfamily/mfg/series level
                       /// }
                        prevIndex  = currIndex;
                    }
                }
                rs1.close();
                rs1 = null; 
            }
            conn1.closeStatement();
        } catch (Exception e) {
            e.printStackTrace();
            closeAll();
            rc = false;
        } 
        message = "  Family Options were";
        if (!rc) {
            message += " NOT";
        }
        message += " deleted successfully for " + bFamilyCode;
        debug (2, message);
        return rc;
    }
/*      
    private boolean deleteAllSubfamilyOptions(String aFamilyCode, String aSubfamilyCode) {              // ECP-2
        boolean rc;
        String message = "";
        String SQLCommand = "";
        
        SQLCommand = "DELETE FROM pub.ps_option_lists";
        SQLCommand += " WHERE family_code = '" + aFamilyCode + "'";
        SQLCommand += " AND subfamily_code = '" + aSubfamilyCode + "'";
        rc = conn1.runUpdate(SQLCommand);
        message = "  Family/Subfamily Options were";
        if (!rc) {
            message += " NOT";
        }
        message += " deleted successfully. ";
        debug (8, message);
        return rc;
    }
*/
    private boolean deleteSfcMfgSeriesIndexOptions(String aFamilyCode, String aSubfamilyCode, String aMfgrCode, String aSeries, int aIndex) {
        boolean rc = true;
        String message;
        String SQLCommand;
        int count = -1;
        
        if (DEBUGLEVEL > 1) {
            count = 0;
            ResultSet rs1;
            String queryString;
            try {
                queryString =  "SELECT mfgr_code, series_code, option_list_index ";
                queryString += " FROM pub.ps_option_lists";
                queryString += " WHERE family_code = '" + aFamilyCode + "'";
                queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";  // Headers have already been deleted
                queryString += " AND mfgr_code = '" + aMfgrCode + "'";
                queryString += " AND series_code = '" + aSeries + "'";
                queryString += " AND option_list_index = " + aIndex;
                rs1 = conn1.runQuery(queryString);
                if (rs1 != null) {
                    while (rs1.next()) {
                        count++;
                    }
                    rs1.close();
                    rs1 = null; 
                }
                debug (8, "Attempting to delete " + count + " options.");
                conn1.closeStatement();
            } catch (Exception e) {
                e.printStackTrace();
                closeAll();
            }
        } 
        
        if (!(count == 0)) {
            SQLCommand = "DELETE FROM pub.ps_option_lists";
            SQLCommand += " WHERE family_code = '" + aFamilyCode + "'";
            SQLCommand += " AND subfamily_code = '" + aSubfamilyCode + "'";
            SQLCommand += " AND mfgr_code = '" + aMfgrCode + "'";
            SQLCommand += " AND series_code = '" + aSeries + "'";
            SQLCommand += " AND option_list_index = " + aIndex;
            debug (8, SQLCommand);
            rc = conn1.runUpdate(SQLCommand);
            message = "  Family/Subfamil/Mfgr/Series/Index Options were";
            if (!rc) {
                message += " NOT";
            }
            message += " deleted successfully for " + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "/" + aSeries + "/" + aIndex;
            if (!rc) {
                debug (2, message);
            }
        }
        return rc;
    }
    
    private boolean deleteSfcMfgSeriesChildrenOptions(String aFamilyCode, String aSubfamilyCode, String aMfgrCode, String aSeries) {
        boolean rc = true;
        String message;
        String SQLCommand;
        int count = -1;
        
        if (DEBUGLEVEL > 1) {
            count = 0;
            ResultSet rs1;
            String queryString;
            try {
                queryString =  "SELECT mfgr_code, series_code ";
                queryString += " FROM pub.ps_option_lists";
                queryString += " WHERE family_code = '" + aFamilyCode + "'";
                queryString += " AND subfamily_code = '" + aSubfamilyCode + "'";  // Headers have already been deleted
                queryString += " AND mfgr_code = '" + aMfgrCode + "'";
                queryString += " AND series_code = '" + aSeries + "'";
                queryString += " AND option_list_type = 'C'";
                rs1 = conn1.runQuery(queryString);
                if (rs1 != null) {
                    while (rs1.next()) {
                        count++;
                    }
                    rs1.close();
                    rs1 = null; 
                }
                debug (8, "Attempting to delete " + count + " Children options.");
                conn1.closeStatement();
            } catch (Exception e) {
                e.printStackTrace();
                closeAll();
            }
        } 
        
        if (!(count == 0)) {
            SQLCommand = "DELETE FROM pub.ps_option_lists";
            SQLCommand += " WHERE family_code = '" + aFamilyCode + "'";
            SQLCommand += " AND subfamily_code = '" + aSubfamilyCode + "'";
            SQLCommand += " AND mfgr_code = '" + aMfgrCode + "'";
            SQLCommand += " AND series_code = '" + aSeries + "'";
            SQLCommand += " AND option_list_type = 'C'";
            debug (8, SQLCommand);
            rc = conn1.runUpdate(SQLCommand);
            message = "  Family/Subfamil/Mfgr/Series Children Options were";
            if (!rc) {
                message += " NOT";
            }
            message += " deleted successfully for " + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "/" + aSeries;
            if (!rc) {
                debug (2, message);
            }
        }   
        return rc;
    }   
    
/*        
    private boolean deleteSfcMfgOptions(String aFamilyCode, String aSubfamilyCode, String aMfgrCode) {              // ECP-2
        boolean rc;
        String message = "";
        String SQLCommand = "";
        
        SQLCommand = "DELETE FROM pub.ps_option_lists";
        SQLCommand += " WHERE family_code = '" + aFamilyCode + "'";
        SQLCommand += " AND subfamily_code = '" + aSubfamilyCode + "'";
        SQLCommand += " AND mfgr_code = '" + aMfgrCode + "'";
        debug (8, SQLCommand);
        rc = conn1.runUpdate(SQLCommand);
        message = "  Family/Subfamil/Mfgr Options were";
        if (!rc) {
            message += " NOT";
        }
        message += " deleted successfully. ";
        debug (8, message);
        return rc;
    }
 */      
    private void deleteOptionsArray(ArrayList<GPSopts> optionsArraysMap, int[] vectorMap) { 
        GPSopts optionsArray; // A handle for a single options Array object
        optionsArray = optionsArraysMap.remove(0);
        vectorMap[optionsArray.getSeqNum()] = -1;
        optionsArray = null;
    }
    
    private boolean deleteOptionHeaders(String aFamilyCode) {
        String message = "";
        boolean rc;
        String SQLCommand = "";
        
        debug (6, "Attempting to delete option list headers for family code: " 
                + aFamilyCode );
        SQLCommand = "DELETE FROM pub.ps_option_lists";
        SQLCommand += " WHERE family_code = '" + aFamilyCode + "'";
        SQLCommand += " AND option_list_type = 'P'"; 
        SQLCommand += " AND option_value_index = 0";   
        debug (8, SQLCommand);
        rc = conn1.runUpdate(SQLCommand);
        message = "  Option Headers were";
        if (!rc) {
            message += " NOT";
        }
        message += " deleted successfully for family code " + aFamilyCode;
        debug (6, message);
        return rc;
    }
    
/*
    
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
            //debug (0, "Attempting to delete option lists for family/subfamily/mfgr/series: " 
            //        + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "/" + aSeriesCode + "...");
            SQLCommand = "DELETE FROM pub.ps_option_lists";
            SQLCommand += " WHERE family_code = '" + aFamilyCode + "'";
            SQLCommand += " AND subfamily_code = '" + aSubfamilyCode + "'";
            SQLCommand += " AND mfgr_code = '" + aMfgrCode + "'";
            SQLCommand += " AND series_code = '" + aSeriesCode + "'";
            //debug (0, SQLCommand);
            rc = aConn.runUpdate(SQLCommand);
            message = "  Options were";
            if (!rc) {
                message += " NOT";
            }
            message += " deleted successfully. ";
            //debug (0, message);
            return true;
        } catch (Exception e) {
            message = "Unexpected error deleting options.";
            System.out.println(message);
            e.printStackTrace();
            return false;
        }
    }
 */
 /*   
    private boolean deleteOrphanedFamilyJunk(String aFamilyCode) {              // ECP-2
        boolean rc;
        String message = "";
        String SQLCommand = "";
        
        //debug (0, "Attempting to delete option lists for family/subfamily/Mfg: '" 
        //            + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "'...");
        SQLCommand = "DELETE FROM pub.ps_option_lists";
        SQLCommand += " WHERE family_code = '" + aFamilyCode + "'";
        //debug (0, SQLCommand);
        rc = conn1.runUpdate(SQLCommand);
        message = "  Orphaned Options were";
        if (!rc) {
            message += " NOT";
        }
        message += " deleted successfully. ";
        //debug (0, message);
        return rc;
    }
   */
    
    private void doCleanUp() {
        int count = 0;
        String fc = "";
        String mc = "";
        String message = "";
        String queryString = "";
        boolean rc;
        ResultSet rs;
        String sc = "";
        String sfc = "";
        String SQLCommand = "";
               
        //debug (0, "Attempting to delete orphaned option lists from previous runs..."); 
        
        /* Perform atomic deletions to avoid lock table full errors */
        
        try {
            queryString =  "SELECT DISTINCT family_code, subfamily_code, mfgr_code, series_code";
            queryString += " FROM pub.ps_option_lists";
            queryString += " WHERE date_created  <> '" + dateCreated + "'";
            rs = conn1.runQuery(queryString); // a whole lot of junk happens here             
            
            /////////////////////////////////////////////////////////////////////////////
            // Now we will Process the Result Set and                                  //
            // delete all options that have been orphaned                              //
            /////////////////////////////////////////////////////////////////////////////

            if (rs != null) {
                //debug (0, "I got the Result Set for orphaned options.");
                while (rs.next()) {   
                    count++;
                    fc  = rs.getString("family_code");
                    sfc = rs.getString("subfamily_code");
                    mc  = rs.getString("mfgr_code");
                    sc  = rs.getString("series_code");
                    SQLCommand = "DELETE FROM pub.ps_option_lists";
                    SQLCommand += " WHERE family_code = '" + fc + "'";
                    SQLCommand += " AND   subfamily_code = '" + sfc + "'";
                    SQLCommand += " AND   mfgr_code = '" + mc + "'";
                    SQLCommand += " AND   series_code = '" + sc + "'";
                    //debug (0, SQLCommand);
                    rc = conn1.runUpdate(SQLCommand);
                    message = "  Orphaned Options were";
                    if (!rc) {
                        message += " NOT";
                    }
                    message += " deleted successfully for";
                    message += " family code = '" + fc + "'";
                    message += "; subfamily code = '" + sfc + "'";  
                    message += "; mfgr code = '" + mc + "'";
                    message += "; series code = '" + sc + "'.";
                    debug (0, message);
                }
                //debug (0, count + " orphaned option lists were deleted.");
            } else {
                //debug (0, "Resultset was null.");
            }    
        } catch (Exception e) {
                e.printStackTrace();
        }
        return;
    }
    
    protected void done() {
        if (running) {
            instanceCount = 0;
            shutDown = false;
            running = false;
        }    
    }
    
    //Warning! This will never be called for a servlet
    // instead servlets use void destroy() to do cleanup
    
    //protected void finalize() {
    //    done();
    //    //System.out.printf("Finalizing Servlet " + SERVLET_NAME + "; instance count is now %d\n", instanceCount);
    //}
    
    private void finishOldFamily() {
        childArrayIndex = -1;  // ECP-1     // Reset the Child Array starting index 
        sqlHeaders.clear();   // ECP-5
        while (optionsArraysFamilyG.size() > 0) {
            optionsArray = optionsArraysFamilyG.get(0); 
            writeNewOptionsToDatabase(conn1, familyCodePrev, "*", "*", "*", optionsArray);
            deleteOptionsArray(optionsArraysFamilyG, optionsArraysFamilyGVectorMap); 
        }
        // Begin ECP-5
        if (sqlHeaderEnabled) {
            int sqlHeaderIndex = 0;
            String message = "";
            while (sqlHeaderIndex < sqlHeaders.size()) {
                boolean rc = conn1.runUpdate(sqlHeaders.get(sqlHeaderIndex++));
                message = "  Family Level Header Option 0 was";
                if (!rc) {
                    message += " NOT";
                }
                message += " written to database successfully for family code ";
                message += familyCodePrev + " option list ";
                message += sqlHeaderIndex;
                //debug (0, message);
            }
        }
        // End ECP-5
        optionsArraysFamilyG.clear();
        for (int i = 0; i < 100; i++) {
            optionsArraysFamilyGVectorMap[i] = -1;
        }
        completedFamily = familyCodePrev;
        optionsArray = null;
        System.gc();            // call the garbage collector
    }
   
    private void finishOldMfgr() {
        childArrayIndex = -1;  // ECP-1     // Reset the Child Array starting index 
        sqlHeaders.clear();   // ECP-5
        copyMfgrOptionsToSubfamily();    
        while (optionsArraysMfgr.size() > 0) {
            optionsArray = optionsArraysMfgr.get(0); 
            if (doMfg) {
                //debug (0, "Writing options for " + mfgrCodePrev);
                writeNewOptionsToDatabase(conn1, familyCodePrev, subfamilyCodePrev, mfgrCodePrev, "*", optionsArray);
            }
            deleteOptionsArray(optionsArraysMfgr, optionsArraysMfgrVectorMap); 
        }
        if (doGlobal) {   // Disabled for now
            childArrayIndex = -1;  // ECP-1     // Reset the Child Array starting index
            while (optionsArraysMfgrG.size() > 0) {
                optionsArray = optionsArraysMfgrG.get(0); 
                if (doMfg) {
                    writeNewOptionsToDatabase(conn1, familyCodePrev, "*", mfgrCodePrev, "*", optionsArray);
                }
                deleteOptionsArray(optionsArraysMfgrG, optionsArraysMfgrGVectorMap); 
            }
        }
        // Begin ECP-5
        if (sqlHeaderEnabled) {
            int sqlHeaderIndex = 0;
            String message = "";
            while (sqlHeaderIndex < sqlHeaders.size()) {
                boolean rc = conn1.runUpdate(sqlHeaders.get(sqlHeaderIndex++));
                message = "  Mfgr Level Header Option 0 was";
                if (!rc) {
                    message += " NOT";
                }
                message += " written to database successfully for mfgr code ";
                message += mfgrCodePrev + " option list ";
                message += sqlHeaderIndex;
                //debug (0, message);
            }
        }
        // End ECP-5
        
        optionsArraysMfgr.clear();
        optionsArraysMfgrG.clear(); 
        for (int i = 0; i < 100; i++) {
            optionsArraysMfgrVectorMap[i] = -1;
            optionsArraysMfgrGVectorMap[i] = -1;
        }
        optionsArray = null;
        System.gc();            // call the garbage collector
    }
    
    private void finishOldSeries() {
        String work = seriesCodePrev;
        int seqNum;
        
        if (work.equals("*none*")) {
            work = "";
        }
        childArrayIndex = -1;  // ECP-1     // Reset the Child Array starting index 
        sqlHeaders.clear();   // ECP-5
        copySeriesOptionsToMfgr();    
        while (optionsArraysSeries.size() > 0) {
             optionsArray = optionsArraysSeries.get(0); 
             seqNum = optionsArray.getSeqNum();
             if (doMfg && doSeries ) {
                 debug (8, "Writing options for Mfg/Series/SeqNum " 
                         + familyCodePrev + "/" + subfamilyCodePrev + "/"
                         + mfgrCodePrev + "/" + work + "/" + seqNum); 
                 if (work.length() < 129) {
                     writeNewOptionsToDatabase(conn1, familyCodePrev, subfamilyCodePrev, mfgrCodePrev, work, optionsArray);
                 } else {
                     debug (0, "    **************Series '" + work + "' string is too long, skipping series...");
                 }
             }
             deleteOptionsArray(optionsArraysSeries, optionsArraysSeriesVectorMap); 
        }
        if (doGlobal) {  // Disabled for now
            childArrayIndex = -1;  // ECP-1     // Reset the Child Array starting index
            while (optionsArraysSeriesG.size() > 0) {
                optionsArray = optionsArraysSeriesG.get(0); 
                if (doMfg && doSeries ) {
                    writeNewOptionsToDatabase(conn1, familyCodePrev, "*", mfgrCodePrev, work, optionsArray);
                }
                deleteOptionsArray(optionsArraysSeriesG, optionsArraysSeriesGVectorMap); 
            }
        }
        // Begin ECP-5
        if (sqlHeaderEnabled) {
            int sqlHeaderIndex = 0;
            String message = "";
            while (sqlHeaderIndex < sqlHeaders.size()) {
                boolean rc = conn1.runUpdate(sqlHeaders.get(sqlHeaderIndex++));
                message = "  Series Level Header Option 0 was";
                if (!rc) {
                    message += " NOT";
                }
                message += " written to database successfully for series code '";
                message += work + "' option list ";
                message += sqlHeaderIndex;
                debug (8, message);
            }
        }
        // End ECP-5
        
        optionsArraysSeries.clear();
        optionsArraysSeriesG.clear(); 
        for (int i = 0; i < 100; i++) {
            optionsArraysSeriesVectorMap[i] = -1;
            optionsArraysSeriesGVectorMap[i] = -1;
        }
        optionsArray = null;
        System.gc();            // call the garbage collector
    }
 
    private void finishOldSubfamily() {
        childArrayIndex = -1;  // ECP-1     // Reset the Child Array starting index 
        sqlHeaders.clear();   // ECP-5
        if (doGlobal) {
            copySubfamilyOptionsToFamily();
        }
        while (optionsArraysSubfamily.size() > 0) {
            optionsArray = optionsArraysSubfamily.get(0); 
            writeNewOptionsToDatabase(conn1, familyCodePrev, subfamilyCodePrev, "*", "*", optionsArray);
            deleteOptionsArray(optionsArraysSubfamily, optionsArraysSubfamilyVectorMap); 
        }
        // Begin ECP-5
        if (sqlHeaderEnabled) {
            int sqlHeaderIndex = 0;
            String message = "";
            while (sqlHeaderIndex < sqlHeaders.size()) {
                boolean rc = conn1.runUpdate(sqlHeaders.get(sqlHeaderIndex++));
                message = "  Subfamily Level Header Option 0 was";
                if (!rc) {
                    message += " NOT";
                }
                message += " written to database successfully for subfamily code ";
                message += subfamilyCodePrev + " option list ";
                message += sqlHeaderIndex;
                //debug (0, message);
            }
        }
        // End ECP-5
        
        optionsArraysSubfamily.clear();
        optionsArraysSubfamilyG.clear(); 
        for (int i = 0; i < 100; i++) {
            optionsArraysSubfamilyVectorMap[i] = -1;
            optionsArraysSubfamilyGVectorMap[i] = -1;
        }
        optionsArray = null;
        completedSubfamily = subfamilyCodePrev;
        System.gc();            // call the garbage collector
    }
    
    private String fmt(long junk) {
        int k = 0;
        String src = Long.toString(junk);
        String work = "";
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
    
    private void loadFamilyRuleSets(String aFamilyCode) {    // This is currently disabled because doGlobal is false
        int i;
        fieldSetFamily = new GPSfieldSet();
        ruleSetsFamily = fieldSetFamily.getRules(conn1, aFamilyCode, "*", GPSfieldSet.SEARCH_ORDER);
        //debug (0, "Found " + fieldSetFamily.size() + " Family Level (Global) rulesets for family code " 
        //            + familyCode + " in " + SERVLET_NAME);
        // Make a vector map where seq num as an index points to ruleset # in searchFieldSet[]
        for (i = 0; i < 100; i++) {
            ruleSetFamilyVectorMap[i] = -1; // initially vector map is empty;  index i = seq num
            optionsArraysFamilyGVectorMap[i] = -1;  
        }
        ruleSetFamilyVectorMapCount = 0; // number of parm field rulesets currently in vector map        
        for (i = 0; i < fieldSetFamily.size(); i++) {
            ruleSet = ruleSetsFamily[i];   // get the ruleset for this family-level parm field
            ruleSetFamilyVectorMap[ruleSet.getSeqNum()] = i;  // Using seqNum as the index, point this vector to the ruleSet index 
            if (ruleSet.getParmDelimiter() == null) {
                ruleSet.setParmDelimiter("");            // Make sure parmDelimiter rule for this field is not null                    
            }
            if ("NS".contains(ruleSet.getDataType()) && ruleSet.getQobject().equals("S") && ruleSet.getQtextBoxSize() == 0 ) {
                // Get name of pre-built select boxes for numeric / string data types
                if (ruleSet.getQselectBoxName() == null) {
                    ruleSet.setQselectBoxName("");   // Make sure Query selectbox name for this field is NOT null
                }
            }
            ruleSetFamilyVectorMapCount++;     // Bump count
        }
        // Note: ruleSetVectorMapCount should now equal fieldSet.size()
        //debug (0, SERVLET_NAME + " found " + ruleSetFamilyVectorMapCount + " Family Level Search parm ruleSets. ");
    }
    
    private void loadSubfamilyRuleSets() {
        int i;
        fieldSetSubfamily = new GPSfieldSet();
        ruleSetsSubfamily = fieldSetSubfamily.getRules(conn1, familyCodeCurr, subfamilyCode, GPSfieldSet.SEARCH_ORDER);
        debug (8, "Found " + fieldSetSubfamily.size() + " subfamily rulesets for family code " 
                                    + familyCodeCurr + " subfamily code " + subfamilyCode);
        // Make a vector map where seq num as an index points to ruleset # in searchFieldSet[]
        for (i = 0; i < 100; i++) {
            ruleSetSubfamilyVectorMap[i] = -1; // initially vector map is empty;  index i = seq num
            optionsArraysSubfamilyVectorMap[i] = -1;
            optionsArraysSubfamilyGVectorMap[i] = -1;
        }
        ruleSetSubfamilyVectorMapCount = 0; // number of parm field rule sets currently in vector map
        for (i = 0; i < fieldSetSubfamily.size(); i++) {
            ruleSet = ruleSetsSubfamily[i];
            ruleSetSubfamilyVectorMap[ruleSet.getSeqNum()] = i;
            if (ruleSet.getParmDelimiter() == null) {
                ruleSet.setParmDelimiter("");
            }
            if ("NS".contains(ruleSet.getDataType()) && ruleSet.getQobject().equals("S") && ruleSet.getQtextBoxSize() == 0 ) {
                // Get name of pre-built select boxes for numeric / string data types
                if (ruleSet.getQselectBoxName() == null) {
                    ruleSet.setQselectBoxName("");
                }
            }
            ruleSetSubfamilyVectorMapCount++;
        }
        debug (8, SERVLET_NAME + " found " + ruleSetSubfamilyVectorMapCount + " Subfamily Search field ruleSets. ");
    }
    
    private void startNewFamily() {
        familyCodePrev = familyCodeCurr;
        familyName = GPSfamilyCodes.lookUpFamilyName(conn1, familyCodeCurr);
        if (familyName.startsWith("**")) {         
            familyName = "*** Not Found ***";
        }
        if (!checkForShutdown()) {
            debug (8, "****************************");
            debug (2, "Processing part numbers found for family code '" + familyCodeCurr +"' in catalogitem table.");
            debug (8, "****************************");
            if (doGlobal) {
                loadFamilyRuleSets(familyCodeCurr);
            } 
            if (!deleteFamilyOptionsFromDatabase(familyCodeCurr)) {
                closeAll();
                done();
                System.out.println(SERVLET_NAME + " Error deleting ps_options from database; Aborting...");  
            }
        }
    }    
    
    private void startNewMfgr() {
        mfgrCodePrev = mfgrCode;
        for (int i = 0; i < 100; i++) {
            optionsArraysMfgrVectorMap[i] = -1;
            optionsArraysMfgrGVectorMap[i] = -1;
        }
    }
    
    private void startNewSeries() {
        seriesCodePrev = seriesCode;
        for (int i = 0; i < 100; i++) {
            optionsArraysSeriesVectorMap[i] = -1;
            optionsArraysSeriesGVectorMap[i] = -1;
        }
    }
    
    private void startNewSubfamily() {
        subfamilyCodePrev = subfamilyCode;
        if (!abortSwitch) {
            if (!checkForShutdown()) {
                loadSubfamilyRuleSets();
                debug (2, ">>>>Processing part numbers found for subfamily code '" + subfamilyCode +"' in catalogitem table.");
            }
        }
    }
        
    private boolean writeNewOptionsToDatabase(
            WDSconnect aConn,
            String aFamilyCode,
            String aSubfamilyCode,
            String aMfgrCode,
            String aSeriesCode,
            GPSopts aOptionsArray) {
        
        String famCode = aOptionsArray.getFamilyCode();
        String subfamCode = aOptionsArray.getSubfamilyCode();
        String childPointer = "";                          // ECP-1
        int count;
        String message = "";
        int parentOptionIndex = 0;                         // ECP-1
        boolean rangeFound = false;                        // ECP-1
        String rawValue = "";                              // ECP-1
        boolean rc;
        int seqNum = aOptionsArray.getSeqNum();
        String SQLCommand = "";
        String work = "";
        String bSeriesCode = aSeriesCode;
        aFamilyCode = fcTestPrefix + aFamilyCode;                                                        // ECP-99
        //debug (0, "Cooking options for family/subfamily/mfgr/series/index/SeqNum: " 
        //            + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "/" + aSeriesCode
        //            + "/" + seqNum + "...");
        aOptionsArray.cookOptionList();
        count = aOptionsArray.getListEntriesRawSize();
        String aFamCode = fcTestPrefix + famCode;
        //debug (0, "Processing family:  " + aFamCode + "/" + aFamilyCode);
        //debug (0, "Processing subfamily:  " + subfamCode + "/" + aSubfamilyCode);
        try {
            //debug (0, "Writing " + count + " options for family/subfamily/mfgr/series/SeqNum/ParmName: " 
            //        + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "/" + aSeriesCode
            //        + "/" + seqNum + "/" + aOptionsArray.getParmName() + " to database...");
            //debug (0, "Processing seqNum:  " + seqNum);
            SQLCommand = "INSERT INTO pub.ps_option_lists";
            SQLCommand += " (family_code, subfamily_code, mfgr_code, series_code,";
            SQLCommand += " option_list_type, option_list_index, ";
            SQLCommand += " option_value_index, option_value_raw, option_value_cooked, option_value_units,";
            SQLCommand += " date_created ) VALUES (";
            SQLCommand += " '" + aFamilyCode + "'";
            SQLCommand += ",'" + aSubfamilyCode + "'"; 
            SQLCommand += ",'" + aMfgrCode + "'";
            SQLCommand += ",'" + bSeriesCode + "'";
            SQLCommand += ",'P'";
            SQLCommand += "," + seqNum;
            SQLCommand += ", 0";
            SQLCommand += ", " + seqNum;
            SQLCommand += ",'" + aOptionsArray.getParmName() + "'";
            SQLCommand += ",'" + aOptionsArray.getDisplayUnits() + "'";
            SQLCommand += ",'" + dateCreated + "'";
            SQLCommand += ")";
            if (!sqlHeaderEnabled) {
                rc = aConn.runUpdate(SQLCommand);
                message = "  Header Option 0 was";
                if (!rc) {
                    message += " NOT";
                }
                message += " written to database successfully. ";
                message += ",'" + aOptionsArray.getParmName() + "'";
                //debug (0, message);
            } else {
                sqlHeaders.add(SQLCommand); // ECP-5
            }
            for (int i = 0; i < count; i++) {
                parentOptionIndex = i + 1;                              // ECP-1
                rangeFound = false;                                     // ECP-1
                childPointer = "";                                      // ECP-1
                rawValue = aOptionsArray.getListEntryRaw(i);            // ECP-1
                if (rawValue.startsWith(BEGINPAIR) &&                   // ECP-1
                    rawValue.indexOf(MIDPAIR,3) > 0 &&                  // ECP-1
                    rawValue.endsWith(ENDPAIR)) {                       // ECP-1
                    rangeFound = aOptionsArray.buildChildren(rawValue); // ECP-1
                    if (rangeFound) {                                   // ECP-1
                        childPointer = "#" + ++childArrayIndex;         // ECP-1
                        //debug (0, "Generating child array # " + childArrayIndex);
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
                
                //debug (0, "Inserting '" + rawValue + "' for Seq Num " + seqNum);
                
                SQLCommand = "INSERT INTO pub.ps_option_lists";
                SQLCommand += " (family_code, subfamily_code, mfgr_code, series_code,";
                SQLCommand += " option_list_type, option_list_index, ";
                SQLCommand += " option_value_index, option_value_raw, option_value_cooked, option_value_units,";
                SQLCommand += " date_created ) VALUES (";
                SQLCommand += " '" + aFamilyCode + "'";
                SQLCommand += ",'" + aSubfamilyCode + "'"; 
                SQLCommand += ",'" + aMfgrCode + "'";
                SQLCommand += ",'" + bSeriesCode + "'";
                SQLCommand += ",'P'";
                SQLCommand += "," + seqNum;
                SQLCommand += "," + parentOptionIndex;
                SQLCommand += ",'" + rawValue + "'";   // ECP-1
                work = aOptionsArray.getListEntryCooked(i);
                SQLCommand += ",'" + work + "'"; 
                SQLCommand += ",'" + childPointer + "'";
                SQLCommand += ",'" + dateCreated + "'";
                SQLCommand += ")";
                if (rawValue.length() < 256) {
                    debug (8, SQLCommand);
                    rc = aConn.runUpdate(SQLCommand);
                } else {
                    debug (0, "   Skipping rawValue '" + rawValue + "' (length = " + rawValue.length() + ") in family/subfamily/Mfgr/Series/seqNum "
                            + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "/" + bSeriesCode + "/" + seqNum);
                    rc = false;
                }
                message = "  Option " + i + " was";
                if (!rc) {
                    message += " NOT";
                }
                message += " written to database successfully. ";
                message += " '" + aFamilyCode + "'";
                message += ",'" + aSubfamilyCode + "'"; 
                message += ",'" + aMfgrCode + "'";
                message += ",'" + aSeriesCode + "'";                
                message += ",'" + aOptionsArray.getListEntryCooked(i) + "'";                     
                
                debug (8, message);
                if (rangeFound) {
                    boolean x = writeNewChildOptionsToDatabase(aConn, aFamilyCode, aSubfamilyCode, aMfgrCode, aSeriesCode, 
                                                   childArrayIndex, parentOptionIndex, aOptionsArray, rawValue);
               }                
            }
            //debug (0, "Free Heap is now " + fmt(Runtime.getRuntime().freeMemory()));
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
        String SQLCommand = "";
        String bSeriesCode = aSeriesCode;
        //debug (0, "Processing child options for range " + aRange + " parent in family/subfamily/mfgr/series/ChildArrayIndex: " 
        //            + aFamilyCode + "/" + aSubfamilyCode + "/" + aMfgrCode + "/" + aSeriesCode
        //            + "/" + aChildArrayIndex + "...");
        count = aOptionsArray.getChildrenSize();
        if (count > 0) {
            try {
                //debug (0, "Writing " + count + " child options for this range to the database...");
                SQLCommand = "INSERT INTO pub.ps_option_lists";
                SQLCommand += " (family_code, subfamily_code, mfgr_code, series_code,";
                SQLCommand += " option_list_type, option_list_index, ";
                SQLCommand += " option_value_index, option_value_raw, option_value_cooked, option_value_units,";
                SQLCommand += " date_created ) VALUES (";
                SQLCommand += " '" + aFamilyCode + "'";
                SQLCommand += ",'" + aSubfamilyCode + "'"; 
                SQLCommand += ",'" + aMfgrCode + "'";
                SQLCommand += ",'" + bSeriesCode + "'";
                SQLCommand += ",'C'";
                SQLCommand += "," + aChildArrayIndex;
                SQLCommand += ", 0";
                SQLCommand += ", '" + parentArrayOptionIndex + "'";
                SQLCommand += ",'" + aOptionsArray.getParmName() + "'";
                SQLCommand += ",'" + aOptionsArray.getDisplayUnits() + "'";
                SQLCommand += ",'" + dateCreated + "'";
                SQLCommand += ")";
                rc = aConn.runUpdate(SQLCommand);
                message = "  Header (Option 0) for children was";
                if (!rc) {
                    message += " NOT";
                }
                message += " written to database successfully. ";
                message += " '" + aFamilyCode + "'";
                message += ",'" + aSubfamilyCode + "'"; 
                message += ",'" + aMfgrCode + "'";
                message += ",'" + aSeriesCode + "'";
                if (!rc) {
                    debug (2, message);
                }
                for (int i = 0; i < count; i++) {
                    SQLCommand = "INSERT INTO pub.ps_option_lists";
                    SQLCommand += " (family_code, subfamily_code, mfgr_code, series_code,";
                    SQLCommand += " option_list_type, option_list_index, ";
                    SQLCommand += " option_value_index, option_value_raw, option_value_cooked, option_value_units,";
                    SQLCommand += " date_created ) VALUES (";
                    SQLCommand += " '" + aFamilyCode + "'";
                    SQLCommand += ",'" + aSubfamilyCode + "'"; 
                    SQLCommand += ",'" + aMfgrCode + "'";
                    SQLCommand += ",'" + bSeriesCode + "'";
                    SQLCommand += ",'C'";
                    SQLCommand += "," + aChildArrayIndex;
                    SQLCommand += "," + (i + 1);
                    SQLCommand += ",'" + aOptionsArray.getChildRaw(i) + "'";
                    SQLCommand += ",'" + aOptionsArray.getChildCooked(i) + "'";
                    SQLCommand += ",''";
                    SQLCommand += ",'" + dateCreated + "'";
                    SQLCommand += ")";
                    debug (8, SQLCommand);
                    rc = aConn.runUpdate(SQLCommand);
                    message = "  Child Option " + i + " was";
                    if (!rc) {
                        message += " NOT";
                    }
                    message += " written to database successfully. ";
                    message += " '" + aFamilyCode + "'";
                    message += ",'" + aSubfamilyCode + "'"; 
                    message += ",'" + aMfgrCode + "'";
                    message += ",'" + aSeriesCode + "'";
                    if (!rc) {
                        debug (2, message);
                    }
                }
                //debug (0, "Free Heap is now " + fmt(Runtime.getRuntime().freeMemory()));
                return true;
            } catch (Exception e) {
                message = "Unexpected error while writing children options to database.";
                System.out.println(message);
                e.printStackTrace();
                return false;
            }
        } else {
            debug (0, "Failed to create child options for family/subfamily/mfgr/series/ChildArrayIndex: " 
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
