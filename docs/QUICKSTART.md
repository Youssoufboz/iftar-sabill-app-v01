# Sabil 23 - Quick Start Guide

Get up and running with Sabil 23 in minutes!

---

## ⚡ 5-Minute Setup

### 1. Prerequisites
```bash
# Verify Java version
java -version
# Should be 11 or higher

# Verify Android SDK
echo $ANDROID_HOME
# Should point to Android SDK directory
```

### 2. Clone & Build
```bash
git clone https://github.com/yourusername/sabil23.git
cd sabil23
./gradlew build
```

### 3. Add API Keys
Edit `app/src/main/res/values/strings.xml`:
```xml
<string name="google_maps_key">YOUR_API_KEY_HERE</string>
<string name="google_places_key">YOUR_API_KEY_HERE</string>
```

### 4. Run
```bash
./gradlew installDebug
```

---

## 🎯 First Time Users

### Opening the App
1. App launches with **Splash Screen** (2.2 seconds)
2. Shows **Main Activity** with interactive Google Map
3. Your current location appears automatically (if permissions granted)

### Granting Permissions
On first launch:
- **Location**: Required for map and restaurant search
- **Notifications**: Required for prayer time alerts
- **Tap "Allow"** for each permission

### Main Features Overview

#### 🗺️ Map View (Main Activity)
- See your location (blue dot)
- Search nearby mosques and restaurants
- Tap markers for details
- Zoom and pan freely

#### 🔍 Search (Search Button)
1. Tap search icon
2. Type restaurant name or type
3. See suggestions appear
4. Tap to view on map

#### 📍 Prayer Times
- Automatic notifications at each prayer time
- Shows prayer name on notification
- Tap to see detailed prayer information

#### 🍽️ Restaurants
- Find halal restaurants nearby
- View ratings and details
- Call directly from app
- Navigate using map

---

## 🔧 Common Tasks

### How to Find Nearby Restaurants

**Method 1: From Map**
1. Open Main Activity
2. Tap search/filter icon
3. Select "Restaurants"
4. View results on map

**Method 2: From Search**
1. Tap search icon
2. Type "restaurants" or specific name
3. Tap result to view on map

### How to Get Prayer Times

**Automatic**:
- Prayer times calculated automatically
- Notifications sent at each prayer time
- Based on your location

**Manual**:
1. Open Navigation menu
2. Tap "Prayer Times"
3. View today's schedule
4. Scroll for future dates

### How to Share a Ride (Iftar)

1. Open Navigation menu
2. Tap "Rides"
3. Tap "New Ride" or view existing
4. Enter details:
   - Pickup location
   - Dropoff location
   - Time
5. Share code with others
6. Track participants

### How to Save Favorites

1. Find restaurant on map
2. Tap marker/result
3. Tap star icon
4. Appears in Favorites

---

## 📱 UI Navigation

```
Splash Screen (2.2 sec)
         ↓
   Main Activity (Map)
    ↙         ↘
 Search      Menu
  Activity   Activity
   ↓          ↓
Results    Navigation Hub
           ↙    ↓    ↘
       Prayer  Rides  Settings
       Times
```

### Bottom Menu
- 🏠 Home - Returns to map
- 🔍 Search - Global search
- 📍 Locations - Saved locations
- ⚙️ Settings - App preferences

### Side Menu
- 🙏 Prayer Times
- 🍽️ Restaurants
- 🚗 Rides
- ❤️ Favorites
- ⚙️ Settings
- ℹ️ About

---

## 🐛 Troubleshooting

### App won't start
**Issue**: Crash on launch
**Solution**:
```bash
# Clear app data
adb shell pm clear com.ramadan.sabil23

# Reinstall
./gradlew installDebug

# Check logs
adb logcat | grep "sabil23"
```

### Map not showing
**Issue**: Blank white map
**Solution**:
- Verify API key in strings.xml
- Check API is enabled in Google Cloud Console
- Ensure location permission granted
- Restart app

### Prayer times wrong
**Issue**: Times don't match prayer times in mosque
**Solution**:
- Verify location is correct
- Check device timezone settings
- Ensure time sync is enabled
- Try different calculation method in settings

### Restaurants not found
**Issue**: No results when searching
**Solution**:
- Check location permission is granted
- Verify search radius is appropriate
- Try broader search term
- Check internet connection

---

## 📊 App Data

### Automatically Stored
- ✅ Prayer times (local database)
- ✅ Search history
- ✅ Favorite restaurants
- ✅ Ride history
- ✅ User settings

### Cached Data
- 🔄 Restaurant list (refreshes hourly)
- 🔄 Map tiles (downloaded when needed)
- 🔄 Search suggestions

### Privacy
- ❌ No data sent to external servers (except API calls)
- ❌ Location only used for feature functionality
- ✅ Full offline mode for prayer times
- ✅ Delete data anytime in Settings

---

## ⌨️ Keyboard Shortcuts (Android Studio)

When developing/debugging:

| Shortcut | Action |
|----------|--------|
| Shift + F10 | Run app |
| Ctrl + Shift + F10 | Debug app |
| Alt + Enter | Quick fix |
| Ctrl + Alt + L | Format code |
| Ctrl + / | Toggle comment |
| Ctrl + Shift + O | Optimize imports |

---

## 📖 Learning Path

### For New Users
1. Read this Quick Start
2. Grant permissions when prompted
3. Explore main map interface
4. Try searching for restaurants
5. Check prayer times

### For New Developers
1. Read [README.md](README.md) for overview
2. Read [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) for architecture
3. Explore source code structure
4. Run unit tests: `./gradlew test`
5. Debug on emulator or device

### For Advanced Developers
1. Study [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) in detail
2. Review [FEATURES.md](FEATURES.md) for full capability list
3. Explore Google Maps and Places API docs
4. Optimize performance using Android Profiler
5. Contribute new features

---

## 🆘 Getting Help

### Documentation
- [README.md](README.md) - Project overview
- [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) - Technical deep-dive
- [FEATURES.md](FEATURES.md) - Complete feature list
- This file - Quick start guide

### External Resources
- [Android Developers](https://developer.android.com)
- [Google Maps API](https://developers.google.com/maps)
- [Google Places API](https://developers.google.com/maps/documentation/places)

### Report Issues
1. Check existing issues on GitHub
2. Provide:
   - Android version
   - Device model
   - Error message from logcat
   - Steps to reproduce
3. Create new issue with details

---

## 🎉 Next Steps

**Now that you're up and running:**

1. **Explore Features** - Use each major feature
2. **Customize Settings** - Adjust preferences
3. **Add Favorites** - Save important locations
4. **Enable Notifications** - Get prayer alerts
5. **Share with Others** - Use ride sharing feature

**To start developing:**

1. Open [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)
2. Set up development environment
3. Make first code change
4. Submit pull request

---

**Happy coding! 🚀**

Last updated: December 2024
