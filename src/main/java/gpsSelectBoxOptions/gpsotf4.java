/*
 * gpsotf4.java
 *
 * Created on July 27, 2010, 2:57 PM
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
 * I make sure original data did not change and
 * toggle the show/hide images option for a Select Box.
 ** 
 * 09/01/2007   DES
 * Modified to use Ajax techniques to build and update
 * dynamic select boxes to collect the
 * product line, family, and subfamily data in the
 * listing of select box options in ps_select_boxes.
 *
 * 07/20/2010 DES Modified to support option Image field
 *
 */
public class gpsotf4 extends HttpServlet {
    
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
        String maximum = "";
        String message = "";
        String minimum = "";
        int oldDisplayOrder = 0;
        String oldOptionDefault = "";
        String oldOptionText = "";
        String oldOptionValue1 = "";
        String optionDefault = "";
        String optionDefaultCode = "";
        String optionText = "";
        String optionImage = "";
        String optionValue1 = "";
        String queryString = "";
        ResultSet rs = null;
        String selectBoxName = "";
        String showImages = "";
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

            //  Get Initial set up and save in Session variables if we got xtrol from gpslmf2.jsp.        
        
            String b1 = request.getParameter("showImages");
            if (b1.equals("SHOW") || b1.equals("HIDE")) {
                // Set our local variables from form vars
                auditUserID = request.getParameter("auditUserID");
                //displayOrder = Integer.parseInt(request.getParameter("displayOrder"));
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                familyName = request.getParameter("family");
                //maximum = request.getParameter("maximum");
                //minimum = request.getParameter("minimum");
                //optionDefault = request.getParameter("optionDefault");
                //if (optionDefault == null) {
                //    optionDefault = "";
                //}
                //if (optionDefault.equals("default")) {
                //    optionDefaultCode = "1";
                //} else {
                //    optionDefaultCode = "0";
                //}
                //optionImage = request.getParameter("optionImage");
                //optionText = request.getParameter("optionText");
                //optionValue1 = request.getParameter("optionValue1");
                //oldDisplayOrder = Integer.parseInt(request.getParameter("oldDisplayOrder"));
                //oldOptionDefault = request.getParameter("oldOptionDefault");
                //oldOptionText = request.getParameter("oldOptionText");
                //oldOptionValue1 = request.getParameter("oldOptionValue1");
                showImages = request.getParameter("showImages").toUpperCase();
                selectBoxName = request.getParameter("selectBoxName");
                subfamilyCode = request.getParameter("subfamilyCode");
                subfamilyName = request.getParameter("subfamily");
                             
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                debug (debugLevel, 8, uStamp + " processed form variables.");
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
        }
    
        // Make sure the Option we want to modify did not change somehow
    
        try {
            /*
            queryString = "SELECT *";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE select_box_name = '" + selectBoxName + "'";
            queryString += " AND family_code = '" + familyCode + "'";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            queryString += " AND option_text = '" + oldOptionText + "'";
            queryString += " AND option_value1 = '" + oldOptionValue1 + "'";
            queryString += " AND option_index = " + oldDisplayOrder;
            debug (debugLevel, 4, uStamp + " Query String is " + queryString);
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (!rs.next()) {
                    rs.close();
                    conn.closeStatement();
                    conn.close();
                    message = " Error! Cannot find the old Option '" + oldOptionText + "'";
                    debug (debugLevel, 0, uStamp + message);
                    request.setAttribute("message", message);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    return;
                }
                rs.close();
            }
        
            // If we get here, the Option we are changing has not
            // been modified by anyone else in the meantime.
        
            // Ummmm Note that the Option record was NOT locked here.
                   
            
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
             */
            debug (debugLevel, 4, uStamp + " Attempting to modify Option " + oldOptionText + " in Select Box " 
                    + familyCode + "/" + subfamilyCode + "/" + selectBoxName);
            showImages = showImages.equals("SHOW") ? "HIDE" : "SHOW";
            SQLCommand = "UPDATE pub.ps_select_boxes";
            SQLCommand += " SET option_image = '" + showImages +"'";
            SQLCommand += " WHERE select_box_name = '" + selectBoxName + "'";
            SQLCommand += " AND family_code = '" + familyCode + "'";
            SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
            SQLCommand += " AND option_index = -1";
            debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
            completedOK = conn.runUpdate(SQLCommand);
            message = showImages + " Images in " + selectBoxName + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " set successfully for family code " + familyCode
                    + " subfamily code " + subfamilyCode + ".";
            debug (debugLevel, 2, uStamp + message);
        
            GPSselectBox selectBox = new GPSselectBox();
            if (selectBox.open(conn, familyCode, subfamilyCode, selectBoxName) < 0) {
                conn.close();
                sWork = uStamp + " failed to obtain Select Box Info.";
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;             
            }
            // Create ArrayList of existing Options for the select box
            
            ArrayList optionList = selectBox.getArrayList();
            String dataType = selectBox.getDataType();
            int size = selectBox.size();
            debug (debugLevel, 2, uStamp + message);
            
            // Add the following dudettes to the Request Object
             
            request.setAttribute("dataType", dataType);
            request.setAttribute("familyName", familyName);
            request.setAttribute("familyCode", familyCode);
            request.setAttribute("maximum", maximum);
            request.setAttribute("statusMessage", message);
            request.setAttribute("minimum", minimum);
            request.setAttribute("optionList", optionList);
            request.setAttribute("selectBoxName", selectBoxName);
            request.setAttribute("showImages", showImages);
            request.setAttribute("size", Integer.toString(size));
            request.setAttribute("subfamilyCode", subfamilyCode);
            request.setAttribute("subfamilyName", subfamilyName);
     
            // Invoke our JSP
            RequestDispatcher view = request.getRequestDispatcher("gpsotf2.jsp");
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
