package com.lrs.lrsvideolibrary.view;

import android.app.Application;

import com.lrs.lrsvideolibrary.view.utils.VideoLRUCacheUtil;

/**
 * lrs
 */
public class App {
    public  static  Application application;
    public static void setApplication(Application application) {
        App.application = application;
        //清理超过大小和存储时间的视频缓存文件
        VideoLRUCacheUtil.checkCacheSize(application.getApplicationContext());
    }
    public static Application getApplication() {
        return application;
    }
}
