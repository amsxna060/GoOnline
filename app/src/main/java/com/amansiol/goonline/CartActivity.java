package com.amansiol.goonline;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amansiol.goonline.adapter.CartAdapter;
import com.amansiol.goonline.models.Cart;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    ArrayList<Cart> cartlist;
    RecyclerView cartRecycler;
    CartAdapter cartAdapter;
    float totalprice=0.0f;
    float shippingprice=0.0f;
    float totalcartprice=0.0f;
    Button continueshopping;
    TextView totalitemprice;
    TextView shippingfee;
    TextView completetotal;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_cart);
        cartRecycler=findViewById(R.id.cartrecycler);
        continueshopping=findViewById(R.id.continueshopping);
        totalitemprice=findViewById(R.id.totalitemprice);
        shippingfee=findViewById(R.id.shippingfee);
        completetotal=findViewById(R.id.completetotal);
        cartRecycler.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        cartlist= new ArrayList<>();
        relativeLayout=findViewById(R.id.cartLayout);
        getCartListFromFirebase();

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(cartRecycler);

        continueshopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this,MainActivity.class));
                finish();
            }
        });



    }

    private void getCartListFromFirebase() {
        cartlist.clear();
        totalprice=0.0f;
        shippingprice=0.0f;
        totalcartprice=0.0f;
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(MainActivity.usernameUid)
                .collection("Cart")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot queryDocumentSnapshot:value)
                        {
                            Cart cart=queryDocumentSnapshot.toObject(Cart.class);
                            cartlist.add(cart);
                            totalprice+=Float.parseFloat(cart.getPrice());
                            Log.d("cartActivity", String.valueOf(totalprice));
                            shippingprice+=5.0;
                            Log.d("cartActivity", String.valueOf(shippingprice));
                            totalitemprice.setText("$ "+ String.format("%.2f",totalprice));
                            shippingfee.setText("$ "+ String.format("%.2f",shippingprice));
                            totalcartprice=totalprice+shippingprice;
                            completetotal.setText("$ "+ String.format("%.2f",totalcartprice));
                        }
                        cartAdapter=new CartAdapter(CartActivity.this,cartlist);
                        cartRecycler.setAdapter(cartAdapter);
                        cartAdapter.notifyDataSetChanged();
                    }
                });



    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartAdapter.CartHolder) {
            // get the removed item name to display it in snack bar
            String name = "This Product";
            // backup of removed item for undo purpose
            final Cart deletedItem = cartlist.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            cartAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            FirebaseFirestore
                    .getInstance()
                    .collection("Users")
                    .document(MainActivity.usernameUid)
                    .collection("Cart")
                    .document(deletedItem.getId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent res = getIntent();
                            overridePendingTransition(0, 0);
                            res.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(res);
                            finish();
                        }
                    });

        }

    }
}