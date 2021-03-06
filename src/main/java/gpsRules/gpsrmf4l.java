/*
 * gpsrmf4l.java
 *
 * Created on November 8, 2007, 4:05 PM
 */

package gpsRules;

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
 * Modified 4/22/2008 by DES to support 4 product line divisions
 * Modified 7/20/2009 by DES to support parmStatus = Active
 * I save the common rules in session variables and then dispatch to the jsp that
 * gets Logical field rules
 *
 *
 */
public class gpsrmf4l extends HttpServlet {
            
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
        
        String dataType = "";
        String errMsg = "";
        GPSrules ruleSet;
        String status = "";
        String work = "";
        
        try {    
             
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
            }
        
            ruleSet = (GPSrules) session.getAttribute("sRuleSet");
            
            String b1 = request.getParameter("B1");
            if (b1.equals("Continue")) {
                session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
                status = request.getParameter("status");
                dataType = request.getParameter("txtDataType");
                if (dataType == null || !dataType.equals("L") ) {         
                    request.setAttribute("message", "Module " + SERVLET_NAME + " detected an invalid Data Type code.");
                    view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    return;
                }
                ruleSet.setDeOrder(Integer.parseInt(request.getParameter("deOrder")));
                ruleSet.setDeRequired(request.getParameter("deRequired").equals("Y"));
                ruleSet.setDescription(request.getParameter("description"));
                ruleSet.setDeToolTip(request.getParameter("deToolTip"));
                ruleSet.setDisplayJust(request.getParameter("displayJust"));
                ruleSet.setDisplayOrder(Integer.parseInt(request.getParameter("displayOrder")));
                ruleSet.setMatchOrder(Integer.parseInt(request.getParameter("matchOrder")));
                ruleSet.setParmName(request.getParameter("parmName"));
                ruleSet.setParmStatus(request.getParameter("parmStatus"));
                ruleSet.setPreviewOrder(Integer.parseInt(request.getParameter("previewOrder")));
                ruleSet.setSearchOrder(Integer.parseInt(request.getParameter("searchOrder")));
                ruleSet.setSearchRequired(request.getParameter("searchRequired").equals("Y"));
                ruleSet.setSearchToolTip(request.getParameter("searchToolTip"));
                ruleSet.setSelectBoxFilter(request.getParameter("selectBoxFilter").equals("Y"));
                // Valid Field Nos are already session scoped!
                ruleSet.setSeriesImplicit(request.getParameter("seriesImplicit").equals("Y"));   
                
                request.setAttribute("status", status);

                //   Put the lists inside the Request Object and forward to the JSP to display
            
                view = request.getRequestDispatcher("gpsrmf4l.jsp");
                view.forward(request,response);  
            }
        } catch (Exception e){
            e.printStackTrace();
            errMsg = "An error occurred in " + SERVLET_NAME + "<br />" + e ;
            request.setAttribute("message", errMsg);
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
