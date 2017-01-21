package com.example.azarchatit.facebookphotosexp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.azarchatit.facebookphotosexp.MainActivity.response2;

public class CustomGallery_Activity extends AppCompatActivity {
    private static Button saveImages, selectImages;

    private static GridView galleryImagesGridView;
    public static ArrayList<String> galleryImageUrls = new ArrayList<String>();
    ;
    private static GridView_Adapter imagesAdapter;


    TransferUtility transferUtility;
    TransferObserver observer;
    AmazonS3Client s3;
    BasicAWSCredentials credentials;
    ProgressBar pb;
    TextView _status;


    public ArrayList<String> selectedItems;

    private static final String TAG = "";
    public Bitmap theBitmap;
    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    String ACCESS_KEY = "AKIAIZGG2QIGKODGSE4Q",
            SECRET_KEY = "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",
            MY_BUCKET = "compphotoexp",
            OBJECT_KEY = "unique_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_gallery_);
        //Init all views
        saveImages = (Button) findViewById(R.id.selectImagesBtn);
        galleryImagesGridView = (GridView) findViewById(R.id.galleryImagesGridView);
        pb = (ProgressBar) findViewById(R.id.myProgress);
        pb.setVisibility(View.INVISIBLE);
        _status = (TextView) findViewById(R.id.txt_progress);

        //getting the position from the album activity
        Intent intent = getIntent();
        int position = intent.getIntExtra("intVariableName", 0);
        Log.d(TAG, "onCreate: position and response" + response2.toString());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //call functions
        getData(response2.getJSONObject(), position);
        setListeners();
        setUpGridView();

    }

    //Set Up GridView method
    private void setUpGridView() {
        imagesAdapter = new GridView_Adapter(CustomGallery_Activity.this, galleryImageUrls);
        galleryImagesGridView.setAdapter(imagesAdapter);


    }

    //Set Listeners method
    private void setListeners() {
        saveImages.setOnClickListener(listener);
    }

    //Show hide select button if images are selected or deselected
    public void showSelectButton() {
        selectedItems = imagesAdapter.getCheckedItems();

        if (selectedItems.size() > 0) {
            saveImages.setText("SAVE " + " - " + selectedItems.size() + " Selected Image(s) ");
            saveImages.setVisibility(View.VISIBLE);
        } else
            saveImages.setVisibility(View.GONE);

    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //When button is clicked then fill array with selected images
            selectedItems = imagesAdapter.getCheckedItems();
            final AlertDialog dialog = new AlertDialog.Builder(CustomGallery_Activity.this).create();
            dialog.setTitle("save Image");
            dialog.setMessage("do you want to save those Images?");
            dialog.setButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    bitmaFuction(selectedItems);
                }
            });
            dialog.setButton2("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }

    };

    @Override
    protected void onPause() {
        super.onPause();
        galleryImageUrls.clear();
    }

    public ArrayList<String> getData(JSONObject data, int position) {

        try {
            Log.d(TAG, "getData: this is the data");
            JSONArray albumsArray = data.getJSONArray("data");

            JSONObject jsonAlbums = albumsArray.getJSONObject(position);
            JSONObject photos = jsonAlbums.getJSONObject("photos");
            JSONArray jsonpicture = photos.getJSONArray("data");
            for (int j = 0; j < jsonpicture.length(); j++) {
                JSONObject photosObject = jsonpicture.getJSONObject(j);
                JSONArray photoArray = photosObject.getJSONArray("webp_images");
                JSONObject photoUrlObject = photoArray.getJSONObject(0);
                String photoUrl = photoUrlObject.getString("source");
                galleryImageUrls.add(photoUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getData: " + e.getMessage());
        }

        return galleryImageUrls;
    }

    /**
     * the images into file
     */
    public void startsave() {
        ArrayList<File> images = new ArrayList<>();
        //File[]images=new File[];
        for (int i = 0; i < bitmapArray.size(); i++) {

            File directory = CustomGallery_Activity.this.getDir("directoryName", Context.MODE_PRIVATE);
            File mypath = new File(directory, selectedItems.get(i).substring(100, 110) + ".jpg");


            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mypath);
                bitmapArray.get(i).compress(Bitmap.CompressFormat.JPEG, 100, fos);
                images.add(mypath);

            } catch (Exception e) {
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                }

            }

        }

        refreshGallery(images);

    }

    /**
     * Uploading to Amazon s3
     */

    public void refreshGallery(ArrayList<File> f) {

        credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        s3 = new AmazonS3Client(credentials);
        transferUtility = new TransferUtility(s3, CustomGallery_Activity.this);
        for (int i = 0; i < bitmapArray.size(); i++) {

            observer = transferUtility.upload(
                    MY_BUCKET, f.get(i).getName()
                    ,
                    f.get(i)
            );
        }
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (state.COMPLETED.equals(observer.getState())) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomGallery_Activity.this);
                    builder.setTitle("");
                    builder.setMessage("images have been saved successfully");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.show();


                    TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
                    messageView.setGravity(Gravity.CENTER_HORIZONTAL);
                    dialog.show();

                    pb.setVisibility(View.INVISIBLE);
                    _status.setVisibility(View.INVISIBLE);
                    bitmapArray.clear();


                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                long _bytesCurrent = bytesCurrent;
                long _bytesTotal = bytesTotal;

                int percentage = (int) ((float) _bytesCurrent / (float) _bytesTotal * 100);
                Log.d("percentage", "" + percentage);
                pb.setVisibility(View.VISIBLE);
                _status.setVisibility(View.VISIBLE);
                pb.setProgress((int) percentage);
                _status.setText("Uploading Progress" + " " + percentage + "%");
            }

            @Override
            public void onError(int id, Exception ex) {

                pb.setVisibility(View.INVISIBLE);
                _status.setVisibility(View.INVISIBLE);

                Toast.makeText(CustomGallery_Activity.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });
    }

    /**
     * saving images into bitmaps
     */


    public void bitmaFuction(final ArrayList<String> imageUrl) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Looper.prepare();
                for (int i = 0; i < imageUrl.size(); i++) {
                    try {

                        theBitmap = Glide.
                                with(CustomGallery_Activity.this).
                                load(imageUrl.get(i)).
                                asBitmap().
                                into(-1, -1).
                                get();

                    } catch (final ExecutionException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (final InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    bitmapArray.add(theBitmap);

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void dummy) {
                if (null != theBitmap) {

                    saveImages.setVisibility(View.GONE);

                    //starting uploading the image
                    startsave();
                }
                ;
            }
        }.execute();

    }

}


