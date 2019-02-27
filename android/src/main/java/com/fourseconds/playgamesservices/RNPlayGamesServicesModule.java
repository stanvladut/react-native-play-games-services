package com.fourseconds.playgamesservices;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNPlayGamesServicesModule extends ReactContextBaseJavaModule {

  public RNPlayGamesServicesModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "RNPlayGamesServices";
  }

  @ReactMethod
  public void test(Promise promise) {
    promise.resolve("activity is null");
  }
}