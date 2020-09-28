package com.example.projectpixer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FaceDetectionFunctionOption extends AppCompatActivity {

    Button scanBtn, repoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection_function_option);

        scanBtn = findViewById(R.id.scanBtn);
        repoBtn = findViewById(R.id.reportBtn);

    }

    @Override
    protected void onResume() {
        super.onResume();

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FaceDetectionFunctionOption.this, FaceDetectionFunction.class);
                startActivity(intent);
            }
        });

        repoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FaceDetectionFunctionOption.this, FaceDetectionReport.class);
                startActivity(intent);
            }
        });
    }
}