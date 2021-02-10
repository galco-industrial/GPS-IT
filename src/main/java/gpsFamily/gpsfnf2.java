/*
 * gpsfnf2.java
 *
 * Created on February 14, 2007, 5:57 PM
 */

package gpsFamily;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
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
 *
 *
 * I renumber the family codes within a Product Line
 *
 */
public class gpsfnf2 extends HttpServlet {
    
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.04";
   
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
        
        String auditUserID = "";
        boolean completedOK = false;
        int startingNumber;
        int incrementBy;
        String enableToolTips = "";
        String familyCode = "";
        int j = 0;
        String message = "";
        String productLineCode = "";
        String productLineCodeSelected = "";
        String queryString = "";
        String SQLCommand = "";
        String work = "";
        ResultSet rs = null;
        GPSproductLines productLines = null;
        GPSfamilyCodes famCodes = null;
    
        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

            //  Get Initial set up and save in Session variables if we got xtrol from gpsfnf2.jsp.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Renumber")) {
                // Set our local variables from form vars
                auditUserID = request.getParameter("auditUserID");
                enableToolTips = request.getParameter("enableToolTips");
                productLineCodeSelected = request.getParameter("productLine");
                //familyCode = request.getParameter("familyCode");
                startingNumber = Integer.parseInt(request.getParameter("startingNumber"));
                incrementBy = Integer.parseInt(request.getParameter("incrementBy"));
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                debug (debugLevel, 8, uStamp + " processed form variables.");
            } else {
                response.sendRedirect ("gpsnull.htm");
                return;
            }
        
        } catch (Exception e){
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
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
        
        // Make sure the Family codes we want to modify did not change somehow
    
        try {
        
            // Here is where we assign family codes with their new numbers
        
            productLines = (GPSproductLines) session.getAttribute("productLines");
            famCodes = (GPSfamilyCodes) session.getAttribute("famCodes");
            debug (debugLevel, 2, uStamp + " Attempting to renumber Family Codes for Product Line " + productLineCodeSelected);
            conn.enableTransactions();
            debug (debugLevel, 4, uStamp + " Transactions have been enabled.");
            for (j=0; j < famCodes.size(); j++) {
                productLineCode = famCodes.getFamilyProductLineCode(j);
                if (productLineCode.equals(productLineCodeSelected)) {
                    SQLCommand = "UPDATE pub.ps_family";
                    SQLCommand += " SET display_order = " + startingNumber;
                    SQLCommand += " WHERE family_code = '" + famCodes.getFamilyCode(j) + "'";
                    debug (debugLevel, 4, uStamp + " executing SQL command:" + SQLCommand);
                    completedOK = conn.runUpdate(SQLCommand);
                    if (!completedOK) {
                        break;
                    }
                    startingNumber += incrementBy;
                }
            }
            message = "Family Codes were";
            if (!completedOK) {
                message += " NOT";
                conn.rollback();
                debug (debugLevel, 2, uStamp + " Transaction was rolled back.");
            } else {
                conn.commit();
                debug (debugLevel, 2, uStamp + " Transaction was committed.");
            }
            message += " renumbered successfully for Product Line " + productLineCodeSelected;
            debug (debugLevel, 2, uStamp + " " + message);
             
            // Get the updated Family Codes
        
            productLines = new GPSproductLines();
            int rc = productLines.open(conn);
            if (rc != 0) {
                sWork = uStamp + " failed to obtain Product Line Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                conn.close();
                return;
            }  
            ArrayList lines = productLines.getArrayList("CP");
            rc = famCodes.open(conn);
            if (rc < 0) {
                sWork = uStamp + " failed to obtain Family Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                famCodes = null;
                conn.close();
                return;                
            }
            ArrayList familyCodes = famCodes.getArrayList();
            request.setAttribute("lines", lines);
            session.setAttribute("productLines", productLines);
            request.setAttribute("familyCodes", familyCodes);
            session.setAttribute("famCodes", famCodes);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsfnf1.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
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
