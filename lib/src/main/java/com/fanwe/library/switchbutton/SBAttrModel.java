package com.fanwe.library.switchbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2017/7/6.
 */

public class SBAttrModel
{
    /**
     * 正常view图片id
     */
    private int imageNormalResId;
    /**
     * 选中view图片id
     */
    private int imageCheckedResId;
    /**
     * 手柄view图片id
     */
    private int imageThumbResId;
    /**
     * 手柄view左边间距
     */
    private int marginLeft;
    /**
     * 手柄view顶部间距
     */
    private int marginTop;
    /**
     * 手柄view右边间距
     */
    private int marginRight;
    /**
     * 手柄view底部间距
     */
    private int marginBottom;
    /**
     * 是否选中
     */
    private boolean isChecked;

    public void parse(Context context, AttributeSet attrs)
    {
        if (attrs == null)
        {
            imageNormalResId = R.drawable.lib_sb_layer_normal_view;
            imageCheckedResId = R.drawable.lib_sb_layer_checked_view;
            imageThumbResId = R.drawable.lib_sb_layer_thumb_view;
        } else
        {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LibSwitchButton);

            imageNormalResId = a.getResourceId(R.styleable.LibSwitchButton_sbImageNormal, R.drawable.lib_sb_layer_normal_view);
            imageCheckedResId = a.getResourceId(R.styleable.LibSwitchButton_sbImageChecked, R.drawable.lib_sb_layer_checked_view);
            imageThumbResId = a.getResourceId(R.styleable.LibSwitchButton_sbImageThumb, R.drawable.lib_sb_layer_thumb_view);

            marginLeft = a.getDimensionPixelSize(R.styleable.LibSwitchButton_sbMarginLeft, 0);
            marginTop = a.getDimensionPixelSize(R.styleable.LibSwitchButton_sbMarginTop, 0);
            marginRight = a.getDimensionPixelSize(R.styleable.LibSwitchButton_sbMarginRight, 0);
            marginBottom = a.getDimensionPixelSize(R.styleable.LibSwitchButton_sbMarginBottom, 0);

            isChecked = a.getBoolean(R.styleable.LibSwitchButton_sbIsChecked, false);
        }
    }

    public int getImageNormalResId()
    {
        return imageNormalResId;
    }

    public int getImageCheckedResId()
    {
        return imageCheckedResId;
    }

    public int getImageThumbResId()
    {
        return imageThumbResId;
    }

    public int getMarginLeft()
    {
        return marginLeft;
    }

    public int getMarginTop()
    {
        return marginTop;
    }

    public int getMarginRight()
    {
        return marginRight;
    }

    public int getMarginBottom()
    {
        return marginBottom;
    }

    public boolean isChecked()
    {
        return isChecked;
    }
}
