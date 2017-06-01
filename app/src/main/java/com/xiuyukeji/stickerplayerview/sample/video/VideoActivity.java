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
import com.xiuyukeji.stickerplayerview.StickerFramePlayerView;
import com.xiuyukeji.stickerplayerview.annotations.PlayerSource;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.bean.TextStickerBean;
import com.xiuyukeji.stickerplayerview.event.EventHandle;
import com.xiuyukeji.stickerplayerview.sample.R;
import com.xiuyukeji.stickerplayerview.sample.base.BaseActivity;
import com.xiuyukeji.stickerplayerview.sample.base.RxIndex;
import com.xiuyukeji.stickerplayerview.sample.video.adapter.ColorAdapter;
import com.xiuyukeji.stickerplayerview.sample.video.adapter.StickerAdapter;
import com.xiuyukeji.stickerplayerview.sample.video.adapter.ThumbAdapter;
import com.xiuyukeji.stickerplayerview.sample.video.adapter.ThumbAdapter.ViewHolder;
import com.xiuyukeji.stickerplayerview.sample.video.bean.StickerItem;
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
import rebus.bottomdialog.BottomDialog;

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
    StickerFramePlayerView mStickerView;

    @BindView(R.id.thumbRecycler)
    RecyclerView mThumbRecyclerView;
    @BindView(R.id.stickerRecycler)
    RecyclerView mStickerRecycler;
    @BindView(R.id.colorRecycler)
    RecyclerView mColorRecycler;

    @BindView(R.id.bold)
    View mBoldView;
    @BindView(R.id.italic)
    View mItalicView;
    @BindView(R.id.underline)
    View mUnderlineView;

    private ThumbAdapter mThumbAdapter;
    private StickerAdapter mStickerAdapter;
    private ColorAdapter mColorAdapter;

    private int mThumbPosition;
    private boolean mIsScrollChange = true;

    private ViewHolder mCurrentHolder;
    private int mLastDrawPosition;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    protected void findView() {
        mThumbAdapter = new ThumbAdapter(this);
        mStickerAdapter = new StickerAdapter(this);
        mColorAdapter = new ColorAdapter(this);

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


        initColor();
        mColorRecycler.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        mColorRecycler.setAdapter(mColorAdapter);

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
            mStickerView.setCurrentFrame(position);
        });
        mGifImage.setOnStopListener(() -> {
            mThumbRecyclerView.scrollToPosition(0);
            mThumbRecyclerView.setOnTouchListener(null);
            mThumbPosition = 0;

            mStickerView.setPlayerState(PlayerSource.EDIT);
            mStickerView.setCurrentFrame(0);
        });

        mThumbAdapter.setOnItemClickListener((holder, v, position) -> {
            if (position == mThumbPosition)
                return;
            mThumbRecyclerView.smoothScrollToPosition(position);
        });
        mThumbAdapter.setOnDrawThumbListener((canvas, position) -> {
            mStickerView.drawCanvas(canvas, position);
            mLastDrawPosition = position;
        });
        mStickerAdapter.setOnItemClickListener((holder, v, position) -> {
            if (!mIsScrollChange) {
                return;
            }

            StickerItem item = mStickerAdapter.get(position);

            mStickerView.addTextSticker(mStickerView.getCurrentFrame(),
                    createAssetsResource(VideoActivity.this, item.getPath()),
                    item.getText(), item.getTextColor(), item.getTextSize(),
                    item.getLeftPadding(), item.getTopPadding(), item.getRightPadding(), item.getBottomPadding());
        });
        mColorAdapter.setOnItemClickListener((holder, v, position) -> {
            if (!mIsScrollChange) {
                return;
            }
            mStickerView.setTextColor(mColorAdapter.get(position));
        });
        mStickerView.setOnSelectedListener(position -> {
            StickerBean stickerBean = mStickerView.getSticker(position);
            if (stickerBean instanceof TextStickerBean) {
                TextStickerBean textStickerBean = (TextStickerBean) stickerBean;
                mBoldView.setSelected(textStickerBean.isBold());
                mItalicView.setSelected(textStickerBean.isItalic());
                mUnderlineView.setSelected(textStickerBean.isUnderline());
            }
        });
        mStickerView.setOnUnselectedListener(stickerBean -> {
            mBoldView.setSelected(false);
            mItalicView.setSelected(false);
            mUnderlineView.setSelected(false);
        });
        mStickerView.setOnLongClickStickerListener(stickerBean -> {
            BottomDialog dialog = new BottomDialog(VideoActivity.this);
            dialog.title(R.string.copy_sticker);
            dialog.canceledOnTouchOutside(true);
            dialog.cancelable(true);
            dialog.inflateMenu(R.menu.menu_copy);
            dialog.setOnItemSelectedListener(id -> {
                copySticker(id);
                return true;
            });
            dialog.show();
        });
        mStickerView.setOnInvalidateListener(() -> {
            if (mLastDrawPosition != mThumbPosition) {
                searchHolder(mThumbPosition);
                mLastDrawPosition = mThumbPosition;
            }
            mCurrentHolder.getStickerThumb().invalidate();
        });
    }

    @OnClick({R.id.start, R.id.stop, R.id.bold, R.id.italic, R.id.underline})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                mGifImage.start();
                mThumbRecyclerView.scrollToPosition(0);
                mThumbRecyclerView.setOnTouchListener(new NotScroll());
                mStickerView.setPlayerState(PlayerSource.PLAYER);
                break;
            case R.id.stop:
                mGifImage.stop();
                break;
            case R.id.bold:
                setStyle(mBoldView);
                break;
            case R.id.italic:
                setStyle(mItalicView);
                break;
            case R.id.underline:
                setStyle(mUnderlineView);
                break;
        }
    }

    private void setStyle(View v) {
        int position = mStickerView.getCurrentPosition();
        if (position == EventHandle.STATE_NORMAL) {
            return;
        }
        v.setSelected(!v.isSelected());
        switch (v.getId()) {
            case R.id.bold:
                mStickerView.setBold(v.isSelected());
                break;
            case R.id.italic:
                mStickerView.setItalic(v.isSelected());
                break;
            case R.id.underline:
                mStickerView.setUnderline(v.isSelected());
                break;
        }
    }

    private void loadThumb(int position) {
        if (mThumbPosition == position) {
            return;
        }

        mGifImage.seekTo(position);

        mStickerView.setCurrentFrame(position);

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
        mStickerAdapter.add(new StickerItem(StickerItem.TEXT, "text1.png", "text1", 0xff000000,
                StickerUtil.dpToPx(VideoActivity.this, 18), 176, 80, 74, 97));
        mStickerAdapter.add(new StickerItem(StickerItem.TEXT, "text2.png", "text2", 0xff000000,
                StickerUtil.dpToPx(VideoActivity.this, 18), 175, 123, 188, 152));

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

    private static class NotScroll implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    private void searchHolder(int position) {
        int count = mThumbRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mThumbRecyclerView.getChildAt(i);
            Object tag = child.getTag();
            if (tag == null) {
                continue;
            }
            if (!(tag instanceof ViewHolder)) {
                continue;
            }
            ViewHolder holder = (ViewHolder) tag;
            if (holder.getLayoutPosition() == position) {
                mCurrentHolder = holder;
                break;
            }
        }
    }

    private void initColor() {
        mColorAdapter.add(0xff000000);
        mColorAdapter.add(0xffffffff);
        mColorAdapter.add(0xffcfcfcf);
        mColorAdapter.add(0xff8a8a8a);
        mColorAdapter.add(0xff212121);
        mColorAdapter.add(0xffff6ca3);
        mColorAdapter.add(0xffff3c85);
        mColorAdapter.add(0xffff3753);
        mColorAdapter.add(0xff8b6cff);
        mColorAdapter.add(0xff5a2eff);
        mColorAdapter.add(0xff9211ff);
        mColorAdapter.add(0xff64e7ff);
        mColorAdapter.add(0xffffb6bf);
        mColorAdapter.add(0xfffff68c);
        mColorAdapter.add(0xffffd76c);
        mColorAdapter.add(0xffffad43);
        mColorAdapter.add(0xffff7b11);
        mColorAdapter.add(0xff7bff6d);
        mColorAdapter.add(0xff2bd57f);
    }

    private void copySticker(int id) {
        int count = mThumbPosition;
        int gifCount = mGifImage.getGifCount();
        switch (id) {
            case R.id.one:
                count += 1;
                break;
            case R.id.five:
                count += 5;
                break;
            case R.id.all:
                count += gifCount;
                break;
        }
        for (int i = mThumbPosition + 1; i <= count; i++) {
            if (i >= gifCount) {
                break;
            }
            mStickerView.copySticker(i);
        }
        mThumbAdapter.notifyDataSetChanged();
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