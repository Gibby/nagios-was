package com.googlecode.nagioswas.checks;

import javax.management.JMException;

import com.googlecode.nagioswas.checks.CheckResult.ResultLevel;
import com.googlecode.nagioswas.services.Perf;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

public abstract class Check {

    protected AdminClient client;
    protected Perf perf; 
    

    public Check(AdminClient client, Perf perf) {
        this.client = client;
        this.perf = perf;
    }
    
    public abstract CheckResult check(int critical, int warning, String name) throws ConnectorException, JMException;
    
    protected long calcRatio(long value, long max) {
        return Math.round((double)value/max * 100);
    }

    protected boolean isCritical(long ratio, long critical) {
        return ratio >= critical;
    }

    protected boolean isWarning(long ratio, long warning) {
        return ratio >= warning;
    }
    
    protected ResultLevel checkResult(long ratio, int critical, int warning)  {
        if(isCritical(ratio, critical)) {
            return ResultLevel.CRITICAL;
        } else if(isWarning(ratio, warning)) {
            return ResultLevel.WARNING;
        } else {
            return ResultLevel.OK;
        }
    }

    protected String formatRangedMessage(long value, String message) {
        if(message != null) {        
            return message + " " + value;
        } else {
            return Long.toString(value);
        }
    }
    
    protected String formatBoundedMessage(long ratio, long value, long max, String message) {
        if(message != null) {        
            return message + " " + value + "/" + max + " (" + ratio + "%)";
        } else {
            return value + "/" + max + " (" + ratio + "%)";
        }
    }
}
