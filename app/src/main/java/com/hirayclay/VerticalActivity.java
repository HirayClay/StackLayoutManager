package com.hirayclay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerticalActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview_vertical)
    RecyclerView verticalRecyclerview;
    List<Integer> imgIds = new ArrayList<>();

    private List<Integer> originIds = Arrays.asList(
            R.drawable.xm2,
            R.drawable.xm3,
            R.drawable.xm4,
            R.drawable.xm5,
            R.drawable.xm6,
            R.drawable.xm7,
            R.drawable.xm1,
            R.drawable.xm8,
            R.drawable.xm9,
            R.drawable.xm1,
            R.drawable.xm2,
            R.drawable.xm3,
            R.drawable.xm4,
            R.drawable.xm5,
            R.drawable.xm6
    );
    private StackAdapter adapter;
    private List<String> datas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical);
        ButterKnife.bind(this);
        vr();
    }

    private void vr() {
        datas = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            datas.add(String.valueOf(i));
        }

        prepareImgIds();

        Config config = new Config();
        config.secondaryScale = 0.95f;
        config.scaleRatio = 0.4f;
        config.maxStackCount = 4;
        config.initialStackCount = 4;
        config.space = 45;
        config.align = Align.TOP;
        verticalRecyclerview.setLayoutManager(new StackLayoutManager(config));
        adapter = new StackAdapter(datas).vertical().imgs(imgIds);
        verticalRecyclerview.setAdapter(adapter);
    }

    private void prepareImgIds() {
        imgIds.addAll(originIds);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                vr();
                break;
            case R.id.RemoveThird:
                imgIds.remove(3);
                datas.remove(3);
                adapter.notifyItemRemoved(3);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rest, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
