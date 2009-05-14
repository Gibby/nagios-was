package com.googlecode.nagioswas.checks;

import javax.management.JMException;

import com.googlecode.nagioswas.checks.CheckResult.ResultLevel;
import com.googlecode.nagioswas.services.JavaVirtualMachine;
import com.googlecode.nagioswas.services.Perf;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

public class HeapSizeCheck extends Check {
    
    public HeapSizeCheck(AdminClient client, Perf perf) {
        super(client, perf);
    }

    public CheckResult check(int critical, int warning, String name) throws ConnectorException, JMException {
        JavaVirtualMachine jvm = JavaVirtualMachine.create(client, perf);
        long heapsize = jvm.getHeapSize();
        long heapsizeMax = jvm.getHeapSizeMax();
        
        long ratio = calcRatio(heapsize, heapsizeMax);
        
        ResultLevel level = checkResult(ratio, critical, warning);
        
        return new CheckResult(level, "heapsize: " + formatBoundedMessage(ratio, heapsize, heapsizeMax, null) );
    }
    

}
