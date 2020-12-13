package com.application.mgoaplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.helper.GeneralHelper;
import com.application.mgoaplication.model.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class ListCategoryAdapter extends RecyclerView.Adapter<ListCategoryAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<CategoryModel> listData = new ArrayList<>();
    private List<CategoryModel> listFiltered = new ArrayList<>();
    private OnClickListener onClickListener;
    private GeneralHelper helper;

    public ListCategoryAdapter(Context context, List<CategoryModel> listData, OnClickListener onClickListener) {
        this.context            = context;
        this.listData           = listData;
        this.listFiltered       = listData;
        this.onClickListener    = onClickListener;
        this.helper             = new GeneralHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_category, parent, false);
        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel item = listFiltered.get(position);
        holder.title.setText(item.getTitle());
        holder.total.setText(item.getTotal() + " Produk");
        if (item.getImage() != null) {
            helper.fetchImage(holder.image.getRootView(), item.getImage(), holder.image);
            holder.image.setVisibility(View.VISIBLE);
        } else {
            holder.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listFiltered.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView title, total;
        OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            image                   = itemView.findViewById(R.id.image);
            title                   = itemView.findViewById(R.id.title);
            total                   = itemView.findViewById(R.id.total);
            this.onClickListener    = onClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClickListener(getAdapterPosition());
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listFiltered = listData;
                } else {
                    List<CategoryModel> filteredList = new ArrayList<>();
                    for (CategoryModel row : listData) {
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    listFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listFiltered = (ArrayList<CategoryModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnClickListener {
        void onClickListener(int position);
    }
}