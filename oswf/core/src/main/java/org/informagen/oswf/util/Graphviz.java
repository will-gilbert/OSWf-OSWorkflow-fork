package org.informagen.oswf.util;

// OSWf Workflow Loaders
import org.informagen.oswf.WorkflowLoader;
import org.informagen.oswf.impl.loaders.URLLoader;

// OSWf Descriptiors
import org.informagen.oswf.descriptors.WorkflowDescriptor;
import org.informagen.oswf.descriptors.AbstractDescriptor;
import org.informagen.oswf.descriptors.ActionDescriptor;
import org.informagen.oswf.descriptors.StepDescriptor;
import org.informagen.oswf.descriptors.SplitDescriptor;
import org.informagen.oswf.descriptors.JoinDescriptor;
import org.informagen.oswf.descriptors.ResultDescriptor;
import org.informagen.oswf.descriptors.ConditionsDescriptor;
 
 // Java - Lang
import java.lang.StringBuffer;

// Java IO
import java.io.File;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;

// Java Collections
import java.util.List;
import java.util.Set;
import java.util.HashSet;

// Simple Logging Facade for Java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Graphviz {

    protected final static Marker fatal = MarkerFactory.getMarker("FATAL");
    private final static Logger logger = LoggerFactory.getLogger(Graphviz.class);

    public static void main(String[] args)  { 
                
        try {
            
            // Convert the filename (arg[0]) into a 'File://' url and
            //   use the URLLoader. 

            File workflowFile = new File(args[0]);
            
            if(workflowFile.exists() == false) {
                logger.error(fatal, "Workflow file does not exist: " + args[0]);
                return;
            }
            
            
            String url = "File://" + workflowFile.getCanonicalPath();
            WorkflowLoader loader = new URLLoader();

            // Parse the file and return a Workflow Descriptor; set
            //   the 'dot' graph title to the filename
            
            WorkflowDescriptor wfd = loader.getWorkflow(url);
            wfd.setWorkflowName(workflowFile.getName());

            // Write the 'dot' file out to the local directory
            
            File dotFile = new File(workflowFile.getName() + ".dot");
            Writer output = new BufferedWriter(new FileWriter(dotFile));
            String dot = new Graphviz(wfd).create();

            output.write(dot);
            output.close();

         } catch (Exception exception) {
            exception.printStackTrace();
        }
        
    }



    //=====================================================================================


    // X11 Pastel 19/28 scheme    
    private static final String RED      = "#fbb4ae";
    private static final String AQUA     = "#f0ffff";
    private static final String BLUE     = "#0000ff";
    private static final String GREEN    = "#ccebc5";
    private static final String PURPLE   = "#decbe4";
    private static final String ORANGE   = "#fed9a6";
    private static final String YELLOW   = "#ffffcc";
    private static final String BROWN    = "#e5d8bd";
    private static final String LAVENDER = "#fddaec";
    private static final String GRAY     = "#f2f2f2";
    private static final String BLACK    = "#000000";
    
    private static final String BACKGROUND = "#f6f6DE";

    final WorkflowDescriptor wfd;
    StringBuffer buffer;
    int warning = 0;
    int finish = 0;

    String background = BACKGROUND;
    Set<String> commonStepsUsed = new HashSet<String>();

  
    public Graphviz(WorkflowDescriptor wfd) {
        this.wfd = wfd;
    }

    public void setBackground(String background) {
        
        if(background == null)
            this.background = "none";
        else
            this.background = background;
    }
    
    public String create() {

        buffer = new StringBuffer(); 

        // Collect workflow parts; Petri Net style; 
        //   Circles (Eggs) for states, squares for actions
        
        List<ActionDescriptor> initialActions = wfd.getInitialActions();
        List<StepDescriptor> steps = wfd.getSteps();
        List<SplitDescriptor> splits = wfd.getSplits();
        List<JoinDescriptor> joins = wfd.getJoins();
    
        writeGraphPreamble();
        
        // Add the States (IA, Step, Split & join)  ===========================================
        
        writeInitialActions(initialActions);
        
        writeSteps(steps);
        
        writeSplits(splits);

        writeJoins(joins);


        // Add the Transitions (Actions) for IA, Step, Split & Join ===========================
        
        // Initial Actions
        for(ActionDescriptor initialAction : initialActions) {
            fromActionEdges("InitialAction", initialAction);
        }            
            
        
        // Steps
        for(StepDescriptor step : steps) {

            if (step.getActions().size() == 0) {

                // I'd be nice to add a finish symbol here

                continue;
            }


            for(ActionDescriptor action : step.getActions()) {
                stepActionTransition(step.getId(), action);
                fromActionEdges("Action", action);
            }
        }

        // Single exit from a Join
        for(JoinDescriptor join : joins) 
            writeResultEdge("Join", join.getId(), join.getResult());


        // Split results
        for(SplitDescriptor split : splits) {
            for(ResultDescriptor result : split.getResults())
                writeResultEdge("Split", split.getId(), result);
        }

        
        writeGraphEnding();
        
        return buffer.toString();
    }

    //=========================================================================================

    private void writeGraphPreamble() {
                
        buffer.append("\n");
        buffer.append("digraph abstract {");
        buffer.append("\n");
        buffer.append("\n");


        buffer.append("graph [ landscape=\"false\" labeljust=\"center\" labelloc=\"top\" fontsize=24 ");

        if(background != null)
            buffer.append("bgcolor=\"").append(background).append("\" ");
            
        buffer.append("label=\"");
        buffer.append(wfd.getWorkflowName()).append("\"]");
        buffer.append("\n");

        buffer.append("node [ color=\"black\" ");
        buffer.append("fillcolor=\"").append(AQUA).append("\" ");
        buffer.append("fontcolor=\"black\" style=\"filled\" fontname=\"Arial\" fontsize=10 shape=egg]");
        buffer.append("\n");

        buffer.append("edge [ fontname=\"Arial\" fontsize=8 arrowtype=\"normal\"]");
        buffer.append("\n");
        buffer.append("\n");

    }
    
    private void writeGraphEnding() {
        buffer.append("\n");
        buffer.append("}\n\n");
    }    


    
    // States - Steps, Splits, and Joins and Initial-Action

    // Initial Actions are 'actions' not 'steps'
    private void writeInitialActions(List<ActionDescriptor> initialActions) {   
        if(initialActions.size() == 0)
            return;
            
        for(ActionDescriptor initialAction : initialActions) {
            buffer.append("InitialAction").append(initialAction.getId());
            buffer.append(" [label=<")
                  .append("<table border='0'><tr><td><font point-size='8.0'>Initial Action: ")
                  .append(initialAction.getId())
                  .append("</font></td></tr><tr><td>")
                  .append(initialAction.getName())
                  .append("</td></tr></table>")
                  .append(">\n   shape=box\n   fontsize=9\n   ");
            buffer.append("fillcolor=\"").append(GREEN).append("\"]");
            buffer.append("\n\n");
        }
        buffer.append("\n");
    }       


    private void writeSteps(List<StepDescriptor> steps) {
    
        if(steps.size() == 0)
            return;
            
        for(StepDescriptor step : steps) {
            buffer.append("Step").append(step.getId());
            buffer.append(" [label=<")
                  .append("<table border='0'><tr><td><font point-size='8.0'>Step: ")
                  .append(step.getId())
                  .append("</font></td></tr><tr><td>")
                  .append(step.getName())
                  .append("</td></tr></table>")
                  .append(">]");
            buffer.append("\n\n");

            List<ActionDescriptor> actions = step.getActions();
            for(ActionDescriptor action : actions) {
                buffer.append("Action").append(action.getId());
                buffer.append(" [label=<")
                      .append("<table border='0'><tr><td><font point-size='8.0'>")
                      .append(action.isCommon() ? "Common Action: " : "Action: ")
                      .append(action.getId())
                      .append("</font></td></tr><tr><td>")
                      .append(action.getName())
                      .append("</td></tr></table>")
                      .append(">\n   shape=box\n   fontsize=9\n   ");
                buffer.append("fillcolor=\"").append(GRAY).append("\"]");
                buffer.append("\n");
            }
            buffer.append("\n\n");

        }
        buffer.append("\n");
    }
        
    private void writeSplits(List<SplitDescriptor> splits) {
    
        if(splits.size() == 0)
            return;
            
        for(SplitDescriptor split : splits) {
            buffer.append("Split").append(split.getId());
            buffer.append(" [label=\"Split: ").append(split.getId()).append("\"\n   shape=trapezium\n   fontsize=9\n   ");
            buffer.append("fillcolor=\"").append(LAVENDER).append("\"]");
            buffer.append("\n\n");
        }

    }

    private void writeJoins(List<JoinDescriptor> joins) {
        if(joins.size() == 0)
            return;
            
        for(JoinDescriptor join : joins) {
            buffer.append("Join").append(join.getId());
            buffer.append(" [label=\"");
            
            List<ConditionsDescriptor> conditions = join.getConditions();      
            // if (conditions.size() > 0) 
            //     buffer.append(conditions.get(0).getType()).append(" ");

            buffer.append("Join: ").append(join.getId());
            buffer.append("\"\n   shape=invtrapezium\n   fontsize=9\n   ");
            buffer.append("fillcolor=\"").append(PURPLE).append("\"]");
            buffer.append("\n\n");
        }
        buffer.append("\n");
    }


    //  Transitions ---------------------------------------------------

    private void stepActionTransition(int id, ActionDescriptor action) {
        buffer.append("Step").append(id).append("->");
        buffer.append("Action").append(action.getId());
        buffer.append("\n");
    }



    private void fromActionEdges(String prefix, ActionDescriptor action) {

        writeUnconditionalResult(prefix, action, action.getUnconditionalResult());
       
        for(ResultDescriptor result : action.getConditionalResults()) 
            writeConditionalResult(prefix, action, result);
        
        buffer.append("\n");
    }

    private void writeUnconditionalResult(String prefix, ActionDescriptor action, ResultDescriptor result) {
        if(okToWriteThisEdge(action, result))
            writeActionResultEdge(prefix, action,  result, 1);
    }

    private void writeConditionalResult(String prefix, ActionDescriptor action, ResultDescriptor result) {
        if(okToWriteThisEdge(action, result))
            writeActionResultEdge(prefix, action,  result, 3);
    }

    /*
    ** This fixes a bug where 'common-actions' are drawn multiple times for every step
    **   split or join which it is transitioning to.  Use a string hash of 
    **   'action:stepId,splitId,joindId" to only write a transition one time.
    */

    private boolean okToWriteThisEdge(ActionDescriptor action, ResultDescriptor result) {

        if(action.isCommon()) {
            String hash = new StringBuffer()
                .append(result.getStep())
                .append("-")
                .append(action.getId())
                .toString();
            
            if(commonStepsUsed.contains(hash) == false) {                    
                commonStepsUsed.add(hash);
                return true;
            } else
                return false;
        } else
            return true;
    }
    

    private void writeActionResultEdge(String prefix, ActionDescriptor action, ResultDescriptor result, int weight) {
    
        if(action.isFinish()) {
            int finishId = writeFinishNode(action.getName());
            buffer.append(prefix).append(action.getId()).append("->");
            writeFinishEdge(finishId, weight);
            return;
        }

        writeResultEdge(prefix, action.getId(), result, weight);
    }


    private void writeResultEdge(String prefix, int id, ResultDescriptor result) {
        writeResultEdge(prefix, id, result, 1);
    }

    private void writeResultEdge(String prefix, int id, ResultDescriptor result, int weight) {
    
        int stepId = result.getStep();
        int splitId = result.getSplit();
        int joinId = result.getJoin();

        if(stepId > 0) {
            buffer.append(prefix).append(id).append("->");
            buffer.append("Step").append(stepId);
        }

        if(splitId > 0) {
            buffer.append(prefix).append(id).append("->");
            buffer.append("Split").append(splitId);
        }

        if(joinId > 0) {
            buffer.append(prefix).append(id).append("->");
            buffer.append("Join").append(joinId);
        }

        
        if(result.getStep() == -1) {
            
            ActionDescriptor action = (ActionDescriptor)result.getParent();
            AbstractDescriptor descriptor = action.getParent();
            
            if(descriptor instanceof StepDescriptor) {
                buffer.append(prefix).append(id).append("->");
                buffer.append("Step").append(descriptor.getId());
            } else if(descriptor instanceof JoinDescriptor) {
                buffer.append(prefix).append(id).append("->");
                buffer.append("Join").append(descriptor.getId());
            } else if(descriptor instanceof SplitDescriptor) {
                buffer.append(prefix).append(id).append("->");
                buffer.append("Split").append(descriptor.getId());
            } else {
                int warning = writeWarningNode();
                buffer.append(prefix).append(id).append("->");
                writeWarningEdge(warning);
            }
            
        }

        // Edge label and styling
        
        String status = result.getExitStatus();
        String owner = result.getOwner();
        
        if(!empty(status) || !empty(owner) || (weight > 1)) {
  
            buffer.append(" [label=\"");
            
            if(!empty(status))
                buffer.append(status);
          
            if(!empty(owner))
                buffer.append("\\nowner:").append(owner);
            
            buffer.append("\"");

            if(weight > 1)
                buffer.append("\n style=\"setlinewidth(").append(weight).append(")\" ");
        
            buffer.append("]");
        }
        
        buffer.append("\n");
    }


    private int writeFinishNode(String actionName) {
        finish++;
        
        buffer.append("Finish").append(finish);
        buffer.append(" [label=\"" + "Finish" + "\"\n   shape=octagon\n   fontsize=8\n   ");
        buffer.append("fillcolor=\"").append(RED);
        buffer.append("\"]\n");
        
        return finish;
    }


    private void writeFinishEdge(int finishId, int weight) {
        
        buffer.append("Finish").append(finishId);
                
        if(weight > 1)
            buffer.append("[style=\"setlinewidth(").append(weight).append(")\"]");
        
        buffer.append("\n");
    }


    private int writeWarningNode() {
        warning++;
        
        buffer.append("Warn").append(warning);
        buffer.append(" [label=\"!\"\n   shape=triangle\n   fontsize=8 ");
        buffer.append("fillcolor=\"").append(YELLOW).append("\"]\n\n");
        
        return warning;
    }


    private void writeWarningEdge(int warningId) {
        buffer.append("Warn").append(warningId).append("\n\n");
    }

    private boolean empty(String string) {
        if(string == null)
            return true;
            
        if(string.trim().length() == 0)
            return true;
        
        return false;
    }


}

