package org.informagen.oswf.simulator.server;

import java.lang.StringBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

// Java Regular Expressions
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// Simple Logging for Java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  This static class manages an application wide name/value pair properties object.  The
 *    inital set of name/value pairs is loaded from a classpath file '<code>/locality.properties</code>'.  After
 *    deployment additional name/value pairs can be added and/or existing pairs can be overwritten
 *    from either a properties file, properties object, or programatically with setProperty.
 */


public class Locality {

    private static Logger logger = LoggerFactory.getLogger(Locality.class);
    private static Logger startupLogger = LoggerFactory.getLogger("StartupLogger");

    private static Pattern substitutionPattern = Pattern.compile("\\$\\{.*?\\}");

    private final Properties properties = new Properties();

    public Locality() { 
        this("locality");
    }

    public Locality(String resourceFilename) { 
    
        String resourceName = resourceFilename;
        ResourceBundle bundle = null;
        
        // TODO: Override the properties file name with an external Java property
        
        if (resourceName == null)
            resourceName = "locality";
        
        // Look for locality properties in the classpath
        try {
            bundle = ResourceBundle.getBundle(resourceName);
        } catch (MissingResourceException e) {
            startupLogger.warn("Application classpath did not have '" + resourceName + "'.properties file");
        }
        
        // Read the name/value pairs from the classpath properties file
        
        if(bundle != null) {

            Enumeration<String> keys = bundle.getKeys();

            while(keys.hasMoreElements()) {
                String key = keys.nextElement();
                properties.setProperty(key, bundle.getString(key));
            }

        }
    
    }


    /**
     * Given a name return the corresponding value as a String.
     *<p>
     *  If the value is not found <code>null</code> is returned.
     *
     * @param name The string used as a key for this name/value pair
     *
     * @return The value corresponding to the <code>name</code>
     */

    public String getValue(String name) {
        return getValue(name, null);
    }


    /**
     * Given a name return the corresponding value as a String.
     *<p>
     *  If the value is not found the <code>theDefault</code> is returned.
     *
     * @param name The string used as a key for this name/value pair
     *
     * @return The value corresponding to the <code>name</code>, null if the key does not exist
     */

    public String getValue(String name, String theDefault) {

        String value = properties.getProperty(name);

        // Assign the default if the name/value pair was not found
        if(value == null)
            value = theDefault;

        return value;
    }


    /**
     * Add the contents of the <code>properties</code> object to the existing properties object
     *  managed by the "Locality" class.  Overwrite any existing properties.
     *<p>
     *  Report overwritten properties as an 'info' level log message.
     *
     * @param properties A set of properties will be added
     * @see #setProperty(String name, String value)
     */

    public void overwrite(Properties properties) {

        // Load properties, overwriting application's builtin properties

        Enumeration<?> names = properties.propertyNames();
        while(names.hasMoreElements()) {
            String name = (String)names.nextElement();
            String value =  (String)properties.getProperty(name);
            setProperty(name, value);
        }
    }

    /**
     * Add a name/value pair to the Locality properties
     *<p>
     *  Report overwritten properties as an 'info' level log message.
     *
     * @author Will Gilbert
     *
     * @param name The name of the property, may be null
     * @param value The value associated with the property name, may be null
     */

    public void setProperty(String name, String value) {

        if(name == null || value == null)
            return;

        String previous = (String)properties.setProperty(name, value);

        if(previous != null)
            logger.info(name + "='" + value + "' was '" + previous + "'");

    }

    /**
     *   Retrieve a common set of properties with 'string' in the 'position' dot-delimited place.
     *<p>
     *   For Example (2, "foo") returns the following set:
     *<pre>
     <code>
     *           hello.world.foo.bar=Hello, World!
     *           peter.parker.foo=Peter Parker
     *           org.informagen.foo=My favorite package name
     *</code>
     *</pre>
     *  <p>But does not include:
     *<pre>
     *<code>
     *           hello.foo=Hello, Foo!
     *</code>
     *</pre>
     *
     * @author Paul Borlin
     *
     * @param position dot-delimited ordinal position of the search string
     * @param string search string
     * @return A Properties object containing properties with matching keys
     * @see #getPropertiesByPattern(String)
     */

     public Properties getPropertiesByPattern(int position, String string) {
        return getPropertiesByPattern("^(([^.]*\\.){" + position +"}" + string + ")");
    }



    /**
     *   Use a regular expression to return a set of properties.  The most common
     *     usage would be to retrieve "com.intellicare.inquiry.*"
     *
     *
     * @author Paul Borlin
     *
     * @param string search string
     * @return A Properties object containing properties with matching keys
     */

    public Properties getPropertiesByPattern(String regex) {

        logger.debug("Pattern: " + regex);

        Properties subProperties = new Properties();

        Enumeration<Object> keys = properties.keys();

        String key;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher("");

        while (keys.hasMoreElements()) {
            key = (String)keys.nextElement();
            matcher.reset(key);
            if (matcher.find()) {
                subProperties.put(key, properties.getProperty(key));
            }
        }

        logger.debug("Size: " + subProperties.size());

        return subProperties;

    }

    /**
     *   Load a properties file into the existing Properties object. Report a 'warn' level
     *     log message if the properties file is not found.
     *<p>
     *   Uses the method <code>Locality.overwrite</code>
     *
     * @param filename The path and filename of a external properties file which will be loaded
     * @see #overwrite(Properties)
     */

    /*expandFilename
     * @see #expandFilename(String)
     */


    public void loadExternalProperties(final String filename) {

        if(filename == null || filename.trim().length() == 0)
            return;

        String expandedFileName = expandEnvironmentVariables(filename);

        Properties properties = new Properties();

        try {

            File file = new File(expandedFileName);

            // If there is no properties file, log it and exit now
            if(file.exists() == false) {
                startupLogger.warn("File does not exist: " + filename);
                return;
            }

            properties.load(new FileInputStream(expandedFileName));

        } catch (IOException ioe) {
            startupLogger.error(ioe.getMessage());
        }

        // Merge these name/value pairs into those managed by Locality
        overwrite(properties);

    }


    /*
    *   Expand a String which contains environment variables, such as ${CATALINA_HOME}.
    *
    * @param filename String containing environment variables, null values allowed
    * @return expanded filename after environment variable substitution
    */

    public static String expandEnvironmentVariables(final String filename) {

        if(filename == null)
            return null;

        if(filename.contains("${") == false)
            return filename;

        // Do some 'env' matching

        Matcher matcher = substitutionPattern.matcher(filename);

        StringBuffer buffer = new StringBuffer();

        int start = 0;
        while(matcher.find(start)) {

            buffer.append(filename.substring(start, matcher.start()));

            // Is there a environmental variable defined?
            String name = filename.substring(matcher.start()+2, matcher.end()-1);
            String value = System.getenv(name);

            if(value != null) {
                buffer.append(value);
                logger.debug("${" + name + "} expands to: " + value);
            } else
                buffer.append("${").append(name).append("}");


            start = matcher.end();
        }

        buffer.append(filename.substring(start));

        return buffer.toString();
    }

}
