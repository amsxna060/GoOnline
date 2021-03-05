package com.amansiol.goonline;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amansiol.goonline.adapter.NearByShopAdapter;
import com.amansiol.goonline.adapter.RecyclerItemClickListener;
import com.amansiol.goonline.models.Shops;
import com.amansiol.goonline.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class NearByStore extends Fragment {

    RecyclerView nearbyshoprecycler;
    NearByShopAdapter nearByShopAdapter;
    ArrayList<String> nearByShopIds;
    CollectionReference mShopRef;
    final double KM5=5.00;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_near_by_store, container, false);

        nearbyshoprecycler=v.findViewById(R.id.nearbyshoprecycler);
        nearbyshoprecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mShopRef = FirebaseFirestore.getInstance().collection("Shops");
        nearByShopIds=new ArrayList<>();
        getNearByShopFromFirebase();

       nearbyshoprecycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), nearbyshoprecycler, new RecyclerItemClickListener.OnItemClickListener() {
           @Override
           public void onItemClick(View view, int position) {
               Intent intent = new Intent(getContext(), ShopDetailsActivity.class);
               intent.putExtra("specificshopid", nearByShopIds.get(position));
               startActivity(intent);
           }

           @Override
           public void onLongItemClick(View view, int position) {

           }
       }));
        return v;
    }

    private void getNearByShopFromFirebase() {
        nearByShopIds.clear();
        mShopRef.addSnapshotListener((value, error) -> {

            for (QueryDocumentSnapshot shopDocumentSnap : value) {

                Shops shops=shopDocumentSnap.toObject(Shops.class);
                GeoPoint shopgeo = shops.getLocation();
                Filter_the_product(shopgeo,shopDocumentSnap.getId(),KM5);
            }

        });
    }

    private void Filter_the_product(GeoPoint shopgeo, String id, double km5) {
        FirebaseFirestore
                .getInstance()
                .collection("Users")
                .document(MainActivity.usernameUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if(document.exists())
                        {
                            User user=document.toObject(User.class);
                            if(filterByRadius(user.getLocation().getLatitude(),user.getLocation().getLongitude(),shopgeo.getLatitude(),shopgeo.getLongitude(),km5)){

                                nearByShopIds.add(id);
                                nearByShopIds.size();
                                Log.d("Filter",nearByShopIds.size()+"");
                            }

                        }else {
                            Toast.makeText(getContext(),"No Person Exists",Toast.LENGTH_LONG).show();
                        }
                        nearByShopAdapter = new NearByShopAdapter(getContext(),nearByShopIds);
                        nearbyshoprecycler.setAdapter(nearByShopAdapter);
                        nearByShopAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }


    private static boolean filterByRadius(double lat1, double lon1, double lat2, double lon2, double radius) {


            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;

            if(dist<=radius)
            {
                Log.d("Filter","It is in range "+dist);
                return true;
            }
            else {
                Log.d("Filter","It is not in range "+dist);
                return false;
            }

    }


}