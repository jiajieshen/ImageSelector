package com.scausum.imageselector.lib.model;

import java.util.List;

/**
 * The image folder item.
 */
public class FolderItem {

    // The absolute path
    public String path;
    // The folder name
    public String name;
    // The image items under folder
    public List<ImageItem> imageList;
    // The status if folder item is selected
    public boolean isSelected;

    @Override
    public String toString() {
        return "FolderItem{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", imageList=" + imageList +
                ", isSelected=" + isSelected +
                '}';
    }

}
