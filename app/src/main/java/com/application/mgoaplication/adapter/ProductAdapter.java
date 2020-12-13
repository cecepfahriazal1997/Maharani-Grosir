package com.application.mgoaplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.helper.GeneralHelper;
import com.application.mgoaplication.model.ProductModel;
import com.application.mgoaplication.model.ProductModel;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<ProductModel> listData = new ArrayList<>();
    private List<ProductModel> listFiltered = new ArrayList<>();
    private OnClickListener onClickListener;
    private GeneralHelper helper;
    private Service service;
    private Map<String, Object> param = new HashMap<>();

    public ProductAdapter(Context context, List<ProductModel> listData, GeneralHelper helper, Service service,
                          OnClickListener onClickListener) {
        this.context            = context;
        this.listData           = listData;
        this.listFiltered       = listData;
        this.onClickListener    = onClickListener;
        this.helper             = helper;
        this.service            = service;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_product, parent, false);
        return new ViewHolder(view, onClickListener, param);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel item = listFiltered.get(position);
        holder.title.setText(item.getName());
        holder.listPicker.removeAllViews();
        holder.description.setText(item.getDescription());
        helper.fetchImage(holder.itemView, item.getImage(), holder.image);

        try {
            Map<String, Object> paramAmount = new HashMap<>();
            if (item.getListPrice() != null) {
                paramAmount.clear();
                JSONArray listPrice = item.getListPrice();
                for (int i = 0; i < listPrice.length(); i++) {
                    JSONObject detailPrice = listPrice.getJSONObject(i);

                    View itemPick       = helper.inflateView(R.layout.item_picker);
                    LinearLayout parent = itemPick.findViewById(R.id.content);
                    TextView title      = itemPick.findViewById(R.id.title);
                    ImageButton minus   = itemPick.findViewById(R.id.minus);
                    ImageButton plus    = itemPick.findViewById(R.id.plus);
                    EditText value      = itemPick.findViewById(R.id.value);

                    paramAmount.put("satuan" + detailPrice.getString("id_satuan"), 0);
                    paramAmount.put("price" + detailPrice.getString("id_satuan"), 0);

                    minus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setTotal(value, item.getId(), detailPrice, 0, paramAmount);
                        }
                    });

                    plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setTotal(value, item.getId(), detailPrice, 1, paramAmount);
                        }
                    });

                    title.setText(detailPrice.getString("satuan"));
                    parent.setPadding((int) helper.convertDpToPixel(8), (int) helper.convertDpToPixel(4), (int) helper.convertDpToPixel(8), 0);
                    parent.setLayoutParams(new LinearLayout.LayoutParams((int) helper.convertDpToPixel(100), ViewGroup.LayoutParams.WRAP_CONTENT));
                    holder.listPicker.addView(itemPick);
                }
                param.put("amount_" + item.getId(), paramAmount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTotal(EditText value, String id, JSONObject detailPrice, int operation, Map<String, Object> paramAmount) {
        int total       = 0;
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
            paramAmount.put("satuan" + detailPrice.getString("id_satuan"), total);
            paramAmount.put("price" + detailPrice.getString("id_satuan"), total * Integer.parseInt(detailPrice.getString("harga")));
            param.put("amount_" + id, paramAmount);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    List<ProductModel> filteredList = new ArrayList<>();
                    for (ProductModel row : listData) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
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
                listFiltered = (ArrayList<ProductModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView title, description;
        FlexboxLayout listPicker;
        OnClickListener  onClickListener;
        FancyButton btnSubmit;
        Map<String, Object> params;

        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener, Map<String, Object> params) {
            super(itemView);
            image                   = itemView.findViewById(R.id.image);
            title                   = itemView.findViewById(R.id.name);
            description             = itemView.findViewById(R.id.description);
            listPicker              = itemView.findViewById(R.id.listPicker);
            btnSubmit               = itemView.findViewById(R.id.btnSubmit);
            this.onClickListener    = onClickListener;
            this.params             = params;

            btnSubmit.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClickListener(v, getAdapterPosition(), params);
        }
    }

    public interface OnClickListener {
        void onClickListener(View view, int position, Map<String, Object> params);
    }
}