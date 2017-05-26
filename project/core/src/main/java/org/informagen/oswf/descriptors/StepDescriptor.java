package org.informagen.oswf.descriptors;

import org.informagen.oswf.Validatable;

// OSWf exceptions
import org.informagen.oswf.exceptions.*;
import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;

import org.informagen.oswf.util.ValidationHelper;
import org.informagen.oswf.util.XMLHelper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

import java.util.*;


/**
 * 
 */
public class StepDescriptor extends AbstractDescriptor implements Validatable {
     
    protected final List<ActionDescriptor> actions = new ArrayList<ActionDescriptor>();
    protected final List<Integer> commonActions = new ArrayList<Integer>();
    protected final List<StepConditionDescriptor> stepConditions = new ArrayList<StepConditionDescriptor>();
    protected final List<FunctionDescriptor> postFunctions = new ArrayList<FunctionDescriptor>();
    protected final List<FunctionDescriptor> preFunctions = new ArrayList<FunctionDescriptor>();
    protected final Map<String,String> metaAttributes = new HashMap<String,String>();

    protected final String name;
    protected final boolean hasActions;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    StepDescriptor(Element stepElement) {
        this(stepElement, null);
    }

    StepDescriptor(Element stepElement, AbstractDescriptor parent) {
        
        setParent(parent);
            
        try {
            setId(Integer.parseInt(stepElement.getAttribute("id")));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid step id value " + stepElement.getAttribute("id"));
        }

        this.name = stepElement.getAttribute("name");

        // OPTIONAL: Step 'meta' elements ========================================

        List<Element> metaElements = XMLHelper.getChildElements(stepElement, "meta");
        for (Element metaElement : metaElements) 
            this.metaAttributes.put(metaElement.getAttribute("name"), XMLHelper.getText(metaElement));
        
        // NodeList children = stepElement.getChildNodes();
        // for (int i = 0; i < children.getLength(); i++) {
        //     Node child = children.item(i);
        // 
        //     if (child.getNodeName().equals("meta")) {
        //         String name = ((Element)child).getAttribute("name");
        //         String value = XMLHelper.getText((Element)child);
        //         this.metaAttributes.put(name, value);
        //     }
        // }

        // OPTIONAL: Step 'pre-functions' elements =============================
        
        Element prefunctionsElement = XMLHelper.getChildElement(stepElement, "pre-functions");

        // Get the all 'function' children
        if (prefunctionsElement != null) {
            
            List<Element> preFunctionElements = XMLHelper.getChildElements(prefunctionsElement, "function");

            for (Element preFunctionElement : preFunctionElements) {
                FunctionDescriptor functionDescriptor = factory.createFunctionDescriptor(preFunctionElement);
                functionDescriptor.setParent(this);
                this.preFunctions.add(functionDescriptor);
            }
        }

        // OPTIONAL: Step 'step-conditions' elements ===========================
        
        Element stepConditionsElement = XMLHelper.getChildElement(stepElement, "step-conditions");

        // Get the all 'step-condition' children
        if (stepConditionsElement != null) {
            List<Element> stepConditionElements = XMLHelper.getChildElements(stepConditionsElement, "step-condition");

            for (Element stepConditionElement : stepConditionElements) {
                StepConditionDescriptor stepConditionDescriptor = factory.createStepConditionDescriptor(stepConditionElement);
                stepConditionDescriptor.setParent(this);
                this.stepConditions.add(stepConditionDescriptor);
            }
        }

        // OPTIONAL: Step 'actions'
        Element actionsElement = XMLHelper.getChildElement(stepElement, "actions");
        
        hasActions = (actionsElement != null);

        if (actionsElement != null) {

            List<Element> actionElements = XMLHelper.getChildElements(actionsElement, "action");

            for ( Element actionElement : actionElements) {
                ActionDescriptor actionDescriptor = factory.createActionDescriptor(actionElement);
                actionDescriptor.setParent(this);
                this.actions.add(actionDescriptor);
            }

            // step -> actions -> common-action[]
            List<Element> commonActionElements = XMLHelper.getChildElements(actionsElement, "common-action");

            for (Element commonActionElement : commonActionElements) {
                
                // This steps parent should be the workflow descriptor; Extact all of the
                //   common-action and add them to this steps actions list
                
                WorkflowDescriptor workflowDescriptor = (WorkflowDescriptor)getParent();

                try {
                    Integer actionId = new Integer(commonActionElement.getAttribute("id"));

                    ActionDescriptor commonActionDescriptor = (ActionDescriptor)workflowDescriptor.getCommonActions().get(actionId);

                    if (commonActionDescriptor != null) {
                        this.actions.add(commonActionDescriptor);
                        this.commonActions.add(actionId);
                    }
                    
                } catch (Exception ex) {
                    //logger.warn("Invalid common actionId:" + ex);
                }
            }
        }

        // OPTIONAL: Step 'post-functions' ===========================================
        
        Element postFunctionsElement = XMLHelper.getChildElement(stepElement, "post-functions");

        if (postFunctionsElement != null) {
            List<Element> postFunctionElements = XMLHelper.getChildElements(postFunctionsElement, "function");

            for (Element postFunctionElement : postFunctionElements) {
                FunctionDescriptor functionDescriptor = factory.createFunctionDescriptor(postFunctionElement);
                functionDescriptor.setParent(this);
                this.postFunctions.add(functionDescriptor);
            }
        }
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public ActionDescriptor getAction(int id) {
        
        for (ActionDescriptor action : actions)
            if (action.getId() == id) 
                return action;

        return null;
    }

    /**
     * Get a List of {@link ActionDescriptor}s for this step
     */
    public List<ActionDescriptor> getActions() {
        return actions;
    }

    /**
     * Get a list of common actions.
     * @return a List of Integer action id's.
     */
    public List<Integer> getCommonActions() {
        return commonActions;
    }

    public Map<String,String> getMetaAttributes() {
        return metaAttributes;
    }

    public String getName() {
        return name;
    }

    /**
     * Get a List of {@link StepConditionDescriptor}s for this step
     */
    public List<StepConditionDescriptor> getStepConditions() {
        return stepConditions;
    }

    public List<FunctionDescriptor> getPostFunctions() {
        return postFunctions;
    }

    public List<FunctionDescriptor> getPreFunctions() {
        return preFunctions;
    }

    public boolean resultsInJoin(int join) {
        
        for(ActionDescriptor actionDescriptor : actions) {
            
            if (actionDescriptor.getUnconditionalResult().getJoin() == join) {
                return true;
            }
            
            for(ConditionalResultDescriptor resultDescriptor : actionDescriptor.getConditionalResults()) {
                if (resultDescriptor.getJoin() == join) {
                    return true;
                }
            }
        }

        return false;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        
        if ((commonActions.size() == 0) && (actions.size() == 0) && hasActions) 
            throw new InvalidWorkflowDescriptorException("Step '" + name + "' actions element must contain at least one action or common-action");

        ValidationHelper.validate(actions);
        ValidationHelper.validate(stepConditions);
        ValidationHelper.validate(preFunctions);
        ValidationHelper.validate(postFunctions);

        for (Integer actionId : commonActions) {
            try {
                ActionDescriptor commonActionReference = (ActionDescriptor) ((WorkflowDescriptor) getParent()).getCommonActions().get(actionId);

                if (commonActionReference == null) {
                    throw new InvalidWorkflowDescriptorException("Common action " + actionId + " specified in step " + getName() + " does not exist");
                }
            } catch (NumberFormatException ex) {
                throw new InvalidWorkflowDescriptorException("Common action '" + actionId + "' is not a valid action ID");
            }
        }
    }


   protected void init(Element step) {
    }
}
