package com.codextech.ibtisam.lepak_app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by HP on 9/27/2017.
 */

public class RealmModelAdapter<T extends RealmObject> extends RealmBaseAdapter<T> {

    public RealmModelAdapter(Context context, RealmResults<T> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return null;
    }
}
