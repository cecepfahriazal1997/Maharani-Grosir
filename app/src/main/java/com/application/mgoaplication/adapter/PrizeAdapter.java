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
import com.application.mgoaplication.model.PrizeModel;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class PrizeAdapter extends RecyclerView.Adapter<PrizeAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<PrizeModel> listData = new ArrayList<>();
    private List<PrizeModel> listFiltered = new ArrayList<>();
    private OnClickListener onClickListener;
    private GeneralHelper helper;

    public PrizeAdapter(Context context, List<PrizeModel> listData, GeneralHelper helper,
                        OnClickListener onClickListener) {
        this.context            = context;
        this.listData           = listData;
        this.listFiltered       = listData;
        this.onClickListener    = onClickListener;
        this.helper             = helper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_prize, parent, false);
        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PrizeModel item = listFiltered.get(position);
        holder.title.setText(item.getTitle());
        holder.coin.setText(item.getCoin());
        holder.date.setText(item.getDate());
        helper.fetchImage(holder.itemView, item.getImage(), holder.image);
    }

    @Override
    public int getItemCount() {
        return listFiltered.size();
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
                    List<PrizeModel> filteredList = new ArrayList<>();
                    for (PrizeModel row : listData) {
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
                listFiltered = (ArrayList<PrizeModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, coin, date;
        OnClickListener  onClickListener;
        ImageView image;
        FancyButton btnSubmit;

        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            image                   = itemView.findViewById(R.id.image);
            title                   = itemView.findViewById(R.id.title);
            coin                    = itemView.findViewById(R.id.coin);
            date                    = itemView.findViewById(R.id.date);
            btnSubmit               = itemView.findViewById(R.id.btnSubmit);
            this.onClickListener    = onClickListener;

            itemView.setOnClickListener(this);
            btnSubmit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClickListener(v, getAdapterPosition());
        }
    }

    public interface OnClickListener {
        void onClickListener(View view, int position);
    }
}