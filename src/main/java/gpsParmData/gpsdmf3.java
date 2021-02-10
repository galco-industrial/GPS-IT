/*
 * gpsdmf3.java
 *
 * Created on March 25, 2008, 4:50 PM
 */

package gpsParmData;

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
 * @version 1.3.00
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 *
 * I obtain the modified parametric data from gpsdmf2.jsp and update
 * the previous parametric data with the new data.
 *
 */
public class gpsdmf3 extends HttpServlet {
        
    private boolean debugSw = false;
    private final String SERVLET_NAME = "gpsdmf3.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
       
        // Get a handle on our session and check for timeout
          
        HttpSession session = request.getSession();  
        if (session.isNew()) {          // check for session timeout
           response.sendRedirect ("gpstimeout.htm");
           return;
        }
                  
        String gDebug = (String) session.getAttribute("debug");
        if (gDebug != null) {
            debugSw = gDebug.equals("Y");
        }
        //debugSw = true;
     
        // Declare some variables here

        String auditDate = DateTime.getDateMMDDYY();
        String auditTimeRaw = Integer.toString(DateTime.getSecondsSinceMidnight());
        String cooked = "";
        String message = "";
        String partNum;
        String raw = "";
        int seqNum;
        String work;
                  
        // Check for invalid Call  i.e., validation key must be set to "OK" */

	String yadda = request.getParameter("validation");
        if (!yadda.equals("OK")) {
            message = "Fatal Error! Validation code in module gpsdmf2.jsp did not complete successfully.";
            request.setAttribute("message", message);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
             
        // Make sure this was a valid call from the web browser 
        // and if so, extract and set session attributes from Request Object
        // otherwise abort
        
        String junk = (String) request.getParameter("B1");
        if (junk != null && junk.equals("Modify")) {
            session.setAttribute("enableToolTips", request.getParameter("enableToolTips"));
            partNum = request.getParameter("partNum").toUpperCase();
        } else {
            message = "Fatal Error! Invalid call to module " + SERVLET_NAME;
            request.setAttribute("message", message);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }    
        
        String auditUserID = (String)session.getAttribute("auditUserID");
        auditUserID = EditText.setDefault(auditUserID,"");
        if (auditUserID.equals("") ) {
            message = "Fatal Error! Missing session-scoped Audit User ID.";
            request.setAttribute("message", message);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
                       
        // The Sequence Number Map contains the parm sequence numbers 
        // that correspond to the form DE objects
        
        List seqNumMap = (List)session.getAttribute("seqNumMap");
        if (seqNumMap == null ) {
            message = "Fatal Error! Missing session-scoped Sequence Number Map.";
            request.setAttribute("message", message);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        // Connect to WDS database    
    
        WDSconnect conn = new WDSconnect();
        if (!conn.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        debug (SERVLET_NAME + " connected to database");
     
        try {
            if (!GPSpart.exists(conn, partNum)) {
                message = "Error! Part number " + partNum + " disappeared!";
                debug (message);
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
            debug ("Part number " + partNum + " was found.");   

            // Delete the existing data from the ps_parm table

            // ** BEGIN TRANSACTION **            
            
            if (!conn.enableTransactions()) {
                message = "Fatal Error - Attempt to enable Transactions in WDS database failed.";
                debug (message);
                request.setAttribute("statusMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
                view.forward(request,response);
                conn.close();
                return;
            }
            debug ("Database Transaction Management has been enabled.");       
            if (GPSparmSet.exists(conn, partNum)) {
                debug ("Attempting to delete existing parametric data for this part number.");
                if (!GPSparmSet.delete(conn, partNum)) {
                    message = "An error occurred while attempting to delete original Parametric Data for Part Number " + partNum;
                    debug (message);
                    conn.rollback();
                    request.setAttribute("statusMessage", message);
                    RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
                    view.forward(request,response);
                    conn.close();
                    return;
                }
            }        

            //      loop through all the parm values, adding them one by one to the ps_parm table
            
            for (int i = 0; i < seqNumMap.size() ;  i++) {
                seqNum = Integer.parseInt((String)seqNumMap.get(i));
                cooked = "inputObject" + Integer.toString(i);
                raw = "raw" + Integer.toString(i);
                junk = request.getParameter(raw);
                if (junk == null) {
                    junk = request.getParameter(cooked);
                }
                debug ("Attempting to add " + cooked + " value='" + junk + "'");
                if (!GPSparmSet.add(conn, partNum, seqNum, junk, auditDate, auditTimeRaw, auditUserID)) {
                    conn.rollback();
                    message = "A Fatal Error occurred while attempting to add modified data to the Parametric Data table.";
                    debug (message);
                    request.setAttribute("statusMessage", message);
                    RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
                    view.forward(request,response);
                    conn.close();
                    return;
                }
                debug ("Adding modified parm data was successful");
            }                

            // ** End Transaction **
        
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occured while talking to the database in " + SERVLET_NAME);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn.close();
            conn = null;
            return;
        }
        conn.commit();
        // Disable transaction management
        if (!conn.disableTransactions()) {
            message = "Fatal Error - Attempt to disable Transactions in WDS database failed.";
            debug (message);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
            view.forward(request,response);
            conn.close();
            return;
        } else {
            debug ("WDS Transaction Management is now disabled.");
        }
        
        // Clean up session scoped junk
        
        session.setAttribute("generatedScript", null);
        session.setAttribute("seqNumMap", null);
        session.setAttribute("previousValue", null);
        
        message = "Modify Operation completed successfully for Part Number " + partNum;
        debug (message);
        request.setAttribute("statusMessage", message);
        RequestDispatcher view = request.getRequestDispatcher("gpsdmf1.jsp");
        view.forward(request,response);
        conn.close();
        return;
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
