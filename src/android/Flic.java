package com.jguix.cordova;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.flic.lib.FlicBroadcastReceiver;
import io.flic.lib.FlicButton;
import io.flic.lib.FlicManager;
import io.flic.lib.FlicManagerInitializedCallback;
import io.flic.lib.FlicBroadcastReceiverFlags;

/**
 * Flic SDK Plugin
 */
public class Flic extends CordovaPlugin {

    public static final String TAG = "Flic";
    private static final String ACTION_INIT = "init";
    private static final String ACTION_GET_KNOWN_BUTTONS = "getKnownButtons";
    private static final String ACTION_GRAB_BUTTON = "grabButton";
    private static final String ACTION_FORGET_BUTTON = "forgetButton";
    private static final String ACTION_ENABLE_BUTTON = "enableButton";
    private static final String ACTION_DISABLE_BUTTON = "disableButton";
    private static final String ACTION_ON_BUTTON_CLICK = "onButtonClick";
    private static final String FLICLIB_EVENT = "io.flic.FLICLIB_EVENT";
    private FlicManager manager;
    private CallbackContext onButtonClickCallback;
    private CallbackContext grabButtonCallbackContext;
    private static ArrayList<JSONObject> buttonCachedEvents;
    private static FlicBroadcastReceiver mFlicReceiver;

    private enum BUTTON_STATUS {BUTTON_DISCONNECTED, BUTTON_CONNECTION_STARTED, BUTTON_CONNECTION_COMPLETED};

