/*
 * sendToClipBoard.java
 *
 * Created on April 2, 2008, 3:48 PM
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
 * *
 * If I successfully write the data to the clip board I return a "true" else I return a "false"
 *
 */
public class sendToClipBoard extends HttpServlet {
    
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
        
        String auditUserID = "";
        boolean completedOK = true;
        String key = "";
        PrintWriter out = null;  
        String queryString = "";
        String result = "error";
        ResultSet rs = null;
        String SQLCommand = "";
        String value = "";
        
        // Build query to look up Part Number 
        // from the database
         
        try {
            response.setContentType("text/xml");
            out = response.getWriter();  
            auditUserID = request.getParameter("auditUserID");
            key = request.getParameter("key");
            value = request.getParameter("value");
            if (auditUserID==null || auditUserID.length() == 0 ||
                    key==null || key.length() == 0 || value==null) {
                out.println(result);
                out.close();
                conn.close();
                return;
            }
            queryString = "SELECT value ";
            queryString += " FROM pub.ps_clip_board";
            queryString += " WHERE user_id = '" + auditUserID + "'";
            queryString += " AND key = '" + key + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                // Delete entry if it already exists
                if (rs.next()) {
                    SQLCommand = "DELETE FROM pub.ps_clip_board";
                    SQLCommand += " WHERE audit_userid = '" + auditUserID + "'";
                    SQLCommand += " AND key = '" + key + "'";
                    completedOK = conn.runUpdate(SQLCommand);
                } 
                rs.close();
            }
            if (completedOK) {
                SQLCommand = "INSERT INTO pub.ps_clip_board";
                SQLCommand += " (audit_userid, key, value1)";
                SQLCommand += " VALUES ( '" + auditUserID + "','" + key + "', '" + value + ")";
                completedOK = conn.runUpdate(SQLCommand);                
            }
            result = completedOK ? "true" : "false"; 
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
