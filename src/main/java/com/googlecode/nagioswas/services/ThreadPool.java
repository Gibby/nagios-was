package com.googlecode.nagioswas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.JMException;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSThreadPoolStats;

public class ThreadPool extends WASPerformanceService {

    public static ThreadPool create(AdminClient adminClient, Perf perf, String name) throws ConnectorException, JMException {
        return new ThreadPool(adminClient, perf, queryMbean(adminClient, getMBeanQuery() + ",name=" + name + ",*"));
    }
    
    public static List<ThreadPool> create(AdminClient adminClient, Perf perf) throws ConnectorException, JMException {
        List<ThreadPool> pools = new ArrayList<ThreadPool>();
        
        Set<ObjectName> mbeans = queryMBeans(adminClient, getMBeanQuery() + ",*");
        
        for(ObjectName mbean : mbeans) {
            pools.add(new ThreadPool(adminClient, perf, mbean));
        }
        
        return pools;
    }
    
    
    private static String getMBeanQuery() {
        return "WebSphere:type=ThreadPool";
    }
    
    private ThreadPool(AdminClient adminClient, Perf perf, ObjectName mBean) {
        super(adminClient, perf, mBean);
    }


    public String getName() throws JMException,
            ConnectorException {
        return (String) adminClient.getAttribute(mBean, "name");
    }
    
    public long getThreadPoolSize(Perf perf) throws JMException, ConnectorException {
        return getBoundedStats(WSThreadPoolStats.PoolSize).getCurrent();
    }
    
    public long getThreadPoolMax(Perf perf) throws JMException, ConnectorException {
        return getBoundedStats(WSThreadPoolStats.PoolSize).getUpperBound();
    }
}
