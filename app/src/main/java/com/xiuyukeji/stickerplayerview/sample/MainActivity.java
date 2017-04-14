package com.xiuyukeji.stickerplayerview.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xiuyukeji.stickerplayerview.StickerPlayerView;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.intefaces.OnClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDoubleClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnLongClickStickerListener;
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
                mStickerPlayerView.addTextSticker(0, BitmapSource.ASSETS, "test.png",
                        "testText", 0xff000000, StickerUtil.dpToPx(MainActivity.this, 18),
                        false, false, false,
                        176, 80, 74, 97);
            }
        });
        mStickerPlayerView.setOnClickStickerListener(new OnClickStickerListener() {
            @Override
            public void onClick(StickerBean stickerBean) {
                Log.i("Tool", "click");
                Snackbar.make(nFabView, "click", Snackbar.LENGTH_SHORT).show();
            }
        });
        mStickerPlayerView.setOnDoubleClickStickerListener(new OnDoubleClickStickerListener() {
            @Override
            public void onDoubleClick(StickerBean stickerBean) {
                Log.i("Tool", "doubleClick");
                Snackbar.make(nFabView, "doubleClick", Snackbar.LENGTH_SHORT).show();
            }
        });
        mStickerPlayerView.setOnLongClickStickerListener(new OnLongClickStickerListener() {
            @Override
            public void onLongClick(StickerBean stickerBean) {
                Log.i("Tool", "longClick");
                Snackbar.make(nFabView, "longClick", Snackbar.LENGTH_SHORT).show();
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
            case R.id.action_clear:
                mStickerPlayerView.clearAllSticker();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
