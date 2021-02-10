/*
 * gpsumf3.java
 *
 * Created on March 19, 2007, 2:14 PM
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
 * *
 * I am called by gpsumf2.jsp when user wants to modify a BaseUnit.
 * I am called by gpsumf2a.jsp when user wants to modify a multiplier Unit.
 * I make the required changes in the WDS ps_units table.
 *
 */
public class gpsumf3 extends HttpServlet {
    
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
        String baseUnits = "";
        boolean baseUnitSw = false;
        boolean completedOK = false;
        String displayOrder = "";
        String displayUnits = "";
        String enableToolTips = "";
        int j = 0;
        String listItem = "";
        String message = "";
        String multiplierBase = "";
        String multiplierExp = "";
        String multiplierPostAdjust = "";
        String multiplierPreAdjust = "";
        String numericBase = "";
        String oldDisplayOrder = "";
        String oldDisplayUnits = "";
        String oldMultiplierBase = "";
        String oldMultiplierExp = "";
        String oldMultiplierPostAdjust = "";
        String oldMultiplierPreAdjust = "";
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

            //  Get Initial set up and save in Session variables if we got xtrol from gpslmf2.jsp.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Modify")) {
                // Set our local variables from form vars
                auditUserID = request.getParameter("auditUserID");
                baseUnits = request.getParameter("baseUnits");
                baseUnitSw = baseUnits.equals(oldDisplayUnits);
                displayOrder = request.getParameter("displayOrder");
                displayUnits = request.getParameter("displayUnits");
                enableToolTips = request.getParameter("enableToolTips");
                multiplierBase = request.getParameter("multiplierBase");
                multiplierExp = request.getParameter("multiplierExp");
                multiplierPreAdjust = request.getParameter("multiplierPreAdjust");
                multiplierPostAdjust = request.getParameter("multiplierPostAdjust");
                numericBase = request.getParameter("numericBase");
                oldDisplayOrder = request.getParameter("oldDisplayOrder");
                oldDisplayUnits = request.getParameter("oldDisplayUnits");
                oldMultiplierBase = request.getParameter("oldMultiplierBase");
                oldMultiplierExp = request.getParameter("oldMultiplierExp");
                oldMultiplierPreAdjust = request.getParameter("oldMultiplierPreAdjust");
                oldMultiplierPostAdjust = request.getParameter("oldMultiplierPostAdjust");
                
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
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
    
        // Make sure the Option we want to modify did not change somehow
    
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_units";
            queryString += " WHERE base_units = '" + baseUnits + "'";
            queryString += " AND display_units = '" + oldDisplayUnits + "'";
            queryString += " AND display_order = " + oldDisplayOrder;
            queryString += " AND numeric_base = " + numericBase;
            if (!baseUnitSw) {
                queryString += " AND multiplier_base = " + oldMultiplierBase;
                queryString += " AND multiplier_exp = " + oldMultiplierExp;
                queryString += " AND multiplier_pre_adjust = " + oldMultiplierPreAdjust;
                queryString += " AND multiplier_post_adjust = " + oldMultiplierPostAdjust;
            }
            debug (debugLevel, 4, uStamp + " Query String is " + queryString);
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (!rs.next()) {
                    sWork = uStamp + " Error! old Unit '" + oldDisplayUnits + "' has changed.";
                    debug (debugLevel, 0, sWork);
                    request.setAttribute("message", sWork);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    conn.closeStatement();
                    conn.close();
                    return;
                }
                rs.close();
                conn.closeStatement();
            }
        
            // If we get here, the Option we are changing has not
            // been modified by anyone else in the mean time.
            
            // Make sure new display unit did not sneak in somehow
            
            if (!oldDisplayUnits.equals(displayUnits)) {
                queryString = "SELECT *";
                queryString += " FROM pub.ps_units";
                queryString += " WHERE display_units = '" + displayUnits + "'";
                debug (debugLevel, 4, uStamp + " Query String is " + queryString);
                rs = conn.runQuery(queryString);
                if (rs != null) {
                    if (rs.next()) {
                        sWork = uStamp + " Error! Unit '" + displayUnits + "' already exists!";
                        debug (debugLevel, 0, sWork);
                        request.setAttribute("message", sWork);
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        conn.closeStatement();
                        conn.close();
                        return;
                    }
                    rs.close();
                    conn.closeStatement();
                }
            }
        
            // Ummmm Note that the Option record was NOT locked here.
        
            debug (debugLevel, 4, uStamp + "Attempting to modify Unit " + oldDisplayUnits);
            SQLCommand = "UPDATE pub.ps_units";
            SQLCommand += " SET display_order = " + displayOrder;
            if (!baseUnitSw) {
                SQLCommand += ", display_units = '" + displayUnits + "'";
                SQLCommand += ", multiplier_base = " + multiplierBase;
                SQLCommand += ", multiplier_exp = " + multiplierExp;
                SQLCommand += ", multiplier_pre_adjust = " + multiplierPreAdjust;
                SQLCommand += ", multiplier_post_adjust = " + multiplierPostAdjust;
            }
            SQLCommand += " WHERE display_units = '" + oldDisplayUnits + "'";
            debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
            completedOK = conn.runUpdate(SQLCommand);
            message = " Unit " + oldDisplayUnits + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " modified successfully.";
            debug (debugLevel, 2, uStamp + message);
               
            unitsList = new ArrayList <String> ();
            unit = new GPSunit();
            queryString = "SELECT base_units, numeric_base, multiplier_base,";
            queryString += " multiplier_exp, multiplier_pre_adjust, multiplier_post_adjust, ";
            queryString += " display_units, display_order";
            queryString += " FROM pub.ps_units";
            queryString += " ORDER BY display_order";
            debug (debugLevel, 4, uStamp + " Query String is " + queryString);
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

            request.setAttribute("statusMessage", message);
            request.setAttribute("unitsList", unitsList);
            RequestDispatcher view = request.getRequestDispatcher("gpsumf1.jsp");
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
