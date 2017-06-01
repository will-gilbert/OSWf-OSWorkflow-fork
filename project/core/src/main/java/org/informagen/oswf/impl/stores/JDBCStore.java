package org.informagen.oswf.impl.stores;

import org.informagen.oswf.typedmap.TypedMap;
import org.informagen.oswf.typedmap.TypedMapFactory;

import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.WorkflowStore;
import org.informagen.oswf.ProcessInstanceState;

import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.LogicalOperator;
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.NestedExpression;
import org.informagen.oswf.query.WorkflowExpressionQuery;

import org.informagen.oswf.impl.DefaultStep;
import org.informagen.oswf.impl.DefaultProcessInstance;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowStoreException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import java.util.*;
import java.util.Date;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;


/**
 * JDBC implementation.
 * <p>
 *
 * The following properties are all <b>required</b>:
 * <ul>
 *  <li><b>datasource</b> - the JNDI location for the DataSource that is to be used.</li>
 *  <li><b>entry.sequence</b> - SQL query that returns the next ID for a workflow entry.
 *  Use special value 'generated' if you want to use underlying database to generate id (will work only under JDK 1.4 and higher).
 *  </li>
 *  <li><b>entry.table</b> - table name for workflow entry</li>
 *  <li><b>entry.id</b> - column name for workflow entry ID field</li>
 *  <li><b>entry.name</b> - column name for workflow entry name field</li>
 *  <li><b>entry.state</b> - column name for workflow entry state field</li>
 *  <li><b>step.sequence</b> - SQL query that returns the next ID for a workflow step.
 *  You can use special value 'generated'.</li>
 *  <li><b>history.table</b> - table name for steps in history</li>
 *  <li><b>current.table</b> - table name for current steps</li>
 *  <li><b>step.id</b> - column name for step ID field</li>
 *  <li><b>step.entryId</b> - column name for workflow entry ID field (foreign key relationship to [entry.table].[entry.id])</li>
 *  <li><b>step.stepId</b> - column name for step workflow definition step field</li>
 *  <li><b>step.actionId</b> - column name for step action field</li>
 *  <li><b>step.owner</b> - column name for step owner field</li>
 *  <li><b>step.actor</b> - column name for step actor field</li>
 *  <li><b>step.startDate</b> - column name for step start date field</li>
 *  <li><b>step.dueDate</b> - column name for optional step due date field</li>
 *  <li><b>step.finishDate</b> - column name for step finish date field</li>
 *  <li><b>step.status</b> - column name for step status field</li>
 *  <li><b>currentPrev.table</b> - table name for the previous IDs for current steps</li>
 *  <li><b>historyPrev.table</b> - table name for the previous IDs for history steps</li>
 *  <li><b>step.previousId</b> - column name for step ID field (foreign key relation to [history.table].[step.id] or [current.table].[step.id])</li>
 * </ul>
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class JDBCStore extends AbstractWorkflowStore implements WorkflowStore {

    private static final Logger logger = LoggerFactory.getLogger(JDBCStore.class);

    private static final String GENERATED = "generated";

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    protected DataSource ds;
    protected String currentPrevTable;
    protected String currentTable;
    protected String entryId;
    protected String entryName;
    protected String entrySequence;
    protected String entryState;
    protected String entryTable;
    protected String historyPrevTable;
    protected String historyTable;
    protected String stepActionId;
    protected String stepActor;
    protected String stepDueDate;
    protected String stepProcessId;
    protected String stepFinishDate;
    protected String stepId;
    protected String stepOwner;
    protected String stepPreviousId;
    protected String stepSequence;
    protected String stepStartDate;
    protected String stepStatus;
    protected String stepStepId;
    protected boolean closeConnWhenDone = false;

    // M E T H O D S  -------------------------------------------------------------------------

    public JDBCStore(Map<String,String> props, Map<String,Object> args) throws WorkflowStoreException {
        super(props, args);
        
        entrySequence = getInitProperty(props, "entry.sequence", "SELECT nextVal('seq_os_wfentry')");
        stepSequence = getInitProperty(props, "step.sequence", "SELECT nextVal('seq_os_currentsteps')");
        entryTable = getInitProperty(props, "entry.table", "OS_WFENTRY");
        entryId = getInitProperty(props, "entry.id", "ID");
        entryName = getInitProperty(props, "entry.name", "NAME");
        entryState = getInitProperty(props, "entry.state", "STATE");
        historyTable = getInitProperty(props, "history.table", "OS_HISTORYSTEP");
        currentTable = getInitProperty(props, "current.table", "OS_CURRENTSTEP");
        currentPrevTable = getInitProperty(props, "currentPrev.table", "OS_CURRENTSTEP_PREV");
        historyPrevTable = getInitProperty(props, "historyPrev.table", "OS_HISTORYSTEP_PREV");
        stepId = getInitProperty(props, "step.id", "ID");
        stepProcessId = getInitProperty(props, "step.processId", "ENTRY_ID");
        stepStepId = getInitProperty(props, "step.stepId", "STEP_ID");
        stepActionId = getInitProperty(props, "step.actionId", "ACTION_ID");
        stepOwner = getInitProperty(props, "step.owner", "OWNER");
        stepActor = getInitProperty(props, "step.actor", "ACTOR");
        stepStartDate = getInitProperty(props, "step.startDate", "START_DATE");
        stepFinishDate = getInitProperty(props, "step.finishDate", "FINISH_DATE");
        stepDueDate = getInitProperty(props, "step.dueDate", "DUE_DATE");
        stepStatus = getInitProperty(props, "step.status", "STATUS");
        stepPreviousId = getInitProperty(props, "step.previousId", "PREVIOUS_ID");

        String jndi = props.get("datasource");

        if (jndi != null) {
            try {
                ds = (DataSource) lookup(jndi);

                if (ds == null) {
                    ds = (DataSource) new javax.naming.InitialContext().lookup(jndi);
                }
            } catch (Exception e) {
                throw new WorkflowStoreException("Error looking up DataSource at " + jndi, e);
            }
        }

    }


    public void setEntryState(long id, ProcessInstanceState state) throws WorkflowStoreException {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();

            String sql = "UPDATE " + entryTable + " SET " + entryState + " = ? WHERE " + entryId + " = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, state.getValue());
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new WorkflowStoreException("Unable to update state for workflow instance #" + id + " to " + state, e);
        } finally {
            cleanup(conn, ps, null);
        }
    }

    public TypedMap getTypedMap(long entryId) {
        HashMap args = new HashMap(1);
        args.put("globalKey", "oswf_" + entryId);

        return TypedMapFactory.getInstance().createTypedMap("jdbc", args);
    }

    ////////////METHOD #2 OF 3 //////////////////
    ////////// ...gur;  ////////////////////
    //kiz
    public boolean checkIfORExists(NestedExpression nestedExpression) {
        //GURKAN;
        //This method checks if OR exists in any nested query
        //This method is used by doNestedNaturalJoin() to make sure
        //OR does not exist within query
        int numberOfExp = nestedExpression.getExpressionCount();

        if (nestedExpression.getExpressionOperator() == LogicalOperator.OR) {
            return true;
        }

        for (int i = 0; i < numberOfExp; i++) {
            Expression expression = nestedExpression.getExpression(i);

            if (expression.isNested()) {
                NestedExpression nestedExp = (NestedExpression) expression;

                return checkIfORExists(nestedExp);
            }
        }

        return false;
    }

    public Step createCurrentStep(long entryId, int wfStepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) throws WorkflowStoreException {
        Connection conn = null;

        try {
            conn = getConnection();

            long id = createCurrentStep(conn, entryId, wfStepId, owner, startDate, dueDate, status);
            addPreviousSteps(conn, id, previousIds);

            return new DefaultStep(id, entryId, wfStepId, 0, owner, startDate, dueDate, null, status, previousIds, null);
        } catch (SQLException e) {
            throw new WorkflowStoreException("Unable to create current step for workflow instance #" + entryId, e);
        } finally {
            cleanup(conn, null, null);
        }
    }

    public ProcessInstance createEntry(String workflowName) throws WorkflowStoreException {
        boolean generatedKeys = GENERATED.equals(entrySequence);

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();

            String sql = "INSERT INTO " + entryTable + " (" + (generatedKeys ? "" : (entryId + ", ")) + entryName + ", " + entryState + ") VALUES (" + (generatedKeys ? "" : "?,") + "?,?)";

            if (logger.isDebugEnabled()) {
                logger.debug("Executing SQL statement: " + sql);
            }

            if (generatedKeys) {
                stmt = conn.prepareStatement(sql, new String[] {entryId});
            } else {
                stmt = conn.prepareStatement(sql);
            }

            long id = 0;

            if (!generatedKeys) {
                id = getNextEntrySequence(conn);
                stmt.setLong(1, id);
                stmt.setString(2, workflowName);
                stmt.setInt(3, ProcessInstanceState.INITIATED.getValue());
            } else {
                stmt.setString(1, workflowName);
                stmt.setInt(2, ProcessInstanceState.INITIATED.getValue());
            }

            stmt.executeUpdate();

            if (generatedKeys) {
                id = getGeneratedKey(stmt, entryId);
            }

            return new DefaultProcessInstance(id, workflowName);
        } catch (SQLException e) {
            throw new WorkflowStoreException("Error creating new workflow instance", e);
        } finally {
            cleanup(conn, stmt, null);
        }
    }

    public List findCurrentSteps(long entryId) throws WorkflowStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        PreparedStatement stmt2 = null;

        try {
            conn = getConnection();

            String sql = "SELECT " + stepId + ", " + stepStepId + ", " + stepActionId + ", " + stepOwner + ", " + stepStartDate + ", " + stepDueDate + ", " + stepFinishDate + ", " + stepStatus + ", " + stepActor + " FROM " + currentTable + " WHERE " + stepProcessId + " = ?";
            String sql2 = "SELECT " + stepPreviousId + " FROM " + currentPrevTable + " WHERE " + stepId + " = ?";

            if (logger.isDebugEnabled()) {
                logger.debug("Executing SQL statement: " + sql);
            }

            stmt = conn.prepareStatement(sql);

            if (logger.isDebugEnabled()) {
                logger.debug("Executing SQL statement: " + sql2);
            }

            stmt2 = conn.prepareStatement(sql2);
            stmt.setLong(1, entryId);

            rset = stmt.executeQuery();

            ArrayList currentSteps = new ArrayList();

            while (rset.next()) {
                long id = rset.getLong(1);
                int stepId = rset.getInt(2);
                int actionId = rset.getInt(3);
                String owner = rset.getString(4);
                Date startDate = rset.getTimestamp(5);
                Date dueDate = rset.getTimestamp(6);
                Date finishDate = rset.getTimestamp(7);
                String status = rset.getString(8);
                String actor = rset.getString(9);

                ArrayList<Long> prevIdsList = new ArrayList<Long>();
                stmt2.setLong(1, id);

                ResultSet rs = stmt2.executeQuery();

                while (rs.next()) {
                    long prevId = rs.getLong(1);
                    prevIdsList.add(new Long(prevId));
                }

                long[] prevIds = new long[prevIdsList.size()];
                int i = 0;
                
                for (Long aLong : prevIdsList) {
                    prevIds[i] = aLong.longValue();
                    i++;
                }

                DefaultStep step = new DefaultStep(id, entryId, stepId, actionId, owner, startDate, dueDate, finishDate, status, prevIds, actor);
                currentSteps.add(step);
            }

            return currentSteps;
        } catch (SQLException e) {
            throw new WorkflowStoreException("Unable to locate current steps for workflow instance #" + entryId, e);
        } finally {
            cleanup(null, stmt2, null);
            cleanup(conn, stmt, rset);
        }
    }

    public ProcessInstance findProcessInstance(long piid) throws WorkflowStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = getConnection();

            String sql = "SELECT " + entryName + ", " + entryState + " FROM " + entryTable + " WHERE " + entryId + " = ?";

            if (logger.isDebugEnabled()) {
                logger.debug("Executing SQL statement: " + sql);
            }

            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, piid);

            rset = stmt.executeQuery();
            rset.next();

            String workflowName = rset.getString(1);
            int state = rset.getInt(2);

            ProcessInstance processInstance = new DefaultProcessInstance(piid, workflowName);
            processInstance.setState(ProcessInstanceState.getProcessInstanceState(state));
            
            return processInstance;
            
        } catch (SQLException e) {
            throw new WorkflowStoreException("Error finding workflow instance #" + entryId);
        } finally {
            cleanup(conn, stmt, rset);
        }
    }

    public List findHistorySteps(long entryId) throws WorkflowStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;
        ResultSet rset = null;

        try {
            conn = getConnection();

            String sql = "SELECT " + stepId + ", " + stepStepId + ", " + stepActionId + ", " + stepOwner + ", " + stepStartDate + ", " + stepDueDate + ", " + stepFinishDate + ", " + stepStatus + ", " + stepActor + " FROM " + historyTable + " WHERE " + stepProcessId + " = ? ORDER BY " + stepId + " DESC";
            String sql2 = "SELECT " + stepPreviousId + " FROM " + historyPrevTable + " WHERE " + stepId + " = ?";

            if (logger.isDebugEnabled()) {
                logger.debug("Executing SQL statement: " + sql);
            }

            stmt = conn.prepareStatement(sql);

            if (logger.isDebugEnabled()) {
                logger.debug("Executing SQL statement: " + sql2);
            }

            stmt2 = conn.prepareStatement(sql2);
            stmt.setLong(1, entryId);

            rset = stmt.executeQuery();

            ArrayList currentSteps = new ArrayList();

            while (rset.next()) {
                long id = rset.getLong(1);
                int stepId = rset.getInt(2);
                int actionId = rset.getInt(3);
                String owner = rset.getString(4);
                Date startDate = rset.getTimestamp(5);
                Date dueDate = rset.getTimestamp(6);
                Date finishDate = rset.getTimestamp(7);
                String status = rset.getString(8);
                String actor = rset.getString(9);

                ArrayList<Long> prevIdsList = new ArrayList<Long>();
                stmt2.setLong(1, id);

                ResultSet rs = stmt2.executeQuery();

                while (rs.next()) {
                    long prevId = rs.getLong(1);
                    prevIdsList.add(new Long(prevId));
                }

                long[] prevIds = new long[prevIdsList.size()];
                int i = 0;

                for (Long aLong : prevIdsList) {
                    prevIds[i] = aLong.longValue();
                    i++;
                }

                DefaultStep step = new DefaultStep(id, entryId, stepId, actionId, owner, startDate, dueDate, finishDate, status, prevIds, actor);
                currentSteps.add(step);
            }

            return currentSteps;
        } catch (SQLException e) {
            throw new WorkflowStoreException("Unable to locate history steps for workflow instance #" + entryId, e);
        } finally {
            cleanup(null, stmt2, null);
            cleanup(conn, stmt, rset);
        }
    }


    public void moveToHistory(Step step, int actionId, Date finishDate, String status, String actor) throws WorkflowStoreException {
        markFinished(step, actionId, finishDate, status, actor);
        moveToHistory(step);
    }


    public Step markFinished(Step step, int actionId, Date finishDate, String status, String actor) throws WorkflowStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();

            String sql = "UPDATE " + currentTable + " SET " + stepStatus + " = ?, " + stepActionId + " = ?, " + stepFinishDate + " = ?, " + stepActor + " = ? WHERE " + stepId + " = ?";

            if (logger.isDebugEnabled()) {
                logger.debug("Executing SQL statement: " + sql);
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, actionId);
            stmt.setTimestamp(3, new Timestamp(finishDate.getTime()));
            stmt.setString(4, actor);
            stmt.setLong(5, step.getId());
            stmt.executeUpdate();

            DefaultStep theStep = (DefaultStep) step;
            theStep.setActionId(actionId);
            theStep.setFinishDate(finishDate);
            theStep.setStatus(status);
            theStep.setActor(actor);

            return theStep;
        } catch (SQLException e) {
            throw new WorkflowStoreException("Unable to mark step finished for #" + step.getProcessInstanceId(), e);
        } finally {
            cleanup(conn, stmt, null);
        }
    }

    public void moveToHistory(Step step) throws WorkflowStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();

            String sql = "INSERT INTO " + historyTable + " (" + stepId + ',' + stepProcessId + ", " + stepStepId + ", " + stepActionId + ", " + stepOwner + ", " + stepStartDate + ", " + stepFinishDate + ", " + stepStatus + ", " + stepActor + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            if (logger.isDebugEnabled()) {
                logger.debug("Executing SQL statement: " + sql);
            }

            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, step.getId());
            stmt.setLong(2, step.getProcessInstanceId());
            stmt.setInt(3, step.getStepId());
            stmt.setInt(4, step.getActionId());
            stmt.setString(5, step.getOwner());
            stmt.setTimestamp(6, new Timestamp(step.getStartDate().getTime()));

            if (step.getFinishDate() != null) {
                stmt.setTimestamp(7, new Timestamp(step.getFinishDate().getTime()));
            } else {
                stmt.setNull(7, Types.TIMESTAMP);
            }

            stmt.setString(8, step.getStatus());
            stmt.setString(9, step.getActor());
            stmt.executeUpdate();

            long[] previousIds = step.getPreviousStepIds();

            if ((previousIds != null) && (previousIds.length > 0)) {
                sql = "INSERT INTO " + historyPrevTable + " (" + stepId + ", " + stepPreviousId + ") VALUES (?, ?)";
                logger.debug("Executing SQL statement: " + sql);
                cleanup(null, stmt, null);
                stmt = conn.prepareStatement(sql);

                for (int i = 0; i < previousIds.length; i++) {
                    long previousId = previousIds[i];
                    stmt.setLong(1, step.getId());
                    stmt.setLong(2, previousId);
                    stmt.executeUpdate();
                }
            }

            sql = "DELETE FROM " + currentPrevTable + " WHERE " + stepId + " = ?";

            if (logger.isDebugEnabled()) {
                logger.debug("Executing SQL statement: " + sql);
            }

            cleanup(null, stmt, null);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, step.getId());
            stmt.executeUpdate();

            sql = "DELETE FROM " + currentTable + " WHERE " + stepId + " = ?";

            if (logger.isDebugEnabled()) {
                logger.debug("Executing SQL statement: " + sql);
            }

            cleanup(null, stmt, null);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, step.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new WorkflowStoreException("Unable to move current step to history step for #" + step.getProcessInstanceId(), e);
        } finally {
            cleanup(conn, stmt, null);
        }
    }

    public List query(WorkflowExpressionQuery e) throws WorkflowStoreException {
        //GURKAN;
        // If it is simple, call buildSimple()
        //  SELECT DISTINCT(ENTRY_ID) FROM OS_HISTORYSTEP WHERE FINISH_DATE < ?
        //
        // If it is nested, call doNestedNaturalJoin() if and only if the query is
        // ANDed including nested-nestd queries
        // If OR exists in any query call buildNested()
        //
        //doNestedNaturalJoin()
        //  This doNestedNaturalJoin() method improves performance of the queries if and only if
        //  the queries including nested queries are ANDed
        //
        //	SELECT DISTINCT (a1.ENTRY_ID) AS retrieved
        //		FROM OS_CURRENTSTEP AS a1 , OS_CURRENTSTEP AS a2 , OS_CURRENTSTEP AS a3 , OS_CURRENTSTEP AS a4
        //			WHERE ((a1.ENTRY_ID = a1.ENTRY_ID AND a1.ENTRY_ID = a2.ENTRY_ID) AND
        //					 (a2.ENTRY_ID = a3.ENTRY_ID AND a3.ENTRY_ID = a4.ENTRY_ID))
        //				AND ( a1.OWNER =  ?  AND a2.STATUS !=  ?  AND a3.OWNER =  ?  AND a4.STATUS !=  ?  )
        //
        //doNestedLeftJoin() //not used
        //  For this method to work, order of queries is matter
        //  This doNestedLeftJoin() method will generate the queries but it works if and only if
        //  the query is in correct order -- it is your luck
        //                SELECT DISTINCT (a0.ENTRY_ID) AS retrieved FROM OS_CURRENTSTEP AS a0
        //                                LEFT JOIN OS_CURRENTSTEP a1  ON a0.ENTRY_ID = a1.ENTRY_ID
        //
        //                                LEFT JOIN OS_CURRENTSTEP a2  ON a1.ENTRY_ID = a2.ENTRY_ID
        //                                LEFT JOIN OS_CURRENTSTEP a3  ON a2.ENTRY_ID = a3.ENTRY_ID
        //                                                WHERE a1.OWNER =  ? AND (a2.STATUS =  ?  OR a3.OWNER =  ?)
        //
        if (logger.isDebugEnabled()) {
            logger.debug("Starting Query");
        }

        Expression expression = e.getExpression();

        if (logger.isDebugEnabled()) {
            logger.debug("Have all variables");
        }

        if (expression.isNested()) {
            NestedExpression nestedExp = (NestedExpression) expression;

            StringBuffer sel = new StringBuffer();
            StringBuffer columns = new StringBuffer();
            StringBuffer leftJoin = new StringBuffer();
            StringBuffer where = new StringBuffer();
            StringBuffer whereComp = new StringBuffer();
            StringBuffer orderBy = new StringBuffer();
            List values = new LinkedList();
            List queries = new LinkedList();

            String columnName;
            String selectString;

            //Expression is nested and see if the expresion has OR
            if (checkIfORExists(nestedExp)) {
                //For doNestedLeftJoin() uncomment these -- again order is matter
                //and comment out last two lines where buildNested() is called
                //
                //columns.append("SELECT DISTINCT (");
                //columns.append("a0" + "." + stepProcessId);
                //columnName = "retrieved";
                //columns.append(") AS " + columnName);
                //columns.append(" FROM ");
                //columns.append(currentTable + " AS " + "a0");
                //where.append("WHERE ");
                //doNestedLeftJoin(e, nestedExp, leftJoin, where, values, queries, orderBy);
                //selectString = columns.toString() + " " + leftJoin.toString() + " " + where.toString() + " " + orderBy.toString();
                //System.out.println("LEFT JOIN ...");
                //
                //
                columnName = buildNested(nestedExp, sel, values);
                selectString = sel.toString();
            } else {
                columns.append("SELECT DISTINCT (");
                columns.append("a1" + '.' + stepProcessId);
                columnName = "retrieved";
                columns.append(") AS " + columnName);
                columns.append(" FROM ");
                where.append("WHERE ");

                doNestedNaturalJoin(e, nestedExp, columns, where, whereComp, values, queries, orderBy);
                selectString = columns.toString() + ' ' + leftJoin.toString() + ' ' + where.toString() + " AND ( " + whereComp.toString() + " ) " + ' ' + orderBy.toString();

                //              System.out.println("NATURAL JOIN ...");
            }

            //System.out.println("number of queries is      : " + queries.size());
            //System.out.println("values.toString()         : " + values.toString());
            //System.out.println("columnName                : " + columnName);
            //System.out.println("where                     : " + where);
            //System.out.println("whereComp                 : " + whereComp);
            //System.out.println("columns                   : " + columns);
            //          System.out.println("Query is : " + selectString + "\n");
            return doExpressionQuery(selectString, columnName, values);
        } else {
            // query is not empty ... it's a SIMPLE query
            // do what the old query did
            StringBuffer qry;
            List values = new LinkedList();

            qry = new StringBuffer();

            String columnName = buildSimple((FieldExpression) expression, qry, values);

            if (e.getSortOrder() != WorkflowExpressionQuery.SORT_NONE) {
                qry.append(" ORDER BY ");

                if (e.getOrderBy() != null) {
                    String fName = fieldName(e.getOrderBy());

                    qry.append(fName);

                    // To help w/ MySQL and Informix, you have to include the column in the query
                    String current = qry.toString();
                    String entry = current.substring(0, current.indexOf(columnName)) + columnName + "), " + fName + ' ';
                    entry += current.substring(current.indexOf(columnName) + columnName.length() + 1);

                    qry = new StringBuffer(entry);

                    if (e.getSortOrder() == WorkflowExpressionQuery.SORT_DESC) {
                        qry.append(" DESC");
                    } else {
                        qry.append(" ASC");
                    }
                } else {
                    qry.append(columnName);
                }
            }

            //System.out.println("Query is: " + qry.toString());
            return doExpressionQuery(qry.toString(), columnName, values);
        }
    }


    protected Connection getConnection() throws SQLException {
        closeConnWhenDone = true;

        return ds.getConnection();
    }

    protected long getNextEntrySequence(Connection c) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL statement: " + entrySequence);
        }

        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            stmt = c.prepareStatement(entrySequence);
            rset = stmt.executeQuery();
            rset.next();

            long id = rset.getLong(1);

            return id;
        } finally {
            cleanup(null, stmt, rset);
        }
    }

    protected long getNextStepSequence(Connection c) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL statement: " + stepSequence);
        }

        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            stmt = c.prepareStatement(stepSequence);
            rset = stmt.executeQuery();
            rset.next();

            long id = rset.getLong(1);

            return id;
        } finally {
            cleanup(null, stmt, rset);
        }
    }

    protected void addPreviousSteps(Connection conn, long id, long[] previousIds) throws SQLException {
        if ((previousIds != null) && (previousIds.length > 0)) {
            if (!((previousIds.length == 1) && (previousIds[0] == 0))) {
                String sql = "INSERT INTO " + currentPrevTable + " (" + stepId + ", " + stepPreviousId + ") VALUES (?, ?)";
                logger.debug("Executing SQL statement: " + sql);

                PreparedStatement stmt = conn.prepareStatement(sql);

                for (int i = 0; i < previousIds.length; i++) {
                    long previousId = previousIds[i];
                    stmt.setLong(1, id);
                    stmt.setLong(2, previousId);
                    stmt.executeUpdate();
                }

                cleanup(null, stmt, null);
            }
        }
    }

    protected void cleanup(Connection connection, Statement statement, ResultSet result) {
        if (result != null) {
            try {
                result.close();
            } catch (SQLException ex) {
                logger.error("Error closing resultset", ex);
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException ex) {
                logger.error("Error closing statement", ex);
            }
        }

        if ((connection != null) && closeConnWhenDone) {
            try {
                connection.close();
            } catch (SQLException ex) {
                logger.error("Error closing connection", ex);
            }
        }
    }

    protected long createCurrentStep(Connection conn, long entryId, int wfStepId, String owner, Date startDate, Date dueDate, String status) throws SQLException, WorkflowStoreException {
        boolean generatedKeys = GENERATED.equals(stepSequence);
        String sql = "INSERT INTO " + currentTable + " (" + (generatedKeys ? "" : (stepId + ',')) + stepProcessId + ", " + stepStepId + ", " + stepActionId + ", " + stepOwner + ", " + stepStartDate + ", " + stepDueDate + ", " + stepFinishDate + ", " + stepStatus + ", " + stepActor + " ) VALUES (" + (generatedKeys ? "" : "?, ") + "?, ?, null, ?, ?, ?, null, ?, null)";

        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL statement: " + sql);
        }

        PreparedStatement stmt = generatedKeys ? conn.prepareStatement(sql, new String[] {
                stepId
            }) : conn.prepareStatement(sql);

        long id = 0;
        int idxCorrection = 1;

        if (!generatedKeys) {
            id = getNextStepSequence(conn);
            stmt.setLong(1, id);
            idxCorrection = 0;
        }

        stmt.setLong(2 - idxCorrection, entryId);
        stmt.setInt(3 - idxCorrection, wfStepId);
        stmt.setString(4 - idxCorrection, owner);
        stmt.setTimestamp(5 - idxCorrection, new Timestamp(startDate.getTime()));

        if (dueDate != null) {
            stmt.setTimestamp(6 - idxCorrection, new Timestamp(dueDate.getTime()));
        } else {
            stmt.setNull(6 - idxCorrection, Types.TIMESTAMP);
        }

        stmt.setString(7 - idxCorrection, status);
        stmt.executeUpdate();

        try {
            if (generatedKeys) {
                id = getGeneratedKey(stmt, stepId);
            }
        } finally {
            cleanup(null, stmt, null);
        }

        return id;
    }

    ////////////METHOD #3 OF 3 //////////////////
    ////////// ...gur;  ////////////////////
    //kardes
    void doNestedNaturalJoin(WorkflowExpressionQuery e, NestedExpression nestedExpression, StringBuffer columns, StringBuffer where, StringBuffer whereComp, List values, List queries, StringBuffer orderBy) { // throws WorkflowStoreException {

        Object value;
        Field currentExpField;

        int numberOfExp = nestedExpression.getExpressionCount();

        for (int i = 0; i < numberOfExp; i++) { //ori

            //for (i = numberOfExp; i > 0; i--) { //reverse 1 of 3
            Expression expression = nestedExpression.getExpression(i); //ori

            //Expression expression = nestedExpression.getExpression(i - 1); //reverse 2 of 3
            if (!(expression.isNested())) {
                FieldExpression fieldExp = (FieldExpression) expression;

                FieldExpression fieldExpBeforeCurrent;
                queries.add(expression);

                int queryId = queries.size();

                if (queryId > 1) {
                    columns.append(" , ");
                }

                //do; OS_CURRENTSTEP AS a1 ....
                if (fieldExp.getContext() == Context.CURRENT_STEPS) {
                    columns.append(currentTable + " AS " + 'a' + queryId);
                } else if (fieldExp.getContext() == Context.HISTORY_STEPS) {
                    columns.append(historyTable + " AS " + 'a' + queryId);
                } else {
                    columns.append(entryTable + " AS " + 'a' + queryId);
                }

                ///////// beginning of WHERE JOINS/s :   //////////////////////////////////////////
                //do for first query; a1.ENTRY_ID = a1.ENTRY_ID
                if (queryId == 1) {
                    where.append("a1" + '.' + stepProcessId);
                    where.append(" = ");

                    if (fieldExp.getContext() == Context.CURRENT_STEPS) {
                        where.append("a" + queryId + '.' + stepProcessId);
                    } else if (fieldExp.getContext() == Context.HISTORY_STEPS) {
                        where.append("a" + queryId + '.' + stepProcessId);
                    } else {
                        where.append("a" + queryId + '.' + entryId);
                    }
                }

                //do; a1.ENTRY_ID = a2.ENTRY_ID
                if (queryId > 1) {
                    fieldExpBeforeCurrent = (FieldExpression) queries.get(queryId - 2);

                    if (fieldExpBeforeCurrent.getContext() == Context.CURRENT_STEPS) {
                        where.append("a" + (queryId - 1) + '.' + stepProcessId);
                    } else if (fieldExpBeforeCurrent.getContext() == Context.HISTORY_STEPS) {
                        where.append("a" + (queryId - 1) + '.' + stepProcessId);
                    } else {
                        where.append("a" + (queryId - 1) + '.' + entryId);
                    }

                    where.append(" = ");

                    if (fieldExp.getContext() == Context.CURRENT_STEPS) {
                        where.append("a" + queryId + '.' + stepProcessId);
                    } else if (fieldExp.getContext() == Context.HISTORY_STEPS) {
                        where.append("a" + queryId + '.' + stepProcessId);
                    } else {
                        where.append("a" + queryId + '.' + entryId);
                    }
                }

                ///////// end of LEFT JOIN : "LEFT JOIN OS_CURRENTSTEP a1  ON a0.ENTRY_ID = a1.ENTRY_ID
                //
                //////// BEGINNING OF WHERE clause //////////////////////////////////////////////////
                value = fieldExp.getValue();
                currentExpField = fieldExp.getField();

                //if the Expression is negated and FieldExpression is "EQUALS", we need to negate that FieldExpression
                if (expression.isNegate()) {
                    //do ; a2.STATUS !=
                    whereComp.append("a" + queryId + '.' + fieldName(fieldExp.getField()));

                    switch (fieldExp.getOperator()) { //WHERE a1.STATUS !=
                    case EQUALS:

                        if (value == null) {
                            whereComp.append(" IS NOT ");
                        } else {
                            whereComp.append(" != ");
                        }

                        break;

                    case NOT_EQUALS:

                        if (value == null) {
                            whereComp.append(" IS ");
                        } else {
                            whereComp.append(" = ");
                        }

                        break;

                    case GT:
                        whereComp.append(" < ");

                        break;

                    case LT:
                        whereComp.append(" > ");

                        break;

                    default:
                        whereComp.append(" != ");

                        break;
                    }

                    switch (currentExpField) {
                    case START_DATE:
                    case FINISH_DATE:
                        values.add(new Timestamp(((java.util.Date) value).getTime()));

                        break;

                    default:

                        if (value == null) {
                            values.add(null);
                        } else {
                            values.add(value);
                        }

                        break;
                    }
                } else {
                    //do; a1.OWNER =
                    whereComp.append("a" + queryId + '.' + fieldName(fieldExp.getField()));

                    switch (fieldExp.getOperator()) { //WHERE a2.FINISH_DATE <
                    case EQUALS:

                        if (value == null) {
                            whereComp.append(" IS ");
                        } else {
                            whereComp.append(" = ");
                        }

                        break;

                    case NOT_EQUALS:

                        if (value == null) {
                            whereComp.append(" IS NOT ");
                        } else {
                            whereComp.append(" <> ");
                        }

                        break;

                    case GT:
                        whereComp.append(" > ");

                        break;

                    case LT:
                        whereComp.append(" < ");

                        break;

                    default:
                        whereComp.append(" = ");

                        break;
                    }

                    switch (currentExpField) {
                    case START_DATE:
                    case FINISH_DATE:
                        values.add(new Timestamp(((java.util.Date) value).getTime()));

                        break;

                    default:

                        if (value == null) {
                            values.add(null);
                        } else {
                            values.add(value);
                        }

                        break;
                    }
                }

                //do; a1.OWNER =  ?  ... a2.STATUS != ?
                whereComp.append(" ? ");

                //////// END OF WHERE clause////////////////////////////////////////////////////////////
                if ((e.getSortOrder() != WorkflowExpressionQuery.SORT_NONE) && (e.getOrderBy() != null)) {
                    // System.out.println("ORDER BY ; queries.size() : " + queries.size());
                    orderBy.append(" ORDER BY ");
                    orderBy.append("a1" + '.' + fieldName(e.getOrderBy()));

                    if (e.getSortOrder() == WorkflowExpressionQuery.SORT_ASC) {
                        orderBy.append(" ASC");
                    } else if (e.getSortOrder() == WorkflowExpressionQuery.SORT_DESC) {
                        orderBy.append(" DESC");
                    }
                }
            } else {
                NestedExpression nestedExp = (NestedExpression) expression;

                where.append('(');

                doNestedNaturalJoin(e, nestedExp, columns, where, whereComp, values, queries, orderBy);

                where.append(')');
            }

            //add AND or OR clause between the queries
            if (i < (numberOfExp - 1)) { //ori

                //if (i > 1) { //reverse 3 of 3
                if (nestedExpression.getExpressionOperator() == LogicalOperator.AND) {
                    where.append(" AND ");
                    whereComp.append(" AND ");
                } else {
                    where.append(" OR ");
                    whereComp.append(" OR ");
                }
            }
        }
    }

    private static long getGeneratedKey(PreparedStatement ps, String pkColumnName) throws SQLException, WorkflowStoreException {
        ResultSet rs = ps.getGeneratedKeys();

        if (!rs.next()) {
            throw new WorkflowStoreException("Empty ResultSet returned by getGeneratedKeys");
        }

        return rs.getLong(pkColumnName);
    }

    private static PreparedStatement prepareStatementWithKeys(Connection conn, String sql, String pkColumnName) throws SQLException {
        return conn.prepareStatement(sql, new String[] {pkColumnName});
    }

    private String getInitProperty(Map<String,String> props, String strName, String strDefault) {
        String o = props.get(strName);

        if (o == null) {
            return strDefault;
        }

        return o;
    }

    private String buildNested(NestedExpression nestedExpression, StringBuffer sel, List values) {
        sel.append("SELECT DISTINCT(");

        // Changed by Anthony on 2 June 2004, to query from OS_CURRENTSTEP instead
        //sel.append(entryId);
        sel.append(stepProcessId);
        sel.append(") FROM ");

        // Changed by Anthony on 2 June 2004, to query from OS_CURRENTSTEP instead
        // sel.append(entryTable);
        sel.append(currentTable);

        if (logger.isDebugEnabled()) {
            logger.debug("Thus far, query is: " + sel.toString());
        }

        for (int i = 0; i < nestedExpression.getExpressionCount(); i++) {
            Expression expression = nestedExpression.getExpression(i);

            if (i == 0) {
                sel.append(" WHERE ");
            } else {
                if (nestedExpression.getExpressionOperator() == LogicalOperator.AND) {
                    sel.append(" AND ");
                } else {
                    sel.append(" OR ");
                }
            }

            if (expression.isNegate()) {
                sel.append(" NOT ");
            }

            // Changed by Anthony on 2 June 2004, to query from OS_CURRENTSTEP instead
            // sel.append(entryId);
            sel.append(stepProcessId);
            sel.append(" IN (");

            if (expression.isNested()) {
                this.buildNested((NestedExpression) nestedExpression.getExpression(i), sel, values);
            } else {
                FieldExpression sub = (FieldExpression) nestedExpression.getExpression(i);
                this.buildSimple(sub, sel, values);
            }

            sel.append(')');
        }

        // Changed by Anthony on 2 June 2004, to query from OS_CURRENTSTEP instead
        // return (entryId);
        return (stepProcessId);
    }

    private String buildSimple(FieldExpression fieldExpression, StringBuffer sel, List values) {
        String table;
        String columnName;

        if (fieldExpression.getContext() == Context.CURRENT_STEPS) {
            table = currentTable;
            columnName = stepProcessId;
        } else if (fieldExpression.getContext() == Context.HISTORY_STEPS) {
            table = historyTable;
            columnName = stepProcessId;
        } else {
            table = entryTable;
            columnName = entryId;
        }

        sel.append("SELECT DISTINCT(");
        sel.append(columnName);
        sel.append(") FROM ");
        sel.append(table);
        sel.append(" WHERE ");
        queryComparison(fieldExpression, sel, values);

        return columnName;
    }

    private List doExpressionQuery(String sel, String columnName, List values) throws WorkflowStoreException {
        if (logger.isDebugEnabled()) {
            logger.debug(sel);
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List results = new ArrayList();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sel);

            if (!values.isEmpty()) {
                for (int i = 1; i <= values.size(); i++) {
                    stmt.setObject(i, values.get(i - 1));
                }
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                // get entryIds and add to results list
                Long id = new Long(rs.getLong(columnName));
                results.add(id);
            }

            return results;
        } catch (SQLException ex) {
            throw new WorkflowStoreException("SQL Exception in query: " + ex.getMessage());
        } finally {
            cleanup(conn, stmt, rs);
        }
    }

    private static String escape(String s) {
        StringBuffer sb = new StringBuffer(s);

        char c;
        char[] chars = s.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            c = chars[i];

            switch (c) {
            case '\'':
                sb.insert(i, '\'');
                i++;

                break;

            case '\\':
                sb.insert(i, '\\');
                i++;
            }
        }

        return sb.toString();
    }

    private String fieldName(Field field) {
        switch (field) {
        case ACTION: // actionId
            return stepActionId;

        case ACTOR:
            return stepActor;

        case FINISH_DATE:
            return stepFinishDate;

        case OWNER:
            return stepOwner;

        case START_DATE:
            return stepStartDate;

        case STEP: // stepId
            return stepStepId;

        case STATUS:
            return stepStatus;

        case STATE:
            return entryState;

        case NAME:
            return entryName;

        case DUE_DATE:
            return stepDueDate;

        default:
            return "1";
        }
    }

    private Object lookup(String location) throws NamingException {
        try {
            InitialContext context = new InitialContext();

            try {
                return context.lookup(location);
            } catch (NamingException e) {
                //ok, couldn't find it, look in env
                return context.lookup("java:comp/env/" + location);
            }
        } catch (NamingException e) {
            throw e;
        }
    }

/*
    private String queryComparison(WorkflowQuery query) {
        Object value = query.getValue();
        int operator = query.getOperator();
        int field = query.getField();

        //int type = query.getType();
        String oper;

        switch (operator) {
        case WorkflowQuery.EQUALS:
            oper = " = ";

            break;

        case WorkflowQuery.NOT_EQUALS:
            oper = " <> ";

            break;

        case WorkflowQuery.GT:
            oper = " > ";

            break;

        case WorkflowQuery.LT:
            oper = " < ";

            break;

        default:
            oper = " = ";
        }

        String left = fieldName(field);
        String right;

        if (value != null) {
            right = '\'' + escape(value.toString()) + '\'';
        } else {
            right = "null";
        }

        return left + oper + right;
    }
*/
    /**
     * Method queryComparison
     *
     * @param    expression          a  FieldExpression
     * @param    sel                 a  StringBuffer
     *
     */
    private void queryComparison(FieldExpression expression, StringBuffer sel, List values) {
        
        Field field = expression.getField();
        Operator operator = expression.getOperator();
        Object value = expression.getValue();

        String oper;

        switch (operator) {
        case EQUALS:

            if (value == null) {
                oper = " IS ";
            } else {
                oper = " = ";
            }

            break;

        case NOT_EQUALS:

            if (value == null) {
                oper = " IS NOT ";
            } else {
                oper = " <> ";
            }

            break;

        case GT:
            oper = " > ";

            break;

        case LT:
            oper = " < ";

            break;

        default:
            oper = " = ";
        }

        String left = fieldName(field);
        String right = "?";

        switch (field) {
        case FINISH_DATE:
            values.add(new Timestamp(((Date) value).getTime()));

            break;

        case START_DATE:
            values.add(new Timestamp(((Date) value).getTime()));

            break;

        case DUE_DATE:
            values.add(new Timestamp(((Date) value).getTime()));

            break;

        default:

            if (value == null) {
                right = "null";
            } else {
                values.add(value);
            }
        }

        sel.append(left);
        sel.append(oper);
        sel.append(right);
    }

/*
    private String queryWhere(WorkflowQuery query) {
        if (query.getLeft() == null) {
            // leaf node
            return queryComparison(query);
        } else {
            int operator = query.getOperator();
            WorkflowQuery left = query.getLeft();
            WorkflowQuery right = query.getRight();

            switch (operator) {
            case WorkflowQuery.AND:
                return '(' + queryWhere(left) + " AND " + queryWhere(right) + ')';

            case WorkflowQuery.OR:
                return '(' + queryWhere(left) + " OR " + queryWhere(right) + ')';

            case WorkflowQuery.XOR:
                return '(' + queryWhere(left) + " XOR " + queryWhere(right) + ')';
            }
        }

        return ""; // not sure if we should throw an exception or how this should be handled
    }
*/
}
