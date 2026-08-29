package com.zybooks.inventoryapp_nicholaslindner;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button buttonLogin;
    Button buttonCreateAccount;
    EditText username;
    EditText password;
    DataBaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.editUsername);
        password = findViewById(R.id.editPassword);

        //Start databases
        databaseHelper = new DataBaseHelper(
                this,
                "inventory.db",
                null,
                1
        );


        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);


        //Simplified by studio - Login goes to  inventory
        buttonLogin.setOnClickListener(v -> {

            String user = username.getText().toString();
            String pass = password.getText().toString();

            SQLiteDatabase db = databaseHelper.getReadableDatabase();

            Cursor cursor = db.query(
                    "users",
                    new String[]{"id"},
                    "username = ? AND password = ?",
                    new String[]{user, pass},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {

                Toast.makeText(
                        MainActivity.this,
                        "Logging in...",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent = new Intent(
                        MainActivity.this,
                        InventoryActivity.class
                );

                startActivity(intent);

            } else {

                Toast.makeText(
                        MainActivity.this,
                        "Invalid username or password",
                        Toast.LENGTH_SHORT
                ).show();
            }

            cursor.close();
        });

        //Simplified by studio - Create Account
        buttonCreateAccount.setOnClickListener(v -> {

            String user = username.getText().toString();
            String pass = password.getText().toString();

            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            //Username as identifier for account existence
            Cursor cursor = db.query(
                    "users",
                    new String[]{"id"},
                    "username = ?",
                    new String[]{user},
                    null,
                    null,
                    null
            );
            //Check if account is already in use
            if (cursor.moveToFirst()) {
                Toast.makeText(
                        MainActivity.this,
                        "username already in use",
                        Toast.LENGTH_SHORT
                ).show();
                //Create account if not in use
            } else {
                ContentValues values = new ContentValues();
                values.put("username", user);
                values.put("password", pass);

                long result = db.insert(
                        "users",
                        null,
                        values
                );
                //Create account message
                Toast.makeText(
                        MainActivity.this,
                        "Account Created",
                        Toast.LENGTH_SHORT
                ).show();
            }
            cursor.close();//Always turn off the lights!
        });

    }}//End


