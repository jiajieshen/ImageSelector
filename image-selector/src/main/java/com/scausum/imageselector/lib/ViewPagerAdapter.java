package com.scausum.imageselector.lib;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.scausum.imageselector.lib.model.ImageItem;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

class ViewPagerAdapter extends PagerAdapter {

    private Activity activity;
    private List<ImageItem> items;
    private PhotoViewAttacher.OnViewTapListener onViewTapListener;

    public ViewPagerAdapter(Activity activity, List<ImageItem> imageItems) {
        this.activity = activity;
        this.items = imageItems;
    }

    public void setItems(List<ImageItem> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageItem imageItem = items.get(position);
        PhotoView photoView = new PhotoView(container.getContext());
        photoView.setOnViewTapListener(onViewTapListener);
        container.addView(photoView,
                ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        Glide.with(activity)
                .load(imageItem.path)
                .asBitmap()
                .fitCenter()
                .into(photoView);
        return photoView;
    }

    public void setOnViewTapListener(PhotoViewAttacher.OnViewTapListener onViewTapListener) {
        this.onViewTapListener = onViewTapListener;
    }
}