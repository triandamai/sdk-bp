package com.projectubu.personaldashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.ideabus.model.XlogUtils;

import java.util.concurrent.TimeUnit;

public class SplashScreen extends AppCompatActivity {
    private String[] LocationPermission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int Location_Request = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
      checkLocationPermission();
    }
    private void initParam(){
        XlogUtils.initXlog(this, true);
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(MyWork.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("1qaz2wsx3edc", ExistingPeriodicWorkPolicy.REPLACE,workRequest);
        startActivity(new Intent(SplashScreen.this,MainActivity.class));
        finish();
    }
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Permission lokasi ");
                builder.setPositiveButton("Oke",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SplashScreen.this, LocationPermission, Location_Request);
                    }
                });
                builder.setNeutralButton("Tidak", null);

/*
                Snackbar.make(coordinatorLayout, "需要給予權限，否則不能連接設備", Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString("確定"), v -> {
                            ActivityCompat.requestPermissions(ChoseActivity.this, LocationPermission, Location_Request);
                        }).show();
*/
            } else {
                ActivityCompat.requestPermissions(SplashScreen.this, LocationPermission, Location_Request);
            }

        } else {
            initParam();//start
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Location_Request && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
            initParam();
        } else {
            finish();
        }
    }

}