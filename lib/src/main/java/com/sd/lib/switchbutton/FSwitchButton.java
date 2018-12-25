package com.sd.lib.switchbutton;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import com.sd.lib.switchbutton.gesture.FGestureManager;
import com.sd.lib.switchbutton.gesture.FScroller;
import com.sd.lib.switchbutton.gesture.FTouchHelper;

public class FSwitchButton extends BaseSwitchButton
{
    private FGestureManager mGestureManager;
    private FScroller mScroller;

    public FSwitchButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    private FScroller getScroller()
    {
        if (mScroller == null)
        {
            mScroller = new FScroller(getContext())
            {
                @Override
                protected void onScrollStart()
                {

                }

                @Override
                protected void onScrollCompute(int lastX, int lastY, int currX, int currY)
                {
                    if (mIsDebug)
                        Log.i(getDebugTag(), "onScroll:" + getViewThumb().getLeft());

                    final int dx = currX - lastX;
                    moveView(dx);
                }

                @Override
                protected void onScrollFinish(boolean isAbort)
                {
                    if (mIsDebug)
                        Log.e(getDebugTag(), "onScrollFinish isAbort:" + isAbort);

                    if (!isAbort)
                        setIdleIfNeed();
                }
            };
        }
        return mScroller;
    }

    private FGestureManager getGestureManager()
    {
        if (mGestureManager == null)
        {
            mGestureManager = new FGestureManager(new FGestureManager.Callback()
            {
                @Override
                public boolean shouldInterceptEvent(MotionEvent event)
                {
                    boolean shouldInterceptEvent = false;
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        if (FTouchHelper.isViewUnder(getViewThumb(), (int) event.getX(), (int) event.getY()))
                            shouldInterceptEvent = true;
                    } else
                    {
                        shouldInterceptEvent = canPull();
                    }

                    if (mIsDebug)
                        Log.i(getDebugTag(), "shouldInterceptEvent:" + shouldInterceptEvent);

                    return shouldInterceptEvent;
                }

                @Override
                public boolean shouldConsumeEvent(MotionEvent event)
                {
                    final boolean shouldConsumeEvent = canPull();
                    if (mIsDebug)
                        Log.i(getDebugTag(), "shouldConsumeEvent:" + shouldConsumeEvent);

                    return shouldConsumeEvent;
                }

                @Override
                public boolean onEventConsume(MotionEvent event)
                {
                    final int dx = (int) getGestureManager().getTouchHelper().getDeltaX();
                    moveView(dx);
                    return true;
                }

                @Override
                public void onEventFinish(FGestureManager.FinishParams params, VelocityTracker velocityTracker, MotionEvent event)
                {
                    if (params.isCancelTouchEvent)
                        return;

                    if (params.hasConsumeEvent)
                    {
                        velocityTracker.computeCurrentVelocity(1000);
                        final int velocity = (int) velocityTracker.getXVelocity();
                        final int minFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity() * 8;

                        boolean checked = false;
                        if (Math.abs(velocity) > minFlingVelocity)
                        {
                            checked = velocity > 0;
                        } else
                        {
                            final int leftMiddle = (getLeftNormal() + getLeftChecked()) / 2;
                            checked = getViewThumb().getLeft() >= leftMiddle;
                        }

                        if (mIsDebug)
                            Log.e(getDebugTag(), "onConsumeEventFinish checked:" + checked);

                        setChecked(checked, true, true);

                        if (getScrollState() == ScrollState.Drag)
                            setScrollState(ScrollState.Idle);
                    } else
                    {
                        if (getGestureManager().getTouchHelper().isClick(event, getContext()))
                            toggleChecked(mAttrModel.isNeedToggleAnim(), true);
                    }
                }
            });

            mGestureManager.getTagHolder().setCallback(new FGestureManager.FTagHolder.Callback()
            {
                @Override
                public void onTagInterceptChanged(boolean tag)
                {
                    FTouchHelper.requestDisallowInterceptTouchEvent(FSwitchButton.this, tag);
                }

                @Override
                public void onTagConsumeChanged(boolean tag)
                {
                    FTouchHelper.requestDisallowInterceptTouchEvent(FSwitchButton.this, tag);
                    if (tag)
                        setScrollState(ScrollState.Drag);
                }
            });
        }
        return mGestureManager;
    }

    @Override
    public boolean setChecked(boolean checked, boolean anim, boolean notifyCallback)
    {
        getGestureManager().setCancelTouchEvent();
        return super.setChecked(checked, anim, notifyCallback);
    }

    @Override
    protected boolean isViewIdle()
    {
        final boolean checkScrollerFinished = getScroller().isFinished();
        if (!checkScrollerFinished)
            return false;

        final boolean checkNotDragging = !getGestureManager().getTagHolder().isTagConsume();
        if (!checkNotDragging)
            return false;

        return true;
    }

    @Override
    protected void abortAnimation()
    {
        getScroller().abortAnimation();
    }

    @Override
    protected boolean onSmoothSlide(int startLeft, int endLeft)
    {
        return getScroller().scrollToX(startLeft, endLeft, -1);
    }

    private boolean canPull()
    {
        final float deltaX = getGestureManager().getTouchHelper().getDeltaXFromDown();
        if (deltaX == 0)
            return false;

        final boolean checkDegreeX = getGestureManager().getTouchHelper().getDegreeXFromDown() < 30;
        if (!checkDegreeX)
            return false;

        final boolean checkMoveLeft = isChecked() && deltaX < 0;
        final boolean checkMoveRight = !isChecked() && deltaX > 0;

        return checkMoveLeft || checkMoveRight;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        getScroller().setMaxScrollDistance(getAvailableWidth());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return getGestureManager().onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return getGestureManager().onTouchEvent(event);
    }

    @Override
    public void computeScroll()
    {
        super.computeScroll();
        if (getScroller().computeScrollOffset())
            ViewCompat.postInvalidateOnAnimation(this);
    }
}
