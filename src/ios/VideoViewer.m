/********* VideoViewer.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>

NSString* STR_ERR_MISSING_PARAMS = @"Missing parameters";
NSString* STR_ERR_INVALID_SRC = @"Invalid file src.";
int INT_ERR_MISSING_PARAMS = -1;
int INT_ERR_INVALID_SRC = 5;

@interface VideoViewer : CDVPlugin <UIDocumentInteractionControllerDelegate> {
  // Member variables go here.
}

@property (nonatomic, strong) UIDocumentInteractionController *docInteractionController;

- (void)show:(CDVInvokedUrlCommand*)command;
@end

@implementation VideoViewer

- (void)setupDocumentControllerWithURL:(NSURL *)url andTitle:(NSString *)title
{
    if (self.docInteractionController == nil) {
        self.docInteractionController = [UIDocumentInteractionController interactionControllerWithURL:url];
        self.docInteractionController.name = title;
        self.docInteractionController.delegate = self;
    } else {
        self.docInteractionController.name = title;
        self.docInteractionController.URL = url;
    }
}

- (UIDocumentInteractionController *) setupControllerWithURL: (NSURL*) fileURL
                                               usingDelegate: (id <UIDocumentInteractionControllerDelegate>) interactionDelegate {

    UIDocumentInteractionController *interactionController = [UIDocumentInteractionController interactionControllerWithURL: fileURL];
    interactionController.delegate = interactionDelegate;

    return interactionController;
}

- (UIViewController *) documentInteractionControllerViewControllerForPreview:(UIDocumentInteractionController *) controller {
    return self.viewController;
}

- (void)show:(CDVInvokedUrlCommand*)command
{
    NSDictionary* params = [command.arguments objectAtIndex:0];
    if (params != nil && [params count] > 0) {
        NSString* src = [params objectForKey:@"src"];
        NSString* title = [params objectForKey:@"title"];
        if (title == nil || [title length] == 0) {
            title = [src componentsSeparatedByString:(@"/")][[src componentsSeparatedByString:(@"/")].count - 1];
        }
        if ([self isValidSrc:src]) {
            UIActivityIndicatorView *activityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:self.viewController.view.frame];
            [activityIndicator setActivityIndicatorViewStyle:UIActivityIndicatorViewStyleWhiteLarge];
            [activityIndicator.layer setBackgroundColor:[[UIColor colorWithWhite:0.0 alpha:0.30] CGColor]];
            CGPoint center = self.viewController.view.center;
            activityIndicator.center = center;
            [self.viewController.view addSubview:activityIndicator];
            
            [activityIndicator startAnimating];

            NSURL* URL = [NSURL URLWithString:src];
            
            if (URL != nil){
                [self setupDocumentControllerWithURL:URL andTitle:title];
                double delayInSeconds = 0.1;
                dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
                dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
                    [activityIndicator stopAnimating];
                    [self.docInteractionController presentPreviewAnimated:YES];
                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"OK"] callbackId:command.callbackId];
                });
            }
            else {
                [self handleError:command error:STR_ERR_INVALID_SRC errorCode:INT_ERR_INVALID_SRC];
            }
        }
        else {
            [self handleError:command error:STR_ERR_INVALID_SRC errorCode:INT_ERR_INVALID_SRC];
        }
    } else {
        [self handleError:command error:STR_ERR_MISSING_PARAMS errorCode:INT_ERR_MISSING_PARAMS];
    }
}

- (bool)isValidSrc:(NSString*)src
{
    NSArray* startURI = @[@"file"];//, @"http", @"https"];
    NSString* start = [src componentsSeparatedByString:(@"://")][0];
    return [startURI containsObject:(start)];
}

- (void)handleError:(CDVInvokedUrlCommand*)command error:(NSString*)errorMessage errorCode:(int)errorCode
{
    NSDictionary *result = [[NSDictionary alloc] init];
    result = @{@"error": errorMessage, @"code": @(errorCode)};
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:result];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end