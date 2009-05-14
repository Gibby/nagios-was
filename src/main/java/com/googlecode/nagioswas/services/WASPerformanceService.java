package com.googlecode.nagioswas.services;

import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSBoundedRangeStatistic;
import com.ibm.websphere.pmi.stat.WSRangeStatistic;
import com.ibm.websphere.pmi.stat.WSStats;

public abstract class WASPerformanceService extends WASService {

    protected Perf perf;
    private WSStats wsStats;
    
    public WASPerformanceService(AdminClient adminClient, Perf perf, ObjectName mBean) {
        super(adminClient, mBean);
        this.perf = perf;
    }

    protected WSRangeStatistic getRangedStats(int stat) throws JMException, ConnectorException {
        WSStats wsStats = lazyLoadStats();
        
        return (WSRangeStatistic) wsStats.getStatistic (stat);
    }

    protected WSBoundedRangeStatistic getBoundedStats(int stat) throws JMException, ConnectorException {
        WSStats wsStats = lazyLoadStats();
        
        return (WSBoundedRangeStatistic) wsStats.getStatistic (stat);
    }
    
    protected WSStats lazyLoadStats() throws InstanceNotFoundException,
            MBeanException, ReflectionException, ConnectorException {
        if(wsStats == null) {
            
            String[] signature = new String[] { "javax.management.ObjectName",
                    "java.lang.Boolean" };
            Object[] params = new Object[] { mBean, Boolean.FALSE };
            wsStats = (WSStats) adminClient.invoke(perf.getMBean(),
                    "getStatsObject", params, signature);
        }
        return wsStats;
    }
}
