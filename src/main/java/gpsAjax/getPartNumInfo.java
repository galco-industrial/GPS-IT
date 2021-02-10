/*
 * getPartNumInfo.java
 *
 * Created on September 11, 2007, 2:21 PM
 */

package gpsAjax;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;
import XML.util.*;

/**
 *
 * @author Sauter
 * @version
 *
 *
 * I return basic GPS related info from the Part Record
 *
 */



public class getPartNumInfo extends HttpServlet {
    
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
        
        PrintWriter out = null;  
        GPSpart part = null;
        String partNum = "";
        String result = "";
        ResultSet rs = null;
        
        // Build query to look up Part Number 
        // from the database
         
        try {
            response.setContentType("text/xml");
            out = response.getWriter();  
            partNum = request.getParameter("partNum");
            part = new GPSpart();
            if (part.read(conn, partNum)) {
                result = Node.textNode("partNum", part.getPartNum());
                result += Node.textNode("partFamilyCode", part.getFamilyCode());
                result += Node.textNode("partSubfamilyCode", part.getSubfamilyCode());
                result += Node.textNode("partHasPSData", part.getHasPSData() ? "true" : "false");
            }
            result = Node.XML_HEADER + Node.textNode("partInfo", result); 
            out.println(result);
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
