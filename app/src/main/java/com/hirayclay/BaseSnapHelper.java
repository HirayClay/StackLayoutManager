package com.hirayclay;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by hiray on 2017/12/27.
 * snap helper helps finding the base position item view
 *
 * @author hiray
 */

public class BaseSnapHelper extends LinearSnapHelper {

    private static final String TAG = "StackLayoutManager";

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        return ((StackLayoutManager) layoutManager).getSnapDistance(targetView);
    }

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        return ((StackLayoutManager)layoutManager).findSnapView();
    }

    static void attach(RecyclerView view) {
        if (view.getOnFlingListener() == null)
            new BaseSnapHelper().attachToRecyclerView(view);
    }
}
