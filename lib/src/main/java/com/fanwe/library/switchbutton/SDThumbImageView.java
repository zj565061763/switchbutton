package com.fanwe.library.switchbutton;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/7/6.
 */

class SDThumbImageView extends ImageView
{
    public SDThumbImageView(Context context)
    {
        super(context);
    }

    public SDThumbImageView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SDThumbImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
