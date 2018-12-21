package com.sd.lib.switchbutton;

import android.content.Context;
import android.view.View;

class SBThumbView extends View
{
    public SBThumbView(Context context)
    {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
