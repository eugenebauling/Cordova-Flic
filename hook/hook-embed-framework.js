const xcode = require('xcode'),
    fs = require('fs'),
    path = require('path');
	
const xcodeProjPath = fromDir('platforms/ios','.xcodeproj', false);
const projectPath = xcodeProjPath + '/project.pbxproj';
const myProj = xcode.project(projectPath);

myProj.parse(function (err) {
	var projectName = myProj.getFirstTarget().firstTarget.name.substr(1);
    projectName = projectName.substr(0, projectName.length-1);
	
	myProj.addBuildPhase([projectName + '/Plugins/cordova-plugin-flic/fliclib.framework'],'PBXCopyFilesBuildPhase', 'My Embedded Frameworks', myProj.getFirstTarget().uuid, 'frameworks');

    fs.writeFileSync(projectPath, myProj.writeSync());
    console.log('Added to Embedded Binaries.');
});