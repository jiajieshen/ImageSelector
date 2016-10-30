package com.fubaisum.imageselector;

import android.app.Application;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by sum on 9/1/16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // LeakCanary检测内存泄漏
        LeakCanary.install(this);
        // BlockCanary检测阻塞
        BlockCanary.install(this, new BlockCanaryContext()).start();
    }

}
