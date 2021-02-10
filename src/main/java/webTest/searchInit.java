/*
 * searchInit.java
 *
 * Created on June 26, 2008, 3:59 PM
 */

package webTest;

import gps.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import OEdatabase.*;
import sauter.util.*;

/**
 *
 * @author Sauter
 * @version 1.0.0
 *
 * I create an initial screen to allow a web client to select a product line and
 * a family to initiate a web search.
 * Then I invoke searchDispatcher.do
 */

public class searchInit extends HttpServlet {
    
    boolean debugSw = true;
    final String SERVLET_NAME = "searchInit.java";
    final String VERSION = "1.0.0";
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        // Connect to WDS database    
    
        WDSconnect conn1 = new WDSconnect();
        if (!conn1.connect()) {         
            request.setAttribute("message", "Module " + SERVLET_NAME + " failed to connect to WDS database");
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            return;
        }
        
        /*
         String hash = "08123412341234123412";
        String result = "";
        System.out.println("hash=" + hash);
        for (int i = 0; i < hash.length(); i++) {
                int j = i + 1;
                int iWork = hash.codePointAt(i);
                int iWork2 = (iWork + 21) ^ j;
                result += String.valueOf(Character.toChars(iWork2));
        }
        System.out.println("result=" + result);
        */
    
        // Build query to extract existing Family codes 
        // and Subfamily Codes from the database
    
        try {
            GPSproductLines productLines = new GPSproductLines();
            if (productLines.open(conn1) != 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                conn1.close();
                return;
            }   
            ArrayList <String> lines = productLines.getArrayList("CP");
            request.setAttribute("lines", lines);  // add product lines array to request object
            RequestDispatcher view = request.getRequestDispatcher("searchInit.jsp");
            view.forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "A fatal error occurred in " + SERVLET_NAME + "  <br />" + e);
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
        } finally {
            conn1.close();
        }
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
