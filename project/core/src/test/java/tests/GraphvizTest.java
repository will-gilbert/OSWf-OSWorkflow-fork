package tests;

// OSWf Core
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.util.Graphviz;
import org.informagen.oswf.descriptors.WorkflowDescriptor;

// OSWf Default Implementations & Service Providers
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

// Simple Logging Facade
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


public class GraphvizTest  {

    private static Logger logger = LoggerFactory.getLogger(GraphvizTest.class);

    @Test
    public void creatBase64Image() throws Exception {

        OSWfEngine wfEngine = new DefaultOSWfEngine("Unit Testing");
        
        OSWfConfiguration config = new MemoryOSWfConfiguration();
        config.load(getClass().getResource("/oswf-usage.xml"));

        wfEngine.setConfiguration(config);
        WorkflowDescriptor wfd = wfEngine.getWorkflowDescriptor("Holiday");
        
        String dot = new Graphviz(wfd).create();
        assertTrue(dot.length() > 0);
        
        logger.debug(dot);

    }

    

}
