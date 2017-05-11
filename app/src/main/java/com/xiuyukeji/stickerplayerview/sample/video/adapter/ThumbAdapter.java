package com.xiuyukeji.stickerplayerview.sample.video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiuyukeji.stickerplayerview.sample.R;
import com.xiuyukeji.stickerplayerview.sample.base.BaseAdapter;
import com.xiuyukeji.stickerplayerview.utils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 缩略图适配器
 *
 * @author Created by jz on 2017/5/11 11:20
 */
public class ThumbAdapter extends BaseAdapter<ThumbAdapter.ViewHolder, ThumbAdapter.ThumbBean> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.number)
        TextView numberView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ThumbBean {
        private final Bitmap bitmap;

        public ThumbBean(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

    public ThumbAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(mInflater.inflate(R.layout.item_video_thumb, parent, false));
        registerClick(holder.itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ThumbBean bean = get(position);

        holder.numberView.setText(String.valueOf(position + 1));
        holder.imageView.setImageBitmap(bean.bitmap);

        holder.itemView.setTag(holder);
    }

    public void recycler() {
        for (ThumbBean bean : mList) {
            ImageUtil.recycleBitmap(bean.bitmap);
        }
        mList.clear();
    }
}
