package com.weightscale;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * WeightData - Data model representing a single weight reading
 */
public class WeightData {

    private int id;
    private double value;
    private String unit;
    private String rawData;
    private Date timestamp;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public WeightData(int id, double value, String unit, String rawData, Date timestamp) {
        this.id = id;
        this.value = value;
        this.unit = unit;
        this.rawData = rawData;
        this.timestamp = timestamp;
    }

    // Getters
    public int getId() { return id; }
    public double getValue() { return value; }
    public String getUnit() { return unit; }
    public String getRawData() { return rawData; }
    public Date getTimestamp() { return timestamp; }

    /**
     * Convert weight to another unit
     */
    public double convertTo(String targetUnit) {
        double valueInKg;
        // First convert to kg
        switch (unit.toLowerCase()) {
            case "kg": valueInKg = value; break;
            case "g":  valueInKg = value / 1000; break;
            case "lb": valueInKg = value * 0.453592; break;
            case "oz": valueInKg = value * 0.0283495; break;
            default:   valueInKg = value;
        }
        // Then convert to target
        switch (targetUnit.toLowerCase()) {
            case "kg": return Math.round(valueInKg * 1000.0) / 1000.0;
            case "g":  return Math.round(valueInKg * 1000 * 100.0) / 100.0;
            case "lb": return Math.round((valueInKg / 0.453592) * 1000.0) / 1000.0;
            case "oz": return Math.round((valueInKg / 0.0283495) * 100.0) / 100.0;
            default:   return value;
        }
    }

    public String getFormattedValue() {
        return String.format("%.3f %s", value, unit);
    }

    public String getTimestampStr() {
        return SDF.format(timestamp);
    }

    @Override
    public String toString() {
        return String.format("[#%04d] %s | Raw: %-25s | Time: %s",
                id, getFormattedValue(), rawData, getTimestampStr());
    }

    /**
     * Export as CSV row
     */
    public String toCsvRow() {
        return String.format("%d,%.3f,%s,%s,%s",
                id, value, unit, rawData.replace(",", ";"), getTimestampStr());
    }
}
