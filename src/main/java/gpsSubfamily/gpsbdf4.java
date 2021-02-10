/*
 * gpsbdf4.java
 *
 * Created on February 21, 2007, 5:17 PM
 */

package gpsSubfamily;

import OEdatabase.WDSconnect;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import gps.util.*;

/**
 *
 * @author Sauter
 *
 * Modified 4/22/2008 by DES to support 4 product line divisions
 * Modified 6/09/2009 by DES to support alt/keywords/buys and index fields
 * Modified 4/05/2010 by DES to support Index Keywords.
 * Modified 7/14/2010 by DES to remove Index Keywords.
 * Modified 9/01/2010 by DES to update the Index.
 * Modified 12/09/2011 by DES to ALWAYS update the Index.
 *
 * I make sure original data did not change and
 * delete the subfamily code in WDS.
 *
 ** Modified 9/29/2010 by DES to support division CP
 *
 */
public class gpsbdf4 extends HttpServlet {
    
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.05";
    
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
        
        String altFamilyCode = "";
        String altSubfamilyCode = "";
        String auditUserID = "";
        int change = 0;
        boolean completedOK = false;
        int displayOrder;
        String enableToolTips = "";
        String familyCode = "";
        //String index = "";
        int indexLevel = 1;
        int j = 0;
        String keywordsPlural = "";
        String keywordsSingular = "";
        String message = "";
        String message2 = "";
        String oldAltFamilyCode = "";
        String oldAltSubfamilyCode = "";
        int oldDisplayOrder;
        String oldFamilyName = "";
        int oldIndexLevel = 0;
        String oldKeywordsIndex = "";
        String oldKeywordsPlural = "";
        String oldKeywordsSingular = "";
        String oldSubfamilyName = "";
        String queryString = "";
        ResultSet rs = null;
        String SQLCommand = "";
        String subfamilyCode = "";
        String subfamilyName = "";
        String work = "";
        
