package com.application.mgoaplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context context;
    private List<ProductModel> listData = new ArrayList<>();
    private OnClickListener onClickListener;
    private GeneralHelper helper;
    private boolean isEdit;

    public CartAdapter(Context context, List<ProductModel> listData, GeneralHelper helper, boolean isEdit,
                       OnClickListener onClickListener) {
        this.context            = context;
        this.listData           = listData;
        this.onClickListener    = onClickListener;
        this.helper             = helper;
        this.isEdit             = isEdit;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel item = listData.get(position);
        holder.title.setText(item.getName());
        holder.listItem.removeAllViews();
        helper.fetchImage(holder.itemView, item.getImage(), holder.image);
//        holder.delete.getIconImageObject().setLayoutParams(new LinearLayout.LayoutParams(48, 48));

        if (isEdit) {
            holder.contentAction.setVisibility(View.VISIBLE);
        } else {
            holder.contentAction.setVisibility(View.GONE);
        }

        try {
            JSONArray listItem = item.getListPrice();
            for (int i = 0; i < listItem.length(); i++) {
                JSONObject detail   = listItem.getJSONObject(i);
                View itemPick       = helper.inflateView(R.layout.item_picker_secondary);
                TextView title      = itemPick.findViewById(R.id.title);
                TextView price      = itemPick.findViewById(R.id.price);
                EditText total      = itemPick.findViewById(R.id.total);
                ImageButton minus   = itemPick.findViewById(R.id.minus);
                ImageButton plus    = itemPick.findViewById(R.id.plus);

                title.setText(detail.getString("nama_satuan"));
                price.setText("Rp. " + helper.formatCurrency(detail.getString("total")));
                total.setText(detail.getString("jumlah"));
                holder.listItem.addView(itemPick);

                if (isEdit) {
                    minus.setVisibility(View.VISIBLE);
                    plus.setVisibility(View.VISIBLE);
                    minus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setTotal(total, 0, detail);
                        }
                    });

                    plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setTotal(total, 1, detail);
                        }
                    });
                } else {
                    minus.setVisibility(View.GONE);
                    plus.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTotal(EditText value, int operation, JSONObject detail) {
        int total    = 0;
        if (!value.getText().toString().isEmpty()) {
            total    = Integer.parseInt(value.getText().toString());
            if (operation == 0) {
                if (total > 0)
                    total--;
            } else {
                total++;
            }
        }
        value.setText(String.valueOf(total));
        try {
            detail.put("jumlah", total);
            notifyDataSetChanged();
        } catch (Exception e) {}
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView title;
        LinearLayout listItem;
        OnClickListener  onClickListener;
        FancyButton delete, update;
        RelativeLayout contentAction;

        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            image                   = itemView.findViewById(R.id.image);
            title                   = itemView.findViewById(R.id.title);
            listItem                = itemView.findViewById(R.id.listItem);
            update                  = itemView.findViewById(R.id.btnUpdate);
            delete                  = itemView.findViewById(R.id.btnDelete);
            contentAction           = itemView.findViewById(R.id.contentAction);
            this.onClickListener    = onClickListener;

            update.setOnClickListener(this);
            delete.setOnClickListener(this);
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