package org.informagen.oswf.impl.stores;

import org.informagen.oswf.impl.stores.HibernateStep;

/**
 * This class exists to seperate the persistence of the Steps.
 *   By subclassing the HibernateCurrentStep from the abstract HibernateStep class
 *   it can be easily written into seperate tables.
 *
 * @see {@link HibernateStep}
 * @see {@link HibernateHistoryStep}
 */
public class HibernateCurrentStep extends HibernateStep {

    //~ Constructors ///////////////////////////////////////////////////////////

    public HibernateCurrentStep() {}

    public HibernateCurrentStep(HibernateStep step) {
        super(step);
    }
}
