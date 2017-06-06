package usage;



/**
 * Basic workflow flow
 */


public interface LeaveRequest  {

    final static String SIMPLE = "Holiday";
    final static String COMPLEX = "LeaveRequest";
    
    final static int INITIAL_ACTION =   1;
    
    // Steps
    final static int EMPLOYEE_REQUEST_STEP         = 100;
    final static int LINE_MANAGER_DESCISION_STEP    = 200;
    final static int HUMAN_RESOURCES_DESCISION_STEP = 300;
    final static int NOTIFY_EMPLOYEE_STEP          = 400;
    
    
    // Actions - Employee
    final static int REQUEST_HOLIDAYS              = 101;
    final static int REQUEST_LEAVE                 = REQUEST_HOLIDAYS;
    
    // Actions - Manager
    final static int LINE_MANAGER_APPROVES         = 201;
    final static int LINE_MANAGER_DENIES           = 202;

    // Actions - HR
    final static int HUMAN_RESOURCES_APPROVES      = 301;
    final static int HUMAN_RESOURCES_DENIES        = 302;

    // Actions - By E-mail System
    final static int NOTIFY_EMPLOYEE               = 401;
    
}
