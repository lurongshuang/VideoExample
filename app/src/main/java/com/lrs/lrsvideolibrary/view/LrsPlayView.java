package com.lrs.lrsvideolibrary.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lrs.lrsvideolibrary.R;
import com.lrs.lrsvideolibrary.view.utils.MyUtil;
import com.lrs.lrsvideolibrary.view.vide_interface.PlayInterface;

import java.util.Timer;
import java.util.TimerTask;

public class LrsPlayView extends FrameLayout implements TouchView.OnTouchSlideListener, View.OnClickListener{
    //播放图层
    BaseView playTextureView;
    //播放con
    private ViewTool mMediaPlayerTool;
    //播放监听
    ViewTool.VideoListener myVideoListener;
    //总父级
    RelativeLayout rlwindow;
    //加载框
    ProgressBar pb_loading;
    //手势监听对象
    TouchView videoTouchView;
    //快进快退父类
    RelativeLayout rl_change_progress;
    //快进快退图标
    ImageView iv_change_progress;
    //快进快退文字 00:00
    TextView tv_change_progress;
    //公共进度
    long changeProgressTime;
    //------------------------------------------------
    //进度操作模块
    //播放进度父级
    LinearLayout ll_progress;
    //播放进度条
    ProgressBar pb_progress;
    //播放当前时间
    TextView tv_pro;
    //播放总时间
    TextView tv_play_total;
    //播放 暂停按钮
    ImageView iv_playstate;
    // 缩放 展开 横屏 竖屏按钮
    ImageView iv_playhorizontal;
    //向下传递监听
    PlayInterface playInterface;
    //操作栏倒计时隐藏毫秒
    static int CountDown = 5000;

    //屏幕亮度
    TextView tvBrightness;

    //锁屏anniu
    ImageView ivlock;
    //是否显示进度 操作收拾是否可用等
    private static boolean showProgress = true;

    /**
     * 设置监听动作
     *
     * @param playInterface
     */
    public void SetPlayInterface(PlayInterface playInterface) {
        this.playInterface = playInterface;
    }

    //构造函数
    public LrsPlayView(Context context) {
        super(context);
        init();
    }

