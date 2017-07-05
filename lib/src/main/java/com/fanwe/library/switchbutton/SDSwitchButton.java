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
import android.view.ViewGroup;
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

    private View mViewNormal;
    private View mViewChecked;
    private View mViewHandle;

    private ViewDragHelper mDragHelper;
    private boolean mIsNeedProcess;
    private int mMargins = -1;
    private LayoutParams mParamsHandleView;
    private boolean mIsAlphaMode = true;

    private boolean mIsDebug;

    private OnCheckedChangedCallback mOnCheckedChangedCallback;

    private void init()
    {
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
        mViewNormal = new View(getContext());
        mViewNormal.setBackgroundResource(R.drawable.lib_sb_layer_bg_normal_view);
        addView(mViewNormal, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mViewChecked = new View(getContext());
        mViewChecked.setBackgroundResource(R.drawable.lib_sb_layer_bg_checked_view);
        addView(mViewChecked, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mViewHandle = new View(getContext());
        mViewHandle.setBackgroundResource(R.drawable.lib_sb_layer_bg_handle_view);
        mParamsHandleView = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        addView(mViewHandle, mParamsHandleView);

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

    public View getViewNormal()
    {
        return mViewNormal;
    }

    public View getViewChecked()
    {
        return mViewChecked;
    }

    public View getViewHandle()
    {
        return mViewHandle;
    }

    public void setViewNormal(View viewNormal)
    {
        if (replaceOldView(mViewNormal, viewNormal))
        {
            mViewNormal = viewNormal;
        }
    }

    public void setViewChecked(View viewChecked)
    {
        if (replaceOldView(mViewChecked, viewChecked))
        {
            mViewChecked = viewChecked;
        }
    }

    public void setViewHandle(View viewHandle)
    {
        if (replaceOldView(mViewHandle, viewHandle))
        {
            mViewHandle = viewHandle;
            mParamsHandleView = (LayoutParams) mViewHandle.getLayoutParams();
        }
    }

    private boolean replaceOldView(View viewOld, View viewNew)
    {
        if (viewNew != null && viewOld != viewNew)
        {
            int index = indexOfChild(viewOld);
            ViewGroup.LayoutParams params = viewOld.getLayoutParams();
            removeView(viewOld);

            if (viewNew.getLayoutParams() != null)
            {
                params = viewNew.getLayoutParams();
            }

            addView(viewNew, index, params);
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
                return child == mViewHandle;
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

                if (mIsAlphaMode)
                {
                    ViewCompat.setAlpha(mViewChecked, percent);
                    ViewCompat.setAlpha(mViewNormal, 1 - percent);
                }
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
            showCheckedView(true);
            showNormalView(false);
            if (anim)
            {
                mDragHelper.smoothSlideViewTo(mViewHandle, getLeftChecked(), mViewHandle.getTop());
            } else
            {
                mViewHandle.setLeft(getLeftChecked());
            }
        } else
        {
            showCheckedView(false);
            showNormalView(true);
            if (anim)
            {
                mDragHelper.smoothSlideViewTo(mViewHandle, getLeftNormal(), mViewHandle.getTop());
            } else
            {
                mViewHandle.setLeft(getLeftNormal());
            }
        }
        invalidate();
    }

    private void showCheckedView(boolean show)
    {
        if (mIsAlphaMode)
        {
            ViewCompat.setAlpha(mViewChecked, show ? 1.0f : 0f);
        } else
        {
            mViewChecked.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void showNormalView(boolean show)
    {
        if (mIsAlphaMode)
        {
            ViewCompat.setAlpha(mViewNormal, show ? 1.0f : 0f);
        } else
        {
            mViewNormal.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * 返回normal状态下HandleView的left值
     *
     * @return
     */
    private int getLeftNormal()
    {
        return mParamsHandleView.leftMargin;
    }

    /**
     * 返回checked状态下HandleView的left值
     *
     * @return
     */
    private int getLeftChecked()
    {
        return getWidth() - mViewHandle.getWidth() - mParamsHandleView.rightMargin;
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
        return mViewHandle.getLeft() - getLeftNormal();
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
                if (mDragHelper.isViewUnder(mViewHandle, (int) ev.getRawX(), (int) ev.getRawY()))
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
        LayoutParams params = (LayoutParams) mViewHandle.getLayoutParams();

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
            mViewHandle.setLayoutParams(params);
        }
    }

    public interface OnCheckedChangedCallback
    {
        void onCheckedChanged(boolean checked, SDSwitchButton view);
    }
}
