/*
 * GPSsubfamily.java
 *
 * Created on February 15, 2007, 3:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

/**
 *
 * @author Sauter
 */
public class GPSsubfamily {
    
    private boolean debugSw = false;
    
    private String familyCode;
    private String subfamilyCode;
    private String subfamilyName;
    private int displayOrder;
    
    /** Creates a new instance of GPSsubfamily */
    public GPSsubfamily() {
    }
    
    public int getDisplayOrder() {
        return displayOrder;
    }
    
    public String getFamilyCode() {
        return familyCode;
    }
    
    public String getSubfamilyCode() {
        return subfamilyCode;
    }
    
    public String getSubfamilyName() {
        return subfamilyName;
    }

    public void setDisplayOrder(int order) {
        displayOrder = order > -1 && order < 10000 ? order : 0;
    }

    public void setFamilyCode(String code) {
        familyCode = code;
    }

}
