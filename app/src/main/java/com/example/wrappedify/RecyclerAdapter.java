package com.example.wrappedify;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    Context context;
    ArrayList<WrappedFeed> arrayList;
    public RecyclerAdapter(Context context, ArrayList<WrappedFeed> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        WrappedFeed wrappedFeed = arrayList.get(position);

        holder.title.setText(wrappedFeed.getTitle());
        holder.message.setText(wrappedFeed.getMessage());
        Picasso.get().load(wrappedFeed.getProfileIcon()).into(holder.profileImage);
        Picasso.get().load(wrappedFeed.getPostImage()).into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        ImageView postImage;
        TextView title;
        TextView message;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.ivProfile);
            postImage = itemView.findViewById(R.id.ivPost);
            title = itemView.findViewById(R.id.title);
            message = itemView.findViewById(R.id.message);

        }
    }
}
