package com.codextech.ibtisam.lepak_app.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.codextech.ibtisam.lepak_app.R;


public class DialogUtils {
    public static void showTipDialog(Context context, CharSequence message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(""+message);
        builder.setTitle(""+context.getString(R.string.tips));
        builder.setPositiveButton(""+context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
