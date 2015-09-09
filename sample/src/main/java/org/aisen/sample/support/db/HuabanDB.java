package org.aisen.sample.support.db;

import org.aisen.android.common.context.GlobalContext;
import org.aisen.orm.SqliteUtility;
import org.aisen.orm.SqliteUtilityBuilder;

/**
 * Created by wangdan on 15/8/29.
 */
public class HuabanDB {

    public static SqliteUtility getDB() {
        return SqliteUtility.getInstance("huaban");
    }

    public static void setDB() {
        new SqliteUtilityBuilder().configDBName("huaban").configVersion(1).build(GlobalContext.getInstance());
    }

}
