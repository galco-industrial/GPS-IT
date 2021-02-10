/*
 * getSelectBoxNames.java
 *
 * Created on August 29, 2007, 4:20 PM
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
 * * I return an xml document containing select box names for a given family/subfamily
 *
 */
public class getSelectBoxNames extends HttpServlet {
    
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
    
        String family = "";
        PrintWriter out = null; 
        String queryString = "";
        String result = "";
        ResultSet rs = null;
        String subfamily = "";
    
        // Build query to extract Select Box Names for specified Family/Subfamily 
        // from the database
        
        try {
            response.setContentType("text/xml");
            out = response.getWriter();  
            family = request.getParameter("family");
            subfamily = request.getParameter("subfamily");
            queryString = "SELECT select_box_name ";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE family_code = '" + family + "'";
            queryString += " AND subfamily_code = '" + subfamily + "'";
            queryString += " AND option_index = -1";
            queryString += " ORDER BY select_box_name";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    result += Node.textNode("selectbox", rs.getString("select_box_name"));
                }
            }
            rs.close(); 
            result = Node.XML_HEADER + Node.textNode("selectboxes", result);
            out.println(result);
        } catch (Exception e) {
            debug (debugLevel, 0, uStamp + " Fatal error: " + e);
            e.printStackTrace();
            out.println("Error");         
        } finally {
            out.close();
            conn.closeStatement();         
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
