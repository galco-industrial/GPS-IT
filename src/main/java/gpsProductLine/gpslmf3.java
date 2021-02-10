/*
 * gpslmf3.java
 *
 * Created on February 7, 2007, 2:29 PM
 */

package gpsProductLine;

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
 *  Modified 4/16/2008 by DES to support 4 divisions
 *
 */
public class gpslmf3 extends HttpServlet {
        
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpslmf3.java";
    private final String VERSION = "1.3.00";

    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        String auditUserID = "";
        boolean completedOK = false;
        int displayOrder;
        int oldDisplayOrder;
        String enableToolTips = "";
        String productLineCode = "";
        String productLineName = "";
        String oldProductLineDivisionCode = "";
        String oldProductLineName = "";
        int j = 0;
        String message = "";
        String queryString = "";
        String SQLCommand = "";
        String work = "";
        ResultSet rs = null;

        /* Get a handle on our session */
        HttpSession session = request.getSession();
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        //debugSw = true;
        
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

            //  Get Initial set up and save in Session variables if we got xtrol from gpslmf2.jsp.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Modify")) {
                // Set our local variables from form vars
                auditUserID = request.getParameter("auditUserID");
                displayOrder = Integer.parseInt(request.getParameter("displayOrder"));
                enableToolTips = request.getParameter("enableToolTips");
                oldDisplayOrder = Integer.parseInt(request.getParameter("oldDisplayOrder"));
                oldProductLineDivisionCode = request.getParameter("oldProductLineDivisionCode");
                oldProductLineName = request.getParameter("oldProductLineName");
                productLineCode = request.getParameter("productLineCode");
                productLineName = request.getParameter("productLineName");
                
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                debug ("Form variables have been processed by gpslmf3.java.");
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
        debug ("Module gpslmf3.java has connected to the database.");
    
        // Make sure the Product Line Code we want to modify did not change somehow
    
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_product_line";
            queryString += " WHERE product_line_code = '" + productLineCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (!rs.next()) {
                    debug ("Error! Product Line Code " + productLineCode + " does not exist!");
                    request.setAttribute("message", "Unusual Error - Product Line Code " + productLineCode + "does not exist.");
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    rs.close();
                    conn.close();
                    return;
                } else {
                    if (!rs.getString("product_line_name").equals(oldProductLineName)
                            || rs.getInt("display_order") != oldDisplayOrder
                            || !(rs.getString("product_line_division").equals(oldProductLineDivisionCode))) {
                        debug ("Error! Product Line Code " + productLineCode + " original value(s) have changed!");
                        debug ("Old Name = " + oldProductLineName + "; new Name = " + rs.getString("product_line_name"));
                        debug ("Old Order = " + oldDisplayOrder + "; new Order = " + rs.getInt("display_order"));
                        debug ("Old Division = " + oldProductLineDivisionCode + "; new Division = " + rs.getString("product_line_division"));
                        request.setAttribute("message", "Unusual Error - Product Line Code " + productLineCode + " original value(s) have changed!");
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        rs.close();
                        conn.close();
                        return;
                    }
                }
                rs.close();
            }
        
            // If we get here, the product line code we are changing has not
            // been modified by anyone else in the mean time.
        
            // Ummmm Note that the Product Line Code record was NOT locked here.
        
            debug ("Attempting to modify Product Line Code " + productLineCode);
            SQLCommand = "UPDATE pub.ps_product_line";
            SQLCommand += " SET product_line_name = '" + productLineName +"'";
            SQLCommand += ", display_order = " + displayOrder;
            SQLCommand += " WHERE product_line_code = '" + productLineCode + "'";
            debug (SQLCommand);
            completedOK = conn.runUpdate(SQLCommand);
            message = "Product Line Code " + productLineCode + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " modified successfully.";
            debug (message);
        
            GPSproductLines productLines = new GPSproductLines();
            if (productLines.open(conn) > -1) {
                ArrayList lines = productLines.getArrayList();
                request.setAttribute("lines", lines);
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpslmf1.jsp");
                view.forward(request,response);
                debug ("Generated updated list of Product Line Codes and re-invoked gpslmf1.jsp.");
            } else {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                debug ("Attempt to generate updated list of Product Line Codes failed.");
            }

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
