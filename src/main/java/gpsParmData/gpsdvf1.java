/*
 * gpsdvf1.java
 *
 * Created on September 27, 2007, 5:20 PM
 */

package gpsParmData;

import OEdatabase.WDSconnect;
import gps.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * 
 * 
 * @author Sauter
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I extract a list of Product Lines
 * from WDS and send them to gpsdvf1.jsp to 
 * get the selected Product Line, Family, Subfamily,
 * and Manufacturer to initiate a Parametric Search by values.
 * 
 * Major changes made
 * 
 * 09/27/2007   DES
 * Modified to use Ajax techniques to build and update
 * dynamic select boxes to collect the
 * product line, family, subfamily and manufacturer data 
 * for use in a subsequent search by parametric values.
 */
public class gpsdvf1 extends HttpServlet {
    
    private boolean debugSw = false;
    private static final String SERVLET_NAME = "gpsdvf1.java";
    private static final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        HttpSession session = request.getSession();         // Get a handle on our session 
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        //debugSw = true;
        
        // Initialize our Session variables for a Search Operation
        
        session.setAttribute("inStockOnly", "Y");
        //session.setAttribute("partNumber", "");
        session.setAttribute("statusMessage", "");
        List <String> previousValues = new ArrayList <String>();
        session.setAttribute("previousValues", null);
        
        // Connect to WDS database
    
        WDSconnect conn = new WDSconnect(); 
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Extract Product Lines from the database
    
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
    
        try {
            request.setAttribute("lines", lines);
            RequestDispatcher view = request.getRequestDispatcher("gpsdvf1.jsp");
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
