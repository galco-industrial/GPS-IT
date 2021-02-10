/*
 * gpsruf4n.java
 *
 * Created on November 30, 2007, 4:36 PM
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
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * * I save the common rules in session variables and then dispatch to the jsp that
 * gets numeric field rules
 *
 *
 */
public class gpsruf4n extends HttpServlet {
    
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsruf4n.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        String dataType = "";
        String errMsg = "";
        String familyCode = "";
        int j;
        String subfamilyCode = "";
        
        GPScvt cvt;
        GPSrules ruleSet;
        ResultSet rs;
        List <String> selectUnits;
        
        String yadda = request.getParameter("validation");
        if (!yadda.equals("OK")) {
            response.sendRedirect ("gpsnull.htm");
        }
         
        HttpSession session = request.getSession();
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        debugSw = true;
        
        if (session.isNew()) {
            response.sendRedirect ("gpstimeout.htm");
        }
        
        ruleSet = (GPSrules) session.getAttribute("sRuleSet");
        
        try {    
            String b1 = request.getParameter("B1");
            if (b1.equals("Continue")) {
                session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
                
                dataType = request.getParameter("txtDataType");
                if (dataType == null || !dataType.equals("N") ) {         
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
            }
        }
    
        try {
            
            cvt = new GPScvt();
            ruleSet.setDefaultValueCooked(cvt.toCooked(ruleSet.getDefaultValueRaw(),
                ruleSet.getDeMultipliers(), ruleSet.getParmDelimiter(),
                ruleSet.getDecShift(), ruleSet.getAllowDuplicates(),
                ruleSet.getAllowTilde(), true));
            ruleSet.setMinValueCooked(cvt.toCooked(ruleSet.getMinValueRaw(),
                ruleSet.getDeMultipliers(), ruleSet.getParmDelimiter(),
                ruleSet.getDecShift(), ruleSet.getAllowDuplicates(),
                ruleSet.getAllowTilde(), true));
            ruleSet.setMaxValueCooked(cvt.toCooked(ruleSet.getMaxValueRaw(),
                ruleSet.getDeMultipliers(), ruleSet.getParmDelimiter(),
                ruleSet.getDecShift(), ruleSet.getAllowDuplicates(),
                ruleSet.getAllowTilde(), true));
            
            familyCode = ruleSet.getFamilyCode();
            subfamilyCode = ruleSet.getSubfamilyCode();
            selectUnits = new ArrayList <String>();
            
            WDSconnect conn = new WDSconnect();      // Connect to WDS database    
            if (!conn.connect()) {         
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
                
            String queryString = "SELECT display_units, multiplier_exp";
            queryString += " FROM pub.ps_units";
            queryString += " ORDER BY base_units, multiplier_exp";
            rs = conn.runQuery(queryString);
            j = 0;
            while(rs.next()) {
                String work = "\""+rs.getString("display_units") + "\", \"" 
                        + rs.getInt("multiplier_exp") + "\"";
                selectUnits.add(work);
                j++;
            }
            if (j == 0) {
                rs.close();
                conn.close();
                request.setAttribute("message", "Module " + SERVLET_NAME + " could not find any display Units codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
            request.setAttribute("selectUnits", selectUnits);
            
            // Now get a list of applicable Select Box Names
            
            List <String> selectBoxNames = new ArrayList <String>();
            
            queryString = "SELECT select_box_name ";
            queryString += " FROM pub.ps_select_boxes";
            queryString += " WHERE family_code = '" + familyCode + "'";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            queryString += " AND option_text = 'NUMERIC'";
            queryString += " AND option_index = -1";
            debug (queryString);
            rs = conn.runQuery(queryString);
            j = 0;
            while(rs.next()) {
                String work =  rs.getString("select_box_name");
                debug ("Found select box named: " + work);
                selectBoxNames.add(work);
                j++;
            }
            debug ("" + j + " select boxes were found.");

            request.setAttribute("selectBoxNames", selectBoxNames);
            rs.close();
            conn.close();

            //   Put the lists inside the Request Object and forward to the JSP to display

        } catch (Exception e){
            request.setAttribute("message", "An error occurred getting display units or select box Names in " 
                    + SERVLET_NAME + "<br />" + e);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            e.printStackTrace();
            return;
        }
        debug (SERVLET_NAME + " says DE select box name is '" + ruleSet.getDeSelectBoxName() + "'");
        RequestDispatcher view = request.getRequestDispatcher("gpsruf4n.jsp");
        view.forward(request,response);  
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
