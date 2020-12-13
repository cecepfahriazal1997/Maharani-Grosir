package com.application.mgoaplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.helper.GeneralHelper;
import com.application.mgoaplication.model.PaymentModel;

import java.util.ArrayList;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
    private Context context;
    private List<PaymentModel> listData = new ArrayList<>();
    private OnClickListener onClickListener;
    private GeneralHelper helper;

    public PaymentAdapter(Context context, List<PaymentModel> listData, GeneralHelper helper,
                          OnClickListener onClickListener) {
        this.context            = context;
        this.listData           = listData;
        this.onClickListener    = onClickListener;
        this.helper             = helper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentModel item = listData.get(position);
        holder.imageView.setImageResource(Integer.parseInt(item.getImage()));
        holder.title.setText(item.getName());
        holder.description.setText(item.getDescription());
        holder.check.setChecked(item.isChecked());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView title, description;
        OnClickListener onClickListener;
        RadioButton check;

        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            imageView               = itemView.findViewById(R.id.icon);
            title                   = itemView.findViewById(R.id.title);
            description             = itemView.findViewById(R.id.description);
            check                   = itemView.findViewById(R.id.check);
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