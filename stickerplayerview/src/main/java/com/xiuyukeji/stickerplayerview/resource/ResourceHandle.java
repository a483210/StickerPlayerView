package com.xiuyukeji.stickerplayerview.resource;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.xiuyukeji.stickerplayerview.StickerException;
import com.xiuyukeji.stickerplayerview.bean.BitmapFrameInfo;
import com.xiuyukeji.stickerplayerview.cache.MemoryCache;
import com.xiuyukeji.stickerplayerview.cache.MemoryReusable;
import com.xiuyukeji.stickerplayerview.cache.impl.UsingFreqMemoryCache;

import java.util.HashMap;
import java.util.Locale;

import static com.xiuyukeji.stickerplayerview.event.EventHandle.STATE_NORMAL;

/**
 * 贴纸资源管理
 *
 * @author Created by jz on 2017/4/18 13:13
 */
public class ResourceHandle {

    private final HashMap<String, Resource> mResources;

    private MemoryCache mBitmapCache;
    private MemoryReusable mMemoryReusable;

    private int mDynamicCount;

    public ResourceHandle() {
        mResources = new HashMap<>();

        setMemoryCache(new UsingFreqMemoryCache());
    }

    public void setMemoryCache(MemoryCache memoryCache) {
        if (mBitmapCache != null && mBitmapCache.size() != 0) {
            throw new StickerException("必须在未添加贴纸之前设置内存管理器！");
        }

        this.mBitmapCache = memoryCache;
        if (mBitmapCache instanceof MemoryReusable) {
            mMemoryReusable = (MemoryReusable) memoryCache;
        } else {
            mMemoryReusable = null;
        }
    }

    /**
     * 返回图像
     *
     * @param index    索引
     * @param uptimeMs 时间
     */
    public void loadBitmap(String index, long uptimeMs, BitmapFrameInfo bitmapFrameInfo) {
        Resource resource = mResources.get(index);
        if (resource == null) {
            bitmapFrameInfo.reset(null, 0);
            return;
        }

        int frameIndex;

        if (resource instanceof DynamicResource) {
            DynamicResource dynamic = (DynamicResource) resource;

            uptimeMs %= dynamic.getDuration();

            frameIndex = Math.round(uptimeMs / dynamic.getDelayTime());
        } else {
            frameIndex = 0;
        }

        String cacheIndex = getCacheIndex(index, frameIndex);

        bitmapFrameInfo.reset(getCacheBitmap(resource, cacheIndex, frameIndex), frameIndex);
    }

    //从缓存中获取图像
    private Bitmap getCacheBitmap(Resource resource, String cacheIndex, int frameIndex) {
        Bitmap bitmap = mBitmapCache.get(cacheIndex);
        if (bitmap == null) {
            if (mMemoryReusable != null
                    && resource instanceof BitmapReusable) {//如果是可重用的
                BitmapReusable bitmapReusable = (BitmapReusable) resource;
                bitmap = mMemoryReusable.getReusableBitmap(bitmapReusable);
                if (bitmap != null) {
                    bitmap = bitmapReusable.getBitmap(bitmap, frameIndex);
                }
            }
            if (bitmap == null) {
                bitmap = resource.getBitmap(frameIndex);
            }
            if (bitmap == null) {
                return null;
            }
            mBitmapCache.put(cacheIndex, bitmap, resource.getWeight());
        }
        return bitmap;
    }

    //获得缓存索引
    private String getCacheIndex(String index, int frameIndex) {
        return String.format(Locale.getDefault(), "%s_%d", index, frameIndex);
    }

    /**
     * 初始化资源
     *
     * @param resource 资源
     * @return 如果初始化失败则返回null
     */
    public Resource initResource(Resource resource) {
        if (resource == null) {
            return null;
        }

        String index = resource.getIndex();
        String cacheIndex = getCacheIndex(index, 0);
        Bitmap bitmap;

        Resource resourceBean = mResources.get(index);
        if (resourceBean != null) {
            bitmap = getCacheBitmap(resource, cacheIndex, 0);
            resourceBean.addedUseCount();
            resource = resourceBean;
        } else {
            mResources.put(index, resource);
            bitmap = resource.init();
            if (resource instanceof DynamicResource) {
                mDynamicCount++;
            }
        }

        if (bitmap == null) {
            return null;
        }

        if (!mBitmapCache.containsIndex(cacheIndex)) {
            mBitmapCache.put(cacheIndex, bitmap, resource.getWeight());
        }
        return resource;
    }

    //资源使用次数加1
    public void addedUseCount(String index) {
        if (TextUtils.isEmpty(index)) {
            return;
        }
        Resource resource = mResources.get(index);
        if (resource == null) {
            return;
        }
        resource.addedUseCount();
    }

    //资源使用次数减1，如果为0则释放资源
    public void decrementUseCount(String index) {
        if (TextUtils.isEmpty(index)) {
            return;
        }
        Resource resource = mResources.get(index);
        if (resource == null) {
            return;
        }

        if (resource.decrementUseCount() <= 0) {
            mResources.remove(index);
            resource.recycle();

            int count = 1;
            if (resource instanceof DynamicResource) {
                DynamicResource dynamicResource = (DynamicResource) resource;
                count = dynamicResource.getFrameCount();
                mDynamicCount--;
            }
            for (int i = 0; i < count; i++) {
                String cacheIndex = getCacheIndex(index, i);
                mBitmapCache.remove(cacheIndex);
            }
            if (mResources.isEmpty()) {
                mBitmapCache.clear();
            }
        }
    }

    /**
     * 判断是否动态资源
     *
     * @param index 索引
     */
    public boolean isDynamic(String index) {
        Resource resource = mResources.get(index);
        if (resource == null) {
            return false;
        }
        return resource instanceof DynamicResource;
    }

    /**
     * 返回资源总帧数
     *
     * @param index 索引
     */
    public int getFrameCount(String index) {
        Resource resource = mResources.get(index);
        if (resource == null) {
            return STATE_NORMAL;
        }
        if (resource instanceof DynamicResource) {
            DynamicResource dynamicResource = (DynamicResource) resource;
            return dynamicResource.getFrameCount();
        } else {
            return 1;
        }
    }

    /**
     * 获得动态资源统计
     */
    public int getDynamicCount() {
        return mDynamicCount;
    }
}
