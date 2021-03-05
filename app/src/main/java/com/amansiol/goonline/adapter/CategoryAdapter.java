package com.amansiol.goonline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amansiol.goonline.R;
import com.amansiol.goonline.models.Category;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    Context context;
    ArrayList<Category> categories;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Category item);
    }

    public CategoryAdapter(Context context, ArrayList<Category> categories, OnItemClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public CategoryAdapter.CategoryHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(context).inflate(R.layout.categories_row,parent,false);
        return new CategoryHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryAdapter.CategoryHolder holder, int position) {

        holder.bind(categories.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {

        ImageView bgimage;
        TextView title;

        public CategoryHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            bgimage=itemView.findViewById(R.id.categories_image);
            title=itemView.findViewById(R.id.categories_title);

        }
        public void bind(final Category item, final OnItemClickListener listener) {
            title.setText(item.getTitle());
            bgimage.setImageResource(item.getBgimage());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
