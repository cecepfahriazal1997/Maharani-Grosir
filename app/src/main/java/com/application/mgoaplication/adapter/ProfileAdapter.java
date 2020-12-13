package com.application.mgoaplication.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.model.ProfileModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private Context context;
    private List<ProfileModel> listData = new ArrayList<>();
    private OnClickListener onClickListener;

    public ProfileAdapter(Context context, List<ProfileModel> listData, OnClickListener onClickListener) {
        this.context            = context;
        this.listData           = listData;
        this.onClickListener    = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false);
        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProfileModel item = listData.get(position);
        holder.title.setText(item.getTitle());
        if (item.getSubtitle() != null) {
            holder.subtitle.setVisibility(View.VISIBLE);
            holder.subtitle.setText(item.getSubtitle());
        } else {
            holder.subtitle.setVisibility(View.VISIBLE);
            holder.subtitle.setVisibility(View.GONE);
        }
        holder.image.setImageResource(item.getImage());
        holder.title.setTextColor(ContextCompat.getColor(context, item.getColorTitle()));
        holder.subtitle.setTextColor(ContextCompat.getColor(context, item.getColorSubtitle()));
        holder.image.setColorFilter(ContextCompat.getColor(context, item.getColorImage()),
                PorterDuff.Mode.SRC_IN);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView title, subtitle;
        OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            image                   = itemView.findViewById(R.id.image);
            title                   = itemView.findViewById(R.id.title);
            subtitle                = itemView.findViewById(R.id.subtitle);
            this.onClickListener    = onClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClickListener(getAdapterPosition());
        }
    }

    public interface OnClickListener {
        void onClickListener(int position);
    }
}