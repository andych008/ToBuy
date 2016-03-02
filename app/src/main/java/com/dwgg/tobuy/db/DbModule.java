package com.dwgg.tobuy.db;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public final class DbModule {
    @Provides
    @Singleton
    DbOpenHelper provideOpenHelper(Application application) {
        return new DbOpenHelper(application);
    }
}
