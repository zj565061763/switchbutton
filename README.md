# SwitchButton
Android开关按钮

## 默认效果
![](http://thumbsnap.com/i/KBISOucv.gif?0705)

## 覆盖全局默认效果
支持覆盖的默认配置：<br>
* colors <br>
可以在项目中定义colors覆盖库中的默认配置<br>
![](http://thumbsnap.com/i/VJIMDfDU.png?0706)<br>

* dimens <br>
可以在项目中定义dimens覆盖库中的默认配置<br>
![](http://thumbsnap.com/i/RoMc4bVA.png?0706)<br>

* drawable <br>
可以在项目中定义drawable覆盖库中的默认配置，覆盖drawable后以上的colors和dimens设置失效<br>
![](http://thumbsnap.com/i/vErZPQhN.png?0706)<br>

## xml属性
* 支持的xml属性配置：<br>
![](http://thumbsnap.com/i/4DrNGJt9.png?0706)<br>

## 自定义效果
![](http://thumbsnap.com/i/YS9spIQs.gif?0706)<br>

1. xml中布局：<br>
```Xml
    <com.fanwe.library.switchbutton.SDSwitchButton
        android:id="@+id/sb_rect"
        android:layout_width="50dp"
        android:layout_height="25dp"
        android:layout_marginTop="10dp"
        app:sbImageChecked="@drawable/layer_checked_view"
        app:sbImageNormal="@drawable/layer_normal_view"
        app:sbImageThumb="@drawable/layer_thumb_view"
        app:sbIsChecked="true"
        app:sbMarginBottom="2dp"
        app:sbMarginLeft="0dp"
        app:sbMarginRight="2dp"
        app:sbMarginTop="2dp"
        app:sbMargins="1dp"/>
```
2. java文件中：<br>
![](http://thumbsnap.com/s/9xlaALzm.png?0706)<br>

## 完全自定义效果
![](http://thumbsnap.com/i/4jo7RqHa.gif?0706)<br>
xml中布局：<br>
![](http://thumbsnap.com/i/8Z9dbQ1f.png?0706)<br>
指定view的id为库中的默认id即可完全自定义view的展示效果，定义任何你想要的效果，可以指定其中一个id或者全部id<br>
<br>
库中支持的id如下：
```Xml
    <!--正常view的id-->
    <item name="lib_sb_view_normal" type="id"/>
    <!--选中view的id-->
    <item name="lib_sb_view_checked" type="id"/>
    <!--手柄view的id-->
    <item name="lib_sb_view_thumb" type="id"/>
```

## 支持的方法
```Java
public interface ISDSwitchButton
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
     * 设置手柄view位置变化回调
     *
     * @param onViewPositionChangedCallback
     */
    void setOnViewPositionChangedCallback(OnViewPositionChangedCallback onViewPositionChangedCallback);

    /**
     * 设置是否是透明度模式来显示隐藏view的
     *
     * @param alphaMode
     */
    void setAlphaMode(boolean alphaMode);

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

    interface OnViewPositionChangedCallback
    {
        /**
         * 手柄view滚动回调
         *
         * @param view
         */
        void onViewPositionChanged(SDSwitchButton view);
    }
}
```
