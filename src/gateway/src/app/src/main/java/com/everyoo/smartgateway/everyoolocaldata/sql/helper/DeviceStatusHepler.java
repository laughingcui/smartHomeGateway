package com.everyoo.smartgateway.everyoolocaldata.sql.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.everyoo.smartgateway.everyoolocaldata.sql.bean.DeviceStatusBean;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/11/1.
 */
public class DeviceStatusHepler extends OrmLiteSqliteOpenHelper {
    public static final String DB_NAME = "device_status.db";
    public static final  int DB_VERSION=1;
    private static DeviceStatusHepler mInstance;
    private Map<String, Dao> daos = new HashMap<>();
    private DeviceStatusHepler(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, DeviceStatusBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource,DeviceStatusBean.class,true);
            onCreate(sqLiteDatabase,connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public  static  synchronized DeviceStatusHepler getInstance(Context context){
        context=context.getApplicationContext();
        if(mInstance==null){
            synchronized (DeviceStatusHepler.class){
                if(mInstance==null){
                    mInstance=new DeviceStatusHepler(context);
                }
            }
        }
        return mInstance;
    }
    public  synchronized  Dao getDao(Class clazz) throws SQLException {
        Dao dao=null;
        String className=clazz.getSimpleName();
        if(daos.containsKey(className)){
            dao=daos.get(clazz);
        }
        if(dao==null){
            dao=super.getDao(clazz);
            daos.put(className,dao);
        }
        return dao;
    }

    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }
}
