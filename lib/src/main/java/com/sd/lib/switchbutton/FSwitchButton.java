package com.sd.lib.switchbutton;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import com.sd.lib.gesture.FGestureManager;
import com.sd.lib.gesture.FTouchHelper;
import com.sd.lib.gesture.scroller.FScroller;
import com.sd.lib.gesture.scroller.SimpleScrollerApi;
import com.sd.lib.gesture.tag.TagHolder;

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
            mScroller = new FScroller(new SimpleScrollerApi(getContext()));
            mScroller.setCallback(new FScroller.Callback()
            {
                @Override
                public void onScrollStateChanged(boolean isFinished)
                {
                    if (isFinished)
                    {
                        if (mIsDebug)
                            Log.i(getDebugTag(), "onScroll finished:" + getViewThumb().getLeft());

                        dealViewIdle();
                    }
                }

                @Override
                public void onScroll(int lastX, int lastY, int currX, int currY)
                {
                    final int dx = currX - lastX;
                    moveView(dx);
                    if (mIsDebug)
                        Log.i(getDebugTag(), "onScroll:" + getViewThumb().getLeft());
                }
            });
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
                public void onEventFinish(boolean hasConsumeEvent, VelocityTracker velocityTracker, MotionEvent event)
                {
                    if (hasConsumeEvent)
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

                        if (setChecked(checked, true, true))
                        {
                            // 更新状态成功，内部会更新view的位置
                        } else
                        {
                            updateViewByState(true);
                        }
                    } else
                    {
                        if (getGestureManager().getTouchHelper().isClick(event, getContext()))
                            toggleChecked(mAttrModel.isNeedToggleAnim, true);
                    }
                }
            });

            mGestureManager.getTagHolder().setCallback(new TagHolder.Callback()
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
                }
            });
        }
        return mGestureManager;
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
        final boolean checkDegreeX = getGestureManager().getTouchHelper().getDegreeXFromDown() < 30;
        if (!checkDegreeX)
            return false;

        final boolean checkMoveLeft = isChecked() && getGestureManager().getTouchHelper().getDeltaXFromDown() < 0;
        final boolean checkMoveRight = !isChecked() && getGestureManager().getTouchHelper().getDeltaXFromDown() > 0;

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
            postInvalidateOnAnimation();
    }
}
