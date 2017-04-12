package com.xiuyukeji.stickerplayerview.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xiuyukeji.stickerplayerview.StickerPlayerView;
import com.xiuyukeji.stickerplayerview.utils.BitmapSource;
import com.xiuyukeji.stickerplayerview.utils.StickerUtil;

/**
 * 主页面
 *
 * @author Created by jz on 2017/4/11 16:28
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FloatingActionButton nFabView;
    private StickerPlayerView mStickerPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        initView();
        setListener();
    }

    private void findView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        nFabView = (FloatingActionButton) findViewById(R.id.fab);
        mStickerPlayerView = (StickerPlayerView) findViewById(R.id.sticker);
    }

    private void initView() {
        setSupportActionBar(mToolbar);
    }

    private void setListener() {
        nFabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStickerPlayerView.invalidate();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                mStickerPlayerView.addTextSticker(0, BitmapSource.ASSETS, "test.png",
                        "testText", 0xff000000, StickerUtil.dpToPx(this, 24),
                        false, false, false,
                        176, 80, 74, 97);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
