package org.informagen.oswf.impl.loaders;


/**
 *
 *  Stores the Workflow Description as XML in the database instead of as a resouce
 */
 
public class HibernateProcessDescription  {

    int id = -1;
    String workflowName;
    String content;

    //~ Constructors ///////////////////////////////////////////////////////////

    public HibernateProcessDescription() {}


    //~ Methods ////////////////////////////////////////////////////////////////

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("id=").append(getId()).append(", ")
              .append("workflowName=").append(getWorkflowName());
    
        return buffer.toString();    
    }

}
