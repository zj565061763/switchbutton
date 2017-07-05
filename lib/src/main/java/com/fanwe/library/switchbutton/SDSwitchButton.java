package com.fanwe.library.switchbutton;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
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

    private static final String TAG = "SDSwitchButton";

    private boolean mIsChecked;

    private View mNormalView;
    private View mCheckedView;
    private View mHandleView;

    private ViewDragHelper mDragHelper;
    private boolean mIsNeedProcess;
    private int mMargins = -1;
    private LayoutParams mHandleParams;

    private boolean mIsDebug;

    private OnCheckedChangedCallback mOnCheckedChangedCallback;

    private void init()
    {
        if (getBackground() == null)
        {
            setBackgroundResource(R.drawable.layer_bg);
        }
        addDefaultViews();
        initViewDragHelper();
        setOnClickListener(mOnClickListenerDefault);
    }

    public void setDebug(boolean debug)
    {
        mIsDebug = debug;
    }

    private void addDefaultViews()
    {
        mNormalView = new View(getContext());
        addView(mNormalView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mCheckedView = new View(getContext());
        addView(mCheckedView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mHandleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mHandleView = new View(getContext());
        addView(mHandleView, mHandleParams);

        setChecked(mIsChecked, false, false);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l)
    {
        super.setOnClickListener(null);
    }

    private View.OnClickListener mOnClickListenerDefault = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            toggleChecked(true, true);
        }
    };

    public View getNormalView()
    {
        return mNormalView;
    }

    public View getCheckedView()
    {
        return mCheckedView;
    }

    public View getHandleView()
    {
        return mHandleView;
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
            mHandleParams = (LayoutParams) mHandleView.getLayoutParams();
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
                return child == mHandleView;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy)
            {
                return child.getTop();
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx)
            {
                return Math.min(Math.max(left, getLeftNormal()), getLeftChecked());
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy)
            {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                float percent = getScrollDistance() / (float) getAvailableWidth();

                ViewCompat.setAlpha(mCheckedView, percent);
                ViewCompat.setAlpha(mNormalView, 1 - percent);
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId)
            {
                super.onViewCaptured(capturedChild, activePointerId);
                if (mIsDebug)
                {
                    Log.i(TAG, "onViewCaptured");
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel)
            {
                super.onViewReleased(releasedChild, xvel, yvel);
                if (mIsDebug)
                {
                    Log.i(TAG, "onViewReleased");
                }
                if (releasedChild.getLeft() >= ((getLeftNormal() + getLeftChecked()) / 2))
                {
                    setChecked(true, true, true);
                } else
                {
                    setChecked(false, true, true);
                }
            }
        });
    }

    /**
     * 切换选中状态
     *
     * @param anim           是否需要动画
     * @param notifyCallback 是否需要通知回调
     */
    public void toggleChecked(boolean anim, boolean notifyCallback)
    {
        setChecked(!mIsChecked, anim, notifyCallback);
    }

    /**
     * 设置选中状态
     *
     * @param checked        true-选中，false-未选中
     * @param anim           是否需要动画
     * @param notifyCallback 是否需要通知回调
     */
    public void setChecked(boolean checked, boolean anim, boolean notifyCallback)
    {
        if (mIsChecked != checked)
        {
            mIsChecked = checked;
            if (notifyCallback)
            {
                if (mOnCheckedChangedCallback != null)
                {
                    mOnCheckedChangedCallback.onCheckedChanged(mIsChecked, this);
                }
            }
        }

        changeViewByCheckedState(anim);
    }

    private void changeViewByCheckedState(boolean anim)
    {
        if (mIsChecked)
        {
            ViewCompat.setAlpha(mCheckedView, 1.0f);
            ViewCompat.setAlpha(mNormalView, 0f);
            if (anim)
            {
                mDragHelper.smoothSlideViewTo(mHandleView, getLeftChecked(), mHandleView.getTop());
            } else
            {
                mHandleView.setLeft(getLeftChecked());
            }
        } else
        {
            ViewCompat.setAlpha(mCheckedView, 0f);
            ViewCompat.setAlpha(mNormalView, 1.0f);
            if (anim)
            {
                mDragHelper.smoothSlideViewTo(mHandleView, getLeftNormal(), mHandleView.getTop());
            } else
            {
                mHandleView.setLeft(getLeftNormal());
            }
        }
        invalidate();
    }

    /**
     * 返回normal状态下HandleView的left值
     *
     * @return
     */
    private int getLeftNormal()
    {
        return mHandleParams.leftMargin;
    }

    /**
     * 返回checked状态下HandleView的left值
     *
     * @return
     */
    private int getLeftChecked()
    {
        return getWidth() - mHandleView.getWidth() - mHandleParams.rightMargin;
    }

    /**
     * 返回HandleView可以移动的宽度大小
     *
     * @return
     */
    private int getAvailableWidth()
    {
        return getLeftChecked() - getLeftNormal();
    }

    /**
     * 返回HandleView滚动的距离
     *
     * @return
     */
    public int getScrollDistance()
    {
        return mHandleView.getLeft() - getLeftNormal();
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

    @Override
    public void computeScroll()
    {
        if (mDragHelper.continueSettling(true))
        {
            invalidate();
        }
    }

    private SDTouchEventHelper mTouchHelper = new SDTouchEventHelper();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (mIsDebug)
        {
            if (ev.getAction() == MotionEvent.ACTION_DOWN)
            {
                Log.i(TAG, "onInterceptTouchEvent:" + ev.getAction() + "--------------------");
            } else
            {
                Log.i(TAG, "onInterceptTouchEvent:" + ev.getAction());
            }
        }
        if (mTouchHelper.isNeedConsume())
        {
            if (mIsDebug)
            {
                Log.e(TAG, "onInterceptTouchEvent Intercept success because isNeedConsume is true with action----------" + ev.getAction());
            }
            return true;
        }

        mTouchHelper.processTouchEvent(ev);
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mTouchHelper.setNeedConsume(false);
                if (mDragHelper.isViewUnder(mHandleView, (int) ev.getRawX(), (int) ev.getRawY()))
                {
                    mTouchHelper.setNeedConsume(true);
                    mDragHelper.processTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isViewCaptured() && checkMoveParams())
                {
                    mTouchHelper.setNeedConsume(true);
                }
                break;
            default:
                mDragHelper.processTouchEvent(ev);
                break;
        }
        return mTouchHelper.isNeedConsume();
    }

    private boolean isViewCaptured()
    {
        return mDragHelper.getViewDragState() == ViewDragHelper.STATE_DRAGGING;
    }

    private boolean checkMoveParams()
    {
        return mTouchHelper.getDegreeX() < 45;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        if (mIsDebug)
        {
            Log.i(TAG, "onTouchEvent:" + event.getAction());
        }

        mTouchHelper.processTouchEvent(event);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if (!isViewCaptured())
                {
                    //触发ViewDragHelper的尝试捕捉
                    mDragHelper.processTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsNeedProcess)
                {
                    if (isViewCaptured())
                    {
                        mDragHelper.processTouchEvent(event);
                    }
                } else
                {
                    if (isViewCaptured() && checkMoveParams())
                    {
                        setNeedProcess(true, event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchHelper.getUpTime() - mTouchHelper.getDownTime() < 100)
                {
                    mOnClickListenerDefault.onClick(this);
                }
            case MotionEvent.ACTION_CANCEL:
                setNeedProcess(false, event);
                mDragHelper.processTouchEvent(event);
                if (mIsDebug)
                {
                    if (mTouchHelper.isNeedConsume())
                    {
                        Log.e(TAG, "onTouchEvent Intercept released with action " + event.getAction());
                    }
                }
                mTouchHelper.setNeedConsume(false);
                break;
            default:
                mDragHelper.processTouchEvent(event);
                break;
        }

        boolean result = super.onTouchEvent(event) || mIsNeedProcess || event.getAction() == MotionEvent.ACTION_DOWN;
        return result;
    }

    private void setNeedProcess(boolean needProcess, MotionEvent event)
    {
        if (mIsNeedProcess != needProcess)
        {
            mIsNeedProcess = needProcess;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        updateHandlerViewParams();
    }

    private void updateHandlerViewParams()
    {
        boolean needUpdate = false;
        LayoutParams params = (LayoutParams) mHandleView.getLayoutParams();

        //----------margins----------
        if (mMargins < 0)
        {
            mMargins = getHeight() / 15;
        }
        if (mMargins != params.leftMargin)
        {
            params.leftMargin = mMargins;
            params.topMargin = mMargins;
            params.rightMargin = mMargins;
            params.bottomMargin = mMargins;
            needUpdate = true;
        }

        //----------width----------
        int width = getHeight() - 2 * mMargins;
        if (params.width != width)
        {
            params.width = width;
            needUpdate = true;
        }

        if (needUpdate)
        {
            mHandleView.setLayoutParams(params);
        }
    }

    public interface OnCheckedChangedCallback
    {
        void onCheckedChanged(boolean checked, SDSwitchButton view);
    }
}
