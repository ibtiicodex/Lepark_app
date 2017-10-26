package com.codextech.ibtisam.lepak_app.adapters;

        import android.content.Context;

        import com.codextech.ibtisam.lepak_app.model.LPTicket;

        import io.realm.RealmResults;

/**
 * Created by HP on 9/27/2017.
 */

public class RealmTicketsAdapter extends RealmModelAdapter<LPTicket> {

    public RealmTicketsAdapter(Context context, RealmResults<LPTicket> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}