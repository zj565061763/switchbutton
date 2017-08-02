package com.fanwe.library.switchbutton;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/7/5.
 */
public class SDSwitchButton extends FrameLayout implements ISDSwitchButton
{
    public SDSwitchButton(@NonNull Context context)
    {
        super(context);
        init(context, null);
    }

    public SDSwitchButton(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        init(context, attrs);
    }

    public SDSwitchButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
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
    private View mViewThumb;

    private int mTouchSlop;
    private long mClickTimeout;

    private SDTouchHelper mTouchHelper = new SDTouchHelper();
    private ViewDragHelper mViewDragHelper;
    private boolean mIsScrollerStarted;

    private SBAttrModel mAttrModel = new SBAttrModel();

    private boolean mIsDebug;

    private OnCheckedChangedCallback mOnCheckedChangedCallback;
    private OnViewPositionChangedCallback mOnViewPositionChangedCallback;

    private void init(Context context, AttributeSet attrs)
    {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mClickTimeout = ViewConfiguration.getPressedStateDuration() + ViewConfiguration.getTapTimeout();

        initViewDragHelper();

        mAttrModel.parse(context, attrs);
        addDefaultViews();
    }

    public void setDebug(boolean debug)
    {
        mIsDebug = debug;
        mTouchHelper.setDebug(debug);
    }

