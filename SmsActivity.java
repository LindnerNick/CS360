package com.zybooks.inventoryapp_nicholaslindner;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class SmsActivity extends AppCompatActivity {

    Button buttonGrantPermission;
    Button buttonBackInventory;

    TextView textPermissionStatus;
    EditText editPhoneNumber;

    private final ActivityResultLauncher<String> smsPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {

                        if (isGranted) {

                            textPermissionStatus.setText(
                                    "SMS permission granted"
                            );

                            Toast.makeText(
                                    this,
                                    "SMS permission granted",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            textPermissionStatus.setText(
                                    "SMS permission denied"
                            );

                            Toast.makeText(
                                    this,
                                    "SMS permission denied. Inventory will continue normally.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        buttonGrantPermission =
                findViewById(R.id.buttonGrantPermission);

        buttonBackInventory =
                findViewById(R.id.buttonBackInventory);

        textPermissionStatus =
                findViewById(R.id.textPermissionStatus);

        editPhoneNumber =
                findViewById(R.id.editPhoneNumber);

        // Load the saved phone number.
        SharedPreferences preferences =
                getSharedPreferences(
                        "inventory_preferences",
                        MODE_PRIVATE
                );

        String savedPhoneNumber =
                preferences.getString("phone_number", "");

        editPhoneNumber.setText(savedPhoneNumber);

        updatePermissionStatus();

        // Request SMS permission.
        buttonGrantPermission.setOnClickListener(v -> {

            String phoneNumber =
                    editPhoneNumber.getText().toString().trim();

            if (phoneNumber.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please enter a phone number",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Save the phone number.
            preferences.edit()
                    .putString("phone_number", phoneNumber)
                    .apply();

            if (checkSelfPermission(
                    Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED) {

                smsPermissionLauncher.launch(
                        Manifest.permission.SEND_SMS
                );

            } else {

                Toast.makeText(
                        this,
                        "SMS permission is already granted",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Return to inventory.
        buttonBackInventory.setOnClickListener(v -> finish());
    }

    private void updatePermissionStatus() {

        if (checkSelfPermission(
                Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED) {

            textPermissionStatus.setText(
                    "SMS permission granted"
            );

        } else {

            textPermissionStatus.setText(
                    "SMS permission not granted"
            );
        }
    }

}//End