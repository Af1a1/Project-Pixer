package com.example.projectpixer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectFunction extends AppCompatActivity {

    Toolbar toolbar;
    Button objBtn, expBtn, txtBtn, faceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_function);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);


        objBtn = findViewById(R.id.objectBtn);
        expBtn = findViewById(R.id.expressionBtn);
        txtBtn = findViewById(R.id.textBtn);

    }

    @Override
    protected void  onResume(){
        super.onResume();

        objBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SelectFunction.this, ObjectFunctionOption.class);
                startActivity(intent);

            }
        });

        expBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SelectFunction.this, expressionFunctionOption.class);
                startActivity(intent);
            }
        });

        txtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SelectFunction.this, textFunctionOption.class);
                startActivity(intent);
            }
        });


    }


}