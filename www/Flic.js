/**
 * A Cordova plugin providing access to the Flic SDK
 *
 * The plugin will fire the following document events from flic buttons:
 *
 * flicButtonClick
 * flicButtonDblClick
 * flicButtonHold
 *
 */

var exec = cordova.require('cordova/exec'),
    channel = cordova.require('cordova/channel');

function Flic() {
    console.log('Flic.js: is created');
}

/**
 * Initialize Flic
 * Input params:
 * - appId: your app client ID
 * - appSecret: your app client secret
 * - appName: your app name
 * - options: a properties object with 2 function callbacks
 *  - options.success: called on function success
 *  - options.error: called on function error
 */
Flic.prototype.init = function(appId, appSecret, appName, options) {
    console.log('Flic.js: init');

    exec(options.success, options.error, 'Flic', 'init', [
        {
            appId: appId,
            appSecret: appSecret,
            appName: appName
        }
    ]);
};

/**
 * Get known buttons
 * In case of success, returns a list of buttons
 * Input params:
 * - options: a properties object with 2 function callbacks
 *  - options.success: called on function success
 *  - options.error: called on function error
 */
Flic.prototype.getKnownButtons = function(options) {
    console.log('Flic.js: getKnownButtons');

    exec(options.success, options.error, 'Flic', 'getKnownButtons', []);
};

/**
 * Grab button
 * In case of success, returns the grabbed button
 * Input params:
 * - options: a properties object with 2 function callbacks
 *  - options.success: called on function success
 *  - options.error: called on function error
 */
Flic.prototype.grabButton = function(options) {
    console.log('Flic.js: grabButton');

    exec(options.success, options.error, 'Flic', 'grabButton', []);
};

/**
 * Forget button
 * Input params:
 * - buttonId: the button ID
 * - options: a properties object with 2 function callbacks
 *  - options.success: called on function success
 *  - options.error: called on function error
 */
/*Flic.prototype.forgetButton = function(buttonId, options) {
    console.log('Flic.js: forgetButton');

    exec(options.success, options.error, 'Flic', 'forgetButton', [
        {
            buttonId: buttonId
        }
    ]);
}*/

/**
 * Enable button, setting active mode and registering it for click, double click and hold events
 * Input params:
 * - buttonId: the button ID
 * - options: a properties object with 2 function callbacks
 *  - options.success: called on function success
 *  - options.error: called on function error
 */
/*Flic.prototype.enableButton = function(buttonId, options) {
    console.log('Flic.js: enableButton');

    exec(options.success, options.error, 'Flic', 'enableButton', [
        {
            buttonId: buttonId
        }
    ]);
}*/

/**
 * Disable button, setting inactive mode and deregistering all events.
 * To use the button again, use enableButton, there's no need to grab the button again
 * Input params:
 * - buttonId: the button ID
 * - options: a properties object with 2 function callbacks
 *  - options.success: called on function success
 *  - options.error: called on function error
 */
/*Flic.prototype.disableButton = function(buttonId, options) {
    console.log('Flic.js: disableButton');

    exec(options.success, options.error, 'Flic', 'disableButton', [
        {
            buttonId: buttonId
        }
    ]);
}*/

/**
 * On flic button click event
 * Waits for a button click event and returns the button pressed and the event
 * Input params:
 * - options: a properties object with 2 function callbacks
 *  - options.success: called on function success
 *  - options.error: called on function error
 */
Flic.prototype.onButtonClick = function(options) {
    exec(options.success, options.error, "Flic", "onButtonClick", []);
}

function onMessageFromNative(msg) {
    console.log('MESSAGE FROM NATIVE: ' + msg.event);
    console.log(msg.button);
        switch (msg.event.toLowerCase()) {
        case 'singleclick':
            cordova.fireDocumentEvent('flicButtonClick', msg.button);
            break;
        case 'doubleclick':
            cordova.fireDocumentEvent('flicButtonDblClick', msg.button);
            break;
        case 'hold':
            cordova.fireDocumentEvent('flicButtonHold', msg.button);
            break;
        default:
            console.error('Unknown Flic Event: ' + msg.event);
    }
}

// Recursive function for calling the queue event endlessly
var triggerButtonEvent = function() {
    exec(function(event) {
		console.log('Flic triggerButtonEvent succeeded');
		console.log('Flic event: ' + JSON.stringify(event));
		cordova.fireDocumentEvent("flicButtonPressed", event);
		triggerButtonEvent();
	}, function(message) {
		console.log('Flic triggerButtonEvent failed: ' + message);
		triggerButtonEvent();
	}, "Flic", "triggerButtonEvent", []);
}

if (cordova.platformId === 'android') {
    channel.onCordovaReady.subscribe(function() {
        exec(onMessageFromNative, undefined, 'Flic', 'messageChannel', []);
    });
} else {
    // Setup of event queue
    channel.onCordovaReady.subscribe(function() {
        triggerButtonEvent();
        console.log("Trigger flicButtonEvent");
    });
}

var flic = new Flic();
module.exports = flic;
