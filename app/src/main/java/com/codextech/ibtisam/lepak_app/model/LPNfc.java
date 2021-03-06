package com.codextech.ibtisam.lepak_app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HP on 10/30/2017.
 */

public class LPNfc extends RealmObject {
    @PrimaryKey
    private long id;
    private long serverId;
    private String coinId;
    private String coinAmount;
    private String coinVehicle;
    private String syncStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public String getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(String coinAmount) {
        this.coinAmount = coinAmount;
    }

    public String getCoinVehicle() {
        return coinVehicle;
    }

    public void setCoinVehicle(String coinVehicle) {
        this.coinVehicle = coinVehicle;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

}
