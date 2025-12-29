# Sabil 23 - Islamic Community Assistance App

> A comprehensive Android application designed to help Muslims find prayer times, nearby mosques, iftar rides, and restaurants during Ramadan and throughout the year.

## 📱 Overview

**Sabil 23** is a feature-rich Android application that serves the Islamic community by providing:
- **Prayer Time Notifications**: Accurate prayer time calculations and notifications
- **Mosque Finder**: Locate nearby mosques on an interactive map
- **Restaurant Directory**: Find halal restaurants and eateries in your area
- **Iftar Ride Sharing**: Connect with others for iftar rides during Ramadan
- **Ride History**: Track and manage your ride history
- **Location-based Search**: Intelligent search with location-aware suggestions

The app is built with modern Android development practices, using Google Maps integration, location services, and local database management.

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
├── Activities
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
├── Services & Managers
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
├── Receivers
│   ├── AdhanNotificationReceiver # Prayer time alerts
│   ├── RestaurantNotificationReceiver # Restaurant alerts
│   ├── NotificationReceiver      # General notifications
│   ├── NotificationRefreshReceiver # Notification updates
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
├── Location
│   └── Location utilities        # Location helper classes
│
└── Models
    └── Data model classes        # Restaurant, Ride, Prayer entities
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio 2023.x or newer
- JDK 11 or higher
- Gradle 8.x
- Google Play Services SDK
- Google Maps API Key
- Google Places API Key

### Installation

#### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/sabil23.git
cd sabil23
```

#### 2. Configure API Keys

Create a `local.properties` file in the project root (if not exists):
```properties
sdk.dir=/path/to/android/sdk
```

Add your Google API credentials to [app/src/main/res/values/strings.xml](app/src/main/res/values/strings.xml):
```xml
<string name="google_maps_key">YOUR_GOOGLE_MAPS_API_KEY</string>
<string name="google_places_key">YOUR_GOOGLE_PLACES_API_KEY</string>
```

#### 3. Build the Project
```bash
./gradlew build
```

#### 4. Run on Device/Emulator
```bash
./gradlew installDebug
```

---

## 📋 Required Permissions

The app requests the following Android permissions:

| Permission | Purpose |
|-----------|---------|
| `INTERNET` | API calls and map data |
| `ACCESS_FINE_LOCATION` | Precise user location for services |
| `ACCESS_COARSE_LOCATION` | Approximate location fallback |
| `POST_NOTIFICATIONS` | Prayer time and event notifications |
| `SCHEDULE_EXACT_ALARM` | Precise prayer time alarms |

Users are prompted to grant these permissions on first launch and during runtime (Android 6.0+).

---

## 🔧 Configuration

### Build Configuration
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)
- **Compile SDK**: API 35
- **Java Version**: 11

### Key Dependencies
```gradle
- androidx.appcompat:appcompat:1.x
- androidx.constraintlayout:constraintlayout:2.x
- com.google.android.gms:play-services-maps
- com.google.android.gms:play-services-location
- com.google.android.material:material:1.x
- com.google.code.gson:gson
- androidx.activity:activity:1.x
```

For complete dependency list, see [gradle/libs.versions.toml](gradle/libs.versions.toml)

---

## 💻 Developer Guide

### Code Organization Best Practices

#### Activity Management
Activities handle UI and user interaction. Each activity is responsible for:
- Loading appropriate layout files
- Managing UI lifecycle
- Delegating business logic to Manager classes

Example structure:
```java
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    private RestaurantManager restaurantManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restaurantManager = new RestaurantManager(this);
    }
}
```

#### Manager Pattern
Manager classes handle business logic and data operations:

```java
public class RestaurantManager {
    private DatabaseHelper dbHelper;
    private GooglePlacesApiService placesService;
    
