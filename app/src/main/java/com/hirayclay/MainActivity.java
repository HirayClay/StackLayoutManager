package com.hirayclay;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    //horizontal reverse recyclerview
    @BindView(R.id.recyclerview1)
    RecyclerView hrRecyclerView;
    @BindView(R.id.button)
    Button button;
    private StackLayoutManager layoutManager;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        resetDefault();
        resetRight();
    }


    @OnClick(R.id.button)
    public void resetDefault() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            datas.add(String.valueOf(i));
        }

        Config config = new Config();
        config.secondaryScale = 1.0f;
        config.scaleRatio = 0.4f;
        config.maxStackCount = 1;
        config.initialStackCount = 1;
        config.space = 15;
        config.align = Align.LEFT;
        recyclerview.setLayoutManager(layoutManager = new StackLayoutManager(config));
        recyclerview.setAdapter(new StackAdapter(datas));

    }

    @OnClick(R.id.button1)
    public void resetRight() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            datas.add(String.valueOf(i));
        }

        Config config = new Config();
        config.secondaryScale = 0.8f;
        config.scaleRatio = 0.4f;
        config.maxStackCount = 4;
        config.initialStackCount = 2;
        config.space = getResources().getDimensionPixelOffset(R.dimen.item_space);

        config.align = Align.RIGHT;
        hrRecyclerView.setLayoutManager(new StackLayoutManager(config));
        hrRecyclerView.setAdapter(new StackAdapter(datas));
    }

    @OnClick(R.id.button2)
    public void viewVertical() {
        startActivity(new Intent(this, VerticalActivity.class));
    }

    @OnClick(R.id.scroll_to_specific_item)
    public void onScrollToItem() {
        layoutManager.scrollToPosition(10);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
