package com.example.kamerapreview;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebService {
    final static String url = "192.168.178.64";


    public void sendKassenbeleg(KassenbelegModel kassenbelegModel) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap myBitmap = kassenbelegModel.getBitmap();
        myBitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        JSONObject postData = new JSONObject();
        try {
            postData.put("uuid", kassenbelegModel.getBelegID());
            postData.put("summe", kassenbelegModel.getSumme().toString());
            postData.put("beschreibung", kassenbelegModel.getBeschreibung());
            postData.put("zeit", kassenbelegModel.getZeit().toString());
            postData.put("foto", base64);

            AsyncTask<String, Void, String> task = saveTask();
            task.execute("http://" + url + ":8080/bilscan/rest/rechnungen/create", postData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getImageRequest(ImageListener imageListener, String uuid){
        AsyncTask<String, Void, String> task = getImageTask(imageListener);
        task.execute("http://" + url + ":8080/bilscan/rest/rechnungen/foto?uuid="+uuid);
    }

    public void getAllKassenbelegeRequest(KassenbelegListener kassenbelegListener){
        AsyncTask<String, Void, String> task = getAllKassenbelege(kassenbelegListener);
        task.execute("http://" + url + ":8080/bilscan/rest/rechnungen/getAll");
    }

    public void deleteKassenbelegRequest(String uuid){
        AsyncTask<String, Void, String> task = deleteKassenbelegTask(uuid);
        task.execute("http://" + url + ":8080/bilscan/rest/rechnungen/clear?uuid="+uuid);
    }


    private AsyncTask<String, Void, String> deleteKassenbelegTask(String uuid){
        return new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    final URL url = new URL(params[0]);
                    final HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("DELETE");
                    //con.setRequestProperty("Accept", "image/png");


                    final String theString = IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8.name());

                    return "erfolgreich";

                }
                catch (Exception e){
                    e.printStackTrace();
                }

                return"";

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
            }
        };
    }

    private AsyncTask<String, Void, String> saveTask(){
        return new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    final URL url = new URL(params[0]);
                    final HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);
                    final String jsonInputString = params[1];
                    try (final OutputStream os = con.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                    try (final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        return response.toString();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                return"";

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
            }
        };
    }

    private AsyncTask<String, Void, String> getImageTask(final ImageListener imageListener){
        return new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                        final URL url = new URL(params[0]);
                    final HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Accept", "image/png");

                        byte[] bytes = Base64.decode(IOUtils.toByteArray(con.getInputStream()),Base64.DEFAULT);
                        imageListener.setImage(bytes);


                        return "erfolgreich";

                }
                catch (Exception e){
                    e.printStackTrace();
                }

                return"";

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
            }
        };
    }

    private AsyncTask<String, Void, String> getAllKassenbelege(final KassenbelegListener kassenbelegListener){
        return new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    final URL url = new URL("http://192.168.178.64:8080/bilscan/rest/rechnungen/getAll");
                    final HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Accept", "application/json");
                    final String jsonString = IOUtils.toString(con.getInputStream(), "UTF_8");


                    kassenbelegListener.setKassenbelege(jsonString);


                    return "erfolgreich";

                }
                catch (Exception e){
                    e.printStackTrace();
                }

                return"";

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
            }
        };
    }
}
