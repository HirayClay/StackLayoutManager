package com.hirayclay;

/**
 * Created by CJJ on 2017/10/16.
 *
 * @author CJJ
 */
enum Align {
  LEFT(1),
  RIGHT(-1),
  TOP(1),
  BOTTOM(-1);

  int layoutDirection;

  Align(int sign) {
    this.layoutDirection = sign;
  }
}
