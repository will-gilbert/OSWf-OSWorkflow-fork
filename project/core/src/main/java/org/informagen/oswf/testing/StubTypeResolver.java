package org.informagen.oswf.testing;

import org.informagen.oswf.impl.DefaultTypeResolver;

// OSWf Core - Interfaces
import org.informagen.oswf.WorkflowContext;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.TypeResolver;
import org.informagen.oswf.Condition;
import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.Register;
import org.informagen.oswf.Validator;

// OSWf Typed Map - Interfaces
import org.informagen.oswf.typedmap.TypedMap;

// OSWf - Utilitiy
import org.informagen.oswf.util.ClassLoaderHelper;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java - Collections
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class StubTypeResolver extends DefaultTypeResolver {
        
    private static final Logger logger = LoggerFactory.getLogger(TypeResolver.class);

    // M E T H O D S  -------------------------------------------------------------------------

    @Override
    public Condition getCondition(String type, String name, Map<String,String> args) throws WorkflowException {

        try {
            Condition condition = super.getCondition(type, name, args);
            if(condition != null)
                return condition;
        } catch (Exception exception) {
            logger.debug("StubTypeResolver: Could not load Condition: "  + exception.getMessage());
        }
        
        return new ConditionStub();
    }

    @Override
    public FunctionProvider getFunction(String type, String name, Map<String,String> args) throws WorkflowException {
        try {
            FunctionProvider functionProvider = super.getFunction(type, name, args);
            if(functionProvider != null)
                return functionProvider;
        } catch (Exception exception) {
            logger.debug("StubTypeResolver: Could not load FunctionProvider: " + exception.getMessage());
        }
        
        return new FunctionProviderStub();
    }

    @Override
    public Register getRegister(String type, Map<String,String> args) throws WorkflowException {
        try {
            Register register = super.getRegister(type, args);
            if(register != null)
                return register;
        } catch (Exception exception) {
            logger.debug("StubTypeResolver: Could not load Register: " + exception.getMessage());
        }
        
        return new RegisterStub();
    }

    @Override
    public Validator getValidator(String type, Map<String,String> args) throws WorkflowException {

        try {
            Validator validator = super.getValidator(type, args);
            if(validator != null)
                return validator;
        } catch (Exception exception) {
            logger.debug("StubTypeResolver: Could not load Validator: "+ exception.getMessage());
        }
        
        return new ValidatorStub();
    }

    @Override
    protected Object loadObject(String className) {
        try {
            return ClassLoaderHelper.loadClass(className.trim(), getClass()).newInstance();
        } catch (Exception e) {
            logger.debug("Could not load class: '" + className + "'", e);
            return null;
        }
    }
    
    public static class ConditionStub implements Condition {
        public boolean passesCondition(Map<String,Object> transientVars, Map<String,String> args, TypedMap ps) throws WorkflowException {return true;}
    }
    
    public static class FunctionProviderStub implements FunctionProvider {
        public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap ps) throws WorkflowException {}
    }
    
    public static class RegisterStub implements Register {
        public Object registerVariable(WorkflowContext context, ProcessInstance entry, Map<String,String> args, TypedMap ps) throws WorkflowException { return null; }
    }
    
    public static class ValidatorStub implements Validator {
        public void validate(Map<String,Object> transientVars, Map<String,String> args, TypedMap ps) throws WorkflowException {}
    }

    
}
