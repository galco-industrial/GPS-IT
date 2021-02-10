/*
 * gpsocf2.java
 *
 * Created on February 27, 2007, 4:26 PM
 */

package gpsSelectBoxOptions;

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
 * @version 1.5.00
 *
 * I am called by gpsocf1.jsp when user selects a select box and wished to create a new option.
 * I extract options for the select box and pass them to gpsocf2.jsp
 * so the user can view existing options while creating the new one.
 * 
 * 09/01/2007   DES
 * Modified to use Ajax techniques to build and update
 * dynamic select boxes to collect the
 * product line, family, and subfamily data in the
 * creation of a new select box options in ps_select_boxes.
 *
 */
public class gpsocf2 extends HttpServlet {
    
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
        String maximum = "";  
        String message = "";
        String minimum = "";
        String optionValue1 = "";
        String optionValue2 = "";
        String productLine = "";
        String queryString = "";
        String selectBoxName = "";
        int size = 0;
        String SQLCommand = "";
        String subfamilyCode = "";
        String subfamilyName = "";
        String work = "";
    
        try {  // Check for invalid Call  i.e., validation key must be set to "OK" 
            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

           // Set our local variables from form vars
            auditUserID = request.getParameter("auditUserID");
            enableToolTips = request.getParameter("enableToolTips");
            familyCode = request.getParameter("familyCode");
            familyName = request.getParameter("familyName");
            productLine = request.getParameter("productLine");
            selectBoxName = request.getParameter("selectBoxName");
            subfamilyCode = request.getParameter("subfamilyCode");
            subfamilyName = request.getParameter("subfamilyName");
            
            // Update Session variables
            session.setAttribute("auditUserID", auditUserID);
            session.setAttribute("enableToolTips", enableToolTips);
            session.setAttribute("sbProductLineCode", productLine);
            session.setAttribute("sbFamilyCode", familyCode);
            session.setAttribute("sbSelectBoxName", selectBoxName);
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
            return;
        }
        
        try {
            
            // Get the Select Box
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
            
            ArrayList <String> optionList = selectBox.getArrayList();
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
            message = "Select Box " + selectBoxName
                    + " in family code " + familyCode + " - subfamily code " + subfamilyCode
                    + " contains " + size + " Options."; 
            debug (debugLevel, 2, message);
            // Add the following dudettes to the Request Object
            
            request.setAttribute("dataType", dataType);
            request.setAttribute("familyCode", familyCode);
            request.setAttribute("familyName", familyName);
            request.setAttribute("maximum", maximum);
            request.setAttribute("minimum", minimum);
            request.setAttribute("optionList", optionList); 
            request.setAttribute("selectBoxName", selectBoxName);
            request.setAttribute("size", Integer.toString(size));
            request.setAttribute("statusMessage", message); 
            request.setAttribute("subfamilyCode", subfamilyCode);
            request.setAttribute("subfamilyName", subfamilyName);
                                   
            // Invoke our JSP
            RequestDispatcher view = request.getRequestDispatcher("gpsocf2.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            session.setAttribute("sessionOptionList", null);
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
