package com.example.jonny.fftcgcompanion.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;

import com.example.jonny.fftcgcompanion.R;
import com.example.jonny.fftcgcompanion.utils.CameraSourcePreview;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.regex.Pattern;

public class CardScannerActivity extends AppCompatActivity
{
    private CameraSource m_cameraSource = null;
    private CameraSourcePreview m_preview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_scanner);

        m_preview = findViewById(R.id.preview);

        createCameraSource(true, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try
        {
            m_preview.start(m_cameraSource);
        } catch (IOException e)
        {
            e.printStackTrace();
            m_cameraSource.release();
            m_cameraSource = null;
        }
    }



    private void createCameraSource(boolean autoFocus, boolean useFlash)
    {
        TextRecognizer textRecogniser = new TextRecognizer.Builder(this).build();
        textRecogniser.setProcessor(new Detector.Processor<TextBlock>()
        {
            @Override
            public void release()
            {
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections)
            {
                String standardRegex = "\\d+-\\d{3}[CRHLS]";
                String prRegex = "PR-\\d{3}";
                SparseArray<TextBlock> items = detections.getDetectedItems();
                for (int i = 0; i < items.size(); ++i) {
                    TextBlock item = items.valueAt(i);
                    String itemString = item.getValue();
                    if (Pattern.matches(standardRegex, itemString)
                    || Pattern.matches(prRegex, itemString))
                    {
                        finishWithResult(itemString);
                    }
                }
            }
        });

        if (!textRecogniser.isOperational())
        {
            finish();
        }

        m_cameraSource = new CameraSource.Builder(this, textRecogniser)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(autoFocus)
                .build();

    }

    private void finishWithResult(String cardID)
    {
        Intent intent = new Intent();
        intent.putExtra("CardID", cardID);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
