package com.example.kamerapreview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class KassenbelegImage extends AppCompatActivity {

    private ImageView imageView;
    private KassenbelegModel kassenbelegModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kassenbeleg_image);
        kassenbelegModel = (KassenbelegModel)getIntent().getSerializableExtra("kassenbeleg");

        imageView = findViewById(R.id.image);

        WebService webService = new WebService();
        webService.getImageRequest(new ImageListener() {
            @Override
            public void setImage(final byte[] imageBytes) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        imageView.setImageBitmap(bitmap);

                        // Stuff that updates the UI

                    }
                });

            }
        },kassenbelegModel.getBelegID());

    }
}
