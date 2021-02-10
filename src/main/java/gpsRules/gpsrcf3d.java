/*
 * gpsrcf3d.java
 *
 * Created on April 27, 2007, 4:30 PM
 */

package gpsRules;

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * * 09/07/2007   DES 
 * Made changes to support selectBoxFilter, matchOrder, PreviewOrder, and Series Implicit
 * 
 *
 */
public class gpsrcf3d extends HttpServlet {
            
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
        
        String b1 = "";
        String ErrMsg = "";
        String work = "";
        
        try {    
             
            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
            }
        
            b1 = request.getParameter("B1");
            if (b1.equals("Continue")) {
                session.setAttribute("deOrder", request.getParameter("deOrder"));
                session.setAttribute("deRequired", request.getParameter("deRequired"));
                session.setAttribute("description", request.getParameter("description"));
                session.setAttribute("deToolTip", request.getParameter("deToolTip"));
                session.setAttribute("displayJust", request.getParameter("displayJust"));
                session.setAttribute("displayOrder", request.getParameter("displayOrder"));
                session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
                session.setAttribute("matchOrder", request.getParameter("matchOrder"));
                session.setAttribute("parmName", request.getParameter("parmName"));
                session.setAttribute("parmStatus", request.getParameter("parmStatus"));
                session.setAttribute("previewOrder", request.getParameter("previewOrder"));
                session.setAttribute("searchOrder", request.getParameter("searchOrder"));
                session.setAttribute("searchRequired", request.getParameter("searchRequired"));
                session.setAttribute("searchToolTip", request.getParameter("searchToolTip"));
                session.setAttribute("selectBoxFilter", request.getParameter("selectBoxFilter"));
                session.setAttribute("seqNum", request.getParameter("seqNum")); // Field No
                session.setAttribute("seqNumbers", request.getParameter("seqNumbers")); // Valid Field Nos
                session.setAttribute("seriesImplicit", request.getParameter("seriesImplicit"));    
            } else {
                response.sendRedirect ("gpsnull.htm");
                return;
            }
        } catch (Exception e){
            ErrMsg = "An error occurred in " + SERVLET_NAME + "<br />" + e ;
            e.printStackTrace();
        } finally {
            if (!ErrMsg.equals("")) {
                request.setAttribute("message", ErrMsg);
                view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
            } else {
                view = request.getRequestDispatcher("gpsraf3d.jsp");
                view.forward(request,response);  
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
