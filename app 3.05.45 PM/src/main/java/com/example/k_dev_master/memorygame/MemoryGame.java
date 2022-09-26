package com.example.k_dev_master.memorygame;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k_dev_master.*;
import com.example.k_dev_master.R;
import com.example.k_dev_master.databinding.ActivityMemorygameBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class MemoryGame extends AppCompatActivity {

    private final long TIME_DISPLAY_STAGE1 = 4000;
    private final long TIME_DISPLAY_STAGE2 = 8000;
    private final long TIME_DISPLAY_STAGE3 = 5000;
    ActivityMemorygameBinding binding;
    private int stageLevel = 0;
    Vector<Card> cards;
    Timer timer;
    String curUser = "none";
    private long recordTime = 0;
    private long currTimer = 0;
    private int gameState;
    private static final int GAME_ONGOING = 0;
    private static final int GAME_DONE = 1;
    Vector<Integer> selectedPos;
    TimerTask tt;
    TextView timerText;
    MemoryGameAdapter adapter;
    TextView stageText;
    TextView recordText;
    TextView rank1;
    TextView rank2;
    TextView rank3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);

        if (getIntent().getStringExtra("curUser").length() != 0) {
            curUser = getIntent().getStringExtra("curUser");
            System.out.println(curUser);
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_memorygame);
//      binding.restartBtn.setOnClickListener(view -> recreate());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        binding.cardViews.setLayoutManager(gridLayoutManager);
        adapter = new MemoryGameAdapter(this);
        binding.cardViews.setAdapter(adapter);

        gameState = GAME_ONGOING;
        recordTime = 0;
        currTimer = 0;
        stageLevel = 1;
        setTask();
        selectedPos = new Vector<>();
        //Timer
        timerText = (TextView) findViewById(R.id.timerTxtView);
        stageText = (TextView) findViewById(R.id.stageTxtView);
        recordText = (TextView) findViewById(R.id.recordTxtView);


        //비교할 후보들 저장

        addCards();
        adapter.setUpPicture(cards);
        binding.cardLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = binding.cardViews.getWidth() / 4 - 10;
                int height = binding.cardViews.getHeight() / 4 - 10;
                Log.e("width", String.valueOf(width));
                Log.e("height", String.valueOf(height));
                adapter.setLength(width, height);
                // set length
                adapter.notifyDataSetChanged();
                // recycleView alert
                Handler handler = new Handler();
                handler.postDelayed(MemoryGame.this::start, 100); // timer need to be implemented
                binding.cardLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        // popup menu button implementation
        ImageButton openMenu = findViewById(R.id.ListViewBtnMemory);
        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MemoryGame.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.pop:
                                startActivity(new Intent(getApplicationContext(), MemoryGame.class));
                                break;
                            case R.id.pop_inst:
                                LayoutInflater inflater = (LayoutInflater)
                                        getSystemService(LAYOUT_INFLATER_SERVICE);
                                View popupView = inflater.inflate(R.layout.popup_instruction_memory, null);

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


    RecyclerView.OnItemTouchListener onItemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent evt) {
            int action = evt.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    View child = recyclerView.findChildViewUnder(evt.getX(), evt.getY());
//                    assert child != null;
                    int pos = recyclerView.getChildAdapterPosition(child);
                    // get position of touched and its index of vector
                    Log.e("pos", String.valueOf(pos));
                    View txtView = child.findViewById(R.id.cardTxtView);
                    int check = cards.get(pos).getCheck();
                    if(check == 1) { // card selected
                        txtView.animate()
                                .rotationYBy(360)
                                .rotationY(90)
                                .setDuration(200)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        txtView.animate()
                                                .rotationYBy(90)
                                                .rotationY(0)
                                                .setDuration(200)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {
                                                        adapter.setImage(pos, cards.get(pos).getDisplay());
                                                    }

                                                    /**
                                                     * game logic is here
                                                     * @param animation
                                                     */
                                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        selectedPos.add(pos);
                                                        // selectedPos add positions
                                                        if (selectedPos.size() == 2) {
                                                            // selectedPos vector, start to compare if we picked two cards
                                                            int pos1 = selectedPos.get(0);
                                                            int pos2 = selectedPos.get(1);
                                                            if(pos1 != pos2) { // must have different place
                                                                String display = cards.get(pos1).getTag();
                                                                String display2 = cards.get(pos2).getTag();
                                                                Log.e("display1", display);
                                                                Log.e("display2", display2);
                                                                if (display.equals(display2)) {
                                                                    Toast.makeText(MemoryGame.this, "Correct!", Toast.LENGTH_SHORT).show();
                                                                    adapter.update(pos1, 2);
                                                                    adapter.update(pos2, 2);
                                                                    Log.e("Matched!", display);
                                                                    if (matchedAll(cards)) {
                                                                        Log.e("In if statement", "yes!");
                                                                        timer.cancel();
                                                                        stageUp();
                                                                    }
                                                                } else {
                                                                    adapter.update(pos1, 0);
                                                                    adapter.update(pos2, 0);
                                                                    Log.e("UnMatched..", display);
                                                                }
                                                            } else {
                                                                adapter.update(pos1, 0);
                                                            }
                                                            selectedPos.removeAllElements(); // selectedPos vector refresh
                                                            selectedPos.clear();
                                                        }
                                                    }
                                                })
                                                .start();
                                    }
                                })
                                .start();
                    }
                    break;
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    };


    private boolean matchedAll(Vector<Card> cards) {
        for (Card card : cards) {
            if (card.getCheck() != 2) { // if a card is matched, the check value is 2
                return false;
            }
        }
        return true;
    }

    private void regame() {
        for (Card card : cards) {
            card.setCheck(0);
        }
        Collections.shuffle(cards);
    }

    private void start() {
        //Stage Text View
        stageText.setText("Stage: " + stageLevel);
        //Countdown Timer
        new CountDownTimer(getTimeDisplay(), 1000) {
            public void onTick(long millisUntilFinished) {
                binding.timerTxtView.setVisibility(View.VISIBLE);
                timerText.setTextColor(Color.RED);
                timerText.setText("Memorize in: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerText.setTextColor(Color.BLACK);
            }
        }.start();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // show cards
            if (stageLevel == 1) {
                binding.cardViews.addOnItemTouchListener(onItemTouchListener);
            }
            adapter.setStartAnimate(true);
            // show all the cards when starting
        }, getTimeDisplay()); // for 10 secs
        handler.postDelayed(this::timer, getTimeDisplay());
    }

    private void timer() {
        binding.timerTxtView.setVisibility(View.VISIBLE);
        //timer implement needed
        //stage 1 2 3 will be initiated in here
        timer = new Timer();
        timer.schedule(tt, 0,100); // count 100 each 10ms
    }

    private void setTask() { // set TimerTake again
        tt = new TimerTask() {
            @Override
            public void run()
            {
                runOnUiThread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.e("Counting Time", String.valueOf(currTimer));
                        currTimer += 1;
                        timerText.setText(getTimerText());
                    }
                });
            }
        };
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void stageUp() {
        binding.timerTxtView.setVisibility(View.GONE);
        if (stageLevel == 1) {
            stageLevel = 2;
            Log.e("Curr time", String.valueOf(currTimer));
            recordTime += currTimer;
        } else if (stageLevel == 2) {
            stageLevel = 3;
            Log.e("Curr time", String.valueOf(currTimer));
            recordTime += currTimer;

        } else if (stageLevel == 3) {
            gameState = GAME_DONE;
            recordTime += currTimer;
            currTimer = 0;
            recordText.setText("Record: " + recordTime);
            Log.e("Total recorded time", String.valueOf(recordTime));
        }
        if (gameState == GAME_ONGOING) {
            currTimer = 0;
            setTask();
            adapter.setStartAnimate(false);
            regame();
            binding.cardLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int width = binding.cardViews.getWidth() / 4 - 10;
                    int height = binding.cardViews.getHeight() / 4 - 10;
                    Log.e("width", String.valueOf(width));
                    Log.e("height", String.valueOf(height));
                    adapter.setLength(width, height);
                    // set length
                    adapter.notifyDataSetChanged();
                    // recycleView alert
                    Handler handler = new Handler();
                    handler.postDelayed(MemoryGame.this::start, 0); // timer need to be implemented
                    binding.cardLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        } else if (gameState == GAME_DONE) {

                setContentView(R.layout.leaderboard_memory);
                rank1 = findViewById(R.id.rank1);
                rank2 = findViewById(R.id.rank2);
                rank3 = findViewById(R.id.rank3);
                ArrayList<Player> lists = new ArrayList<>();
                String fname = "userProfiles";
                writeFile(fname, recordTime);
                String fileContents = "";
                try {
                    InputStream iStream = openFileInput(fname);
                    if(iStream != null) {
                        InputStreamReader iStreamReader = new InputStreamReader(iStream);
                        BufferedReader bufferedReader = new BufferedReader(iStreamReader);
                        String temp = "";
                        StringBuffer sBuffer = new StringBuffer();
                        while ((temp = bufferedReader.readLine()) != null) {
                            sBuffer.append(temp);
                            Log.e("temp rn","" + temp );
                            fileContents = sBuffer.toString();
                            String[] words = temp.split(",");
                            Log.e("after words","" + words[0] );
                            String name = words[0];
                            Player player;
                            Log.e("after player","" + words.length);

                            if (words.length == 2) {
                                long time = Long.parseLong(words[1]);
                                player = new Player(name, time);
                            } else {
                                player = new Player(name, Long.MAX_VALUE);
                            }
                            Log.e("after done","" + 4 );
                            Log.e("list aded","" + lists.size() );
                            Log.e("playername","" + player.getName() );
                            Log.e("player","" + player.getRecordedTime() );
                            lists.add(player);
                        }
                        iStream.close();
                    }
                } catch (FileNotFoundException e) {
                    System.out.println((e.getMessage()));
                } catch(Exception e) {
                }
                Collections.sort(lists);
                Log.e("list size=","" + lists.size() );
                if (lists.size() >= 1) {
                    String str = (new Long(lists.get(0).getRecordedTime())).toString();
                    rank1.setText("1. " + lists.get(0).getName() + ", " + str);
                }
                if (lists.size() >= 2) {
                    rank2.setText("2. " + lists.get(1).getName() + ", " + lists.get(1).getRecordedTime());
                }
                if (lists.size() >= 3) {
                    rank3.setText("3. " + lists.get(2).getName() + ", " + lists.get(2).getRecordedTime());
                }
            Button back = findViewById(R.id.backbutton);
            back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MemoryGame.class);
                    intent.putExtra("curUser", curUser);
                    startActivity(intent);
                }
            });
        }
    }
    // save the user to file
    private void writeFile(String fileName, Long time) {
        try {
            InputStream iStream = openFileInput(fileName);
            if(iStream != null) {
                InputStreamReader iStreamReader = new InputStreamReader(iStream);
                BufferedReader bufferedReader = new BufferedReader(iStreamReader);
                String temp = "";
                StringBuffer sBuffer = new StringBuffer();

                while ((temp = bufferedReader.readLine()) != null) {
                    sBuffer.append(temp);
                    Log.e("name : ", curUser);
                    Log.e("what is temp : ", temp);
                    if (temp != "\n") {
                        String[] words = temp.split(",");
                        String name = words[0];
                        if (curUser.equals(name)) {
                            Log.e("time:", ""+time);
                            FileOutputStream fOS = openFileOutput(fileName, Context.MODE_APPEND);
                            String time_string = String.valueOf(time);
                            fOS.write(time_string.getBytes(Charset.forName("UTF-8")));
                            fOS.write("\n".getBytes(StandardCharsets.UTF_8));
                            fOS.close();
                            break;
                        }
                    }
                }
                iStream.close();

            }

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void addCards() {
        cards = new Vector<>();
        cards.add(new Card(R.drawable.card_clubs1, "1"));
        cards.add(new Card(R.drawable.card_clubs1, "1"));
        cards.add(new Card(R.drawable.card_clubs2, "2"));
        cards.add(new Card(R.drawable.card_clubs2, "2"));
        cards.add(new Card(R.drawable.card_clubs3, "3"));
        cards.add(new Card(R.drawable.card_clubs3, "3"));
        cards.add(new Card(R.drawable.card_clubs4, "4"));
        cards.add(new Card(R.drawable.card_clubs4, "4"));
        cards.add(new Card(R.drawable.card_clubs5, "5"));
        cards.add(new Card(R.drawable.card_clubs5, "5"));
        cards.add(new Card(R.drawable.card_clubs6, "6"));
        cards.add(new Card(R.drawable.card_clubs6, "6"));
        cards.add(new Card(R.drawable.card_clubs7, "7"));
        cards.add(new Card(R.drawable.card_clubs7, "7"));
        cards.add(new Card(R.drawable.card_clubs8, "8"));
        cards.add(new Card(R.drawable.card_clubs8, "8"));
        boolean shuffle = false;
        if (shuffle) {
            Collections.shuffle(cards);
        }
    }

    private long getTimeDisplay() {
        if (stageLevel == 1) {
            return TIME_DISPLAY_STAGE1;
        } else if (stageLevel == 2) {
            return TIME_DISPLAY_STAGE2;
        } else if (stageLevel == 3) {
            return TIME_DISPLAY_STAGE3;
        } else {
            return 0;
        }
    }

    private String getTimerText()
    {
        int rounded = (int) Math.round(currTimer);

        long milliseconds = currTimer % 10;
        int seconds = rounded / 10;

        return formatTime(milliseconds, seconds);
    }

    private String formatTime(long milliseconds, int seconds)
    {
        return String.format("%02d",seconds) + "." + String.format("%01d",milliseconds);
    }

}
