/*
 * gpsdpf2.java
 *
 * Created on June 7, 2007, 3:32 PM
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
 * @version 1.5.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 *
 * I delete parametric data from the database using part numbers obtained
 * from a worksheet. The worksheet must be in the default import directory.
 * I check each row to ensure that the family code and subfamily codes match
 * before I actually delete the parametric data for a part number.
 * The worksheet is in CSV format.
 *
 * Modification History
 *
 * Date     Who What
 *
 * 9/13/2007    DES Changed from "delete Operation to "Purge" Operation
 *
 */
public class gpsdpf2 extends HttpServlet {
            
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.00";
        
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
    
        String action;
        String auditUserID;
        int badCols = 0;
        int badRows = 0;
        int col = 0;
        boolean colIsBad;
        List <String> dataRow;      // a rose is a rose is a rose  (a synonym for the headingRow)
        boolean editOnly;
        String enableToolTips;
        String errorMsg;
        int errors = 0;
        int expectedCols;
        String familyCode;
        String familyDescription;
        GPSrules fieldRules[];       // Class to create a collection of fields and their rules
        GPSfieldSet fieldSet;
        String fileExt = ".csv";
        String fsep = File.separator;
        int foundCols;
        String gpsImportPath = getServletContext().getInitParameter("importPath");
        List <String> headingRow;   // an ArrayList object used in processing spreadsheet columns
        int i;
        int i1;
        int i2;
        String importFileName;
        String importFQFileName = "";
        String importFQLogFileName = "";
        String importLogFileName;
        BufferedReader in = null;       // input file stream
        String inputLine;
        int j;  
        int lastParmCol;
        String logExt = ".del";
        String mfgrCode;
        PrintWriter out = null;     // output file stream for log file
        boolean parmDataExists;
        int parmField;
        String parmName;
        //List <String> parmsToAdd;   // an ArrayList to hold validated parm data for adding  
        GPSpart partRec = null;      // a part rec object to contain part rec variables I am using
        String partNum;
        String partNumExt;
        int replaceRow = 0;
        String result;
        int row = 0;
        String rowFamilyCode;
        boolean rowIsBad;
        String rowSubfamilyCode;
        ResultSet rs = null;        // general purpose rs to hold DB query results
        GPSrules ruleSet = null;     // a convenient rules object to point to a fieldRules[] item
        String subfamilyCode;
        String subfamilyDescription;
        String traxDate = DateTime.getDateMMDDYY();
        String traxTime = DateTime.getTimeHHMMSS(":");
        GPSunit units = null;
        boolean verbose;
        String work;
        String work1;
        String work2;
        String work3;

