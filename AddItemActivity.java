package com.zybooks.inventoryapp_nicholaslindner;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {

    EditText itemName;
    EditText quantity;
    Button buttonAddItem;
    DataBaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        itemName = findViewById(R.id.editItemName);
        quantity = findViewById(R.id.editQuantity);
        buttonAddItem = findViewById(R.id.buttonAddItem);

        databaseHelper = new DataBaseHelper(
                this,
                "inventory.db",
                null,
                1
        );

        buttonAddItem.setOnClickListener(v->{

            String name = itemName.getText().toString();
            String amount = quantity.getText().toString();

            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            //Convert User text to Int
            int quantityNumber = Integer.parseInt(amount);

            ContentValues values = new ContentValues();

            values.put("item_name", name);
            values.put("quantity", quantityNumber);

            long result = db.insert(
                    "inventory",
                    null,
                    values
            );

            Toast.makeText(
                    this,
                    "Added: " + name + " - " + amount,
                    Toast.LENGTH_SHORT
            ).show();

                });
    }
}//End