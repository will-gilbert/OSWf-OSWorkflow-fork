package org.informagen.oswf.impl.stores;

import org.informagen.oswf.typedmap.TypedMap;

import org.informagen.oswf.exceptions.QueryNotSupportedException;
import org.informagen.oswf.exceptions.WorkflowStoreException;

import org.informagen.oswf.WorkflowStore;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.Step;

import org.informagen.oswf.TypedMapStore;
import org.informagen.oswf.impl.MemoryTypedMapStore;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

// Java Collections
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;


/**
 * Needed to use a copy from the OSWorkflow hibernate SPI in order to work around
 *  in accessible private methods
 */
public abstract class AbstractWorkflowStore implements WorkflowStore {

    private static final Marker fatal = MarkerFactory.getMarker("FATAL");
    private static final Logger logger = LoggerFactory.getLogger(AbstractWorkflowStore.class);
                                                                                               
    private TypedMapStore typedMapStore;

    // AbstractWorkflowStore methods /////////////////////////////////////////////////

    protected AbstractWorkflowStore() {}

    public AbstractWorkflowStore(Map<String,String> config, Map<String,Object> args) {
        setTypedMapStore((TypedMapStore)args.get("typedMapStore"));
    }

    public void setTypedMapStore(TypedMapStore typedMapStore) {
        this.typedMapStore = typedMapStore;
    }

    public TypedMapStore getTypedMapStore() {

        if(typedMapStore == null) {
            typedMapStore = new MemoryTypedMapStore();
            logger.warn("TypedMapStore not defined, using MemoryPropertySetStore");
        }

        return typedMapStore;
    }

    public void setEntryState(final long piid, final ProcessInstanceState state) throws WorkflowStoreException {
 
        final ProcessInstance pi = findProcessInstance(piid);
 
        if(pi != null) {
            pi.setState(state);
            logger.debug("AbstractWorkflowStore.setEntryState: " + pi.toString());
        }
    }

    public TypedMap getTypedMap(long entryId) {
        return getTypedMapStore().getTypedMap(entryId);
    }

    public Step markFinished(Step step, int actionId, Date finishDate, String status, String actor) throws WorkflowStoreException {
    
        step.setActionId(actionId);
        step.setFinishDate(finishDate);
        step.setStatus(status);
        step.setActor(actor);
        
        logger.debug("AbstractWorkflowStore.markFinished: " + step.toString());
    
        return step;
    }

 
}
