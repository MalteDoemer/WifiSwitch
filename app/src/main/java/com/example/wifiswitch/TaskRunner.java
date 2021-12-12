package com.example.wifiswitch;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private static TaskRunner instance;

    private TaskRunner() {
    }

    public static synchronized TaskRunner getInstance() {
        if (instance == null)
            instance = new TaskRunner();

        return instance;
    }

    public interface Callback<R> {
        void onComplete(R result, Exception except);
    }

    public <Res> void executeAsync(Callable<Res> callable, Callback<Res> callback) {
        executor.execute(() -> {
            final Res result;
            try {
                result = callable.call();
            } catch (Exception e) {
                handler.post(() -> {
                    callback.onComplete(null, e);
                });
                return;
            }

            handler.post(() -> {
                callback.onComplete(result, null);
            });
        });
    }
}
