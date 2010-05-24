package com.googlecode.nagioswas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.JMException;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSSessionManagementStats;

public class SessionManager extends WASPerformanceService {

    public static SessionManager create(AdminClient adminClient, Perf perf, String name) throws ConnectorException, JMException {
        return new SessionManager(adminClient, perf, queryMbean(adminClient, getMBeanQuery() + ",name=" + name + ",*"));
    }
    
    public static List<SessionManager> create(AdminClient adminClient, Perf perf) throws ConnectorException, JMException {
        List<SessionManager> pools = new ArrayList<SessionManager>();
        
        String query = getMBeanQuery() + ",*";
        Set<ObjectName> mbeans = queryMBeans(adminClient, query);
        
        if(mbeans.isEmpty()) {
        	throw new IllegalArgumentException("No session managers found using query \"" + query + "\", make sure PMI is enabled");
        }
        
        for(ObjectName mbean : mbeans) {
            pools.add(new SessionManager(adminClient, perf, mbean));
        }
        
        return pools;
    }
    
    
    private static String getMBeanQuery() {
        return "WebSphere:type=SessionManager";
    }
    
    private SessionManager(AdminClient adminClient, Perf perf, ObjectName mBean) {
        super(adminClient, perf, mBean);
    }


    public String getName() throws JMException,
            ConnectorException {
        return (String) adminClient.getAttribute(mBean, "name");
    }
    
    public long getLiveCount(Perf perf) throws JMException, ConnectorException {
        return getRangedStats(WSSessionManagementStats.LiveCount).getCurrent();
    }
}
