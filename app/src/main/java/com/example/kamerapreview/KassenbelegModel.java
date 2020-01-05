package com.example.kamerapreview;

import android.graphics.Bitmap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;



public class KassenbelegModel implements Serializable {

    @JsonIgnore
    private File imageFile;
    @JsonProperty("beschreibung")
    private String beschreibung="";
    @JsonProperty("summe")
    private Double summe;
    @JsonProperty("uuid")
    private String belegID;
    @JsonProperty("zeit")
    private String zeit;
    @JsonIgnore
    private Bitmap bitmap;

    public File getImageFile(){
        return imageFile;
    }

    public void setImageFile(File file){
        imageFile=file;
    }

    @JsonProperty("beschreibung")
    public String getBeschreibung(){
        return beschreibung;
    }

    @JsonProperty("beschreibung")
    public void setBeschreibung(String beschreibung){
        this.beschreibung=beschreibung;
    }

    @JsonProperty("summe")
    public Double getSumme(){
        return summe;
    }

    @JsonProperty("summe")
    public void setSumme(Double summe){
        this.summe=summe;
    }

    @JsonProperty("uuid")
    public String getBelegID(){
        return belegID;
    }

    @JsonProperty("uuid")
    public void setBelegID(String belegID) {
        this.belegID = belegID;
    }

    @JsonProperty("zeit")
    public String getZeit(){
        return zeit;
    }

    @JsonProperty("zeit")
    public void setZeit(String zeit){
        this.zeit = zeit;
    }

    public void setBitmap(Bitmap bitmap){this.bitmap=bitmap;}

    public Bitmap getBitmap() {
        return bitmap;
    }
}
