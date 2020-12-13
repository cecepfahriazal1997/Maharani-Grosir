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
import com.application.mgoaplication.model.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class GridCategoryAdapter extends RecyclerView.Adapter<GridCategoryAdapter.ViewHolder> {
    private Context context;
    private List<CategoryModel> listData = new ArrayList<>();
    private OnClickListener onClickListener;
    private GeneralHelper helper;

    public GridCategoryAdapter(Context context, List<CategoryModel> listData, OnClickListener onClickListener) {
        this.context            = context;
        this.listData           = listData;
        this.onClickListener    = onClickListener;
        this.helper             = new GeneralHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_category, parent, false);
        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel item = listData.get(position);
        holder.title.setText(item.getTitle());
        helper.fetchImage(holder.image.getRootView(), item.getImage(), holder.image);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView title;
        OnClickListener  onClickListener;

        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            image                   = itemView.findViewById(R.id.image);
            title                   = itemView.findViewById(R.id.title);
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