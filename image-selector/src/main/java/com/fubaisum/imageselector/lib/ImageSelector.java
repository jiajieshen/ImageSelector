package com.fubaisum.imageselector.lib;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

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

    // feature
    boolean isCameraEnable = true;
    boolean isPreviewEnable = true;
    boolean isMultipleChoice = true;
    boolean isShowGif = false;
    boolean isOnlyShowGif = false;
    // other data
    int maxSelectedSize = DEFAULT_MAX_MULTIPLE_CHOICE_SIZE;
    // hook
    ImageSelectorHook hook;

    private ImageSelector() {
        instance = this;
    }

    static void clearInstance() {
        instance.hook = null;
        instance = null;
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

    @NonNull
    static ImageSelectorHook getHook() {
        if (instance.hook == null) {
            return ImageSelectorHook.EMPTY_HOOK;
        } else {
            return instance.hook;
        }
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
