package com.codextech.ibtisam.lepak_app.adapters;

import android.content.Context;

import com.codextech.ibtisam.lepak_app.model.Book;

import io.realm.RealmResults;

/**
 * Created by HP on 9/27/2017.
 */

public class RealmBooksAdapter extends RealmModelAdapter<Book> {

    public RealmBooksAdapter(Context context, RealmResults<Book> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}