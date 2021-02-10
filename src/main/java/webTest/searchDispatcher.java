/*
 * searchDispatcher.java
 *
 * Created on June 20, 2008, 3:09 PM
 */

package webTest;

import gps.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import OEdatabase.*;
import sauter.util.*;

/**
 *
 * @author Sauter
 * @version 1.3.01
 *
 * I receive a request from the web client and inspect the bread crumb trail
 * and any additional data contained within the POST parameters in the Request Object.
 * I determine the next operation to be performed and the related data to fetch.
 * Then I invoke the appropriate modules to produce the required Response object.
 *
 * NOTICE! If there are more than 100 parm filters 
 * I will crash with array indeces out of bounds errors
 */
public class searchDispatcher extends HttpServlet {
    
    int debugLevel = 0;
    long lastFreeMemory = 0;  // Not thread safe!
    final String SERVLET_NAME = "searchDispatcher.java";
    final String VERSION = "1.3.01";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        /////////////////////////////////////////////////
        // Define and initialize method variables here //
        /////////////////////////////////////////////////
        
        ArrayList<String> BCTarray = new ArrayList<String>();
        int count = 0;
        GPScvt cvt = new GPScvt();
        String dataType = "";
        String delimiter = "";
        double[] dFilterValuesHigh = new double[100]; // use me for numeric range match if filterValues[index] is "";
        double[] dFilterValuesLow = new double[100]; // use me for numeric range match if filterValues[index] is "";
        GPSfieldSet displayFieldSet = null;
        GPSrules[] displayRuleSets = null;
        String displayUnits = "";
        double dParmValueIndividualHigh = 0;
        double dParmValueIndividualLow = 0;
        double elapsed;
        String familyCode;
        String familyName;
        int filterCount = 0;
        String filterName = "";
        String filterSeqNumString = "";
        int filterSeqNum = 0;
        int[] filterSeqNums = new int[100];
        String filterValue = "";
        String filterValueCooked = "";
        String filterValueHigh = "";
        String filterValueLow = "";
        String[] filterValues = new String[100]; // holds a valid string match value when not "";
        String[] filterValuesHigh = new String[100];
        String[] filterValuesLow = new String[100];
        boolean inStockOnly = true;
        long iStart;
        long iStop;
        int iWork;
        //String lastSelected = "";
        GPSmanNameList manNameList = null;
        boolean match = false;
        ArrayList<String> mfgrCodes = null;
        ArrayList<String> mfgrNames = null;
        String mfgrCode;
        String mfgrCodeSelected;
        String mfgrName;
        String mfgrName2;
        boolean mfgrSwitch = false;
        String narrow = "";    // Narrow Your Search within... string
        GPSoptionsArray optionsArray;
        int optionsArrayIndex = 0;
        ArrayList<GPSoptionsArray> optionsArrays = new ArrayList<GPSoptionsArray>();
        int[] optionsArraysVectorMap = new int[100]; 
        ArrayList<String> optionsSet = null;
        String pageEnd = "";
        ArrayList<String> pageIndex = null;
        int pageNum = 0;
        int pageStart = 0;
        int pageStop = 0;
        int parmSeqNum = 0;
        GPSparmSet parmSet = null;
        String parmValue = "";
        String parmValueNext = "";
        String parmValueIndividual = "";
        String parmValueIndividualHigh = "";
        String parmValueIndividualLow = "";
        String[] parmValues = new String[100]; // parm values for a part; indexed by seqnum
        String[] parmValues2 = new String[1];
        int parmValues2Count = 0;
        boolean parmValues2Match = false;
        String partDescription = "";
        String partNumber = "";
        int[] partNumSeqNums = new int[100]; // this is an array of all seq nums found for a given partnum in a resultset
        int partNumSeqNumsCount = 0; // count of seq nums found for this part num
        int partNumbersPerPage = 10;
        String previousMfgrCode = "";
        String previousPartNumber = "";
        String previousSeriesCode = "";
        String productLineCode;
        String productLineName;
        int qtyAvailable = 0;
        String queryString = "";
        String redirect = "index.jsp";
        String results = "";
        int resultsBegin = -1;
        int resultsEnd = -1;
        int resultsFound = -1;
        ArrayList<String> resultsPage = null;
        ResultSet rs = null;
        String rsMfgrCode = "";
        String rsSeriesCode = "";
        StringBuffer sBuffer = null;
        GPSfieldSet searchFieldSet = null;
        GPSrules searchRuleSet = null;
        GPSrules[] searchRuleSets = null;
        int[] searchRuleSetVectorMap = new int[100];
        int searchRuleSetVectorMapCount = 0;
        String searchWithin;
        String searchWithin2;
        String[] searchWithinArray = null;
        boolean searchWithinSwitch = false;
        String selSeqNum = "";
        String selectBoxName = "";
        String seriesCode;
        String seriesCodeSelected;
        ArrayList<String> seriesCodes = null;
        String seriesName;
        boolean seriesSwitch = false;
        GPSpartsAvailable stock = null;
        GPSsubfamilyCodes subfam = null;
        String subfamilyCode;
        String subfamilyCodeSelected;
        boolean subfamiliesExist = true;
        ArrayList<String> subfamilyCodes = null;
        String subfamilyName;
        String[] temp = null;
        boolean toggle = false;;
        String work;
         
        ///////////////////////////////////////////////////////////////
        // Parse Request Object to obtain arguments, if any          //
        ///////////////////////////////////////////////////////////////

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Detroit"));
        iStart = cal.getTimeInMillis();
            
        debug (4, "Begin parsing Request Object...");
        work = request.getQueryString();
        if (work == null) {
            work = "";
        }
        debug (2, "Query String is: " + work);
        
        work = request.getParameter("instockonly");
        if (work == null) {
            work = "";
        }
        inStockOnly = work.equals("y") ? true : false;
            
        debug (2, "In stock only is " + inStockOnly);
        
        productLineCode = request.getParameter("productlinecode");
        if (productLineCode == null) {
            productLineCode = "";
        }
        if (productLineCode.equals("")) {   // productLineCode is mandatory
            System.out.println("Missing product line code.");
            response.sendRedirect(redirect);
            return;
        }
        
        debug (2, "productLineCode:  " + productLineCode);
        
        productLineName = request.getParameter("productlinename");
        if (productLineName == null) {
            productLineName = "";
        }
        debug (2, "productLineName:  " + productLineName);
        
        familyCode = request.getParameter("familycode");
        if (familyCode == null) {
            familyCode = "";
        }
        if (familyCode.equals("")) {    // familyCode is mandatory
            System.out.println("Missing family code.");
            response.sendRedirect(redirect);
            return;
        }
        debug (2, "familyCode:  " + familyCode);
        
        familyName = request.getParameter("familyname");
        if (familyName == null) {
            familyName = "";
        }
        debug (2, "familyName:  " + familyName);
        
        subfamilyCode = request.getParameter("subfamilycode");
        if (subfamilyCode == null) {
            subfamilyCode = "";
        }
        debug (2, "subfamilyCode:  " + subfamilyCode);
        
        subfamilyName = request.getParameter("subfamilyname");
        if (subfamilyName == null) {
            subfamilyName = "";
        } 
        debug (2, "subfamilyName:  " + subfamilyName);  
        
        subfamilyCodeSelected = request.getParameter("subfamilycodes");
        if (subfamilyCodeSelected == null) {
            subfamilyCodeSelected = "";
        }
        if (!subfamilyCodeSelected.equals("")) {
            //lastSelected = "sfc";
            subfamilyCode = subfamilyCodeSelected;
        }
        debug (2, "subfamilyCodeSelected:  " + subfamilyCodeSelected);
        
        mfgrCode = request.getParameter("mfgrcode");
        if (mfgrCode == null) {
            mfgrCode = "";
        }
        debug (2, "mfgrCode:  " + mfgrCode);
        
        mfgrName = request.getParameter("mfgrname");
        if (mfgrName == null) {
            mfgrName = "";
        }
        debug (2, "mfgrName:  " + mfgrName);
        
