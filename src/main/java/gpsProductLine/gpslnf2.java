/*
 * gpslnf2.java
 *
 * Created on February 8, 2007, 6:31 PM
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
 * I obtain info to delete a selected Product Line Code
 *
 *  Modified 4/21/2008 by DES to support 4 divisions
 *
 */
public class gpslnf2 extends HttpServlet {
        
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpslnf2.java";
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
        String productLineCode;
        String queryString = "";
        String SQLCommand = "";
        String work = "";
        ResultSet rs = null;
        GPSproductLines productLines = null;
    
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
        
            productLines = (GPSproductLines) session.getAttribute("productLines");
            debug ("Attempting to renumber Product Line Codes.");
            conn.enableTransactions();
            debug ("Transactions have been enabled.");
            for (j=0; j < productLines.size(); j++) {
                productLineCode = productLines.getProductLineCode(j);
                SQLCommand = "UPDATE pub.ps_product_line";
                SQLCommand += " SET display_order = " + startingNumber;
                SQLCommand += " WHERE product_line_code = '" + productLineCode + "'";
                debug (SQLCommand);
                completedOK = conn.runUpdate(SQLCommand);
                if (!completedOK) {
                    break;
                }
                startingNumber += incrementBy;
            }
            message = "Product Line Codes were";
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
             
            // Get the update Product Lines
        
            productLines = null;
            productLines = new GPSproductLines();
            if (productLines.open(conn) > -1) {
                ArrayList lines = productLines.getArrayList();
                request.setAttribute("lines", lines);
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpslnf1.jsp");
                view.forward(request,response);
                debug ("Generated updated list of Product Line Codes and re-invoked gpslnf1.jsp.");
            } else {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                debug ("Attempt to generate updated list of Product Line Codes failed.");
            }
            
            // Ummmm Note that the Product Line Code record was NOT locked here.
        
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
