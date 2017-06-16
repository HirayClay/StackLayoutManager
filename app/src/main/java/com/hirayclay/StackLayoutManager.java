package com.hirayclay;

import android.support.annotation.FloatRange;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by CJJ on 2017/5/17.
 *
 * @author CJJ
 */

public class StackLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "StackLayoutManager";


    int currentPosition = 0;
    int interval = 60;
    int unit;
    int totalOffset;

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.i(TAG, "onLayoutChildren: ");
        detachAndScrapAttachedViews(recycler);
        int count = getItemCount();
        int width = getWidth();
        int height = getHeight();
        fill(recycler, state);

//        int start = 0;
//        if (currentPosition != 0)
//            start = currentPosition - 3 > 0 ? currentPosition - 3 : 0;
//        for (int i = start; i < count; i++) {
//            View view = recycler.getViewForPosition(i);
//            addView(view);
//            measureChildWithMargins(view, 0, 0);
//            int verticalSpace = getHeight() - getDecoratedMeasuredHeight(view);
//            int horizontalSpace = getWidth() - getDecoratedMeasuredWidth(view);
//            int childW = view.getMeasuredWidth();
//            int childH = view.getMeasuredHeight();
//
//            if (unit <= 0)
//                unit = childW + interval;
//            if (i == currentPosition) {
//                Log.i(TAG, "onLayoutChildren: i == currentPosition");
//                layoutDecoratedWithMargins(view, interval * 3, verticalSpace / 2, interval * 3 + childW, childH + verticalSpace / 2);
//            } else if (i < currentPosition) {
//                layoutDecoratedWithMargins(view, getLeftAtPosition(i), verticalSpace / 2, getLeftAtPosition(i) + childW, verticalSpace / 2 + childH);
//                float alpha = getAlphaAtPosition(i);
//                float scale = getScaleAtPosition(i);
//                Log.i(TAG, "onLayoutChildren: i< currentPosition alpha: " + alpha + "  scale:" + scale);
//                view.setAlpha(alpha);
//                view.setScaleY(scale);
//            } else {
//                if (i - currentPosition == 1) {
//                    int left = view.getMeasuredWidth() + interval * 4;
//                    float scale = getScaleAtPosition(i);
//                    float alpha = getAlphaAtPosition(i);
//                    Log.i(TAG, "onLayoutChildren: i< currentPosition left: " + left + "  scale:" + scale);
//                    layoutDecoratedWithMargins(view, left, verticalSpace / 2, left + childW, verticalSpace / 2 + childH);
//                    view.setAlpha(alpha);
//                    view.setScaleY(scale);
//                }
//            }
//        }

    }

    /**
     * @link {https://github.com/mcxtzhang/ZLayoutManager/blob/master/layoutmanager/src/main/java/com/mcxtzhang/layoutmanager/flow/FlowLayoutManager.java}
     * @param recycler
     * @param state
     */
    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {

    }

    public int getLeftAtPosition(int position) {
        int i = currentPosition - position;

        if (position == currentPosition)
            return interval * 3;

        if (position < currentPosition) {
            if (i == 1)
                return interval * 2;
            if (i == 2)
                return interval;
            if (i == 3)
                return 0;
        }

        return 0;
    }


    public float getAlphaAtPosition(int position) {
        int offset = currentPosition - position;
        if (offset > 0) {
            float alpha = 1 - 0.1f * offset;
            if (alpha <= 0.7f)
                return 0f;
            else return alpha;
        } else return 1.0f;

    }


    public float getScaleAtPosition(int position) {
        int offset = currentPosition - position;
        if (offset > 0) {
            float scale = 1 - 0.1f * offset;
            if (scale < 0.7f)
                return 0f;
            else return scale;
        } else return 0.9f;
    }

    /******************************precise math method*******************************/
    public float alpha(int position) {
        float alpha;
        int curPos = totalOffset / unit;
        float n = (totalOffset + 0.0f) / unit;
        if (position > curPos)
            alpha = 1.0f;
        else {
            float o = 1 - (n - position) / 3;
//            alpha = interpolator(n - position);
            alpha = o;
        }
        //for precise checking
        return alpha <= 0.001f ? 0 : alpha;
    }

    public float scale(int position) {
        float scale = 0f;
        int curPos = totalOffset / unit;
        float n = (totalOffset + 0.0f) / unit;
        // position = curPos+1;
        if (position > curPos) {
            scale = 1 - ((n - curPos) % 1) * 0.1f;
        } else {//position <= curPos
            if (position < curPos - 3)
                scale = 0f;
            else {
                scale = 1f - (curPos - position) / 3f;
            }
        }
        return scale;

    }

    /**
     * @param position the target position
     * @return
     */
    public int left(int position) {
        int left = 0;
        int curPos = totalOffset / unit;
        float n = (totalOffset + 0.0f) / unit;
        if (position <= curPos) {
            if (n >= 3f)
                left = 0;
            else {
                left = (int) (interval * (1 - n));
            }
        } else {
            left = interval * 3 + position * unit - totalOffset;
            left = left <= 0 ? 0 : left;
        }

        return left;
    }

    public float interpolator(@FloatRange(from = 0f, to = 1.0f) float input) {
        return (float) (Math.sqrt(input * input));
    }

    @Override
    public boolean canScrollHorizontally() {
//        Log.i(TAG, "canScrollHorizontally: ");
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.i(TAG, "scrollHorizontallyBy: " + dx);

        int consumeX = 0;
        int count = getChildCount();
        totalOffset += dx;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int position = getPosition(child);
            float alpha = alpha(position);
            float left = left(position);
            float scale = scale(position);
            Log.i(TAG, "scrollHorizontallyBy: position:" + position + "alpha:" + alpha + "---left:" + left + "---scale:" + scale);
            child.setAlpha(alpha);
            child.setScaleY(scale);
            child.offsetLeftAndRight((int) (left - child.getLeft()));
        }

        return dx;
    }


//    @Override
//    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
//        super.onMeasure(recycler, state, widthSpec, heightSpec);
//        int count = getChildCount();
//        int height = 0;
//        for (int i = 0; i < count; i++) {
//            View child = getChildAt(i);
//            measureChildWithMargins(child, widthSpec, heightSpec);
//            height = Math.max(height, child.getHeight());
//        }
//        setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), height);
//    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }
}
