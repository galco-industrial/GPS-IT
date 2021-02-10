/*
 * gpsdvf4.java
 *
 * Created on November 28, 2006, 12:56 PM
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

/**
 *
 * @author Sauter
 * @version 1.3.00
 *
 * Modification History
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * 07/21/2007 DES   fixed rankscore calculation if there was only one searchweight in totalweights
 *
 *
 */
public class gpsdvf4 extends HttpServlet {
            
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsdvf4.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        String work = request.getParameter("validation"); // Check for invalid Call
        if (!work.equals("OK")) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6499");
            return;
        }
        work = request.getParameter("B1");
        if (work == null || !work.equals("Search")) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6500");
            return;
        }    
        HttpSession session = request.getSession(); // Get a handle on our session
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        //debugSw = true;
                
	if (session.isNew()) {                      // check for timeout
           response.sendRedirect ("gpstimeout.htm");
           return;
        }
        
        // Declare some constants and variables here
        
        final String UC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String LC = "abcdefghijklmnopqrstuvwxyz";
        final String SP = " ";
        final String NU = "0123456789";
        final String QU = "\"";
        final String AP = "'";
               
        String auditUserID;
        long available = 0;
        String cooked = "";
        int countTotal;
        int countMatch;
        String dataType;
        String enableToolTips;
        String familyCode;
        String familyDescription;
        float floatWork;
        boolean goodSoFar;
        int i;
        boolean inStockOnly;
        int j;
        int k;
        //String head[];
        int m;
        String manCode;
        String manDescription;
        float maxWork;
        float minWork;
        String multiMessage = "";
        float numericMatchResult;
        String parmLabel;
        String parmValueCooked;
        String parmValueRaw;
        String partNum;
        String queryString;
        String raw = "";
        float rankScore; 
        int seqNum;
        String subfamilyCode;
        String subfamilyDescription;
        String temp;
        float totalWeights = 0;
        //String value[];
        String work2;
        String work3;
        String work4;
                     
        GPSparmSet aParmSet[] = new GPSparmSet[ 200 ];
        ResultSet rs = null;
        GPSfieldSet qfs = null;
        GPSmatchItem matchItem = null;
        GPSparmSet parmSet = null;
        GPSrules rules[] = null;
        List <String> matchedPN = new ArrayList <String> ();
        ArrayList<GPSmatchItem> matchItems = new ArrayList<GPSmatchItem>();
        List <String> matchedRank = new ArrayList <String> ();
        List <String> generatedScript = new ArrayList <String> ();

        // Check request values and set Session Variables
        // Make sure we have no null object references.
        
        auditUserID = setDefault(request.getParameter("auditUserID"),"");
        if (auditUserID.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6501");
            return;
        }
        session.setAttribute("auditUserID", auditUserID);
        
        enableToolTips = setDefault(request.getParameter("enableToolTips"),"");
        //if (enableToolTips.equals("") ) {
        //    response.sendRedirect ("gpsabend.jsp?rc=gps6502");
        //    return;
        //}
        session.setAttribute("enableToolTips", enableToolTips);
       
        familyCode = setDefault(request.getParameter("familyCode"),"");
        if (familyCode.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6503");
            return;
        }
        session.setAttribute("familyCode", familyCode);
        
        familyDescription = setDefault(request.getParameter("familyDescription"),"");
        if (familyDescription.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6504");
            return;
        }
        session.setAttribute("familyDescription", familyDescription);
               
        manCode = setDefault(request.getParameter("manCode"),"");
        if (manCode.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6505");
            return;
        }
        session.setAttribute("manCode", manCode);
        
        work = (String) session.getAttribute("inStockOnly");
        inStockOnly = work.equals("Y") ? true : false;
        // session.setAttribute("inStockOnly", inStockOnly ? "Y" : "N");
        
        //manDescription = setDefault(request.getParameter("manDescription"),"");
        //if (manDescription.equals("") ) {
        //    response.sendRedirect ("gpsabend.jsp?rc=gps6506");
        //    return;
       // }
        //session.setAttribute("manDescription", manDescription);

        subfamilyCode = setDefault(request.getParameter("subfamilyCode"),"");
        if (subfamilyCode.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6507");
            return;
        }
        session.setAttribute("subfamilyCode", subfamilyCode);
        
        subfamilyDescription = setDefault(request.getParameter("subfamilyDescription"),"");
        if (subfamilyDescription.equals("") ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6508");
            return;
        }
        session.setAttribute("subfamilyDescription", subfamilyDescription);
            
        // The Sequence Number Map contains the parm sequence numbers 
        // that correspond to the form Search objects order
        
        List seqNumMap = (List)session.getAttribute("seqNumMap");
        if (seqNumMap == null ) {
            response.sendRedirect ("gpsabend.jsp?rc=gps6521");
            return;
        }
        
        debug("Request parms have been validated.");
        WDSconnect conn = new WDSconnect();  // Connect to WDS database    
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        debug("Database is open.");
         
        try {
            countTotal = 0;
            countMatch = 0;
            debug ("Looking up Rules for Search Fields.");
            qfs = new GPSfieldSet();
            rules = qfs.getRulesInSearchOrder(conn, familyCode, subfamilyCode);
            debug("Field rules are loaded; found rules for " + qfs.count() + " fields");
            
            // Pre-calc min and max for each numeric field
            for (i = 0; i < seqNumMap.size() ;  i++) {
                // i is the index of every field in search screen
                seqNum = Integer.parseInt((String)seqNumMap.get(i));
                // seqNum is the corresponding parm Seq Number for the parm value
                debug("Entry " + i + " in SeqNumMap() is " + seqNum);
                cooked = "inputObject" + Integer.toString(i);
                debug("Cooked Object name is " + cooked);
                raw = "raw" + Integer.toString(i);
                debug("Raw Object name is " + raw);
                work = request.getParameter(raw); // work with raw value if present
                if (work == null) {  // otherwise work with cooked value
                    work = request.getParameter(cooked); 
                    debug("Raw value is null!");
                } else {
                    debug("Raw value is '" + work + "'");
                    temp = request.getParameter(cooked);
                    if (temp == null) {
                        debug("Cooked value is null!");
                    } else {
                        debug("Cooked value is '" + temp +"'");
                    }
                }
                if (work == null) {
                    work = "";
                    debug("Cooked field " + cooked + " was null!");
                } else {
                    debug("Using value: '" + work + "'");
                }
                work = work.trim();
                if (work.length() != 0) { // if it is a non-blank entry we're gonna search on it
                    for (j = 0; j < qfs.count(); j++) {  // find the matching field rules set
                        k = rules[j].getSeqNum();
                        debug("Field Rules " + j + " has SeqNum " + k);
                        if (k == seqNum) {
                            break;
                        }
                    }
                    // j is index of corresponding field rules
                    dataType = rules[j].getDataType();
                    if (dataType.equals("N")) {
                        debug("Numeric search field " + i + " contains '" + work + "' Field Rules Index is " + j);
                        debug("and SeqNum is " + seqNum);
                        debug("");
                        rules[j].setSearchString(work);
                        floatWork = Float.parseFloat(work);
                        rules[j].setSearchValue(floatWork);
                        minWork = (float) rules[j].getSearchMin();
                        minWork = floatWork - (floatWork * minWork / (float) 100.0);
                        rules[j].setSearchMinValue(minWork);
                        maxWork = (float) rules[j].getSearchMax();
                        maxWork = floatWork + (floatWork * maxWork / (float) 100.0);
                        rules[j].setSearchMaxValue(maxWork);
                        debug("Numeric fields Min " + minWork + " and Max " + maxWork + " have been calculated.");
                    }
                    if (dataType.equals("S")) {
                        rules[j].setSearchString(work);
                    
                    }
                    if (dataType.equals("L")) {
                        rules[j].setSearchString(work);
                    
                    }
                    if (dataType.equals("D")) {
                    }
                }
            } // end for (int i = 0; i < seqNumMap.size() ;  i++) {  
            
            queryString = "SELECT part_num ";
            queryString += " FROM pub.part";
            debug("familyCode is '" + familyCode + "'");
            queryString += " WHERE family_code = '" + familyCode +"'";
            queryString += " AND has_ps_data = '1'";
            debug("subfamilyCode is '" + subfamilyCode + "'");
            if (!subfamilyCode.equals("*")) {
                queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            }
            debug("manCode is '" + manCode + "'");
            if (!manCode.equals("*")) {
                queryString += " AND sales_subcat = '" + manCode + "'";
            }
            debug(queryString);
            
            rs = conn.runQuery(queryString);
            if (rs == null) {
                request.setAttribute("message", "A fatal error occured while talking to the database in " + SERVLET_NAME);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
            debug("Processing Part Numbers...");

            // loop through all the part numbers
            // checking for a close match
            while (rs.next()) {
                if (countTotal == 0) {  // first time through?
                    for (i = 0; i < qfs.count(); i++) { // iterate through searchable items
                                                        // to calculate totalWeights
                        work = rules[i].getSearchString();
                        if (work != null) { // if we're searching on this field
                            seqNum = rules[i].getSeqNum(); // get Seq Num of field we're matching on
                            dataType = rules[i].getDataType();
                            if (dataType.equals("N")) {
                                totalWeights += rules[i].getSearchWeight();
                            }
                        }
                    }
                    debug("Calculated Total Relative Weights " + totalWeights);
                }
                countTotal++;
                rankScore = 0;   // init rank for this part num 
                goodSoFar = true;
                partNum = rs.getString("part_num");
                debug("Processing PN " + partNum);
                available = GPSpart.getAvailable(conn, partNum);
                debug("In stock and available = " + available);
                
                // Skip this part if instock only and there is no stock
                
                if (inStockOnly && available < 1) {
                    goodSoFar = false;
                }
                if (goodSoFar) {
                    // Get parms for this PN
                    parmSet = new GPSparmSet();
                    parmSet.read(conn, partNum);
                    debug("Parm values for " + partNum + " were read.");
                    // Check if this PN matches all search criteria
                
                    String head[] = {"","","",""}; // new String[4];
                    String value[] = {"","","",""}; //new String[4];
                    m = 0;
                    for (i = 0; i < qfs.count(); i++) { // iterate through searchable items
                        work = rules[i].getSearchString();
                        if (work != null) { // if we're searching on this field
                            seqNum = rules[i].getSeqNum(); // get Seq Num of field we're matching on
                            dataType = rules[i].getDataType();
                            parmLabel = rules[i].getParmName();
                            parmValueRaw = parmSet.getParmValue(seqNum);
                            debug("Parm field " + seqNum + " contains '" + parmValueRaw + "'");
                            if (dataType.equals("N")) {
                                work2 = Float.toString(rules[i].getSearchValue());
                                work3 = Float.toString(rules[i].getSearchMinValue());
                                work4 = Float.toString(rules[i].getSearchMaxValue());
                                debug("Search Numeric Value is " + work2);
                                debug("Search Min Numeric Value is " + work3);
                                debug("Search Max Numeric Value is " + work4);
                                numericMatchResult = GPSparmCheck.numericMatch(parmValueRaw,
                                rules[i].getSearchValue(), rules[i].getSearchMinValue(), 
                                rules[i].getSearchMaxValue(), rules[i].getParmDelimiter(),
                                rules[i].getAllowTilde());
                                debug("Calculated Score is " + numericMatchResult);
                                if (numericMatchResult < 0) {
                                    debug("BOO! - No Match or Parse error.");
                                    goodSoFar = false;
                                    break;
                                } else {
                                    debug("YAY! - Numeric Value is within range.");
                                                                       
                                    // fix rankscore if there was only one searchweight in totalweights  07/21/2007
                                   
                                    if (totalWeights == rules[i].getSearchWeight()) {
                                        rankScore = numericMatchResult;
                                    } else {
                                        rankScore += numericMatchResult * (totalWeights - rules[i].getSearchWeight()) / totalWeights;
                                    }
                                }
                            }
                            if (dataType.equals("S")) {
                                debug("Search Value for field " + seqNum + " contains '" + work + "'");
                                if (!GPSparmCheck.stringMatch(parmValueRaw,
                                    work, rules[i].getParmDelimiter(), rules[i].getAllowTilde()) ) {
                                    goodSoFar = false;
                                    debug("BOO! - No Match or Parse error.");
                                    break;
                                } else {
                                    debug("YAY! - We got a Match.");
                                }
                            }
                            if (dataType.equals("L")) {
                                debug("Search Value for field " + seqNum + " contains '" + work + "'");
                                if (!parmValueRaw.equals(work)) {
                                    goodSoFar = false;
                                    debug("BOO! - No Match or Parse error.");
                                    break;
                                } else {
                                    debug("YAY! - We got a Match.");
                                }
                            }
                            if (dataType.equals("D")) {
                            // Not supported yet !!!!!!!!!!!!!!!!!!!!!!!!!!!
                            }
                            // Now that we did whatever test on this parm field
                            if (goodSoFar) {
                                // add parm to match set if it was mandatory for a search
                                debug ("Match was good, so here is where I check to see if this was a mandatory search parm");
                                if (rules[i].getSearchRequired()) {
                                    if (m < 4) {
                                        head[ m ] = parmLabel;
                                        debug ("head is " + parmLabel);
                                        value[ m ] = parmValueRaw;
                                        debug ("value is " + parmValueRaw);
                                        m++;
                                    }
                                }
                            }
                        } // end if (work.length() != 0) {
                    }  // end for (i = 0; i < qfs.count(); i++) {
                    // if goodSoFar is true, calculate final weight and add to sort List
                    if (goodSoFar) {
                        debug("Calculated rank score for this PN is " + rankScore);
                        debug("*****************************We got a MATCH*******************************");
                        matchedPN.add(partNum);
                        matchItem = new GPSmatchItem(partNum, rankScore, available, 
                            head[0], value[0], head[1], value[1],
                            head[2], value[2], head[3], value[3]);
                        matchItems.add(matchItem);
                        countMatch++;
                    }   
                    parmSet = null;
                }
                if (!goodSoFar) {
                    debug("No match.");
                }
            }  // end while (rs.next()) {
            rs.close();
            debug("Found " + countMatch + " out of " + countTotal + " candidates.");
            if (countMatch > 1) {
                Collections.sort(matchItems);
                debug ("Items were sorted successfully.");
            }
            if (countMatch > 0) {
                for ( i = 0 ; i < matchItems.size(); i++) {
                    matchItem = matchItems.get(i);
                    if (i == 0) {
                        work = "aHead[0] = \"" + matchItem.getHead1() + "\";";
                        generatedScript.add(work);
                        work = "aHead[1] = \"" + matchItem.getHead2() + "\";";
                        generatedScript.add(work);
                        work = "aHead[2] = \"" + matchItem.getHead3() + "\";";
                        generatedScript.add(work);
                        work = "aHead[3] = \"" + matchItem.getHead4() + "\";";
                        generatedScript.add(work);
                    }
                    work = "aPartNum[f] = \"" + matchItem.getPartNum() + "\";";
                    generatedScript.add(work);
                    work = "aRank[f] = " + matchItem.getRank() + ";";
                    generatedScript.add(work);
                    work = "aQuantity[f] = " + matchItem.getAvailable() + ";";
                    generatedScript.add(work);
                    work = "aParm1[f] = \"" + matchItem.getParm1() + "\";";
                    generatedScript.add(work);
                    work = "aParm2[f] = \"" + matchItem.getParm2() + "\";";
                    generatedScript.add(work);
                    work = "aParm3[f] = \"" + matchItem.getParm3() + "\";";
                    generatedScript.add(work);
                    work = "aParm4[f] = \"" + matchItem.getParm4() + "\";";
                    generatedScript.add(work);
                    generatedScript.add("f++;");
                }
            }
       
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occured while talking to the database in " + SERVLET_NAME);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn.close();
            return;
        }
        multiMessage = "Found " + countMatch + " out of " + countTotal + " items.";
        request.setAttribute("statusMessage", multiMessage);
        request.setAttribute("matches", matchedPN);
        request.setAttribute("generatedScript", generatedScript);
        RequestDispatcher view = request.getRequestDispatcher("gpsdvf3a.jsp");
        view.forward(request,response);
        conn.close();
        conn = null;
        return;
    }
   
private void debug (String x) {
    if (debugSw) {
        System.out.println(x);
    }
}

private String setDefault(String item, String itemDefault) {
    if (item == null) {
        item = "";
    }
    if (item.equals("") ) {
        item = itemDefault;
    }
    return item;
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
