package org.informagen.oswf.functions;

import org.informagen.typedmap.TypedMap;

import org.informagen.oswf.*;

import org.informagen.oswf.OSWfEngine;

// OSWf Exceptions
import org.informagen.oswf.exceptions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 *
 *
 * @author $Author: hani $
 * @version $Revision: 1.4 $
 */
public class JNDIFunctionProvider implements FunctionProvider {
    
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Logger logger = LoggerFactory.getLogger(JNDIFunctionProvider.class);

    // M E T H O D S  -------------------------------------------------------------------------

    public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap ps) throws WorkflowException {
        String location = (String) args.get(OSWfEngine.JNDI_LOCATION);

        if (location == null) {
            throw new WorkflowException(OSWfEngine.JNDI_LOCATION + " argument is null");
        }

        location = location.trim();

        FunctionProvider provider;

        try {
            try {
                provider = (FunctionProvider) new InitialContext().lookup(location);
            } catch (NamingException e) {
                provider = (FunctionProvider) new InitialContext().lookup("java:comp/env/" + location);
            }
        } catch (NamingException e) {
            String message = "Could not get handle to JNDI FunctionProvider at: " + location;
            throw new WorkflowException(message, e);
        }

        provider.execute(transientVars, args, ps);
    }
}
