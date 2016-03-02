package com.dwgg.tobuy.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dwgg.tobuy.R;
import com.dwgg.tobuy.databinding.ItemsItemBinding;
import com.dwgg.tobuy.vm.ItemsItemViewModel;

import java.util.Collections;
import java.util.List;

import rx.functions.Action1;

final class ItemsAdapter extends BaseAdapter implements Action1<List<ItemsItemViewModel>> {
    private final LayoutInflater inflater;


    private ItemsItemBinding binding;

    private List<ItemsItemViewModel> items = Collections.emptyList();

    public ItemsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void call(List<ItemsItemViewModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ItemsItemViewModel getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getToBuyItem().getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.items_item, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemsItemBinding) convertView.getTag();
        }
        ItemsItemViewModel item = getItem(position);
        binding.setModel(item);

        return convertView;
    }

    public void delete(int position) {
        items.remove(position);
        notifyDataSetChanged();
    }
}
