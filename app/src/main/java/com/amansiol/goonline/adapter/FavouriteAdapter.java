package com.amansiol.goonline.adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amansiol.goonline.MainActivity;
import com.amansiol.goonline.R;
import com.amansiol.goonline.models.Cart;
import com.amansiol.goonline.models.Favorite;
import com.amansiol.goonline.models.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {
    Context context;
    ArrayList<Favorite> products;
    CollectionReference mShopRef;
    Product product;

    public FavouriteAdapter(Context context, ArrayList<Favorite> products) {
        this.context = context;
        this.products = products;
        mShopRef = FirebaseFirestore.getInstance().collection("Shops");
        product = new Product();
    }

    @NonNull
    @NotNull
    @Override
    public FavouriteAdapter.FavouriteViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.fav_row, parent, false);
        return new FavouriteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FavouriteAdapter.FavouriteViewHolder holder, int position) {
        String id = products.get(position).getId();
        Cart cart = new Cart();
        if (id != null) {
            mShopRef.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot shops) {
                            for (QueryDocumentSnapshot shop : shops) {
                                mShopRef.document(shop.getId())
                                        .collection("Products")
                                        .addSnapshotListener((products, error) -> {

                                            for (QueryDocumentSnapshot prouduct : products) {

                                                if (prouduct.exists()) {
                                                    product = prouduct.toObject(Product.class);
                                                    if (product.getId().equals(id)) {
                                                        String product_title = product.getProduct_short_name();
                                                        String shop_name = product.getShop_name();
                                                        float strike_price = Float.parseFloat(product.getProduct_price());
                                                        float dis_price = Float.parseFloat(product.getDiscount());
                                                        float price = strike_price - dis_price;
//        String image=products.get(position).getImage1();
                                                        String image1 = product.getImage1();
                                                        if (image1 == null) {
                                                            holder.product_image.setImageResource(R.drawable.temppic);
                                                        } else {
                                                            Picasso.get().load(image1).into(holder.product_image);
                                                        }
                                                        cart.setId(product.getId());
                                                        cart.setPrice(String.valueOf(price));
                                                        holder.brand_name.setText(product.getProduct_short_name());
                                                        holder.product_title.setText(product.getProduct_long_title());
                                                        holder.product_cost.setText("$ " + String.format("%.2f", price));
                                                        holder.discountstrike.setText(Html.fromHtml("<strike>$ " + String.format("%.2f", strike_price) + "</strike>"));
                                                        holder.discountrate.setText("$ " + String.format("%.2f", dis_price) + " OFF");
                                                        holder.add_to_cart.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                FirebaseFirestore.getInstance()
                                                                        .collection("Users")
                                                                        .document(MainActivity.usernameUid)
                                                                        .collection("Cart")
                                                                        .document(product.getId())
                                                                        .set(cart, SetOptions.merge());
                                                            }
                                                        });
                                                    }
                                                }

                                            }
                                        });
                            }
                        }
                    });
        } else {
            Log.d("favAdapter", "id is null");
        }

    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    public class FavouriteViewHolder extends RecyclerView.ViewHolder {
        TextView discountrate, discountstrike, product_cost, brand_name, product_title;
        ImageView product_image;
        Button add_to_cart;
        RelativeLayout whole_layout;
        RelativeLayout noexist;

        public FavouriteViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            discountrate = itemView.findViewById(R.id.discountrate);
            discountstrike = itemView.findViewById(R.id.discountstrike);
            product_cost = itemView.findViewById(R.id.product_cost);
            brand_name = itemView.findViewById(R.id.brand_name);
            product_title = itemView.findViewById(R.id.product_title);
            product_image = itemView.findViewById(R.id.product_image);
            add_to_cart = itemView.findViewById(R.id.add_to_cart);
            whole_layout = itemView.findViewById(R.id.wholelayout);
        }
    }
}
