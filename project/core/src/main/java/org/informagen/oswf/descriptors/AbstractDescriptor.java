package org.informagen.oswf.descriptors;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;


public abstract class AbstractDescriptor implements Serializable {

    private AbstractDescriptor parent;
    private boolean hasId = false;
    private int id;
    
    protected final DescriptorFactory factory = DescriptorFactory.getInstance();

    // M E T H O D S  -------------------------------------------------------------------------

    public void setId(int id) {  
        this.id = id;
        hasId = true;
    }

    public int getId() {
        return id;
    }

    public void setParent(AbstractDescriptor parent) {
        this.parent = parent;
    }

    public AbstractDescriptor getParent() {
        return parent;
    }

    public boolean hasId() {  
        return hasId;
    }
}
