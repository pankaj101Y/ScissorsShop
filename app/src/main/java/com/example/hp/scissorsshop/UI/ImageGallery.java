package com.example.hp.scissorsshop.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.hp.scissorsshop.R;

public class ImageGallery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        RecyclerView imageGalleryView=findViewById(R.id.imageGalleryView);
    }
}
