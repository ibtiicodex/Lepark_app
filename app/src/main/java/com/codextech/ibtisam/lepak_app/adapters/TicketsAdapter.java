package com.codextech.ibtisam.lepak_app.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.app.Prefs;
import com.codextech.ibtisam.lepak_app.model.Ticket;
import com.codextech.ibtisam.lepak_app.realm.RealmController;

import io.realm.Realm;
import io.realm.RealmResults;

public class TicketsAdapter extends RealmRecyclerViewAdapter<Ticket> {

    private final Context context;
    private Realm realm;
    private LayoutInflater inflater;

    public TicketsAdapter(Context context) {
        this.context = context;
    }

    // create new views (invoked by the layout manager)
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tickets, parent, false);
        return new CardViewHolder(view);
    }

    // replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        realm = RealmController.getInstance().getRealm();
        // get the article
        final Ticket ticket = getItem(position);
        // cast the generic view holder to our specific one
        final CardViewHolder holder = (CardViewHolder) viewHolder;
        // set the title and the snippet
        holder.textName.setText(ticket.getAgentName());
        holder.textTimeIn.setText(ticket.getTimeIn());
        holder.textTimeOut.setText(ticket.getTimeOut());
        holder.textNumber.setText(ticket.getNumber());
        holder.textPrice.setText(ticket.getPrice());
        holder.textLocation.setText(ticket.getLocation());
        //remove single match from realm
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                RealmResults<Ticket> results = realm.where(Ticket.class).findAll();
                // Get the ticket title to show it in toast message
                Ticket b = results.get(position);
                String title = b.getAgentName();
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

        private CardView card;
        private TextView textName;
        private TextView textTimeOut;
        private TextView textTimeIn;
        private TextView textNumber;
        private TextView textPrice;
        private TextView textLocation;

        //  public ImageView imageBackground;
        private CardViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.card_books);
            textName = (TextView) itemView.findViewById(R.id.agnetName);
            textTimeIn = (TextView) itemView.findViewById(R.id.timeIn);
            textTimeOut = (TextView) itemView.findViewById(R.id.timeOut);
            textNumber = (TextView) itemView.findViewById(R.id.Dnumber);
            textPrice = (TextView) itemView.findViewById(R.id.Dprice);
            textLocation = (TextView) itemView.findViewById(R.id.Dlocation);

        }
    }
}
