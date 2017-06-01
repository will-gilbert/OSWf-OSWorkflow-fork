package org.informagen.oswf.simulator.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.lang.Comparable;


public class ProcessDescriptorSummary implements Comparable<ProcessDescriptorSummary>, IsSerializable {

    public String name;
    public int initialActionCount;
    public int stepCount;
    public int splitCount;
    public int joinCount;
    public int piCount;


    public ProcessDescriptorSummary() {}
 
    public ProcessDescriptorSummary name(String name) {this.name = name; return this; }
    public ProcessDescriptorSummary initialActionCount(int initialActionCount) {this.initialActionCount = initialActionCount; return this; }
    public ProcessDescriptorSummary stepCount(int stepCount) {this.stepCount = stepCount; return this; }
    public ProcessDescriptorSummary splitCount(int splitCount) {this.splitCount = splitCount; return this; }
    public ProcessDescriptorSummary joinCount(int joinCount) {this.joinCount = joinCount; return this; }
    public ProcessDescriptorSummary piCount(int piCount) {this.piCount = piCount; return this; }

    public boolean equals(Object object) {
        
        if(((object == null) || !(object instanceof ProcessDescriptorSummary)))
            return false;
        
        ProcessDescriptorSummary that = (ProcessDescriptorSummary)object;
        return this.name.equals(that.name);
    }

    // this < that => -1
    // this > that => +1
    // return 0 otherwise
    
    public int compareTo(ProcessDescriptorSummary that) throws ClassCastException {
        
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
