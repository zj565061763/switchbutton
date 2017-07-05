package com.fanwe.library.switchbutton;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/7/5.
 */

public class SDSwitchButton extends FrameLayout implements ISDSwitchButton
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

    /**
     * 是否选中
     */
    private boolean mIsChecked;
    /**
     * 正常view
     */
    private View mViewNormal;
    /**
     * 选中view
     */
    private View mViewChecked;
    /**
     * 手柄view
     */
    private View mViewHandle;
    /**
     * 手柄view是否被自定义
     */
    private boolean mIsViewHandleCustom;

    private ViewDragHelper mDragHelper;
    private GestureDetector mGestureDetector;
    private boolean mIsNeedProcess;
    /**
     * 是否是透明度模式来显示隐藏view的
     */
    private boolean mIsAlphaMode = true;
    /**
     * 手柄view左边间距
     */
    private int mMarginLeft;
    /**
     * 手柄view顶部间距
     */
    private int mMarginTop;
    /**
     * 手柄view右边间距
     */
    private int mMarginRight;
    /**
     * 手柄view底部间距
     */
    private int mMarginBottom;

    private boolean mIsDebug;

    private OnCheckedChangedCallback mOnCheckedChangedCallback;

    private void init()
    {
        addDefaultViews();
        initGestureDetector();
        initViewDragHelper();
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
        addView(mViewHandle, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

        setChecked(mIsChecked, false, false);
    }


    private void initGestureDetector()
    {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                toggleChecked(true, true);
                return super.onSingleTapUp(e);
            }
        });
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
     * 根据状态改变view
     *
     * @param anim
     */
    private void updateViewByState(boolean anim)
    {
        if (mIsChecked)
        {
            showCheckedView(true);
            showNormalView(false);

            final int left = getLeftChecked();
            if (mViewHandle.getLeft() != left)
            {
                if (anim)
                {
                    mDragHelper.smoothSlideViewTo(mViewHandle, getLeftChecked(), mViewHandle.getTop());
                } else
                {
                    mViewHandle.layout(left, mViewHandle.getTop(), left + mViewHandle.getMeasuredWidth(), mViewHandle.getBottom());
                }
                invalidate();
            }
        } else
        {
            showCheckedView(false);
            showNormalView(true);

            final int left = getLeftNormal();
            if (mViewHandle.getLeft() != left)
            {
                if (anim)
                {
                    mDragHelper.smoothSlideViewTo(mViewHandle, getLeftNormal(), mViewHandle.getTop());
                } else
                {
                    mViewHandle.layout(left, mViewHandle.getTop(), left + mViewHandle.getMeasuredWidth(), mViewHandle.getBottom());
                }
                invalidate();
            }
        }
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
        return mMarginLeft;
    }

    /**
     * 返回checked状态下HandleView的left值
     *
     * @return
     */
    private int getLeftChecked()
    {
        return getMeasuredWidth() - mViewHandle.getMeasuredWidth() - mMarginRight;
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
    private int getScrollDistance()
    {
        return mViewHandle.getLeft() - getLeftNormal();
    }

    /**
     * 返回手柄view布局参数
     */
    private LayoutParams getParamsHandleView()
    {
        return (LayoutParams) mViewHandle.getLayoutParams();
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        View normal = findViewById(R.id.view_normal);
        if (normal != null)
        {
            removeView(normal);
            setViewNormal(normal);
        }

        View checked = findViewById(R.id.view_checked);
        if (checked != null)
        {
            removeView(checked);
            setViewChecked(checked);
        }

        View handle = findViewById(R.id.view_handle);
        if (handle != null)
        {
            removeView(handle);
            setViewHandle(handle);
        }
    }

    /**
     * 设置正常view
     *
     * @param viewNormal
     */
    private void setViewNormal(View viewNormal)
    {
        if (replaceOldView(mViewNormal, viewNormal))
        {
            mViewNormal = viewNormal;
        }
    }

    /**
     * 设置选中view
     *
     * @param viewChecked
     */
    private void setViewChecked(View viewChecked)
    {
        if (replaceOldView(mViewChecked, viewChecked))
        {
            mViewChecked = viewChecked;
        }
    }

    /**
     * 设置手柄view
     *
     * @param viewHandle
     */
    private void setViewHandle(View viewHandle)
    {
        if (replaceOldView(mViewHandle, viewHandle))
        {
            mViewHandle = viewHandle;
            mIsViewHandleCustom = true;
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

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mTouchHelper.setNeedConsume(true);
                if (getParent() != null)
                {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
        }
        return mTouchHelper.isNeedConsume();
    }

    private boolean checkMoveParams()
    {
        return mTouchHelper.getDegreeX() < 45;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        mGestureDetector.onTouchEvent(event);
        mTouchHelper.processTouchEvent(event);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                //触发ViewDragHelper的尝试捕捉
                mDragHelper.processTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsNeedProcess)
                {
                    mDragHelper.processTouchEvent(event);
                } else
                {
                    if (checkMoveParams())
                    {
                        setNeedProcess(true, event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null)
                {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
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
        updateViewByState(false);
    }

    /**
     * 更新手柄view的布局参数
     */
    private void updateHandlerViewParams()
    {
        boolean needUpdate = false;
        LayoutParams params = getParamsHandleView();

        //----------margins----------
        if (!mIsViewHandleCustom)
        {
            if (mMarginLeft == 0)
            {
                mMarginLeft = getHeight() / 15;
            }
            if (mMarginTop == 0)
            {
                mMarginTop = getHeight() / 15;
            }
            if (mMarginRight == 0)
            {
                mMarginRight = getHeight() / 15;
            }
            if (mMarginBottom == 0)
            {
                mMarginBottom = getHeight() / 15;
            }
        } else
        {
            mMarginLeft = params.leftMargin;
            mMarginTop = params.topMargin;
            mMarginRight = params.rightMargin;
            mMarginBottom = params.bottomMargin;
        }

        if (params.leftMargin != mMarginLeft)
        {
            params.leftMargin = mMarginLeft;
            needUpdate = true;
        }
        if (params.topMargin != mMarginTop)
        {
            params.topMargin = mMarginTop;
            needUpdate = true;
        }
        if (params.rightMargin != mMarginRight)
        {
            params.rightMargin = mMarginRight;
            needUpdate = true;
        }
        if (params.bottomMargin != mMarginBottom)
        {
            params.bottomMargin = mMarginBottom;
            needUpdate = true;
        }

        //----------height----------
        if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT)
        {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            needUpdate = true;
        }
        //----------width----------
        if (params.width <= 0)
        {
            int width = getHeight() - mMarginTop - mMarginBottom;
            if (params.width != width)
            {
                params.width = width;
                needUpdate = true;
            }
        }

        if (needUpdate)
        {
            mViewHandle.setLayoutParams(params);
        }
    }

    //----------ISDSwitchButton implements start----------

    @Override
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

        updateViewByState(anim);
    }

    @Override
    public void toggleChecked(boolean anim, boolean notifyCallback)
    {
        setChecked(!mIsChecked, anim, notifyCallback);
    }

    @Override
    public void setOnCheckedChangedCallback(OnCheckedChangedCallback onCheckedChangedCallback)
    {
        mOnCheckedChangedCallback = onCheckedChangedCallback;
    }

    @Override
    public void setMarginLeft(int marginLeft)
    {
        mMarginLeft = marginLeft;
        updateHandlerViewParams();
    }

    @Override
    public void setMarginTop(int marginTop)
    {
        mMarginTop = marginTop;
        updateHandlerViewParams();
    }

    @Override
    public void setMarginRight(int marginRight)
    {
        mMarginRight = marginRight;
        updateHandlerViewParams();
    }

    @Override
    public void setMarginBottom(int marginBottom)
    {
        mMarginBottom = marginBottom;
        updateHandlerViewParams();
    }

    @Override
    public void setAlphaMode(boolean alphaMode)
    {
        mIsAlphaMode = alphaMode;
    }

    //----------ISDSwitchButton implements end----------

}
