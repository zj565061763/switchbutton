package com.fanwe.library.switchbutton;

import android.view.MotionEvent;

/**
 * 触摸事件处理帮助类
 */
class SDTouchEventHelper
{
    private boolean mIsNeedConsume = false;

    private float mDownX;
    private float mDownY;

    private float mMoveX;
    private float mMoveY;
    private float mLastMoveX;
    private float mLastMoveY;

    private float mMoveDistanceX;
    private float mMoveDistanceY;

    private float mDistanceX;
    private float mDistanceY;

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

                mMoveDistanceX = mMoveX - mLastMoveX;
                mMoveDistanceY = mMoveY - mLastMoveY;

                mDistanceX = mMoveX - mDownX;
                mDistanceY = mMoveY - mDownY;

                final float angleX = Math.abs(mMoveDistanceY) / Math.abs(mMoveDistanceX);
                mDegreeX = Math.toDegrees(Math.atan(angleX));

                final float angleY = Math.abs(mMoveDistanceX) / Math.abs(mMoveDistanceY);
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
     * 设置是否需要消费事件
     *
     * @param needConsume
     */
    public void setNeedConsume(boolean needConsume)
    {
        mIsNeedConsume = needConsume;
    }

    /**
     * 是否需要消费事件
     *
     * @return
     */
    public boolean isNeedConsume()
    {
        return mIsNeedConsume;
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
    public float getMoveDistanceX()
    {
        return mMoveDistanceX;
    }

    /**
     * 最后两次ACTION_MOVE的y距离<br>
     * 大于0：往下移动<br>
     * 小于0：往上移动<br>
     *
     * @return
     */
    public float getMoveDistanceY()
    {
        return mMoveDistanceY;
    }

    /**
     * 当前ACTION_MOVE的x和ACTION_DOWN的x的距离
     *
     * @return
     */
    public float getDistanceX()
    {
        return mDistanceX;
    }

    /**
     * 当前ACTION_MOVE的y和ACTION_DOWN的y的距离
     *
     * @return
     */
    public float getDistanceY()
    {
        return mDistanceY;
    }

    /**
     * 最后两次ACTION_MOVE方向向左
     *
     * @return
     */
    public boolean isMoveLeft()
    {
        return getMoveDistanceX() < 0;
    }

    /**
     * 最后两次ACTION_MOVE方向向左
     *
     * @return
     */
    public boolean isMoveRight()
    {
        return getMoveDistanceX() > 0;
    }

    /**
     * 最后两次ACTION_MOVE方向向上
     *
     * @return
     */
    public boolean isMoveUp()
    {
        return getMoveDistanceY() < 0;
    }

    /**
     * 最后两次ACTION_MOVE方向向下
     *
     * @return
     */
    public boolean isMoveDown()
    {
        return getMoveDistanceY() > 0;
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

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("\r\n");
        sb.append("mMoveDistanceX:").append(mMoveDistanceX).append("\r\n")
                .append("mMoveDistanceY:").append(mMoveDistanceY).append("\r\n")
                .append("mDegreeX:").append(mDegreeX).append("\r\n")
                .append("mDegreeY:").append(mDegreeY).append("\r\n");
        return sb.toString();
    }
}
