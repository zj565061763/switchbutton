package com.fanwe.library.switchbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/7/6.
 */

public class SDThumbImageView extends ImageView
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
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (getHeight() > 0 && getHeight() != getWidth())
        {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = getHeight();
            setLayoutParams(params);
        }
    }
}
