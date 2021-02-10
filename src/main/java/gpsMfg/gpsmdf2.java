/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gpsMfg;

import OEdatabase.WWWconnect;
import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author dunlop
 */
public class gpsmdf2 extends HttpServlet {
    
    private final String SERVLET_NAME = "gpsmdf2.java";
    private final String VERSION = "7.1.00";
    
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
        
        // Connect to WWW database     
        WWWconnect conn = new WWWconnect();
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }        
        
        boolean activeBoolean = false;
        String activeCode = "";
        String auditDate = "";
        String auditTime = "";
        String auditUserID = "";
        boolean completedOK = false;
        String enableToolTips = "";        
        String mfgCode = "";
        String mfgAlias = "";  
        String message = "";
        String queryString = "";
        String SQLCommand = "";
        int rc = 0;
        ResultSet rs = null;
        String work = "";
        RequestDispatcher view;    
        
        
        try {    
                        
            // Set our local variables from form             
            mfgCode = request.getParameter("mfgcode");
            mfgAlias = request.getParameter("mfgname");                        
            //  Set/Update session vars
            session.setAttribute("auditUserID", auditUserID);
            session.setAttribute("enableToolTips", enableToolTips);
            debug (debugLevel, 8, uStamp + " processed form variables.");                
            debug (debugLevel, 4, uStamp + " Attempting to Read Manufacturer Alias " + mfgAlias + " / " + mfgCode);
                    
            queryString = "SELECT mfg_code, mfg_alias, ";
            queryString += " audit_date, audit_time, audit_userid, active ";
            queryString += " FROM pub.ps_index_mfg";
            queryString += " WHERE mfg_alias = '" + mfgAlias + "'";            
            queryString += " AND mfg_code = '" + mfgCode + "'";            
            rs = conn.runQuery(queryString);
            if (rs != null && rs.next()) {
                activeBoolean = rs.getBoolean("active");
                activeCode = activeBoolean ? "Yes" : "No";
                auditDate = rs.getDate("audit_date").toString();
                auditTime = rs.getString("audit_time");
                auditUserID = rs.getString("audit_userid");
                mfgAlias = rs.getString("mfg_alias");
                mfgCode = rs.getString("mfg_code");                
                completedOK = true;
            }
            rs.close();
            conn.closeStatement();
            
            if (completedOK) {
                message = "";                
                request.setAttribute("statusMessage", message);
                request.setAttribute("ActiveCode", activeCode);
                request.setAttribute("mfgAliasName", mfgAlias);                
                request.setAttribute("mfgAliasCode", mfgCode);                
                request.setAttribute("auditUserID", auditUserID);
                request.setAttribute("auditDate", auditDate);
                request.setAttribute("auditTime", auditTime);
                view = request.getRequestDispatcher("gpsmdf2.jsp");
                view.forward(request,response);
                return;
            }
            conn.close();
            sWork = uStamp + " could not find Manufacturer Alias: " + mfgAlias + "/" + mfgCode;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;  
        } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
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
