JVM heapsize is provided for the entire server. It is measured as:
percent used/maximum configured

To monitor, use the following plugin invocation:
`check_was -s heapsize -w 80 -c 90 -p <server name>`

-w sets the threshold percent used for issuing warnings
-c sets the threshold percent used for issuing critical issues
-p sets the server name in check\_was.servers to be used