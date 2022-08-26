# react-native-help-scout-beacon

A react-native implementation/bridge of the helpscout-beacon package.

## Installation

```sh
yarn add react-native-help-scout-beacon
```

### iOS Setup

#### Photos

The attachments menu for sending a message has code to allow users to take a photo or select one from their photo library. Even if you have attachments disabled, Apple flags usage of these APIs; so, it requires a description string in the app’s `Info.plist` file.

The required settings are `NSPhotoLibraryUsageDescription`, `NSCameraUsageDescription`, `NSMicrophoneUsageDescription` and `NSPhotoLibraryAddUsageDescription`.

#### Documents

To access the documents picker in the attachment menu you must create an iCloud container that matches your app’s bundle ID.

#### Push Notifications

If you do not already have push notifications active in your application, you may receive a warning when uploading to App Store Connect. Similar to Photos, App Store Connect flags usage of these APIs in code independent of whether you invoke them or not.
To support push notifications, perform the following setup:

- When the app starts up, initialize the notification system for Beacon by calling `HSBeacon.initializeBeaconPushNotificationSupport()` (imported from the Beacon module)
- When failed to register to remote notifications let Beacon knows by calling `HSBeacon.failedToRegisterForRemoteNotificationsWithError(error)`.
- When successfully received a push token, you can set it natively by calling `HSBeacon.setDeviceToken(token)` or using the JS method `Beacon.registerPushNotificationToken(token)`.
- Add the following to your `application:didReceiveRemoteNotification` for correctly handling incoming push notifications:

```swift
if HSBeacon.isBeaconPushNotification(userInfo) {
  HSBeacon.handlePushNotification(userInfo, beaconSettings: settings)
}
```

### Android Setup

- When successfully received a push token, you can set it natively by calling `Beacon.setFirebaseCloudMessagingToken(token)` or using the JS method `Beacon.registerPushNotificationToken(token)`.

- Add the following to your `onMessageReceived(RemoteMessage)` for correctly handling incoming push notifications:

```java
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
      if (remoteMessage.getData().size() > 0) {
        BeaconPushNotificationsProcessor beaconPushNotificationsProcessor = new BeaconPushNotificationsProcessor()
          if (BeaconPushNotificationsProcessor.isBeaconNotification(remoteMessage.getData())) {
              beaconPushNotificationsProcessor.process(this, remoteMessage.getData());
          } else {
              // here can process your own push messages
          }
      }
  }
```

## Usage

```js
import Beacon from 'react-native-help-scout-beacon';

// ...

Beacon.open({ beaconId: '<beacon_id>' });
```

## Feature Support as of v1

### iOS

- [x] Open
  - [x] Normal Mode
  - [x] Safe Mode
- [x] Authenticate Users
  - [x] User attributes
- [x] Logout
- [ ] Session attributes
- [x] Contact form
  - [x] Prefilling
    - [ ] Attachments
    - [x] Custom fields
  - [x] Resetting
- [x] Navigate to a screen
  - [x] Open with search results
  - [x] Open to an article
  - [x] Open to the contact form
  - [x] Previous messages
  - [x] Open chat
  - [x] Open ask
- [x] Custom Suggestions
- [ ] Open and close events
- [x] Push Notifications

### Android

- [x] Open
  - [x] Normal Mode
  - [x] Safe Mode
- [x] Authenticate Users
  - [x] User attributes
- [x] Logout
- [ ] Session attributes
- [x] Contact form
  - [x] Prefilling
    - [x] Attachments
    - [x] Custom fields
  - [x] Resetting
- [x] Navigate to a screen
  - [x] Open with search results
  - [x] Open to an article
  - [x] Open to the contact form
  - [x] Previous messages
  - [x] Open chat
  - [x] Open ask
- [x] Custom Suggestions
- [ ] Open and close events
- [x] Push Notifications

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
