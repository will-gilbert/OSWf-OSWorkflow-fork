package org.informagen.oswf.descriptors;


import org.informagen.oswf.util.WorkflowXMLParser;

import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;
import org.informagen.oswf.Validatable;
 
import org.informagen.oswf.util.ValidationHelper;
import org.informagen.oswf.util.XMLHelper;

import org.w3c.dom.*;

import org.xml.sax.*;

import java.io.*;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Describes a single workflow
 *
 */
 
public class WorkflowDescriptor extends AbstractDescriptor implements Validatable {

    // Class constants
    public static final String XML_HEADER = "<?xml version='1.0' encoding='UTF-8'?>";
    public static final String DOCTYPE_DECL = "<!DOCTYPE workflow SYSTEM 'OSWf-3.0.dtd'>";

    // Instance variables
    protected ConditionsDescriptor globalConditions = null;
    protected final Map<String,String> metaAttributes = new HashMap<>();
    protected final List<ActionDescriptor> commonActionsList = new ArrayList<>();
    protected final List<ActionDescriptor> globalActions = new ArrayList<>();
    protected final List<ActionDescriptor> initialActions = new ArrayList<>();
    protected final List<JoinDescriptor> joins = new ArrayList<JoinDescriptor>();
    protected final List<RegisterDescriptor> registers = new ArrayList<>();
    protected final List<SplitDescriptor> splits = new ArrayList<>();
    protected final List<StepDescriptor> steps = new ArrayList<>();
    protected final Map<Integer,ActionDescriptor> commonActions = new HashMap<>();
    protected final Map<Integer,FunctionDescriptor> triggerFunctions = new HashMap<>();

    protected String workflowName = null;
    protected String version = null;
    protected String description = null;

    // C O N S T R U C T O R  -----------------------------------------------------------------

