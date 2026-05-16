# Namma-Shaale Inventory Management System

An offline-first Android app for managing school assets in government-aided primary and secondary schools across Karnataka. Built as a capstone project during the Android Development Internship at MindMatrix.

---

## The Problem

Government-aided schools in Karnataka manage all their assets — furniture, electronics, equipment — using handwritten paper registers. This leads to:

- Data loss due to damaged or misplaced registers
- No way to track asset condition over time
- Generating repair request lists manually before every SDMC meeting
- No record of when or how assets went missing or got damaged

**Namma-Shaale** solves this by giving teachers a simple Android app to register assets, do monthly condition checks, log issues, and generate shareable reports — all without internet.

---

## What the App Does

- **Asset Registration** — Add assets with name, serial number, category, condition, and an optional photo
- **Monthly Health Check** — Update each asset's condition (Working / Needs Repair / Broken) in bulk with a progress tracker
- **Issue Logging** — Log damage or loss with a timestamp; mark issues as resolved while keeping history
- **Repair Request List** — Auto-generated list of all assets needing attention, updated in real time
- **Live Dashboard** — See total, working, needs-repair, and broken counts at a glance
- **Report Generation** — Generate a summary report shareable via WhatsApp, email, or any app

---

## Tech Stack

| | |
|---|---|
| Language | Kotlin 1.9.22 |
| Architecture | MVVM + Repository + DAO |
| Database | Room (SQLite) |
| Reactive UI | LiveData + ViewModel |
| Navigation | Navigation Component 2.7.7 |
| Camera | CameraX 1.3.2 |
| Image Loading | Glide 4.16.0 |
| UI | Material Design 3 |
| Async | Kotlin Coroutines |
| Build | Gradle 8.11 + AGP 8.7.3 |

---

## Clone and Run

**Requirements:** Android Studio Hedgehog / Iguana, Android device or emulator running API 26 (Android 8.0) or higher.

```bash
git clone https://github.com/AS-ciii/NammaShaaleInventory.git
cd NammaShaaleInventory
```

Open the folder in Android Studio, let Gradle sync, then hit **Run ▶**.

---

## Install the APK

1. Download the latest APK from the [Releases](https://github.com/AS-ciii/NammaShaaleInventory/releases) page
2. On your Android phone, go to **Settings → Install unknown apps** and allow your browser or file manager
3. Open the downloaded APK file and tap **Install**
4. Once installed, open **Namma-Shaale** from your app drawer

> Requires Android 8.0 or above. No internet connection needed.

---

## Using the App

1. **Dashboard** — Opens on launch. Shows live asset counts and recent health check activity.
2. **Add an asset** — Tap the **+** button on the Asset List screen. Fill in the details and optionally take a photo.
3. **Monthly health check** — Go to the Health Check tab. Tap each asset card to set its current condition.
4. **Log an issue** — Go to Issue Log, tap **+**, select the asset, and describe the problem.
5. **View repair needs** — The Repair Request tab auto-lists all assets marked Needs Repair or Broken.
6. **Generate a report** — Go to the Report screen and tap **Share** to send it via WhatsApp, email, etc.
