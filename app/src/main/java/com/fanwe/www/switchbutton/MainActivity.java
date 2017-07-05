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
    }
}
