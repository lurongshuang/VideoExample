package com.lrs.lrsvideolibrary.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * lrs 2019 7 25
 */
public class BaseView extends FrameLayout {
    //视图容器
    private TextureView mTextureView;
    //视图容器生命周期
    private SimpleSurfaceTextureListener mSurfaceTextureListener;
    //播放内容宽度
    private int mVideoWidth;
    //播放内容高                 //为了视频不变形
    private int mVideoHeight;
    //参数
    private SurfaceTexture mSurfaceTexture;

    //是否等比例放大
    private int proportionVideo = 3; // 1等比例放大显示  2 等比例显示 3 全屏显示
    //构造函数
    public BaseView(@NonNull Context context) {
        super(context);
        init();
    }
    //构造函数
    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    //构造函数
    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    //初始化视图
    private void init(){
        //初始化 操控功能
        initTextureView(null);
    }
    //初始化视图
    private void initTextureView(SurfaceTexture surfaceTexture) {
        mTextureView = new TextureView(getContext());
        mTextureView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mTextureView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(mVideoWidth!=0 && mVideoHeight!=0){
                    setVideoCenter(mTextureView.getWidth(), mTextureView.getHeight(), mVideoWidth, mVideoHeight);
                }
            }
        });
        if(surfaceTexture == null) {
            mSurfaceTexture = newSurfaceTexture();
        }else{
            mSurfaceTexture = surfaceTexture;
        }
        mTextureView.setSurfaceTexture(mSurfaceTexture);

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if(mSurfaceTextureListener != null){
                    mSurfaceTextureListener.onSurfaceTextureAvailable(surface, width, height);
                }
            }
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                if(mSurfaceTextureListener != null){
                    mSurfaceTextureListener.onSurfaceTextureSizeChanged(surface, width, height);
                }
            }
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if(mSurfaceTextureListener != null){
                    return mSurfaceTextureListener.onSurfaceTextureDestroyed(surface);
                }
                //当view被销毁时 不销毁SurfaceTexture
                return false;
            }
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                if(mSurfaceTextureListener != null){
                    mSurfaceTextureListener.onSurfaceTextureUpdated(surface);
                }
            }
        });

        addView(mTextureView, 0);
    }

    //视频居中播放
    private void setVideoCenter(float viewWidth, float viewHeight, float videoWidth, float videoHeight){
        //初始化矩阵
        Matrix matrix = new Matrix();
        float sx = viewWidth/videoWidth;
        float sy = viewHeight/videoHeight;
        float maxScale = Math.max(sx, sy);
        matrix.preTranslate((viewWidth - videoWidth) / 2, (viewHeight - videoHeight) / 2);
        matrix.preScale(videoWidth/viewWidth, videoHeight/viewHeight);
        switch (proportionVideo) {
            case 1:
                // 1等比例放大显示  视频等比例放大铺满
                matrix.postScale(maxScale, maxScale, viewWidth/2, viewHeight/2);
                break;
            case 2:
                //2 等比例显示 视频等比例缩小显示
                if(viewHeight>viewWidth) {
                    matrix.postScale( viewWidth/videoWidth, viewWidth/videoWidth, viewWidth/2, viewHeight/2);
                }else {
                    matrix.postScale( viewHeight/videoHeight, viewHeight/videoHeight, viewWidth/2, viewHeight/2);
                }
                break;
            case 3:
                //3 全屏显示 宽高铺满显示
                matrix.postScale( sx, sy, viewWidth/2, viewHeight/2);
                break;
        }
        mTextureView.setTransform(matrix);
        mTextureView.postInvalidate();
    }

    public void resetTextureView(){
        resetTextureView(null);
    }
    public void resetTextureView(SurfaceTexture surfaceTexture){
        removeView(mTextureView);
        initTextureView(surfaceTexture);
    }
    //等比例原则
    public  void setProportionVideo(int proportionVideo) {
        this.proportionVideo = proportionVideo;
    }
    //设置视频源的宽高
    public void setVideoSize(int videoWidth, int videoHeight){
        this.mVideoWidth = videoWidth;
        this.mVideoHeight = videoHeight;
        //刷新布局
        setVideoCenter(mTextureView.getWidth(),mTextureView.getHeight(),mVideoWidth,mVideoHeight);
    }

    public void setSurfaceTextureListener(SimpleSurfaceTextureListener surfaceTextureListener){
        this.mSurfaceTextureListener = surfaceTextureListener;
    }

    //初始化SurfaceTexture
    public SurfaceTexture newSurfaceTexture(){

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int texName = textures[0];
        SurfaceTexture surfaceTexture = new SurfaceTexture(texName);
        surfaceTexture.detachFromGLContext();
        return surfaceTexture;
    }

    public SurfaceTexture getSurfaceTexture() {
        //mSurfaceTexture.isReleased() NoSuchMethodError No virtual method isReleased()Z in class Landroid/graphics/SurfaceTexture
        if(mSurfaceTexture != null){
            return mSurfaceTexture;
        }
        return null;
    }

    public abstract static class SimpleSurfaceTextureListener implements TextureView.SurfaceTextureListener {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }
}
