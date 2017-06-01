package org.informagen.oswf;

import org.informagen.oswf.exceptions.WorkflowStoreException;
import org.informagen.oswf.descriptors.WorkflowDescriptor;
import org.informagen.oswf.WorkflowStore;
import org.informagen.oswf.VariableResolver;
import org.informagen.oswf.TypeResolver;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowLoaderException;

import java.net.URL;

import java.util.Map;
import java.util.Set;


/**
 * OSWfConfiguration object that is responsible for all 'static' workflow information.
 * This includes loading of workflow configurations, setting up the workflow
 * descriptor factory, as well as proxying calls to the underlying descriptor
 * factory.
 *
 * Also used to configure persistance PropertySet classes
 *
 *  Renamed from Configuration to OSWfConfiguration because there are
 *   too many classes named Configuration and OSWorkflow had workflow
 *   configuration, PropertySet configuration and Persistence configuration,
 *   which could have a Hibernate configuration ...
 *
 * @author hani
 */
public interface OSWfConfiguration {


    /**
     * @return true if this factory has been initialised.
     * If the factory is not initialised, then {@link #load(java.net.URL)}
     * will be called.
     */

    boolean isInitialized();

    /**
     * Get the persistence variables for the persistence store.
     *   Note that this returns the actual args and not a copy,
     *   so modifications to the returned Map could potentially
     *   affect store behaviour.
     */

    Map<String,Object> getPersistenceArgs();
 

    /**
     * Save a persistence arg into the persistence store. Returns self to allow
     *   method chaining i.e. after constructor or load
     * @param name the argument name
     * @param object the persistence object
     */

    OSWfConfiguration addPersistenceArg(String name, Object object);

    /**
     * Return the resolver to use for all variables specified in scripts
     */

    VariableResolver getVariableResolver();
    
    TypeResolver getTypeResolver();
    
    /**
     * Get the named workflow descriptor.
     * @param name the workflow name
     * @throws WorkflowLoaderException if there was an error looking up the descriptor or if it could not be found.
     */

    WorkflowDescriptor getWorkflow(String name) throws WorkflowLoaderException;
    
    String getWorkflowAsXML(String name) throws WorkflowLoaderException;

    /**
     * Get a list of all available workflow descriptor names.
     * @throws WorkflowLoaderException if the underlying factory does not support this method
     * or if there was an error looking up workflow names.
     */

    Set<String> getWorkflowNames() throws WorkflowLoaderException;

    WorkflowStore getWorkflowStore() throws WorkflowStoreException;

    OSWfConfiguration load() throws WorkflowLoaderException;
  
    /**
     * Load the specified configuration file. Returns self to allow
     *   method chaining i.e. after constructor or instance
     * @param url url to the configuration file.
     */

    
    OSWfConfiguration load(URL url) throws WorkflowLoaderException;

}
