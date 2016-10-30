package com.fubaisum.imageselector.lib;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.fubaisum.imageselector.lib.model.FolderItem;
import com.fubaisum.imageselector.lib.model.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sum on 8/27/16.
 */
class LoadImageBiz {

    private AppCompatActivity activity;
    private List<FolderItem> folderItemList;
    private OnLoadCompleteListener onLoadCompleteListener;

    private Configuration configuration;

    public LoadImageBiz(AppCompatActivity activity, Configuration configuration) {
        this.activity = activity;
        this.configuration = configuration;
    }

    public List<FolderItem> getFolderItemList() {
        return folderItemList;
    }

    public void loadWithoutPermission() {

        initFolderItemList();

        if (onLoadCompleteListener != null) {
            onLoadCompleteListener.onLoadComplete();
        }
    }

    public void loadWithPermission() {

        initFolderItemList();

        activity.getSupportLoaderManager().initLoader(0, null, mLoaderCallback);
    }


    private void initFolderItemList() {
        folderItemList = new ArrayList<>();
        FolderItem allImagesFolderItem = new FolderItem();
        allImagesFolderItem.name = activity.getString(R.string.is_all_images);
        allImagesFolderItem.path = "/sdcard";
        allImagesFolderItem.imageList = new ArrayList<>(1);
        allImagesFolderItem.isSelected = true;
        folderItemList.add(allImagesFolderItem);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String selection;
            String[] imageTypes;
            if (configuration.isShowGif) {
                selection = IMAGE_PROJECTION[4] + ">0 AND "
                        + IMAGE_PROJECTION[3] + "=? OR "
                        + IMAGE_PROJECTION[3] + "=? OR "
                        + IMAGE_PROJECTION[3] + "=? ";
                imageTypes = new String[]{"image/jpeg", "image/png", "image/gif"};
            } else {
                selection = IMAGE_PROJECTION[4] + ">0 AND "
                        + IMAGE_PROJECTION[3] + "=? OR "
                        + IMAGE_PROJECTION[3] + "=? ";
                imageTypes = new String[]{"image/jpeg", "image/png"};
            }
            String sortOrder = IMAGE_PROJECTION[2] + " DESC";
            return new CursorLoader(activity,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,
                    selection,
                    imageTypes,
                    sortOrder);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            addDataIntoFolderItemList(data);

            if (onLoadCompleteListener != null) {
                onLoadCompleteListener.onLoadComplete();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }

        private void addDataIntoFolderItemList(Cursor data) {
            if (data == null || data.isClosed() || data.getCount() <= 0) {
                return;
            }
            List<ImageItem> fullList = new ArrayList<>(data.getCount());
            while (data.moveToNext()) {
                String imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                if (TextUtils.isEmpty(imagePath) || TextUtils.isEmpty(imageName)) {
                    continue;
                }
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    continue;
                }
                File folderFile = imageFile.getParentFile();
                if (folderFile == null || !folderFile.exists()) {
                    continue;
                }
                String folderPath = folderFile.getAbsolutePath();
                FolderItem folder = getFolderItemByPath(folderPath);
                if (folder == null) {// create a new folder item
                    folder = new FolderItem();
                    folder.name = folderFile.getName();
                    folder.path = folderPath;
                    folder.imageList = new ArrayList<>();
                    // add new folder item into folder item list.
                    folderItemList.add(folder);
                }
                ImageItem newImage = new ImageItem(imagePath);
                folder.imageList.add(newImage);
                fullList.add(newImage);
            }
            data.close();
            // set full list into the first folder item
            folderItemList.get(0).imageList = fullList;
        }

    };

    @Nullable
    private FolderItem getFolderItemByPath(String path) {
        for (FolderItem folderItem : folderItemList) {
            if (TextUtils.equals(folderItem.path, path)) {
                return folderItem;
            }
        }
        return null;
    }

    /**
     *
     */
    interface OnLoadCompleteListener {
        void onLoadComplete();
    }

    public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
        this.onLoadCompleteListener = listener;
    }
}
