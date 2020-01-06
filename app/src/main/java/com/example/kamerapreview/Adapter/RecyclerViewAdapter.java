package com.example.kamerapreview.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kamerapreview.KassenbelegModel;
import com.example.kamerapreview.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<KassenbelegModel> kassenbelegModelList;

    public RecyclerViewAdapter(Context context, List<KassenbelegModel> kassenbelegModelList) {
        this.context = context;
        this.kassenbelegModelList = kassenbelegModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.kassenbeleg_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KassenbelegModel kassenbelegModel = kassenbelegModelList.get(position); // each kassenbeleg object inside of our list
        holder.beschreibung.setText(kassenbelegModel.getBeschreibung());
        holder.summe.setText(kassenbelegModel.getSumme().toString()+"€");
        holder.datum.setText(kassenbelegModel.getZeit());
    }

    @Override
    public int getItemCount() {
        return kassenbelegModelList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView beschreibung;
        public TextView summe;
        public TextView datum;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            beschreibung = itemView.findViewById(R.id.beschreibung);
            summe = itemView.findViewById(R.id.summe);
            datum = itemView.findViewById(R.id.datum);
        }
    }

    public KassenbelegModel getKassenbelegModel(int position){
        return kassenbelegModelList.get(position);
    }
}