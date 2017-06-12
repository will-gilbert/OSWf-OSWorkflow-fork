package org.informagen.oswf.impl;

import org.informagen.oswf.VariableResolver;

import org.informagen.oswf.PersistentVars;

import java.io.Serializable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

// Java - Utilities
import java.util.Map;
import java.util.StringTokenizer;

public class DefaultVariableResolver implements VariableResolver, Serializable {

    private static String GET = "get";
    private static String SET = "set";
    private static String IS = "is";


    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = -4819078273560683753L;

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    // this is an Object to avoid the designer having a dependency on oscore
    // private transient Object beanProvider = null;

    // M E T H O D S  -------------------------------------------------------------------------

    // VariableResolver interface =============================================================

    /**
     * Parses a string for instances of "${foo}" and returns a string with all instances replaced
     * with the string value of the foo object (<b>foo.toString()</b>). If the string being passed
     * in only refers to a single variable and contains no other characters (for example: ${foo}),
     * then the actual object is returned instead of converting it to a string.
     */
       
    public String translateVariables(String s, Map<String,Object> transientVars, PersistentVars persistentVars) {

        String temp = s.trim();

        if (temp.startsWith("${") && temp.endsWith("}") && (temp.indexOf('$', 1) == -1)) {

            // The string is just a variable reference, just lookup it's value if available
            String var = temp.substring(2, temp.length() - 1);
            Object object = getVariableFromMaps(var, transientVars, persistentVars);
            
            return (object == null) ? null : object.toString();

        } else {
            // the string passed in contains multiple variables (or none!) and should be treated as a string
            while (true) {
                int x = s.indexOf("${");
                int y = s.indexOf("}", x);

                if ((x != -1) && (y != -1)) {
                    String var = s.substring(x + 2, y);
                    String t = null;
                    Object o = getVariableFromMaps(var, transientVars, persistentVars);

                    if (o != null) {
                        t = o.toString();
                    }

                    if (t != null) {
                        s = s.substring(0, x) + t + s.substring(y + 1);
                    } else {
                        // the variable doesn't exist, so don't display anything
                        s = s.substring(0, x) + s.substring(y + 1);
                    }
                } else {
                    break;
                }
            }

            return s;
        }
    }

    // End of VariableResolver interface ======================================================

    // public void setBeanProvider(BeanProvider beanProvider) {
    //     this.beanProvider = beanProvider;
    // }
    // 
    // public BeanProvider getBeanProvider() {
    //     return (BeanProvider)beanProvider;
    // }

    public Object getVariableFromMaps(String var, Map<String,Object> transientVars, PersistentVars persistentVars) {

        int firstDot = var.indexOf('.');
        String actualVar = var;

        if (firstDot != -1) {
            actualVar = var.substring(0, firstDot);
        }
        
        // Check the transient variables map firist
        Object o = transientVars.get(actualVar);
        
        // If not found, the check the persistent varialbes; beware that HibernatePersistentVars
        //  is currently throwing an exception if the variable is not available.
        //  Need to fix this.
        if (o == null) {
           try { 
               o = persistentVars.getAsActualType(actualVar);
           } catch (Exception exception) {
               return null;
           }
        }

        // I'm not sure what this does?.....
        if (firstDot != -1) {
            o = getProperty(o, var.substring(firstDot + 1));
        }

        return o;
    }

    private Object getProperty(Object object, String property) {
        
        if ((property == null) || (object == null)) {
            return null;
        }

        // Split out property on dots ( "person.name.first" -> "person","name","first" -> getPerson().getName().getFirst() )
        StringTokenizer st = new StringTokenizer(property, ".");

        if (st.countTokens() == 0) {
            return null;
        }

        // Holder for Object at current depth along chain.
        Object result = object;

        try {
            // Loop through properties in chain.
            while (st.hasMoreTokens()) {
                String currentPropertyName = st.nextToken();

                // Assign to holder the next property in the chain.
                result = invokeProperty(result, currentPropertyName);
            }

            // Return holder Object
            return result;
        } catch (NullPointerException e) {
            // It is very likely that one of the properties returned null. If so, catch the exception and return null.
            return null;
        }
    }

    /**
     * Convert property name into getProperty name ( "something" -> "getSomething" )
     */
    private String createMethodName(String prefix, String propertyName) {
        return prefix + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
    }

    /**
     * Invoke the method/field getter on the Object.
     * It tries (in order) obj.getProperty(), obj.isProperty(), obj.property(), obj.property.
     */
    private Object invokeProperty(Object obj, String property) {
        if ((property == null) || (property.length() == 0)) {
            return null; // just in case something silly happens.
        }

        Class cls = obj.getClass();
        Object[] oParams = {};
        Class[] cParams = {};

        try {
            // First try object.getProperty()
            Method method = cls.getMethod(createMethodName(GET, property), cParams);

            return method.invoke(obj, oParams);
        } catch (Exception e1) {
            try {
                // First try object.isProperty()
                Method method = cls.getMethod(createMethodName(IS, property), cParams);

                return method.invoke(obj, oParams);
            } catch (Exception e2) {
                try {
                    // Now try object.property()
                    Method method = cls.getMethod(property, cParams);

                    return method.invoke(obj, oParams);
                } catch (Exception e3) {
                    try {
                        // Now try object.property()
                        Field field = cls.getField(property);

                        return field.get(obj);
                    } catch (Exception e4) {
                        // oh well
                        return null;
                    }
                }
            }
        }
    }


}