    public WorkflowDescriptor(Element workflowElement) {
        
        // OPTIONAL: version  =================================================================

        this.version = workflowElement.getAttribute("version");

        // OPTIONAL: workflow -> description  =================================================
 
        this.description = XMLHelper.getChildText(workflowElement, "description");

        // OPTIONAL: workflow -> meta[]  ======================================================
 
        List<Element> metaElements = XMLHelper.getChildElements(workflowElement, "meta");
        for (Element metaElement : metaElements) 
            this.metaAttributes.put(metaElement.getAttribute("name"), XMLHelper.getText(metaElement));
       
        // OPTIONAL: workflow -> registers -> registor[]  ======================================
        
        Element registersElement = XMLHelper.getChildElement(workflowElement, "registers");

        if (registersElement != null) {
            
            List<Element> registerElements = XMLHelper.getChildElements(registersElement, "register");

            for (Element registerElement : registerElements) {
                RegisterDescriptor registerDescriptor = factory.createRegisterDescriptor(registerElement);
                registerDescriptor.setParent(this);
                this.registers.add(registerDescriptor);
            }
        }

        // OPTIONAL: workflow -> global-conditions -> condition -> condition[]  ===============
        
        Element globalConditionsElement = XMLHelper.getChildElement(workflowElement, "global-conditions");

        if (globalConditionsElement != null) {
            Element conditionsElement = XMLHelper.getChildElement(globalConditionsElement, "conditions");

            ConditionsDescriptor conditionsDescriptor = factory.createConditionsDescriptor(conditionsElement);
            conditionsDescriptor.setParent(this);
            this.globalConditions = conditionsDescriptor;
        }

        // REQUIRED: workflow -> initial-actions -> action[]  =================================
        
        Element intial_actionsElement = XMLHelper.getChildElement(workflowElement, "initial-actions");
        List<Element> initialActionElements = XMLHelper.getChildElements(intial_actionsElement, "action");

        for (Element initialActionElement : initialActionElements) {
            ActionDescriptor actionDescriptor = factory.createActionDescriptor(initialActionElement);
            actionDescriptor.setParent(this);
            this.initialActions.add(actionDescriptor);
        }

        // OPTIONAL: workflow -> global-actions -> action[]  ==================================
         
        Element global_actionsElement = XMLHelper.getChildElement(workflowElement, "global-actions");

        if (global_actionsElement != null) {
            List<Element> globalActionElements = XMLHelper.getChildElements(global_actionsElement, "action");

            for (Element globalActionElement : globalActionElements) {
                ActionDescriptor actionDescriptor = factory.createActionDescriptor(globalActionElement);
                actionDescriptor.setParent(this);
                this.globalActions.add(actionDescriptor);
            }
        }

        // OPTIONAL: workflow -> common-actions -> action[]  ==================================
        
        //   - Store actions in HashMap for now. When parsing Steps, we'll resolve
        //      any common actions into local references
        
        Element common_actionsElement = XMLHelper.getChildElement(workflowElement, "common-actions");

        if (common_actionsElement != null) {
            List<Element> commonActionElements = XMLHelper.getChildElements(common_actionsElement, "action");

            for (Element commonActionElement : commonActionElements) {
                ActionDescriptor actionDescriptor = factory.createActionDescriptor(commonActionElement);
                actionDescriptor.setParent(this);
                addCommonAction(actionDescriptor);
            }
        }

        // OPTIONAL: workflow -> trigger-functions -> trigger-function[]  =======================
        
        Element trigger_functionsElement = XMLHelper.getChildElement(workflowElement, "trigger-functions");

        if (trigger_functionsElement != null) {
            List<Element> triggerFunctionElements = XMLHelper.getChildElements(trigger_functionsElement, "trigger-function");

            for (Element timerFunctionElement : triggerFunctionElements) {
                Integer id = new Integer(timerFunctionElement.getAttribute("id"));
                FunctionDescriptor function = factory.createFunctionDescriptor(XMLHelper.getChildElement(timerFunctionElement, "function"));
                function.setParent(this);
                this.triggerFunctions.put(id, function);
            }
        }

        // REQUIRED: workflow -> steps -> step{}  =============================================
        
        Element stepsElement = XMLHelper.getChildElement(workflowElement, "steps");
        List<Element> stepElements = XMLHelper.getChildElements(stepsElement, "step");

        for (Element stepElement : stepElements) {
            StepDescriptor stepDescriptor = factory.createStepDescriptor(stepElement, this);
            this.steps.add(stepDescriptor);
        }

        // OPTIONAL: workflow  splits -> split[]  =============================================
         
        Element splitsElement = XMLHelper.getChildElement(workflowElement, "splits");

        if (splitsElement != null) {
            List<Element> splitElements = XMLHelper.getChildElements(splitsElement, "split");

            for (Element splitElement : splitElements) {
                SplitDescriptor splitDescriptor = factory.createSplitDescriptor(splitElement);
                splitDescriptor.setParent(this);
                this.splits.add(splitDescriptor);
            }
        }

        // OPTIONAL: workflow -> joins -> join[]  =============================================
        
        Element joinsElement = XMLHelper.getChildElement(workflowElement, "joins");

        if (joinsElement != null) {
            List<Element> joinElements = XMLHelper.getChildElements(joinsElement, "join");

            for (Element joinElement : joinElements) {
                JoinDescriptor joinDescriptor = factory.createJoinDescriptor(joinElement);
                joinDescriptor.setParent(this);
                this.joins.add(joinDescriptor);
            }
        }
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public ActionDescriptor getAction(int id) {
        
        // check global actions
        for (Iterator iterator = globalActions.iterator(); iterator.hasNext();) {
            ActionDescriptor actionDescriptor = (ActionDescriptor) iterator.next();

            if (actionDescriptor.getId() == id) {
                return actionDescriptor;
            }
        }

        // check steps
        for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
            StepDescriptor stepDescriptor = (StepDescriptor) iterator.next();
            ActionDescriptor actionDescriptor = stepDescriptor.getAction(id);

            if (actionDescriptor != null) {
                return actionDescriptor;
            }
        }

        //check initial actions, which we now must have unique id's
        for (Iterator iterator = initialActions.iterator(); iterator.hasNext();) {
            ActionDescriptor actionDescriptor = (ActionDescriptor) iterator.next();

            if (actionDescriptor.getId() == id) {
                return actionDescriptor;
            }
        }

        return null;
    }

    /**
     * Get a Map of the common actions specified, keyed on actionId (an Integer)
     * @return A list of {@link ActionDescriptor} objects
     */

    public Map<Integer,ActionDescriptor> getCommonActions() {
        return commonActions;
    }

    /**
     * Get a List of the global actions specified
     * @return A list of {@link ActionDescriptor} objects
     */

    public List<ActionDescriptor> getGlobalActions() {
        return globalActions;
    }

    public ConditionsDescriptor getGlobalConditions() {
        return globalConditions;
    }

    public ActionDescriptor getInitialAction(int id) {
        for (Iterator iterator = initialActions.iterator(); iterator.hasNext();) {
            ActionDescriptor actionDescriptor = (ActionDescriptor) iterator.next();

            if (actionDescriptor.getId() == id) {
                return actionDescriptor;
            }
        }

        return null;
    }

    /**
     * Get a List of initial steps for this workflow
     * @return A list of {@link ActionDescriptor} objects
     */

    public List<ActionDescriptor> getInitialActions() {
        return initialActions;
    }

