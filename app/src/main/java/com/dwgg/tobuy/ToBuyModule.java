package com.dwgg.tobuy;

import android.app.Application;

import com.dwgg.tobuy.db.DbModule;
import com.dwgg.tobuy.db.DbOpenHelper;
import com.dwgg.tobuy.model.ToBuyModel;
import com.dwgg.tobuy.ui.UiModule;
import com.dwgg.tobuy.util.RxBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                DbModule.class,
                UiModule.class
        }
)
public final class ToBuyModule {
    private final Application application;

    ToBuyModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    RxBus provideRxBus() {
        return RxBus.getDefault();
    }

    @Provides
    @Singleton
    ToBuyModel provideToBuyModel(DbOpenHelper dbOpenHelper) {
        return new ToBuyModel(dbOpenHelper);
    }
}
