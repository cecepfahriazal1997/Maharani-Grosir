package com.application.mgoaplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.helper.GeneralHelper;
import com.application.mgoaplication.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductSecondaryAdapter extends RecyclerView.Adapter<ProductSecondaryAdapter.ViewHolder> {
    private Context context;
    private List<ProductModel> listData = new ArrayList<>();
    private OnClickListener onClickListener;
    private GeneralHelper helper;

    public ProductSecondaryAdapter(Context context, List<ProductModel> listData, GeneralHelper helper,
                                   OnClickListener onClickListener) {
        this.context            = context;
        this.listData           = listData;
        this.onClickListener    = onClickListener;
        this.helper             = helper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_product_secondary, parent, false);
        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel item = listData.get(position);
        holder.title.setText(item.getName());
        holder.description.setText(item.getDescription());
        helper.fetchImage(holder.itemView, item.getImage(), holder.image);

        try {
            JSONArray listItem = item.getListPrice();
            holder.listItem.removeAllViews();
            for (int i = 0; i < listItem.length(); i++) {
                JSONObject detail = listItem.getJSONObject(i);
                View itemPick = helper.inflateView(R.layout.item_price);
                TextView title = itemPick.findViewById(R.id.title);
                TextView price = itemPick.findViewById(R.id.price);
                title.setText(detail.getString("jumlah") + " x " + detail.getString("nama_satuan"));
                price.setText("Rp. " + helper.formatCurrency(detail.getString("total")));
                holder.listItem.addView(itemPick);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView title, description;
        OnClickListener  onClickListener;
        LinearLayout listItem;

        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            image                   = itemView.findViewById(R.id.image);
            title                   = itemView.findViewById(R.id.name);
            description             = itemView.findViewById(R.id.description);
            listItem                = itemView.findViewById(R.id.listItem);
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