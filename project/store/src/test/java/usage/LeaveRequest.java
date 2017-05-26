package usage;



/**
 * Basic workflow flow
 */


public interface LeaveRequest  {

    // Process Definitions
    final static String SIMPLE  = "Holiday";
    final static String COMPLEX = "LeaveRequest";
    final static String ALT     = "LeaveRequest Alt";
    final static String WRONG   = "LeaveRequest Wrong";
    
    // Initial Actions
    final static int INITIAL_ACTION =   1;
    
    // Steps
    final static int EMPLOYEE_REQUEST_STEP         = 100;
    final static int LINE_MANAGER_REVISION_STEP    = 200;
    final static int HUMAN_RESOURCES_REVISION_STEP = 300;
    final static int NOTIFY_EMPLOYEE_STEP          = 400;
    
    
    // Actions - Employee
    final static int REQUEST_HOLIDAYS              = 101;
    
    // Actions - Manager
    final static int LINE_MANAGER_APPROVES         = 201;
    final static int LINE_MANAGER_DENIES           = 202;

    // Actions - HR
    final static int HUMAN_RESOURCES_APPROVES      = 301;
    final static int HUMAN_RESOURCES_DENIES        = 302;

    // Actions - Semi-Automatic
    final static int NOTIFY_EMPLOYEE               = 401;
    
}
