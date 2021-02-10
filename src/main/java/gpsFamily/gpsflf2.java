/*
 * gpsflf2.java
 *
 * Created on May 15, 2009, 3:01 PM
 */
package gpsFamily;

import OEdatabase.WDSconnect;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 * Modified 4/05/2010 by DES to support Index Keywords.
 * Modified 06/26/2010 by DES to Add Alias references
 * Modified 7/14/2010 by DES to remove Index Keywords.
 *
 */
public class gpsflf2 extends HttpServlet {
    
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.04";
    
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

        ArrayList <String> aliasList = new ArrayList <String> ();
        String allowParentAll = "";
        String altFamilyCode = "*undefined*";
        String altFamilyName = "None";
        String altProductLineCode = "*undefined*";
        String altProductLineName = "None";
        String custBuys = "None";
        int displayOrder = -1;
        String familyCode = "*undefined*";
        String familyName = "*undefined*";
        int familyCodeIndex = -1;
        int i = 0;
        GPSindex index = null;
        String keywordsIndex = "*undefined*";
        String keywordsPlural = "*undefined*";
        String keywordsSingular = "*undefined*";
        String parentFamilyCode = "*undefined*";
        String parentFamilyName = "None";
        String productLineName = "*undefined*";
        String productLineCode = "*undefined*";
        String totalBuys = "None";
        String work = "";
        String work2 = "";
        
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
    
        // Build query to extract existing Product Line codes 
        // and Family Codes from the database in prep for an delete
    
        try {
            GPSproductLines productLines = new GPSproductLines();
            int rc = productLines.open(conn);
            if (rc != 0) {
                sWork = uStamp + " failed to obtain Product Line Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                conn.close();
                return;
            }  
            ArrayList lines = productLines.getArrayList("CP");
            GPSfamilyCodes famCodes = new GPSfamilyCodes();
            rc = famCodes.open(conn);
            if (rc < 0) {
                sWork = uStamp + " failed to obtain Family Codes. Error " + rc;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                famCodes = null;
                conn.close();
                return;                
            }
            ArrayList familyCodes = famCodes.getArrayList();
            familyCode = request.getParameter("code");
            familyCodeIndex = famCodes.getFamilyCodeIndex(familyCode);
            if (familyCodeIndex > -1) {
                allowParentAll = famCodes.getAllowParentAll(familyCodeIndex);
                altFamilyCode = famCodes.getAltFamilyCode(familyCodeIndex);
                altProductLineCode = famCodes.getAltProductLineCode(familyCodeIndex);
                keywordsIndex = ""; //famCodes.getKeywordsIndex(familyCodeIndex);
                keywordsPlural = famCodes.getKeywordsPlural(familyCodeIndex);
                keywordsSingular = famCodes.getKeywordsSingular(familyCodeIndex);
                parentFamilyCode = famCodes.getParentFamilyCode(familyCodeIndex);
                if (!parentFamilyCode.equals("")) {
                    parentFamilyName = famCodes.getFamilyName(parentFamilyCode);
                }
                if (!altFamilyCode.equals("")) {
                    altFamilyName = famCodes.getFamilyName(altFamilyCode);
                }
                if (!altProductLineCode.equals("")) {
                    altProductLineName = productLines.getProductLineName(altProductLineCode);
                }
                custBuys = fmt(famCodes.getCustBuys(familyCodeIndex));
                totalBuys = fmt(famCodes.getTotalBuys(familyCodeIndex));
            }
            familyName = famCodes.getFamilyName(familyCode);
            productLineCode = famCodes.getFamilyProductLineCode(familyCode);
            productLineName = productLines.getProductLineName(productLineCode);
            displayOrder = famCodes.getFamilyCodeDisplayOrder(familyCode);
            index = new GPSindex(conn, familyCode, "*", false);
            for (i = 0; i < index.getSize(); i++) {
                work = index.getFamilyAlias(i) + "/";
                work2 = index.getSubfamilyAlias(i);
                if (work2.trim().equals("")) {
                    work2 = "&lt;none&gt;";
                }
                work += work2;
                if (!index.getActiveAlias(i).equals("Y")) {
                    work += " (Inactive)";
                }
                aliasList.add(work);
            }
            if (index.getSize() == 0) {
                aliasList.add("No aliases exist for this Family.");
            }
            request.setAttribute("productLineName", productLineName);
            request.setAttribute("productLineCode", productLineCode);
            request.setAttribute("displayOrder", Integer.toString(displayOrder));
            request.setAttribute("familyName", familyName);
            request.setAttribute("parentFamilyCode", parentFamilyCode);
            request.setAttribute("parentFamilyName", parentFamilyName);
            request.setAttribute("allowParentAll", allowParentAll);
            request.setAttribute("altFamilyCode", altFamilyCode);
            request.setAttribute("altFamilyName", altFamilyName);
            request.setAttribute("altProductLineCode", altProductLineCode);
            request.setAttribute("altProductLineName", altProductLineName);
            //request.setAttribute("keywordsIndex", keywordsIndex);
            request.setAttribute("keywordsPlural", keywordsPlural);
            request.setAttribute("keywordsSingular", keywordsSingular);
            request.setAttribute("custBuys", custBuys);
            request.setAttribute("totalBuys", totalBuys);
            request.setAttribute("aliasList", aliasList);
            request.setAttribute("statusMessage", "");
            RequestDispatcher view = request.getRequestDispatcher("gpsflf2.jsp");
            view.forward(request,response);
            productLines = null;
            famCodes = null;
        } catch (Exception e) {
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
   
    private String fmt(int junk) {
        String work = "";
        int k = 0;
        String src = Integer.toString(junk);
        while (src.length() > 0) {
             if (++k == 4) {
                 work = "," + work;
                 k = 1;
             }
             work = src.substring(src.length() - 1) + work;
             src = src.substring(0, src.length() - 1);
        }
        return work;
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
