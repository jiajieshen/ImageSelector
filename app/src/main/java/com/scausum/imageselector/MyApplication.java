package com.scausum.imageselector;

import android.app.Application;

/**
 * Created by sum on 9/1/16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        // LeakCanary检测内存泄漏
//        LeakCanary.install(this);
//        // BlockCanary检测阻塞
//        BlockCanary.install(this, new BlockCanaryContext()).start();
//
//        CrashWoodpecker.instance()
//                .withKeys("ImageSelector")
//                .setPatchMode(PatchMode.SHOW_LOG_PAGE)
//                .setPassToOriginalDefaultHandler(true)
//                .flyTo(this);
    }

}
