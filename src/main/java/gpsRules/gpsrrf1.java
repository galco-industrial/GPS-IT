/*
 * gpsrrf1.java
 *
 * Created on March 26, 2007, 3:01 PM
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
 * @version 1.5.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I create product line list, family code list, and subfamily list for 
 * a subsequent call to gpsrrf1.jsp
 *
 *
 */

public class gpsrrf1 extends HttpServlet {
            
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
        RequestDispatcher view = null;
        
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
        
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {        
            sWork = uStamp + " failed to connect to WDS database; aborting.";
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
   
        try {
            GPSproductLines productLines = new GPSproductLines();
            if (productLines.open(conn) != 0) {
                conn.close();
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }   
            ArrayList <String> lines = productLines.getArrayList("CP");
            
            GPSfamilyCodes famCodes = new GPSfamilyCodes();
            if (famCodes.open(conn) < 0) {
                conn.close();
                request.setAttribute("message", "Database error when module " + SERVLET_NAME + " attempted to obtain Family Codes.");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;                
            }
            ArrayList <String> familyCodes = famCodes.getArrayList();
            
            GPSsubfamilyCodes subfamCodes = new GPSsubfamilyCodes();
            if (subfamCodes.open(conn) < 0) {
                conn.close();
                request.setAttribute("message", "Database error when module " + SERVLET_NAME + " attempted to obtain Family Codes.");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;                
            }
            ArrayList <String> subfamilyCodes = subfamCodes.getArrayList();
            
            request.setAttribute("subfamilyCodes", subfamilyCodes);            
            request.setAttribute("familyCodes", familyCodes);
            request.setAttribute("lines", lines);  // add product lines array to request object
            request.setAttribute("statusMessage", "");
            view = request.getRequestDispatcher("gpsrrf1.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + "  <br />" + e);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } finally {
            conn.close();
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
