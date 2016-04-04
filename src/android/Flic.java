package com.jguix.cordova;

import android.content.Intent;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.flic.lib.FlicButton;
import io.flic.lib.FlicButtonCallback;
import io.flic.lib.FlicButtonCallbackFlags;
import io.flic.lib.FlicManager;
import io.flic.lib.FlicManagerInitializedCallback;

/**
 * Flic SDK Plugin
 */
public class Flic extends CordovaPlugin {

    public static final String LOG_TAG = "Flic";
    private static final String ACTION_INIT = "init";
    private static final String ACTION_GET_KNOWN_BUTTONS = "getKnownButtons";
    private static final String ACTION_GRAB_BUTTON = "grabButton";
    private static final String ACTION_FORGET_BUTTON = "forgetButton";
    private static final String ACTION_ENABLE_BUTTON = "enableButton";
    private static final String ACTION_DISABLE_BUTTON = "disableButton";
    private static final String ACTION_WAIT_FOR_BUTTON_EVENT = "waitForButtonEvent";
    private static final String ACTION_TRIGGER_BUTTON_EVENT = "triggerButtonEvent";
    private FlicManager manager;
    private CallbackContext grabButtonCallbackContext;
    private CallbackContext waitForButtonEventCallbackContext;
    private CallbackContext triggerButtonEventCallbackContext;
    private enum BUTTON_STATUS {BUTTON_DISCONNECTED, BUTTON_CONNECTION_STARTED, BUTTON_CONNECTION_COMPLETED};

    /**
     * Constructor.
     */
    public Flic() {
    }

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
        Log.v(LOG_TAG, "Init Flic");
    }

    @Override
    public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.v(LOG_TAG, "Flic action: " + action);
        if (ACTION_INIT.equals(action)) {
            // Get app credentials from arguments
            final JSONObject options = args.getJSONObject(0);
            final String appId = options.getString("appId");
            final String appSecret = options.getString("appSecret");
            final String appName = options.getString("appName");

            // Set app credentials
            FlicManager.setAppCredentials(appId, appSecret, appName);
            // Get manager
            FlicManager.getInstance(this.cordova.getActivity().getApplicationContext(), new FlicManagerInitializedCallback() {
                @Override
                public void onInitialized(FlicManager manager) {
                    Log.d(LOG_TAG, "Ready to use manager");
                    Flic.this.manager = manager;

                    // Call callback function
                    callbackContext.success("Done initializing Flic");
                }
            });

            return true;
        } else if (ACTION_GET_KNOWN_BUTTONS.equals(action)) {
            JSONArray jsonButtons = new JSONArray();
            // Restore buttons grabbed in a previous run of the activity
            List<FlicButton> buttons = manager.getKnownButtons();
            for (FlicButton button : buttons) {
                JSONObject jsonButton = createJSONButton(button);
                jsonButtons.put(jsonButton);
                Log.d(LOG_TAG, "Found an existing button: " + jsonButton.get("buttonId")
                        + ", color: " + jsonButton.get("color")
                        + ", status: " + jsonButton.get("status"));
                // Register events for button
                enableButton(button);
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
            Log.d(LOG_TAG, "Grabbing button");

            return true;
        } else if (ACTION_FORGET_BUTTON.equals(action)) {
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

            return true;
        } else if (ACTION_WAIT_FOR_BUTTON_EVENT.equals(action)) {
            // Keeps track of invoking callback context for later use
            this.waitForButtonEventCallbackContext = callbackContext;

            return true;
        } else if (ACTION_TRIGGER_BUTTON_EVENT.equals(action)) {
            // Keeps track of invoking callback context for later use
            this.triggerButtonEventCallbackContext = callbackContext;

            return true;
        } else {
            callbackContext.error("Flic." + action + " is not a supported function.");
            return false;
        }
    }

    private void enableButton(FlicButton button) {
        // Unregister button from any events
        button.removeAllFlicButtonCallbacks();

        // Register button for click, double click and hold events
        button.addFlicButtonCallback(buttonCallback);
        button.setFlicButtonCallbackFlags(FlicButtonCallbackFlags.CLICK_OR_DOUBLE_CLICK_OR_HOLD);

        // Set active mode
        button.setActiveMode(true);
    }

    private void disableButton(FlicButton button) {
        // Unregister button from any events
        button.removeAllFlicButtonCallbacks();

        // Set inactive mode
        button.setActiveMode(false);
    }

    private JSONObject createJSONButton(FlicButton button) {
        String buttonId = null, color = null, status = null;
        JSONObject jsonButton = new JSONObject();

        try {
            if (button != null) {
                buttonId = button.getButtonId();
                color = button.getColor();
                status = BUTTON_STATUS.values()[button.getConnectionStatus()].name();
            }
            jsonButton.put("buttonId", buttonId);
            jsonButton.put("color", color);
            jsonButton.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonButton;
    }

    private JSONObject createJSONButtonEvent(FlicButton button, String event) {
        JSONObject jsonButtonEvent = new JSONObject();

        try {
            JSONObject jsonButton;
            jsonButton = createJSONButton(button);
            jsonButtonEvent.put("button", jsonButton);
            jsonButtonEvent.put("event", event);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonButtonEvent;
    }

    private FlicButtonCallback buttonCallback = new FlicButtonCallback() {
        @Override
        public void onButtonSingleOrDoubleClickOrHold(
                FlicButton button,
                boolean wasQueued,
                int timeDiff,
                boolean isSingleClick,
                boolean isDoubleClick,
                boolean isHold) {
            String event = isSingleClick ? "singleClick" : (isDoubleClick ? "doubleClick" : "hold");
            Log.d(LOG_TAG, "Received event: " + event);

            JSONObject jsonButtonEvent = createJSONButtonEvent(button, event);
            // Reports to waitForButtonEvent callback
            if (waitForButtonEventCallbackContext != null) {
                waitForButtonEventCallbackContext.success(jsonButtonEvent);
                waitForButtonEventCallbackContext = null;
            }
            // Reports to triggerButtonEvent callback
            if (triggerButtonEventCallbackContext != null) {
                triggerButtonEventCallbackContext.success(jsonButtonEvent);
                triggerButtonEventCallbackContext = null;
            }
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FlicButton button = manager.completeGrabButton(requestCode, resultCode, data);

        if (button != null) {
                JSONObject jsonButton = createJSONButton(button);
                Log.d(LOG_TAG, "Got a button: " + button.getButtonId()
                        + ", color: " + button.getColor()
                        + ", status: " + BUTTON_STATUS.values()[button.getConnectionStatus()].name());
                // Register events for button
                enableButton(button);
                grabButtonCallbackContext.success(jsonButton);

        }
    }

}
