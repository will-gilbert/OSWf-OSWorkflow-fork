package tests.propertyset;

// OSWf Core
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowLoaderException;
import org.informagen.oswf.exceptions.WorkflowStoreException;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.MemoryPropertySet;
import org.informagen.oswf.propertyset.ProxyPropertySet;

import java.util.HashMap;

// JUnit 4.x testing
import org.junit.Before;

public class ProxyTest extends AbstractTestClass {

    @Before
    public void setUp() throws WorkflowLoaderException, WorkflowStoreException {
        new MemoryOSWfConfiguration().load();
        
        PropertySet proxiedPropertySet = new MemoryPropertySet();
        propertySet = new ProxyPropertySet(proxiedPropertySet);
    }
}
