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
        if(name == null || name.contains("*")) {
            // check all thread pools
            managers = SessionManager.create(client, perf);
        } else {
            managers = new ArrayList<SessionManager>();
            managers.add(SessionManager.create(client, perf, name));
        }
            
        boolean isCritical = false;
        boolean isWarning = false;
        StringBuffer output = new StringBuffer();
        StringBuffer perfOutput = new StringBuffer();

        long totalSize = 0;
        
        for(SessionManager manager : managers) {
            if(matchName(name, manager.getName())) {
            
                long size = manager.getLiveCount(perf);
                totalSize += size;
                
                if(isCritical(size, critical)) {
                    isCritical = true;
                }
                if(isWarning(size, warning)) {
                    isWarning = true;
                }
                
                if(output.length() > 0) {
                    output.append(", ");
                }
                output.append(formatRangedMessage(size, manager.getName()));
                perfOutput.append(formatPerfData(size, "", critical, warning, 
                        escapePerfLabel(manager.getName())));
            }
        }

        if(isCritical(totalSize, critical)) {
            isCritical = true;
        }
        if(isWarning(totalSize, warning)) {
            isWarning = true;
        }
        
        output.insert(0, formatRangedMessage(totalSize, "total") + ", ");
        perfOutput.insert(0, formatPerfData(totalSize, "", critical, warning, 
                escapePerfLabel("total")));
        
        ResultLevel level;
        
        if(isCritical) {
            level = ResultLevel.CRITICAL;
        } else if(isWarning) {
            level = ResultLevel.WARNING;
        } else {
            level = ResultLevel.OK;
        }
        
        return new CheckResult(level, "live sessions: " + output.toString()
                + "|" + perfOutput.toString());
    }
    
    private boolean matchName(String pattern, String name) {
        if(pattern == null) {
            // no pattern provided, match all
            return true;
        } else if(pattern.endsWith("*")) {
            return name.startsWith(pattern.substring(0, pattern.length() - 1));
        } else {
            return pattern.equals(name);
        }
    }
}
