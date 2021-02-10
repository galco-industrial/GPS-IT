/*
 * gpsrmf7.java
 *
 * Created on November 26, 2007, 3:23 PM
 */

package gpsRules;

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
 * Modified 7/20/2009 by DES to support parmStatus = Active
 * My job is to write the modified rule back to the database.
 *
 */
public class gpsrmf7 extends HttpServlet {
            
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
        
        String auditUserID = "";
        boolean completionCode;
        String dataType = "";
        String familyCode = "";
        int openError;
        String queryString = "";
        String resultMsg = "";
        GPSrules ruleSet;
        String seqNumString = "";
        String status = "";
        String subfamilyCode = "";
        String work = "";

        try {    
            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
            }

            session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
            status = request.getParameter("status");
            auditUserID = (String) session.getAttribute("auditUserID");
            ruleSet = (GPSrules) session.getAttribute("sRuleSet");
    
            seqNumString = Integer.toString(ruleSet.getSeqNum());
            familyCode = ruleSet.getFamilyCode();
            subfamilyCode = ruleSet.getSubfamilyCode();

            // first delete the old row
            
            completionCode = ruleSet.deleteRules(conn, familyCode, subfamilyCode, seqNumString, "D", auditUserID);
            debug (debugLevel, 4, uStamp + " Delete completion code is " + completionCode);
            if (!completionCode) {
                resultMsg = " Something ugly happened when I tried to delete the old Rules.";
            } else {
                completionCode = ruleSet.writeRules(conn, familyCode, subfamilyCode, seqNumString, "U", auditUserID);
                debug (debugLevel, 4, uStamp + " Update completion code is " + completionCode);
                if (!completionCode) {
                    resultMsg = " Something ugly happened when I tried to write the modified Rules.";
                } else {
                    resultMsg = " Rules have been updated successfully!";
                }
            }
            request.setAttribute("message", resultMsg);
            request.setAttribute("status", status);
            view = request.getRequestDispatcher("gpsrmf7.jsp");
            view.forward(request,response);
        } catch (Exception e){
            resultMsg = "An error occurred in " + SERVLET_NAME + "<br />" + e;
            debug (debugLevel, 0, uStamp + resultMsg);
            e.printStackTrace();
            request.setAttribute("message", resultMsg);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } finally {
            if (conn != null) {
                conn.close();
            }
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
