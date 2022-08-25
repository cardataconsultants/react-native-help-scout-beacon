import Beacon
import Foundation

extension NSDictionary {
    
    func ifExists<T>(key: String, ofType type: T.Type = T.self, callback: (T) -> Void) {
        if let value = self.value(forKey: key) as? T {
            callback(value)
        }
    }
    
}

extension UIColor {
    
    static func from(hex: String) -> UIColor {
        var cString:String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()

        if (cString.hasPrefix("#")) {
            cString.remove(at: cString.startIndex)
        }

        if ((cString.count) != 6) {
            return UIColor.gray
        }

        var rgbValue:UInt64 = 0
        Scanner(string: cString).scanHexInt64(&rgbValue)

        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
    
}

extension BeaconRoute {
    
    static func from(jsRoute: String, articleId: String? = nil) -> BeaconRoute {
        switch jsRoute {
        case "home": return .home
        case "article": return .article(articleId!)
        case "contact": return .askMessage
        case "chat": return .askChat
        case "ask": return .ask
        case "previous-messages": return .previousMessages
        default: fatalError("Invalid route: " + jsRoute);
        }
    }
    
}

final class HSBridgeHelper {
    
    private init() {}
    
    static func extractBeaconUser(fromSettings settings: NSDictionary) -> HSBeaconUser? {
        guard let identity = settings.value(forKey: "identity") as? NSDictionary else {
            return nil
        }
        
        return extractBeaconUser(fromIdentity: identity)
    }
    
    static func extractBeaconUser(fromIdentity identity: NSDictionary) -> HSBeaconUser? {
        let user = HSBeaconUser()
        user.email = identity.value(forKey: "email") as? String
        user.name = identity.value(forKey: "name") as? String
        user.company = identity.value(forKey: "company") as? String
        user.jobTitle = identity.value(forKey: "jobTitle") as? String
        
        if let avatar = identity.value(forKey: "avatar") as? String, let avatarUrl = URL(string: avatar) {
            user.avatar = avatarUrl
        }
        
        if let attributes = identity.value(forKey: "attributes") as? NSDictionary {
            attributes.forEach {
                if let key = $0 as? String, let value = $1 as? String {
                    user.addAttribute(withKey: key, value: value)
                }
            }
        }
        
        return user
    }
    
    static func extractBeaconSettings(from rawSettings: NSDictionary) -> HSBeaconSettings {
        guard let beaconId = rawSettings.value(forKey: "beaconId") as? String else {
            fatalError("Missing required beaconId on the settings")
        }
        
        let settings = HSBeaconSettings(beaconId: beaconId)
        rawSettings.ifExists(key: "docsEnabled") { settings.docsEnabled = $0 }
        rawSettings.ifExists(key: "messagingEnabled") { settings.messagingEnabled = $0 }
        rawSettings.ifExists(key: "enablePreviousMessages") { settings.enablePreviousMessages = $0 }
        rawSettings.ifExists(key: "beaconTitle") { settings.beaconTitle = $0 }
        rawSettings.ifExists(key: "tintColorOverride", ofType: String.self) { settings.tintColorOverride = UIColor.from(hex: $0) }
        rawSettings.ifExists(key: "color", ofType: String.self) { settings.color = UIColor.from(hex: $0) }
        rawSettings.ifExists(key: "useNavigationBarAppearance") { settings.useNavigationBarAppearance = $0 }
        rawSettings.ifExists(key: "useLocalTranslationOverrides") { settings.useLocalTranslationOverrides = $0 }
        rawSettings.ifExists(key: "focusMode", ofType: String.self) {
            switch($0) {
            case "neutral": settings.focusModeOverride = .neutral
            case "self-service": settings.focusModeOverride = .selfService
            case "ask-first": settings.focusModeOverride = .askFirst
            default: fatalError("Invalid focus mode: " + $0)
            }
        }
        
        return settings
    }
    
    static func extractBeaconSuggestions(from array: NSArray) -> [HSBeaconSuggestionItem] {
        let allSuggestions: [HSBeaconSuggestionItem?] = array.map { suggestionRaw in
            guard let suggestion = suggestionRaw as? NSDictionary, let type = suggestion.value(forKey: "type") as? String else {
                return nil
            }
            
            if type == "link" {
                guard
                    let link = suggestion.value(forKey: "link") as? String,
                    let url = URL(string: link),
                    let label = suggestion.value(forKey: "label") as? String
                else {
                    return nil
                }
                return HSBeaconLinkSuggestion(url: url, text: label)
            } else if type == "article" {
                guard let articleId = suggestion.value(forKey: "articleId") as? String else {
                    return nil
                }
                return HSBeaconArticleSuggestion(id: articleId)
            } else {
                fatalError("Suggestion type not supported: " + type)
            }
        }
        
        let validSuggestions = allSuggestions.filter { $0 != nil } as! [HSBeaconSuggestionItem]
        return validSuggestions
    }
    
}
