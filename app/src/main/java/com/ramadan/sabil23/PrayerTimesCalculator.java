package com.ramadan.sabil23;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class for calculating Islamic prayer times based on location
 */
public class PrayerTimesCalculator {
    private static final String TAG = "PrayerTimesCalculator";
    private static final String PREF_NAME = "prayer_times_prefs";

    // Calculation methods
    public static final int CALCULATION_METHOD_MWL = 0;      // Muslim World League
    public static final int CALCULATION_METHOD_ISNA = 1;     // Islamic Society of North America
    public static final int CALCULATION_METHOD_EGYPT = 2;    // Egyptian General Authority of Survey
    public static final int CALCULATION_METHOD_MAKKAH = 3;   // Umm al-Qura University, Makkah
    public static final int CALCULATION_METHOD_KARACHI = 4;  // University of Islamic Sciences, Karachi
    public static final int CALCULATION_METHOD_TEHRAN = 5;   // Institute of Geophysics, University of Tehran
    public static final int CALCULATION_METHOD_JAFARI = 6;   // Shia Ithna Ashari, Leva Research Institute, Qum

    // Default calculation parameters
    private static final double[] FAJR_ANGLE = {-18, -15, -19.5, -18.5, -18, -17.7, -16};
    private static final double[] ISHA_ANGLE = {-17, -15, -17.5, -90, -18, -14, -14};
    private static final double[] MAGHRIB_OFFSET = {0, 0, 0, 0, 0, 4.5, 4};
    private static final int[] ISHA_OFFSET = {0, 0, 0, 90, 0, 0, 0};

    // Default calculation method
    private static final int DEFAULT_CALCULATION_METHOD = CALCULATION_METHOD_MWL;

    // Astronomical constants
    private static final double DEG_TO_RAD = Math.PI / 180.0;
    private static final double RAD_TO_DEG = 180.0 / Math.PI;

    // Time adjustments in minutes
    private int fajrAdjustment = 0;
    private int sunriseAdjustment = 0;
    private int dhuhrAdjustment = 0;
    private int asrAdjustment = 0;
    private int maghribAdjustment = 0;
    private int ishaAdjustment = 0;

    // Calculation parameters
    private double latitude;
    private double longitude;
    private double timezone;
    private int calculationMethod;
    private int asrJuristic;  // 0 = Shafi'i, 1 = Hanafi
    private int highLatitudeAdjustment;  // 0 = None, 1 = Middle of Night, 2 = 1/7th of Night, 3 = Angle Based

