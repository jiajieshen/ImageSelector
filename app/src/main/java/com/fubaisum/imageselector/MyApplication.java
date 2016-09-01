package com.fubaisum.imageselector;

import android.app.Application;

import com.github.moduth.blockcanary.BlockCanaryContext;

/**
 * Created by sum on 9/1/16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        // LeakCanary检测内存泄漏
//        LeakCanary.install(this);
//        // BlockCanary阻塞检测
//        BlockCanary.install(this, new AppBlockCanaryContext()).start();
    }

    public class AppBlockCanaryContext extends BlockCanaryContext {
        // override to provide context like app qualifier, uid, network type, block threshold, log save path

        // this is default block threshold, you can set it by phone's performance
        @Override
        public int getConfigBlockThreshold() {
            return 500;
        }

        // if set true, notification will be shown, else only write log file
        @Override
        public boolean isNeedDisplay() {
            return BuildConfig.DEBUG;
        }

        // path to save log file
        @Override
        public String getLogPath() {
            return "/blockcanary/performance";
        }
    }
}
