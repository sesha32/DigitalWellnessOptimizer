package com.example.digitalwellnessoptimizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceCaptureWorker extends Worker {
    private static final String TAG = "FaceCaptureWorker";
    private ExecutorService cameraExecutor;

    public FaceCaptureWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public Result doWork() {
        captureImage();
        return Result.success();
    }

    private void captureImage() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(getApplicationContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                ImageCapture imageCapture = new ImageCapture.Builder().build();

                File outputDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (outputDir == null) return;

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                File photoFile = new File(outputDir, "IMG_" + timeStamp + ".jpg");

                ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
                imageCapture.takePicture(outputOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Log.d(TAG, "Image captured: " + photoFile.getAbsolutePath());
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Image capture failed", exception);
                    }
                });

                cameraProvider.unbindAll();
            } catch (Exception e) {
                Log.e(TAG, "Camera initialization failed", e);
            }
        }, cameraExecutor);
    }
}
