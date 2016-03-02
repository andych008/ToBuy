package com.dwgg.tobuy.util;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Andy on 3/1/2016.
 */
public class Tools {
    public static final void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public static final void showToast(Context context, String s, boolean isShort) {
        Toast.makeText(context, s, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public static final void showToastSafty(final Context context, String s, boolean isShort) {
        doShowToastSafty(context, s, isShort);
    }

    public static final void showToastSafty(final Context context, String s) {
        doShowToastSafty(context, s, false);
    }

    private static final void doShowToastSafty(final Context context, String s, final boolean isShort) {
        Observable.just(s)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Toast.makeText(context, s, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
                    }
                });
    }

    public static final void showSnackbar(View view, String s) {
        Snackbar.make(view, s, Snackbar.LENGTH_LONG).show();
    }
}
