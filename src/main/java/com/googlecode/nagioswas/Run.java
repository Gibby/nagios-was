package com.googlecode.nagioswas;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.nagioswas.checks.Check;
import com.googlecode.nagioswas.checks.CheckResult;
import com.googlecode.nagioswas.checks.HeapSizeCheck;
import com.googlecode.nagioswas.checks.JDBCConnectionPoolCheck;
import com.googlecode.nagioswas.checks.LiveSessionCheck;
import com.googlecode.nagioswas.checks.ThreadPoolCheck;
import com.googlecode.nagioswas.checks.CheckResult.ResultLevel;
import com.googlecode.nagioswas.services.Perf;
import com.ibm.websphere.management.AdminClient;

public class Run {

    public static void main(String[] args) {
        try {
            Map<String, String> arguments = argsToMap(args);
            
            String profile = arguments.get("-p");
            if(profile == null) {
                throw new RuntimeException("Server (-p) must be specified");
            } else if(Config.getString(profile, "hostname") == null) {
                throw new RuntimeException("Server (-p) unknown, please check check_was.servers");
            }
            String service = arguments.get("-s");
            if(service == null) {
                throw new RuntimeException("Service (-s) must be specified");
            }
            if(!arguments.containsKey("-w")) {
                throw new RuntimeException("Warning level (-w) must be specified");
            }
            if(!arguments.containsKey("-c")) {
                throw new RuntimeException("Critical level (-c) must be specified");
            }
            
            int warning = Integer.parseInt(arguments.get("-w"));
            int critical = Integer.parseInt(arguments.get("-c"));
            
            String name = arguments.get("-n");
            AdminClient client = new WASAdminClient().create(profile);
            Perf perf = Perf.create(client);
    
            Check check = null;
            if(service.equalsIgnoreCase("heapsize")) {
                check = new HeapSizeCheck(client, perf);
            } else if(service.equalsIgnoreCase("threadpool")) {
                check = new ThreadPoolCheck(client, perf);
            } else if(service.equalsIgnoreCase("connectionpool")) {
                check = new JDBCConnectionPoolCheck(client, perf);
            } else if(service.equalsIgnoreCase("sessions")) {
                check = new LiveSessionCheck(client, perf);
            }
            
            if(check != null) {
                CheckResult result = check.check(critical, warning, name);
                System.out.println(result.getLevel() + " - " + result.getMessage());
                
                if(result.getLevel() == ResultLevel.CRITICAL) {
                    System.exit(2);
                } else if(result.getLevel() == ResultLevel.WARNING) {
                    System.exit(1);
                } else if(result.getLevel() == ResultLevel.OK) {
                    System.exit(0);
                }
            } else {
                System.out.println("Unknown service");
                System.exit(3);
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("ERROR - " + e.getMessage());
            System.exit(3);
        }
    }

    private static Map<String, String> argsToMap(String[] args) {
        Map<String, String> map = new HashMap<String, String>();
        
        for(int i = 1; i<args.length; i+=2) {
            map.put(args[i-1], args[i]);
        }
        
        return map;
    }    
}
