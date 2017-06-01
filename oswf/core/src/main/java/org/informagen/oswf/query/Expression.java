package org.informagen.oswf.query;

import java.io.Serializable;


/**
 * Abstract base class for expressions used in a workflow query.
 * Expressions can be negated and/or nested. The default is no negation.
 * <p></p>
 * Expressions which are supported by all stores are {@link FieldExpression} and {@link NestedExpression}.
 * <p></p>
 * Store specific expressions like XPathExpression can be added.
 *
 * @author Christine Zimmermann
 */
 
 
public abstract class Expression implements Serializable {

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    protected boolean negate = false;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    protected Expression() {}

    // M E T H O D S  -------------------------------------------------------------------------

    public boolean isNegate() {
        return negate;
    }

    abstract public boolean isNested();
}
