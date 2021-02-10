/*
 * gpsodf4.java
 *
 * Created on March 5, 2007, 3:54 PM
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
 * @version 1.5.00
 *
 * I make sure original data did not change and
 * delete the Select Box Option from the Select Box.
 *
 * ** 
 * 09/01/2007   DES
 * Modified to use Ajax techniques to build and update
 * dynamic select boxes to collect the
 * product line, family, and subfamily data in the
 * deleting of select box options in ps_select_boxes.
 *
 */
public class gpsodf4 extends HttpServlet {
    
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
        String displayOrder = "";
        String enableToolTips = "";
        String familyCode = "";
        String familyName = "";
        int index = 0;
        String maximum = "";
        String message = "";
        String minimum = "";
        String optionDefault = "";
        String optionText = "";
        String optionValue1 = "";
        String optionValue2 = "";
        ResultSet rs = null;
        String selectBoxName = "";
        int size = 0;
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
    
            //  Get Initial set up and save in Session variables if we got xtrol from gpssdf2.jsp.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Delete")) {
                // Set our local variables from form vars
                auditUserID = request.getParameter("auditUserID");
                enableToolTips = request.getParameter("enableToolTips");
                displayOrder = request.getParameter("displayOrder");
                familyCode = request.getParameter("familyCode");
                familyName = (String) session.getAttribute("sbFamilyName");
                maximum = request.getParameter("maximum");
                minimum = request.getParameter("minimum");
                optionText = request.getParameter("optionText");
                optionValue1 = request.getParameter("optionValue1");
                //optionValue2 = request.getParameter("optionValue2");
                optionDefault = request.getParameter("optionDefault");
                selectBoxName = request.getParameter("selectBoxName");
                subfamilyCode = request.getParameter("subfamilyCode");
                subfamilyName = (String) session.getAttribute("sbSubfamilyName");
                            
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
            if (selectBox.open(conn, familyCode, subfamilyCode, selectBoxName) < 0 ) {
                conn.close();
                sWork = uStamp + " failed to obtain Select Box Info.";
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;  
            }
            index = selectBox.getDisplayOrderIndexOf(displayOrder);
            if (index == -1
                || !selectBox.getOptionText(index).equals(optionText)
                || !selectBox.getOptionValue1(index).equals(optionValue1) ) {
                    message = " Error! Select Box Option in " + familyCode + "/" 
                            + subfamilyCode + "/" + selectBoxName + " - original value(s) have changed!";
                    debug (debugLevel, 0, uStamp + message);
                    request.setAttribute("message", message);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    conn.close();
                    return;
            }
                  
            // If we get here, the select box we are deleting has not
            // been modified by anyone else in the mean time.
        
            // Ummmm Note that the record was NOT locked here and we did not check default.
            
            debug (debugLevel, 2, uStamp + " Attempting to Delete Select Box Option from " 
                    + familyCode + "/" + subfamilyCode + "/" + selectBoxName);
            SQLCommand = "DELETE FROM pub.ps_select_boxes";
            SQLCommand += " WHERE select_box_name = '" + selectBoxName + "'";
            SQLCommand += " AND family_code = '" + familyCode + "'";
            SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
            SQLCommand += " AND option_text = '" + optionText + "'";
            SQLCommand += " AND option_value1 = '" + optionValue1 + "'";
            SQLCommand += " AND option_index = " + displayOrder;
            debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
            completedOK = conn.runUpdate(SQLCommand);
            
            message = " Select Box Option in " + familyCode + "/" + subfamilyCode + "/" + selectBoxName + " was";
            if (!completedOK) {
                message += " NOT";
            }
            message += " deleted successfully.";
            debug (debugLevel, 2, uStamp + message);
            
            selectBox = new GPSselectBox();
            if (selectBox.open(conn, familyCode, subfamilyCode, selectBoxName) < 0 ) {
                conn.close();
                sWork = uStamp + " failed to obtain Select Box Info.";
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;  
            }
            
            ArrayList optionList = selectBox.getArrayList();
            String dataType = selectBox.getDataType();
            size = selectBox.size();
            
            // Add the following dudettes to the Request Object
            
            request.setAttribute("dataType", dataType);
            request.setAttribute("familyName", familyName);
            request.setAttribute("familyCode", familyCode);
            request.setAttribute("maximum", maximum);
            request.setAttribute("minimum", minimum);
            request.setAttribute("optionList", optionList); 
            request.setAttribute("selectBoxName", selectBoxName);
            request.setAttribute("size", Integer.toString(size));
            request.setAttribute("subfamilyCode", subfamilyCode);
            request.setAttribute("subfamilyName", subfamilyName);
            request.setAttribute("statusMessage", message); 
            
            // Invoke our JSP
            RequestDispatcher view = request.getRequestDispatcher("gpsodf2.jsp");
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
