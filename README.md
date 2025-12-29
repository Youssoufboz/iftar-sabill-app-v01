# Sabil 23 - Islamic Community Assistance App

> A comprehensive Android application designed to help Muslims find prayer times, nearby mosques, iftar rides, and restaurants during Ramadan and throughout the year.

**Repository**: [github.com/Youssoufboz/iftar-sabill-app-v01](https://github.com/Youssoufboz/iftar-sabill-app-v01)  
**Contact**: bouzekriyousouf@gmail.com  
**Issues & Feedback**: [GitHub Issues](https://github.com/Youssoufboz/iftar-sabill-app-v01/issues)

---

## 📱 Quick Overview

**Sabil 23** serves the Islamic community by providing:
- ✅ **Prayer Time Notifications** - Accurate Adhan alerts based on location
- ✅ **Mosque Finder** - Locate nearby mosques on interactive map
- ✅ **Restaurant Directory** - Find halal restaurants in your area
- ✅ **Iftar Ride Sharing** - Connect with others for rides during Ramadan
- ✅ **Ride History** - Track and manage your ride history
- ✅ **Smart Search** - Location-aware search with suggestions

Built with modern Android development practices: Java 11, Google Maps integration, location services, and SQLite database.

---

## 🚀 Quick Start (5 minutes)

### Prerequisites
- Android Studio 2023.2+
- JDK 11+
- Android SDK API 35
- Git

### Setup Steps

```bash
# 1. Clone the repository
git clone https://github.com/Youssoufboz/iftar-sabill-app-v01.git
cd sabil23v0

# 2. Create local.properties with your SDK path
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# 3. Configure Google Maps and Places APIs
# Edit app/src/main/res/values/strings.xml with your API keys:
# - google_maps_api_key
# - google_places_api_key

# 4. Build and run
./gradlew build
# Open in Android Studio and run on emulator or device
```

**For detailed setup and troubleshooting**, see [QUICKSTART Guide](docs/QUICKSTART.md)

---

## ✨ Key Features

### 🕌 Prayer Time Management
- Real-time prayer time calculations based on user location
- Adhan (call to prayer) notifications with customizable alerts
- Support for multiple calculation methods
- Alarm scheduling using ExactAlarm API for precise notifications

### 📍 Interactive Map Features
- Google Maps integration with custom markers
- Real-time location tracking using Google Location Services
- Clustered markers for multiple locations
- Custom map styling and visual indicators

### 🍽️ Restaurant Discovery
- Search for nearby halal restaurants
- Detailed restaurant information via Google Places API
- Bottom sheet UI for restaurant details
- Expandable restaurant list with filtering

### 🚗 Ride Management
- Browse and manage Iftar rides
- Ride history tracking
- Location-based ride suggestions
- Ride status notifications

### 🔔 Smart Notifications
- Push notifications for prayer times
- Restaurant recommendations
- Ride notifications
- Boot persistence using BootReceiver

### 🔍 Advanced Search
- Multi-field search with suggestions
- Location-aware search results
- Search history and suggestions
- Real-time result filtering

**Full feature documentation**: [FEATURES Guide](docs/FEATURES.md)

---

## 🏗️ Project Architecture

### Technology Stack
- **Language**: Java 11
- **Android SDK**: Target API 35 (Android 15), Min API 24 (Android 7.0)
- **Build System**: Gradle 8.x with Kotlin DSL
- **Architecture Pattern**: MVVM-inspired with Manager classes
- **Maps**: Google Maps API v2
- **Location**: Google Play Services Location API
- **Database**: SQLite via DatabaseHelper
- **JSON**: Gson for serialization

### Package Structure

```
com.ramadan.sabil23/
├── Activities (8)
│   ├── MainActivity              # Main map-based interface
│   ├── NavigationActivity        # Navigation hub
│   ├── SplashActivity            # App startup screen
│   ├── SearchActivity            # Global search interface
│   ├── MapSelectionActivity      # Location picker
│   ├── RestaurantMapActivity     # Restaurant map view
│   ├── RideHistoryActivity       # Ride management
│   ├── SideMenuActivity          # Navigation menu
│   └── AdhanNotificationActivity # Prayer notification details
│
├── Services & Managers (8+)
│   ├── PrayerTimesCalculator    # Prayer time computations
│   ├── RestaurantManager         # Restaurant data management
│   ├── RideManager               # Ride operations
│   ├── SearchManager             # Search logic
│   ├── NavigationManager         # Navigation coordination
│   ├── AdhanNotificationManager  # Prayer notification handling
│   └── GooglePlacesApiService    # Google Places API wrapper
│
├── Database
│   └── DatabaseHelper            # SQLite operations
│
├── Receivers (5)
│   ├── AdhanNotificationReceiver # Prayer time alerts
│   ├── RestaurantNotificationReceiver
│   ├── NotificationReceiver      # General notifications
│   ├── NotificationRefreshReceiver
│   └── BootReceiver              # Device startup handling
│
├── Adapters
│   ├── RestaurantAdapter         # Restaurant list UI
│   ├── SearchResultAdapter       # Search results UI
│   └── SearchSuggestionsAdapter  # Search suggestions UI
│
├── UI Components
│   └── RestaurantDetailsBottomSheet # Restaurant details popup
│
└── Models
    └── Data classes (Restaurant, Ride, Prayer, etc.)
```

**For in-depth architecture**, see [DEVELOPER GUIDE](docs/DEVELOPER_GUIDE.md)

---

## 📋 Required Permissions

| Permission | Purpose |
|-----------|---------|
| `INTERNET` | API calls and map data |
| `ACCESS_FINE_LOCATION` | Precise user location for services |
| `ACCESS_COARSE_LOCATION` | Approximate location fallback |
| `POST_NOTIFICATIONS` | Prayer time and event notifications |
| `SCHEDULE_EXACT_ALARM` | Precise prayer time alarms |

Users are prompted to grant these permissions on first launch and during runtime (Android 6.0+).

---

## 🔧 Build Configuration

### SDK Versions
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)
- **Compile SDK**: API 35
- **Java Version**: 11

### Key Dependencies
- androidx.appcompat:appcompat:1.x
- androidx.constraintlayout:constraintlayout:2.x
- com.google.android.gms:play-services-maps
- com.google.android.gms:play-services-location
- com.google.android.material:material:1.x
- com.google.code.gson:gson

For complete dependencies, see [gradle/libs.versions.toml](gradle/libs.versions.toml)

### Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run on device
./gradlew installDebug

# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

---

## 🗂️ Documentation Structure

This project uses a modular documentation approach:

- **README.md** (you are here) - Project overview and quick start
- [**docs/QUICKSTART.md**](docs/QUICKSTART.md) - Detailed setup guide with API configuration
- [**docs/FEATURES.md**](docs/FEATURES.md) - Complete feature reference with UI examples
- [**docs/DEVELOPER_GUIDE.md**](docs/DEVELOPER_GUIDE.md) - Architecture, code patterns, database schema, API integration, testing
- [**docs/CONTRIBUTING.md**](docs/CONTRIBUTING.md) - Contribution guidelines, code style, workflow

---

## 🐛 Troubleshooting

### Maps not displaying
- Verify Google Maps API key in `app/src/main/res/values/strings.xml`
- Ensure device has Google Play Services installed
- Check AndroidManifest.xml has proper meta-data

### Location permission denied
- Verify app has `ACCESS_FINE_LOCATION` permission
- User must grant permission when prompted
- Test on Android 6.0+ for runtime permissions

### Notifications not triggering
- Verify `POST_NOTIFICATIONS` permission is granted
- Check device settings for app notification permissions
- Ensure `BootReceiver` is declared in AndroidManifest.xml

### Prayer times are inaccurate
- Verify user location is correct
- Check device timezone is set correctly
- Confirm calculation method matches your region
- Update app and device to latest versions

---

## 🤝 Contributing

Contributions are welcome! Follow these steps:

1. **Fork** the repository on GitHub
2. **Create** a feature branch: `git checkout -b feature/amazing-feature`
3. **Make** your changes following code style guidelines
4. **Test** thoroughly on multiple devices/Android versions
5. **Commit** with clear messages: `git commit -m "Add: detailed description"`
6. **Push** to your fork: `git push origin feature/amazing-feature`
7. **Submit** a Pull Request with description and screenshots (if UI changes)

**For detailed contributing guidelines**, see [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md)

### Code Style
- Use meaningful variable names
- Follow Android naming conventions
- Add Javadoc comments for public methods
- Keep methods focused and small
- Use constants for magic numbers

---

## 📞 Support

For issues, questions, or suggestions:

- **Report Bugs**: [GitHub Issues](https://github.com/Youssoufboz/iftar-sabill-app-v01/issues)
- **Email**: bouzekriyousouf@gmail.com
- **GitHub**: [Youssoufboz](https://github.com/Youssoufboz)

---

## 📄 License

This project is open source and available under the MIT License. See the LICENSE file for details.

---

## 🙏 Acknowledgments

- Google Maps and Places API documentation
- Google Play Services
- Android community
- Islamic community for inspiration and feedback

---

**Last Updated**: December 2024  
**Project Repository**: [github.com/Youssoufboz/iftar-sabill-app-v01](https://github.com/Youssoufboz/iftar-sabill-app-v01)
