package com.udacity.popularmovies.stagetwo.event;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * This class is used to post Otto Events from any thread (main or background)
 * This is required because we need to fire an event inside the onPerformSync callback of SyncAdapter. See MovieSyncAdapter.onPerformSync
 *
 * Code copied from StackOverflow-
 * http://stackoverflow.com/questions/15431768/how-to-send-event-from-service-to-activity-with-otto-event-bus
 *
 * Created by kunaljaggi on 6/5/16.
 */

public class MainThreadBus extends Bus {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    MainThreadBus.super.post(event);
                }
            });
        }
    }
}
