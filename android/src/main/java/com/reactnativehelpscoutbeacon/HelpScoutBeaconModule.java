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
import com.helpscout.beacon.model.PreFilledForm;
import com.helpscout.beacon.model.SuggestedArticle;
import com.helpscout.beacon.ui.BeaconActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
    String rawFocusMode = rawSettings.hasKey("focusMode") ? rawSettings.getString("focusMode") : "invalid";
    FocusMode focusMode;
    switch(rawFocusMode) {
      case "neutral": focusMode = FocusMode.NEUTRAL; break;
      case "self-service": focusMode = FocusMode.SELF_SERVICE; break;
      case "ask-first": focusMode = FocusMode.ASK_FIRST; break;
      default: focusMode = null; break;
    }
    BeaconConfigOverrides configOverrides = new BeaconConfigOverrides(
      rawSettings.hasKey("docsEnabled") ? rawSettings.getBoolean("docsEnabled") : null,
      rawSettings.hasKey("messagingEnabled") ? rawSettings.getBoolean("messagingEnabled") : null,
      rawSettings.hasKey("chatEnabled") ? rawSettings.getBoolean("chatEnabled") : null,
      null,
      rawSettings.hasKey("color") ? rawSettings.getString("color") : null,
      focusMode,
      rawSettings.hasKey("enablePreviousMessages") ? rawSettings.getBoolean("enablePreviousMessages") : true
    );
    return configOverrides;
  }

  private BeaconUser extractBeaconUserFromIdentity(ReadableMap identity) {
    if(identity == null) {
      return null;
    }

    Map<String, String> attributes = new HashMap<>();
    ReadableMap rawAttributes = identity.hasKey("attributes") ? identity.getMap("attributes") : null;
    if(rawAttributes != null) {
      ReadableMapKeySetIterator iterator = rawAttributes.keySetIterator();
      while(iterator.hasNextKey()) {
        String key = iterator.nextKey();
        attributes.put(key, rawAttributes.getString(key));
      }
    }

    BeaconUser user = new BeaconUser(
      identity.getString("email"),
      identity.hasKey("name") ? identity.getString("name") : null,
      identity.hasKey("company") ? identity.getString("company") : null,
      identity.hasKey("jobTitle") ? identity.getString("jobTitle") : null,
      identity.hasKey("avatar") ? identity.getString("avatar") : null,
      attributes
    );

    return user;
  }

  private BeaconUser extractBeaconUserFromSettings(ReadableMap rawSettings) {
    ReadableMap identity = rawSettings.getMap("identity");
    return extractBeaconUserFromIdentity(identity);
  }

  private void configureBeacon(ReadableMap rawSettings) {
    Beacon.Builder builder = new Beacon.Builder()
      .withBeaconId(rawSettings.getString("beaconId"));
    if(rawSettings.hasKey("logsEnabled")) {
      builder = builder.withLogsEnabled(rawSettings.getBoolean("logsEnabled"));
    }
    builder.build();
    BeaconConfigOverrides configOverrides = extractBeaconSettings(rawSettings);
    Beacon.setConfigOverrides(configOverrides);
  }

  private List<SuggestedArticle> extractBeaconSuggestions(ReadableArray rawSuggestions) {
    List<SuggestedArticle> suggestions = new ArrayList<>();

    for(int index = 0; index < rawSuggestions.size(); index++) {
      ReadableMap rawSuggestion = rawSuggestions.getMap(index);
      String type = rawSuggestion.getString("type");
      if(type == "link") {
        String link = rawSuggestion.getString("link");
        String label = rawSuggestion.getString("label");
        suggestions.add(new SuggestedArticle.SuggestedArticleWithUrl(label, link));
      } else if(type == "article") {
        String articleId = rawSuggestion.getString("articleId");
        suggestions.add(new SuggestedArticle.SuggestedArticleWithId(articleId));
      } else {;
        // throw new Error("ASDF - Invalid suggestion type: " + type);
      }
    }

    return suggestions;
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
    promise.resolve(null);
  }

  @ReactMethod
  public void identify(ReadableMap identity, Promise promise) {
    BeaconUser user = extractBeaconUserFromIdentity(identity);
    if(user != null) {
      Beacon.identify(user.getEmail(), user.getName(), user.getCompany(), user.getJobTitle(), user.getAvatar());
    }
    promise.resolve(null);
  }

  @ReactMethod
  public void logout(Promise promise) {
    Beacon.logout();
    promise.resolve(null);
  }

  @ReactMethod
  public void registerPushNotificationToken(String token, Promise promise) {
    Beacon.setFirebaseCloudMessagingToken(token);
    promise.resolve(null);
  }

  @ReactMethod
  public void suggest(ReadableArray suggestions, Promise promise) {
    List<SuggestedArticle> beaconSuggestions = extractBeaconSuggestions(suggestions);
    Beacon.setOverrideSuggestedArticlesOrLinks(beaconSuggestions);
    promise.resolve(null);
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
    promise.resolve(null);
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
    promise.resolve(null);
  }

  @ReactMethod
  public void prefillContactForm(ReadableMap formData, Promise promise) {
    ReadableMap customFieldValues = formData.getMap("customFieldValues");
    Map<Integer, String> attributes = new HashMap<>();
    ReadableMapKeySetIterator iterator = customFieldValues.keySetIterator();
    while(iterator.hasNextKey()) {
      String key = iterator.nextKey();
      attributes.put(Integer.parseInt(key), customFieldValues.getString(key));
    }
    PreFilledForm prefilled = new PreFilledForm(
      formData.getString("name"),
      formData.getString("subject"),
      formData.getString("message"),
      attributes,
      (List) formData.getArray("attachments").toArrayList(),
      formData.getString("email")
    );
    Beacon.addPreFilledForm(prefilled);
    promise.resolve(null);
  }

  @ReactMethod
  public void resetContactForm(Promise promise) {
    Beacon.contactFormReset();
  }

  @ReactMethod
  public void resetPrefilledForm(Promise promise) {
    Beacon.prefilledFormReset();
  }

}
