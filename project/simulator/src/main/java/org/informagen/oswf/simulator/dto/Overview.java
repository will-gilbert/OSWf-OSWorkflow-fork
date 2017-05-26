package org.informagen.oswf.simulator.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.lang.Comparable;

public class Overview implements  Comparable<Overview>, IsSerializable {
    
    public Integer id;
    public String stepName;
    public int current;
    public int history;
    
    public long maxPending = 0;
    public long avgPending = 0;

    public String maxPendingDisplay;
    public String avgPendingDisplay;
    
    public Overview id(Integer id) {this.id = id; return this; }
    public Overview stepName(String stepName) {this.stepName = stepName; return this; }
    public Overview current(int current) {this.current = current; return this; }
    public Overview history(int history) {this.history = history; return this; }


    // this < that => -1
    // this > that => +1
    // return 0 otherwise
    
    public int compareTo(Overview that) throws ClassCastException {
        
        // Need the next 3 tests in case the database return null objects
        if(this.id == null && that.id == null)
            return 0;

        // We are null and the other is not, we must be less
        if(this.id == null)
            return -1;

        // We are not null and the other is, we must be greater
        if(that.id == null)
            return 1;

        // Finally, compare the two objects by name
        return this.id.compareTo(that.id);
    }
   
}
