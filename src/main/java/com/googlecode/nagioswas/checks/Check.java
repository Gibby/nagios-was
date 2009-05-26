package com.googlecode.nagioswas.checks;

import javax.management.JMException;

import com.googlecode.nagioswas.checks.CheckResult.ResultLevel;
import com.googlecode.nagioswas.services.Perf;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;

public abstract class Check {

    protected AdminClient client;
    protected Perf perf; 
    

    public Check(AdminClient client, Perf perf) {
        this.client = client;
        this.perf = perf;
    }
    
    public abstract CheckResult check(int critical, int warning, String name) throws ConnectorException, JMException;
    
    protected double calcRatio(long value, long max) {
        return ((double)value)/max * 100;
    }

    protected boolean isCritical(double ratio, long critical) {
        return ratio >= critical;
    }

    protected boolean isWarning(double ratio, long warning) {
        return ratio >= warning;
    }
    
    protected ResultLevel checkResult(double ratio, int critical, int warning)  {
        if(isCritical(ratio, critical)) {
            return ResultLevel.CRITICAL;
        } else if(isWarning(ratio, warning)) {
            return ResultLevel.WARNING;
        } else {
            return ResultLevel.OK;
        }
    }

    protected String formatRangedMessage(long value, String message) {
        if(message != null) {        
            return message + " " + value;
        } else {
            return Long.toString(value);
        }
    }
    
    private String formatDouble(double d) {
        return (Math.round(d * 100d) / 100d) + "";
    }
    
    protected String formatBoundedMessage(double ratio, long value, long max, String message) {
        StringBuffer sb = new StringBuffer();
        if(message != null) {
            sb.append(message).append(" ");
        }
        sb.append(value).append("/").append(max).append(" (").append(formatDouble(ratio)).append("%)");
        
        return sb.toString();
    }
    
    protected String formatPerfData(double ratio, String uom, long critical, long warning, String perfLabel) {
        StringBuffer sb = new StringBuffer();

        sb.append(perfLabel).append("=");
        sb.append(formatDouble(ratio)).append(uom).append(";");
        sb.append(warning).append(";").append(critical);
        sb.append("; ");
        
        return sb.toString();
    }
    
    protected String escapePerfLabel(String p) {
        return p.replaceAll("[\\s/\\=\\'\\)]+", "").replaceAll("[\\.\\(]+", "_").toLowerCase();
    }
    
}
