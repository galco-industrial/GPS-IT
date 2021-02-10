/*
 * gpsdxf3.java
 *
 * Created on January 10, 2007, 1:46 PM
 */

package gpsParmData;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import sauter.util.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 * Modification History
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 */
public class gpsdxf3 extends HttpServlet {
            
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
        
                
        String catCode = "";
        String famCode = "";
        int j = 0;
        String lineCode = "";
        String queryString = "";
        ResultSet rs = null;
        Statement statement;
        String subcatCode = "";
        String subfamCode = "";
        String work = "";
        String work2 = "";
        
        // Check for invalid Call  i.e., validation key must be set to "OK" 
        work = request.getParameter("validation");
        if (!work.equals("OK")) {
            conn.close();
            response.sendRedirect ("gpsnull.htm");
        }
        
       // Extract Subcategory codes from the database
    
        try {
            catCode = request.getParameter("categoryCode");
            famCode = request.getParameter("familyCode");
            lineCode = request.getParameter("lineCode");
            subcatCode = request.getParameter("subcategoryCode");
            subfamCode = request.getParameter("subfamilyCode");
                    
            session.setAttribute("auditUserID", request.getParameter("auditUserID"));
            session.setAttribute("preExistingOnly", request.getParameter("preExistingOnly"));
            work = request.getParameter("excludePreExisting");
            if (work == null) {
                work = "N";
            }
            session.setAttribute("excludePreExisting", work);

            @SuppressWarnings("unchecked")

            List <String> template = (ArrayList <String>) session.getAttribute("template"); 
        
            // Get template codes
         
            queryString = "SELECT DISTINCT template_num";
            queryString += " FROM pub.part_cat";
            queryString += " WHERE category = '" + catCode + "' AND subcategory = '" + subcatCode + "'";
            queryString += " ORDER BY template_num";
            rs = conn.runQuery(queryString);
            j = 0;
            template.clear();
            template.add("\"0\",\"Please choose a Template\"");
            while(rs.next()) {
                work2 = removeLeadingSpaces(rs.getString("template_num"));
                if (work2.length() > 0) {
                    work2 = EditText.encodeEntity(work2);
                    work = "\"" + work2 + "\", \"" + work2 + "\"";
                    template.add(work);
                    j++;
                }
            }
            statement = rs.getStatement();
            rs.close();
            statement.close();
            conn.close();
            debug (debugLevel, 4, uStamp + " found " + j + " template codes.");
        
            request.setAttribute("selectedLine",lineCode); 
            request.setAttribute("selectedFamily",famCode); 
            request.setAttribute("selectedSubfamily",subfamCode); 
            request.setAttribute("selectedCategory",catCode); 
            request.setAttribute("selectedSubcategory",subcatCode); 

            //   Forward the goodies to the JSP     
     
            view = request.getRequestDispatcher("gpsdxf1.jsp");
            view.forward(request,response);  
        } catch (Exception e){
            request.setAttribute("message", "An error occurred in " + SERVLET_NAME + ": <br />" + e);
            e.printStackTrace();
            view = request.getRequestDispatcher("showMessage.jsp");
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
    
    private String removeLeadingSpaces(String work) {
        while (work.indexOf(" ") == 0) {
            work = work.substring(1);
        }
        return work;
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
