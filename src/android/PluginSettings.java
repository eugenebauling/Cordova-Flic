package com.jguix.cordova;

import android.content.SharedPreferences;
import android.util.Log;

// singletog class for storing settings
public class PluginSettings {

    public static final String TAG = "Flic";
    static SharedPreferences mPreferences;
    private static String appId;
    private static String appSecret;
    private static String appName;
    private static boolean reloadOnFlicEvent = false;
    private static boolean isActivityStarted;
    private static boolean isInitialized = false;

    protected static void initialize(SharedPreferences preferences) {
        if(preferences == null){
            return;
        }

        if(isInitialized){
            Log.d(TAG, "Settings have been already initialized");
            return;
        }

        Log.d(TAG, "Initialize settings");
        mPreferences = preferences;

        String _appId = preferences.getString("appId", "");
        String _appSecret = preferences.getString("appSecret", "");
        String _appName = preferences.getString("appName", "");
        boolean _reloadOnFlicEvent = preferences.getBoolean("reloadOnFlicEvent", false);
        boolean _isActivityStarted = preferences.getBoolean("isActivityStarted", false);

        setAppId(_appId);
        setAppSecret(_appSecret);
        setAppName(_appName);
        setReloadOnFlicEvent(_reloadOnFlicEvent);
        setIsActivityStarted(_isActivityStarted);

        isInitialized = true;
    }

    public static void setAppId(String value) {
        appId = value;

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("appId", value);
        editor.apply();
    }

    public static void setAppSecret(String value) {
        appSecret = value;

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("appSecret", value);
        editor.apply();
    }

    public static void setAppName(String value) {
        appName = value;

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("appName", value);
        editor.apply();
    }

    public static void setReloadOnFlicEvent(boolean value) {
        reloadOnFlicEvent = value;

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("reloadOnFlicEvent", value);
        editor.apply();
    }

    public static void setIsActivityStarted(boolean value) {
        isActivityStarted = value;

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("isActivityStarted", value);
        editor.apply();
    }

    public static String getAppId() {
        return appId;
    }

    public static String getAppSecret() {
        return appSecret;
    }

    public static String getAppName() {
        return appName;
    }

    public static boolean isReloadOnFlicEvent() {
        return reloadOnFlicEvent;
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    public static boolean isActivityStarted() {
        return isActivityStarted;
    }
}
