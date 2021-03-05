package com.amansiol.goonline.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.amansiol.goonline.R;
import com.amansiol.goonline.constant.AddressKey;
import com.amansiol.goonline.models.Shops;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class NearByShopAdapter  extends RecyclerView.Adapter<NearByShopAdapter.NearByViewHolder> {

    Context context;
    ArrayList<String> all_near_by_shop_id_list;

    public NearByShopAdapter(Context context, ArrayList<String> all_near_by_shop_id_list) {
        this.context = context;
        this.all_near_by_shop_id_list = all_near_by_shop_id_list;
    }

    @NonNull
    @NotNull
    @Override
    public NearByShopAdapter.NearByViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
       View v= LayoutInflater.from(context).inflate(R.layout.nearbyshop_row,parent,false);
        return new NearByViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NearByShopAdapter.NearByViewHolder holder, int position) {

        FirebaseFirestore
                .getInstance()
                .collection("Shops")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot x : value)
                        {
                            Shops shops = x.toObject(Shops.class);
                            Log.d("Filter","It before Filtering");
                            if(shops.getUid().equals(all_near_by_shop_id_list.get(position)))
                            {
                                Log.d("Filter","It is Filtering");
                                HashMap<String, String> addressmap;
                                addressmap = shops.getAddress();
                                holder.shop_icon.setImageResource(R.drawable.shop_icon);
                                holder.shop_Name.setText(shops.getShopname());
                                holder.address.setText(String.format(
                                        "%s\n%s\n%s ,%s\n%s ,%s "
                                        , shops.getName()
                                        , addressmap.get(AddressKey.SUBLOCALITY)
                                        , addressmap.get(AddressKey.LOCALITY)
                                        , addressmap.get(AddressKey.POSTAL_CODE)
                                        , addressmap.get(AddressKey.STATE_NAME)
                                        , addressmap.get(AddressKey.COUNTRY_NAME)
                                ));
                            }
                        }
                    }
                });


    }

    @Override
    public int getItemCount() {
        return all_near_by_shop_id_list.size();
    }

    public class NearByViewHolder extends RecyclerView.ViewHolder {
        ImageView shop_icon;
        TextView shop_Name;
        TextView address;
        public NearByViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            shop_icon=itemView.findViewById(R.id.shop_icon);
            shop_Name=itemView.findViewById(R.id.shop_name);
            address=itemView.findViewById(R.id.address_shop);
        }
    }
}
