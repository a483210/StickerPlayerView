package com.xiuyukeji.stickerplayerview.sample.video;

import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.ImageUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.xiuyukeji.stickerplayerview.StickerPlayerView;
import com.xiuyukeji.stickerplayerview.sample.R;
import com.xiuyukeji.stickerplayerview.sample.base.BaseActivity;
import com.xiuyukeji.stickerplayerview.sample.base.RxIndex;
import com.xiuyukeji.stickerplayerview.sample.video.adapter.StickerAdapter;
import com.xiuyukeji.stickerplayerview.sample.video.adapter.StickerAdapter.StickerBean;
import com.xiuyukeji.stickerplayerview.sample.video.adapter.ThumbAdapter;
import com.xiuyukeji.stickerplayerview.sample.widget.MyGifImageView;
import com.xiuyukeji.stickerplayerview.sample.widget.recycler.FixedItemLayoutManager;
import com.xiuyukeji.stickerplayerview.sample.widget.recycler.ReboundScrollListener;
import com.xiuyukeji.stickerplayerview.utils.ImageUtil;
import com.xiuyukeji.stickerplayerview.utils.StickerUtil;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.xiuyukeji.stickerplayerview.resource.ResourceFactory.createAssetsResource;

/**
 * 视频
 *
 * @author Created by jz on 2017/5/5 14:57
 */
public class VideoActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.start)
    View mStartView;

    @BindView(R.id.gif)
    MyGifImageView mGifImage;
    @BindView(R.id.sticker)
    StickerPlayerView mStickerView;

    @BindView(R.id.thumbRecycler)
    RecyclerView mThumbRecyclerView;
    @BindView(R.id.stickerRecycler)
    RecyclerView mStickerRecycler;

    private ThumbAdapter mThumbAdapter;
    private StickerAdapter mStickerAdapter;

    private int mThumbPosition;
    private boolean mIsScrollChange = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    protected void findView() {
        mThumbAdapter = new ThumbAdapter(this);
        mStickerAdapter = new StickerAdapter(this);

        super.findView();
    }

    @Override
    protected void initView() {
        setSupportActionBar(mToolbar);

        mThumbRecyclerView.setLayoutManager(new FixedItemLayoutManager());
        mThumbRecyclerView.setAdapter(mThumbAdapter);
        mThumbRecyclerView.addOnScrollListener(new ScrollListener());

        mStickerRecycler.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        mStickerRecycler.setAdapter(mStickerAdapter);

        mGifImage.loadAssetsGif("video.gif");

        mStickerView.setFrameRate((int) (1000f / mGifImage.getDelayTime()));

        loadAllThumbToDir();
        loadSticker();
    }

    @Override
    protected void setListener() {
        mGifImage.setOnProgressListener(position -> {
            if (position > 0) {
                mThumbRecyclerView.smoothScrollToPosition(position);
            }
        });
        mGifImage.setOnStopListener(() -> {
            mThumbRecyclerView.scrollToPosition(0);
            mThumbRecyclerView.setOnTouchListener(null);
        });

        mThumbAdapter.setOnItemClickListener((holder, v, position) -> {
            if (position == mThumbPosition)
                return;
            mThumbRecyclerView.smoothScrollToPosition(position);
        });
        mStickerAdapter.setOnItemClickListener((holder, v, position) -> {
            mStickerView.addTextSticker(0, 60,
                    createAssetsResource(VideoActivity.this, "tuzi.gif"),
                    "testText", 0xff000000, StickerUtil.dpToPx(VideoActivity.this, 18),
                    5, 54, 113, 47, 118);
        });
    }

    @OnClick({R.id.start, R.id.stop})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                mGifImage.start();
                mThumbRecyclerView.scrollToPosition(0);
                mThumbRecyclerView.setOnTouchListener(new NotScroll());
                break;
            case R.id.stop:
                mGifImage.stop();
                break;
        }
    }

    private void loadThumb(int position) {
        if (mThumbPosition == position)
            return;

        mGifImage.seekTo(position);

        mThumbPosition = position;
    }

    private void loadAllThumbToDir() {
        showLoading("加载缩略图中，请稍候！", false);

        Flowable
                .create((FlowableOnSubscribe<RxIndex<Bitmap>>) subscribe -> {
                    int width = mGifImage.getGifWidth();
                    int height = mGifImage.getGifHeight();

                    int thumbWidth = width / 3;
                    int thumbHeight = height / 3;

                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    int count = mGifImage.getGifCount();
                    for (int i = 0; i < count; i++) {
                        int rel = mGifImage.getThumb(bitmap, i);
                        if (rel < 0) {
                            ImageUtil.recycleBitmap(bitmap);
                            subscribe.onError(new RuntimeException("读取Gif缩略图出错！"));
                            break;
                        }

                        subscribe.onNext(new RxIndex<>(i,
                                ImageUtils.compressByScale(bitmap, thumbWidth, thumbHeight)));
                    }
                    ImageUtil.recycleBitmap(bitmap);
                    subscribe.onComplete();
                }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(path -> {
                    mThumbAdapter.add(new ThumbAdapter.ThumbBean(path.value));
                    mThumbAdapter.notifyItemChanged(path.index);
                }, throwable -> {
                    Snackbar.make(mStartView, throwable.getMessage(), Snackbar.LENGTH_LONG).show();
                    throwable.printStackTrace();
                }, () -> hideLoading(1000));
    }

    private void loadSticker() {
        mStickerAdapter.add(new StickerBean("dynamic.gif"));
        mStickerAdapter.add(new StickerBean("text1.png"));
        mStickerAdapter.add(new StickerBean("text2.png"));
        mStickerAdapter.add(new StickerBean("tuzi.gif"));
        mStickerAdapter.notifyDataSetChanged();
    }

    private class ScrollListener extends ReboundScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            mIsScrollChange = false;
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrollSelected(int position) {
            loadThumb(position);
            mIsScrollChange = true;
        }
    }

    public static class NotScroll implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGifImage.stop();
        mStickerView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStickerView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThumbAdapter.recycler();
    }
}