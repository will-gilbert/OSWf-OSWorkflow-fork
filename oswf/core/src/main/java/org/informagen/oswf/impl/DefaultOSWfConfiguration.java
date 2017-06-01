package org.informagen.oswf.impl;


// OSWf Interface 
import org.informagen.oswf.OSWfConfiguration; 
import org.informagen.oswf.TypedMapStore; 

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowLoaderException;
import org.informagen.oswf.exceptions.WorkflowStoreException;

// Persistence
import org.informagen.oswf.WorkflowStore;
import org.informagen.oswf.typedmap.TypedMapFactory;

// Variable and Type Resolvers
import org.informagen.oswf.VariableResolver;
import org.informagen.oswf.impl.DefaultVariableResolver;
import org.informagen.oswf.TypeResolver;
import org.informagen.oswf.impl.DefaultTypeResolver;

// Workflow Loader
import org.informagen.oswf.WorkflowLoader; 
import org.informagen.oswf.impl.loaders.URLLoader;
import org.informagen.oswf.util.WorkflowLocation;
import org.informagen.oswf.descriptors.WorkflowDescriptor;

// OSWf Util
import org.informagen.oswf.util.ClassLoaderHelper;
import org.informagen.oswf.util.XMLHelper;

// XML Parsing
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.InputStream;
import java.io.Serializable;

import java.net.URL;

// Java - Reflection
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

// Java Collections
import java.util.List;
import java.util.Set;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

/**
 * Default implementation for an OSWf configuration object
 *
 * This configuration object is passed to the setOSWfConfiguration
 * method. If the configuration is not initialized, the {@link #load(java.net.URL)} method will be called by
 * the workflow. Alternatively, the actor can explicitly load the configuration by calling that method before
 * calling Workflow#setOSWfConfiguration(OSWfConfiguration).
 * <p>
 * The loading behaviour comes into play when specifying a configuration remotely, for example in an EJB
 * environment. It might be desirable to ensure that the configuration is loaded from within the EJB server,
 * rather than in the calling client.
 *
 * @author Hani Suleiman
 * @version $Revision: 1.15 $
 */


public class DefaultOSWfConfiguration implements OSWfConfiguration, Serializable {

    // Looks up and returns Process Definitions; Defaults to 'URLLoader'
    //  if undefined, see 'getWorkflowLoader' method

    protected WorkflowLoader workflowLoader = null; 
    
    // Coverts ${variable} into strings; 'DefaultVariableResolver' works in most
    //  cases rarely needs to be redefined

    protected VariableResolver variableResolver = null;

    protected TypeResolver typeResolver = null;
   
    // Implements the WorkflowStore ; Lazy instanciation; Defaults to in memory datastore
    //   for each unit testing

    protected Map<String,Object> persistenceArgs = new HashMap<String,Object>();

    protected String workflowStoreClassname = null;
    protected Map<String,String> workflowStoreParameters = new HashMap<String,String>();

    protected String typedMapStoreClassname = null;
    protected Map<String,String> typedMapStoreParameters = new HashMap<String,String>();

    // The instatiated workflow store
    protected transient WorkflowStore workflowStore = null;
    protected transient TypedMapStore typedMapStore = null;
    
    protected boolean initialized = false;


    // M E T H O D S  -------------------------------------------------------------------------

    public boolean isInitialized() {
        return initialized;
    }

    public Map<String,Object> getPersistenceArgs() {
        return persistenceArgs;
    }

    public OSWfConfiguration addPersistenceArg(String name, Object object) {
        persistenceArgs.put(name, object);
        return this;        
    }

    public VariableResolver getVariableResolver() {
        if(variableResolver == null)
            variableResolver = new DefaultVariableResolver();
            
        return variableResolver;
    }

    public TypeResolver getTypeResolver() {
        if(typeResolver == null)
            typeResolver = new DefaultTypeResolver();

        return typeResolver;
    }

    public WorkflowDescriptor getWorkflow(String name) throws WorkflowLoaderException {
        
        WorkflowDescriptor workflow = getWorkflowLoader().getWorkflow(name);

        if (workflow == null) {
            throw new WorkflowLoaderException("Unknown workflow name");
        }

        return workflow;
    }

