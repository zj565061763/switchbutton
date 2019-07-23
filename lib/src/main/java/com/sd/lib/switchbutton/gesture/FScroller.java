package com.sd.lib.switchbutton.gesture;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 滚动帮助类
 */
public class FScroller
{
    private ScrollerApi mScrollerApi;
    /**
     * 最大滚动距离
     */
    private int mMaxScrollDistance;
    /**
     * 最大滚动时长
     */
    private int mMaxScrollDuration = 400;
    /**
     * 最小滚动时长
     */
    private int mMinScrollDuration = 200;

    private int mLastX;
    private int mLastY;
    private boolean mIsFinished = true;

    private Callback mCallback;

    public FScroller(Context context)
    {
        this(context, null);
    }

    public FScroller(Context context, Interpolator interpolator)
    {
        this(new SimpleScrollerApi(context, interpolator));
    }

    public FScroller(ScrollerApi scrollerApi)
    {
        setScrollerApi(scrollerApi);
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
     * 设置回调对象
     *
     * @param callback
     */
    public void setCallback(Callback callback)
    {
        mCallback = callback;
    }

    /**
     * 设置api处理对象
     *
     * @param scrollerApi
     */
    public void setScrollerApi(ScrollerApi scrollerApi)
    {
        if (scrollerApi == null)
            throw new NullPointerException();

        mScrollerApi = scrollerApi;
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

    public final void setFriction(float friction)
    {
        mScrollerApi.setFriction(friction);
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
                onScrollerCompute(mLastX, mLastY, currX, currY);
            }
        }

        mLastX = currX;
        mLastY = currY;

        updateFinished();
        return compute;
    }

    /**
     * 停止滚动
     *
     * @return true-滚动被停止
     */
    public final boolean abortAnimation()
    {
        mScrollerApi.abortAnimation();
        return updateFinished(true);
    }

    private void updateFinished()
    {
        updateFinished(false);
    }

    private boolean updateFinished(boolean isAbort)
    {
        final boolean finish = mScrollerApi.isFinished();
        if (mIsFinished == finish)
            return false;

        mIsFinished = finish;

        if (finish)
            onScrollerFinish(isAbort);
        else
            onScrollerStart();

        return true;
    }

    protected void onScrollerStart()
    {
        if (mCallback != null)
            mCallback.onScrollerStart();
    }

    protected void onScrollerCompute(int lastX, int lastY, int currX, int currY)
    {
        if (mCallback != null)
            mCallback.onScrollerCompute(lastX, lastY, currX, currY);
    }

    protected void onScrollerFinish(boolean isAbort)
    {
        if (mCallback != null)
            mCallback.onScrollerFinish(isAbort);
    }

    /**
     * 计算时长
     *
     * @param dx          x方向移动距离
     * @param dy          y方向移动距离
     * @param distanceMax 最大可以移动距离
     * @param durationMax 最大时长
     * @param durationMin 最小时长
     * @return
     */
    public static int computeDuration(int dx, int dy, int distanceMax, int durationMax, int durationMin)
    {
        durationMax = Math.abs(durationMax);
        durationMin = Math.abs(durationMin);
        distanceMax = Math.abs(distanceMax);

        if (distanceMax == 0)
            return durationMin;

        if (durationMin > durationMax)
            throw new IllegalArgumentException();

        final float distance = (float) Math.sqrt(Math.abs(dx * dx) + Math.abs(dy * dy));
        if (distance == 0)
            return 0;

        final float disPercent = distance / distanceMax;
        final int duration = (int) ((disPercent * durationMin) + durationMin);

        return Math.min(duration, durationMax);
    }

    public interface Callback
    {
        /**
         * 开始回调
         */
        void onScrollerStart();

        /**
         * 调用{@link FScroller#computeScrollOffset()}方法后回调
         *
         * @param lastX
         * @param lastY
         * @param currX
         * @param currY
         */
        void onScrollerCompute(int lastX, int lastY, int currX, int currY);

        /**
         * 结束回调
         *
         * @param isAbort
         */
        void onScrollerFinish(boolean isAbort);
    }

    public interface ScrollerApi
    {
        void setFriction(float friction);

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

        public SimpleScrollerApi(Context context, Interpolator interpolator)
        {
            mScroller = new Scroller(context, interpolator);
        }

        @Override
        public void setFriction(float friction)
        {
            mScroller.setFriction(friction);
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
