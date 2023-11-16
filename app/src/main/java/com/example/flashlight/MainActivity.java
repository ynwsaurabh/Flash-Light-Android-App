package com.example.flashlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    ImageButton image_button; // declared an image button
    boolean isFlashlightOn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image_button = findViewById(R.id.btnlight);
        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFlashlight();
            }
        });

        // Request CAMERA permission at runtime
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            // Permissions granted, initi.alize flashlight
                            initFlashlight();
                        } else {
                            Toast.makeText(MainActivity.this, "Camera Permission is Required", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();

    }

    private void initFlashlight() {
        // Check if the device has a camera flash
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "Your device doesn't support a flashlight.", Toast.LENGTH_SHORT).show();
            image_button.setEnabled(false);
            return;
        }

        // Get the camera manager and try to open the camera
        final CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            final String cameraId = cameraManager.getCameraIdList()[0];
            // Set initial state and update UI
            setFlashlightState(false);
            // Store the camera ID for later use
            image_button.setTag(cameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to access the camera.", Toast.LENGTH_SHORT).show();
            image_button.setEnabled(false);
        }
    }

    private void toggleFlashlight() {
        String cameraId = (String) image_button.getTag();
        if (cameraId == null) {
            // Camera not initialized
            return;
        }

        try {
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            isFlashlightOn = !isFlashlightOn;
            cameraManager.setTorchMode(cameraId, isFlashlightOn);
            setFlashlightState(isFlashlightOn);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to toggle flashlight.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setFlashlightState(boolean isOn) {
        if (isOn) {
            Toast.makeText(this, "Flashlight is ON", Toast.LENGTH_SHORT).show();
            image_button.setImageResource(R.drawable.torch_on);
            image_button.setContentDescription(getString(R.string.flashlight_on));
        } else {
            Toast.makeText(this, "Flashlight is OFF", Toast.LENGTH_SHORT).show();
            image_button.setImageResource(R.drawable.flash_off);
            image_button.setContentDescription(getString(R.string.flashlight_off));
        }
    }

}