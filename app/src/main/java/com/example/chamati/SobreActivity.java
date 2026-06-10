package com.example.chamati;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SobreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());
    }
}
