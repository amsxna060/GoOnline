package com.amansiol.goonline.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amansiol.goonline.R;
import com.amansiol.goonline.models.Comment;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    Context context;
    ArrayList<Comment> commentList;

    public CommentAdapter(Context context, ArrayList<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @NotNull
    @Override
    public CommentAdapter.CommentHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.commentrow, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentAdapter.CommentHolder holder, int position) {
        holder.username.setText(commentList.get(position).getUsername());
        String tempprofilepic = commentList.get(position).getImage();
        if (tempprofilepic == null) {
            Picasso.get().load(R.drawable.profile).into(holder.imageView);
        } else {
            Picasso.get().load(tempprofilepic).into(holder.imageView);
        }
        holder.usercmt.setText(commentList.get(position).getCommentbody());
        final String posttime=commentList.get(position).getTimestamp();
        Calendar cal= Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(posttime));
        final String time= DateFormat.format("hh:mm aa",cal).toString();
        final String date= DateFormat.format("dd MMM yyyy",cal).toString();
        holder.time.setText("at "+time+", On " +date);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {

        CircularImageView imageView;
        TextView username;
        TextView usercmt;
        TextView time;

        public CommentHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profilepic);
            username = itemView.findViewById(R.id.username);
            usercmt = itemView.findViewById(R.id.commentbody);
            time = itemView.findViewById(R.id.time);
        }
    }
}
