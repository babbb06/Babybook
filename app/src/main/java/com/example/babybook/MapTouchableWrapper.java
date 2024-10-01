package com.example.babybook;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MapTouchableWrapper extends FrameLayout {

    public interface OnTouchListener {
        void onTouch();
    }

    private OnTouchListener onTouchListener;

    public MapTouchableWrapper(Context context) {
        super(context);
    }

    public MapTouchableWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapTouchableWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnTouchListener(OnTouchListener listener) {
        this.onTouchListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (onTouchListener != null) {
            onTouchListener.onTouch();
        }
        return super.dispatchTouchEvent(ev);
    }
}
