package com.lrs.lrsvideolibrary.view.vide_interface;

/**
 * lrs
 */
public interface PlayInterface {
       //切换屏幕方向
       void onVideoRationChanged();
       //开始播放
       void onVideoStart();
       //播放暂停
       void onVideoPause();
       //播放停止
       void onVideoStop();
       //播放完成
       void onVideoCompletion();
       //播放进度
       void onVideoPlayListener(long total,long progress);
       //播放出现问题
       void onVideoError(int what);
}
