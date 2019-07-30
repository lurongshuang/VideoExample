package com.lrs.videoexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.lrs.lrsvideolibrary.view.LrsPlayView;
import com.lrs.lrsvideolibrary.view.vide_interface.PlayInterface;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PlayInterface {
    private  static int withnumber;
    LrsPlayView lrsPlayView;
    RelativeLayout rlwin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lrsPlayView =  findViewById(R.id.lrsview);
        rlwin = findViewById(R.id.rlwin);
        //权限
        getPermiss();
        //设置监听器
        lrsPlayView.SetPlayInterface(this);
        //播放地址
        lrsPlayView.setVideoURL("https://vd4.bdstatic.com/mda-ij3q3bd7cassmnsc/sc/mda-ij3q3bd7cassmnsc.mp4");
        //静音
        lrsPlayView.setVolume(0);
        //循环播放
        lrsPlayView.setLooping(true);
        lrsPlayView.prepare();
    }
    @Override
    public void onPause() {
        super.onPause();
        if(lrsPlayView!=null && lrsPlayView.getIsPlaying()) {
            lrsPlayView.pause();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(lrsPlayView!=null && !lrsPlayView.getIsPlaying()) {
            lrsPlayView.start();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                SetonConfigurationChanged(2);
                return  true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //权限获取
    private void getPermiss() {
        AndPermission.with(this).permission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CHANGE_CONFIGURATION,
                Manifest.permission.CHANGE_CONFIGURATION,
                Manifest.permission.WRITE_SETTINGS
        ).callback(new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            }

            @Override
            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

            }
        }).start();
    }
    /**
     * 屏幕旋转时调用此方法
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
//            竖屏
            SetonConfigurationChanged(2);
        }
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
//           横屏
            SetonConfigurationChanged(1);
        }
    }
    /**
     *布局处理
     * @param type 1 横屏 2 竖屏
     */
    private void  SetonConfigurationChanged(int type) {
        if(type == 1) {
            fullscreen(true);
            android.view.ViewGroup.LayoutParams pp =rlwin.getLayoutParams();
            withnumber = pp.height;
            pp.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            pp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            rlwin.setLayoutParams(pp);
        }else {
            fullscreen(false);
            android.view.ViewGroup.LayoutParams pp =rlwin.getLayoutParams();
            pp.height = withnumber;
            pp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            rlwin.setLayoutParams(pp);
        }
    }
    //状态栏操作
    private void fullscreen(boolean enable) {
        if (enable) { //显示状态栏
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        } else { //隐藏状态栏
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(lp);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
 }

    @Override
    public void onVideoRationChanged() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onVideoStart() {
        super.onStart();
    }

    @Override
    public void onVideoPause() {
    }

    @Override
    public void onVideoStop() {
        super.onStop();
    }

    @Override
    public void onVideoCompletion() {
    }

    @Override
    public void onVideoPlayListener(long total,long progress) {
    }
    @Override
    public void onVideoError(int what) {
    }
}
