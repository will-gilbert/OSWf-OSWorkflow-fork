package org.informagen.oswf.impl.stores;

import org.informagen.oswf.exceptions.WorkflowStoreException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;


/**
 * @author Christopher Farnham
 * Created on Feb 27, 2004
 */
public class MySQLStore extends JDBCStore {


    protected String entrySequenceIncrement = null;
    protected String entrySequenceRetrieve = null;
    protected String stepSequenceIncrement = null;
    protected String stepSequenceRetrieve = null;

    // M E T H O D S  -------------------------------------------------------------------------


    public MySQLStore(Map<String,String> props, Map<String,Object> args)throws WorkflowStoreException {
        super(props, args);
        
        stepSequenceIncrement = props.get("step.sequence.increment");
        stepSequenceRetrieve = props.get("step.sequence.retrieve");
        entrySequenceIncrement = props.get("entry.sequence.increment");
        entrySequenceRetrieve = props.get("entry.sequence.retrieve");
    }

    protected long getNextEntrySequence(Connection c) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            stmt = c.prepareStatement(entrySequenceIncrement);
            stmt.executeUpdate();
            rset = stmt.executeQuery(entrySequenceRetrieve);

            rset.next();

            long id = rset.getLong(1);

            return id;
        } finally {
            cleanup(null, stmt, rset);
        }
    }

    protected long getNextStepSequence(Connection c) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            stmt = c.prepareStatement(stepSequenceIncrement);
            stmt.executeUpdate();
            rset = stmt.executeQuery(stepSequenceRetrieve);

            rset.next();

            long id = rset.getLong(1);

            return id;
        } finally {
            cleanup(null, stmt, rset);
        }
    }
}
