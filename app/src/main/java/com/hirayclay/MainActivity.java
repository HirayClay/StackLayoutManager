package com.hirayclay;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setData();
            }
        }, 1200);
    }

    @OnClick(R.id.button)
    public void setData() {
        List<String> datas = new ArrayList<>();
        datas.add("AlicePurple");
        datas.add("Bob");
        datas.add("Stephan");
        datas.add("Fury");
        datas.add("Weiry");
        datas.add("PingTong");
        datas.add("RoserMan");
        datas.add("BreakingBad");

        recyclerview.setLayoutManager(new StackLayoutManager());
        recyclerview.setAdapter(new StackAdapter(datas));
    }
}
