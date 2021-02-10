/*
 * gpsunf2.java
 *
 * Created on March 21, 2007, 1:42 PM
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
 * @version 1.3.00
 *
 * I renumber the units; If something unexpected happens, I rollback the operation.
 *
 */
public class gpsunf2 extends HttpServlet {
    
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsunf2.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
                                                        
        /* Get a handle on our session */
        HttpSession session = request.getSession();
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }

        String auditUserID = "";
        boolean completedOK = false;
        int startingNumber;
        int incrementBy;
        String enableToolTips = "";
        int j = 0;
        String message = "";
        String displayUnits;
        String queryString = "";
        String SQLCommand = "";
        String work = "";
        ResultSet rs = null;
        GPSunit unit = null;
    
        // Check Permissions !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        
        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }
    
            /* Check for time out   */

            if (session.isNew()) {
                response.sendRedirect ("gpstimeout.htm");
                return;
            }

            //  Get Initial set up saved in Session variables.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Renumber")) {
                // Set our local variables from form vars
                auditUserID = request.getParameter("auditUserID");
                enableToolTips = request.getParameter("enableToolTips");
                startingNumber = Integer.parseInt(request.getParameter("startingNumber"));
                incrementBy = Integer.parseInt(request.getParameter("incrementBy"));
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                debug ("Form variables have been processed by " + SERVLET_NAME);
            } else {
                response.sendRedirect ("gpsnull.htm");
                return;
            }
        
        } catch (Exception e){
            e.printStackTrace();
            request.setAttribute("message", "An error occurred in module " + SERVLET_NAME);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        debug ("Module " + SERVLET_NAME + " has connected to the database.");
    
        // Make sure the Product Line Code we want to modify did not change somehow
    
        try {
        
            // Here is where we assign new product lines with their new numbers
        
            @SuppressWarnings("unchecked")

            ArrayList <GPSunit> units = (ArrayList <GPSunit>) session.getAttribute("sessionUnits");
            debug ("Attempting to renumber Unit Codes.");
            conn.enableTransactions();
            debug ("Transactions have been enabled.");
            for (j=0; j < units.size(); j++) {
                unit = units.get(j);
                displayUnits = unit.getDisplayUnits();
                SQLCommand = "UPDATE pub.ps_units";
                SQLCommand += " SET display_order = " + startingNumber;
                SQLCommand += " WHERE display_units = '" + displayUnits + "'";
                debug (SQLCommand);
                completedOK = conn.runUpdate(SQLCommand);
                if (!completedOK) {
                    break;
                }
                startingNumber += incrementBy;
            }
            message = "Unit Codes were";
            if (!completedOK) {
                message += " NOT";
                conn.rollback();
                debug ("Transaction was rolled back.");
            } else {
                conn.commit();
                debug ("Transaction was committed.");
            }
            message += " renumbered successfully.";
            debug (message);
             
            // Get the update Units
        
            units = new ArrayList <GPSunit> ();
            ArrayList <String>  unitsList = new ArrayList <String> ();
            String listItem = "";
            queryString = "SELECT base_units, numeric_base, multiplier_base,";
            queryString += " multiplier_exp, multiplier_pre_adjust, multiplier_post_adjust, ";
            queryString += " display_units, display_order";
            queryString += " FROM pub.ps_units";
            queryString += " ORDER BY display_order";
            rs = conn.runQuery(queryString);
            //GPSunit unit = new GPSunit();
            if (rs != null) {
                while (rs.next()) {
                    unit = new GPSunit();
                    unit.setBaseUnits(rs.getString("base_units"));
                    unit.setDisplayOrder(rs.getInt("display_order"));
                    unit.setDisplayUnits(rs.getString("display_units"));
                    unit.setMultiplierBase(rs.getFloat("multiplier_base"));
                    unit.setMultiplierExp(rs.getInt("multiplier_exp"));
                    unit.setMultiplierPreAdjust(rs.getFloat("multiplier_pre_adjust"));
                    unit.setMultiplierPostAdjust(rs.getFloat("multiplier_post_adjust"));
                    unit.setNumericBase(rs.getInt("numeric_base"));
                    units.add(unit);
                    unitsList.add(unit.getArrayListElement());
                    unit = null;
                }
                rs.close();
            }
            session.setAttribute("sessionUnits", units);
            
            request.setAttribute("unitsList", unitsList);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsunf1.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Fatal error when " + SERVLET_NAME + " attempted to talk to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request, response);
            debug ("Fatal error when " + SERVLET_NAME + " attempted to talk to database.");
        } finally {
            conn.close();        
        }    
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