        try {    
        
            /* Check for invalid Call  i.e., validation key must be set to "OK" */

            work = request.getParameter("validation");
            if (!work.equals("OK")) {
                response.sendRedirect ("gpsnull.htm");
                return;
            }

            //  Get Initial set up and save in Session variables if we got xtrol from gpsbdf2.jsp.        
        
            String b1 = request.getParameter("B1");
            if (b1.equals("Delete")) {
                // Set our local variables from form vars
                auditUserID = request.getParameter("auditUserID");
                enableToolTips = request.getParameter("enableToolTips");
                //index =  request.getParameter("index");
                //if (index == null) {
                //    index = "0";
                //}
                oldAltFamilyCode = request.getParameter("txtAltFamilyCode");
                oldAltSubfamilyCode = request.getParameter("txtAltSubfamilyCode");
                oldDisplayOrder = Integer.parseInt(request.getParameter("oldDisplayOrder"));
                oldFamilyName = request.getParameter("familyName");
                oldIndexLevel = Integer.parseInt(request.getParameter("txtIndexLevel"));
                oldKeywordsIndex = ""; //request.getParameter("txtKeywordsIndex");
                oldKeywordsPlural = request.getParameter("txtKeywordsPlural");
                oldKeywordsSingular = request.getParameter("txtKeywordsSingular");
                oldSubfamilyName = request.getParameter("oldSubfamilyName");
                familyCode = request.getParameter("familyCode");
                subfamilyCode = request.getParameter("subfamilyCode");
                
                
                //  Set/Update session vars
                session.setAttribute("auditUserID", auditUserID);
                session.setAttribute("enableToolTips", enableToolTips);
                debug (debugLevel, 8, uStamp + " processed form variables.");
            } else {
                response.sendRedirect ("gpsnull.htm");
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

    
        // Make sure the Family Code we want to delete did not change somehow
    
        try {
            queryString = "SELECT *";
            queryString += " FROM pub.ps_subfamily";
            queryString += " WHERE family_code = '" + familyCode + "'";
            queryString += " AND subfamily_code = '" + subfamilyCode + "'";
            rs = conn.runQuery(queryString);
            if (rs != null) {
                if (!rs.next()) {
                    sWork = uStamp + " Error! Subfamily Code '" + subfamilyCode + "' for Family Code '" + familyCode + "' does not exist!";
                    debug (debugLevel, 0, sWork);
                    request.setAttribute("message", sWork);
                    RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                    view.forward(request,response);
                    rs.close();
                    conn.closeStatement();
                    conn.close();
                    return;
                } else {
                    /* if (!rs.getString("subfamily_name").equals(oldSubfamilyName)
                            || rs.getInt("display_order") != oldDisplayOrder
                            || !rs.getString("alt_family_code").equals(oldAltFamilyCode)
                            || !rs.getString("alt_subfamily_code").equals(oldAltSubfamilyCode)
                            || !rs.getString("keywords_singular").equals(oldKeywordsSingular)
                            || !rs.getString("keywords_plural").equals(oldKeywordsPlural)
                            || rs.getInt("index_level") != oldIndexLevel) {
                    */
                    if (!rs.getString("subfamily_name").equals(oldSubfamilyName)) {
                        change =+ 1;
                    }
                    if (rs.getInt("display_order") != oldDisplayOrder) {
                        change += 2;
                    }
                    if (!rs.getString("alt_family_code").equals(oldAltFamilyCode)) {
                        change += 4;
                    }
                    if (!rs.getString("alt_subfamily_code").equals(oldAltSubfamilyCode)) {
                        change += 8;
                    }
                    if (!rs.getString("keywords_singular").equals(oldKeywordsSingular)) {
                        change += 16;
                    }
                    if (!rs.getString("keywords_plural").equals(oldKeywordsPlural)) {
                        change += 32;
                    }
                    //if (!rs.getString("index_keywords").equals(oldKeywordsIndex)) {
                    //    change += 64;
                    //}
                    if (rs.getInt("index_level") != oldIndexLevel) {
                            change += 128;
                    }
                    if (change != 0) {
                        sWork = uStamp + " Error! Subfamily Code '" + subfamilyCode + "' for Family Code '" 
                                + familyCode + "' original value(s) have changed! Code: " + Integer.toString(change);
                        debug (debugLevel, 0, sWork);
                        request.setAttribute("message", sWork);
                        RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                        view.forward(request,response);
                        rs.close();
                        conn.closeStatement();
                        conn.close();
                        return;
                    }
                }
                rs.close();
            }
        
            // If we get here, the subfamily code we are deleting has not
            // been modified by anyone else in the mean time.
        
            // Ummmm Note that the subfamily Code record was NOT locked here.
            
            boolean cancel = false;
            
            GPSsubfamilyCodes subfamCodes = new GPSsubfamilyCodes();
            ArrayList <String> refs;
            
            // check for rules references
            
            if (!cancel) {
                refs = subfamCodes.getRuleReferences(conn, familyCode, subfamilyCode);
                if (refs == null) {
                    cancel = true;
                    message = "Cannot delete; Database error when accessing Rules table.";
                } else {
                    if (refs.size() != 0) {
                        cancel = true;
                        message = "Cannot delete when subfamily references still exist in Rules table.";
                    }
                }
                refs = null;                  
            }
            
            // check for SelectBox references
            
            if (!cancel) {
                refs = subfamCodes.getSelectBoxReferences(conn, familyCode, subfamilyCode);
                if (refs == null) {
                    cancel = true;
                    message = "Cannot delete; Database error when accessing Select Boxes table.";
                } else {
                    if (refs.size() != 0) {
                        cancel = true;
                        message = "Cannot delete subfamily when references still exist in Select Boxes table.";
                    }
                }
                refs = null;                  
            }
            
            // check for Part references
            
            if (!cancel) {
                refs = subfamCodes.getPartReferences(conn, familyCode, subfamilyCode);
                if (refs == null) {
                    cancel = true;
                    message = "Cannot delete; Database error when accessing Part table.";
                } else {
                    if (refs.size() != 0) {
                        cancel = true;
                        message = "Cannot delete when subfamily references still exist in Part table.";
                    }
                }
                refs = null;                  
            }
            
            if (!cancel) {
            // If there are no active references in database, continue:
      
                debug (debugLevel, 2, uStamp + " Attempting to Delete Family Code / Subfamily Code " + familyCode + "/" + subfamilyCode);
                SQLCommand = "DELETE FROM pub.ps_subfamily";
                SQLCommand += " WHERE family_code = '" + familyCode + "'";
                SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
                debug (debugLevel, 4, uStamp + " SQL Command is " + SQLCommand);
                completedOK = conn.runUpdate(SQLCommand);
                message = " Subfamily Code " + subfamilyCode + " was";
                if (!completedOK) {
                    message += " NOT";
                }
                message += " deleted successfully.";
                debug (debugLevel, 2, uStamp + message);
            }
            
            if (!cancel) {
                if (completedOK) {
                    if (true) {  //if (index.equals("1")) {
                        queryString = "SELECT *";
                        queryString += " FROM pub.ps_index_entry";
                        queryString += " WHERE family_alias = '" + oldFamilyName + "'";
                        queryString += " AND subfamily_alias = '" + oldSubfamilyName + "'";
                        queryString += " AND family_code = '" + familyCode + "'";
                        queryString += " AND subfamily_code = '" + subfamilyCode + "'";
                        queryString += " AND division = 'CP'";
                        rs = conn.runQuery(queryString);
                        completedOK = false;
                        if (rs != null) {
                            if (rs.next()) {
                                completedOK = true;
                                message2 = " Found Index Alias for '" + oldFamilyName + " / " + oldSubfamilyName + "' OK";
                                sWork = uStamp + message2;
                                debug (debugLevel, 5, sWork);
                            }
                            rs.close();
                            conn.closeStatement();
                        }
                        if (completedOK) {
                            debug (debugLevel, 4, uStamp + " Attempting to delete Index Alias " + oldFamilyName + " / " + oldSubfamilyName
                                + " for family/subfamily " + familyCode + "/" + subfamilyCode);
                            SQLCommand = "DELETE FROM pub.ps_index_entry";
                            SQLCommand += " WHERE family_alias = '" + oldFamilyName + "'";
                            SQLCommand += " AND subfamily_alias = '" + oldSubfamilyName + "'";
                            SQLCommand += " AND family_code = '" + familyCode + "'";
                            SQLCommand += " AND subfamily_code = '" + subfamilyCode + "'";
                            SQLCommand += " AND division = 'CP'";
                            completedOK = conn.runUpdate(SQLCommand);
                            message += "<br />Index '" + oldFamilyName + "/" + oldSubfamilyName + "' was";
                            if (!completedOK) {
                                message += " NOT";
                            }
                            message += " deleted successfully for family/subfamily Code " + familyCode + "/" + subfamilyCode;
                            debug (debugLevel, 4, uStamp + message);
                        } else {
                            message += "<br />Could not find Index for '" + oldFamilyName + "/" + oldSubfamilyName + "'";
                            sWork = uStamp + message;
                            debug (debugLevel, 0, sWork);
                        }
                    } else {
                        message += "<br />Subfamily was NOT removed from the Index.";
                    }
                }
            }         
            
            
            GPSproductLines productLines = new GPSproductLines();
            if (productLines.open(conn) != 0) {
                request.setAttribute("message", "Module " + SERVLET_NAME + " failed to obtain Product Line Codes.");
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                productLines = null;
                //subfamCodes = null;
                //famCodes = null;
                conn.close();
                return;
            }   
            //ArrayList subfamilyCodesList = subfamCodes.getArrayList();
            //ArrayList familyCodesList = famCodes.getArrayList();
            ArrayList <String> productLinesList = productLines.getArrayList("CP");
            request.setAttribute("lines", productLinesList);
            //request.setAttribute("familyCodesList", familyCodesList);
            //request.setAttribute("subfamilyCodesList", subfamilyCodesList);
            request.setAttribute("statusMessage", message);
            RequestDispatcher view = request.getRequestDispatcher("gpsbdf1.jsp");
            view.forward(request,response);
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
