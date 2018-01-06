package com.hirayclay;

import android.app.Application;

/**
 * Created by hiray on 2017/12/28.
 *
 * @author hiray
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Toasty.init(this);
    }
}
