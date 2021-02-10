/*
 * GPSmatchItem.java
 *
 * Created on December 3, 2006, 3:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gps.util;

/**
 *
 * @author Sauter
 */
public class GPSmatchItem implements Comparable<GPSmatchItem> {
        
    private boolean debugSw = false;
    private static final String version = "1.5.00";
    
    private long available;
    private String head1;
    private String head2;
    private String head3;
    private String head4;
    private String parm1;
    private String parm2;
    private String parm3;
    private String parm4;
    private String partNum;
    private float rank;
        
    /** Creates a new instance of GPSmatchItem */
    public GPSmatchItem() {
    }
    
    public GPSmatchItem(String aPartNum, float aRank, long aAvailable, 
            String aHead1, String aParm1, String aHead2, String aParm2, 
            String aHead3, String aParm3, String aHead4, String aParm4) {
        
        available = aAvailable;
        partNum = aPartNum;
        rank = aRank;
        
        head1 = aHead1;
        head2 = aHead2;
        head3 = aHead3;
        head4 = aHead4;
        parm1 = aParm1;
        parm2 = aParm2;
        parm3 = aParm3;
        parm4 = aParm4;
    }
    
    public int compareTo(GPSmatchItem o ) {
        int diff;
        diff = Float.compare(rank, o.getRank());
        if (diff != 0) {
            return diff;
        }
        diff = partNum.compareTo(o.getPartNum());
        return diff;
    }
        
    public long getAvailable() {
        return available;
    }
    
    public String getHead1() {
        return head1;
    }
    
    public String getHead2() {
        return head2;
    }
    
    public String getHead3() {
        return head3;
    }
    
    public String getHead4() {
        return head4;
    }
    
    public String getParm1() {
        return parm1;
    }
    
    public String getParm2() {
        return parm2;
    }
    
    public String getParm3() {
        return parm3;
    }
    
    public String getParm4() {
        return parm4;
    }
    
    public String getPartNum() {
        return partNum;
    }
    
    public float getRank() {
        return rank;
    }
        
    public void setAvailable(long x) {
        if (x > -1) {
            available = x;
        } else {
            available = 0;
        }
    }
    
    public void setHead1(String x) {
        head1 = x;
    }
    
    public void setHead2(String x) {
        head2 = x;
    }
    
    public void setHead3(String x) {
        head3 = x;
    }
    
    public void setHead4(String x) {
        head4 = x;
    }
 
    public void setParm1(String x) {
        parm1 = x;
    }
    
    public void setParm2(String x) {
        parm2 = x;
    }
    
    public void setParm3(String x) {
        parm3 = x;
    }
    
    public void setParm4(String x) {
        parm4 = x;
    }
    
    public void setPartNum(String x) {
        partNum = x;
    }
    
    public void setRank(float x) {
        rank = x;
    }
    
}
