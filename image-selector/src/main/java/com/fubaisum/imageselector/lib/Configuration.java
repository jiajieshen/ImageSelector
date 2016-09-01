package com.fubaisum.imageselector.lib;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sum on 8/30/16.
 */
public class Configuration implements Parcelable {

    public static final int DEFAULT_MAX_MULTIPLE_CHOICE_COUNT = 9;

    // optional callback mode
    public boolean isActivityCallback;
    public boolean isEventBusCallback;
    // feature
    public boolean isShowGif;
    public boolean isShowCamera;
    public boolean isPreviewEnable;
    public boolean isMultipleChoiceMode;
    // other data
    public int maxSelectableSize;

    public Configuration() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isActivityCallback ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isEventBusCallback ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShowGif ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShowCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPreviewEnable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isMultipleChoiceMode ? (byte) 1 : (byte) 0);
        dest.writeInt(this.maxSelectableSize);
    }

    protected Configuration(Parcel in) {
        this.isActivityCallback = in.readByte() != 0;
        this.isEventBusCallback = in.readByte() != 0;
        this.isShowGif = in.readByte() != 0;
        this.isShowCamera = in.readByte() != 0;
        this.isPreviewEnable = in.readByte() != 0;
        this.isMultipleChoiceMode = in.readByte() != 0;
        this.maxSelectableSize = in.readInt();
    }

    public static final Creator<Configuration> CREATOR = new Creator<Configuration>() {
        @Override
        public Configuration createFromParcel(Parcel source) {
            return new Configuration(source);
        }

        @Override
        public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };
}
