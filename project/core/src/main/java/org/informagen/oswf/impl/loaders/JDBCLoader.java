package org.informagen.oswf.impl.loaders;

import org.informagen.oswf.impl.loaders.AbstractWorkflowLoader;


// OSWf - Loaders
import org.informagen.oswf.WorkflowLoader;
import org.informagen.oswf.util.WorkflowLocation;
import org.informagen.oswf.util.WorkflowXMLParser;

// OSWF - Descriptors
import org.informagen.oswf.descriptors.WorkflowDescriptor;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowLoaderException;
import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xml.sax.SAXException;

// Java - IO
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

// Java - JDBC
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Java - Collections
import java.util.Map;
import java.util.Set;
import java.util.Properties;

// Java - JNDI
import javax.naming.InitialContext;
import javax.naming.NamingException;



/**
 * Workflow loader that stores workflows in a database.
 * The database requires a property called 'datasource' which is the JNDI
 * name of the datasource for this factory.
 * <p>
 * Also required is a database table called OS_WORKFLOWDEFS with two columns,
 * WF_NAME which contains the workflow name, and WF_DEFINITION which will contain the xml
 * workflow descriptor, the latter can be either a TEXT or BINARY type.
 * <p>
 * Note that this class is provided as an example, and users are encouraged to use
 * their own implementations that are more suited to their particular needs.
 *
 * @author Hubert Felber, Philipp Hug
 * Date: May 01, 2003
 * Time: 11:17:06 AM
 */

public class JDBCLoader extends AbstractWorkflowLoader {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowLoader.class);

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------
    
    // Passed in as 'parameters'
    private String workflowTable = "OSWF_WORKFLOWDEFS";
    private String identifier = "WF_IDENTIFIER";
    private String definition = "WF_DEFINITION";

    protected DataSource datasource;
    
    // C O N S T U C T O R S  ----------------------------------------------------------------- 

    public JDBCLoader(Properties parameters, Map<String,Object> persistentArgs) throws WorkflowLoaderException {
       super(parameters, persistentArgs);
       
       workflowTable = properties.getProperty("table", workflowTable);
       identifier = properties.getProperty("identifier-column", identifier);
       definition = properties.getProperty("definition-column", definition);
              
       try {
           String datasource = properties.getProperty("datasource");
           if(datasource != null)
               this.datasource = (DataSource) new InitialContext().lookup(datasource);
        } catch (NamingException namingException) {
            throw new WorkflowLoaderException("Could not read workflow names from datasource", namingException);
        }
    }

    // P R O T E C T E D   M E T H O D S  -----------------------------------------------------

    protected InputStream fetchProcessDefinition(WorkflowLocation workflowLocation) throws WorkflowLoaderException {
 
        String processDefinition = null;
        Connection connection = null;
        
        if(datasource == null)
            throw new WorkflowLoaderException("'datasource' has not been defined for JDBCLoader");
        
        try {
            connection = datasource.getConnection();
            
            String selectWorkflow = new StringBuilder()
                .append("SELECT ").append(definition)
                .append(" FROM ").append(workflowTable)
                .append(" WHERE ").append(identifier).append(" = ?")
                .toString();

            PreparedStatement preparedStatement = connection.prepareStatement(selectWorkflow);
            preparedStatement.setString(1, workflowLocation.location);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) 
                processDefinition = resultSet.getString(1);

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException sqlException) {
            throw new WorkflowLoaderException(sqlException);            
        } finally {
            try {
                if (connection != null) 
                    connection.close();
            } catch (SQLException sqlException) {}
        }
        
        if(processDefinition == null)
            throw new WorkflowLoaderException("Process definition '" + workflowLocation.location + "' not found");
        
        return new ByteArrayInputStream(processDefinition.getBytes());
    }

}
