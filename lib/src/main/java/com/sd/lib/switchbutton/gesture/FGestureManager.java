/*
 * Copyright (C) 2017 Sunday (https://github.com/zj565061763)
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
package com.sd.lib.switchbutton.gesture;

import android.view.MotionEvent;
import android.view.VelocityTracker;

public class FGestureManager
{
    private final FTouchHelper mTouchHelper = new FTouchHelper();
    private final FTagHolder mTagHolder = new FTagHolder();

    private VelocityTracker mVelocityTracker;

    private boolean mHasConsumeEvent = false;
    private boolean mIsCancelTouchEvent = false;

    private final Callback mCallback;

    public FGestureManager(Callback callback)
    {
        if (callback == null)
            throw new NullPointerException("callback is null");
        mCallback = callback;
    }

    /**
     * 返回触摸帮助类
     *
     * @return
     */
    public FTouchHelper getTouchHelper()
    {
        return mTouchHelper;
    }

    /**
     * 返回标识持有者
     *
     * @return
     */
    public FTagHolder getTagHolder()
    {
        return mTagHolder;
    }

    private VelocityTracker getVelocityTracker()
    {
        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();
        return mVelocityTracker;
    }

    private void releaseVelocityTracker()
    {
        if (mVelocityTracker != null)
        {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 设置取消触摸事件
     */
    public void setCancelTouchEvent()
    {
        if (mTagHolder.isTagConsume() || mTagHolder.isTagIntercept())
        {
            mIsCancelTouchEvent = true;
            mTagHolder.reset();
        }
    }

    /**
     * 外部调用
     *
     * @param event
     * @return
     */
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        mTouchHelper.processTouchEvent(event);
        getVelocityTracker().addMovement(event);

        boolean tagIntercept = false;

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
        {
            onEventFinish(event);
        } else
        {
            if (action == MotionEvent.ACTION_DOWN)
                onEventStart(event);

            tagIntercept = mCallback.shouldInterceptEvent(event);
        }

        if (mIsCancelTouchEvent)
            tagIntercept = false;

        mTagHolder.setTagIntercept(tagIntercept);
        return mTagHolder.isTagIntercept();
    }

    /**
     * 外部调用
     *
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        mTouchHelper.processTouchEvent(event);
        getVelocityTracker().addMovement(event);

        boolean tagConsume = false;

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
        {
            onEventFinish(event);
        } else if (action == MotionEvent.ACTION_DOWN)
        {
            onEventStart(event);
            return mCallback.onEventActionDown(event);
        } else
        {
            if (mTagHolder.isTagConsume())
            {
                final boolean consume = mCallback.onEventConsume(event);

                // 标识消费过事件
                if (consume)
                    mHasConsumeEvent = true;

                tagConsume = consume;
            } else
            {
                if (!mIsCancelTouchEvent)
                    tagConsume = mCallback.shouldConsumeEvent(event);
            }
        }

        if (mIsCancelTouchEvent)
            tagConsume = false;

        mTagHolder.setTagConsume(tagConsume);
        return mTagHolder.isTagConsume();
    }

    private void onEventStart(MotionEvent event)
    {

    }

    private void onEventFinish(MotionEvent event)
    {
        mTagHolder.reset();

        final FinishParams params = new FinishParams(mHasConsumeEvent, mIsCancelTouchEvent);
        mCallback.onEventFinish(params, getVelocityTracker(), event);

        mHasConsumeEvent = false;
        mIsCancelTouchEvent = false;
        releaseVelocityTracker();
    }

    public static class FinishParams
    {
        /**
         * 本次按下到结束的过程中{@link Callback#onEventConsume(MotionEvent)}方法是否消费过事件
         */
        public final boolean hasConsumeEvent;
        /**
         * 本次按下到结束的过程中是否调用过{@link #setCancelTouchEvent()}方法，取消事件
         */
        public final boolean isCancelTouchEvent;

        private FinishParams(boolean hasConsumeEvent, boolean isCancelTouchEvent)
        {
            this.hasConsumeEvent = hasConsumeEvent;
            this.isCancelTouchEvent = isCancelTouchEvent;
        }
    }

    public abstract static class Callback
    {
        /**
         * 是否开始拦截事件(由{@link #onInterceptTouchEvent(MotionEvent)}方法触发)
         *
         * @param event
         * @return
         */
        public boolean shouldInterceptEvent(MotionEvent event)
        {
            return false;
        }

        /**
         * 是否消费{@link MotionEvent#ACTION_DOWN}事件(由{@link #onTouchEvent(MotionEvent)}方法触发)
         * <br>
         * 注意，只有此方法返回了true，才有后续的移动等事件，默认返回true
         *
         * @param event
         * @return
         */
        public boolean onEventActionDown(MotionEvent event)
        {
            return true;
        }

        /**
         * 是否开始消费事件(由{@link #onTouchEvent(MotionEvent)}方法触发)
         *
         * @param event
         * @return
         */
        public abstract boolean shouldConsumeEvent(MotionEvent event);

        /**
         * 事件回调
         *
         * @param event
         * @return
         */
        public abstract boolean onEventConsume(MotionEvent event);

        /**
         * 事件结束，收到{@link MotionEvent#ACTION_UP}或者{@link MotionEvent#ACTION_CANCEL}事件
         *
         * @param params          {@link FinishParams}
         * @param velocityTracker 速率计算对象，这里返回的对象还未进行速率计算，如果要获得速率需要先进行计算{@link VelocityTracker#computeCurrentVelocity(int)}
         * @param event           {@link MotionEvent#ACTION_UP}或者{@link MotionEvent#ACTION_CANCEL}
         */
        public abstract void onEventFinish(FinishParams params, VelocityTracker velocityTracker, MotionEvent event);
    }

    public static class FTagHolder
    {
        /**
         * 是否需要拦截事件标识(用于onInterceptTouchEvent方法)
         */
        private boolean mTagIntercept = false;
        /**
         * 是否需要消费事件标识(用于onTouchEvent方法)
         */
        private boolean mTagConsume = false;

        private Callback mCallback;

        private FTagHolder()
        {
        }

        //---------- public method start ----------

        public void setCallback(Callback callback)
        {
            mCallback = callback;
        }

        public boolean isTagIntercept()
        {
            return mTagIntercept;
        }

        public boolean isTagConsume()
        {
            return mTagConsume;
        }

        //---------- public method end ----------

        /**
         * 设置是否需要拦截事件标识(用于onInterceptTouchEvent方法)
         *
         * @param tagIntercept
         */
        void setTagIntercept(boolean tagIntercept)
        {
            if (mTagIntercept != tagIntercept)
            {
                mTagIntercept = tagIntercept;
                if (mCallback != null)
                    mCallback.onTagInterceptChanged(tagIntercept);
            }
        }

        /**
         * 设置是否需要消费事件标识(用于onTouchEvent方法)
         *
         * @param tagConsume
         */
        void setTagConsume(boolean tagConsume)
        {
            if (mTagConsume != tagConsume)
            {
                mTagConsume = tagConsume;
                if (mCallback != null)
                    mCallback.onTagConsumeChanged(tagConsume);
            }
        }

        void reset()
        {
            setTagIntercept(false);
            setTagConsume(false);
        }

        public interface Callback
        {
            void onTagInterceptChanged(boolean tag);

            void onTagConsumeChanged(boolean tag);
        }
    }
}
