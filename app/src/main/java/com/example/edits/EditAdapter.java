package com.example.edits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.EditViewHolder> {

    // 1. Interface to handle click actions (Update / Delete) back in the Fragment
    public interface OnEditActionListener {
        void onUpdateClick(Edit edit);
        void onDeleteClick(Edit edit);
    }

    private final Context context;
    private final ArrayList<Edit> editList;
    private final OnEditActionListener listener;

    // Constructor
    public EditAdapter(Context context, ArrayList<Edit> editList, OnEditActionListener listener) {
        this.context = context;
        this.editList = editList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the single row layout item_edit.xml
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit, parent, false);
        return new EditViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull EditViewHolder holder, int position) {
        Edit edit = editList.get(position);

        // Bind text data to views
        holder.tvTitle.setText(edit.getTitle());
        holder.tvShowName.setText(edit.getShowName());
        holder.tvCharacterName.setText(edit.getCharacterName());

        // Load thumbnail using Glide
        if (edit.getImageUrl() != null && !edit.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(edit.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivThumbnail);
        } else {
            holder.ivThumbnail.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Click listeners for Update and Delete buttons
        holder.btnUpdate.setOnClickListener(v -> listener.onUpdateClick(edit));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(edit));
    }

    @Override
    public int getItemCount() {
        return editList.size();
    }

    // 2. ViewHolder: Holds the views of a single row so we find them only once
    public static class EditViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle, tvShowName, tvCharacterName;
        Button btnUpdate, btnDelete;

        public EditViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvShowName = itemView.findViewById(R.id.tvShowName);
            tvCharacterName = itemView.findViewById(R.id.tvCharacterName);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}