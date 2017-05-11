package com.xiuyukeji.stickerplayerview.sample.video.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.xiuyukeji.stickerplayerview.sample.R;
import com.xiuyukeji.stickerplayerview.sample.base.BaseAdapter;
import com.xiuyukeji.stickerplayerview.sample.widget.ColorMaskView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 颜色适配器
 *
 * @author Created by jz on 2017/5/11 17:58
 */
public class ColorAdapter extends BaseAdapter<ColorAdapter.ViewHolder, Integer> {
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.color)
        ColorMaskView colorView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public ColorAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(mInflater.inflate(R.layout.item_video_color, parent, false));
        registerClick(holder.itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.colorView.setColor(get(position));

        holder.itemView.setTag(holder);
    }
}
