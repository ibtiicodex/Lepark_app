package com.codextech.ibtisam.lepak_app.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.codextech.ibtisam.lepak_app.model.Ticket;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HP on 9/27/2017.
 */

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from Ticket.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(Ticket.class);
        realm.commitTransaction();
    }

    //find all objects in the Ticket.class
    public RealmResults<Ticket> getBooks() {

        return realm.where(Ticket.class).findAll();
    }

    //query a single item with the given id
    public Ticket getBook(String id) {

        return realm.where(Ticket.class).equalTo("id", id).findFirst();
    }

    //isServiceRunning if Ticket.class is empty
    public boolean hasBooks() {

        return !realm.allObjects(Ticket.class).isEmpty();
    }

    //query example
    public RealmResults<Ticket> queryedBooks() {

        return realm.where(Ticket.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                //
                .findAll();


    }
}
