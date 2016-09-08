package com.fubaisum.imageselector.lib;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by sum on 8/26/16.
 */
public class ImageSelector {

    public static final String EXTRA_RESULT_LIST = "EXTRA_RESULT_LIST";
    public static final String EXTRA_CONFIGURATION = "EXTRA_CONFIGURATION";

    private Configuration configuration;

    private ImageSelector(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * ImageSelector Builder
     */
    public static class Builder {

        private Configuration config;

        public Builder() {
            config = new Configuration();
            config.isShowGif = false;
            config.isShowCamera = true;
            config.isPreviewEnable = true;
            config.isMultipleChoiceMode = true;
            config.maxSelectableSize = Configuration.DEFAULT_MAX_MULTIPLE_CHOICE_COUNT;
        }

        public Builder setShowGif(boolean isShowGif) {
            config.isShowGif = isShowGif;
            return this;
        }

        public Builder setShowCamera(boolean isShowCamera) {
            config.isShowCamera = isShowCamera;
            return this;
        }

        public Builder setPreviewEnable(boolean isEnable) {
            config.isPreviewEnable = isEnable;
            return this;
        }

        public Builder setRadioChoiceMode(boolean isRadio) {
            config.isMultipleChoiceMode = !isRadio;
            return this;
        }

        public Builder setMultipleChoiceMode(boolean isMultipleChoice) {
            config.isMultipleChoiceMode = isMultipleChoice;
            return this;
        }

        public Builder setMaxSelectableSize(int maxSelectableSize) {
            config.maxSelectableSize = maxSelectableSize;
            return this;
        }

        public ImageSelector build() {
            if (!config.isMultipleChoiceMode || config.maxSelectableSize == 1) {
                config.maxSelectableSize = 1;
            } else {
                if (config.maxSelectableSize <= 0) {
                    throw new IllegalStateException("The multiple choice count shouldn't be less than 0.");
                }
            }
            return new ImageSelector(config);
        }
    }

    public void launchForActivityCallback(Activity activity, int requestCode) {
        configuration.isActivityCallback = true;
        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        intent.putExtra(EXTRA_CONFIGURATION, configuration);
        activity.startActivityForResult(intent, requestCode);
    }

    public void launchForEventBusCallback(Activity activity) {
        configuration.isEventBusCallback = true;
        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        intent.putExtra(EXTRA_CONFIGURATION, configuration);
        activity.startActivity(intent);
    }
}
