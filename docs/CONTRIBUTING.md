# Contributing to Sabil 23

Thank you for your interest in contributing to Sabil 23! This document provides guidelines and instructions for contributing to the project.

---

## 📋 Table of Contents

1. [Getting Started](#getting-started)
2. [Code of Conduct](#code-of-conduct)
3. [How to Contribute](#how-to-contribute)
4. [Development Workflow](#development-workflow)
5. [Code Style Guide](#code-style-guide)
6. [Git Workflow](#git-workflow)
7. [Testing Requirements](#testing-requirements)
8. [Documentation Standards](#documentation-standards)
9. [Pull Request Process](#pull-request-process)
10. [Community](#community)

---

## 🚀 Getting Started

### Prerequisites
- JDK 11 or higher
- Android Studio 2023.2 or newer
- Android SDK (API 35)
- Git knowledge (basic)
- Google API keys for testing

### Initial Setup
```bash
# 1. Fork the repository on GitHub
# 2. Clone your fork
git clone https://github.com/yourusername/sabil23.git
cd sabil23

# 3. Add upstream remote
git remote add upstream https://github.com/original-owner/sabil23.git

# 4. Create local.properties
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# 5. Configure API keys
# Edit app/src/main/res/values/strings.xml with your keys

# 6. Sync Gradle and build
./gradlew sync
./gradlew build
```

---

## 📜 Code of Conduct

### Our Commitment
We are committed to providing a welcoming and inspiring community for all. Please read and adhere to our Code of Conduct:

- **Be Respectful**: Respect differing opinions and backgrounds
- **Be Inclusive**: Welcome newcomers and support their growth
- **Be Honest**: Provide honest feedback constructively
- **Be Professional**: Maintain professional communication
- **Be Safe**: Report harmful behavior to maintainers

### Unacceptable Behavior
- Harassment, discrimination, or offensive language
- Trolling or insulting comments
- Publishing private information
- Unwelcome sexual advances
- Any other conduct harmful to the community

### Reporting Issues
Email concerns to: maintainers@sabil23.com

---

## 🤝 How to Contribute

### Types of Contributions

#### 1. **Bug Reports**
Report bugs by creating a GitHub issue with:
- Clear title describing the bug
- Detailed description of the problem
- Steps to reproduce
- Expected vs actual behavior
- Android version and device
- Screenshots/logs if applicable

**Example Issue Title**: "Prayer notifications not triggering on Android 14"

#### 2. **Feature Requests**
Suggest features by opening an issue with:
- Clear, descriptive title
- Problem statement (what problem does it solve?)
- Proposed solution
- Alternative approaches considered
- Why this feature is important to you

**Example Issue Title**: "Add support for multiple mosques locations"

#### 3. **Code Improvements**
- Performance optimizations
- Code refactoring
- Bug fixes
- Test additions
- Documentation improvements

#### 4. **Documentation**
- Update README or guides
- Add code comments
- Create tutorials
- Improve existing documentation
- Fix typos and grammar

#### 5. **Testing**
- Write unit tests
- Write instrumented tests
- Test on various devices
- Report edge cases
- Suggest test improvements

---

## 💻 Development Workflow

### Setting Up Development Environment

#### 1. Android Studio Configuration
```
Settings → Code Style → Java
  ├─ Scheme: "Project"
  ├─ Spacing: Google style (4 spaces)
  ├─ Imports: Organize by package
  └─ Line length: 120 characters

Settings → Editor → Inspections
  └─ Enable all Android lint checks
```

#### 2. Build Configuration
```bash
# Build and run tests
./gradlew clean build

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run specific test
./gradlew test --tests *PrayerTimesCalculatorTest
```

#### 3. Code Analysis
```bash
# Run lint checks
./gradlew lint

# Generate lint report
./gradlew lintDebug
```

### Creating a Feature Branch

```bash
# Update main branch
git checkout main
git pull upstream main

# Create feature branch (use descriptive name)
git checkout -b feature/add-user-authentication
# or for bugs
git checkout -b fix/prayer-notification-crash
# or for documentation
git checkout -b docs/update-api-guide
```

### Branch Naming Convention
- **Features**: `feature/feature-name`
- **Bugs**: `fix/bug-name`
- **Docs**: `docs/documentation-name`
- **Tests**: `test/test-name`
- **Refactor**: `refactor/component-name`
- **Chore**: `chore/task-name`

---

## 📝 Code Style Guide

### Java Code Style

#### Class and Method Structure
```java
// 1. Package declaration
package com.ramadan.sabil23;

// 2. Imports (organized by group)
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

// 3. Class declaration
public class ExampleActivity extends AppCompatActivity {
    
    // 4. Constants (in UPPER_CASE)
    private static final String TAG = "ExampleActivity";
    private static final int DEFAULT_TIMEOUT = 5000;
    
    // 5. Class variables
    private GoogleMap mGoogleMap;
    private RestaurantManager restaurantManager;
    
    // 6. Constructors
    public ExampleActivity() {
        // Default constructor
    }
    
    // 7. Lifecycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
    }
    
    // 8. Public methods
    public void loadRestaurants() {
        // Implementation
    }
    
    // 9. Private methods
    private void setupMap() {
        // Implementation
    }
}
```

#### Naming Conventions
```java
// Constants
public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
private static final float DEFAULT_ZOOM = 15f;

// Variables
private String userName;              // Private field
private GoogleMap mGoogleMap;         // Private with prefix 'm'
public String restaurantName;         // Public (rare)

// Methods
public void loadRestaurants() { }      // camelCase
public String getRestaurantName() { }  // getters/setters
public boolean isRestaurantOpen() { }  // boolean prefix 'is'

// Listeners
private View.OnClickListener clickListener;
private GoogleMap.OnMarkerClickListener markerClickListener;
```

#### Code Formatting
```java
// Line length: Max 120 characters
// Indentation: 4 spaces (not tabs)
// Braces: Opening brace on same line (Android style)

// Good
if (location != null) {
    // Process location
}

// Bad
if (location != null)
{
    // Process location
}

// Comments
// Single line comment for brief explanation

/*
 * Multi-line comment for detailed explanation
 * Use for complex logic or important notes
 */

/** 
 * Javadoc for public methods
 * @param location User's current location
 * @return List of nearby restaurants
 */
public List<Restaurant> getNearbyRestaurants(LatLng location) {
    // Implementation
}
```

#### Best Practices
```java
// 1. Use meaningful variable names
// Good
List<Restaurant> restaurantList = apiService.search(location);

// Bad
List<Restaurant> list = service.search(l);

// 2. Keep methods focused and small (< 30 lines ideal)
// Good
private void handleLocationPermission() {
    if (hasPermission()) {
        startLocationUpdates();
    } else {
        requestPermission();
    }
}

// 3. Use constants instead of magic numbers
private static final int SEARCH_RADIUS = 5000; // 5km

// 4. Handle null cases
if (restaurant != null && !restaurant.getName().isEmpty()) {
    displayRestaurant(restaurant);
}

// 5. Use meaningful exception handling
try {
    // API call
} catch (IOException e) {
    Log.e(TAG, "Failed to fetch restaurants", e);
    handleNetworkError();
}
```

### Resource Naming

#### Layouts
```xml
<!-- Activity layouts: activity_<name>.xml -->
activity_main.xml
activity_search.xml
activity_restaurant_map.xml

<!-- Fragment layouts: fragment_<name>.xml -->
fragment_prayer_times.xml

<!-- List item layouts: item_<name>.xml -->
item_restaurant.xml

<!-- Custom view layouts: view_<name>.xml -->
view_restaurant_details.xml
```

#### Drawables
```
<!-- Icons: ic_<name>.xml -->
ic_location.xml
ic_search.xml
ic_menu.xml

<!-- Backgrounds: bg_<name>.xml or <name>_background.xml -->
bg_button_blue.xml
button_background.xml

<!-- Selectors: <name>_selector.xml -->
button_selector.xml
```

#### Values
```
colors.xml          <!-- Color definitions -->
dimens.xml          <!-- Dimension constants -->
strings.xml         <!-- UI text strings -->
styles.xml          <!-- Theme and style definitions -->
attrs.xml           <!-- Custom attributes -->
```

---

## 🔄 Git Workflow

### Commit Messages

#### Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

#### Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Test additions/changes
- `chore`: Build, CI/CD, dependency updates

#### Examples
```bash
# Good commits
git commit -m "feat(search): add voice search capability"
git commit -m "fix(notifications): resolve prayer alarm not triggering on Android 14"
git commit -m "docs(readme): update installation instructions"
git commit -m "test(prayer-times): add unit tests for Ramadan calculations"
git commit -m "refactor(database): simplify restaurant query logic"

# Bad commits
git commit -m "fixed stuff"
git commit -m "update"
git commit -m "WIP"
```

#### Detailed Commit Message Example
```bash
git commit -m "feat(ride-sharing): add ride history display

- Display list of user's previous rides
- Show ride details (driver, time, location)
- Add ability to re-book same route
- Implement ride rating system

Closes #123
```

### Keeping Fork Updated
```bash
# Fetch latest changes from upstream
git fetch upstream main

# Rebase your branch on upstream
git rebase upstream/main

# If conflicts occur, resolve them then:
git add .
git rebase --continue

# Force push to your fork (only your branch!)
git push origin feature/your-feature --force
```

### Working with Branches

```bash
# Create and checkout branch
git checkout -b feature/new-feature

# Make changes and commit
git add .
git commit -m "feat: add new feature"

# Push to your fork
git push origin feature/new-feature

# Create Pull Request on GitHub
# Then delete local branch when merged
git branch -d feature/new-feature
git push origin --delete feature/new-feature
```

---

## 🧪 Testing Requirements

### Unit Tests
Location: `app/src/test/java/`

Every feature should have unit tests covering:
- Normal cases
- Edge cases
- Error handling

```bash
./gradlew test
```

Example test:
```java
@RunWith(AndroidJUnit4.class)
public class PrayerTimesCalculatorTest {
    
    private PrayerTimesCalculator calculator;
    
    @Before
    public void setUp() {
        calculator = new PrayerTimesCalculator();
    }
    
    @Test
    public void testDhuhrCalculation() {
        Calendar date = Calendar.getInstance();
        Calendar dhuhr = calculator.calculateDhuhrTime(
            date, 31.9454, 35.9284);
        assertNotNull(dhuhr);
    }
}
```

### Instrumented Tests
Location: `app/src/androidTest/java/`

For Android-dependent code:
```bash
./gradlew connectedAndroidTest
```

### Test Coverage
- Target: 70%+ code coverage
- Critical features: 90%+ coverage
- Use: `./gradlew testDebugUnitTestCoverage`

### Manual Testing
- Test on minimum SDK (API 24)
- Test on target SDK (API 35)
- Test on various devices if possible
- Test offline functionality
- Test permission requests

---

## 📚 Documentation Standards

### Code Comments
```java
// Use javadoc for public methods
/**
 * Fetches nearby restaurants from Google Places API.
 * 
 * @param location Current user location (LatLng)
 * @param radius Search radius in meters
 * @param callback Callback with results
 * @throws IOException if API call fails
 */
public void fetchNearbyRestaurants(LatLng location, int radius, 
                                   Callback<List<Restaurant>> callback) 
        throws IOException {
    // Implementation
}

// Use brief comments for complex logic
// Cluster markers at zoom level < 12 for performance
if (currentZoom < 12) {
    clusterManager.cluster();
}
```

### README Updates
When contributing new features:
1. Update README.md Features section
2. Document new permissions if added
3. Update architecture section if changed
4. Add to API integration section if using new API

### FEATURES.md Updates
Keep FEATURES.md current with:
- New features added
- Feature modifications
- Removed features (mark as deprecated)
- Updated specifications

### Commit Documentation
Every commit should:
- Have clear message
- Reference issues (#123)
- Explain "why" not just "what"
- Include breaking changes in footer

---

## 🔀 Pull Request Process

### Before Creating PR
- [ ] Fork repository
- [ ] Create feature branch
- [ ] Make changes following code style
- [ ] Write/update tests
- [ ] Update documentation
- [ ] Run all tests locally
- [ ] Rebase on main branch
- [ ] No merge conflicts

### Creating Pull Request
1. Go to your fork on GitHub
2. Click "Compare & pull request"
3. Fill in PR template:

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update

## Related Issues
Closes #123

## Testing
Describe how you tested:
- [ ] Unit tests added/updated
- [ ] Tested on Android X
- [ ] Manual testing completed

## Screenshots (if applicable)
Add screenshots showing UI changes

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-reviewed code
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] Tests pass
- [ ] No new warnings
```

### PR Review Process
1. At least 2 maintainer approvals required
2. All checks must pass (CI/CD)
3. Code coverage shouldn't decrease
4. Address reviewer comments

### After PR Merge
1. Delete feature branch
2. Pull latest main locally
3. Update CHANGELOG if appropriate

---

## 🤖 Continuous Integration

### Automated Checks
All PRs undergo:
- ✅ Build check (`./gradlew build`)
- ✅ Unit tests (`./gradlew test`)
- ✅ Lint checks (`./gradlew lint`)
- ✅ Code coverage analysis
- ✅ Security scans

### View CI Status
- GitHub Actions tab shows status
- Must pass before merge
- Can request re-run if infrastructure issue

---

## 🆘 Getting Help

### Questions About Contributing?
- Read this guide completely
- Check existing issues/PRs for similar questions
- Ask in GitHub issue with `question` label
- Reach out to maintainers

### Need Development Help?
- Check [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)
- Review existing code for patterns
- Ask in PR discussion
- Comment on related issues

### Discord Community
- Join our Discord server for chat
- Ask questions in #development channel
- Share ideas in #feature-requests
- Discuss code in #code-review

---

## 🎯 Good First Issues

Looking for where to start? Check for issues labeled:
- `good-first-issue` - Good for newcomers
- `help-wanted` - Could use assistance
- `documentation` - Doc improvements
- `bug` - Bug fixes

---

## 📝 Example Contribution Workflow

```bash
# 1. Fork and clone
git clone https://github.com/yourusername/sabil23.git
cd sabil23

# 2. Create feature branch
git checkout -b feature/add-search-filters

# 3. Make changes
# - Update SearchActivity.java
# - Add SearchFilter model
# - Update layout files
# - Add unit tests

# 4. Commit changes
git add .
git commit -m "feat(search): add restaurant filtering by cuisine type"

# 5. Push to fork
git push origin feature/add-search-filters

# 6. Create Pull Request on GitHub
# - Go to https://github.com/yourusername/sabil23
# - Click "New Pull Request"
# - Fill in description
# - Submit PR

# 7. Address review comments
# - Make requested changes
# - Commit and push updates
# - Conversation continues in PR

# 8. Once approved and merged
# - Delete local branch
# - Delete remote branch
# - Celebrate! 🎉
```

---

## 📋 Contribution Checklist

Before submitting PR:
- [ ] Code follows style guidelines
- [ ] Changes are well-commented
- [ ] Tests written/updated
- [ ] All tests pass locally
- [ ] Documentation updated
- [ ] No debugging code left
- [ ] No hard-coded values (use constants)
- [ ] No unnecessary dependencies added
- [ ] Commits have clear messages
- [ ] PR description is complete

---

## 🙏 Thank You!

Your contributions make Sabil 23 better for the entire Muslim community. Whether it's code, documentation, bug reports, or feature suggestions, your effort is appreciated!

Happy contributing! 🚀

---

**Last Updated**: December 2024

For questions: developers@sabil23.com
For issues: GitHub Issues page
For discussions: GitHub Discussions page
