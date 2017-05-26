package org.informagen.oswf.descriptors;

import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;
import org.informagen.oswf.Validatable;

import org.informagen.oswf.util.ValidationHelper;
import org.informagen.oswf.util.XMLHelper;

import org.w3c.dom.Element;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 
 */
public class ResultDescriptor extends AbstractDescriptor implements Validatable {

    private static final String DEFAULT_STATUS = "Pending";
    private static final String DEFAULT_EXIT_STATUS = "Finished";

    protected final List<FunctionDescriptor> preFunctions = new ArrayList<FunctionDescriptor>();
    protected final List<FunctionDescriptor> postFunctions = new ArrayList<FunctionDescriptor>();
    protected final List<ValidatorDescriptor> validators = new ArrayList<ValidatorDescriptor>();

    protected final String displayName;
    protected final String exitStatus;
    protected final String owner;
    protected final String status;
    protected final String dueDate;

    protected boolean hasStep = false;

    protected int join;
    protected int split;
    protected int step = 0;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    ResultDescriptor(Element resultElement) {

        this.displayName = resultElement.getAttribute("display-name");
        this.owner = resultElement.getAttribute("owner");
        this.dueDate = resultElement.getAttribute("due-date");

        // Can only set a final variable once; use local variables of the same name 
        //  for alternative logic then assign to instance variable when determined
        
        // Look for OSWf 'exit-status'
        String exitStatus = resultElement.getAttribute("exit-status");
        
        // Look for Deprecated OSWorkflow 'old-status' found in 'unconditional-result' elements
        if(exitStatus.trim().length() == 0)
            exitStatus = resultElement.getAttribute("old-status");
        
        if(exitStatus.trim().length() == 0)
            exitStatus = DEFAULT_EXIT_STATUS;

        String status = resultElement.getAttribute("status");

        if(status.trim().length() == 0)
            status = DEFAULT_STATUS;

        // Assign to instance variables
        this.exitStatus = exitStatus;
        this.status = status;

        try {
            setId(Integer.parseInt(resultElement.getAttribute("id")));
        } catch (NumberFormatException e) {}

        try {
            this.split = Integer.parseInt(resultElement.getAttribute("split"));
        } catch (Exception ex) {
            this.split = 0;
        }

        try {
            join = Integer.parseInt(resultElement.getAttribute("join"));
        } catch (Exception ex) {
            this.join = 0;
        }

        try {
            this.step = Integer.parseInt(resultElement.getAttribute("step"));
            this.hasStep = true;
        } catch (Exception ex) {
            this.step = 0;
        }


        // OPTIONAL: result -> validators -> validator[]  =====================================
        
        Element validatorsELement = XMLHelper.getChildElement(resultElement, "validators");

        if (validatorsELement != null) {
            List<Element> validatorElements = XMLHelper.getChildElements(validatorsELement, "validator");

            for (Element validatorElement : validatorElements) {
                ValidatorDescriptor validatorDescriptor = factory.createValidatorDescriptor(validatorElement);
                validatorDescriptor.setParent(this);
                this.validators.add(validatorDescriptor);
            }
        }
        

        // OPTIONAL: result -> pre-functions -> function[]  ===================================
        
        Element preFunctionsElement = XMLHelper.getChildElement(resultElement, "pre-functions");

        if (preFunctionsElement != null) {
            List<Element> preFunctionElements = XMLHelper.getChildElements(preFunctionsElement, "function");

            for (Element preFunctionElement : preFunctionElements) {
                FunctionDescriptor functionDescriptor = factory.createFunctionDescriptor(preFunctionElement);
                functionDescriptor.setParent(this);
                this.preFunctions.add(functionDescriptor);
            }
        }


        // OPTIONAL: result -> post-functions -> function[]  ==================================
        
        Element postFunctionsElement = XMLHelper.getChildElement(resultElement, "post-functions");

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

    public String getDisplayName() {
        return displayName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public int getJoin() {
        return join;
    }

    public String getExitStatus() {
        return exitStatus;
    }

    public String getOwner() {
        return owner;
    }

    public List<FunctionDescriptor> getPostFunctions() {
        return postFunctions;
    }

    public List<FunctionDescriptor> getPreFunctions() {
        return preFunctions;
    }

    public int getSplit() {
        return split;
    }

    public String getStatus() {
        return status;
    }


    public int getStep() {
        return step;
    }

    public List<ValidatorDescriptor> getValidators() {
        return validators;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        
        // Validate the Collections
        ValidationHelper.validate(preFunctions);
        ValidationHelper.validate(postFunctions);
        ValidationHelper.validate(validators);

        //if it's not a split or a join or a finish, then we require a next step
        
        if ((split == 0) && (join == 0) && !(getParent() instanceof ActionDescriptor && ((ActionDescriptor)getParent()).isFinish())) {
            
            StringBuffer error = new StringBuffer("Result ");

            if (getId() > 0) {
                error.append("#").append(getId());
            }

            error.append(" is not a split or join, and has no ");

            if (!hasStep) {
                throw new InvalidWorkflowDescriptorException(error.append("next step").toString());
            }

            if ((status == null) || (status.length() == 0)) {
                throw new InvalidWorkflowDescriptorException(error.append("status").toString());
            }
        }
    }

 }
