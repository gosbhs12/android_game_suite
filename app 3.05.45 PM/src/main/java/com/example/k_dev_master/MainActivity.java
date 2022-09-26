package com.example.k_dev_master;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.k_dev_master.game2048.LogicGame2048;
import com.example.k_dev_master.gomoku.GomokuGame;
import com.example.k_dev_master.memorygame.MemoryGame;
import com.example.k_dev_master.user.UserProfile;


public class MainActivity extends AppCompatActivity {

    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 3;
    private String curUser = "";

    //private CircleIndicator3 mIndicator;

    public static int gameSelect = 0;

    //mPager -> popupview로 넘어가는게 불가능하면
    //popupview -> mpager로 넘겨야할듯
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);

        //Child->Parent Data 받기
        ActivityResultLauncher<Intent> mainResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            curUser = data.getStringExtra("curUser");
                            //System.out.println("Cur User: " + curUser);
                        }
                    }
                });

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this); // 1
        View mainView = inflater.inflate(R.layout.activity_main, null); // 2 and 3
        setContentView(mainView);


        /**         * Horizontal Slide View Fragment         */
        //ViewPager2
        mPager = findViewById(R.id.viewpager);
        //Adapter
        pagerAdapter = new MyAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);
        //ViewPager Setting
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mPager.setCurrentItem(0);
        //시작 지점
        mPager.setOffscreenPageLimit(3);
        //최대 이미지 수
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }
        });


        Button btTeamInfo = findViewById(R.id.teamInfo); // TeamInfo button
        Button commonStart = findViewById(R.id.commonStart); // GameStart button
        Button profileCreate = findViewById(R.id.userProfile);
        Button quitButton = findViewById(R.id.exit); // exit button


        btTeamInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShowTeamInfo.class);
                startActivity(intent);
                finish();
            }
        });

        profileCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                mainResultLauncher.launch(intent);
            }
        });

        commonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameSelect == 1) {
                    Intent intent = new Intent(getApplicationContext(), LogicGame2048.class);
                    startActivity(intent);
                } else if (gameSelect == 2) {
                    Intent intent = new Intent(getApplicationContext(), MemoryGame.class);
                    intent.putExtra("curUser", curUser);
                    startActivity(intent);
                } else if (gameSelect == 3) {
                    Intent intent = new Intent(getApplicationContext(), GomokuGame.class);
                    startActivity(intent);
                }
            }
        });


        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });


    }

}