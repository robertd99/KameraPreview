package com.example.kamerapreview;

import android.graphics.Bitmap;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class KassenbelegModel implements Serializable {

    private File imageFile;
    private String beschreibung="";
    private Double summe;
    private String belegID;
    private String zeit;
    private Bitmap bitmap;

    public void setImageFile(File file){
        imageFile=file;
    }

    public File getImageFile(){
        return imageFile;
    }

    public void setBeschreibung(String beschreibung){
        this.beschreibung=beschreibung;
    }

    public String getBeschreibung(){
        return beschreibung;
    }

    public void setSumme(Double summe){
        this.summe=summe;
    }

    public Double getSumme(){
        return summe;
    }

    public void setBelegID(String belegID) {
        this.belegID = belegID;
    }

    public String getBelegID(){
        return belegID;
    }

    public void setZeit(String zeit){
        this.zeit = zeit;
    }

    public String getZeit(){
        return zeit;
    }

    public void setBitmap(Bitmap bitmap){this.bitmap=bitmap;}

    public Bitmap getBitmap() {
        return bitmap;
    }
}
