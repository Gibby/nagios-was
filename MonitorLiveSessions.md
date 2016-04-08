Live session usage can be monitored for the entire server (all hosts) or with a named host. It is measured as:
Number of live sessions

To monitor, use the following plugin invocation:
`check_was -s sessions -w 200 -c 400 -p <server name> -n <host name>`

-w sets the threshold number used for issuing warnings
-c sets the threshold number used for issuing critical issues
-p sets the server name in check\_was.servers to be used
-n sets the name of the host to be monitored. Optional, if not provided all host will be checked.