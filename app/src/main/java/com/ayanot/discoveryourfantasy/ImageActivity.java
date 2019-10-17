package com.ayanot.discoveryourfantasy;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class ImageActivity extends AppCompatActivity {

    ImageView imageView;
    TextView descriptionImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.fullScreenImage);
        descriptionImage = findViewById(R.id.descriptionImage);

        String imgName = getIntent().getExtras().getString("IMAGE_NAME");
        descriptionImage.setText(imgName.split("\\.")[0]);

        InputStream inputStream = null;
        try {
            inputStream = getApplicationContext().getAssets().open("images/" + imgName);
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            imageView.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        imageView.setImageResource();
    }
}
