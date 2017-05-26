package org.informagen.oswf.simulator.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProcessInstance implements IsSerializable{

    public long piid;
    public String state;
    public int currentStepCount;
    public int historyStepCount;

    public ProcessInstance(){};
    public ProcessInstance(long piid) {
        this.piid = piid;
    };
    
    public ProcessInstance piid(long piid) { this.piid = piid; return this;}
    public ProcessInstance state(String state) { this.state = state; return this;}
    public ProcessInstance currentStepCount(int currentStepCount) { this.currentStepCount = currentStepCount; return this;}
    public ProcessInstance historyStepCount(int historyStepCount) { this.historyStepCount = historyStepCount; return this;}

    public int hashCode() {
        return new Long(piid).hashCode();
    }

    public boolean equals(Object object) {
        if(object instanceof ProcessInstance == false)
            return false;
       
       return piid == ((ProcessInstance)object).piid;
    }
   
}
