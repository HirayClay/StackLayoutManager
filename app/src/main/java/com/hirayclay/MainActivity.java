package com.hirayclay;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    int animateValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setData();
    }

    @OnClick(R.id.button)
    public void setData() {
        List<String> datas = new ArrayList<>();
        datas.add("Item1");
        datas.add("Item2");
        datas.add("Item3");
        datas.add("Item4");
        datas.add("Item5");
        datas.add("Item6");
        datas.add("Item7");
        datas.add("Item8");
        datas.add("Item9");
        datas.add("Item10");
        datas.add("Item11");

        recyclerview.setLayoutManager(new StackLayoutManager());
        recyclerview.setAdapter(new StackAdapter(datas));

        ObjectAnimator animator = ObjectAnimator.ofInt(MainActivity.this, "animateValue", 0, 200);
        animator.setDuration(300);
        animator.start();
    }

    public int getAnimateValue() {
        return animateValue;
    }

    public void setAnimateValue(int animateValue) {
        this.animateValue = animateValue;
        Log.i(TAG, "setAnimateValue: " + animateValue);
    }
}
