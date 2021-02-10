/*
 * gpsrdf4.java
 *
 * Created on May 8, 2007, 2:59 PM
 */

package gpsRules;

import OEdatabase.WDSconnect;
import java.io.*;
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
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 *
 * I delete the selected global or local rule 
 * after gpsrdf3.java verified that no related paramteric data exists.
 *
 */
public class gpsrdf4 extends HttpServlet {
            
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
        RequestDispatcher view = null;
        
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
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }

        String auditUserID = "";
        String b1 = "";
        boolean completedOK = false;
        String enableToolTips;
        String errMsg = "";
        String familyCode = "";
        String familyName;
        String productLineName = "";
        String result = "";
        String ruleScope;
        String seqNum = "";
        String SQLCommand = "";
        String subfamilyCode = "";
        String subfamilyName;
        String work = "";

        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                conn.close();
                response.sendRedirect ("gpsnull.htm");
            }

	    b1 = request.getParameter("B1");
            if (b1.equals("Delete")) {
                enableToolTips = request.getParameter("enableToolTips");
                familyCode = request.getParameter("familyCode");
                familyName = request.getParameter("familyName");
                productLineName = request.getParameter("productLineName");
                ruleScope = request.getParameter("ruleScope");
                seqNum = request.getParameter("seqNum");
                subfamilyCode = request.getParameter("subfamilyCode");
            
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                session.setAttribute("familyCode", familyCode);
                session.setAttribute("subfamilyCode", subfamilyCode);
                session.setAttribute("ruleScope", ruleScope);
                session.setAttribute("seqNum", seqNum);
            } else {
                errMsg = "Error - Invalid call to " + SERVLET_NAME;
            }
        } catch (Exception e){
            e.printStackTrace();
            errMsg = "An error occurred in " + SERVLET_NAME + "<br />" + e ;
        } finally {
            if (!errMsg.equals("")) {
                conn.close();
                request.setAttribute("message", errMsg);
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
            }
        }
     
        try {
            debug (debugLevel, 4, uStamp + " Attempting to DELETE Rule Set for field " + seqNum );
            SQLCommand = "DELETE ";
            SQLCommand += " FROM pub.ps_rules";
            SQLCommand += " WHERE family_code = '" + familyCode + "'";
            SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
            SQLCommand += " AND seq_num = " + seqNum ;
            completedOK = conn.runUpdate(SQLCommand);
            if (completedOK) {
                result = "Rules for field " + seqNum + " were successfully deleted.";
            } else {
                result = "An error occurred trying to delete Rule " + seqNum;
            } 
        } catch (Exception e) {
            result = "An error occurred trying to delete Rule " + seqNum;
        } finally {
            request.setAttribute("statusMessage", result);
            view = request.getRequestDispatcher("gpsrdf4.jsp");
            view.forward(request,response);
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
