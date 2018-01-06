package com.hirayclay;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.widget.Toast;

/**
 * Created by hiray on 2017/12/28.
 *
 * @author hiray
 */

public class Toasty {

    private static Context mApp;
    private static AppCompatTextView mTextView;
    private static Toast mToast;
    private static int color = Color.parseColor("#cccccc");
    public static void init(Application app) {
        mApp = app;
    }

    public static void toast(String message) {
        createIfNull();
        mTextView.setText(message);
        mToast.show();
    }

    private static void createIfNull() {
        if (mToast == null)
            synchronized (Toasty.class) {
                if (mToast == null) {
                    mToast = new Toast(mApp);
                    mTextView = new AppCompatTextView(mApp);
                    mTextView.setTextColor(color);
                    mTextView.setTextSize(20);
                    mToast.setView(mTextView);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
            }
    }
}
