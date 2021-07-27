package com.example.problemstatement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class CordDisplay extends AppCompatActivity {

    ListView lv;
    Button btnRef, btnFav;
    TextView tvRecord;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cord_display);

        //=======================InitUI=======================
        lv = findViewById(R.id.lvList);
        btnFav = findViewById(R.id.btnFav);
        btnRef = findViewById(R.id.btnRefresh);
        tvRecord = findViewById(R.id.tvNumDisplay);
        //=======================InitUI=======================

        String folderLocation_I = getFilesDir().getAbsolutePath() + "/MyFolder";
        File targetFile = new File(folderLocation_I, "data.txt");
        if (targetFile.exists() == true) {

            String data = "";
            try {
                FileReader reader = new FileReader(targetFile);
                BufferedReader br = new BufferedReader(reader);
                String line = br.readLine();
                while (line != null){
                    data += line + "\n";
                    line = br.readLine();
                }
                String [] array = data.split("\n");
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1,array);
                lv.setAdapter(adapter);
                tvRecord.setText("Number of records:" + array.length);
                br.close();
                reader.close();
            } catch (Exception e) {
                Toast.makeText(CordDisplay.this, "Failed to read!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            Log.d("Content", data);
        }



        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CordDisplay.this, Favourite.class);
                startActivity(i);
            }
        });

        btnRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyDataSetChanged();
            }
        });
    }
}