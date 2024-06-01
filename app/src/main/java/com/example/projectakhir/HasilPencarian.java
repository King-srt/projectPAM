package com.example.projectakhir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class HasilPencarian extends AppCompatActivity {
    TextView cari1;
    TextView cari2;
    EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_pencarian);

        cari1=findViewById(R.id.textView4);
        cari2=findViewById(R.id.textView5);
        search=findViewById(R.id.editText_search);

        Intent intent = getIntent();
        String dataCari = intent.getStringExtra("data");
        cari1.setText(dataCari);
        cari2.setText(dataCari);
        search.setText(dataCari);
    }
}