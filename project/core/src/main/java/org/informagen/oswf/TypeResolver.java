package org.informagen.oswf;

import org.informagen.oswf.Condition;
import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.Register;
import org.informagen.oswf.Validator;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;

// Java - Collections
import java.util.Map;

public interface TypeResolver {
        
    Condition getCondition(String type, String name, Map<String,String> args) throws WorkflowException;

    FunctionProvider getFunction(String type, String name, Map<String,String> args) throws WorkflowException;

    Register getRegister(String type, Map<String,String> args) throws WorkflowException;
    
    Validator getValidator(String type, Map<String,String> args) throws WorkflowException;

    void addFunctionAlias(String name, String classname);

    void addConditionAlias(String name, String classname);

    void addRegisterAlias(String name, String classname);
   
}
