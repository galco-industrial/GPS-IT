/*
 * gpsdkf2.java
 *
 * Created on March 22, 2010, 5:05 PM
 */

package gpsParmData;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author Sauter
 * @version 1.5.00
 *
 *
 */
public class gpsdkf2 extends HttpServlet {
                
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
        
        String abortFileName = "";
        String auditUserID = "";
        PrintWriter out = null;     // output file stream for log file
        String work = "";
        
        
        try {
            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

            // ************************************************************
            //  Get form values from HTTP Request; save in local & Session variables
            //  making sure we got xtrol from gpsdif1.        
            // ************************************************************
            work = request.getParameter("B1");
            if (work.equals("Kill")) {
                abortFileName = request.getParameter("abortFileName");
                auditUserID = request.getParameter("auditUserID");
                session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
            } else {
                request.setAttribute("message", "Module " + SERVLET_NAME + " was not properly invoked");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;
            }
        } catch (Exception e){
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
            
        // ********************************************************
        // Now try to open the output abort file                  *
        // ********************************************************
    
        try {
            deleteAbortFile(abortFileName);
            out = new PrintWriter( new BufferedWriter ( new FileWriter(abortFileName)));
            out.close();
            request.setAttribute("message", "Kill request sent to Import Operation for user ID " + auditUserID);
            request.setAttribute("auditUserID", auditUserID);
            RequestDispatcher view = request.getRequestDispatcher("gpsdkf2.jsp");
            view.forward(request,response);
            return;
        } catch (Exception e) {
            sWork = uStamp + "Error trying to create file " + abortFileName + " " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
    }
    
    private void debug (int limit, int level, String x) {
        if (limit >= level) {
            System.out.println(x);
        }
    }
                    
    private boolean deleteAbortFile(String fName) {
        try {
            File f = new File(fName);
            if (f.exists() && f.canWrite() && f.isFile()) {
                return f.delete();
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
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