    private void addDefaultViews()
    {
        ImageView imageNormal = new ImageView(getContext());
        imageNormal.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageNormal.setImageResource(mAttrModel.getImageNormalResId());
        addView(imageNormal, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mViewNormal = imageNormal;

        ImageView imageChecked = new ImageView(getContext());
        imageChecked.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageChecked.setImageResource(mAttrModel.getImageCheckedResId());
        addView(imageChecked, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mViewChecked = imageChecked;

        ImageView imageThumb = new SDThumbImageView(getContext());
        imageThumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageThumb.setImageResource(mAttrModel.getImageThumbResId());
        LayoutParams pThumb = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        pThumb.gravity = Gravity.CENTER_VERTICAL;
        pThumb.leftMargin = mAttrModel.getMarginLeft();
        pThumb.topMargin = mAttrModel.getMarginTop();
        pThumb.rightMargin = mAttrModel.getMarginRight();
        pThumb.bottomMargin = mAttrModel.getMarginBottom();
        addView(imageThumb, pThumb);
        mViewThumb = imageThumb;

        setChecked(mAttrModel.isChecked(), false, false);
    }

    private void initViewDragHelper()
    {
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback()
        {
            @Override
            public boolean tryCaptureView(View child, int pointerId)
            {
                return false;
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId)
            {
                super.onViewCaptured(capturedChild, activePointerId);
                if (mIsDebug)
                {
                    Log.i(TAG, "ViewDragHelper onViewCaptured----------");
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel)
            {
                super.onViewReleased(releasedChild, xvel, yvel);
                if (mIsDebug)
                {
                    Log.i(TAG, "ViewDragHelper onViewReleased");
                }
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy)
            {
                return child.getTop();
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx)
            {
                int result = Math.max(getLeftNormal(), left);
                result = Math.min(result, getLeftChecked());
                return result;
            }

            @Override
            public void onViewDragStateChanged(int state)
            {
                super.onViewDragStateChanged(state);
                if (state == ViewDragHelper.STATE_IDLE)
                {
                    if (mIsScrollerStarted)
                    {
                        mIsScrollerStarted = false;
                        updateViewVisibilityByState();
                    }
                }
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy)
            {
                super.onViewPositionChanged(changedView, left, top, dx, dy);

                float percent = getScrollPercent();
                ViewCompat.setAlpha(mViewChecked, percent);
                ViewCompat.setAlpha(mViewNormal, 1 - percent);
                if (mOnViewPositionChangedCallback != null)
                {
                    mOnViewPositionChangedCallback.onViewPositionChanged(SDSwitchButton.this);
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
        int left = mIsChecked ? getLeftChecked() : getLeftNormal();

        if (mViewThumb.getLeft() != left)
        {
            if (mIsDebug)
            {
                Log.i(TAG, "updateViewByState anim:" + anim);
            }

            if (anim)
            {
                mViewDragHelper.smoothSlideViewTo(mViewThumb, left, mViewThumb.getTop());
            } else
            {
                mViewThumb.layout(left, mViewThumb.getTop(), left + mViewThumb.getMeasuredWidth(), mViewThumb.getBottom());
            }
            invalidate();
        }

        if (mViewDragHelper.getViewDragState() == ViewDragHelper.STATE_SETTLING)
        {
            //触发滚动成功，不需要立即更新view的可见状态，动画结束后更新
            mIsScrollerStarted = true;
        } else
        {
            // 立即更新view的可见状态
            updateViewVisibilityByState();
        }
        mViewThumb.setSelected(mIsChecked);

        if (mOnViewPositionChangedCallback != null)
        {
            mOnViewPositionChangedCallback.onViewPositionChanged(SDSwitchButton.this);
        }
    }

    private void updateViewVisibilityByState()
    {
        if (mIsDebug)
        {
            Log.i(TAG, "updateViewVisibilityByState:" + mIsChecked);
        }

        if (mIsChecked)
        {
            showCheckedView(true);
            showNormalView(false);
        } else
        {
            showCheckedView(false);
            showNormalView(true);
        }
    }

    private void showCheckedView(boolean show)
    {
        float alpha = show ? 1.0f : 0f;
        if (ViewCompat.getAlpha(mViewChecked) != alpha)
        {
            ViewCompat.setAlpha(mViewChecked, alpha);
        }
    }

    private void showNormalView(boolean show)
    {
        float alpha = show ? 1.0f : 0f;
        if (ViewCompat.getAlpha(mViewNormal) != alpha)
        {
            ViewCompat.setAlpha(mViewNormal, alpha);
        }
    }

    /**
     * 返回normal状态下手柄view的left值
     *
     * @return
     */
    private int getLeftNormal()
    {
        return getParamsThumbView().leftMargin;
    }

    /**
     * 返回checked状态下手柄view的left值
     *
     * @return
     */
    private int getLeftChecked()
    {
        return getMeasuredWidth() - mViewThumb.getMeasuredWidth() - getParamsThumbView().rightMargin;
    }

    /**
     * 返回手柄view可以移动的宽度大小
     *
     * @return
     */
    private int getAvailableWidth()
    {
        return getLeftChecked() - getLeftNormal();
    }

    /**
     * 返回手柄view滚动的距离
     *
     * @return
     */
    private int getScrollDistance()
    {
        return mViewThumb.getLeft() - getLeftNormal();
    }

    /**
     * 返回手柄view布局参数
     */
    private LayoutParams getParamsThumbView()
    {
        return (LayoutParams) mViewThumb.getLayoutParams();
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        View normal = findViewById(R.id.lib_sb_view_normal);
        if (normal != null)
        {
            removeView(normal);
            setViewNormal(normal);
        }

        View checked = findViewById(R.id.lib_sb_view_checked);
        if (checked != null)
        {
            removeView(checked);
            setViewChecked(checked);
        }

        View thumb = findViewById(R.id.lib_sb_view_thumb);
        if (thumb != null)
        {
            removeView(thumb);
            setViewThumb(thumb);
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
     * @param viewThumb
     */
    private void setViewThumb(View viewThumb)
    {
        if (replaceOldView(mViewThumb, viewThumb))
        {
            mViewThumb = viewThumb;
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
        if (mViewDragHelper.continueSettling(true))
        {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (mTouchHelper.isNeedIntercept())
        {
            return true;
        }

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mViewDragHelper.processTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                if (canPull())
                {
                    mTouchHelper.setNeedIntercept(true);
                    SDTouchHelper.requestDisallowInterceptTouchEvent(this, true);
                }
                break;
        }
        return mTouchHelper.isNeedIntercept();
    }

    private boolean checkMoveParams()
    {
        return mTouchHelper.getDegreeXFrom(SDTouchHelper.EVENT_DOWN) < 40;
    }

    private boolean canPull()
    {
        boolean canPull = checkMoveParams()
                && ((mIsChecked && mTouchHelper.isMoveLeftFrom(SDTouchHelper.EVENT_DOWN))
                || (!mIsChecked && mTouchHelper.isMoveRightFrom(SDTouchHelper.EVENT_DOWN)));

        if (mIsDebug)
        {
            Log.i(TAG, "canPull:" + canPull);
        }

        return canPull;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        mTouchHelper.processTouchEvent(event);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                if (mTouchHelper.isNeedCosume())
                {
                    processMoveEvent(event);
                } else
                {
                    if (mTouchHelper.isNeedIntercept() || canPull())
                    {
                        mTouchHelper.setNeedCosume(true);
                        mTouchHelper.setNeedIntercept(true);
                        SDTouchHelper.requestDisallowInterceptTouchEvent(this, true);
                    } else
                    {
                        mTouchHelper.setNeedCosume(false);
                        mTouchHelper.setNeedIntercept(false);
                        SDTouchHelper.requestDisallowInterceptTouchEvent(this, false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mViewDragHelper.processTouchEvent(event);
                onActionUp(event);

                mTouchHelper.setNeedCosume(false);
                mTouchHelper.setNeedIntercept(false);
                SDTouchHelper.requestDisallowInterceptTouchEvent(this, false);
                break;
            default:
                mViewDragHelper.processTouchEvent(event);
                break;

        }

        return super.onTouchEvent(event) || mTouchHelper.isNeedCosume() || event.getAction() == MotionEvent.ACTION_DOWN;
    }

    private void processMoveEvent(MotionEvent event)
    {
        // 捕获view
        if (mViewDragHelper.getCapturedView() != mViewThumb)
        {
            mViewDragHelper.captureChildView(mViewThumb, SDTouchHelper.getPointerId(event));
        }

        // 处理view的拖动逻辑
        mViewDragHelper.processTouchEvent(event);
    }

    private void onActionUp(MotionEvent event)
    {
        long duration = event.getEventTime() - event.getDownTime();
        if (duration < mClickTimeout
                && mTouchHelper.getDeltaXFrom(SDTouchHelper.EVENT_DOWN) < mTouchSlop
                && mTouchHelper.getDeltaYFrom(SDTouchHelper.EVENT_DOWN) < mTouchSlop)
        {
            toggleChecked(mAttrModel.isNeedToggleAnim(), true);
        } else
        {
            if (mTouchHelper.isNeedCosume())
            {
                final int left = mViewThumb.getLeft();
                final boolean checked = left >= ((getLeftNormal() + getLeftChecked()) / 2);

                boolean updatePosition = false;
                if (checked)
                {
                    if (left != getLeftChecked())
                    {
                        updatePosition = true;
                    }
                } else
                {
                    if (left != getLeftNormal())
                    {
                        updatePosition = true;
                    }
                }

                if (setChecked(checked, true, true))
                {
                    // 更新状态成功，内部会更新view的位置
                } else
                {
                    if (updatePosition)
                    {
                        updateViewByState(true);
                    }
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        if (mIsDebug)
        {
            Log.i(TAG, "onLayout");
        }
        updateViewByState(false);
    }

    //----------ISDSwitchButton implements start----------

    @Override
    public boolean isChecked()
    {
        return mIsChecked;
    }

    @Override
    public boolean setChecked(boolean checked, boolean anim, boolean notifyCallback)
    {
        if (mIsChecked != checked)
        {
            mIsChecked = checked;

            updateViewByState(anim);

            // 通知回调
            if (notifyCallback)
            {
                if (mOnCheckedChangedCallback != null)
                {
                    mOnCheckedChangedCallback.onCheckedChanged(mIsChecked, this);
                }
            }
            return true;
        } else
        {
            return false;
        }
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
    public void setOnViewPositionChangedCallback(OnViewPositionChangedCallback onViewPositionChangedCallback)
    {
        mOnViewPositionChangedCallback = onViewPositionChangedCallback;
    }

    @Override
    public float getScrollPercent()
    {
        return getScrollDistance() / (float) getAvailableWidth();
    }

    @Override
    public View getViewNormal()
    {
        return mViewNormal;
    }

    @Override
    public View getViewChecked()
    {
        return mViewChecked;
    }

    @Override
    public View getViewThumb()
    {
        return mViewThumb;
    }

    //----------ISDSwitchButton implements end----------

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        mViewDragHelper.abort();
    }
}
