package com.application.mgoaplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.model.ModelPickerMaster;

import java.util.ArrayList;
import java.util.List;

public class AdapterPickerMaster extends RecyclerView.Adapter<AdapterPickerMaster.ViewHolder> implements Filterable {
    private Context context;
    private List<ModelPickerMaster> listData = new ArrayList<>();
    private List<ModelPickerMaster> listFiltered = new ArrayList<>();
    private OnClickListener onClickListener;

    public AdapterPickerMaster(Context context, List<ModelPickerMaster> listData, OnClickListener onClickListener) {
        this.context            = context;
        this.listData           = listData;
        this.listFiltered         = listData;
        this.onClickListener    = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picker_master, parent, false);

        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelPickerMaster item = listFiltered.get(position);
        holder.Title.setText(item.getTitle());

        if (item.isMultiple()) {
            holder.Check.setVisibility(View.VISIBLE);
            holder.Check.setChecked(item.isChecked());
        } else {
            holder.Check.setVisibility(View.GONE);
        }
        if (item.isChecked()) {
            holder.Content.setBackgroundColor(ContextCompat.getColor(context, R.color.transparentPrimary));
            holder.Title.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            holder.Content.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.Title.setTextColor(ContextCompat.getColor(context, R.color.mediumBlack));
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
                    List<ModelPickerMaster> filteredList = new ArrayList<>();
                    for (ModelPickerMaster row : listData) {
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
                listFiltered = (ArrayList<ModelPickerMaster>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Title;
        AppCompatCheckBox Check;
        OnClickListener  onClickListener;
        LinearLayout Content;

        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            Title       = itemView.findViewById(R.id.title);
            Check       = itemView.findViewById(R.id.check);
            Content     = itemView.findViewById(R.id.content);

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

    public List<ModelPickerMaster> getListData() {
        return listFiltered;
    }
}
