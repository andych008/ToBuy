package com.dwgg.tobuy.ui;

import dagger.Module;

@Module(
        injects = {
                ItemsFragment.class,
                NewItemFragment.class,
                MainActivity.class
        },
        complete = false,
        library = true
)
public final class UiModule {


}
