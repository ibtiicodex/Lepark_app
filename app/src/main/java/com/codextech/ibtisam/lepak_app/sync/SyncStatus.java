package com.codextech.ibtisam.lepak_app.sync;

import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by HP on 11/1/2017.
 */

public class SyncStatus {

    public static final String SYNC_STATUS_TICKET_ADD_NOT_SYNCED = "ticket_add_not_synced";
    public static final String SYNC_STATUS_TICKET_ADD_SYNCED = "ticket_add_synced";
    public static final String SYNC_STATUS_TICKET_EDIT_NOT_SYNCED = "ticket_edit_not_synced";
    public static final String SYNC_STATUS_TICKET_EDIT_SYNCED = "ticket_edit_synced";
    public static final String SYNC_STATUS_COIN_EDIT_NOT_SYNCED = "ticket_edit_not_synced";
    public static String AREANAME = "";
    private static String TAG="SyncStatus";

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                Log.d(TAG, "getMacAddr: "+res1.toString());
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

}
