package com.googlecode.nagioswas;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Security;
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
            try {
                adminClient = AdminClientFactory.createAdminClient(props);
                return adminClient;
            } catch(ConnectorException e) {
                System.err.println("Could not connect to a SSL SOAP port, trying to set socket factories");
                // huge hack! Try setting the SSL provider
                Security.setProperty("ssl.SocketFactory.provider", "com.ibm.jsse2.SSLSocketFactoryImpl");
                Security.setProperty("ssl.ServerSocketFactory.provider", "com.ibm.jsse2.SSLServerSocketFactoryImpl");
                
                adminClient = AdminClientFactory.createAdminClient(props);
                return adminClient;                
            }
        }

        File sslTrustStore = new File(Config.getString(profile, "truststore"));
        File sslKeyStore = new File(Config.getString(profile, "keystore"));

        String sslTrustStorePassword = Config.getString(profile, "truststorepassword");
        String sslKeyStorePassword = Config.getString(profile, "keystorepassword");
        
        if(Config.getBoolean(profile, "securityenabled")) {
            props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");
            props.setProperty(AdminClient.CONNECTOR_AUTO_ACCEPT_SIGNER, "true");
    
            checkStore(sslTrustStore, sslTrustStorePassword, "Trust");
            checkStore(sslKeyStore, sslKeyStorePassword, "Key");
    
            props.setProperty("javax.net.ssl.trustStore", sslTrustStore
                    .getAbsolutePath());
            props.setProperty("javax.net.ssl.keyStore", sslKeyStore
                    .getAbsolutePath());
            props.setProperty("javax.net.ssl.trustStorePassword", sslTrustStorePassword);
            props.setProperty("javax.net.ssl.keyStorePassword", sslKeyStorePassword);
    
            props.setProperty(AdminClient.USERNAME, Config.getString(profile, "username"));
            props.setProperty(AdminClient.PASSWORD, Config.getString(profile, "password"));
        } else {
            props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "false");
        }
        
        adminClient = AdminClientFactory.createAdminClient(props);

        return adminClient;
    }
    
    private void checkStore(File store, String password, String type) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(store);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(fis, password.toCharArray());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(type + "store could not be opened, check configuration: "
                    + store.getAbsolutePath(), e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(type + "store does not exists: "
                    + store.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(type + "store could not be opened, check configuration: "
                    + store.getAbsolutePath(), e);
        } finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException ignored) {
                    ;
                }
            }
        }
    }
}
