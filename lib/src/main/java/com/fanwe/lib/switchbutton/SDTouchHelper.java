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

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

/**
 * 触摸事件处理帮助类<br>
 */
class SDTouchHelper
{
    private static final String TAG = "SDTouchHelper";

    private boolean mDebug;

    /**
     * 最后一次ACTION_DOWN事件
     */
    public static final int EVENT_DOWN = 0;
    /**
     * 当前事件的上一次事件
     */
    public static final int EVENT_LAST = 1;

    /**
     * onInterceptTouchEvent方法是否需要拦截事件
     */
    private boolean mIsNeedIntercept = false;
    /**
     * onTouchEvent方法是否需要消费事件
     */
    private boolean mIsNeedCosume = false;

    private float mCurrentX;
    private float mCurrentY;
    private float mLastX;
    private float mLastY;

    private float mDownX;
    private float mDownY;

    private float mMoveX;
    private float mMoveY;

    private float mUpX;
    private float mUpY;

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
        mLastX = mCurrentX;
        mLastY = mCurrentY;

        mCurrentX = ev.getRawX();
        mCurrentY = ev.getRawY();

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mDownX = mCurrentX;
                mDownY = mCurrentY;
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = mCurrentX;
                mMoveY = mCurrentY;
                break;
            case MotionEvent.ACTION_UP:
                mUpX = mCurrentX;
                mUpY = mCurrentY;
                break;
            default:
                break;
        }

        if (mDebug)
        {
            StringBuilder sb = getDebugInfo();
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

    public float getUpX()
    {
        return mUpX;
    }

    public float getUpY()
    {
        return mUpY;
    }

    /**
     * 返回当前事件和指定事件之间的x轴方向增量
     *
     * @param event {@link #EVENT_DOWN} {@link #EVENT_LAST}
     * @return
     */
    public float getDeltaXFrom(int event)
    {
        switch (event)
        {
            case EVENT_DOWN:
                return mCurrentX - mDownX;
            case EVENT_LAST:
                return mCurrentX - mLastX;
            default:
                return 0;
        }
    }

    /**
     * 返回当前事件和指定事件之间的y轴方向增量
     *
     * @param event {@link #EVENT_DOWN} {@link #EVENT_LAST}
     * @return
     */
    public float getDeltaYFrom(int event)
    {
        switch (event)
        {
            case EVENT_DOWN:
                return mCurrentY - mDownY;
            case EVENT_LAST:
                return mCurrentY - mLastY;
            default:
                return 0;
        }
    }

    /**
     * 返回当前事件和指定事件之间的x轴方向夹角
     *
     * @param event {@link #EVENT_DOWN} {@link #EVENT_LAST}
     * @return
     */
    public double getDegreeXFrom(int event)
    {
        float dx = getDeltaXFrom(event);
        if (dx == 0)
        {
            return 0;
        }
        float dy = getDeltaYFrom(event);

        float angle = Math.abs(dy) / Math.abs(dx);
        return Math.toDegrees(Math.atan(angle));
    }

    /**
     * 返回当前事件和指定事件之间的y轴方向夹角
     *
     * @param event {@link #EVENT_DOWN} {@link #EVENT_LAST}
     * @return
     */
    public double getDegreeYFrom(int event)
    {
        float dy = getDeltaYFrom(event);
        if (dy == 0)
        {
            return 0;
        }
        float dx = getDeltaXFrom(event);

        float angle = Math.abs(dx) / Math.abs(dy);
        return Math.toDegrees(Math.atan(angle));
    }

    /**
     * 返回当前事件相对于指定事件是否向左移动
     *
     * @param event {@link #EVENT_DOWN} {@link #EVENT_LAST}
     * @return
     */
    public boolean isMoveLeftFrom(int event)
    {
        return getDeltaXFrom(event) < 0;
    }

    /**
     * 返回当前事件相对于指定事件是否向右移动
     *
     * @param event {@link #EVENT_DOWN} {@link #EVENT_LAST}
     * @return
     */
    public boolean isMoveRightFrom(int event)
    {
        return getDeltaXFrom(event) > 0;
    }

    /**
     * 返回当前事件相对于指定事件是否向上移动
     *
     * @param event {@link #EVENT_DOWN} {@link #EVENT_LAST}
     * @return
     */
    public boolean isMoveUpFrom(int event)
    {
        return getDeltaYFrom(event) < 0;
    }

    /**
     * 返回当前事件相对于指定事件是否向下移动
     *
     * @param event {@link #EVENT_DOWN} {@link #EVENT_LAST}
     * @return
     */
    public boolean isMoveDownFrom(int event)
    {
        return getDeltaYFrom(event) > 0;
    }

    /**
     * 根据条件返回合法的x方向增量
     *
     * @param view    要处理的view
     * @param minLeft view的最小left
     * @param maxLeft view的最大left
     * @param dx      x方向将要叠加的增量
     * @return
     */
    public int getLegalDeltaX(View view, int minLeft, int maxLeft, int dx)
    {
        int future = view.getLeft() + dx;
        if (isMoveLeftFrom(EVENT_LAST))
        {
            //如果向左拖动
            if (future < minLeft)
            {
                int comsume = minLeft - future;
                dx += comsume;
            }
        } else if (isMoveRightFrom(EVENT_LAST))
        {
            //如果向右拖动
            if (future > maxLeft)
            {
                int comsume = future - maxLeft;
                dx -= comsume;
            }
        }
        return dx;
    }

    /**
     * 根据条件返回合法的y方向增量
     *
     * @param view   要处理的view
     * @param minTop view的最小top
     * @param maxTop view的最大top
     * @param dy     y方向将要叠加的增量
     * @return
     */
    public int getLegalDeltaY(View view, int minTop, int maxTop, int dy)
    {
        int future = view.getTop() + dy;
        if (isMoveUpFrom(EVENT_LAST))
        {
            //如果向上拖动
            if (future < minTop)
            {
                int comsume = minTop - future;
                dy += comsume;
            }
        } else if (isMoveDownFrom(EVENT_LAST))
        {
            //如果向下拖动
            if (future > maxTop)
            {
                int comsume = future - maxTop;
                dy -= comsume;
            }
        }
        return dy;
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

    public StringBuilder getDebugInfo()
    {
        StringBuilder sb = new StringBuilder("\r\n")
                .append("DownX:").append(mDownX).append("\r\n")
                .append("DownY:").append(mDownY).append("\r\n")
                .append("MoveX:").append(mMoveX).append("\r\n")
                .append("MoveY:").append(mMoveY).append("\r\n").append("\r\n")

                .append("DeltaX from down:").append(getDeltaXFrom(EVENT_DOWN)).append("\r\n")
                .append("DeltaY from down:").append(getDeltaYFrom(EVENT_DOWN)).append("\r\n")
                .append("DeltaX from last:").append(getDeltaXFrom(EVENT_LAST)).append("\r\n")
                .append("DeltaY from last:").append(getDeltaYFrom(EVENT_LAST)).append("\r\n").append("\r\n")

                .append("DegreeX from down:").append(getDegreeXFrom(EVENT_DOWN)).append("\r\n")
                .append("DegreeY from down:").append(getDegreeYFrom(EVENT_DOWN)).append("\r\n")
                .append("DegreeX from last:").append(getDegreeXFrom(EVENT_LAST)).append("\r\n")
                .append("DegreeY from last:").append(getDegreeYFrom(EVENT_LAST)).append("\r\n");
        return sb;
    }
}
