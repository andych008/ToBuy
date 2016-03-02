package com.dwgg.tobuy.db.dao;

import com.dwgg.tobuy.db.ToBuyItem;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;

public class ToBuyItemDaoImpl extends BaseDaoImpl<ToBuyItem, Long> {

    public ToBuyItemDaoImpl(ConnectionSource connectionSource, DatabaseTableConfig<ToBuyItem> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }

    public ToBuyItemDaoImpl(ConnectionSource connectionSource, Class<ToBuyItem> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public ToBuyItemDaoImpl(Class<ToBuyItem> dataClass) throws SQLException {
        super(dataClass);
    }
}
