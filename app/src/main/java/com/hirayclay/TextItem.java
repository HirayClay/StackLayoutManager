package com.hirayclay;

public class TextItem implements Item {

  public static final int TYPE_TEXT = 0;
  public final String text;

  private TextItem(String text) {
    this.text = text;
  }

  public static TextItem create(String text) {
    return new TextItem(text);
  }

  @Override
  public int type() {
    return TYPE_TEXT;
  }
}
