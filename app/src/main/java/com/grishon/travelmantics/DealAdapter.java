package com.grishon.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder>{

    private ArrayList<TravelDeal> deals;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener listener;
    private ImageView imageDeal;

    public DealAdapter(){

        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        this.deals = FirebaseUtil.arrayList;
        listener = new ChildEventListener()  {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deal: ", travelDeal.getTitle());
                travelDeal.setId(dataSnapshot.getKey());
                deals.add(travelDeal);
                notifyItemInserted(deals.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(listener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.populate_deals,parent, false);
        return new DealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DealViewHolder holder, int position) {
        TravelDeal deal = deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView titleText, descriptionText, priceText;
        public DealViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.popTitleText);
            descriptionText = itemView.findViewById(R.id.popDescriptionText);
            priceText = itemView.findViewById(R.id.popPriceText);
            imageDeal = itemView.findViewById(R.id.imageDeal);
            itemView.setOnClickListener(this);
        }
        public void bind(TravelDeal deal){
            titleText.setText(deal.getTitle());
            descriptionText.setText(deal.getDescription());
            priceText.setText(deal.getPrice());
            showImage(deal.getImgUrl());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            TravelDeal selectedDeal = deals.get(position);
            Log.d("Click", String.valueOf(position));
            Intent intent = new Intent(v.getContext(), DealActivity.class);
            intent.putExtra("Deal", selectedDeal);
            v.getContext().startActivity(intent);
        }
    }
    private void showImage(String url) {
        if (url != null && url.isEmpty()==false) {
            Picasso.get()
                    .load(url)
                    .resize(160, 160)
                    .centerCrop()
                    .into(imageDeal);
        }
    }
}
