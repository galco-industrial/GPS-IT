/*
 * DateTime.java
 *
 * Created on October 31, 2006, 6:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sauter.util;

import java.util.*;
import java.text.*;

/**
 *
 * @author Sauter
 */
public class DateTime {
    
    private static final String DATE_FORMAT = "MM/dd/yy";
    private static final String DATE_FORMAT2 = "yy/MM/dd";
    private static final String DATE_FORMAT3 = "yyyy-MM-dd";
    
    /** Creates a new instance of DateTime */
    public DateTime() {
    }
 
    public static String getDateMMDDYY() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Detroit"));
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("America/Detroit"));
        String date = sdf.format(cal.getTime());
        return date;
    }
    
    public static String getDateYYMMDD() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Detroit"));
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT2);
        sdf.setTimeZone(TimeZone.getTimeZone("America/Detroit"));
        String date = sdf.format(cal.getTime());
        return date;
    }
    
    public static String getDateYYYYMMDD() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Detroit"));
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT3);
        sdf.setTimeZone(TimeZone.getTimeZone("America/Detroit"));
        String date = sdf.format(cal.getTime());
        return date;
    }
        
    public static String getTimeHHMMSS(String sep) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Detroit"));
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("America/Detroit"));
        String time = Integer.toString(cal.get(Calendar.HOUR_OF_DAY) + 100).substring(1) + sep +
            Integer.toString(cal.get(Calendar.MINUTE) + 100).substring(1) + sep +
            Integer.toString(cal.get(Calendar.SECOND) + 100).substring(1);
        return time;
    }
           
    public static String getTimeRawStr() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Detroit"));
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("America/Detroit"));
        String timeRaw = Integer.toString(getSecondsSinceMidnight(cal));
        return timeRaw;
    }
    
    public static String getTimeStamp() {  // Format "MM/DD/YYYY HH:MM:SS.SSS""
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Detroit"));
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("America/Detroit"));
        String date = sdf.format(cal.getTime());
        date = date.substring(0,6) + "20" + date.substring(6,8);
        String timeStamp = date + " " + Integer.toString(cal.get(Calendar.HOUR_OF_DAY) + 100).substring(1) + ":" +
            Integer.toString(cal.get(Calendar.MINUTE) + 100).substring(1) + ":" +
            Integer.toString(cal.get(Calendar.SECOND) + 100).substring(1) + ".000";
        return timeStamp;
    }
 
    public static int getSecondsSinceMidnight() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Detroit"));
        int secs = getSecondsSinceMidnight(cal);
        return secs;
    }
    
    public static int getSecondsSinceMidnight(Calendar cal) {
        int secs = cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND);
        return secs;
    }
    
}
