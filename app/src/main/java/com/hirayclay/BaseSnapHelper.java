package com.hirayclay;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;

/**
 * Created by hiray on 2017/12/27.
 * snap helper helps finding the base position item view
 *
 * @author hiray
 */

public class BaseSnapHelper extends SnapHelper {

    private static final String TAG = "StackLayoutManager";

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        return ((StackLayoutManager) layoutManager).getSnapDistance(targetView);
    }

    //获取snapView
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        return ((StackLayoutManager)layoutManager).findSnapView();
    }

    //获取fling结束后应该snap到的位置
    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        return ((StackLayoutManager)layoutManager).findTargetSnapPosition(velocityX,velocityY);
    }

    static void attach(RecyclerView view) {
        if (view.getOnFlingListener() == null)
            new BaseSnapHelper().attachToRecyclerView(view);
    }
}
