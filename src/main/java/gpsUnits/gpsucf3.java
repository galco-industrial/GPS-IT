/*
 * gpsucf3.java
 *
 * Created on March 13, 2007, 2:07 PM
 */

package gpsUnits;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 *I accept the infor required to create a new unit. I handle the
 *creation of base units from the gpsucf2.jsp gui and multiplied units from
 *the gpsucf2a.jsp gui
 *
 */
public class gpsucf3 extends HttpServlet {
    
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
        boolean baseUnitFlag = true;
        String baseUnits = "";
        boolean completedOK = false;
        
        int displayOrder = 0;
        String displayUnits = "";
        String enableToolTips = "";
        int j = 0; 
        String listItem = "";
        String message = "";
        float multiplierBase = 0;
        int multiplierExp = 0;
        float multiplierPostAdjust = 0;
        float multiplierPreAdjust = 0;
        int numericBase = 0;
        String queryString = "";
        ResultSet rs = null;
        String SQLCommand = "";
        GPSunit unit = null;
        ArrayList <String> unitsList = null;
        String work = "";
                
        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

            String b1 = request.getParameter("B1");
            if (b1.equals("Create")) {
                // Set our local variables from form vars
                //auditUserID = request.getParameter("auditUserID");
                enableToolTips = request.getParameter("enableToolTips");
                baseUnits = request.getParameter("baseUnits");
                displayOrder = Integer.parseInt(request.getParameter("displayOrder"));
                displayUnits = request.getParameter("displayUnits");
                multiplierBase = Float.parseFloat(request.getParameter("multiplierBase"));
                multiplierExp = Integer.parseInt(request.getParameter("multiplierExp"));
                multiplierPreAdjust = Float.parseFloat(request.getParameter("multiplierPreAdjust"));
                multiplierPostAdjust = Float.parseFloat(request.getParameter("multiplierPostAdjust"));
                numericBase = Integer.parseInt(request.getParameter("numericBase"));
                                                
                //  Set/Update session vars
                session.setAttribute("enableToolTips", enableToolTips);
                debug (debugLevel, 4, uStamp + " processed form variables.");
            } else {
                response.sendRedirect ("gpsnull.htm");
                return;
            }
        } catch (Exception e){
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Make sure the unit we want to add did not sneak into database somehow
    
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_units";
            queryString += " WHERE display_units = '" + displayUnits + "'";
            debug (debugLevel, 4, uStamp + " Query String is " + queryString);
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    sWork = uStamp + " Error! Unit " + displayUnits + " already exists!";
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
            
            // If this is a multiplier unit
            // make sure the base unit exists    
        
            if (!displayUnits.equals(baseUnits)) {
                baseUnitFlag = false;
                queryString = "SELECT *";
                queryString += " FROM pub.ps_units";
                queryString += " WHERE display_units = '" + baseUnits + "'";
                queryString += " AND base_units = '" + baseUnits + "'";
                debug (debugLevel, 4, uStamp + " Query String is " + queryString);
                rs = conn.runQuery(queryString);
                if (rs != null) {
                    if (!rs.next()) {
                        sWork = uStamp + " Error! Base Unit " + baseUnits + " does not exist!";
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
                debug (debugLevel, 4, uStamp + " Base Unit " + baseUnits + " exists for new unit " + displayUnits);
            }
            
            if (baseUnitFlag) {
                debug (debugLevel, 4, uStamp + " Attempting to add new base unit " + displayUnits);
            } else {
                debug (debugLevel, 4, uStamp + " Attempting to add new unit " + displayUnits);
            }
            SQLCommand = "INSERT INTO pub.ps_units";
            SQLCommand += " (display_units, base_units, display_order, numeric_base, ";
            SQLCommand += " multiplier_base, multiplier_exp, multiplier_pre_adjust, multiplier_post_adjust)";
            SQLCommand += " VALUES ( '" + displayUnits + "','" + baseUnits + "', " + displayOrder + ", "
                    + numericBase + ", " + multiplierBase + ", " + multiplierExp + ", " 
                    + multiplierPreAdjust + ", " + multiplierPostAdjust + ")";
            debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
            completedOK = conn.runUpdate(SQLCommand);
            message = " Unit " + displayUnits + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " created successfully.";
            debug (debugLevel, 2, uStamp + message);
        
            // Get the units for re-display
            // Get a new List
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
            
            session.setAttribute("sessionUnitsList", unitsList);
            // Add the following dudettes to the Request Object
            
            request.setAttribute("baseUnits", baseUnits);
            request.setAttribute("statusMessage", message); 
            request.setAttribute("unitsList", unitsList); 
            
            // Invoke our JSP
            RequestDispatcher view = null;
            if (baseUnitFlag) {
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
