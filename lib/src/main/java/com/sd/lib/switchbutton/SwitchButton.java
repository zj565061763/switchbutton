package com.sd.lib.switchbutton;

import android.view.View;

public interface SwitchButton
{
    /**
     * 是否处于选中状态
     *
     * @return
     */
    boolean isChecked();

    /**
     * 设置选中状态
     *
     * @param checked        true-选中，false-未选中
     * @param anim           是否需要动画
     * @param notifyCallback 是否需要通知回调
     * @return true-调用此方法后状态发生了变化
     */
    boolean setChecked(boolean checked, boolean anim, boolean notifyCallback);

    /**
     * 切换选中状态
     *
     * @param anim           是否需要动画
     * @param notifyCallback 是否需要通知回调
     */
    void toggleChecked(boolean anim, boolean notifyCallback);

    /**
     * 设置选中变化回调
     *
     * @param callback
     */
    void setOnCheckedChangeCallback(OnCheckedChangeCallback callback);

    /**
     * 设置手柄view位置变化回调
     *
     * @param callback
     */
    void setOnViewPositionChangeCallback(OnViewPositionChangeCallback callback);

    /**
     * 设置滚动状态变化回调
     *
     * @param callback
     */
    void setOnScrollStateChangeCallback(OnScrollStateChangeCallback callback);

    /**
     * 返回滚动的百分比[0-1]
     *
     * @return
     */
    float getScrollPercent();

    /**
     * 返回滚动状态
     *
     * @return
     */
    ScrollState getScrollState();

    /**
     * 返回正常状态view
     *
     * @return
     */
    View getViewNormal();

    /**
     * 返回选中状态view
     *
     * @return
     */
    View getViewChecked();

    /**
     * 返回手柄view
     *
     * @return
     */
    View getViewThumb();

    interface OnCheckedChangeCallback
    {
        /**
         * 选中状态变化回调
         *
         * @param checked
         * @param switchButton
         */
        void onCheckedChanged(boolean checked, SwitchButton switchButton);
    }

    interface OnViewPositionChangeCallback
    {
        /**
         * 手柄view滚动回调
         *
         * @param switchButton
         */
        void onViewPositionChanged(SwitchButton switchButton);
    }

    enum ScrollState
    {
        /**
         * 空闲状态
         */
        Idle,
        /**
         * 拖动状态
         */
        Drag,
        /**
         * 惯性滑动状态
         */
        Fling
    }

    interface OnScrollStateChangeCallback
    {
        /**
         * 滚动状态变化回调
         *
         * @param oldState
         * @param newState
         * @param switchButton
         */
        void onScrollStateChanged(ScrollState oldState, ScrollState newState, SwitchButton switchButton);
    }
}
