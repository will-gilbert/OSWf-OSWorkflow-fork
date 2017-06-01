package org.informagen.oswf.impl;

import org.informagen.oswf.WorkflowContext;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class DefaultWorkflowContext implements WorkflowContext {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private String actor;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public DefaultWorkflowContext(String actor) {
        this.actor = actor;
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public void setRollbackOnly() {
        // does nothing, this is basic, remember!
    }
}
