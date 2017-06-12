package org.informagen.oswf.simulator.server;

// DTO
import org.informagen.oswf.simulator.dto.ProcessDescriptorSummary;
import org.informagen.oswf.simulator.dto.ProcessInstance;
import org.informagen.oswf.simulator.dto.ProcessInstanceState;
import org.informagen.oswf.simulator.dto.Action;
import org.informagen.oswf.simulator.dto.Step;
import org.informagen.oswf.simulator.dto.ProcessVariable;
import org.informagen.oswf.simulator.dto.StepActions;
import org.informagen.oswf.simulator.dto.Overview;


// OSWf - Interfaces
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.OSWfConfiguration;

// OSWf - SPI, Util
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;

// OSWf - Descriptors
import org.informagen.oswf.descriptors.WorkflowDescriptor;
import org.informagen.oswf.descriptors.ActionDescriptor;
import org.informagen.oswf.descriptors.StepDescriptor;

// OSWf - Query
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.WorkflowExpressionQuery;

// OSWf - Exceptions
import org.informagen.oswf.exceptions.WorkflowException;
import org.informagen.oswf.exceptions.WorkflowLoaderException;
import org.informagen.oswf.exceptions.InvalidInputException;
import org.informagen.oswf.exceptions.InvalidActionException;

// OSWf - PropertSet
import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.Type;
import org.informagen.oswf.exceptions.InvalidValueTypeException;



// Application - RPC 
import org.informagen.oswf.simulator.rpc.OSWfService;
import org.informagen.oswf.simulator.rpc.ServiceException;

// Simple Logging Facade
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java - Text
import java.text.SimpleDateFormat;

// Java - Collections
import java.util.Collections;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

// Java - util
import java.util.Date;

import static java.util.Collections.EMPTY_MAP;

// Google DI Annotation
import com.google.inject.Inject;


public class OSWfServiceImpl implements OSWfService {

    private static Logger logger = LoggerFactory.getLogger(OSWfServiceImpl.class);
    private static Logger startupLogger = LoggerFactory.getLogger("StartupLogger");

    protected static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yy HH:mm");

    private OSWfConfiguration configuration;
    private final OSWfEngine wfEngine;

    @Inject
    public OSWfServiceImpl(OSWfConfiguration configuration) {
        this.configuration = configuration;
        startupLogger.info("Configuring OSWfService");
 
        wfEngine = new DefaultOSWfEngine("reporter");
        wfEngine.setConfiguration(configuration);
    }

    
    public List<ProcessDescriptorSummary> fetchWorkflowListing() throws ServiceException {
        
        List<ProcessDescriptorSummary> list = new ArrayList<ProcessDescriptorSummary>();
        
        try {
            
            Set<String> names = configuration.getWorkflowNames();
        
            for(String name : names) {
            
                WorkflowDescriptor wd;
            
                try {
                    wd = configuration.getWorkflow(name);
                } catch (WorkflowLoaderException workflowLoaderException) {
                    
                    // Just log an error but keep reading workflow descriptions
                    logger.error(workflowLoaderException.getMessage());
                    continue;
                }
            
                list.add(new ProcessDescriptorSummary()
                    .name(wd.getWorkflowName())
                    .initialActionCount(wd.getInitialActions().size())
                    .stepCount(wd.getSteps().size())
                    .splitCount(wd.getSplits().size())
                    .joinCount(wd.getJoins().size())
                    .piCount(fetchProcessInstancesList(name).size())
                );
            }

        } catch (WorkflowLoaderException workflowLoaderException) {
            throw new ServiceException(workflowLoaderException.getMessage());
        }
        
        Collections.sort(list);
        return list;
        
    }

    public List<ProcessInstance> fetchProcessInstances(String workflowName) throws ServiceException {
        
        List<ProcessInstance> list = new ArrayList<ProcessInstance>();

        for(Long piid : fetchProcessInstancesList(workflowName))
            list.add(new ProcessInstance()
                .piid(piid)
                .state(wfEngine.getProcessInstanceState(piid).getName())
                .currentStepCount(wfEngine.getCurrentSteps(piid).size())
                .historyStepCount(wfEngine.getHistorySteps(piid).size())
            );
                                
   
        return list;
    }

