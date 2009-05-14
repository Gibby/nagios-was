package com.googlecode.nagioswas;


import java.io.File;
import java.util.Properties;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.exception.ConnectorException;

public class WASAdminClient {
    private boolean connector_security_enabled = false;

    public AdminClient create(String profile) throws ConnectorException {
        AdminClient adminClient;
        
        Properties props = new Properties();
        props.setProperty(AdminClient.CONNECTOR_TYPE,
                AdminClient.CONNECTOR_TYPE_SOAP);
        props.setProperty(AdminClient.CONNECTOR_HOST, Config.getString(profile, "hostname"));
        props.setProperty(AdminClient.CONNECTOR_PORT, Config.getString(profile, "port"));
        props.setProperty(AdminClient.CACHE_DISABLED, "false");

        if (connector_security_enabled) {
            adminClient = AdminClientFactory.createAdminClient(props);
            return adminClient;
        }

        File sslTrustStore = new File(Config.getString(profile, "truststore"));
        File sslKeyStore = new File(Config.getString(profile, "keystore"));

        if(Config.getBoolean(profile, "securityenabled")) {
            props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");
            props.setProperty(AdminClient.CONNECTOR_AUTO_ACCEPT_SIGNER, "true");
    
            if (!sslTrustStore.exists()) {
                throw new RuntimeException("Truststore does not exists: "
                        + sslTrustStore);
            }
            if (!sslKeyStore.exists()) {
                throw new RuntimeException("Keystore does not exists: "
                        + sslKeyStore);
            }
    
            props.setProperty("javax.net.ssl.trustStore", sslTrustStore
                    .getAbsolutePath());
            props.setProperty("javax.net.ssl.keyStore", sslKeyStore
                    .getAbsolutePath());
            props.setProperty("javax.net.ssl.trustStorePassword", Config.getString(profile, "truststorepassword"));
            props.setProperty("javax.net.ssl.keyStorePassword", Config.getString(profile, "keystorepassword"));
    
            props.setProperty(AdminClient.USERNAME, Config.getString(profile, "username"));
            props.setProperty(AdminClient.PASSWORD, Config.getString(profile, "password"));
        } else {
            props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "false");
        }
        
        adminClient = AdminClientFactory.createAdminClient(props);

        return adminClient;
    }
}
