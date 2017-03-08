package com.scausum.imageselector.lib;

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

import com.scausum.imageselector.lib.model.ImageItem;
import com.scausum.imageselector.lib.model.PreviewStateInfo;
import com.scausum.imageselector.lib.widget.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by sum on 2/6/16.
 */
public class ImagePreviewActivity extends AppCompatActivity
        implements View.OnClickListener, PhotoViewAttacher.OnViewTapListener {

    public static final String EXTRA_RADIO_SELECTED_PATH = "extra_radio_selected_path";
    public static final String EXTRA_SELECTED_POSITIONS = "extra_selected_positions";

    private static final String ARG_IMAGE_ITEM_LIST = "arg_image_item_list";
    private static final String ARG_IMAGE_ITEM_POSITION = "arg_image_item_position";
    private static final String ARG_PREVIEW_STATE_INFO = "arg_preview_state_info";

    private Toolbar toolbar;
    private Button btnDone;
    private View parentBottom;
    private CheckBox cbSelected;
    private HackyViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<ImageItem> imageList;
    private PreviewStateInfo stateInfo;

    public static void launch(Activity activity, int requestCode,
                              ArrayList<ImageItem> imageList, int position, PreviewStateInfo stateInfo) {
        if (imageList == null || imageList.size() == 0) {
            return;
        } else if (position < 0 || position >= imageList.size()) {
            return;
        }
        Intent intent = new Intent(activity, ImagePreviewActivity.class);
        intent.putParcelableArrayListExtra(ARG_IMAGE_ITEM_LIST, imageList);
        intent.putExtra(ARG_IMAGE_ITEM_POSITION, position);
        intent.putExtra(ARG_PREVIEW_STATE_INFO, stateInfo);
        activity.startActivityForResult(intent, requestCode);
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
        imageList = intent.getParcelableArrayListExtra(ARG_IMAGE_ITEM_LIST);
        final int displayPosition = intent.getIntExtra(ARG_IMAGE_ITEM_POSITION, 0);
        stateInfo = intent.getParcelableExtra(ARG_PREVIEW_STATE_INFO);

        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle(getString(R.string.is_preview_toolbar_title, displayPosition + 1, imageList.size()));
            }
        });
        updateDisplayDoneButton();

        viewPagerAdapter.setItems(imageList);
        viewPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(displayPosition);

        if (stateInfo.isMultipleChoiceMode()) {
            cbSelected.setText(R.string.is_select);
            cbSelected.setChecked(imageList.get(displayPosition).isSelected);
            parentBottom.setVisibility(View.VISIBLE);
        } else {
            btnDone.setEnabled(true);
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
                toolbar.setTitle(getString(R.string.is_preview_toolbar_title, displayPosition + 1, imageList.size()));
                if (stateInfo.isMultipleChoiceMode()) {
                    boolean isSelected = imageList.get(displayPosition).isSelected;
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
        ImageItem imageItem = imageList.get(crrPosition);

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
    public void onBackPressed() {

        int[] selectedPositions = getSelectedPositions();
        Intent intent = new Intent();
        if (selectedPositions != null) {
            intent.putExtra(EXTRA_SELECTED_POSITIONS, selectedPositions);
        }
        setResult(RESULT_CANCELED, intent);

        finish();
    }

    private int[] getSelectedPositions() {
        if (!stateInfo.isMultipleChoiceMode()) {
            return null;
        }
        if (stateInfo.crrSelectedSize == 0) {
            return null;
        }
        int[] selectedPositions = new int[stateInfo.crrSelectedSize];
        int j = 0;
        int imageItemListSize = imageList.size();
        for (int i = 0; i < imageItemListSize; i++) {
            if (imageList.get(i).isSelected) {
                selectedPositions[j] = i;
                j++;
                if (j >= stateInfo.crrSelectedSize) {
                    break;
                }
            }
        }

        return selectedPositions;
    }

    @Override
    public void onClick(View v) {
        if (stateInfo.isMultipleChoiceMode()) {
            int[] selectedPositions = getSelectedPositions();
            Intent intent = new Intent();
            if (selectedPositions != null) {
                intent.putExtra(EXTRA_SELECTED_POSITIONS, selectedPositions);
            }
            setResult(RESULT_OK, intent);
        } else {
            int displayPosition = viewPager.getCurrentItem();
            String path = imageList.get(displayPosition).path;
            Intent intent = new Intent();
            intent.putExtra(EXTRA_RADIO_SELECTED_PATH, path);
            setResult(RESULT_OK, intent);
        }

        finish();
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        toggleDisplayFloatLayout();
    }

    private void toggleDisplayFloatLayout() {
        boolean isVisible = toolbar.getVisibility() == View.VISIBLE;
        isVisible = !isVisible;
        toolbar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        if (stateInfo.isMultipleChoiceMode()) {
            parentBottom.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }
}
