package com.example.kamerapreview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.kamerapreview.Adapter.RecyclerViewAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RecyclerView extends AppCompatActivity {

    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    List<KassenbelegModel> kassenbelege = new ArrayList<KassenbelegModel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);



        KassenbelegModel kassenbelegModel1 = new KassenbelegModel();
        kassenbelegModel1.setSumme(10.0);
        kassenbelegModel1.setBeschreibung("10.0");
        kassenbelegModel1.setZeit("10/10/10");

        KassenbelegModel kassenbelegModel2 = new KassenbelegModel();
        kassenbelegModel2.setSumme(20.0);
        kassenbelegModel2.setBeschreibung("20.0");
        kassenbelegModel2.setZeit("20/20/20");

        kassenbelege.add(kassenbelegModel1);
        kassenbelege.add(kassenbelegModel2);





        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

///rausnehmen

        recyclerViewAdapter = new RecyclerViewAdapter(RecyclerView.this, kassenbelege);

        recyclerView.setAdapter(recyclerViewAdapter);

///

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(RecyclerView.this,KassenbelegImage.class);
                        intent.putExtra("kassenbeleg",recyclerViewAdapter.getKassenbelegModel(position));
                        startActivity(intent);

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


        WebService webService = new WebService();
        webService.getAllKassenbelegeRequest(new KassenbelegListener() {
            @Override
            public void setKassenbelege(final String string) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                   kassenbelege = new ArrayList(Arrays.asList(objectMapper.readValue(string, KassenbelegModel[].class)));

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //HIER DIE RECYCLER VIEW ERSTELLEN!!!!!!

                            recyclerViewAdapter = new RecyclerViewAdapter(RecyclerView.this, kassenbelege);

                            recyclerView.setAdapter(recyclerViewAdapter);
                        }
                    });

                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, @NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, @NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            recyclerViewAdapter.notifyItemRemoved(position);
            WebService webService = new WebService();
            webService.deleteKassenbelegRequest(kassenbelege.get(position).getBelegID());
            kassenbelege.remove(position);
            Toast.makeText(RecyclerView.this, "Kassenbeleg gelöscht!", Toast.LENGTH_SHORT).show();
        }
    };
}