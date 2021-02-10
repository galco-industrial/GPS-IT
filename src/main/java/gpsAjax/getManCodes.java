/*
 * getManCodes.java
 *
 * Created on September 27, 2007, 7:01 PM
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
 * I return an xml document containing manufacturer codes for a given family/subfamily
 */
public class getManCodes extends HttpServlet {
    
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
            return;
        }
    
        String familyCode = "";
        PrintWriter out = null;
        String result = "";
        String subfamilyCode = "";
        
        // Build query to extract Manufacturer Codes for specified Family/Subfamily 
        // from the database
        
        try {
            response.setContentType("text/xml");
            familyCode = request.getParameter("family");
            subfamilyCode = request.getParameter("subfamily");
            if (familyCode == null || familyCode.equals("")) {         
                debug (debugLevel, 0, uStamp +  " error - missing family code.");
                conn.close();
                return;
            }
            if (subfamilyCode == null || subfamilyCode.equals("")) {         
                debug (debugLevel, 0, uStamp +  " error - missing subfamily code.");
                conn.close();
                return;
            }
        
            out = response.getWriter();
            result = GPSpart.getManCodesXMLList(conn, familyCode, subfamilyCode);
            out.println(result);
            debug (debugLevel, 2, uStamp + " XML result: " + result);
        } catch (Exception e) {
            out.println("error");
            debug (debugLevel, 0, uStamp + " Fatal error: " + e);
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
