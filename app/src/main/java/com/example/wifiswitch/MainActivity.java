package com.example.wifiswitch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.wifiswitch.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SmartSwitch.StateChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();


    public static final int ACTIVITY_NEW_SWITCH_ID = 2;

    public static final String EXTRA_NAME = "com.example.wifiswitch.MainActivity.EXTRA_NAME";
    public static final String EXTRA_HOST = "com.example.wifiswitch.MainActivity.EXTRA_HOST";

    // Used to load the 'wifiswitch' library on application startup.
    static {
        System.loadLibrary("wifiswitch");
    }

    private ActivityMainBinding binding;
    private SwitchConfig switchConfig;
    private RecyclerViewAdapter adapter;


    private final ArrayList<SmartSwitch> switches = new ArrayList<>();


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle(R.string.app_name);
        binding.toolbar.inflateMenu(R.menu.menu);

        // load the config from file
        switchConfig = new SwitchConfig(this, switches, getString(R.string.config_file_path));

        // create the recycler view
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(manager);
        adapter = new RecyclerViewAdapter(this, switches);
        binding.recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recyclerView.getContext(), manager.getOrientation());
        binding.recyclerView.addItemDecoration(dividerItemDecoration);

        // get the state of all switches in the config and the the state listener
        switches.forEach(smartSwitch -> {
            smartSwitch.setStateChangedListener(this);
            smartSwitch.updateState();
        });

        // this is called when the user clicks on an item
        adapter.setItemClickListener((view, pos) -> {
            switches.get(pos).toggleSwitch();
        });

        // this is called when the user removes an item
        adapter.setDeleteButtonClickListener((view, pos) -> {
            switches.remove(pos);
            adapter.notifyItemRemoved(pos);
        });


        binding.toolbar.getMenu().findItem(R.id.action_refresh).setOnMenuItemClickListener(menuItem -> {
            switches.forEach(SmartSwitch::updateState);
            return true;
        });

        binding.toolbar.getMenu().findItem(R.id.action_delete_config).setOnMenuItemClickListener(menuItem -> {
            switches.clear();
            adapter.notifyDataSetChanged();
            return true;
        });

        binding.toolbar.getMenu().findItem(R.id.action_add).setOnMenuItemClickListener(menuItem -> {
            // TODO implement

            Intent activity = new Intent(this, ActivityNewSwitch.class);
            startActivityForResult(activity, ACTIVITY_NEW_SWITCH_ID);
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ACTIVITY_NEW_SWITCH_ID:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String name = data.getStringExtra(EXTRA_NAME);
                    String host = data.getStringExtra(EXTRA_HOST);

                    SmartSwitch smartSwitch = new SmartSwitch(name, host);
                    switches.add(smartSwitch);
                    adapter.notifyItemInserted(switches.indexOf(smartSwitch));

                    smartSwitch.setStateChangedListener(this);
                    smartSwitch.updateState();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i(TAG, "onActivityResult: canceled");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        switches.forEach(SmartSwitch::updateState);

        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(TAG, "onPause()");

        try {
            switchConfig.save();
            Log.i(TAG, "onPause: saved config");
        } catch (IOException e) {
            Log.e(TAG, "onPause: IOException while saving config: ", e);
        }
    }

    @Override
    public void onStateChanged(SmartSwitch smartSwitch) {
        adapter.notifyItemChanged(switches.indexOf(smartSwitch));
    }
}