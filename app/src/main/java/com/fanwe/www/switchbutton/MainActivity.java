package com.fanwe.www.switchbutton;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.ViewGroup;

import com.fanwe.lib.adapter.FSimpleRecyclerAdapter;
import com.fanwe.lib.adapter.viewholder.FRecyclerViewHolder;
import com.fanwe.lib.selectmanager.FSelectManager;
import com.fanwe.lib.switchbutton.SwitchButton;
import com.fanwe.lib.switchbutton.FSwitchButton;
import com.fanwe.library.SDLibrary;
import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.view.SDRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SDBaseActivity
{
    private FSwitchButton sb_rect;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        SDLibrary.getInstance().init(getApplication());
        setContentView(R.layout.activity_main);
        sb_rect = findViewById(R.id.sb_rect);

        testRecyclerView();

        sb_rect.setOnCheckedChangeCallback(new SwitchButton.OnCheckedChangeCallback()
        {
            @Override
            public void onCheckedChanged(boolean checked, SwitchButton view)
            {
                LogUtil.i("onCheckedChanged:" + checked);
            }
        });
        sb_rect.setOnViewPositionChangeCallback(new SwitchButton.OnViewPositionChangeCallback()
        {
            @Override
            public void onViewPositionChanged(SwitchButton view)
            {
                LogUtil.i("percent:" + view.getScrollPercent());
                float percent = view.getScrollPercent() * 0.8f + 0.2f;
                ViewCompat.setScaleY(view.getViewNormal(), percent);
                ViewCompat.setScaleY(view.getViewChecked(), percent);
            }
        });
    }


    private SDRecyclerView rv_content;

    private void testRecyclerView()
    {
        rv_content = findViewById(R.id.rv_content);

        List<FSelectManager.SelectableModel> listModel = new ArrayList<>();
        for (int i = 0; i < 100; i++)
        {
            FSelectManager.SelectableModel model = new FSelectManager.SelectableModel();
            listModel.add(model);
        }

        FSimpleRecyclerAdapter<FSelectManager.SelectableModel> adapter = new FSimpleRecyclerAdapter<FSelectManager.SelectableModel>(this)
        {
            @Override
            public void onBindData(FRecyclerViewHolder<FSelectManager.SelectableModel> holder, int position, final FSelectManager.SelectableModel model)
            {
                FSwitchButton sb = holder.get(R.id.sb);
                sb.setChecked(model.isSelected(), false, false);
                sb.setOnCheckedChangeCallback(new SwitchButton.OnCheckedChangeCallback()
                {
                    @Override
                    public void onCheckedChanged(boolean checked, SwitchButton view)
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
        adapter.getDataHolder().setData(listModel);

        rv_content.setAdapter(adapter);
    }
}
