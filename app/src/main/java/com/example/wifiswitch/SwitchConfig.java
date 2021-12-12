package com.example.wifiswitch;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SwitchConfig {

    private static final String TAG = SwitchConfig.class.getSimpleName();

    private final Context context;
    private final String filename;

    private List<SmartSwitch> smartSwitches;

    public SwitchConfig(Context context, List<SmartSwitch> data, String filename) {
        this.context = context;
        this.filename = filename;
        this.smartSwitches = data;

        try {
            load();
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            Log.e(TAG, "SwitchConfig: IOException while loading config", e);
        }
    }

    private void load() throws IOException {
        FileInputStream fis = context.openFileInput(filename);
        Gson gson = new Gson();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            Type type = new TypeToken<ArrayList<SmartSwitch>>() {
            }.getType();
            ArrayList<SmartSwitch> loaded =gson.fromJson(reader, type);

            smartSwitches.clear();
            smartSwitches.addAll(loaded);
        }

    }

    public void save() throws IOException {
        Gson gson = new Gson();

        try (FileOutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            stream.write(gson.toJson(smartSwitches).getBytes(StandardCharsets.UTF_8));
        }
    }
}
