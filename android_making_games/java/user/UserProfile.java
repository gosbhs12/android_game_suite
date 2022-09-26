package com.example.k_dev_master.user;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.k_dev_master.MainActivity;
import com.example.k_dev_master.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {
    //private View userView;
    private EditText userName;
    private final String fileName = "userProfiles";

    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(UserProfile.this);
        View userView = inflater.inflate(R.layout.internalstorage, null);
        setContentView(userView);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        userName = findViewById(R.id.user_name);

        Button profileCheck = findViewById(R.id.profile_create);
        Button profileExit = findViewById(R.id.profile_exit);

        profileCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFile(getApplicationContext());
                if(!checkUser(getApplicationContext())) {
                    writeUser(getApplicationContext());
                }
                //Need to add user name to intent
                getIntent().putExtra("curUser", userName.getText().toString().trim());
                //System.out.println("In UP extra: " + getIntent().getStringExtra("curUser"));

                System.out.println("In UP extra: " + getIntent().getStringExtra("curUser"));
                setResult(RESULT_OK, getIntent());
            }
        });

        profileExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //curUser = "User Name";
                finish();
            }
        });


    }

    private void checkFile(Context context) {
        // File file = new File(context.getFilesDir(), fileName);
        File file = new File(getFilesDir(), fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Toast.makeText(context, String.format("File %s creation failed", fileName), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkUser(Context context) {
        try {
            FileInputStream fileInputStream =  context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            boolean ret = false;

            while (line != null) {
                if (line.length() != 0) {
                    if(line.equals(userName.getText().toString().trim())) {
                        ret = true;
                        Toast.makeText(context, String.format("Welcome %s!", userName.getText().toString().trim())
                                , Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                line = reader.readLine();
            }
            reader.close();
            inputStreamReader.close();
            fileInputStream.close();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void writeUser(Context context) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_APPEND);

            fileOutputStream.write(userName.getText().toString().getBytes(Charset.forName("UTF-8")));

            String basic = ",";
            fileOutputStream.write(basic.getBytes());
            fileOutputStream.close();



            Toast.makeText(context, String.format("New Profile %s Created!", userName.getText().toString()), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, String.format("Write to %s failed", fileName), Toast.LENGTH_SHORT).show();
        }
    }

    public String getFileName() {
        return fileName;
    }
}
