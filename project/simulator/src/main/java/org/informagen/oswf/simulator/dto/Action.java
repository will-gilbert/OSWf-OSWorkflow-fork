package org.informagen.oswf.simulator.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.lang.Comparable;


/**
 *  Use to transfer both an Initial Action and a Step Action
 *
 */

public class Action implements Comparable<Action>, IsSerializable {
    
    public int actionId;
    public String name;

    public Action() {}
    
    public Action(int actionId, String name) {
        this.actionId = actionId;
        this.name = name;
    }
    
    public Action actionId(int actionId) { this.actionId = actionId; return this; }
    public Action name(String name) { this.name = name; return this; }

    // this < that => -1
    // this > that => +1
    // return 0 otherwise
    
    public int compareTo(Action that) throws ClassCastException {
        
        // Need the next 3 tests in case the database return null objects
        if(this.name == null && that.name == null)
            return 0;

        // We are null and the other is not, we must be less
        if(this.name == null)
            return -1;

        // We are not null and the other is, we must be greater
        if(that.name == null)
            return 1;

        // Finally, compare the two objects by name
        return this.name.compareTo(that.name);
    }
   
}
