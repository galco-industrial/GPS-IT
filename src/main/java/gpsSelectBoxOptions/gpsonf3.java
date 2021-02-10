/*
 * gpsonf3.java
 *
 * Created on March 6, 2007, 5:22 PM
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
 *I renumber the Options in a Select Box
 *
 * *
 * 09/01/2007   DES
 * Modified to use Ajax techniques to build and update
 * dynamic select boxes to collect the
 * product line, family, and subfamily data in the
 * renumbering of select box options in ps_select_boxes.
 *
 */
public class gpsonf3 extends HttpServlet {
    
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
        String familyName = "";
        int j = 0;
        int incrementBy = 0;
        String maximum = "";    
        String minimum = "";
        String message = "";
        String optionValue1 = "";
        String optionValue2 = "";
        String productLine = "";
        String queryString = "";
        ResultSet rs = null;
        String selectBoxName = "";
        int size = 0;
        String SQLCommand = "";
        int startingNumber = 0;
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

            //  Get Initial set up and save in Session variables if we got xtrol from gpsonf2.jsp.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Renumber")) {
                // Set our local variables from form vars
                auditUserID = request.getParameter("auditUserID");
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                familyName = request.getParameter("family");
                incrementBy = Integer.parseInt(request.getParameter("incrementBy"));
                selectBoxName = request.getParameter("selectBoxName");
                startingNumber = Integer.parseInt(request.getParameter("startingNumber"));
                subfamilyCode = request.getParameter("subfamilyCode");
                subfamilyName = request.getParameter("subfamily");
                
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
        }

        try {
        
            // Here is where we assign new display order numbers
        
            GPSselectBox selectBox = (GPSselectBox) session.getAttribute("sessionSelectBox");
            debug (debugLevel, 4, uStamp + " Attempting to renumber Select Box Options.");
            conn.enableTransactions();
            debug (debugLevel, 6, uStamp + " Transactions have been enabled.");
            for (j=0; j < selectBox.size(); j++) {
                String optionText = selectBox.getOptionText(j);
                SQLCommand = "UPDATE pub.ps_select_boxes";
                SQLCommand += " SET option_index = " + startingNumber;
                SQLCommand += " WHERE select_box_name = '" + selectBoxName + "'";
                SQLCommand += " AND family_code = '" + familyCode + "'";
                SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
                SQLCommand += " AND option_text = '" + optionText + "'";
                debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
                completedOK = conn.runUpdate(SQLCommand);
                if (!completedOK) {
                    break;
                }
                startingNumber += incrementBy;
            }
            message = " Options in Select Box " + selectBoxName + " were";
            if (!completedOK) {
                message += " NOT";
                conn.rollback();
                debug (debugLevel, 6, uStamp + " Transaction was rolled back.");
            } else {
                conn.commit();
               debug (debugLevel, 6, uStamp + " Transaction was committed.");
            }
            message += " renumbered successfully.";
            debug (debugLevel, 2, uStamp + message);
            
            conn.disableTransactions();
            
            // Get the update Product Lines
        
            selectBox = new GPSselectBox();
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
            dataType = selectBox.getDataType();
            maximum = selectBox.getMaximum();
            if (maximum.equals("")) {
                maximum = "(none)";
            }
            minimum = selectBox.getMinimum();
            if (minimum.equals("")) {
                minimum = "(none)";
            }
            size = selectBox.size();
            
            // Remember the Options for this Select Box in a Session Scope Attribute
            session.setAttribute("sessionSelectBox", selectBox);
            
            // Add the following dudettes to the Request Object
            
            request.setAttribute("dataType", dataType);
            request.setAttribute("familyName", familyName);
            request.setAttribute("familyCode", familyCode);
            request.setAttribute("maximum", maximum);
            request.setAttribute("minimum", minimum);
            request.setAttribute("optionList", optionList); 
            request.setAttribute("selectBoxName", selectBoxName);
            request.setAttribute("size", Integer.toString(size));
            request.setAttribute("statusMessage", message); 
            request.setAttribute("subfamilyCode", subfamilyCode);
            request.setAttribute("subfamilyName", subfamilyName);
                       
            // Invoke our JSP
            RequestDispatcher view = request.getRequestDispatcher("gpsonf2.jsp");
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
