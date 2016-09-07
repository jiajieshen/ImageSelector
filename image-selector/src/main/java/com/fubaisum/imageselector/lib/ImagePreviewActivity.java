package com.fubaisum.imageselector.lib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fubaisum.imageselector.lib.widget.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by sum on 2/6/16.
 */
public class ImagePreviewActivity extends AppCompatActivity {

    private static final String ARG_IMAGE_URL_LIST = "arg_image_url_list";
    private static final String ARG_IMAGE_POSITION = "arg_image_position";

    HackyViewPager viewPager;

    private List<String> imageUrlList;

    public static void launch(Activity activity, ArrayList<String> imageUrlList, int position) {
        if (imageUrlList == null || imageUrlList.size() == 0) {
            return;
        } else if (position < 0 || position >= imageUrlList.size()) {
            return;
        }
        Intent intent = new Intent(activity, ImagePreviewActivity.class);
        intent.putStringArrayListExtra(ARG_IMAGE_URL_LIST, imageUrlList);
        intent.putExtra(ARG_IMAGE_POSITION, position);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.is_activity_image_preview);
        getWindow().setBackgroundDrawable(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

//        Intent intent = getIntent();
//        imageUrlList = intent.getStringArrayListExtra(ARG_IMAGE_URL_LIST);
//        int crrPosition = intent.getIntExtra(ARG_IMAGE_POSITION, 0);
//
//        if (imageUrlList == null || imageUrlList.size() == 0) {
//            throw new IllegalArgumentException();
//        } else if (crrPosition < 0 || crrPosition >= imageUrlList.size()) {
//            throw new IndexOutOfBoundsException();
//        }
//
//        setupViewPager(crrPosition);
    }

    private void setupViewPager(int crrPosition) {
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setCurrentItem(crrPosition);
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageUrlList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setOnViewTapListener(onViewTapListener);
            container.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

            String imageUrl = imageUrlList.get(position);
            Glide.with(ImagePreviewActivity.this)
                    .load(imageUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(photoView);

            return photoView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private PhotoViewAttacher.OnViewTapListener onViewTapListener = new PhotoViewAttacher.OnViewTapListener() {
        @Override
        public void onViewTap(android.view.View view, float x, float y) {
            finish();
        }
    };

}
