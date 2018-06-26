/*
 * Copyright (C) 2017 zhengjun, fanwe (http://www.fanwe.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanwe.lib.switchbutton;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.fanwe.lib.gesture.FTouchHelper;

public abstract class BaseSwitchButton extends FrameLayout implements SwitchButton
{
    public BaseSwitchButton(Context context)
    {
        super(context);
        init(context, null);
    }

    public BaseSwitchButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseSwitchButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 是否选中
     */
    private boolean mIsChecked;
    /**
     * 正常view
     */
    private View mViewNormal;
    /**
     * 选中view
     */
    private View mViewChecked;
    /**
     * 手柄view
     */
    private View mViewThumb;

    protected final SBAttrModel mAttrModel = new SBAttrModel();

    private OnCheckedChangeCallback mOnCheckedChangeCallback;
    private OnViewPositionChangeCallback mOnViewPositionChangeCallback;

    protected boolean mIsDebug;

    private void init(Context context, AttributeSet attrs)
    {
        mAttrModel.parse(context, attrs);
        mIsChecked = mAttrModel.isChecked;
        mIsDebug = mAttrModel.isDebug;

        addDefaultViews();
    }

    public void setDebug(boolean debug)
    {
        mIsDebug = debug;
    }

    protected final String getDebugTag()
    {
        return getClass().getSimpleName();
    }

    private void addDefaultViews()
    {
        ImageView imageNormal = new ImageView(getContext());
        imageNormal.setScaleType(ImageView.ScaleType.FIT_XY);
        imageNormal.setImageResource(mAttrModel.imageNormalResId);
        addView(imageNormal, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mViewNormal = imageNormal;

        ImageView imageChecked = new ImageView(getContext());
        imageChecked.setScaleType(ImageView.ScaleType.FIT_XY);
        imageChecked.setImageResource(mAttrModel.imageCheckedResId);
        addView(imageChecked, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mViewChecked = imageChecked;

        ImageView imageThumb = new SBThumbImageView(getContext());
        imageThumb.setScaleType(ImageView.ScaleType.FIT_XY);
        imageThumb.setImageResource(mAttrModel.imageThumbResId);
        LayoutParams pThumb = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        pThumb.gravity = Gravity.CENTER_VERTICAL;
        pThumb.leftMargin = mAttrModel.marginLeft;
        pThumb.topMargin = mAttrModel.marginTop;
        pThumb.rightMargin = mAttrModel.marginRight;
        pThumb.bottomMargin = mAttrModel.marginBottom;
        addView(imageThumb, pThumb);
        mViewThumb = imageThumb;
    }

    @Override
    public void onViewAdded(View child)
    {
        super.onViewAdded(child);
        if (getChildCount() > 3)
            throw new IllegalArgumentException("can not add view to SwitchButton");
    }

    @Override
    public void onViewRemoved(View child)
    {
        super.onViewRemoved(child);
        if (getChildCount() < 3)
            throw new IllegalArgumentException("can not remove view from SwitchButton");
    }

    /**
     * 返回normal状态下手柄view的left值
     *
     * @return
     */
    protected final int getLeftNormal()
    {
        return getParamsThumbView().leftMargin;
    }

    /**
     * 返回checked状态下手柄view的left值
     *
     * @return
     */
    protected final int getLeftChecked()
    {
        return getMeasuredWidth() - mViewThumb.getMeasuredWidth() - getParamsThumbView().rightMargin;
    }

    /**
     * 返回手柄view可以移动的宽度大小
     *
     * @return
     */
    protected final int getAvailableWidth()
    {
        return getLeftChecked() - getLeftNormal();
    }

    /**
     * 返回手柄view滚动的距离
     *
     * @return
     */
    private int getScrollDistance()
    {
        return mViewThumb.getLeft() - getLeftNormal();
    }

    /**
     * 返回手柄view布局参数
     */
    private LayoutParams getParamsThumbView()
    {
        return (LayoutParams) mViewThumb.getLayoutParams();
    }

    /**
     * 停止滑动动画
     */
    protected abstract void abortAnimation();

    /**
     * 执行滑动逻辑
     *
     * @param startLeft
     * @param endLeft
     * @return
     */
    protected abstract boolean onSmoothSlide(int startLeft, int endLeft);

    /**
     * 根据状态改变view
     *
     * @param anim
     */
    protected final void updateViewByState(boolean anim)
    {
        if (mIsDebug)
            Log.i(getDebugTag(), "----------updateViewByState anim:" + anim);

        abortAnimation();

        boolean isScrollerStarted = false;

        final int startLeft = mViewThumb.getLeft();
        final int endLeft = mIsChecked ? getLeftChecked() : getLeftNormal();

        if (startLeft != endLeft)
        {
            if (mIsDebug)
                Log.i(getDebugTag(), "updateViewByState:" + startLeft + " -> " + endLeft);

            if (anim)
            {
                isScrollerStarted = onSmoothSlide(startLeft, endLeft);
            } else
            {
                mViewThumb.layout(endLeft, mViewThumb.getTop(), endLeft + mViewThumb.getMeasuredWidth(), mViewThumb.getBottom());
            }
        }

        if (isScrollerStarted)
        {
            // 触发滚动成功，不需要立即更新view的可见状态，动画结束后更新
            invalidate();
        } else
        {
            // 立即更新view的可见状态
            dealViewIdle();
        }

        if (mViewThumb.isSelected() != mIsChecked)
            mViewThumb.setSelected(mIsChecked);

        notifyViewPositionChanged();
    }

    protected final void dealViewIdle()
    {
        if (mIsDebug)
            Log.i(getDebugTag(), "dealViewIdle:" + mIsChecked);

        if (mIsChecked)
        {
            showCheckedView(true);
            showNormalView(false);
        } else
        {
            showCheckedView(false);
            showNormalView(true);
        }
    }

    private void showCheckedView(boolean show)
    {
        float alpha = show ? 1.0f : 0f;
        if (mViewChecked.getAlpha() != alpha)
            mViewChecked.setAlpha(alpha);
    }

    private void showNormalView(boolean show)
    {
        float alpha = show ? 1.0f : 0f;
        if (mViewNormal.getAlpha() != alpha)
            mViewNormal.setAlpha(alpha);
    }

    /**
     * 移动手柄view
     *
     * @param delta 移动量
     */
    protected final void moveView(int delta)
    {
        if (delta == 0)
            return;

        final int current = mViewThumb.getLeft();
        final int min = getLeftNormal();
        final int max = getLeftChecked();
        delta = FTouchHelper.getLegalDelta(current, min, max, delta);

        if (delta == 0)
            return;

        mViewThumb.offsetLeftAndRight(delta);
        notifyViewPositionChanged();
    }

    private void notifyViewPositionChanged()
    {
        float percent = getScrollPercent();
        mViewChecked.setAlpha(percent);
        mViewNormal.setAlpha(1 - percent);

        if (mOnViewPositionChangeCallback != null)
            mOnViewPositionChangeCallback.onViewPositionChanged(BaseSwitchButton.this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        updateViewByState(false);
    }

    //----------SwitchButton implements start----------

    @Override
    public boolean isChecked()
    {
        return mIsChecked;
    }

    @Override
    public boolean setChecked(boolean checked, boolean anim, boolean notifyCallback)
    {
        if (mIsChecked == checked)
            return false;

        if (mIsDebug)
            Log.e(getDebugTag(), "setChecked:" + checked);

        mIsChecked = checked;
        updateViewByState(anim);

        if (notifyCallback)
        {
            if (mOnCheckedChangeCallback != null)
                mOnCheckedChangeCallback.onCheckedChanged(mIsChecked, this);
        }
        return true;
    }

    @Override
    public void toggleChecked(boolean anim, boolean notifyCallback)
    {
        setChecked(!mIsChecked, anim, notifyCallback);
    }

    @Override
    public void setOnCheckedChangeCallback(OnCheckedChangeCallback onCheckedChangeCallback)
    {
        mOnCheckedChangeCallback = onCheckedChangeCallback;
    }

    @Override
    public void setOnViewPositionChangeCallback(OnViewPositionChangeCallback onViewPositionChangeCallback)
    {
        mOnViewPositionChangeCallback = onViewPositionChangeCallback;
    }

    @Override
    public float getScrollPercent()
    {
        return getScrollDistance() / (float) getAvailableWidth();
    }

    @Override
    public View getViewNormal()
    {
        return mViewNormal;
    }

    @Override
    public View getViewChecked()
    {
        return mViewChecked;
    }

    @Override
    public View getViewThumb()
    {
        return mViewThumb;
    }

    //----------SwitchButton implements end----------
}
