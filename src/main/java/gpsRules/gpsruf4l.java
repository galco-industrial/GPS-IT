/*
 * gpsruf4l.java
 *
 * Created on November 30, 2007, 4:29 PM
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
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I save the common rules in session variables and then dispatch to the jsp that
 * gets Logical field rules
 *
 *
 */
public class gpsruf4l extends HttpServlet {
    
            
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsruf4l.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String errMsg = "";
        String dataType = "";
        GPSrules ruleSet;
        
        try {    
             
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            String yadda = request.getParameter("validation");
            if (!yadda.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
            }
         
            /* Get a handle on our session */
        
            HttpSession session = request.getSession();
            String gDebug = (String) session.getAttribute("debug");
            if (gDebug != null) {
                debugSw = gDebug.equals("Y");
            }
            //debugSw = true;
            
            if (session.isNew()) {
                response.sendRedirect ("gpstimeout.htm");
            }
        
            ruleSet = (GPSrules) session.getAttribute("sRuleSet");
            
            String b1 = request.getParameter("B1");
            if (b1.equals("Continue")) {
                session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
                
                dataType = request.getParameter("txtDataType");
                if (dataType == null || !dataType.equals("L") ) {         
                    request.setAttribute("message", "Module " + SERVLET_NAME + " detected an invalid Data Type code.");
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
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
                //ruleSet.setSeqNum(Integer.parseInt(request.getParameter("seqNum"))); // Field No
                //session.setAttribute("seqNumbers", request.getParameter("seqNumbers")); 
                // Valid Field Nos are already session scoped!
                ruleSet.setSeriesImplicit(request.getParameter("seriesImplicit").equals("Y"));            
            }

        } catch (Exception e){
            e.printStackTrace();
            errMsg = "An error occurred in " + SERVLET_NAME + "<br />" + e ;
        } finally {
            if (!errMsg.equals("")) {
                request.setAttribute("message", errMsg);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
            } else {
                RequestDispatcher view = request.getRequestDispatcher("gpsruf4l.jsp");
                view.forward(request,response);  
            }
        }
    }
    
    private void debug (String x) {
        if (debugSw) {
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
