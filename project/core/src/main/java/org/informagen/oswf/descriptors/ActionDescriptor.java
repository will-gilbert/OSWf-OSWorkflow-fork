package org.informagen.oswf.descriptors;

import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;
import org.informagen.oswf.Validatable;

import org.informagen.oswf.util.ValidationHelper;
import org.informagen.oswf.util.XMLHelper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.util.Iterator;


/**
 * 
 */
public class ActionDescriptor extends AbstractDescriptor implements Validatable {

    // Child elements
    protected final Map<String,String> metaAttributes = new HashMap<String,String>();
    protected final List<ValidatorDescriptor> validators = new ArrayList<ValidatorDescriptor>();
    protected final List<FunctionDescriptor> preFunctions = new ArrayList<FunctionDescriptor>();
    protected final List<ConditionalResultDescriptor> conditionalResults = new ArrayList<ConditionalResultDescriptor>();
    protected final List<FunctionDescriptor> postFunctions = new ArrayList<FunctionDescriptor>();

    protected final RestrictionDescriptor restriction;
    protected final ResultDescriptor defaultResult;

    // Action element attributes
    protected final String name;
    protected final String view;
    protected final boolean autoExecute;
    protected final boolean finish;
    
    // Set for 'commom-actions'
    protected boolean isCommon;

    // C O N S T R U C T O R  -----------------------------------------------------------------

    ActionDescriptor(Element actionElement) {
        
        try {
            setId(Integer.parseInt(actionElement.getAttribute("id")));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid action id value '" + actionElement.getAttribute("id") + "'");
        }

        this.name = actionElement.getAttribute("name");
        this.view = actionElement.getAttribute("view");
        this.autoExecute = "true".equalsIgnoreCase(actionElement.getAttribute("auto"));
        this.finish = "true".equalsIgnoreCase(actionElement.getAttribute("finish"));
        

        // OPTIONAL: action -> meta[]  ========================================================

        List<Element> metaElements = XMLHelper.getChildElements(actionElement, "meta");
        for (Element metaElement : metaElements) 
            this.metaAttributes.put(metaElement.getAttribute("name"), XMLHelper.getText(metaElement));

        // OPTIONAL: action -> validators -> validator[]  =====================================
        
        Element validatorsElement = XMLHelper.getChildElement(actionElement, "validators");

        if (validatorsElement != null) {
            List<Element> validatorElements = XMLHelper.getChildElements(validatorsElement, "validator");

            for (Element validatorElement : validatorElements) {
                ValidatorDescriptor validatorDescriptor = factory.createValidatorDescriptor(validatorElement);
                validatorDescriptor.setParent(this);
                this.validators.add(validatorDescriptor);
            }
        }

        // OPTIONAL: action -> pre-functions -> function[] ====================================
        
        Element preFunctionsElement = XMLHelper.getChildElement(actionElement, "pre-functions");

        if (preFunctionsElement != null) {
            List<Element> preFunctionElements = XMLHelper.getChildElements(preFunctionsElement, "function");

            for (Element preFunctionElement : preFunctionElements ) {
                FunctionDescriptor functionDescriptor = factory.createFunctionDescriptor(preFunctionElement);
                functionDescriptor.setParent(this);
                this.preFunctions.add(functionDescriptor);
            }
        }

        // OPTIONAL: action -> results -> conditional-result[]  ===============================
        
        Element resultsElememt = XMLHelper.getChildElement(actionElement, "results");
        List<Element> resultElements = XMLHelper.getChildElements(resultsElememt, "conditional-result");

        // Add decrecated 'result' elements
        resultElements.addAll(XMLHelper.getChildElements(resultsElememt, "result"));

        for (Element resultElement : resultElements) {
            ConditionalResultDescriptor conditionalResultDescriptor = factory.createConditionalResultDescriptor(resultElement);
            conditionalResultDescriptor.setParent(this);
            this.conditionalResults.add(conditionalResultDescriptor);
        }

        // REQUIRED: action -> results -> default-result  ======================================

        // Look for 'default-result' then check for deprecated 'unconditional-result'
        Element defaultResultElement = XMLHelper.getChildElement(resultsElememt, "default-result");

        if(defaultResultElement == null)
            defaultResultElement = XMLHelper.getChildElement(resultsElememt, "unconditional-result");

        // This allows loading a workflow with actions without default-results
        if (defaultResultElement != null) {
            defaultResult = factory.createResultDescriptor(defaultResultElement);
            defaultResult.setParent(this);
        } else
            defaultResult = null;
            

        // OPTIONAL: action - > post-functions -> function[]  =================================
        
        Element postFunctionsElement = XMLHelper.getChildElement(actionElement, "post-functions");

        if (postFunctionsElement != null) {
            
            List<Element> postFunctionElements = XMLHelper.getChildElements(postFunctionsElement, "function");

            for (Element postFunctionElement : postFunctionElements) {
                FunctionDescriptor functionDescriptor = factory.createFunctionDescriptor(postFunctionElement);
                functionDescriptor.setParent(this);
                this.postFunctions.add(functionDescriptor);
            }
        }

        // OPTIONAL: action -> restrict-to  ===================================================
        
        Element restrictElement = XMLHelper.getChildElement(actionElement, "restrict-to");

        if (restrictElement != null) {
            
            restriction = new RestrictionDescriptor(restrictElement);

            if (restriction.getConditionsDescriptor() != null)
                restriction.setParent(this);
        } else
            restriction = null;
    }

    // M E T H O D S  -------------------------------------------------------------------------

    void setCommon(boolean isCommon) {
        this.isCommon = isCommon;
    }

    public boolean isCommon() {
        return isCommon;
    }

    public boolean getAutoExecute() {
        return autoExecute;
    }

    public List<ConditionalResultDescriptor> getConditionalResults() {
        return conditionalResults;
    }

    public boolean isFinish() {
        return finish;
    }

    public Map<String,String> getMetaAttributes() {
        return metaAttributes;
    }

    public String getName() {
        return name;
    }

    public List<FunctionDescriptor> getPostFunctions() {
        return postFunctions;
    }

    public List<FunctionDescriptor> getPreFunctions() {
        return preFunctions;
    }

    public RestrictionDescriptor getRestriction() {
        return restriction;
    }

    public ResultDescriptor getUnconditionalResult() {
        return defaultResult;
    }

    public List<ValidatorDescriptor> getValidators() {
        return validators;
    }


    public String getView() {
        return view;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (name != null) 
            sb.append(name);

        if ((view != null) && (view.length() > 0))
            sb.append(" (").append(view).append(")");

        return sb.toString();
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        
        ValidationHelper.validate(preFunctions);
        ValidationHelper.validate(postFunctions);
        ValidationHelper.validate(validators);
        ValidationHelper.validate(conditionalResults);

        if ((conditionalResults.size() > 0) && (defaultResult == null)) {
            throw new InvalidWorkflowDescriptorException("Action " + name + " has conditional results but no fallback default result");
        }

        if (restriction != null)
            restriction.validate();

        if (defaultResult != null)
            defaultResult.validate();
    }


    // P R O T E C T E D   M E T H O D S ------------------------------------------------------  

    protected void init(Element action) {}
}
