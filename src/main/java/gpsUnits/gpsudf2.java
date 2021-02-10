/*
 * gpsudf2.java
 *
 * Created on March 19, 2007, 6:06 PM
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
 * I am called by gpsudf1.jsp when user wants to delete a Unit.
 * I pass existing Units and related details to gpsudf2.jsp
 */
public class gpsudf2 extends HttpServlet {
    
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
        
        String auditUserID = "";
        String displayUnits = "";
        String enableToolTips = "";
        int size = 0;
        String work = "";
        String queryString = "";
        ResultSet rs = null;
        GPSunit unit = null;
        ArrayList unitsList = null;
    
        try {    

            //  Get Initial set up and save in Session variables if we got xtrol from gpslcf1.        
            // Set our local variables from form vars
            displayUnits = request.getParameter("displayUnits");
            enableToolTips = request.getParameter("enableToolTips");
           
            // Update Session variables
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
            unit = new GPSunit();
            queryString = "SELECT base_units, numeric_base, multiplier_base,";
            queryString += " multiplier_exp, multiplier_pre_adjust, multiplier_post_adjust, ";
            queryString += " display_units, display_order";
            queryString += " FROM pub.ps_units";
            queryString += " WHERE display_units = '" + displayUnits + "'";
            debug (debugLevel, 4, uStamp + " Query String is " + queryString);
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    unit.setBaseUnits(rs.getString("base_units"));
                    unit.setDisplayOrder(rs.getInt("display_order"));
                    unit.setDisplayUnits(rs.getString("display_units"));
                    unit.setMultiplierBase(rs.getFloat("multiplier_base"));
                    unit.setMultiplierExp(rs.getInt("multiplier_exp"));
                    unit.setMultiplierPreAdjust(rs.getFloat("multiplier_pre_adjust"));
                    unit.setMultiplierPostAdjust(rs.getFloat("multiplier_post_adjust"));
                    unit.setNumericBase(rs.getInt("numeric_base"));
                } else {
                    sWork = uStamp + " Error! Display Units " + displayUnits + " does not exist!";
                    debug (debugLevel, 0, sWork);
                    request.setAttribute("message", sWork);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    rs.close();
                    conn.closeStatement();
                    conn.close();
                    return;
                }
                rs.close();
            }
            
            // Get references to the display units (if any) from the rules table
            
            ArrayList references = GPSunit.getUnitReferences(conn, displayUnits);
            
            // Add the following dudettes to the Request Object
                        
            request.setAttribute("baseUnits", unit.getBaseUnits());
            request.setAttribute("displayOrder", Integer.toString(unit.getDisplayOrder()));
            request.setAttribute("displayUnits", displayUnits);
            request.setAttribute("multiplierBase", Float.toString(unit.getMultiplierBase()));
            request.setAttribute("multiplierExp", Integer.toString(unit.getMultiplierExp()));
            request.setAttribute("multiplierPreAdjust", Float.toString(unit.getMultiplierPreAdjust()));
            request.setAttribute("multiplierPostAdjust", Float.toString(unit.getMultiplierPostAdjust()));
            request.setAttribute("numericBase", Integer.toString(unit.getNumericBase()));
            request.setAttribute("references", references);
            request.setAttribute("statusMessage", ""); 
            request.setAttribute("unitsList", unitsList); 
            
            // Invoke our JSP
            RequestDispatcher view = null;
            view = request.getRequestDispatcher("gpsudf2.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            session.setAttribute("sessionUnitsList", null);
        } finally {
            conn.closeStatement();
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
