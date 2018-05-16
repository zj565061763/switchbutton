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
     * @param onCheckedChangeCallback
     */
    void setOnCheckedChangeCallback(OnCheckedChangeCallback onCheckedChangeCallback);

    /**
     * 设置手柄view位置变化回调
     *
     * @param onViewPositionChangeCallback
     */
    void setOnViewPositionChangeCallback(OnViewPositionChangeCallback onViewPositionChangeCallback);

    /**
     * 获得滚动的百分比[0-1]
     *
     * @return
     */
    float getScrollPercent();

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
         * @param view
         */
        void onCheckedChanged(boolean checked, SwitchButton view);
    }

    interface OnViewPositionChangeCallback
    {
        /**
         * 手柄view滚动回调
         *
         * @param view
         */
        void onViewPositionChanged(SwitchButton view);
    }
}
