package com.codextech.ibtisam.lepak_app.model;

import io.realm.RealmObject;

/**
 * Created by HP on 11/16/2017.
 */

public class AllSites extends RealmObject {
    private int id;
    private String site_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }
}
