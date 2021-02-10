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
public class gpsmdf3 extends HttpServlet {
    private final String SERVLET_NAME = "gpsmdf3.java";
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
        ArrayList <String> mfgNames = null;
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
            /* Check for invalid Call  i.e., validation key must be set to "OK" */
            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }
            
            String b1 = request.getParameter("B1");
            if (b1.equals("Delete")) {
                // Set our local variables from form vars
                activeCode = request.getParameter("active").substring(0,1);
                auditUserID = request.getParameter("auditUserID");
                enableToolTips = request.getParameter("enableToolTips");
                mfgCode = request.getParameter("mfgAliasCode");
                mfgAlias = request.getParameter("mfgAliasName");   
                session.setAttribute("aiActiveCode", activeCode);
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips); 
                debug (debugLevel, 8, uStamp + " processed form variables.");
            } else {
                conn.close();
                response.sendRedirect ("gpsnull.htm");
                return;
            } 
            // Make sure the Alias we want to delete is still in  database 
            try {
                queryString = "SELECT *";
                queryString += " FROM pub.ps_index_mfg";
                queryString += " WHERE mfg_alias = '" + mfgAlias + "'";                
                queryString += " AND mfg_code = '" + mfgCode + "'";                
                rs = conn.runQuery(queryString);
                if (rs != null) {
                    if (rs.next()) {
                        completedOK = true;
                        message = " Found Manufacturer Alias for '" + mfgAlias + " / " + mfgCode + "' OK";
                        sWork = uStamp + message;
                        debug (debugLevel, 5, sWork);
                    }
                    rs.close();
                    conn.closeStatement();
                }
                if (completedOK) {
                    debug (debugLevel, 4, uStamp + " Attempting to delete Manufacturer Alias " + mfgAlias + " / " + mfgCode);
                    SQLCommand = "DELETE FROM pub.ps_index_mfg";
                    SQLCommand += " WHERE mfg_alias = '" + mfgAlias + "'";
                    SQLCommand += " AND mfg_code = '" + mfgCode + "'";
                    completedOK = conn.runUpdate(SQLCommand);
                    message = " Manufacturer Alias '" + mfgAlias + " / " + mfgCode + "' was";
                    if (!completedOK) {
                        message += " NOT";
                    }
                    message += " deleted successfully ";
                    debug (debugLevel, 4, uStamp + message);                    
                } else {
                    message = " Could not find Manufacturer Alias for '" + mfgAlias + " / " + mfgCode + "' - unusual Error!";
                    sWork = uStamp + message;
                    debug (debugLevel, 0, sWork);
                }     
                mfgNames = new ArrayList <String> ();
                queryString = "SELECT mfg_code, mfg_alias, ";
                queryString += " audit_date, audit_time, audit_userid, active ";
                queryString += " FROM pub.ps_index_mfg";
                queryString += " ORDER BY mfg_alias";
                rs = conn.runQuery(queryString);
                if (rs != null) {
                    while (rs.next()) {
                        mfgAlias = rs.getString("mfg_alias");
                        mfgCode = rs.getString("mfg_code");                    
                        activeBoolean = rs.getBoolean("active");
                        activeCode = activeBoolean ? "Yes" : "No";
                        work = "'" + mfgCode                            
                                + "','" + mfgAlias
                                + "','" + activeCode
                                + "'";
                        mfgNames.add(work);
                    }
                    rs.close();
                    conn.closeStatement();
                }
                request.setAttribute("mfgNames", mfgNames);
                request.setAttribute("statusMessage", message);
                view = request.getRequestDispatcher("gpsmdf1.jsp");
                view.forward(request,response);         
            } catch (Exception e) {
                sWork = uStamp + " unexpected error " + e;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                e.printStackTrace();
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
            } finally {
                conn.close();        
            }    
       } catch (Exception e){
            conn.close();
            e.printStackTrace();
            request.setAttribute("message", "An error occurred in module " + SERVLET_NAME);
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
