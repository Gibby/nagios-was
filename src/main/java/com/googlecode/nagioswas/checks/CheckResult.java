package com.googlecode.nagioswas.checks;

public class CheckResult {

    public enum ResultLevel{ CRITICAL, WARNING, OK};
    
    private ResultLevel level;
    private String message;
    
    public CheckResult(ResultLevel level, String message) {
        this.level = level;
        this.message = message;
    }

    public ResultLevel getLevel() {
        return level;
    }
    
    public String getMessage() {
        return message;
    }
}
