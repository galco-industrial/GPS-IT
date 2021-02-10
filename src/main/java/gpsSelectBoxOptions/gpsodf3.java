/*
 * gpsodf3.java
 *
 * Created on March 5, 2007, 2:07 PM
 */

package gpsSelectBoxOptions;

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
 * *
 * I get info to prepare to delete a selected Select Box Option
 ** 
 * 09/01/2007   DES
 * Modified to use Ajax techniques to build and update
 * dynamic select boxes to collect the
 * product line, family, and subfamily data in the
 * deleting of select box options in ps_select_boxes.
 *
 */
public class gpsodf3 extends HttpServlet {
    
    private final String SERVLET_NAME = this.getClass().getName();
    private final String VERSION = "1.5.01";
   
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
     
        String selectBoxName = request.getParameter("selectBoxName");
        if (selectBoxName == null) {
            response.sendRedirect ("gpsnull.htm");
            return;
        }
        String familyCode = request.getParameter("familyCode");
        if (familyCode == null) {
            response.sendRedirect ("gpsnull.htm");
            return;
        }
        String subfamilyCode = request.getParameter("subfamilyCode");
        if (subfamilyCode == null) {
            response.sendRedirect ("gpsnull.htm");
            return;
        }
        String displayOrder = request.getParameter("displayOrder");
        if (displayOrder == null) {
            response.sendRedirect ("gpsnull.htm");
            return;
        }
    
        // Build query to extract existing Select Box Info Line codes from the database
    
        try {
            GPSselectBox selectBox = new GPSselectBox();
            if (selectBox.open(conn, familyCode, subfamilyCode, selectBoxName) > -1) {
                String dataType = selectBox.getDataType();
                ArrayList optionList = selectBox.getArrayList();
                int index = selectBox.getDisplayOrderIndexOf(displayOrder);
                String maximum = selectBox.getMaximum();
                if (maximum.length() == 0) {
                    maximum = "(none)";
                }
                String minimum = selectBox.getMinimum();
                if (minimum.length() == 0) {
                    minimum = "(none)";
                }
                String optionText = selectBox.getOptionText(index);
                String optionValue1 = selectBox.getOptionValue1(index);
                String optionValue2 = selectBox.getOptionValue2(index);
                String optionImage = selectBox.getOptionImage(index);
                String imagePath = "";
                String imageURLBase = "";
                String fileStatus = "";
                if (!optionImage.equals("")) {
                    imagePath = getServletContext().getInitParameter("imagePath");
                    imageURLBase = getServletContext().getInitParameter("imageURLBase");
                    
                    imagePath += familyCode.toLowerCase() + "\\";
                    imageURLBase += familyCode.toLowerCase() + "/";
                    if (!subfamilyCode.equals("") && !subfamilyCode.equals("*")) {
                        imagePath += subfamilyCode.toLowerCase() + "\\";
                        imageURLBase += subfamilyCode.toLowerCase() + "/";
                    }
                    debug (debugLevel, 0, "Image URL Base is " + imageURLBase);
                    debug (debugLevel, 0, "Image Path is " + imagePath);
                    debug (debugLevel, 0, "FQ File Name is " + imagePath + optionImage);
                    //String work = imagePath + optionImage;
                    //work = work.substring(7);
                    //debug (debugLevel, 0, "work is " + work);
                    //File fName = new File(work);
                    //if (fName.exists()) {
                       // debug (debugLevel, 0, "Name exists.");
                    //} else {
                    //    debug (debugLevel, 0, "Name does not exist.");
                    //}
                    //if (fName.isFile()) {
                    //    debug (debugLevel, 0, "Is a file.");
                    //} else {
                    //    debug (debugLevel, 0, "Is not a file.");
                    //}
                    //if (!fName.exists() || !fName.isFile()) {
                    //    fileStatus = " *Not Found*";
                    //}
                    //fName = null;
                }
                String optionDefault = "False";
                if (selectBox.getOptionDefault() == index) {
                    optionDefault = "True";
                }
          
                // Add the following dudettes to the Request Object
                
                request.setAttribute("dataType", dataType);
                request.setAttribute("displayOrder", displayOrder);
                request.setAttribute("familyCode", familyCode);
                request.setAttribute("familyName", (String) session.getAttribute("sbFamilyName"));
                request.setAttribute("fileStatus", fileStatus);
                request.setAttribute("imagePath", imagePath);
                request.setAttribute("imageURLBase", imageURLBase);
                request.setAttribute("maximum", maximum);
                request.setAttribute("minimum", minimum);
                request.setAttribute("optionList", optionList); 
                request.setAttribute("optionText", optionText);
                request.setAttribute("optionImage", optionImage);
                request.setAttribute("optionValue1", optionValue1);
                request.setAttribute("optionValue2", optionValue2);
                request.setAttribute("optionDefault", optionDefault);
                request.setAttribute("selectBoxName", selectBoxName);
                request.setAttribute("statusMessage", "");
                request.setAttribute("subfamilyCode", subfamilyCode);
                request.setAttribute("subfamilyName", (String) session.getAttribute("sbSubfamilyName"));
            
                RequestDispatcher view = request.getRequestDispatcher("gpsodf3.jsp");
                view.forward(request,response);
            } else {
                conn.close();
                sWork = uStamp + " failed to obtain Select Box Info for select box "
                        + familyCode + "/" + subfamilyCode + "/" + selectBoxName;
                debug (debugLevel, 0, sWork);
                request.setAttribute("message", sWork);
                RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
                view.forward(request,response);
                return;  
            }
        } catch (Exception e) {
            sWork = uStamp + " unexpected error " + e;
            debug (debugLevel, 0, sWork);
            request.setAttribute("message", sWork);
            e.printStackTrace();
            RequestDispatcher view = request.getRequestDispatcher("showMessage.jsp");
            view.forward(request,response);
            session.setAttribute("sessionOptionList", null);
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
