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

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;

public class FGestureManager
{
    private final ViewGroup mViewGroup;

    private FTouchHelper mTouchHelper;
    private final TagHolder mTagHolder;
    private final FScroller mScroller;

    private State mState = State.Idle;
    private LifecycleInfo mLifecycleInfo;

    private IdleRunnable mIdleRunnable;
    private VelocityTracker mVelocityTracker;

    private boolean mDebug;

    private final Callback mCallback;

    public FGestureManager(ViewGroup viewGroup, Callback callback)
    {
        if (viewGroup == null || callback == null)
            throw new NullPointerException();

        mViewGroup = viewGroup;
        mCallback = callback;

        mTagHolder = new TagHolder()
        {
            @Override
            protected void onTagConsumeChanged(boolean tag)
            {
                if (tag)
                    setState(State.Consume);

                super.onTagConsumeChanged(tag);
            }
        };

        mScroller = new FScroller(viewGroup.getContext())
        {
            @Override
            protected void onScrollerStart()
            {
                if (mDebug)
                    Log.i(FGestureManager.class.getSimpleName(), "onScrollerStart");

                setState(State.Fling);
                super.onScrollerStart();
            }

            @Override
            protected void onScrollerCompute(int lastX, int lastY, int currX, int currY)
            {
                mCallback.onScrollerCompute(lastX, lastY, currX, currY);
                super.onScrollerCompute(lastX, lastY, currX, currY);
            }

            @Override
            protected void onScrollerFinish(boolean isAbort)
            {
                if (mDebug)
                    Log.e(FGestureManager.class.getSimpleName(), "onScrollerFinish isAbort:" + isAbort);

                if (mTagHolder.isTagConsume())
                {
                    setState(State.Consume);
                } else
                {
                    if (isAbort)
                        postIdleRunnable();
                    else
                        setState(State.Idle);
                }

                super.onScrollerFinish(isAbort);
            }
        };
    }

    public void setDebug(boolean debug)
    {
        mDebug = debug;
    }

    public FTouchHelper getTouchHelper()
    {
        if (mTouchHelper == null)
            mTouchHelper = new FTouchHelper();
        return mTouchHelper;
    }

    public TagHolder getTagHolder()
    {
        return mTagHolder;
    }

    public FScroller getScroller()
    {
        return mScroller;
    }

    public State getState()
    {
        return mState;
    }

    public LifecycleInfo getLifecycleInfo()
    {
        if (mLifecycleInfo == null)
            mLifecycleInfo = new LifecycleInfo();
        return mLifecycleInfo;
    }

