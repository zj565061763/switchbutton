package com.sd.www.switchbutton;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sd.lib.switchbutton.FSwitchButton;
import com.sd.lib.switchbutton.SwitchButton;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    private FSwitchButton sb_rect;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sb_rect = findViewById(R.id.sb_rect);

        sb_rect.setOnCheckedChangeCallback(new SwitchButton.OnCheckedChangeCallback()
        {
            @Override
            public void onCheckedChanged(boolean checked, SwitchButton view)
            {
                Log.i(TAG, "onCheckedChanged:" + checked);
            }
        });
        sb_rect.setOnViewPositionChangeCallback(new SwitchButton.OnViewPositionChangeCallback()
        {
            @Override
            public void onViewPositionChanged(SwitchButton view)
            {
                Log.i(TAG, "percent:" + view.getScrollPercent());
                float percent = view.getScrollPercent() * 0.8f + 0.2f;
                view.getViewNormal().setScaleY(percent);
                view.getViewChecked().setScaleY(percent);
            }
        });
    }
}
