/*
 * gpsdsf1.java
 *
 * Created on September 8, 2016, 2:36 PM
 *
 * I will attempt to gracefully shut down a running
 * Build Option Lists instance for Galco web landing pages.
 *
 * NOTE that I invoke gpsdsf1.jsp which then invokes gpsdbf3.java.
 *
 */

package gpsParmData;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.1.00
 *
 * I set up a jsp to abort a currently executing gpsdbf3.java module.
 *
 */
public class gpsdsf1 extends HttpServlet {
    
private final int DEBUGLEVEL = 5;
    private final String SERVLET_NAME = "gpsdsf1.java";
    private final String VERSION = "1.1.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
         
        /* Get a handle on our session */
        HttpSession session = request.getSession();
        boolean debugSw = false;
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
  
        try {
            request.setAttribute("statusMessage", "");
            RequestDispatcher view = request.getRequestDispatcher("gpsdsf1.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + ": <br />" + e);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        }
    }
        
    private void debug (int level, String x) {
        if (DEBUGLEVEL >= level) {
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
