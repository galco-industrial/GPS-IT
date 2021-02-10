/*
 * gpsrmf2.java
 *
 * Created on November 6, 2007, 4:41 PM
 */

package gpsRules;

import OEdatabase.WDSconnect;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.01
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 * Modified 7/20/2009 by DES to support parmStatus = Active
 * Modified 2.16.2010 by DES to display search and display order
 * I look up the global or local rules for the product line/family/subfamily
 *
 */
public class gpsrmf2 extends HttpServlet {
            
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
        String enableToolTips = "";
        String familyCode = "";
        String familyName = "";
        GPSrules fieldRules[];                 // Class to create a collection of fields and their rules
        GPSfieldSet fieldSet = null;
        String productLineCode = "";
        String productLineName = "";
        GPSrules ruleSet = null;               // a convenient rules object to point to a fieldRules[] item
        String subfamilyCode = "";
        String subfamilyName = "";
        GPSunit units = null;
        String work = "";
        
        /* Check for invalid Call  i.e., validation key must be set to "OK" */

        work = request.getParameter("validation");
        if (!work.equals("OK")) {
            conn.close();
            response.sendRedirect ("gpsnull.htm");
            return;
        }
    
        String b1 = request.getParameter("B1");
        if (b1.equals("Continue")) {
            auditUserID = request.getParameter("auditUserID");
            enableToolTips = request.getParameter("enableToolTips");
            familyCode = request.getParameter("familyCode");
            familyName = request.getParameter("familyName");
            productLineCode = request.getParameter("productLine");
            productLineName = request.getParameter("productLineName");
            subfamilyCode = request.getParameter("subfamilyCode");
            subfamilyName = request.getParameter("subfamilyName");
            
            session.setAttribute("auditUserID", auditUserID);
            session.setAttribute("enableToolTips", enableToolTips);
            session.setAttribute("sbFamilyCode", familyCode);
            session.setAttribute("sbProductLineCode", productLineCode);
            session.setAttribute("sbSubfamilyCode", subfamilyCode);
        } else {
            conn.close();
            response.sendRedirect ("gpsnull.htm");
            return;
        }
    
        try {
            
            debug (debugLevel, 4, uStamp + " Looking up parametric rules for family/subfamily...");
            fieldSet = new GPSfieldSet();
            fieldRules = fieldSet.getRules(conn, familyCode, subfamilyCode, GPSfieldSet.SEQUENCE_NUMBER_ORDER);
            ArrayList <String> rulesList = new ArrayList <String> ();
            for (int i = 0; i < fieldSet.count(); i++) {
                if (subfamilyCode.equals("*") && fieldRules[i].getRuleScope().equals("G") ) {
                    rulesList.add(fieldRules[i].getArrayListElement3());
                } else if (!subfamilyCode.equals("*") && fieldRules[i].getRuleScope().equals("L") ) {
                    rulesList.add(fieldRules[i].getArrayListElement3());
                }
            }

            request.setAttribute("familyCode", familyCode);
            request.setAttribute("familyName", familyName);
            request.setAttribute("productLineCode", productLineCode);
            request.setAttribute("productLineName", productLineName);
            request.setAttribute("rulesList", rulesList);
            request.setAttribute("statusMessage", "");
            request.setAttribute("subfamilyCode", subfamilyCode);
            request.setAttribute("subfamilyName", subfamilyName);
            
            view = request.getRequestDispatcher("gpsrmf2.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + "  <br />" + e);
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
