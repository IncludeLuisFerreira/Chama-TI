package com.example.chamati;

import android.os.Bundle;

public class SobreActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);
        setActivityTitle("Sobre o Sistema");
        setSelectedNavItem(R.id.nav_sobre);
    }
}
