package com.xiuyukeji.stickerplayerview.sample.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.xiuyukeji.stickerplayerview.sample.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 视频
 *
 * @author Created by jz on 2017/5/5 14:57
 */
public class VideoActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.gif)
    MyGifImageView mGifImage;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        findView();
        initView();
        setListener();
    }

    private void findView() {
        ButterKnife.bind(this);
    }

    private void initView() {
        setSupportActionBar(mToolbar);

        mGifImage.loadAssetsGif("video.gif");
    }

    private void setListener() {
        mGifImage.setOnProgressListener(position -> Log.i("Tool", "position = " + position));
        mGifImage.setOnStopListener(() -> Log.i("Tool", "stop"));
    }

    @OnClick({R.id.start, R.id.stop})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                mGifImage.start();
                break;
            case R.id.stop:
                mGifImage.stop();
                break;
        }
    }

}