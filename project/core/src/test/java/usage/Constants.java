package usage;


public interface Constants  {
    
    final static int INITIAL_ACTION    =   1;
    final static int CONTRACTOR_ACTION =   2;
    
    // Steps
    final static int EMPLOYEE_REQUEST_STEP         = 100;
    final static int LINE_MANAGER_CLAIM_STEP       = 250;
    final static int LINE_MANAGER_REVISION_STEP    = 200;
    final static int HUMAN_RESOURCES_REVISION_STEP = 300;
    final static int NOTIFY_EMPLOYEE_STEP          = 400;
    
    
    // Actions - Employee
    final static int REQUEST_HOLIDAYS              = 101;
    final static int REQUEST_LEAVE                 = REQUEST_HOLIDAYS;
    
    // Actions = Manager
    final static int CLAIM_WORK_ITEM               = 251;
    final static int LINE_MANAGER_APPROVES         = 201;
    final static int LINE_MANAGER_DENIES           = 202;
    final static int RELEASE_WORK_ITEM             = 203;

    // Actions - HR
    final static int HUMAN_RESOURCES_APPROVES      = 301;
    final static int HUMAN_RESOURCES_DENIES        = 302;

    // Actions - By E-mail System
    final static int NOTIFY_EMPLOYEE               = 401;
    
}
