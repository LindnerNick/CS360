package com.zybooks.inventoryapp_nicholaslindner;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.health.connect.datatypes.units.Length;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {
    List<InventoryItem> inventoryItems;
    Button buttonNotifications;
    Button buttonAddItem;
    DataBaseHelper databaseHelper;
    EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        inventoryItems = new ArrayList<>();
        TableLayout tableInventory = findViewById(R.id.tableInventory);
        editSearch = findViewById(R.id.editSearch);


        databaseHelper = new DataBaseHelper(
                this,
                "inventory.db",
                null,
                1
        );

        loadInventory();

        Toast.makeText(
                this,
                "items found:" + inventoryItems.size(),
                Toast.LENGTH_LONG
        ).show();

        buttonNotifications = findViewById(R.id.buttonNotifications);
        buttonAddItem = findViewById(R.id.buttonAddItem);

        buttonAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(
                    InventoryActivity.this,
                    AddItemActivity.class
            );

            startActivity(intent);
            });


        //Simplified by studio - SMS notification screen
        buttonNotifications.setOnClickListener(v -> {

            Intent intent = new Intent(
                    InventoryActivity.this,
                    SmsActivity.class
            );

            startActivity(intent);

        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadInventory();
        displayInventory("");

        //Do the items load?
       // Toast.makeText(
                //this,
                //"items found:" + inventoryItems.size(),
                //Toast.LENGTH_SHORT
        //).show();

    }
    private void displayInventory(String searchText) {

        TableLayout tableInventory = findViewById(R.id.tableInventory);

        while (tableInventory.getChildCount() > 1) {
            tableInventory.removeViewAt(1);
        }

        for (InventoryItem item : inventoryItems) {

            //Lets user search items
            if (!item.getItemName()
                    .toLowerCase()
                    .contains(searchText)) {

                continue;
            }

            TableRow row = new TableRow(this);

            //Grab item name and show
            TextView itemName = new TextView(this);
            itemName.setText(item.getItemName());
            itemName.setTextSize(18);
            itemName.setPadding(12, 12, 12, 12);

            //Grab item quantity and show
            TextView quantity = new TextView(this);
            quantity.setText(String.valueOf(item.getQuantity()));
            quantity.setTextSize(18);
            quantity.setPadding(12, 12, 12, 12);

            //Create Edit button
            Button editButton = new Button(this);
            editButton.setText("Edit");

            editButton.setOnClickListener(v -> {

                Intent intent = new Intent(
                        InventoryActivity.this,
                        EditItemActivity.class
                );

                intent.putExtra("item_id", item.getId());

                startActivity(intent);
            });

            //Create Delete Button
            Button deleteButton = new Button(this);
            deleteButton.setText("Delete");

            deleteButton.setOnClickListener(v -> {

                SQLiteDatabase db =
                        databaseHelper.getWritableDatabase();

                db.delete(
                        "inventory",
                        "id = ?",
                        new String[]{
                                String.valueOf(item.getId())
                        }
                );

                Toast.makeText(
                        this,
                        "Item deleted",
                        Toast.LENGTH_SHORT
                ).show();

                loadInventory();
                displayInventory("");
            });


            row.addView(itemName);
            row.addView(quantity);
            row.addView(editButton);
            row.addView(deleteButton);

            tableInventory.addView(row);
        }
    }

    private void loadInventory() {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "inventory",
                null,
                null,
                null,
                null,
                null,
                null
        );

        //Don't gooo doubling to try to please me!!!(music note would add to the effect)
        inventoryItems.clear();

        if (cursor.moveToFirst()) {

            do {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String itemName = cursor.getString(
                        cursor.getColumnIndexOrThrow("item_name")
                );

                int quantity = cursor.getInt(
                        cursor.getColumnIndexOrThrow("quantity")
                );

                InventoryItem item = new InventoryItem(
                        id,
                        itemName,
                        quantity
                );

                inventoryItems.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void sendOutOfStockAlert(String itemName) {

        // Check if user granted permission.
        if (checkSelfPermission(
                Manifest.permission.SEND_SMS
        ) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(
                    this,
                    "SMS permission not granted. Inventory will continue normally.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        SharedPreferences preferences =
                getSharedPreferences(
                        "inventory_preferences",
                        MODE_PRIVATE
                );

        String phoneNumber =
                preferences.getString("phone_number", "");

        if (phoneNumber.isEmpty()) {

            Toast.makeText(
                    this,
                    "No phone number has been entered for SMS alerts.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String message =
                "Inventory Alert: " + itemName +
                        " is out of stock.";

        SmsManager smsManager =
                SmsManager.getDefault();

        smsManager.sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null
        );

        Toast.makeText(
                this,
                "SMS alert sent for " + itemName,
                Toast.LENGTH_LONG
        ).show();
    }

}//End