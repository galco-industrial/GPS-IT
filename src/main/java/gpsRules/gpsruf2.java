/*
 * gpsruf2.java
 *
 * Created on November 30, 2007, 3:14 PM
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
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I look up the global or local rules for the product line/family/subfamily
 * to be copied
 *
 */
public class gpsruf2 extends HttpServlet {
        
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsruf2.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String auditUserID = "";
        String enableToolTips = "";
        String familyCode = "";
        String familyName = "";
        String productLineCode = "";
        String productLineName = "";
        String subfamilyCode = "";
        String subfamilyName = "";
        String work = "";
        
        GPSfieldSet fieldSet = null;
        GPSrules fieldRules[];                 // Class to create a collection of fields and their rules
        GPSrules ruleSet = null;               // a convenient rules object to point to a fieldRules[] item
        GPSunit units = null;
        /* Get a handle on our session */
        HttpSession session = request.getSession();
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        //debugSw = true;
        
        /* Check for time out   */
        
        if (session.isNew()) {
            response.sendRedirect ("gpstimeout.htm");
            return;
        }
    
        // Check Permissions here *************************
        
        /* Check for invalid Call  i.e., validation key must be set to "OK" */

        work = request.getParameter("validation");
        if (!work.equals("OK")) {
            response.sendRedirect ("gpsnull.htm");
            return;
        }
    
        String b1 = request.getParameter("B1");
        if (b1.equals("Continue")) {
            auditUserID = request.getParameter("auditUserID");
            session.setAttribute("auditUserID", auditUserID);
            enableToolTips = request.getParameter("enableToolTips");
            session.setAttribute("enableToolTips", enableToolTips);
            productLineCode = request.getParameter("productLine");
            session.setAttribute("sbProductLineCode", productLineCode);
            productLineName = request.getParameter("productLineName");
            familyCode = request.getParameter("familyCode");
            session.setAttribute("sbFamilyCode", familyCode);
            familyName = request.getParameter("familyName");
            subfamilyCode = request.getParameter("subfamilyCode");
            session.setAttribute("sbSubfamilyCode", subfamilyCode);
            subfamilyName = request.getParameter("subfamilyName");
        } else {
            response.sendRedirect ("gpsnull.htm");
            return;
        }
    
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        try {
            
            debug("Looking up parametric rules for family/subfamily...");
            fieldSet = new GPSfieldSet();
            fieldRules = fieldSet.getRules(conn, familyCode, subfamilyCode, GPSfieldSet.SEQUENCE_NUMBER_ORDER);
            ArrayList <String> rulesList = new ArrayList <String> ();
            for (int i = 0; i < fieldSet.count(); i++) {
                if (subfamilyCode.equals("*") && fieldRules[i].getRuleScope().equals("G") ) {
                    rulesList.add(fieldRules[i].getArrayListElement());
                } else if (!subfamilyCode.equals("*") && fieldRules[i].getRuleScope().equals("L") ) {
                    rulesList.add(fieldRules[i].getArrayListElement());
                }
            }
            
            request.setAttribute("rulesList", rulesList);
            request.setAttribute("productLineCode", productLineCode);
            request.setAttribute("productLineName", productLineName);
            request.setAttribute("familyCode", familyCode);
            request.setAttribute("familyName", familyName);
            request.setAttribute("subfamilyCode", subfamilyCode);
            request.setAttribute("subfamilyName", subfamilyName);
            request.setAttribute("statusMessage", "");
            RequestDispatcher view = request.getRequestDispatcher("gpsruf2.jsp");
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
