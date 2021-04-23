package com.hirayclay;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;

/**
 * Created by CJJ on 2017/6/20.
 *
 * @author CJJ
 */

public class Config {

    @IntRange(from = 2)
    public int space = 60;
    public int maxStackCount = 3;
    public int initialStackCount = 0;
    @FloatRange(from = 0f, to = 1f)
    public float secondaryScale;
    @FloatRange(from = 0f, to = 1f)
    public float scaleRatio;
    /**
     * the real scroll distance might meet requirement,
     * so we multiply a factor fro parallex
     */
    @FloatRange(from = 1f,to = 2f)
    public float parallex = 1f;
    Align align;

}
