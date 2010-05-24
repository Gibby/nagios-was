package com.googlecode.nagioswas.services;

import java.util.Set;

import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

public abstract class WASService {

    protected AdminClient adminClient;

    protected ObjectName mBean;
    

    public WASService(AdminClient adminClient, ObjectName mBean) {
        if (mBean == null) {
            throw new IllegalArgumentException("MBean can not be null, make sure PMI is enabled");
        }

        this.adminClient = adminClient;
        this.mBean = mBean;
    }

    @SuppressWarnings("unchecked")
    protected static Set<ObjectName> queryMBeans(AdminClient adminClient,
            String query) throws MalformedObjectNameException,
            ConnectorException {
        ObjectName queryName = new ObjectName(query);
        return adminClient.queryNames(queryName, null);
    }

    protected static ObjectName queryMbean(AdminClient adminClient, String query)
            throws JMException, ConnectorException {
        Set<ObjectName> mbeans = queryMBeans(adminClient, query);
        if (mbeans.size() > 0) {
            return mbeans.iterator().next();
        } else {
            return null;
        }
    }

    public ObjectName getMBean() {
        return mBean;
    }

    public Object getAttribute(ObjectName mbean, String attrName)
            throws JMException, ConnectorException {
        return adminClient.getAttribute(mbean, attrName);
    }

    public void printAttributes(ObjectName on) throws JMException,
            ConnectorException {
        for (MBeanAttributeInfo attr : adminClient.getMBeanInfo(on)
                .getAttributes()) {
            System.out.println(attr.getName() + " -- " + attr.getDescription());
        }
    }


}
