package org.informagen.oswf.testing;



import org.informagen.oswf.testing.OSWfTestCase;
import org.informagen.oswf.testing.HibernateTestSupport;


import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.Step;
import org.informagen.oswf.StepCondition;
import org.informagen.oswf.ProcessInstanceState;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;

import org.informagen.oswf.descriptors.WorkflowDescriptor;
import org.informagen.oswf.descriptors.StepDescriptor;
import org.informagen.oswf.descriptors.StepConditionDescriptor;

import org.informagen.oswf.exceptions.InvalidInputException;
import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.testing.OSWfAssertions;

// Hibernate
import org.hibernate.Session;
import org.hibernate.SessionFactory;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

// Java - Collections
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.text.SimpleDateFormat;


// JUnit 4.x testing
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


public abstract class OSWfHibernateTestCase extends OSWfTestCase {

    private HibernateTestSupport hibernateTestSupport = null;


    public OSWfHibernateTestCase(String... resources) {

        hibernateTestSupport = new HibernateTestSupport();
    
        for (String resource : resources) 
            hibernateTestSupport.addResource(resource);
    }

    // Hibernate Support Methods ==============================================================

    protected SessionFactory getSessionFactory() throws Exception {
        if(hibernateTestSupport == null)
            throw new RuntimeException("HibernateSupport has not been initialized.");
    
        return hibernateTestSupport.getSessionFactory();
    }

    protected Session getSession() {
        if(hibernateTestSupport == null)
            throw new RuntimeException("HibernateSupport has not been initialized.");
    
        return hibernateTestSupport.getSession();
    }

    protected void closeSession() {
        if(hibernateTestSupport == null)
            throw new RuntimeException("HibernateSupport has not been initialized.");
    
        hibernateTestSupport.closeSession();
    }

    protected void closeSessionFactory() {
        if(hibernateTestSupport == null)
            throw new RuntimeException("HibernateSupport has not been initialized.");
    
        hibernateTestSupport.closeSessionFactory();
    }

    protected HibernateTestSupport getHibernateTestSupport() {
        return hibernateTestSupport;
    }




}
