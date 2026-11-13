# 🛡️ Eschaton Tools

[![Android Build](https://img.shields.io/badge/Android-SDK%2037-00E676?style=for-the-badge&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-FFB300?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-4285F4?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Languages](https://img.shields.io/badge/Languages-EN%20%7C%20DE%20%7C%20FA-FF3B30?style=for-the-badge)](#-languages--زبان‌ها)

**Eschaton Tools** is an off-grid survival, emergency response, and peer-to-peer tactical navigation application designed for Android. Engineered to operate entirely offline without relying on cellular towers, internet access, or cloud infrastructure, Eschaton Tools provides vital capabilities for survival in disaster, wilderness, or grid-down scenarios.

---

## 🌐 Languages / زبان‌ها

- [🇬🇧 English Documentation](#-english-version)
- [🇩🇪 Deutsche Dokumentation](#-deutsche-version)
- [🇮🇷 مستندات فارسی](#-نسخه-فارسی)

---

## 🇬🇧 English Version

### 🎯 Mission Statement
To empower individuals, emergency responders, and survivalists with reliable, battery-efficient, and zero-dependency mobile tools that ensure communication, navigation, emergency signaling, and life-saving first-aid guidance when all external infrastructure fails.

### ⚡ Core Features (9 Tactical Modules)

#### 🎛️ 1. Tactical Command Dashboard (Main Launch Screen)
- **High-Speed Action Cards**: Quick 1-tap access to all 9 tactical modules via large 60dp icon badges.
- **Dynamic Language Switcher**: Toggle seamlessly between **English**, **German**, and **Farsi** in the top bar.
- **Full RTL Support**: Automatic layout direction switching for Right-to-Left languages (Farsi).

#### 🧭 2. Offline Solar Compass & Navigation
- **3D Digital Compass**: Real-time heading utilizing hardware magnetic & accelerometer sensors.
- **True & Magnetic North**: Real-time declination computation.
- **Pitch & Roll Leveling**: Live tactical pitch and roll indicators for orientation calibration.
- **Offline Waypoints**: Save, edit, and navigate to custom GPS waypoints (lat/long, altitude, distance, bearing).

#### 🚨 3. Emergency Beacon & Flashlight SOS
- **Morse Code SOS Strobe**: High-intensity flashlight pulsing according to international S.O.S. Morse code standards (`... --- ...`).
- **High-Frequency Strobe**: Adjustable flash frequencies (1 Hz - 20 Hz) for long-distance optical signals.
- **Acoustic Rescue Siren**: High-decibel audio siren synthesizer (Wail, Hi-Lo, Audio Morse, Horn).
- **Text-to-Morse Transmitter**: Convert custom emergency text messages into optical & audio Morse loops.
- **Screen SOS Flasher**: Full-screen high-contrast visual signaling overlay.

#### 🩺 4. Survival & First Aid Guide
- **100% Offline Field Manual**: Emergency medical, wilderness survival, and disaster preparedness guide.
- **Life-Saving Procedures**: Step-by-step interactive workflows for CPR, severe hemorrhage control (tourniquet application), fracture immobilization, hypothermia, and heatstroke.
- **Wilderness Modules**: Water purification, emergency shelter construction, fire crafting, foraging safety, and signal fires.

#### 📡 5. P2P Mesh & Direct Messaging
- **Infrastructure-Free Communication**: Direct peer-to-peer messaging using Wi-Fi Direct and Bluetooth Low Energy (BLE) Mesh.
- **1-Tap Emergency SOS Broadcast**: Broadcast GPS location + distress status payload instantly to all nearby nodes.
- **Offline Mesh Chat**: Encrypted local message feeds.

#### 🗺️ 6. Tactical Radar Map & HUD Grid
- **Game-Style HUD Radar**: Real-time Canvas radar display with concentric distance range rings (50m, 100m, 250m, 500m, 1km, 5km).
- **Live Marker Overlays**: Displays active saved GPS waypoints (amber) and discovered P2P mesh nodes (red) live on the radar grid.

#### 🔤 7. Signal Codes & Field Ciphers
- **NATO Phonetic Alphabet**: Complete interactive A-Z phonetic reference table.
- **Ground-to-Air Rescue Symbols**: International 10ft visual ground symbols chart ('V', 'X', 'SOS', 'L', 'LL', 'N', 'Y') for search aircraft.
- **Field Caesar Cipher**: Offline text encoder/decoder for encrypting tactical notes and mesh messages.

#### ☀️ 8. Solar Panel & Astrogation Calculator
- **Solar Panel Tilt Optimizer**: Computes exact optimal tilt angle and azimuth for solar panels based on current GPS latitude and date.
- **Night Polaris Finder**: Step-by-step night sky alignment guide to determine True North using the Big Dipper and Polaris.
- **Moon Phase & Tides Estimator**: Calculates lunar illumination % and high/low tide predictions.

#### 🧮 9. Ration & Power Budget Calculator
- **Food & Water Ration Estimator**: Input survivor team size and available water/calories to calculate exact survival duration (days remaining).
- **Battery & Powerbank Runtime**: Input battery capacity (mAh) to estimate remaining operating hours for standby, GPS compass, and LED strobe.

#### 🔊 10. Ultrasonic Emitter & Seismic Pulse
- **Ultrasonic Emitter (15 kHz - 22 kHz)**: Generates high-frequency tone bursts for animal/wildlife deterrents or rescue signaling.
- **Seismic Structural Metronome**: Generates rhythmic acoustic pulses (BPM adjustable) for signaling buried survivors through solid structures.

---

## 🇩🇪 Deutsche Version

### ⚡ Hauptmerkmale (9 Taktische Module)

- **🎛️ Taktisches Dashboard**: Große 60dp-Modul-Karten für den schnellen Zugriff.
- **🧭 Solar-Kompass & Navigation**: 3D-Kompass, wahre & magnetische Nordausrichtung, Neigung & Rollwinkel, GPS-Wegpunkte.
- **🚨 Notfall-Bake & SOS**: Stroboskop-Taschenlampe, Tonsirene, Morsecode-Sender, Bildschirm-SOS.
- **🩺 Erste Hilfe & Überlebenshandbuch**: 100% Offline-Medizinhandbuch (CPR, Tourniquet, Hypothermie, Wasseraufbereitung, Unterschlupfbau, Feuer machen).
- **📡 P2P-Mesh-Netzwerk**: Direktnachrichten über Wi-Fi Direct und BLE Mesh ohne Mobilfunknetz.
- **🗺️ Taktisches Radar-Map-HUD**: Entfernungsringe (50m - 5km) mit Wegpunkt- und P2P-Knoten-Markierungen.
- **🔤 Signal-Codes & Chiffren**: NATO-Phonetisches Alphabet, Boden-Luft-Rettungssymbole, Feld-Chiffrierung.
- **☀️ Solar- & Astrogation-Rechner**: Neigungswinkel-Optimierung für Solarmodule, Polaris-Nordstern-Ausrichtung, Mondphasen & Gezeiten.
- **🧮 Rationierungs- & Batterie-Budget**: Berechnung der verbleibenden Überlebenstage für Wasser/Nahrung & Akkulaufzeit.
- **🔊 Ultraschall-Sender & Seismischer Puls**: Hochfrequenz-Tier-Abschreckung (15-22kHz) und akustischer Klopf-Metronom.

---

## 🇮🇷 نسخه فارسی

### ⚡ قابلیت‌های اصلی (۹ ماژول تاکتیکی)

- **🎛️ داشبورد فرماندهی تاکتیکی**: صفحه اصلی با آیکون‌های ۶۰ دی‌پی برای دسترسی ۱-لمسی به تمام ماژول‌ها.
- **🧭 قطب‌نمای خورشیدی و مسیریابی**: قطب‌نمای ۳ بعدی، شمال حقیقی و مغناطیسی، شیب‌سنج، نقاط ثبت‌شده GPS.
- **🚨 فرستنده اضطراری و SOS**: چراغ چشمک‌زن سخت‌افزاری، آژیر صوتی، ارسال کد مورس سفارشی، چشمک‌زن صفحه نمایش.
- **🩺 راهنمای کمک‌های اولیه و بقا**: کتابچه پزشکی ۱۰۰٪ آفلاین (احیای قلبی CPR، تورنیکه، سرمازدگی، تصفیه آب، پناهگاه، آتش).
- **📡 شبکه مش P2P**: چت مستقیم آفلاین از طریق Wi-Fi Direct و بلوتوث بدون نیاز به اینترنت.
- **🗺️ نقشه رادار تاکتیکی**: رادار تاکتیکی به سبک بازی با حلقه‌های فاصله (۵۰متر تا ۵کیلومتر)، نمایش زنده نقاط و همتاها.
- **🔤 کدهای سیگنال و رمزنگاری**: الفبای فونتیک ناتو، نمادهای امداد هوایی، رمزنگاری میدانی.
- **☀️ ناوبری خورشیدی و نجومی**: زاویه‌یاب پنل خورشیدی، ستاره قطبی، فاز ماه و جزرومد.
- **🧮 محاسبه جیره‌بندی و انرژی**: تخمین روزهای بقای آب و غذا بر اساس تعداد نفرات، تخمین دوام باتری.
- **🔊 امواج اولتراسونیک و پالس صوتی**: فرستنده امواج فرکانس بالا (۱۵-۲۲کیلوهرتز) و پالس آکوستیک امداد.

---

## 🏗️ Technical Architecture

- **UI Framework**: **Jetpack Compose** with **Material Design 3 (M3)** expressive tactical design system.
- **Architecture Pattern**: Clean Architecture with **MVVM** (Model-View-ViewModel) and **Repository Pattern**.
- **Local Persistence**: **Room Database** & **DataStore Preferences**.
- **Asynchronous Execution**: Kotlin **Coroutines** & **StateFlow**.
- **Hardware Interop**: Android Sensor Manager API, Camera2 API, Location Services, Bluetooth LE, Wi-Fi P2P framework, AudioTrack PCM buffer synthesizer.

---

## 🛠️ Build & Installation

### Prerequisites
- **Android Studio**: Ladybug / 2024.2.1+
- **JDK**: Java 17 or 21
- **Target SDK**: 37 | **Min SDK**: 24 (Android 7.0+)

### Building via Gradle CLI
```bash
# Clone the repository
git clone https://github.com/agoldust/Eschaton-Tools.git
cd Eschaton-Tools

# Build Debug APK
./gradlew assembleDebug

# Install on connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
