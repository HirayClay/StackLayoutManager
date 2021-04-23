package com.hirayclay;

import java.util.Arrays;
import java.util.List;

public class ImageDataListUtil {

  public static List<Integer> imageList() {
    return Arrays.asList(
        R.drawable.xm2,
        R.drawable.xm3,
        R.drawable.xm4,
        R.drawable.xm5,
        R.drawable.xm6,
        R.drawable.xm7,
        R.drawable.xm1,
        R.drawable.xm8,
        R.drawable.xm9,
        R.drawable.xm1,
        R.drawable.xm2,
        R.drawable.xm3,
        R.drawable.xm4,
        R.drawable.xm5,
        R.drawable.xm6);
  }

  public static List<Item> dataList() {
    return Arrays.asList(TextItem.create("left 2 test right"), ImageItem.create(imageList()));
  }
}
