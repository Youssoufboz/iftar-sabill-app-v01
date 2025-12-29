# Sabil 23 - Complete Features Documentation

Comprehensive list of all features, capabilities, and technical specifications.

---

## 📋 Table of Contents
1. [Core Features](#core-features)
2. [User Interface](#user-interface)
3. [Maps Integration](#maps-integration)
4. [Prayer Time Management](#prayer-time-management)
5. [Restaurant Discovery](#restaurant-discovery)
6. [Ride Sharing](#ride-sharing)
7. [Notifications](#notifications)
8. [Search & Discovery](#search--discovery)
9. [Data Management](#data-management)
10. [Settings & Customization](#settings--customization)
11. [Technical Specifications](#technical-specifications)
12. [API Integrations](#api-integrations)

---

## 🎯 Core Features

### 1. Main Map Interface
- **Interactive Google Maps Display**
  - Real-time user location (blue dot)
  - Custom markers for mosques and restaurants
  - Zoom levels: 1x to 20x
  - Pan and rotate functionality
  - Hybrid and satellite view options
  - Clustering for dense marker areas
  
- **Map Controls**
  - Zoom in/out buttons
  - Center on location button
  - Compass indicator
  - Current location indicator

### 2. Location Services
- **GPS Location Tracking**
  - Fine location accuracy (±5 meters)
  - Coarse location fallback
  - Automatic location updates every 10 seconds
  - Battery optimization: balanced power mode
  - Location caching for offline use

- **Address Resolution**
  - Geocoding (coordinates to address)
  - Reverse geocoding (address to coordinates)
  - Display full address on map markers
  - Address suggestions in search

### 3. Search Functionality
- **Multi-Type Search**
  - Search by place name
  - Search by category (mosque, restaurant, etc.)
  - Search by cuisine type
  - Address/location search

- **Search Features**
  - Real-time suggestions as user types
  - Search history saved locally
  - Previous searches appear as suggestions
  - One-tap to re-search previous queries
  - Search results sorted by relevance and distance

- **Advanced Search**
  - Filter by distance radius
  - Filter by rating
  - Filter by open/closed status
  - Save search filters

---

## 🎨 User Interface

### Screens & Activities

#### 1. Splash Screen (SplashActivity)
- **Duration**: 2.2 seconds
- **Features**:
  - Logo animation (fade-in)
  - App name animation (slide-up)
  - Full-screen immersive mode
  - Smooth transition to main screen
  
- **Customization**:
  - Adjustable duration in code
  - Custom logo/branding support
  - Theme-aware styling

#### 2. Main Activity
- **Components**:
  - Full-screen Google Map
  - Search bar at top
  - Floating action buttons
  - Navigation menu button
  - Zoom controls
  - Current location button

- **Interactions**:
  - Tap map to dismiss keyboard
  - Long-press to select location
  - Swipe for menu
  - Pinch to zoom
  - Double-tap to zoom in

#### 3. Search Activity (SearchActivity)
- **Layout**:
  - Full-width search bar
  - Search suggestions dropdown
  - Search results list
  - Map view of results
  - Result filters panel

- **Features**:
  - Real-time result updates
  - Result pagination
  - Sort options (distance, rating, name)
  - View/hide on map toggle

#### 4. Navigation Activity
- **Menu Options**:
  - Prayer Times
  - Restaurants
  - Rides
  - Favorites
  - Settings
  - About & Support

- **Menu Features**:
  - Hamburger menu icon
  - Slide-out panel
  - Icons with labels
  - Navigation history

#### 5. Restaurant Map Activity
- **Features**:
  - Dedicated restaurant map
  - Filter by cuisine
  - Filter by rating
  - List/Map view toggle
  - Restaurant clustering

#### 6. Ride History Activity
- **Displays**:
  - List of past rides
  - Ride details (time, location, status)
  - Passenger count
  - Ride status indicator
  - Share ride functionality

#### 7. Side Menu Activity
- **Options**:
  - Quick access to main features
  - User profile section
  - Settings shortcuts
  - About information
  - Version information

#### 8. Map Selection Activity
- **Purpose**: Selecting pickup/dropoff locations
- **Features**:
  - Map with placeable marker
  - Search integration
  - Confirmation dialog
  - Selected location display

#### 9. Adhan Notification Activity
- **Shows**:
  - Prayer name
  - Prayer time
  - Next prayer countdown
  - Prayer duration
  - Upcoming prayers for the day

### UI Components

#### Buttons & Controls
- **Floating Action Buttons (FAB)**
  - Primary action button (search)
  - Secondary actions (filter, etc.)
  - Animated show/hide
  - Customizable colors

- **Custom Buttons**
  - Rounded buttons with gradient
  - Blue background primary color
  - White text
  - Ripple effect on tap
  - Disabled state styling

#### Text Input
- **Search Bar**
  - Character limit: 100
  - Auto-suggest while typing
  - Clear button (X icon)
  - Voice input ready
  - Keyboard optimization

- **Text Fields**
  - Input validation
  - Error message display
  - Focus highlights
  - Cursor positioning

#### Dialogs & Sheets
- **Bottom Sheet**
  - Restaurant details display
  - Smooth drag-down animation
  - Back button handling
  - Expandable/collapsible

- **Confirmation Dialogs**
  - Delete confirmation
  - Permission request dialogs
  - Setting change confirmation

#### Animations
- **Fade In**: Logo and text animations
- **Slide Up**: Text slide from bottom
- **Bounce**: Button click effect
- **Fade Out**: Screen transition
- **Rotation**: Loading spinner
- **Translation**: Menu slide-out

---

## 📍 Maps Integration

### Google Maps Features
- **Map Types**
  - Normal (default)
  - Satellite
  - Terrain
  - Hybrid

- **Layers**
  - Traffic layer (toggle-able)
  - Transit layer (toggle-able)
  - Building layer (automatic)

- **Custom Styling**
  - Custom marker icons
  - Color-coded markers (mosque, restaurant, etc.)
  - Marker clustering
  - Animated marker drops

- **User Location**
  - Blue dot indicator
  - Location accuracy circle
  - Updates automatically
  - Caching when offline

### Marker Management
- **Marker Types**:
  - Mosque markers (green)
  - Restaurant markers (red)
  - Pickup location (blue)
  - Dropoff location (orange)
  - User location (blue dot)

- **Marker Interactions**:
  - Tap marker for details
  - Info window with title/address
  - Custom info window layout
  - Long-press to select
  - Drag to reposition (some markers)

- **Marker Clustering**
  - Automatic clustering at zoom < 12
  - Cluster count display
  - Expand cluster on tap
  - Smooth cluster animation

### Camera Control
- **Zoom Levels**: 1x (world) to 20x (street)
- **Pan**: Smooth dragging across map
- **Rotate**: Two-finger rotation
- **Tilt**: Two-finger vertical drag
- **Auto-center**: Button to center on user

---

## 🕌 Prayer Time Management

### Prayer Time Calculation
- **Calculation Methods**:
  - Islamic Society of North America (ISNA)
  - Muslim World League (MWL)
  - University of Islamic Sciences Karachi (UISK)
  - Umm al-Qura University
  - Egyptian General Authority of Survey

- **Prayer Times Calculated**:
  - **Fajr**: Pre-dawn prayer
  - **Sunrise**: Sun's upper limb appears
  - **Dhuhr**: Midday prayer
  - **Asr**: Afternoon prayer
  - **Maghrib**: Sunset prayer
  - **Isha**: Night prayer

- **Calculation Parameters**:
  - User's latitude & longitude
  - Date (Gregorian calendar)
  - Timezone
  - Altitude above sea level
  - Atmospheric refraction

### Prayer Notifications
- **Types of Alerts**:
  - Adhan (audio call to prayer)
  - Notification badge
  - Lock screen notification
  - Vibration pattern

- **Notification Timing**:
  - Triggered at exact prayer time
  - Customizable advance reminder (5-30 min)
  - Multiple reminders optional
  - Silent mode respect

- **Notification Content**:
  - Prayer name
  - Exact time
  - Next prayer time
  - Remaining time to next prayer

### Prayer Time Management
- **Display Options**:
  - Daily view (all prayers in one screen)
  - Weekly view
  - Monthly view
  - Upcoming prayers list

- **Features**:
  - Prayer duration display
  - Time until next prayer
  - Current prayer highlight
  - Hijri date conversion
  - Prayer recommendation notes

---

## 🍽️ Restaurant Discovery

### Restaurant Information
- **Data Displayed**:
  - Restaurant name
  - Cuisine type
  - Rating (1-5 stars)
  - Review count
  - Address
  - Phone number
  - Website (if available)
  - Operating hours
  - Price level ($-$$$$)

- **Additional Details**:
  - Photos gallery
  - Full address
  - Directions
  - User reviews
  - Facility icons (WiFi, parking, etc.)

### Restaurant Search
- **Search Methods**:
  - By name
  - By cuisine type
  - By location
  - By radius (1km - 50km)
  - By rating (filter minimum)
  - By price level

- **Search Results**:
  - List view with thumbnails
  - Distance to restaurant
  - Rating display
  - Quick-tap to call
  - Quick-tap to directions

### Restaurant Management
- **Favorites**:
  - Save restaurants to favorites
  - Access from menu
  - Quick add/remove
  - Favorites sync (optional)

- **Recently Viewed**:
  - Automatic history
  - Quick re-access
  - Clear history option
  - Search history

### Directions & Navigation
- **Integration**:
  - Google Maps directions
  - Apple Maps (iOS compatible)
  - Waze integration (if installed)
  - Phone number to call directly
  - Address to copy/share

---

## 🚗 Ride Sharing (Iftar Rides)

### Ride Creation
- **Ride Details**:
  - Driver name
  - Pickup location (map-selectable)
  - Dropoff location (map-selectable)
  - Departure time
  - Number of seats available
  - Vehicle description
  - Special requirements/notes

- **Validation**:
  - Both locations required
  - Time in future only
  - At least 1 seat available
  - Driver information complete

### Ride Browsing
- **Display Information**:
  - Driver name
  - Vehicle type
  - Seats available
  - Departure time
  - Route on map
  - Driver rating/reviews
  - Estimated time to destination

- **Filtering Options**:
  - By date
  - By time range
  - By departure location
  - By destination
  - Available seats only

### Ride Joining
- **Process**:
  1. View ride details
  2. Confirm ride matches needs
  3. Tap "Join Ride"
  4. Confirm passenger details
  5. View confirmation

- **Confirmation**:
  - Ride ID/code
  - Driver contact
  - Pickup time
  - Location details
  - Passenger count

### Ride Management
- **Features**:
  - View all active rides
  - Cancel ride (before departure)
  - Share ride with others
  - Rate driver post-ride
  - Report issues
  - View ride history

### Ride History
- **Data Tracked**:
  - All completed rides
  - Driver ratings
  - Time saved
  - Total distances
  - Monthly statistics

- **History Features**:
  - View past ride details
  - Driver contact info
  - Route map
  - Re-book same route
  - Delete history entries

---

## 🔔 Notifications

### Notification Types

#### 1. Prayer Time Notifications
- **Trigger**: At exact prayer time
- **Content**: Prayer name, time, next prayer
- **Sound**: Customizable adhan/tone
- **Vibration**: Pattern customizable
- **Action**: Tap to open prayer details

#### 2. Restaurant Notifications
- **Trigger**: When nearby restaurants open
- **Content**: Restaurant name, specials
- **Action**: Tap to view details
- **Frequency**: Once per restaurant per day

#### 3. Ride Notifications
- **Trigger**: New rides available in area
- **Trigger**: Ride status changes
- **Content**: Ride details, updated status
- **Action**: Tap to view full ride

#### 4. System Notifications
- **App Updates**: New version available
- **Feature Updates**: New features released
- **Maintenance**: Service notifications

### Notification Settings
- **Enable/Disable By Type**:
  - Prayer notifications toggle
  - Restaurant notifications toggle
  - Ride notifications toggle

- **Customization**:
  - Notification sound (silent/custom/default)
  - Vibration pattern
  - LED color
  - Notification time advance (5-30 min)
  - Do Not Disturb integration

- **Permissions**:
  - POST_NOTIFICATIONS permission required (Android 13+)
  - Revoke anytime in system settings
  - Request flow on first notification

### Persistent Notifications
- **Behavior After Reboot**:
  - BootReceiver triggers
  - All alarms rescheduled
  - Notifications resume automatically
  - No manual reset needed

---

## 🔍 Search & Discovery

### Search Types

#### 1. Quick Search
- **Bar Location**: Top of main screen
- **Trigger**: Type in search bar
- **Results**: Real-time suggestions
- **Actions**: Tap to open, pin to map

#### 2. Advanced Search
- **Access**: From menu → Search
- **Filters**:
  - Distance radius
  - Open/closed status
  - Rating minimum
  - Price level
  - Cuisine/category

- **Results Display**:
  - List with images
  - Map pins
  - Distance shown
  - Quick action buttons

#### 3. Voice Search
- **Support**: Ready for implementation
- **Trigger**: Mic icon in search bar
- **Process**: Speak query, transcribed to text
- **Languages**: English, Arabic, etc.

### Suggestions & Auto-Complete
- **Real-Time Suggestions**:
  - As user types
  - Based on previous searches
  - Based on common searches
  - Based on nearby places

- **History**:
  - Last 20 searches saved
  - Tap to re-search
  - Clear history option
  - Auto-clear after 30 days (optional)

### Trending & Popular
- **Trending Searches**:
  - Top searches in area
  - Seasonal/special (Ramadan focus)
  - New places opened

- **Popular Places**:
  - Most visited
  - Highest rated
  - Recently reviewed
  - Near user location

---

## 💾 Data Management

### Local Storage
- **SQLite Database**:
  - Restaurants (cached list)
  - Prayer times (calculated daily)
  - Favorites (user selection)
  - Search history (recent searches)
  - Rides (personal rides)
  - User settings

- **SharedPreferences**:
  - User preferences
  - App settings
  - Last known location
  - UI state (selected tabs)

### Data Synchronization
- **Automatic Updates**:
  - Prayer times: Daily recalculation
  - Restaurant cache: Every 6 hours
  - User location: Every 10 seconds (when app active)

- **Manual Refresh**:
  - Pull-to-refresh gesture
  - Refresh button in menu
  - Tap location button

### Data Backup
- **Automatic Backup**:
  - Favorites to device
  - Search history backup
  - Settings backup
  - Android Backup Service integration

- **Manual Backup**:
  - Export to file
  - Share favorites list
  - Cloud backup (optional)

### Data Deletion
- **User Controls**:
  - Clear search history
  - Delete all favorites
  - Reset settings
  - Full app data wipe

- **Privacy**:
  - No cloud storage by default
  - Local data only
  - User controls data lifetime
  - Delete anytime

---

## ⚙️ Settings & Customization

### Appearance Settings
- **Theme**:
  - Light mode
  - Dark mode
  - Auto (system preference)

- **Text Size**:
  - Small, Normal, Large, Extra Large
  - Applies to all text

- **Map Style**:
  - Normal, Satellite, Hybrid, Terrain
  - Save preference

### Prayer Settings
- **Calculation Method**:
  - Select from 5+ methods
  - Default: ISNA

- **Prayer Notifications**:
  - Enable/disable each prayer
  - Sound selection
  - Vibration toggle
  - Advance reminder time

- **Timezone**:
  - Auto-detect (GPS-based)
  - Manual selection
  - DST handling

### Location Settings
- **Default Location**:
  - Auto-detect via GPS
  - Manual entry
  - Saved home/work locations

- **Location Accuracy**:
  - High accuracy
  - Battery saving
  - Device sensors only

### Language & Region
- **Language**:
  - English
  - Arabic
  - More languages (future)

- **Region**:
  - Affects prayer calculation
  - Affects date format
  - Affects time format

### Notification Settings
- **Global Notifications**:
  - Master enable/disable
  - Sound vs silent
  - Vibration

- **Per-Type Settings**:
  - Prayer notifications
  - Restaurant notifications
  - Ride notifications
  - System notifications

### Privacy Settings
- **Permissions Management**:
  - View all permissions
  - Enable/disable individually
  - Explanation for each

- **Data Usage**:
  - Track data usage
  - Limit background data
  - Offline mode

- **Crash Reporting**:
  - Opt-in/out
  - Firebase Crashlytics
  - Anonymous error reports

---

## 🔧 Technical Specifications

### System Requirements
- **Minimum Android Version**: API 24 (Android 7.0)
- **Target Android Version**: API 35 (Android 15)
- **Compile Android Version**: API 35

### Performance
- **App Size**: ~50-80 MB
- **RAM Requirements**: 50-100 MB
- **Storage**: 10 MB for app + cache
- **Startup Time**: < 3 seconds

### Connectivity
- **Network**: Requires internet for API calls
- **Offline Mode**: Prayer times work offline
- **Data Usage**: ~5-10 MB per month average
- **Sync Frequency**: Configurable

### Battery & Power
- **Battery Optimization**:
  - Balanced location accuracy
  - Background sync minimal
  - Battery saver compatible
  - Doze mode compatible

- **Locations Update**:
  - Interval: 10 seconds
  - Fastest: 5 seconds
  - Priority: Balanced power accuracy

### Security
- **Permissions**:
  - ACCESS_FINE_LOCATION
  - ACCESS_COARSE_LOCATION
  - INTERNET
  - POST_NOTIFICATIONS
  - SCHEDULE_EXACT_ALARM

- **Data Encryption**:
  - API calls via HTTPS
  - Local data unencrypted (optional encryption)
  - Credentials not stored locally

- **Privacy**:
  - No analytics by default
  - Optional crash reporting
  - No third-party tracking
  - User controls all data

---

## 🌐 API Integrations

### Google Maps API
- **Services Used**:
  - Maps SDK for Android
  - Directions API
  - Geocoding API
  - Place Details API

- **Features Enabled**:
  - Map rendering
  - Marker placement
  - Camera controls
  - Tile downloading

- **Quota**: 25,000 maps loads/day (free tier)

### Google Places API
- **Services Used**:
  - Nearby Search
  - Place Details
  - Place Photos
  - Autocomplete

- **Features Enabled**:
  - Restaurant search
  - Place details
  - Photos display
  - Autocomplete suggestions

- **Quota**: 
  - 150 requests per second
  - Text search: 1000/day free

### Google Location Services
- **Services**:
  - Fused Location Provider
  - Geofencing (ready for implementation)
  - Activity Recognition (ready)

### Firebase Services (Optional)
- **Crashlytics**: Error reporting
- **Analytics**: User behavior (optional)
- **Remote Config**: Feature flags (ready)

---

## 📈 Feature Roadmap

### Current Release (v1.0)
- ✅ Prayer time calculations
- ✅ Prayer notifications
- ✅ Restaurant discovery
- ✅ Ride sharing (basic)
- ✅ Map interface
- ✅ Search functionality
- ✅ Favorites management

### Future Releases
- 🔜 User accounts & cloud sync
- 🔜 Social features (ratings, reviews)
- 🔜 Advanced ride features
- 🔜 Mosque directories with details
- 🔜 Event calendar
- 🔜 Quran integration
- 🔜 Language support (10+ languages)
- 🔜 Widget support
- 🔜 Smartwatch app
- 🔜 API for third-party integration

---

## 🎓 Version History

| Version | Release Date | New Features | Fixes |
|---------|-------------|--------------|-------|
| 1.0 | December 2024 | Initial release | N/A |

---

**Last Updated**: December 2024

For feature requests or bug reports, visit the GitHub Issues page.