        try {       // Check for invalid Call to servlet
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
            if (work.equals("Delete")) {
                auditUserID = request.getParameter("auditUserID");
                editOnly = request.getParameter("editOnly").equals("N") ? false : true;
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                familyDescription = request.getParameter("familyDescription");
                importFileName = request.getParameter("fileName");
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
            importFQFileName = gpsImportPath + importFileName + fileExt;
            in = new BufferedReader( new InputStreamReader( new FileInputStream(importFQFileName)));
        } catch (Exception e) {
            conn.close();
            sWork = uStamp + "Error trying to open worksheet file " + importFQFileName + " " + e;
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
            out.println("Purge Log Run Date " + traxDate + " Time " + traxTime);
            out.println("Log file name: " + importFQLogFileName);
            out.println("Worksheet file name: " + importFQFileName);
            if (editOnly) {
                out.println("**** Option 'Edit Only' selected - no parametric data will be deleted. ****");
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
         
            out.println("Verifying Worksheet Select Box Nanmes Header..."); 
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
            partRec = new GPSpart();            // make me a part object :-)
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
                partNum = (String) dataRow.get(5);
                partNum = partNum.trim().toUpperCase();
            
                // If we got here OK, that means
                // Number of columns match, check for a process request
                // D means delete the parametric data for this part number
                // E means EDIT ONLY - Do not delete any data
                // anything else we ignore
                if (verbose) {
                    out.println("Action is " + action);
                }
                if ("DE".indexOf(action) != -1) {
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
                    if (partNum.length() == 0) {
                        out.println("Validation Error at row " + Integer.toString(row) + ", missing part number. ");
                        badCols++;
                    } else {
                        partNumExt = partNum;
                        if (!partNum.endsWith("-" + mfgrCode)) {
                            partNumExt = partNum + "-" + mfgrCode;
                        }
                        partNumExt = partNumExt.trim();
                        partNumExt = partNumExt.toUpperCase();
                        if (editOnly) {
                            if (!partRec.exists(conn, partNumExt)) {
                                out.println("Validation Error at row " + Integer.toString(row) + ", Part Number " + partNum + " not found in WDS database");
                                badCols++;
                            }
                        }
                    }
                    if (badCols == 0) {
                        colIsBad = false;
                                                          
                        // *********************************************************************
                        // If Delete was selected and row passed validation, do this:
                        // *********************************************************************
                    
                        if (!editOnly && badCols == 0) {
                            boolean updateFailed = false;
                            if ("D".indexOf(action) != -1) {   // action must be Delete
                                if (verbose) {
                                    out.println("Deleting parametric data using row " + Integer.toString(row) + ", Part Number " + partNum +
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
                                    if (action.equals("D") && parmDataExists) {
                                        // delete any parm data if present when action = Delete
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
                                            out.println("Parametric data deleted for " + partNum);
                                            parmDataExists = false;
                                        }
                                    }
                                
                                    // ******************************************
                                    // Now Update part rec
                                    // ******************************************
                                
                                    if (!parmDataExists) {
                                        if (verbose) {
                                            out.println("Success.\nAttempting to update part record...");
                                        }
                                        partRec.setHasPSData(false);
                                        partRec.setFamilyCode("");
                                        partRec.setSubfamilyCode("");
                                        if (!partRec.updateFSH(conn, partNumExt)) {
                                            updateFailed = true;
                                            out.print("Attempt to update part record failed for part number " + partNum + " at row " + Integer.toString(row));
                                            conn.rollback();
                                            abortImport(request, response, in, out, conn); 
                                            return;
                                        }
                                        if (action.equals("D")) {
                                            replaceRow++;
                                            if (verbose) {
                                                out.println(partNum + " - parametric data has been successfully deleted.");
                                            }
                                        }
                                    } else {
                                        if (verbose) {
                                            out.println("Did not delete Parametric data for " + partNum );
                                        }
                                    }
                                    conn.commit();
                                    if (verbose) {
                                        out.println("Transaction successfully committed.");
                                    }
                                } else {
                                    out.println("Delete Error at row " + Integer.toString(row) + ", Part Number " + partNum + " not found in WDS database");
                                    badCols++;
                                } // end if (partRec.read(conn, partNumExt))
                            } // end if ("D".indexOf(action) != -1)
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
     
            out.println("\n" + Integer.toString(errors) + " error" +
                (errors != 1 ? "s were" : " was") + " found.");
            out.println("\n" + Integer.toString(badRows) + " bad row" +
                (badRows != 1 ? "s were" : " was") + " found.");
            out.println("\n" + Integer.toString(replaceRow) + " entr" +
                (replaceRow != 1 ? "ies " : "y ") + " with parametric data purged.");
            out.println("\nPurge operation complete.");                       
                
            in.close();
            out.close();
            conn.close();
            request.setAttribute("familyDescription", familyDescription);
            request.setAttribute("subfamilyDescription", subfamilyDescription);
            request.setAttribute("logFileName", importFQLogFileName);
            request.setAttribute("message", "Processing complete. Log file was created successfully.");
            RequestDispatcher view = request.getRequestDispatcher("gpsdpf2.jsp");
            view.forward(request,response);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Error deleting parametric data. Good luck.");
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
            out.println("Purge Operation aborting with fatal error.");
            request.setAttribute("message", "Purge fatal error - refer to log file for details");
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
