package com.hirayclay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.support.v7.widget.RecyclerView.NO_POSITION;
import static com.hirayclay.Align.BOTTOM;
import static com.hirayclay.Align.LEFT;
import static com.hirayclay.Align.RIGHT;
import static com.hirayclay.Align.TOP;

/**
 * Created by CJJ on 2017/5/17.
 * my thought is simple：we assume the first item in the initial state is the base position ，
 * we only need to calculate the appropriate position{@link #left(int index)}for the given item
 * index with the given offset{@link #mTotalOffset}.After solve this thinking confusion ,this
 * layoutManager is easy to implement
 *
 * @author CJJ
 */

class StackLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "StackLayoutManager";

    //the space unit for the stacked item
    private int mSpace = 60;
    /**
     * the offset unit,deciding current position(the sum of  {@link #mItemWidth} and {@link #mSpace})
     */
    private int mUnit;
    //item width
    private int mItemWidth;
    private int mItemHeight;
    //the counting variable ,record the total offset including parallex
    private int mTotalOffset;
    //record the total offset without parallex
    private int mRealOffset;
    private ObjectAnimator animator;
    private int animateValue;
    private int duration = 300;
    private RecyclerView.Recycler recycler;
    private int lastAnimateValue;
    //the max stacked item count;
    private int maxStackCount = 4;
    //initial stacked item
    private int initialStackCount = 4;
    private float secondaryScale = 0.8f;
    private float scaleRatio = 0.4f;
    private float parallex = 1f;
    private int initialOffset;
    private boolean initial;
    private int mMinVelocityX;
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private int pointerId;
    private Align direction = LEFT;
    private RecyclerView mRV;
    private Method sSetScrollState;
    private int mPendingScrollPosition = NO_POSITION;

    StackLayoutManager(Config config) {
        this();
        this.maxStackCount = config.maxStackCount;
        this.mSpace = config.space;
        this.initialStackCount = config.initialStackCount;
        this.secondaryScale = config.secondaryScale;
        this.scaleRatio = config.scaleRatio;
        this.direction = config.align;
        this.parallex = config.parallex;
    }


    @SuppressWarnings("unused")
    public StackLayoutManager() {
        setAutoMeasureEnabled(true);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0)
            return;
        this.recycler = recycler;
        detachAndScrapAttachedViews(recycler);
        //got the mUnit basing on the first child,of course we assume that  all the item has the same size
        View anchorView = recycler.getViewForPosition(0);
        measureChildWithMargins(anchorView, 0, 0);
        mItemWidth = anchorView.getMeasuredWidth();
        mItemHeight = anchorView.getMeasuredHeight();
        if (canScrollHorizontally())
            mUnit = mItemWidth + mSpace;
        else mUnit = mItemHeight + mSpace;
        //because this method will be called twice
        initialOffset = resolveInitialOffset();
        mMinVelocityX = ViewConfiguration.get(anchorView.getContext()).getScaledMinimumFlingVelocity();
        fill(recycler, 0);

    }

    //we need take direction into account when calc initialOffset
    private int resolveInitialOffset() {
        int offset = initialStackCount * mUnit;
        if (mPendingScrollPosition != NO_POSITION) {
            offset = mPendingScrollPosition * mUnit;
            mPendingScrollPosition = NO_POSITION;
        }

        if (direction == LEFT)
            return offset;
        if (direction == RIGHT)
            return -offset;
        if (direction == TOP)
            return offset;
        else return offset;
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (!initial) {
            fill(recycler, initialOffset, false);
            initial = true;
        }
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        initial = false;
        mTotalOffset = mRealOffset = 0;
    }

    /**
     * the magic function :).all the work including computing ,recycling,and layout is done here
     *
     * @param recycler ...
     */
    private int fill(RecyclerView.Recycler recycler, int dy, boolean apply) {
        int delta = direction.layoutDirection * dy;
        // multiply the parallex factor
        if (apply)
            delta = (int) (delta * parallex);
        if (direction == LEFT)
            return fillFromLeft(recycler, delta);
        if (direction == RIGHT)
            return fillFromRight(recycler, delta);
        if (direction == TOP)
            return fillFromTop(recycler, delta);
        else return dy;//bottom alignment is not necessary,we don't support that
    }

    public int fill(RecyclerView.Recycler recycler, int dy) {
        return fill(recycler, dy, true);
    }

    private int fillFromTop(RecyclerView.Recycler recycler, int dy) {
        if (mTotalOffset + dy < 0 || (mTotalOffset + dy + 0f) / mUnit > getItemCount() - 1)
            return 0;
        detachAndScrapAttachedViews(recycler);
        mTotalOffset += direction.layoutDirection * dy;
        int count = getChildCount();
        //removeAndRecycle  views
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (recycleVertically(child, dy))
                removeAndRecycleView(child, recycler);
        }
        int currPos = mTotalOffset / mUnit;
        int leavingSpace = getHeight() - (left(currPos) + mUnit);
        int itemCountAfterBaseItem = leavingSpace / mUnit + 2;
        int e = currPos + itemCountAfterBaseItem;

        int start = currPos - maxStackCount >= 0 ? currPos - maxStackCount : 0;
        int end = e >= getItemCount() ? getItemCount() - 1 : e;

        int left = getWidth() / 2 - mItemWidth / 2;
        //layout views
        for (int i = start; i <= end; i++) {
            View view = recycler.getViewForPosition(i);

            float scale = scale(i);
            float alpha = alpha(i);

            addView(view);
            measureChildWithMargins(view, 0, 0);
            int top = (int) (left(i) - (1 - scale) * view.getMeasuredHeight() / 2);
            int right = view.getMeasuredWidth() + left;
            int bottom = view.getMeasuredHeight() + top;
            layoutDecoratedWithMargins(view, left, top, right, bottom);
            view.setAlpha(alpha);
            view.setScaleY(scale);
            view.setScaleX(scale);
        }

        return dy;
    }

    private int fillFromRight(RecyclerView.Recycler recycler, int dy) {

        if (mTotalOffset + dy < 0 || (mTotalOffset + dy + 0f) / mUnit > getItemCount() - 1)
            return 0;
        detachAndScrapAttachedViews(recycler);
        mTotalOffset += dy;
        int count = getChildCount();
        //removeAndRecycle  views
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (recycleHorizontally(child, dy))
                removeAndRecycleView(child, recycler);
        }


        int currPos = mTotalOffset / mUnit;
        int leavingSpace = left(currPos);
        int itemCountAfterBaseItem = leavingSpace / mUnit + 2;
        int e = currPos + itemCountAfterBaseItem;

        int start = currPos - maxStackCount <= 0 ? 0 : currPos - maxStackCount;
        int end = e >= getItemCount() ? getItemCount() - 1 : e;

        //layout view
        for (int i = start; i <= end; i++) {
            View view = recycler.getViewForPosition(i);

            float scale = scale(i);
            float alpha = alpha(i);

            addView(view);
            measureChildWithMargins(view, 0, 0);
            int left = (int) (left(i) - (1 - scale) * view.getMeasuredWidth() / 2);
            int top = 0;
            int right = left + view.getMeasuredWidth();
            int bottom = view.getMeasuredHeight();

            layoutDecoratedWithMargins(view, left, top, right, bottom);
            view.setAlpha(alpha);
            view.setScaleY(scale);
            view.setScaleX(scale);
        }

        return dy;
    }

    private int fillFromLeft(RecyclerView.Recycler recycler, int dy) {
        if (mTotalOffset + dy < 0 || (mTotalOffset + dy + 0f) / mUnit > getItemCount() - 1)
            return 0;
        detachAndScrapAttachedViews(recycler);
        mTotalOffset += direction.layoutDirection * dy;
        int count = getChildCount();
        //removeAndRecycle  views
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (recycleHorizontally(child, dy))
                removeAndRecycleView(child, recycler);
        }


        int currPos = mTotalOffset / mUnit;
        int leavingSpace = getWidth() - (left(currPos) + mUnit);
        int itemCountAfterBaseItem = leavingSpace / mUnit + 2;
        int e = currPos + itemCountAfterBaseItem;

        int start = currPos - maxStackCount >= 0 ? currPos - maxStackCount : 0;
        int end = e >= getItemCount() ? getItemCount() - 1 : e;

        //layout view
        for (int i = start; i <= end; i++) {
            View view = recycler.getViewForPosition(i);

            float scale = scale(i);
            float alpha = alpha(i);

            addView(view);
            measureChildWithMargins(view, 0, 0);
            int left = (int) (left(i) - (1 - scale) * view.getMeasuredWidth() / 2);
            int top = 0;
            int right = left + view.getMeasuredWidth();
            int bottom = top + view.getMeasuredHeight();
            layoutDecoratedWithMargins(view, left, top, right, bottom);
            view.setAlpha(alpha);
            view.setScaleY(scale);
            view.setScaleX(scale);
        }

        return dy;
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mVelocityTracker.addMovement(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (animator != null && animator.isRunning())
                    animator.cancel();
                pointerId = event.getPointerId(0);

            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (v.isPressed()) v.performClick();
                mVelocityTracker.computeCurrentVelocity(1000, 14000);
                float xVelocity = mVelocityTracker.getXVelocity(pointerId);
                int o = mTotalOffset % mUnit;
                int scrollX;
                if (Math.abs(xVelocity) < mMinVelocityX && o != 0) {
                    if (o >= mUnit / 2)
                        scrollX = mUnit - o;
                    else scrollX = -o;
                    int dur = (int) (Math.abs((scrollX + 0f) / mUnit) * duration);
                    Log.i(TAG, "onTouch: ======BREW===");
                    brewAndStartAnimator(dur, scrollX);
                }
            }
            return false;
        }

    };

    private RecyclerView.OnFlingListener mOnFlingListener = new RecyclerView.OnFlingListener() {
        @Override
        public boolean onFling(int velocityX, int velocityY) {
            int o = mTotalOffset % mUnit;
            int s = mUnit - o;
            int scrollX;
            int vel = absMax(velocityX, velocityY);
            if (vel * direction.layoutDirection > 0) {
                scrollX = s;
            } else
                scrollX = -o;
            int dur = computeSettleDuration(Math.abs(scrollX), Math.abs(vel));
            brewAndStartAnimator(dur, scrollX);
            setScrollStateIdle();
            return true;
        }
    };

    private int absMax(int a, int b) {
        if (Math.abs(a) > Math.abs(b))
            return a;
        else return b;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mRV = view;
        //check when raise finger and settle to the appropriate item
        view.setOnTouchListener(mTouchListener);

        view.setOnFlingListener(mOnFlingListener);
    }

    private int computeSettleDuration(int distance, float xvel) {
        float sWeight = 0.5f * distance / mUnit;
        float velWeight = xvel > 0 ? 0.5f * mMinVelocityX / xvel : 0;

        return (int) ((sWeight + velWeight) * duration);
    }

    private void brewAndStartAnimator(int dur, int finalXorY) {
        animator = ObjectAnimator.ofInt(StackLayoutManager.this, "animateValue", 0, finalXorY);
        animator.setDuration(dur);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                lastAnimateValue = 0;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                lastAnimateValue = 0;
            }
        });
    }

    /******************************precise math method*******************************/
    private float alpha(int position) {
        float alpha;
        int currPos = mTotalOffset / mUnit;
        float n = (mTotalOffset + .0f) / mUnit;
        if (position > currPos)
            alpha = 1.0f;
        else {
            //temporary linear map,barely ok
            alpha = 1 - (n - position) / maxStackCount;
        }
        //for precise checking,oh may be kind of dummy
        return alpha <= 0.001f ? 0 : alpha;
    }

    private float scale(int position) {
        switch (direction) {
            default:
            case LEFT:
            case RIGHT:
                return scaleDefault(position);
        }
    }

    private float scaleDefault(int position) {

        float scale;
        int currPos = this.mTotalOffset / mUnit;
        float n = (mTotalOffset + .0f) / mUnit;
        float x = n - currPos;
        // position >= currPos+1;
        if (position >= currPos) {
            if (position == currPos)
                scale = 1 - scaleRatio * (n - currPos) / maxStackCount;
            else if (position == currPos + 1)
            //let the item's (index:position+1) scale be 1 when the item slide 1/2 mUnit,
            // this have better visual effect
            {
//                scale = 0.8f + (0.4f * x >= 0.2f ? 0.2f : 0.4f * x);
                scale = secondaryScale + (x > 0.5f ? 1 - secondaryScale : 2 * (1 - secondaryScale) * x);
            } else scale = secondaryScale;
        } else {//position <= currPos
            if (position < currPos - maxStackCount)
                scale = 0f;
            else {
                scale = 1f - scaleRatio * (n - currPos + currPos - position) / maxStackCount;
            }
        }
        return scale;
    }

    /**
     * @param position the index of the item in the adapter
     * @return the accurate left position for the given item
     */
    private int left(int position) {


        int currPos = mTotalOffset / mUnit;
        int tail = mTotalOffset % mUnit;
        float n = (mTotalOffset + .0f) / mUnit;
        float x = n - currPos;

        switch (direction) {
            default:
            case LEFT:
            case TOP:
                //from left to right or top to bottom
                //these two scenario are actually same
                return ltr(position, currPos, tail, x);
            case RIGHT:
                return rtl(position, currPos, tail, x);
        }
    }

    /**
     * @param position ..
     * @param currPos  ..
     * @param tail     .. change
     * @param x        ..
     * @return the left position for given item
     */
    private int rtl(int position, int currPos, int tail, float x) {
        //虽然是做对称变换，但是必须考虑到scale给 对称变换带来的影响
        float scale = scale(position);
        int ltr = ltr(position, currPos, tail, x);
        return (int) (getWidth() - ltr - (mItemWidth) * scale);
    }

    private int ltr(int position, int currPos, int tail, float x) {
        int left;

        if (position <= currPos) {

            if (position == currPos) {
                left = (int) (mSpace * (maxStackCount - x));
            } else {
                left = (int) (mSpace * (maxStackCount - x - (currPos - position)));

            }
        } else {
            if (position == currPos + 1)
                left = mSpace * maxStackCount + mUnit - tail;
            else {
                float closestBaseItemScale = scale(currPos + 1);

                //调整因为scale导致的left误差
//                left = (int) (mSpace * maxStackCount + (position - currPos) * mUnit - tail
//                        -(position - currPos)*(mItemWidth) * (1 - closestBaseItemScale));

                int baseStart = (int) (mSpace * maxStackCount + mUnit - tail + closestBaseItemScale * (mUnit - mSpace) + mSpace);
                left = (int) (baseStart + (position - currPos - 2) * mUnit - (position - currPos - 2) * (1 - secondaryScale) * (mUnit - mSpace));
                if (BuildConfig.DEBUG)
                    Log.i(TAG, "ltr: currPos " + currPos
                            + "  pos:" + position
                            + "  left:" + left
                            + "   baseStart" + baseStart
                            + " currPos+1:" + left(currPos + 1));
            }
            left = left <= 0 ? 0 : left;
        }
        return left;
    }


    @SuppressWarnings("unused")
    public void setAnimateValue(int animateValue) {
        this.animateValue = animateValue;
        int dy = this.animateValue - lastAnimateValue;
        fill(recycler, direction.layoutDirection * dy, false);
        lastAnimateValue = animateValue;
    }

    @SuppressWarnings("unused")
    public int getAnimateValue() {
        return animateValue;
    }

    /**
     * should recycle view with the given dy or say check if the
     * view is out of the bound after the dy is applied
     *
     * @param view ..
     * @param dy   ..
     * @return ..
     */
    private boolean recycleHorizontally(View view/*int position*/, int dy) {
        return view != null && (view.getLeft() - dy < 0 || view.getRight() - dy > getWidth());
    }

    private boolean recycleVertically(View view, int dy) {
        return view != null && (view.getTop() - dy < 0 || view.getBottom() - dy > getHeight());
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return fill(recycler, dx);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return fill(recycler, dy);
    }

    @Override
    public boolean canScrollHorizontally() {
        return direction == LEFT || direction == RIGHT;
    }

    @Override
    public boolean canScrollVertically() {
        return direction == TOP || direction == BOTTOM;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    /**
     * we need to set scrollstate to {@link RecyclerView#SCROLL_STATE_IDLE} idle
     * stop RV from intercepting the touch event which block the item click
     */
    private void setScrollStateIdle() {
        try {
            if (sSetScrollState == null)
                sSetScrollState = RecyclerView.class.getDeclaredMethod("setScrollState", int.class);
            sSetScrollState.setAccessible(true);
            sSetScrollState.invoke(mRV, RecyclerView.SCROLL_STATE_IDLE);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void scrollToPosition(int position) {
        if (position > getItemCount() - 1) {
            Log.i(TAG, "position is " + position + " but itemCount is " + getItemCount());
            return;
        }
        int currPosition = mTotalOffset / mUnit;
        int distance = (position - currPosition) * mUnit;
        int dur = computeSettleDuration(Math.abs(distance), 0);
        brewAndStartAnimator(dur, distance);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        initial = false;
    }

    @SuppressWarnings("unused")
    public interface CallBack {

        float scale(int totalOffset, int position);

        float alpha(int totalOffset, int position);

        float left(int totalOffset, int position);
    }
}
