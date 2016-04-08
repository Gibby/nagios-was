This page described the initial configuration of the plugin. You will probably need to go through this list before getting the plugin running the first time.

  1. Place check\_was, `check_was-<version>.jar` and check\_was.profiles in the same directory (e.g. /opt/plugins/custom). This can be on any computer with TCP access to the server running WAS, or on the WAS server itself.
  1. Make sure check\_was is executable by your Nagios user.
  1. Update check\_was by setting the environment variables at the start of the script to the appropriate values for your server. JAVA\_HOME must point to an IBM JRE/JDK. WAS\_HOME needs to point to a directory that contains a directory named "runtimes" containing the following WAS libraries: `com.ibm.ws.admin.client_<version>.jar` and `com.ibm.ws.webservices.thinclient_<version>.jar`. If you run the plugin on the same server as WAS, WAS\_HOME should point to the WAS install directory.
  1. Edit check\_was.servers. This file should contain the configuration to connect to your WAS server. For each server, the following properties should be provided:
  1. <server alias>.hostname=<the hostname or IP of the WAS server>
  1. <server alias>.port=<the port of the SOAP connector on the server, e.g. 8880>
  1. <server alias>.username=<the admin user name>
  1. <server alias>.password=<the admin password>
  1. <server alias>.securityenabled=<true if security is enabled, false otherwise>
  1. <server alias>.truststore=<the path to the keystore containing the certificated to be used for SSL. If you are running the plugin on your WAS server and use the default WAS keystores, this should point to `etc/DummyClientTrustFile.jks` in your profile>
  1. <server alias>.truststorepassword=<the password for the trust store>
  1. <server alias>.keystore=<the path to the keystore containing the private key to be used for SSL. If you are running the plugin on your WAS server and use the default WAS keystores, this should point to `etc/DummyClientKeyFile.jks` in your profile>
  1. <server alias>.keystorepassword=<the password for the key store>
  1. You're done.