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

    private FGestureManager getGestureManager()
    {
        if (mGestureManager == null)
        {
            mGestureManager = new FGestureManager(new FGestureManager.Callback()
            {
                @Override
                public boolean shouldInterceptTouchEvent(MotionEvent event)
                {
                    final boolean canPull = canPull();
                    if (mIsDebug)
                    {
                        Log.i(getDebugTag(), "shouldInterceptTouchEvent:" + canPull);
                    }
                    return canPull;
                }

                @Override
                public void onTagInterceptChanged(boolean tagIntercept)
                {
                    FTouchHelper.requestDisallowInterceptTouchEvent(FSwitchButton.this, tagIntercept);
                }

                @Override
                public boolean shouldConsumeTouchEvent(MotionEvent event)
                {
                    final boolean canPull = canPull();
                    if (mIsDebug)
                    {
                        Log.i(getDebugTag(), "shouldConsumeTouchEvent:" + canPull);
                    }
                    return canPull;
                }

                @Override
                public void onTagConsumeChanged(boolean tagConsume)
                {
                    FTouchHelper.requestDisallowInterceptTouchEvent(FSwitchButton.this, tagConsume);
                }

                @Override
                public boolean onConsumeEvent(MotionEvent event)
                {
                    final int dx = (int) getGestureManager().getTouchHelper().getDeltaXFrom(FTouchHelper.EVENT_LAST);
                    moveView(dx);
                    return true;
                }

                @Override
                public void onConsumeEventFinish(MotionEvent event, VelocityTracker velocityTracker)
                {
                    if (getGestureManager().hasConsumeEvent())
                    {
                        if (mIsDebug)
                        {
                            Log.e(getDebugTag(), "onConsumeEventFinish");
                        }

                        final boolean checked = getViewThumb().getLeft() >= (getAvailableWidth() / 2);

                        if (setChecked(checked, true, true))
                        {
                            // 更新状态成功，内部会更新view的位置
                        } else
                        {
                            updateViewByState(true);
                        }
                    } else
                    {
                        if (getGestureManager().isClick(event, getContext()))
                        {
                            toggleChecked(mAttrModel.isNeedToggleAnim(), true);
                        }
                    }
                }
            });
        }
        return mGestureManager;
    }

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
                        {
                            Log.e(getDebugTag(), "onScroll finished:" + getViewThumb().getLeft());
                        }
                        dealViewIdle();
                    }
                }

                @Override
                public void onScroll(int dx, int dy)
                {
                    moveView(dx);
                    if (mIsDebug)
                    {
                        Log.i(getDebugTag(), "onScroll:" + getViewThumb().getLeft());
                    }
                }
            });
        }
        return mScroller;
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
        final boolean checkDegreeX = getGestureManager().getTouchHelper().getDegreeXFrom(FTouchHelper.EVENT_DOWN) < 30;
        final boolean checkMoveLeft = isChecked() && getGestureManager().getTouchHelper().isMoveLeftFrom(FTouchHelper.EVENT_DOWN);
        final boolean checkMoveRight = !isChecked() && getGestureManager().getTouchHelper().isMoveRightFrom(FTouchHelper.EVENT_DOWN);

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
        {
            invalidate();
        }
    }
}