    public JoinDescriptor getJoin(int id) {
        for (Iterator iterator = joins.iterator(); iterator.hasNext();) {
            JoinDescriptor joinDescriptor = (JoinDescriptor) iterator.next();

            if (joinDescriptor.getId() == id) {
                return joinDescriptor;
            }
        }

        return null;
    }

    /**
     * Get a List of initial steps for this workflow
     * @return A list of {@link JoinDescriptor} objects
     */

    public List<JoinDescriptor> getJoins() {
        return joins;
    }

    public Map getMetaAttributes() {
        return metaAttributes;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public List<RegisterDescriptor> getRegisters() {
        return registers;
    }

    public SplitDescriptor getSplit(int id) {
        
        for(SplitDescriptor splitDescriptor : splits) {
            if (splitDescriptor.getId() == id) {
                return splitDescriptor;
            }
        }

        return null;
    }

    /**
     * Get a List of initial steps for this workflow
     * @return A list of {@link SplitDescriptor} objects
     */
    public List<SplitDescriptor> getSplits() {
        return splits;
    }

    public StepDescriptor getStep(int id) {

        for(StepDescriptor step : steps) {
            if (step.getId() == id) {
                return step;
            }
        }

        return null;
    }

    /**
     * Get a List of steps in this workflow
     * @return a List of {@link StepDescriptor} objects
     */
    public List<StepDescriptor> getSteps() {
        return steps;
    }

    public FunctionDescriptor getTriggerFunction(int id) {
        return triggerFunctions.get(id);
    }

    /**
     * Get a Map of all trigger functions in this workflow
     * @return a Map with Integer keys and {@link FunctionDescriptor} values
     */
    public Map<Integer,FunctionDescriptor> getTriggerFunctions() {
        return triggerFunctions;
    }

    /**
     * Add a common action
     * @param descriptor The action descriptor to add
     * @throws IllegalArgumentException if the descriptor's ID already exists in the workflow
     */
    void addCommonAction(ActionDescriptor descriptor) {
        descriptor.setCommon(true);
        addAction(commonActions, descriptor);
        addAction(commonActionsList, descriptor);
    }

    /**
     * Add a global action
     * @param descriptor The action descriptor to add
     * @throws IllegalArgumentException if the descriptor's ID already exists in the workflow
     */
    void addGlobalAction(ActionDescriptor descriptor) {
        addAction(globalActions, descriptor);
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        ValidationHelper.validate(getRegisters());
        ValidationHelper.validate(getTriggerFunctions().values());
        ValidationHelper.validate(getGlobalActions());
        ValidationHelper.validate(getInitialActions());
        ValidationHelper.validate(getCommonActions().values());
        ValidationHelper.validate(getSteps());
        ValidationHelper.validate(getSplits());
        ValidationHelper.validate(getJoins());

        Set<Integer> globalActionIds = new HashSet<Integer>();

        for(ActionDescriptor action :  globalActions)
            globalActionIds.add(new Integer(action.getId()));

        for(StepDescriptor step : getSteps()) {
            for(ActionDescriptor stepAction : step.getActions()) {

                // Check to see if it's a common action (dups are ok, since that's the point of common actions)
                if (!stepAction.isCommon()) {
                    if (!globalActionIds.add(stepAction.getId())) {
                        throw new InvalidWorkflowDescriptorException("Duplicate occurance of action ID '" + stepAction.getId() + "' found in step " + step.getId());
                    }
                }
            }
        }

        // Now we have all our unique actions, let's check that no common action id's exist in them
        for(Integer commonActionId : commonActions.keySet()) {
            if (globalActionIds.contains(commonActionId)) {
                throw new InvalidWorkflowDescriptorException("common-action ID '" + commonActionId + "' is duplicated in a step action");
            }
        }

        // Check the initial-actions
        for(ActionDescriptor initialAction : initialActions) {
            if (globalActionIds.contains(initialAction.getId())) {
                throw new InvalidWorkflowDescriptorException("initial-action ID '" + initialAction.getId() + "' is duplicated in a step action");
            }
        }

        //validateDTD();
    }


    private void addAction(List<ActionDescriptor> actionsList, ActionDescriptor descriptor) {

        if (getAction(descriptor.getId()) != null) {
            throw new IllegalArgumentException("action with id " + descriptor.getId() + " already exists for this step.");
        }

        actionsList.add(descriptor);
    }

    private void addAction(Map<Integer,ActionDescriptor> actionsMap, ActionDescriptor descriptor) {

        if (getAction(descriptor.getId()) != null) {
            throw new IllegalArgumentException("action with id " + descriptor.getId() + " already exists for this step.");
        }

        actionsMap.put(new Integer(descriptor.getId()), descriptor);
    }
    
}
