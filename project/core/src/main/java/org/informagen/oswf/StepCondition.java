package org.informagen.oswf;


/**
 * Container for OSWf 'step-condition'
 *
 */
public class StepCondition {
    
    long stepId;
    String stepName;
    String name;
    boolean value;
    
    private StepCondition() {}
    
    public StepCondition(long stepId, String name) {
        this(stepId, name, false);
    }
    
    public StepCondition(long stepId, String name, boolean value) {
        this.stepId = stepId;
        this.name = name;
        this.value = value;
    }
 
    public long getStepId() {
        return stepId;
    }
   
    public String getName() {
        return name;
    }
    
    public boolean getValue() {
        return value;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }


    public int hashCode() {
        return new StringBuffer()
            .append(stepId)
            .append(name)
            .toString()
        .hashCode();
    }

    @Override
    public boolean equals(Object object) {

        if (object != null && (object instanceof StepCondition)) {
            StepCondition that = (StepCondition)object;
            return this.getName().equals(that.getName()) && 
                  (this.getStepId() == that.getStepId())
            ;
        }

        return false;        
    }



}
