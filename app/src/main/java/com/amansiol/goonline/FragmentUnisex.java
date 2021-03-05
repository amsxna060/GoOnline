package com.amansiol.goonline;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amansiol.goonline.adapter.CategoryAdapter;
import com.amansiol.goonline.models.Category;

import java.util.ArrayList;


public class FragmentUnisex extends Fragment {

    RecyclerView categoriesRecycler;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> categories;
    String[] categoryNames = {"Tee-Shirts","Shirts"};
    int[] categoryImages = {R.drawable.unisex_tshirt, R.drawable.images};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_unisex, container, false);
        categoriesRecycler = view.findViewById(R.id.recycler_cat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // set the layout manager to RecyclerView
        categoriesRecycler.setLayoutManager(linearLayoutManager);
        // init Arraylist
        categories = new ArrayList<>();

        //Get data from this function and store it in arraylist
        getAllCatories();
        return  view;
    }
    private void getAllCatories() {

        for (int i = 0; i <categoryNames.length;i++) {
            Category category=new Category(categoryNames[i],categoryImages[i]);
            categories.add(category);

        }
        //then pass this array list to Categories Adapter

        categoryAdapter= new CategoryAdapter(getContext(), categories, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Category item) {
                Intent i=new Intent(getContext(),FilteredProduct.class);
                i.putExtra("product_type",item.getTitle().trim());
                i.putExtra("gender","Unisex");
                startActivity(i);
            }
        });
        // then just connect ProductAdaptor to RecyclerView to populate all data there.
        categoriesRecycler.setAdapter(categoryAdapter);

    }
}