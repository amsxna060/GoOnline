package com.amansiol.goonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.amansiol.goonline.adapter.FavouriteAdapter;
import com.amansiol.goonline.adapter.RecyclerItemClickListener;
import com.amansiol.goonline.models.Favorite;
import com.amansiol.goonline.models.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FavActivity extends AppCompatActivity {

    ArrayList<Product> favproducts;
    RecyclerView recyclerView;
    ArrayList<Favorite> favoritesIdList;
    FavouriteAdapter favouriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_fav);
        favproducts=new ArrayList<>();
        favoritesIdList=new ArrayList<>();
        recyclerView=findViewById(R.id.cartrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavActivity.this));
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(MainActivity.usernameUid)
                .collection("Fav")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            Favorite favorite = queryDocumentSnapshot.toObject(Favorite.class);
                            favoritesIdList.add(favorite);
                        }
                        favouriteAdapter = new FavouriteAdapter(FavActivity.this,favoritesIdList);
                        recyclerView.setAdapter(favouriteAdapter);
                        favouriteAdapter.notifyDataSetChanged();
                    }
                });
        recyclerView
                .addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                        recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        final CharSequence[] options = {"Delete"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(FavActivity.this);
                        builder.setTitle("Select");
                        builder.setCancelable(true);
                        builder.setItems(options, (dialog, which) -> {
                            if (options[which] == "Delete") {
                                DeleteProduct(favoritesIdList.get(position).getId());
                                favoritesIdList.remove(position);
                                favouriteAdapter.notifyDataSetChanged();
                            }
                        });
                        builder.show();
                    }
                }));

    }

    private void DeleteProduct(String id) {
        FirebaseFirestore
                .getInstance()
                .collection("Users")
                .document(MainActivity.usernameUid)
                .collection("Fav")
                .document(id)
                .delete();

    }


}