package com.weightscale;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * WeightLogger - Handles logging of weight readings to file and console
 */
public class WeightLogger {

    private String logFilePath;
    private String csvFilePath;
    private List<WeightData> sessionData;
    private boolean consoleOutput;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public WeightLogger(boolean consoleOutput) {
        this.consoleOutput = consoleOutput;
        this.sessionData = new ArrayList<>();

        String timestamp = SDF.format(new Date());
        this.logFilePath = "weight_log_" + timestamp + ".txt";
        this.csvFilePath = "weight_data_" + timestamp + ".csv";

        // Write CSV header
        writeCsvHeader();
    }

    private void writeCsvHeader() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFilePath, true))) {
            pw.println("ID,Value,Unit,RawData,Timestamp");
        } catch (IOException e) {
            System.err.println("Warning: Could not create CSV file: " + e.getMessage());
        }
    }

    /**
     * Log a new weight reading
     */
    public void log(WeightData data) {
        sessionData.add(data);

        // Console output
        if (consoleOutput) {
            System.out.println(data);
        }

        // Append to log file
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFilePath, true))) {
            pw.println(data.toString());
        } catch (IOException e) {
            System.err.println("Log file error: " + e.getMessage());
        }

        // Append to CSV
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFilePath, true))) {
            pw.println(data.toCsvRow());
        } catch (IOException e) {
            System.err.println("CSV file error: " + e.getMessage());
        }
    }

    /**
     * Print session statistics
     */
    public void printStats() {
        if (sessionData.isEmpty()) {
            System.out.println("No data recorded.");
            return;
        }

        DoubleSummaryStatistics stats = sessionData.stream()
                .mapToDouble(WeightData::getValue)
                .summaryStatistics();

        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║       SESSION STATISTICS          ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.printf("║  Total Readings : %-15d║%n", stats.getCount());
        System.out.printf("║  Min Weight     : %-12.3f kg ║%n", stats.getMin());
        System.out.printf("║  Max Weight     : %-12.3f kg ║%n", stats.getMax());
        System.out.printf("║  Avg Weight     : %-12.3f kg ║%n", stats.getAverage());
        System.out.println("╠══════════════════════════════════╣");
        System.out.printf("║  Log File  : %-21s║%n", logFilePath);
        System.out.printf("║  CSV File  : %-21s║%n", csvFilePath);
        System.out.println("╚══════════════════════════════════╝");
    }

    public List<WeightData> getSessionData() {
        return Collections.unmodifiableList(sessionData);
    }

    public int getReadingCount() {
        return sessionData.size();
    }
}
