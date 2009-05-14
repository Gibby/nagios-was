package com.googlecode.nagioswas.services;

import javax.management.JMException;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSJVMStats;

public class JavaVirtualMachine extends WASPerformanceService {

    public static JavaVirtualMachine create(AdminClient adminClient, Perf perf)
            throws ConnectorException, JMException {
        return new JavaVirtualMachine(adminClient, perf, queryMbean(adminClient,
                "WebSphere:type=JVM,*"));
    }

    public JavaVirtualMachine(AdminClient adminClient, Perf perf, ObjectName mBean) {
        super(adminClient, perf, mBean);
    }
    
    public long getHeapSize() throws JMException, ConnectorException {
        return getBoundedStats(WSJVMStats.HeapSize).getCurrent(); 
    }
    
    public long getHeapSizeMax() throws JMException, ConnectorException {
        return getBoundedStats(WSJVMStats.HeapSize).getUpperBound(); 
    }
}
