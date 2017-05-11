package com.xiuyukeji.stickerplayerview.sample;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xiuyukeji.stickerplayerview.StickerPlayerView;
import com.xiuyukeji.stickerplayerview.sample.base.BaseActivity;
import com.xiuyukeji.stickerplayerview.sample.video.VideoActivity;
import com.xiuyukeji.stickerplayerview.utils.StickerUtil;

import butterknife.BindView;
import butterknife.OnClick;

import static com.xiuyukeji.stickerplayerview.resource.ResourceFactory.createAssetsResource;

/**
 * 主页面
 *
 * @author Created by jz on 2017/4/11 16:28
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.sticker)
    StickerPlayerView mStickerPlayerView;
    @BindView(R.id.added)
    FloatingActionButton nAddedView;
    @BindView(R.id.copy)
    FloatingActionButton nCopyView;
    @BindView(R.id.replace)
    FloatingActionButton nReplaceView;
    @BindView(R.id.delete)
    FloatingActionButton nDeleteView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setSupportActionBar(mToolbar);
    }

    @Override
    protected void setListener() {
        mStickerPlayerView.setOnClickStickerListener(stickerBean -> Log.i("Tool", "click"));
        mStickerPlayerView.setOnDoubleClickStickerListener(stickerBean -> Log.i("Tool", "doubleClick"));
        mStickerPlayerView.setOnLongClickStickerListener(stickerBean -> Log.i("Tool", "longClick"));
    }

    @OnClick({R.id.added, R.id.copy, R.id.replace, R.id.delete})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.added:
//                mStickerPlayerView.addTextSticker(mStickerPlayerView.getCurrentFrame(), mStickerPlayerView.getCurrentFrame(),
//                        createAssetsResource(MainActivity.this, "text1.png"),
//                        "testText", 0xff000000, StickerUtil.dpToPx(MainActivity.this, 18),
//                        176, 80, 74, 97);
                mStickerPlayerView.addTextSticker(0, 60,
                        createAssetsResource(MainActivity.this, "tuzi.gif"),
                        "testText", 0xff000000, StickerUtil.dpToPx(MainActivity.this, 18),
                        5, 54, 113, 47, 118);
                break;
            case R.id.copy:
                mStickerPlayerView.copySticker();
                break;
            case R.id.replace:
                mStickerPlayerView.replaceTextSticker(
                        createAssetsResource(MainActivity.this, "text2.png"),
                        mStickerPlayerView.getCurrentPosition(), 0,
                        175, 123, 188, 152);
                break;
            case R.id.delete:
                mStickerPlayerView.deleteSticker();
//                if (mStickerPlayerView.getCurrentFrame() >= 60) {
//                    mStickerPlayerView.setCurrentFrame(0);
//                } else {
//                    mStickerPlayerView.setCurrentFrame(mStickerPlayerView.getCurrentFrame() + 1);
//                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_video:
                Intent intent = new Intent(this, VideoActivity.class);
                startActivity(intent);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStickerPlayerView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStickerPlayerView.resume();
    }
}
