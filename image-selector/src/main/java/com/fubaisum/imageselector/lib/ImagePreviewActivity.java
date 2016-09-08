package com.fubaisum.imageselector.lib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.fubaisum.imageselector.lib.adapter.ViewPagerAdapter;
import com.fubaisum.imageselector.lib.model.ImageItem;
import com.fubaisum.imageselector.lib.model.PreviewStateInfo;
import com.fubaisum.imageselector.lib.widget.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by sum on 2/6/16.
 */
public class ImagePreviewActivity extends AppCompatActivity
        implements View.OnClickListener, PhotoViewAttacher.OnViewTapListener {

    private static final String ARG_IMAGE_ITEM_LIST = "arg_image_item_list";
    private static final String ARG_IMAGE_ITEM_POSITION = "arg_image_item_position";
    private static final String ARG_PREVIEW_STATE_INFO = "arg_preview_state_info";

    private Toolbar toolbar;
    private Button btnDone;
    private View parentBottom;
    private CheckBox cbSelected;
    private HackyViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<ImageItem> imageItemList;
    private PreviewStateInfo stateInfo;

    public static void launch(Activity activity,
                              ArrayList<ImageItem> imageUrlList,
                              int position,
                              PreviewStateInfo stateInfo) {
        if (imageUrlList == null || imageUrlList.size() == 0) {
            return;
        } else if (position < 0 || position >= imageUrlList.size()) {
            return;
        }
        Intent intent = new Intent(activity, ImagePreviewActivity.class);
        intent.putParcelableArrayListExtra(ARG_IMAGE_ITEM_LIST, imageUrlList);
        intent.putExtra(ARG_IMAGE_ITEM_POSITION, position);
        intent.putExtra(ARG_PREVIEW_STATE_INFO, stateInfo);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.is_activity_image_preview);
        getWindow().setBackgroundDrawable(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        setupToolbar();
        setupViewPager();
        setupBottomFloatLayout();

        initViewInfo();
    }

    private void initViewInfo() {
        Intent intent = getIntent();
        imageItemList = intent.getParcelableArrayListExtra(ARG_IMAGE_ITEM_LIST);
        final int displayPosition = intent.getIntExtra(ARG_IMAGE_ITEM_POSITION, 0);
        stateInfo = intent.getParcelableExtra(ARG_PREVIEW_STATE_INFO);

        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle(getString(R.string.is_preview_toolbar_title, displayPosition + 1, imageItemList.size()));
            }
        });
        updateDisplayDoneButton();

        viewPagerAdapter.setItems(imageItemList);
        viewPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(displayPosition);

        if (stateInfo.isMultipleChoiceMode()) {
            cbSelected.setText(R.string.is_select);
            cbSelected.setChecked(imageItemList.get(displayPosition).isSelected);
            parentBottom.setVisibility(View.VISIBLE);
        } else {
            parentBottom.setVisibility(View.GONE);
        }
    }

    private void updateDisplayDoneButton() {
        if (stateInfo.crrSelectedSize == 0) {
            btnDone.setText(R.string.is_action_done);
            btnDone.setEnabled(false);
            return;
        }

        if (stateInfo.isMultipleChoiceMode()) {
            btnDone.setText(getString(R.string.is_action_button_string, stateInfo.crrSelectedSize, stateInfo.maxSelectableSize));
        } else {
            btnDone.setText(R.string.is_action_done);
        }
        btnDone.setEnabled(true);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.is_preview_toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        btnDone = (Button) findViewById(R.id.is_btn_preview_toolbar_done);
        assert btnDone != null;
        btnDone.setEnabled(false);
        btnDone.setOnClickListener(this);
    }

    private void setupViewPager() {
        viewPager = (HackyViewPager) findViewById(R.id.is_view_pager);
        assert viewPager != null;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int displayPosition) {
                toolbar.setTitle(getString(R.string.is_preview_toolbar_title, displayPosition + 1, imageItemList.size()));
                if (stateInfo.isMultipleChoiceMode()) {
                    boolean isSelected = imageItemList.get(displayPosition).isSelected;
                    cbSelected.setChecked(isSelected);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPagerAdapter = new ViewPagerAdapter(this, null);
        viewPagerAdapter.setOnViewTapListener(this);
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupBottomFloatLayout() {
        parentBottom = findViewById(R.id.is_parent_preview_bottom);

        cbSelected = (CheckBox) findViewById(R.id.is_cb_preview_image_state);
        cbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageItem();
            }
        });
    }

    private void onSelectImageItem() {
        int crrPosition = viewPager.getCurrentItem();
        ImageItem imageItem = imageItemList.get(crrPosition);

        if (stateInfo.isMultipleChoiceMode()) {
            // Check border
            if (!imageItem.isSelected && stateInfo.isUpToLimit()) {
                cbSelected.setChecked(false);
                Toast.makeText(this, R.string.is_msg_amount_limit, Toast.LENGTH_SHORT).show();
                return;
            }

            imageItem.isSelected = !imageItem.isSelected;
            if (imageItem.isSelected) {
                stateInfo.crrSelectedSize++;
            } else {
                stateInfo.crrSelectedSize--;
            }
        }

        updateDisplayDoneButton();
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onViewTap(View view, float x, float y) {
        boolean isVisible = toolbar.getVisibility() == View.VISIBLE;
        isVisible = !isVisible;
        toolbar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        if (stateInfo.isMultipleChoiceMode()) {
            parentBottom.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }
}