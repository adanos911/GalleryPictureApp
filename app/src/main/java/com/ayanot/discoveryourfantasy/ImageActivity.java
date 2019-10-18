package com.ayanot.discoveryourfantasy;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";

    ImageView imageView;
    TextView descriptionImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.fullScreenImage);
        descriptionImage = findViewById(R.id.descriptionImage);

        Image image = getIntent().getExtras().getParcelable(Image.class.getSimpleName());

        descriptionImage.setText(image.getName().split("\\.")[0]);

        Picasso.with(this).load(image.getHref()).into(imageView);

//        imageView.setImageResource();
    }
}
