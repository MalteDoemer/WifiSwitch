package com.example.wifiswitch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;

import com.example.wifiswitch.databinding.ActivityNewSwitchBinding;

public class ActivityNewSwitch extends AppCompatActivity {

    ActivityNewSwitchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewSwitchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle(R.string.new_switch_activity_title);
        setSupportActionBar(binding.toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        binding.addSwitchButton.setOnClickListener(view -> {

            Intent result = new Intent();

            Editable name = binding.switchNameInput.getText();
            Editable host = binding.switchHostInput.getText();

            if (name == null || host == null) {
                setResult(Activity.RESULT_CANCELED);
                finish();
                return;
            }

            result.putExtra(MainActivity.EXTRA_NAME, name.toString());
            result.putExtra(MainActivity.EXTRA_HOST, host.toString());
            setResult(Activity.RESULT_OK, result);
            finish();
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // we don't call super so we can do our own stuff
        // the upwards arrow is the only item
        setResult(Activity.RESULT_CANCELED);
        finish();
        return true;
    }
}