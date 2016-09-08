package com.fubaisum.imageselector.lib.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CheckResult;

/**
 * Created by sum on 9/7/16.
 */
public class PreviewStateInfo implements Parcelable {

    public int maxSelectableSize;
    public int crrSelectedSize;

    @Override
    public String toString() {
        return "PreviewStateInfo{" +
                "maxSelectableSize=" + maxSelectableSize +
                ", crrSelectedSize=" + crrSelectedSize +
                '}';
    }

    public PreviewStateInfo() {
    }

    @CheckResult
    public boolean isMultipleChoiceMode() {
        if (maxSelectableSize == 0) {
            throw new IllegalStateException();
        }
        return maxSelectableSize > 1;
    }

    @CheckResult
    public boolean isUpToLimit() {
        if (maxSelectableSize == 0) {
            throw new IllegalStateException();
        }
        return crrSelectedSize == maxSelectableSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxSelectableSize);
        dest.writeInt(this.crrSelectedSize);
    }

    protected PreviewStateInfo(Parcel in) {
        this.maxSelectableSize = in.readInt();
        this.crrSelectedSize = in.readInt();
    }

    public static final Creator<PreviewStateInfo> CREATOR = new Creator<PreviewStateInfo>() {
        @Override
        public PreviewStateInfo createFromParcel(Parcel source) {
            return new PreviewStateInfo(source);
        }

        @Override
        public PreviewStateInfo[] newArray(int size) {
            return new PreviewStateInfo[size];
        }
    };
}
