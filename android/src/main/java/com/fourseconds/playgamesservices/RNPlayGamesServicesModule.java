
package com.fourseconds.playgamesservices;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNPlayGamesServicesModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNPlayGamesServicesModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNPlayGamesServices";
  }
}