    /**
     * Constructor with default calculation method
     */
    public PrayerTimesCalculator(double latitude, double longitude, double timezone) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.calculationMethod = DEFAULT_CALCULATION_METHOD;
        this.asrJuristic = 0;  // Default to Shafi'i
        this.highLatitudeAdjustment = 0;  // Default to no adjustment
    }

    /**
     * Constructor with specified calculation method
     */
    public PrayerTimesCalculator(double latitude, double longitude, double timezone, int calculationMethod) {
        this(latitude, longitude, timezone);
        this.calculationMethod = calculationMethod;
    }

    /**
     * Sets the calculation method
     */
    public void setCalculationMethod(int calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    /**
     * Sets the Asr juristic method
     * @param asrJuristic 0 for Shafi'i, 1 for Hanafi
     */
    public void setAsrJuristic(int asrJuristic) {
        this.asrJuristic = asrJuristic;
    }

    /**
     * Sets the high latitude adjustment method
     * @param adjustment 0 = None, 1 = Middle of Night, 2 = 1/7th of Night, 3 = Angle Based
     */
    public void setHighLatitudeAdjustment(int adjustment) {
        this.highLatitudeAdjustment = adjustment;
    }

    /**
     * Sets time adjustments in minutes
     */
    public void setTimeAdjustments(int fajr, int sunrise, int dhuhr, int asr, int maghrib, int isha) {
        this.fajrAdjustment = fajr;
        this.sunriseAdjustment = sunrise;
        this.dhuhrAdjustment = dhuhr;
        this.asrAdjustment = asr;
        this.maghribAdjustment = maghrib;
        this.ishaAdjustment = isha;
    }

    /**
     * Calculates prayer times for a given date
     * @return Array of Calendar objects for [Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha]
     */
    public Calendar[] getPrayerTimes(Calendar date) {
        // Initialize array for prayer times
        Calendar[] prayerTimes = new Calendar[6];
        for (int i = 0; i < 6; i++) {
            prayerTimes[i] = (Calendar) date.clone();
        }

        // Get day of year
        int dayOfYear = date.get(Calendar.DAY_OF_YEAR);

        // Get Julian date
        double julianDate = getJulianDate(date);

        // Calculate sun position
        double D = getSunPosition(julianDate);

        // Calculate equation of time and declination
        double EqT = getEquationOfTime(D);
        double Dec = getSunDeclination(D);

        // Calculate prayer times
        double fajrTime = computePrayerTime(FAJR_ANGLE[calculationMethod], EqT, Dec, true);
        double sunriseTime = computePrayerTime(0.833, EqT, Dec, true);
        double dhuhrTime = computeMidDay(EqT);
        double asrTime = computeAsrTime(EqT, Dec);
        double maghribTime = computePrayerTime(0.833, EqT, Dec, false);

        // Adjust Maghrib time if needed
        if (MAGHRIB_OFFSET[calculationMethod] > 0) {
            maghribTime += MAGHRIB_OFFSET[calculationMethod] / 60.0;
        }

        // Calculate Isha time
        double ishaTime;
        if (ISHA_OFFSET[calculationMethod] > 0) {
            ishaTime = maghribTime + ISHA_OFFSET[calculationMethod] / 60.0;
        } else {
            ishaTime = computePrayerTime(ISHA_ANGLE[calculationMethod], EqT, Dec, false);
        }

        // Apply time adjustments
        fajrTime += fajrAdjustment / 60.0;
        sunriseTime += sunriseAdjustment / 60.0;
        dhuhrTime += dhuhrAdjustment / 60.0;
        asrTime += asrAdjustment / 60.0;
        maghribTime += maghribAdjustment / 60.0;
        ishaTime += ishaAdjustment / 60.0;

        // Convert to Calendar objects
        setTimeToCalendar(prayerTimes[0], fajrTime);
        setTimeToCalendar(prayerTimes[1], sunriseTime);
        setTimeToCalendar(prayerTimes[2], dhuhrTime);
        setTimeToCalendar(prayerTimes[3], asrTime);
        setTimeToCalendar(prayerTimes[4], maghribTime);
        setTimeToCalendar(prayerTimes[5], ishaTime);

        return prayerTimes;
    }

    /**
     * Gets the Iftar (Maghrib) time for the given date
     */
    public Calendar getIftarTime(Calendar date) {
        Calendar[] prayerTimes = getPrayerTimes(date);
        return prayerTimes[4]; // Maghrib time
    }

    /**
     * Gets the Suhoor end time (Fajr) for the given date
     */
    public Calendar getSuhoorEndTime(Calendar date) {
        Calendar[] prayerTimes = getPrayerTimes(date);
        return prayerTimes[0]; // Fajr time
    }

    /**
     * Computes the time of a prayer based on sun angle
     */
    private double computePrayerTime(double angle, double eqt, double dec, boolean isFajr) {
        // Convert angle to radians
        double angleRad = Math.abs(angle) * DEG_TO_RAD;

        // Calculate hour angle
        double latRad = latitude * DEG_TO_RAD;
        double decRad = dec * DEG_TO_RAD;

        double term1 = Math.sin(angleRad) - Math.sin(latRad) * Math.sin(decRad);
        double term2 = Math.cos(latRad) * Math.cos(decRad);

        double cosHourAngle = term1 / term2;

        // Check for no sunrise/sunset
        if (cosHourAngle > 1 || cosHourAngle < -1) {
            // Handle high latitude cases
            return computeHighLatitudeAdjustment(angle, eqt, dec, isFajr);
        }

        double hourAngle = Math.acos(cosHourAngle) * RAD_TO_DEG;

        // Convert to hours
        hourAngle = hourAngle / 15.0;

        // Adjust for Fajr/Isha
        if (isFajr) {
            hourAngle = 12 - hourAngle;
        } else {
            hourAngle = 12 + hourAngle;
        }

        // Adjust for equation of time and timezone
        double time = hourAngle - (longitude / 15.0) - eqt;

        // Normalize to 0-24 range
        time = (time + 24) % 24;

        return time;
    }

    /**
     * Computes the midday time
     */
    private double computeMidDay(double eqt) {
        double time = 12 - (longitude / 15.0) - eqt;
        return (time + 24) % 24;
    }

    /**
     * Computes the Asr time
     */
    private double computeAsrTime(double eqt, double dec) {
        // Asr shadow factor (1 for Shafi'i, 2 for Hanafi)
        double shadowFactor = 1 + (asrJuristic == 1 ? 1 : 0);

        // Calculate sun angle at Asr
        double latRad = latitude * DEG_TO_RAD;
        double decRad = dec * DEG_TO_RAD;

        double term1 = Math.atan(1 / (shadowFactor + Math.tan(Math.abs(latRad - decRad))));
        double asrAngle = Math.abs(term1) * RAD_TO_DEG;

        return computePrayerTime(90 - asrAngle, eqt, dec, false);
    }

    /**
     * Handles high latitude adjustments
     */
    private double computeHighLatitudeAdjustment(double angle, double eqt, double dec, boolean isFajr) {
        // Default to no adjustment
        if (highLatitudeAdjustment == 0) {
            return Double.NaN; // No valid time
        }

        // Get sunrise and sunset times
        double sunriseTime = computePrayerTime(0.833, eqt, dec, true);
        double sunsetTime = computePrayerTime(0.833, eqt, dec, false);

        // Calculate night duration
        double nightDuration;
        if (sunsetTime < sunriseTime) {
            nightDuration = 24 - sunriseTime + sunsetTime;
        } else {
            nightDuration = sunsetTime - sunriseTime;
        }

        // Apply adjustment method
        if (highLatitudeAdjustment == 1) {
            // Middle of night method
            if (isFajr) {
                return sunriseTime - (nightDuration / 2);
            } else {
                return sunsetTime + (nightDuration / 2);
            }
        } else if (highLatitudeAdjustment == 2) {
            // 1/7th of night method
            if (isFajr) {
                return sunriseTime - (nightDuration / 7);
            } else {
                return sunsetTime + (nightDuration / 7);
            }
        } else {
            // Angle based method (default)
            double portion = angle / 60.0;
            if (isFajr) {
                return sunriseTime - (nightDuration * portion);
            } else {
                return sunsetTime + (nightDuration * portion);
            }
        }
    }

    /**
     * Gets the Julian date for a given Calendar
     */
    private double getJulianDate(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        if (month <= 2) {
            year -= 1;
            month += 12;
        }

        double A = Math.floor(year / 100.0);
        double B = 2 - A + Math.floor(A / 4.0);

        double JD = Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + day + B - 1524.5;

        return JD;
    }

    /**
     * Gets the sun's position in the ecliptic
     */
    private double getSunPosition(double jd) {
        double D = jd - 2451545.0;
        return (D * 0.9856) % 360; // Mean anomaly of the sun
    }

    /**
     * Gets the equation of time
     */
    private double getEquationOfTime(double D) {
        double W = (D * 0.9856) % 360;
        double M = W - 3.289;

        double E = M + 1.916 * Math.sin(M * DEG_TO_RAD) + 0.020 * Math.sin(2 * M * DEG_TO_RAD) + 282.634;
        E = (E + 360) % 360;

        double L = E - 0.06571 * D - 6.622;
        double RA = Math.atan2(0.91746 * Math.sin(E * DEG_TO_RAD), Math.cos(E * DEG_TO_RAD)) * RAD_TO_DEG;
        RA = (RA + 360) % 360;

        double Lquadrant = Math.floor(L / 90) * 90;
        double RAquadrant = Math.floor(RA / 90) * 90;

        RA = RA + (Lquadrant - RAquadrant);
        RA = RA / 15;

        return L / 15 - RA;
    }

    /**
     * Gets the sun's declination
     */
    private double getSunDeclination(double D) {
        double W = (D * 0.9856) % 360;
        double M = W - 3.289;

        double L = M + 1.916 * Math.sin(M * DEG_TO_RAD) + 0.020 * Math.sin(2 * M * DEG_TO_RAD) + 282.634;
        L = (L + 360) % 360;


        double sinDec = 0.39782 * Math.sin(L * DEG_TO_RAD);
        double cosDec = Math.sqrt(1 - sinDec * sinDec);

        return Math.asin(sinDec) * RAD_TO_DEG;
    }

    /**
     * Sets the time portion of a Calendar object based on decimal hours
     */
    private void setTimeToCalendar(Calendar cal, double time) {
        if (Double.isNaN(time)) {
            // Handle invalid time
            return;
        }

        int hours = (int) Math.floor(time);
        int minutes = (int) Math.floor((time - hours) * 60);
        int seconds = (int) Math.floor(((time - hours) * 60 - minutes) * 60);

        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, seconds);
        cal.set(Calendar.MILLISECOND, 0);

        // Adjust for timezone
        cal.add(Calendar.HOUR_OF_DAY, (int) Math.floor(timezone));
        cal.add(Calendar.MINUTE, (int) Math.floor((timezone - Math.floor(timezone)) * 60));
    }

    /**
     * Loads prayer time calculation settings from SharedPreferences
     */
    public static PrayerTimesCalculator fromPreferences(Context context, double latitude, double longitude) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Get timezone offset
        TimeZone tz = TimeZone.getDefault();
        double timezone = tz.getOffset(new Date().getTime()) / (1000.0 * 60 * 60);

        // Get calculation method
        int calculationMethod = prefs.getInt("calculation_method", DEFAULT_CALCULATION_METHOD);

        // Create calculator
        PrayerTimesCalculator calculator = new PrayerTimesCalculator(latitude, longitude, timezone, calculationMethod);

        // Set Asr juristic method
        int asrJuristic = prefs.getInt("asr_juristic", 0);
        calculator.setAsrJuristic(asrJuristic);

        // Set high latitude adjustment
        int highLatAdjustment = prefs.getInt("high_latitude_adjustment", 0);
        calculator.setHighLatitudeAdjustment(highLatAdjustment);

        // Set time adjustments
        int fajrAdj = prefs.getInt("fajr_adjustment", 0);
        int sunriseAdj = prefs.getInt("sunrise_adjustment", 0);
        int dhuhrAdj = prefs.getInt("dhuhr_adjustment", 0);
        int asrAdj = prefs.getInt("asr_adjustment", 0);
        int maghribAdj = prefs.getInt("maghrib_adjustment", 0);
        int ishaAdj = prefs.getInt("isha_adjustment", 0);

        calculator.setTimeAdjustments(fajrAdj, sunriseAdj, dhuhrAdj, asrAdj, maghribAdj, ishaAdj);

        return calculator;
    }
}