    public void fetchNearbyRestaurants(LatLng location, int radius) {
        // Implementation
    }
}
```

#### Database Operations
SQLite operations are abstracted in `DatabaseHelper`:

```java
public class DatabaseHelper extends SQLiteOpenHelper {
    public Cursor getRestaurants(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }
}
```

### Prayer Time Calculations
The `PrayerTimesCalculator` class uses mathematical formulas to compute accurate prayer times:
- Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha
- Timezone-aware calculations
- Multiple calculation methods support

### Location Services Integration
```java
private FusedLocationProviderClient fusedLocationClient;

private void getLastLocation() {
    if (ContextCompat.checkSelfPermission(this, 
        Manifest.permission.ACCESS_FINE_LOCATION) 
        == PackageManager.PERMISSION_GRANTED) {
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            // Handle location update
        });
    }
}
```

### Google Places API Integration
```java
public class GooglePlacesApiService {
    public void searchNearbyPlaces(LatLng location, String placeType) {
        // API call to fetch nearby places
        // Parse response and return results
    }
}
```

### Notification Architecture
Two types of notifications:
1. **Adhan Notifications**: Prayer time alerts
2. **Restaurant Notifications**: Recommendations and offers

```java
public class AdhanNotificationManager {
    private NotificationManager notificationManager;
    
    public void scheduleAdhanNotification(Calendar prayerTime) {
        // Schedule alarm for exact prayer time
        // Trigger notification when alarm fires
    }
}
```

### Handle Broadcast Receivers
Receivers handle system events and alarms:

```java
public class AdhanNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Show notification when alarm triggers
    }
}

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Reschedule notifications after device reboot
    }
}
```

### Permission Handling
Runtime permissions are requested dynamically:

```java
private void requestLocationPermission() {
    ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
        LOCATION_PERMISSION_REQUEST_CODE);
}

@Override
public void onRequestPermissionsResult(int requestCode, 
    @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
        if (grantResults.length > 0 
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
        }
    }
}
```

---

## 🗂️ File Structure Details

### Resource Files

#### Layouts (`res/layout/`)
- `activity_main.xml` - Main map interface with search bar
- `activity_navigation.xml` - Navigation hub layout
- `activity_splash.xml` - Splash screen UI
- `activity_search.xml` - Global search interface
- `activity_restaurant_map.xml` - Restaurant map view
- `activity_ride_history.xml` - Ride list view

#### Drawables (`res/drawable/`)
- **Icons**: Location, navigation, menu, filters, etc.
- **Backgrounds**: Button styles, gradients, selection states
- **Markers**: Custom map marker designs
- **Shapes**: Custom circles and button backgrounds

#### Animations (`res/anim/`)
- `fade_in.xml` - Fade-in transition
- `fade_out.xml` - Fade-out transition
- `slide_up.xml` - Slide-up animation
- `bounce.xml` - Bounce effect for UI elements

#### Colors (`res/color/`)
- `switch_track_selector.xml` - Toggle switch colors
- `switch_thumb_selector.xml` - Toggle thumb colors

#### Values (`res/values/`)
- `strings.xml` - UI text and API keys
- `colors.xml` - App color palette
- `styles.xml` - Theme and styling definitions
- `dimens.xml` - Dimension constants

#### Night Mode (`res/values-night/`)
- Dark theme variants for all color resources

---

## 🔄 Development Workflow

### Building Variants

#### Debug Build
```bash
./gradlew assembleDebug
```
- Full debugging enabled
- Not minified
- Useful for development

#### Release Build
```bash
./gradlew assembleRelease
```
- Minified with ProGuard
- All debug symbols removed
- Optimized for distribution

### Testing

#### Unit Tests
```bash
./gradlew test
```
Run local unit tests in `app/src/test/`

#### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```
Run on-device tests in `app/src/androidTest/`

---

## 📦 Dependencies & Licenses

| Dependency | Version | Purpose | License |
|-----------|---------|---------|---------|
| androidx.appcompat | Latest | Android compatibility | Apache 2.0 |
| com.google.android.gms.maps | Latest | Map functionality | Google Mobile Services |
| com.google.android.gms.location | Latest | Location services | Google Mobile Services |
| com.google.code.gson | Latest | JSON parsing | Apache 2.0 |
| androidx.material | Latest | Material Design | Apache 2.0 |
| androidx.constraintlayout | Latest | Layout system | Apache 2.0 |
| androidx.activity | Latest | Activity framework | Apache 2.0 |

