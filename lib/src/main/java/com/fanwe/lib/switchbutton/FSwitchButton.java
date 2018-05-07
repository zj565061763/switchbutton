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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fanwe.lib.touchhelper.view.FGestureFrameLayout;

public class FSwitchButton extends FGestureFrameLayout implements FISwitchButton
{
    public FSwitchButton(Context context)
    {
        super(context);
        init(context, null);
    }

    public FSwitchButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init(context, attrs);
    }

    public FSwitchButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private static final String TAG = FSwitchButton.class.getSimpleName();

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

    private boolean mIsScrollerStarted;
    private final SBAttrModel mAttrModel = new SBAttrModel();

    private boolean mIsDebug;

    private OnCheckedChangedCallback mOnCheckedChangedCallback;
    private OnViewPositionChangedCallback mOnViewPositionChangedCallback;

    private void init(Context context, AttributeSet attrs)
    {
        mAttrModel.parse(context, attrs);

        addDefaultViews();
        setDebug(mAttrModel.isDebug());
    }

    public void setDebug(boolean debug)
    {
        mIsDebug = debug;
        getTouchHelper().setDebug(debug);
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

    /**
     * 根据状态改变view
     *
     * @param anim
     */
    private void updateViewByState(boolean anim)
    {
        final int left = mIsChecked ? getLeftChecked() : getLeftNormal();

        if (mViewThumb.getLeft() != left)
        {
            if (mIsDebug)
            {
                Log.i(TAG, "updateViewByState anim:" + anim);
            }

            if (anim)
            {
                if (getScroller().startScrollToX(mViewThumb.getLeft(), left, -1))
                {
                    mIsScrollerStarted = true;
                }
            } else
            {
                mViewThumb.layout(left, mViewThumb.getTop(), left + mViewThumb.getMeasuredWidth(), mViewThumb.getBottom());
            }
            invalidate();
        }

        if (mIsScrollerStarted)
        {
            //触发滚动成功，不需要立即更新view的可见状态，动画结束后更新
        } else
        {
            // 立即更新view的可见状态
            updateViewVisibilityByState();
        }

        mViewThumb.setSelected(mIsChecked);

        if (mOnViewPositionChangedCallback != null)
        {
            mOnViewPositionChangedCallback.onViewPositionChanged(FSwitchButton.this);
        }
    }

    private void updateViewVisibilityByState()
    {
        if (mIsDebug)
        {
            Log.i(TAG, "updateViewVisibilityByState:" + mIsChecked);
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
    private int getAvailableWidth()
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

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        View normal = findViewById(R.id.lib_sb_view_normal);
        if (normal != null)
        {
            removeView(normal);
            setViewNormal(normal);
        }

        View checked = findViewById(R.id.lib_sb_view_checked);
        if (checked != null)
        {
            removeView(checked);
            setViewChecked(checked);
        }

        View thumb = findViewById(R.id.lib_sb_view_thumb);
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

    private boolean checkMoveParams()
    {
        return getTouchHelper().getDegreeXFrom(FTouchHelper.EVENT_DOWN) < 40;
    }

    private boolean canPull()
    {
        boolean canPull = checkMoveParams()
                && ((mIsChecked && getTouchHelper().isMoveLeftFrom(FTouchHelper.EVENT_DOWN))
                || (!mIsChecked && getTouchHelper().isMoveRightFrom(FTouchHelper.EVENT_DOWN)));

        if (mIsDebug)
        {
            Log.i(TAG, "canPull:" + canPull);
        }

        return canPull;
    }

    @Override
    protected boolean shouldInterceptTouchEvent(MotionEvent event)
    {
        return canPull();
    }

    private boolean mOnGestureScrolled;

    @Override
    protected boolean onGestureScroll(MotionEvent event)
    {
        mOnGestureScrolled = true;

        final int x = mViewThumb.getLeft();
        final int minX = getLeftNormal();
        final int maxX = getLeftChecked();
        final int dx = (int) getTouchHelper().getDeltaXFrom(FTouchHelper.EVENT_LAST);

        final int dxLegal = getTouchHelper().getLegalDeltaX(x, minX, maxX, dx);
        mViewThumb.offsetLeftAndRight(dxLegal);

        onViewPositionChanged();
        return true;
    }

    @Override
    protected void onGestureFinish(MotionEvent event, float velocityX, float velocityY)
    {
        if (mOnGestureScrolled)
        {
            mOnGestureScrolled = false;

            final int left = mViewThumb.getLeft();
            final boolean checked = left >= ((getLeftNormal() + getLeftChecked()) / 2);

            boolean updatePosition = false;
            if (checked)
            {
                if (left != getLeftChecked())
                {
                    updatePosition = true;
                }
            } else
            {
                if (left != getLeftNormal())
                {
                    updatePosition = true;
                }
            }

            if (setChecked(checked, true, true))
            {
                // 更新状态成功，内部会更新view的位置
            } else
            {
                if (updatePosition)
                {
                    updateViewByState(true);
                }
            }
        }
    }

    @Override
    protected boolean onGestureSingleTapUp(MotionEvent event)
    {
        toggleChecked(mAttrModel.isNeedToggleAnim(), true);
        return super.onGestureSingleTapUp(event);
    }

    @Override
    protected void onComputeScroll(int dx, int dy)
    {
        mViewThumb.offsetLeftAndRight(dx);
        onViewPositionChanged();
    }

    @Override
    protected void onComputeScrollFinish()
    {
        mIsScrollerStarted = false;
        updateViewVisibilityByState();
    }

    protected void onViewPositionChanged()
    {
        float percent = getScrollPercent();
        mViewChecked.setAlpha(percent);
        mViewNormal.setAlpha(1 - percent);
        if (mOnViewPositionChangedCallback != null)
        {
            mOnViewPositionChangedCallback.onViewPositionChanged(FSwitchButton.this);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        if (mIsDebug)
        {
            Log.i(TAG, "onLayout");
        }
        updateViewByState(false);
    }

    //----------FISwitchButton implements start----------

    @Override
    public boolean isChecked()
    {
        return mIsChecked;
    }

    @Override
    public boolean setChecked(boolean checked, boolean anim, boolean notifyCallback)
    {
        if (mIsChecked != checked)
        {
            mIsChecked = checked;

            updateViewByState(anim);

            // 通知回调
            if (notifyCallback)
            {
                if (mOnCheckedChangedCallback != null)
                {
                    mOnCheckedChangedCallback.onCheckedChanged(mIsChecked, this);
                }
            }
            return true;
        } else
        {
            return false;
        }
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

    //----------FISwitchButton implements end----------
}
