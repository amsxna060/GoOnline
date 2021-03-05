package com.amansiol.goonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

public class DashBoard extends AppCompatActivity {

    MaterialCardView shopprofile,myproduct,add_product,change_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_dash_board);
        shopprofile=findViewById(R.id.shopprofile);
        myproduct=findViewById(R.id.myproduct);
        add_product=findViewById(R.id.add_product);
        change_address=findViewById(R.id.change_address);

        shopprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this,ShopProfile.class);
                startActivity(intent);
            }
        });
        change_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this,ChangeAddressShop.class);
                startActivity(intent);
            }
        });
        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this,AddProduct.class);
                startActivity(intent);
            }
        });
        myproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this,ShopAllProduct.class);
                startActivity(intent);
            }
        });
    }
}