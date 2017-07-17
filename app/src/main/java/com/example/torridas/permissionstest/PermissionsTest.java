package com.example.torridas.permissionstest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PermissionsTest extends AppCompatActivity {

    private ImageView imagineView;
    private String fileAbsolutePath;

    private File createImagineFile() throws IOException {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpeg", storageDir);
        fileAbsolutePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions_test);

        Button buttonPoza = (Button) findViewById(R.id.btn_pic);
        Button buttonSalveza = (Button) findViewById(R.id.btn_save); // ???
        imagineView = (ImageView)findViewById(R.id.imagine);

        buttonPoza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PermissionsTest.this, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent pictureTakeIntet = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if( pictureTakeIntet.resolveActivity(getPackageManager())!= null ){


                        File photoFile = null;
                        try {
                            photoFile = createImagineFile();
                        }
                        catch (IOException ex ){
                            // vad eu ce fac aici.
                        }

                        if( photoFile != null ) {
                            Uri photoUri = FileProvider.getUriForFile(PermissionsTest.this,
                                    "com.example.android.fileprovider", photoFile);
                            pictureTakeIntet.putExtra(MediaStore.EXTRA_OUTPUT, photoUri );
                            startActivityForResult(pictureTakeIntet, Strings.CAMERA_APP_REQUEST );
                        }
                    }



                } else {
                    Toast.makeText(PermissionsTest.this, "NU AM PERMISIUNE!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == Strings.CAMERA_APP_REQUEST && resultCode == RESULT_OK ) {
            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileAbsolutePath), 400, 400);
            imagineView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // shouldShowRequestPermissionRationale -> in cazul in care am mai intrebat, a zis nu, dar am nevoie.

        if( ContextCompat.checkSelfPermission(PermissionsTest.this, Manifest.permission.CAMERA )
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(PermissionsTest.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    Strings.COMBO_REQUEST);
        }
        /*if( ContextCompat.checkSelfPermission(PermissionsTest.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PermissionsTest.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},Strings.STORAGE_WRITE_REQUEST);
        } */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch( requestCode ) {
            case Strings.COMBO_REQUEST: {
                if ( grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED ) {
                    Toast toast =  Toast.makeText(PermissionsTest.this, "Am acces peste tot! >:)", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }
}



