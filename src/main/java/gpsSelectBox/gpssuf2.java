/*
 * gpssuf2.java
 *
 * Created on February 27, 2007, 1:28 PM
 */

package gpsSelectBox;

import OEdatabase.WDSconnect;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 * *
 * * I get info to prepare to Copy a selected Select Box
 *
 * *
 * * * * 08/23/2007   DES
 * Modified to use Ajax techniques to build and update
 * dynamic select boxes to collect the
 * product line, family, and subfamily data in the
 * copying of a select box in ps_select_boxes.
 *
 */
public class gpssuf2 extends HttpServlet {
    
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
    
        String validation = request.getParameter("validation");
        if (!validation.equals("OK")) {
            response.sendRedirect ("gpsnull.htm");
            return;
        }
    
        String auditUserID = request.getParameter("auditUserID");
        String enableToolTips = request.getParameter("enableToolTips");
        String familyCode = request.getParameter("familyCode");
        String familyName = request.getParameter("familyName");
        String productLine = request.getParameter("productLine");
        String selectBoxName = request.getParameter("selectBoxName");
        String subfamilyCode = request.getParameter("subfamilyCode");
        String subfamilyName = request.getParameter("subfamilyName");
                
        //  Set/Update session vars
        session.setAttribute("auditUserID", auditUserID);
        session.setAttribute("enableToolTips", enableToolTips);
        session.setAttribute("sbProductLineCode", productLine);
        session.setAttribute("sbFamilyCode", familyCode);
        session.setAttribute("sbSubfamilyCode", subfamilyCode);
        debug (debugLevel, 8, uStamp + " processed form variables.");
            
        String dataType = "";
        String maximum = "";
        String minimum = "";
        int rc = 0;
        int size = 0;
               
        // Build query to extract existing Select Box Info Line codes from the database
    
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
        
            GPSselectBox selectBox = new GPSselectBox();
            if (selectBox.open(conn, familyCode, subfamilyCode, selectBoxName) > -1) {
                size = selectBox.size();  
                dataType = selectBox.getDataType();
                minimum = selectBox.getMinimum();
                maximum = selectBox.getMaximum();
                request.setAttribute("dataType", dataType);
                request.setAttribute("lines", lines);
                request.setAttribute("maximum", maximum); 
                request.setAttribute("minimum", minimum);
                request.setAttribute("size", Integer.toString(size));
                request.setAttribute("srcFamilyCode", familyCode);
                request.setAttribute("srcFamilyName", familyName);
                request.setAttribute("srcSelectBoxName", selectBoxName);
                request.setAttribute("srcSubfamilyCode", subfamilyCode);
                request.setAttribute("srcSubfamilyName", subfamilyName);
                request.setAttribute("statusMessage", "");
                RequestDispatcher view = request.getRequestDispatcher("gpssuf2.jsp");
                view.forward(request,response);
            } else {
                conn.close();
                sWork = uStamp + " failed to obtain Select Box Info.";
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
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
