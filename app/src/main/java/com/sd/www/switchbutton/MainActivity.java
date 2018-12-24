package com.sd.www.switchbutton;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.sd.lib.switchbutton.FSwitchButton;
import com.sd.lib.switchbutton.SwitchButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = MainActivity.class.getSimpleName();
    private FSwitchButton sb_custom;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sb_custom = findViewById(R.id.sb_custom);

        sb_custom.setOnCheckedChangeCallback(new SwitchButton.OnCheckedChangeCallback()
        {
            @Override
            public void onCheckedChanged(boolean checked, SwitchButton switchButton)
            {
                Log.i(TAG, "onCheckedChanged:" + checked);
            }
        });
        sb_custom.setOnViewPositionChangeCallback(new SwitchButton.OnViewPositionChangeCallback()
        {
            @Override
            public void onViewPositionChanged(SwitchButton switchButton)
            {
                final float percent = switchButton.getScrollPercent();
                Log.i(TAG, "percent:" + percent);

                float scalePercent = percent * 0.8f + 0.2f;
                switchButton.getViewNormal().setScaleY(scalePercent);
                switchButton.getViewChecked().setScaleY(scalePercent);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        FSwitchButton sb_test = findViewById(R.id.sb_test);
        sb_test.toggleChecked(true, false);
    }
}
