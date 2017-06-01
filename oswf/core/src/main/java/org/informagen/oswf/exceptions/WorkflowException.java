package org.informagen.oswf.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;


/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 10, 2003
 * Time: 11:26:07 AM
 */
public class WorkflowException extends Exception {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private Throwable rootCause;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public WorkflowException() {
    }

    public WorkflowException(String s) {
        super(s);
    }

    public WorkflowException(String s, Throwable rootCause) {
        super(s);
        this.rootCause = rootCause;
    }

    public WorkflowException(Throwable rootCause) {
        this.rootCause = rootCause;
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public Throwable getCause() {
        return rootCause;
    }

    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        String msg = super.getMessage();

        if (msg != null) {
            sb.append(msg);

            if (rootCause != null) {
                sb.append(": ");
            }
        }

        if (rootCause != null) {
            sb.append("root cause: " + ((rootCause.getMessage() == null) ? rootCause.toString() : rootCause.getMessage()));
        }

        return sb.toString();
    }

    public Throwable getRootCause() {
        return rootCause;
    }

    public void printStackTrace() {
        super.printStackTrace();

        if (rootCause != null) {
            synchronized (System.err) {
                System.err.println("\nRoot cause:");
                rootCause.printStackTrace();
            }
        }
    }

    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);

        if (rootCause != null) {
            synchronized (s) {
                s.println("\nRoot cause:");
                rootCause.printStackTrace(s);
            }
        }
    }

    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);

        if (rootCause != null) {
            synchronized (s) {
                s.println("\nRoot cause:");
                rootCause.printStackTrace(s);
            }
        }
    }
}
