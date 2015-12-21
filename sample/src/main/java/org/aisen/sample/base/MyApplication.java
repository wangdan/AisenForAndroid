package org.aisen.sample.base;

import com.nostra13.universalimageloader.cache.disc.impl.BaseDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.aisen.android.common.context.GlobalContext;
import org.aisen.huaban.R;
import org.aisen.sample.support.db.AisenDB;

import java.io.File;

/**
 * Created by wangdan on 15/4/23.
 */
public class MyApplication extends GlobalContext {

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoader();

        AisenDB.setDB();
    }

    private void initImageLoader() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        DisplayImageOptions imageOption = builder.cacheInMemory(true)
                                                    .showImageOnLoading(R.drawable.comm_loading)
                                                    .cacheOnDisk(true)
                                                    .build();

        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration.Builder(this)
                        .threadPriority(Thread.NORM_PRIORITY - 2)
                        .diskCache(new BaseDiskCache(new File(getImagePath())) {

                            @Override
                            public File getDirectory() {
                                return super.getDirectory();
                            }

                        })
                        .denyCacheImageMultipleSizesInMemory()
                        .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                        .diskCacheSize(100 * 1024 * 1024)
                        .tasksProcessingOrder(QueueProcessingType.LIFO)
                        .defaultDisplayImageOptions(imageOption)
                        .writeDebugLogs()
                        .build();

        ImageLoader.getInstance().init(config);
    }

}
