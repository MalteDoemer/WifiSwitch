package com.example.wifiswitch;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SmartSwitch {

    private static final String TAG = SmartSwitch.class.getSimpleName();

    public enum State {
        On,
        Off,
        Unknown,
    }

    public interface StateChangedListener {
        void onStateChanged(SmartSwitch smartSwitch);
    }


    private final String name;
    private final String host;

    private transient State relayState;
    private transient StateChangedListener listener;

    public SmartSwitch(String name, String host) {
        this.name = name;
        this.host = host;
        relayState = State.Unknown;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public void setStateChangedListener(StateChangedListener listener) {
        this.listener = listener;
    }

    public void setRelayState(State state) {
        if (listener != null) listener.onStateChanged(this);
        relayState = state;
    }

    public State getRelayState() {

        if (relayState == null) {
            relayState = State.Unknown;
        }

        return relayState;
    }


    /**
     * Queries the state of the relay via an http request.
     */
    public void updateState() {
        URL url;
        try {
            url = new URL("http://" + host + "/report");
        } catch (MalformedURLException e) {
            Log.e(TAG, "updateState: invalid url: ", e);
            setRelayState(State.Unknown);
            return;
        }

        TaskRunner.getInstance().executeAsync(new HttpGetTask(url), ((result, except) -> {
            if (except != null) {
                Log.e(TAG, "updateState: http request failed", except);
                setRelayState(State.Unknown);
                return;
            }

            updateStateBasedOnResult(result);
        }));
    }

    /**
     * Toggles the relay of the smart switch by sending an http request.
     */
    public void toggleSwitch() {
        URL url;

        try {
            url = new URL("http://" + host + "/toggle");
        } catch (MalformedURLException e) {
            Log.e(TAG, "toggleSwitch: invalid url", e);
            setRelayState(State.Unknown);
            return;
        }

        TaskRunner.getInstance().executeAsync(new HttpGetTask(url), (result, except) -> {
            if (except != null) {
                Log.e(TAG, "toggleSwitch: http request failed", except);
                setRelayState(State.Unknown);
                return;
            }

            updateStateBasedOnResult(result);
        });
    }

    private void updateStateBasedOnResult(HttpGetResult result) {
        if (result.getResponse() != null) {
            JsonElement relay = result.getResponse().get("relay");

            if (relay == null || !relay.isJsonPrimitive()) {
                Log.e(TAG, "updateStateBasedOnResult: unexpected json response");
                setRelayState(State.Unknown);
                return;
            }

            boolean state = relay.getAsBoolean();
            setRelayState(state ? State.On : State.Off);
        } else if (result.getCode() == HttpURLConnection.HTTP_OK) {
            Log.e(TAG, "updateStateBasedOnResult: received empty response");
            setRelayState(State.Unknown);
        } else {
            Log.e(TAG, "updateStateBasedOnResult: http request failed: code = " + result.getCode());
            setRelayState(State.Unknown);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "SmartSwitch{" +
                "name='" + getName() + '\'' +
                ", host='" + getHost() + '\'' +
                ", relayState=" + getRelayState() +
                '}';
    }
}