    private List<Long> fetchProcessInstancesList(String workflowName) throws ServiceException {

        FieldExpression activeProcessInstances = new FieldExpression(
            Context.ENTRY, 
            Field.NAME,  Operator.EQUALS, workflowName
        ); 

        try {
            
            WorkflowExpressionQuery query = new WorkflowExpressionQuery(activeProcessInstances);        
            return wfEngine.query(query);
            
        } catch (WorkflowException workflowException) {
            throw new ServiceException(workflowException.getMessage());
        }

    }



    public List<Action> fetchInitialActions(String workflowName)  throws ServiceException {
            
        List<Action> list = new ArrayList<Action>();
 
        try {
            WorkflowDescriptor wd = configuration.getWorkflow(workflowName);
            
            for(ActionDescriptor action : wd.getInitialActions()) 
               list.add( new Action(action.getId(), action.getName()) );
            
        } catch (WorkflowLoaderException workflowLoaderException) {
            throw new ServiceException(workflowLoaderException.getMessage());
        }

        Collections.sort(list);
        
        return list;
        
    }

    public ProcessInstanceState fetchProcessInstanceState(long piid)  throws ServiceException {
    
       ProcessInstanceState dto = new ProcessInstanceState();

       WorkflowDescriptor wfDescriptor = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(piid));
 
       // Current Steps
       for(org.informagen.oswf.Step step : wfEngine.getCurrentSteps(piid)) 
            dto.addCurrentStep(new Step()
                .id(step.getId())
                .name(wfDescriptor.getStep(step.getStepId()).getName())
                .status(step.getStatus())
                .owner(step.getOwner())
                .startDate(step.getStartDate() == null ? "" : dateFormatter.format(step.getStartDate()))
                .dueDate(step.getDueDate() == null ? "" : dateFormatter.format(step.getDueDate()))
           );
 
       // History Steps
       for(org.informagen.oswf.Step step : wfEngine.getHistorySteps(piid)) 
            dto.addHistoryStep(new Step()
                .id(step.getId())
                .name(wfDescriptor.getStep(step.getStepId()).getName())
                .status(step.getStatus())
                .actor(step.getActor())
                .action( wfDescriptor.getAction(step.getActionId()) == null ? "" : 
                         wfDescriptor.getAction(step.getActionId()).getName() )
                .startDate(step.getStartDate() == null ? "" : dateFormatter.format(step.getStartDate()))
                .finishDate(step.getFinishDate() == null ? "" : dateFormatter.format(step.getFinishDate()))   
           );

       // Process Variables; interate over all types but access as 'String' for display
       try { 
           
           PersistentVars persistentVars = wfEngine.getPersistentVars(piid);

           for(Type type : Type.getTypes())
               for(String key : persistentVars.getKeys(type) ) 
                   dto.addProcessVariable(new ProcessVariable()
                      .name(key)
                      .type(type.getName())
                      .value(persistentVars.getString(key))
                   ); 
           
       } catch (InvalidValueTypeException invalidPropertyTypeException) {
           logger.error(invalidPropertyTypeException.getMessage());
       }

       Collections.reverse(dto.historySteps);
        
