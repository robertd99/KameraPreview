package com.example.kamerapreview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RecyclerView extends AppCompatActivity {

    Button getImage;
    ImageView imageView;
    TextView textView;
    TextView textJson;
    Button getJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);


        textJson = findViewById(R.id.textJson);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        textView.setText("Das hier ist ein test");

        getJson = findViewById(R.id.getJson);
        getJson.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getBaseContext(), "clicktest", Toast.LENGTH_LONG).show();

                WebService webService = new WebService();
                webService.getAllKassenbelegeRequest(new KassenbelegListener() {
                    @Override
                    public void setKassenbelege(final String string) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                textJson.setText(string);

                                // Stuff that updates the UI

                            }
                        });
                    }
                });

            }
        });


        getImage = findViewById(R.id.getimage);
        getImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getBaseContext(), "clicktest", Toast.LENGTH_LONG).show();

                WebService webService = new WebService();
                webService.getImageRequest(new ImageListener() {
                    @Override
                    public void setImage(final byte[] imageBytes) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                textView.setText("Das hier ist ein test22222");

                                Bitmap  bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                                imageView.setImageBitmap(bitmap);

                                // Stuff that updates the UI

                            }
                        });

                    }
                });

            }
        });
    }


}
