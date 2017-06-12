package org.informagen.oswf.impl.stores;

import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.Step;

import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.PersistentVarsFactory;

import org.informagen.oswf.impl.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.*;

import java.util.*;


/**
 * Simple flat file implementation.
 *
 * Following properties are <b>required</b>:
 * <ul>
 * <li><b>storeFile</b> - the absolute path to the store file
 * </ul>
 *
 * @author <a href="mailto:gbort@msn.com">Guillaume Bort</a>
 */
public class SerializableStore extends MemoryWorkflowStore {
    //~ Static fields/initializers /////////////////////////////////////////////

    protected static final Marker fatal = MarkerFactory.getMarker("FATAL");
    protected static final Logger logger = LoggerFactory.getLogger(SerializableStore.class);

    static String storeFile;

    // M E T H O D S  -------------------------------------------------------------------------

    public SerializableStore(Map<String,String> props, Map<String,Object> args) {
        super(props, args);
        
        storeFile = (String) props.get("storeFile");

        // check whether the file denoted by the storeFile property is a normal file.
        if (!new File(storeFile).isFile()) {
            logger.error(fatal, "storePath property should indicate a normal file");
        }

        // check wheter the directory containing the storeFile exist
        if (!new File(storeFile).getParentFile().exists()) {
            logger.error(fatal, "directory " + new File(storeFile).getParent() + " not found");
        }
        
    }


    public PersistentVars getPersistentVars(long entryId) {

        PersistentVars persistentVars = (PersistentVars) SerializableCache.getInstance().propertySetCache.get(new Long(entryId));

        if (persistentVars == null) {
            persistentVars = PersistentVarsFactory.getInstance().createPersistentVars("serializable", null);
            SerializableCache.getInstance().propertySetCache.put(new Long(entryId), persistentVars);
        }

        return persistentVars;
    }

    public static void setStoreFile(String storeFile) {
        SerializableStore.storeFile = storeFile;
    }

    public static String getStoreFile() {
        return storeFile;
    }

    public Step createCurrentStep(long entryId, int stepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) {
        long id = SerializableCache.getInstance().globalStepId++;
        DefaultStep step = new DefaultStep(id, entryId, stepId, 0, owner, startDate, dueDate, null, status, previousIds, null);

        List currentSteps = (List) SerializableCache.getInstance().currentStepsCache.get(new Long(entryId));

        if (currentSteps == null) {
            currentSteps = new ArrayList();
            SerializableCache.getInstance().currentStepsCache.put(new Long(entryId), currentSteps);
        }

        currentSteps.add(step);
        SerializableCache.store();

        return step;
    }

    public ProcessInstance createEntry(String workflowName) {
        long id = SerializableCache.getInstance().globalEntryId++;
        DefaultProcessInstance entry = new DefaultProcessInstance(id, workflowName);
        SerializableCache.getInstance().entryCache.put(new Long(id), entry);
        SerializableCache.store();

        return entry;
    }

    public List findCurrentSteps(long entryId) {
        List currentSteps = (List) SerializableCache.getInstance().currentStepsCache.get(new Long(entryId));

        if (currentSteps == null) {
            currentSteps = new ArrayList();
            SerializableCache.getInstance().currentStepsCache.put(new Long(entryId), currentSteps);
        }

        return currentSteps;
    }

    public ProcessInstance findProcessInstance(long entryId) {
        return (ProcessInstance) SerializableCache.getInstance().entryCache.get(new Long(entryId));
    }

    public List findHistorySteps(long entryId) {
        List historySteps = (List) SerializableCache.getInstance().historyStepsCache.get(new Long(entryId));

        if (historySteps == null) {
            historySteps = new ArrayList();
            SerializableCache.getInstance().historyStepsCache.put(new Long(entryId), historySteps);
        }

        return historySteps;
    }


    public Step markFinished(Step step, int actionId, Date finishDate, String status, String actor) {

        List<DefaultStep> currentSteps = (List<DefaultStep>) SerializableCache.getInstance().currentStepsCache.get(step.getProcessInstanceId());
        
        for (DefaultStep theStep : currentSteps) {

            if (theStep.getId() == step.getId()) {
                theStep.setStatus(status);
                theStep.setActionId(actionId);
                theStep.setFinishDate(finishDate);
                theStep.setActor(actor);
                return theStep;
            }
        }

        SerializableCache.store();

        return null;
    }

    public void moveToHistory(Step step) {

        List<Step> currentSteps = (List<Step>) SerializableCache.getInstance().currentStepsCache.get(step.getProcessInstanceId());
        List<Step> historySteps = (List<Step>) SerializableCache.getInstance().historyStepsCache.get(step.getProcessInstanceId());

        if (historySteps == null) {
            historySteps = new ArrayList();
            SerializableCache.getInstance().historyStepsCache.put(step.getProcessInstanceId(), historySteps);
        }

        DefaultStep simpleStep = (DefaultStep) step;

        for (Step currentStep :  currentSteps) {

            if (simpleStep.getId() == currentStep.getId()) {
                // iterator.remove();
                currentSteps.remove(currentStep);
                historySteps.add(simpleStep);

                break;
            }
        }

        SerializableCache.store();
    }
}


class SerializableCache implements Serializable {

    protected static final Marker fatal = MarkerFactory.getMarker("FATAL");

    //~ Static fields/initializers /////////////////////////////////////////////

    private static transient SerializableCache instance;

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    HashMap currentStepsCache;
    HashMap entryCache;
    HashMap historyStepsCache;
    HashMap propertySetCache;
    long globalEntryId = 1;
    long globalStepId = 1;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    private SerializableCache() {
        entryCache = new HashMap();
        currentStepsCache = new HashMap();
        historyStepsCache = new HashMap();
        propertySetCache = new HashMap();
    }

    // M E T H O D S  -------------------------------------------------------------------------

    // public List query(WorkflowQuery query) {
    //     // not implemented
    //     return Collections.EMPTY_LIST;
    // }

    static SerializableCache getInstance() {
        if (instance == null) {
            instance = load();
        }

        return instance;
    }

    static SerializableCache load() {
        try {
            FileInputStream fis = new FileInputStream(new File(SerializableStore.storeFile));
            ObjectInputStream ois = new ObjectInputStream(fis);
            SerializableCache o = (SerializableCache) ois.readObject();
            fis.close();

            return o;
        } catch (Exception e) {
            SerializableStore.logger.error(fatal, "cannot store in file " + SerializableStore.storeFile + ". Create a new blank store.");
        }

        return new SerializableCache();
    }

    static void store() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(SerializableStore.storeFile));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(getInstance());
            fos.close();
        } catch (Exception e) {
            SerializableStore.logger.error(fatal, "cannot store in file " + SerializableStore.storeFile + ".");
        }
    }
}
