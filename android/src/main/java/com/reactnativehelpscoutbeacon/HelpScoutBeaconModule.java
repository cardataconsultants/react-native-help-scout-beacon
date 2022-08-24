package com.reactnativehelpscoutbeacon;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.module.annotations.ReactModule;
import com.helpscout.beacon.Beacon;
import com.helpscout.beacon.internal.core.model.ContactFormConfigApi;
import com.helpscout.beacon.model.BeaconConfigOverrides;
import com.helpscout.beacon.model.BeaconScreens;
import com.helpscout.beacon.model.BeaconUser;
import com.helpscout.beacon.model.FocusMode;
import com.helpscout.beacon.ui.BeaconActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ReactModule(name = HelpScoutBeaconModule.NAME)
public class HelpScoutBeaconModule extends ReactContextBaseJavaModule {
  public static final String NAME = "HelpScoutBeacon";

  public HelpScoutBeaconModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  private BeaconConfigOverrides extractBeaconSettings(ReadableMap rawSettings) {
    boolean messagingEnabled = rawSettings.getBoolean("messagingEnabled");
    boolean docsEnabled = rawSettings.getBoolean("docsEnabled");
    boolean chatEnabled = rawSettings.getBoolean("chatEnabled");
    String color = rawSettings.getString("color");
    String rawFocusMode = rawSettings.getString("focusMode");
    FocusMode focusMode;
    switch(rawFocusMode) {
      case "neutral": focusMode = FocusMode.NEUTRAL; break;
      case "self-service": focusMode = FocusMode.SELF_SERVICE; break;
      case "ask-first": focusMode = FocusMode.ASK_FIRST; break;
      default: throw new Error("Invalid focus mode: " + rawFocusMode);
    }
    boolean enablePreviousMessages = rawSettings.getBoolean("enablePreviousMessages");
    BeaconConfigOverrides configOverrides = new BeaconConfigOverrides(
      docsEnabled, messagingEnabled, chatEnabled, null, color, focusMode, enablePreviousMessages);
    return configOverrides;
  }

  private BeaconUser extractBeaconUserFromIdentity(ReadableMap identity) {
    if(identity == null) {
      return null;
    }

    Map<String, String> attributes = new HashMap<>();
    ReadableMap rawAttributes = identity.getMap("attributes");
    if(rawAttributes != null) {
      ReadableMapKeySetIterator iterator = rawAttributes.keySetIterator();
      while(iterator.hasNextKey()) {
        String key = iterator.nextKey();
        attributes.put(key, rawAttributes.getString(key));
      }
    }

    BeaconUser user = new BeaconUser(
      identity.getString("email"),
      identity.getString("name"),
      identity.getString("company"),
      identity.getString("jobTitle"),
      identity.getString("avatar"),
      attributes
    );

    return user;
  }

  private BeaconUser extractBeaconUserFromSettings(ReadableMap rawSettings) {
    ReadableMap identity = rawSettings.getMap("identity");
    return extractBeaconUserFromIdentity(identity);
  }

  private void configureBeacon(ReadableMap rawSettings) {
    Beacon beacon = new Beacon.Builder()
      .withBeaconId(rawSettings.getString("beaconId"))
      .withLogsEnabled(rawSettings.getBoolean("logsEnabled"))
      .build();
    BeaconConfigOverrides configOverrides = extractBeaconSettings(rawSettings);
    Beacon.setConfigOverrides(configOverrides);
  }

  @ReactMethod
  public void open(ReadableMap rawSettings, String signature, Promise promise) {
    configureBeacon(rawSettings);

    if(signature != null) {
      BeaconActivity.openInSecureMode(getReactApplicationContext(), signature);
    } else {
      BeaconActivity.open(getReactApplicationContext());
    }

    BeaconUser user = extractBeaconUserFromSettings(rawSettings);
    if(user != null) {
      Beacon.identify(user.getEmail(), user.getName(), user.getCompany(), user.getJobTitle(), user.getAvatar());
    }
  }

  @ReactMethod
  public void identify(ReadableMap identity, Promise promise) {
    BeaconUser user = extractBeaconUserFromIdentity(identity);
    if(user != null) {
      Beacon.identify(user.getEmail(), user.getName(), user.getCompany(), user.getJobTitle(), user.getAvatar());
    }
  }

  @ReactMethod
  public void logout(Promise promise) {
    Beacon.logout();
  }

  @ReactMethod
  public void registerPushNotificationToken(String token, Promise promise) {
    Beacon.setFirebaseCloudMessagingToken(token);
  }

  @ReactMethod
  public void suggest(ReadableArray suggestions, Promise promise) {

  }

  @ReactMethod
  public void navigate(String route, ReadableMap rawSettings, String signature, String articleId, Promise promise) {
    BeaconScreens screen;
    switch(route) {
      case "home": screen = BeaconScreens.DEFAULT; break;
      case "article": screen = BeaconScreens.ARTICLE_SCREEN; break;
      case "contact": screen = BeaconScreens.CONTACT_FORM_SCREEN; break;
      case "chat": screen = BeaconScreens.CHAT; break;
      case "ask": screen = BeaconScreens.ASK; break;
      case "previous-messages": screen = BeaconScreens.PREVIOUS_MESSAGES; break;
      default: throw new Error("Invalid route: " + route);
    }

    ArrayList<String> data = new ArrayList<>();
    if(route == "article") {
      data.add(articleId);
    }

    if(signature == null) {
      BeaconActivity.open(getReactApplicationContext(), screen, data);
    } else {
      BeaconActivity.openInSecureMode(getReactApplicationContext(), signature, BeaconScreens.SEARCH_SCREEN, data);
    }
  }

  @ReactMethod
  public void search(String query, ReadableMap rawSettings, String signature, Promise promise) {
    ArrayList<String> searchList = new ArrayList<String>();
    searchList.add(query);

    configureBeacon(rawSettings);

    if(signature == null) {
      BeaconActivity.open(getReactApplicationContext(), BeaconScreens.SEARCH_SCREEN, searchList);
    } else {
      BeaconActivity.openInSecureMode(getReactApplicationContext(), signature, BeaconScreens.SEARCH_SCREEN, searchList);
    }
  }

}
