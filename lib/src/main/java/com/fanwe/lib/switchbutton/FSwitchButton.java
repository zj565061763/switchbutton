/*
 * Copyright (C) 2017 zhengjun, fanwe (http://www.fanwe.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanwe.lib.switchbutton;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Scroller;

import com.fanwe.lib.gesture.FGestureManager;
import com.fanwe.lib.gesture.FScroller;
import com.fanwe.lib.gesture.FTouchHelper;
import com.fanwe.lib.gesture.tag.TagHolder;

public class FSwitchButton extends BaseSwitchButton
{
    public FSwitchButton(Context context)
    {
        super(context);
    }

    public FSwitchButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FSwitchButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    private FGestureManager mGestureManager;
    private FScroller mScroller;

    private FScroller getScroller()
    {
        if (mScroller == null)
        {
            mScroller = new FScroller(new Scroller(getContext()));
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
                        final boolean checked = getViewThumb().getLeft() >= ((getLeftNormal() + getLeftChecked()) / 2);

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
        final boolean checkMoveLeft = isChecked() && getGestureManager().getTouchHelper().getDeltaXFromDown() < 0;
        final boolean checkMoveRight = !isChecked() && getGestureManager().getTouchHelper().getDeltaXFromDown() > 0;

        return checkDegreeX && (checkMoveLeft || checkMoveRight);
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
            invalidate();
    }
}
