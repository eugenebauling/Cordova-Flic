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
 * Input config:
 * - appId: your app client ID
 * - appSecret: your app client secret
 * - appName: your app name
 * - reloadOnFlicEvent: in case we should reload our activity when the Flic event happened
 * - success: called on function success
 * - error: called on function error
 */
Flic.prototype.init = function(config, success, error) {
	console.log('Flic.js: init');
    if (!config){
		console.warn('Flic.js: init failure, please provide config for the Flic plugin');
		return;
	}
	
    exec(success || function() {}, 
		error  || function() {}, 
		'Flic', 
		'init', 
		[config]);
};

/**
 * Get known buttons
 * In case of success, returns a list of buttons
 * Input params:
 *  - success: called on function success
 *  - error: called on function error
 */
Flic.prototype.getKnownButtons = function(success, error) {
    console.log('Flic.js: getKnownButtons');

    exec(success, error, 'Flic', 'getKnownButtons', []);
};

/**
 * Grab button
 * In case of success, returns the grabbed button
 * Input params:
 *  - success: called on function success
 *  - error: called on function error
 */
Flic.prototype.grabButton = function(success, error) {
    console.log('Flic.js: grabButton');

    exec(success, error, 'Flic', 'grabButton', []);
};

/**
 * On flic button click event
 * Waits for a button click event and returns the button pressed and the event
 * Input params:
 *  - success: called on function success
 *  - error: called on function error
 */
Flic.prototype.onButtonClick = function(success, error) {
    exec(success, error, "Flic", "onButtonClick", []);
}

var flic = new Flic();
module.exports = flic;
