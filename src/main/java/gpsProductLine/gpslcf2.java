/*
 * gpslcf2.java
 *
 * Created on February 1, 2007, 5:40 PM
 */

package gpsProductLine;

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
 * @version 1.3.00
 *
 * Modified 2008/04/16 by DES to support division codes within Product Line
 *
 */
public class gpslcf2 extends HttpServlet {
        
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpslcf2.java";
    private final String VERSION = "1.3.00";
    
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        String auditUserID = "";
        boolean completedOK = false;
        int displayOrder = 0;
        String enableToolTips = "";
        String productLineCode = "";
        String productLineDivision = "";
        String productLineName = "";
        int j = 0;
        String message = "";
        String queryString = "";
        String SQLCommand = "";
        String work = "";
        ResultSet rs = null;
    
        /* Get a handle on our session */
        HttpSession session = request.getSession();
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        //debugSw = true;
        
        // Check Permissions !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        
        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }
    
            /* Check for time out   */

            if (session.isNew()) {
                response.sendRedirect ("gpstimeout.htm");
                return;
            }

            //  Get Initial set up and save in Session variables if we got xtrol from gpslcf1.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Create")) {
                // Set our local variables from form vars
                auditUserID = request.getParameter("auditUserID");
                displayOrder = Integer.parseInt(request.getParameter("displayOrder"));
                enableToolTips = request.getParameter("enableToolTips");
                productLineCode = request.getParameter("productLineCode");
                productLineDivision = request.getParameter("productLineDivision");
                productLineName = request.getParameter("productLineName");
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                debug ("Form variables have been processed by " + SERVLET_NAME);
            } else {
                response.sendRedirect ("gpsnull.htm");
                return;
            }
        } catch (Exception e){
            e.printStackTrace();
            request.setAttribute("message", "An error occurred in module " + SERVLET_NAME);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        work = productLineDivision;
        if (work.length() != 2 || !("CP,DR,FS,ES".contains(work))) {
            message = "Fatal Error! Module " + SERVLET_NAME 
                + " found invalid division code:  " + work;
            debug (message);
            request.setAttribute("message", message);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
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
        debug ("Module " + SERVLET_NAME + " has connected to the database.");
    
        // Make sure the Product Line Code we want to add did not sneak into database somehow
    
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_product_line";
            queryString += " WHERE product_line_code = '" + productLineCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    debug ("Error! Product Line Code " + productLineCode + " already exists!");
                    request.setAttribute("message", "Unusual Error - Product Line Code " + productLineCode + "already exists.");
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    rs.close();
                    conn.close();
                    return;
                }
                rs.close();
            }
            debug ("Attempting to add Product Line Code " + productLineCode);
            SQLCommand = "INSERT INTO pub.ps_product_line";
            SQLCommand += " (product_line_code, product_line_name, product_line_division, display_order)";
            SQLCommand += " VALUES ( '" + productLineCode + "','" + productLineName + "','" + productLineDivision + "', " + displayOrder + ")";
            debug (SQLCommand);
            completedOK = conn.runUpdate(SQLCommand);
            message = "Product Line Code " + productLineCode + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " created successfully.";
            debug (message);
        
            GPSproductLines productLines = new GPSproductLines();
            if (productLines.open(conn) > -1) {
                ArrayList lines = productLines.getArrayList();
                request.setAttribute("lines", lines);
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpslcf1.jsp");
                view.forward(request,response);
                debug ("Generated updated list of Product Line Codes and re-invoked gpslcf1.jsp.");
            } else {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                debug ("Attempt to generate updated list of Product Line Codes failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Fatal error when " + SERVLET_NAME + " attempted to talk to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request, response);
            debug ("Fatal error when " + SERVLET_NAME + " attempted to talk to database.");
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
