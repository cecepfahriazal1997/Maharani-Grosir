package com.application.mgoaplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.helper.GeneralHelper;
import com.application.mgoaplication.model.CouponModel;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<CouponModel> listData = new ArrayList<>();
    private List<CouponModel> listFiltered = new ArrayList<>();
    private OnClickListener onClickListener;
    private GeneralHelper helper;

    public CouponAdapter(Context context, List<CouponModel> listData, GeneralHelper helper,
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
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CouponModel item = listFiltered.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.amount.setText("Rp. " + helper.formatCurrency(item.getAmount()));
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
                    List<CouponModel> filteredList = new ArrayList<>();
                    for (CouponModel row : listData) {
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
                listFiltered = (ArrayList<CouponModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, amount, description;
        OnClickListener onClickListener;
        FancyButton btnSubmit;

        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            title                   = itemView.findViewById(R.id.title);
            amount                  = itemView.findViewById(R.id.amount);
            description             = itemView.findViewById(R.id.description);
            btnSubmit               = itemView.findViewById(R.id.btnSubmit);
            this.onClickListener    = onClickListener;

            btnSubmit.setOnClickListener(this);
            itemView.setOnClickListener(this);
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