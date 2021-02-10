/*
 * gpsdaf1.java
 *
 * Created on August 24, 2006, 2:47 PM
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
 * @author Sauter
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I build a list of product lines, families, and subfamilies and pass the lists
 * to gpsdaf1.jsp for the user to choose in prep for adding parametric data
 *
 * modified 09/10/2007 by DES to use Ajax techniques to look up family subfamily codes
 * and remember previous selections for repeat operations.
 *
 */
public class gpsdaf1 extends HttpServlet {
        
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsdaf1.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        ResultSet rs = null;
        int j = 0;
    
        HttpSession session = request.getSession();         // Get a handle on our session 
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        //debugSw = true;

        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();                // Connect to WDS database 
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Build queries to extract Product Lines from the database
    
        try {
                        
            // First let's get a list of Product Lines
            
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
                    
            request.setAttribute("lines", lines);
            request.setAttribute("statusMessage", "");
            RequestDispatcher view = request.getRequestDispatcher("gpsdaf1.jsp");
            view.forward(request,response);

        } catch (Exception e) {
            request.setAttribute("message", "An error occurred in " + SERVLET_NAME + "<br />" + e);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            e.printStackTrace();
            conn.close();
            return;
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
        return "I select the Family and Subfamily for which data will be entered";
    }
    // </editor-fold>
}