        mfgrCodeSelected = request.getParameter("mfgrcodes");
        if (mfgrCodeSelected == null) {
            mfgrCodeSelected = "";
        }
        if (!mfgrCodeSelected.equals("")) {
            //lastSelected = "mfc";
            mfgrCode = mfgrCodeSelected;
        }
        debug (2, "mfgrCodeSelected:  " + mfgrCodeSelected);
        
        seriesCode = request.getParameter("seriescode");
        if (seriesCode == null) {
            seriesCode = "";
        }
          
        seriesCode = request.getParameter("seriescode");
        if (seriesCode == null) {
            seriesCode = "";
        }
        debug (2, "seriesCode:  " + seriesCode);
        
        seriesName = request.getParameter("seriesname");
        if (seriesName == null) {
            seriesName = "";
        }
        debug (2, "seriesName:  " + seriesName);

        seriesCodeSelected = request.getParameter("seriescodes");
        if (seriesCodeSelected == null) {
            seriesCodeSelected = "";
        }
        if (!seriesCodeSelected.equals("")) {
            //lastSelected = "sc";
            seriesCode = seriesCodeSelected;
        }
        debug (2, "seriesCodeSelected:  " + seriesCodeSelected);
        
        // Series name defaults to series code for now
        if (seriesName.equals("")) {
            seriesName = seriesCode;
        }
         
        searchWithin = request.getParameter("searchwithin");
        if (searchWithin == null) {
            searchWithin = "";
        }
        if (searchWithin.equalsIgnoreCase("Search Within Results")) {
            searchWithin = "";
        }
        
        searchWithin2 = request.getParameter("searchwithinresults");
        if (searchWithin2 == null) {
            searchWithin2 = "";
        }

        selSeqNum = request.getParameter("selseqnum");
        if (selSeqNum == null) {
            selSeqNum = "";
        }
        debug (2, "selSeqNum:  " + selSeqNum);
        
        work = request.getParameter("pagenum");
        if (work == null || work.equals("") || !selSeqNum.equals("")) {
            work = "1";
        }
        if (!mfgrCodeSelected.equals("") || !seriesCodeSelected.equals("")) {
            work = "1";
        }
        if (searchWithin2.equals("") && !searchWithin.equals("")) {
            work = "1";
        }
        pageNum = Integer.parseInt(work);
        debug (2, "pageNum:  " + pageNum);
        
        work = request.getParameter("itemsperpage");
        if (work == null || work.equals("")) {
            work = "20";
        }
        partNumbersPerPage = Integer.parseInt(work);
        debug (2, "Items / Page:  " + partNumbersPerPage);
        
        if (searchWithin.equals("")) {
            searchWithin = searchWithin2;
        }
        debug (2, "Search Within = '" + searchWithin + "'");
        searchWithin = EditText.replaceFunnyCharacters(searchWithin, " ");
        debug (2, "Edited Search Within = '" + searchWithin + "'");
        if (searchWithin.length() > 0 ) {
            searchWithinArray = searchWithin.split(" ");
            searchWithinSwitch = true;
        }

        // Open up a connection to each of the 3 databases

