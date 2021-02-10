/*
 * gpsruf3.java
 *
 * Created on November 30, 2007, 3:23 PM
 */

package gpsRules;

import OEdatabase.WDSconnect;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 *  I look up the global or local rules for the product line/family/subfamily
 * to be copied
 *
 */
public class gpsruf3 extends HttpServlet {
        
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsruf3.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        //String auditUserID = "";
        //String enableToolTips = "";
        String dataType = "";
        String familyCode = "";
        String familyName = "";
        String familyCodeString = "";
        int iSeqNum = 0;
        String message = "";
        String productLineCode = "";
        String productLineName = "";
        String queryString = "";
        String scopeString = "";
        String subfamilyCode = "";
        String subfamilyName = "";
        String subfamilyCodeString = "";
        String scope = "";
        String seqNum = "";
        String work = "";
        String work2 = "";
        
        int hour = 0;
        int minute = 0;
        int second = 0;
        int secondsSinceMidnight = 0;
        
        GPSfamilyCodes famCodes;
        GPSproductLines lineCodes;
        GPSrules ruleSet = null;               // a convenient rules object to point to a fieldRules[] item
        GPSsubfamilyCodes subfamCodes;
        GPSunit units = null;
        ResultSet rs = null;
        
        /* Get a handle on our session */
        HttpSession session = request.getSession();
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        debugSw = true;
        
        /* Check for time out   */
        
        if (session.isNew()) {
            response.sendRedirect ("gpstimeout.htm");
            return;
        }
    
        // Check Permissions here *************************
        
        productLineName = request.getParameter("productLine");
        familyCode = request.getParameter("familyCode");
        subfamilyCode = request.getParameter("subfamilyCode");
        scope = request.getParameter("scope");
        seqNum = request.getParameter("seqNum");
        iSeqNum = Integer.parseInt(seqNum);
            
         
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn.close();
            return;
        }
    
        // Build query to extract existing Product Line codes 
        // and Family Codes from the database
    
        try {
            ruleSet = (GPSrules) session.getAttribute("sRuleSet");
            
            debug("Looking up data for the selected parametric ruleset for family/subfamily...");
            
            if (!ruleSet.read(conn, familyCode, subfamilyCode, scope, iSeqNum)) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to read RuleSet");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
            
            famCodes = new GPSfamilyCodes();
            if (famCodes.open(conn) < 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to read Family Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
            ruleSet.setFamilyName(famCodes.getFamilyName(familyCode));
            productLineCode = famCodes.getFamilyProductLineCode(familyCode);
            
            lineCodes = new GPSproductLines();
            if (lineCodes.open(conn) < 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to read Product Line Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
            
            ruleSet.setProductLineCode(productLineCode);
            ruleSet.setProductLineName(lineCodes.getProductLineName(productLineCode));
            lineCodes = null;
            famCodes = null;
            
            if (!subfamilyCode.equals("*")) {
                subfamCodes = new GPSsubfamilyCodes();
                if (subfamCodes.open(conn, familyCode) < 0) {
                    request.setAttribute("message", "Module " + SERVLET_NAME + " failed to read Subfamily Codes.");
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    conn.close();
                    return;
                }
                subfamilyName = subfamCodes.getSubfamilyName(familyCode, subfamilyCode);
                subfamCodes = null;
            } else {
                subfamilyName = "All Subfamilies";
            }
            
            ruleSet.setSubfamilyName(subfamilyName);
            debug (SERVLET_NAME + " says DE select box name is '" + ruleSet.getDeSelectBoxName() + "'");
            
            
            // Now serve up the rules for the initial rules screen here

            RequestDispatcher view = request.getRequestDispatcher("gpsruf3.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + " <br />" + e);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } finally {
            conn.close();
        }
    }
    
    private void debug (String x) {
        if (debugSw) {
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
