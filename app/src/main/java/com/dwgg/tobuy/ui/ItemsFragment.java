package com.dwgg.tobuy.ui;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.dwgg.tobuy.R;
import com.dwgg.tobuy.ToBuyApp;
import com.dwgg.tobuy.databinding.FragmentItemsBinding;
import com.dwgg.tobuy.db.DbOpenHelper;
import com.dwgg.tobuy.db.ToBuyItem;
import com.dwgg.tobuy.event.ItemReloadEvent;
import com.dwgg.tobuy.model.ToBuyModel;
import com.dwgg.tobuy.util.RxBus;
import com.dwgg.tobuy.util.Tools;
import com.dwgg.tobuy.vm.ItemsItemViewModel;
import com.jakewharton.rxbinding.widget.AdapterViewItemClickEvent;
import com.jakewharton.rxbinding.widget.RxAdapterView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static android.support.v4.view.MenuItemCompat.SHOW_AS_ACTION_IF_ROOM;
import static android.support.v4.view.MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT;

public final class ItemsFragment extends Fragment {

    public interface Listener {
        void onNewItemClicked();

        void onModifyItemClicked(long id, String desc);
    }

    public static ItemsFragment newInstance() {
        ItemsFragment fragment = new ItemsFragment();
        return fragment;
    }

    @Inject
    DbOpenHelper dbOpenHelper;

    @Inject
    RxBus rxBus;

    @Inject
    ToBuyModel model;

    private FragmentItemsBinding binding;
    private Listener listener;
    private ItemsAdapter adapter;
    private CompositeSubscription subscriptions;

    @Override
    public void onAttach(Activity activity) {
        if (!(activity instanceof Listener)) {
            throw new IllegalStateException("Activity must implement fragment Listener.");
        }
        super.onAttach(activity);
        ToBuyApp.objectGraph(activity).inject(this);
        Timber.tag(this.getClass().getSimpleName());
        setHasOptionsMenu(true);

        listener = (Listener) activity;
        adapter = new ItemsAdapter(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu; this adds fragment_items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);

        // the first way to show menu
        MenuItem item = menu.add(R.string.new_item).setIcon(android.R.drawable.ic_input_add)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (listener != null) {
                            listener.onNewItemClicked();
                        }
                        return true;
                    }
                });
        MenuItemCompat.setShowAsAction(item, SHOW_AS_ACTION_IF_ROOM | SHOW_AS_ACTION_WITH_TEXT);
    }

    // the second way to show menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reload) {
            load();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_items, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.list.setEmptyView(binding.empty);
        binding.list.setAdapter(adapter);
        initItemOnLongClick();

        RxAdapterView.itemClickEvents(binding.list)
                .observeOn(Schedulers.io())
                .subscribe(new Action1<AdapterViewItemClickEvent>() {
                    @Override
                    public void call(AdapterViewItemClickEvent event) {
                        Timber.i("action on : " + Thread.currentThread().getName());
                        //update
                        ItemsItemViewModel item = adapter.getItem(event.position());
                        boolean complete = !item.isComplete();
                        try {
                            if (model.update(item.getToBuyItem().getId(), complete)) {
                                item.setComplete(complete);
                            } else {
                                Tools.showToastSafty(getContext(), "update fail");
                            }
                        } catch (SQLException e) {
                            Timber.e(e, "getMessage ; " + e.getMessage() + "\ngetSQLState ; " + e.getSQLState() + "\ngetErrorCode ; " + e.getErrorCode());
                            Tools.showToastSafty(getContext(), "fail");
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        subscriptions = new CompositeSubscription();
        subscriptions.add(rxBus.toObserverable(ItemReloadEvent.class)
                .subscribe(new Action1<ItemReloadEvent>() {
                    @Override
                    public void call(ItemReloadEvent userEvent) {
                        load();
                    }
                }));
        load();
    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        switch (item.getItemId()) {
            case 0:
                // modify
//                Tools.showToast(getContext(), "modify" + position, true);
                ToBuyItem toBuyItem = adapter.getItem(position).getToBuyItem();
                listener.onModifyItemClicked(toBuyItem.getId(), toBuyItem.getDescription());
                break;
            case 1:
                // delete
                try {
                    if (model.delete(adapter.getItem(position).getToBuyItem())) {
                        adapter.delete(position);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.unsubscribe();
    }

    private void load() {
        try {
            subscriptions.add(model.query()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<List<ToBuyItem>, List<ItemsItemViewModel>>() {
                        @Override
                        public List<ItemsItemViewModel> call(List<ToBuyItem> toBuyItems) {
                            List<ItemsItemViewModel> vms = new ArrayList();
                            for (ToBuyItem toBuyItem : toBuyItems) {
                                vms.add(new ItemsItemViewModel(toBuyItem));
                            }
                            return vms;
                        }
                    })
                    .subscribe(adapter));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initItemOnLongClick() {
        binding.list.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "modify");
                menu.add(0, 1, 0, "delete");
//                menu.add(0, 2, 0, "delete ALL");
            }
        });
    }
}
