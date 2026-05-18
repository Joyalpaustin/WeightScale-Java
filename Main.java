package com.weightscale;

import java.util.Scanner;

/**
 * ============================================================
 *   WEIGHT SCALE INTERFACE - Java Application
 *   Serial Communication | 9600 Baud | Prefix/Suffix Parsing
 * ============================================================
 *
 *  Author  : Joyal
 *  Version : 1.0.0
 *  Date    : 2026
 *
 *  Description:
 *    Connects to a weighing scale via serial port at 9600 baud.
 *    Receives continuous weight readings, parses prefix/suffix,
 *    displays real-time values, logs to file, and exports CSV.
 * ============================================================
 */
public class Main {

    // ─── Default Configuration ───────────────────────────────
    private static final String DEFAULT_PORT      = "COM3";     // Change to /dev/ttyUSB0 for Linux
    private static final boolean CONTINUOUS_MODE  = true;
    private static final String DEFAULT_PREFIX    = "ST,GS,+";  // Example: scale prefix
    private static final String DEFAULT_SUFFIX    = "\r\n";     // Carriage return + newline

    public static void main(String[] args) throws InterruptedException {

        printBanner();

        // ─── Parse CLI arguments or use defaults ──────────────
        String port     = args.length > 0 ? args[0] : DEFAULT_PORT;
        boolean continu = args.length > 1 ? args[1].equalsIgnoreCase("true") : CONTINUOUS_MODE;
        String prefix   = args.length > 2 ? args[2] : DEFAULT_PREFIX;
        String suffix   = args.length > 3 ? args[3] : DEFAULT_SUFFIX;

        // ─── Initialize components ────────────────────────────
        WeightLogger logger = new WeightLogger(true);
        WeightScaleReader reader = new WeightScaleReader(port, continu, prefix, suffix);

        // ─── Wire up callbacks ────────────────────────────────
        reader.setOnWeightReceived(data -> {
            logger.log(data);
            // Optional: Alert if weight exceeds threshold
            if (data.getValue() > 100.0) {
                System.out.println("  ⚠  ALERT: Weight exceeds 100kg limit!");
            }
        });

        reader.setOnError(err -> {
            System.err.println("[ERROR] " + err);
        });

        // ─── Start reading ────────────────────────────────────
        reader.startReading();

        if (reader.isSimulationMode()) {
            System.out.println("\n[SIMULATION] Press ENTER to stop reading...\n");
        } else {
            System.out.println("\n[LIVE] Reading from " + port + " | Press ENTER to stop...\n");
        }

        // ─── Wait for user to press Enter ─────────────────────
        new Scanner(System.in).nextLine();

        // ─── Stop and print stats ─────────────────────────────
        reader.stopReading();
        Thread.sleep(200); // Let any pending reads complete
        logger.printStats();

        System.out.println("\nThank you for using Weight Scale Interface!");
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════╗");
        System.out.println("  ║      WEIGHT SCALE INTERFACE  v1.0.0          ║");
        System.out.println("  ║      Serial: 9600 Baud | Java Edition        ║");
        System.out.println("  ╠══════════════════════════════════════════════╣");
        System.out.println("  ║  Modes  : Continuous / Single Read           ║");
        System.out.println("  ║  Format : Prefix + Weight + Unit + Suffix    ║");
        System.out.println("  ║  Output : Console + Log File + CSV Export    ║");
        System.out.println("  ╚══════════════════════════════════════════════╝");
        System.out.println();
    }
}
