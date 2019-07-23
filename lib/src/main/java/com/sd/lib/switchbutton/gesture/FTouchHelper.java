package com.sd.lib.switchbutton.gesture;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.LinkedList;
import java.util.List;

/**
 * 触摸事件处理帮助类<br>
 */
public class FTouchHelper
{
    private float mCurrentX;
    private float mCurrentY;

    private float mLastX;
    private float mLastY;

    private float mDownX;
    private float mDownY;

    /**
     * 处理触摸事件
     *
     * @param event
     */
    public void processTouchEvent(MotionEvent event)
    {
        mLastX = mCurrentX;
        mLastY = mCurrentY;

        mCurrentX = event.getRawX();
        mCurrentY = event.getRawY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mDownX = mCurrentX;
                mDownY = mCurrentY;
                break;
            default:
                break;
        }
    }

    public float getCurrentX()
    {
        return mCurrentX;
    }

    public float getCurrentY()
    {
        return mCurrentY;
    }

    public float getLastX()
    {
        return mLastX;
    }

    public float getLastY()
    {
        return mLastY;
    }

    public float getDownX()
    {
        return mDownX;
    }

    public float getDownY()
    {
        return mDownY;
    }

    //---------- Delta Start ----------

    /**
     * 返回当前事件和上一次事件之间的x轴方向增量
     *
     * @return
     */
    public float getDeltaX()
    {
        return mCurrentX - mLastX;
    }

    /**
     * 返回当前事件和上一次事件之间的y轴方向增量
     */
    public float getDeltaY()
    {
        return mCurrentY - mLastY;
    }

    /**
     * 返回当前事件和{@link MotionEvent#ACTION_DOWN}事件之间的x轴方向增量
     *
     * @return
     */
    public float getDeltaXFromDown()
    {
        return mCurrentX - mDownX;
    }

    /**
     * 返回当前事件和{@link MotionEvent#ACTION_DOWN}事件之间的y轴方向增量
     *
     * @return
     */
    public float getDeltaYFromDown()
    {
        return mCurrentY - mDownY;
    }

    //---------- Delta End ----------


    //---------- Degree Start ----------

    /**
     * 返回当前事件和上一次事件之间的x轴方向夹角
     *
     * @return
     */
    public double getDegreeX()
    {
        final float dx = getDeltaX();
        if (dx == 0)
            return 0;

        final float dy = getDeltaY();
        final float angle = Math.abs(dy) / Math.abs(dx);
        return Math.toDegrees(Math.atan(angle));
    }

    /**
     * 返回当前事件和上一次事件之间的y轴方向夹角
     *
     * @return
     */
    public double getDegreeY()
    {
        final float dy = getDeltaY();
        if (dy == 0)
            return 0;

        final float dx = getDeltaX();
        final float angle = Math.abs(dx) / Math.abs(dy);
        return Math.toDegrees(Math.atan(angle));
    }

    /**
     * 返回当前事件和{@link MotionEvent#ACTION_DOWN}事件之间的x轴方向夹角
     *
     * @return
     */
    public double getDegreeXFromDown()
    {
        final float dx = getDeltaXFromDown();
        if (dx == 0)
            return 0;

        final float dy = getDeltaYFromDown();
        final float angle = Math.abs(dy) / Math.abs(dx);
        return Math.toDegrees(Math.atan(angle));
    }

    /**
     * 返回当前事件和{@link MotionEvent#ACTION_DOWN}事件之间的y轴方向夹角
     *
     * @return
     */
    public double getDegreeYFromDown()
    {
        final float dy = getDeltaYFromDown();
        if (dy == 0)
            return 0;

        final float dx = getDeltaXFromDown();
        final float angle = Math.abs(dx) / Math.abs(dy);
        return Math.toDegrees(Math.atan(angle));
    }

    //---------- Degree End ----------

    /**
     * 是否是点击事件
     *
     * @param event
     * @param context
     * @return
     */
    public boolean isClick(MotionEvent event, Context context)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            final long clickTimeout = ViewConfiguration.getPressedStateDuration() + ViewConfiguration.getTapTimeout();
            final int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

            final long duration = event.getEventTime() - event.getDownTime();
            final int dx = (int) getDeltaXFromDown();
            final int dy = (int) getDeltaYFromDown();

            if (duration < clickTimeout && dx < touchSlop && dy < touchSlop)
                return true;
        }
        return false;
    }

    //----------static method start----------

    /**
     * 返回合理的增量
     *
     * @param current 当前值
     * @param min     最小值
     * @param max     最大值
     * @param delta   增量
     * @return
     */
    public static int getLegalDelta(int current, int min, int max, int delta)
    {
        if (delta == 0)
            return 0;

        final int future = current + delta;
        if (future < min)
        {
            delta += (min - future);
        } else if (future > max)
        {
            delta += (max - future);
        }
        return delta;
    }

    /**
     * 是否请求当前view的父view不要拦截事件
     *
     * @param view
     * @param disallowIntercept true-请求父view不要拦截，false-父view可以拦截
     */
    public static void requestDisallowInterceptTouchEvent(View view, boolean disallowIntercept)
    {
        final ViewParent parent = view.getParent();
        if (parent != null)
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /**
     * view是否处于某个坐标点下面，相对父布局的坐标
     *
     * @param view
     * @param x
     * @param y
     * @return
     */
    public static boolean isViewUnder(View view, int x, int y)
    {
        return x >= view.getLeft() && x < view.getRight()
                && y >= view.getTop() && y < view.getBottom();
    }

    /**
     * view是否处于某个坐标点下面，相对屏幕的坐标
     *
     * @param view
     * @param x
     * @param y
     * @return
     */
    public static boolean isViewUnderScreen(View view, int x, int y)
    {
        final int[] location = new int[2];
        view.getLocationOnScreen(location);
        return x >= location[0] && x < location[0] + view.getWidth()
                && y >= location[1] && y < location[1] + view.getHeight();
    }

    /**
     * 找到parent中处于指定坐标下的child
     *
     * @param parent
     * @param x
     * @param y
     * @return
     */
    public static List<View> findChildrenUnder(ViewGroup parent, int x, int y)
    {
        final List<View> list = new LinkedList<>();

        final int count = parent.getChildCount();
        for (int i = count - 1; i >= 0; i--)
        {
            final View child = parent.getChildAt(i);
            if (isViewUnder(child, x, y))
                list.add(child);
        }
        return list;
    }

    /**
     * 找到parent中处于指定坐标下最顶部的child(Z值最大，最后面添加)
     *
     * @param parent
     * @param x
     * @param y
     * @return
     */
    public static View findTopChildUnder(ViewGroup parent, int x, int y)
    {
        if (Build.VERSION.SDK_INT < 21)
            return parent.getChildAt(parent.getChildCount() - 1);

        final List<View> list = findChildrenUnder(parent, x, y);
        if (list.isEmpty())
            return null;

        View target = list.remove(0);
        for (View item : list)
        {
            if (item.getZ() > target.getZ())
                target = item;
        }

        return target;
    }

    /**
     * view是否已经滚动到最左边
     *
     * @param view
     * @return
     */
    public static boolean isScrollToLeft(View view)
    {
        return !view.canScrollHorizontally(-1);
    }

    /**
     * view是否已经滚动到最顶部
     *
     * @param view
     * @return
     */
    public static boolean isScrollToTop(View view)
    {
        return !view.canScrollVertically(-1);
    }

    /**
     * view是否已经滚动到最右边
     *
     * @param view
     * @return
     */
    public static boolean isScrollToRight(View view)
    {
        return !view.canScrollHorizontally(1);
    }

    /**
     * view是否已经滚动到最底部
     *
     * @param view
     * @return
     */
    public static boolean isScrollToBottom(View view)
    {
        return !view.canScrollVertically(1);
    }

    //----------static method end----------
}
