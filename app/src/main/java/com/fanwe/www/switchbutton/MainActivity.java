package com.fanwe.www.switchbutton;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.ViewGroup;

import com.fanwe.library.SDLibrary;
import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.adapter.SDSimpleRecyclerAdapter;
import com.fanwe.library.adapter.viewholder.SDRecyclerViewHolder;
import com.fanwe.library.model.SelectableModel;
import com.fanwe.library.switchbutton.ISDSwitchButton;
import com.fanwe.library.switchbutton.SDSwitchButton;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.view.SDRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SDBaseActivity
{
    private SDSwitchButton sb_rect;


    @Override
    protected void init(Bundle savedInstanceState)
    {
        SDLibrary.getInstance().init(getApplication());
        setContentView(R.layout.activity_main);
        sb_rect = (SDSwitchButton) findViewById(R.id.sb_rect);

        testRecyclerView();

        sb_rect.setDebug(true);
        sb_rect.setOnCheckedChangedCallback(new SDSwitchButton.OnCheckedChangedCallback()
        {
            @Override
            public void onCheckedChanged(boolean checked, SDSwitchButton view)
            {
                SDToast.showToast("" + checked);
            }
        });
        sb_rect.setOnViewPositionChangedCallback(new ISDSwitchButton.OnViewPositionChangedCallback()
        {
            @Override
            public void onViewPositionChanged(SDSwitchButton view)
            {
                float percent = view.getScrollPercent() * 0.8f + 0.2f;
                ViewCompat.setScaleY(view.getViewNormal(), percent);
                ViewCompat.setScaleY(view.getViewChecked(), percent);
            }
        });

    }


    private SDRecyclerView rv_content;

    private void testRecyclerView()
    {
        rv_content = (SDRecyclerView) findViewById(R.id.rv_content);

        List<SelectableModel> listModel = new ArrayList<>();
        for (int i = 0; i < 100; i++)
        {
            SelectableModel model = new SelectableModel();
            listModel.add(model);
        }

        SDSimpleRecyclerAdapter<SelectableModel> adapter = new SDSimpleRecyclerAdapter<SelectableModel>(listModel, this)
        {
            @Override
            public void onBindData(SDRecyclerViewHolder<SelectableModel> holder, int position, final SelectableModel model)
            {
                SDSwitchButton sb = holder.get(R.id.sb);
                sb.setDebug(true);
                sb.setChecked(model.isSelected(), false, false);
                sb.setOnCheckedChangedCallback(new ISDSwitchButton.OnCheckedChangedCallback()
                {
                    @Override
                    public void onCheckedChanged(boolean checked, SDSwitchButton view)
                    {
                        model.setSelected(checked);
                    }
                });
            }

            @Override
            public int getLayoutId(ViewGroup parent, int viewType)
            {
                return R.layout.item_listview;
            }
        };
        rv_content.setAdapter(adapter);
    }
}
