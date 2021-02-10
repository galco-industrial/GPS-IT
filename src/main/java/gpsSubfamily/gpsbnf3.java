/*
 * gpsbnf3.java
 *
 * Created on February 22, 2007, 2:52 PM
 */

package gpsSubfamily;

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
 * @version 1.5.03
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 * Modified 6/09/2009 by DES to support alt/keywords/buys and index fields
 *
 * I renumber the subfamilies within a family
 *
 */
public class gpsbnf3 extends HttpServlet {
    
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
        boolean completedOK = false;
        int startingNumber;
        int incrementBy;
        String enableToolTips = "";
        String subfamilyCode = "";
        int j = 0;
        String message = "";
        String familyCode = "";
        String familyCodeSelected = "";
        String queryString = "";
        int rc = 0;
        String SQLCommand = "";
        String work = "";
        ResultSet rs = null;
        GPSsubfamilyCodes subfamCodes = null;
        
        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

            //  Get Initial set up and save in Session variables if we got xtrol from gpsbnf1.jsp.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Renumber")) {
                // Set our local variables from form vars
                auditUserID = request.getParameter("auditUserID");
                enableToolTips = request.getParameter("enableToolTips");
                familyCodeSelected = request.getParameter("familyCode");
                startingNumber = Integer.parseInt(request.getParameter("startingNumber"));
                incrementBy = Integer.parseInt(request.getParameter("incrementBy"));
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                debug (debugLevel, 8, uStamp + " processed form variables.");
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
    
        try {
        
            // Here is where we assign subfamily codes with their new numbers
        
            subfamCodes = new GPSsubfamilyCodes();
            rc = subfamCodes.open(conn);
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
            debug (debugLevel, 4, uStamp + " Attempting to renumber Subfamily Codes for Family " + familyCodeSelected);
            conn.enableTransactions();
            debug (debugLevel, 8, uStamp + " Transactions have been enabled.");
            for (j=0; j < subfamCodes.size(); j++) {
                familyCode = subfamCodes.getFamilyCode(j);
                if (familyCode.equals(familyCodeSelected)) {
                    subfamilyCode = subfamCodes.getSubfamilyCode(j);
                    SQLCommand = "UPDATE pub.ps_subfamily";
                    SQLCommand += " SET display_order = " + startingNumber;
                    SQLCommand += " WHERE family_code = '" + familyCode + "'";
                    SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
                    debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
                    completedOK = conn.runUpdate(SQLCommand);
                    if (!completedOK) {
                        break;
                    }
                    startingNumber += incrementBy;
                }
            }
            message = " Subfamily Codes were";
            if (!completedOK) {
                message += " NOT";
                conn.rollback();
                debug (debugLevel, 8, uStamp + " Transaction was rolled back.");
            } else {
                conn.commit();
                debug (debugLevel, 8, uStamp + " Transaction was committed.");
            }
            message += " renumbered successfully for Family Code " + familyCodeSelected;
            debug (debugLevel, 8, uStamp + message);
             
            // Get the updated Family/Subfamily Codes
        
            // GPSsubfamilyCodes subfamCodes = new GPSsubfamilyCodes();
            /*
             if (subfamCodes.open(conn) < 0) {
                request.setAttribute("message", "Database error when module" + SERVLET_NAME + " attempted to obtain Subfamily Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                session.setAttribute("sessionProductLines", null);
                session.setAttribute("sessionFamCodes", null);
                session.setAttribute("sessionSubfamCodes", null);
                subfamCodes = null;
                conn.close();
                return;                
            }
            GPSfamilyCodes famCodes = new GPSfamilyCodes();
            if (famCodes.open(conn) < 0) {
                request.setAttribute("message", "Database error when module " + SERVLET_NAME + " attempted to obtain Family Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                session.setAttribute("sessionProductLines", null);
                session.setAttribute("sessionFamCodes", null);
                session.setAttribute("sessionSubfamCodes", null);
                subfamCodes = null;
                famCodes = null;
                conn.close();
                return;                
            }
             */
            GPSproductLines productLines = new GPSproductLines();
            if (productLines.open(conn) != 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                //subfamCodes = null;
                //famCodes = null;
                conn.close();
                return;
            }   
            //ArrayList subfamilyCodesList = subfamCodes.getArrayList();
            //ArrayList familyCodesList = famCodes.getArrayList();
            ArrayList <String> productLinesList = productLines.getArrayList("CP");
            request.setAttribute("lines", productLinesList);
            //request.setAttribute("familyCodesList", familyCodesList);
            //request.setAttribute("subfamilyCodesList", subfamilyCodesList);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsbnf1.jsp");
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
