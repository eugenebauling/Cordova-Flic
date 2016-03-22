# Cordova-Flic
A Cordova plugin providing access to the Flic SDK

# Installation
Some manual adjustments after plugin install and build are still required:

1. Config-file platforms/android/src/io/cordova/hellocordova/MainActivity.java
  * Override public void onActivityResult and call super class "super.onActivityResult(requestCode, resultCode, data);"

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

# Roadmap

* Add an event mode where one can register flic events in Javascript using document.addEventListener
* Complete plugin.xml for a total automatic installation
* Publish plugin in the public plugin registry
* Implement function forgetButton(buttonId). Forget a button, which will never be associated to the app until it is grabbed again.
* Implement function enableButton(buttonId). Subscribe button to single click, double click and hold events.
* Implement function disableButton(buttonId). Unsubscribe button to single click, double click and hold events. Unlike when forgetting the button, the button will still be associated the app.
* Implement function getButton(buttonId). Get a button by its device ID.
* Implement function setActiveMode(buttonId). Set button active mode.
* Implement function for more refined event subscription (onButtonClickOrHold, onButtonSingleOrDoubleClick, onButtonSingleOrDoubleClickOrHold, onButtonUpOrDown, onConnectionCompleted, onConnectionFailed, onConnectionStarted, onDisconnect, onReadRemoteRSSI)

