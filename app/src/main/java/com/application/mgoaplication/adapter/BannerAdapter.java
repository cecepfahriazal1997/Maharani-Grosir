package com.application.mgoaplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.model.BannerModel;
import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends
        SliderViewAdapter<BannerAdapter.SliderAdapterVH> {

    private Context context;
    private List<BannerModel> mBannerModels = new ArrayList<>();

    public BannerAdapter(Context context, List<BannerModel> list) {
        this.context = context;
        this.mBannerModels = list;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        BannerModel sliderItem = mBannerModels.get(position);
        Glide.with(viewHolder.itemView)
                .load(sliderItem.getImage())
                .fitCenter()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(viewHolder.image);
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getCount() {
        return mBannerModels.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView image;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            image           = itemView.findViewById(R.id.image);
            this.itemView   = itemView;
        }
    }
}