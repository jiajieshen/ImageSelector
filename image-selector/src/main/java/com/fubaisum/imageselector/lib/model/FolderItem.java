package com.fubaisum.imageselector.lib.model;

import java.util.List;

/**
 * The folder item.
 */
public class FolderItem {

    // The absolute path
    public String path;
    // The file name
    public String name;
    // The imageItemList under this folder
    public List<ImageItem> imageItemList;
    // The status if this folder item is selected
    public boolean isSelected;

    @Override
    public String toString() {
        return "FolderItem{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", imageItemList=" + imageItemList +
                ", isSelected=" + isSelected +
                '}';
    }

}
