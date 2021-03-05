package com.amansiol.goonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;

import com.amansiol.goonline.adapter.ProductAdaptor;
import com.amansiol.goonline.models.Product;
import com.amansiol.goonline.tokenizer.SpaceTokenizer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchActivity extends AppCompatActivity {
    MultiAutoCompleteTextView searchingtext;
    ImageButton searchbtn;
    ImageView searchcancel;
    RelativeLayout wholesearchview;
    ArrayList<String> suggestionlist;
    private String querytext;
    ArrayAdapter<String> searchAdapter;
    // searching recyclerView
    RecyclerView searchProducts;
    ArrayList<Product> searchingList;
    ProductAdaptor searchProductAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_search);
        searchingtext = findViewById(R.id.searchtext);
        searchbtn = findViewById(R.id.searchbtn);
//        searchcancel = findViewById(R.id.cancelbtn);
        wholesearchview = findViewById(R.id.searchview);
        suggestionlist = new ArrayList<>();

        FirebaseFirestore
                .getInstance()
                .collection("Keyword")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot x : queryDocumentSnapshots) {
                            String keyword = x.getData().get("word").toString();
                            suggestionlist.add(keyword);

                        }
                        searchAdapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_dropdown_item_1line, suggestionlist);
                        searchingtext.setAdapter(searchAdapter);
                    }
                });

        searchingtext.setThreshold(1);
        searchingtext.setTokenizer(new SpaceTokenizer());
        querytext = searchingtext.getText().toString().trim().toUpperCase().toLowerCase();
        searchProducts = findViewById(R.id.searchproduct);
        int orientation = this.getResources().getConfiguration().orientation;
        GridLayoutManager searchGridManager;

          /*
            for checking orientation of device at run time.
         */
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            searchGridManager = new GridLayoutManager(getApplicationContext(), 2);
        } else {
            // code for landscape mode
            searchGridManager = new GridLayoutManager(getApplicationContext(), 3);
        }


        searchProducts.setLayoutManager(searchGridManager);
        // init Arraylist
        searchingList = new ArrayList<>();

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Search", "" + searchingtext.getText().toString());
                searchingList.clear();
                searchProductAdaptor = new ProductAdaptor(SearchActivity.this, searchingList);
                searchProducts.setAdapter(searchProductAdaptor);
                searchProductAdaptor.notifyDataSetChanged();
                getFilterProductByKeyword(searchingtext.getText().toString());

            }
        });
        String searchText = getIntent().getStringExtra("searchlabel");
        if (searchText != null && !searchText.isEmpty()) {
            searchingtext.setText(searchText);
            Log.d("Search", "" + searchingtext.getText().toString());
            searchingList.clear();
            searchProductAdaptor = new ProductAdaptor(SearchActivity.this, searchingList);
            searchProducts.setAdapter(searchProductAdaptor);
            searchProductAdaptor.notifyDataSetChanged();
            getFilterProductByKeyword(searchingtext.getText().toString());

        }
    }

    private void getFilterProductByKeyword(String toString) {


        CollectionReference mShopRef = FirebaseFirestore.getInstance().collection("Shops");

        mShopRef.addSnapshotListener(SearchActivity.this, (value, error) -> {
            String[] words = toString.toUpperCase().toLowerCase().trim().split(" ");
            String keyword = null;
            for (String word : words)
                keyword = word + " ";
//            for (QueryDocumentSnapshot shop : value) {
//                mShopRef.document(shop.getId())
//                        .collection("Products")
//                        .whereEqualTo("fullkeyword", toString.toUpperCase().toLowerCase().trim())
//                        .addSnapshotListener(SearchActivity.this, (value1, error1) -> {
//                            for (QueryDocumentSnapshot product : value1) {
//                                Product product1 = product.toObject(Product.class);
//                                searchingList.add(product1);
//                                Log.d("Search", "" + searchingList.size());
//                            }
//                            searchProductAdaptor = new ProductAdaptor(SearchActivity.this, searchingList);
//                            searchProducts.setAdapter(searchProductAdaptor);
//                            searchProductAdaptor.notifyDataSetChanged();
//
//                        });
//            }
            for (QueryDocumentSnapshot shop : value) {
                mShopRef.document(shop.getId())
                        .collection("Products")
                        .whereArrayContainsAny("keywords", Arrays.asList(words))
                        .addSnapshotListener(SearchActivity.this, (value1, error1) -> {
                            for (QueryDocumentSnapshot product : value1) {
                                Product product1 = product.toObject(Product.class);
                                if(product1.getKeywords().containsAll(Arrays.asList(words)))
                                {
                                    searchingList.add(product1);
                                    Log.d("Search", "" + searchingList.size());
                                }

                            }
                            searchProductAdaptor = new ProductAdaptor(SearchActivity.this, searchingList);
                            searchProducts.setAdapter(searchProductAdaptor);
                            searchProductAdaptor.notifyDataSetChanged();

                        });
            }

        });
    }

}