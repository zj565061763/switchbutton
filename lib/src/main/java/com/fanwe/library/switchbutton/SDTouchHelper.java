package com.fanwe.library.switchbutton;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

/**
 * 触摸事件处理帮助类<br>
 * 关于baseDown的解释：<br>
 * true：最后一次ACTION_MOVE和最后一次ACTION_DOWN的逻辑关系<br>
 * false：最后一次ACTION_MOVE和最后第二次ACTION_MOVE的逻辑关系<br>
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
            StringBuilder sb = getDebugParams()
                    .append("DegreeX down:").append(getDegreeX(true)).append("\r\n")
                    .append("DegreeY down:").append(getDegreeY(true)).append("\r\n")
                    .append("DegreeX move:").append(getDegreeX(false)).append("\r\n")
                    .append("DegreeY move:").append(getDegreeY(false)).append("\r\n");

            Log.i(TAG, "event " + ev.getAction() + ":" + sb.toString());
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
     * 返回x坐标之间的距离<br>
     *
     * @param baseDown
     * @return
     */
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

    /**
     * 返回y坐标之间的距离<br>
     *
     * @param baseDown
     * @return
     */
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

    /**
     * 手指是否向左移动
     *
     * @param baseDown
     * @return
     */
    public boolean isMoveLeft(boolean baseDown)
    {
        return getDistanceX(baseDown) < 0;
    }

    /**
     * 手指是否向右移动
     *
     * @param baseDown
     * @return
     */
    public boolean isMoveRight(boolean baseDown)
    {
        return getDistanceX(baseDown) > 0;
    }

    /**
     * 手指是否向上移动
     *
     * @param baseDown
     * @return
     */
    public boolean isMoveUp(boolean baseDown)
    {
        return getDistanceY(baseDown) < 0;
    }

    /**
     * 手指是否向下移动
     *
     * @param baseDown
     * @return
     */
    public boolean isMoveDown(boolean baseDown)
    {
        return getDistanceY(baseDown) > 0;
    }

    /**
     * 获得x方向的手指移动夹角
     *
     * @return [0-90]度
     */
    public double getDegreeX(boolean baseDown)
    {
        double result = 0;

        float distanceX = getDistanceX(baseDown);
        if (distanceX != 0)
        {
            float angle = Math.abs(getDistanceY(baseDown)) / Math.abs(distanceX);
            result = Math.toDegrees(Math.atan(angle));
        }

        return result;
    }

    /**
     * 获得y方向的手指移动夹角
     *
     * @return [0-90]度
     */
    public double getDegreeY(boolean baseDown)
    {
        double result = 0;

        float distanceY = getDistanceY(baseDown);
        if (distanceY != 0)
        {
            float angle = Math.abs(getDistanceX(baseDown)) / Math.abs(distanceY);
            result = Math.toDegrees(Math.atan(angle));
        }

        return result;
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

    public StringBuilder getDebugParams()
    {
        StringBuilder sb = new StringBuilder("\r\n");
        sb.append("DownX:").append(mDownX).append("\r\n")
                .append("DownY:").append(mDownY).append("\r\n")
                .append("MoveX:").append(mMoveX).append("\r\n")
                .append("MoveY:").append(mMoveY).append("\r\n").append("\r\n")
                .append("DistanceDownX:").append(mDistanceDownX).append("\r\n")
                .append("DistanceDownY:").append(mDistanceDownY).append("\r\n")
                .append("DistanceMoveX:").append(mDistanceMoveX).append("\r\n")
                .append("DistanceMoveY:").append(mDistanceMoveY).append("\r\n").append("\r\n");
        return sb;
    }
}
