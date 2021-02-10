/*
 * gpsrcf3n.java
 *
 * Created on April 27, 2007, 2:47 PM
 */

package gpsRules;

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
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I save the common rules in session variables and then dispatch to the jsp that
 * gets numeric field rules
 *
 * modified 9/6/2007 for select Box selection
 *
 * Modified 10/24/07 by DES to use the GPSrules Class to create a session scoped rules object
 * that stores the rules values during execution of gpsrcf1 thru gpsrcf5 instead of 
 * storing the data in individual session variables.
 *
 */
public class gpsrcf3n extends HttpServlet {
            
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
        
        String b1 = "";
        String dataType = "";
        String errMsg = "";
        String familyCode = "";
        int j;
        String queryString = "";
        ResultSet rs;
        GPSrules ruleSet;
        List <String> selectBoxNames;
        List <String> selectUnits;
        Statement statement;
        String subfamilyCode = "";
        String work = "";
               
        work = request.getParameter("validation");
        if (!work.equals("OK")) {
            conn.close();
            response.sendRedirect ("gpsnull.htm");
        }

        ruleSet = (GPSrules) session.getAttribute("sRuleSet");
        
        try {    
            b1 = request.getParameter("B1");
            if (b1.equals("Continue")) {
                session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
                dataType = request.getParameter("txtDataType");
                if (dataType == null || !dataType.equals("N") ) {         
                    conn.close();
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
                ruleSet.setSeqNum(Integer.parseInt(request.getParameter("seqNum"))); // Field No
                // Valid Field Nos are already session scoped!
                ruleSet.setSeriesImplicit(request.getParameter("seriesImplicit").equals("Y")); 
            } else {
                conn.close();
                response.sendRedirect ("gpsnull.htm");
                return;
            }
        } catch (Exception e){
            conn.close();
            errMsg = "An error occurred in " + SERVLET_NAME + "<br />" + e ;
            e.printStackTrace();
            request.setAttribute("message", errMsg);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        }
    
        try {
            familyCode = ruleSet.getFamilyCode();
            subfamilyCode = ruleSet.getSubfamilyCode();
            selectUnits = new ArrayList <String>();
            
            queryString = "SELECT display_units, multiplier_exp";
            queryString += " FROM pub.ps_units";
            queryString += " ORDER BY base_units, multiplier_exp";
            rs = conn.runQuery(queryString);
            j = 0;
            while(rs.next()) {
                work = "\""+rs.getString("display_units") + "\", \"" 
                        + rs.getInt("multiplier_exp") + "\"";
                selectUnits.add(work);
                j++;
            }
            statement = rs.getStatement();
            rs.close();
            statement.close();
            if (j == 0) {
                conn.close();
                request.setAttribute("message", "Module " + SERVLET_NAME + " could not find any display Units codes.");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
            request.setAttribute("selectUnits", selectUnits);
            
            // Now get a list of applicable Select Box Names
            
            selectBoxNames = new ArrayList <String>();
            
            queryString = "SELECT select_box_name ";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE family_code = '" + familyCode + "'";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            queryString += " AND option_text = 'NUMERIC'";
            queryString += " AND option_index = -1";
            debug (debugLevel, 4, uStamp + " Query string is " + queryString);
            rs = conn.runQuery(queryString);
            j = 0;
            while(rs.next()) {
                work =  rs.getString("select_box_name");
                debug (debugLevel, 6, uStamp + " Found select box named: " + work);
                selectBoxNames.add(work);
                j++;
            }
            statement = rs.getStatement();
            rs.close();
            statement.close();
            conn.close();
            debug (debugLevel, 4, uStamp + j + " select boxes were found.");

            request.setAttribute("selectBoxNames", selectBoxNames);
            
            //   Put the lists inside the Request Object and forward to the JSP to display
            
            view = request.getRequestDispatcher("gpsrcf3n.jsp");
            view.forward(request,response);
        } catch (Exception e){
            conn.close();
            request.setAttribute("message", "An error occurred getting display units or select box Names in " 
                    + SERVLET_NAME + "<br />" + e);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            e.printStackTrace();
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
