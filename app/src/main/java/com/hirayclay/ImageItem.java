package com.hirayclay;

import java.util.List;

public class ImageItem implements Item {

  public static final int TYPE_IMAGE_LIST = 1;
  public final List<Integer> imageList;

  public ImageItem(List<Integer> imageList) {
    this.imageList = imageList;
  }

  public static ImageItem create(List<Integer> list) {
    return new ImageItem(list);
  }

  @Override
  public int type() {
    return TYPE_IMAGE_LIST;
  }
}
