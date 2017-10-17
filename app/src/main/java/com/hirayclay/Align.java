package com.hirayclay;

/**
 * Created by CJJ on 2017/10/16.
 *
 * @author CJJ
 */

enum Align {


    LEFT(1),
    RIGHT(-1);
    int sign;

    Align(int sign) {
        this.sign = sign;
    }
}
