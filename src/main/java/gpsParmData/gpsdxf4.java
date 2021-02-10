/*
 * gpsdxf4.java
 *
 * Created on January 10, 2007, 5:25 PM
 */

package gpsParmData;

import OEdatabase.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import sauter.util.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 * Modification History
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 *
 * I extract data from the part table and parametric data if present.
 * I include any digest data I find as well.
 * The extract file is a worksheet in CSV format.
 *
 * Modification History
 *
 * Date     Who What
 * 06/01/2007 DES Added code to support import/export of dimensions and weight
 *              field info contained inside the part table
 * 06/21/2007 DES Changed Dimensions units to be Height, Width, Depth, in that order.
 * 08/11/2008 DES fixed code that removes mfgr code from part number
 *
 */
public class gpsdxf4 extends HttpServlet {
            
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
        RequestDispatcher view = null;
        
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
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
       
        // Check for invalid Call  i.e., validation key must be set to "OK" 
        String work = request.getParameter("validation");
        if (!work.equals("OK")) {
            response.sendRedirect ("gpsnull.htm");
            conn.close();
            return;
        }
       
        String allow;
        String auditDate = DateTime.getDateYYMMDD(); 
        String auditTimeRaw = Integer.toString(DateTime.getSecondsSinceMidnight());
        String auditUserID = "";
        String catalog = "";
        String catCode = "";
        String cooked = "";
        GPScvt cvt = new GPScvt();
        String dataMax;
        String dataMin;
        String dataMult;
        String dataRequired;
        String dataSelectBoxName = "";
        String dataType;
        String dataTypeCode = "";
        String dataUnits;
        String defaults = "";
        String errorMsg = "";
        boolean excludePreExisting = false;
        int f;
        String famCode = "";
        String fExt = "";
        GPSselectBox fieldSelBoxes[];
        GPSfieldSet fieldSet = null;
        String fileName = "";
        String fsep = File.separator;
        String gpsExportPath = getServletContext().getInitParameter("exportPath");
        boolean hasPSData = false;
        String hhmmss = DateTime.getTimeHHMMSS("");
        int i;
        int i1;
        int iwork;
        int j = 0;
        String junk = "";
        int k;
        String listCode = "";
        boolean matchesSelectBox = false;
        String outFileName = "";
        String parmData = "";
        String parmName = "";
        String parms[];
        String parmSeqNum;
        GPSparmSet parmSet = null;
        String parmStatus = "";
        //String parmTemplate = "";
        String partNum = "";
        String partNumEdited = "";
        String partNumRaw = "";
        List <String> partNums = null;
        String partSeries = "";
        String partSubcat = "";
        //int pictureFieldSeqNum = 0;
        String preamble = "";
        boolean preExistingOnly = false;
        String printHeader = "";
        String printHeaderA = "";
        String printHeaderB = "";
        String printHeaderC = "";
        String printHeaderD = "";
        String printLine = "";
        String printLineA = "";
        String printLineB = "";
        String printLineC = "";
        String printLineD = "";
        PrintWriter out = null;
        String queryString = "";
        String raw = "";
        ResultSet rs;
        ResultSet rsDigest = null;
        GPSrules rules[] = null;
        GPSselectBox selectBox = null;
        String selectBoxName = "";
        String sep = ",";
        String sep1 = "\"";
        String sep2 = "\",\"";
        String seriesCode = "";
        Statement statement = null;
        String subcatCode = "";
        String subfamCode = "";
        String temp;
        String tempDataType;
        GPSunit units = null;
        String work1;
        String work2;


    String partNumFull = "";

    

    

    boolean rc;

    boolean testPage = false;
 
    GPSdigestData digest = null;

