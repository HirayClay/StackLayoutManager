package com.hirayclay;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by CJJ on 2017/5/17.
 * my thought is simple：we assume the first item in the initial state is the base position ，
 * we only need to calculate the appropriate position{@link #left(int index)}for the given item
 * index with the given offset{@link #mTotalOffset}.After solve this thinking confusion ,this
 * layoutManager is easy to implement
 *
 * @author CJJ
 */

public class StackLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "StackLayoutManager";

    //the space unit for the stacked item
    private int mSpace = 60;
    //the offset unit,deciding current position(the sum of one child's width and one space)
    private int mUnit;
    //the counting variable ,record the total offset
    int mTotalOffset;
    private RecyclerView.Recycler recycler;
    private int maxStackCount = 4;//the max stacked item count;
    private int initialStackCount = 4;//initial stacked item
    private float secondaryScale = 0.8f;
    private float scaleRatio = 0.4f;
    private int initialOffset;
    private boolean initial;
    private ItemChangeListener mItemChangeListener;
    //the item position in the base position
    private int mCurrItem;

    public StackLayoutManager(Config config) {
        this();
        this.maxStackCount = config.maxStackCount;
        this.mSpace = config.space;
        this.initialStackCount = config.initialStackCount;
        this.secondaryScale = config.secondaryScale;
        this.scaleRatio = config.scaleRatio;
        this.mItemChangeListener = config.itemSelectedListener;
    }


     StackLayoutManager() {
        setAutoMeasureEnabled(true);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.recycler = recycler;
        detachAndScrapAttachedViews(recycler);
        //got the mUnit basing on the first child,of course we assume that  all the item has the same size
        View anchorView = recycler.getViewForPosition(0);
        measureChild(anchorView, 0, 0);
        mUnit = anchorView.getMeasuredWidth() + mSpace;
        //because this method will be called twice
        initialOffset = initialStackCount * mUnit;
        mCurrItem = initialStackCount;
        fill(recycler, 0);

    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (!initial) {
            fill(recycler, initialOffset);
            initial = true;
        }
    }

    /**
     * the magic function :).all the work including computing ,recycling,and layout is done here
     *
     * @param recycler ..
     */
    private int fill(RecyclerView.Recycler recycler, int dy) {
        if (mTotalOffset + dy < 0 || (mTotalOffset + dy + 0f) / mUnit > getItemCount() - 1)
            return 0;
        detachAndScrapAttachedViews(recycler);
        mTotalOffset += dy;
        int count = getChildCount();
        //removeAndRecycle  views
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null && shouldRecycle(child, dy))
                removeAndRecycleView(child, recycler);
        }


        int curPos = mTotalOffset / mUnit;
//        float n = (mTotalOffset + 0f) / mUnit;
//        float x = n % 1f;
        int start = curPos - maxStackCount >= 0 ? curPos - maxStackCount : 0;
        int end = curPos + maxStackCount > getItemCount() ? getItemCount() : curPos + maxStackCount;

        //layout view
        for (int i = start; i < end; i++) {
            View view = recycler.getViewForPosition(i);

            float scale = scale(i);
            float alpha = alpha(i);

            addView(view);
            measureChild(view, 0, 0);
            int left = (int) (left(i) - (1 - scale) * view.getMeasuredWidth() / 2);
            layoutDecoratedWithMargins(view, left, 0, left + view.getMeasuredWidth(), view.getMeasuredHeight());
            view.setAlpha(alpha);
            view.setScaleY(scale);
            view.setScaleX(scale);
        }
        Log.i(TAG, "fill Done here!");
        return dy;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        BaseSnapHelper.attach(view);
    }


    /******************************precise math method*******************************/
    private float alpha(int position) {
        float alpha;
        int curPos = mTotalOffset / mUnit;
        float n = (mTotalOffset + .0f) / mUnit;
        if (position > curPos)
            alpha = 1.0f;
        else {
            //temporary linear map,barely ok
            alpha = 1 - (n - position) / maxStackCount;
        }
        //for precise checking,oh may be kind of dummy
        return alpha <= 0.001f ? 0 : alpha;
    }

    private float scale(int position) {
        float scale;
        int curPos = this.mTotalOffset / mUnit;
        float n = (mTotalOffset + .0f) / mUnit;
        float x = n - curPos;
        // position >= curPos+1;
        if (position >= curPos) {
            if (position == curPos)
                scale = 1 - scaleRatio * x / maxStackCount;
            else if (position == curPos + 1)
            //let the item's (index:position+1) scale be 1 when the item slide 1/2 mUnit,
            // this have better visual effect
            {
//                scale = 0.8f + (0.4f * x >= 0.2f ? 0.2f : 0.4f * x);
                scale = secondaryScale + (x > 0.5f ? 1 - secondaryScale : 2 * (1 - secondaryScale) * x);
            } else scale = secondaryScale;
        } else {//position <= curPos
            if (position < curPos - maxStackCount)
                scale = 0f;
            else {
                scale = 1f - scaleRatio * (n - curPos + curPos - position) / maxStackCount;
            }
        }
        return scale;
    }

    /**
     * @param position the index of the item in the adapter
     * @return the appropriate left for the given item
     */
    private int left(int position) {

        int left;
        int curPos = mTotalOffset / mUnit;
        float n = (mTotalOffset + .0f) / mUnit;
        float x = n - curPos;
        if (position <= curPos) {

            if (position == curPos) {
                left = (int) (mSpace * (maxStackCount - x));
            } else {
                left = (int) (mSpace * (maxStackCount - x - (curPos - position)));

            }
        } else {

            if (position == curPos + 2) {
                float prevItemScale = scale(position - 1);
                left = (int) (mSpace * maxStackCount + position * mUnit - mTotalOffset - (1 - prevItemScale) * (mUnit - mSpace));

            } else {
                left = mSpace * maxStackCount + position * mUnit - mTotalOffset;

            }

            left = left <= 0 ? 0 : left;
        }
        return left;
    }

    /**
     * should recycle view with the given dy or say check if the
     * view is out of the bound after the dy is applied
     *
     * @param view ..
     * @param dy   ..
     * @return true if need recycle
     */
    private boolean shouldRecycle(View view/*int position*/, int dy) {
        return view.getLeft() - dy < 0 || view.getRight() - dy > getWidth();
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return fill(recycler, dx);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    public int getCurrItem() {
        return mCurrItem;
    }

    public int[] getSnapDistance(View targetView) {
        int snapPos = maxStackCount * mSpace;
        int left = targetView.getLeft();
        //        int o = ;
//        int s = mUnit - o;
//        int scrollX;
//        if (velocityX > 0) {
//            scrollX = s;
//        } else
//            scrollX = -o;
//        int dur = computeSettleDuration(Math.abs(scrollX), Math.abs(velocityX))/* (int) (3000f / Math.abs(velocityX) * duration)*/;
//        brewAndStartAnimator(dur, scrollX);
        int[] r = new int[2];
        r[1] = 0;
        int temp = left - snapPos;
        // if snap view is on the left of base position,we need to calculate the percent
        if (temp < 0) {
            int absDistance = Math.abs(temp);
            float percent = (absDistance + .0f) / mSpace;
            temp = -(int) (percent * mUnit);
        }
        r[0] = temp;
        Log.i(TAG, "getSnapDistance: " + r[0]);
        return r;
    }

    public View findSnapView() {
        if (mTotalOffset == 0)
            return getChildAt(0);
        int currPos = mTotalOffset / mUnit;
        boolean half = (mTotalOffset + .0f) % mUnit > mUnit / 2;
        int destPos;
        if (currPos >= maxStackCount) {
            if (half)
                destPos = maxStackCount;
            else destPos = maxStackCount - 1;
        } else {
            if (half)
                destPos = currPos + 1;
            else destPos = currPos;
        }
        Log.i(TAG, "findSnapView: "+destPos);
        return getChildAt(destPos);
    }
}
