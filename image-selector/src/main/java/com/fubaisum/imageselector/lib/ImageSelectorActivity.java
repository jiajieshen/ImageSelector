package com.fubaisum.imageselector.lib;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.fubaisum.imageselector.lib.adapter.FolderItemAdapter;
import com.fubaisum.imageselector.lib.adapter.ImageItemAdapter;
import com.fubaisum.imageselector.lib.event.SelectCompleteEvent;
import com.fubaisum.imageselector.lib.model.FolderItem;
import com.fubaisum.imageselector.lib.model.ImageItem;
import com.fubaisum.imageselector.lib.util.FileUtils;
import com.fubaisum.imageselector.lib.widget.ItemOffsetDecoration;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sum on 8/25/16.
 */
public class ImageSelectorActivity extends AppCompatActivity
        implements View.OnClickListener,
        FolderItemAdapter.OnItemClickListener,
        ImageItemAdapter.OnItemClickListener, PermissionHelper.OnPermissionListener {

    private static final int REQUEST_CAMERA = 0x1001;

    private Button btnDone;
    private ImageItemAdapter imageItemAdapter;
    private Button btnFolderCategory;

    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior bottomSheetBehavior;
    private FolderItemAdapter folderItemAdapter;

    private Configuration configuration;

    private PermissionHelper permissionHelper;
    private boolean isLoadingImageWithCheck;
    private boolean isLoadImageComplete;
    private boolean isShowingCameraWithCheck;
    private File mTmpFile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.is_activity_image_selector);
        getWindow().setBackgroundDrawable(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        Intent intent = getIntent();
        configuration = intent.getParcelableExtra(ImageSelector.EXTRA_CONFIGURATION);

        setupToolbar();
        setupImageRecyclerView();
        setupFolderCategoryLayout();
        setupFolderItemAdapter();

        permissionHelper = new PermissionHelper();
        permissionHelper.setOnPermissionListener(this);
        // load image from system database
        loadImageWithCheck();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.is_toolbar);
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

        btnDone = (Button) findViewById(R.id.is_btn_toolbar_done);
        assert btnDone != null;
        btnDone.setEnabled(false);
        btnDone.setOnClickListener(this);
    }

    private void setupImageRecyclerView() {
        RecyclerView imageRecyclerView = (RecyclerView) findViewById(R.id.is_recycler_view_image);
        assert imageRecyclerView != null;

        imageRecyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        imageRecyclerView.setLayoutManager(gridLayoutManager);

        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(this, R.dimen.is_image_recycler_view_item_offset);
        imageRecyclerView.addItemDecoration(itemOffsetDecoration);

        imageItemAdapter = new ImageItemAdapter(this);
        imageItemAdapter.setMultipleChoiceMode(configuration.isMultipleChoiceMode);
        imageItemAdapter.setMaxSelectableSize(configuration.maxSelectableSize);
        imageItemAdapter.setCanShowCamera(configuration.isShowCamera);
        imageItemAdapter.setCanPreview(configuration.isPreviewEnable);
        imageItemAdapter.setOnItemClickListener(this);

        imageRecyclerView.setAdapter(imageItemAdapter);
    }

    private void setupFolderCategoryLayout() {
        btnFolderCategory = (Button) findViewById(R.id.is_btn_folder_category);
        assert btnFolderCategory != null;
        btnFolderCategory.setOnClickListener(this);
    }

    private void setupFolderItemAdapter() {
        folderItemAdapter = new FolderItemAdapter(this);
        folderItemAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.is_btn_folder_category) {
            showFolderBottomSheetDialog();
        } else if (viewId == R.id.is_btn_toolbar_done) {
            executeCallback();
        }
    }

    private void showFolderBottomSheetDialog() {
        if (bottomSheetDialog == null) {
            View vgBottomSheet = getLayoutInflater().inflate(R.layout.is_bottom_sheet_folder, null);
            assert vgBottomSheet != null;
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(vgBottomSheet);
            bottomSheetBehavior = BottomSheetBehavior.from((View) vgBottomSheet.getParent());

            RecyclerView folderRecyclerView = (RecyclerView) vgBottomSheet.findViewById(R.id.is_recycler_view_folder);
            assert folderRecyclerView != null;
            folderRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            folderRecyclerView.setLayoutManager(layoutManager);
            folderRecyclerView.setAdapter(folderItemAdapter);
        }

        bottomSheetDialog.show();
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog = null;
            }
        });
    }

    private void executeCallback() {
        ArrayList<String> pathList = getSelectedImagePathList();
        if (configuration.isActivityCallback) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra(ImageSelector.EXTRA_RESULT_LIST, pathList);
            setResult(RESULT_OK, intent);
        } else if (configuration.isEventBusCallback) {
            try {
                Class.forName("org.greenrobot.eventbus.EventBus");
                SelectCompleteEvent event = new SelectCompleteEvent(pathList);
                EventBus.getDefault().post(event);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        finish();
    }

    @NonNull
    private ArrayList<String> getSelectedImagePathList() {
        int crrSelectedSize = imageItemAdapter.getCurrentSelectedSize();
        ArrayList<String> pathList = new ArrayList<>(crrSelectedSize);

        FolderItem firstFolderItem = folderItemAdapter.getItems().get(0);
        List<ImageItem> fullList = firstFolderItem.imageItemList;
        for (ImageItem image : fullList) {
            if (image.isSelected) {
                pathList.add(image.path);
                if (pathList.size() >= crrSelectedSize) {
                    break;
                }
            }
        }
        return pathList;
    }

    private void loadImageWithCheck() {
        isLoadingImageWithCheck = true;
        permissionHelper.accessExternalStorageWithCheck(this);
    }

    @Override
    public void accessExternalStorage() {
        if (isLoadingImageWithCheck && !isLoadImageComplete) {
            isLoadingImageWithCheck = false;
            loadImage(true);
        } else if (isShowingCameraWithCheck) {
            isShowingCameraWithCheck = false;
            permissionHelper.showCameraWithCheck(this);
        }
    }

    @Override
    public void onAccessExternalStorageDenied() {
        if (isLoadingImageWithCheck && !isLoadImageComplete) {
            loadImage(false);
            showDeniedDialog(R.string.is_permission_rationale_read_storage);
        } else if (isShowingCameraWithCheck) {
            showDeniedDialog(R.string.is_permission_rationale_write_storage);
        }
    }

    private void loadImage(boolean isHasPermission) {
        final LoadImageBiz loadImageBiz = new LoadImageBiz(this, configuration);
        loadImageBiz.setOnLoadCompleteListener(new LoadImageBiz.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete() {
                List<FolderItem> items = loadImageBiz.getFolderItemList();
                onLoadImageSuccess(items);
            }
        });
        if (isHasPermission) {
            loadImageBiz.loadWithPermission();
        } else {
            loadImageBiz.loadWithoutPermission();
        }
    }

    private void onLoadImageSuccess(List<FolderItem> items) {

        isLoadImageComplete = true;

        folderItemAdapter.setItems(items);
        folderItemAdapter.notifyDataSetChanged();

        FolderItem displayItem = items.get(0);
        imageItemAdapter.setItems(displayItem.imageItemList, true);
        imageItemAdapter.notifyDataSetChanged();

        btnFolderCategory.setText(displayItem.name);
    }

    @Override
    public void showCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                mTmpFile = FileUtils.createTmpFile(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mTmpFile != null && mTmpFile.exists()) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                startActivityForResult(intent, REQUEST_CAMERA);
            } else {
                Toast.makeText(this, R.string.is_error_image_not_exist, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.is_msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCameraDenied() {
        showDeniedDialog(R.string.is_permission_rationale_camera);
    }

    private void showDeniedDialog(@StringRes int mesId) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.is_permission_dialog_title)
                .setMessage(mesId)
                .setPositiveButton(R.string.is_permission_dialog_ok, null)
                .create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    // notify system the image has change
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTmpFile)));
                }
            } else {
                // delete tmp file
                while (mTmpFile != null && mTmpFile.exists()) {
                    boolean success = mTmpFile.delete();
                    if (success) {
                        mTmpFile = null;
                    }
                }
            }
        }
    }

    @Override
    public void onClickCameraItem() {
        isShowingCameraWithCheck = true;
        permissionHelper.accessExternalStorageWithCheck(this);
    }

    @Override
    public void onClickImageItem(ImageItem imageItem) {
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSelectImageItem(int crrSelectedSize) {
        if (crrSelectedSize == 0) {
            btnDone.setText(R.string.is_action_done);
            btnDone.setEnabled(false);
            return;
        }

        int maxSize = configuration.maxSelectableSize;
        if (configuration.isMultipleChoiceMode) {
            btnDone.setText(getString(R.string.is_action_button_string, getString(R.string.is_action_done), crrSelectedSize, maxSize));
        }
        btnDone.setEnabled(true);
    }

    @Override
    public void onClickFolderItem(FolderItem folderItem) {
        boolean isExpectCamera = folderItemAdapter.isFullImageListFolderItem(folderItem);
        // refresh image list
        imageItemAdapter.setItems(folderItem.imageItemList, isExpectCamera);
        imageItemAdapter.notifyDataSetChanged();
        // collapsed the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        // refresh category tips
        btnFolderCategory.setText(folderItem.name);
    }

}
