This page contains common questions, especially around problems in specific environments or with certain WAS versions

### java.lang.NoClassDefFoundError: org.osgi.service.url.AbstractURLStreamHandlerService ###
check\_was failes to start with java.lang.NoClassDefFoundError: org.osgi.service.url.AbstractURLStreamHandlerService in the stacktrace. This happens on WAS 6.1.0.11 and 6.1.0.13 due to a bug in WAS. A fix is [available from IBM](http://www-01.ibm.com/support/docview.wss?uid=swg24017327).