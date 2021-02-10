/*
 * gpsdxf1.java
 *
 * Created on November 20, 2006, 2:24 PM
 */

package gpsParmData;

import OEdatabase.WDSconnect;
import gps.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 * Modification History
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I build a list of product lines, families, and subfamilies and pass the lists
 * to gpsdxf1.jsp for the user to choose in prep for exporting part and digest data
 *
 */

public class gpsdxf1 extends HttpServlet {
            
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
        
        int j = 0;
        String queryString = "";
        ResultSet rs = null;
        Statement statement;
        String work = "";
    
        // Build queries to extract product Line, Family and Subfamily codes from the database
    
        try {
                        
            // First let's get a list of Product Lines
            
            GPSproductLines productLines = new GPSproductLines();
            if (productLines.open(conn) != 0) {
                conn.close();
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
            
            ArrayList <String> lines = productLines.getArrayList("CP");
                    
            // Next get a set of family codes
        
            GPSfamilyCodes famCodes = new GPSfamilyCodes();
            famCodes.open(conn);
            ArrayList <String> families = famCodes.getArrayList();
            if (families == null) {
                conn.close();
                request.setAttribute("message", "Module " + SERVLET_NAME + " could not find any Family codes.");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
                
            // Now get valid subfamily codes
        
            List <String> subfamilies = GPSsubfamilyCodes.getAllSubfamilies(conn);
            if (subfamilies == null) {
                conn.close();
                request.setAttribute("message", "Module " + SERVLET_NAME + " could not find any Subfamily codes.");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
         
            session.setAttribute("lines", lines);
            session.setAttribute("families", families);
            session.setAttribute("subfamilies", subfamilies);
        
            //   We have put the list pointers inside the Session Object for the JSP to display

        } catch (Exception e) {
            request.setAttribute("message", "An error occurred in " + SERVLET_NAME + "<br />" + e);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            e.printStackTrace();
            conn.close();
            return;
        }
     
        // Now let's get the other goodies         
    
        try {
            List <String> cats = new ArrayList <String>();
            List <String> subcats = new ArrayList <String>();
            List <String> template = new ArrayList <String>();
            queryString = "";
            queryString = "SELECT category";
            queryString += " FROM pub.part_cat";
            queryString += " WHERE subcategory=''";
            queryString += " ORDER BY category";
            rs = conn.runQuery(queryString);
            j = 0;
            while(rs.next()) {
                work = "\"" + rs.getString("category") + "\""; 
                cats.add(work);
                j++;
            }
            statement = rs.getStatement();
            rs.close();
            statement.close();
            if (j == 0) {
                conn.close();
                request.setAttribute("message", "Module " + SERVLET_NAME + " could not find any Category codes.");
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
         
            subcats.add( "\"\",\"\",\"Please select a Category first\"" );
            template.add( "\"\",\"Please select a Category first\"" );
            
            //   Save the list inside the Session Object and forward to the JSP to display
         
            session.setAttribute("categories", cats);
            session.setAttribute("subcategories", subcats);
            session.setAttribute("template", template);
            session.setAttribute("excludePreExisting", "N");
            session.setAttribute("preExistingOnly", "N");
            session.setAttribute("includeDigest", "Y");
            request.setAttribute("selectedLine","0");
            request.setAttribute("selectedFamily","0"); 
            request.setAttribute("selectedSubfamily","0"); 
            
            //   Forward the goodies to the JSP     
     
            view = request.getRequestDispatcher("gpsdxf1.jsp");
            view.forward(request,response); 
        } catch (Exception e){
            request.setAttribute("message", "An error occurred in " + SERVLET_NAME + " <br />" + e);
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
