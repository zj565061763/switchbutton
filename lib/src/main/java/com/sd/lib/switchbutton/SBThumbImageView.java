package com.sd.lib.switchbutton;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

class SBThumbImageView extends ImageView
{
    public SBThumbImageView(Context context)
    {
        super(context);
    }

    public SBThumbImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SBThumbImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
