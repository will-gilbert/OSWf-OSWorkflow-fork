package org.informagen.oswf.impl.stores;

import org.informagen.oswf.impl.stores.HibernateStep;

/**
 * This class exists to seperate the persistence of the Steps.
 *   By subclassing the HibernateHistoryStep from the abstract HibernateStep class
 *   it can be easily written into seperate tables.
 *
 */

public class HibernateHistoryStep extends HibernateStep {

    //~ Constructors ///////////////////////////////////////////////////////////

    public HibernateHistoryStep() {}

    public HibernateHistoryStep(HibernateStep step) {
        super(step);
    }
}
