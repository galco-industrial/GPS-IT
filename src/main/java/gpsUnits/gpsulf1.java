/*
 * gpsulf1.java
 *
 * Created on March 7, 2007, 4:13 PM
 */

package gpsUnits;

import OEdatabase.WDSconnect;
import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 * I extract the records from the ps_units table for listing by gpsulf1.jsp
 *
 */
public class gpsulf1 extends HttpServlet {
    
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
           
        String baseUnits = "";
        int displayOrder = 0;
        String displayUnits = "";
        int i = 0;
        String listItem = "";
        String message = "";
        float multiplierBase = 0;
        int multiplierExp = 0;
        float multiplierPostAdjust = 0;
        float multiplierPreAdjust = 0;
        int numericBase = 0;
        String queryString = "";
        ResultSet rs = null;
        GPSunit unit = null;
        ArrayList <String> unitsList = null;
          
        // Build query to extract existing Units records from the database
    
        try {
            unitsList = new ArrayList <String> ();
            unit = new GPSunit();
            queryString = "SELECT base_units, numeric_base, multiplier_base,";
            queryString += " multiplier_exp, multiplier_pre_adjust, multiplier_post_adjust, ";
            queryString += " display_units, display_order";
            queryString += " FROM pub.ps_units";
            queryString += " ORDER BY display_order";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    unit.setBaseUnits(rs.getString("base_units"));
                    unit.setDisplayOrder(rs.getInt("display_order"));
                    unit.setDisplayUnits(rs.getString("display_units"));
                    unit.setMultiplierBase(rs.getFloat("multiplier_base"));
                    unit.setMultiplierExp(rs.getInt("multiplier_exp"));
                    unit.setMultiplierPreAdjust(rs.getFloat("multiplier_pre_adjust"));
                    unit.setMultiplierPostAdjust(rs.getFloat("multiplier_post_adjust"));
                    unit.setNumericBase(rs.getInt("numeric_base"));
                    unitsList.add(unit.getArrayListElement());
                }
                rs.close();
            }
                       
            request.setAttribute("statusMessage", message);
            request.setAttribute("unitsList", unitsList);
            RequestDispatcher view = request.getRequestDispatcher("gpsulf1.jsp");
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
