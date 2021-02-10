/*
 * gpssuf3.java
 *
 * Created on February 27, 2007, 1:44 PM
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
 * @version 1.3.00
 *
 * *
 * I make sure original data did not change and
 * make a copy of the Select Box code in WDS.
 *
 */
public class gpssuf3 extends HttpServlet {
    
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
        String dataType = "";
        String enableToolTips = "";
        
        
        
        String familyCode = "";
        String familyName = "";
        String maximum = "";
        String message = "";
        String minimum = "";
        String optionDefault = "";
        boolean optionDflt = false;
        int optionIndex = 0;
        String optionText = "";
        String queryString = "";
        int rc = 0;
        ResultSet rs = null;
        String selectBoxName = "";
        int size = 0;
        String SQLCommand = "";
        int srcCount = -1;
        String srcFamilyCode = "";
        String srcFamilyName = "";
        String srcSelectBoxName = "";
        String srcSubfamilyCode = "";
        String srcSubfamilyName = "";
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

            //  Get Initial set up and save in Session variables if we got xtrol from gpssdf2.jsp.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Copy")) {
                // Set our local variables from form vars
                auditUserID = request.getParameter("auditUserID");
                dataType = request.getParameter("dataType");
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                familyName = request.getParameter("familyName");
                maximum = request.getParameter("maximum");
                minimum = request.getParameter("minimum");
                selectBoxName = request.getParameter("selectBoxName");
                size = Integer.parseInt(request.getParameter("size"));
                srcFamilyCode = request.getParameter("srcFamilyCode");
                srcSelectBoxName = request.getParameter("srcSelectBoxName");
                srcSubfamilyCode = request.getParameter("srcSubfamilyCode");
                subfamilyCode = request.getParameter("subfamilyCode");
                subfamilyName = request.getParameter("subfamilyName");
                               
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
            return;
        }
    
        // Make sure the Select Box we want to copy did not change somehow
    
        try {
            GPSselectBox selectBox = new GPSselectBox();
            if (selectBox.open(conn, srcFamilyCode, srcSubfamilyCode, srcSelectBoxName) < 0 ) {
                conn.close();
                sWork = uStamp + " failed to obtain Select Box Info.";
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
            if (!selectBox.getDataType().equals(dataType)
                    || !selectBox.getMinimum().equals(minimum)
                    || !selectBox.getMaximum().equals(maximum)
                    || selectBox.size() != size ) {
                
                message = " Error! Select Box " + selectBoxName + " original value(s) have changed!";
                debug (debugLevel, 0, uStamp + message);
                request.setAttribute("message", message);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
                  
            // If we get here, the select box we are Copying has not
            // been modified by anyone else in the mean time.
        
            // Ummmm Note that the record was NOT locked here.
            
            if (!conn.enableTransactions() ) {
                debug (debugLevel, 0, uStamp + " Enabling Transactions failed.");
            } else {
                debug (debugLevel, 4, uStamp + " Enabling Transactions.");
            }
            
            debug (debugLevel, 4, uStamp + " Making sure new select box does not exist.");
            
            SQLCommand = "SELECT *";
            SQLCommand += " FROM pub.ps_select_boxes";
            SQLCommand += " WHERE select_box_name = '" + selectBoxName + "'";
            SQLCommand += " AND family_code = '" + familyCode + "'";
            SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
            debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
            rs = conn.runQuery(SQLCommand);
            if (rs != null) {
                if (!rs.next()) {
                    completedOK = true;
                }
            }
            rs.close();
            if (completedOK) {

                debug (debugLevel, 4, uStamp + " New select Box does not exist.");
                debug (debugLevel, 4, uStamp + " Attempting to Create a copy of Select Box " + srcSelectBoxName + " and any existing options.");
            } else {
                debug (debugLevel, 4, uStamp + " Error when detecting that New Select Box currently does not exist.");
            }
            if (completedOK) {
                debug (debugLevel, 4, uStamp + " Creating Select Box Header row and copying options...");
                SQLCommand = "SELECT *";
                SQLCommand += " FROM pub.ps_select_boxes";
                SQLCommand += " WHERE select_box_name = '" + srcSelectBoxName + "'";
                SQLCommand += " AND family_code = '" + srcFamilyCode + "'";
                SQLCommand += " AND subfamily_code = '" + srcSubfamilyCode + "'";
                debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
                rs = conn.runQuery(SQLCommand);
                if (rs != null) {
                    while (rs.next() && completedOK) {
                        optionIndex = rs.getInt("option_index");
                        optionText = rs.getString("option_text");
                        optionDflt = rs.getBoolean("option_dflt");
                        optionDefault = optionDflt ? "1" : "0";
                        minimum = rs.getString("option_value1");
                        maximum = rs.getString("option_value2");
                        SQLCommand = "INSERT INTO pub.ps_select_boxes";
                        SQLCommand += " (family_code, subfamily_code, select_box_name, option_index, option_text, option_dflt, option_value1, option_value2)";
                        SQLCommand += " VALUES ( '" + familyCode + "','" + subfamilyCode + "','" + selectBoxName + "', " + optionIndex + ", '" + optionText
                                + "', " + optionDefault + ", '" + minimum + "', '" + maximum + "')";
                        debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
                        completedOK = conn.runUpdate(SQLCommand);
                        srcCount++;
                    }
                    if (!completedOK) {
                        debug (debugLevel, 4, uStamp + " Error while duplicating options in new select box.");
                    }
                } else {
                    completedOK = false;
                    debug (debugLevel, 4, uStamp + " Error! Source Select Box disappeared.");
                }
            } else {
                debug (debugLevel, 0, uStamp + " Failed to create new Select Box.");
            }
            
            message = " Select Box " + srcSelectBoxName + " and " + srcCount + " options were";
            if (!completedOK) {
                message += " NOT";
                if (!conn.rollback() ) {
                    debug (debugLevel, 0, uStamp + " Rollback failed.");
                } else {
                    debug (debugLevel, 2, uStamp + " Rollback successful.");
                }
            } else {
                if (!conn.commit() ) {
                    debug (debugLevel, 0, uStamp + " Commit failed.");
                } else {
                    debug (debugLevel, 2, uStamp + " Commit successful.");
                }
            }
            message += " copied to " + selectBoxName + " in family code " 
                    + familyCode + " subfamily code " + subfamilyCode + " successfully.";
            debug (debugLevel, 2, uStamp + message);
            
        } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            if (!conn.rollback() ) {
                debug (debugLevel, 0, uStamp + " Rollback failed.");
            } else {
                debug (debugLevel, 2, uStamp + " Rollback successful.");
            }
            if (!conn.disableTransactions() ) {
                debug (debugLevel, 0, uStamp + " Disable Transactions failed.");
            } else {
                debug (debugLevel, 2, uStamp + " Transactions disabled.");
            }
            conn.close();
            return;
        }
        
        if (!conn.disableTransactions() ) {
            debug (debugLevel, 0, uStamp + " Disable Transactions failed.");
        } else {
            debug (debugLevel, 2, uStamp + " Transactions disabled.");
        }
                
        // Build query to extract existing Select Boxes and their attributes from the database
    
        try {
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
            RequestDispatcher view = request.getRequestDispatcher("gpssuf1.jsp");
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