       return dto;

    }

    public List<Overview> fetchWorkflowOverview(String workflowName) throws ServiceException {
        
        List<Overview> list = new ArrayList<Overview>();
        
        try {

            WorkflowDescriptor wd = configuration.getWorkflow(workflowName);

            for(StepDescriptor step : wd.getSteps()) {
                Overview overview = new Overview()
                    .id(step.getId())
                    .stepName(step.getName())
                ;

                list.add(overview);    
            }
            
            // Get all of the Current and History Steps for this workflow
            List<org.informagen.oswf.Step> currentSteps = new ArrayList<org.informagen.oswf.Step>();
            List<org.informagen.oswf.Step> historySteps = new ArrayList<org.informagen.oswf.Step>();
            for(Long piid : fetchProcessInstancesList(workflowName)) {
                currentSteps.addAll(wfEngine.getCurrentSteps(piid));
                historySteps.addAll(wfEngine.getHistorySteps(piid));
            }

            // Count all of the Current Steps
            for (org.informagen.oswf.Step step : currentSteps) {
                for(Overview overview : list) {
                    if(overview.id == step.getStepId()) {
                        overview.current++;
                        long pending = new Date().getTime() - step.getStartDate().getTime();
                        overview.maxPending = Math.max(overview.maxPending, pending);
                        break;
                    }
                }
            }

            // Count all of the History Steps
            for (org.informagen.oswf.Step step : historySteps) {
                for(Overview overview : list) {
                    if(overview.id == step.getStepId()) {
                        overview.history++;
                        if(step.getFinishDate() != null) {
                            long pending = step.getFinishDate().getTime() - step.getStartDate().getTime();
                            overview.avgPending = (overview.avgPending*(overview.history-1) + pending)/overview.history;
                        }
                        break;
                    }
                }
            }


        } catch (WorkflowLoaderException workflowLoaderException) {
            throw new ServiceException(workflowLoaderException.getMessage());
        }
        
        Collections.sort(list);

        return list;
    }


    public List<StepActions> fetchStepActions(long piid) throws ServiceException {
        
        List<StepActions> list = new ArrayList<StepActions>();
 
        WorkflowDescriptor wfDescriptor = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(piid));
        
        try {
 
            List<org.informagen.oswf.Step> steps = wfEngine.getCurrentSteps(piid);
 
            for(org.informagen.oswf.Step step : steps) {
                
                int stepId = step.getStepId();
                String stepName = wfDescriptor.getStep(stepId).getName();
                StepActions stepActions = new StepActions(stepId, stepName);
  
                List<ActionDescriptor>actions = wfDescriptor.getStep(stepId).getActions();
        
                for (ActionDescriptor action : actions) 
                    stepActions.addAction(new Action(action.getId(), action.getName()));
                
                list.add(stepActions);
            }
        } catch (Exception exception) {
            throw new ServiceException(exception);
        }

        Collections.sort(list);

        return list;
    }

    // Actor based Workflow Engine instances ==================================================


    public Long startProcess(String workflowName, String actor, int initialAction, Map<String,Object> inputs) throws ServiceException {

        OSWfEngine actorEngine = new DefaultOSWfEngine(actor);
        
        if(configuration != null)
            actorEngine.setConfiguration(configuration);

        long piid = 0;
        
        try {
            
            piid = actorEngine.initialize(workflowName, initialAction,(inputs != null) ? inputs : Collections.EMPTY_MAP); 
            
        } catch (InvalidInputException invalidInputException) {
            throw new ServiceException(invalidInputException);
        } catch (WorkflowException workflowException) {
            logger.error(workflowException.getMessage());
            throw new ServiceException("This workflow could not be used to create process instances. It may reference class which are not available to the Simulator", "warning");
        } 

        return new Long(piid);
    }

    public void doAction(String workflowName, long piid, String actor, int actionId, Map<String,Object> inputs) throws ServiceException {

        OSWfEngine actorEngine = new DefaultOSWfEngine(actor);
        
        if(configuration != null)
            actorEngine.setConfiguration(configuration);

        try {
            
            actorEngine.doAction(piid, actionId, (inputs != null) ? inputs : Collections.EMPTY_MAP);
            
        } catch (InvalidActionException invalidActionException) {
            WorkflowDescriptor wfDescriptor = actorEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(piid));
            String actionName = wfDescriptor.getAction(actionId).getName(); 
            logger.warn(invalidActionException.getMessage());
            throw new ServiceException("Could not execute action '" + actionName + "' (actionId=" + actionId + "), it may have action restrictions.", "warning"); 
        } catch (InvalidInputException invalidInputException) {
            throw new ServiceException(invalidInputException);
        } catch (WorkflowException workflowException) {
            throw new ServiceException(workflowException);
        }
    }

    


   
}
