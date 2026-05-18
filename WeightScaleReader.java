package com.weightscale;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * WeightScaleReader - Serial Communication Handler
 * Reads weight data from a physical scale at 9600 baud rate
 * Supports prefix/suffix parsing and continuous reading mode
 */
public class WeightScaleReader {

    // Serial port configuration
    public static final int BAUD_RATE = 9600;
    public static final int DATA_BITS = 8;
    public static final int STOP_BITS = 1;
    public static final String PARITY = "NONE";

    private String portName;
    private boolean isContinuous;
    private String prefix;
    private String suffix;
    private boolean isRunning = false;

    private Consumer<WeightData> onWeightReceived;
    private Consumer<String> onError;

    // For simulation/testing when no physical scale is connected
    private boolean simulationMode = false;
    private ScheduledExecutorService scheduler;

    public WeightScaleReader(String portName, boolean isContinuous, String prefix, String suffix) {
        this.portName = portName;
        this.isContinuous = isContinuous;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public void setOnWeightReceived(Consumer<WeightData> callback) {
        this.onWeightReceived = callback;
    }

    public void setOnError(Consumer<String> callback) {
        this.onError = callback;
    }

    /**
     * Start reading from the serial port
     * Falls back to simulation mode if port not available
     */
    public void startReading() {
        isRunning = true;
        System.out.println("=== Weight Scale Reader Started ===");
        System.out.println("Port     : " + portName);
        System.out.println("Baud Rate: " + BAUD_RATE);
        System.out.println("Mode     : " + (isContinuous ? "Continuous" : "Single Read"));
        System.out.println("Prefix   : '" + prefix + "'");
        System.out.println("Suffix   : '" + suffix + "'");
        System.out.println("===================================");

        // Try real serial port first, fallback to simulation
        try {
            startSerialReading();
        } catch (Exception e) {
            System.out.println("[INFO] Physical port not found. Starting SIMULATION mode...");
            simulationMode = true;
            startSimulation();
        }
    }

    /**
     * Real serial port reading using JSSC / RXTX library
     * This uses raw streams - replace with jssc.SerialPort for production
     */
    private void startSerialReading() throws Exception {
        // NOTE: In production, use JSSC library:
        // jssc.SerialPort serialPort = new jssc.SerialPort(portName);
        // serialPort.openPort();
        // serialPort.setParams(BAUD_RATE, DATA_BITS, STOP_BITS, 0);
        //
        // For now, throw to trigger simulation
        throw new Exception("JSSC library not loaded - use simulation");
    }

    /**
     * Simulation mode - generates realistic weight data for testing
     */
    private void startSimulation() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        Random random = new Random();
        final double[] baseWeight = {50.0};
        final int[] readCount = {0};

        Runnable readTask = () -> {
            if (!isRunning) {
                scheduler.shutdown();
                return;
            }

            // Simulate realistic weight fluctuation
            double fluctuation = (random.nextDouble() - 0.5) * 0.5;
            baseWeight[0] = Math.max(0, baseWeight[0] + fluctuation);
            double weight = Math.round(baseWeight[0] * 100.0) / 100.0;

            // Build raw serial string with prefix/suffix
            String rawData = prefix + String.format("%07.3f", weight) + "kg" + suffix;
            readCount[0]++;

            // Parse and dispatch
            WeightData data = parseWeightData(rawData, readCount[0]);
            if (data != null && onWeightReceived != null) {
                onWeightReceived.accept(data);
            }

            // If not continuous, stop after one read
            if (!isContinuous) {
                isRunning = false;
                scheduler.shutdown();
            }
        };

        if (isContinuous) {
            scheduler.scheduleAtFixedRate(readTask, 0, 500, TimeUnit.MILLISECONDS);
        } else {
            scheduler.schedule(readTask, 100, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Parse raw serial string → WeightData object
     * Strips prefix/suffix and extracts numeric weight value
     */
    public WeightData parseWeightData(String rawData, int readId) {
        try {
            String cleaned = rawData.trim();

            // Strip prefix
            if (!prefix.isEmpty() && cleaned.startsWith(prefix)) {
                cleaned = cleaned.substring(prefix.length());
            }

            // Strip suffix
            if (!suffix.isEmpty() && cleaned.endsWith(suffix)) {
                cleaned = cleaned.substring(0, cleaned.length() - suffix.length());
            }

            // Extract unit (kg, g, lb, oz)
            String unit = "kg";
            for (String u : new String[]{"kg", "lb", "oz", "g"}) {
                if (cleaned.toLowerCase().contains(u)) {
                    unit = u;
                    cleaned = cleaned.toLowerCase().replace(u, "").trim();
                    break;
                }
            }

            double value = Double.parseDouble(cleaned.trim());
            return new WeightData(readId, value, unit, rawData, new Date());

        } catch (NumberFormatException e) {
            if (onError != null) onError.accept("Parse error for: " + rawData);
            return null;
        }
    }

    public void stopReading() {
        isRunning = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        System.out.println("[INFO] Weight Scale Reader stopped.");
    }

    public boolean isSimulationMode() {
        return simulationMode;
    }
}
