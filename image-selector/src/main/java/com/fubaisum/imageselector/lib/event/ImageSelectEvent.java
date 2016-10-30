package com.fubaisum.imageselector.lib.event;

import java.util.ArrayList;

/**
 * Created by sum on 8/29/16.
 */
public class ImageSelectEvent {

    private ArrayList<String> imagePathList;

    public ImageSelectEvent(ArrayList<String> pathList) {
        this.imagePathList = pathList;
    }

    public ArrayList<String> getImagePathList() {
        return imagePathList;
    }
}
