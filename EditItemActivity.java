package com.zybooks.inventoryapp_nicholaslindner;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditItemActivity extends AppCompatActivity {

    EditText itemName;
    EditText quantity;
    Button buttonUpdateItem;
    DataBaseHelper databaseHelper;
    int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        itemName = findViewById(R.id.editItemName);
        quantity = findViewById(R.id.editQuantity);
        buttonUpdateItem = findViewById(R.id.buttonUpdateItem);

        // Get the ID from InventoryActivity.
        itemId = getIntent().getIntExtra("item_id", -1);

        databaseHelper = new DataBaseHelper(
                this,
                "inventory.db",
                null,
                1
        );

        // Load the current item information.
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "inventory",
                new String[]{"item_name", "quantity"},
                "id = ?",
                new String[]{String.valueOf(itemId)},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow("item_name")
            );

            int amount = cursor.getInt(
                    cursor.getColumnIndexOrThrow("quantity")
            );

            itemName.setText(name);
            quantity.setText(String.valueOf(amount));
        }

        cursor.close();

        // Update the inventory item.
        buttonUpdateItem.setOnClickListener(v -> {

            String name = itemName.getText().toString();
            String amount = quantity.getText().toString();

            // Convert quantity from text to integer.
            int quantityNumber = Integer.parseInt(amount);

            SQLiteDatabase updateDb =
                    databaseHelper.getWritableDatabase();

            // Get the item's current quantity before updating it.
            Cursor oldQuantityCursor = updateDb.query(
                    "inventory",
                    new String[]{"quantity"},
                    "id = ?",
                    new String[]{String.valueOf(itemId)},
                    null,
                    null,
                    null
            );

            int oldQuantity = -1;

            if (oldQuantityCursor.moveToFirst()) {

                oldQuantity = oldQuantityCursor.getInt(
                        oldQuantityCursor.getColumnIndexOrThrow("quantity")
                );
            }

            oldQuantityCursor.close();

            ContentValues values = new ContentValues();

            values.put("item_name", name);
            values.put("quantity", quantityNumber);

            // Update the specific item using its ID.
            updateDb.update(
                    "inventory",
                    values,
                    "id = ?",
                    new String[]{String.valueOf(itemId)}
            );

            Toast.makeText(
                    this,
                    "Item updated",
                    Toast.LENGTH_SHORT
            ).show();

            // Only alert when the quantity changes from
            // above zero to exactly zero.
            if (oldQuantity > 0 && quantityNumber == 0) {

                Toast.makeText(
                        this,
                        name + " is out of stock!",
                        Toast.LENGTH_LONG
                ).show();
            }

            // Return to inventory.
            finish();
        });
    }

}//End