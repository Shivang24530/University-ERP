package edu.univ.erp.domain;

// This class holds the calculated statistics for one grade component
public class ClassStatistic {
    private String component;
    private double average;
    private double min;
    private double max;
    private int count; // How many students were graded

    // Constructor
    public ClassStatistic(String component, double average, double min, double max, int count) {
        this.component = component;
        this.average = average;
        this.min = min;
        this.max = max;
        this.count = count;
    }

    // Getters
    public String getComponent() { return component; }
    public double getAverage() { return average; }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public int getCount() { return count; }
}