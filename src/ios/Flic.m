//
//  Flic
//
//  Created by Maxim Dukhanov <m.dukhanov@gmail.com>
//

#import "Flic.h"
#import <Cordova/CDVAvailability.h>
#import <fliclib/fliclib.h>


@interface Flic () <SCLFlicManagerDelegate, SCLFlicButtonDelegate>
@end

@implementation Flic

static NSString * const pluginNotInitializedMessage = @"flic is not initialized";
static NSString * const TAG = @"[TAF Flic] ";
@synthesize onButtonClickCallbackId;

- (void)pluginInitialize
{
    [self log:@"pluginInitialize"];
    //[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(openApplicationURL:) name:UIApplicationLaunchOptionsURLKey object:nil];
}

- (BOOL)openApplicationURL:(NSNotification *)notification
{
    [self log:@"openApplicationURL"];
    BOOL wasHandled = NO;
    //wasHandled = [[SCLFlicManager sharedManager] handleOpenURL:url];
    return wasHandled;
}

- (void) init:(CDVInvokedUrlCommand*)command
{
    [self log:@"init"];
    
	NSDictionary *config = [command.arguments objectAtIndex:0];
	NSString* APP_ID = [config objectForKey:@"appId"];
	NSString* APP_SECRET = [config objectForKey:@"appSecret"];
	
    self.flicManager = [SCLFlicManager configureWithDelegate:self defaultButtonDelegate:self appID:APP_ID appSecret:APP_SECRET backgroundExecution:NO];
    
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:[self knownButtons]];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId ];
}

- (void) getKnownButtons:(CDVInvokedUrlCommand*)command
{
    [self log:@"getKnownButtons"];
    
    CDVPluginResult* result;
    // in case plugin is not initialized
    if (self.flicManager == nil) {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:pluginNotInitializedMessage];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        return;
    }
    
	result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:[self knownButtons]];
	[self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void) grabButton:(CDVInvokedUrlCommand*)command
{
    [self log:@"grabButton"];
    
    CDVPluginResult* result;
    // in case plugin is not initialized
    if (self.flicManager == nil) {
        [self log:@"flicManager is null"];
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:pluginNotInitializedMessage];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        return;
    }
    
	[[SCLFlicManager sharedManager] grabFlicFromFlicAppWithCallbackUrlScheme:@"winfleet-tracker"];
	result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
	[self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void) waitForButtonEvent:(CDVInvokedUrlCommand*)command
{
    // do not use it
    return;
}

- (void) triggerButtonEvent:(CDVInvokedUrlCommand*)command
{
    // do not use it
    return;
}

- (void) onButtonClick:(CDVInvokedUrlCommand *)command
{
    self.onButtonClickCallbackId = command.callbackId;
    return;
}

// button received
- (void)flicManager:(SCLFlicManager *)manager didGrabFlicButton:(SCLFlicButton *)button withError:(NSError *)error;
{
    if(error)
    {
        [self log:@"Could not grab"];
    }
    
    [self log:@"Grabbed button"];
    
    // un-comment the following line if you need lower click latency for your application
    // this will consume more battery so don't over use it
    // button.lowLatency = YES;
    //[self updateUI];
}

// button was unregistered
- (void)flicManager:(SCLFlicManager *)manager didForgetButton:(NSUUID *)buttonIdentifier error:(NSError *)error;
{
    [self log:@"Unregistered button"];
    //[self updateUI];
}

// button was clicked
- (void)flicButton:(SCLFlicButton *)button didReceiveButtonDown:(BOOL)queued age:(NSInteger)age;
{
    [self log:@"didReceiveButtonDown"];
    
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:queued];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:self.onButtonClickCallbackId];
}

// button was clicked
- (void)flicButton:(SCLFlicButton *)button didReceiveButtonClick:(BOOL)queued age:(NSInteger)age;
{
    [self log:@"didReceiveButtonClick"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:queued];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:self.onButtonClickCallbackId];
}

- (NSMutableArray*)knownButtons
{
    NSMutableArray *buttons = [[NSMutableArray alloc] init];
    
    NSLog(@"get knownButtons");
    
    NSArray * kButtons = [[SCLFlicManager sharedManager].knownButtons allValues];
    for (SCLFlicButton *button in kButtons) {
        NSMutableDictionary* b = [NSMutableDictionary dictionaryWithCapacity:2];
        
        NSLog(@"buttonId: %@", button.buttonIdentifier);
        
        [b setObject:@"test" forKey:@"buttonId"];
        [b setObject:@"yellow" forKey:@"color"];
        [buttons addObject:b];
    }
    
    return buttons;
}

- (BOOL)handleOpenURL:(NSURL *)url
{
    NSLog(@"handleOpenURL %@", url);
    return YES;
}

-(void)log:(NSString *)text
{
    NSLog(@"%@%@", TAG, text);
}

@end

