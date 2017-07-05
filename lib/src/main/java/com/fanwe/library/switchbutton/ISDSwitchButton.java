package com.fanwe.library.switchbutton;

/**
 * Created by Administrator on 2017/7/5.
 */

public interface ISDSwitchButton
{

    /**
     * 设置选中状态
     *
     * @param checked        true-选中，false-未选中
     * @param anim           是否需要动画
     * @param notifyCallback 是否需要通知回调
     */
    void setChecked(boolean checked, boolean anim, boolean notifyCallback);

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
     * @param onCheckedChangedCallback
     */
    void setOnCheckedChangedCallback(OnCheckedChangedCallback onCheckedChangedCallback);

    /**
     * 设置手柄view左边间距
     *
     * @param marginLeft
     */
    void setMarginLeft(int marginLeft);

    /**
     * 设置手柄view顶部间距
     *
     * @param marginTop
     */
    void setMarginTop(int marginTop);

    /**
     * 设置手柄view右边间距
     *
     * @param marginRight
     */
    void setMarginRight(int marginRight);

    /**
     * 设置手柄view底部间距
     *
     * @param marginBottom
     */
    void setMarginBottom(int marginBottom);

    /**
     * 设置是否是透明度模式来显示隐藏view的
     *
     * @param alphaMode
     */
    void setAlphaMode(boolean alphaMode);


    interface OnCheckedChangedCallback
    {
        /**
         * 选中状态变化回调
         *
         * @param checked
         * @param view
         */
        void onCheckedChanged(boolean checked, SDSwitchButton view);
    }
}