    public String getWorkflowAsXML(String name) throws WorkflowLoaderException {
        
        String xml = getWorkflowLoader().getWorkflowAsXML(name);

        if (xml == null) 
            throw new WorkflowLoaderException("Unknown workflow name");

        return xml;
    }

    public Set<String> getWorkflowNames() throws WorkflowLoaderException {
        return getWorkflowLoader().getWorkflowNames();
    }

    // Lazy instanciation so that the persistenceArgs can be supplied via
    //      the configuration routine

    public WorkflowStore getWorkflowStore() throws WorkflowStoreException {
        
        if (workflowStore == null) 
            workflowStore = createWorkflowStore();

        return workflowStore;
    }

    public OSWfConfiguration load() throws WorkflowLoaderException {
        return load(null);
    }

    public OSWfConfiguration load(URL url) throws WorkflowLoaderException {

        // Parse the url, if NULL then look for default 'oswf.xml' in the classpath
        InputStream is = getInputStream(url);

        // If no configuration file was found use in-memory configuation with URL loader
        if (is == null) {
            workflowLoader = new org.informagen.oswf.impl.loaders.URLLoader();
            return new MemoryOSWfConfiguration();
        }

        try {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            DocumentBuilder db;

            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new WorkflowLoaderException("Error creating document builder", e);
            }

            Document document = db.parse(is);
            Element root = XMLHelper.getDocumentRoot(document);

            // Instance these classes in the confi
            variableResolver = parseVariableResolver(root);
            typeResolver = parseTypeResolver(root);
            workflowLoader = parseWorkflowLoader(root);
            
            parsePersistence(root);
            parsePropertySets(root);
        
            initialized = true;
            
        } catch (WorkflowLoaderException e) {
            throw e;
        } catch (Exception e) {
            throw new WorkflowLoaderException("Error in OSWf configuration", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                throw new WorkflowLoaderException("Error in OSWf configuration", e);
            }
        }

