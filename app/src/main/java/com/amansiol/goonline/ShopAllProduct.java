package com.amansiol.goonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.amansiol.goonline.adapter.RecyclerItemClickListener;
import com.amansiol.goonline.adapter.ShopProductAdapter;
import com.amansiol.goonline.models.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ShopAllProduct extends AppCompatActivity {

    RecyclerView allProducts;
    ShopProductAdapter allProductsAdaptor;
    ArrayList<Product> listofProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_shop_all_product);
        init();
        allProducts
                .addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                        allProducts, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
                        intent.putExtra("myproduct", listofProducts.get(position));
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        final CharSequence[] colors = {"Edit", "Delete"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(ShopAllProduct.this);
                        builder.setTitle("Select");
                        builder.setCancelable(true);
                        builder.setItems(colors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (colors[which] == "Edit") {
                                    UpdateProduct(listofProducts.get(position).getId());
                                } else if (colors[which] == "Delete") {
                                    DeleteProduct(listofProducts.get(position).getId());
                                    listofProducts.remove(position);
                                    allProductsAdaptor.notifyDataSetChanged();
                                }
                            }
                        });
                        builder.show();
                    }
                }));
        getMyProductFromCloudStore();
    }

    private void DeleteProduct(String id) {
        FirebaseFirestore
                .getInstance()
                .collection("Shops")
                .document(MainActivity.usernameUid)
                .collection("Products")
                .document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                        listofProducts.clear();
                        getMyProductFromCloudStore();
                        allProductsAdaptor.notifyDataSetChanged();
                    }
                });

    }

    private void UpdateProduct(String id) {

        Intent intent = new Intent(ShopAllProduct.this,AddProduct.class);
        intent.putExtra("edit","edit");
        intent.putExtra("id",id);
        startActivity(intent);
    }

    private void init() {
        allProducts = findViewById(R.id.allproduct);
        GridLayoutManager gridLayoutManager;
        /*
            for checking orientation of device at run time.
         */
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        } else {
            // code for landscape mode
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        }
        // set the layout manager to RecyclerView
        allProducts.setLayoutManager(gridLayoutManager);
        // init Arraylist
        listofProducts = new ArrayList<>();
    }

    private void getMyProductFromCloudStore() {
        listofProducts.clear();
        allProductsAdaptor = new ShopProductAdapter(ShopAllProduct.this, listofProducts);
        // then just connect ProductAdaptor to RecyclerView to populate all data there.
        allProducts.setAdapter(allProductsAdaptor);
        allProductsAdaptor.notifyDataSetChanged();
        CollectionReference mOnlyMyProductRef = FirebaseFirestore
                .getInstance()
                .collection("Shops")
                .document(MainActivity.usernameUid)
                .collection("Products");
        mOnlyMyProductRef.addSnapshotListener((value, error) -> {
            for (QueryDocumentSnapshot documentDocumentSnap : value) {
                Product product = documentDocumentSnap.toObject(Product.class);
                listofProducts.add(product);
            }

            //then pass this array list to Product Adapter

            allProductsAdaptor = new ShopProductAdapter(ShopAllProduct.this, listofProducts);
            // then just connect ProductAdaptor to RecyclerView to populate all data there.
            allProducts.setAdapter(allProductsAdaptor);
            allProductsAdaptor.notifyDataSetChanged();
        });
    }
}