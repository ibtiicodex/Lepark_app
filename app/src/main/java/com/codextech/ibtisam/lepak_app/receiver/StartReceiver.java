package com.codextech.ibtisam.lepak_app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
//		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//			Intent newIntent = new Intent(context, ScanService.class);
//	    	newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startService(newIntent);
//		}
	}
}
