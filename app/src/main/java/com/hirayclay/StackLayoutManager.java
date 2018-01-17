package com.hirayclay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.State;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.List;

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

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final int INVALID_OFFSET = Integer.MIN_VALUE;

    public static final int NO_POSITION = -1;

    public static final long NO_ID = -1;

    public static final int INVALID_TYPE = -1;

    public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;

    public static final int VERTICAL = OrientationHelper.VERTICAL;

    private OrientationHelper mOrientationHelper;

    private LayoutState mLayoutState;
    private State mState;
    private AnchorInfo mAnchorInfo = new AnchorInfo();
    /**
     * Stashed to avoid allocation, currently only used in #fill()
     */
    private final LayoutChunkResult mLayoutChunkResult = new LayoutChunkResult();

    //the space unit for the stacked item
    private int mSpace = 60;
    /**
     * the offset unit,deciding current position(the sum of  {@link #mItemWidth} and {@link #mSpace})
     */
    private int mUnit;
    //item width
    private int mItemWidth;
    private int mItemHeight;
    //the counting variable ,record the total offset
    private int mTotalOffset;
    private ObjectAnimator animator;
    private int animateValue;
    private int duration = 300;
    private RecyclerView.Recycler mRecycler;
    private int lastAnimateValue;
    //the max stacked item count;
    private int maxStackCount = 4;
    //initial stacked item
    private int initialStackCount = 4;
    private float secondaryScale = 0.8f;
    private float scaleRatio = 0.4f;
    private int initialOffset;
    private boolean initial;
    private int mMinVelocityX;
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private Align direction = LEFT;
    private int mOrientation = HORIZONTAL;
    /**
     * Defines if layout should be calculated from end to start.
     *
     * @see #mShouldReverseLayout
     */
    private boolean mReverseLayout = false;
    /**
     * Works the same way as {@link android.widget.AbsListView#setStackFromBottom(boolean)} and
     * it supports both orientations.
     * see {@link android.widget.AbsListView#setStackFromBottom(boolean)}
     */
    private boolean mStackFromEnd = false;
    /**
     * This keeps the final value for how LayoutManager should start laying out views.
     */
    private boolean mShouldReverseLayout = false;

    /**
     * 主要是记录布局方向的变化，对于确定锚点比较有作用（比如布局发生变化之后，那么锚点信息的确定
     * 就不能再参照children了
     * {@link #updateAnchorFromChildren(RecyclerView.Recycler, State, AnchorInfo)}）
     * We need to track this so that we can ignore current position when it changes.
     */
    private boolean mLastStackFromEnd;

    private View.OnTouchListener mTouchListener = new TouchDealer();
    private RecyclerView.OnFlingListener mOnFlingListener = new FlingDealer();

    private class TouchDealer implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            mVelocityTracker.addMovement(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (animator != null && animator.isRunning())
                    animator.cancel();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (v.isPressed()) v.performClick();
                mVelocityTracker.computeCurrentVelocity(1000, 14000);
                float xVelocity = mVelocityTracker.getXVelocity();
                int o = mTotalOffset % mUnit;
                int scrollX;
                if (Math.abs(xVelocity) < mMinVelocityX && o != 0) {
                    if (o >= mUnit / 2)
                        scrollX = mUnit - o;
                    else scrollX = -o;
                    int dur = (int) (Math.abs((scrollX + 0f) / mUnit) * duration);
                    brewAndStartAnimator(dur, scrollX);
                }
            }
            return false;
        }
    }

    private class FlingDealer extends RecyclerView.OnFlingListener {

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
            return true;
        }
    }


    StackLayoutManager(Config config) {
        this();
        this.maxStackCount = config.maxStackCount;
        this.mSpace = config.space;
        this.initialStackCount = config.initialStackCount;
        this.secondaryScale = config.secondaryScale;
        this.scaleRatio = config.scaleRatio;
        this.direction = config.align;
    }

    @SuppressWarnings("unused")
    public StackLayoutManager() {
        setAutoMeasureEnabled(true);
    }

    /**
     * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    /**
     * Used to reverse item traversal and layout order.
     * This behaves similar to the layout change for RTL views. When set to true, first item is
     * laid out at the end of the UI, second item is laid out before it etc.
     * <p>
     * For horizontal layouts, it depends on the layout direction.
     * When set to true, If {@link android.support.v7.widget.RecyclerView} is LTR, than it will
     * layout from RTL, if {@link android.support.v7.widget.RecyclerView}} is RTL, it will layout
     * from LTR.
     */
    public void setReverseLayout(boolean reverseLayout) {
        if (mReverseLayout == reverseLayout)
            return;
        mReverseLayout = reverseLayout;
        requestLayout();
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, State state) {
        //we cache mRecycler mState for animator
        mRecycler = recycler;
        mState = state;
        if (state.getItemCount() == 0 && state.isPreLayout())
            return;
        // resolve layout direction
        resolveShouldLayoutReverse();
        //assume that every item has same size ;this is the precondition of StackLayoutManager
        ensureContractSizeAndEtc(recycler);
        ensureLayoutState();
        if (!mAnchorInfo.mValid) {
            updateAnchorInfoForLayout(recycler, state, mAnchorInfo);
            mAnchorInfo.mValid = true;
            mAnchorInfo.mLayoutFromEnd = mReverseLayout ^ mStackFromEnd;
        }
        detachAndScrapAttachedViews(recycler);

        int extraForStart = 0;
        int extraForEnd = 0;
        int startOffset;
        int endOffset;
        //

        if (mAnchorInfo.mLayoutFromEnd) {
            //todo do later
        } else {
            updateLayoutToFillEnd(mAnchorInfo);
            mLayoutState.mExtra = extraForEnd;
            fill(recycler, mLayoutState, state, false);
            endOffset = mLayoutState.mOffset;
            if (mLayoutState.mAvailable > 0)
                extraForStart += mLayoutState.mAvailable;

//            updateLayoutToFillStart(mAnchorInfo);

        }

        //we record the new size after pre-layout
        if (!state.isPreLayout())
            mOrientationHelper.onLayoutComplete();
        else {
            //this layout pass is done ,reset anchor for next pass if there is
            mAnchorInfo.reset();
        }

    }

    @Override
    public void onLayoutCompleted(State state) {
        super.onLayoutCompleted(state);
        mAnchorInfo.reset();
    }

    private void ensureContractSizeAndEtc(RecyclerView.Recycler recycler) {
        View anchorView = recycler.getViewForPosition(0);
        measureChildWithMargins(anchorView, 0, 0);
        mItemWidth = anchorView.getMeasuredWidth();
        mItemHeight = anchorView.getMeasuredHeight();
        initialOffset = initialStackCount * mUnit;
        mMinVelocityX = ViewConfiguration.get(anchorView.getContext()).getScaledMinimumFlingVelocity();
        if (canScrollHorizontally())
            mUnit = mItemWidth + mSpace;
        else mUnit = mItemHeight + mSpace;
    }

    @Google
    private void updateAnchorInfoForLayout(RecyclerView.Recycler recycler, State state, AnchorInfo anchorInfo) {
        //todo
        //pending state might support in future
        //and we try update anchor from pending data,but now no

        //from children
        if (updateAnchorFromChildren(recycler, state, anchorInfo))
            return;

        //fall back to padding
        //this case happens when first layout  or LayoutManager changed
        anchorInfo.assignCoordinateFromPadding();
        anchorInfo.mPosition = mStackFromEnd ? state.getItemCount() - 1 : 0;
    }

    @Google
    private void updateLayoutToFillEnd(AnchorInfo mAnchorInfo) {
        int itemPosition = mAnchorInfo.mPosition;
        int coordinate = mAnchorInfo.mCoordinate;
        mLayoutState.mItemDirection = mShouldReverseLayout ? LayoutState.ITEM_DIRECTION_HEAD :
                LayoutState.ITEM_DIRECTION_TAIL;
        mLayoutState.mLayoutDirection = LayoutState.LAYOUT_END;
        mLayoutState.mAvailable = mOrientationHelper.getEndAfterPadding() - coordinate;
        mLayoutState.mCurrentPosition = itemPosition;
        mLayoutState.mOffset = coordinate;
    }


    @Google
    private void resolveShouldLayoutReverse() {
        if (mOrientation == VERTICAL || !isLayoutRTL()) {
            mShouldReverseLayout = mReverseLayout;
        } else {
            mShouldReverseLayout = !mReverseLayout;
        }
    }

    private boolean isLayoutRTL() {
        return getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    @Google
    private boolean updateAnchorFromChildren(RecyclerView.Recycler recycler, State state, AnchorInfo anchorInfo) {
        if (getChildCount() == 0) {
            return false;
        }
        //todo  take focused child into account
//        final View focused = getFocusedChild();
//        if (focused != null && anchorInfo.isViewValidAsAnchor(focused, state)) {
//            anchorInfo.assignFromViewAndKeepVisibleRect(focused);
//            return true;
//        }

        //layout direction changed,we cannot update anchor from children
        if (mLastStackFromEnd != mStackFromEnd) {
            return false;
        }
        View referenceChild = anchorInfo.mLayoutFromEnd
                ? findReferenceChildClosestToEnd(recycler, state)
                : findReferenceChildClosestToStart(recycler, state);
        if (referenceChild != null) {
            anchorInfo.assignFromView(referenceChild);
            // If all visible views are removed in 1 pass, reference child might be out of bounds.
            // If that is the case, offset it back to 0 so that we use these pre-layout children.
            if (!state.isPreLayout() && supportsPredictiveItemAnimations()) {
                // validate this child is at least partially visible. if not, offset it to start
                final boolean notVisible =
                        mOrientationHelper.getDecoratedStart(referenceChild) >= mOrientationHelper
                                .getEndAfterPadding()
                                || mOrientationHelper.getDecoratedEnd(referenceChild)
                                < mOrientationHelper.getStartAfterPadding();
                if (notVisible) {
                    anchorInfo.mCoordinate = anchorInfo.mLayoutFromEnd
                            ? mOrientationHelper.getEndAfterPadding()
                            : mOrientationHelper.getStartAfterPadding();
                }
            }
            return true;
        }
        return false;
    }

    private View findReferenceChildClosestToStart(RecyclerView.Recycler recycler, State state) {
        return mShouldReverseLayout ? findLastReferenceChild(recycler, state) :
                findFirstReferenceChild(recycler, state);
    }

    private View findReferenceChildClosestToEnd(RecyclerView.Recycler recycler, State state) {
        return mShouldReverseLayout ? findFirstReferenceChild(recycler, state) :
                findLastReferenceChild(recycler, state);
    }

    private View findFirstReferenceChild(RecyclerView.Recycler recycler, RecyclerView.State state) {
        return findReferenceChild(recycler, state, 0, getChildCount(), state.getItemCount());
    }

    private View findLastReferenceChild(RecyclerView.Recycler recycler, RecyclerView.State state) {
        return findReferenceChild(recycler, state, getChildCount() - 1, -1, state.getItemCount());
    }

    private View findReferenceChild(RecyclerView.Recycler recycler, RecyclerView.State state,
                                    int start, int end, int itemCount) {
        ensureLayoutState();
        View invalidMatch = null;
        View outOfBoundsMatch = null;
        final int boundsStart = mOrientationHelper.getStartAfterPadding();
        final int boundsEnd = mOrientationHelper.getEndAfterPadding();
        final int diff = end > start ? 1 : -1;
        for (int i = start; i != end; i += diff) {
            final View view = getChildAt(i);
            final int position = getPosition(view);
            if (position >= 0 && position < itemCount) {
                if (((LayoutParams) view.getLayoutParams()).isItemRemoved()) {
                    if (invalidMatch == null) {
                        invalidMatch = view; // removed item, least preferred
                    }
                } else if (mOrientationHelper.getDecoratedStart(view) >= boundsEnd
                        || mOrientationHelper.getDecoratedEnd(view) < boundsStart) {
                    if (outOfBoundsMatch == null) {
                        outOfBoundsMatch = view; // item is not visible, less preferred
                    }
                } else {
                    return view;
                }
            }
        }
        return outOfBoundsMatch != null ? outOfBoundsMatch : invalidMatch;
    }

    private void fill(RecyclerView.Recycler recycler, LayoutState layoutState, State state, boolean stopOnFocuse) {

        int start = layoutState.mAvailable;

        int remainingSpace = layoutState.mAvailable + layoutState.mExtra;
        while (remainingSpace > 0 && layoutState.hasMore(state)) {
            mLayoutChunkResult.resetInternal();

            layoutChunk(recycler, state, layoutState, mLayoutChunkResult);
        }

    }

    private void layoutChunk(RecyclerView.Recycler recycler, State state,
                             LayoutState layoutState, LayoutChunkResult result) {
        View view = layoutState.next(recycler);
        if (view == null) {
            if (DEBUG && layoutState.mScrapList == null) {
                throw new RuntimeException("received null view when unexpected");
            }
            // if we are laying out views in scrap, this may return null which means there is
            // no more items to layout.
            result.mFinished = true;
            return;
        }
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (layoutState.mScrapList == null) {
            if (mShouldReverseLayout == (layoutState.mLayoutDirection
                    == LayoutState.LAYOUT_START)) {
                addView(view);
            } else {
                addView(view, 0);
            }
        } else {
            if (mShouldReverseLayout == (layoutState.mLayoutDirection
                    == LayoutState.LAYOUT_START)) {
                addDisappearingView(view);
            } else {
                addDisappearingView(view, 0);
            }
        }
        measureChildWithMargins(view, 0, 0);
        result.mConsumed = mOrientationHelper.getDecoratedMeasurement(view);
        int left, top, right, bottom;
        if (mOrientation == VERTICAL) {
            if (isLayoutRTL()) {
                right = getWidth() - getPaddingRight();
                left = right - mOrientationHelper.getDecoratedMeasurementInOther(view);
            } else {
                left = getPaddingLeft();
                right = left + mOrientationHelper.getDecoratedMeasurementInOther(view);
            }
            if (layoutState.mLayoutDirection == LayoutState.LAYOUT_START) {
                bottom = layoutState.mOffset;
                top = layoutState.mOffset - result.mConsumed;
            } else {
                top = layoutState.mOffset;
                bottom = layoutState.mOffset + result.mConsumed;
            }
        } else {
            top = getPaddingTop();
            bottom = top + mOrientationHelper.getDecoratedMeasurementInOther(view);

            if (layoutState.mLayoutDirection == LayoutState.LAYOUT_START) {
                right = layoutState.mOffset;
                left = layoutState.mOffset - result.mConsumed;
            } else {
                left = layoutState.mOffset;
                right = layoutState.mOffset + result.mConsumed;
            }
        }
        // We calculate everything with View's bounding box (which includes decor and margins)
        // To calculate correct layout position, we subtract margins.
        layoutDecoratedWithMargins(view, left, top, right, bottom);
        if (DEBUG) {
            Log.d(TAG, "laid out child at position " + getPosition(view) + ", with l:"
                    + (left + params.leftMargin) + ", t:" + (top + params.topMargin) + ", r:"
                    + (right - params.rightMargin) + ", b:" + (bottom - params.bottomMargin));
        }
        // Consume the available space if the view is not removed OR changed
        if (params.isItemRemoved() || params.isItemChanged()) {
            result.mIgnoreConsumed = true;
        }
        result.mFocusable = view.hasFocusable();
    }


    @Override
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
        //when items(before the base position) removed
        //we update the offset to keep consistence
//        mTotalOffset -= itemCount * mUnit;
    }

    /**
     * the magic function :).all the work including computing ,recycling,and layout is done here
     *
     * @param recycler ...
     * @param state
     */
    private int fill(RecyclerView.Recycler recycler, State state, int dy) {
        if ((state != null && state.isPreLayout()) || getItemCount() == 0)
            return -1;
        int delta = direction.layoutDirection * dy;
        if (direction == LEFT)
            return fillFromLeft(recycler, state, delta);
        else return dy;
    }

    private int fillFromLeft(RecyclerView.Recycler recycler, State state, int dy) {
        if (mTotalOffset + dy < 0 || (mTotalOffset + dy + 0f) / mUnit > getItemCount() - 1)
            return 0;
//        detachAndScrapAttachedViews(mRecycler);
        mTotalOffset += direction.layoutDirection * dy;
        ensureLayoutState();
        mLayoutState.prepareStartAndEnd();
        layoutChildrenByLayoutState(recycler, state);
        return dy;
    }

    private void layoutChildrenByLayoutState(RecyclerView.Recycler recycler, State state) {

        while (mLayoutState.hasMore(state)) {
            View view = mLayoutState.next(recycler);
            int position = getPosition(view);
            float scale = scale(position);
            float alpha = alpha(position);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int[] bounds = getBounds(position, scale);
            layoutDecoratedWithMargins(view, bounds[0], bounds[1], bounds[2], bounds[3]);
            view.setAlpha(alpha);
            view.setScaleY(scale);
            view.setScaleX(scale);
        }

    }

    private int[] getBounds(int position, float scale) {
        int[] bounds = new int[4];
        int left = (int) (left(position) - (1 - scale) * mItemWidth / 2);
        int top = 0;
        int right = left + mItemWidth;
        int bottom = top + mItemWidth;
        bounds[0] = left;
        bounds[1] = top;
        bounds[2] = right;
        bounds[3] = bottom;
        return bounds;
    }

    private void ensureLayoutState() {
        if (mLayoutState == null)
            mLayoutState = new LayoutState();
        if (mOrientationHelper == null)
            mOrientationHelper = OrientationHelper.createOrientationHelper(this, mOrientation);
    }

    private int absMax(int a, int b) {
        if (Math.abs(a) > Math.abs(b))
            return a;
        else return b;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        //check when raise finger and settle to the appropriate item
        view.setOnTouchListener(mTouchListener);

        view.setOnFlingListener(mOnFlingListener);
    }

    private int computeSettleDuration(int distance, float xvel) {
        float sWeight = 0.5f * distance / mUnit;
        float velWeight = 0.5f * mMinVelocityX / xvel;

        return (int) ((sWeight + velWeight) * duration);
    }

    private void brewAndStartAnimator(int dur, int finalX) {
        animator = ObjectAnimator.ofInt(StackLayoutManager.this, "animateValue", 0, finalX);
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
        switch (direction) {
            default:
            case LEFT:
            case RIGHT:
                return scaleDefault(position);
        }
    }

    private float scaleDefault(int position) {

        float scale;
        int curPos = this.mTotalOffset / mUnit;
        float n = (mTotalOffset + .0f) / mUnit;
        float x = n - curPos;
        // position >= curPos+1;
        if (position >= curPos) {
            if (position == curPos)
                scale = 1 - scaleRatio * (n - curPos) / maxStackCount;
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
     * @return the accurate left position for the given item
     */
    private int left(int position) {
        int curPos = mTotalOffset / mUnit;
        int tail = mTotalOffset % mUnit;
        float n = (mTotalOffset + .0f) / mUnit;
        float x = n - curPos;

        switch (direction) {
            default:
            case LEFT:
            case TOP:
                //from left to right or top to bottom
                //these two cases are actually same
                return ltr(position, curPos, tail, x);
            case RIGHT:
                return rtl(position, curPos, tail, x);
        }
    }

    /**
     * @param position ..
     * @param curPos   ..
     * @param tail     .. change
     * @param x        ..
     * @return the left position for given item
     */
    private int rtl(int position, int curPos, int tail, float x) {
        //we need take the effect that scale has on symmetry transformation into account,
        //now we can get the accurate position for right-to-left
        float scale = scale(position);
        int ltr = ltr(position, curPos, tail, x);
        return (int) (getWidth() - ltr - (mItemWidth) * scale);
    }

    private int ltr(int position, int curPos, int tail, float x) {
        int left;
        if (position <= curPos) {
            if (position == curPos) {
                left = (int) (mSpace * (maxStackCount - x));
            } else {
                left = (int) (mSpace * (maxStackCount - x - (curPos - position)));
            }
        } else {
            if (position == curPos + 1)
                left = mSpace * maxStackCount + mUnit - tail;
            else {
                float closestBaseItemScale = scale(curPos + 1);
                int baseStart = (int) (mSpace * maxStackCount + mUnit - tail + closestBaseItemScale * (mUnit - mSpace) + mSpace);
                left = (int) (baseStart + (position - curPos - 2) * mUnit - (position - curPos - 2) * (1 - secondaryScale) * (mUnit - mSpace));
                if (BuildConfig.DEBUG)
                    Log.i(TAG, "ltr: curPos " + curPos
                            + "  pos:" + position
                            + "  left:" + left
                            + "   baseStart" + baseStart
                            + " curPos+1:" + left(curPos + 1));
            }
            left = left <= 0 ? 0 : left;
        }
        return left;
    }


    @SuppressWarnings("unused")
    public void setAnimateValue(int animateValue) {
        this.animateValue = animateValue;
        int dy = this.animateValue - lastAnimateValue;
        RecyclerView recyclerView;
        fill(mRecycler, mState, direction.layoutDirection * dy);
        lastAnimateValue = animateValue;
    }

    @SuppressWarnings("unused")
    public int getAnimateValue() {
        return animateValue;
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, State state) {

        return fill(recycler, state, dx);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, State state) {
        return fill(recycler, state, dy);
    }

    @Override
    public boolean canScrollHorizontally() {
        return direction == LEFT || direction == RIGHT;
    }

    @Override
    public boolean canScrollVertically() {
        return direction == TOP;
    }

    @Override
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }


    /**
     * @param recycler    {@link android.support.v7.widget.RecyclerView.Recycler}
     * @param layoutState {@link LayoutState}
     */
    private void recycleViewByLayoutState(RecyclerView.Recycler recycler, LayoutState layoutState) {
        //just return,it is now in layout pass
        if (!layoutState.mRecycle)
            return;
        if (canScrollHorizontally()) {

        } else {

        }
    }

    // **********************helper class below**********************************//

    @SuppressWarnings("unused")
    public interface CallBack {

        float scale(int totalOffset, int position);

        float alpha(int totalOffset, int position);

        float left(int totalOffset, int position);
    }

    /**
     * save some layout info,such as scroll direction,
     * optimizing bullshit code
     */
    @Google
    private class LayoutState {
        static final int LAYOUT_START = -1;

        static final int LAYOUT_END = 1;

        static final int ITEM_DIRECTION_HEAD = -1;

        static final int ITEM_DIRECTION_TAIL = 1;
        /**
         * scroll from end to start(e.g. from bottom to top when vertical or right to left when horizontal)
         */
        public static final int SCROLL_END = 1;
        /**
         * scroll from start to end
         */
        public static final int SCROLL_START = -1;

        /**
         * decide whether recycle or not
         */
        public boolean mRecycle = true;
        /**
         * views that we  use first
         */
        private List<RecyclerView.ViewHolder> mScrapList = null;
        /**
         * {@link #SCROLL_END} or {@link #SCROLL_START}
         */
        private int mScrollDirection = SCROLL_END;
        /**
         * index we start in fetching view
         */
        private int mCurrentPosition = Integer.MAX_VALUE;
        /**
         * scroll offset this time
         */
        private int mOffset;

        /**
         * Number of pixels that we should fill, in the layout direction.
         */
        int mAvailable;

        /**
         * Used if you want to pre-layout items that are not yet visible.
         * The difference with {@link #mAvailable} is that, when recycling, distance laid out for
         * {@link #mExtra} is not considered to avoid recycling visible children.
         */
        int mExtra = 0;

        private int end = NO_POSITION;

        /**
         * Defines the direction in which the data adapter is traversed.
         * Should be {@link #ITEM_DIRECTION_HEAD} or {@link #ITEM_DIRECTION_TAIL}
         */
        int mItemDirection;

        /**
         * Defines the direction in which the layout is filled.
         * Should be {@link #LAYOUT_START} or {@link #LAYOUT_END}
         */
        int mLayoutDirection;

        public void updateLayoutState(int scrollDirection, int delta) {
            mScrollDirection = scrollDirection;
            mOffset = delta;
            if (scrollDirection == SCROLL_END) {
                View child = getClosetChildToEnd();
                mCurrentPosition = getPosition(child) + mScrollDirection;
            } else {
                View child = getClosetChildToStart();
                mCurrentPosition = getPosition(child) + mScrollDirection;
            }
        }

        private View next(RecyclerView.Recycler recycler) {
            View view = recycler.getViewForPosition(mCurrentPosition);
            mCurrentPosition += mScrollDirection;
            return view;
        }

        /**
         * @return true if there is no more element
         */
        public boolean hasMore(State state) {
            return mCurrentPosition >= 0 && mCurrentPosition < state.getItemCount();
        }


        View getClosetChildToEnd() {
            return getChildAt(getChildCount() - 1);
        }

        View getClosetChildToStart() {
            return getChildAt(0);
        }

        /**
         * compute the start and end position when layout view
         */
        private void prepareStartAndEnd() {
            int curPos = mTotalOffset / mUnit;
            int leavingSpace = getWidth() - (left(curPos) + mUnit);
            int itemCountAfterBaseItem = leavingSpace / mUnit + 2;
            int e = curPos + itemCountAfterBaseItem;
            mCurrentPosition = curPos - maxStackCount >= 0 ? curPos - maxStackCount : 0;
            end = e >= getItemCount() ? getItemCount() - 1 : e;
        }
    }


    @Google
    class AnchorInfo {
        int mPosition;
        int mCoordinate;
        /**
         * 是否布局是从End开始，由reverseLayout和stackFromEnd决定，
         * 两者只要不同那么布局都是End开始
         */
        boolean mLayoutFromEnd;
        boolean mValid;

        AnchorInfo() {
            reset();
        }

        void reset() {
            mPosition = NO_POSITION;
            mCoordinate = INVALID_OFFSET;
            mLayoutFromEnd = false;
            mValid = false;
        }

        public void assignFromView(View child) {
            if (mLayoutFromEnd) {
                mCoordinate = mOrientationHelper.getDecoratedEnd(child)
                        + mOrientationHelper.getTotalSpaceChange();
            } else {
                mCoordinate = mOrientationHelper.getDecoratedStart(child);
            }

            mPosition = getPosition(child);
        }

        void assignCoordinateFromPadding() {
            mCoordinate = mLayoutFromEnd
                    ? mOrientationHelper.getEndAfterPadding()
                    : mOrientationHelper.getStartAfterPadding();
        }
    }

    @Google
    protected static class LayoutChunkResult {
        public int mConsumed;
        public boolean mFinished;
        public boolean mIgnoreConsumed;
        public boolean mFocusable;

        void resetInternal() {
            mConsumed = 0;
            mFinished = false;
            mIgnoreConsumed = false;
            mFocusable = false;
        }
    }
}
