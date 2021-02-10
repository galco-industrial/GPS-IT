/*
 * gpsscf2.java
 *
 * Created on February 23, 2007, 3:18 PM
 */

package gpsSelectBox;

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
 * I obtain the values from gpsscf1.jsp and create a new Select Box in WDS
 *
 * * 08/13/2007   DES
 * Modified to use Ajax techniques to build and update
 * dynamic select boxes to collect the
 * product line, family, and subfamily data in the
 * creation of a new select box in ps_select_boxes.
 */
public class gpsscf2 extends HttpServlet {
    
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
        boolean completedOK = false;
        int count = 0;
        String dataType = "";
        String enableToolTips = "";
        String familyCode = "";
        String maximum = "";    
        String minimum = "";
        String message = "";
        String optionValue1 = "";
        String optionValue2 = "";
        String productLine = "";
        String queryString = "";
        int rc = 0;
        ResultSet rs = null;
        String selectBoxName = "";
        String SQLCommand = "";
        String subfamilyCode = "";
        String work = "";
        
        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

            //  Get Initial set up and save in Session variables if we got xtrol from gpslcf1.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Create")) {
                // Set our local variables from form vars
                
                auditUserID = request.getParameter("auditUserID");
                dataType = request.getParameter("dataType");
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                maximum = request.getParameter("maximum");
                minimum = request.getParameter("minimum");
                productLine = request.getParameter("productLine");
                selectBoxName = request.getParameter("selectBoxName");
                subfamilyCode = request.getParameter("subfamilyCode");
                
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                session.setAttribute("sbProductLineCode", productLine);
                session.setAttribute("sbFamilyCode", familyCode);
                session.setAttribute("sbName", selectBoxName);
                session.setAttribute("sbSubfamilyCode", subfamilyCode);
                debug (debugLevel, 8, uStamp + " processed form variables.");
            } else {
                conn.close();
                response.sendRedirect ("gpsnull.htm");
                return;
            }
        } catch (Exception e){
            conn.close();
            e.printStackTrace();
            request.setAttribute("message", "An error occurred in module " + SERVLET_NAME);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Make sure the Select Box we want to add did not sneak into database somehow
    
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE select_box_name = '" + selectBoxName + "'";
            queryString += " AND family_code = '" + familyCode + "'";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            queryString += " AND option_index = -1";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    sWork = uStamp + " Error! Select Box " + selectBoxName + " already exists!";
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
                conn.closeStatement();
            }
            debug (debugLevel, 4, uStamp + " Attempting to create Select Box " + selectBoxName 
                    + " for family/subfamily " + familyCode + "/" + subfamilyCode);
            SQLCommand = "INSERT INTO pub.ps_select_boxes";
            SQLCommand += " (family_code, subfamily_code, select_box_name, option_index, option_text, option_dflt, option_value1, option_value2)";
            SQLCommand += " VALUES ( '" + familyCode + "','" + subfamilyCode + "','" 
                    + selectBoxName + "', -1, '" + dataType + "', 0, '" + minimum + "', '" + maximum + "')";
            completedOK = conn.runUpdate(SQLCommand);
            message = " Select Box " + selectBoxName + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " created successfully for family/subfamily " + familyCode + "/" + subfamilyCode;
            debug (debugLevel, 4, uStamp + message);
        
            GPSproductLines productLines = new GPSproductLines();
            rc = productLines.open(conn);
            if (rc != 0) {
                productLines = null;
                conn.close();
                sWork = uStamp + " failed to obtain Product Line Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }   
            ArrayList <String> lines = productLines.getArrayList("CP");
            
            request.setAttribute("lines", lines);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsscf1.jsp");
            view.forward(request,response);
            debug (debugLevel, 4, uStamp + " Generated updated list of Select Boxes and re-invoked gpsscf1.jsp.");
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
