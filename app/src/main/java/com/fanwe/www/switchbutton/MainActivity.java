package com.fanwe.www.switchbutton;

import android.os.Bundle;

import com.fanwe.library.SDLibrary;
import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.switchbutton.SDSwitchButton;

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

        sb.getNormalView().setBackgroundResource(R.drawable.switch_setting_white_corner_track);
        sb.getCheckedView().setBackgroundResource(R.drawable.switch_setting_main_color_corner_track);
        sb.getHandleView().setBackgroundResource(R.drawable.switch_setting_white_round_thumb);
    }
}
