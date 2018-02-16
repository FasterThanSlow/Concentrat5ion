package com.example.concentration;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(promptsView);

        final EditText count = (EditText) promptsView.findViewById(R.id.editText);
        final EditText size = (EditText) promptsView.findViewById(R.id.editText2);
        final EditText speed = (EditText) promptsView.findViewById(R.id.editText3);

        builder.setPositiveButton("Запустить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                LinearLayout layout = findViewById(R.id.circles_container);

                int starCount = Integer.parseInt(count.getText().toString());
                int speedStr = Integer.parseInt(speed.getText().toString());
                int sizeStr = Integer.parseInt(size.getText().toString());

                if(starCount == 0){
                    starCount = 2;
                }
                if(speedStr == 0){
                    speedStr = 5;
                }
                if(sizeStr == 0){
                    sizeStr = 70;
                }

                SurfaceView view = new MovementView(MainActivity.this, starCount, speedStr, sizeStr);

                layout.addView(view);
            }
        });
        builder.setNegativeButton("Стандартные настройки", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                LinearLayout layout = findViewById(R.id.circles_container);
                SurfaceView view = new MovementView(MainActivity.this,4, 5, 70);
                layout.addView(view);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
