package com.googlecode.nagioswas.checks;

import java.util.ArrayList;
import java.util.List;

import javax.management.JMException;

import com.googlecode.nagioswas.checks.CheckResult.ResultLevel;
import com.googlecode.nagioswas.services.JDBCConnectionPool;
import com.googlecode.nagioswas.services.Perf;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

public class JDBCConnectionPoolCheck extends Check {
    
    public JDBCConnectionPoolCheck(AdminClient client, Perf perf) {
        super(client, perf);
    }

    public CheckResult check(int critical, int warning, String name) throws ConnectorException, JMException {
        List<JDBCConnectionPool> pools;
        
        if(name == null) {
            // check all thread pools
            pools = JDBCConnectionPool.create(client, perf);
        } else {
            pools = new ArrayList<JDBCConnectionPool>();
            pools.add(JDBCConnectionPool.create(client, perf, name));
        }
            
        boolean isCritical = false;
        boolean isWarning = false;
        StringBuffer output = new StringBuffer();
        for(JDBCConnectionPool pool : pools) {
            long size = pool.getPoolSize(perf);
            long max = pool.getPoolMax(perf);
            
            long ratio = calcRatio(size, max);
            
            if(isCritical(ratio, critical)) {
                isCritical = true;
            }
            if(isWarning(ratio, warning)) {
                isCritical = true;
            }
            
            if(output.length() > 0) {
                output.append(", ");
            }
            output.append(formatBoundedMessage(ratio, size, max, pool.getName()));
        }
        
        ResultLevel level;
        
        if(isCritical) {
            level = ResultLevel.CRITICAL;
        } else if(isWarning) {
            level = ResultLevel.WARNING;
        } else {
            level = ResultLevel.OK;
        }
        
        return new CheckResult(level, "connection pool size: " + output.toString());
    }
}
