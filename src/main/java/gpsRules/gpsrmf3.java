/*
 * gpsrmf3.java
 *
 * Created on November 7, 2007, 2:03 PM
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
 * @version 1.5.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 * Modified 7/20/2009 by DES to support parmStatus = Active
 *  I look up the global or local rules for the product line/family/subfamily
 *
 */
public class gpsrmf3 extends HttpServlet {
            
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

        String dataType = "";
        GPSfamilyCodes famCodes;
        String familyCode = "";
        String familyName = "";
        String familyCodeString = "";
        int hour = 0;
        int iSeqNum = 0;
        GPSproductLines lineCodes;
        String message = "";
        int minute = 0;
        String productLineCode = "";
        String productLineName = "";
        String queryString = "";
        ResultSet rs = null;
        GPSrules ruleSet = null;  // a convenient rules object to point to a fieldRules[] item
        String scopeString = "";
        String subfamilyCode = "";
        String subfamilyName = "";
        String subfamilyCodeString = "";
        String scope = "";
        int second = 0;
        int secondsSinceMidnight = 0;
        String seqNum = "";
        GPSsubfamilyCodes subfamCodes;
        GPSunit units = null;
        String work = "";
        String work2 = "";

        productLineName = request.getParameter("productLine");
        familyCode = request.getParameter("familyCode");
        subfamilyCode = request.getParameter("subfamilyCode");
        scope = request.getParameter("scope");
        seqNum = request.getParameter("seqNum");
        iSeqNum = Integer.parseInt(seqNum);

        // Build query to extract existing Product Line codes 
        // and Family Codes from the database
    
        try {
            ruleSet = (GPSrules) session.getAttribute("sRuleSet");
            debug (debugLevel, 4, uStamp + " Looking up data for the selected parametric rule for family/subfamily...");
            if (!ruleSet.read(conn, familyCode, subfamilyCode, scope, iSeqNum)) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to read RuleSet");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
            famCodes = new GPSfamilyCodes();
            if (famCodes.open(conn) < 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to read Family Codes.");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
            ruleSet.setFamilyName(famCodes.getFamilyName(familyCode));
            productLineCode = famCodes.getFamilyProductLineCode(familyCode);
            lineCodes = new GPSproductLines();
            if (lineCodes.open(conn) < 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to read Product Line Codes.");
                view = request.getRequestDispatcher("showMessage.jsp");
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
                    request.setAttribute("message", uStamp + " failed to read Subfamily Codes.");
                    view = request.getRequestDispatcher("showMessage.jsp");
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
            debug (debugLevel, 4, uStamp + " says DE select box name is '" + ruleSet.getDeSelectBoxName() + "'");

            request.setAttribute("status", ruleSet.getParmStatus());

            // Now serve up the rules for the initial rules screen here
            
            view = request.getRequestDispatcher("gpsrmf3.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + " <br />" + e);
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
