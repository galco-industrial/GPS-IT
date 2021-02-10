/*
 * gpsocf3.java
 *
 * Created on March 2, 2007, 3:18 PM
 */

package gpsSelectBoxOptions;

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
 *
 * * I accept the info to create a new Select Box Option from gpsocf2.jsp
 * and attempt to create the new Option in WDS
 *
 * * 
 * 09/01/2007   DES
 * Modified to use Ajax techniques to build and update
 * dynamic select boxes to collect the
 * product line, family, and subfamily data in the
 * creation of a new select box options in ps_select_boxes.
 *
 * 07/19/2010 DES modified to support option image field.
 *
 */
public class gpsocf3 extends HttpServlet {
    
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.01";
   
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
        int displayOrder = 0;
        String enableToolTips = "";
        String familyCode = "";
        String familyName = "";
        int j = 0;
        String message = "";
        String oldDefault = "";
        String optionImage = "";
        String optionText = "";
        String optionValue1 = "";
        String optionValue2 = "";
        String optionDefault = "";
        String optionDefaultCode = "0";
        String queryString = "";
        int rc = 0;
        ResultSet rs = null;
        String selectBoxName = "";
        String SQLCommand = "";
        String subfamilyCode = "";
        String subfamilyName = "";
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
                auditUserID = request.getParameter("auditUserID");
                displayOrder = Integer.parseInt(request.getParameter("displayOrder"));
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                familyName = request.getParameter("familyName");
                oldDefault = request.getParameter("oldDefault");
                optionDefault = request.getParameter("optionDefault");
                optionImage = request.getParameter("optionImage");
                optionText = request.getParameter("optionText");
                optionValue1 = request.getParameter("optionValue1");
                optionValue2 = request.getParameter("optionValue1");
                selectBoxName = request.getParameter("selectBoxName");
                subfamilyCode = request.getParameter("subfamilyCode");
                subfamilyName = request.getParameter("subfamilyName");
                if (optionDefault == null) {
                    optionDefault = "";
                }
                if (optionDefault.equals("default")) {
                    optionDefaultCode = "1";
                } else {
                    optionDefaultCode = "0";
                }
                                                                
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);

                debug (debugLevel, 4, uStamp + " processed form variables.");
            } else {
                response.sendRedirect ("gpsnull.htm");
                return;
            }
        } catch (Exception e){
            conn.close();
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    
        // Make sure the OptionText or OptionValue1 we want to add did not sneak into database somehow
    
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE select_box_name = '" + selectBoxName + "'";
            queryString += " AND family_code = '" + familyCode + "'";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            queryString += " AND option_index != -1";
            queryString += " AND ( option_text = '" + optionText + "'";
            queryString += " OR option_value1 = '" + optionValue1 + "')";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (rs.next()) {
                    rs.close();
                    conn.closeStatement();
                    conn.close();
                    message = " Error! Option Text " + optionText + 
                            " or Option Value 1 " + optionValue1 + " already exists for family "
                            + familyName + " subfamily " + subfamilyName + "!";
                    debug (debugLevel, 0, uStamp + message);
                    request.setAttribute("message", message);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    return;
                }
                rs.close();
            }
            
            if (optionDefaultCode.equals("1")) {
                debug (debugLevel, 4, uStamp + " First I am turning off any pre-existing defaults.");
                SQLCommand = "UPDATE pub.ps_select_boxes";
                SQLCommand += " SET option_dflt = 0";
                SQLCommand += " WHERE select_box_name = '" + selectBoxName + "'";
                SQLCommand += " AND family_code = '" + familyCode + "'";
                SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
                SQLCommand += " AND option_dflt = 1";
                debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
                completedOK = conn.runUpdate(SQLCommand);     
                if (completedOK) {
                    debug (debugLevel, 4, uStamp + " Defaults were initialized successfully.");
                } else {
                    debug (debugLevel, 0, uStamp + " Error when attempting to initialize the defaults for Select Box "
                            + familyCode + "/" + subfamilyCode + "/" + selectBoxName);
                }
            }
            debug (debugLevel, 2, uStamp + " Attempting to add Option " + optionText + " for Select Box "
                            + familyCode + "/" + subfamilyCode + "/" + selectBoxName);
            SQLCommand = "INSERT INTO pub.ps_select_boxes";
            SQLCommand += " (family_code, subfamily_code, select_box_name, option_index, option_text, option_dflt, option_value1, option_value2, option_image)";
            SQLCommand += " VALUES ( '" + familyCode + "','" + subfamilyCode
                    + "','" + selectBoxName + "'," + displayOrder + ",'" + optionText + 
                    "', " + optionDefaultCode + ",'" + optionValue1 + "','" + optionValue2 + "','" + optionImage + "')";
            debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
            completedOK = conn.runUpdate(SQLCommand);
            message = " Option " + optionText + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " created successfully.";
            debug (debugLevel, 2, uStamp + message);
        
             // Get the Product Line List
            
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
            request.setAttribute("statusMessage", message); 

            request.setAttribute("lines", lines);
            RequestDispatcher view = request.getRequestDispatcher("gpsocf1.jsp");
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
