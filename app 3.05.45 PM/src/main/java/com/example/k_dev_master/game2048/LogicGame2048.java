package com.example.k_dev_master.game2048;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.k_dev_master.MainActivity;
import com.example.k_dev_master.R;

public class LogicGame2048 extends AppCompatActivity {
    // 2048 Game
    private MainView2048 view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        view = new MainView2048(this);
        setContentView(R.layout.activity_main_2048game);
        ImageButton openMenu = findViewById(R.id.ListViewBtn2048);
        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(LogicGame2048.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.pop:
                                startActivity(new Intent(getApplicationContext(), LogicGame2048.class));
                                break;
                            case R.id.pop_inst:
                                LayoutInflater inflater = (LayoutInflater)
                                        getSystemService(LAYOUT_INFLATER_SERVICE);
                                View popupView = inflater.inflate(R.layout.popup_instruction_2048, null);

                                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                boolean focusable = true; // lets taps outside the popup also dismiss it
                                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                                // show the popup window
                                // which view you pass in doesn't matter, it is only used for the window tolken
                                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                                // dismiss the popup window when touched
                                popupView.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        popupWindow.dismiss();
                                        return true;
                                    }
                                });
                                break;
                            case R.id.pop_exit:
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                                break;
                            default:
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }
}
