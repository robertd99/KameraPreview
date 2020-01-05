package com.example.kamerapreview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Double.parseDouble;

public class Analyzer extends AppCompatActivity {

    Button sendButton;
    EditText editBeschreibung;
    EditText editSumme;
    ImageView imageView;
    File imageFile;
    private KassenbelegModel kassenbelegModel= new KassenbelegModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyzer);

        editBeschreibung = findViewById(R.id.editBeschreibung);
        editSumme = findViewById(R.id.editSumme);
        imageView = findViewById(R.id.image);

        kassenbelegModel = (KassenbelegModel)getIntent().getSerializableExtra("kassenbeleg");
        imageFile =  kassenbelegModel.getImageFile();

        kassenbelegModel.setBitmap(BitmapFactory.decodeFile(kassenbelegModel.getImageFile().getAbsolutePath()));


        sendButton = findViewById(R.id.beleg_senden);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                kassenbelegModel.setBeschreibung(editBeschreibung.getText().toString());
                String summe= new String(editSumme.getText().toString());
                String summeDouble = summe.replaceAll("€","");
                String summeDouble2 = summe.replaceAll(",",".");

                kassenbelegModel.setSumme(Double.parseDouble(summeDouble2));
                Bitmap myBitmapRotated = rotateBitmap(kassenbelegModel.getBitmap(),90);
                Bitmap myBitmapCropped= cropBitmap(myBitmapRotated);
                kassenbelegModel.setBitmap(myBitmapCropped);

                WebService webService = new WebService();
                webService.sendKassenbeleg(kassenbelegModel);

                Toast.makeText(getBaseContext(), "gesendet", Toast.LENGTH_SHORT).show();
            }
        });


        Bitmap rotatedBitmap = rotateBitmap(kassenbelegModel.getBitmap(),90);
        Bitmap croppedBitmap = cropBitmap((rotatedBitmap));


        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(croppedBitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

            Task<FirebaseVisionText> result =
                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) throws IllegalArgumentException {



                                    String getLine = new String();
                                    List<Integer> ykoordinatenList = new ArrayList<Integer>();

                                    List<FirebaseVisionText.TextBlock> textBlockList = firebaseVisionText.getTextBlocks();
                                    for (FirebaseVisionText.TextBlock textBlock : textBlockList) {
                                        List<FirebaseVisionText.Line> textLines = textBlock.getLines();
                                        for (FirebaseVisionText.Line textLine : textLines) {
                                            Pattern summenzeilenfilter = Pattern.compile("(?i)(.*summe|zu zahlen.*)"); // B*S(\w*)+[.{0,4}]* reg ex B -beliebig viele character bis , dann 2 mal 0-9
                                            Matcher matcher = summenzeilenfilter.matcher(textLine.getText());
                                            while (matcher.find()) {
                                                Point[] pointarray = textLine.getCornerPoints();
                                                for (Point point : pointarray) {
                                                    ykoordinatenList.add(point.y);
                                                }
                                            }
                                        }
                                    }
                                    if (ykoordinatenList.isEmpty()==false) {


                                        for (FirebaseVisionText.TextBlock textBlock : textBlockList) {
                                            List<FirebaseVisionText.Line> textLines = textBlock.getLines();
                                            for (FirebaseVisionText.Line textLine : textLines) {
                                                if (textLine.getCornerPoints()[0].y > ykoordinatenList.get(0) - 10 && textLine.getCornerPoints()[1].y < ykoordinatenList.get(2) + 10) {
                                                    Pattern endpreisfilter = Pattern.compile("(?i)(.*\\d+,\\d\\d.*)"); // B*S(\w*)+[.{0,4}]* reg ex B -beliebig viele character bis , dann 2 mal 0-9
                                                    Matcher matcher = endpreisfilter.matcher(textLine.getText());
                                                    while (matcher.find()) {
                                                        getLine += textLine.getText();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if(ykoordinatenList.isEmpty()){
                                        kassenbelegModel.setSumme(0.0);
                                        Toast.makeText(getBaseContext(),"Kassenbeleg nicht erkannt!", Toast.LENGTH_SHORT).show();
                                    }
                                    try {
                                        analyzeString(getLine);
                                    } catch (IllegalArgumentException e) {
                                        kassenbelegModel.setSumme(0.0);
                                        if(!ykoordinatenList.isEmpty()) {
                                            Toast.makeText(getBaseContext(), "Summe nicht erkannt!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            })

                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                        }
                                    });


        fileToImageView();
    }

    private void fileToImageView() {
        if ( imageFile.exists()==false) {
            Toast.makeText(getBaseContext(), "Error: Please try again!", Toast.LENGTH_LONG).show();
        }
        else {
            Bitmap rotatedBitmap = rotateBitmap(kassenbelegModel.getBitmap(), 90);
            Bitmap croppedBitmap = cropBitmap(rotatedBitmap);
            imageView.setImageBitmap(croppedBitmap);

            imageFile.delete();
        }
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(),source.getHeight(),matrix,true);
    }

    private static Bitmap cropBitmap(Bitmap source){
        Matrix matrix = new Matrix();
        return Bitmap.createBitmap(source,source.getWidth()/24*4,source.getHeight()/32*22,source.getWidth()/12*9,source.getHeight()/32*4,matrix,true);

    }

    private void analyzeString(String values)throws IllegalArgumentException{

        if(values.isEmpty()){
            throw new IllegalArgumentException();
        }
        String value = values.replaceAll(",",".");
        String valueOhneBuchstaben = value.replaceAll("[a-z]","");
        String valuesOhneBuchstaben2 = valueOhneBuchstaben.replaceAll("[A-Z]","");
        try {
            kassenbelegModel.setSumme(parseDouble(value));
            editSumme.setText(kassenbelegModel.getSumme()+"€");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            kassenbelegModel.setSumme(0.0);
            Toast.makeText(getBaseContext(), "String to double failed",Toast.LENGTH_LONG).show();

        }


    }


}