        return this;
    }


    // P R O T E C T E D   M E T H O D S  -------------------------------------------------------

      
    /* Persistence - Store & PropertySet ----------------------------------------------
    **
    **   <persistence>
    **      <workflow-store class="org.informagen.oswf.impl.stores.MemoryStore"/>
    **      <propertyset-store class="org.informagen.oswf.impl.MemoryPropertySetStore"/>
    **   </persistence>
    **
    ** Read the WorkflowStore classname aka persistence.
    **  If not found use 'MemoryStore' and 'MemoryTypedMap'
    */
 
    protected void parsePersistence(Element root) throws Exception {
        
        Element persistenceElement = XMLHelper.getChildElement(root, "persistence");

        if (persistenceElement != null) {
        
            Element storeElement = XMLHelper.getChildElement(persistenceElement, "workflow-store");
            if(storeElement != null) {
                workflowStoreClassname = storeElement.getAttribute("class");
        
                // Load any name/value properties which the WorkflowStore may need
                List<Element> propertyElements = XMLHelper.getChildElements(storeElement, "parameter");
                for (Element e : propertyElements) 
                    workflowStoreParameters.put(e.getAttribute("name"), e.getAttribute("value"));
            } 

            Element propertySetElement = XMLHelper.getChildElement(persistenceElement, "propertyset-store");
            if(propertySetElement != null) {
                typedMapStoreClassname = propertySetElement.getAttribute("class");
        
                // Load any name/value properties which the WorkflowStore may need
                List<Element> propertyElements = XMLHelper.getChildElements(propertySetElement, "parameter");
                for (Element e : propertyElements) 
                    typedMapStoreParameters.put(e.getAttribute("name"), e.getAttribute("value"));
            } 
        } 
    }
                       
    /* Workflow Description Loader --------------------------------------------
    **
    **  <loader class="org.informagen.oswf.impl.loaders.URLLoader">
    **    <parameter name="datasource"   value="jdbc/H2"/>
    **    <workflow name="Validator"  type="resource" location="core/validator.oswf.xml"/>
    **  </loader>
    **
    */

    protected WorkflowLoader parseWorkflowLoader(Element root) throws Exception {
 
        WorkflowLoader workflowLoader = null;
        
        Element loaderElement = XMLHelper.getChildElement(root, "loader");
        String loaderClassname = null;
 
        if (loaderElement != null) {
            
            loaderClassname = loaderElement.getAttribute("class");

            if ( loaderClassname == null || loaderClassname.isEmpty() ) {
                loaderClassname = "org.informagen.oswf.impl.loaders.URLLoader";
                //throw new WorkflowLoaderException("Element 'loader' does not specify a class attribute");
            }

            // Read the loader properties
            Properties workflowParameters = new Properties();
            List<Element> elements = XMLHelper.getChildElements(loaderElement, "parameter");

            for(Element e : elements) 
                workflowParameters.put(e.getAttribute("name"), e.getAttribute("value"));

            try {
                Class workflowLoaderClass = ClassLoaderHelper.loadClass(loaderClassname, getClass());
                Constructor<WorkflowLoader> constructor = workflowLoaderClass.getConstructor(new Class[]{Properties.class, Map.class});
                workflowLoader = constructor.newInstance(workflowParameters, getPersistenceArgs());

            } catch (Exception exception) {
                throw new WorkflowLoaderException("Error creating WorkflowLoader: " + loaderClassname, exception);
            }


            // Read the set of optional workflow name, type and location
            Map<String,WorkflowLocation> workflows = new HashMap<String,WorkflowLocation>();
            List<Element> workflowElements = XMLHelper.getChildElements(loaderElement, "workflow");

            for(Element e : workflowElements) {
                String workflowName = e.getAttribute("name");
                String type = e.getAttribute("type");
                String location = e.getAttribute("location");
                workflows.put(workflowName, new WorkflowLocation(type, location));
            }

            workflowLoader.init(workflows);
            workflowLoader.initDone();
        }
        
        return workflowLoader;
    }

    /* Avaliable PropertySets --------------------------------------------
    **
    **  <propertysets> 
    **     <propertyset name="memory" class="...MemoryPropertySet"/>
    **  </propertysets>
    **
    */

    protected void parsePropertySets(Element root) throws Exception {

        Element propertySetsElement = XMLHelper.getChildElement(root, "propertysets");
        if (propertySetsElement != null) {

            List<Element> propertySetElements = XMLHelper.getChildElements(propertySetsElement, "propertyset");

            for(Element propertySetElement : propertySetElements) {
            
                String propertySetName = propertySetElement.getAttribute("name");
                String propertySetClassname = propertySetElement.getAttribute("class");
                Map<String,String> parameters = new HashMap<String,String>();

                // Save configuration parameters as a Map for each PropertySet
                List<Element> parameterElements = XMLHelper.getChildElements(propertySetElement, "parameter");

                for(Element parameterElement : parameterElements) {
                    parameters.put(
                        parameterElement.getAttribute("name"), 
                        parameterElement.getAttribute("value")
                    );
                }

                TypedMapFactory.getInstance()
                    .addNamedTypedMap(
                        propertySetName, 
                        propertySetClassname, 
                        parameters
                    )
                ;
            }
        }
    }

    /* Variable Resolver --------------------------------------------------------------
    **
    **  Read a resolver classname from a configuration XML file (Optional)
    **
    **    <variable-resolver class='org.informagen.oswf.util.DefaultVariableResolver' />
    **
    **  The 'DefaultVariableResolver' class is sufficient for variable interpolation
    **    and rarely needs to be specified.
    */

    protected VariableResolver parseVariableResolver(Element root) throws Exception {

        VariableResolver variableResolver = null;

        Element variableResolverElement = XMLHelper.getChildElement(root, "variable-resolver");
        if (variableResolverElement != null) {
            String classname = variableResolverElement.getAttribute("class");

            if (classname != null) 
                variableResolver = (VariableResolver) ClassLoaderHelper.loadClass(classname, getClass()).newInstance();
        }
        
        return variableResolver;
    }
 
            
    /* Type Resolver --------------------------------------------------------------
    **
    **  Read a type resolver classname from a configuration XML file (Optional)
    **
    **    <type-resolver class='org.informagen.oswf.util.DefaultTypeResolver' >
    **        <function alias='NullFunction' class='tests.util.NullFunction' />
    **        <condition alias='hasStatusOf' class='tests.util.TrueCondition' />        
    **    </type-resolver>
    **
    **  The 'DefaultVariableResolver' class is sufficient for variable interpolation
    **    and rarely needs to be specified.
    */

    protected TypeResolver parseTypeResolver(Element root) throws Exception {
        
        TypeResolver typeResolver = null;

        Element typeResolverElement = XMLHelper.getChildElement(root, "type-resolver");
        
        if (typeResolverElement != null) {
            
            String resolverClassname = typeResolverElement.getAttribute("class");
            if (resolverClassname != null) {
                
                typeResolver = (TypeResolver) ClassLoaderHelper.loadClass(resolverClassname, getClass()).newInstance();
                
                // Function Aliases
                List<Element> functionElements = XMLHelper.getChildElements(typeResolverElement, "function");
                for (Element e : functionElements) 
                    typeResolver.addFunctionAlias(e.getAttribute("alias"), e.getAttribute("class"));

                // Condition Aliases                    
                List<Element> conditionElements = XMLHelper.getChildElements(typeResolverElement, "condition");
                for (Element e : conditionElements) 
                    typeResolver.addConditionAlias(e.getAttribute("alias"), e.getAttribute("class"));

                // Register Aliases                    
                List<Element> registerElements = XMLHelper.getChildElements(typeResolverElement, "register");
                for (Element e : registerElements) 
                    typeResolver.addRegisterAlias(e.getAttribute("alias"), e.getAttribute("class"));
            }
        }
        return typeResolver;
    }
      
    protected WorkflowStore createWorkflowStore() throws WorkflowStoreException {
        
        WorkflowStore workflowStore = null;
        String classname = workflowStoreClassname;
                
        if(classname == null)
            throw new WorkflowStoreException("'workflow-store' class not defined");

        addPersistenceArg("typedMapStore", createTypedMapStore());
            
        try {
            Class workflowStoreClass = ClassLoaderHelper.loadClass(classname, getClass());
            Constructor<WorkflowStore> constructor = workflowStoreClass.getConstructor(new Class[]{Map.class, Map.class});
            workflowStore = constructor.newInstance(typedMapStoreParameters, getPersistenceArgs());

        } catch (Exception exception) {
            throw new WorkflowStoreException("Error creating WorkflowStore: " + classname, exception);
        }
        
        return workflowStore;
    }

    protected TypedMapStore createTypedMapStore() throws WorkflowStoreException {
        
        TypedMapStore typedMapStore = null;
        String classname = typedMapStoreClassname;
        
        if((classname != null) && (getPersistenceArgs().containsKey("typedMapStore") == false) ) {

            try {
                Class typedMapStoreClass = ClassLoaderHelper.loadClass(classname, getClass());
                Constructor<TypedMapStore> constructor = typedMapStoreClass.getConstructor(new Class[]{Map.class, Map.class});
                typedMapStore = constructor.newInstance(typedMapStoreParameters, getPersistenceArgs());
            } catch (Exception exception) {
                throw new WorkflowStoreException("Error creating TypedMapStore: " + classname, exception);
            }
            
        } else if(getPersistenceArgs().containsKey("typedMapStore") == false)
            throw new WorkflowStoreException("TypedMapStore not defined");
            
        return typedMapStore;
    }


    /**
     * Parse the configuration file passed as a URL.
     * If null, load the configuration 'oswf.xml' from the current context classloader.
     *
     * The search order is:
     * <ul>
     *   <li>Specified URL</li>
     *   <li>oswf.xml</li>
     *   <li>/oswf.xml</li>
     *   <li>META-INF/oswf.xml</li>
     *   <li>/META-INF/oswf.xml</li>
     * </ul>
     */

    protected InputStream getInputStream(URL url) {
        
        InputStream is = null;
    
        //  Parse the OSWf configuration via the URL; Fail silently
        //     as we have a few other options to try
        if (url != null) {
            try {
                is = url.openStream();
            } catch (Exception ex) {
            }
        }
    
        // When the URL is null, look in the classpath for a file named 'oswf.xml'

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    
        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("oswf.xml");
            } catch (Exception e) {}
        }
    
        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("/oswf.xml");
            } catch (Exception e) {}
        }
    
        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("META-INF/oswf.xml");
            } catch (Exception e) {}
        }
    
        if (is == null) {
            try {
                is = classLoader.getResourceAsStream("/META-INF/oswf.xml");
            } catch (Exception e) {}
        }
    
        return is;
    }


    protected WorkflowLoader getWorkflowLoader() {
        
        // Use 'URLLoader' as a reasonable default
        if(workflowLoader == null) {
            workflowLoader = new URLLoader();
            initialized = true;
        }
        
        return workflowLoader;
    }


}
