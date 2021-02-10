/*
 * gpsobf3.java
 *
 * Created on July 28, 2008, 3:59 PM
 */

package gpsSelectBoxOptions;

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
 * I export parm data for a string or numeric text field.
 * The export file is a worksheet in CSV format.
 *
 * Modification History
 *
 */
public class gpsobf3 extends HttpServlet {
    
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
        
        String auditDate; 
        String auditTimeRaw;
        String auditUserID = "";
        String cooked = "";
        GPScvt cvt = new GPScvt();
        String dataType = "";
        String displayUnits = "";
        String familyCode = "";
        String fExt = "";
        String fileName = "";
        String fsep = File.separator;
        String gpsExportPath = "";
        String hhmmss;
        int i = 0;
        int j = 0;
        int k;
        String listCode = "";
        String message = "";
        PrintWriter out = null;
        String outFileName = "";
        String parmName = "";
        int parmSeqNum = 0;
        String printHeaderA = "";
        String printHeaderB = "";
        String queryString = "";
        String raw = "";
        ResultSet rs = null;
        GPSrules ruleSet = null;
        GPSrules[] ruleSets = null;
        String subfamilyCode = "";
        RequestDispatcher view = null;
        String work = "";
   
        try {
            auditDate = DateTime.getDateYYMMDD(); 
            auditTimeRaw = Integer.toString(DateTime.getSecondsSinceMidnight());
            hhmmss = DateTime.getTimeHHMMSS("");
            gpsExportPath = getServletContext().getInitParameter("exportPath");
        
            auditUserID = (String) session.getAttribute("auditUserID");
            familyCode = request.getParameter("familyCode");
            subfamilyCode = request.getParameter("subfamilyCode");
            parmSeqNum = Integer.parseInt(request.getParameter("seqNum"));
           
            ruleSets = (GPSrules[]) session.getAttribute("sRuleSets");
            i = 0;
            while (ruleSets[i] != null) {
                if (parmSeqNum == ruleSets[i].getSeqNum()) {
                    ruleSet = ruleSets[i];
                    break;
                }
                i++;
            }
            if (ruleSet == null) {
                conn.close();
                message = " Could not locate ruleset for seq num " + familyCode + "/" + subfamilyCode + "/" + parmSeqNum;
                debug (debugLevel, 0, uStamp + message);
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
            debug (debugLevel, 4, uStamp + " Field rule set has been located for seq num " 
                    + familyCode + "/" + subfamilyCode + "/" + parmSeqNum);
            dataType = ruleSet.getDataType();
            parmName = ruleSet.getParmName();
            if (dataType.equals("N")) {
                displayUnits = ruleSet.getDisplayUnits();
            }
            displayUnits = ruleSet.getDisplayUnits();
        
            ////////////////////////////////////////////////////////////////////////////////
            // Get parm data for this extract                                             //
            ////////////////////////////////////////////////////////////////////////////////
            queryString = "SELECT DISTINCT v.parm_value";
            queryString += " FROM pub.part p, pub.ps_parm_data v";
            queryString += " WHERE p.has_ps_data = 1";
            queryString += " AND p.family_code = '" + familyCode + "'";
            if (!subfamilyCode.equals("*")) {
                queryString += " AND p.subfamily_code = '" + subfamilyCode + "'";
            }
            queryString += " AND p.part_num = v.part_num";
            queryString += " AND v.seq_num = " + parmSeqNum;
            queryString += " AND v.parm_value <> ''";
            queryString += " ORDER BY v.parm_value";
            debug (debugLevel, 4, uStamp + " SQL query string is " + queryString);
            rs = conn.runQuery(queryString);
            if (rs == null) {
                message = " SQL statement error: " + conn.getError();
                debug (debugLevel, 0, uStamp + message);
                request.setAttribute("message", message);
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
        
            // Build path & file name and Open the output file
        
            fileName = familyCode.toLowerCase() + "." + subfamilyCode.toLowerCase() + "." 
                    + dataType.toLowerCase() + parmSeqNum + "."
                    + auditUserID.toLowerCase() + "." + removeSlashes(auditDate) + "." + hhmmss;
            fExt = ".box";
            outFileName = gpsExportPath + fileName + fExt;
            debug (debugLevel, 4, uStamp + " Output file name is " + outFileName);
        
            // Write Header Rows
        
            printHeaderA = "\"H\",\"Family Code\",\"" + familyCode + "\"";
            debug (debugLevel, 8, uStamp + printHeaderA);
            printHeaderA = "\"H\",\"Subfamily Code\",\"" + subfamilyCode + "\"";
            debug (debugLevel, 8, uStamp + printHeaderA);
            printHeaderA = "\"H\",\"Data Type\",\"" + dataType + "\"";
            debug (debugLevel, 8, uStamp + printHeaderA);
            printHeaderA = "\"H\",\"Seq Num\"," + parmSeqNum;
            debug (debugLevel, 8, uStamp + printHeaderA);
            printHeaderA = "\"H\",\"Parm Name\",\"" + parmName + "\"";
            debug (debugLevel, 8, uStamp + printHeaderA);
            if (dataType.equals("N")) {
                printHeaderA = "\"H\",\"Display Units\",\"" + displayUnits + "\"";
                debug (debugLevel, 8, uStamp + printHeaderA);
            }
            printHeaderA = "\"H\",\"Select Box Name\",\"\"";
            debug (debugLevel, 8, uStamp + printHeaderA);
            printHeaderA = "\"H\",\"Order\",\"Raw 1\",\"Raw 2\",\"Cooked\",\"Parent\",\"Default\"";
            debug (debugLevel, 8, uStamp + printHeaderA);
                
            // Let's begin to build each data row now...
        
            j = 0;
            ArrayList<String> optionList = new ArrayList<String>();
            ArrayList<String> optionValue = new ArrayList<String>();
        
            while(rs.next()) {
                raw = rs.getString("parm_value");       
                if (dataType.equals("N")) {
                    optionValue.add(raw);
                } else {
                    k = (j + 1) * 10;
                    work = Integer.toString(k);
                    work = "\"I\" , " + work + " , \"" + raw + "\" , \"" + raw + "\" , \"" + raw + "\" , \"\" , \"\"";
                    optionList.add(work);
                    debug (debugLevel, 8, uStamp + work);
                }
                j++;
            }
            rs.close();
            conn.closeStatement();
            debug (debugLevel, 6, uStamp + " Found " + j + " unique values.");
        
            if (dataType.equals("N")) {
                debug (debugLevel, 4, uStamp + " Sorting Numeric values in ascending order...");
                j = 0;
                double dWork = 0;
                while (optionValue.size() > 0) {
                    int lowestPointer = 0;
                    raw = optionValue.get(0);
                    double lowestValue = Double.parseDouble(raw);
                    for (i = 1; i < optionValue.size(); i++) {
                        raw = optionValue.get(i);
                        dWork = Double.parseDouble(raw);
                        if (dWork < lowestValue) {
                            lowestValue = dWork;
                            lowestPointer = i;
                        }
                    }
                    raw = optionValue.remove(lowestPointer);
                    optionList.add(raw);
                }
                k = optionList.size();
                debug (debugLevel, 6, uStamp + " Found " + k + " numeric options...");

                if (k > 25) {
                    String raw1 = "";
                    String raw2 = "";
                    String cooked1 = "";
                    String cooked2 = "";
                    j = (int) Math.pow(k, 0.5);
                    if (k > 625) {
                        j = (int) Math.pow(k, 0.3333);
                    } else {
                        j = (int) Math.pow(k, 0.5);
                    }
                    debug (debugLevel, 6, uStamp + " Parent Select Box will contain around " + (int)(k/j+1) + " options.");
                    j--;
                    for (int m = 0; m < k; ) {
                        raw1 = optionList.get(m);
                        m = m + j;
                        if (m >= k) {
                            m = k - 1;
                        }
                        raw2 = optionList.get(m);
                        cooked1 = cvt.toCooked(raw1, ruleSet.getDeMultipliers(), 
                            ruleSet.getParmDelimiter(), ruleSet.getDecShift(), 
                            ruleSet.getAllowDuplicates(), ruleSet.getAllowTilde(), true);
                        cooked2 = cvt.toCooked(raw2, ruleSet.getDeMultipliers(), 
                            ruleSet.getParmDelimiter(), ruleSet.getDecShift(), 
                            ruleSet.getAllowDuplicates(), ruleSet.getAllowTilde(), true);
                        work = "\"{[" + raw1 + "]~[" + raw2 + "]}\" , \"" 
                            + cooked1 + " ~ " + cooked2 + "\"";
                        m++;
                        optionList.add(work);
                        debug (debugLevel, 8, uStamp + work);
                    }
                } else {
                    // handle the child boxes here
                }
            }
            
            //   Put the lists inside the Request Object and forward to the JSP to display
        
            request.setAttribute("count", j);
            request.setAttribute("familyCode", familyCode); 
            request.setAttribute("outFileName", outFileName);
            request.setAttribute("subfamily", subfamilyCode); 
                        
            session.setAttribute("sRuleSets", null);
            
            //   Forward the goodies to the JSP     
     
            request.setAttribute("statusMessage", "Operation Completed successfully<br />File Name is" + outFileName);
            view = request.getRequestDispatcher("gpsobf3.jsp");
            view.forward(request,response);
        } catch (IOException e){
            conn.closeStatement();
            conn.close();
            message = " I/O error: " + e;
            debug (debugLevel, 0, uStamp + message);
            request.setAttribute("message", message);
            e.printStackTrace();
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
     } catch (Exception e){
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            session.setAttribute("sessionOptionList", null);
        }
            conn.closeStatement();
        conn.close();
       
    }
          
    private void debug (int limit, int level, String x) {
        if (limit >= level) {
            System.out.println(x);
        }
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
