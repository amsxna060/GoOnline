package com.amansiol.goonline.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amansiol.goonline.R;
import com.amansiol.goonline.models.Product;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ShopProductAdapter extends RecyclerView.Adapter<ShopProductAdapter.ShopViewHolder> {

    Context context;
    ArrayList<Product> products;

    public ShopProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @NotNull
    @Override
    public ShopProductAdapter.ShopViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.shoponeproduct,parent,false);
        return new ShopViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ShopProductAdapter.ShopViewHolder holder, int position) {
        String product_title=products.get(position).getProduct_short_name();
        String shop_name=products.get(position).getShop_name();
        float strike_price= Float.parseFloat(products.get(position).getProduct_price());
        float dis_price= Float.parseFloat(products.get(position).getDiscount());
        float price=strike_price-dis_price;
//        String image=products.get(position).getImage1();
        String image1=products.get(position).getImage1();
        if(image1==null)
        {
            holder.product_image.setImageResource(R.drawable.temppic);
        }else {
            Picasso.get().load(image1).into(holder.product_image);
        }

        holder.brand_name.setText(products.get(position).getProduct_short_name());
        holder.product_title.setText(products.get(position).getProduct_long_title());
        holder.product_cost.setText("$ "+ String.format("%.2f",price));
        holder.discountstrike.setText(Html.fromHtml("<strike>$ "+String.format("%.2f",strike_price)+"</strike>"));
        holder.discountrate.setText("$ "+ String.format("%.2f",dis_price)+" OFF");
    }



    @Override
    public int getItemCount() {
        return products.size();
    }


    public class ShopViewHolder extends RecyclerView.ViewHolder {
        TextView discountrate,discountstrike,product_cost,brand_name,product_title;
        ImageView product_image;
        public ShopViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            discountrate=itemView.findViewById(R.id.discountrate);
            discountstrike=itemView.findViewById(R.id.discountstrike);
            product_cost=itemView.findViewById(R.id.product_cost);
            brand_name=itemView.findViewById(R.id.brand_name);
            product_title=itemView.findViewById(R.id.product_title);
            product_image=itemView.findViewById(R.id.product_image);
        }

    }
}
