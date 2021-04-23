package com.demo.demo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    RecyclerView recycler_view = findViewById(R.id.recycler_view);
    setup(recycler_view);
  }

  private void setup(RecyclerView recycler_view) {}
}
