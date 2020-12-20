package com.application.mgoaplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.helper.GeneralHelper;
import com.application.mgoaplication.model.AnnouncementModel;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {
    private Context context;
    private List<AnnouncementModel> listData = new ArrayList<>();
    private OnClickListener onClickListener;
    private GeneralHelper helper;

    public AnnouncementAdapter(Context context, List<AnnouncementModel> listData, GeneralHelper helper,
                               OnClickListener onClickListener) {
        this.context            = context;
        this.listData           = listData;
        this.onClickListener    = onClickListener;
        this.helper             = helper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnnouncementModel item = listData.get(position);
        holder.title.setText(item.getTitle());
        holder.date.setText(item.getDate());
        helper.setTextHtml(holder.description, item.getDescription());
        helper.fetchImage(holder.imageView.getRootView(), item.getImage(), holder.imageView);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView title, description, date;
        OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            imageView               = itemView.findViewById(R.id.image);
            title                   = itemView.findViewById(R.id.title);
            description             = itemView.findViewById(R.id.description);
            date                    = itemView.findViewById(R.id.date);
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