# Cordova-Flic
A Cordova plugin providing access to the Flic SDK

## Installation
    $ cordova plugin add cordova-plugin-flic

Set android:minSdkVersion="19" or higher in AndroidManifest.xml

    $ cordova build android

## Plugin API
It has been currently stripped to the minimum needed from a Javascript app.

The following functions are available:

* Flic.init (appId, appSecret, appName, options). Initialize Flic and register known buttons for receiving single click, double click and hold events
  * appId: your app client ID
  * appSecret: your app client secret
  * appName: your app name
  * options: a properties object with 2 function callbacks
    * options.success: called on function success
    * options.error: called on function error
* Flic.getKnownButtons(options). Get known buttons. Returns the list of buttons grabbed in a previous run of the app
  * options: a properties object with 2 function callbacks
    * options.success: called on function success
    * options.error: called on function error
* Flic.grabButton(options). Grab a button and register it for receiving single click, double click and hold events. Returns the grabbed button
  * options: a properties object with 2 function callbacks
    * options.success: called on function success
    * options.error: called on function error
* Flic.waitForButtonEvent(options). Get last pressed button event. Returns the button and the event
  * options: a properties object with 2 function callbacks
    * options.success: called on function success
    * options.error: called on function error

## Sample usage code
    // Init flic
    Flic.init(appId, appSecret, appName, {
        success: function(result) {
            console.log('Flic init succeeded');

            // Get known buttons
            Flic.getKnownButtons({
                success: function(buttons) {
                    console.log('Flic getKnownButtons succeeded');
                    console.log('Flic known buttons: ' + JSON.stringify(buttons));
                },
                error: function(message) {
                    console.log('Flic getKnownButtons failed: ' + message);
                }
            });

            // Wait for next button event (one time subscription)
            Flic.waitForButtonEvent({
                success: function(result) {
                    console.log("Waited for " + result.button.color + " button, event " + result.event);
                },
                error: function(message) {
                    console.log("Error waiting for button event: " + message);
                }
            });

        },
        error: function(message) {
            console.log('Flic init failed: ' + message);
        }
    });

    // Subscription to any button events
    document.addEventListener('flicButtonPressed', function (result) {
        console.log(result.button.color + " button received " + result.event + " event");
    }, false);

## Roadmap
Next steps:

* Implement function forgetButton(buttonId). Forget a button, which will never be associated to the app until it is grabbed again.
* Implement function enableButton(buttonId). Subscribe button to single click, double click and hold events.
* Implement function disableButton(buttonId). Unsubscribe button to single click, double click and hold events. Unlike when forgetting the button, the button will still be associated the app.
* Implement function getButton(buttonId). Get a button by its device ID.
* Implement function setActiveMode(buttonId). Set button active mode.
* Implement function for more refined event subscription (onButtonClickOrHold, onButtonSingleOrDoubleClick, onButtonSingleOrDoubleClickOrHold, onButtonUpOrDown, onConnectionCompleted, onConnectionFailed, onConnectionStarted, onDisconnect, onReadRemoteRSSI)
