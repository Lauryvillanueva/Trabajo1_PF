package com.uninorte.transdigital;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

public class Intro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        FlowManager.init(new FlowConfig.Builder(this).build());
    }

    public void Onclik_Iniciar(View view) {
        Intent i = new Intent(this, Login.class);
        startActivity(i);

    }

    public void Onclick_Registro(View view) {
        Intent i = new Intent(this, Register.class);
        startActivity(i);

    }
}