        try {
            auditUserID = request.getParameter("auditUserID");
            catCode = request.getParameter("categoryCode");
            if (catCode.equals("All Categories")) {
                catCode = "*";
            } 
            famCode = request.getParameter("familyCode");
            listCode = request.getParameter("listCode");
            preExistingOnly = request.getParameter("preExistingOnly").equals("Y") ? true : false;
            seriesCode = request.getParameter("manufacturerSeries");
            subcatCode = request.getParameter("subcategoryCode");
            subfamCode = request.getParameter("subfamilyCode");
            junk = request.getParameter("excludePreExisting");
            if (junk == null) {
                junk = "N";
            }
            excludePreExisting = junk.equals("Y") ? true : false;
            if (preExistingOnly && excludePreExisting) {
                response.sendRedirect ("gpsabend.jsp?rc=gps5011");
                conn.close();
                //conn3.close();
                return;
            }
                        
            session.setAttribute("auditUserID", auditUserID); // Update session User ID
                       
            // Create a fieldSet object and get field set rules in display order
            
            fieldSet = new GPSfieldSet();
            rules = fieldSet.getRules(conn, famCode, subfamCode, GPSfieldSet.SEQUENCE_NUMBER_ORDER);
            debug (debugLevel, 4, uStamp + " Parm Field rulesets have been created.");
        
            //**************************************************************
            // Let's get any select boxes associated with fields
            //**************************************************************
    
            try {        
                debug (debugLevel, 4, uStamp + " Looking up Select Box values for numeric and string parm fields...");
                fieldSelBoxes = new GPSselectBox[99];
                for (i = 0; i < fieldSet.count(); i++) {
                    work = rules[i].getDataType();
                    if ("NS".indexOf(work) != -1) {     // Only operate on numeric and string data types
                        if (rules[i].getDeTextBoxSize() == 0) { // if a select box is associated with this field
                            work = rules[i].getDeSelectBoxName();
                            if (work.length() != 0 ) {          // and the select box name is present...
                                fieldSelBoxes[i] = new GPSselectBox(); //create a select box object
                                j = fieldSelBoxes[i].open(conn, rules[i].getFamilyCode(), rules[i].getSubfamilyCode(), work);
                                    // and load the options into it
                                if (j > -1) {  // make sure there were no errors on loading the options
                                    debug(debugLevel, 4, uStamp + " Select Box '" + work + "' values were loaded for column " 
                                            + Integer.toString(i + 1));
                                    if (debugLevel > 5) { // display the options if we're in debug mode
                                        for (i1 = 0; i1 < fieldSelBoxes[i].size(); i1++) {
                                            work2 = fieldSelBoxes[i].getOptionText(i1);
                                            debug(debugLevel, 6, uStamp + "option " + Integer.toString(i1) + " - " + work2);
                                        }
                                    }
                                } else {  // If an error occurred while trying to load the options...
                                    fieldSelBoxes[i] = null;
                                    errorMsg = uStamp + " Error # " + Integer.toString(j) 
                                        + " encountered while loading values for Family Code " 
                                        + rules[i].getFamilyCode() + " Subfamily Code " 
                                        + rules[i].getSubfamilyCode() + " Select Box named '" 
                                        + work + "' in " + SERVLET_NAME + ".";
                                    debug(debugLevel, 6, errorMsg);
                                    request.setAttribute("message", errorMsg);
                                    view = request.getRequestDispatcher("showMessage.jsp");
                                    view.forward(request,response);
                                    conn.close();
                                    return;
                                }
                            } else {        // Intercept missing select box name here and abort.
                                errorMsg = uStamp + " Missing Select Box Name while loading values for field " + i;
                                debug(debugLevel, 0, errorMsg);
                                request.setAttribute("message", errorMsg);
                                view = request.getRequestDispatcher("showMessage.jsp");
                                view.forward(request,response);
                                conn.close();
                                return;
                            } // end if (work.length() != 0 ) {
                        } // end if (rules[i].getDETextBoxSize() == 0) {
                    } // end if ("NS".indexOf(work) != -1) {
                } // end for (i = 0; i < fieldSet.count(); i++) {
                debug(debugLevel, 4, uStamp + " Select Boxes have been loaded.");
            } catch (Exception e){
                e.printStackTrace();
                errorMsg = "An unexpected error occurred in " + SERVLET_NAME + " while getting Select Box contents for the parm fields.<br />" + e ;
                request.setAttribute("message", errorMsg);
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
        
            // Build initial column headings for the csv output file
            // an "H" in column 1 of a row flags it as a Header row
            // an "E" in column 1 of a row means edit the row -- do not load the data
            // an "A" in column 1 of a row means ADD the parm data if valid and IF IT DOES NOT already exist.
            // an "R" in column 1 of a row means add the parm data if valid, replacing old data EVEN IF IT already exists.
        
            preamble = sep2 + sep2 + sep2 + sep2 + sep2 + sep2 + sep2 + sep2 + sep2 + sep2; 
            allow = sep1 + "H" + sep2 + "Allow" + preamble;
            parmSeqNum = sep1 + "H" + sep2 + "Seq Num" + preamble;
            dataMax = sep1 + "H" + sep2 + "Max" + preamble;
            dataMin = sep1 + "H" + sep2 + "Min" + preamble;
            dataMult = sep1 + "H" + sep2 + "Mult" + preamble;
            dataRequired = sep1 + "H" + sep2 + "Reqd" + preamble;
            dataSelectBoxName = sep1 + "H" + sep2 + "Select Box" + preamble;
            dataType = sep1 + "H" + sep2 + "Data Type" + preamble;
            dataUnits = sep1 + "H" + sep2 + "Units" + sep2 + sep2 + sep2 + sep2 + sep2 + sep2 + sep2
                + sep2 + "Inches" + sep2 + "Inches" + sep2 + "Inches";

            for (f = 0; f < fieldSet.count(); f++) {
                debug (debugLevel, 4, uStamp + " Processing field " + f);
                parmSeqNum += sep2 + rules[f].getRuleScope() + rules[f].getSeqNum();
                parmName += sep2 + rules[f].getParmName();
                tempDataType = rules[f].getDataType();
                debug (debugLevel, 4, uStamp + "   Data type is " + tempDataType);
                dataType += sep2 + toDataType(tempDataType);
                selectBoxName = "";
                if ("SN".contains(rules[f].getDataType())) {
                    if (rules[f].getDeObject().equals("S")) {
                        if (rules[f].getDeTextBoxSize() == 0) {
                            selectBoxName = rules[f].getDeSelectBoxName();
                        }
                    }
                }
                debug (debugLevel, 4, uStamp + "   Select Box Name is " + selectBoxName);
                dataSelectBoxName += sep2 + selectBoxName; 
                dataRequired += sep2 + (rules[f].getDeRequired() ? "Req'd" : "Opt");
                work = rules[f].getDeMultipliers();
                if (work == null) {
                    work = "";
                }
                dataMult += sep2 + work;
                work = "";
                if (tempDataType.equals("N")) {
                    work = rules[f].getAllowDuplicates() ? "D" : "";
                    work += rules[f].getAllowFractions() ? "F" : "";
                    work += rules[f].getAllowSign() ? "S" : "";
                    work += rules[f].getAllowZero() ? "Z" : "";
                    work += rules[f].getAllowTilde() ? "~" : "";
                    work += rules[f].getParmDelimiter();
                }
                if (tempDataType.equals("S")) {
                    work = rules[f].getAllowDuplicates() ? "D" : "";
                    work += rules[f].getAllowTilde() ? "~" : "";
                    work += rules[f].getParmDelimiter();
                }
                allow += sep2 + work;
                if (tempDataType.equals("N")) {
                    dataUnits += sep2 + rules[f].getDisplayUnits().trim();
                    units = new GPSunit();
                    units.open(conn, rules[f].getDisplayUnits().trim());
                    rules[f].setDecShift(units.getMultiplierExp());
                    temp = rules[f].getDefaultValueRaw();
                    temp = cvt.toCooked(temp, rules[f].getDeMultipliers(), rules[f].getParmDelimiter(), 
                        rules[f].getDecShift(), rules[f].getAllowDuplicates(), 
                        rules[f].getAllowTilde(), true);
                    defaults += sep2 + temp;
                    temp = rules[f].getMinValueRaw();
                    temp = cvt.toCooked(temp, rules[f].getDeMultipliers(), "", rules[f].getDecShift(), false, false, true);
                    dataMin += sep2 + temp;
                    temp = rules[f].getMaxValueRaw();
                    temp = cvt.toCooked(temp, rules[f].getDeMultipliers(), "", rules[f].getDecShift(), false, false, true);
                    dataMax += sep2 + temp;
                    units = null;
                }
                if (tempDataType.equals("S")) {
                    temp = rules[f].getDefaultValueRaw();
                    defaults += sep2 + temp;
                    dataUnits += sep2 + "";
                    dataMin += sep2 + rules[f].getMinLength();
                    dataMax += sep2 + rules[f].getMaxLength();
                }
                if (tempDataType.equals("L")) {
                    temp = rules[f].getDefaultValueRaw();
                    if (temp.equals("U") ) {
                        temp = "";
                    }
                    defaults += sep2 + temp;
                    dataUnits += sep2 + "";
                    dataMin += sep2 + "";
                    dataMax += sep2 + "";
                }
                if (tempDataType.equals("D")) {
                    temp = rules[f].getDefaultValueRaw();
                    defaults += sep2 + temp;
                    dataUnits += sep2 + "";
                    dataMin += sep2 + "";
                    dataMax += sep2 + "";
                }
            }
            if (f == 0) {
                request.setAttribute("message", "Error! No parametric fields were defined in Rules Table.");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
        
            // Here we create a dummy column that serves as the LAST column in every row.
   
            allow += sep2 + "X" + sep1;
            dataMax += sep2 + "X" + sep1;
            dataMin += sep2 + "X" + sep1;
            dataMult += sep2 + "X" + sep1;
            dataRequired += sep2 + "X" + sep1;
            dataSelectBoxName += sep2 + "X" + sep1;
            dataType += sep2 + "X" + sep1;
            dataUnits += sep2 + "X" + sep1;
            parmName += sep2 + "EndRow" + sep1;
            parmSeqNum += sep2 + "X" + sep1;
   
            ////////////////////////////////////////////////////////////////////////////////
            // Get part numbers for this extract                                          //
            ////////////////////////////////////////////////////////////////////////////////
         
            partNums = new ArrayList <String> ();
        
            queryString = "SELECT part_num, sales_cat, sales_subcat, series,";
            queryString += " description, description2, description3,";
            queryString += " gross_weight, net_weight, weight_unit,";
            queryString += " p_height, p_width, p_depth,";
            queryString += " family_code, subfamily_code, has_ps_data";
            queryString += " FROM pub.part";
            queryString += " WHERE sales_cat = '" + catCode + "'";
            if (!subcatCode.equals("*")) {
                queryString += " AND sales_subcat = '" + subcatCode + "'";
            }
            //if (!seriesCode.equals("*")) {
            //    queryString += " AND series = '" + seriesCode + "'";
            //}
            if (excludePreExisting) {
                queryString += " AND has_ps_data = '0'";
            }
            if (preExistingOnly) {
                queryString += " AND has_ps_data = '1'";
            }
            queryString += " AND (family_code = '" + famCode + "' OR family_code = '')";
            queryString += " AND (subfamily_code = '" + subfamCode + "' OR subfamily_code = '')";
            queryString += " ORDER BY sales_subcat, series, part_num";
            rs = conn.runQuery(queryString);
            if (rs == null) {
                String m = conn.getError();
                request.setAttribute("message", "SQL statement error in " + SERVLET_NAME + " <br />" + m);
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
        
            // Let's set up headers and object for digest data
        
            digest = new GPSdigestData();
            rc = digest.initDigestHeaders(conn, catCode, subcatCode);
            debug (debugLevel, 4, uStamp + " Digest Object return code is " + rc);
            debug (debugLevel, 4, uStamp + " Found template '" + digest.getTemplate() + "'");
            debug (debugLevel, 4, uStamp + " for category code " + catCode);
            debug (debugLevel, 4, uStamp + " for subcategory code " + subcatCode);
            printHeaderD = "";
            for (j = 0; j < digest.size(); j++) {
                printHeaderD += ",\"" + EditText.encodeEntity(digest.getDigestHeader(j)) + "\"";
            }
            debug (debugLevel, 4, uStamp + " Digest headers = " + printHeaderD);
           
            // Build path & file name and Open the output file
        
            // Build path & file name and Open the output file
        
            fileName = famCode.toLowerCase() + "." + subfamCode.toLowerCase() + "." 
                + auditUserID.toLowerCase() + "." + removeSlashes(auditDate) + "." + hhmmss;
            fExt = ".xtr";
            outFileName = gpsExportPath + fileName + fExt;
            out = new PrintWriter( new BufferedWriter ( new FileWriter(outFileName)));

            // Write Header Rows
        
            printHeaderA = "\"Action\",\"Family\",\"Subfamily\",\"Mfgr\",\"Series\",\"Part Number\",\"Gross Wt\",\"Net Wt\",\"Wt Unit\",\"Height\",\"Width\",\"Depth";
            printHeaderB = parmName; // Parm headers
            printHeaderC = ",\"Description\",\"Description2\",\"Description3\",\"Cat\"";
            printHeader = printHeaderA + printHeaderB + printHeaderC + printHeaderD;
            out.println(printHeader);
            out.println(dataUnits);
            out.println(dataMin);
            out.println(dataMax);
            out.println(dataType);
            out.println(dataSelectBoxName);
            out.println(dataRequired);
            out.println(dataMult);
            out.println(parmSeqNum);
            out.println(allow);
        
            // free up some bytes here... I know... I know... memory is cheap but
            // we're a big honking web server for crying out loud.

            allow = "";
            dataMax = "";
            dataMin = "";
            dataMult = "";
            dataRequired = "";
            dataSelectBoxName = "";
            dataType = "";
            dataUnits = "";
            parmName = "";
            parmSeqNum = "";
            
            // Let's begin to build each data row now...
        
            j = 0;     
            while(rs.next()) {
                // filter the junk here:
                // Assemble a row
                // Build Part A; default action is E - edit only
                printLineA = sep1 + "E" + sep2 + famCode + sep2 + subfamCode;
                partSubcat = grab(rs, "sales_subcat"); 
                printLineA += sep2 + partSubcat; 
                // subcatCode is really a secret name for the manufacturer code
                partSeries = grab(rs, "series");
                printLineA += sep2 + partSeries;
                work = grab(rs, "part_num");
                partNumRaw = work;
                partNumEdited = editPartNum(work, partSubcat);
                printLineA += sep2 + partNumEdited;
            
                // Now we add the weight and dimensions here
            
                printLineA += sep2 + grabz(rs, "gross_weight");
                printLineA += sep2 + grabz(rs, "net_weight");
                printLineA += sep2 + grab(rs, "weight_unit");
                printLineA += sep2 + grabz(rs, "p_height"); // Height
                printLineA += sep2 + grabz(rs, "p_width"); // Width
                printLineA += sep2 + grabz(rs, "p_depth"); // Depth or Length
                       
                // Build Part B - Parm Data fields
                printLineB = defaults;
                hasPSData = rs.getBoolean("has_ps_data");
                parmData = "";
                if (hasPSData) {
                    parms = GPSparmSet.getArrayOfRawParms(conn, work);
                    k = 0;
                    while (rules[k] != null) {
                        iwork = rules[k].getSeqNum();
                        dataTypeCode = rules[k].getDataType();
                        parmStatus = rules[k].getParmStatus();
                        parmName = rules[k].getParmName();
                        raw = parms[iwork];
                        cooked = ""; 
                        if (raw.length() != 0 ) {       
                            if (dataTypeCode.equals("N")) {     // Process Numerics
                                if (fieldSelBoxes[k] != null) {  // if field values come from a select box
                                    matchesSelectBox = false;
                                    cooked = "raw{" + raw + "}";  //default cooked if raw value cannot be
                                                                  //cooked for an inactive rule
                                    for (i1 = 0; i1 < fieldSelBoxes[k].size(); i1++) {
                                        // Note that for numeric items,
                                        // we match raw values to raw values!
                                        // but the data in the worksheet is shown in cooked format
                                        work1 = fieldSelBoxes[k].getOptionValue1(i1);
                                        if (raw.equals(work1)) {
                                            matchesSelectBox = true;
                                            cooked = fieldSelBoxes[k].getOptionText(i1);
                                            break;
                                        }
                                    }
                                    if (parmStatus.equals("A") && !matchesSelectBox) {
                                        errorMsg = uStamp + " No match in Select Box for field " + Integer.toString(k) 
                                            + ", Part Number " + partNumRaw + ", column " + parmName +
                                            ", value = " + raw;
                                        debug(debugLevel, 0, errorMsg);
                                        request.setAttribute("message", errorMsg);
                                        view = request.getRequestDispatcher("showMessage.jsp");
                                        view.forward(request,response);
                                        statement = rs.getStatement();
                                        rs.close();
                                        statement.close();
                                        out.close();
                                        conn.close();
                                        return;
                                    }
                                } else {
                                    // If we get here, there was no Select Box to process
                                    // so we just cook the raw value for display
                                    cooked = cvt.toCooked(raw, rules[k].getDeMultipliers(), 
                                        rules[k].getParmDelimiter(), rules[k].getDecShift(), 
                                        rules[k].getAllowDuplicates(), rules[k].getAllowTilde(), true);
                                }
                                            
                            // Is it a string field?
                        
                            } else if (dataTypeCode.equals("S")) {   // Process String fields here
                                if (fieldSelBoxes[k] != null) {
                                    matchesSelectBox = false;
                                    cooked = "raw{" + raw + "}";  //default cooked if raw value cannot be
                                                                  //cooked for an inactive rule
                                    for (i1 = 0; i1 < fieldSelBoxes[k].size(); i1++) {
                                        // Note that for String items,
                                        // we match raw values to raw values!
                                        // but display them as cooked
                                        work1 = fieldSelBoxes[k].getOptionValue1(i1);
                                        if (raw.equals(work1)) {
                                            matchesSelectBox = true;
                                            cooked = fieldSelBoxes[k].getOptionText(i1);
                                            break;
                                        }
                                    }
                                    if (parmStatus.equals("A") && !matchesSelectBox) {
                                        errorMsg = uStamp + " No match in Select Box for field " + Integer.toString(k) 
                                            + ", Part Number " + partNumRaw + ", column " + parmName +
                                            ", value = " + raw;
                                        debug(debugLevel, 0, errorMsg);
                                        request.setAttribute("message", errorMsg);
                                        view = request.getRequestDispatcher("showMessage.jsp");
                                        view.forward(request,response);
                                        statement = rs.getStatement();
                                        rs.close();
                                        statement.close();
                                        out.close();
                                        conn.close();
                                        return;
                                    }
                                } else {
                                    // we get here if there is no select box for this field
                                    cooked = raw;
                                }
                        
                                // Process Logicals    
                                             
                            } else if (dataTypeCode.equals("L")) {
                                cooked = raw;
                                if (cooked.equals("U")) { // if logical field default is "undefined" then leave it blank
                                    cooked = "";
                                }
                            
                            // Process Dates
                        
                            } else if (dataTypeCode.equals("D")) {   // LATER, dude
                                cooked = "";
                            }
                        }  
                    
                        // Append cooked value
                    
                        parmData += sep2 + cooked; 
                        k++;
                    }
                    if (k == f) {
                        printLineB = parmData;
                    } else {
                        errorMsg = uStamp + " Fatal Error - Field count mismatch in exporting parm data. Expected " + f
                            + "; found " + k + " while processing part number " + partNumRaw;
                        debug (debugLevel, 0, errorMsg);
                        request.setAttribute("message", errorMsg);
                        view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        statement = rs.getStatement();
                        rs.close();
                        statement.close();
                        out.close();
                        conn.close();
                        return;
                    }
                }
                       
                // Build Part C
            
                printLineC = sep2 + "X";
                printLineC += sep2 + grab(rs, "description");
                printLineC += sep2 + grab(rs, "description2");
                printLineC += sep2 + grab(rs, "description3");
                printLineC += sep2 + grab(rs, "sales_cat") + sep1;
            
                // Compute Part D

                printLineD = "";
                rc = digest.readDigestValues(conn, partNumFull);
                debug (debugLevel, 4, uStamp + " Digest Look Up RC for Part Number " + partNumFull + " was " + rc);
                if (rc) {
                    for (int jj = 0; jj < digest.size(); jj++ ) {
                        printLineD += ",\"" + digest.getDigestValue(jj) + "\"";
                    }
                }
            
                printLine = printLineA + printLineB + printLineC + printLineD;
                out.println(printLine);
                j++;
            }
            partNums.add(Integer.toString(j) + " part numbers found.");
            partNums.add("Extract complete.");
            out.close();
            statement = rs.getStatement();
            rs.close();
            statement.close();
                        
            //   Put the lists inside the Request Object and forward to the JSP to display
        
            request.setAttribute("selectedList",listCode); 
            request.setAttribute("selectedFamily",famCode);
            request.setAttribute("selectedSubfamily",subfamCode); 
            request.setAttribute("selectedCategory",catCode); 
            request.setAttribute("selectedSubcategory",subcatCode);
            request.setAttribute("selectedSeries",seriesCode);
            request.setAttribute("partNums",partNums);
            request.setAttribute("outFileName",outFileName);

            //   Forward the goodies to the JSP     
     
            view = request.getRequestDispatcher("gpsdxf2.jsp");
            view.forward(request,response);  
        } catch (IOException e){
            request.setAttribute("message", "An I/O error occurred in " + SERVLET_NAME + ": <br />" + e);
            e.printStackTrace();
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } catch (Exception e){
            request.setAttribute("message", "An error occurred in " + SERVLET_NAME + ": <br />" + e);
            e.printStackTrace();
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } finally {
            conn.close();
        }
    }
            
    private void debug (int limit, int level, String x) {
        if (limit >= level) {
            System.out.println(x);
        }
    }

    private String grab(ResultSet r, String columnName)
        throws SQLException {
        try {
            return EditText.toDoubleQuote(r.getString(columnName).trim());
        }
        catch (SQLException e) {
            e.printStackTrace();
            return "\"error\"";
        }
    }
    
    private String grabz(ResultSet r, String columnName)
        throws SQLException {
        try {
            String work = r.getString(columnName).trim();
            if (Float.parseFloat(work) != 0.0) {
                return work;
            } else {
                return "";
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return "\"error\"";
        }
    }
    
    private String editPartNum(String pn, String mfgr) {
        pn = pn.trim();
        mfgr = "-" + mfgr.trim();
        if (pn.endsWith(mfgr)) {
            pn = pn.substring(0, pn.length() - mfgr.length() );
        }
        return pn;
    }

    private String toDataType(String x) {
        if (x.equals("N")) {
            return "Numeric";
        }
        if (x.equals("S")) {
            return "String";
        }
        if (x.endsWith("L")) {
            return "Logical";
        }
        if (x.equals("D")) {
            return "Date";
        }
        return "";
    }

    private String removeSlashes(String x) {
        int i = x.indexOf("/");
        while (i != -1) {
            x = x.substring(0, i) + x.substring(i+1);
            i = x.indexOf("/");
        }
        return x;
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
