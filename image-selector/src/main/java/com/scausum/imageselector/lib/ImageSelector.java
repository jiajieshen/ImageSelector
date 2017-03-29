package com.scausum.imageselector.lib;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by sum on 8/26/16.
 */
public class ImageSelector {

    /**
     * Extra for ImageSelectorActivity result.
     */
    public static final String EXTRA_IMAGE_PATH_LIST = "EXTRA_IMAGE_PATH_LIST";

    private static final int DEFAULT_MAX_MULTIPLE_CHOICE_SIZE = 9;

    private static ImageSelector instance;

    String galleryTitle = ImageSelector.class.getSimpleName();
    boolean isCameraEnable = true;
    boolean isPreviewEnable = true;
    boolean isMultipleChoice = true;
    boolean isShowGif = false;
    boolean isOnlyShowGif = false;
    int maxSelectedSize = DEFAULT_MAX_MULTIPLE_CHOICE_SIZE;
    ImageSelectorHook hook;

    private ImageSelector() {
        instance = this;
    }

    /**
     * Clear the ImageSelector static instance to avoid memory leak.
     */
    static void clearInstance() {
        instance = null;
    }

    static String getGalleryTitle() {
        return instance.galleryTitle;
    }

    static boolean isCameraEnable() {
        return instance.isCameraEnable;
    }

    static boolean isPreviewEnable() {
        return instance.isPreviewEnable;
    }

    static boolean isMultipleChoice() {
        return instance.isMultipleChoice;
    }

    static boolean isShowGif() {
        return instance.isShowGif;
    }

    static boolean isOnlyShowGif() {
        return instance.isOnlyShowGif;
    }

    static int getMaxSelectedSize() {
        return instance.maxSelectedSize;
    }

    @Nullable
    static ImageSelectorHook getHook() {
        return instance.hook;
    }

    /**
     * @param activity
     * @param requestCode
     */
    public void launch(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * ImageSelector Builder
     */
    public static class Builder {

        private ImageSelector imageSelector;

        public Builder() {
            imageSelector = new ImageSelector();
        }

        public Builder setCameraEnable(boolean enable) {
            imageSelector.isCameraEnable = enable;
            return this;
        }

        public Builder setPreviewEnable(boolean enable) {
            imageSelector.isPreviewEnable = enable;
            return this;
        }

        public Builder setMultipleChoice(boolean isMultipleChoice) {
            imageSelector.isMultipleChoice = isMultipleChoice;
            return this;
        }

        public Builder setMaxSelectedSize(int maxSelectedSize) {
            imageSelector.maxSelectedSize = maxSelectedSize;
            return this;
        }

        public Builder setShowGif(boolean isShowGif) {
            imageSelector.isShowGif = isShowGif;
            return this;
        }

        public Builder setOnlyShowGif(boolean isOnlyShowGif) {
            imageSelector.isOnlyShowGif = isOnlyShowGif;
            return this;
        }

        public Builder setHook(ImageSelectorHook hook) {
            imageSelector.hook = hook;
            return this;
        }

        public Builder setGalleryTitle(String title) {
            imageSelector.galleryTitle = title;
            return this;
        }

        public ImageSelector build() {
            if (imageSelector.isMultipleChoice) {
                if (imageSelector.maxSelectedSize <= 1) {
                    throw new IllegalStateException("The value of maxSelectedSize must be bigger than 1.");
                }
            } else {
                imageSelector.maxSelectedSize = 1;
            }
            return imageSelector;
        }
    }
}
