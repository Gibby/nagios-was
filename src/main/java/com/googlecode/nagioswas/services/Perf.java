package com.googlecode.nagioswas.services;

import javax.management.JMException;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

public class Perf extends WASService {

    public static Perf create(AdminClient adminClient) throws ConnectorException, JMException {
        return new Perf(adminClient, queryMbean(adminClient, "WebSphere:type=Perf,*"));
    }
    
    public Perf(AdminClient adminClient, ObjectName mBean) {
        super(adminClient, mBean);
    }
}
