JDBC connection pool usage can be monitored for the entire server (all connection pools) or with a named connection pool. It is measured as:
percent used/maximum configured

To monitor, use the following plugin invocation:
`check_was -s connectionpool -w 80 -c 90 -p <server name> -n <connection pool name>`

-w sets the threshold percent used for issuing warnings
-c sets the threshold percent used for issuing critical issues
-p sets the server name in check\_was.servers to be used
-n sets the name of the connection pool to be monitored. Optional, if not provided all connection pools will be checked.