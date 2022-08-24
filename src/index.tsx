import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-help-scout-beacon' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const HelpScoutBeacon = NativeModules.HelpScoutBeacon
  ? NativeModules.HelpScoutBeacon
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export declare module HelpScoutBeacon {
  export type FocusMode = 'neutral' | 'self-service' | 'ask-first';

  export type Route =
    | 'home'
    | 'article'
    | 'contact'
    | 'chat'
    | 'ask'
    | 'previous-messages';

  export interface Settings {
    beaconId: string;
    identity?: Identity;

    docsEnabled?: boolean;
    messagingEnabled?: boolean;
    chatEnabled?: boolean;
    focusMode?: FocusMode;
    color?: string;
    enablePreviousMessages?: boolean;

    //android
    logsEnabled?: boolean;
    //ios
    beaconTitle?: string;
    tintColorOverride?: string;
    useLocalTranslationOverrides?: boolean;
    useNavigationBarAppearance?: boolean;
  }

  export interface Identity {
    email: string;
    name?: string;
    company?: string;
    jobTitle?: string;
    avatar?: string;
    attributes?: Record<string, string>;
  }

  export interface Suggestion {
    type: 'link' | 'article';

    //link only
    link?: string;
    label?: string;

    //article only
    articleId?: string;
  }
}

export default {
  /**
   * Opens the helpscout beacon interface.
   *
   * If you provide a valid signature, you can use it to authenticate a user in Secure Mode and retrieve their previous conversations.
   * Note: you should not store the secret key in the app; instead, your server should provide the computed signature value.
   *
   * If you provide a identity, the identify function will be called automatically.
   *
   * @param settings The settings used to show the beacon
   * @param signature The signature to be used in Secure Mode. Can be undefined for Normal Mode usage.
   */
  open: (settings: HelpScoutBeacon.Settings, signature?: string) => {
    HelpScoutBeacon.open(settings, signature);
  },
  /**
   * Authenticates the user. You can provide the name and email address to pre-populate and hide the fields on the ‘Create a message’ screen.
   *
   * @param identity the email is required.
   */
  identify: (identity: HelpScoutBeacon.Identity) => {
    HelpScoutBeacon.identify(identity);
  },
  /**
   * Calling this method resets the current Beacon state, and clears the following data stored in the app-specific Keychain:
   *  - Device ID (UUID generated specifically for Beacon to allow access to Previous Messages from this device only)
   *  - Name
   *  - Email
   */
  logout: () => {
    HelpScoutBeacon.logout();
  },
  /**
   * Registers a token for receiving push notifications related to helpscout.
   * @param token the push notification token received from APNS or GCM
   */
  registerPushNotificationToken: (token: string) => {
    HelpScoutBeacon.registerPushNotificationToken(token);
  },
  /**
   * Suggests links or articles for the user
   * @param suggestions the suggestions array
   */
  suggest: (suggestions: HelpScoutBeacon.Suggestion[]) => {
    HelpScoutBeacon.suggest(suggestions);
  },
  /**
   * This method opens the Beacon and shows a specific screen
   *
   * If you provide a valid signature, you can use it to authenticate a user in Secure Mode and retrieve their previous conversations.
   * Note: you should not store the secret key in the app; instead, your server should provide the computed signature value.
   * @param route The route to navigate to
   * @param articleId The articleId (required when route is article)
   * @param settings The settings used to show the beacon
   * @param signature The signature to be used in Secure Mode. Can be undefined for Normal Mode usage.
   */
  navigate: (
    route: string,
    settings: HelpScoutBeacon.Settings,
    signature?: string,
    articleId?: string
  ) => {
    HelpScoutBeacon.navigate(route, settings, signature, articleId);
  },
  /**
   * This method opens the Beacon, searches docs articles and loads the results screen
   *
   * If you provide a valid signature, you can use it to authenticate a user in Secure Mode and retrieve their previous conversations.
   * Note: you should not store the secret key in the app; instead, your server should provide the computed signature value.
   *
   * @param query The search query
   * @param settings The settings used to show the beacon
   * @param signature The signature to be used in Secure Mode. Can be undefined for Normal Mode usage.
   */
  search: (
    query: string,
    settings: HelpScoutBeacon.Settings,
    signature?: string
  ) => {
    HelpScoutBeacon.search(query, settings, signature);
  },
};
