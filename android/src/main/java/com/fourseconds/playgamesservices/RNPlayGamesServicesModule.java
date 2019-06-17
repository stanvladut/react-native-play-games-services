package com.fourseconds.playgamesservices;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.ActivityEventListener;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import android.util.Log;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.Scopes;


import com.google.android.gms.games.Games;
import com.google.android.gms.games.AchievementsClient;

public class RNPlayGamesServicesModule extends ReactContextBaseJavaModule {
  private GoogleSignInClient mGoogleSignInClient;
  private AchievementsClient mAchievementsClient;
  public static final int RC_SIGN_IN = 9001;
  private static final int RC_ACHIEVEMENT_UI = 9003;

  private Promise signInPromise;
  private Promise achievementPromise;
  private Promise signInSilentlyPromise;

  static WritableMap getUserProperties(@NonNull GoogleSignInAccount acct) {
    Uri photoUrl = acct.getPhotoUrl();

    WritableMap user = Arguments.createMap();
    user.putString("id", acct.getId());
    user.putString("name", acct.getDisplayName());
    user.putString("givenName", acct.getGivenName());
    user.putString("familyName", acct.getFamilyName());
    user.putString("email", acct.getEmail());
    user.putString("photo", photoUrl != null ? photoUrl.toString() : null);

    WritableMap params = Arguments.createMap();
    params.putMap("user", user);
    params.putString("idToken", acct.getIdToken());
    params.putString("serverAuthCode", acct.getServerAuthCode());
    params.putString("accessToken", null);
    params.putString("accessTokenExpirationDate", null); // Deprecated as of 2018-08-06

    WritableArray scopes = Arguments.createArray();
    for (Scope scope : acct.getGrantedScopes()) {
        String scopeString = scope.toString();
        if (scopeString.startsWith("http")) {
            scopes.pushString(scopeString);
        }
    }
    params.putArray("scopes", scopes);
    return params;
  }

  public RNPlayGamesServicesModule(ReactApplicationContext reactContext) {
    super(reactContext);
    reactContext.addActivityEventListener(new RNGoogleSigninActivityEventListener());
  }

  @Override
  public String getName() {
    return "RNPlayGamesServices";
  }

  @ReactMethod
  public void init(final Promise promise) {
    GoogleSignInOptions.Builder optionsBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
    
    /** Create the signInClient for Games Sign In and no other options for the moment. */
    mGoogleSignInClient = GoogleSignIn.getClient(getReactApplicationContext(), optionsBuilder.build());
    promise.resolve(true);
  }

  @ReactMethod
  public void isSignedIn(Promise promise) {
      promise.resolve(GoogleSignIn.getLastSignedInAccount(getReactApplicationContext()) != null);
  }

  @ReactMethod
  public void getLastSignedInAccount(Promise promise) {
    promise.resolve(getUserProperties(GoogleSignIn.getLastSignedInAccount(getReactApplicationContext())));
  }

  private void handleSignInTaskResult(Task<GoogleSignInAccount> result) {
    try {
        GoogleSignInAccount account = result.getResult(ApiException.class);
        onConnected(account);
        WritableMap params = getUserProperties(account);
        signInSilentlyPromise.resolve(params);
    } catch (ApiException e) {
        int code = e.getStatusCode();
        String errorDescription = GoogleSignInStatusCodes.getStatusCodeString(code);
        signInSilentlyPromise.reject(String.valueOf(code), errorDescription);
    }
  }

  @ReactMethod
  public void signInSilently(Promise promise) {
    // promise may be still in progress
    signInSilentlyPromise = promise;

    if (mGoogleSignInClient == null) {
      promise.reject("There's no Google Sign In Client defined.");
      return;
    }

    if (getCurrentActivity() == null) {
      promise.reject("There's no Activity defined.");
      return;
    }
  
    Task<GoogleSignInAccount> result = mGoogleSignInClient.silentSignIn();
    if (result.isSuccessful()) {
        // There's immediate result available.
        handleSignInTaskResult(result);
    } else {
        result.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(Task task) {
                handleSignInTaskResult(task);
            }
        });
    }    
  }

  @ReactMethod
  public void signIn(Promise promise) {
    signInPromise = promise;
  
    if (mGoogleSignInClient == null) {
        promise.reject("There's no Google Sign In Client defined.");
        return;
    }

    if (getCurrentActivity() == null) {
        promise.reject("There's no Activity defined.");
        return;
    }

    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
    getCurrentActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
  }
  
  @ReactMethod
  public void showAchievements(final Promise promise) {
    if(mAchievementsClient == null) {
      promise.reject("Please sign in first");
      return;
    }

    achievementPromise = promise;

    mAchievementsClient.getAchievementsIntent()
      .addOnSuccessListener(new OnSuccessListener<Intent>() {
        @Override
        public void onSuccess(Intent intent) {
          getCurrentActivity().startActivityForResult(intent, RC_ACHIEVEMENT_UI);
        }
      })
      .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          promise.reject("Could not launch achievements intent");
        }
      });
  }

  private void onConnected(GoogleSignInAccount googleSignInAccount) {
    mAchievementsClient = Games.getAchievementsClient(getCurrentActivity(), googleSignInAccount);
    //TODO add these later
    //mLeaderboardsClient = Games.getLeaderboardsClient(this, googleSignInAccount);
    //mEventsClient = Games.getEventsClient(this, googleSignInAccount);
    //mPlayersClient = Games.getPlayersClient(getCurrentActivity(), googleSignInAccount);

  }

  private class RNGoogleSigninActivityEventListener extends BaseActivityEventListener {
    @Override
    public void onActivityResult(Activity activity, final int requestCode, final int resultCode, final Intent intent) {
      if (requestCode == RC_ACHIEVEMENT_UI && achievementPromise != null)
        achievementPromise.resolve("Achievement dialog complete");
      
      if (requestCode == RC_SIGN_IN) {
        // The Task returned from this call is always completed, no need to attach a listener.
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            WritableMap params = getUserProperties(account);
            
            onConnected(account);
            
            if (signInPromise != null) {
              signInPromise.resolve(params);
            }
        } catch (ApiException e) {
            int code = e.getStatusCode();
            String errorDescription = GoogleSignInStatusCodes.getStatusCodeString(code);
             if (signInPromise != null) {
               signInPromise.reject(String.valueOf(code), errorDescription);
             }
        }
      }
    }
  }
}