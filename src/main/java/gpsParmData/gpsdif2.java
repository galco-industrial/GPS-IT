/*
 * gpsdif2.java
 *
 * Created on September 5, 2006, 5:29 PM
 */

package gpsParmData;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import gps.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import sauter.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.01
 *
 *
 * I import data from the worksheet into the part table and parametric data table.
 * The import file is a worksheet in CSV format.
 *
 * Modification History
 *
 * Date     Who What
 * 6/6/2007 DES Added code to support import/export of dimensions and weight
 *              field info contained inside the part table
 * 6/19/2007 DES Added code to print out number of rows processed.
 * 6/19/2007 DES Extract missing Mfgr Code from PN if I can
 * 6/19/2007 DES Ignore checking blank numeric and string fields against select box entries
 *                  if field is blank and not required for DE
 * 8/15/2007 DES Modified code to import weight iff unit = "oz" and either net or gross are valid and nonzero
 * 2/24/2010 DES Added sleepTime variable to throttle the Progress SQL broker run time
 * 4/27/2011 DES Added code to update audit date / time / userID
 *
 */
public class gpsdif2 extends HttpServlet {
            
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.01";
       
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
     
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
                        
        /* Boilerplate */
        int debugLevel = 0;
        HttpSession session = null;
        String sWork = "";
        String userID = "";
        String userRole = "";
        String uStamp = "";
        
