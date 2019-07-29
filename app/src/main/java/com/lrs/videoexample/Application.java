package com.lrs.videoexample;

import com.lrs.lrsvideolibrary.view.App;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化 视频对象
        App.setApplication(this);
    }
}
