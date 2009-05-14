package com.googlecode.nagioswas.checks;

import java.util.ArrayList;
import java.util.List;

import javax.management.JMException;

import com.googlecode.nagioswas.checks.CheckResult.ResultLevel;
import com.googlecode.nagioswas.services.Perf;
import com.googlecode.nagioswas.services.SessionManager;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

public class LiveSessionCheck extends Check {
    
    public LiveSessionCheck(AdminClient client, Perf perf) {
        super(client, perf);
    }

    public CheckResult check(int critical, int warning, String name) throws ConnectorException, JMException {
        List<SessionManager> managers;
        
        if(name == null) {
            // check all thread pools
            managers = SessionManager.create(client, perf);
        } else {
            managers = new ArrayList<SessionManager>();
            managers.add(SessionManager.create(client, perf, name));
        }
            
        boolean isCritical = false;
        boolean isWarning = false;
        StringBuffer output = new StringBuffer();
        for(SessionManager manager : managers) {
            long size = manager.getLiveCount(perf);
            
            if(isCritical(size, critical)) {
                isCritical = true;
            }
            if(isWarning(size, warning)) {
                isCritical = true;
            }
            
            if(output.length() > 0) {
                output.append(", ");
            }
            output.append(formatRangedMessage(size, manager.getName()));
        }
        
        ResultLevel level;
        
        if(isCritical) {
            level = ResultLevel.CRITICAL;
        } else if(isWarning) {
            level = ResultLevel.WARNING;
        } else {
            level = ResultLevel.OK;
        }
        
        return new CheckResult(level, "live sessions: " + output.toString());
    }
}
