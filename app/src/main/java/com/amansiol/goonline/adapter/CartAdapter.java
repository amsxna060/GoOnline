package com.amansiol.goonline.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amansiol.goonline.R;
import com.amansiol.goonline.models.Cart;
import com.amansiol.goonline.models.Product;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    Context context;
    ArrayList<Cart> cartlist;
    CollectionReference mShopRef;

    public CartAdapter(Context context, ArrayList<Cart> cartlist) {
        this.context = context;
        this.cartlist = cartlist;
        mShopRef = FirebaseFirestore.getInstance().collection("Shops");
    }

    @NonNull
    @NotNull
    @Override
    public CartAdapter.CartHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cartrow, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CartAdapter.CartHolder holder, int position) {
        String id = cartlist.get(position).getId();
        if (id != null) {
            mShopRef.addSnapshotListener((value, error) -> {
                for (QueryDocumentSnapshot shopDocumentSnap : value) {
                    mShopRef.document(shopDocumentSnap.getId())
                            .collection("Products")
                            .addSnapshotListener((value1, error1) -> {
                                for (QueryDocumentSnapshot documentSnapshot : value1) {
                                    if (documentSnapshot.exists()) {
                                        Product product = documentSnapshot.toObject(Product.class);
                                        if (product.getId().equals(id)) {
                                            holder.short_title.setText(product.getProduct_short_name());
                                            holder.long_title.setText(product.getProduct_long_title());
                                            Picasso.get().load(product.getImage1()).into(holder.cartimageitem);
                                            float strike_price = Float.parseFloat(product.getProduct_price());
                                            float dis_price = Float.parseFloat(product.getDiscount());
                                            float price = strike_price - dis_price;
                                            holder.product_price.setText("$ " + String.format("%.2f", price));
                                        }
                                    }

                                }
                            });
                }

            });
        } else {
            Log.d("cartAdapter", "id is null");
        }


    }

    public void removeItem(int position) {
        cartlist.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Cart item, int position) {
        cartlist.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return cartlist.size();
    }

    public class CartHolder extends RecyclerView.ViewHolder {

        TextView product_price, short_title, long_title;
        ImageView cartimageitem;
        public RelativeLayout viewBackground, viewForeground;

        public CartHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            product_price = itemView.findViewById(R.id.product_price);
            short_title = itemView.findViewById(R.id.product_title);
            cartimageitem = itemView.findViewById(R.id.cartitemimage);
            long_title = itemView.findViewById(R.id.product_long_title);
            viewForeground = itemView.findViewById(R.id.mainlayout);
            viewBackground = itemView.findViewById(R.id.view_background);

        }
    }
}
