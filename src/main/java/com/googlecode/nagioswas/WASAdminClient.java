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

    public AdminClient create(String profile) throws ConnectorException {
        AdminClient adminClient;
        
        Properties props = new Properties();
        props.setProperty(AdminClient.CONNECTOR_TYPE,
                AdminClient.CONNECTOR_TYPE_SOAP);
        props.setProperty(AdminClient.CONNECTOR_HOST, Config.getString(profile, "hostname"));
        props.setProperty(AdminClient.CONNECTOR_PORT, Config.getString(profile, "port"));
        props.setProperty(AdminClient.CACHE_DISABLED, "false");

        if(Config.getBoolean(profile, "securityenabled")) {
            props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");
            props.setProperty(AdminClient.CONNECTOR_AUTO_ACCEPT_SIGNER, "true");
    
            File sslTrustStore = new File(Config.getString(profile, "truststore"));
            File sslKeyStore = new File(Config.getString(profile, "keystore"));
            
            String sslTrustStorePassword = Config.getString(profile, "truststorepassword");
            String sslKeyStorePassword = Config.getString(profile, "keystorepassword");

            String sslTrustStoreType = getStoreType(profile, "trust", sslTrustStore);
            String sslKeyStoreType = getStoreType(profile, "key", sslKeyStore);
            
            checkStore(sslTrustStore, sslTrustStorePassword,sslTrustStoreType, "Trust");
            checkStore(sslKeyStore, sslKeyStorePassword, sslKeyStoreType, "Key");
    
            props.setProperty("javax.net.ssl.trustStore", sslTrustStore
                    .getAbsolutePath());
            props.setProperty("javax.net.ssl.keyStore", sslKeyStore
                    .getAbsolutePath());
            props.setProperty("javax.net.ssl.trustStorePassword", sslTrustStorePassword);
            props.setProperty("javax.net.ssl.keyStorePassword", sslKeyStorePassword);

            props.setProperty("javax.net.ssl.trustStoreType", sslTrustStoreType);
            props.setProperty("javax.net.ssl.keyStoreType", sslKeyStoreType);

            
            props.setProperty(AdminClient.USERNAME, Config.getString(profile, "username"));
            props.setProperty(AdminClient.PASSWORD, Config.getString(profile, "password"));
        } else {
            props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "false");
        }
        
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
    
    private String getStoreType(String profile, String type, File store) {
        String storeType = Config.getString(profile, type + "storetype");
        
        // if not configured by the user, look at the file ending
        if(storeType == null && store != null) {
            int fileTypeStart = store.getName().lastIndexOf(".");
            
            if(fileTypeStart > 0) {
                String fileType = store.getName().substring(fileTypeStart +1 );

                // map file type into keystore type
                if(fileType.equalsIgnoreCase("p12")) {
                    storeType = "pkcs12";
                } else if(fileType.equalsIgnoreCase("jks")) {
                    storeType = "jks";
                } else {
                    storeType = fileType;
                }
            }
        }
        
        // default to the JVM default type
        if(storeType == null) {
            storeType = KeyStore.getDefaultType();
        }
        
        return storeType;
    }
    
    private void checkStore(File store, String password, String storeType, String type) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(store);
            KeyStore ks = KeyStore.getInstance(storeType);
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
