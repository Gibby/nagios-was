package com.googlecode.nagioswas.checks;

import java.util.ArrayList;
import java.util.List;

import javax.management.JMException;

import com.googlecode.nagioswas.checks.CheckResult.ResultLevel;
import com.googlecode.nagioswas.services.Perf;
import com.googlecode.nagioswas.services.ThreadPool;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

public class ThreadPoolCheck extends Check {
    
    public ThreadPoolCheck(AdminClient client, Perf perf) {
        super(client, perf);
    }

    public CheckResult check(int critical, int warning, String name) throws ConnectorException, JMException {
        List<ThreadPool> pools;
        
        if(name == null) {
            // check all thread pools
            pools = ThreadPool.create(client, perf);
        } else {
            pools = new ArrayList<ThreadPool>();
            pools.add(ThreadPool.create(client, perf, name));
        }
            
        boolean isCritical = false;
        boolean isWarning = false;
        StringBuffer output = new StringBuffer();
        StringBuffer perfOutput = new StringBuffer();

        for(ThreadPool pool : pools) {
            long size = pool.getThreadPoolSize(perf);
            long max = pool.getThreadPoolMax(perf);
            
            double ratio = calcRatio(size, max);
            
            if(isCritical(ratio, critical)) {
                isCritical = true;
            }
            if(isWarning(ratio, warning)) {
                isWarning = true;
            }
            
            if(output.length() > 0) {
                output.append(", ");
            }
            output.append(formatBoundedMessage(ratio, size, max, pool.getName()));
            perfOutput.append(formatPerfData(ratio, "%", critical, warning, escapePerfLabel(pool.getName())));

        }
        
        ResultLevel level;
        
        if(isCritical) {
            level = ResultLevel.CRITICAL;
        } else if(isWarning) {
            level = ResultLevel.WARNING;
        } else {
            level = ResultLevel.OK;
        }
        
        return new CheckResult(level, "thread pool size: " + output.toString()
                + "|" + perfOutput.toString());
    }
}
