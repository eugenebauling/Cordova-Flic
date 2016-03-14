package com.jguix.cordova;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.flic.lib.FlicManager;

/**
 * Flic SDK Plugin
 */
public class Flic extends CordovaPlugin {

    public static final String LOG_TAG = "Flic";
    private static final String ACTION_SET_APP_CREDENTIALS = "setAppCredentials";

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
    public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (ACTION_SET_APP_CREDENTIALS.equals(action)) {

            // Get app credentials from arguments
            final JSONObject options = args.getJSONObject(0);
            final String appId = options.getString("appId");
            final String appSecret = options.getString("appSecret");
            final String appName = options.getString("appName");

            // Set app credentials
            Log.v(LOG_TAG,"Flic action: " + action);
            FlicManager.setAppCredentials(appId, appSecret, appName);

            return true;
        } else {
            callbackContext.error("Flic." + action + " is not a supported function. Did you mean '" + ACTION_SET_APP_CREDENTIALS + "'?");
            return false;
        }
    }

}