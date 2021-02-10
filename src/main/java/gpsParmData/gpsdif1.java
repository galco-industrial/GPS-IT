/*
 * gpsdif1.java
 *
 * Created on October 24, 2006, 1:06 PM
 */

package gpsParmData;

import OEdatabase.WDSconnect;
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
 *  Modification History
 *
 * 5/29/2007 DES Get Import path from application init variable
 *                  getServletContext().getInitParameter("importPath")
 *
 *                  Added type <String> to generic List object collections
 *
 */
public class gpsdif1 extends HttpServlet {
            
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
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }

        List <String> families = null;
        String gpsImportPath = getServletContext().getInitParameter("importPath");
        int j = 0;
        String message = "";
        String queryString="";
        ResultSet rs = null;
        List <String> subfamilies = null;
        String work = "";
    
        // Build queries to extract Family and Subfamily codes from the database
    
        try {
            families = new ArrayList <String> ();
            subfamilies = new ArrayList <String> ();
            queryString = "SELECT family_code, family_name FROM pub.ps_family ORDER BY family_name";
            rs = conn.runQuery(queryString);
            while(rs.next()) {
                work = "\"" + rs.getString("family_code") + "\", \"" + rs.getString("family_name") + "\"";
                families.add(work);
                j++;
            }
            rs.close();
            conn.closeStatement();
            if (j == 0) {
                conn.close();
                sWork = uStamp + " could not find any Family codes.";
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
            request.setAttribute("families", families);

            // Now get valid subfamily codes
         
            queryString = "SELECT family_code, subfamily_code, subfamily_name";
            queryString += " FROM pub.ps_subfamily ORDER BY family_code, subfamily_name";
            rs = conn.runQuery(queryString);
            j = 0;
            while(rs.next()) {
                work = "\"" + rs.getString("family_code") + "\", \"" + rs.getString("subfamily_code") + "\", \"" 
                        + rs.getString("subfamily_name") + "\"";
                if (work.indexOf("*") == -1) {
                    subfamilies.add(work);
                }
                j++;
            }
            rs.close();
            conn.closeStatement();
            if (j == 0) {
                conn.close();
                sWork = uStamp + " could not find any Subfamily codes.";
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
            request.setAttribute("subfamilies", subfamilies);
        
            //   Put the lists inside the Request Object and forward to the JSP to display

            request.setAttribute("gpsImportPath", gpsImportPath);
            //   Forward the goodies to the JSP     
            RequestDispatcher view = request.getRequestDispatcher("gpsdif1.jsp");
            view.forward(request,response); 
        } catch (Exception e){
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
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
