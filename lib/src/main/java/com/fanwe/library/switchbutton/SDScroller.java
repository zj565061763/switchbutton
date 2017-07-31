package com.fanwe.library.switchbutton;

import android.content.Context;
import android.widget.Scroller;

class SDScroller extends Scroller
{
    /**
     * 默认最小滚动时长
     */
    public static final int MIN_SCROLL_DURATION = 256;
    /**
     * 默认最大滚动时长
     */
    public static final int MAX_SCROLL_DURATION = 600;

    /**
     * 最大滚动距离
     */
    private int mMaxScrollDistance;
    /**
     * 最大滚动时长
     */
    private int mMaxScrollDuration = MAX_SCROLL_DURATION;
    /**
     * 最小滚动时长
     */
    private int mMinScrollDuration = MIN_SCROLL_DURATION;

    private int mLastX;
    private int mLastY;

    /**
     * 两次computeScrollOffset()之间x移动的距离
     */
    private int mDistanceMoveX;
    /**
     * 两次computeScrollOffset()之间y移动的距离
     */
    private int mDistanceMoveY;

    public SDScroller(Context context)
    {
        super(context);
    }

    /**
     * 设置最大滚动距离
     *
     * @param maxScrollDistance
     */
    public void setMaxScrollDistance(int maxScrollDistance)
    {
        mMaxScrollDistance = maxScrollDistance;
    }

    /**
     * 设置最大滚动时长
     *
     * @param maxScrollDuration
     */
    public void setMaxScrollDuration(int maxScrollDuration)
    {
        mMaxScrollDuration = maxScrollDuration;
    }

    /**
     * 设置最小滚动时长
     *
     * @param minScrollDuration
     */
    public void setMinScrollDuration(int minScrollDuration)
    {
        mMinScrollDuration = minScrollDuration;
    }

    // scroll
    public boolean startScrollX(int startX, int dx, int duration)
    {
        return startScrollExtend(startX, 0, dx, 0, duration);
    }

    public boolean startScrollY(int startY, int dy, int duration)
    {
        return startScrollExtend(0, startY, 0, dy, duration);
    }

    // scrollTo
    public boolean startScrollToX(int startX, int endX, int duration)
    {
        return startScrollTo(startX, 0, endX, 0, duration);
    }

    public boolean startScrollToY(int startY, int endY, int duration)
    {
        return startScrollTo(0, startY, 0, endY, duration);
    }

    public boolean startScrollTo(int startX, int startY, int endX, int endY, int duration)
    {
        int dx = endX - startX;
        int dy = endY - startY;

        return startScrollExtend(startX, startY, dx, dy, duration);
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
    public boolean startScrollExtend(int startX, int startY, int dx, int dy, int duration)
    {
        if (dx == 0 && dy == 0)
        {
            return false;
        } else
        {
            startScroll(startX, startY, dx, dy, duration);
            return true;
        }
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration)
    {
        //最终调用的方法

        mLastX = startX;
        mLastY = startY;

        if (duration < 0)
        {
            duration = getDuration(dx, dy);
        }

        super.startScroll(startX, startY, dx, dy, duration);
    }

    @Override
    public boolean computeScrollOffset()
    {
        boolean result = super.computeScrollOffset();

        int currX = getCurrX();
        int currY = getCurrY();

        mDistanceMoveX = currX - mLastX;
        mDistanceMoveY = currY - mLastY;

        mLastX = currX;
        mLastY = currY;
        return result;
    }

    /**
     * 两次computeScrollOffset()之间x移动的距离
     *
     * @return
     */
    public int getDistanceMoveX()
    {
        return mDistanceMoveX;
    }

    /**
     * 两次computeScrollOffset()之间y移动的距离
     *
     * @return
     */
    public int getDistanceMoveY()
    {
        return mDistanceMoveY;
    }

    /**
     * 返回根据滚动距离和滚动速度算出的滚动时长
     *
     * @param dx x滚动距离
     * @param dy y滚动距离
     * @return
     */
    public int getDuration(int dx, int dy)
    {
        int duration = computeDuration(dx, dy, mMaxScrollDistance, mMaxScrollDuration, mMinScrollDuration);
        return duration;
    }

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
        {
            return minDuration;
        }

        float distance = (float) Math.sqrt(Math.abs(dx * dx) + Math.abs(dy * dy));
        float disPercent = distance / maxDistance;
        int duration = (int) ((disPercent + 1) * minDuration);
        return Math.min(duration, maxDuration);
    }
}
