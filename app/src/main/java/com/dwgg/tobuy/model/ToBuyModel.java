package com.dwgg.tobuy.model;

import com.dwgg.tobuy.db.DbOpenHelper;
import com.dwgg.tobuy.db.ToBuyItem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;

import rx.Observable;

/**
 * Created by Andy on 3/2/2016.
 */
public class ToBuyModel {

    private Dao<ToBuyItem, Long> itemDao;


    public ToBuyModel(DbOpenHelper dbOpenHelper) {
        try {
            itemDao = DaoManager.createDao(dbOpenHelper.getConnectionSource(), ToBuyItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Observable<List<ToBuyItem>> query() throws SQLException {
        return Observable.just(itemDao.queryBuilder().orderBy("complete", true).query());
    }

    public boolean delete(ToBuyItem item) throws SQLException {
        return itemDao.delete(item) == 1;
    }

    public boolean create(String description) throws SQLException {
        return itemDao.create(new ToBuyItem(description)) == 1;
    }

    public boolean update(long id, String description) throws SQLException {
        List<ToBuyItem> list = itemDao.queryForEq("_id", id);
        if (list.size() > 0) {
            ToBuyItem toBuyItem = list.get(0);
            toBuyItem.setDescription(description);
            return itemDao.update(toBuyItem) == 1;
        }
        return false;
    }

    public boolean update(long id, boolean complete) throws SQLException {
        List<ToBuyItem> list = itemDao.queryForEq("_id", id);
        if (list.size() > 0) {
            ToBuyItem toBuyItem = list.get(0);
            toBuyItem.setComplete(complete);
            return itemDao.update(toBuyItem) == 1;
        }
        return false;
    }
}
