/*
 * gpsdpf1.java
 *
 * Created on June 7, 2007, 2:55 PM
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
 * Purge parm data
 * 
 * 
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 *  Modification History
 *
 *
 */
public class gpsdpf1 extends HttpServlet {
            
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

        String gpsImportPath = getServletContext().getInitParameter("importPath");
        List <String> families;
        int j = 0;
        String queryString="";
        ResultSet rs = null;
        Statement statement;
        List <String> subfamilies;
    
        // Build queries to extract Family and Subfamily codes from the database
    
        try {
            families = new ArrayList <String> ();
            subfamilies = new ArrayList <String> ();
            queryString = "SELECT family_code, family_name FROM pub.ps_family ORDER BY family_name";
            rs = conn.runQuery(queryString);
            j = 0;
            while(rs.next()) {
                String work = "\""+rs.getString("family_code")+"\", \""+rs.getString("family_name")+"\"";
                families.add(work);
                j++;
            }
            statement = rs.getStatement();
            rs.close();
            statement.close();
            if (j == 0) {
                conn.close();
                request.setAttribute("message", "Module " + SERVLET_NAME + " could not find any Family codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
            request.setAttribute("families", families);

            // Now get valid subfamily codes
         
            queryString = "SELECT family_code, subfamily_code, subfamily_name FROM pub.ps_subfamily ORDER BY family_code, subfamily_name";
            rs = conn.runQuery(queryString);
            j = 0;
            while(rs.next()) {
                String work = "\""+rs.getString("family_code")+"\", \""+rs.getString("subfamily_code")+"\", \""+rs.getString("subfamily_name")+"\"";
                if (work.indexOf("*") == -1) {subfamilies.add(work);}
                j++;
            }
            statement = rs.getStatement();
            rs.close();
            statement.close();
            if (j == 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " could not find any Subfamily codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
            request.setAttribute("subfamilies", subfamilies);
        
            //   Put the lists inside the Request Object and forward to the JSP to display

            request.setAttribute("gpsImportPath", gpsImportPath);
            //   Forward the goodies to the JSP     
            conn.close();
            RequestDispatcher view = request.getRequestDispatcher("gpsdpf1.jsp");
            view.forward(request,response);  
        } catch (Exception e){
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
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
