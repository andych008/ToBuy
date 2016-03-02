package com.dwgg.tobuy.vm;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;

import com.android.databinding.library.baseAdapters.BR;
import com.dwgg.tobuy.db.ToBuyItem;

/**
 * @author DwGG
 *         Created at 2016/03/01 12:07
 */
public class ItemsItemViewModel extends BaseObservable {
    private String text;
    private boolean complete;
    private ToBuyItem toBuyItem;

    public ItemsItemViewModel() {
    }

    public ItemsItemViewModel(ToBuyItem toBuyItem) {
        this.toBuyItem = toBuyItem;
        text = toBuyItem.getDescription();
        complete = toBuyItem.isComplete();
    }

    @Bindable
    public CharSequence getText() {

        CharSequence description = text;
        if (complete) {
            SpannableString spannable = new SpannableString(description);
            spannable.setSpan(new StrikethroughSpan(), 0, description.length(), 0);
            description = spannable;
        }

        return description;
    }

    @Bindable
    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
        toBuyItem.setComplete(complete);
        notifyChange();
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }

    public ToBuyItem getToBuyItem() {
        return toBuyItem;
    }

    public void setToBuyItem(ToBuyItem toBuyItem) {
        this.toBuyItem = toBuyItem;
    }
}
