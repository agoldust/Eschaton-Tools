# 🛡️ Eschaton Tools

**Eschaton Tools** is an off-grid survival, emergency response, and peer-to-peer tactical navigation application designed for Android. Engineered to operate entirely offline without relying on cellular towers, internet access, or cloud infrastructure, Eschaton Tools provides vital tools for survival in disaster, wilderness, or grid-down scenarios.

---

## 🎯 Mission Statement

To empower individuals, emergency responders, and survivalists with reliable, battery-efficient, and zero-dependency mobile tools that ensure communication, navigation, emergency signaling, and life-saving first-aid guidance when all external infrastructure fails.

---

## ⚡ Core Features

### 1. 🧭 Offline Compass & Navigation
- **Digital Compass & Bearing**: Real-time compass heading utilizing hardware magnetic & accelerometer sensors with low latency filtering.
- **Magnetic Declination Compensation**: Automatic and manual declination offset computation for true north alignment.
- **Offline Waypoints & Coordinates**: Save, edit, and navigate to custom GPS waypoints (lat/long, altitude, accuracy) stored locally on device.
- **Tactical Grid**: High-contrast cardinal readout, pitch/roll indicators, and target bearing orientation.

### 2. 🚨 Emergency Beacon & Flashlight SOS
- **Morse Code SOS Strobe**: High-intensity flashlight pulsing according to international S.O.S. Morse code standards (`... --- ...`).
- **Custom Strobe Modes**: Adjustable flash frequencies (1 Hz - 20 Hz) for signal visibility over long distances.
- **Screen Beacon**: High-contrast pulsing emergency screen beacon with customizable color modes (Emergency Red, Signal Amber, High-Visibility White).
- **Loud Acoustic Alarm**: High-decibel emergency audio alarm designed to assist search-and-rescue teams.

### 3. 🩺 Survival & First Aid Guide
- **Offline Knowledge Base**: Comprehensive, instant-search emergency medical, wilderness survival, and disaster preparedness manuals.
- **Life-Saving Procedures**: Step-by-step interactive workflows for CPR, severe hemorrhage control (tourniquet application), burn triage, hypothermia treatment, and fracture immobilization.
- **Wilderness Survival Modules**: Water purification techniques, emergency shelter construction, fire crafting, and edible/toxic flora identification.

### 4. 📻 P2P Mesh & Direct Messaging
- **Infrastructure-Free Communication**: Direct peer-to-peer messaging using Wi-Fi Direct and Bluetooth Low Energy (BLE).
- **Emergency Signal Broadcast**: Broadcast emergency alert messages (S.O.S., location tags, status updates) to nearby devices.
- **Offline Mesh Chat**: Local conversation channels with message queuing and local database persistence.

---

## 🏗️ Technical Architecture

Eschaton Tools is built following modern Android architectural standards and high-reliability software practices:

- **UI Framework**: **Jetpack Compose** with **Material Design 3 (M3)** expressive survival/tactical styling.
- **Navigation**: State-driven navigation leveraging **Jetpack Navigation 3**.
- **Architecture Pattern**: Clean Architecture with **MVVM** (Model-View-ViewModel) and **Repository Pattern**.
- **Local Persistence**: **Room Database** for waypoints, survival guides, and offline P2P message history; **DataStore Preferences** for app state.
- **Asynchronous Execution**: Kotlin **Coroutines** and **StateFlow** / **SharedFlow** for non-blocking UI and hardware sensor streams.
- **Hardware Interop**: Android Sensor Manager API, Android Camera2 API for flash control, Android Location Services, Bluetooth LE, and Wi-Fi P2P framework.
- **Runtime Permissions**: Accompanist Permissions integration with graceful fallback UI.

---

## 📵 Offline Operation Guidelines

1. **Zero External Network Dependencies**: All features, guides, databases, and calculations execute strictly on-device. No API calls or telemetry are transmitted.
2. **Hardware Fallback**:
   - If GPS location is unavailable or disabled, the compass operates seamlessly using hardware magnetometer and accelerometer sensors.
   - If camera flash is missing on the device, screen strobe and acoustic alarm fall back automatically.
3. **Power Management**:
   - Dark OLED background (`#0B0F10`) maximizes battery conservation during prolonged grid outages.
   - Sensor listeners and camera flash tasks are automatically paused when the screen is turned off or app enters background (unless explicit active emergency beacon mode is armed).

---

## 🔒 Permission Requirements & Justifications

| Permission | Category | Justification / Usage |
|---|---|---|
| `android.permission.CAMERA` | Hardware / Optical | Required to control the camera flash hardware for high-intensity SOS Morse code and strobe beacons. |
| `android.permission.FLASHLIGHT` | Hardware / Light | Direct access to device flashlight hardware. |
| `android.permission.ACCESS_FINE_LOCATION` | Location | Access precise GPS coordinates for compass bearing calculations, waypoint recording, and emergency location tags. |
| `android.permission.ACCESS_COARSE_LOCATION` | Location | Fallback network/cell location when fine GPS is acquiring lock. |
| `android.permission.BLUETOOTH_SCAN` | P2P / Nearby | Discover nearby P2P mesh peers via Bluetooth Low Energy (BLE) scanning (SDK 31+). |
| `android.permission.BLUETOOTH_CONNECT` | P2P / Nearby | Establish P2P connections to neighboring devices for messaging (SDK 31+). |
| `android.permission.BLUETOOTH_ADVERTISE` | P2P / Nearby | Broadcast emergency P2P mesh presence to neighboring devices (SDK 31+). |
| `android.permission.ACCESS_WIFI_STATE` | Network / Wi-Fi | Inspect Wi-Fi state for Wi-Fi Direct P2P mesh networking. |
| `android.permission.CHANGE_WIFI_STATE` | Network / Wi-Fi | Initialize and manage Wi-Fi Direct peer connections. |
| `android.permission.NEARBY_WIFI_DEVICES` | P2P / Nearby | Discover and connect to nearby Wi-Fi Direct peer nodes without internet access (SDK 33+). |
| `android.permission.VIBRATE` | Haptics | Provide tactile haptic feedback during Morse code transmissions and tactical UI interactions. |

---

## 🛠️ Build & Installation

### Prerequisites
- **Android Studio**: Ladybug / 2024.2.1 or newer (AGP 9.x compatible)
- **JDK**: Java 17 or Java 21
- **Target SDK**: 37 | **Min SDK**: 24 (Android 7.0+)

### Building from CLI
```bash
# Clone the repository
git clone https://github.com/agoldust/Eschaton-Tools.git
cd Eschaton-Tools

# Assemble Debug APK
./gradlew assembleDebug

# Run Unit Tests
./gradlew testDebugUnitTest

# Install & launch on a connected device/emulator
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell monkey -p de.goldoust.eschatontools -c android.intent.category.LAUNCHER 1
```
