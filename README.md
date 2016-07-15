# Cordova-Flic
A Cordova plugin providing access to the Flic SDK

## Installation
    $ cordova plugin add cordova-plugin-flic

Set android:minSdkVersion="19" or higher in AndroidManifest.xml

    $ cordova build android

## Plugin API
It has been currently stripped to the minimum needed from a Javascript app.

The following functions are available:

* Flic.init (config, , success, error). Initialize Flic and register known buttons for receiving single click, double click and hold events
  * config:
	* appId: your app client ID
	* appSecret: your app client secret
	* appName: your app name
	* reloadOnFlicEvent: in case you want to start the App via Flic event (Android only, Boolean, default: false)
  * success: called on function success
  * error: called on function error
* Flic.getKnownButtons(options). Get known buttons. Returns the list of buttons grabbed in a previous run of the app
  * success: called on function success
  * error: called on function error
* Flic.grabButton(options). Grab a button and register it for receiving single click, double click and hold events. Returns the grabbed button
  * success: called on function success
  * error: called on function error

## Sample usage code
    // Init flic
    Flic.init({
			appId: 'your app id'
			appSecret: 'your app client secret' 
			appName: 'your app name'
			reloadOnFlicEvent: true
		}, 
        function(result) {
            console.log('Flic init succeeded');

            // Get known buttons
            Flic.getKnownButtons({
                function(buttons) {
                    console.log('Flic getKnownButtons succeeded');
                    console.log('Flic known buttons: ' + JSON.stringify(buttons));
                },
                function(message) {
                    console.log('Flic getKnownButtons failed: ' + message);
                }
            });

        },
        function(message) {
            console.log('Flic init failed: ' + message);
        }
    );

    // Subscription to button events
	Flic.onButtonClick(onFlicButtonPressed, onFlicButtonPressedError)

    function onFlicButtonPressed(result) {
        console.log(result.event); // (String) singleClick or doubleClick or hold
        console.log(result.button.buttonId); // (String)
        console.log(result.button.color); // (String) green
        console.log(result.wasQueued); // (Boolean) If the event was locally queued in the button because it was disconnected. After the connection is completed, the event will be sent with this parameter set to true.
		console.log(result.timeDiff); // (Number) If the event was queued, the timeDiff will be the number of seconds since the event happened.
    }
	
	function onFlicButtonPressedError(err){
		console.log(err);
	}
## Sample app

Copy the files from the example folder to your project's platforms/android/assets/wwww folder.

![Sample app screenshot](/sample/sample_app_screenshot.jpg)

## Roadmap
Next steps:

* Implement function forgetButton(buttonId). Forget a button, which will never be associated to the app until it is grabbed again.
* Implement function enableButton(buttonId). Subscribe button to single click, double click and hold events.
* Implement function disableButton(buttonId). Unsubscribe button to single click, double click and hold events. Unlike when forgetting the button, the button will still be associated the app.
* Implement function getButton(buttonId). Get a button by its device ID.
* Implement function setActiveMode(buttonId). Set button active mode.
* Implement function for more refined event subscription (onButtonClickOrHold, onButtonSingleOrDoubleClick, onButtonSingleOrDoubleClickOrHold, onButtonUpOrDown, onConnectionCompleted, onConnectionFailed, onConnectionStarted, onDisconnect, onReadRemoteRSSI)
