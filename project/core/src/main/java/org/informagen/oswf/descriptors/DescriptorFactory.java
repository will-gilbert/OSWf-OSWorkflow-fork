package org.informagen.oswf.descriptors;

import org.w3c.dom.Element;


/**
 * 
 *
 */
public class DescriptorFactory {
    
    private static DescriptorFactory instance;

    // C O N S T R U C T O R  -----------------------------------------------------------------

    private DescriptorFactory() {}

    // M E T H O D S  -------------------------------------------------------------------------

    public static DescriptorFactory getInstance() {
        if(instance == null)
            instance = new DescriptorFactory();
        
        return instance;
    }

    public ActionDescriptor createActionDescriptor(Element action) {
        return new ActionDescriptor(action);
    }

    public ConditionDescriptor createConditionDescriptor(Element function) {
        return new ConditionDescriptor(function);
    }

    public ConditionalResultDescriptor createConditionalResultDescriptor(Element element) {
        return new ConditionalResultDescriptor(element);
    }

    public ConditionsDescriptor createConditionsDescriptor(Element element) {
        return new ConditionsDescriptor(element);
    }

    public FunctionDescriptor createFunctionDescriptor(Element function) {
        return new FunctionDescriptor(function);
    }

    public JoinDescriptor createJoinDescriptor(Element join) {
        return new JoinDescriptor(join);
    }

    public StepConditionDescriptor createStepConditionDescriptor(Element stepCondition) {
        return new StepConditionDescriptor(stepCondition);
    }

    public RegisterDescriptor createRegisterDescriptor(Element register) {
        return new RegisterDescriptor(register);
    }

    public ResultDescriptor createResultDescriptor(Element element) {
        return new ResultDescriptor(element);
    }

    public SplitDescriptor createSplitDescriptor(Element split) {
        return new SplitDescriptor(split);
    }

    public StepDescriptor createStepDescriptor(Element step) {
        return new StepDescriptor(step);
    }

    public StepDescriptor createStepDescriptor(Element step, AbstractDescriptor parent) {
        return new StepDescriptor(step, parent);
    }

    public ValidatorDescriptor createValidatorDescriptor(Element validator) {
        return new ValidatorDescriptor(validator);
    }

    public WorkflowDescriptor createWorkflowDescriptor(Element root) {
        return new WorkflowDescriptor(root);
    }
}
