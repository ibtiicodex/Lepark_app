package com.codextech.ibtisam.lepak_app.wiget;

import android.app.Application;
import android.os.Build;
import android.posapi.PosApi;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class App extends Application {

	public static final String DEVICE_MODEL_IMA3511 = "iMA3511";
	public static final String DEVICE_MODEL_IMA3507 = "iMA3507";
	public static final String DEVICE_MODEL_SK_16 = "SK-16";
	public static final String DEVICE_MODEL_IMA128   = "X8";
	public static final String DEVICE_MODEL_IMA80M01   = "br6580_we_emmc_m";
	public static final String DEVICE_MODEL_IMA3512   = "iMA3512";
	public static final String DEVICE_MODEL_A380LTE   = "iMA35S05";
	public static final String DEVICE_MODEL_IMA35S09   = "3508";

	private static String mCurDev1 = "";

	static App instance = null;
	//PosSDK mSDK = null;
	static PosApi mPosApi = null;
	public App(){
		super.onCreate();
		instance = this;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.v("hello", "APP onCreate~~");
//		mPosApi = PosApi.getInstance(this);
//		init();
		RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
				.name(Realm.DEFAULT_REALM_NAME)
				.schemaVersion(0)
				.deleteRealmIfMigrationNeeded()
				.build();
		Realm.setDefaultConfiguration(realmConfiguration);

	}

	public static void init(){
		if (Build.MODEL.equalsIgnoreCase("3508")|| Build.MODEL.equalsIgnoreCase("403")) {
			mPosApi.initPosDev("ima35s09");
			setCurDevice("ima35s09");
		} else if(Build.MODEL.equalsIgnoreCase("5501")){
			mPosApi.initPosDev("ima35s12");
			setCurDevice("ima35s12");
		}else{
			mPosApi.initPosDev(PosApi.PRODUCT_MODEL_IMA80M01);
			setCurDevice(PosApi.PRODUCT_MODEL_IMA80M01);
		}
	}

	public static  App getInstance(){
		if(instance==null){
			instance =new App();
		}
		return instance;
	}


	public String getCurDevice() {
		return mCurDev1;
	}

	public static  void setCurDevice(String mCurDev) {
		mCurDev1 = mCurDev;
	}

	public PosApi getPosApi(){
		return mPosApi;
	}

}
