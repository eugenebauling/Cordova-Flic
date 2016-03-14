var exec = require('cordova');

function Flic() { 
	console.log("Flic.js: is created");
}

Flic.prototype.setAppCredentials = function(appId, appSecret, appName, options) {
	console.log("Flic.js: setAppCredentials");

	cordova.exec(options.success, options.error, "Flic", "setAppCredentials", [
		{
			appId: appId,
			appSecret: appSecret,
			appName: appName
		}
	]);
}

var flic = new Flic();
module.exports = flic;
