# Offline Location Tracker

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![Room](https://img.shields.io/badge/Room-cache-3DDC84?style=flat-square&logo=android&logoColor=white)
![Hilt](https://img.shields.io/badge/Hilt-DI-34A853?style=flat-square&logo=google&logoColor=white)

An offline-first Android app that reads the device location, stores every point in a local **Room** database and shows the history. The UI only observes the database, so the app keeps working without internet: Room is the **single source of truth**.

> Lab #9 · Mobile Application Development course. Ukrainian version: [README.uk.md](README.uk.md)

## Features

- Runtime location permission flow
- Current position via `FusedLocationProviderClient`: latitude, longitude, accuracy
- History list that updates automatically through Kotlin `Flow`
- Clear all saved points
- Works offline: everything is read from the local cache

## Architecture

```mermaid
flowchart LR
  UI[Compose UI<br/>LocationsScreen] -->|events| VM[LocationViewModel<br/>StateFlow]
  VM --> R[LocationRepository]
  R -->|getCurrentLocation| F[FusedLocationProviderClient]
  R -->|insert / observe| DB[(Room<br/>LocationDao)]
  DB -->|Flow| VM
```

| Layer | Classes |
|---|---|
| UI | `LocationsScreen`, `PermissionsFlow` (Jetpack Compose) |
| State | `LocationViewModel` exposing `StateFlow` |
| Data | `LocationRepository`, `LocationDao`, `LocationEntity`, `AppDatabase` |
| DI | Hilt: `LocationApp`, `AppModule` |

## Tech stack

Kotlin · Coroutines & Flow · Jetpack Compose · Room · Google Play Services Location · Hilt

## Run locally

1. Open the project in Android Studio (Ladybug or newer) and let Gradle sync
2. Run on a device or an emulator with Google Play Services
3. Grant location permission and tap **Отримати позицію** (Get position) to save the current point
