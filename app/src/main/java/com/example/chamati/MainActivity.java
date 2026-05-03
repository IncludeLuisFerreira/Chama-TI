package com.example.chamati;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cadastroBtn = findViewById(R.id.cadastroBtn);
        Button listaChamadoBtn = findViewById(R.id.listaChamadoBtn);

        cadastroBtn.setOnClickListener(v -> 
            startActivity(new Intent(this, CadastroChamadoActivity.class)));
        
        listaChamadoBtn.setOnClickListener(v -> 
            startActivity(new Intent(this, ListaChamadoActivity.class)));
    }
}