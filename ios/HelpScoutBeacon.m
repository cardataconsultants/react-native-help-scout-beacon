#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(HelpScoutBeacon, NSObject)

RCT_EXTERN_METHOD(open:(NSDictionary *)settings
                 signature:(NSString *)signature
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(identify:(NSDictionary *)identity
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(logout:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(registerPushNotificationToken:(NSString *)token
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(suggest:(NSArray *)suggestions
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(navigate:(NSString *)suggestions
                  settings:(NSDictionary *)settings
                  signature:(NSString *)signature
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(search:(NSString *)query
                  settings:(NSDictionary *)settings
                  signature:(NSString *)signature
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)


+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
