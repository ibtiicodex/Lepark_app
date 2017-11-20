package com.codextech.ibtisam.lepak_app.model;

import io.realm.RealmObject;

/**
 * Created by HP on 10/26/2017.
 */

public class LPLocation extends RealmObject {

    private String id;
    private String locationName;
    private String cityId;

    private String cityName;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {

        this.locationName = locationName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

}
