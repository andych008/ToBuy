package com.dwgg.tobuy.db;

import com.dwgg.tobuy.db.dao.ToBuyItemDaoImpl;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "to_buy_item", daoClass = ToBuyItemDaoImpl.class)
public class ToBuyItem {

    //    @DatabaseField(id=true, /*generatedId = true, */columnName = "_id")
    @DatabaseField(generatedId = true, columnName = "_id")
    private long id;
    @DatabaseField(columnName = "description", unique = true)
    private String description;
    @DatabaseField(columnName = "complete")
    private boolean complete;

    public ToBuyItem() {
    }

    public ToBuyItem(String description) {
        this.description = description;
        this.complete = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
