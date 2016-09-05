package com.fubaisum.imageselector.lib.adapter;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fubaisum.imageselector.lib.R;
import com.fubaisum.imageselector.lib.model.FolderItem;

import java.util.List;

/**
 * Created by sum on 8/27/16.
 */
public class FolderItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<FolderItem> items;
    private OnItemClickListener onItemClickListener;

    private boolean isGlideSkipMemoryCache;
    private int crrSelectedPosition = 0;

    public FolderItemAdapter(Activity activity) {
        this.activity = activity;

        isGlideSkipMemoryCache = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public List<FolderItem> getItems() {
        return items;
    }

    public void setItems(@NonNull List<FolderItem> items) {
        this.items = items;
    }

    public int getCurrentSelectedPosition() {
        return crrSelectedPosition;
    }

    public boolean isFullImageListFolderItem(FolderItem folderItem) {
        return folderItem == items.get(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View itemView = layoutInflater.inflate(R.layout.is_item_folder, parent, false);
        final FolderItemViewHolder viewHolder = new FolderItemViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                onFolderItemClick(position);
            }
        });
        return viewHolder;
    }

    private void onFolderItemClick(int position) {
        if (position < 0 || position >= getItemCount()) {
            return;
        }
        if (crrSelectedPosition != position) {
            FolderItem oldSelectedItem = items.get(crrSelectedPosition);
            oldSelectedItem.isSelected = false;
            notifyItemChanged(crrSelectedPosition);
        }
        FolderItem newSelectedItem = items.get(position);
        newSelectedItem.isSelected = true;
        notifyItemChanged(position);

        // change the record
        crrSelectedPosition = position;

        if (onItemClickListener != null) {
            onItemClickListener.onClickFolderItem(newSelectedItem);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FolderItemViewHolder viewHolder = (FolderItemViewHolder) holder;
        FolderItem item = items.get(position);

        viewHolder.tvName.setText(item.name);
        viewHolder.tvPath.setText(item.path);
        viewHolder.ivState.setVisibility(item.isSelected ? View.VISIBLE : View.INVISIBLE);
        viewHolder.tvSize.setText(activity.getString(R.string.is_photo_size, item.imageItemList.size()));
        Glide.with(activity)
                .load(position == 0 ? R.mipmap.is_thumbnail_default : item.imageItemList.get(0).path)
                .asBitmap()
                .error(R.mipmap.is_thumbnail_default)
                .skipMemoryCache(isGlideSkipMemoryCache)
                .centerCrop()
                .dontAnimate()
                .into(viewHolder.ivCover);

    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }


    /**
     * ViewHolder for folder item
     */
    private static class FolderItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivCover;
        private TextView tvName;
        private TextView tvPath;
        private TextView tvSize;
        private ImageView ivState;

        public FolderItemViewHolder(View itemView) {
            super(itemView);

            ivCover = (ImageView) itemView.findViewById(R.id.is_iv_folder_cover);
            tvName = (TextView) itemView.findViewById(R.id.is_tv_folder_name);
            tvPath = (TextView) itemView.findViewById(R.id.is_tv_folder_path);
            tvSize = (TextView) itemView.findViewById(R.id.is_tv_folder_size);
            ivState = (ImageView) itemView.findViewById(R.id.is_iv_folder_state);
        }

    }

    /**
     *
     */
    public interface OnItemClickListener {
        void onClickFolderItem(FolderItem folderItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
