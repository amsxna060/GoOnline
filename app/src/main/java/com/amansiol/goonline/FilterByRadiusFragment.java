package com.amansiol.goonline;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amansiol.goonline.adapter.ProductAdaptor;
import com.amansiol.goonline.models.Product;
import com.amansiol.goonline.models.Shops;
import com.amansiol.goonline.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FilterByRadiusFragment extends Fragment {

    RecyclerView allProducts;
    ProductAdaptor allProductsAdaptor;
    ArrayList<Product> listofProducts_5km;
    ArrayList<Product> listofProducts_10km;
    ArrayList<Product> listofProducts_15km;
    String comingProductType;
    String comingProductGenType;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    CollectionReference mShopRef;
    CollectionReference mProductRef;
    CollectionReference userCollectionRef;
    DocumentReference userDocumentRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_filter_by_radius, container, false);
        init(view);
        listofProducts_5km = new ArrayList<>();
        listofProducts_10km = new ArrayList<>();
        listofProducts_15km = new ArrayList<>();
        /*
        this is basically coming data (product type and gender type ) from home fragment which we click on any category
         */
        comingProductType = getActivity().getIntent().getStringExtra("product_type");
        comingProductGenType=getActivity().getIntent().getStringExtra("gender");

        //Get data from this function and store it in arraylist

        get5kmRangeProduct();


        // listener for changes occur in radio buttons
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId==R.id.radioButton1)
            {
                listofProducts_5km.clear();
                listofProducts_10km.clear();
                listofProducts_15km.clear();
                allProductsAdaptor = new ProductAdaptor(getContext(), listofProducts_15km);
                // then just connect ProductAdaptor to RecyclerView to populate all data there.
                allProducts.setAdapter(allProductsAdaptor);
                allProductsAdaptor.notifyDataSetChanged();
                get5kmRangeProduct();

            }
            else if(checkedId==R.id.radioButton2) {
                listofProducts_5km.clear();
                listofProducts_10km.clear();
                listofProducts_15km.clear();
                allProductsAdaptor = new ProductAdaptor(getContext(), listofProducts_15km);
                // then just connect ProductAdaptor to RecyclerView to populate all data there.
                allProducts.setAdapter(allProductsAdaptor);
                allProductsAdaptor.notifyDataSetChanged();
                get10kmRangeProduct();
            }
            else if(checkedId==R.id.radioButton3) {
                listofProducts_5km.clear();
                listofProducts_10km.clear();
                listofProducts_15km.clear();
                allProductsAdaptor = new ProductAdaptor(getContext(), listofProducts_15km);
                // then just connect ProductAdaptor to RecyclerView to populate all data there.
                allProducts.setAdapter(allProductsAdaptor);
                allProductsAdaptor.notifyDataSetChanged();
                get15kmRangeProduct();
            }
        });

        return  view;
    }

    private void get5kmRangeProduct() {
        listofProducts_5km.clear();
        mShopRef.addSnapshotListener((value, error) -> {
            for(QueryDocumentSnapshot shopDocSnapshot : value)
            {
                Shops shops=shopDocSnapshot.toObject(Shops.class);
                userCollectionRef=FirebaseFirestore.getInstance().collection("Users");
                userDocumentRef=userCollectionRef.document(MainActivity.usernameUid);
                userDocumentRef.addSnapshotListener((value1, error12) -> {
                    if(value1.exists())
                    {
                        User user = value1.toObject(User.class);
                        double distance=filterByRadius(user.getLocation().getLatitude(), user.getLocation().getLongitude(), shops.getLocation().getLatitude(),shops.getLocation().getLongitude());
                        boolean inRange = distance<=5.0;
                        Log.d("FilterByRadius","Result : "+ "Distance between You and "+shops.getShopname() + " is "+String.format("%1.2f",distance)+" Km.");
                        Log.d("InRange"," "+inRange);
                        if(inRange){
                            mProductRef
                                    .document(shops.getUid())
                                    .collection("Products")
                                    .addSnapshotListener((products, error1) -> {
                                        for( QueryDocumentSnapshot productSnap : products)
                                        {
                                            Product product = productSnap.toObject(Product.class);
                                            String productType = product.getProduct_type();
                                            String productGenType = product.getGender();
                                            productType = productType.trim();
                                            productGenType=productGenType.trim();
                                            if (productType.compareToIgnoreCase(comingProductType) == 0
                                                    && productGenType.compareToIgnoreCase(comingProductGenType) == 0 ) {
                                                listofProducts_5km.add(product);
                                                Log.d("product_type", product.getProduct_type());
                                                Log.d("productGen_type", product.getGender());
                                                Log.d("InRange","Inside if loop"+listofProducts_5km.size());
                                            }
                                        }
                                        Log.d("InRange","outside of loop "+listofProducts_5km.size());
                                        Log.d("InRange","outside of inner for loop "+listofProducts_5km.size());
                                        //then pass this array list to Product Adapter
                                        allProductsAdaptor = new ProductAdaptor(getContext(), listofProducts_5km);
                                        // then just connect ProductAdaptor to RecyclerView to populate all data there.
                                        allProducts.setAdapter(allProductsAdaptor);
                                        allProductsAdaptor.notifyDataSetChanged();
                                    });

                        }

                    }else {
                        Toast.makeText(getContext(),"Error While Fetching User Location",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }

    private void get10kmRangeProduct() {
        listofProducts_10km.clear();
        mShopRef.addSnapshotListener((value, error) -> {
            for(QueryDocumentSnapshot shopDocSnapshot : value)
            {
                Shops shops=shopDocSnapshot.toObject(Shops.class);
                userCollectionRef=FirebaseFirestore.getInstance().collection("Users");
                userDocumentRef=userCollectionRef.document(MainActivity.usernameUid);
                userDocumentRef.addSnapshotListener((value1, error12) -> {
                    if(value1.exists())
                    {
                        User user = value1.toObject(User.class);
                        double distance=filterByRadius(user.getLocation().getLatitude(), user.getLocation().getLongitude(), shops.getLocation().getLatitude(),shops.getLocation().getLongitude());

                        boolean inRange = distance>5.0 && distance<=10.0 ;
                        Log.d("FilterByRadius","Result : "+ "Distance between You and "+shops.getShopname() + " is "+String.format("%1.2f",distance)+" Km.");
                        Log.d("InRange"," "+inRange);

                        if(inRange){
                            mProductRef
                                    .document(shops.getUid())
                                    .collection("Products")
                                    .addSnapshotListener((products, error1) -> {
                                        for( QueryDocumentSnapshot productSnap : products)
                                        {
                                            Product product = productSnap.toObject(Product.class);
                                            String productType = product.getProduct_type();
                                            String productGenType = product.getGender();
                                            productType = productType.trim();
                                            productGenType=productGenType.trim();
                                            if (productType.compareToIgnoreCase(comingProductType) == 0
                                                    && productGenType.compareToIgnoreCase(comingProductGenType) == 0 ) {
                                                listofProducts_10km.add(product);
                                            }
                                        }
                                        //then pass this array list to Product Adapter
                                        allProductsAdaptor = new ProductAdaptor(getContext(), listofProducts_10km);
                                        // then just connect ProductAdaptor to RecyclerView to populate all data there.
                                        allProducts.setAdapter(allProductsAdaptor);
                                        allProductsAdaptor.notifyDataSetChanged();
                                    });

                        }

                    }else {
                        Toast.makeText(getContext(),"Error While Fetching User Location",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }

    private void get15kmRangeProduct() {
        listofProducts_15km.clear();
        mShopRef.addSnapshotListener((value, error) -> {
            for(QueryDocumentSnapshot shopDocSnapshot : value)
            {
                Shops shops=shopDocSnapshot.toObject(Shops.class);
                userCollectionRef=FirebaseFirestore.getInstance().collection("Users");
                userDocumentRef=userCollectionRef.document(MainActivity.usernameUid);
                userDocumentRef.addSnapshotListener((value1, error12) -> {
                    if(value1.exists())
                    {
                        User user = value1.toObject(User.class);
                        double distance=filterByRadius(user.getLocation().getLatitude(), user.getLocation().getLongitude(), shops.getLocation().getLatitude(),shops.getLocation().getLongitude());
                        boolean inRange = distance>10.0 && distance<=15.0;
                        Log.d("FilterByRadius","Result : "+ "Distance between You and "+shops.getShopname() + " is "+String.format("%1.2f",distance)+" Km.");
                        Log.d("InRange"," "+inRange);
                        if(inRange){
                            mProductRef
                                    .document(shops.getUid())
                                    .collection("Products")
                                    .addSnapshotListener((products, error1) -> {
                                        for( QueryDocumentSnapshot productSnap : products)
                                        {
                                            Product product = productSnap.toObject(Product.class);
                                            String productType = product.getProduct_type();
                                            String productGenType = product.getGender();
                                            productType = productType.trim();
                                            productGenType=productGenType.trim();
                                            if (productType.compareToIgnoreCase(comingProductType) == 0
                                                    && productGenType.compareToIgnoreCase(comingProductGenType) == 0 ) {
                                                listofProducts_15km.add(product);
                                                Log.d("product_type", product.getProduct_type());
                                                Log.d("productGen_type", product.getGender());
                                                Log.d("InRange","Inside if loop"+listofProducts_15km.size());
                                            }
                                        }
                                        Log.d("InRange","outside of loop "+listofProducts_15km.size());
                                        Log.d("InRange","outside of inner for loop "+listofProducts_15km.size());
                                        //then pass this array list to Product Adapter
                                        allProductsAdaptor = new ProductAdaptor(getContext(), listofProducts_15km);
                                        // then just connect ProductAdaptor to RecyclerView to populate all data there.
                                        allProducts.setAdapter(allProductsAdaptor);
                                        allProductsAdaptor.notifyDataSetChanged();
                                    });

                        }

                    }else {
                        Toast.makeText(getContext(),"Error While Fetching User Location",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }

// initialize of views
    private void init(View view) {
        allProducts =view.findViewById(R.id.allproduct);
        radioGroup=view.findViewById(R.id.nearby);
        radioButton1=view.findViewById(R.id.radioButton1);
        radioButton2=view.findViewById(R.id.radioButton2);
        radioButton3=view.findViewById(R.id.radioButton3);
        mProductRef = FirebaseFirestore.getInstance().collection("Shops");
        mShopRef = FirebaseFirestore.getInstance().collection("Shops");
        radioButton1.setChecked(true);

        GridLayoutManager gridLayoutManager;
        /*
            for checking orientation of device at run time.
         */
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            // code for landscape mode
            gridLayoutManager = new GridLayoutManager(getContext(), 3);
        }
        // set the layout manager to RecyclerView
        allProducts.setLayoutManager(gridLayoutManager);
    }

    // this function will give me distance in km b/w user and shopkeeper
    private static double filterByRadius(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;

            return (dist);
        }
    }

}