package com.codextech.ibtisam.lepak_app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.app.Prefs;
import com.codextech.ibtisam.lepak_app.model.LPTicket;
import com.codextech.ibtisam.lepak_app.realm.RealmController;
import com.codextech.ibtisam.lepak_app.sync.SyncStatus;

import io.realm.Realm;
import io.realm.RealmResults;

public class TicketsAdapter extends RealmRecyclerViewAdapter<LPTicket> {

    private final Context context;
    private Realm realm;
    private LayoutInflater inflater;

    public TicketsAdapter(Context context) {
        this.context = context;
    }

    // create new views (invoked by the layout manager)
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card_ticket view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tickets, parent, false);
        return new CardViewHolder(view);
    }

    // replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        realm = RealmController.getInstance().getRealm();
        // get the article
        final LPTicket LPTicket = getItem(position);
        // cast the generic view holder to our specific one
        final CardViewHolder holder = (CardViewHolder) viewHolder;
        // set the title and the snippet
        holder.tvSiteName.setText(LPTicket.getSiteName());
        holder.textVehicalType.setText(LPTicket.getVehicleType());
        holder.tvTimeIn.setText(LPTicket.getTimeIn());
        holder.textTimeOut.setText(LPTicket.getTimeOut());
        holder.textNumber.setText(LPTicket.getNumber());
        holder.textPrice.setText(LPTicket.getPrice());
        holder.textLocation.setText(LPTicket.getLocation());
        if (LPTicket.getSyncStatus().equals(SyncStatus.SYNC_STATUS_TICKET_ADD_NOT_SYNCED)) {
            holder.syncStatus.setTextColor(Color.parseColor("#FF1100"));
            holder.syncStatus.setText("not added to server");
        } else if (LPTicket.getSyncStatus().equals(SyncStatus.SYNC_STATUS_TICKET_EDIT_NOT_SYNCED)) {
            holder.syncStatus.setTextColor(Color.parseColor("#FF1100"));
            holder.syncStatus.setText("not edited to server");
        } else {
            holder.syncStatus.setTextColor(context.getResources().getColor(R.color.textcolor));
            holder.syncStatus.setText("synced");
        }
        holder.textServerId.setText(LPTicket.getServer_id());
        //remove single match from realm
        holder.card_ticket.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                RealmResults<LPTicket> results = realm.where(LPTicket.class).findAll();
                // Get the LPTicket title to show it in toast message
                LPTicket b = results.get(position);
                String title = b.getSiteName();
                // All changes to data must happen in a transaction
                realm.beginTransaction();
                // remove single match
                results.remove(position);
                realm.commitTransaction();
                if (results.size() == 0) {
                    Prefs.with(context).setPreLoad(false);
                }
                notifyDataSetChanged();
                Toast.makeText(context, title + " is removed from Realm" + position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    public int getItemCount() {

        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    private static class CardViewHolder extends RecyclerView.ViewHolder {
        private CardView card_ticket;
        private TextView tvSiteName;
        private TextView textTimeOut;
        private TextView tvTimeIn;
        private TextView textNumber;
        private TextView textPrice;
        private TextView textLocation;
        private TextView textVehicalType;
        private TextView textServerId;
        public TextView syncStatus;

        //  public ImageView imageBackground;
        private CardViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);
            card_ticket = (CardView) itemView.findViewById(R.id.card_ticket);
            tvSiteName = (TextView) itemView.findViewById(R.id.tvSiteName);
            tvTimeIn = (TextView) itemView.findViewById(R.id.tvTimeIn);
            textTimeOut = (TextView) itemView.findViewById(R.id.timeOut);
            textNumber = (TextView) itemView.findViewById(R.id.Dnumber);
            textPrice = (TextView) itemView.findViewById(R.id.Dprice);
            textLocation = (TextView) itemView.findViewById(R.id.Dlocation);
            syncStatus = (TextView) itemView.findViewById(R.id.tvSyncStatus);
            textVehicalType = (TextView) itemView.findViewById(R.id.tvVehicleType);
            textServerId = (TextView) itemView.findViewById(R.id.tvServerId);

        }
    }
}
