/*
 * Copyright (C) 2017 Sunday (https://github.com/zj565061763)
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
package com.sd.lib.switchbutton.gesture;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 滚动帮助类
 */
public abstract class FScroller
{
    private final ScrollerApi mScrollerApi;
    /**
     * 最大滚动距离
     */
    private int mMaxScrollDistance;
    /**
     * 最大滚动时长
     */
    private int mMaxScrollDuration = 600;
    /**
     * 最小滚动时长
     */
    private int mMinScrollDuration = 256;

    private int mLastX;
    private int mLastY;
    private boolean mIsFinished = true;

    public FScroller(Context context)
    {
        this(new SimpleScrollerApi(context));
    }

    public FScroller(Context context, Interpolator interpolator)
    {
        this(new SimpleScrollerApi(context, interpolator));
    }

    public FScroller(ScrollerApi scrollerApi)
    {
        if (scrollerApi == null)
            throw new NullPointerException();

        mScrollerApi = scrollerApi;
    }

    /**
     * 返回滚动是否结束
     *
     * @return true-滚动结束，false-滚动中
     */
    public final boolean isFinished()
    {
        return mIsFinished;
    }

    /**
     * 设置最大滚动距离
     *
     * @param distance
     */
    public final void setMaxScrollDistance(int distance)
    {
        mMaxScrollDistance = distance;
    }

    /**
     * 设置最大滚动时长
     *
     * @param duration
     */
    public final void setMaxScrollDuration(int duration)
    {
        mMaxScrollDuration = duration;
    }

    /**
     * 设置最小滚动时长
     *
     * @param duration
     */
    public final void setMinScrollDuration(int duration)
    {
        mMinScrollDuration = duration;
    }

    // scrollTo
    public final boolean scrollToX(int startX, int endX, int duration)
    {
        return scrollTo(startX, 0, endX, 0, duration);
    }

    public final boolean scrollToY(int startY, int endY, int duration)
    {
        return scrollTo(0, startY, 0, endY, duration);
    }

    public final boolean scrollTo(int startX, int startY, int endX, int endY, int duration)
    {
        final int dx = endX - startX;
        final int dy = endY - startY;

        return scrollDelta(startX, startY, dx, dy, duration);
    }

    // scrollDelta
    public final boolean scrollDeltaX(int startX, int dx, int duration)
    {
        return scrollDelta(startX, 0, dx, 0, duration);
    }

    public final boolean scrollDeltaY(int startY, int dy, int duration)
    {
        return scrollDelta(0, startY, 0, dy, duration);
    }

    /**
     * 所有此类的滚动扩展方法最终需要调用的方法
     *
     * @param startX
     * @param startY
     * @param dx
     * @param dy
     * @param duration
     * @return true-提交滚动任务成功
     */
    public final boolean scrollDelta(int startX, int startY, int dx, int dy, int duration)
    {
        final boolean scroll = dx != 0 || dy != 0;

        if (scroll)
        {
            mLastX = startX;
            mLastY = startY;

            if (duration < 0)
                duration = computeDuration(dx, dy, mMaxScrollDistance, mMaxScrollDuration, mMinScrollDuration);

            mScrollerApi.startScroll(startX, startY, dx, dy, duration);
            updateFinished();
        }
        return scroll;
    }

    public final boolean flingX(int startX, int velocityX, int minX, int maxX)
    {
        return fling(startX, 0, velocityX, 0, minX, maxX, 0, 0);
    }

    public final boolean flingY(int startY, int velocityY, int minY, int maxY)
    {
        return fling(0, startY, 0, velocityY, 0, 0, minY, maxY);
    }

    public final boolean fling(int startX, int startY,
                               int velocityX, int velocityY,
                               int minX, int maxX,
                               int minY, int maxY)
    {
        final boolean fling = (startX > minX && startX < maxX && velocityX != 0)
                || (startY > minY && startY < maxY && velocityY != 0);

        if (fling)
        {
            mLastX = startX;
            mLastY = startY;

            mScrollerApi.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            updateFinished();
        }
        return fling;
    }

    /**
     * 计算滚动距离
     *
     * @return true-滚动中，false-滚动结束
     */
    public final boolean computeScrollOffset()
    {
        final boolean compute = mScrollerApi.computeScrollOffset();

        final int currX = mScrollerApi.getCurrX();
        final int currY = mScrollerApi.getCurrY();

        if (compute)
        {
            if (currX != mLastX || currY != mLastY)
            {
                onScrollCompute(mLastX, mLastY, currX, currY);
            }
        }

        mLastX = currX;
        mLastY = currY;

        updateFinished();
        return compute;
    }

    /**
     * 停止滚动
     */
    public final void abortAnimation()
    {
        mScrollerApi.abortAnimation();
        updateFinished(true);
    }

    private void updateFinished()
    {
        updateFinished(false);
    }

    private void updateFinished(boolean isAbort)
    {
        final boolean finish = mScrollerApi.isFinished();
        if (mIsFinished != finish)
        {
            mIsFinished = finish;

            if (finish)
                onScrollFinish(isAbort);
            else
                onScrollStart();
        }
    }

    /**
     * 滚动开始回调
     */
    protected abstract void onScrollStart();

    /**
     * 调用{@link FScroller#computeScrollOffset()}回调
     *
     * @param lastX 上一次的x
     * @param lastY 上一次的y
     * @param currX 当前x
     * @param currY 当前y
     */
    protected abstract void onScrollCompute(int lastX, int lastY, int currX, int currY);

    /**
     * 滚动结束回调
     *
     * @param isAbort
     */
    protected abstract void onScrollFinish(boolean isAbort);

    /**
     * 计算时长
     *
     * @param dx          x方向移动距离
     * @param dy          y方向移动距离
     * @param maxDistance 最大可以移动距离
     * @param maxDuration 最大时长
     * @param minDuration 最小时长
     * @return
     */
    public static int computeDuration(int dx, int dy, int maxDistance, int maxDuration, int minDuration)
    {
        maxDistance = Math.abs(maxDistance);
        if (maxDistance == 0)
            return minDuration;

        final float distance = (float) Math.sqrt(Math.abs(dx * dx) + Math.abs(dy * dy));
        final float disPercent = distance / maxDistance;
        final int duration = (int) ((disPercent * minDuration) + minDuration);
        return Math.min(duration, maxDuration);
    }

    public interface ScrollerApi
    {
        void startScroll(int startX, int startY, int dx, int dy, int duration);

        void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY);

        boolean computeScrollOffset();

        void abortAnimation();

        boolean isFinished();

        int getCurrX();

        int getCurrY();
    }

    private static class SimpleScrollerApi implements FScroller.ScrollerApi
    {
        private final Scroller mScroller;

        public SimpleScrollerApi(Context context)
        {
            mScroller = new Scroller(context);
        }

        public SimpleScrollerApi(Context context, Interpolator interpolator)
        {
            mScroller = new Scroller(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration)
        {
            mScroller.startScroll(startX, startY, dx, dy, duration);
        }

        @Override
        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY)
        {
            mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
        }

        @Override
        public boolean computeScrollOffset()
        {
            return mScroller.computeScrollOffset();
        }

        @Override
        public void abortAnimation()
        {
            mScroller.abortAnimation();
        }

        @Override
        public boolean isFinished()
        {
            return mScroller.isFinished();
        }

        @Override
        public int getCurrX()
        {
            return mScroller.getCurrX();
        }

        @Override
        public int getCurrY()
        {
            return mScroller.getCurrY();
        }
    }
}
