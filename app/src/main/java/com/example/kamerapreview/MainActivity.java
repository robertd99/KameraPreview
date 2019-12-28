package com.example.kamerapreview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {

    private Button kameraButton;
    private Button recyclerViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kameraButton = findViewById(R.id.kameraButton);
        kameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openKamera();
            }
        });
        recyclerViewButton= findViewById(R.id.recyclerViewButton);
        recyclerViewButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                openRecyclerView();
            }
        });
    }
    public void openKamera(){
        Intent intent = new Intent(this,Kamera.class);
        startActivity(intent);
    }

    public void openRecyclerView(){
        Intent intent = new Intent(this,RecyclerView.class);
        startActivity(intent);
    }
}
