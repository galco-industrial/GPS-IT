/*
 * gpsucf2.java
 *
 * Created on March 9, 2007, 4:36 PM
 */

package gpsUnits;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 * I am called by gpsucf1.jsp when user selects a Base Unit box and wished to create a new Unit.
 * I pass existing Units and related details to gpsucf2.jsp
 */

public class gpsucf2 extends HttpServlet {
    
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
        
        
        String auditUserID = "";
        String baseUnits = "";
        String enableToolTips = "";
        int size = 0;
        ArrayList unitsList = null;
        String work = "";
    
        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

            //  Get Initial set up and save in Session variables if we got xtrol from gpslcf1.        
        
            // Set our local variables from form vars
            auditUserID = request.getParameter("auditUserID");
            enableToolTips = request.getParameter("enableToolTips");
            baseUnits = request.getParameter("baseUnits");
           
            // Update Session variables
            session.setAttribute("auditUserID", auditUserID);
            session.setAttribute("enableToolTips", enableToolTips);
            debug (debugLevel, 4, uStamp + " processed form variables.");
        } catch (Exception e){
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        try {
            
            // Get the Original List
            unitsList = (ArrayList) session.getAttribute("sessionUnitsList");

            // Add the following dudettes to the Request Object
            
            request.setAttribute("baseUnits", baseUnits);
            request.setAttribute("statusMessage", ""); 
            request.setAttribute("unitsList", unitsList); 
            
            // Invoke our JSP
            RequestDispatcher view = null;
            if (baseUnits.equals("*new*")) {
                view = request.getRequestDispatcher("gpsucf2.jsp");
            } else {
                view = request.getRequestDispatcher("gpsucf2a.jsp");
            }
            view.forward(request,response);
        } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            session.setAttribute("sessionUnitsList", null);
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