---

## 🐛 Troubleshooting

### Common Issues

#### Issue: Maps not displaying
**Solution**: 
- Verify Google Maps API key in `strings.xml`
- Ensure device has Google Play Services installed
- Check AndroidManifest.xml has proper meta-data

#### Issue: Location permission denied
**Solution**:
- Check app has `ACCESS_FINE_LOCATION` permission
- User must grant permission when prompted
- Test on Android 6.0+ for runtime permissions

#### Issue: Notifications not triggering
**Solution**:
- Verify `POST_NOTIFICATIONS` permission granted
- Check `NotificationManager` is properly initialized
- Ensure `BootReceiver` is declared in manifest
- Check device settings for app notification permissions

#### Issue: Prayer times are inaccurate
**Solution**:
- Verify user location is correct
- Check timezone is set correctly on device
- Confirm calculation method matches region
- Update timezone settings

---

## 📝 Logging & Debugging

The app uses Android's standard logging:

```java
import android.util.Log;

private static final String TAG = "MainActivity";

Log.d(TAG, "Debug message");
Log.i(TAG, "Info message");
Log.w(TAG, "Warning message");
Log.e(TAG, "Error message");
```

View logs using:
```bash
adb logcat | grep TAG_NAME
```

---

## 🔐 Security Considerations

1. **API Key Security**
   - Keep API keys in `strings.xml` (not committed to public repo)
   - Use separate keys for development and production
   - Rotate keys regularly

2. **Location Data**
   - Only collect location when necessary
   - Request minimum required permissions
   - Clear location history when appropriate

3. **Data Storage**
   - SQLite database is local to app
   - Encrypt sensitive user data
   - Use SharedPreferences for simple key-value data

4. **Network Communication**
   - Use HTTPS for all API calls
   - Validate SSL certificates
   - Handle network timeouts gracefully

---

## 🚀 Deployment

### Preparing for Release

1. **Update Version Numbers**
   ```gradle
   versionCode = 1
   versionName = "1.0"
   ```

2. **Generate Signed APK**
   ```bash
   ./gradlew assembleRelease
   ```

3. **Test Release Build**
   - Test on multiple device configurations
   - Verify all features work correctly
   - Check permission requests

4. **Sign with Release Key**
   - Create keystore if not exists
   - Sign APK with release key
   - Verify signature

### Distribution

- **Google Play Store**: Submit APK + metadata
- **Direct Distribution**: Host APK on server
- **Beta Testing**: Use Google Play beta track

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Make your changes**
   - Follow existing code style
   - Write clear commit messages
   - Add comments for complex logic
4. **Test your changes**
   - Run unit tests
   - Test on multiple devices/Android versions
   - Verify no regressions
5. **Submit a Pull Request**
   - Describe changes clearly
   - Reference any related issues
   - Include screenshots if UI changes

### Code Style Guidelines
- Use meaningful variable names
- Follow Android naming conventions
- Add Javadoc comments for public methods
- Keep methods focused and small
- Use constants for magic numbers

---

## 📞 Support & Contact

For issues, questions, or suggestions:

- **Report Issues**: Open an issue on GitHub
- **Email**: support@sabil23.com
- **Discord**: Join our community server

---

## 📄 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Google Maps and Places API
- Google Play Services
- Android community and contributors
- Islamic community for inspiration and feedback

---

## 📊 Project Statistics

- **Total Activities**: 8
- **Total Services/Managers**: 8+
- **Total Broadcast Receivers**: 5
- **Database Tables**: Multiple (SQLite)
- **API Integrations**: Google Maps, Google Places
- **Permissions Required**: 5
- **Min SDK**: API 24 | **Target SDK**: API 35

---

**Last Updated**: December 2024

For the latest updates and documentation, visit the [GitHub Repository](https://github.com/yourusername/sabil23)
