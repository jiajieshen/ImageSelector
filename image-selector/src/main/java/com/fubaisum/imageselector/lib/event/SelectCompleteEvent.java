package com.fubaisum.imageselector.lib.event;

import java.util.ArrayList;

/**
 * Created by sum on 8/29/16.
 */
public class SelectCompleteEvent {

    private ArrayList<String> selectedPathList;

    public SelectCompleteEvent(ArrayList<String> pathList) {
        this.selectedPathList = pathList;
    }

    public ArrayList<String> getSelectedPathList() {
        return selectedPathList;
    }
}
