/*
 * gpsruf6.java
 *
 * Created on December 5, 2007, 3:19 PM
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
 * I am used to copy a rule set to a new field in a family / subfamily. 
 * I get a list of product lines and xfer to
 * gpsruf6.jsp to get the family, subfamily (scope), and rule set number
 * to be created from the source rules as modified.
 *
 */
public class gpsruf6 extends HttpServlet {
    
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsruf6.java";
    private final String VERSION = "1.3.00";
    
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
        //debugSw = true;
    
        // Check Permissions here *************************
    
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Build query to extract existing Product Line codes 
        // from the database
    
        try {
            // Initialize our Session for creating a new rule from the old rule
           
            // GPSrules ruleSet = (GPSrules) session.getAttribute("sRuleSet");
            
            GPSproductLines productLines = new GPSproductLines();
            if (productLines.open(conn) != 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                conn.close();
                return;
            }   
            ArrayList <String> lines = productLines.getArrayList("CP");
            request.setAttribute("lines", lines);  // add product lines array to request object
            request.setAttribute("statusMessage", "");
            RequestDispatcher view = request.getRequestDispatcher("gpsruf6.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + "  <br />" + e);
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
