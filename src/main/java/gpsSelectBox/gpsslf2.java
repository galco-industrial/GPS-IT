/*
 * gpsslf2.java
 *
 * Created on February 22, 2007, 5:17 PM
 */

package gpsSelectBox;

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
 *
 * I extract a list of Select Boxes and their attributes
 * from WDS and send them to a JSP to display.
 *
 * * 08/23/2007   DES
 * Modified to use Ajax techniques to build and update
 * dynamic select boxes to collect the
 * product line, family, and subfamily data in the
 * creation of a new select box in ps_select_boxes.
 *
 * 07/19/2010 DES modified to support option image field.
 *
 */
public class gpsslf2 extends HttpServlet {
    
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
        int count = 0;
        String enableToolTips = "";
        String familyCode = "";
        String message = "A fatal error occurred when attempting to extract Select Box Names from the database";
        String optionDataType = "";
        String optionDataTypeChar = "";
        String optionImage = "";
        String optionValue1 = "";
        String optionValue2 = "";
        String productLine = "";
        ResultSet rs = null;
        String selectBoxName = "";
        String subfamilyCode = "";

        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */
            String work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

            //  Get Initial set up and save in Session variables if we got xtrol from gpslcf1.        
        
            // Set our local variables from form vars
                
            auditUserID = request.getParameter("auditUserID");
            enableToolTips = request.getParameter("enableToolTips");
            familyCode = request.getParameter("familyCode");
            productLine = request.getParameter("productLine");
            subfamilyCode = request.getParameter("subfamilyCode");
                
            //  Set/Update session vars

            session.setAttribute("enableToolTips", enableToolTips);
            session.setAttribute("sbProductLineCode", productLine);
            session.setAttribute("sbFamilyCode", familyCode);
            session.setAttribute("sbSubfamilyCode", subfamilyCode);
            debug (debugLevel, 8, uStamp + " processed form variables.");
        } catch (Exception e){
            conn.close();
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        }

        // Build query to extract existing Select Boxes and their attributes from the database
    
        try {
            ArrayList <String> selectBoxes = new ArrayList <String>();
            String queryString = "SELECT * ";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE option_index = -1";
            queryString += " AND family_code = '" + familyCode + "'";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            queryString += " ORDER BY select_box_name";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                while (rs.next()) {
                    familyCode = rs.getString("family_code");
                    subfamilyCode = rs.getString("subfamily_code");
                    selectBoxName = rs.getString("select_box_name");
                    optionDataType = rs.getString("option_text");
                    if (optionDataType.length() == 0) {
                        optionDataType = "*Invalid*";
                    }
                    optionDataTypeChar = optionDataType.substring(0,1).toUpperCase();
                    if ("NS".indexOf(optionDataTypeChar) == -1) {
                        optionDataTypeChar = "X";
                    }
                    optionImage = rs.getString("option_image").toUpperCase();
                    optionValue1 = rs.getString("option_value1");
                    optionValue2 = rs.getString("option_value2");
                    if (!optionImage.equals("SHOW")) {
                        optionImage = "HIDE";
                    }
                    
                    selectBoxes.add("\"" + familyCode + "\",\"" + subfamilyCode + "\",\"" 
                        + selectBoxName + "\",\"" + optionDataType + "\",\""
                        + optionValue1 + "\",\"" + optionValue2 + "\",\"" + optionImage + "\"");
                    
                } //if (rs.next()) {
                count = selectBoxes.size();
                if (count == 0) {
                    message = "No Select Boxes were found.";
                } else {
                    message = "" + count + " Select Boxes were found.";
                }
                rs.close();
            }
            request.setAttribute("selectBoxList", selectBoxes);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsslf2.jsp");
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
