package com.example.torridas.permissionstest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.messaging.FirebaseMessaging;

public class PermissionsTest extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ImageView imagineView;
    private String fileAbsolutePath;
    private GoogleMap googleMap;
    private MapFragment mapFragment;
    private GoogleApiClient googleApiClient;
    private Location mLastKnownLocation;
    private CameraPosition cameraPosition;
    private FirebaseAuth mAtuh;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //auth

    private Button buttonLogIn;
    private Button buttonSaveUser;
    private EditText userInput;
    private EditText userPass;
    private TextView currUser;
    private CallbackManager callbackManager;
    private LoginButton loginButton;


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

        buttonLogIn = (Button) findViewById(R.id.btn_logIn);
        buttonSaveUser = (Button) findViewById(R.id.btn_registerUser);
        userInput = (EditText) findViewById(R.id.userEdit);
        userPass = (EditText) findViewById(R.id.passwordEdit);
        currUser = (TextView) findViewById(R.id.currentUser);

        Button buttonPoza = (Button) findViewById(R.id.btn_pic);
        Button buttonSalveza = (Button) findViewById(R.id.btn_save);
        Button buttonShare = (Button) findViewById(R.id.btn_share);

        FirebaseMessaging.getInstance().subscribeToTopic("Razboi");

        mAtuh = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //este aici
                } else {
                    // nu este aici
                }
            }
        };

        imagineView = (ImageView)

                findViewById(R.id.imagine);

        googleApiClient = new GoogleApiClient.Builder(this).

                enableAutoManage(this, this).

                addConnectionCallbacks(this).

                addApi(LocationServices.API).

                addApi(Places.GEO_DATA_API).

                addApi(Places.PLACE_DETECTION_API).

                build();
        googleApiClient.connect();


        buttonPoza.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PermissionsTest.this, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent pictureTakeIntet = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (pictureTakeIntet.resolveActivity(getPackageManager()) != null) {


                        File photoFile = null;
                        try {
                            photoFile = createImagineFile();
                        } catch (IOException ex) {
                            // vad eu ce fac aici.
                        }

                        if (photoFile != null) {
                            Uri photoUri = FileProvider.getUriForFile(PermissionsTest.this,
                                    "com.example.android.fileprovider", photoFile);
                            pictureTakeIntet.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(pictureTakeIntet, Strings.CAMERA_APP_REQUEST);
                        }
                    }


                } else {
                    Toast.makeText(PermissionsTest.this, "NU AM PERMISIUNE!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSalveza.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                mapFragment = MapFragment.newInstance();
                android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.frame, mapFragment);
                fragmentTransaction.commit();
                mapFragment.getMapAsync(PermissionsTest.this);
            }
        });


        buttonShare.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                final File photoFile = new File(fileAbsolutePath);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
                startActivity(Intent.createChooser(shareIntent, "Share this using.. ?"));
            }
        });


        buttonSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mAtuh.createUserWithEmailAndPassword(userInput.getText().toString(),
                            userPass.getText().toString()).addOnCompleteListener(PermissionsTest.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("LOG:", "FAILED! ", task.getException());
                            } else {
                                Toast.makeText(PermissionsTest.this, "User created succesfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.d("TAG", "Ceva nu merge!");
                }
            }
        });
    }

    private void FaUnCont(AccessToken token) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(token.getToken());
        mAtuh.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PermissionsTest.this, "Facebook Account created!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PermissionsTest.this, "Failed to create account! ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Strings.CAMERA_APP_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileAbsolutePath), 400, 400);
            imagineView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAtuh.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // shouldShowRequestPermissionRationale -> in cazul in care am mai intrebat, a zis nu, dar am nevoie.

        if (ContextCompat.checkSelfPermission(PermissionsTest.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PermissionsTest.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},
                    Strings.COMBO_REQUEST);
        }

        mAtuh.addAuthStateListener(mAuthListener);
        /*if( ContextCompat.checkSelfPermission(PermissionsTest.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PermissionsTest.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},Strings.STORAGE_WRITE_REQUEST);
        } */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Strings.COMBO_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast toast = Toast.makeText(PermissionsTest.this, "Am acces peste tot! >:)", Toast.LENGTH_SHORT);
                    toast.show();

                }
            }
        }
    }

    private void updateLocationUI() {
        if (ContextCompat.checkSelfPermission(PermissionsTest.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(PermissionsTest.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            cameraPosition = googleMap.getCameraPosition();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude()), 14.0f));
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.addMarker(new MarkerOptions().position(new LatLng(44.42645499999999, 26.1043950000000)).title("Piata Unirii"));
        updateLocationUI();
        getDeviceLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}