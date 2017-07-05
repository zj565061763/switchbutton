package com.fanwe.library.switchbutton;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/7/5.
 */

public class SDSwitchButton extends FrameLayout
{
    public SDSwitchButton(@NonNull Context context)
    {
        super(context);
        init();
    }

    public SDSwitchButton(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SDSwitchButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private boolean mIsChecked;

    private View mNormalView;
    private View mCheckedView;
    private View mHandleView;

    private ViewDragHelper mDragHelper;

    private OnCheckedChangedCallback mOnCheckedChangedCallback;

    private void init()
    {
        if (getBackground() == null)
        {
            setBackgroundResource(R.drawable.layer_bg);
        }
        addDefaultViews();
        initViewDragHelper();
    }

    private void addDefaultViews()
    {
        mNormalView = new View(getContext());
        addView(mNormalView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mCheckedView = new View(getContext());
        addView(mCheckedView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        LayoutParams paramsHandle = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mHandleView = new View(getContext());
        addView(mCheckedView, paramsHandle);
    }

    public void setNormalView(View normalView)
    {
        if (replaceOldView(mNormalView, normalView))
        {
            mNormalView = normalView;
        }
    }

    public void setCheckedView(View checkedView)
    {
        if (replaceOldView(mCheckedView, checkedView))
        {
            mCheckedView = checkedView;
        }
    }

    public void setHandleView(View handleView)
    {
        if (replaceOldView(mHandleView, handleView))
        {
            mHandleView = handleView;
        }
    }

    private boolean replaceOldView(View oldView, View newView)
    {
        if (newView != null && oldView != newView)
        {
            int index = indexOfChild(oldView);
            removeView(oldView);
            addView(newView, index);
            return true;
        } else
        {
            return false;
        }
    }

    private void initViewDragHelper()
    {
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback()
        {
            @Override
            public boolean tryCaptureView(View child, int pointerId)
            {
                if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_SETTLING)
                {
                    return false;
                }
                return child == mHandleView;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx)
            {
                return Math.min(Math.max(left, getLeftNormal()), getLeftChecked());
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel)
            {
                super.onViewReleased(releasedChild, xvel, yvel);

                if (getLeft() >= getAvailableWidth() / 2)
                {
                    setChecked(true, true);
                } else
                {
                    setChecked(false, true);
                }
            }
        });
    }

    public void setChecked(boolean checked, boolean anim)
    {
        setChecked(checked, anim, true);
    }

    public void setChecked(boolean checked, boolean anim, boolean notifyEvent)
    {
        mIsChecked = checked;
        changeViewByCheckedState(anim);
        if (notifyEvent)
        {
            if (mOnCheckedChangedCallback != null)
            {
                mOnCheckedChangedCallback.onCheckedChanged(mIsChecked, this);
            }
        }
    }

    private void changeViewByCheckedState(boolean anim)
    {
        if (mIsChecked)
        {
            showNormalView(false);
            showCheckedView(true);
            if (anim)
            {
                mDragHelper.smoothSlideViewTo(mHandleView, getLeftChecked(), mHandleView.getTop());
            } else
            {

            }
        } else
        {
            showNormalView(true);
            showCheckedView(false);
            if (anim)
            {
                mDragHelper.smoothSlideViewTo(mHandleView, getLeftNormal(), mHandleView.getTop());
            } else
            {

            }
        }
    }

    private int getLeftNormal()
    {
        return getLeft() + getPaddingLeft();
    }

    private int getLeftChecked()
    {
        return getWidth() - mHandleView.getWidth() - getPaddingRight();
    }

    private int getAvailableWidth()
    {
        return getLeftChecked() - getLeftNormal();
    }

    private void showNormalView(boolean show)
    {

    }

    private void showCheckedView(boolean show)
    {

    }

    public void setOnCheckedChangedCallback(OnCheckedChangedCallback onCheckedChangedCallback)
    {
        mOnCheckedChangedCallback = onCheckedChangedCallback;
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
    }

    private SDTouchEventHelper mTouchHelper = new SDTouchEventHelper();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {

        mTouchHelper.processTouchEvent(ev);
        switch (ev.getAction())
        {


        }
        return mTouchHelper.isNeedConsume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);
    }

    public interface OnCheckedChangedCallback
    {
        void onCheckedChanged(boolean checked, SDSwitchButton view);
    }
}
