# Cordova-Flic
A Cordova plugin providing access to the Flic SDK

# Installation
    $ cordova plugin add https://github.com/jguix/Cordova-Flic
    $ cordova build android

# Plugin API
It has been currently stripped to the minimum needed from a Javascript app.

The following functions are available:

* Flic.init (appId, appSecret, appName, options). Initialize Flic
  * appId: your app client ID
  * appSecret: your app client secret
  * appName: your app name
  * options: a properties object with 2 function callbacks
    * options.success: called on function success
    * options.error: called on function error
* Flic.getKnownButtons(options). Get known buttons and register them for receiving single click, double click and hold events. Returns a list of buttons
  * options: a properties object with 2 function callbacks
    * options.success: called on function success
    * options.error: called on function error
* Flic.grabButton(options). Grab a button and register it for receiving single click, double click and hold events. Returns the grabbed button
  * options: a properties object with 2 function callbacks
    * options.success: called on function success
    * options.error: called on function error
* Flic.getLastButtonEvent(options). Get last pressed button event. Returns the button and the event
  * options: a properties object with 2 function callbacks
    * options.success: called on function success
    * options.error: called on function error

# Sample usage code

    Flic.init(appId, appSecret, appName, {
            success: function(result) {
                console.log('Flic init succeeded');

                Flic.getKnownButtons({
                    success: function(buttons) {
                        console.log('Flic getKnownButtons succeeded');
                        console.log('Flic known buttons: ' + JSON.stringify(buttons));
                    },
                    error: function(message) {
                        console.log('Flic getKnownButtons failed: ' + message);
                    }
                });

            },
            error: function(message) {
                console.log('Flic init failed: ' + message);
            }
         });

    // Horrible hack, waiting for event implementation
    setInterval(function() {
            Flic.getLastButtonEvent({
                success: function(result) {
                    if (result.button.color == "green") {
                        console.log("Green button " + result.event);
                    } else if (result.button.color == "turquoise") {
                        console.log("Turquoise button " + result.event);
                    }
                },
                error: function(message) {
                    console.log('Flic getLastButtonEvent failed: ' + message);
                }
            });
        }, 1000);

# Roadmap
Next steps:

* Add an event mode where one can register flic events in Javascript using document.addEventListener
* Publish plugin in the public plugin registry
* Implement function forgetButton(buttonId). Forget a button, which will never be associated to the app until it is grabbed again.
* Implement function enableButton(buttonId). Subscribe button to single click, double click and hold events.
* Implement function disableButton(buttonId). Unsubscribe button to single click, double click and hold events. Unlike when forgetting the button, the button will still be associated the app.
* Implement function getButton(buttonId). Get a button by its device ID.
* Implement function setActiveMode(buttonId). Set button active mode.
* Implement function for more refined event subscription (onButtonClickOrHold, onButtonSingleOrDoubleClick, onButtonSingleOrDoubleClickOrHold, onButtonUpOrDown, onConnectionCompleted, onConnectionFailed, onConnectionStarted, onDisconnect, onReadRemoteRSSI)

