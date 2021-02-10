package gpsProductLine;
/*
 * gpsldf2.java
 *
 * Created on February 7, 2007, 4:35 PM
 */

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
 * I obtain info to delete a selected Product Line Code
 *
 *  Modified 4/16/2008 by DES to support 4 divisions
 *
 */
public class gpsldf2 extends HttpServlet {
        
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsldf2.java";
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
        
    // Check Permissions here *************************
    
    int displayOrder;
    String productLineCode;
    String productLineDivisionCode;
    String productLineName;

// Connect to WDS database    
    
     WDSconnect conn = new WDSconnect();
     if (!conn.connect()) {         
         request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
         RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
         view.forward(request,response);
         return;
     }
    
// Build query to extract existing Product Line codes from the database
    
    try {
        GPSproductLines productLines = new GPSproductLines();
        if (productLines.open(conn) > -1) {
            productLineCode = request.getParameter("code");
            productLineName = productLines.getProductLineName(productLineCode);
            displayOrder = productLines.getProductLineCodeDisplayOrder(productLineCode);
            productLineDivisionCode = productLines.getProductLineDivision(productLineCode);
            request.setAttribute("productLineName", productLineName);
            request.setAttribute("productLineDivisionCode", productLineDivisionCode);
            request.setAttribute("displayOrder", Integer.toString(displayOrder));
            request.setAttribute("statusMessage", "");
            RequestDispatcher view = request.getRequestDispatcher("gpsldf2.jsp");
            view.forward(request,response);
        } else {
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        }
    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + ": <br />" + e);
        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
        view.forward(request,response);
    } finally {
        conn.close();
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
