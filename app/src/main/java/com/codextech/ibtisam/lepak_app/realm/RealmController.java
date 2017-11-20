package com.codextech.ibtisam.lepak_app.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.codextech.ibtisam.lepak_app.model.LPLocation;
import com.codextech.ibtisam.lepak_app.model.LPNfc;
import com.codextech.ibtisam.lepak_app.model.LPTicket;

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

    public LPLocation getLocationFromLocationName(String locationName) {
        return realm.where(LPLocation.class).equalTo("locationName", locationName).findFirst();
    }
    //clear all objects from LPTicket.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(LPTicket.class);
        realm.commitTransaction();
    }
    //find all objects in the LPTicket.class
    public RealmResults<LPTicket> getTickets() {
        RealmResults<LPTicket> results = realm.where(LPTicket.class).findAllSorted("id", false);
        return results;
    }
    public RealmResults<LPNfc> getNfcGet() {
        return realm.where(LPNfc.class).findAll();
    }
    //query a single item with the given id
    public LPTicket getTicket(String id) {
        return realm.where(LPTicket.class).equalTo("id", id).findFirst();
    }

    public LPTicket getTicketFromNumber(String num) {
        return realm.where(LPTicket.class).equalTo("number", num).findFirst();
    }
    //isServiceRunning if LPTicket.class is empty
    public boolean hasTickets() {
        return !realm.allObjects(LPTicket.class).isEmpty();
    }

    //query example
    public RealmResults<LPTicket> queryedTickets() {

        return realm.where(LPTicket.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                //
                .findAll();
    }
}
