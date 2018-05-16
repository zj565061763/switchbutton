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
import android.view.ViewGroup;
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

    private OnCheckedChangedCallback mOnCheckedChangedCallback;
    private OnViewPositionChangedCallback mOnViewPositionChangedCallback;

    protected boolean mIsDebug;

    private void init(Context context, AttributeSet attrs)
    {
        mAttrModel.parse(context, attrs);
        setDebug(mAttrModel.isDebug());

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
        imageNormal.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageNormal.setImageResource(mAttrModel.getImageNormalResId());
        addView(imageNormal, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mViewNormal = imageNormal;

        ImageView imageChecked = new ImageView(getContext());
        imageChecked.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageChecked.setImageResource(mAttrModel.getImageCheckedResId());
        addView(imageChecked, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mViewChecked = imageChecked;

        ImageView imageThumb = new SBThumbImageView(getContext());
        imageThumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageThumb.setImageResource(mAttrModel.getImageThumbResId());
        LayoutParams pThumb = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        pThumb.gravity = Gravity.CENTER_VERTICAL;
        pThumb.leftMargin = mAttrModel.getMarginLeft();
        pThumb.topMargin = mAttrModel.getMarginTop();
        pThumb.rightMargin = mAttrModel.getMarginRight();
        pThumb.bottomMargin = mAttrModel.getMarginBottom();
        addView(imageThumb, pThumb);
        mViewThumb = imageThumb;

        setChecked(mAttrModel.isChecked(), false, false);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        final View normal = findViewById(R.id.lib_sb_view_normal);
        if (normal != null)
        {
            removeView(normal);
            setViewNormal(normal);
        }

        final View checked = findViewById(R.id.lib_sb_view_checked);
        if (checked != null)
        {
            removeView(checked);
            setViewChecked(checked);
        }

        final View thumb = findViewById(R.id.lib_sb_view_thumb);
        if (thumb != null)
        {
            removeView(thumb);
            setViewThumb(thumb);
        }
    }

    /**
     * 设置正常view
     *
     * @param viewNormal
     */
    private void setViewNormal(View viewNormal)
    {
        if (replaceOldView(mViewNormal, viewNormal))
        {
            mViewNormal = viewNormal;
        }
    }

    /**
     * 设置选中view
     *
     * @param viewChecked
     */
    private void setViewChecked(View viewChecked)
    {
        if (replaceOldView(mViewChecked, viewChecked))
        {
            mViewChecked = viewChecked;
        }
    }

    /**
     * 设置手柄view
     *
     * @param viewThumb
     */
    private void setViewThumb(View viewThumb)
    {
        if (replaceOldView(mViewThumb, viewThumb))
        {
            mViewThumb = viewThumb;
        }
    }

    private boolean replaceOldView(View viewOld, View viewNew)
    {
        if (viewNew != null && viewOld != viewNew)
        {
            int index = indexOfChild(viewOld);
            ViewGroup.LayoutParams params = viewOld.getLayoutParams();
            removeView(viewOld);

            if (viewNew.getLayoutParams() != null)
            {
                params = viewNew.getLayoutParams();
            }

            addView(viewNew, index, params);
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * 返回normal状态下手柄view的left值
     *
     * @return
     */
    private int getLeftNormal()
    {
        return getParamsThumbView().leftMargin;
    }

    /**
     * 返回checked状态下手柄view的left值
     *
     * @return
     */
    private int getLeftChecked()
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
        {
            Log.i(getDebugTag(), "----------updateViewByState anim:" + anim);
        }

        abortAnimation();

        boolean isScrollerStarted = false;

        final int startLeft = mViewThumb.getLeft();
        final int endLeft = mIsChecked ? getLeftChecked() : getLeftNormal();

        if (startLeft != endLeft)
        {
            if (mIsDebug)
            {
                Log.i(getDebugTag(), "updateViewByState:" + mViewThumb.getLeft() + " -> " + endLeft);
            }

            if (anim)
            {
                if (onSmoothSlide(startLeft, endLeft))
                {
                    isScrollerStarted = true;
                }
            } else
            {
                mViewThumb.layout(endLeft, mViewThumb.getTop(), endLeft + mViewThumb.getMeasuredWidth(), mViewThumb.getBottom());
            }
            invalidate();
        }

        if (isScrollerStarted)
        {
            // 触发滚动成功，不需要立即更新view的可见状态，动画结束后更新
        } else
        {
            // 立即更新view的可见状态
            dealViewIdle();
        }

        mViewThumb.setSelected(mIsChecked);
        notifyViewPositionChanged();
    }

    protected final void dealViewIdle()
    {
        if (mIsDebug)
        {
            Log.i(getDebugTag(), "dealViewIdle isChecked:" + mIsChecked);
        }

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
        {
            mViewChecked.setAlpha(alpha);
        }
    }

    private void showNormalView(boolean show)
    {
        float alpha = show ? 1.0f : 0f;
        if (mViewNormal.getAlpha() != alpha)
        {
            mViewNormal.setAlpha(alpha);
        }
    }

    /**
     * 移动手柄view
     *
     * @param delta 移动量
     */
    protected final void moveView(int delta)
    {
        final int current = mViewThumb.getLeft();
        final int min = getLeftNormal();
        final int max = getLeftChecked();
        final int deltaLegal = FTouchHelper.getLegalDelta(current, min, max, delta);

        if (deltaLegal == 0) return;

        mViewThumb.offsetLeftAndRight(deltaLegal);
        notifyViewPositionChanged();
    }

    private void notifyViewPositionChanged()
    {
        float percent = getScrollPercent();
        mViewChecked.setAlpha(percent);
        mViewNormal.setAlpha(1 - percent);
        if (mOnViewPositionChangedCallback != null)
        {
            mOnViewPositionChangedCallback.onViewPositionChanged(BaseSwitchButton.this);
        }
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
        {
            return false;
        }

        mIsChecked = checked;

        if (mIsDebug)
        {
            Log.e(getDebugTag(), "setChecked:" + checked);
        }

        updateViewByState(anim);
        if (notifyCallback)
        {
            if (mOnCheckedChangedCallback != null)
            {
                mOnCheckedChangedCallback.onCheckedChanged(mIsChecked, this);
            }
        }
        return true;
    }

    @Override
    public void toggleChecked(boolean anim, boolean notifyCallback)
    {
        setChecked(!mIsChecked, anim, notifyCallback);
    }

    @Override
    public void setOnCheckedChangedCallback(OnCheckedChangedCallback onCheckedChangedCallback)
    {
        mOnCheckedChangedCallback = onCheckedChangedCallback;
    }

    @Override
    public void setOnViewPositionChangedCallback(OnViewPositionChangedCallback onViewPositionChangedCallback)
    {
        mOnViewPositionChangedCallback = onViewPositionChangedCallback;
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
