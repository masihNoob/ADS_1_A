package com.malang.exo.exotrip.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.malang.exo.exotrip.CustomVolleyRequest;
import com.malang.exo.exotrip.SliderUtils;

import java.util.List;

public class ImageViewAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<SliderUtils> sliderImg;
    private ImageLoader imageLoader;

    //mengambil data dari internet
    // private Integer [] images = {R.drawable.gmb1, R.drawable.gmb2, R.drawable.gmb3};

    public ImageViewAdapter(List<SliderUtils> sliderImg,Context context){
        this.sliderImg = sliderImg;
        this.context = context;
    }

    @Override
    public int getCount() {
        return sliderImg.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image_loader, null);

        SliderUtils sliderUtils = sliderImg.get(position);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageViewer);
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(sliderUtils.getSliderImageUrl(), imageLoader.getImageListener(imageView, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));

        //imageView.setImageResource(images[position]);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
