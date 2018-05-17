package com.example.hp.scissorsshop.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.hp.scissorsshop.Adapters.ReviewAdapter;
import com.example.hp.scissorsshop.R;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        RecyclerView reviewRecycler = findViewById(R.id.reviewRecyclerView);
        ReviewAdapter adapter=new ReviewAdapter();
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        reviewRecycler.setLayoutManager(manager);
        reviewRecycler.setAdapter(adapter);
    }
}
