package com.hirayclay;

import android.view.View;

/**
 * Created by hiray on 2017/12/27.
 *
 * @author hiray notify the observer the item in the base position has changed
 */
public interface ItemChangeListener {

  /**
   * @param itemView the new item in the base position
   * @param position the item's position in list
   */
  void onItemChange(View itemView, int position);
}