        WDSconnect conn1 = new WDSconnect();                // Connect to WDS database 
        if (!conn1.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to WDS database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        SROconnect conn2 = new SROconnect();                // Connect to SRO database 
        if (!conn2.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to SRO database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn1.close();
            return;
        }
        
        WWWconnect conn3 = new WWWconnect();                // Connect to Web database 
        if (!conn3.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to Web database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn1.close();
            conn2.close();
            return;
        }
        
        // Look up Product Line Name if missing               

        if (productLineName.length() == 0) {
            productLineName = GPSproductLines.lookUpProductLineName(conn1, productLineCode);
        }   

        // Look up Family Name if missing               

        if (familyName.length() == 0) {
            familyName = GPSfamilyCodes.lookUpFamilyName(conn1, familyCode);
        } 
        
        // Set Narrow your search to the family name:
        
        narrow = familyName; 
        
        // Create a subfamily code object here
        // and load it
        // make sure there is at least 1 subfamily code
        
        subfam = new GPSsubfamilyCodes();
        if (subfam.open(conn1, familyCode) != 0) {
            System.out.println("Unexpected error reading subfamily codes.");
            response.sendRedirect(redirect);
            conn1.close();
            conn2.close();
            conn3.close();
            return;
        }
        
        // The subfamilesExist flag is on by default.
        // If there is only one subfamily code for this family
        // AND it is the same as the family code:
        // turn off the subfamilesExist flag
        
        if (subfam.size() == 1) {
            work = subfam.getSubfamilyCode(0);
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
        
        if (!mfgrCode.equals("") && mfgrName.equals("") ) {
            mfgrName = GPSmanCodes.getMfgrName(conn2, mfgrCode);
            debug (2, "Mfgr Name is set to " + mfgrName + " for Mfgr code " + mfgrCode);
        }

        //////////////////////////////////////////////////////////////
        // Get a rule set for each parm field                       //
        // if we have a family and a subfamily code                 //
        //////////////////////////////////////////////////////////////
        
        if (!familyCode.equals("") && !subfamilyCode.equals("")) {
           
            searchFieldSet = new GPSfieldSet();
            try {
                searchRuleSets = searchFieldSet.getRules(conn1, familyCode, subfamilyCode, searchFieldSet.SEARCH_ORDER);
                // searchRuleSets is an array of parm fields and their corresponding rulesets
            } catch (Exception e) {
                System.out.println("Unexpected error reading rules for family code " 
                    + familyCode + " subfamily code " + subfamilyCode + " in " + SERVLET_NAME);
                response.sendRedirect(redirect);
                conn1.close();
                conn2.close();
                conn3.close();
                return;
            }
            debug (2, "Found " + searchFieldSet.size() + " rulesets for family code " 
                    + familyCode + " subfamily code " + subfamilyCode + " in " + SERVLET_NAME);
            
            // Make a vector map where seq num as an index points to ruleset # in searchFieldSet[]
            
            for (int i = 0; i < 100; i++) {
                searchRuleSetVectorMap[i] = -1; // initially vector map is empty;  index i = seq num
            }
            searchRuleSetVectorMapCount = 0; // number of parm field rule sets currently in vector map
            
            for (int i = 0; i < searchFieldSet.size(); i++) {
                searchRuleSet = searchRuleSets[i];
                filterSeqNum = searchRuleSet.getSeqNum();
                searchRuleSetVectorMap[filterSeqNum] = i;
                dataType = searchRuleSet.getDataType();
                //parmDataTypes[filterSeqNum] = dataType;
                if (searchRuleSet.getParmDelimiter() == null) {
                    searchRuleSet.setParmDelimiter("");
                }
                //parmDelimiters[filterSeqNum] = delimiter;
                //parmTildes[filterSeqNum] = searchRuleSet.getAllowTilde();
                //parmSearchItems[filterSeqNum] = searchRuleSet.getSearchOrder() != 0;
                //selectBoxName = "";
                if ("NS".contains(dataType) && searchRuleSet.getQobject().equals("S") && searchRuleSet.getQtextBoxSize() == 0 ) {
                    // Get name of pre-built select boxes for numeric / string data types
                    work = searchRuleSet.getQselectBoxName();
                    if (work == null) {
                        searchRuleSet.setQselectBoxName("");
                    }
                          
                }
                //parmQSelectBoxNames[filterSeqNum] = selectBoxName;
                searchRuleSetVectorMapCount++;
            }
            
            // Note: searchRuleSetVectorMapCount should now equal searchFieldSet.size()
            
            debug (2, SERVLET_NAME + " found " + searchRuleSetVectorMapCount + " Search field ruleSets. ");
            
            //////////////////////////////////////////////////////////
            // Do not read this comment; it is no longer applicable //
            //////////////////////////////////////////////////////////
            
            if (searchRuleSetVectorMapCount == 0 || searchRuleSetVectorMapCount != searchFieldSet.size()) {
                System.out.println(SERVLET_NAME + " Error processing search fields; Aborting...");
                response.sendRedirect(redirect); 
                conn1.close();
                conn2.close();
                conn3.close();
                return;
            }
        }   
        
        /////////////////////////////////////////////////
        //     Initial Start Up                        //
        /////////////////////////////////////////////////
        
        //  The initial Request object must contain 
        //     productlinecode=xxxxxxxx
        //     familycode=xxxxxxxx
        //  
        // All other possible Request parms should be null or empty strings
        
        // Build the initial root of the bread crumb trail
        
        work = "\"searchInit.do\",\"New Search\"";
        BCTarray.add(work);
        work = "\"searchDispatcher.do?productlinecode=" + productLineCode + "&familycode=" + familyCode + "\",\""
            + productLineName + ": " + familyName + "\"";
        BCTarray.add(work);

        // IMPORTANT!!!
        // Note that if subfamilies do not exist, the subfamily code will be the 
        // same as the family code and the subfamiliesExist flag will be false
        
        // Do we need to build a subfamily code list?
        
        if (subfamilyCode.equals("")) {  // subfamilies exist and no subfamily selected
            if (mfgrCode.equals("")) {
                subfamilyCodes = subfam.getArrayList(familyCode);
            } else {
                if (seriesCode.equals("")) {
                    subfamilyCodes = subfam.getArrayList(conn1, familyCode, mfgrCode);
                } else {
                    subfamilyCodes = subfam.getArrayList(conn1, familyCode, mfgrCode, seriesCode);
                }
            }
        } else {  // Subfamily Code is already defined
            if (subfamiliesExist) {
                work = "\"&subfamilycode=" + subfamilyCode + "\",\""
                    + subfamilyName + "\"";
                BCTarray.add(work);
            }
        }
        
        // Do we need to build a mfgr code list?
        
        if (mfgrCode.equals("")) {
            if (subfamilyCode.equals("")) { // Is there no subfamily selected yet?
                mfgrCodes = GPSmanCodes.getAllMfgrCodes(conn1, familyCode);
                mfgrNames = GPSmanCodes.getMfgrNames(conn2, mfgrCodes);
                
            } else {
                // if a subfamily code exists,
                // we will build a custom mfgr code list later from the parts
                // that actually have parametric data
                // mfgrCodes = GPSmanCodes.getAllMfgrCodes(conn1, familyCode, subfamilyCode);
                // mfgrNames = GPSmanCodes.getMfgrNames(conn2, mfgrCodes);
                // note that mfgrNames ArrayList() contains both mfgr code and mfgr name entries
            }
        } else {
            work = "\"&mfgrcode=" + mfgrCode + "\",\""
                + mfgrName + "\"";
            BCTarray.add(work); 
        }
        
        // Do we need to build a mfgr series list?
        
        if (!mfgrCode.equals("")) { // If a manufacturer code was selected then
            if (seriesCode.equals("")) { // Is there no series code selected yet?
                if (subfamilyCode.equals("")) { // Is there no subfamily selected yet?
                    seriesCodes = GPSmanCodes.getMfgrSeries(conn1, familyCode, mfgrCode);
                } else {
                    // if a mfgr code was selected, but no series code
                    // and a subfamily code exists,
                    // we will build a custom series code list later 
                    // for this manufacturer from the parts
                    // that actually have parametric data
                    // seriesCodes = GPSmanCodes.getMfgrSeries(conn1, familyCode, subfamilyCode, mfgrCode);
                }        
            } else {
                work = "\"&seriescode=" + seriesCode + "\",\""
                    + "Series: " + seriesName + "\"";
                BCTarray.add(work);
            }
        }
                
        // Look up all the parts that match 
        
        if (subfamilyCode.equals("")) { 
            try {
                
                // Look up top N selling items in family if no subfamily code
            
            } catch (Exception e) {
            
            }
        } else {
            
        // here we have a fam & subfam code
        // so process any parm filters if they exist
            
            try {
                
                // first let's process the request object to
                // get any filter seqnums and their values from a previous visit to the server.
                // whether we got here via the breadcrumb trail or a form post request.
                // every parm filter consists of a field sequence number snXX=
                // and a value vXX=
                // where XX runs from 0 and up.
                // there are no gaps in the number sequence.
                // the first filter is sn0= / v0=
                // sn0= defines the filter seq num; v0= defines the filter value
                // the second filter is sn1= / v1=, and so on...
                // store the filter components in filterSeqNums[] and filterValues[]
              
                for (filterCount = 0; filterCount < 100; filterCount++) {
                    work = "sn" + filterCount;
                    filterSeqNumString = request.getParameter(work);
                    if (filterSeqNumString != null && !filterSeqNumString.equals("")) { // does a filter SN exist?
                        work = "v" + filterCount;
                        filterValue = request.getParameter(work);
                        if (filterValue != null && !filterValue.equals("")) {  // if seq num is not null, filter value should be not null
                            filterSeqNums[filterCount] = Integer.parseInt(filterSeqNumString);
                            filterValues[filterCount] = filterValue;
                        } else {
                            System.out.println(SERVLET_NAME + " found unmatched filter value for seq num " 
                                    + filterSeqNumString + " in sn" + filterCount + "=; Aborting...");
                            response.sendRedirect(redirect); 
                            conn1.close();
                            conn2.close();
                            conn3.close();
                            return;
                        }
                    } else { 
                        // found a null snXX; therefor we reached the end of the list
                        break;
                    }
                }
                
                // Now filterSeqNums[] contains filter seq nums
                // filterValues[] has the values
                // filterCount has the count
                
                // Oh, BTW... Did we just get a new filter in a recent post request?
                // (Like.... User clicked a filter option in a select box)
                // If so, reset pageNum to 1
                
                if (!selSeqNum.equals("")) {
                    // yes, then look for the corresponding filter value.
                    // there will only be one.
                    pageNum = 1;
                    filterValue = "";
                    for (int i = 0; i < 100; i++) {
                        work = "parmvalue" + i;
                        debug (4, SERVLET_NAME + " checking for request object " + work);
                        work = request.getParameter(work);
                        if (work != null && !work.equals("")) {
                            filterValue = work; // found it!
                            break;
                        }
                    }
                    if (filterValue.equals("")) {
                        System.out.println(SERVLET_NAME + " failed to map filter seq num " + selSeqNum + " to a value. Aborting...");
                        response.sendRedirect(redirect); 
                        conn1.close();
                        conn2.close();
                        conn3.close();
                        return;
                    }
                    
                    // Now I have the newly selected filter value and it's field seq number
                    // Add it to the filter array
                    
                    filterSeqNums[filterCount] = Integer.parseInt(selSeqNum);
                    filterValues[filterCount] = filterValue;
                    filterCount++;
                }
                
                ///////////////////////////////////////////////////////////////////////////
                //  Build Updated Bread Crumb Trail                                          //
                ///////////////////////////////////////////////////////////////////////////

                // Now lets add the latest filters to the BCT
                // and define the corresponding hidden text boxes for the new form object
                
                debug (4, SERVLET_NAME + " is building an updated bread crumb trail. ");
                for (int i = 0; i < filterCount; i++) {
                    filterSeqNum = filterSeqNums[i];
                    
                    filterSeqNumString = Integer.toString(filterSeqNum);
                    filterValue = filterValues[i];
                    debug (4, SERVLET_NAME + " filter " + i + " - SeqNum " + filterSeqNum + " Value is '" + filterValue + "'.");
                    searchRuleSet = searchRuleSets[searchRuleSetVectorMap[filterSeqNum]];
                    filterName = searchRuleSet.getParmName();
                    debug (4, SERVLET_NAME + "     Parm Field Name is '" + filterName + "'.");
                    displayUnits = "";
                    dataType = searchRuleSet.getDataType();
                    //dataType = parmDataTypes[filterSeqNum];
                    debug (4, SERVLET_NAME + "     Data Type is '" + dataType + "'.");
                    if (dataType.equals("N")) {
                    // if (searchRuleSet.getDataType().equals("N")) {
                        displayUnits = searchRuleSet.getDisplayUnits();
                        if (displayUnits == null) {
                            displayUnits = "";
                        }
                        if (!displayUnits.equals("")) {
                            displayUnits = " " + displayUnits;
                        }
                    }
                    debug (4, SERVLET_NAME + "     Display Units is '" + displayUnits + "'.");
                    
                    //////////////////////////////////////////////////////////////////////
                    // Get cooked Value for Numeric fields                              //
                    //////////////////////////////////////////////////////////////////////
                    
                    if (dataType.equals("N")) { // Filter is a Numeric
                        selectBoxName =  searchRuleSet.getQselectBoxName();
                        //selectBoxName =  parmQSelectBoxNames[filterSeqNum];
                        debug (4, SERVLET_NAME + "     Select Box Name is '" + selectBoxName + "'.");
                        if (selectBoxName.length() != 0) { // Pre-defined Select Box Exists?
                            filterValueCooked = GPSselectBox.rawToCooked(conn1, familyCode, searchRuleSet.getSubfamilyCode(),
                                selectBoxName, filterValue);
                            debug (4, SERVLET_NAME + "     filter Cooked Value is '" + filterValueCooked + "'.");
                        } else { // No pre-defined Select Box
                            debug (4, SERVLET_NAME + "     Select Box doest not exist for this parm field.");
                            filterValueLow = parseFilterValueLow(filterValue);
                            debug (4, SERVLET_NAME + "     Parsed Filter Value low is  '" + filterValueLow + "'.");
                            filterValueHigh = parseFilterValueHigh(filterValue);
                            debug (4, SERVLET_NAME + "     Parsed Filter Value High is  '" + filterValueHigh + "'.");
                            filterValueCooked = cvt.toCooked(filterValueLow, searchRuleSet.getDeMultipliers(),
                                    searchRuleSet.getParmDelimiter(),
                                    //parmDelimiters[filterSeqNum], 
                                    searchRuleSet.getDecShift(), 
                                    searchRuleSet.getAllowDuplicates(), searchRuleSet.getAllowTilde(), true);
                            if (!filterValueLow.equals(filterValueHigh)) { // Do We have a range ?
                                filterValueCooked = filterValueCooked + " ~ " 
                                    + cvt.toCooked(filterValueHigh, searchRuleSet.getDeMultipliers(),
                                        searchRuleSet.getParmDelimiter(),
                                        //parmDelimiters[filterSeqNum],
                                        searchRuleSet.getDecShift(), 
                                        searchRuleSet.getAllowDuplicates(), searchRuleSet.getAllowTilde(), true);
                                // When we have no pre-built select box and we have a numeric range,
                                // Set filterValues[i] to empty string
                                // and set range values instead
                                filterValues[i] = "";
                                dFilterValuesLow[i] = Double.parseDouble(filterValueLow);
                                dFilterValuesHigh[i] = Double.parseDouble(filterValueHigh);
                            }
                        }
                        debug (4, SERVLET_NAME + "     Cooked Filter Value is now '" + filterValueCooked + "'.");
                    }
                    
                    //////////////////////////////////////////////////////////////////////
                    // Get cooked Value for String fields                               //
                    //////////////////////////////////////////////////////////////////////
                    
                    if (dataType.equals("S")) { // Filter is a String
                        selectBoxName =  searchRuleSet.getQselectBoxName();
                        //selectBoxName =  parmQSelectBoxNames[filterSeqNum];
                        debug (4, SERVLET_NAME + "     Select Box Name is '" + selectBoxName + "'.");
                        if (selectBoxName.length() != 0) { // Pre-defined Select Box Exists?
                            filterValueCooked = GPSselectBox.rawToCooked(conn1, familyCode, searchRuleSet.getSubfamilyCode(),
                                selectBoxName, filterValue);
                            filterValueLow = parseFilterValueLow(filterValue);
                            debug (4, SERVLET_NAME + "     Parsed Filter Value low is  '" + filterValueLow + "'.");
                            filterValueHigh = parseFilterValueHigh(filterValue);
                            debug (4, SERVLET_NAME + "     Parsed Filter Value High is  '" + filterValueHigh + "'.");
                            debug (4, SERVLET_NAME + "     filter Cooked Value is '" + filterValueCooked + "'.");
                        } else { // No pre-defined Select Box
                            debug (4, SERVLET_NAME + "     Select Box doest not exist for this parm field.");
                            filterValueLow = parseFilterValueLow(filterValue);
                            debug (4, SERVLET_NAME + "     Parsed Filter Value low is  '" + filterValueLow + "'.");
                            filterValueHigh = parseFilterValueHigh(filterValue);
                            debug (4, SERVLET_NAME + "     Parsed Filter Value High is  '" + filterValueHigh + "'.");
                            filterValueCooked = filterValueLow;
                                    
                            if (filterValueLow.equals(filterValueHigh)) { // Do We have a range ?
                                // No Range
                                filterValueHigh = filterValueLow;
                            } else {
                                // We have a range
                                filterValueCooked = filterValueCooked + " ~ " + filterValueHigh;
                            }
                            // When we have no pre-built select box and we have a string range,
                            // Set filterValues[i] to empty string
                            // and set range values instead
                            
                        }
                        debug (4, SERVLET_NAME + "     Cooked Filter Value is now '" + filterValueCooked + "'.");
                        filterValues[i] = "";
                        filterValuesLow[i] = filterValueLow;
                        filterValuesHigh[i] = filterValueHigh;
                    }                    

                    //////////////////////////////////////////////////////////////////////
                    // Get cooked Value for Logical fields                               //
                    //////////////////////////////////////////////////////////////////////
                    
                    if (dataType.equals("L")) { // Filter is a Logical
                        filterValueCooked = filterValue.toLowerCase().equals("y") ? "Yes" : "No";
                    }
                    
                    debug (3, SERVLET_NAME + " processed parm filter " + i + " for seq num " + filterSeqNumString
                            + ", raw value = '" + filterValue + "', Cooked Value = '" + filterValueCooked 
                            + "', Filter Name " + filterName + ", Display units " + displayUnits);
                    debug (3, "Select Box name was '" + selectBoxName + "'");
                    
                    // Update the BCT
                   
                    work = "\"&sn" + i + "=" + filterSeqNumString + "&v" + i + "=" + filterValue + "\",\""
                    + filterName + ": " + filterValueCooked + displayUnits + "\"";
                    BCTarray.add(work);
                    
                    // Create entry for hidden text boxes for this filter
                    
                    request.setAttribute("sn" + i, filterSeqNumString);
                    request.setAttribute("v" + i, filterValue);
                }
                
                /////////////////////////////////////////////////////////////////
                // If search within results, add to BCT                        //
                /////////////////////////////////////////////////////////////////
                
                if (!searchWithin.equals("")) {
                    work = "\"&searchwithin=" + searchWithin + "\",\"Search Within Results:  " + searchWithin + "\"";
                    BCTarray.add(work);
                }
                
                /////////////////////////////////////////////////////////////////
                // Note that at this point, if there are any ranges in the     //
                // filter values arrays, the corresponding filterValues[i]     //
                // entry will be ""                                            //
                /////////////////////////////////////////////////////////////////

                // Since a subfamily code exists, look up all matching part numbers
                // for this family / subfamily
                // and extract parm data for the parm filters that exist
              
                for (int i = 0; i < 100; i++) {
                    optionsArraysVectorMap[i] = -1; // Initialize
                    // optionsArraysVectorMap points to index of select box Options Array 
                    // where i is the field seq num for the select box options Array
                    // These options arrays will contain list of options that actually exist
                    // within each parm field for the part numbers actually found.
                }
                
                displayFieldSet = new GPSfieldSet();
                displayRuleSets = displayFieldSet.getRules(conn1, familyCode, subfamilyCode, GPSfieldSet.DISPLAY_ORDER);
                
                ///////////////////////////////////////////////////////////////
                // build in stock only class here                            //
                ///////////////////////////////////////////////////////////////
                
                stock = GPSpart.getAvailableArray(conn1, familyCode, subfamilyCode, mfgrCode, seriesCode);
                manNameList = new GPSmanNameList();
                resultsPage = new ArrayList<String>();
                
                queryString = "SELECT p.part_num, v.seq_num, v.parm_value";
                if (mfgrCode.equals("")) {
                    queryString += ", p.sales_subcat";
                    mfgrSwitch = true; // We have to build a custom mfgr code table
                } else {
                    if (seriesCode.equals("")) {
                        queryString += ", p.sales_subcat, p.series";
                        seriesSwitch = true; // We have to build a custom series code table
                    }
                }
                queryString += " FROM pub.part p, pub.ps_parm_data v";
                queryString += " WHERE p.family_code = '" + familyCode + "'";
                queryString += " AND p.subfamily_code = '" + subfamilyCode + "'";
                queryString += " AND p.part_num = v.part_num";
                queryString += " AND p.has_ps_data = 1";
                if (!mfgrCode.equals("")) {
                    queryString += " AND p.sales_subcat = '" + mfgrCode + "'";
                }
                if (!seriesCode.equals("")) {
                    queryString += " AND p.series = '" + seriesCode + "'";
                }
                /*
                if (searchRuleSetVectorMapCount > 0) {
                    toggle = false;
                    ////////////////////////////////////////////////////////////////////////
                    // Notice! If a parent and child select box filter both exist         //
                    // in the BCT, the SQL statement will contain redundant entries, e.g. //
                    // v.seq_num = 5 OR v.seq_num = 5 ...                                 //
                    // but the ResultSet will be just fine!  :-)                          //
                    ////////////////////////////////////////////////////////////////////////
                    queryString += " AND (";
                    for (int i = 1; i < 100; i++) {
                        if (searchRuleSetVectorMap[i] > -1) {
                            if (toggle) {
                                queryString += " OR";
                            }
                            queryString += " v.seq_num = " + searchRuleSets[searchRuleSetVectorMap[i]].getSeqNum();
                            toggle = true;
                        }
                    }            
                    queryString += " )";
                }
                 */
                queryString += " ORDER BY p.part_num";
                debug (2, "SQL statement is " + queryString);
                rs = conn1.runQuery(queryString); // a whole lot of junk happens here
                
                /////////////////////////////////////////////////////////////////////////////
                // Now we will Process the Result Set                                      //
                // Apply filters, build part number list, and create options arrays for    //
                // remaining search filters                                                //
                /////////////////////////////////////////////////////////////////////////////
                
                debug (2, "I got the Result Set; building the Part Number list now...");
                if (rs != null) {
                    // Initialization 
                    previousPartNumber = "";
                    previousMfgrCode = "";
                    previousSeriesCode = "";
                    for (int i = 0; i < 100; i++) {
                        parmValues[i] = "";
                    }
                    partNumSeqNumsCount = 0;
                    rsMfgrCode = "";
                    if (mfgrSwitch) {
                        mfgrCodes = new ArrayList<String>();
                    }
                    rsSeriesCode = "";
                    if (seriesSwitch) {
                        seriesCodes = new ArrayList<String>();
                    }
                    pageStart = (pageNum - 1) * partNumbersPerPage;
                    pageStop = pageStart + partNumbersPerPage - 1;
                    while (rs.next()) {
                        partNumber = rs.getString("part_num").toUpperCase();
                        parmSeqNum = rs.getInt("seq_num");
                        //parmSeqNumString = Integer.toString(parmSeqNum);
                        parmValueNext = rs.getString("parm_value");
                        if (mfgrSwitch) {
                            rsMfgrCode = rs.getString("sales_subcat");
                        }
                        if (seriesSwitch) {
                            rsSeriesCode = rs.getString("series");
                        }
                        if (previousPartNumber.equals("")) { // this is for first time through
                            previousPartNumber = partNumber;
                            previousMfgrCode = rsMfgrCode;
                            previousSeriesCode = rsSeriesCode;                            
                        }
                        // check for control break
   
                        if (previousPartNumber.equals(partNumber)) { // part number has not changed
                            parmValues[parmSeqNum] = parmValueNext;
                            partNumSeqNums[partNumSeqNumsCount++] = parmSeqNum;
                        } else {
                            
                            ///////////////////////////////////////////////////////
                            // Part Number Change Control Break                  //
                            ///////////////////////////////////////////////////////
                            
                            // check stock if necessary
                            
                            // then apply any filters
                            // remember that the current part number data
                            // now at the cursor in the result set
                            // has not being processed here yet!
                            // control break, eh?
                          
                            match = true;
                            
                            if (inStockOnly) {
                                qtyAvailable = stock.getPartsAvailable(previousPartNumber);
                                debug (4, previousPartNumber + " stock = " + qtyAvailable);
                                if (qtyAvailable < 1) {
                                    match = false;
                                }
                            }
                                
                            if (match) {
                                for (int i = 0; i < filterCount; i++) {
                                    debug (10, "Match filter loop index i = " + i);
                                    debug (10, "  filterSNs[i] is " + filterSeqNums[i]);
                                    debug (10, "  filterValues[i] is '" + filterValues[i] + "'");
                                    debug (10, "  parmValues[filterSNs[i]] is '" + parmValues[filterSeqNums[i]] + "'");
                                    debug (10, "  dFilterValuesLow[i] is '" + dFilterValuesLow[i] + "'");
                                    debug (10, "  dFilterValuesHigh[i] is '" + dFilterValuesHigh[i] + "'");
                                    debug (10, "  filterValuesLow[i] is '" + filterValuesLow[i] + "'");
                                    debug (10, "  filterValuesHigh[i] is '" + filterValuesHigh[i] + "'");
                                    filterSeqNum = filterSeqNums[i];
                                    searchRuleSet = searchRuleSets[searchRuleSetVectorMap[filterSeqNum]];
                                    parmValue = parmValues[filterSeqNum].trim();
                                    if (parmValue.length() == 0) {
                                            match = false;
                                            debug (8, "Parm was missing; Match failed.");
                                            break;
                                    }
                                    filterValue = filterValues[i];
                                    dataType = searchRuleSet.getDataType();
                                    //dataType = parmDataTypes[filterSeqNum];
                                    if (dataType.equals("L")) {
                                        if (!filterValue.equals(parmValue)) {
                                            match = false;
                                            debug (8, "Logical Parm mismatch; Match failed.");
                                            break;
                                        }
                                    } else { // Numeric and String handlers are here
                                        delimiter = searchRuleSet.getParmDelimiter();
                                        if (delimiter.length() != 0 && parmValue.contains(delimiter)) {
                                            parmValues2 = parmValue.split(delimiter);  //parmValue.split(delimiter);
                                            parmValues2Count = parmValues2.length;
                                            debug (10, "****Found a delimiter**** in parm data: " + parmValue);
                                            debug (10, "    parm Array size is " + parmValues2Count);
                                        } else {
                                            parmValues2[0] = parmValue;
                                            parmValues2Count = 1;
                                        }
                                        parmValues2Match = false;
                                        for (int j = 0; j < parmValues2Count; j++) {
                                            parmValueIndividual = parmValues2[j];
                                            if (searchRuleSet.getAllowTilde() && parmValueIndividual.contains("~")) {
                                                temp = parmValueIndividual.split("~");
                                                parmValueIndividualLow = temp[0];
                                                parmValueIndividualHigh = temp[1];
                                            } else {
                                                parmValueIndividualLow = parmValueIndividual;
                                                parmValueIndividualHigh = parmValueIndividual;
                                            }
                                            if (dataType.equals("N")) {
                                                if (filterValue.length() == 0) {
                                                    dParmValueIndividualLow = Double.parseDouble(parmValueIndividualLow);
                                                    dParmValueIndividualHigh = Double.parseDouble(parmValueIndividualHigh);
                                                    if (dParmValueIndividualHigh < dFilterValuesLow[i] 
                                                            || dParmValueIndividualLow > dFilterValuesHigh[i] ) {
                                                    } else {
                                                        parmValues2Match = true;
                                                        parmValues2Count = 0;
                                                        break;
                                                    }
                                                } else {
                                                    if (filterValue.equals(parmValueIndividual)) {
                                                        parmValues2Match = true;
                                                        parmValues2Count = 0;
                                                        break;
                                                    }         
                                                }
                                            } else if (dataType.equals("S")) {
                                                debug (10, "parmValueIndividualHigh = " + parmValueIndividualHigh);
                                                debug (10, "parmValueIndividualLow = " + parmValueIndividualLow);
                                                debug (10, "filterValuesHigh[i] = " + filterValuesHigh[i]);
                                                debug (10, "filterValuesLow[i] = " + filterValuesLow[i]);
                                                debug (10, Integer.toString(parmValueIndividualHigh.compareToIgnoreCase(filterValuesLow[i])));
                                                debug (10, Integer.toString(parmValueIndividualLow.compareToIgnoreCase(filterValuesHigh[i])));
                                                
                                                 if (parmValueIndividualHigh.compareToIgnoreCase(filterValuesLow[i]) < 0
                                                        || parmValueIndividualLow.compareToIgnoreCase(filterValuesHigh[i]) > 0  ) {
                                                 } else {
                                                     debug (10, "I gotta match!");
                                                     parmValues2Match = true;
                                                     parmValues2Count = 0;
                                                     break;
                                                 }                                                                          
                                            }
                                        }  // end for (int j = 0; j < parmValues2Count; j++) {
                                        match = parmValues2Match;
                                        if (!match) {
                                            debug (8, "Match failed.");
                                            break;
                                        }
                                    } // end else
                                } // end for
                            } // end if (match)      
                            
                            if (match) {
                                resultsFound++;
                                if (searchWithinSwitch || (resultsFound >= pageStart && resultsFound <= pageStop)) {
                                    mfgrName2 = manNameList.getName(conn2, previousMfgrCode);
                                    sBuffer = new StringBuffer("<h3 align=\"left\"><u>" + mfgrName2 + "</u></h3>");
                                    parmSet = new GPSparmSet(conn1, previousPartNumber, displayRuleSets, parmValues);
                                    sBuffer.append("<ul>");
                                    if (!previousSeriesCode.equals("") ) {
                                        sBuffer.append("<li><b>Series:  " + previousSeriesCode + "</b></li>");
                                    }
                                    sBuffer.append(parmSet.getDescription());
                                    sBuffer.append("</ul>");
                                    parmSet = null;
                                    sBuffer.append("<h3 align=\"left\">Item #" + previousPartNumber);
                                    if (!inStockOnly) {
                                        qtyAvailable = stock.getPartsAvailable(previousPartNumber);
                                    }
                                    if (qtyAvailable > 0) {
                                        sBuffer.append("&nbsp;&nbsp;<b>" + qtyAvailable + " IN STOCK </b>");
                                    }
                                    sBuffer.append("</h3><hr />");
                                    partDescription = sBuffer.toString();
                                    debug (8, "Description is \n" + partDescription + "\n");
                                    if (searchWithinSwitch) {
                                        for (int i = 0; i < searchWithinArray.length; i++) {
                                            match = GPSpart.searchWithin(partDescription, searchWithinArray[i]);
                                            if (!match) {
                                                break;
                                            }
                                        }
                                    }
                                    if (!match) {
                                        partDescription = "";
                                        resultsFound--;
                                    }
                                }
                            }
                 
                            // if all matched and description is within results page range, add to results page
                            // and update option lists

                            if (match) {
                                if (resultsFound >= pageStart && resultsFound <= pageStop) {
                                            resultsPage.add(partDescription);
                                            if (resultsBegin == -1) {
                                                resultsBegin = resultsFound;
                                            }
                                            resultsEnd = resultsFound;
                                            debug (2, "Added PN " + previousPartNumber + " as result " + resultsFound);
                                }
                               
                                // Apply updates to our mfgr code table if reqd
                                
                                if (mfgrSwitch) {
                                    if (!mfgrCodes.contains(previousMfgrCode)) {
                                        mfgrCodes.add(previousMfgrCode);
                                    }
                                }
                                
                                // Apply updates to our series table if reqd
                                
                                if (seriesSwitch) {
                                    if (!previousSeriesCode.equals("")) {
                                        if (!seriesCodes.contains(previousSeriesCode)) {
                                            seriesCodes.add(previousSeriesCode);
                                        }
                                    }
                                }
                                
                                // now erase parm values for seq nums that were filters
                                // unless they were range SNs

                                for (int i = 0; i < filterCount; i++) {
                                    if (!filterValues[i].equals("")) {
                                        parmValues[filterSeqNums[i]] = "";
                                    }
                                }
              
                                
                                ////////////////////////////////////////////////////////////////
                                // add remaining parms to option lists that were NOT filters  //
                                ////////////////////////////////////////////////////////////////
                                
                                for (iWork = 0; iWork < partNumSeqNumsCount; iWork++) {
                                    int sn = partNumSeqNums[iWork];
                                    int index = searchRuleSetVectorMap[sn];
                                    if (index > -1) { // index will be -1 if this seqnum is not among the searchable field rulesets
                                        searchRuleSet = searchRuleSets[index];
                                        parmValue = parmValues[sn];
                                        if (!parmValue.equals("")) { // parmSearchItems[filterSeqNum]) { // if an option value is present AND a searchable field
                                            optionsArrayIndex = optionsArraysVectorMap[sn]; // find out which option array it goes inside
                                            if (optionsArrayIndex == -1) { // if options array does not exist yet, build one 
                                                optionsArray = new GPSoptionsArray(searchRuleSet); // s[searchRuleSetVectorMap[sn]]);
                                                optionsArrays.add(optionsArray); // Add it to the list of options arrays
                                                optionsArrayIndex = optionsArrays.size() - 1; // calculate it's index
                                                optionsArraysVectorMap[sn] = optionsArrayIndex; // update the vector map
                                            } else {
                                                optionsArray = optionsArrays.get(optionsArrayIndex);
                                            }
                                            optionsArray.add(parmValue); // Add the parm value
                                            debug (10, "Added raw value '" + parmValue + "' for seq num " + sn);
                                        }
                                    //} else {
                                    //    System.out.println("AHA! I found a non-searchable parm field and skipped it! " + sn + ": " + parmValues[sn]);
                                    }
                                    parmValues[sn] = ""; // then erase it to clean up after this prev part num
                                }   // end iteration through all seq nums found for this part
                                    // we ignored seq nums used as filters
                                    // and added any remaining options found to their appropriate option arrays for searchable fields
                                    // then we reset the data in the parmValues map
                                    // at this point all parmValues including those used as filters should be zero length strings
                            } else { // noMatch
                                for (int i = 0; i < partNumSeqNumsCount; i++) {
                                    parmValues[i] = ""; // blow away all values on a part num that did not match
                                }
                            } // end If (match) {}
                            previousPartNumber = partNumber;
                            previousMfgrCode = rsMfgrCode;
                            previousSeriesCode = rsSeriesCode; 
                            parmValues[parmSeqNum] = parmValueNext;
                            partNumSeqNumsCount = 0; // reset
                            partNumSeqNums[partNumSeqNumsCount++] = parmSeqNum;
                            debug (8, "Now processing part number " + partNumber);
                            
                        } // end else part of control break on part number change
                        
                        ////////////////////////////////////////////////////////
                        // END of Control Break Logic here!!!!!               //
                        ////////////////////////////////////////////////////////
                        
                    } // end While loop plowing thru recordset
                
                    if (!previousPartNumber.equals("")) {
                    
                        // Process the last record if present ( I know, I know, this is really ugly right now )
                    
                        ////////////////////////////////////////////////////////
                        // REPEAT of Control Break Logic here!!!!!            //
                        ////////////////////////////////////////////////////////
                                        
                            // check stock if necessary
                            
                            // then apply any filters
                            // remember that the current part number data
                            // now at the cursor in the result set
                            // has not being processed here yet!
                            // control break, eh?
                          
                            match = true;
                            
                            if (inStockOnly) {
                                qtyAvailable = stock.getPartsAvailable(previousPartNumber);
                                debug (4, previousPartNumber + " stock = " + qtyAvailable);
                                if (qtyAvailable < 1) {
                                    match = false;
                                }
                            }
                                
                            if (match) {
                                for (int i = 0; i < filterCount; i++) {
                                    debug (10, "Match filter loop index i = " + i);
                                    debug (10, "  filterSNs[i] is " + filterSeqNums[i]);
                                    debug (10, "  filterValues[i] is '" + filterValues[i] + "'");
                                    debug (10, "  parmValues[filterSNs[i]] is '" + parmValues[filterSeqNums[i]] + "'");
                                    debug (10, "  dFilterValuesLow[i] is '" + dFilterValuesLow[i] + "'");
                                    debug (10, "  dFilterValuesHigh[i] is '" + dFilterValuesHigh[i] + "'");
                                    debug (10, "  filterValuesLow[i] is '" + filterValuesLow[i] + "'");
                                    debug (10, "  filterValuesHigh[i] is '" + filterValuesHigh[i] + "'");
                                    filterSeqNum = filterSeqNums[i];
                                    searchRuleSet = searchRuleSets[searchRuleSetVectorMap[filterSeqNum]];
                                    parmValue = parmValues[filterSeqNum].trim();
                                    if (parmValue.length() == 0) {
                                            match = false;
                                            debug (8, "Parm was missing; Match failed.");
                                            break;
                                    }
                                    filterValue = filterValues[i];
                                    dataType = searchRuleSet.getDataType();
                                    //dataType = parmDataTypes[filterSeqNum];
                                    if (dataType.equals("L")) {
                                        if (!filterValue.equals(parmValue)) {
                                            match = false;
                                            debug (8, "Logical Parm mismatch; Match failed.");
                                            break;
                                        }
                                    } else { // Numeric and String handlers are here
                                        delimiter = searchRuleSet.getParmDelimiter();
                                        if (delimiter.length() != 0 && parmValue.contains(delimiter)) {
                                            parmValues2 = parmValue.split(delimiter);  //parmValue.split(delimiter);
                                            parmValues2Count = parmValues2.length;
                                            debug (10, "****Found a delimiter**** in parm data: " + parmValue);
                                            debug (10, "    parm Array size is " + parmValues2Count);
                                        } else {
                                            parmValues2[0] = parmValue;
                                            parmValues2Count = 1;
                                        }
                                        parmValues2Match = false;
                                        for (int j = 0; j < parmValues2Count; j++) {
                                            parmValueIndividual = parmValues2[j];
                                            if (searchRuleSet.getAllowTilde() && parmValueIndividual.contains("~")) {
                                                temp = parmValueIndividual.split("~");
                                                parmValueIndividualLow = temp[0];
                                                parmValueIndividualHigh = temp[1];
                                            } else {
                                                parmValueIndividualLow = parmValueIndividual;
                                                parmValueIndividualHigh = parmValueIndividual;
                                            }
                                            if (dataType.equals("N")) {
                                                if (filterValue.length() == 0) {
                                                    dParmValueIndividualLow = Double.parseDouble(parmValueIndividualLow);
                                                    dParmValueIndividualHigh = Double.parseDouble(parmValueIndividualHigh);
                                                    if (dParmValueIndividualHigh < dFilterValuesLow[i] 
                                                            || dParmValueIndividualLow > dFilterValuesHigh[i] ) {
                                                    } else {
                                                        parmValues2Match = true;
                                                        parmValues2Count = 0;
                                                        break;
                                                    }
                                                } else {
                                                    if (filterValue.equals(parmValueIndividual)) {
                                                        parmValues2Match = true;
                                                        parmValues2Count = 0;
                                                        break;
                                                    }         
                                                }
                                            } else if (dataType.equals("S")) {
                                                debug (10, "parmValueIndividualHigh = " + parmValueIndividualHigh);
                                                debug (10, "parmValueIndividualLow = " + parmValueIndividualLow);
                                                debug (10, "filterValuesHigh[i] = " + filterValuesHigh[i]);
                                                debug (10, "filterValuesLow[i] = " + filterValuesLow[i]);
                                                debug (10, Integer.toString(parmValueIndividualHigh.compareToIgnoreCase(filterValuesLow[i])));
                                                debug (10, Integer.toString(parmValueIndividualLow.compareToIgnoreCase(filterValuesHigh[i])));
                                                
                                                 if (parmValueIndividualHigh.compareToIgnoreCase(filterValuesLow[i]) < 0
                                                        || parmValueIndividualLow.compareToIgnoreCase(filterValuesHigh[i]) > 0  ) {
                                                 } else {
                                                     debug (10, "I gotta match!");
                                                     parmValues2Match = true;
                                                     parmValues2Count = 0;
                                                     break;
                                                 }                                                                          
                                            }
                                        }  // end for (int j = 0; j < parmValues2Count; j++) {
                                        match = parmValues2Match;
                                        if (!match) {
                                            debug (8, "Match failed.");
                                            break;
                                        }
                                    } // end else
                                } // end for
                            } // end if (match)      
                            
                            if (match) {
                                resultsFound++;
                                if (searchWithinSwitch || (resultsFound >= pageStart && resultsFound <= pageStop)) {
                                    mfgrName2 = manNameList.getName(conn2, previousMfgrCode);
                                    sBuffer = new StringBuffer("<h3 align=\"left\"><u>" + mfgrName2 + "</u></h3>");
                                    parmSet = new GPSparmSet(conn1, previousPartNumber, displayRuleSets, parmValues);
                                    sBuffer.append("<ul>");
                                    if (!previousSeriesCode.equals("") ) {
                                        sBuffer.append("<li><b>Series:  " + previousSeriesCode + "</b></li>");
                                    }
                                    sBuffer.append(parmSet.getDescription());
                                    sBuffer.append("</ul>");
                                    parmSet = null;
                                    sBuffer.append("<h3 align=\"left\">Item #" + previousPartNumber);
                                    if (!inStockOnly) {
                                        qtyAvailable = stock.getPartsAvailable(previousPartNumber);
                                    }
                                    if (qtyAvailable > 0) {
                                        sBuffer.append("&nbsp;&nbsp;<b>" + qtyAvailable + " IN STOCK </b>");
                                    }
                                    sBuffer.append("</h3><hr />");
                                    partDescription = sBuffer.toString();
                                    debug (8, "Description is \n" + partDescription + "\n");
                                    if (searchWithinSwitch) {
                                        for (int i = 0; i < searchWithinArray.length; i++) {
                                            match = GPSpart.searchWithin(partDescription, searchWithinArray[i]);
                                            if (!match) {
                                                break;
                                            }
                                        }
                                    }
                                    if (!match) {
                                        partDescription = "";
                                        resultsFound--;
                                    }
                                }
                            }
                 
                            // if all matched and description is within results page range, add to results page
                            // and update option lists

                            if (match) {
                                if (resultsFound >= pageStart && resultsFound <= pageStop) {
                                            resultsPage.add(partDescription);
                                            if (resultsBegin == -1) {
                                                resultsBegin = resultsFound;
                                            }
                                            resultsEnd = resultsFound;
                                            debug (2, "Added PN " + previousPartNumber + " as result " + resultsFound);
                                }
                               
                                // Apply updates to our mfgr code table if reqd
                                
                                if (mfgrSwitch) {
                                    if (!mfgrCodes.contains(previousMfgrCode)) {
                                        mfgrCodes.add(previousMfgrCode);
                                    }
                                }
                                
                                // Apply updates to our series table if reqd
                                
                                if (seriesSwitch) {
                                    if (!previousSeriesCode.equals("")) {
                                        if (!seriesCodes.contains(previousSeriesCode)) {
                                            seriesCodes.add(previousSeriesCode);
                                        }
                                    }
                                }
                                
                                // now erase parm values for seq nums that were filters
                                // unless they were range SNs

                                for (int i = 0; i < filterCount; i++) {
                                    if (!filterValues[i].equals("")) {
                                        parmValues[filterSeqNums[i]] = "";
                                    }
                                }
              
                                
                                ////////////////////////////////////////////////////////////////
                                // add remaining parms to option lists that were NOT filters  //
                                ////////////////////////////////////////////////////////////////
                                
                                for (iWork = 0; iWork < partNumSeqNumsCount; iWork++) {
                                    int sn = partNumSeqNums[iWork];
                                    int index = searchRuleSetVectorMap[sn];
                                    if (index > -1) { // index will be -1 if this seqnum is not among the searchable field rulesets
                                        searchRuleSet = searchRuleSets[index];
                                        parmValue = parmValues[sn];
                                        if (!parmValue.equals("")) { // parmSearchItems[filterSeqNum]) { // if an option value is present AND a searchable field
                                            optionsArrayIndex = optionsArraysVectorMap[sn]; // find out which option array it goes inside
                                            if (optionsArrayIndex == -1) { // if options array does not exist yet, build one 
                                                optionsArray = new GPSoptionsArray(searchRuleSet); // s[searchRuleSetVectorMap[sn]]);
                                                optionsArrays.add(optionsArray); // Add it to the list of options arrays
                                                optionsArrayIndex = optionsArrays.size() - 1; // calculate it's index
                                                optionsArraysVectorMap[sn] = optionsArrayIndex; // update the vector map
                                            } else {
                                                optionsArray = optionsArrays.get(optionsArrayIndex);
                                            }
                                            optionsArray.add(parmValue); // Add the parm value
                                            debug (10, "Added raw value '" + parmValue + "' for seq num " + sn);
                                        }
                                    //} else {
                                    //    System.out.println("AHA! I found a non-searchable parm field and skipped it! " + sn + ": " + parmValues[sn]);
                                    }
                                    parmValues[sn] = ""; // then erase it to clean up after this prev part num
                                }   // end iteration through all seq nums found for this part
                                    // we ignored seq nums used as filters
                                    // and added any remaining options found to their appropriate option arrays for searchable fields
                                    // then we reset the data in the parmValues map
                                    // at this point all parmValues including those used as filters should be zero length strings
                            } else { // noMatch
                                for (int i = 0; i < partNumSeqNumsCount; i++) {
                                    parmValues[i] = ""; // blow away all values on a part num that did not match
                                }
                            } // end If (match) {}
                        
                        ////////////////////////////////////////////////////////
                        // END of Repeated Control Break Logic here!!!!!      //
                        ////////////////////////////////////////////////////////
                        
                    }
                    rs.close();
                    rs = null;
                    conn1.closeStatement();
                                
                    if (mfgrSwitch && !searchWithinSwitch) {
                        mfgrNames = new ArrayList<String>();
                        for (int i = 0; i < mfgrCodes.size(); i++) {
                            work = mfgrCodes.get(i);
                            mfgrNames.add("\"" + work + "\",\"" + GPSmanCodes.getMfgrName(conn2, work) + "\"");
                        }
                    }

                    if (searchWithinSwitch) {
                        seriesCodes = null;
                    } else {
                        if (seriesSwitch) {
                            for (int i = 0; i < seriesCodes.size(); i++) {
                                work = seriesCodes.get(i);
                                work = "\"" + work + "\",\"" + work + "\"";
                                seriesCodes.set(i, work);
                            }
                        }
                    }
                    
                } else {
                    // if we get here we had a null result set
                    System.out.println("Unexpected SQL error in " + SERVLET_NAME + "; the result set is null.");
                    response.sendRedirect(redirect);
                    conn1.close();
                    conn2.close();
                    conn3.close();
                    return;
                }
                debug (2, SERVLET_NAME + " found " + resultsFound + " Part Numbers");
                results = "Results " + (++resultsBegin) + " - " + (++resultsEnd);

                ////////////////////////////////////////////////////////////////////////////
                // Generate Javascript for building options arrays in browser             //
                // Note that refine.jsp currently uses up to 16 option arrays.            //
                // extras are simply ignored                                              //
                ////////////////////////////////////////////////////////////////////////////
                
                iWork = 0;
                for (int i = 0; i < optionsArrays.size(); i++) {
                    optionsArray = optionsArrays.get(i);
                    count = optionsArray.size();
                    parmSeqNum = optionsArray.getSeqNum();
                    debug (4, SERVLET_NAME + " found " + count + " options for seq num " + parmSeqNum);
                    if (count > 1) {
                        optionsSet = optionsArray.cookedOptionList(conn1);
                        request.setAttribute("parmset" + iWork++, optionsSet);
                    }
                }

                if (subfamilyName.equals("")) {
                    subfamilyName = subfam.getSubfamilyName(familyCode, subfamilyCode);
                }
              
                //////////////////////////////////////////////////////////////////////
                // Build Page Index                                                 //
                //////////////////////////////////////////////////////////////////////
                
                pageIndex = new ArrayList<String>();
                iWork = resultsFound;
                iWork = (iWork + partNumbersPerPage - 1) / partNumbersPerPage; // # pages
                pageEnd = "1";
                for (int i = 1; i <= iWork; i++) {
                    pageEnd = Integer.toString(i);
                     pageIndex.add(pageEnd);
                }
            } catch (Exception e) {
                e.printStackTrace();
                debug (5, SERVLET_NAME + " died while building Part Number Result Set");
                response.sendRedirect(redirect); 
                conn1.close();
                conn2.close();
                conn3.close();
                return;
            }
        }
        
        conn1.close();
        conn2.close();
        conn3.close();
               
        Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("EST"));
        iStop = cal2.getTimeInMillis();
        elapsed = Double.parseDouble(Long.toString(iStop - iStart))/1000;

        request.setAttribute("bctarray", BCTarray);
        request.setAttribute("elapsed", Double.toString(elapsed));
        request.setAttribute("familycode", familyCode);
        request.setAttribute("familyname", familyName);
        request.setAttribute("instockonly", inStockOnly ? "y" : "n");
        request.setAttribute("itemsfound", Integer.toString(++resultsFound));
        request.setAttribute("itemsperpageset", Integer.toString(partNumbersPerPage));
        request.setAttribute("mfgrcode", mfgrCode);
        request.setAttribute("mfgrname", mfgrName);
        request.setAttribute("mfgrnames", mfgrNames);
        request.setAttribute("pageend", pageEnd);
        request.setAttribute("pageindex", pageIndex);
        request.setAttribute("pagenum", pageNum);
        request.setAttribute("partnumberspage", resultsPage);
        request.setAttribute("productlinecode", productLineCode);
        request.setAttribute("productlinename", productLineName);
        request.setAttribute("results", results);
        request.setAttribute("searchwithin", searchWithin);
        request.setAttribute("seriescode", seriesCode);
        request.setAttribute("seriescodes", seriesCodes);
        request.setAttribute("seriesname", seriesName);
        request.setAttribute("subfamilycode", subfamilyCode);
        request.setAttribute("subfamilycodes", subfamilyCodes);
        request.setAttribute("subfamilyname", subfamilyName);
               
        RequestDispatcher view = request.getRequestDispatcher("refine.jsp");
        view.forward(request,response);
        debug (2, "Invoked refine.jsp");
        debug (2, "");
    }
    
