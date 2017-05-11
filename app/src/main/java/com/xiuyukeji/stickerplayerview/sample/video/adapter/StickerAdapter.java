package com.xiuyukeji.stickerplayerview.sample.video.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiuyukeji.stickerplayerview.sample.R;
import com.xiuyukeji.stickerplayerview.sample.base.BaseAdapter;
import com.xiuyukeji.stickerplayerview.sample.utils.BitmapUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 贴纸适配器
 *
 * @author Created by jz on 2017/5/11 16:51
 */
public class StickerAdapter extends BaseAdapter<StickerAdapter.ViewHolder, StickerAdapter.StickerBean> {
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class StickerBean {
        private final String path;

        public StickerBean(String path) {
            this.path = path;
        }
    }

    public StickerAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(mInflater.inflate(R.layout.item_video_sticker, parent, false));
        registerClick(holder.itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StickerBean bean = get(position);

        BitmapUtil.bind(mContext, holder.imageView, "file:///android_asset/" + bean.path);

        holder.itemView.setTag(holder);
    }
}
