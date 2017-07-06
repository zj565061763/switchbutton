package com.fanwe.www.switchbutton;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;

import com.fanwe.library.SDLibrary;
import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.switchbutton.ISDSwitchButton;
import com.fanwe.library.switchbutton.SDSwitchButton;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDToast;

public class MainActivity extends SDBaseActivity
{
    private SDSwitchButton sb;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        SDLibrary.getInstance().init(getApplication());
        setContentView(R.layout.activity_main);
        sb = (SDSwitchButton) findViewById(R.id.sb);

        sb.setDebug(true);

        sb.setOnCheckedChangedCallback(new SDSwitchButton.OnCheckedChangedCallback()
        {
            @Override
            public void onCheckedChanged(boolean checked, SDSwitchButton view)
            {
                SDToast.showToast("" + checked);
            }
        });
        sb.setOnViewPositionChangedCallback(new ISDSwitchButton.OnViewPositionChangedCallback()
        {
            @Override
            public void onViewPositionChanged(SDSwitchButton view)
            {
                float percent = view.getScrollPercent() * 0.8f + 0.2f;


                ViewCompat.setScaleY(view.getViewNormal(), percent);
                ViewCompat.setScaleY(view.getViewChecked(), percent);
                LogUtil.i("percent:" + view.getScrollPercent());
            }
        });

//        sb.setChecked(true, false, false);
    }
}
