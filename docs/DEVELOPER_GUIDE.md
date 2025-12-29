# Sabil 23 - Professional Developer Guide

A comprehensive technical reference for developers working with the Sabil 23 codebase.

---

## 📑 Table of Contents

1. [Project Architecture](#project-architecture)
2. [Setup Instructions](#setup-instructions)
3. [Code Organization](#code-organization)
4. [Key Components](#key-components)
5. [Data Flow](#data-flow)
6. [API Integration](#api-integration)
7. [Database Schema](#database-schema)
8. [Testing Strategy](#testing-strategy)
9. [Performance Optimization](#performance-optimization)
10. [Debugging Tools](#debugging-tools)
11. [Build System](#build-system)
12. [Deployment](#deployment)

---

## 🏗️ Project Architecture

### Architecture Pattern: Manager Pattern with MVVM Principles

```
┌─────────────────────────────────────┐
│         User Interface (Activities)   │
│  MainActivity, SearchActivity, etc.   │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│  Business Logic (Manager Classes)    │
│  RestaurantManager, RideManager      │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│  Data Access Layer                   │
│  DatabaseHelper, GooglePlacesAPI     │
└─────────────────────────────────────┘
```

### Design Patterns Used

#### 1. **Manager Pattern**
Centralizes business logic for specific features:
```java
public class RestaurantManager {
    private DatabaseHelper db;
    private GooglePlacesApiService apiService;
    
    public List<Restaurant> getNearbyRestaurants(LatLng location, int radius) {
        // Business logic isolated from UI
    }
}
```

#### 2. **Adapter Pattern**
UI adapters for RecyclerView/ListView:
```java
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {
    private List<Restaurant> restaurants;
    // Adapter implementation
}
```

#### 3. **Broadcast Receiver Pattern**
Handles system events and alarms:
```java
public class AdhanNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle notification trigger
    }
}
```

#### 4. **Singleton Pattern**
Ensures single instance of managers:
```java
public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;
    
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }
}
```

---

## 🔧 Setup Instructions

### Development Environment

#### System Requirements
- **OS**: Windows, macOS, or Linux
- **Java**: JDK 11 or higher
- **Android Studio**: 2023.2.x or newer
- **Gradle**: 8.x (auto-managed by wrapper)

#### Step-by-Step Installation

1. **Clone Repository**
   ```bash
   git clone https://github.com/Youssoufboz/iftar-sabill-app-v01.git
   cd sabil23v0
   ```

2. **Set SDK Path**
   ```bash
   # Create local.properties
   echo "sdk.dir=/path/to/Android/Sdk" > local.properties
   ```

3. **Obtain API Keys**
   
   a. **Google Maps API Key**
   - Visit [Google Cloud Console](https://console.cloud.google.com/)
   - Create new project
   - Enable Maps SDK for Android
   - Create Android API key with app package name
   
   b. **Google Places API Key**
   - Same project as above
   - Enable Places API
   - Can use same API key if enabled

4. **Add API Keys to Project**
   
   Edit [app/src/main/res/values/strings.xml](app/src/main/res/values/strings.xml):
   ```xml
   <string name="google_maps_key">YOUR_MAPS_API_KEY_HERE</string>
   <string name="google_places_key">YOUR_PLACES_API_KEY_HERE</string>
   ```

5. **Sync Gradle**
   ```bash
   ./gradlew sync
   ```

6. **Build Project**
   ```bash
   ./gradlew build
   ```

7. **Run on Emulator or Device**
   ```bash
   ./gradlew installDebug
   ```

### IDE Configuration

#### Android Studio
- **Code Style**: File → Settings → Code Style → Java → Set to Android conventions
- **Lint**: File → Settings → Editor → Inspections → Enable Android Lint
- **Emulator**: AVD Manager → Create virtual device with API 31+

#### IntelliJ IDEA
- Install Android plugin
- Configure Android SDK location
- Follow same process as Android Studio

---

## 📦 Code Organization

### Directory Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ramadan/sabil23/
│   │   │       ├── Activities/
│   │   │       │   ├── MainActivity.java
│   │   │       │   ├── NavigationActivity.java
│   │   │       │   ├── SearchActivity.java
│   │   │       │   ├── MapSelectionActivity.java
│   │   │       │   ├── RestaurantMapActivity.java
│   │   │       │   ├── RideHistoryActivity.java
│   │   │       │   ├── SideMenuActivity.java
│   │   │       │   ├── SplashActivity.java
│   │   │       │   └── AdhanNotificationActivity.java
│   │   │       │
│   │   │       ├── Managers/
│   │   │       │   ├── RestaurantManager.java
│   │   │       │   ├── RideManager.java
│   │   │       │   ├── SearchManager.java
│   │   │       │   ├── NavigationManager.java
│   │   │       │   └── AdhanNotificationManager.java
│   │   │       │
│   │   │       ├── Services/
│   │   │       │   ├── GooglePlacesApiService.java
│   │   │       │   └── RestaurantService.java
│   │   │       │
│   │   │       ├── Receivers/
│   │   │       │   ├── AdhanNotificationReceiver.java
│   │   │       │   ├── RestaurantNotificationReceiver.java
│   │   │       │   ├── NotificationReceiver.java
│   │   │       │   ├── NotificationRefreshReceiver.java
│   │   │       │   └── BootReceiver.java
│   │   │       │
│   │   │       ├── Adapters/
│   │   │       │   ├── RestaurantAdapter.java
│   │   │       │   ├── SearchResultAdapter.java
│   │   │       │   └── SearchSuggestionsAdapter.java
│   │   │       │
│   │   │       ├── Models/
│   │   │       │   ├── Restaurant.java
│   │   │       │   ├── Ride.java
│   │   │       │   ├── PrayerTime.java
│   │   │       │   └── SearchResult.java
│   │   │       │
│   │   │       ├── Utils/
│   │   │       │   ├── PrayerTimesCalculator.java
│   │   │       │   └── LocationUtils.java
│   │   │       │
│   │   │       ├── UI/
│   │   │       │   └── RestaurantDetailsBottomSheet.java
│   │   │       │
│   │   │       └── Database/
│   │   │           └── DatabaseHelper.java
│   │   │
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── drawable/
│   │   │   ├── anim/
│   │   │   ├── color/
│   │   │   ├── menu/
│   │   │   ├── values/
│   │   │   ├── values-night/
│   │   │   ├── xml/
│   │   │   └── mipmap-*/
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   ├── test/
│   │   └── java/com/ramadan/sabil23/
│   │       └── Unit tests
│   │
│   └── androidTest/
│       └── java/com/ramadan/sabil23/
│           └── Instrumented tests
│
├── build.gradle.kts
└── proguard-rules.pro
```

### Naming Conventions

#### Java Classes
```
Activities:           *Activity.java         (MainActivity, SearchActivity)
Adapters:             *Adapter.java          (RestaurantAdapter)
Managers:             *Manager.java          (RestaurantManager)
Services:             *Service.java          (GooglePlacesApiService)
Receivers:            *Receiver.java         (AdhanNotificationReceiver)
Models:               *Model.java or plain   (Restaurant, Ride, PrayerTime)
Utilities:            *Utils.java            (LocationUtils)
Database:             DatabaseHelper.java
```

#### Resources
```
Layouts:              activity_*.xml         (activity_main.xml)
Menus:                menu_*.xml             (menu_options.xml)
Drawables:            ic_*.xml (icons)       (ic_location.xml)
Animations:           *.xml in anim/         (fade_in.xml)
Colors:               colors.xml
Strings:              strings.xml
Dimensions:           dimens.xml
Styles:               styles.xml
```

#### Variables
```
Activity fields:      mVariableName          (mGoogleMap, mFusedLocationClient)
Constants:            CONSTANT_NAME          (DEFAULT_ZOOM, SPLASH_DURATION)
Local variables:      variableName           (location, radius)
```

---

## 🔑 Key Components

### 1. MainActivity

**Purpose**: Main entry point with Google Maps integration

**Key Responsibilities**:
- Display interactive map
- Handle location updates
- Search for restaurants/mosques
- Show custom map markers
- Manage zoom and camera

**Key Methods**:
```java
public void onMapReady(GoogleMap googleMap)      // Called when map is ready
private void getLastLocation()                    // Fetch user location
private void searchRestaurants(LatLng location)  // API call for restaurants
private Bitmap createMarkerIcon(int drawableId)  // Custom marker creation
```

**Dependencies**:
- GoogleMap, FusedLocationProviderClient
- RestaurantManager, SearchManager
- DatabaseHelper

### 2. SearchActivity

**Purpose**: Global search interface with suggestions

**Key Features**:
- Real-time search suggestions
- Result filtering and sorting
- Search history

**Key Methods**:
```java
private void performSearch(String query)         // Execute search
private void loadSuggestions(String query)       // Load suggestions
private void displayResults(List<SearchResult> results)
```

### 3. PrayerTimesCalculator

**Purpose**: Calculate accurate prayer times

**Algorithm**:
- Hijri calendar conversion
- Solar declination calculation
- Timezone-aware computations

**Key Methods**:
```java
public Calendar calculateFajrTime(Calendar date, double latitude, double longitude)
public Calendar calculateDhuhrTime(Calendar date, double latitude, double longitude)
public Calendar calculateIshaTime(Calendar date, double latitude, double longitude)
// ... other prayer time methods
```

### 4. RestaurantManager

**Purpose**: Manage restaurant data and operations

**Responsibilities**:
- Fetch nearby restaurants
- Cache restaurant data
- Manage restaurant details

**Key Methods**:
```java
public void fetchNearbyRestaurants(LatLng location, int radius, Callback callback)
public List<Restaurant> getCachedRestaurants()
public void saveRestaurantToFavorites(Restaurant restaurant)
```

### 5. AdhanNotificationManager

**Purpose**: Handle prayer time notifications

**Flow**:
1. Calculate prayer times
2. Schedule AlarmManager for each prayer
3. Trigger notification at exact time
4. Persist after device reboot

**Key Methods**:
```java
public void scheduleAllPrayerAlarms(Calendar date)
private void scheduleAlarm(Calendar prayerTime, int prayerType)
public void cancelAllAlarms()
```

### 6. DatabaseHelper

**Purpose**: SQLite database abstraction

**Tables**:
- restaurants
- rides
- favorites
- search_history
- prayer_times

**Key Methods**:
```java
public void addRestaurant(Restaurant restaurant)
public List<Restaurant> queryRestaurants(String query)
public void updateRideStatus(int rideId, String status)
```

---

## 🔄 Data Flow

### Typical User Flow: Search for Restaurants

```
┌──────────────────────────────────────────────────────────────┐
│ User Input: Opens SearchActivity, enters "halal restaurant"  │
└───────────────────────┬──────────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────┐
│ SearchActivity.performSearch(query)                           │
│ - Validates input                                            │
│ - Updates suggestions                                        │
└───────────────────────┬──────────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────┐
│ SearchManager.search(query, location)                        │
│ - Prepares query parameters                                 │
│ - Calls API service                                         │
└───────────────────────┬──────────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────┐
│ GooglePlacesApiService.searchNearby(location, query)         │
│ - Makes HTTP request to Google Places API                    │
│ - Parses JSON response with Gson                            │
└───────────────────────┬──────────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────┐
│ RestaurantManager.processResults(apiResults)                 │
│ - Converts API results to Restaurant objects                │
│ - Caches in DatabaseHelper                                  │
│ - Returns to caller                                         │
└───────────────────────┬──────────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────┐
│ SearchActivity.displayResults(restaurants)                   │
│ - Updates RecyclerView with RestaurantAdapter               │
│ - Shows results on map with custom markers                  │
└───────────────────────┬──────────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────┐
│ User sees restaurants on map and in list                     │
│ User can tap for details via RestaurantDetailsBottomSheet    │
└──────────────────────────────────────────────────────────────┘
```

### Prayer Notification Flow

```
┌──────────────────────────────────────────────────────────────┐
│ App Startup: BootReceiver detects device reboot             │
└───────────────────────┬──────────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────┐
│ AdhanNotificationManager.scheduleAllPrayerAlarms()           │
│ - Calculates prayer times using PrayerTimesCalculator       │
│ - Creates AlarmManager for each prayer time                 │
└───────────────────────┬──────────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────┐
│ AlarmManager triggers at scheduled prayer time              │
│ Broadcasts Intent to AdhanNotificationReceiver              │
└───────────────────────┬──────────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────┐
│ AdhanNotificationReceiver.onReceive()                        │
│ - Retrieves prayer name from intent                         │
│ - Calls NotificationManager to show notification            │
└───────────────────────┬──────────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────┐
│ User sees prayer time notification on device                │
│ User can tap to open AdhanNotificationActivity              │
└──────────────────────────────────────────────────────────────┘
```

---

## 🌐 API Integration

### Google Maps API

#### Initialization
```java
// In MainActivity.onCreate()
SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
    .findFragmentById(R.id.map);
mapFragment.getMapAsync(this);

// In onMapReady()
@Override
public void onMapReady(GoogleMap googleMap) {
    mGoogleMap = googleMap;
    mGoogleMap.setMyLocationEnabled(true);
    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
}
```

#### Adding Markers
```java
private Bitmap createMarkerIcon(int drawableId) {
    Drawable drawable = ContextCompat.getDrawable(this, drawableId);
    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return bitmap;
}

public void addMarker(LatLng location, String title) {
    MarkerOptions options = new MarkerOptions()
        .position(location)
        .title(title)
        .icon(BitmapDescriptorFactory.fromBitmap(
            createMarkerIcon(R.drawable.ic_location)));
    
    Marker marker = mGoogleMap.addMarker(options);
}
```

### Google Places API

#### Search Nearby Places
```java
public class GooglePlacesApiService {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private String apiKey;
    
    public void searchNearby(double latitude, double longitude, 
                            String placeType, SearchCallback callback) {
        String url = BASE_URL + "nearbysearch/json"
            + "?location=" + latitude + "," + longitude
            + "&radius=5000"
            + "&type=" + placeType
            + "&key=" + apiKey;
        
        // Make HTTP request
        // Parse JSON response
        // Call callback with results
    }
    
    public interface SearchCallback {
        void onSuccess(List<PlaceResult> results);
        void onError(String error);
    }
}
```

### JSON Parsing with Gson

```java
// Define model classes matching API response
public class PlaceResult {
    @SerializedName("name")
    private String name;
    
    @SerializedName("geometry")
    private Geometry geometry;
    
    @SerializedName("place_id")
    private String placeId;
    
    // Getters and setters
}

// Parse response
Gson gson = new Gson();
PlaceResult[] results = gson.fromJson(jsonString, PlaceResult[].class);
```

---

## 📊 Database Schema

### SQLite Database Structure

#### restaurants Table
```sql
CREATE TABLE restaurants (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    address TEXT,
    phone_number TEXT,
    rating REAL,
    place_id TEXT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_restaurant_location ON restaurants(latitude, longitude);
```

#### rides Table
```sql
CREATE TABLE rides (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    driver_name TEXT NOT NULL,
    pickup_location TEXT NOT NULL,
    dropoff_location TEXT NOT NULL,
    pickup_latitude REAL,
    pickup_longitude REAL,
    dropoff_latitude REAL,
    dropoff_longitude REAL,
    departure_time TIMESTAMP,
    status TEXT DEFAULT 'SCHEDULED',
    participants INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ride_status ON rides(status);
CREATE INDEX idx_ride_departure ON rides(departure_time);
```

#### favorites Table
```sql
CREATE TABLE favorites (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    restaurant_id INTEGER,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(restaurant_id) REFERENCES restaurants(id)
);

CREATE INDEX idx_favorite_user ON favorites(user_id);
```

#### prayer_times Table
```sql
CREATE TABLE prayer_times (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date DATE UNIQUE,
    fajr_time TIME,
    sunrise_time TIME,
    dhuhr_time TIME,
    asr_time TIME,
    maghrib_time TIME,
    isha_time TIME,
    latitude REAL,
    longitude REAL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_prayer_date ON prayer_times(date);
```

### Database Helper Usage

```java
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sabil23.db";
    private static final int DATABASE_VERSION = 1;
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    // CRUD Operations
    public long addRestaurant(Restaurant restaurant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", restaurant.getName());
        values.put("latitude", restaurant.getLatitude());
        values.put("longitude", restaurant.getLongitude());
        return db.insert("restaurants", null, values);
    }
    
    public List<Restaurant> getNearbyRestaurants(double lat, double lng, 
                                                 double radius) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM restaurants " +
            "WHERE ABS(latitude - " + lat + ") < " + radius + 
            " AND ABS(longitude - " + lng + ") < " + radius;
        
        Cursor cursor = db.rawQuery(query, null);
        List<Restaurant> restaurants = new ArrayList<>();
        
        if (cursor.moveToFirst()) {
            do {
                // Parse cursor and create Restaurant object
            } while (cursor.moveToNext());
        }
        cursor.close();
        return restaurants;
    }
}
```

---

## 🧪 Testing Strategy

### Unit Testing

**Test File Location**: `app/src/test/java/`

#### Example: Testing Prayer Time Calculator
```java
public class PrayerTimesCalculatorTest {
    
    private PrayerTimesCalculator calculator;
    
    @Before
    public void setUp() {
        calculator = new PrayerTimesCalculator();
    }
    
    @Test
    public void testDhuhrCalculation() {
        // Arrange
        Calendar date = Calendar.getInstance();
        date.set(2024, Calendar.DECEMBER, 29);
        double latitude = 31.9454;
        double longitude = 35.9284; // Example coordinates
        
        // Act
        Calendar dhuhrTime = calculator.calculateDhuhrTime(date, latitude, longitude);
        
        // Assert
        assertNotNull(dhuhrTime);
        assertEquals(12, dhuhrTime.get(Calendar.HOUR_OF_DAY)); // Approximate
    }
    
    @Test
    public void testFajrIsBeforeSunrise() {
        // Test that Fajr is always before Sunrise
        // Implementation...
    }
}
```

### Instrumented Testing

**Test File Location**: `app/src/androidTest/java/`

#### Example: Testing Database Operations
```java
@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {
    
    private DatabaseHelper dbHelper;
    
    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbHelper = new DatabaseHelper(context);
        // Clear database before each test
    }
    
    @Test
    public void testAddAndRetrieveRestaurant() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Mosque");
        restaurant.setLatitude(31.9454);
        restaurant.setLongitude(35.9284);
        
        // Act
        long id = dbHelper.addRestaurant(restaurant);
        
        // Assert
        assertTrue(id > 0);
        
        // Verify retrieval
        Restaurant retrieved = dbHelper.getRestaurantById(id);
        assertNotNull(retrieved);
        assertEquals("Test Mosque", retrieved.getName());
    }
}
```

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests com.ramadan.sabil23.PrayerTimesCalculatorTest

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate test coverage report
./gradlew testDebugUnitTestCoverage
```

---

## ⚡ Performance Optimization

### Memory Management

#### Bitmap Optimization
```java
// Bad: Loading full-resolution bitmap
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_location);

// Good: Downscale bitmap to needed size
BitmapFactory.Options options = new BitmapFactory.Options();
options.inSampleSize = 2; // Load 1/4 of original size
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), 
    R.drawable.ic_location, options);
```

#### RecyclerView Optimization
```java
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {
    
    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        // Reuse views efficiently
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }
    
    // Use DiffUtil for efficient updates
    public void updateRestaurants(List<Restaurant> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
            new DiffCallback(restaurants, newList));
        restaurants = newList;
        diffResult.dispatchUpdatesTo(this);
    }
}
```

### Network Optimization

#### Request Caching
```java
public class GooglePlacesApiService {
    private Map<String, List<PlaceResult>> cache = new HashMap<>();
    private static final long CACHE_DURATION = 3600000; // 1 hour
    
    public void searchNearby(LatLng location, String type, 
                            SearchCallback callback) {
        String cacheKey = location.latitude + "," + location.longitude + ":" + type;
        
        // Check cache first
        if (cache.containsKey(cacheKey)) {
            callback.onSuccess(cache.get(cacheKey));
            return;
        }
        
        // Make API request
        makeApiRequest(location, type, results -> {
            cache.put(cacheKey, results);
            callback.onSuccess(results);
        });
    }
}
```

### Database Optimization

#### Indexing
```java
public class DatabaseHelper extends SQLiteOpenHelper {
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create indexes for frequently queried columns
        db.execSQL("CREATE INDEX idx_restaurant_location " +
            "ON restaurants(latitude, longitude)");
        
        db.execSQL("CREATE INDEX idx_ride_status " +
            "ON rides(status)");
    }
}
```

#### Batch Operations
```java
public void addMultipleRestaurants(List<Restaurant> restaurants) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.beginTransaction();
    
    try {
        for (Restaurant restaurant : restaurants) {
            ContentValues values = new ContentValues();
            values.put("name", restaurant.getName());
            db.insert("restaurants", null, values);
        }
        db.setTransactionSuccessful();
    } finally {
        db.endTransaction();
    }
}
```

### Location Updates

#### Efficient Location Requests
```java
public void requestLocationUpdates() {
    LocationRequest locationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        .setInterval(10000)      // Update every 10 seconds
        .setFastestInterval(5000); // Fastest update every 5 seconds
    
    fusedLocationClient.requestLocationUpdates(locationRequest, 
        locationCallback, Looper.getMainLooper());
}
```

---

## 🔍 Debugging Tools

### Logcat Filtering

```bash
# View logs from specific tag
adb logcat | grep MainActivity

# View logs with timestamp
adb logcat -v threadtime | grep "TAG_NAME"

# Save logs to file
adb logcat > app_logs.txt

# Clear logs
adb logcat -c

# View crash logs only
adb logcat | grep "FATAL"
```

### Android Profiler

Located in Android Studio: **View → Tool Windows → Profiler**

#### CPU Profiling
- Detect performance bottlenecks
- Analyze thread activity
- Identify slow methods

#### Memory Profiling
- Track heap allocation
- Identify memory leaks
- Monitor garbage collection

#### Network Profiling
- Monitor API calls
- Track bandwidth usage
- Analyze request/response timing

### Debug Breakpoints

```java
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {
    
    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        // Set breakpoint here to debug
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }
}
```

**Using Breakpoints**:
1. Click on line number in editor
2. Run in Debug mode
3. Program pauses at breakpoint
4. Use debugger controls to step through code

### Custom Logging

```java
public class Logger {
    private static final String TAG = "SabilDebug";
    
    public static void d(String message) {
        Log.d(TAG, message);
    }
    
    public static void e(String message, Throwable error) {
        Log.e(TAG, message, error);
    }
}

// Usage
Logger.d("Restaurants loaded: " + restaurants.size());
Logger.e("Failed to fetch location", exception);
```

---

## 🛠️ Build System

### Gradle Configuration

[app/build.gradle.kts](app/build.gradle.kts)
```kotlin
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.ramadan.sabil23"
    compileSdk = 35
    
    defaultConfig {
        applicationId = "com.ramadan.sabil23"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    
    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
```

### Build Variants

```bash
# Debug build (default)
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Build and install on device
./gradlew installDebug

# Clean build
./gradlew clean build

# Build with dependency tree
./gradlew dependencies
```

### ProGuard Configuration

[app/proguard-rules.pro](app/proguard-rules.pro)
```
# Preserve custom classes
-keep class com.ramadan.sabil23.** { *; }

# Preserve model classes for Gson
-keep class com.ramadan.sabil23.models.** { *; }

# Preserve Google Maps and Places
-keep class com.google.android.gms.** { *; }

# Keep Firebase
-keep class com.google.firebase.** { *; }

# Keep Gson
-keep class com.google.gson.** { *; }
```

---

## 📦 Deployment

### Pre-Release Checklist

- [ ] Update version code and version name
- [ ] Test on multiple Android versions (API 24, 31, 35)
- [ ] Verify all permissions requested properly
- [ ] Test on both phone and tablet
- [ ] Check app performance with Profiler
- [ ] Verify API keys are correct
- [ ] Test offline functionality
- [ ] Check privacy policy and terms
- [ ] Review all string resources for typos
- [ ] Test dark mode (values-night)

### Building Release APK

```bash
# Build release APK
./gradlew assembleRelease

# APK location: app/build/outputs/apk/release/app-release.apk
```

### Signing APK

```bash
# Create keystore (if not exists)
keytool -genkey -v -keystore my-key.keystore -keyalg RSA \
    -keysize 2048 -validity 10000 -alias my-key

# Sign APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
    -keystore my-key.keystore app-release-unsigned.apk my-key

# Verify signature
jarsigner -verify -verbose -certs app-release.apk
```

### Google Play Store Submission

1. Create Google Play Developer account
2. Prepare app listing with screenshots and description
3. Create signed APK/AAB
4. Upload to Play Store
5. Set pricing (free/paid)
6. Select countries for distribution
7. Submit for review

---

## 📚 Additional Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Google Maps API Documentation](https://developers.google.com/maps/documentation)
- [Google Places API](https://developers.google.com/maps/documentation/places)
- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Material Design Guidelines](https://material.io/design)

---

**Last Updated**: December 2024

For questions or clarifications, contact the development team.
