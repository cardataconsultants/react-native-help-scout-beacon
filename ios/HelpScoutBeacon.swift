import Beacon
import Foundation


@objc(HelpScoutBeacon)
class HelpScoutBeacon: NSObject {

    @objc(open:signature:withResolver:withRejecter:)
    func open(_ rawSettings: NSDictionary?, signature: NSString?, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        guard let rawSettings = rawSettings else {
          reject("missing-settings", "Missing settings. The beacon id is obligatory.", nil)
          return
        }
        
        let settings = HSBridgeHelper.extractBeaconSettings(from: rawSettings)
        DispatchQueue.main.async {
            if let signature = signature {
                HSBeacon.open(settings, signature: String(signature))
            } else {
                HSBeacon.open(settings)
            }
        }
        
        if let user = HSBridgeHelper.extractBeaconUser(fromSettings: rawSettings) {
            DispatchQueue.main.async {
                HSBeacon.identify(user)
            }
        }
        resolve(nil)
    }
    
    @objc(identify:withResolver:withRejecter:)
    func identify(_ identity: NSDictionary?, resolve:RCTPromiseResolveBlock, reject:RCTPromiseRejectBlock) -> Void {
        guard let identity = identity, let user = HSBridgeHelper.extractBeaconUser(fromIdentity: identity) else {
          reject("missing-identity", "Missing or invalid identity.", nil)
          return
        }
        
        DispatchQueue.main.async {
            HSBeacon.identify(user)
        }
        resolve(nil)
    }
    
    @objc(logout:withRejecter:)
    func logout(_ resolve:RCTPromiseResolveBlock, reject:RCTPromiseRejectBlock) -> Void {
        DispatchQueue.main.async {
            HSBeacon.logout()
        }
        resolve(nil)
    }
    
    @objc(registerPushNotificationToken:withResolver:withRejecter:)
    func registerPushNotificationToken(_ token: NSString?, resolve:RCTPromiseResolveBlock, reject:RCTPromiseRejectBlock) -> Void {
        guard let token = token, let tokenNsData = token.data(using: String.Encoding.utf8.rawValue) else {
          reject("missing-token", "Missing or invalid token.", nil)
          return
        }
        
        DispatchQueue.main.async {
            HSBeacon.setDeviceToken(Data(tokenNsData))
        }
        
        resolve(nil)
    }
    
    @objc(suggest:withResolver:withRejecter:)
    func suggest(_ suggestions: NSArray?, resolve:RCTPromiseResolveBlock, reject:RCTPromiseRejectBlock) -> Void {
        guard let suggestions = suggestions else {
          reject("missing-suggestions", "Missing or invalid suggestions.", nil)
          return
        }
        
        let beaconSuggestions = HSBridgeHelper.extractBeaconSuggestions(from: suggestions)
        
        DispatchQueue.main.async {
            HSBeacon.suggest(with: beaconSuggestions)
        }
        resolve(nil)
    }
    
    @objc(navigate:settings:signature:articleId:withResolver:withRejecter:)
    func navigate(_ jsRoute: NSString?, settings rawSettings: NSDictionary?, signature: NSString?, articleId: NSString?,  resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        guard let rawSettings = rawSettings else {
          reject("missing-settings", "Missing settings. The beacon id is obligatory.", nil)
          return
        }
        guard let jsRoute = jsRoute else {
          reject("missing-route", "Missing route.", nil)
          return
        }
        
        let route = BeaconRoute.from(jsRoute: String(jsRoute), articleId: articleId != nil ? String(articleId!) : nil)
        
        let settings = HSBridgeHelper.extractBeaconSettings(from: rawSettings)
        DispatchQueue.main.async {
            if let signature = signature {
                HSBeacon.navigate(route.route, beaconSettings: settings, signature: String(signature))
            } else {
                HSBeacon.navigate(route.route, beaconSettings: settings)
            }
        }
        
        if let user = HSBridgeHelper.extractBeaconUser(fromSettings: rawSettings) {
            DispatchQueue.main.async {
                HSBeacon.identify(user)
            }
        }
        resolve(nil)
    }
    
    @objc(search:settings:signature:withResolver:withRejecter:)
    func search(_ query: NSString?, settings rawSettings: NSDictionary?, signature: NSString?, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        guard let rawSettings = rawSettings else {
          reject("missing-settings", "Missing settings. The beacon id is obligatory.", nil)
          return
        }
        guard let query = query else {
          reject("missing-route", "Missing route.", nil)
          return
        }
        
        let settings = HSBridgeHelper.extractBeaconSettings(from: rawSettings)
        DispatchQueue.main.async {
            if let signature = signature {
                HSBeacon.search(String(query), beaconSettings: settings, signature: String(signature))
            } else {
                HSBeacon.search(String(query), beaconSettings: settings)
            }
        }
        
        if let user = HSBridgeHelper.extractBeaconUser(fromSettings: rawSettings) {
            DispatchQueue.main.async {
                HSBeacon.identify(user)
            }
        }
        resolve(nil)
    }
}
