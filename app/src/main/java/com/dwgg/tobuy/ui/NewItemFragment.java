package com.dwgg.tobuy.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.dwgg.tobuy.R;
import com.dwgg.tobuy.ToBuyApp;
import com.dwgg.tobuy.databinding.NewItemBinding;
import com.dwgg.tobuy.db.DbOpenHelper;
import com.dwgg.tobuy.event.ItemReloadEvent;
import com.dwgg.tobuy.event.TextEvent;
import com.dwgg.tobuy.model.ToBuyModel;
import com.dwgg.tobuy.util.RxBus;
import com.dwgg.tobuy.util.Tools;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.sql.SQLException;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;


public final class NewItemFragment extends DialogFragment {
    private static final String KEY_ID = "id";
    private static final String KEY_DESC = "desc";
    private boolean isUpdate;

    @Inject
    DbOpenHelper dbOpenHelper;

    @Inject
    RxBus rxBus;

    @Inject
    ToBuyModel model;

    private CompositeSubscription subscriptions;

    public static NewItemFragment newInstance(long id, String description) {
        Bundle args = new Bundle();
        args.putLong(KEY_ID, id);
        args.putString(KEY_DESC, description);
        NewItemFragment fragment = new NewItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NewItemFragment newInstance() {
        NewItemFragment fragment = new NewItemFragment();
        return fragment;
    }

    private final PublishSubject<String> createClicked = PublishSubject.create();
    private NewItemBinding binding;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ToBuyApp.objectGraph(activity).inject(this);
        Timber.tag(this.getClass().getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeSubscription();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.new_item, null, false);

        View view = binding.getRoot();

        isUpdate = getArguments() != null;
        if (isUpdate) {
            binding.input.setText(getArgDesc());
        }


        Observable.combineLatest(createClicked, RxTextView.textChanges(binding.input),
                new Func2<String, CharSequence, String>() {
                    @Override
                    public String call(String ignored, CharSequence text) {
                        return text.toString();
                    }
                }) //
                .observeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String description) {
                        try {
                            if (isUpdate) {
                                //update
                                if (model.update(getArgId(), description)) {
                                    rxBus.post(new ItemReloadEvent());
                                } else {
                                    Tools.showToastSafty(getContext(), "update fail");
                                }
                            } else {
                                //create
                                if (model.create(description)) {
                                    rxBus.post(new ItemReloadEvent());
                                } else {
                                    Tools.showToastSafty(getContext(), "create fail");
                                }
                            }
                        } catch (SQLException e) {
                            Timber.e(e, "getMessage ; " + e.getMessage() + "\ngetSQLState ; " + e.getSQLState() + "\ngetErrorCode ; " + e.getErrorCode());
                            Tools.showToastSafty(getContext(), "fail");
                        }
                    }
                });

        return new AlertDialog.Builder(context) //
                .setTitle(isUpdate ? R.string.modify_item : R.string.new_item)
                .setView(view)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createClicked.onNext("clicked");
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                    }
                })
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        subscriptions.add(rxBus.registerSticky(TextEvent.class)
                .subscribe(new Action1<TextEvent>() {
                    @Override
                    public void call(TextEvent event) {
                        Tools.showToastSafty(getContext(), event.getText());
                    }
                }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }

    private long getArgId() {
        return getArguments().getLong(KEY_ID);
    }

    private String getArgDesc() {
        return getArguments().getString(KEY_DESC);
    }
}
