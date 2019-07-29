package com.lrs.lrsvideolibrary.view.utils;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.lrs.lrsvideolibrary.view.App;
import com.lrs.lrsvideolibrary.view.bean.VideoCacheBean;

import java.util.ArrayList;

/**
 * lrs
 */

public class VideoCacheDBUtil {

    private static LiteOrm liteOrmDB = LiteOrm.newSingleInstance(new DataBaseConfig(App.getApplication().getApplicationContext(), "VideoPlayerDemo"));

    public static void save(VideoCacheBean bean) {
        liteOrmDB.save(bean);
    }

    public static void delete(VideoCacheBean bean) {
        liteOrmDB.delete(bean);
    }

    //根据播放时间 降序
    public static ArrayList<VideoCacheBean> query() {
        ArrayList<VideoCacheBean> list = liteOrmDB
                .query(new QueryBuilder<>(VideoCacheBean.class).appendOrderDescBy(VideoCacheBean.PLAY_TIME));
        return list;
    }

    public static VideoCacheBean query(String key) {
        ArrayList<VideoCacheBean> list = liteOrmDB
                .query(new QueryBuilder<>(VideoCacheBean.class).where(VideoCacheBean.KEY + "=?", key));
        if(list.size() > 0){
            return list.get(0);
        }else{
            return null;
        }
    }
}
