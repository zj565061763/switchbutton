package com.fanwe.library.switchbutton;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

/**
 * 触摸事件处理帮助类
 */
class SDTouchHelper
{
    /**
     * onInterceptTouchEvent方法是否需要拦截事件
     */
    private boolean mIsNeedIntercept = false;
    /**
     * onTouchEvent方法是否需要消费事件
     */
    private boolean mIsNeedCosume = false;

    private float mDownX;
    private float mDownY;

    private float mMoveX;
    private float mMoveY;
    private float mLastMoveX;
    private float mLastMoveY;

    private float mDistanceMoveX;
    private float mDistanceMoveY;

    private float mDistanceDownX;
    private float mDistanceDownY;

    private double mDegreeX;
    private double mDegreeY;

    /**
     * 处理触摸事件
     *
     * @param ev
     */
    public void processTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();

                mLastMoveX = mDownX;
                mLastMoveY = mDownY;
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = ev.getRawX();
                mMoveY = ev.getRawY();

                mDistanceMoveX = mMoveX - mLastMoveX;
                mDistanceMoveY = mMoveY - mLastMoveY;

                mDistanceDownX = mMoveX - mDownX;
                mDistanceDownY = mMoveY - mDownY;

                final float angleX = Math.abs(mDistanceMoveY) / Math.abs(mDistanceMoveX);
                mDegreeX = Math.toDegrees(Math.atan(angleX));

                final float angleY = Math.abs(mDistanceMoveX) / Math.abs(mDistanceMoveY);
                mDegreeY = Math.toDegrees(Math.atan(angleY));

                mLastMoveX = mMoveX;
                mLastMoveY = mMoveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
    }

    /**
     * 设置onInterceptTouchEvent方法是否需要拦截事件
     *
     * @param needIntercept
     */
    public void setNeedIntercept(boolean needIntercept)
    {
        mIsNeedIntercept = needIntercept;
    }

    /**
     * onInterceptTouchEvent方法是否需要拦截事件
     *
     * @return
     */
    public boolean isNeedIntercept()
    {
        return mIsNeedIntercept;
    }

    /**
     * 设置onTouchEvent方法是否需要消费事件
     *
     * @param needCosume
     */
    public void setNeedCosume(boolean needCosume)
    {
        mIsNeedCosume = needCosume;
    }

    /**
     * onTouchEvent方法是否需要消费事件
     *
     * @return
     */
    public boolean isNeedCosume()
    {
        return mIsNeedCosume;
    }

    /**
     * 最后一次ACTION_DOWN的x坐标
     *
     * @return
     */
    public float getDownX()
    {
        return mDownX;
    }

    /**
     * 最后一次ACTION_DOWN的y坐标
     *
     * @return
     */
    public float getDownY()
    {
        return mDownY;
    }

    /**
     * 最后一次ACTION_MOVE的x坐标
     *
     * @return
     */
    public float getMoveX()
    {
        return mMoveX;
    }

    /**
     * 最后一次ACTION_MOVE的y坐标
     *
     * @return
     */
    public float getMoveY()
    {
        return mMoveY;
    }

    /**
     * 最后两次ACTION_MOVE的x距离<br>
     * 大于0：往右移动<br>
     * 小于0：往左移动<br>
     *
     * @return
     */
    public float getDistanceMoveX()
    {
        return mDistanceMoveX;
    }

    /**
     * 最后两次ACTION_MOVE的y距离<br>
     * 大于0：往下移动<br>
     * 小于0：往上移动<br>
     *
     * @return
     */
    public float getDistanceMoveY()
    {
        return mDistanceMoveY;
    }

    /**
     * 当前ACTION_MOVE的x和ACTION_DOWN的x的距离
     *
     * @return
     */
    public float getDistanceDownX()
    {
        return mDistanceDownX;
    }

    /**
     * 当前ACTION_MOVE的y和ACTION_DOWN的y的距离
     *
     * @return
     */
    public float getDistanceDownY()
    {
        return mDistanceDownY;
    }

    /**
     * 最后两次ACTION_MOVE方向向左
     *
     * @return
     */
    public boolean isMoveLeft()
    {
        return getDistanceMoveX() < 0;
    }

    /**
     * 最后两次ACTION_MOVE方向向左
     *
     * @return
     */
    public boolean isMoveRight()
    {
        return getDistanceMoveX() > 0;
    }

    /**
     * 最后两次ACTION_MOVE方向向上
     *
     * @return
     */
    public boolean isMoveUp()
    {
        return getDistanceMoveY() < 0;
    }

    /**
     * 最后两次ACTION_MOVE方向向下
     *
     * @return
     */
    public boolean isMoveDown()
    {
        return getDistanceMoveY() > 0;
    }

    /**
     * 最后两次ACTION_MOVE相对x方向的移动夹角
     *
     * @return [0-90]度
     */
    public double getDegreeX()
    {
        return mDegreeX;
    }

    /**
     * 最后两次ACTION_MOVE相对y方向的移动夹角
     *
     * @return [0-90]度
     */
    public double getDegreeY()
    {
        return mDegreeY;
    }

    //----------static method start----------

    /**
     * 是否请求当前view的父view不要拦截事件
     *
     * @param view
     * @param disallowIntercept true-请求父view不要拦截，false-父view可以拦截
     */
    public static void requestDisallowInterceptTouchEvent(View view, boolean disallowIntercept)
    {
        ViewParent parent = view.getParent();
        if (parent == null)
        {
            return;
        }
        parent.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /**
     * view是否已经滚动到最顶部
     *
     * @param view
     * @return
     */
    public static boolean isScrollToTop(View view)
    {
        return !ViewCompat.canScrollVertically(view, -1);
    }

    /**
     * view是否已经滚动到最底部
     *
     * @param view
     * @return
     */
    public static boolean isScrollToBottom(View view)
    {
        return !ViewCompat.canScrollVertically(view, 1);
    }

    /**
     * view是否已经滚动到最左边
     *
     * @param view
     * @return
     */
    public static boolean isScrollToLeft(View view)
    {
        return !ViewCompat.canScrollHorizontally(view, -1);
    }

    /**
     * view是否已经滚动到最右边
     *
     * @param view
     * @return
     */
    public static boolean isScrollToRight(View view)
    {
        return !ViewCompat.canScrollHorizontally(view, -1);
    }

    /**
     * 返回MotionEvent的PointerId
     *
     * @param event
     * @return
     */
    public static int getPointerId(MotionEvent event)
    {
        return event.getPointerId(MotionEventCompat.getActionIndex(event));
    }

    //----------static method end----------

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("\r\n");
        sb.append("mDistanceMoveX:").append(mDistanceMoveX).append("\r\n")
                .append("mDistanceMoveY:").append(mDistanceMoveY).append("\r\n")
                .append("mDegreeX:").append(mDegreeX).append("\r\n")
                .append("mDegreeY:").append(mDegreeY).append("\r\n");
        return sb.toString();
    }
}
