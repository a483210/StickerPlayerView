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
    private StickerPlayerView mStickerPlayerView;
    private FloatingActionButton nAddedView;
    private FloatingActionButton nCopyView;
    private FloatingActionButton nReplaceView;
    private FloatingActionButton nDeleteView;

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
        mStickerPlayerView = (StickerPlayerView) findViewById(R.id.sticker);
        nAddedView = (FloatingActionButton) findViewById(R.id.added);
        nCopyView = (FloatingActionButton) findViewById(R.id.copy);
        nReplaceView = (FloatingActionButton) findViewById(R.id.replace);
        nDeleteView = (FloatingActionButton) findViewById(R.id.delete);
    }

    private void initView() {
        setSupportActionBar(mToolbar);
    }

    private void setListener() {
        nAddedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStickerPlayerView.addTextSticker(mStickerPlayerView.getCurrentFrame(),
                        BitmapSource.ASSETS, "text1.png",
                        "testText", 0xff000000, StickerUtil.dpToPx(MainActivity.this, 18),
                        176, 80, 74, 97);
            }
        });
        nCopyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStickerPlayerView.copySticker();
            }
        });
        nReplaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStickerPlayerView.replaceTextSticker(
                        BitmapSource.ASSETS, "text2.png",
                        175, 123, 188, 152);
            }
        });
        nDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStickerPlayerView.deleteSticker();
            }
        });
        mStickerPlayerView.setOnClickStickerListener(new OnClickStickerListener() {
            @Override
            public void onClick(StickerBean stickerBean) {
                Log.i("Tool", "click");
                Snackbar.make(nAddedView, "click", Snackbar.LENGTH_SHORT).show();
            }
        });
        mStickerPlayerView.setOnDoubleClickStickerListener(new OnDoubleClickStickerListener() {
            @Override
            public void onDoubleClick(StickerBean stickerBean) {
                Log.i("Tool", "doubleClick");
                Snackbar.make(nAddedView, "doubleClick", Snackbar.LENGTH_SHORT).show();
            }
        });
        mStickerPlayerView.setOnLongClickStickerListener(new OnLongClickStickerListener() {
            @Override
            public void onLongClick(StickerBean stickerBean) {
                Log.i("Tool", "longClick");
                Snackbar.make(nAddedView, "longClick", Snackbar.LENGTH_SHORT).show();
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
