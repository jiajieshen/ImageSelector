package com.scausum.imageselector.lib;

/**
 * Created by sum on 10/30/16.
 */

import android.app.Activity;

public interface ImageSelectorHook {

    void onImageThumbnailClick(Activity activity, String imagePath);

}
