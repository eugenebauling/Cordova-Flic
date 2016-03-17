cordova.define("cordova-plugin-flic.Flic", function(require, exports, module) {
var exec = require('cordova');

function Flic() { 
	console.log("Flic.js: is created");
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
	console.log("Flic.js: init");

	cordova.exec(options.success, options.error, "Flic", "init", [
		{
			appId: appId,
			appSecret: appSecret,
			appName: appName
		}
	]);
}

/**
 * Get known buttons
 * In case of success, returns a list of buttons
 * Input params:
 * - options: a properties object with 2 function callbacks
 *  - options.success: called on function success
 *  - options.error: called on function error
 */
/*Flic.prototype.getKnownButtons = function(options) {
	console.log("Flic.js: getKnownButtons");

	cordova.exec(options.success, options.error, "Flic", "getKnownButtons");
}*/
Flic.prototype.getKnownButtons = function() {
	console.log("Flic.js: getKnownButtons");

	cordova.exec(function(result){
		/*alert("OK" + result);*/
	},
	function(error){
		/*alert("Error" + error);*/
	},"Flic", "getKnownButtons", [{message: null}]);
}

/**
 * Grab button
 * In case of success, returns the grabbed button
 * Input params:
 * - options: a properties object with 2 function callbacks
 *  - options.success: called on function success
 *  - options.error: called on function error
 */
/*Flic.prototype.grabButton = function(options) {
	console.log("Flic.js: grabButton");

	cordova.exec(options.success, options.error, "Flic", "grabButton");
}*/

Flic.prototype.grabButton = function() {
	console.log("Flic.js: grabButton");

	cordova.exec(function(result){
		/*alert("OK" + result);*/
	},
	function(error){
		/*alert("Error" + error);*/
	},"Flic", "grabButton", [{message: null}]);
}

/**
 * Forget button
 * Input params:
 * - buttonId: the button ID
 * - options: a properties object with 2 function callbacks
 *  - options.success: called on function success
 *  - options.error: called on function error
 */
/*Flic.prototype.forgetButton = function(buttonId, options) {
	console.log("Flic.js: forgetButton");

	cordova.exec(options.success, options.error, "Flic", "forgetButton", [
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
	console.log("Flic.js: enableButton");

	cordova.exec(options.success, options.error, "Flic", "enableButton", [
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
	console.log("Flic.js: disableButton");

	cordova.exec(options.success, options.error, "Flic", "disableButton", [
		{
			buttonId: buttonId
		}
	]);
}*/

var flic = new Flic();
module.exports = flic;

});
