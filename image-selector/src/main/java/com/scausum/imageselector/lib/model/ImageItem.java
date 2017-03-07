package com.scausum.imageselector.lib.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The image item.
 */
public class ImageItem implements Parcelable {

    // The absolute path
    public String path;
    // The status if image item is selected
    public boolean isSelected;

    public ImageItem(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "path='" + path + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected ImageItem(Parcel in) {
        this.path = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ImageItem> CREATOR = new Parcelable.Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
