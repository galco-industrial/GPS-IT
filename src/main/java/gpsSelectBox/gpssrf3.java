/*
 * gpssrf3.java
 *
 * Created on February 26, 2007, 3:55 PM
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
 * I make sure original data did not change and
 * rename the Select Box code and its references in WDS.
 * 08/23/2007   DES
 * Modified to use Ajax techniques to build and update
 * dynamic select boxes to collect the
 * product line, family, and subfamily data in the
 * renaming of a select box in ps_select_boxes.
 *
 */
public class gpssrf3 extends HttpServlet {
    
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
        String maximum = "";
        String message = "";
        String minimum = "";
        String oldSelectBoxName = "";
        String queryString = "";
        int rc = 0;
        int refCount = 0;
        ResultSet rs = null;
        String selectBoxName = "";
        int size = 0;
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

            //  Get Initial set up and save in Session variables if we got xtrol from gpssdf2.jsp.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Rename")) {
                // Set our local variables from form vars
                auditUserID = request.getParameter("auditUserID");
                dataType = request.getParameter("dataType");
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                maximum = request.getParameter("maximum");
                minimum = request.getParameter("minimum");
                oldSelectBoxName = request.getParameter("oldSelectBoxName");
                selectBoxName = request.getParameter("selectBoxName");
                size = Integer.parseInt(request.getParameter("size"));
                subfamilyCode = request.getParameter("subfamilyCode");
                
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
    
        // Make sure the Select Box we want to delete did not change somehow
    
        try {
            GPSselectBox selectBox = new GPSselectBox();
            if (selectBox.open(conn, familyCode, subfamilyCode, oldSelectBoxName) < 0 ) {
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
                    || !selectBox.getFamilyCode().equals(familyCode)
                    || !selectBox.getSubfamilyCode().equals(subfamilyCode)
                    || selectBox.size() != size ) {
                
                message = " Error! Select Box " + selectBoxName + " original value(s) have changed!";
                debug (debugLevel, 0, uStamp + message);
                request.setAttribute("message", message);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
                  
            // If we get here, the select box we are Renaming has not
            // been modified by anyone else in the mean time.
        
            // Ummmm Note that the record was NOT locked here.
            
            // Obtain references that exist in the Rules Table
            
            if (!conn.enableTransactions() ) {
                debug (debugLevel, 0, uStamp + " Enabling Transactions failed.");
            } else {
                debug (debugLevel, 4, uStamp + " Enabling Transactions.");
            }
            
            ArrayList <String> references = selectBox.getRuleReferences(conn, familyCode, subfamilyCode, oldSelectBoxName);
            if (references != null) {
                refCount = references.size();
            }
            debug (debugLevel, 4, uStamp +  " Found " + refCount + " rule references to Select Box " + oldSelectBoxName + " in Rules Table.");
            debug (debugLevel, 2, uStamp + " Attempting to Rename Select Box " + oldSelectBoxName + " and any existing options and references.");
            SQLCommand = "UPDATE pub.ps_select_boxes";
            SQLCommand += " SET select_box_name = '" + selectBoxName +"'";
            SQLCommand += " WHERE select_box_name = '" + oldSelectBoxName + "'";
            SQLCommand += " AND family_code = '" + familyCode + "'";
            SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
            debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
            completedOK = conn.runUpdate(SQLCommand);
            if (completedOK && refCount != 0) {
                debug (debugLevel, 2, uStamp + " Attempting to modify Rules Table q_select_box_name with new Select Box Name.");
                SQLCommand = "UPDATE pub.ps_rules";
                SQLCommand += " SET q_select_box_name = '" + selectBoxName +"'";
                SQLCommand += " WHERE q_select_box_name = '" + oldSelectBoxName + "'";
                SQLCommand += " AND family_code = '" + familyCode + "'";
                SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
                debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
                completedOK = conn.runUpdate(SQLCommand);
            }
            if (completedOK && refCount != 0) {
                debug (debugLevel, 2, uStamp + " Attempting to modify Rules Table de_select_box_name with new Select Box Name.");
                SQLCommand = "UPDATE pub.ps_rules";
                SQLCommand += " SET de_select_box_name = '" + selectBoxName +"'";
                SQLCommand += " WHERE de_select_box_name = '" + oldSelectBoxName + "'";
                SQLCommand += " AND family_code = '" + familyCode + "'";
                SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
                debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
                completedOK = conn.runUpdate(SQLCommand);
            }
            
            message = " Select Box " + oldSelectBoxName + ", its options and " + refCount + " Rule references were";
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
            message += " renamed to " + selectBoxName + " successfully for family code " 
                + familyCode + " and subfamily code " + subfamilyCode + ".";
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
        
        // Return for another possible rename
        
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
            RequestDispatcher view = request.getRequestDispatcher("gpssrf1.jsp");
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
