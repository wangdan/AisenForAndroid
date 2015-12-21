package org.aisen.sample.support.db;

import org.aisen.android.common.context.GlobalContext;
import org.aisen.android.common.utils.SystemUtils;
import org.aisen.orm.SqliteUtility;
import org.aisen.orm.SqliteUtilityBuilder;

/**
 * Created by wangdan on 15/8/29.
 */
public class AisenDB {

    public static final String DB_NAME = "aisen";

    public static SqliteUtility getDB() {
        return SqliteUtility.getInstance(DB_NAME);
    }

    public static void setDB() {
        int versionCode = SystemUtils.getVersionCode(GlobalContext.getInstance());
        new SqliteUtilityBuilder().configDBName(DB_NAME).configVersion(versionCode).build(GlobalContext.getInstance());
    }

}
