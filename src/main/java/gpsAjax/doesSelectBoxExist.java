/*
 * doesSelectBoxExist.java
 *
 * Created on August 21, 2007, 2:39 PM
 */

package gpsAjax;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;


/**
 *
 * @author Sauter
 * @version
 *
 * If a Select box exists I return a "true" else I return a "false"
 *
 */
public class doesSelectBoxExist extends HttpServlet {
    
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
        String name = "";
        PrintWriter out = response.getWriter();  
        String queryString = "";
        String result = "error";
        ResultSet rs = null;
        String selectBoxName = "";
        String subfamily = "";
 
        // Build query to look up Select Box Name 
        // from the database
         
        try {
            response.setContentType("text/xml");
            family = request.getParameter("family");
            subfamily = request.getParameter("subfamily");
            name = request.getParameter("name"); // Select Box Name
            selectBoxName = "";
            queryString = "SELECT select_box_name ";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE select_box_name = '" + name + "'";
            queryString += " AND family_code = '" + family + "'";
            queryString += " AND subfamily_code = '" + subfamily + "'";
            queryString += " AND option_index = -1";
                
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    selectBoxName = rs.getString("select_box_name");
                }
            }
            rs.close();
            if (selectBoxName.equals(name)) {
                result = "true";
            } else {
                result = "false";
            }
            out.println(result);
        } catch (Exception e) {
            out.println("error");
            debug (debugLevel, 0, uStamp + " Fatal error: " + e);
            e.printStackTrace();
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