    //构造函数
    public LrsPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //构造函数
    public LrsPlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //视图初始化
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.lrsplayview, this);
        playTextureView = findViewById(R.id.playTextureView);
        rlwindow = findViewById(R.id.rlwindow);
        pb_loading = findViewById(R.id.pb_loading);
        videoTouchView = findViewById(R.id.videoTouchView);
        rl_change_progress = findViewById(R.id.rl_change_progress);
        iv_change_progress = findViewById(R.id.iv_change_progress);
        tv_change_progress = findViewById(R.id.tv_change_progress);
        ll_progress = findViewById(R.id.ll_progress);
        pb_progress = findViewById(R.id.pb_progress);
        tv_pro = findViewById(R.id.tv_pro);
        tv_play_total = findViewById(R.id.tv_play_total);
        iv_playstate = findViewById(R.id.iv_playstate);
        iv_playhorizontal = findViewById(R.id.iv_playhorizontal);
        tvBrightness = findViewById(R.id.tvBrightness);
        ivlock = findViewById(R.id.ivlock);
        ivlock.setImageResource(R.drawable.ic_videolock);
        ivlock.setTag(true);
        ivlock.setOnClickListener(this);
        //透明度
        ll_progress.setAlpha(0.8f);
        iv_playstate.setOnClickListener(this);
        iv_playhorizontal.setOnClickListener(this);
        //滑动监听
        videoTouchView.setOnTouchSlideListener(this);
        //播放初始化
        mMediaPlayerTool = ViewTool.getInstance();
        mMediaPlayerTool.initMediaPLayer();
        playTextureView.setProportionVideo(2);
    }

    /**
     * 设置播放地址
     *
     * @param url 播放地址
     */
    public void setVideoURL(String url) {
        if (mMediaPlayerTool != null) {
            mMediaPlayerTool.setDataSource(url);
        }
    }

    /**
     * 是否显示声音
     *
     * @param volume 1显示声音 0 不显示声音
     */
    public void setVolume(float volume) {
        if (mMediaPlayerTool != null) {
            mMediaPlayerTool.setVolume(volume);
        }
    }

    /**
     * 获取声音状态
     *
     * @return 1有声音 0 无声音
     */
    public float getVolume() {
        if (mMediaPlayerTool != null) {
            return mMediaPlayerTool.getVolume();
        }
        return 0;
    }

    /**
     * 是否循环播放
     *
     * @param looping
     */
    public void setLooping(boolean looping) {
        if (mMediaPlayerTool != null) {
            mMediaPlayerTool.setLooping(looping);
        }
    }

    /**
     * 获取当前循环状态
     *
     * @return
     */
    public boolean getLooping() {
        if (mMediaPlayerTool != null) {
            return mMediaPlayerTool.isLooping();
        }
        return false;
    }

    /**
     * 视频宽度
     *
     * @return
     */
    public int getVideoWidth() {
        if (mMediaPlayerTool != null) {
            return mMediaPlayerTool.getVideoWidth();
        }
        return 0;
    }

    /**
     * 视频高度
     *
     * @return
     */
    public int getVideoHeight() {
        if (mMediaPlayerTool != null) {
            return mMediaPlayerTool.getVideoHeight();
        }
        return 0;
    }


    /**
     * 开始播放
     */
    public void prepare() {
        playVideo();
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean getIsPlaying() {
        if (mMediaPlayerTool != null) {
            return mMediaPlayerTool.isPlaying();
        }
        return false;
    }

    private void playVideo() {
        pb_loading.setVisibility(View.VISIBLE);
        myVideoListener = new ViewTool.VideoListener() {
            @Override
            public void onStart() {
                pb_loading.setVisibility(View.GONE);
                playTextureView.setVideoSize(mMediaPlayerTool.getVideoWidth(), mMediaPlayerTool.getVideoHeight());
                iv_playstate.setTag(R.id.playState, true);
                if (showProgress) {
                    ll_progress.setVisibility(View.VISIBLE);
                    ll_progress.setTag(R.id.showState, true);
                    fullscreen(((Activity)getContext()),false);
                }
                playInterface.onVideoStart();
                countDownGone();
            }

            @Override
            public void onStop() {
                rl_change_progress.setVisibility(View.GONE);
                tv_pro.setText("00:00");
                tv_play_total.setText("00:00");
                iv_playstate.setTag(R.id.playState, false);
                playInterface.onVideoStop();
            }

            @Override
            public void onCompletion() {
                onStop();
            }

            @Override
            public void onRotationInfo(int rotation) {
                playTextureView.setRotation(rotation);
            }

            @Override
            public void onPlayProgress(long currentPosition) {
                String date = MyUtil.fromMMss(mMediaPlayerTool.getDuration() - currentPosition);
                pb_progress.setProgress((int) currentPosition);
                pb_progress.setMax((int) mMediaPlayerTool.getDuration());
                tv_pro.setText(MyUtil.fromMMss(currentPosition));
                tv_play_total.setText(MyUtil.fromMMss(mMediaPlayerTool.getDuration()));
                playInterface.onVideoPlayListener(mMediaPlayerTool.getDuration(), currentPosition);
            }

            @Override
            public void onError(int what) {
                playInterface.onVideoError(what);
            }
        };
        mMediaPlayerTool.setVideoListener(myVideoListener);
        playTextureView.resetTextureView();
        mMediaPlayerTool.setPlayTextureView(playTextureView);
        mMediaPlayerTool.setSurfaceTexture(playTextureView.getSurfaceTexture());
        mMediaPlayerTool.prepare();
    }


    private void changeProgressText(RelativeLayout pb_play_progress, float distant) {
        float radio = distant / pb_play_progress.getWidth();
        if(radio == Float.POSITIVE_INFINITY) {
            return;
        }
        changeProgressTime += mMediaPlayerTool.getDuration() * radio;
        if (changeProgressTime < 0) {
            changeProgressTime = 0;
        }
        if (changeProgressTime > mMediaPlayerTool.getDuration()) {
            changeProgressTime = mMediaPlayerTool.getDuration();
        }

        String changeTimeStr = MyUtil.fromMMss(changeProgressTime);
        String rawTime = MyUtil.fromMMss(mMediaPlayerTool.getDuration());
        tv_change_progress.setText(changeTimeStr + " / " + rawTime);

        pb_progress.setProgress((int) changeProgressTime);
        pb_progress.setMax((int) mMediaPlayerTool.getDuration());
        tv_pro.setText(changeTimeStr);
        tv_play_total.setText(rawTime);

        if (changeProgressTime > mMediaPlayerTool.getCurrentPosition()) {
            iv_change_progress.setImageResource(R.mipmap.video_fast_forward);
        } else {
            iv_change_progress.setImageResource(R.mipmap.video_fast_back);
        }
    }

    //进度滑动监听
    @Override
    public void onSlide(float distant) {
        if (!showProgress) {
            return;
        }
        if (mMediaPlayerTool == null) {
            return;
        }
        if (!rl_change_progress.isShown()) {
            rl_change_progress.setVisibility(View.VISIBLE);
            changeProgressTime = mMediaPlayerTool.getCurrentPosition();
        }
        changeProgressText(rl_change_progress, distant);
    }

    @Override
    public void onUp(int index) {
        switch (index) {
            case 1:
                if (!showProgress) {
                    return;
                }
                if (rl_change_progress.isShown()) {
                    rl_change_progress.setVisibility(View.GONE);
                }
                mMediaPlayerTool.seekTo(changeProgressTime);
                break;
            case 2:
            case 3:
                if (!showProgress) {
                    return;
                }
                tvBrightness.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick() {
        boolean isshow = (boolean) ll_progress.getTag(R.id.showState);
        boolean isshow2 = (boolean) ivlock.getTag(R.id.showState);
        if (isshow || isshow2) {
            if (showProgress) {
                ll_progress.setVisibility(View.GONE);
                ll_progress.setTag(R.id.showState, false);
                fullscreen(((Activity)getContext()),true);
            }
            ivlock.setVisibility(View.GONE);
            ivlock.setTag(R.id.showState, false);
        } else {
            if (showProgress) {
                ll_progress.setVisibility(View.VISIBLE);
                ll_progress.setTag(R.id.showState, true);
                fullscreen(((Activity)getContext()),false);
            }
            countDownGone();
        }
    }

    //设置当前界面亮度
    public static void setBrightness(Activity activity, float brightness) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        window.setAttributes(lp);
    }

    //获取亮度
    public static float getScreenBrightness(Activity context) {
        return context.getWindow().getAttributes().screenBrightness * 255;
    }

    @Override
    public void onBrightnessSlide(float percent) {
        if (!showProgress) {
            return;
        }
        //减少调用次数 滑动坐标增长3调用一次
        if (((int) percent) % 3 == 0) {
            float now = getScreenBrightness(((Activity) getContext()));
            float br = 0;
            if (percent < 0) {
                br = now + 10;
            } else {
                br = now - 10;
            }
            if (br > 255) {
                br = 255;
            } else if (br < 0) {
                br = 0;
            }
            tvBrightness.setVisibility(View.VISIBLE);
            float a = (br / 255);
            int b = (int) (a * 100);
            tvBrightness.setText("当前亮度 " + b + "%");
            setBrightness(((Activity) getContext()), br);
        }
    }

    //获取最大心凉
    public float getSlideMax(Activity activity) {
        AudioManager mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        float max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return max;
    }

    //获取当前音量
    public float getslide(Activity activity) {
        AudioManager mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        float current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return current;
    }

    public void setSlide(Activity activity, int Type) {
        AudioManager mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, Type, AudioManager.FLAG_PLAY_SOUND);
    }

    @Override
    public void onVolumeSlide(float Slide) {
        if (!showProgress) {
            return;
        }
        //减少调用次数 滑动坐标增长3调用一次
        Log.e("Slide", Slide + "");
        if (((int) Slide) % 3 == 0) {
            float cu = getslide(((Activity) getContext()));
            float to = getSlideMax(((Activity) getContext()));
            int br;
            if (Slide > 0) {
                br = AudioManager.ADJUST_LOWER;
            } else {
                br = AudioManager.ADJUST_RAISE;
            }
            tvBrightness.setVisibility(View.VISIBLE);
            float a = (cu / to);
            int b = (int) (a * 100);
            tvBrightness.setText("当前声音 " + b + "%");
            setSlide(((Activity) getContext()), br);
        }
    }

    public void pause() {
        changePlayState(iv_playstate);
        if (mMediaPlayerTool != null) {
            mMediaPlayerTool.pause();
        }
    }

    public void stop() {
        changePlayState(iv_playstate);
        if (mMediaPlayerTool != null) {
            mMediaPlayerTool.stop();
        }
    }

    public void start() {
        changePlayState(iv_playstate);
        if (mMediaPlayerTool != null) {
            mMediaPlayerTool.start();
        }
    }

    public void changePlayState(View view) {
        boolean isplay = (boolean) view.getTag(R.id.playState);
        if (isplay) {
            mMediaPlayerTool.pause();
            iv_playstate.setTag(R.id.playState, false);
            ((ImageView) view).setImageResource(R.drawable.ic_video_play);
        } else {
            mMediaPlayerTool.start();
            iv_playstate.setTag(R.id.playState, true);
            ((ImageView) view).setImageResource(R.drawable.ic_video_pause);
        }
    }
    private void fullscreen(Activity activity ,boolean enable) {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            return;
        }
        if (enable) { //隐藏状态栏
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(lp);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            //隐藏虚拟按键
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                View v = activity.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                View decorView = activity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }

        } else { //显示状态栏
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(lp);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //显示虚拟按键
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                //低版本sdk
                View v = activity.getWindow().getDecorView();
                v.setSystemUiVisibility(View.VISIBLE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                View decorView = activity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_playstate) {
            changePlayState(view);
        } else if (view.getId() == R.id.iv_playhorizontal) {
            playInterface.onVideoRationChanged();
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                ivlock.setVisibility(View.GONE);
                ivlock.setTag(R.id.showState, false);
                showProgress = true;
                iv_playhorizontal.setImageResource(R.drawable.ic_video_horizontal);
                fullscreen(((Activity)getContext()),false);
            } else if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                //如果屏幕当前是竖屏显示，则设置屏幕锁死为横屏显示
                ivlock.setVisibility(View.VISIBLE);
                ivlock.setTag(R.id.showState, true);
//                showProgress = false;
                iv_playhorizontal.setImageResource(R.drawable.ic_video_vertical);
                fullscreen(((Activity)getContext()),true);
            }
        } else if (view.getId() == R.id.ivlock) {
            boolean isshow = (boolean) ivlock.getTag();
            if (isshow) {
                //变为false
                showProgress = false;
                view.setTag(false);
                ivlock.setImageResource(R.drawable.ic_lockunblock);
                ll_progress.setVisibility(View.GONE);
                ll_progress.setTag(R.id.showState, false);
                fullscreen(((Activity)getContext()),true);
            } else {
                showProgress = true;
                view.setTag(true);
                ivlock.setImageResource(R.drawable.ic_videolock);
                ll_progress.setVisibility(View.VISIBLE);
                ll_progress.setTag(R.id.showState, true);
                fullscreen(((Activity)getContext()),false);
            }


        }
    }

    TimerTask task = null;
    Handler handler = null;

    public void isHandler() {
        if (handler == null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    switch (message.what) {
                        case 200:
                            if (showProgress) {
                                ll_progress.setVisibility(View.GONE);
                                ll_progress.setTag(R.id.showState, false);
                                fullscreen(((Activity)getContext()),true);
                            }
                            ivlock.setVisibility(View.GONE);
                            ivlock.setTag(R.id.showState, false);
                            break;
                    }
                }
            };
        }
    }

    public void countDownGone() {
        isHandler();
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            ivlock.setVisibility(View.VISIBLE);
            ivlock.setTag(R.id.showState, true);
        } else {
            ivlock.setVisibility(View.GONE);
            ivlock.setTag(R.id.showState, false);
        }
        float i = mMediaPlayerTool.getRotation();
        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 200;
                    handler.sendMessage(message);
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, CountDown);
        } else {
            task.cancel();
            task = null;
            countDownGone();
        }
    }

    public void setCountDown(int CountDown) {
        this.CountDown = CountDown;
    }

}
