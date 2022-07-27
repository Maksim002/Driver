package ru.telecor.gm.mobile.droid.utils;

import android.app.Activity;
import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadGuiUtils {
    public static Handler handler;
    private static final ExecutorService threadPoolExecutor;
    public static int MaxThreadsPools = 9;

    static {
        threadPoolExecutor = Executors.newFixedThreadPool(MaxThreadsPools);
    }

    public static Handler getHandler() {
        if (handler == null) {
            handler = new Handler();// TODO Can't create handler inside thread Thread[pool-1-thread-3,5,main] that has not called Looper.prepare()
        }
        return handler;
    }

    public static void doInPostHandler(Runnable runnable) {
        getHandler().post(runnable);
    }

    public static void doInThreadPool(Runnable runnable) {
        if (runnable != null) {
            threadPoolExecutor.submit(runnable);
        }
    }

    public static void doInNewThread(Runnable runnable) {
        if (runnable != null) {
            new Thread(runnable).start();
        }
    }

    public static void doInNewThreadOnPostHandler(final Runnable runnable) {
        if (runnable != null) {
            new Thread(() -> doInPostHandler(runnable)).start();
        }
    }

    public static void doInThreadPoolOnPostHandler(final Runnable runnable) {
        if (runnable != null) {
            threadPoolExecutor.submit(() -> doInPostHandler(runnable));
        }
    }

    public static void doInPostDelayedHandler(final Runnable runnable, final long milliseconds) {
        if (runnable != null) {
            getHandler().postDelayed(runnable, milliseconds);
        }
    }

    public static void runOnUiThread(Activity activity, Runnable runnable) {
        if (activity != null && runnable != null) {
            activity.runOnUiThread(runnable);
        }
    }

    public static void initCachedThreadsInPool() {
        for (int i = 0; i < MaxThreadsPools; i++) {
            doInThreadPool(() -> {
                try {
                    Thread.sleep(99);
                } catch (InterruptedException ignored) {
                }
            });
        }
    }
}
