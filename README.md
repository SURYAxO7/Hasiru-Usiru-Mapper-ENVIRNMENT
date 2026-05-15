# Hasiru-Usiru Mapper

**AI-powered environmental mapping and green auditing platform** for citizens, students, and municipalities in Bengaluru, Mysuru, and beyond.

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20Clean-orange)

## Features

- **Interactive Google Maps** — Real-time tree (green) and empty pit (red) markers with clustering
- **AI Tree Identification** — Google Gemini API species detection with Kannada descriptions
- **Oxygen Score Engine** — `Oxygen Score = Girth × Species Factor` with community analytics
- **Empty Pit Reporting** — AI-recommended native species for planting locations
- **Species Guide** — Offline-cached educational content in English and Kannada
- **Offline-First** — Room database with background sync via WorkManager
- **Gamification** — Points, badges, leaderboards, weekly eco-challenges
- **Firebase Backend** — Auth, Firestore, Storage, Cloud Messaging
- **Admin Dashboard** — Municipality report verification and analytics
- **Multilingual** — Dynamic English/Kannada switching

## Project Structure

```
Hasiru-Usiru-Mapper/
├── app/                          # Android application
│   └── src/main/java/com/hasiru/usiru/mapper/
│       ├── core/                 # DI, network, sync, FCM, locale
│       ├── data/                 # Room, Firebase, Retrofit, repositories
│       ├── domain/               # Models, engines, repository interfaces
│       └── presentation/         # Compose UI, ViewModels, navigation
├── backend/                      # Node.js REST API (optional supplement)
├── firebase/                     # Security rules & schema docs
└── README.md
```

## Architecture

```
Presentation (Compose + ViewModel)
        ↓
Domain (Use Cases, Models, Repository Interfaces)
        ↓
Data (Room + Firebase + Retrofit Implementations)
```

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Hilt, Coroutines, Flow, Room, Retrofit, Firebase, Google Maps SDK, CameraX, WorkManager, Gemini AI

## Setup

### Prerequisites

- Android Studio Ladybug (2024.2+) or newer
- JDK 17
- Android SDK 35
- Firebase project
- Google Cloud project with Maps SDK & Gemini API enabled

### 1. Clone and Configure

```bash
cd "HAsiru usiru"
cp local.properties.example local.properties
```

Edit `local.properties`:

```properties
sdk.dir=/path/to/Android/sdk
MAPS_API_KEY=your_google_maps_api_key
GEMINI_API_KEY=your_gemini_api_key
API_BASE_URL=http://10.0.2.2:3000/api/
```

### 2. Firebase Setup

1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add Android app with package `com.hasiru.usiru.mapper`
3. Download `google-services.json` → place in `app/`
4. Enable **Authentication** (Email/Password + Google)
5. Create **Firestore** database
6. Enable **Storage** and **Cloud Messaging**
7. Deploy security rules:

```bash
firebase deploy --only firestore:rules,storage
```

### 3. Google Maps

1. Enable Maps SDK for Android in Google Cloud Console
2. Add API key to `local.properties` as `MAPS_API_KEY`
3. Enable billing (required for Maps, free tier available)

### 4. Build APK

```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

./gradlew assembleRelease
# Requires signing config in app/build.gradle.kts
```

### 5. Backend API (Optional)

```bash
cd backend
npm install
cp .env.example .env   # Set JWT_SECRET, Firebase credentials
npm run dev
```

API runs at `http://localhost:3000`

## Oxygen Score Formula

| Species | Factor |
|---------|--------|
| Neem | 1.5 |
| Peepal | 1.8 |
| Banyan | 2.0 |
| Coconut | 1.2 |
| Mango | 1.4 |
| Honge | 1.6 |
| Ashoka | 1.3 |

**Formula:** `Oxygen Score = Tree Girth (cm) × Species Factor`

## Admin Access

Set user role to `ADMIN` or `MUNICIPALITY` in Firestore `users/{uid}.role` to access the admin dashboard module.

## Testing

```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests
```

## Deployment Checklist

- [ ] Replace `google-services.json` with production Firebase config
- [ ] Configure ProGuard for release builds
- [ ] Set up Firebase App Check
- [ ] Deploy Firestore & Storage security rules
- [ ] Configure release signing keystore
- [ ] Enable Firebase Crashlytics
- [ ] Submit to Google Play Console

## License

Developed for environmental education and smart city green initiatives. See project documentation for academic and municipal use guidelines.

## Contributing

Contributions welcome from environmental volunteers, students, and developers. Tag trees responsibly and verify species identifications in the field.
