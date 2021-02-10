/*
 * Convert.java
 *
 * Created on November 1, 2006, 2:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sauter.util;

/**
 *
 * @author Sauter
 */
public class Convert {
    
    /** Creates a new instance of Convert */
    public Convert() {
    }
  
    public static boolean checkDoubleRange(String item, double min, double max) {
        double x;
        if (isDouble(item)) {
            x = Double.parseDouble(item);
            if (x >= min && x <= max) {
                return true;
            }
        }
        return false;
    }
        
    public static boolean checkFloatRange(String item, float min, float max) {
        float x;
        if (isFloat(item)) {
            x = Float.parseFloat(item);
            if (x >= min && x <= max) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIntegerRange(String item, int min, int max) {
        int x;
        if (isInteger(item)) {
            x = Integer.parseInt(item);
            if (x >= min && x <= max) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean checkLongRange(String item, long min, long max) {
        long x;
        if (isLong(item)) {
            x = Long.parseLong(item);
            if (x >= min && x <= max) {
                return true;
            }
        }
        return false;
    }
 
    public static float getFloat(String work) {
        if (isFloat(work)) {
            return Float.parseFloat(work);
        } else {
            return 0;
        }
    }
        
    public static boolean isDouble(String work) {
        double f;
        try {
            f = Double.parseDouble(work);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
 
    public static boolean isFloat(String work) {
        float f;
        try {
            f = Float.parseFloat(work);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isInteger(String work) {
        int f;
        try {
            f = Integer.parseInt(work);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
     
    public static boolean isLong(String work) {
        long f;
        try {
            f = Long.parseLong(work);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
