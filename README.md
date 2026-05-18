# ⚖️ Weight Scale Interface - Java

A Java application that reads weight data from a physical weighing scale via **serial communication at 9600 baud rate**, parses prefix/suffix formats, and supports both **continuous and single-read modes**.

---

## 📁 Project Structure

```
WeightScaleProject/
├── src/
│   └── com/weightscale/
│       ├── Main.java             ← Entry point
│       ├── WeightScaleReader.java ← Serial port communication
│       ├── WeightData.java       ← Data model
│       └── WeightLogger.java     ← File logging & CSV export
├── lib/
│   └── jssc-2.9.4.jar           ← Serial port library (download separately)
└── README.md
```

---

## ⚙️ Serial Communication Settings

| Parameter  | Value     |
|------------|-----------|
| Baud Rate  | **9600**  |
| Data Bits  | 8         |
| Stop Bits  | 1         |
| Parity     | None      |
| Mode       | Continuous / Single |

---

## 🚀 How to Run

### Step 1: Download JSSC Library
Download from: https://github.com/java-native/jssc/releases
Place `jssc-2.9.4.jar` inside the `lib/` folder.

### Step 2: Compile
```bash
javac -cp lib/jssc-2.9.4.jar -d out src/com/weightscale/*.java
```

### Step 3: Run
```bash
# Windows (COM port)
java -cp out;lib/jssc-2.9.4.jar com.weightscale.Main COM3 true "ST,GS,+" "\r\n"

# Linux/Mac (USB port)
java -cp out:lib/jssc-2.9.4.jar com.weightscale.Main /dev/ttyUSB0 true "ST,GS,+" "\r\n"
```

### CLI Arguments (all optional)
```
java com.weightscale.Main [PORT] [CONTINUOUS] [PREFIX] [SUFFIX]

  PORT       : Serial port name (default: COM3)
  CONTINUOUS : true/false (default: true)
  PREFIX     : Data prefix string (default: "ST,GS,+")
  SUFFIX     : Data suffix string (default: "\r\n")
```

---

## 📊 Output

### Console
```
[#0001] 050.234 kg | Raw: ST,GS,+0050.234kg\r\n | Time: 2026-05-18 10:30:01.234
[#0002] 050.198 kg | Raw: ST,GS,+0050.198kg\r\n | Time: 2026-05-18 10:30:01.734
```

### Files Generated
- `weight_log_YYYYMMDD_HHmmss.txt` — Full log
- `weight_data_YYYYMMDD_HHmmss.csv` — CSV export

### Session Statistics
```
╔══════════════════════════════════╗
║       SESSION STATISTICS          ║
╠══════════════════════════════════╣
║  Total Readings : 120            ║
║  Min Weight     : 49.823 kg      ║
║  Max Weight     : 50.614 kg      ║
║  Avg Weight     : 50.201 kg      ║
╚══════════════════════════════════╝
```

---

## 🔌 Simulation Mode

If no physical scale is connected, the app automatically runs in **simulation mode** — generating realistic weight data for testing without any hardware.

---

## 📦 Data Format

The scale sends data in this format:
```
[PREFIX][WEIGHT_VALUE][UNIT][SUFFIX]
Example: ST,GS,+0050.234kg\r\n
```

The parser automatically:
1. Strips the prefix
2. Strips the suffix
3. Extracts numeric value
4. Detects unit (kg, g, lb, oz)
5. Supports unit conversion

---

## 🧰 Technology Used

| Component | Technology |
|-----------|-----------|
| Language  | Java 8+   |
| Serial Comm | JSSC 2.9.4 |
| Logging   | File I/O (java.io) |
| Scheduling | ScheduledExecutorService |
| Data Export | CSV format |

---

## ✅ Features

- [x] 9600 baud serial communication
- [x] Continuous & single-read modes
- [x] Prefix / Suffix parsing
- [x] Real-time console display
- [x] File logging (TXT + CSV)
- [x] Session statistics
- [x] Unit conversion (kg/g/lb/oz)
- [x] Overload alert (>100kg)
- [x] Auto simulation mode (no hardware needed)
- [x] CLI configurable

---

*Built with Java | Weight Scale Interface v1.0.0*
