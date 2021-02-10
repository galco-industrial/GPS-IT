/*
 * getFamilyCodes.java
 *
 * Created on August 14, 2007, 4:30 PM
 *
 * modified 08/19/2016 by DES to support * All Family Codes (ECP-1)
 *
 */

package gpsAjax;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version
 *
 * I return an xml document containing family codes for a given product line
 *
 */
public class getFamilyCodes extends HttpServlet {
    
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.6.00";
    
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
            return;
        }
    
        // Build query to extract Family Codes for specified Product Line 
        // from the database
        
        GPSfamilyCodes famCodes = null;
        String message = "";
        PrintWriter out = null;
        String productLine = "";
        int rc = 0;
        String result = "";
                
        try {
            response.setContentType("text/xml");
            out = response.getWriter();
            famCodes = new GPSfamilyCodes();
            productLine = request.getParameter("productLine");
            rc = famCodes.open(conn, productLine);
            if (rc < 0 ) {
                message = " database error " + rc;
                debug (debugLevel, 0, uStamp + message);
                out.println("");
                out.close();
                conn.close();
                return;
            }
            result = famCodes.getXMLList2(productLine);  // ECP-1
            out.println(result);
            out.close();
        } catch (Exception e) {
            message = " fatal error error " + e;
            debug (debugLevel, 0, uStamp + message);
            e.printStackTrace();
        } finally {
            out.close();
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