    private void setState(State state)
    {
        if (state == null)
            throw new NullPointerException();

        if (mDebug)
            Log.i(FGestureManager.class.getSimpleName(), "setState:" + mState + " -> " + state);

        cancelIdleRunnable();

        final State old = mState;
        if (old != state)
        {
            mState = state;
            mCallback.onStateChanged(old, state);
        }
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

    private void postIdleRunnable()
    {
        cancelIdleRunnable();
        mIdleRunnable = new IdleRunnable(mState);
        mIdleRunnable.post();
    }

    private void cancelIdleRunnable()
    {
        if (mIdleRunnable != null)
        {
            mIdleRunnable.cancel();
            mIdleRunnable = null;
        }
    }

    /**
     * 取消消费事件
     */
    public void cancelConsumeEvent()
    {
        if (mTagHolder.isTagConsume())
        {
            if (mDebug)
                Log.i(FGestureManager.class.getSimpleName(), "cancelConsumeEvent");

            getLifecycleInfo().setCancelConsumeEvent(true);

            if (getScroller().isFinished())
            {
                /**
                 * 调用取消消费事件方法之后，外部有可能立即调用滚动的方法变更状态为{@link State.Fling}
                 * 所以此处延迟设置{@link State.Idle}状态
                 */
                postIdleRunnable();
            }

            mTagHolder.reset();
            mCallback.onCancelConsumeEvent();
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
        getTouchHelper().processTouchEvent(event);
        getVelocityTracker().addMovement(event);

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
        {
            onEventFinish(event);
        } else
        {
            if (action == MotionEvent.ACTION_DOWN)
                onEventStart(event);

            if (!mTagHolder.isTagIntercept())
                mTagHolder.setTagIntercept(mCallback.shouldInterceptEvent(event));
        }

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
        getTouchHelper().processTouchEvent(event);
        getVelocityTracker().addMovement(event);

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
            if (!getLifecycleInfo().isCancelConsumeEvent())
            {
                if (!mTagHolder.isTagConsume())
                {
                    mTagHolder.setTagConsume(mCallback.shouldConsumeEvent(event));
                } else
                {
                    mCallback.onEventConsume(event);
                    getLifecycleInfo().setHasConsumeEvent(true);
                }
            }
        }

        return mTagHolder.isTagConsume();
    }

    private void onEventStart(MotionEvent event)
    {

    }

    private void onEventFinish(MotionEvent event)
    {
        mTagHolder.reset();
        mCallback.onEventFinish(getVelocityTracker(), event);

        releaseVelocityTracker();
        getLifecycleInfo().reset();

        if (mState == State.Consume)
            setState(State.Idle);
    }

    private final class IdleRunnable implements Runnable
    {
        private final State mLastState;
        private boolean mPost;

        public IdleRunnable(State state)
        {
            mLastState = state;
        }

        @Override
        public void run()
        {
            mPost = false;
            if (mIdleRunnable == this)
                mIdleRunnable = null;

            if (mState == mLastState)
            {
                if (mDebug)
                    Log.i(FGestureManager.class.getSimpleName(), "IdleRunnable run:" + this);

                setState(State.Idle);
            }
        }

        public void post()
        {
            cancel();
            mViewGroup.post(this);
            mPost = true;

            if (mDebug)
                Log.i(FGestureManager.class.getSimpleName(), "IdleRunnable post:" + this);
        }

        public void cancel()
        {
            mViewGroup.removeCallbacks(this);

            if (mPost)
            {
                if (mDebug)
                    Log.i(FGestureManager.class.getSimpleName(), "IdleRunnable cancel:" + this);
            }
        }
    }

    public static final class LifecycleInfo
    {
        private boolean mHasConsumeEvent;
        private boolean mIsCancelConsumeEvent;

        /**
         * 从按下到当前{@link Callback#onEventConsume(MotionEvent)}方法是否消费过事件
         *
         * @return
         */
        public boolean hasConsumeEvent()
        {
            return mHasConsumeEvent;
        }

        /**
         * 是否取消过消费事件
         *
         * @return
         */
        public boolean isCancelConsumeEvent()
        {
            return mIsCancelConsumeEvent;
        }

        void setHasConsumeEvent(boolean has)
        {
            mHasConsumeEvent = has;
        }

        void setCancelConsumeEvent(boolean cancel)
        {
            mIsCancelConsumeEvent = cancel;
        }

        void reset()
        {
            mHasConsumeEvent = false;
            mIsCancelConsumeEvent = false;
        }
    }

    public enum State
    {
        /**
         * 空闲
         */
        Idle,
        /**
         * 消费事件
         */
        Consume,
        /**
         * Scroller滚动
         */
        Fling
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
         */
        public abstract void onEventConsume(MotionEvent event);

        /**
         * 取消消费事件回调
         */
        public void onCancelConsumeEvent()
        {
        }

        /**
         * 事件结束，收到{@link MotionEvent#ACTION_UP}或者{@link MotionEvent#ACTION_CANCEL}事件
         *
         * @param velocityTracker 速率计算对象，这里返回的对象还未进行速率计算，如果要获得速率需要先进行计算{@link VelocityTracker#computeCurrentVelocity(int)}
         * @param event           {@link MotionEvent#ACTION_UP}或者{@link MotionEvent#ACTION_CANCEL}
         */
        public abstract void onEventFinish(VelocityTracker velocityTracker, MotionEvent event);

        /**
         * 状态变化回调{@link State}
         *
         * @param oldState
         * @param newState
         */
        public abstract void onStateChanged(State oldState, State newState);

        public abstract void onScrollerCompute(int lastX, int lastY, int currX, int currY);
    }

    //---------- TagHolder Start ----------

    public static class TagHolder
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

        private TagHolder()
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
         * @param tag
         */
        void setTagIntercept(boolean tag)
        {
            if (mTagIntercept != tag)
            {
                mTagIntercept = tag;
                onTagInterceptChanged(tag);
            }
        }

        /**
         * 设置是否需要消费事件标识(用于onTouchEvent方法)
         *
         * @param tag
         */
        void setTagConsume(boolean tag)
        {
            if (mTagConsume != tag)
            {
                mTagConsume = tag;
                onTagConsumeChanged(tag);
            }
        }

        void reset()
        {
            setTagIntercept(false);
            setTagConsume(false);
        }

        protected void onTagInterceptChanged(boolean tag)
        {
            if (mCallback != null)
                mCallback.onTagInterceptChanged(tag);
        }

        protected void onTagConsumeChanged(boolean tag)
        {
            if (mCallback != null)
                mCallback.onTagConsumeChanged(tag);
        }

        public interface Callback
        {
            void onTagInterceptChanged(boolean tag);

            void onTagConsumeChanged(boolean tag);
        }
    }

    //---------- TagHolder Start ----------
}
