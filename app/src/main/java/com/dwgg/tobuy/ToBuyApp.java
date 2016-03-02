package com.dwgg.tobuy;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import dagger.ObjectGraph;
import timber.log.Timber;

public final class ToBuyApp extends Application {
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        LeakCanary.install(this);

        objectGraph = ObjectGraph.create(new ToBuyModule(this));
    }

    public static ObjectGraph objectGraph(Context context) {
        return ((ToBuyApp) context.getApplicationContext()).objectGraph;
    }
}