    private String format(long value) {
        boolean minus = false;
        if (value < 0) {
            minus = true;
            value = - value;
        }
        String work = Long.toString(value);
        String result = "";
        String trio = "";
        int len = 0;
        while (work.length() > 3) {
            len = work.length() - 3;
            result = "," + work.substring(len) + result;
            work = work.substring(0, len);
        }
        result = work + result;
        if (minus) {
            result = "-" + result;
        }
        return result;
    }    
        
    private String freeMemory() {
        long free = Runtime.getRuntime().freeMemory();
        long diff = free - lastFreeMemory;
        lastFreeMemory = free;
        return " " + format(free) + " " + format(diff);
    }
        
    private boolean matchTest(String parmValueString, String delimiter, String filterValue) {
        String[] parmValues = parmValueString.split("delimiter");
        for (int i = 0; i < parmValues.length; i++) {
            if (parmValues[i].trim().equals(filterValue)) {
                return true;
            }
        }
        return false; // no match
    }
    
    private boolean matchTest(String parmValueString, String delimiter, double filterValueLow, double filterValueHigh) {
        String[] parmValues = parmValueString.split("delimiter");
        for (int i = 0; i < parmValues.length; i++) {
            double dParmValue = Double.parseDouble(parmValues[i]);
            if (dParmValue >= filterValueLow && dParmValue <= filterValueHigh) {
                return true;
            }
        }
        return false; // no match
    }
    
    private String parseFilterValueLow(String work) {
        String result = work;
        if (work.startsWith("{[") && work.endsWith("]}") && work.contains("]~[")) {
            result = work.substring(2, work.indexOf("]~["));
        }
        return result;
    }
    
    private String parseFilterValueHigh(String work) {
        String result = work;
        if (work.startsWith("{[") && work.endsWith("]}") && work.contains("]~[")) {
            result = work.substring(work.indexOf("]~[") + 3, work.indexOf("]}"));
        }
        return result;
    }
    
    private void debug (int level, String x) {
        if (debugLevel >= level) {
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
        return "I am the great all powerful GPS Search Utility";
    }
    // </editor-fold>
}
