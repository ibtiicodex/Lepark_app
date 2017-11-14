package com.codextech.ibtisam.lepak_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by HP on 10/18/2017.
 */

public class SessionManager {
    public static final String TAG = "SessionManager";
    private static final String KEY_SITE_ID = "site_login_id";
    private static final String KEY_SITE_NAME = "site_login_name";
    private static final String KEY_LOGIN_TOKEN = "user_login_token";
    private static final String KEY_LOGIN_TIMESTAMP = "user_login_timestamp";
    private static final String KEY_CAR_AMOUNT = "user_car_amount";
    private static final String KEY_BIKE_AMOUNT = "user_bike_amount";
    private static final String KEY_VAN_AMOUNT = "user_van_amount";
    private static final String KEY_TRUCK_AMOUNT = "user_truck_amount";
    private static final String KEY_AREANMAE = "key_area_name";
    // Sharedpref file name
    private static final String PREF_NAME = "ProjectLastingSalesPreffs";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;


    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public Boolean isSiteSignedIn() {
        if (getLoginToken().equals("")) {
            return false;
        }
        if (getLoginTimestamp() == 00L) {
            return false;
        }
        Long oldTimestamp = getLoginTimestamp();
        Long currentTimestamp = Calendar.getInstance().getTimeInMillis();
        Long oldAnd24Hours = oldTimestamp + 15552000000L; //Six months expiry
        if (currentTimestamp > oldAnd24Hours) {
            return false;
        }
        return true;
    }
    public void loginSite(String id, String name, String token, Long timeStamp, String car, String bike, String van, String truck ,String areaName) {
        Log.d(TAG, "loginSite: ");
//        deleteDataIfDifferentUser();
        setKeySiteId(id);
        setKeySiteName(name);
        setLoginTimestamp(timeStamp);
        setLoginToken(token);
        setKeyCarAmount(car);
        setKeyBikeAmount(bike);
        setKeyVanAmount(van);
        setKeyTruckAmount(truck);
        setKeyAreanmae(areaName);
        editor.commit();
    }

    private void deleteDataIfDifferentUser(String number) {
        //TODO implement
    }
    public void logoutSite() {
        deleteAllUserData();
        setKeySiteId("");
        setLoginTimestamp(00L);
        setLoginToken("");
        setKeyCarAmount("");
        setKeyBikeAmount("");
        setKeyVanAmount("");
        setKeyTruckAmount("");
        setKeyAreanmae("");
        editor.commit();
    }

    private void deleteAllUserData() {
        //TODO implement
    }

    public String getLoginToken() {
        return pref.getString(KEY_LOGIN_TOKEN, "");
    }

    public void setLoginToken(String loginToken) {
        editor.putString(KEY_LOGIN_TOKEN, loginToken);
        editor.commit();
    }

    public String getKeySiteId() {
        return pref.getString(KEY_SITE_ID, "");
    }

    public void setKeySiteId(String path) {
        editor.putString(KEY_SITE_ID, path);
        editor.commit();
    }

    public String getKeySiteName() {
        return pref.getString(KEY_SITE_NAME, "");
    }

    public void setKeySiteName(String name) {
        editor.putString(KEY_SITE_NAME, name);
        editor.commit();
    }


    public String getKeyCarAmount() {
        return pref.getString(KEY_CAR_AMOUNT, "");
    }

    public void setKeyCarAmount(String car) {
        editor.putString(KEY_CAR_AMOUNT, car);
        editor.commit();
    }

    public String getKeyBikeAmount() {
        return pref.getString(KEY_BIKE_AMOUNT, "");
    }

    public void setKeyBikeAmount(String bike) {
        editor.putString(KEY_BIKE_AMOUNT, bike);
        editor.commit();
    }

    public String getKeyVanAmount() {
        return pref.getString(KEY_VAN_AMOUNT, "");
    }

    public void setKeyVanAmount(String name) {
        editor.putString(KEY_VAN_AMOUNT, name);
        editor.commit();
    }

    public String getKeyTruckAmount() {
        return pref.getString(KEY_TRUCK_AMOUNT, "");
    }

    public void setKeyTruckAmount(String name) {
        editor.putString(KEY_TRUCK_AMOUNT, name);
        editor.commit();
    }


    public Long getLoginTimestamp() {
        return pref.getLong(KEY_LOGIN_TIMESTAMP, 00l);
    }

    public void setLoginTimestamp(Long timestamp) {
        editor.putLong(KEY_LOGIN_TIMESTAMP, timestamp);
        editor.commit();
    }

    public  String getKeyAreanmae()  {
        return pref.getString(KEY_AREANMAE, "");
    }

    public void setKeyAreanmae(String name) {
        editor.putString(KEY_AREANMAE, name);
        editor.commit();
    }
}