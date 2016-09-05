package com.fubaisum.imageselector.lib.adapter;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fubaisum.imageselector.lib.R;
import com.fubaisum.imageselector.lib.model.ImageItem;
import com.fubaisum.imageselector.lib.widget.SquareImageView;

import java.util.List;

/**
 * Created by sum on 8/25/16.
 */
public class ImageItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_CAMERA = 0;
    private final int TYPE_IMAGE = 1;

    private Activity activity;
    private List<ImageItem> items;
    private OnItemClickListener onItemClickListener;

    private boolean isGlideSkipMemoryCache;

    private boolean isCanShowCamera;// the camera configuration
    private boolean isMultipleChoiceMode;
    private int maxSelectableSize;
    private int crrSelectedSize;
    private ImageItem lastSelectedItem;// only use in radio mode

    private boolean isShowCamera;// decided by configuration and folder

    public ImageItemAdapter(Activity activity) {
        this.activity = activity;

        isGlideSkipMemoryCache = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public void setMultipleChoiceMode(boolean isMultipleChoice) {
        this.isMultipleChoiceMode = isMultipleChoice;
    }

    public void setMaxSelectableSize(int maxSelectableSize) {
        this.maxSelectableSize = maxSelectableSize;
    }

    public void setCanShowCamera(boolean canShowCamera) {
        isCanShowCamera = canShowCamera;
    }

    public void setItems(List<ImageItem> items, boolean isExpectShowCamera) {
        this.items = items;
        this.isShowCamera = isCanShowCamera && isExpectShowCamera;
    }

    public int getCurrentSelectedSize() {
        return crrSelectedSize;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CAMERA) {
            return newCameraItemViewHolder();
        } else if (viewType == TYPE_IMAGE) {
            return newImageItemViewHolder();
        }
        throw new RuntimeException("No such view type : + " + viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) {
            return position == 0 ? TYPE_CAMERA : TYPE_IMAGE;
        } else {
            return TYPE_IMAGE;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (isShowCamera) {
            if (position == 0) {
                return;
            }
        }
        ImageItemViewHolder imageItemViewHolder = (ImageItemViewHolder) viewHolder;
        ImageItem item = items.get(isShowCamera ? position - 1 : position);
        Glide.with(activity)
                .load(item.path)
                .asBitmap()
                .skipMemoryCache(isGlideSkipMemoryCache)
                .error(R.mipmap.is_thumbnail_default)
                .into(imageItemViewHolder.sivThumbnail);
        imageItemViewHolder.cbState.setChecked(item.isSelected);
        if (item.isSelected) {
            imageItemViewHolder.showMaskView();
        } else {
            imageItemViewHolder.hideMaskView();
        }
    }

    @Override
    public int getItemCount() {
        if (isShowCamera) {
            return items != null ? items.size() + 1 : 1;
        } else {
            return items != null ? items.size() : 0;
        }
    }

    @NonNull
    private RecyclerView.ViewHolder newCameraItemViewHolder() {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View itemView = layoutInflater.inflate(R.layout.is_item_camera, null);
        final CameraItemViewHolder cameraItemViewHolder = new CameraItemViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraItemClick();
            }
        });
        return cameraItemViewHolder;
    }

    private void onCameraItemClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onClickCameraItem();
        }
    }

    @NonNull
    private RecyclerView.ViewHolder newImageItemViewHolder() {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View itemView = layoutInflater.inflate(R.layout.is_item_image, null);
        final ImageItemViewHolder imageItemViewHolder = new ImageItemViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = imageItemViewHolder.getAdapterPosition();
                // TODO: If not previewing the image, invoke the onSelectImageItem() method.
                onSelectImageItem(null, position);
            }
        });
        imageItemViewHolder.cbState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = imageItemViewHolder.getAdapterPosition();
                onSelectImageItem((CheckBox) v, position);
            }
        });
        return imageItemViewHolder;
    }

    private void onClickImageItem(int position) {
        if (position < 0 || position >= getItemCount()) {
            return;
        }
        ImageItem imageItem = items.get(isShowCamera ? position - 1 : position);
        if (onItemClickListener != null) {
            onItemClickListener.onClickImageItem(imageItem);
        }
    }

    private void onSelectImageItem(CheckBox checkBox, int position) {
        if (position < 0 || position >= getItemCount()) {
            return;
        }
        if (isMultipleChoiceMode) {// multiple choice mode
            handleMultipleChoiceEvent(checkBox, position);
        } else {// radio choice mode
            handleRadioChoiceEvent(position);
        }
    }

    private void handleMultipleChoiceEvent(@Nullable CheckBox checkBox, int position) {
        // Get current selected item
        ImageItem crrItem = items.get(isShowCamera ? position - 1 : position);
        // Check the selectable upper limit before add a new choice.
        if (!crrItem.isSelected && crrSelectedSize + 1 > maxSelectableSize) {
            if (checkBox != null) {
                checkBox.setChecked(false);
                Toast.makeText(activity, R.string.is_msg_amount_limit, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        // refresh new selected item view
        crrItem.isSelected = !crrItem.isSelected;
        notifyItemChanged(position);
        // update record
        if (crrItem.isSelected) {
            crrSelectedSize++;
        } else {
            crrSelectedSize--;
        }
        // notify observer
        if (onItemClickListener != null) {
            onItemClickListener.onSelectImageItem(crrSelectedSize);
        }
    }

    private void handleRadioChoiceEvent(int position) {
        // Get current selected item
        ImageItem crrItem = items.get(isShowCamera ? position - 1 : position);
        if (crrItem == lastSelectedItem) {
            // refresh selected item view
            crrItem.isSelected = !crrItem.isSelected;
            notifyItemChanged(position);
            // update record
            crrSelectedSize = 0;
            lastSelectedItem = null;
        } else {
            // refresh last selected item view
            if (lastSelectedItem != null) {
                lastSelectedItem.isSelected = false;
                int lastSelectedPosition = findPositionFromImageList(lastSelectedItem);
                if (lastSelectedPosition != -1) {
                    notifyItemChanged(isShowCamera ? lastSelectedPosition + 1 : lastSelectedPosition);
                }
            }
            // refresh new selected item view
            crrItem.isSelected = true;
            notifyItemChanged(position);
            // update record
            crrSelectedSize = 1;
            lastSelectedItem = crrItem;
        }

        // notify observer
        if (onItemClickListener != null) {
            onItemClickListener.onSelectImageItem(crrSelectedSize);
        }
    }

    /**
     * Find image item in current image list.
     *
     * @return -1 when not found
     */
    private int findPositionFromImageList(ImageItem target) {
        int size = items.size();
        for (int i = 0; i < size; i++) {
            if (items.get(i) == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * ViewHolder for camera item
     */
    static class CameraItemViewHolder extends RecyclerView.ViewHolder {

        public CameraItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * ViewHolder for image item
     */
    static class ImageItemViewHolder extends RecyclerView.ViewHolder {

        private SquareImageView sivThumbnail;
        private CheckBox cbState;
        private View vMask;

        public ImageItemViewHolder(View itemView) {
            super(itemView);

            sivThumbnail = (SquareImageView) itemView.findViewById(R.id.is_siv_image_thumbnail);
            cbState = (CheckBox) itemView.findViewById(R.id.is_cb_image_state);
        }

        private void showMaskView() {
            if (vMask == null) {
                ViewStub viewStub = (ViewStub) itemView.findViewById(R.id.is_stub_image_mask);
                vMask = viewStub.inflate();
            }
            vMask.setVisibility(View.VISIBLE);
        }

        private void hideMaskView() {
            if (vMask != null) {
                vMask.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     *
     */
    public interface OnItemClickListener {

        void onClickCameraItem();

        void onClickImageItem(ImageItem imageItem);

        void onSelectImageItem(int crrSelectedNumber);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

}
