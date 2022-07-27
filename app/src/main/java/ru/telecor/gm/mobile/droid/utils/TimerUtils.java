package ru.telecor.gm.mobile.droid.utils;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtils {

    public static void doInTimerSchedule(final Runnable runnable, int delay) {
        if (runnable != null) {
            new Timer().schedule(new TimerTask() {
                public void run() {
                    runnable.run();
                }
            }, delay);
        }
    }

    public static void doInTimerScheduleInPostHandler(final Runnable runnable, final int delay) {
        if (runnable != null) {
            new Timer().schedule(new TimerTask() {
                public void run() {
                    ThreadGuiUtils.doInPostHandler(runnable);
                }
            }, delay);
        }
    }

    public static Timer doInTimerScheduleInPostHandlerInstance(final Runnable runnable, int delay) {
        if (runnable != null) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    ThreadGuiUtils.doInPostHandler(runnable);
                }
            }, delay);
            return timer;
        } else {
            return null;
        }
    }

    public static void doInTimerScheduleWithDelay(final Runnable runnable, int delay, int period) {
        if (runnable != null) {
            new Timer().schedule(new TimerTask() {
                public void run() {
                    runnable.run();
                }
            }, delay, period);
        }
    }

    public static Timer doInTimerScheduleWithDelayInstance(final Runnable runnable, final int delay, final int period) {
        if (runnable != null) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    runnable.run();
                }
            }, delay, period);
            return timer;
        } else {
            return null;
        }
    }
}