        session = request.getSession();
        if (session.isNew()) {
            response.sendRedirect ("gpstimeout.htm");
            return;
        }
        sWork = (String) session.getAttribute("debugLevel");
        if (sWork != null) {
            debugLevel = Integer.parseInt(sWork);
        }
        sWork = (String) session.getAttribute("userID");
        if (sWork != null) {
            userID = sWork;
        }
        sWork = (String) session.getAttribute("userRole");
        if (sWork != null) {
            userRole = sWork;
        }
        uStamp = SERVLET_NAME + " Version " + VERSION + " User ID '" + userID + "' Role '" + userRole + "'";
        debug (debugLevel, 10, uStamp + " executing.");        
        /* End Boilerplate */
        
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {        
            sWork = uStamp + " failed to connect to WDS database; aborting.";
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        // **************************************************************
        // *    Declare and initiatize method variables here            *
        // **************************************************************
    
        String abortFileName = "abort.";
        int abortInterval = 50; // Check for abort semaphore every this many records
        int abortTick = 1;
        String action = "";
        boolean active = false;
        int addRow = 0;
        String auditTimeRaw = DateTime.getTimeRawStr();
        String auditUserID;
        int badCols = 0;
        int badInactiveCols = 0;
        int badRows = 0;
        String charSet;
        int col = 0;
        boolean colIsBad;
        List <String> dataRow;    // a rose is a rose is a rose  (a synonym for the headingRow)
        String dataType;
        int decShift;
        String deMultipliers;
        boolean deRequired;
        String dimensionsDepth;
        float dimensionsDepthF;
        String dimensionsHeight;
        float dimensionsHeightF;
        String dimensionsWidth;   
        float dimensionsWidthF;
        boolean editOnly;
        String enableToolTips;
        String errorMsg;
        int errors = 0;
        int expectedCols;
        String familyCode;
        String familyDescription;
        GPSrules fieldRules[];        // Class to create a collection of fields and their rules
        GPSselectBox fieldSelBoxes[]; // Class array of SelectBoxes and their contents
        GPSfieldSet fieldSet;         // 
        String fieldToValidate;
        String fileExt = ".csv";
        String fsep = File.separator;
        String flags;
        int foundCols;
        String gpsImportPath = getServletContext().getInitParameter("importPath");
        List <String> headingRow;   // an ArrayList object used in processing spreadsheet columns
        int i;
        int i1;
        int i2;
        boolean importDimensions;
        String importFileName;
        String importFQFileName = "";
        String importFQLogFileName = "";
        String importLogFileName;
        boolean importSeries;
        boolean importWeight;
        BufferedReader in = null;   // input file stream
        String inputLine;
        int j;  
        int lastDash = -1;
        int lastParmCol;
        String logExt = ".log";
        boolean matchesSelectBox;
        float maximum;
        int maxLen;
        String mfgrCode;
        float minimum;
        int minLen;
        PrintWriter out = null;     // output file stream for log file
        boolean parmDataExists;
        String parmDelimiter;
        int parmField;
        boolean parmIsBad[] = new boolean[100];
        String parmName;
        List <String> parmsToAdd;   // an ArrayList to hold validated parm data for adding  
        String partNum;
        String partNumExt;
        GPSpart partRec = null;   // a part rec object to contain part rec variables I am using
        int replaceRow = 0;
        String result;
        ResultSet rs = null;        // general purpose rs to hold DB query results
        int row = 0;
        String rowFamilyCode;
        boolean rowIsBad;
        String rowSubfamilyCode;
        GPSrules ruleSet = null;  // a convenient rules object to point to a fieldRules[] item
        GPSselectBox selectBox;   // points to a SelectBox object for this field
        String series;
        long sleepTime = 100;       // Time to sleep in milliseconds after each transaction commit
        String subfamilyCode;
        String subfamilyDescription;
        boolean suppressWeightWarnings;
        boolean suppressDimensionWarnings;
        String traxDate = DateTime.getDateMMDDYY();
        String traxTime = DateTime.getTimeHHMMSS(":");
        GPSunit units = null;
        boolean verbose;
        GPScvt verify = null;   // Class to do raw and cooked conversion yadda
        int warningCount = 0;
        String weightGross;
        float weightGrossF;
        String weightNet;
        float weightNetF;
        String weightUnit;
        String work;
        String work1;
        String work2;
        String work3;
        
        try {
            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

            // ************************************************************
            //  Get form values from HTTP Request; save in local & Session variables
            //  making sure we got xtrol from gpsdif1.        
            // ************************************************************
            work = request.getParameter("B1");
            if (work.equals("Import")) {
                auditUserID = request.getParameter("auditUserID");
                editOnly = request.getParameter("editOnly").equals("N") ? false : true;
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                familyDescription = request.getParameter("familyDescription");
                importFileName = request.getParameter("fileName");
                work = request.getParameter("suppressWeightWarnings");
                abortFileName = gpsImportPath + abortFileName + auditUserID; 
                if (work == null) {
                    work = "N";
                }
                suppressWeightWarnings = work.equals("Y") ? true : false;
                work = request.getParameter("suppressDimensionWarnings");
                if (work == null) {
                    work = "N";
                }
                suppressDimensionWarnings = work.equals("Y") ? true : false;
                work = request.getParameter("importSeries");
                if (work == null) {
                    work = "N";
                }
                importSeries = work.equals("Y") ? true : false;
                work = request.getParameter("importDimensions");
                if (work == null) {
                    work = "N";
                }
                importDimensions = work.equals("Y") ? true : false;
                work = request.getParameter("importWeight");
                if (work == null) {
                    work = "N";
                }
                importWeight = work.equals("Y") ? true : false;
                subfamilyCode = request.getParameter("subfamilyCode");
                subfamilyDescription = request.getParameter("subfamilyDescription");
                verbose = request.getParameter("logLevel").equals("H") ? true : false;
                
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
            } else {
                conn.close();
                request.setAttribute("message", "Module " + SERVLET_NAME + " was not properly invoked");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
        } catch (Exception e){
            conn.close();
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }

        // ********************************************************
        // Try to open the import worksheet   
        // ********************************************************
    
        try {
            deleteAbortFile(abortFileName);
            importFQFileName = gpsImportPath + importFileName + fileExt;
            in = new BufferedReader( new InputStreamReader( new FileInputStream(importFQFileName)));
        } catch (Exception e) {
            conn.close();
            sWork = uStamp + "Error trying to open import file " + importFQFileName + " " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // ********************************************************
        // Now try to open the output log file                    *
        // ********************************************************
    
        try {
            importFQLogFileName = gpsImportPath + importFileName + logExt;
            out = new PrintWriter( new BufferedWriter ( new FileWriter(importFQLogFileName)));
        } catch (Exception e) {
            conn.close();
            in.close();
            sWork = uStamp + "Error trying to open output file " + importFQFileName + logExt + " " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        //**********************************************************
        // Output Log File Headers                                 
        //**********************************************************
    
        try {
            out.println("Galco Parametric Search");
            out.println("Import Log Run Date " + traxDate + " Time " + traxTime);
            out.println("Log file name: " + importFQLogFileName);
            out.println("Import file name: " + importFQFileName);
            if (editOnly) {
                out.println("**** Option 'Edit Only' selected - no data will be imported. ****");
            }
            if (importSeries) {
                out.println("**** Option 'Import Series' selected - non-blank values will be imported to part detail. ****");
            }
            if (importDimensions) {
                out.println("**** Option 'Import Dimensions' selected - non-blank values will be imported to part detail. ****");
            }
            if (importWeight) {
                out.println("**** Option 'Import Weight' selected - non-blank values will be imported to part detail. ****");
            }
        } catch (Exception e){
            in.close();
            out.close();
            conn.close();
            sWork = uStamp +  " Unexpected error writing headers in log file " + e ;
            e.printStackTrace();
            request.setAttribute("message", sWork);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }

        //**************************************************************
        // Let's get a set of rules for each field for this fam/subfam *
        // Be sure to get decimal units shift for numeric units fields *
        //**************************************************************
    
        try {        
            out.println("Looking up parametric rules for data fields...");
            fieldSet = new GPSfieldSet();
            fieldRules = fieldSet.getRules(conn, familyCode, subfamilyCode, GPSfieldSet.SEQUENCE_NUMBER_ORDER);
            // true means we get fields in seqNum order vs de_order
            units = new GPSunit();
            for (i = 0; i < fieldSet.count(); i++) {
                if ( fieldRules[i].getDataType().equals("N") ) {
                    units.open(conn, fieldRules[i].getDisplayUnits());
                    fieldRules[i].setDecShift(units.getMultiplierExp());
                }
                if (!fieldRules[i].getParmStatus().equals("A") ) {
                    out.println("WARNING! - Parm Field " + fieldRules[i].getParmName() 
                        + " - Seq Num " + fieldRules[i].getSeqNum() + " is not ACTIVE.");
                }
            }
            units = null;  // release units object
            expectedCols = fieldSet.count() + 13;
            lastParmCol = expectedCols - 1;
            // The first twelve columns are fixed:
            // Action FamilyCode SubfamilyCode Mfgr Series PN
            // gross weight, net weight, weight units (OZ),
            // Height, width, length (inches),
            // followed by parm columns followed by a terminating column containing "X"
            out.println("Rulesets Lookup is complete.");
        } catch (Exception e){
            e.printStackTrace();
            errorMsg = "An unexpected error occurred in " + SERVLET_NAME + " while getting the GPS rules for the parm fields.<br />" + e ;
            request.setAttribute("message", errorMsg);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            units = null;
            fieldSet = null;
            fieldRules = null;
            in.close();
            out.close();
            conn.close();
            return;
        }

        //**************************************************************
        // Let's get any select boxes associated with fields
        //**************************************************************
    
        try {        
            out.println("Looking up Select Box values for data fields...");
            fieldSelBoxes = new GPSselectBox[99];
            for (i = 0; i < fieldSet.count(); i++) {
                work = fieldRules[i].getDataType();
                if ("NS".indexOf(work) != -1) {
                    if (fieldRules[i].getDeTextBoxSize() == 0) {
                        work = fieldRules[i].getDeSelectBoxName();
                        if (work.length() != 0 ) {
                            fieldSelBoxes[i] = new GPSselectBox();
                            j = fieldSelBoxes[i].open(conn, fieldRules[i].getFamilyCode(), fieldRules[i].getSubfamilyCode(), work);
                            if (j > -1) {
                                if (verbose) {
                                    out.println("Select Box '" + work + "' values were loaded for column " + Integer.toString(i + 1));
                                    for (i1 = 0; i1 < fieldSelBoxes[i].size(); i1++) {
                                        work2 = fieldSelBoxes[i].getOptionText(i1);
                                        out.println("option " + Integer.toString(i1) + " - " + work2);
                                    }
                                }
                            } else {
                                fieldSelBoxes[i] = null;
                                out.println("Error # " + Integer.toString(j) 
                                    + " encountered while reading values for Select Box named'" + work + "'.");
                            }
                        }
                    }
                }
            }
            out.println("Complete.");
        } catch (Exception e){
            e.printStackTrace();
            errorMsg = "An unexpected error occurred in " + SERVLET_NAME + " while getting Select Box contents for the parm fields.<br />" + e ;
            request.setAttribute("message", errorMsg);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            fieldSet = null;
            fieldRules = null;
            fieldSelBoxes = null;
            in.close();
            out.close();
            conn.close();
            return;
        }

        //*******************************************************
        // Read parm names and verify                         *
        //******************************************************* 
    
        try {
            out.println("Verifying Worksheet Parm Field Names...");
            inputLine = in.readLine();
            row++;
            headingRow = new ArrayList <String>();
            headingRow = CSV.getItems(inputLine, ",");  // Read csv data fields into array list
            foundCols = headingRow.size();
            if (foundCols != expectedCols) {
                columnError(expectedCols, foundCols, row, request, response, in, out, conn); 
                return;
            }
            for (i = 12; i < lastParmCol; i++) {
                work1 = (String) headingRow.get(i);
                work1 = work1.trim();
                if (verbose) {
                    out.println("Checking parm field name in column "+ Integer.toString(i + 1));
                }
                work2 = fieldRules[i - 12].getParmName().trim();
                if (!work1.equals(work2)) {
                    out.println("Error - Parm Name mismatch at column " + Integer.toString(i + 1) 
                        + "; Expected " + work2 + "; found " + work1);
                    abortImport(request, response, in, out, conn);
                    return;
                }
            }
            work1 = (String) headingRow.get(lastParmCol);
            work1 = work1.trim();
            if (!work1.equals("EndRow")) {
                out.println("Error - invalid ending column in worksheet at row " + Integer.toString(row) 
                    + "; Expected EndRow; found " + work1);
                abortImport(request, response, in, out, conn);
                return;
            }
            out.println("Complete.");
        
            //*******************************************************
            // Read units names and verify                          *
            //*******************************************************
        
            out.println("Verifying Worksheet Units...");
            inputLine = in.readLine();
            row++;
            headingRow.clear();
            headingRow = CSV.getItems(inputLine, ",");
            foundCols = headingRow.size();
            if (foundCols != expectedCols) {
                columnError(expectedCols, foundCols, row, request, response, in, out, conn); 
                return;
            }
            for (i = 12; i < lastParmCol; i++) {
                work = fieldRules[i-12].getDataType();
                if (work.equals("N")) {
                    work1 = (String) headingRow.get(i);
                    work1 = work1.trim();
                    if (verbose) {
                        out.println("Checking numeric units for column "+ Integer.toString(i + 1));
                    }
                    work2 = fieldRules[i - 12].getDisplayUnits().trim();
                    if (!work1.equals(work2)) {
                        out.println("Error - Units mismatch at column " + Integer.toString(i + 1) + 
                            "; Expected " + work2 + "; found " + work1);
                        abortImport(request, response, in, out, conn);
                        return;
                    }
                }
            }
            work1 = (String) headingRow.get(lastParmCol);
            work1 = work1.trim();
            if (!work1.equals("X")) {
                out.println("Error - invalid ending column in worksheet at row " + Integer.toString(row) + 
                        "; Expected X; found " + work1);
                abortImport(request, response, in, out, conn);
                return;
            }
            out.println("Complete.");
               
            //*******************************************************
            // Read min and verify columns                          *
            //*******************************************************
         
            out.println("Verifying Worksheet Min Columns...");
            inputLine = in.readLine();
            row++;
            headingRow.clear();
            headingRow = CSV.getItems(inputLine, ",");
            foundCols = headingRow.size();
            if (foundCols != expectedCols) {
                columnError(expectedCols, foundCols, row, request, response, in, out, conn); 
                return;
            }
            out.println("Complete.");
                
                
            //*******************************************************
            // Read max and verify columns                          *
            //*******************************************************
        
            out.println("Verifying Worksheet Max Columns");
            inputLine = in.readLine();
            row++;
            headingRow.clear();
            headingRow = CSV.getItems(inputLine, ",");
            foundCols = headingRow.size();
            if (foundCols != expectedCols) {
                columnError(expectedCols, foundCols, row, request, response, in, out, conn); 
                return;
            }
            work1 = (String) headingRow.get(lastParmCol);
            work1 = work1.trim();
            if (!work1.equals("X")) {
                out.println("Error - invalid ending column in worksheet at row " + Integer.toString(row) + 
                        "; Expected X; found " + work1);
                abortImport(request, response, in, out, conn);
                return;
            }
            out.println("Complete.");  
                
            //*******************************************************
            // Read Data Type headers and verify                    *
            //*******************************************************
         
            out.println("Verifying Worksheet Data Type Headers..."); 
            inputLine = in.readLine();
            row++;
            headingRow.clear();
            headingRow = CSV.getItems(inputLine, ",");
            foundCols = headingRow.size();
            if (foundCols != expectedCols) {
                columnError(expectedCols, foundCols, row, request, response, in, out, conn); 
                return;
            }
            for (i = 12; i < lastParmCol; i++) {
                work1 = (String) headingRow.get(i);
                work1 = work1.trim().substring(0,1).toUpperCase();
                work2 = fieldRules[i - 12].getDataType();
                if (!work1.equals(work2)) {
                    out.println("Error - Data Type mismatch at column " + Integer.toString(i + 1) +
                        "; Expected " + work2 + "; found " + work1);
                    abortImport(request, response, in, out, conn);
                    return;
                }
            }
            work1 = (String) headingRow.get(lastParmCol);
            work1 = work1.trim();
            if (!work1.equals("X")) {
                out.println("Error - invalid ending column in worksheet at row " + Integer.toString(row) + 
                        "; Expected X; found " + work1);
                abortImport(request, response, in, out, conn);
                return;
            }
            out.println("Complete.");
            
            //*******************************************************
            // Read Select Box Name headers and verify              *
            //*******************************************************
         
            out.println("Verifying Worksheet Select Box Names Header..."); 
            inputLine = in.readLine();
            row++;
            headingRow.clear();
            headingRow = CSV.getItems(inputLine, ",");
            foundCols = headingRow.size();
            if (foundCols != expectedCols) {
                columnError(expectedCols, foundCols, row, request, response, in, out, conn); 
                return;
            }
            for (i = 12; i < lastParmCol; i++) {
                work1 = (String) headingRow.get(i);
                work1 = work1.trim().toUpperCase();
                work2 = "";
                if ("SN".contains(fieldRules[i - 12].getDataType())) {
                    if (fieldRules[i - 12].getDeObject().equals("S")) {
                        if (fieldRules[i - 12].getDeTextBoxSize() == 0) {
                            work2 = fieldRules[i - 12].getDeSelectBoxName();
                        }
                    }
                }
                if (!work1.equals(work2)) {
                    out.println("Error - Select Box Name mismatch at column " + Integer.toString(i + 1) +
                        "; Expected '" + work2 + "'; found '" + work1 + "'");
                    abortImport(request, response, in, out, conn);
                    return;
                }
            }
            work1 = (String) headingRow.get(lastParmCol);
            work1 = work1.trim();
            if (!work1.equals("X")) {
                out.println("Error - invalid ending column in worksheet at row " + Integer.toString(row) + 
                        "; Expected X; found " + work1);
                abortImport(request, response, in, out, conn);
                return;
            }
            out.println("Complete.");            
            
            //*******************************************************
            // Read Reqd and verify columns                         *
            //*******************************************************
        
            out.println("Verifying Worksheet Req'd Columns...");
            inputLine = in.readLine();
            row++;
            headingRow.clear();
            headingRow = CSV.getItems(inputLine, ",");
            foundCols = headingRow.size();
            if (foundCols != expectedCols) {
                columnError(expectedCols, foundCols, row, request, response, in, out, conn); 
                return;
            }
            work1 = (String) headingRow.get(lastParmCol);
            work1 = work1.trim();
            if (!work1.equals("X")) {
                out.println("Error - invalid ending column in worksheet at row " + Integer.toString(row) + 
                        "; Expected X; found " + work1);
                abortImport(request, response, in, out, conn);
                return;
            }
            out.println("Complete.");  
                                        
            //*******************************************************
            // Read Multipliers     columns                         *
            //*******************************************************
         
            out.println("Verifying Worksheet Multipliers Columns..."); 
            inputLine = in.readLine();
            row++;
            headingRow.clear();
            headingRow = CSV.getItems(inputLine, ",");
            foundCols = headingRow.size();
            if (foundCols != expectedCols) {
                columnError(expectedCols, foundCols, row, request, response, in, out, conn); 
                return;
            }
            work1 = (String) headingRow.get(lastParmCol);
            work1 = work1.trim();
            if (!work1.equals("X")) {
                out.println("Error - invalid ending column in worksheet at row " + Integer.toString(row) + 
                        "; Expected X; found " + work1);
                abortImport(request, response, in, out, conn);
                return;
            }
            out.println("Complete."); 
                        
            //*******************************************************
            // Read Sequence Numbers                                *
            //*******************************************************
         
            out.println("Verifying Worksheet Sequence Number Headers..."); 
            inputLine = in.readLine();
            row++;
            headingRow.clear();
            headingRow = CSV.getItems(inputLine, ",");
            foundCols = headingRow.size();
            if (foundCols != expectedCols) {
                columnError(expectedCols, foundCols, row, request, response, in, out, conn); 
                return;
            }
            for (i = 12; i < lastParmCol; i++) {
                work1 = (String) headingRow.get(i);
                work1 = work1.trim();
                work2 = work1.substring(0,1);
                work1 = work1.substring(1);
                if (!Convert.isInteger(work1)) {
                    work1 = "-1";  // if work is not a valid integer, ensure next test will fail
                }
                i1 = Integer.parseInt(work1);
                i2 = fieldRules[i - 12].getSeqNum();
                if (!(fieldRules[i - 12].getRuleScope().equals(work2)) || i1 != i2) {
                    out.println("Error - Sequence Number mismatch at column " + Integer.toString(i + 1) + 
                        "; Expected " + fieldRules[i - 12].getRuleScope() + Integer.toString(i2) + 
                        "; found " + work2 + Integer.toString(i1));
                    abortImport(request, response, in, out, conn);
                    return;
                }
            }
            work1 = (String) headingRow.get(lastParmCol);
            work1 = work1.trim();
            if (!work1.equals("X")) {
                out.println("Error - invalid ending column in worksheet at row " + Integer.toString(row) + 
                        "; Expected X; found " + work1);
                abortImport(request, response, in, out, conn);
                return;
            }
            out.println("Complete.");
        
            //*******************************************************
            // Read Allow columns 
            //*******************************************************
         
            out.println("Verifying Worksheet Allow Columns...");
            inputLine = in.readLine();
            row++;
            headingRow.clear();
            headingRow = CSV.getItems(inputLine, ",");
            foundCols = headingRow.size();
            if (foundCols != expectedCols) {
                columnError(expectedCols, foundCols, row, request, response, in, out, conn); 
                return;
            }
            work1 = (String) headingRow.get(lastParmCol);
            work1 = work1.trim();
            if (!work1.equals("X")) {
                out.println("Error - invalid ending column in worksheet at row " + Integer.toString(row) + 
                        "; Expected X; found " + work1);
                abortImport(request, response, in, out, conn);
                return;
            }
            out.println("Complete.");
            out.println("Header validation completed successfully.");
        } catch (Exception e){
            e.printStackTrace();
            errorMsg = "An unexpected error occurred in " + SERVLET_NAME + " while verifying worksheet headers.<br />" + e ;
            request.setAttribute("message", errorMsg);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            in.close();
            out.close();
            conn.close();
            return;
        }

        //*********************************************************************
        // Now let's do the data loop thingie and process each row from the
        // worksheet
        //*********************************************************************
    
        try {
            dataRow = headingRow;               // Rename the headingRow ListArray 
            parmsToAdd = new ArrayList <String> ();       // Create an ArrayList object
            partRec = new GPSpart();            // make me a part object :-)
            partRec.setAuditDate(DateTime.getDateMMDDYY());
            partRec.setAuditTime(DateTime.getTimeHHMMSS(":"));
            partRec.setAuditUserID(auditUserID);
            verify = new GPScvt();              // and I'd like to order a cooked/raw object
            if (!editOnly) {
                // Enable transaction management unless I am just doing a casual edit
                if (!conn.enableTransactions()) {
                    out.println("Fatal Error - Attempt to enable Transactions in WDS database failed.");
                    abortImport(request, response, in, out, conn);
                    return;
                }
                if (verbose) {
                    out.println("Database Transaction Management has been enabled.");
                }
            }
            inputLine = in.readLine();     // get the next input record to prime the loop
            while (inputLine != null) {     // Let's rumble!'
                if (verbose) {
                    out.println("Reading next record...");
                }
                row++;                      // bump count
                dataRow.clear();            // init input object
                parmsToAdd.clear();         // init container for edited input yadda
                rowIsBad = false;           // row is assumed good until proven guilty
                if (verbose) {
                    out.println("Checking row " + Integer.toString(row));
                }
                dataRow = CSV.getItems(inputLine, ",");  // parse an input line into the ArrayList
                foundCols = dataRow.size();
                if (foundCols != expectedCols) {
                    columnError(expectedCols, foundCols, row, request, response, in, out, conn); 
                    return;   // No match and We're Hx'
                }
                work1 = (String) dataRow.get(lastParmCol);
                work1 = work1.trim();
                if (!work1.equals("X")) {
                    out.println("Error - invalid ending column in worksheet at row " + Integer.toString(row) + 
                        "; Expected X; found " + work1);
                    abortImport(request, response, in, out, conn);
                    return;
                }
                action = (String) dataRow.get(0);
                action = action.trim();
                rowFamilyCode = (String) dataRow.get(1);
                rowFamilyCode = rowFamilyCode.trim();
                rowSubfamilyCode = (String) dataRow.get(2);
                rowSubfamilyCode = rowSubfamilyCode.trim();
                mfgrCode = (String) dataRow.get(3);
                mfgrCode = mfgrCode.trim().toUpperCase();
                series = (String) dataRow.get(4);
                series = series.trim();
                partNum = (String) dataRow.get(5);
                partNum = partNum.trim().toUpperCase();
                weightGross = (String) dataRow.get(6);
                weightGross = weightGross.trim();
                weightGrossF = Convert.getFloat(weightGross);
                if (!suppressWeightWarnings && weightGrossF <= 0) {
                    out.println("Warning! Invalid or missing Gross Weight at row " + Integer.toString(row) + ", Part Number " + partNum);
                    warningCount++;
                }
                weightNet = (String) dataRow.get(7);
                weightNet = weightNet.trim();
                weightNetF = Convert.getFloat(weightNet);
                if (!suppressWeightWarnings && weightNetF <= 0) {
                    out.println("Warning! Invalid or missing Net Weight at row " + Integer.toString(row) + ", Part Number " + partNum);
                    warningCount++;
                }
                weightUnit = (String) dataRow.get(8);
                weightUnit = weightUnit.trim().toLowerCase();
                if (!suppressWeightWarnings && !weightUnit.equals("oz") ) {
                    out.println("Warning! Invalid or missing Weight Unit at row " + Integer.toString(row) + ", Part Number " + partNum 
                            + "; expected 'oz' found '" + weightUnit + "'");
                    warningCount++;
                }
                dimensionsHeight = (String) dataRow.get(9);
                dimensionsHeight = dimensionsHeight.trim();
                dimensionsHeightF = Convert.getFloat(dimensionsHeight);
                if (!suppressDimensionWarnings && dimensionsHeightF <= 0) {
                    out.println("Warning! Invalid or missing Height at row " + Integer.toString(row) + ", Part Number " + partNum);
                    warningCount++;
                }
                dimensionsWidth = (String) dataRow.get(10);
                dimensionsWidth = dimensionsWidth.trim();
                dimensionsWidthF = Convert.getFloat(dimensionsWidth);
                if (!suppressDimensionWarnings && dimensionsWidthF <= 0) {
                    out.println("Warning! Invalid or missing Width at row " + Integer.toString(row) + ", Part Number " + partNum);
                    warningCount++;
                }
                dimensionsDepth = (String) dataRow.get(11);
                dimensionsDepth = dimensionsDepth.trim();
                dimensionsDepthF = Convert.getFloat(dimensionsDepth);
                if (!suppressDimensionWarnings && dimensionsDepthF <= 0) {
                    out.println("Warning! Invalid or missing Depth at row " + Integer.toString(row) + ", Part Number " + partNum);
                    warningCount++;
                }
            
                // If we got here OK, that means
                // Number of columns match, check for a process request
                // R means replace the data
                // I means insert parm data but do NOT replace existing
                // E means EDIT ONLY - Do not insert any data
                // anything else we ignore
                if (verbose) {
                    out.println("Action is " + action);
                }
                if ("RIE".indexOf(action) != -1) {
                    badCols = 0;
                    // first check that family and subfamily codes match
                    if (!familyCode.equals(rowFamilyCode)) {
                        out.println("Validation Error at row " + Integer.toString(row) + ", invalid Family Code: " + rowFamilyCode + 
                            ", expected: " + familyCode + ", Part Number " + partNum);
                        badCols++;
                    }
                    if (!subfamilyCode.equals(rowSubfamilyCode)) {
                        out.println("Validation Error at row " + Integer.toString(row) + ", invalid Subfamily Code: " + rowSubfamilyCode + 
                            ", expected: " + subfamilyCode + ", Part Number " + partNum);
                        badCols++;
                    }
                    //////////////////////////////////////////////////////////////////////////////////
                    // Modified 06/19/2007 by DES to extract missing Mfgr Code from PN if necessary //
                    //////////////////////////////////////////////////////////////////////////////////
                
                    //if (mfgrCode.length() == 0 && partNum.length() == 0) {
                    //    out.println("Validation Error at row " + Integer.toString(row) + ", missing manufacturer code for Part Number " + partNum);
                    //    badCols++;
                    //}
                    if (partNum.length() == 0) {
                        out.println("Validation Error at row " + Integer.toString(row) + ", missing part number. ");
                        badCols++;
                    } else {
                        // I get here iff the part number is not blank
                        // if Mfgr code was blank,
                        // attempt to extract Mfgr code from a PN if I can
                        if (mfgrCode.length() == 0) {
                            lastDash = partNum.lastIndexOf("-");
                            if (lastDash > -1) {
                                work = partNum.substring(lastDash);
                                if (work.length() > 1 && work.length() < 6) {
                                    mfgrCode = work.substring(1).toUpperCase();
                                }
                            } 
                        }
                        partNumExt = partNum;
                        // Now see if Mfgr Code is still missing...
                        if (mfgrCode.length() == 0) {
                            out.println("Validation Error at row " + Integer.toString(row) + ", missing manufacturer code for Part Number " + partNum);
                            badCols++;
                        } else {
                            if (!partNum.endsWith("-" + mfgrCode)) {
                                partNumExt = partNum + "-" + mfgrCode;
                            }
                        }
                        partNumExt = partNumExt.trim();
                        partNumExt = partNumExt.toUpperCase();
                        // At this point, the part number is in standard WDS format, i.e.,
                        // it ends with a dash followed by a mfgr code and
                        // it is all UC and has no embedded spaces.
                        if (editOnly) {
                            if (!partRec.exists(conn, partNumExt)) {
                                out.println("Validation Error at row " + Integer.toString(row) + ", Part Number " + partNumExt + " not found in WDS database");
                                badCols++;
                            }
                        }
                    }
                
                    // Now Prepare to validate the parametric fields for this row
                
                    if (badCols == 0) {             // only process parms if static fields were OK
                        colIsBad = false;           // init
                        for (col = 12; col < lastParmCol; col++) {      // Plow through each parm field one by one
                            colIsBad = false;                           // column is good unless I find a booboo
                            parmField = col - 12;                       // calculate parm field relative column #
                            ruleSet = fieldRules[parmField];            // get the field ruleset for this relative parm field
                            parmName = ruleSet.getParmName();           // Parm Field Name/label
                            dataType = ruleSet.getDataType();           // Data Type
                            deRequired = ruleSet.getDeRequired();       // true if required for data entry
                            active = ruleSet.getParmStatus().equals("A");
                            fieldToValidate = (String) dataRow.get(col);    // get the field to validate
                            fieldToValidate = fieldToValidate.trim();       // remove any funny spaces
                            parmIsBad[parmField] = false;                // First assume parm in NOT bad
                            if (fieldToValidate.length() == 0) {        // if empty field
                                dataType = "";  // set data type to empty string to skip num/str/log/date validations
                                if (deRequired) {                       // if blank and fields was mandatory, then flag as an error
                                    parmIsBad[parmField] = true;
                                    if (active) {
                                        badCols++;                          // bump bad column count
                                        colIsBad = true;                    // flag this as a bad column
                                    } else {
                                        badInactiveCols++;
                                        out.println("Warning - The following Inactive field contains errors:");
                                    }
                                    out.println("Missing mandatory field at row " + Integer.toString(row) 
                                        + ", Part Number " + partNum + ", column " + parmName);
                                }
                            } else {
                                                
                                // ************************************************
                                // 
                                // Note that when I reach this point
                                // if a field is blank, 
                                // dataType is set to ""
                                // to suppress further validation checks
                                // and ColIsBad is set to true if field was mandatory
                                //
                                // ************************************************
                        
                                // If this field is a select box item and field was not blank,
                                // Set fieldToValidate to OptionValue1 that matches the Option text 
                                // Option Text is a cooked value
                                // OptionValue1 is a corresponding raw value
                        
                                if (fieldSelBoxes[parmField] != null) {
                                    matchesSelectBox = false;
                                    for (i1 = 0; i1 < fieldSelBoxes[parmField].size(); i1++) {
                                        work1 = fieldSelBoxes[parmField].getOptionText(i1);
                                        if (fieldToValidate.equals(work1)) {
                                            fieldToValidate = fieldSelBoxes[parmField].getOptionValue1(i1);
                                            matchesSelectBox = true;
                                            break;
                                        }
                                    }
                                    if (!matchesSelectBox) {
                                        parmIsBad[parmField] = true;
                                        if (active) {
                                            badCols++;                          // bump bad column count
                                            colIsBad = true;                    // flag this as a bad column
                                        } else {
                                            badInactiveCols++;
                                            out.println("Warning - The following Inactive field contains errors:");
                                        }
                                        out.println("No match in Select Box at row " + Integer.toString(row) 
                                            + ", Part Number " + partNum + ", column " + parmName + "', value = " + fieldToValidate);
                                    }
                                }
                            }
                        
                            // ************************************************
                            // Perform validation on a Numeric field
                            // ************************************************
                        
                            if (dataType.equals("N")) {
                                deMultipliers = ruleSet.getDeMultipliers();
                                parmDelimiter = ruleSet.getParmDelimiter();
                                decShift = ruleSet.getDecShift();
                                flags = ruleSet.getFlags();
                                minimum = ruleSet.getMinValueFloat();
                                maximum = ruleSet.getMaxValueFloat();
                                fieldToValidate = verify.toRaw(fieldToValidate, deMultipliers, parmDelimiter, flags, minimum, maximum, decShift, "\n");
                                errorMsg = verify.getErrorMsg();
                                if (errorMsg.length() != 0) {
                                    parmIsBad[parmField] = true;
                                    if (active) {
                                        badCols++;                          // bump bad column count
                                        colIsBad = true;                    // flag this as a bad column
                                    } else {
                                        badInactiveCols++;
                                        out.println("Warning - The following Inactive field contains errors:");
                                    }
                                    out.println("Validation Error at row " + Integer.toString(row) + ", Part Number " 
                                        + partNum + ", column " + parmName + ", value = " + fieldToValidate);
                                    out.println(errorMsg);
                                    
                                }
                            }  // end of numeric
                        
                            // ***********************************************************
                            // STRING Validation
                            // ***********************************************************
                        
                            if (dataType.equals("S")) {
                                parmDelimiter = ruleSet.getParmDelimiter();
                                flags = ruleSet.getFlags();
                                minLen = ruleSet.getMinLength();
                                maxLen = ruleSet.getMaxLength();
                                charSet = ruleSet.getCharSet();
                                fieldToValidate = verify.checkString(fieldToValidate, parmDelimiter, flags, minLen, maxLen, charSet, "\n");
                                errorMsg = verify.getErrorMsg();
                                if (errorMsg.length() != 0) {
                                    parmIsBad[parmField] = true;
                                    if (active) {
                                        badCols++;                          // bump bad column count
                                        colIsBad = true;                    // flag this as a bad column
                                    } else {
                                        badInactiveCols++;
                                        out.println("Warning - The following Inactive field contains errors:");
                                    }
                                    out.println("Validation Error at row " + Integer.toString(row) + ", Part Number " + partNum 
                                        + ", column " + parmName + ", flags were '" + flags + "', value = " + fieldToValidate);
                                    out.println(errorMsg);
                                }
                            }  // end of string validation
                        
                            // **********************************************************
                            // Process Logicals
                            // **********************************************************
                        
                            if (dataType.equals("L")) {
                                work1 = fieldToValidate;
                                work1 = work1.toUpperCase();
                                work1 = work1.substring(0,1);
                                if ("YN".indexOf(work1) == -1) {
                                    parmIsBad[parmField] = true;
                                    if (active) {
                                        badCols++;                          // bump bad column count
                                        colIsBad = true;                    // flag this as a bad column
                                    } else {
                                        badInactiveCols++;
                                        out.println("Warning - The following Inactive field contains errors:");
                                    }
                                    out.println("Expected Y or N at row " + Integer.toString(row) 
                                        + ", Part Number " + partNum + ", column " + parmName + ", value = " + fieldToValidate);

                                } else {
                                    fieldToValidate = work1;
                                }
                            }  // end of logical
                        
                            // ***************************************************
                            // process Date time validation here
                            // ***************************************************
                        
                            if (dataType.equals("D")) {
                                parmIsBad[parmField] = true;
                                if (active) {
                                    badCols++;                          // bump bad column count
                                    colIsBad = true;                    // flag this as a bad column
                                } else {
                                    badInactiveCols++;
                                    out.println("Warning - The following Inactive field contains errors:");
                                }
                                out.println("Dates not supported yet at row " + Integer.toString(row) + ", Part Number " + partNum + ", column " + parmName + ", value = " + work);
                            } // end of date
                        
                            parmsToAdd.add(fieldToValidate);
                        
                            // ***************************************************
                            // finished processing this parm field
                            // ***************************************************
                        
                        } // end FOR - Done checking all parm fields for this row
                                      
                        // *********************************************************************
                        // If Insert or Replace was selected and row passed validation, do this:
                        // *********************************************************************
                    
                        if (!editOnly && badCols == 0) {
                            boolean updateFailed = false;
                            if ("RI".indexOf(action) != -1) {   // action must be Replace or Insert
                                if (verbose) {
                                    out.println("Updating using row " + Integer.toString(row) + ", Part Number " + partNum +
                                        ", action = " + action);
                                }
                            
                                partNumExt = partNum;
                                if (!partNum.endsWith("-" + mfgrCode)) {
                                    partNumExt = partNum + "-" + mfgrCode;
                                }
                                partNumExt = partNumExt.trim();
                                partNumExt = partNumExt.toUpperCase();
                                if (partRec.read(conn, partNumExt)) {  // look up this part number
                                    parmDataExists = GPSparmSet.exists(conn, partNumExt);
                                    if (action.equals("R") && parmDataExists) {
                                        // delete any parm data if present when action = REPLACE
                                        if (verbose) {
                                            out.println("Attempting to delete existing paramteric data for " + partNum + "...");
                                        }
                                        if (!GPSparmSet.delete(conn, partNumExt)) {
                                            updateFailed = true;
                                            out.println("Attempt to delete existing parametric data failed for part number " + partNum + " at row " + Integer.toString(row));
                                            conn.rollback();
                                            abortImport(request, response, in, out, conn);
                                            return;
                                        } else {
                                            if (verbose) {out.println("I just deleted existing parametric data for " + partNum); }
                                            parmDataExists = false;
                                        }
                                    }
                                
                                    // ******************************************
                                    // Now Insert the parametric data
                                    // ******************************************
                                
                                    if (!parmDataExists) {
                                        if (verbose) {
                                            out.println("Attempting to add parametric data for " + partNum + "... ");
                                        }
                                        for (int n = 0; n < parmsToAdd.size(); n++) {
                                            work = (String) parmsToAdd.get(n);
                                            ruleSet = fieldRules[n];
                                            if (!parmIsBad[n]) {  // Add all parms that were good -- even if field is inactive
                                                if (!GPSparmSet.add(conn, partNumExt, ruleSet.getSeqNum(), work, traxDate, auditTimeRaw, auditUserID)) {
                                                    updateFailed = true;
                                                    out.print("Attempt to add parametric data failed for part number " + partNum + " at row " + Integer.toString(row));
                                                    conn.rollback();
                                                    abortImport(request, response, in, out, conn);
                                                    return;
                                                }
                                            }
                                        }
                                        if (verbose) {
                                            out.println("Success.\nAttempting to update part record...");
                                        }
                                        partRec.setFamilyCode(familyCode);
                                        partRec.setSubfamilyCode(subfamilyCode);
                                        partRec.setHasPSData(true);
                                    
                                        // Import Series ????
                                    
                                        if (series.length() != 0) {
                                            if (importSeries) {
                                                if (series.equalsIgnoreCase("$DEL$")) {
                                                    series = "";
                                                }
                                                partRec.setSeries(series);
                                                if (verbose) {
                                                    out.println("    Series will be set to '" + series +"'.");
                                                }
                                            }
                                        }
                                        if (importDimensions) {
                                            // We will only import the Dimensions
                                            // iff all three dimensions
                                            // are valid numerics and non-zero
                                            if (dimensionsHeightF > 0
                                                    && dimensionsWidthF > 0
                                                    && dimensionsDepthF > 0) {
                                                partRec.setDimensionsHeight(dimensionsHeight);
                                                partRec.setDimensionsWidth(dimensionsWidth);
                                                partRec.setDimensionsLength(dimensionsDepth);
                                                if (verbose) {
                                                    out.println("    Dimensions will be updated.");
                                                }
                                            }
                                        }
                                        /////////////////////////////////////////////////////////////
                                        // Modified 8/15/2007 DES                                  //
                                        /////////////////////////////////////////////////////////////
                                        if (importWeight) {
                                            // We will only import the weight
                                            // iff the weight unit is "oz"
                                            // (oz is the standard)
                                            // and either the gross or net weights
                                            // are valid numerics and non-zero
                                            if (weightUnit.equals("oz")) {
                                                if (weightGrossF > 0 ) {
                                                    partRec.setWeightGross(weightGross);
                                                    if (verbose) {
                                                        out.println("Gross Weight will be updated.");
                                                    }
                                                }
                                                if (weightNetF > 0 ) {
                                                    partRec.setWeightNet(weightNet);
                                                    if (verbose) {
                                                        out.println("Net Weight will be updated.");
                                                    }
                                                }
                                                if (weightGrossF > 0 || weightNetF > 0 ) {
                                                    partRec.setWeightUnit(weightUnit);
                                                }
                                            }
                                        }
                                        if (importSeries || importDimensions || importWeight) {
                                            if (!partRec.updateFSHS(conn, partNumExt)) {
                                                updateFailed = true;
                                                out.println("Attempt to update part record with static field values failed for part number " + partNum + " at row " + Integer.toString(row));
                                                conn.rollback();
                                                abortImport(request, response, in, out, conn);
                                                return;
                                            } else {
                                                if (verbose) {
                                                    out.println("Static field values were successfully updated in part table.");
                                                }
                                            }
                                        } else {
                                            if (!partRec.updateFSH(conn, partNumExt)) {
                                                updateFailed = true;
                                                out.print("Attempt to update part record failed for part number " + partNum + " at row " + Integer.toString(row));
                                                conn.rollback();
                                                abortImport(request, response, in, out, conn);
                                                return;
                                            }
                                        }
                                        if (action.equals("R")) {
                                            replaceRow++;
                                            if (verbose) {
                                                out.println(partNum + " - parametric data has been successfully replaced.");
                                            }
                                        } else {
                                            addRow++;
                                            if (verbose) {
                                                out.println(partNum + " - parametric data has been successfully added.");
                                            }
                                        }
                                    } else {
                                        if (verbose) {
                                            out.println("Ignoring Parametric data for " + partNum + " which already exists.");
                                        }
                                    }
                                    conn.commit();
                                    if (sleepTime != 0) {
                                        Thread.sleep(sleepTime);
                                    }
                                    if (verbose) {
                                        out.println("Transaction successfully committed.");
                                    }
                                } else {
                                    out.println("Validation Error at row " + Integer.toString(row) + ", Part Number " + partNum + " not found in WDS database");
                                    badCols++;
                                } // end if (partRec.read(conn, partNumExt))
                            } // end if ("RI".indexOf(action) != -1)
                        } // end if (!editOnly && badCols == 0)
                    } // end if (!rowIsBad)
                    if (badCols > 0) {
                        errors += badCols;
                        badRows++;
                        if (verbose) {
                            out.println("Validation for row "  + Integer.toString(row) + " failed with "  + 
                                Integer.toString(badCols) + " errors."); 
                        }
                    }
                } //  end  if ("RIE".indexOf(action) != -1) {                
                // Did we receive an abort sigtnal?
                if (--abortTick < 1) {
                    if (deleteAbortFile(abortFileName)) {
                        out.println("Execution suspended by Abort Command.");
                        abortImport(request, response, in, out, conn);
                        return;
                    }
                    abortTick = abortInterval;
                }
                // get another record here
                inputLine = in.readLine();
            } // end while
        
            if (!editOnly) {
                // Disable transaction management
                if (!conn.disableTransactions()) {
                    out.println("Fatal Error - Attempt to disable Transactions in WDS database failed.");
                    abortImport(request, response, in, out, conn);
                    return;
                } else {
                    if (verbose) {
                        out.println("WDS Transaction Management is now disabled.");
                    }
                }
            }
            out.println("\n" + Integer.toString(row) + " Rows were processed."); 
            out.println("\n" + Integer.toString(warningCount) + " Warning" +
                (warningCount != 1 ? "s were" : " was") + " found.");        
            out.println("\n" + Integer.toString(errors) + " error" +
                (errors != 1 ? "s were" : " was") + " found.");
            out.println("\n" + Integer.toString(badInactiveCols) + " Inactive Parm field error" +
                (badInactiveCols != 1 ? "s were" : " was") + " found.");
            out.println("\n" + Integer.toString(badRows) + " bad row" +
                (badRows != 1 ? "s were" : " was") + " found.");
            out.println("\n" + Integer.toString(addRow) + " entr" +
                (addRow != 1 ? "ies were" : "y was") + " added with new parametric data.");
            out.println("\n" + Integer.toString(replaceRow) + " entr" +
                (replaceRow != 1 ? "ies were" : "y was") + " replaced with new parametric data.");
            out.println("\nValidation complete.");                       
                
            in.close();
            out.close();
            conn.close();
            request.setAttribute("familyDescription", familyDescription);
            request.setAttribute("subfamilyDescription", subfamilyDescription);
            request.setAttribute("logFileName", importFQLogFileName);
            request.setAttribute("message", "Processing complete. Log file was created successfully.");
            RequestDispatcher view = request.getRequestDispatcher("gpsdif2.jsp");
            view.forward(request,response);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Error importing parametric data. Good luck.");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            in.close();
            out.close();
            conn.close();
            return;
        }
    }
 
    private void abortImport(HttpServletRequest request, HttpServletResponse response,
                BufferedReader in, PrintWriter out, WDSconnect aConn)
        throws ServletException, IOException {
        try {            
            out.println("Import Operation aborting with fatal error.");
            request.setAttribute("message", "Import fatal error - refer to log file for details");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            out.close();
            in.close();
            aConn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return;
        }    
    }
    
    private void columnError(int expected, int found, int row,
                HttpServletRequest request, HttpServletResponse response,
                BufferedReader in, PrintWriter out, WDSconnect aConn)
        throws ServletException, IOException {
        try {   
            out.println("Error at row " + row + " - Expected " + expected + " columns; found " + found);
            abortImport(request, response, in, out, aConn);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }    
    }
         
    private void debug (int limit, int level, String x) {
        if (limit >= level) {
            System.out.println(x);
        }
    }
    
    private boolean deleteAbortFile(String fName) {
        try {
            File f = new File(fName);
            if (f.exists() && f.canWrite() && f.isFile()) {
                return f.delete();
            } else {
                return false;
            }
        } catch (Exception e) {
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
