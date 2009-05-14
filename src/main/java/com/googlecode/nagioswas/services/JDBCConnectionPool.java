package com.googlecode.nagioswas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.JMException;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSJDBCConnectionPoolStats;

public class JDBCConnectionPool extends WASPerformanceService {

    public static JDBCConnectionPool create(AdminClient adminClient, Perf perf, String name) throws ConnectorException, JMException {
        return new JDBCConnectionPool(adminClient, perf, queryMbean(adminClient, getMBeanQuery() + ",name=" + name + ",*"));
    }
    
    public static List<JDBCConnectionPool> create(AdminClient adminClient, Perf perf) throws ConnectorException, JMException {
        List<JDBCConnectionPool> pools = new ArrayList<JDBCConnectionPool>();
        
        Set<ObjectName> mbeans = queryMBeans(adminClient, getMBeanQuery() + ",*");
        
        for(ObjectName mbean : mbeans) {
            pools.add(new JDBCConnectionPool(adminClient, perf, mbean));
        }
        
        return pools;
    }
    
    
    private static String getMBeanQuery() {
        return "WebSphere:type=JDBCProvider";
    }
    
    private JDBCConnectionPool(AdminClient adminClient, Perf perf, ObjectName mBean) {
        super(adminClient, perf, mBean);
    }


    public String getName() throws JMException,
            ConnectorException {
        return (String) adminClient.getAttribute(mBean, "name");
    }
    
    public long getPoolSize(Perf perf) throws JMException, ConnectorException {
        return getBoundedStats(WSJDBCConnectionPoolStats.PoolSize).getCurrent();
    }
    
    public long getPoolMax(Perf perf) throws JMException, ConnectorException {
        return getBoundedStats(WSJDBCConnectionPoolStats.PoolSize).getUpperBound();
    }
}
