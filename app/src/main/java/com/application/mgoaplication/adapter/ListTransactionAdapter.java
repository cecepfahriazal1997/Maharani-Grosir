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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.helper.GeneralHelper;
import com.application.mgoaplication.model.ProductModel;
import com.application.mgoaplication.model.TransactionModel;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class ListTransactionAdapter extends RecyclerView.Adapter<ListTransactionAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<TransactionModel> listData = new ArrayList<>();
    private List<TransactionModel> listFiltered = new ArrayList<>();
    private OnClickListener onClickListener;
    private ProductSecondaryAdapter productAdapter;
    private GeneralHelper helper;

    public ListTransactionAdapter(Context context, List<TransactionModel> listData, GeneralHelper helper, OnClickListener onClickListener) {
        this.context            = context;
        this.listData           = listData;
        this.listFiltered       = listData;
        this.onClickListener    = onClickListener;
        this.helper             = helper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionModel item = listFiltered.get(position);
        holder.orderNumber.setText("#" + item.getOrderNumber());
        holder.amount.setText("Rp. " + item.getAmount());
        holder.total.setText(item.getTotal() + " Produk");
        holder.date.setText(item.getDate());
        if (item.getStatus().equals("0") || item.getStatus().equals("1")) {
            holder.btnSubmit.setText("Tanya Penjual");
        } else {
            holder.btnSubmit.setText("Beli Lagi");
        }

        if (item.getStatus().equals("0")) {
            holder.status.setText("Sedang Dikemas");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.gold));
            holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.box));
        } else if (item.getStatus().equals("1")) {
            holder.status.setText("Sedang Diproses");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.blue));
            holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.truck));
        } else if (item.getStatus().equals("2")) {
            holder.status.setText("Selesai");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.greenTosca));
            holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.box_done));
        }
        initData(holder.recyclerView, item.getItem());
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
                    List<TransactionModel> filteredList = new ArrayList<>();
                    for (TransactionModel row : listData) {
//                        if (row.getOrderNumber().toLowerCase().contains(charString.toLowerCase())) {
//                            filteredList.add(row);
//                        }
                        for (ProductModel detail : row.getItem()) {
                            if (detail.getName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
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
                listFiltered = (ArrayList<TransactionModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView orderNumber, date, total, amount, status;
        OnClickListener onClickListener;
        RecyclerView recyclerView;
        FancyButton btnSubmit;

        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            icon                    = itemView.findViewById(R.id.icon);
            orderNumber             = itemView.findViewById(R.id.orderNumber);
            date                    = itemView.findViewById(R.id.date);
            total                   = itemView.findViewById(R.id.total);
            amount                  = itemView.findViewById(R.id.amount);
            status                  = itemView.findViewById(R.id.status);
            recyclerView            = itemView.findViewById(R.id.list);
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

    private void initData(RecyclerView recyclerView, List<ProductModel> list) {
        LinearLayoutManager linearLayoutManager     = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        ProductSecondaryAdapter adapter             = new ProductSecondaryAdapter(context, list, helper,
                new ProductSecondaryAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(int position) {
                    }
                });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        this.productAdapter = adapter;
    }
}