    public Flic() { }
    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.v(TAG, "Init Flic");
    }

    @Override
    public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Flic action: " + action);

        if (ACTION_INIT.equals(action)) {
            Log.d(TAG, "ACTION: Init");

            initAppSettings(args);
            initBroadcastReceiver();

            // Get manager
            FlicManager.getInstance(this.cordova.getActivity().getApplicationContext(), new FlicManagerInitializedCallback() {
                @Override
                public void onInitialized(FlicManager manager) {
                    Log.d(TAG, "FlicManager Ready");
                    Flic.this.manager = manager;

                    // Auto-enable buttons grabbed in a previous run of the activity
                    List<FlicButton> buttons = manager.getKnownButtons();
                    for (FlicButton button : buttons) {
                        // Register events for button
                        enableButton(button);
                    }

                    // Call callback function
                    callbackContext.success("Done initializing Flic");
                }
            });

            return true;
        } else if (ACTION_GET_KNOWN_BUTTONS.equals(action)) {
            Log.d(TAG, "ACTION: Get Known Buttons");
            JSONArray jsonButtons = new JSONArray();
            // Restore buttons grabbed in a previous run of the activity
            List<FlicButton> buttons = manager.getKnownButtons();
            for (FlicButton button : buttons) {
                JSONObject jsonButton = createJSONButton(button);
                jsonButtons.put(jsonButton);
                Log.d(TAG, "Found an existing button: " + jsonButton.get("buttonId")
                      + ", color: " + jsonButton.get("color")
                      + ", status: " + jsonButton.get("status"));
            }
            // Call callback function
            callbackContext.success(jsonButtons);

            return true;
        } else if (ACTION_GRAB_BUTTON.equals(action)) {
            // Keeps track of invoking callback context for later use
            this.grabButtonCallbackContext = callbackContext;
            // Tells cordova to send the callback to this plugin
            this.cordova.setActivityResultCallback(this);
            // Initiate grab button
            manager.initiateGrabButton(this.cordova.getActivity());
            Log.d(TAG, "ACTION: Grabbing button");

            return true;
        } else if (ACTION_FORGET_BUTTON.equals(action)) {
            Log.d(TAG, "ACTION: Forget Button");
            // Get buttonId from arguments
            final JSONObject options = args.getJSONObject(0);
            final String buttonId = options.getString("buttonId");

            // Forget button
            manager.forgetButton(manager.getButtonByDeviceId(buttonId));

            // Call callback function
            callbackContext.success();

            return true;
        } else if (ACTION_ENABLE_BUTTON.equals(action)) {
            // Get buttonId from arguments
            final JSONObject options = args.getJSONObject(0);
            final String buttonId = options.getString("buttonId");
            FlicButton button = manager.getButtonByDeviceId(buttonId);

            // Enable button
            enableButton(button);

            // Call callback function
            callbackContext.success();
            Log.d(TAG, "ACTION: Enable Button");

            return true;
        } else if (ACTION_DISABLE_BUTTON.equals(action)) {
            // Get buttonId from arguments
            final JSONObject options = args.getJSONObject(0);
            final String buttonId = options.getString("buttonId");
            FlicButton button = manager.getButtonByDeviceId(buttonId);

            // Disable button
            disableButton(button);

            // Call callback function
            callbackContext.success();
            Log.d(TAG, "ACTION: Disable Button");

            return true;
        } else if (ACTION_ON_BUTTON_CLICK.equals(action)) {
            Log.d(TAG, "ACTION:  onButtonClick");
            this.onButtonClickCallback = callbackContext;

            // restore button events, which were happened while the App was inactive
            restoreButtonEvents();

            return true;
        } else {
            Log.w(TAG, "ACTION: UNKNOWN");
            callbackContext.error("Flic." + action + " is not a supported function.");
            return false;
        }
    }

    private void enableButton(FlicButton button) {
        if (button != null) {
            button.registerListenForBroadcast(FlicBroadcastReceiverFlags.CLICK_OR_DOUBLE_CLICK_OR_HOLD);
            Log.d(TAG, "SUCCESS: Registered a button " + button.getButtonId());
            Log.d(TAG, "Registerd  FlicBroadcastReceiverFlags=" + FlicBroadcastReceiverFlags.CLICK_OR_DOUBLE_CLICK_OR_HOLD);
            Toast.makeText(super.webView.getContext(), "Flic Button Registered", Toast.LENGTH_SHORT).show();
        } else {
            Log.w(TAG, "WARNING: Did not register any button");
            Toast.makeText(super.webView.getContext(), "WARNING: Did not register any button", Toast.LENGTH_SHORT).show();
        }
    }

    private void disableButton(FlicButton button) {
        Log.d(TAG, "disableButton");

        // Unregister button from any events
        button.removeAllFlicButtonCallbacks();

        // Set inactive mode
        button.setActiveMode(false);
    }

    private JSONObject createJSONButton(FlicButton button) {
        JSONObject jsonButton = new JSONObject();

        try {
            if (button != null) {
                String buttonId = button.getButtonId();
                String color = button.getColor();
                String status = BUTTON_STATUS.values()[button.getConnectionStatus()].name();

                jsonButton.put("buttonId", buttonId);
                jsonButton.put("color", color);
                jsonButton.put("status", status);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonButton;
    }

    private JSONObject createJSONButtonEvent(FlicButton button, String event, boolean wasQueued, int timeDiff) {
        JSONObject jsonButtonEvent = new JSONObject();

        try {
            JSONObject jsonButton;
            jsonButton = createJSONButton(button);
            jsonButtonEvent.put("button", jsonButton);
            jsonButtonEvent.put("event", event);
            jsonButtonEvent.put("wasQueued", wasQueued);
            jsonButtonEvent.put("timeDiff", timeDiff);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonButtonEvent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FlicButton button = manager.completeGrabButton(requestCode, resultCode, data);

        if (button != null) {
            JSONObject jsonButton = createJSONButton(button);
            Log.d(TAG, "Got a button: " + button.getButtonId()
                  + ", color: " + button.getColor()
                  + ", status: " + BUTTON_STATUS.values()[button.getConnectionStatus()].name());
            // Register events for button
            enableButton(button);
            grabButtonCallbackContext.success(jsonButton);
        }
    }

    private void initAppSettings(JSONArray args) throws JSONException {
        // Get app credentials from arguments
        final JSONObject options = args.getJSONObject(0);
        String _appId = options.getString("appId");
        String _appSecret = options.getString("appSecret");
        String _appName = options.getString("appName");
        boolean _reloadOnFlicEvent = options.optBoolean("reloadOnFlicEvent", false);

        SharedPreferences preferences = this.cordova.getActivity().getApplicationContext().getSharedPreferences(TAG, 0);
        PluginSettings.initialize(preferences);
        PluginSettings.setAppId(_appId);
        PluginSettings.setAppSecret(_appSecret);
        PluginSettings.setAppName(_appName);
        PluginSettings.setReloadOnFlicEvent(_reloadOnFlicEvent);
        PluginSettings.setIsActivityStarted(true);

        setAppCredentials();
    }

    private void initBroadcastReceiver() {
        Log.d(TAG, "initBroadcastReceiver()");

        // in case receiver is already registered
        // unregister previous receiver first
        if(this.mFlicReceiver != null){
            this.cordova.getActivity().getApplicationContext().unregisterReceiver(this.mFlicReceiver);
        }

        this.mFlicReceiver = new FlicBroadcastReceiver() {
            @Override
            protected void onRequestAppCredentials(Context context) {
                Log.d(TAG, "FlicBroadcastReceiver ***onRequestAppCredentials");
                setAppCredentials();
            }
            @Override
            public void onButtonSingleOrDoubleClickOrHold(Context context, FlicButton button, boolean wasQueued, int timeDiff, boolean isSingleClick, boolean isDoubleClick, boolean isHold) {
                String event = isSingleClick ? "singleClick" : (isDoubleClick ? "doubleClick" : "hold");
                Log.d(TAG, "onButtonSingleOrDoubleClickOrHold event: " + event);
                sendButtonEvent(button, event, wasQueued, timeDiff);
            }
            @Override
            public void onButtonRemoved(Context context, FlicButton button) {
                Log.d(TAG, "onButtonRemoved");
            }
        };

        //Register the broadcast reciever
        IntentFilter filter = new IntentFilter();
        filter.addAction(FLICLIB_EVENT);
        Log.d(TAG, "***Registering Receiver*** IntentFilter: " + FLICLIB_EVENT);
        this.cordova.getActivity().getApplicationContext().registerReceiver(this.mFlicReceiver, filter);
    }

    public void sendButtonEvent(FlicButton button, String action, boolean wasQueued, int timeDiff) {
        Log.d(TAG, "Sending Event Event to Plugin Action: " + action + " Button: " + button);

        JSONObject event = createJSONButtonEvent(button, action, wasQueued, timeDiff);

        if(isMainActivityActive()){
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, event);
            pluginResult.setKeepCallback(true);
            if (onButtonClickCallback != null) {
                onButtonClickCallback.sendPluginResult(pluginResult);
            }
        } else{
            Log.d(TAG, "Main activity is closed");
            boolean reloadApp = PluginSettings.isReloadOnFlicEvent();
            if(reloadApp){
                if(this.buttonCachedEvents == null)
                    this.buttonCachedEvents = new ArrayList<JSONObject>();

                this.buttonCachedEvents.add(event);

                Log.d(TAG, "Reload main activity");
                forceActivityStart();
            }
        }
    }

    private void restoreButtonEvents(){
        if(this.buttonCachedEvents != null && isMainActivityActive() && onButtonClickCallback != null) {
            for(JSONObject buttonEvent:this.buttonCachedEvents){
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, buttonEvent);
                pluginResult.setKeepCallback(true);
                onButtonClickCallback.sendPluginResult(pluginResult);
            }
            this.buttonCachedEvents = null;
        }
    }

    public void setAppCredentials() {
        Log.d(TAG, "Setting Flic App Credentials");
        SharedPreferences preferences = this.cordova.getActivity().getApplicationContext().getSharedPreferences(TAG, 0);
        PluginSettings.initialize(preferences);

        FlicManager.setAppCredentials(PluginSettings.getAppId(), PluginSettings.getAppSecret(), PluginSettings.getAppName());
    }

    private Boolean isMainActivityActive() {
        if(!PluginSettings.isInitialized()){
            SharedPreferences preferences = this.cordova.getActivity().getApplicationContext().getSharedPreferences(TAG, 0);
            PluginSettings.initialize(preferences);
        }
        return PluginSettings.isActivityStarted();
    }

    private void forceActivityStart() {
        Log.i(TAG, "forceActivityStart");
        if(isMainActivityActive()){
            Log.i(TAG, "activity already started");
            return;
        }

        Intent intent = cordova.getActivity().getPackageManager().getLaunchIntentForPackage(cordova.getActivity().getApplicationContext().getPackageName());
        // hide an app after reloading
        intent.putExtra("forceReload", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_FROM_BACKGROUND);
        cordova.getActivity().startActivity(intent);
    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (onButtonClickCallback != null) {
            onButtonClickCallback = null;
        }
        PluginSettings.setIsActivityStarted(false);
    }
}
