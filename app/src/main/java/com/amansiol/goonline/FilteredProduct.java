package com.amansiol.goonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import org.jetbrains.annotations.NotNull;


public class FilteredProduct extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    // navigation Objects

    private DrawerLayout mNavDrawer;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView search;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.catagories_nav_layout);

        /*
        NavigationView inits
         */
        toolbar=findViewById(R.id.toolbar);
        mNavDrawer=findViewById(R.id.catagories_nav_lay);
        navigationView = findViewById(R.id.navigation_view);
        search=findViewById(R.id.title);


        search.setOnClickListener(v -> {
            startActivity(new Intent(FilteredProduct.this,SearchActivity.class));
        });



        /*
        Navigation Related Code
         */
        // making toolbar as actionbar
        setSupportActionBar(toolbar);


        // making toogle button to open navigation drawer
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,mNavDrawer,toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorTheme));

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout,new FilterByRadiusFragment())
                    .commit(); }
        // this is two function calls is to connecting togglebutton to navigation drawer.
        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();
        // this is for setup onclick listener on navigation drawer menu item or button.
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if(mNavDrawer.isDrawerOpen(GravityCompat.START)){
            mNavDrawer.closeDrawer(GravityCompat.START);
        }
        else {
            startActivity(new Intent(FilteredProduct.this,MainActivity.class));
            finish();
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.shirt:
                Intent i=new Intent(getApplicationContext(),FilteredProduct.class);
                i.putExtra("product_type",item.getTitle().toString().trim());
                i.putExtra("gender","Mens");
                startActivity(i);
                finish();
                break;
            case R.id.tshirt:
                Intent i1=new Intent(getApplicationContext(),FilteredProduct.class);
                i1.putExtra("product_type",item.getTitle().toString().trim());
                i1.putExtra("gender","Mens");
                startActivity(i1);
                finish();
                break;
            case R.id.jeans:
                Intent i2=new Intent(getApplicationContext(),FilteredProduct.class);
                i2.putExtra("product_type",item.getTitle().toString().trim());
                i2.putExtra("gender","Mens");
                startActivity(i2);
                finish();
                break;
            case R.id.hoddies:
                Intent i3=new Intent(getApplicationContext(),FilteredProduct.class);
                i3.putExtra("product_type",item.getTitle().toString().trim());
                i3.putExtra("gender","Mens");
                startActivity(i3);
                finish();
                break;
            case R.id.blazers:
                Intent i4=new Intent(getApplicationContext(),FilteredProduct.class);
                i4.putExtra("product_type",item.getTitle().toString().trim());
                i4.putExtra("gender","Mens");
                startActivity(i4);
                finish();
                break;
            case R.id.lehenga:
                Intent i5=new Intent(getApplicationContext(),FilteredProduct.class);
                i5.putExtra("product_type",item.getTitle().toString().trim());
                i5.putExtra("gender","Womens");
                startActivity(i5);
                finish();
                break;
            case R.id.tops:
                Intent i6=new Intent(getApplicationContext(),FilteredProduct.class);
                i6.putExtra("product_type",item.getTitle().toString().trim());
                i6.putExtra("gender","Womens");
                startActivity(i6);
                finish();
                break;
            case R.id.sports:
                Intent i7=new Intent(getApplicationContext(),FilteredProduct.class);
                i7.putExtra("product_type",item.getTitle().toString().trim());
                i7.putExtra("gender","Womens");
                startActivity(i7);
                finish();
                break;
            case R.id.saree:
                Intent i8=new Intent(getApplicationContext(),FilteredProduct.class);
                i8.putExtra("product_type",item.getTitle().toString().trim());
                i8.putExtra("gender","Womens");
                startActivity(i8);
                finish();
                break;
            case R.id.sweater:
                Intent i9=new Intent(getApplicationContext(),FilteredProduct.class);
                i9.putExtra("product_type",item.getTitle().toString().trim());
                i9.putExtra("gender","Womens");
                startActivity(i9);
                finish();
                break;
            case R.id.casual:
                Intent i10=new Intent(getApplicationContext(),FilteredProduct.class);
                i10.putExtra("product_type",item.getTitle().toString().trim());
                i10.putExtra("gender","KidsWear");
                startActivity(i10);
                finish();
                break;
            case R.id.partywear:
                Intent i11=new Intent(getApplicationContext(),FilteredProduct.class);
                i11.putExtra("product_type",item.getTitle().toString().trim());
                i11.putExtra("gender","KidsWear");
                startActivity(i11);
                finish();
                break;
            case R.id.utshirts:
                Intent i12=new Intent(getApplicationContext(),FilteredProduct.class);
                i12.putExtra("product_type",item.getTitle().toString().trim());
                i12.putExtra("gender","Unisex");
                startActivity(i12);
                finish();
                break;
            case R.id.ushirts:
                Intent i13=new Intent(getApplicationContext(),FilteredProduct.class);
                i13.putExtra("product_type",item.getTitle().toString().trim());
                i13.putExtra("gender","Unisex");
                startActivity(i13);
                finish();
                break;


        }
        mNavDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
    // this is for inflating  menu  for cart and favourite button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.mainmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.fav:
                startActivity(new Intent(getApplicationContext(),ImageBasedSeachActivity.class));
                break;
            case R.id.cart:
                startActivity(new Intent(getApplicationContext(),CartActivity.class));
                break;
            case R.id.search:
                startActivity(new Intent(FilteredProduct.this,SearchActivity.class));
                break;
        }
        return false;
    }

}