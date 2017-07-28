package com.fanwe.library.switchbutton;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

/**
 * 触摸事件处理帮助类
 */
class SDTouchHelper
{
    private static final String TAG = "SDTouchHelper";

    private boolean mDebug;

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

    private float mDistanceDownX;
    private float mDistanceDownY;

    private float mDistanceMoveX;
    private float mDistanceMoveY;

    private double mDegreeX;
    private double mDegreeY;

    public void setDebug(boolean debug)
    {
        mDebug = debug;
    }

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

                mDistanceDownX = mMoveX - mDownX;
                mDistanceDownY = mMoveY - mDownY;

                mDistanceMoveX = mMoveX - mLastMoveX;
                mDistanceMoveY = mMoveY - mLastMoveY;

                if (mDistanceDownX != 0)
                {
                    final float angleX = Math.abs(mDistanceDownY) / Math.abs(mDistanceDownX);
                    mDegreeX = Math.toDegrees(Math.atan(angleX));
                } else
                {
                    mDegreeX = 0;
                }

                if (mDistanceDownY != 0)
                {
                    final float angleY = Math.abs(mDistanceDownX) / Math.abs(mDistanceDownY);
                    mDegreeY = Math.toDegrees(Math.atan(angleY));
                } else
                {
                    mDegreeY = 0;
                }

                mLastMoveX = mMoveX;
                mLastMoveY = mMoveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }

        if (mDebug)
        {
            Log.i(TAG, "event " + ev.getAction() + ":" + toString());
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

    public float getDownX()
    {
        return mDownX;
    }

    public float getDownY()
    {
        return mDownY;
    }

    public float getMoveX()
    {
        return mMoveX;
    }

    public float getMoveY()
    {
        return mMoveY;
    }

    public float getDistanceX(boolean baseDown)
    {
        if (baseDown)
        {
            return mDistanceDownX;
        } else
        {
            return mDistanceMoveX;
        }
    }

    public float getDistanceY(boolean baseDown)
    {
        if (baseDown)
        {
            return mDistanceDownY;
        } else
        {
            return mDistanceMoveY;
        }
    }

    public boolean isMoveLeft(boolean baseDown)
    {
        return getDistanceX(baseDown) < 0;
    }

    public boolean isMoveRight(boolean baseDown)
    {
        return getDistanceX(baseDown) > 0;
    }

    public boolean isMoveUp(boolean baseDown)
    {
        return getDistanceY(baseDown) < 0;
    }

    public boolean isMoveDown(boolean baseDown)
    {
        return getDistanceY(baseDown) > 0;
    }

    /**
     * 当前ACTION_MOVE的和ACTION_DOWN的x方向夹角
     *
     * @return [0-90]度
     */
    public double getDegreeX()
    {
        return mDegreeX;
    }

    /**
     * 当前ACTION_MOVE的和ACTION_DOWN的y方向夹角
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
        sb.append("mDownX:").append(mDownX).append("\r\n")
                .append("mDownY:").append(mDownY).append("\r\n")
                .append("mMoveX:").append(mMoveX).append("\r\n")
                .append("mMoveY:").append(mMoveY).append("\r\n").append("\r\n")
                .append("mDistanceDownX:").append(mDistanceDownX).append("\r\n")
                .append("mDistanceDownY:").append(mDistanceDownY).append("\r\n")
                .append("mDistanceMoveX:").append(mDistanceMoveX).append("\r\n")
                .append("mDistanceMoveY:").append(mDistanceMoveY).append("\r\n").append("\r\n")
                .append("mDegreeX:").append(mDegreeX).append("\r\n")
                .append("mDegreeY:").append(mDegreeY).append("\r\n");
        return sb.toString();
    }
}
