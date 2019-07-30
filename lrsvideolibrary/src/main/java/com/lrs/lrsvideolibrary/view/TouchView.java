package com.lrs.lrsvideolibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lrs.lrsvideolibrary.R;

/**
 * lrs 2019 7 25
 */
public class TouchView extends FrameLayout {

    private OnTouchSlideListener onTouchSlideListener;
    private float slideMove;
    private float slideClick;

    public TouchView(@NonNull Context context) {
        super(context);
        init();
    }

    public TouchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        slideMove = getResources().getDimension(R.dimen.dp_10);
        slideClick = getResources().getDimension(R.dimen.dp_5);
    }

    float downX;
    float downY;
    boolean isSlideing;
    boolean isRight;
    boolean isLeft;
    boolean dontSlide;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
            case MotionEvent.ACTION_MOVE:
                if (onTouchSlideListener != null) {
                    float moveX = event.getRawX();
                    float moveY = event.getRawY();
                    float slideX = moveX - downX;
                    float slideY = moveY - downY;

                    if(isSlideing || isRight || isLeft) {
                        if (Math.abs(slideX) > slideMove && !dontSlide) {
                            requestDisallowInterceptTouchEvents(this, true);
                            downX = moveX;
                        }
                        if (isSlideing) {
                            onTouchSlideListener.onSlide(slideX);
                            downX = moveX;
                        } else if (isLeft) {
                            onTouchSlideListener.onVolumeSlide(slideY);
//                            downY = moveY;
                        } else if (isRight) {
                            onTouchSlideListener.onBrightnessSlide(slideY);
//                            downY = moveY;
                        }
                    }else {
                        //判断 x轴变化
                        if (downX - moveX > 20 || moveX - downX > 20) {
                            //横向滑动
                            isSlideing = true;
                        } else if (downY - moveY > 20 || moveY - downY > 20) {
                            //上下滑动
                            if (downX >= getWidth() * 0.5) {
                                //在右侧滑动
                                isRight = true;
                            } else {
                                //在左侧滑动
                                isLeft = true;
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                requestDisallowInterceptTouchEvents(this, false);
                if (isRight) {
                    if (onTouchSlideListener != null) {
                        onTouchSlideListener.onUp(2);
                    }
                } else if (isLeft) {
                    if (onTouchSlideListener != null) {
                        onTouchSlideListener.onUp(3);
                    }
                } else if (isSlideing) {
                    if (onTouchSlideListener != null) {
                        onTouchSlideListener.onUp(1);
                    }
                } else {
                    float upX = event.getRawX();
                    float upY = event.getRawY();

                    if (Math.abs(upX - downX) < slideClick && Math.abs(upY - downY) < slideClick) {
                        //单击事件
                        if (onTouchSlideListener != null) {
                            onTouchSlideListener.onClick();
                        }
                    }
                }
                isSlideing = false;
                dontSlide = false;
                isLeft = false;
                isRight = false;
                break;
        }
        return true;
    }

    //递归拦截所有父view的事件
    private void requestDisallowInterceptTouchEvents(ViewGroup viewGroup, boolean isIntercept) {

        ViewParent parent = viewGroup.getParent();
        if (parent instanceof ViewGroup) {
            ViewGroup parenViewGroup = (ViewGroup) parent;
            requestDisallowInterceptTouchEvents(parenViewGroup, isIntercept);
            parenViewGroup.requestDisallowInterceptTouchEvent(isIntercept);
        }
    }

    public interface OnTouchSlideListener {
        void onSlide(float distant);

        //1快进，2 left 3 right
        void onUp(int index);

        void onClick();

        void onBrightnessSlide(float percent);

        void onVolumeSlide(float Slide);
    }

    public void setOnTouchSlideListener(OnTouchSlideListener onTouchSlideListener) {
        this.onTouchSlideListener = onTouchSlideListener;
    }
}
