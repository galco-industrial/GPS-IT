/*
 * gpsbnf2.java
 *
 * Created on April 9, 2010, 4:26 PM
 */

package gpsSubfamily;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version
 */
public class gpsbnf2 extends HttpServlet {
    
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.04";
    
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
      
        String familyCode = "";
        String familyName = "";
        String productLineCode = "";
        String productLineName = "";
        int rc = 0;

        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {        
            sWork = uStamp + " failed to connect to WDS database; aborting.";
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        // Build query to extract existing Subfamilies,  Families, and
        // Product Line codes from the database
    
        try {
            productLineCode = request.getParameter("productLine");
            productLineName = request.getParameter("productLineName");
            familyCode = request.getParameter("familyCode");
            familyName = request.getParameter("familyName");
            
                        
            GPSsubfamilyCodes subfamCodes = new GPSsubfamilyCodes();
            rc = subfamCodes.open(conn, familyCode);
            if (rc < 0) {
                subfamCodes = null;
                conn.close();
                sWork = uStamp + " failed to obtain Subfamily Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;                
            }
            ArrayList subfamilyCodes = subfamCodes.getArrayList();

            request.setAttribute("lineCode", productLineCode);
            request.setAttribute("lineName", productLineName);
            request.setAttribute("familyCode", familyCode);
            request.setAttribute("familyName", familyName);
            request.setAttribute("subfamilyCodesList", subfamilyCodes);
            request.setAttribute("statusMessage", "");
            RequestDispatcher view = request.getRequestDispatcher("gpsbnf2.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
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
