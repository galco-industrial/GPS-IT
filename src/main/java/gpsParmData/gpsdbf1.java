/*
 * gpsdbf1.java
 *
 * Created on March 26, 2009, 2:36 PM
 *
 * I am used to create option list data in the ps_option_lists table
 * for Galco web landing pages in catalog.htm
 *
 * I plow through the catalogitem table and for every part of type Catalog
 * I create filter option list data for the following landing pages:
 * a family level search using global rules (disabled 8/2016)
 * a family/mfgr level search using global rules (disabled 8/2016)
 * a family/subfamily level search using family/subfamily rules
 * a family/subfamily/mfgr level search using family/subfamily rules (8/2016)
 * a family/subfamily/mfgr/series level search using family/subfamily rules (8/2016)
 * NOTE that gpsdbf3.java replaces gpsdbf2.java which is now deprecated.
 */

package gpsParmData;

import OEdatabase.WDSconnect;
import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 * *
 * I extract a list of Product Lines
 * from WDS and send them to a JSP to set up 
 * an operation to create options in ps_option_lists table
 * for Web landing pages.
 *
 * use Ajax techniques to build and update
 * dynamic select boxes to collect the
 * product line and family data in the
 * creation of new option lists data.
 *
 * modified 8/19/2016 by DES to support * wildcard for All Product Lines and families (ECP-1)
 *
 */
public class gpsdbf1 extends HttpServlet {
                
    private boolean debugSw = false;
    private int debugLevel = 5;
    private final String REDIRECT = "index.jsp";
    private final String SERVLET_NAME = "gpsdbf1.java";
    private final String VERSION = "1.4.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
         
        /* Get a handle on our session */
        HttpSession session = request.getSession();
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
    
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        GPSproductLines productLines = new GPSproductLines();
        if (productLines.open(conn) != 0) {
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            productLines = null;
            conn.close();
            return;
        }   
        // ArrayList <String> lines = productLines.getArrayList2();   // ECP-1
        ArrayList <String> lines = productLines.getArrayList();   // ECP-1
        
        try {
            request.setAttribute("statusMessage", "");
            request.setAttribute("lines", lines);
            RequestDispatcher view = request.getRequestDispatcher("gpsdbf1.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + ": <br />" + e);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } finally {
            conn.close();
        }
    }
        
    private void debug (int level, String x) {
        if (debugLevel >= level) {
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
