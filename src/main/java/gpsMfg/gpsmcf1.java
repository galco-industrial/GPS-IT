/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gpsMfg;

import OEdatabase.SROconnect;
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
public class gpsmcf1 extends HttpServlet {
    
    private final String SERVLET_NAME = "gpsmcf1.java";
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
        
        
        RequestDispatcher view;
        // Connect to SRO database    
    
        SROconnect conn = new SROconnect();
           
        if (!conn.connect()) {        
            sWork = uStamp + " failed to connect to SRO database; aborting.";
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
         // Build query to extract existing Manufacturer codes from the database        
    
        try { 
            GPSmfgrNames names = new GPSmfgrNames();
            int rc = names.open(conn);
            if (rc != 0) {
                sWork = uStamp + " failed to obtain Manufacturer Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                names = null;
                conn.close();
                return;
            }   
            ArrayList mNames = names.getArrayList();            
            
            request.setAttribute("mfgNames", mNames);          
            request.setAttribute("statusMessage", "");
            view = request.getRequestDispatcher("gpsmcf1.jsp");
            view.forward(request,response);
            names = null;
            
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
        return "I build a Manufacturer Code Object for creating Manufacturer Aliases.";
    }
    // </editor-fold>
}


