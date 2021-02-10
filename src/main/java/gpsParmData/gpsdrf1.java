/*
 * gpsdrf1.java
 *
 * Created on December 8, 2006, 1:53 PM
 */

package gpsParmData;

import gps.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import sauter.util.*;

/**
 *
 * @author Sauter
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I call a jsp which asks for a PN.
 * The JSP then calls gpsdrf2.java to get the parametric data
 * to display.
 *
 */
public class gpsdrf1 extends HttpServlet {
                
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsdrf1.java";
    private final String VERSION = "1.3.00";
        
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String partNum = "";
        HttpSession session = request.getSession(); // Get a handle on our session
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        //debugSw = true;
        
	if (session.isNew()) {                      // check for timeout
           response.sendRedirect ("gpstimeout.htm");
           return;
        }
        
        //request.setAttribute("close", "0"); // creates a "BACK" button on the JSP form
        //request.setAttribute("partNumber1", partNum);
        //request.setAttribute("partNumber2", "");
        //request.setAttribute("partNumber3", "");
        //request.setAttribute("partNumber4", "");
        //request.setAttribute("partNumber5", "");
        RequestDispatcher view = request.getRequestDispatcher("gpsdrf1.jsp");
        view.forward(request,response);   
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
