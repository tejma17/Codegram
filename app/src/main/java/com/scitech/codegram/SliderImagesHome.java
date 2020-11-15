package com.scitech.codegram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderImagesHome extends SliderViewAdapter<SliderImagesHome.SliderAdapterVH> {

    final Context context;
    LayoutInflater inflater;
    private List<Integer> mSliderItems;

    public SliderImagesHome(Context context, List<Integer> mSliderItems) {
        this.context = context;
        this.mSliderItems = mSliderItems;
    }

    public void renewItems(List<Integer> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Integer integer) {
        this.mSliderItems.add(integer);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }


    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_image_home, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        Integer sliderItem = mSliderItems.get(position);

        viewHolder.imageView.setImageResource(sliderItem);

    }


    public static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        final View itemView;
        final ImageView imageView;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.quote);
            this.itemView = itemView;
        }
    }
}
