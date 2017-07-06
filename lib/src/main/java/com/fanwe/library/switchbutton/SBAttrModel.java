package com.fanwe.library.switchbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2017/7/6.
 */

class SBAttrModel
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
    private int marginLeft = -1;
    /**
     * 手柄view顶部间距
     */
    private int marginTop = -1;
    /**
     * 手柄view右边间距
     */
    private int marginRight = -1;
    /**
     * 手柄view底部间距
     */
    private int marginBottom = -1;
    /**
     * 是否选中
     */
    private boolean isChecked;
    /**
     * 是否需要点击切换动画
     */
    private boolean isNeedToggleAnim;

    /**
     * 解析xml属性
     *
     * @param context
     * @param attrs
     */
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

            if (a.hasValue(R.styleable.LibSwitchButton_sbMargins))
            {
                int margins = a.getDimensionPixelSize(R.styleable.LibSwitchButton_sbMargins, -1);
                marginLeft = margins;
                marginTop = margins;
                marginRight = margins;
                marginBottom = margins;
            }

            if (a.hasValue(R.styleable.LibSwitchButton_sbMarginLeft))
            {
                marginLeft = a.getDimensionPixelSize(R.styleable.LibSwitchButton_sbMarginLeft, -1);
            }
            if (a.hasValue(R.styleable.LibSwitchButton_sbMarginTop))
            {
                marginTop = a.getDimensionPixelSize(R.styleable.LibSwitchButton_sbMarginTop, -1);
            }
            if (a.hasValue(R.styleable.LibSwitchButton_sbMarginRight))
            {
                marginRight = a.getDimensionPixelSize(R.styleable.LibSwitchButton_sbMarginRight, -1);
            }
            if (a.hasValue(R.styleable.LibSwitchButton_sbMarginBottom))
            {
                marginBottom = a.getDimensionPixelSize(R.styleable.LibSwitchButton_sbMarginBottom, -1);
            }

            isChecked = a.getBoolean(R.styleable.LibSwitchButton_sbIsChecked, false);
            isNeedToggleAnim = a.getBoolean(R.styleable.LibSwitchButton_sbIsNeedToggleAnim, true);

            a.recycle();
        }
    }

    public int getImageNormalResId()
    {
        return imageNormalResId;
    }

    public void setImageNormalResId(int imageNormalResId)
    {
        this.imageNormalResId = imageNormalResId;
    }

    public int getImageCheckedResId()
    {
        return imageCheckedResId;
    }

    public void setImageCheckedResId(int imageCheckedResId)
    {
        this.imageCheckedResId = imageCheckedResId;
    }

    public int getImageThumbResId()
    {
        return imageThumbResId;
    }

    public void setImageThumbResId(int imageThumbResId)
    {
        this.imageThumbResId = imageThumbResId;
    }

    public int getMarginLeft()
    {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft)
    {
        this.marginLeft = marginLeft;
    }

    public int getMarginTop()
    {
        return marginTop;
    }

    public void setMarginTop(int marginTop)
    {
        this.marginTop = marginTop;
    }

    public int getMarginRight()
    {
        return marginRight;
    }

    public void setMarginRight(int marginRight)
    {
        this.marginRight = marginRight;
    }

    public int getMarginBottom()
    {
        return marginBottom;
    }

    public void setMarginBottom(int marginBottom)
    {
        this.marginBottom = marginBottom;
    }

    public boolean isChecked()
    {
        return isChecked;
    }

    public void setChecked(boolean checked)
    {
        isChecked = checked;
    }

    public boolean isNeedToggleAnim()
    {
        return isNeedToggleAnim;
    }

    public void setNeedToggleAnim(boolean needToggleAnim)
    {
        isNeedToggleAnim = needToggleAnim;
    }
}
