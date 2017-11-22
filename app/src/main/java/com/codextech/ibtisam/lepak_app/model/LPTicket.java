package com.codextech.ibtisam.lepak_app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HP on 9/27/2017.
 */

public class LPTicket extends RealmObject {

    @PrimaryKey
    private long id;
    private String siteName;
    private String number;
    private String price;
    private String location;
    private long timeIn;
    //in the memories of timeIn which was deleted
    private long timeOut;
    private String syncStatus;
    private String vehicleType;
    private String server_id;


    private String blockUser;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String name) {
        this.siteName = name;
    }

    public long getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(long timeIn) {
        this.timeIn = timeIn;
    }

    //    public String getTimeIn() {
//        return timeIn;
//    }
//
//    public void setTimeIn(String timeIn) {
//        this.timeIn = timeIn;
//    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public String getBlockUser() {
        return blockUser;
    }

    public void setBlockUser(String blockUser) {
        this.blockUser = blockUser;
    }

}
