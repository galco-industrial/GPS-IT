/*
 * buildOptions.java
 *
 * Created on June 12, 2008, 4:04 PM
 */

package webTest;

import OEdatabase.*;
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
 * @version
 */
public class buildOptions extends HttpServlet {
    
    private boolean debugSw = true;
    private final String SERVLET_NAME = "buildOptions.java";
    private final String VERSION = "1.3.00";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        
        WDSconnect conn1 = new WDSconnect();                // Connect to WDS database 
        if (!conn1.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to WDS database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        WWWconnect conn3 = new WWWconnect();                // Connect to Web database 
        if (!conn3.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to Web database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn1.close();
            return;
        }
                
        String familyCode = "DRIVES";
        List<String> options1 = null;
        List<String> options2 = null;
        List<String> options31 = null;
        List<String> options32 = null;
        List<String> options48 = null;
        long iStart;
        long iStop;
        long iElapsed;
        
        int seqNum = 1;
        String subfamilyCode = "AC";
        try {
            //ResultSet rs = null;
            //OptionsBuilder builder = new OptionsBuilder();
/*
            String queryString = "SELECT p.part_num, v.parm_value";
            queryString += " FROM pub.part p, pub.ps_parm_data v";
            queryString += " WHERE p.family_code = 'DRIVES'";
            queryString += " AND p.subfamily_code = 'AC'";
            queryString += " AND p.part_num = v.part_num";
            queryString += " AND v.seq_num = 1";
            debug ("SQL statement is " + queryString);
            rs = conn1.runQuery(queryString);
            debug ("I got the Result Set; building the option list now...");
*/
            
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("EST"));
            iStart = cal.getTimeInMillis();
            options1 = OptionsBuilder.getStringOptionsRaw(conn1, conn3, familyCode, subfamilyCode, 1, true);
            options2 = OptionsBuilder.getStringOptionsRaw(conn1, conn3, familyCode, subfamilyCode, 2, true);
            options31 = OptionsBuilder.getStringOptionsCooked(conn1, conn3, familyCode, subfamilyCode, 31, true, "DRIVES-NEMA-TYPE");
            options32 = OptionsBuilder.getStringOptionsCooked(conn1, conn3, familyCode, subfamilyCode, 32, true, "DRIVES-IEC-RATING");
            options48 = OptionsBuilder.getStringOptionsRaw(conn1, conn3, familyCode, subfamilyCode, 48, true);
            Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("EST"));
            iStop = cal2.getTimeInMillis();
            iElapsed = iStop - iStart;
                
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occured while talking to the databases in " + SERVLET_NAME);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            conn1.close();
            conn3.close();
            return;
        }
        
        conn1.close();
        conn3.close();
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet buildOptions</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Servlet buildOptions at " + request.getContextPath () + "</h1>");
        //out.println("<h2>Start time was " + iStart  + "</h2>");
        //out.println("<h2>Stop time was " + iStop  + "</h2>");
        out.println("<h2>Elapsed time was " + iElapsed + " milliseconds." + "</h2>");
        out.println("<h2>Options 1</h2>");
        if (options1 == null) {
            out.println("<p>");
            out.println("Sorry, Error creating options1 list.");
            out.println("</p>");            
        } else {
            if (options1.size() == 0) {
            out.println("<p>");
            out.println("Sorry, no options1 were found.");
            out.println("</p>"); 
            }
            for (int i = 0; i < options1.size(); i++) {
                out.println("<p>");
                out.println(options1.get(i));
                out.println("</p>");
            }
        }    
        out.println("<h2>Options 2</h2>");
        if (options2 == null) {
            out.println("<p>");
            out.println("Sorry, Error creating options2 list.");
            out.println("</p>");            
        } else {
            if (options2.size() == 0) {
            out.println("<p>");
            out.println("Sorry, no options2 were found.");
            out.println("</p>"); 
            }
            for (int i = 0; i < options2.size(); i++) {
                out.println("<p>");
                out.println(options2.get(i));
                out.println("</p>");
            }
        }
        out.println("<h2>Options 31</h2>");
        if (options31 == null) {
            out.println("<p>");
            out.println("Sorry, Error creating options31 list.");
            out.println("</p>");            
        } else {
            if (options31.size() == 0) {
            out.println("<p>");
            out.println("Sorry, no options31 were found.");
            out.println("</p>"); 
            }
            for (int i = 0; i < options31.size(); i++) {
                out.println("<p>");
                out.println(options31.get(i));
                out.println("</p>");
            }
        }
        out.println("<h2>Options 32</h2>");
        if (options32 == null) {
            out.println("<p>");
            out.println("Sorry, Error creating options32 list.");
            out.println("</p>");            
        } else {
            if (options32.size() == 0) {
            out.println("<p>");
            out.println("Sorry, no options32 were found.");
            out.println("</p>"); 
            }
            for (int i = 0; i < options32.size(); i++) {
                out.println("<p>");
                out.println(options32.get(i));
                out.println("</p>");
            }
        }
        out.println("<h2>Options 48</h2>");
        if (options48 == null) {
            out.println("<p>");
            out.println("Sorry, Error creating options48 list.");
            out.println("</p>");            
        } else {
            if (options48.size() == 0) {
            out.println("<p>");
            out.println("Sorry, no options48 were found.");
            out.println("</p>"); 
            }
            for (int i = 0; i < options48.size(); i++) {
                out.println("<p>");
                out.println(options48.get(i));
                out.println("</p>");
            }
        } 
        out.println("</body>");
        out.println("</html>");
        out.close();
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
