/*
 * gpsrmf6l.java
 *
 * Created on November 27, 2007, 5:00 PM
 */

package gpsRules;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import gps.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import sauter.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 * Modified 7/20/2009 by DES to support parmStatus = Active
 * My job is to check any pre-existing parametric data to ensure that it conforms to the newly
 * modified rules for this logical field.
 *
 *
 */
public class gpsrmf6l extends HttpServlet {
            
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
        
        List <String> booBooList = new ArrayList <String> (); // List of parm values that disobey new rules
        String booBooText = "";
        String dataType = "";
        String familyCode = "";
        String junk = "";
        int openError;
        String partNum = "";
        String queryString = "";
        String rawValue = null;
        String [] rawValues = null;
        boolean required = false;
        String resultMsg = "";
        ResultSet rs;
        ResultSet rs2;
        GPSrules ruleSet;
        String seqNumString = "";
        Statement statement;
        String status = "";
        String subfamilyCode = "";
        String validate = "";
        String work = "";

        try {    
            work = request.getParameter("validation2");
            if (!work.equals("OK")) {
                conn.close();
                response.sendRedirect ("gpsnull.htm");
            }

            session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
            status = request.getParameter("status");
            ruleSet = (GPSrules) session.getAttribute("sRuleSet");
    
            /* Process String data types here */
            
            junk = request.getParameter("modify");
            if (junk == null || !junk.equals("Modify Rule")) {
                conn.close();
                request.setAttribute("message", "Module " + SERVLET_NAME + " detected an invalid invocation.");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
            dataType = ruleSet.getDataType();
            if (dataType == null || !dataType.equals("L") ) {         
                conn.close();
                request.setAttribute("message", "Module " + SERVLET_NAME + " detected an invalid Data Type code.");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
            if (!ruleSet.getParmStatus().equals("A")) {
                conn.close();
                view = request.getRequestDispatcher("gpsrmf6i.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
            
            familyCode = ruleSet.getFamilyCode();
            required = ruleSet.getDeRequired();
            seqNumString = Integer.toString(ruleSet.getSeqNum());
            subfamilyCode = ruleSet.getSubfamilyCode();
                  
            // Let's get any pre-existing parametric data for this field
            debug (debugLevel, 4, uStamp + " Checking pre-existing part numbers and parametric data");
            queryString = " SELECT part_num";
            queryString += " FROM pub.part";
            queryString += " WHERE family_code = '" + familyCode + "'";
            if (ruleSet.getRuleScope().equals("L")) {
                queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            }
            queryString += " AND has_ps_data = '1'";
            debug (debugLevel, 4, uStamp + " Query String is " + queryString);
            rs = conn.runQuery(queryString);
            if (rs != null) {
                debug (debugLevel, 4, uStamp + " Plowing through the Part Numbers");
                while (rs.next()) {
                    partNum = rs.getString("part_num");
                    debug (debugLevel, 6, uStamp + " Checking part number " + partNum );
                    queryString = " SELECT parm_value";
                    queryString += " FROM pub.ps_parm_data";
                    queryString += " WHERE part_num = '" + partNum + "'";
                    queryString += " AND seq_num = " + seqNumString;
                    debug (debugLevel, 8, uStamp + " Query string is " + queryString);
                    rs2 = conn.runQuery(queryString);
                    rawValue = null;
                    if (rs2 != null) {
                        if (rs2.next()) {
                            rawValue = rs2.getString("parm_value");
                        }
                        statement = rs2.getStatement();
                        rs2.close();
                        statement.close();
                    }
                    if (rawValue == null || rawValue.trim().equals("")) {
                        rawValue = "";
                    }
                    debug (debugLevel, 8, uStamp + " Checking parm value '" + rawValue + "' for Part Number " + partNum);
                    if (required && (rawValue.equals("") || rawValue.equals("U"))) {
                        // check for missing mandatory value here
                        resultMsg = " Error - missing required value for part number " + partNum;
                        booBooList.add(resultMsg);
                        debug (debugLevel, 2, uStamp + resultMsg);
                    }
                }
                statement = rs.getStatement();
                rs.close();
                statement.close();
            }
            debug (debugLevel, 4, uStamp + " Done.");
            request.setAttribute("status", status);
            if (booBooList.size() > 0) {
                for (int m = 0; m < booBooList.size(); m++) {
                    booBooText += booBooList.get(m) + "<br />";
                }
                request.setAttribute("booBooText", booBooText);
                request.setAttribute("booBooCount", Integer.toString(booBooList.size()));
                view = request.getRequestDispatcher("gpsrmf6e.jsp");
                view.forward(request,response);  
            } else {
                view = request.getRequestDispatcher("gpsrmf6.jsp");
                view.forward(request,response);
            }
        } catch (Exception e){
            resultMsg = " Unexpectd error " + e;
            e.printStackTrace();
            debug (debugLevel, 0, uStamp + resultMsg);
            request.setAttribute("message", resultMsg);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } finally {
            if (conn != null) {
                conn.close();
            }
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
