package com.amansiol.goonline.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amansiol.goonline.DescriptionActivity;
import com.amansiol.goonline.R;
import com.amansiol.goonline.models.Product;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
/*
  This is ProductAdapter Class which
  is used to populate the data into
  RecyclerView UI.
 */
public class ProductAdaptor extends RecyclerView.Adapter<ProductAdaptor.ProductViewHolder> {

    Context context;
    ArrayList<Product> products;

    //constructor
    public ProductAdaptor(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }
    /*
    This function is used for inflate or connect oneproduct.xml layout
    to this java adapter
     */
    @NonNull
    @NotNull
    @Override
    public ProductAdaptor.ProductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.oneproduct,parent,false);
        return new ProductViewHolder(v);
    }
  /*
  This function is used for set data regarding one product
  to all the view present in oneproduct.xml like image in imageview
  and product title and product price in respective textView etc.
   */
    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductAdaptor.ProductViewHolder holder, int position) {
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, DescriptionActivity.class);
                intent.putExtra("myproduct",products.get(position));
                context.startActivity(intent);
            }
        });
    }



    /*
    this function will give us how many product we have till now.
   */
    @Override
    public int getItemCount() {
        return products.size();
    }
 /*
 This is View holder class Used by ProductAdapter to
 how many view we have in oneproduct.xml and to access those view
 in ProductAdapter.java class.
  */
    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView discountrate,discountstrike,product_cost,brand_name,product_title;
        ImageView product_image;
        public ProductViewHolder(@NonNull @NotNull View itemView) {
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
