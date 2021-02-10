/*
 * gpsdmf1.java
 *
 * Created on September 14, 2007, 8:01 PM
 */

package gpsParmData;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author Sauter
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I set the stage for the user to modify existing parametric data
 * for a part number
 *
 */

public class gpsdmf1 extends HttpServlet {
    
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsdmf1.java";
    private final String VERSION = "1.3.00";
    
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
            
        
        try {
            request.setAttribute("statusMessage", "");
            RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
            view.forward(request,response);

        } catch (Exception e) {
            request.setAttribute("message", "An error occurred in " + SERVLET_NAME + "<br />" + e);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            e.printStackTrace();
            return;
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
