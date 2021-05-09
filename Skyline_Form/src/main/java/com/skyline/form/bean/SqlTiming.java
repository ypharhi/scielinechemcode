package com.skyline.form.bean;

public class SqlTiming {
	 
    private final String statement;
    private int count;
    private long cumulativeMillis;
 
    public SqlTiming(String statement) {
        this.statement = statement;
    }
 
    public synchronized SqlTiming recordTiming(long time) {
        count++;
        cumulativeMillis += time;
        return this;
    }
 
    public String getSqlStatement() {
        return statement;
    }
 
    public int getExecutionCount() {
        return count;
    }
 
    public long getCumulativeExecutionTime() {
        return cumulativeMillis;
    }
 
    public float getAvgExecutionTime() {
        return (float)cumulativeMillis / (float)count;
    }
}