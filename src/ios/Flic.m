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
static NSString * const BUTTON_EVENT_SINGLECLICK = @"singleClick";
static NSString * const BUTTON_EVENT_DOUBLECLICK = @"doubleClick";
static NSString * const BUTTON_EVENT_HOLD = @"hold";
@synthesize onButtonClickCallbackId;

- (void)pluginInitialize
{
    [self log:@"pluginInitialize"];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleOpenURL:) name:@"flicApp" object:nil];
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
        NSLog(@"Could not grab: %@", error);
    }
    
    [self log:@"Grabbed button"];
}

// button was unregistered
- (void)flicManager:(SCLFlicManager *)manager didForgetButton:(NSUUID *)buttonIdentifier error:(NSError *)error;
{
    [self log:@"Unregistered button"];
}

// button was clicked
- (void)flicButton:(SCLFlicButton *)button didReceiveButtonClick:(BOOL)queued age:(NSInteger)age
{
    [self log:@"didReceiveButtonClick"];
    
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[self getButtonEventObject:BUTTON_EVENT_SINGLECLICK button:button]];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:self.onButtonClickCallbackId];
}

// button was double clicked
- (void)flicButton:(SCLFlicButton *)button didReceiveButtonDoubleClick:(BOOL)queued age:(NSInteger)age
{
    [self log:@"didReceiveButtonDoubleClick"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[self getButtonEventObject:BUTTON_EVENT_DOUBLECLICK button:button]];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:self.onButtonClickCallbackId];
}

// button was hold
- (void)flicButton:(SCLFlicButton *)button didReceiveButtonHold:(BOOL)queued age:(NSInteger)age
{
    [self log:@"didReceiveButtonHold"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[self getButtonEventObject:BUTTON_EVENT_HOLD button:button]];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:self.onButtonClickCallbackId];
}

- (NSDictionary*)getButtonEventObject:(NSString *)event button:(SCLFlicButton *)button
{
    // not yet implemented
    NSDictionary *buttonResult = @{
                                   @"buttonId": @"test",
                                   @"color": button.color.description
                                   };
    NSDictionary *result = @{
                   @"event": event,
                   @"button": buttonResult
                };
    
    return result;
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
        [b setObject:[button.color description] forKey:@"color"];
        [buttons addObject:b];
    }
    
    return buttons;
}

- (void)handleOpenURL:(NSNotification*)notification
{
    NSURL* url = [notification object];
    
    if ([url isKindOfClass:[NSURL class]]) {
        [[SCLFlicManager sharedManager] handleOpenURL:url];
        
        NSLog(@"handleOpenURL %@", url);
    }
}

-(void)log:(NSString *)text
{
    NSLog(@"%@%@", TAG, text);
}

@end

