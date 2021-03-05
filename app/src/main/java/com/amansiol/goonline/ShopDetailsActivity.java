package com.amansiol.goonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

import com.amansiol.goonline.adapter.ProductAdaptor;
import com.amansiol.goonline.constant.AddressKey;
import com.amansiol.goonline.models.Product;
import com.amansiol.goonline.models.Shops;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class ShopDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerViewspecificshop;
    ProductAdaptor adaptorspecificshop;
    ArrayList<Product> listspecificshop;
    TextView shop_Name;
    TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_shop_details);
        recyclerViewspecificshop=findViewById(R.id.specificshop);
        listspecificshop=new ArrayList<>();

        shop_Name=findViewById(R.id.shop_name);
        address=findViewById(R.id.address_shop);

        GridLayoutManager gridLayoutManager;
        /*
                for checking orientation of device at run time.
         */
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            gridLayoutManager = new GridLayoutManager(this, 2);
        } else {
            // code for landscape mode
            gridLayoutManager = new GridLayoutManager(this, 3);
        }
        // set the layout manager to RecyclerView
        recyclerViewspecificshop.setLayoutManager(gridLayoutManager);

        String shopId=getIntent().getStringExtra("specificshopid");

        if(shopId!=null)
        {
            FirebaseFirestore
                    .getInstance()
                    .collection("Shops")
                    .document(shopId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Shops shops = documentSnapshot.toObject(Shops.class);
                            HashMap<String, String> addressmap;
                            addressmap = shops.getAddress();
                            shop_Name.setText(shops.getShopname());
                            address.setText(String.format(
                                    "%s\n%s\n%s ,%s\n%s ,%s "
                                    , shops.getName()
                                    , addressmap.get(AddressKey.SUBLOCALITY)
                                    , addressmap.get(AddressKey.LOCALITY)
                                    , addressmap.get(AddressKey.POSTAL_CODE)
                                    , addressmap.get(AddressKey.STATE_NAME)
                                    , addressmap.get(AddressKey.COUNTRY_NAME)
                            ));
                        }
                    });




            FirebaseFirestore
                    .getInstance()
                    .collection("Shops")
                    .document(shopId)
                    .collection("Products")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots)
                            {
                                Product product = queryDocumentSnapshot.toObject(Product.class);
                                listspecificshop.add(product);
                            }
                            adaptorspecificshop=new ProductAdaptor(ShopDetailsActivity.this,listspecificshop);
                            recyclerViewspecificshop.setAdapter(adaptorspecificshop);
                        }
                    });
        }
    }
}